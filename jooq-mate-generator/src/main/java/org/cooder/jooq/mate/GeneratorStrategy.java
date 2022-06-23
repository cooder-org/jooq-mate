package org.cooder.jooq.mate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import org.jooq.tools.StringUtils;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

public class GeneratorStrategy {
    @Getter
    private String indent = "    ";

    @Getter
    private String directory;

    @Getter
    private String packageName = "org.cooder.jooq.generate";
    private Set<String> ignoreFieldNames = new HashSet<>();
    private Set<String> includeTableNames = new HashSet<>();
    private Set<String> excludeTableNames = new HashSet<>();
    private Map<String, TableStrategy> tableStrategies = new HashMap<>();

    @Setter
    @Accessors(fluent = true)
    private boolean generatePojoWithLombok = true;

    @Setter
    @Accessors(fluent = true)
    private boolean generateInterface = true;

    @Setter
    @Accessors(fluent = true)
    private boolean generateRecord = true;

    @Setter
    @Accessors(fluent = true)
    private boolean generatePojo = true;

    private NameConverter interfaceNameConverter;
    private NameConverter recordNameConverter;
    private NameConverter pojoNameConverter;

    public GeneratorStrategy withIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public GeneratorStrategy withDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public GeneratorStrategy withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public GeneratorStrategy withInterfaceNameConverter(NameConverter tableNameConverter) {
        this.interfaceNameConverter = tableNameConverter;
        return this;
    }

    public GeneratorStrategy withRecordNameConverter(NameConverter recordNameConverter) {
        this.recordNameConverter = recordNameConverter;
        return this;
    }

    public GeneratorStrategy withPojoNameConverter(NameConverter pojoNameConverter) {
        this.pojoNameConverter = pojoNameConverter;
        return this;
    }

    public GeneratorStrategy withTableStrategy(String tableName, TableStrategy ts) {
        this.tableStrategies.put(tableName, ts);
        return this;
    }

    public GeneratorStrategy ignoreFieldNames(String... fields) {
        ignoreFieldNames.addAll(Arrays.asList(fields));
        return this;
    }

    public GeneratorStrategy includeTableNames(String... tables) {
        includeTableNames.addAll(Arrays.asList(tables));
        return this;
    }

    public GeneratorStrategy excludeTableNames(String... tables) {
        excludeTableNames.addAll(Arrays.asList(tables));
        return this;
    }

    public boolean isIgnoreTable(String tableName) {
        return includeTableNames.isEmpty() ? excludeTableNames.contains(tableName) : !includeTableNames.contains(tableName);
    }

    public boolean isIgnoreField(String tableName, String fieldName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ignoreFieldNames.contains(fieldName) || (ts != null && ts.isIgnoreField(fieldName));
    }

    public boolean isGeneratePojoWithLombok(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? generatePojoWithLombok : ts.isGeneratePojoWithLombok(generatePojoWithLombok);
    }

    public boolean isGenerateInterface(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? generateInterface : ts.isGenerateInterface(generateInterface);
    }

    public boolean isGenerateRecord(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? generateRecord : ts.isGenerateRecord(generateRecord);
    }

    public boolean isGeneratePojo(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? generatePojo : ts.isGeneratePojo(generatePojo);
    }

    public List<TypeName> getGeneratedInterfaceSuperInterfaces(String tableName) {
        String[] ss = new String[0];
        TableStrategy ts = getTableStrategy(tableName);
        if(ts != null) {
            ss = ts.getGeneratedInterfaceSuperInterfaces();
        }

        ClassName itc = interfaceClassName(tableName);
        List<TypeName> ret = new ArrayList<>();
        for (String s : ss) {
            ret.add(typeNameFrom(itc, s));
        }
        return ret;
    }

    public TypeName getPojoSuperClass(String tableName) {
        String sc = "";
        TableStrategy ts = getTableStrategy(tableName);
        if(ts != null) {
            sc = ts.getGeneratedPojoSuperClass();
        }
        ClassName pjc = pojoClassName(tableName);
        if(!StringUtils.isEmpty(sc)) {
            return typeNameFrom(pjc, sc);
        } else {
            return null;
        }
    }

