package org.cooder.jooq.mate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.cooder.jooq.mate.ConfigurationParser.Config;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig.UniqKey;

import com.alibaba.excel.util.StringUtils;

public class SqlGenerator {

    public List<String> generate(Config conf) {
        List<String> sqls = new ArrayList<>();

        List<TableConfig> tcs = conf.tables();
        for (TableConfig tc : tcs) {
            sqls.addAll(generate(tc));
        }
        return sqls;
    }

    private List<String> generate(TableConfig tc) {
        List<String> sqls = new ArrayList<>();
        int shardingCount = tc.getShardingCount();

        sqls.addAll(generate(tc, 0));
        for (int i = 0; i < shardingCount; i++) {
            sqls.addAll(generate(tc, i + 1));
        }

        return sqls;
    }

    private List<String> generate(TableConfig tc, int shardingIndex) {
        List<String> sqls = new ArrayList<>();
        String tableName = shardingIndex == 0 ? tc.getTableName() : String.format("%s_%02d", tc.getTableName(), shardingIndex);

        sqls.add(String.format("DROP TABLE IF EXISTS `%s`;", tableName));

        StringBuilder sb = new StringBuilder();

        if(shardingIndex == 0) {
            sb.append(String.format("CREATE TABLE `%s` (\n", tableName));

            tc.getFields().forEach(fc -> sb.append(String.format(
                    "  `%s` %s NOT NULL %s %s COMMENT '%s',\n",
                    fc.getFieldName(),
                    fc.getDataType(),
                    StringUtils.isEmpty(fc.getDefaultValue()) ? "" : "DEFAULT " + fc.getDefaultValue(),
                    fc.isAutoIncrement() ? "AUTO_INCREMENT" : "",
                    fc.getComment())));

            if(!StringUtils.isEmpty(tc.getPrimaryKey())) {
                sb.append(String.format("\n  PRIMARY KEY (`%s`),", tc.getPrimaryKey()));
            }

            if(Objects.nonNull(tc.getUniqueKey())) {
                UniqKey uk = tc.getUniqueKey();
                String[] vs = uk.getValue().split(",");
                String value = Arrays.asList(vs).stream().map(v -> String.format("`%s`", v.trim())).collect(Collectors.joining(","));
                sb.append(String.format("\n  UNIQUE KEY %s (%s),", uk.getName(), value));
            }

            if(sb.charAt(sb.length() - 1) == ',') {
                sb.deleteCharAt(sb.length() - 1);
            }

            sb.append("\n");
            if(!StringUtils.isEmpty(tc.getAutoIncrement())) {
                sb.append(String.format(") ENGINE=%s AUTO_INCREMENT=%s DEFAULT CHARSET=%s COMMENT=\"%s\";\n",
                        tc.getEngine(), tc.getAutoIncrement(), tc.getDefaultCharset(), tc.getTableDesc()));
            } else {
                sb.append(String.format(") ENGINE=%s DEFAULT CHARSET=%s COMMENT=\"%s\";\n",
                        tc.getEngine(), tc.getDefaultCharset(), tc.getTableDesc()));
            }
        } else {
            sb.append(String.format("CREATE TABLE `%s` like `%s`;\n", tableName, tc.getTableName()));
        }

        sqls.add(sb.toString());

        return sqls;
    }
}
