package org.cooder.jooq.mate;

import java.util.List;

import org.cooder.jooq.mate.ConfigurationParser.Config;
import org.cooder.jooq.mate.ConfigurationParser.JooqConfig;
import org.jooq.DSLContext;
import org.jooq.codegen.GenerationTool;
import org.jooq.impl.DSL;
import org.jooq.meta.jaxb.Configuration;
import org.jooq.meta.jaxb.Database;
import org.jooq.meta.jaxb.Generate;
import org.jooq.meta.jaxb.Generator;
import org.jooq.meta.jaxb.Jdbc;
import org.jooq.meta.jaxb.Target;

public class JooqGenerator {

    private final Configuration configuration = new Configuration();

    public JooqGenerator(Config conf) {
        JooqConfig jc = conf.jooqConfig();
        String includeTables = String.join("|", conf.tableNames());
        configuration.withJdbc(new Jdbc()
                .withDriver("com.mysql.cj.jdbc.Driver")
                .withUrl(jc.url)
                .withUser(jc.user)
                .withPassword(jc.password))
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName("org.jooq.meta.mysql.MySQLDatabase")
                                .withIncludes(includeTables))
                        .withGenerate(new Generate()
                                .withDaos(jc.generateDaos)
                                .withRecords(jc.generateRecords)
                                .withPojos(jc.generatePojos))
                        .withTarget(new Target()
                                .withPackageName(jc.packageName)
                                .withDirectory(jc.directory)));
    }

    public void executeDDL(JooqConfig jc, List<String> sqls) {
        System.out.println("generate database tables: ");
        sqls.forEach(System.out::println);

        DSLContext db = DSL.using(jc.url, jc.user, jc.password);
        db.batch(sqls.toArray(new String[0])).execute();

    }

    public void generate() throws Exception {
        new GenerationTool().run(configuration);
    }
}
