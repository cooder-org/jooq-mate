package org.cooder.jooq.mate;

import java.util.ArrayList;
import java.util.List;

import org.cooder.jooq.mate.ConfigurationParser.Config;
import org.cooder.jooq.mate.ConfigurationParser.JooqConfig;
import org.cooder.jooq.mate.ConfigurationParser.TableConfig;
import org.jooq.DSLContext;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.MatcherRule;
import org.jooq.meta.jaxb.MatcherTransformType;
import org.jooq.meta.jaxb.Matchers;
import org.jooq.meta.jaxb.MatchersTableType;
import org.jooq.meta.jaxb.Strategy;
import org.jooq.meta.jaxb.Target;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JooqGenerator {

    private final Configuration configuration = new Configuration();

    public JooqGenerator(Config conf) {
        JooqConfig jc = conf.jooqConfig();
        String includeTables = String.join("|", conf.tableNames());

        List<MatchersTableType> tableStrategies = buildTableStrategies(conf.tables());

        configuration.withJdbc(new Jdbc()
                .withDriver("com.mysql.cj.jdbc.Driver")
                .withUrl(jc.getUrl())
                .withUser(jc.getUser())
                .withPassword(jc.getPassword()))
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName("org.jooq.meta.mysql.MySQLDatabase")
                                .withInputSchema(jc.getInputSchema())
                                .withOutputSchemaToDefault(true)
                                .withIncludes(includeTables))
                        .withGenerate(new Generate()
                                .withDaos(jc.isGenerateDaos())
                                .withRecords(jc.isGenerateRecords())
                                .withPojos(jc.isGeneratePojos()))
                        .withTarget(new Target()
                                .withPackageName(jc.getPackageName())
                                .withDirectory(conf.jooqDirectory()))
                        .withStrategy(new Strategy()
                                .withMatchers(new Matchers()
                                        .withTables(tableStrategies))));
    }

    public void executeDDL(JooqConfig jc, List<String> sqls) {
        log.info("generate database tables: ");
        sqls.forEach(log::info);

        DSLContext db = DSL.using(jc.getUrl(), jc.getUser(), jc.getPassword());
        db.batch(sqls.toArray(new String[0])).execute();

    }

    public void generate() throws Exception {
        new GenerationTool().run(configuration);
    }

    private List<MatchersTableType> buildTableStrategies(List<TableConfig> tables) {
        List<MatchersTableType> ret = new ArrayList<>();
        tables.forEach(tc -> {
            MatchersTableType type = new MatchersTableType();
            type.withExpression(tc.getTableName());
            type.withDaoClass(new MatcherRule().withTransform(MatcherTransformType.PASCAL).withExpression(tc.getJooqDaoClass()));
            type.withPojoClass(new MatcherRule().withTransform(MatcherTransformType.PASCAL).withExpression(tc.getJooqPojoClass()));
            type.withPojoImplements(tc.getJooqPojoImplements());
            ret.add(type);
        });

        return ret;
    }

}
