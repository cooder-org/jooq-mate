package org.cooder.jooq.mate;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

import org.cooder.jooq.mate.ConfigurationParser.Config;
import org.cooder.jooq.mate.ConfigurationParser.JooqMateConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig.FieldConfig;
import org.cooder.jooq.mate.TypeGeneratorStrategy.TableStrategy;
import org.cooder.jooq.mate.types.AbstractRecord;
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

    private TypeGeneratorStrategy strategy;

    public TypeGenerator() {
        this(new TypeGeneratorStrategy());
    }

    public TypeGenerator(TypeGeneratorStrategy strategy) {
        this.strategy = strategy;
    }

    public void generate(final Config conf) throws Exception {
        withConfig(conf);
        List<TableConfig> tbs = conf.tables();
        for (TableConfig tc : tbs) {
            generateTable(new TableConfigMeta(tc));
        }
    }

    public void generateTables(String tablesClassName) throws Exception {
        java.lang.reflect.Field[] fs = Class.forName(tablesClassName).getDeclaredFields();
        for (java.lang.reflect.Field f : fs) {
            if((f.getModifiers() & java.lang.reflect.Modifier.PUBLIC) > 0) {
                Object table = f.get(null);
                if(table instanceof TableImpl) {
                    generateTable(new JooqTableMeta((TableImpl<?>) table));
                }
            }
        }
    }

    public void generateTable(TableMeta table) throws IOException {
        if(ignoreTable(table)) {
            return;
        }

        String tableName = table.getName();
        FieldMeta[] fields = Arrays.asList(table.fields())
                .stream()
                .filter(f -> !ignore(tableName, f))
                .collect(Collectors.toList())
                .toArray(new FieldMeta[0]);

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

    public void generateInterface(String tableName, String comment, FieldMeta[] fields) throws IOException {
        String clazzName = strategy.interfaceClazzName(tableName);
        TypeSpec.Builder ts = TypeSpec.interfaceBuilder(clazzName)
                .addModifiers(Modifier.PUBLIC)
                .addJavadoc(comment);

        ts.addSuperinterfaces(strategy.getGeneratedInterfaceSuperInterfaces(tableName));

        generateGetterSetter(ts, fields, INTERFACE);

        output(strategy.interfacePackageName(tableName), ts.build());
    }

    public void generateRecord(String tableName, FieldMeta[] fields) throws IOException {
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

    public void generatePojo(String tableName, FieldMeta[] fields) throws IOException {
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

    protected void generatePojoFields(TypeSpec.Builder ts, FieldMeta[] fields) {
        for (int i = 0; i < fields.length; i++) {
            FieldMeta f = fields[i];
            String fieldName = StringUtils.toCamelCaseLC(f.getName());
            FieldSpec spec = FieldSpec.builder(f.getType(), fieldName, Modifier.PRIVATE)
                    .addJavadoc(f.getComment())
                    .build();
            ts.addField(spec);
        }
    }

    protected void generateGetterSetter(TypeSpec.Builder ts, FieldMeta[] fields, int type) {
        for (int i = 0; i < fields.length; i++) {
            ts.addMethod(generateGetter(i, fields[i], type));
            ts.addMethod(generateSetter(i, fields[i], type));
        }
    }

    private FieldSpec generateRecordFields(FieldMeta[] fields) {
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

    protected MethodSpec generateGetter(int index, FieldMeta field, int type) {
        String nameLC = StringUtils.toCamelCaseLC(field.getName());
        MethodSpec.Builder b = MethodSpec.methodBuilder("get" + StringUtils.toCamelCase(field.getName()))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(field.getType())
                .addJavadoc("获取`" + field.getNameDesc() + "`");

        if(INTERFACE == type) {
            b.addModifiers(Modifier.ABSTRACT);
        } else if(RECORD == type) {
            b.addStatement("return ($T)get($L)", field.getType(), index);
        } else if(POJO == type) {
            b.addStatement("return this.$N", nameLC);
        }
        return b.build();
    }

    protected MethodSpec generateSetter(int index, FieldMeta field, int type) {
        String nameLC = StringUtils.toCamelCaseLC(field.getName());
        MethodSpec.Builder b = MethodSpec.methodBuilder("set" + StringUtils.toCamelCase(field.getName()))
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(field.getType(), nameLC)
                .returns(void.class)
                .addJavadoc("设置`" + field.getNameDesc() + "`");

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

    private boolean ignoreTable(TableMeta table) {
        return this.strategy.isIgnoreTable(table.getName());
    }

    private boolean ignore(String tableName, FieldMeta f) {
        return this.strategy.isIgnoreField(tableName, f.getName());
    }

    private void withConfig(final Config conf) {
        JooqMateConfig mc = conf.mateConfig;
        this.strategy.withDirectory(mc.directory)
                .withPackageName(mc.packageName)
                .ignoreFieldNames(mc.ignoreFieldNames)
                .includeTableNames(mc.includeTableNames)
                .excludeTableNames(mc.excludeTableNames)
                .generateInterface(mc.generateInterface)
                .generateRecord(mc.generateRecord)
                .generatePojo(mc.generatePojo)
                .generatePojoWithLombok(mc.generatePojoWithLombok)
                .withInterfaceNameConverter((s, tableName) -> conf.getTableConfig(tableName).jooqmateInterfaceName)
                .withRecordNameConverter((s, tableName) -> conf.getTableConfig(tableName).jooqmateRecordName)
                .withPojoNameConverter((s, tableName) -> conf.getTableConfig(tableName).jooqmatePojoName);

        for (TableConfig tc : conf.tables) {
            this.strategy.withTableStrategy(tc.tableName, new TableStrategy()
                    .setSubPackageName(tc.jooqmateSubpackage)
                    .ignoreFieldNames(tc.jooqmateIgnoreFieldNames)
                    .setGeneratedInterfaceSuperInterfaces(tc.jooqmateInterfaceSupers)
                    .setGeneratedPojoSuperClass(tc.jooqmatePojoSuperClass));
        }
    }

    private interface TableMeta {
        String getName();

        String getComment();

        FieldMeta[] fields();
    }

    private interface FieldMeta {
        String getName();

        Class<?> getType();

        String getComment();

        default String getNameDesc() {
            return getComment();
        }

    }

    private static class JooqTableMeta implements TableMeta {
        private final TableImpl<?> table;

        public JooqTableMeta(TableImpl<?> table) {
            this.table = table;
        }

        @Override
        public String getName() {
            return table.getName();
        }

        @Override
        public String getComment() {
            return table.getComment();
        }

        @Override
        public FieldMeta[] fields() {
            Field<?>[] fs = table.fields();
            FieldMeta[] metas = new FieldMeta[fs.length];
            for (int i = 0; i < fs.length; i++) {
                metas[i] = new JooqFieldMeta(fs[i]);
            }
            return metas;
        }
    }

    private static class JooqFieldMeta implements FieldMeta {
        private final Field<?> field;

        public JooqFieldMeta(Field<?> field) {
            this.field = field;
        }

        @Override
        public String getName() {
            return field.getName();
        }

        @Override
        public Class<?> getType() {
            return field.getType();
        }

        @Override
        public String getComment() {
            return field.getComment();
        }
    }

    private static class TableConfigMeta implements TableMeta {
        private TableConfig tableConfig;

        public TableConfigMeta(TableConfig tc) {
            this.tableConfig = tc;
        }

        @Override
        public String getName() {
            return tableConfig.tableName;
        }

        @Override
        public String getComment() {
            return String.format("%s<br>\n\n%s", tableConfig.tableNameDesc, tableConfig.tableDesc);
        }

        @Override
        public FieldMeta[] fields() {
            List<FieldConfig> fs = tableConfig.fields;
            FieldMeta[] ret = new FieldMeta[fs.size()];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new FieldConfigMeta(fs.get(i));
            }

            return ret;
        }
    }

    private static class FieldConfigMeta implements FieldMeta {
        private FieldConfig fieldConfig;

        public FieldConfigMeta(FieldConfig fieldConfig) {
            this.fieldConfig = fieldConfig;
        }

        @Override
        public String getName() {
            return fieldConfig.fieldName;
        }

        @Override
        public Class<?> getType() {
            Class<?> clazz = null;
            String dataType = fieldConfig.dataType.toUpperCase();
            if(dataType.contains("BIGINT")) {
                clazz = Long.class;
            } else if(dataType.contains("INT")) {
                clazz = Integer.class;
            } else if(dataType.contains("CHAR")) {
                clazz = String.class;
            } else if(dataType.contains("DECIMAL")) {
                clazz = BigDecimal.class;
            } else if(dataType.contains("TIMESTAMP")) {
                clazz = LocalTime.class;
            }
            return clazz;
        }

        @Override
        public String getComment() {
            boolean hasMore = false;
            StringBuilder sb = new StringBuilder();
            sb.append(fieldConfig.fieldNameDesc).append("\n");
            if(!StringUtils.isEmpty(fieldConfig.enums)) {
                sb.append('\n');
                sb.append("取值说明: ").append(fieldConfig.enums).append("\n");
                hasMore = true;
            }
            if(!StringUtils.isEmpty(fieldConfig.example)) {
                if(!hasMore) {
                    sb.append('\n');
                }
                sb.append("例如:  ").append(fieldConfig.example).append("\n");
            }

            return sb.toString();
        }

        @Override
        public String getNameDesc() {
            return fieldConfig.getFieldNameDesc();
        }
    }

}
