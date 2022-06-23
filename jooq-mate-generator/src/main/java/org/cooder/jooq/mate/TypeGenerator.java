package org.cooder.jooq.mate;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.jooq.Field;
import org.jooq.impl.TableImpl;
import org.jooq.tools.StringUtils;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.FieldSpec.Builder;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

public class TypeGenerator {
    private static final int INTERFACE = 1;
    private static final int RECORD = 2;
    private static final int POJO = 3;

    private GeneratorStrategy strategy;

    public TypeGenerator() {
        this(new GeneratorStrategy());
    }

    public TypeGenerator(GeneratorStrategy strategy) {
        this.strategy = strategy;
    }

    public void generateTables(String tablesClassName) throws Exception {
        java.lang.reflect.Field[] fs = Class.forName(tablesClassName).getDeclaredFields();
        for (java.lang.reflect.Field f : fs) {
            if((f.getModifiers() & java.lang.reflect.Modifier.PUBLIC) > 0) {
                Object table = f.get(null);
                if(table instanceof TableImpl) {
                    generateTable((TableImpl<?>) table);
                }
            }
        }
    }

    public void generateTable(TableImpl<?> table) throws IOException {
        if(ignoreTable(table)) {
            return;
        }

        String tableName = table.getName();
        Field<?>[] fields = Arrays.asList(table.fields())
                .stream()
                .filter(f -> !ignore(tableName, f))
                .collect(Collectors.toList())
                .toArray(new Field[0]);

        if(strategy.isGenerateInterface(tableName)) {
            String comment = table.getComment();
            generateInterface(tableName, comment, fields);
        }

        if(strategy.isGenerateRecord(tableName)) {
            generateRecord(tableName, fields);
        }

        if(strategy.isGeneratePojo(tableName)) {
            generatePojo(tableName, fields);
        }
    }

    public void generateInterface(String tableName, String comment, Field<?>[] fields) throws IOException {
        String clazzName = strategy.interfaceClazzName(tableName);
        TypeSpec.Builder ts = TypeSpec.interfaceBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(comment);

        ts.addSuperinterfaces(strategy.getGeneratedInterfaceSuperInterfaces(tableName));

        generateGetterSetter(ts, fields, INTERFACE);

        output(strategy.interfacePackageName(tableName), ts.build());
    }

    public void generateRecord(String tableName, Field<?>[] fields) throws IOException {
        String clazz = strategy.recordClazzName(tableName);
        ClassName genericType = strategy.pojoClassName(tableName);
        TypeSpec.Builder ts = TypeSpec.classBuilder(clazz)
                .addModifiers(Modifier.PUBLIC)
                .superclass(ParameterizedTypeName.get(ClassName.get(AbstractRecord.class), genericType))
                .addSuperinterface(strategy.interfaceClassName(tableName));

        ts.addField(generateRecordFields(fields));
        ts.addMethod(generateRecordConstructor(genericType));
        generateGetterSetter(ts, fields, RECORD);

        output(strategy.recordPackageName(tableName), ts.build());
    }

    public void generatePojo(String tableName, Field<?>[] fields) throws IOException {
        ClassName pojoCN = strategy.pojoClassName(tableName);
        TypeSpec.Builder ts = TypeSpec.classBuilder(pojoCN.simpleName())
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(strategy.interfaceClassName(tableName));

        TypeName superClass = strategy.getPojoSuperClass(tableName);
        if(superClass != null) {
            ts.superclass(superClass);
        }

        generatePojoFields(ts, fields);
        if(strategy.isGeneratePojoWithLombok(tableName)) {
            ts.addAnnotation(lombok.Data.class)
                    .addAnnotation(lombok.experimental.SuperBuilder.class)
                    .addAnnotation(lombok.NoArgsConstructor.class)
                    .addAnnotation(AnnotationSpec.builder(lombok.ToString.class).addMember("callSuper", "true").build());
            if(superClass != null) {
                ts.addAnnotation(AnnotationSpec.builder(lombok.EqualsAndHashCode.class).addMember("callSuper", "true").build());
            } else {
                ts.addAnnotation(lombok.EqualsAndHashCode.class);
            }
        } else {
            generateGetterSetter(ts, fields, POJO);
        }

        output(strategy.pojoPackageName(tableName), ts.build());
    }

