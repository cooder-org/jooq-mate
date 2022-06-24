package org.cooder.jooq.mate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.cooder.jooq.mate.ConfigurationParser.TableConfig.FieldConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig.UniqKey;
import org.cooder.jooq.mate.utils.TypeUtils;
import org.springframework.beans.BeanUtils;

import com.alibaba.excel.EasyExcelFactory;
import com.alibaba.excel.ExcelReader;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.read.metadata.holder.ReadRowHolder;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConfigurationParser {

    public Config parse(String file) {
        Config config = new Config();

        parseJooqConfig(file, config.jooqConfig());
        parseJooqMateConfig(file, config.mateConfig());
        parseTableConfigs(file, config);

        return config;
    }

    private void parseTableConfigs(String file, Config config) {
        TableConfigListenerImpl listener = new TableConfigListenerImpl();
        ExcelReader reader = EasyExcelFactory.read(file, TableConfig.Header.class, listener).build();
        List<ReadSheet> sheets = reader.excelExecutor().sheetList();
        for (ReadSheet sheet : sheets) {
            listener.reset();
            if(sheet.getSheetName().toLowerCase().startsWith("table")) {
                log.info("process table config: ", sheet.getSheetName());
                reader.read(sheet);
                config.addTable(listener.getTableConfig());
            }
        }
    }

    private void parseJooqConfig(String file, JooqConfig config) {
        SingleTableListenerImpl<JooqConfig.Header> listener = new SingleTableListenerImpl<>();
        ExcelReader reader = EasyExcelFactory.read(file, listener).build();

        ReadSheet sheet = EasyExcelFactory.readSheet("jooq-codegen-config").head(JooqConfig.Header.class).build();
        reader.read(sheet);

        listener.getDatas().forEach(v -> {
            if(v.getConfigValue() == null) {
                return;
            }
            Object value = cast(v.getConfigName(), v.getConfigValue(), config.getClass());
            if(value != null) {
                TypeUtils.setValue(config, v.getConfigName(), value);
            }
        });
    }

    private void parseJooqMateConfig(String file, JooqMateConfig config) {
        SingleTableListenerImpl<JooqMateConfig.Header> listener = new SingleTableListenerImpl<>();
        ExcelReader reader = EasyExcelFactory.read(file, listener).build();

        ReadSheet sheet = EasyExcelFactory.readSheet("jooq-mate-config").head(JooqMateConfig.Header.class).build();
        reader.read(sheet);

        listener.getDatas().forEach(v -> {
            if(v.getConfigValue() == null) {
                return;
            }
            Object value = cast(v.getConfigName(), v.getConfigValue(), config.getClass());
            TypeUtils.setValue(config, v.getConfigName(), value);
        });
    }

    static Object cast(String name, Object value, Class<?> to) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(to, name);
        if(value == null || pd == null) {
            return null;
        }

        Class<?> pt = pd.getPropertyType();
        if(pt == boolean.class || pt == Boolean.class) {
            value = Boolean.valueOf((String) value);
        } else if(pt == String[].class) {
            value = value.toString().split(",");
        } else if(pt == int.class || pt == Integer.class) {
            value = Integer.valueOf(value.toString());
        } else if(pt == UniqKey.class) {
            value = new UniqKey();
        }

        return value;
    }

    @Slf4j
    private static class SingleTableListenerImpl<T> extends AnalysisEventListener<T> {
        @Getter
        private List<T> datas = new ArrayList<>();

        @Override
        public void invoke(T data, AnalysisContext context) {
            log.info("row readed: {}", data);
            datas.add(data);
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // no-op
        }
    }

    @Slf4j
    private static class TableConfigListenerImpl extends AnalysisEventListener<TableConfig.Header> {
        @Getter
        private TableConfig tableConfig;

        private boolean espectFiled;

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public void invoke(TableConfig.Header data, AnalysisContext context) {
            if(data.getConfigName().equals("fieldName")) {
                espectFiled = true;
            } else if(espectFiled) {
                ReadRowHolder readRowHolder = context.readRowHolder();
                Map<Integer, ReadCellData<?>> cellDataMap = (Map) readRowHolder.getCellMap();
                FieldConfig fc = buildFieldConfig(cellDataMap);
                tableConfig.addField(fc);
                log.info("field config readed: {}", fc);
            } else {
                Object value = cast(data.getConfigName(), data.getConfigValue1(), tableConfig.getClass());
                if(value instanceof UniqKey) {
                    ((UniqKey) value).setName(data.getConfigValue1());
                    ((UniqKey) value).setValue(data.getConfigValue2());
                }

                TypeUtils.setValue(tableConfig, data.getConfigName(), value);
                log.info("table config readed: {}", tableConfig);
            }
        }

        public void reset() {
            tableConfig = new TableConfig();
        }

        @Override
        public void doAfterAllAnalysed(AnalysisContext context) {
            // no-op
        }

        private FieldConfig buildFieldConfig(Map<Integer, ReadCellData<?>> dataMap) {
            FieldConfig fc = new FieldConfig();

            Field[] clazzFields = FieldConfig.class.getDeclaredFields();
            for (Field f : clazzFields) {
                String fieldName = f.getName();
                ExcelProperty anno = f.getAnnotation(ExcelProperty.class);
                if(anno != null) {
                    int index = anno.index();
                    ReadCellData<?> cellData = dataMap.get(index);
                    Object value = cellData == null ? null : cellData.getStringValue();
                    if(value != null) {
                        value = cast(fieldName, value, FieldConfig.class);
                        TypeUtils.setValue(fc, fieldName, value);
                    }

                }
            }

            return fc;
        }
    }

    @Getter
    @Accessors(fluent = true)
    public static class Config {
        JooqConfig jooqConfig = new JooqConfig();
        JooqMateConfig mateConfig = new JooqMateConfig();
        List<TableConfig> tables = new ArrayList<>();

        public Config addTable(TableConfig tc) {
            this.tables.add(tc);
            return this;
        }

        public List<String> tableNames() {
            return tables.stream().map(t -> t.tableName.trim()).collect(Collectors.toList());
        }

        public TableConfig getTableConfig(String tableName) {
            return tables.stream().filter(t -> t.tableName.equals(tableName)).findFirst().get();
        }
    }

    @Data
    public static class JooqConfig {
        String sqlDialect;
        String url;
        String user;
        String password;
        String inputSchema;
        String directory;
        String packageName;
        boolean generateRecords;
        boolean generatePojos;
        boolean generateDaos;

        @Data
        public static class Header {
            @ExcelProperty(value = "ConfigName", index = 0)
            String configName;

            @ExcelProperty(value = "ConfigValue", index = 1)
            String configValue;
        }
    }

    @Data
    public static class JooqMateConfig {
        int indent;
        String directory;
        String packageName;
        String[] ignoreFieldNames = new String[0];
        String[] includeTableNames = new String[0];
        String[] excludeTableNames = new String[0];
        boolean generateInterface;
        boolean generateRecord;
        boolean generatePojo;
        boolean generatePojoWithLombok;

        @Data
        public static class Header {
            @ExcelProperty(value = "ConfigName", index = 0)
            String configName;

            @ExcelProperty(value = "ConfigValue", index = 1)
            String configValue;
        }
    }

    @lombok.Data
    public static class TableConfig {
        String tableName;
        String tableNameDesc;
        String tableDesc;
        String primaryKey;
        UniqKey uniqueKey;
        String engine;
        String autoIncrement;
        String defaultCharset;
        int shardingCount;
        String jooqDaoClass;
        String jooqPojoClass;
        String jooqPojoImplements;
        String jooqmateSubpackage;
        String[] jooqmateIgnoreFieldNames = new String[0];
        String jooqmateInterfaceName;
        String[] jooqmateInterfaceSupers = new String[0];
        String jooqmateRecordName;
        String jooqmatePojoName;
        String jooqmatePojoSuperClass;
        String jooqmatePojoImplements;
        List<FieldConfig> fields = new ArrayList<>();

        public TableConfig addField(FieldConfig c) {
            this.fields.add(c);
            return this;
        }

        @Data
        public static class UniqKey {
            String name;
            String value;
        }

        @Data
        public static class FieldConfig {
            @ExcelProperty(index = 0)
            String fieldName;
            @ExcelProperty(index = 1)
            String fieldNameDesc;
            @ExcelProperty(index = 2)
            String dataType;
            @ExcelProperty(index = 3)
            String defaultValue = "";
            @ExcelProperty(index = 4)
            String enums = "";
            @ExcelProperty(index = 5)
            String example = "";
            @ExcelProperty(index = 6)
            boolean autoIncrement;
        }

        @Data
        public static class Header {
            @ExcelProperty(value = "ConfigName", index = 0)
            String configName;

            @ExcelProperty(value = "ConfigValue1", index = 1)
            String configValue1;

            @ExcelProperty(value = "ConfigValue2", index = 2)
            String configValue2;
        }
    }
}
