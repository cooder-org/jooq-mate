package org.cooder.jooq.mate;

import java.util.List;

import org.cooder.jooq.mate.ConfigurationParser.Config;

public class MateGeneratorTool {
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            showUsage();
            return;
        }

        for (String file : args) {
            generate(file);
        }
    }

    private static void generate(String file) throws Exception {
        Config conf = new ConfigurationParser().parse(file);

        List<String> sqls = new SqlGenerator().generate(conf);

        JooqGenerator jooq = new JooqGenerator(conf);
        jooq.executeDDL(conf.jooqConfig(), sqls);
        jooq.generate();

        new TypeGenerator().generate(conf);

    }

    private static void showUsage() {
        System.out.println("Usage : MateGeneratorTool <configuration-file>");
        System.exit(-1);
    }
}