    protected void generatePojoFields(TypeSpec.Builder ts, Field<?>[] fields) {
        for (int i = 0; i < fields.length; i++) {
            Field<?> f = fields[i];
            String fieldName = StringUtils.toCamelCaseLC(f.getName());
            FieldSpec spec = FieldSpec.builder(f.getType(), fieldName, Modifier.PRIVATE)
                    .addJavadoc(f.getComment())
                    .build();
            ts.addField(spec);
        }
    }

    protected void generateGetterSetter(TypeSpec.Builder ts, Field<?>[] fields, int type) {
        for (int i = 0; i < fields.length; i++) {
            ts.addMethod(generateGetter(i, fields[i], type));
            ts.addMethod(generateSetter(i, fields[i], type));
        }
    }

    private FieldSpec generateRecordFields(Field<?>[] fields) {
        Builder b = FieldSpec.builder(AbstractRecord.Field[].class, "FIELDS", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
        com.squareup.javapoet.CodeBlock.Builder cb = CodeBlock.builder();
        cb.add("new Field[] {\n");

        for (int i = 0; i < fields.length; i++) {
            String fieldName = StringUtils.toCamelCaseLC(fields[i].getName());
            cb.add(strategy.getIndent() + "$T.builder().name($S).type($T.class).desc($S).build(),\n", AbstractRecord.Field.class,
                    fieldName, fields[i].getType(), fields[i].getComment());
        }
        cb.add("}");
        b.initializer(cb.build());
        return b.build();
    }

    private MethodSpec generateRecordConstructor(ClassName genericType) {
        MethodSpec.Builder b = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC);
        b.addStatement("super($T.class, FIELDS)", genericType);
        return b.build();
    }

    protected MethodSpec generateGetter(int index, Field<?> field, int type) {
        String nameLC = StringUtils.toCamelCaseLC(field.getName());
        MethodSpec.Builder b = MethodSpec.methodBuilder("get" + StringUtils.toCamelCase(field.getName()))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(field.getType())
                .addJavadoc("获取`" + field.getComment() + "`");

        if(INTERFACE == type) {
            b.addModifiers(Modifier.ABSTRACT);
        } else if(RECORD == type) {
            b.addStatement("return ($T)get($L)", field.getType(), index);
        } else if(POJO == type) {
            b.addStatement("return this.$N", nameLC);
        }
        return b.build();
    }

    protected MethodSpec generateSetter(int index, Field<?> field, int type) {
        String nameLC = StringUtils.toCamelCaseLC(field.getName());
        MethodSpec.Builder b = MethodSpec.methodBuilder("set" + StringUtils.toCamelCase(field.getName()))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(field.getType(), nameLC)
                .returns(void.class)
                .addJavadoc("设置`" + field.getComment() + "`");

        if(INTERFACE == type) {
            b.addModifiers(Modifier.ABSTRACT);
        } else if(RECORD == type) {
            b.addStatement("set($L, $N)", index, nameLC);
        } else if(POJO == type) {
            b.addStatement("this.$N = $N", nameLC, nameLC);
        }

        return b.build();
    }

    private void output(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .indent(strategy.getIndent())
                .skipJavaLangImports(true)
                .addFileComment("\nThis file is generated by JOOQ-MATE.\n")
                .build();
        javaFile.writeTo(new File(strategy.getDirectory()));

        System.out.println(String.format("generated: %s.%s", packageName, typeSpec.name));
    }

    private boolean ignoreTable(TableImpl<?> table) {
        return this.strategy.isIgnoreTable(table.getName());
    }

    private boolean ignore(String tableName, Field<?> f) {
        return this.strategy.isIgnoreField(tableName, f.getName());
    }
}
