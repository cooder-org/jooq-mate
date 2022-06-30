package org.cooder.jooq.mate;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

import org.cooder.jooq.mate.ConfigurationParser.Config;
import org.cooder.jooq.mate.ConfigurationParser.JooqMateConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig.FieldConfig;
import org.cooder.jooq.mate.TypeGeneratorStrategy.TableStrategy;
import org.jooq.Field;
import org.jooq.impl.TableImpl;
import org.jooq.tools.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TypeGenerator implements Generator {
    private TypeGeneratorStrategy strategy;

    public TypeGenerator() {
        this(new TypeGeneratorStrategy());
    }

    public TypeGenerator(TypeGeneratorStrategy strategy) {
        this.strategy = strategy;
    }

    public void generate(final Config conf) {
        withConfig(conf);
        List<TableConfig> tbs = conf.tables();
        for (TableConfig tc : tbs) {
            generate(new TableConfigMeta(tc));
        }
    }

    public void generateTables(String tablesClassName) throws Exception {
        java.lang.reflect.Field[] fs = Class.forName(tablesClassName).getDeclaredFields();
        for (java.lang.reflect.Field f : fs) {
            if((f.getModifiers() & java.lang.reflect.Modifier.PUBLIC) > 0) {
                Object table = f.get(null);
                if(table instanceof TableImpl) {
                    generate(new JooqTableMeta((TableImpl<?>) table));
                }
            }
        }
    }

    @Override
    public void generate(TableMeta table) {
        if(ignoreTable(table)) {
            return;
        }

        TypeInterfaceGenerator.of(strategy).generate(table);
        TypeRecordGenerator.of(strategy).generate(table);
        TypePojoGenerator.of(strategy).generate(table);
    }

    private boolean ignoreTable(TableMeta table) {
        return this.strategy.isIgnoreTable(table.getName());
    }

    private void withConfig(final Config conf) {
        JooqMateConfig mc = conf.mateConfig;
        this.strategy.withDirectory(mc.getDirectory())
                .withPackageName(mc.getPackageName())
                .ignoreFieldNames(mc.getIgnoreFieldNames())
                .includeTableNames(mc.getIncludeTableNames())
                .excludeTableNames(mc.getExcludeTableNames())
                .generateInterface(mc.isGenerateInterface())
                .generateRecord(mc.isGenerateRecord())
                .generatePojo(mc.isGeneratePojo())
                .generatePojoWithLombok(mc.isGeneratePojoWithLombok())
                .withInterfaceNameConverter((s, tableName) -> conf.getTableConfig(tableName).getJooqmateInterfaceName())
                .withRecordNameConverter((s, tableName) -> conf.getTableConfig(tableName).getJooqmateRecordName())
                .withPojoNameConverter((s, tableName) -> conf.getTableConfig(tableName).getJooqmatePojoName());

        for (TableConfig tc : conf.tables) {
            this.strategy.withTableStrategy(tc.getTableName(), new TableStrategy()
                    .setSubPackageName(tc.getJooqmateSubpackage())
                    .ignoreFieldNames(tc.getJooqmateIgnoreFieldNames())
                    .setGeneratedInterfaceSuperInterfaces(tc.getJooqmateInterfaceSupers())
                    .setGeneratedPojoSuperClass(tc.getJooqmatePojoSuperClass()));
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
            return tableConfig.getTableName();
        }

        @Override
        public String getComment() {
            return String.format("%s<br>\n\n%s", tableConfig.getTableNameDesc(), tableConfig.getTableDesc());
        }

        @Override
        public FieldMeta[] fields() {
            List<FieldConfig> fs = tableConfig.getFields();
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
            return fieldConfig.getFieldName();
        }

        @Override
        public Class<?> getType() {
            Class<?> clazz = null;
            String dataType = fieldConfig.getDataType().toUpperCase();
            if(dataType.contains("BIGINT")) {
                clazz = Long.class;
            } else if(dataType.contains("INT")) {
                clazz = Integer.class;
            } else if(dataType.contains("CHAR") || dataType.contains("TEXT")) {
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
            sb.append(fieldConfig.getFieldNameDesc()).append("\n");
            if(!StringUtils.isEmpty(fieldConfig.getEnums())) {
                sb.append('\n');
                sb.append("取值说明: ").append(fieldConfig.getEnums()).append("\n");
                hasMore = true;
            }
            if(!StringUtils.isEmpty(fieldConfig.getExample())) {
                if(!hasMore) {
                    sb.append('\n');
                }
                sb.append("例如:  ").append(fieldConfig.getExample()).append("\n");
            }

            return sb.toString();
        }

        @Override
        public String getNameDesc() {
            return fieldConfig.getFieldNameDesc();
        }

        @Override
        public String getEnums() {
            return fieldConfig.getEnums();
        }
    }
}