    public TableStrategy getTableStrategy(String tableName) {
        return tableStrategies.get(tableName);
    }

    public String convertInterfaceName(String tableName) {
        if(interfaceNameConverter != null) {
            return interfaceNameConverter.apply(this, tableName);
        }
        return StringUtils.toCamelCase(tableName);
    }

    public String convertRecordName(String tableName) {
        if(recordNameConverter != null) {
            return recordNameConverter.apply(this, tableName);
        }
        return convertInterfaceName(tableName) + "Record";
    }

    public String convertPojoName(String tableName) {
        if(pojoNameConverter != null) {
            return pojoNameConverter.apply(this, tableName);
        }
        return convertInterfaceName(tableName) + "Entity";
    }

    public String subpackage(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? "" : ts.getSubPackageName();
    }

    public String interfacePackageName(String tableName) {
        return getPackageName() + subpackage(tableName);
    }

    public String pojoPackageName(String tableName) {
        return getPackageName() + ".pojos" + subpackage(tableName);
    }

    public String recordPackageName(String tableName) {
        return getPackageName() + ".records" + subpackage(tableName);
    }

    public String interfaceClazzName(String tableName) {
        return convertInterfaceName(tableName);
    }

    public String recordClazzName(String tableName) {
        return convertRecordName(tableName);
    }

    public String pojoClazzName(String tableName) {
        return convertPojoName(tableName);
    }

    public ClassName interfaceClassName(String tableName) {
        return ClassName.get(interfacePackageName(tableName), interfaceClazzName(tableName));
    }

    public ClassName recordClassName(String tableName) {
        return ClassName.get(recordPackageName(tableName), recordClazzName(tableName));
    }

    public ClassName pojoClassName(String tableName) {
        return ClassName.get(pojoPackageName(tableName), pojoClazzName(tableName));
    }

    private TypeName typeNameFrom(ClassName itc, String name) {
        int idx = name.indexOf('<');
        if(idx >= 0) {
            ClassName base = ClassName.bestGuess(name.substring(0, idx));
            String gps = name.substring(idx + 1, name.lastIndexOf('>')).trim();
            if(StringUtils.isEmpty(gps)) {
                return ParameterizedTypeName.get(base, itc);
            } else {
                String[] parameters = gps.split(",");
                List<TypeName> typeNames = Arrays.asList(parameters).stream()
                        .map(p -> typeNameFrom(itc, p))
                        .collect(Collectors.toList());
                return ParameterizedTypeName.get(base, typeNames.toArray(new TypeName[0]));
            }
        } else {
            return ClassName.bestGuess(name);
        }
    }

    @FunctionalInterface
    public static interface NameConverter extends BiFunction<GeneratorStrategy, String, String> {

    }

    @Setter
    @Accessors(chain = true)
    public static class TableStrategy {
        @Getter
        private String subPackageName = "";
        private Set<String> ignoreFieldNames = new HashSet<>();

        private Boolean generatePojoWithLombok;
        private Boolean generateInterface;
        private Boolean generateRecord;
        private Boolean generatePojo;

        @Getter
        private String[] generatedInterfaceSuperInterfaces = new String[0];

        @Getter
        private String generatedPojoSuperClass = "";

        public TableStrategy ignoreFieldNames(String... fields) {
            ignoreFieldNames.addAll(Arrays.asList(fields));
            return this;
        }

        public boolean isGeneratePojo(boolean def) {
            return generatePojo == null ? def : generatePojo;
        }

        public boolean isGenerateRecord(boolean def) {
            return generateRecord == null ? def : generateRecord;
        }

        public boolean isGenerateInterface(boolean def) {
            return generateInterface == null ? def : generateInterface;
        }

        public boolean isGeneratePojoWithLombok(boolean def) {
            return generatePojoWithLombok == null ? def : generatePojoWithLombok;
        }

        public boolean isIgnoreField(String fieldName) {
            return ignoreFieldNames.contains(fieldName);
        }
    }
}
