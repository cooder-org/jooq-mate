package org.cooder.jooq.mate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

import org.jooq.tools.StringUtils;

import com.squareup.javapoet.ClassName;

import lombok.Data;
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

    private UnaryOperator<String> interfaceNameCoverter;

    private UnaryOperator<String> pojoNameCoverter;

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

    public GeneratorStrategy withInterfaceNameConverter(UnaryOperator<String> tableNameCoverter) {
        this.interfaceNameCoverter = tableNameCoverter;
        return this;
    }

    public GeneratorStrategy withPojoNameCoverter(UnaryOperator<String> pojoNameCoverter) {
        this.pojoNameCoverter = pojoNameCoverter;
        return this;
    }

    public GeneratorStrategy withtableStrategy(String tableName, TableStrategy ts) {
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

    public List<ClassName> getGeneratedInterfaceSuperInterfaces(String tableName) {
        String[] ss = new String[0];
        TableStrategy ts = getTableStrategy(tableName);
        if(ts != null) {
            ss = ts.getGeneratedInterfaceSuperInterfaces();
        }

        List<ClassName> ret = new ArrayList<>();
        for (String s : ss) {
            ret.add(ClassName.bestGuess(s));
        }
        return ret;
    }

    public ClassName getPojoSuperClass(String tableName) {
        String sc = "";
        TableStrategy ts = getTableStrategy(tableName);
        if(ts != null) {
            sc = ts.getGeneratedPojoSuperClass();
        }

        if(!StringUtils.isEmpty(sc)) {
            return ClassName.bestGuess(sc);
        } else {
            return null;
        }
    }

    public TableStrategy getTableStrategy(String tableName) {
        return tableStrategies.get(tableName);
    }

    public String convertInterfaceName(String tableName) {
        if(interfaceNameCoverter != null) {
            return interfaceNameCoverter.apply(tableName);
        }
        return StringUtils.toCamelCase(tableName);
    }

    public String convertPojoName(String tableName) {
        if(pojoNameCoverter != null) {
            return pojoNameCoverter.apply(tableName);
        }
        return convertInterfaceName(tableName);
    }

    @Data
    @Accessors(chain = true)
    public static class TableStrategy {
        private String subPackageName = "";
        private Set<String> ignoreFieldNames = new HashSet<>();

        private Boolean generatePojoWithLombok;
        private Boolean generateInterface;
        private Boolean generateRecord;
        private Boolean generatePojo;

        private String[] generatedInterfaceSuperInterfaces = new String[0];
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
