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

public class TypeGeneratorStrategy extends GeneratorStrategy {

    @Getter
    private String indent = "    ";

    @Getter
    @Setter
    private String jooqPackageName;
    private Set<String> ignoreFieldNames = new HashSet<>();
    private Set<String> includeTableNames = new HashSet<>();
    private Set<String> excludeTableNames = new HashSet<>();
    private Map<String, TableStrategy> tableStrategies = new HashMap<>();
    private RepoGeneratorStrategy repoStrategy = new RepoGeneratorStrategy();

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

    @Setter
    @Accessors(fluent = true)
    private boolean generateRepo = true;

    private NameConverter interfaceNameConverter;
    private NameConverter recordNameConverter;
    private NameConverter pojoNameConverter;
    private NameConverter repoNameConverter;

    public TypeGeneratorStrategy withIndent(String indent) {
        this.indent = indent;
        return this;
    }

    public GeneratorStrategy withIndent(int spaces) {
        withIndent(MateUtils.repeat(" ", spaces));
        return this;
    }

    public TypeGeneratorStrategy withDirectory(String directory) {
        this.directory = directory;
        return this;
    }

    public TypeGeneratorStrategy withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public TypeGeneratorStrategy withInterfaceNameConverter(NameConverter tableNameConverter) {
        this.interfaceNameConverter = tableNameConverter;
        return this;
    }

    public TypeGeneratorStrategy withRecordNameConverter(NameConverter recordNameConverter) {
        this.recordNameConverter = recordNameConverter;
        return this;
    }

    public TypeGeneratorStrategy withPojoNameConverter(NameConverter pojoNameConverter) {
        this.pojoNameConverter = pojoNameConverter;
        return this;
    }

    public TypeGeneratorStrategy withTableStrategy(String tableName, TableStrategy ts) {
        this.tableStrategies.put(tableName, ts);
        return this;
    }

    public TypeGeneratorStrategy ignoreFieldNames(String... fields) {
        for (String f : fields) {
            ignoreFieldNames.add(f.trim());
        }
        return this;
    }

    public TypeGeneratorStrategy includeTableNames(String... tables) {
        if(tables != null) {
            includeTableNames.addAll(Arrays.asList(tables));
        }

        return this;
    }

    public TypeGeneratorStrategy excludeTableNames(String... tables) {
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
        String name = null;
        if(interfaceNameConverter != null) {
            name = interfaceNameConverter.apply(this, tableName);
        }
        return StringUtils.isEmpty(name) ? StringUtils.toCamelCase(tableName) : name;
    }

    public String convertRecordName(String tableName) {
        String name = null;
        if(recordNameConverter != null) {
            name = recordNameConverter.apply(this, tableName);
        }
        return StringUtils.isEmpty(name) ? convertInterfaceName(tableName) + "Record" : name;
    }

    public String convertPojoName(String tableName) {
        String name = null;
        if(pojoNameConverter != null) {
            name = pojoNameConverter.apply(this, tableName);
        }
        return StringUtils.isEmpty(name) ? convertInterfaceName(tableName) + "Entity" : name;
    }

    public String convertRepoName(String tableName) {
        String name = null;
        if(repoNameConverter != null) {
            name = repoNameConverter.apply(this, tableName);
        }
        return StringUtils.isEmpty(name) ? convertInterfaceName(tableName) + "Repo" : name;
    }

    public String getTypePackageName() {
        return getPackageName() + ".type";
    }

    public String getRepoPakcgaeName() {
        return getPackageName() + ".repo";
    }

    public String getServicePackageName() {
        return getPackageName() + ".service";
    }

    public String getApiPackageName() {
        return getPackageName() + ".api";
    }

    public String subpackage(String tableName) {
        TableStrategy ts = getTableStrategy(tableName);
        return ts == null ? "" : ts.getSubPackageName();
    }

    public String interfacePackageName(String tableName) {
        return getTypePackageName() + subpackage(tableName);
    }

    public String pojoPackageName(String tableName) {
        return getTypePackageName() + ".pojos" + subpackage(tableName);
    }

    public String repoPackageName(String tableName) {
        return getTypePackageName() + ".repos" + subpackage(tableName);
    }

    public String recordPackageName(String tableName) {
        return getTypePackageName() + ".records" + subpackage(tableName);
    }

    public String jooqRecordPackageName(String tableName) {
        return getJooqPackageName() + ".tables.records";
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

    public String repoClazzName(String tableName) {
        return convertRepoName(tableName);
    }

    public ClassName interfaceClassName(String tableName) {
        return ClassName.get(interfacePackageName(tableName), interfaceClazzName(tableName));
    }

    public ClassName recordClassName(String tableName) {
        return ClassName.get(recordPackageName(tableName), recordClazzName(tableName));
    }

    public ClassName jooqRecordClassName(String tableName) {
        return ClassName.get(jooqRecordPackageName(tableName), recordClazzName(tableName));
    }

    public ClassName pojoClassName(String tableName) {
        return ClassName.get(pojoPackageName(tableName), pojoClazzName(tableName));
    }

    public ClassName repoClassName(String tableName) {
        return ClassName.get(repoPackageName(tableName), repoClazzName(tableName));
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
    public static interface NameConverter extends BiFunction<TypeGeneratorStrategy, String, String> {

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

    public boolean isGenerateRepo(String tableName) {
        return generateRepo;
    }

}
