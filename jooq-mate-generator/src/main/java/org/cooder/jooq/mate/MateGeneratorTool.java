package org.cooder.jooq.mate;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;

import org.cooder.jooq.mate.ConfigurationParser.Config;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "MateGeneratorTool")
public class MateGeneratorTool implements Callable<Integer> {

    @Parameters(index = "0", description = "config file")
    private String file;

    @Option(names = { "-ct", "--createTable" })
    private boolean createTable = false;

    @Option(names = { "-jc", "--jooqCodegen" })
    private boolean jooqCodegen = false;

    @Option(names = { "-cpf", "--createProjectFrom" }, paramLabel = "FILE", description = "path of the project template dir")
    private File projectTemplate;

    public static void main(String[] args) {
        int exitCode = new CommandLine(new MateGeneratorTool()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        generate(file);

        return 0;
    }

    private void generate(String file) throws Exception {
        if(projectTemplate != null && !projectTemplate.exists()) {
            System.err.println("unexist: " + projectTemplate.getAbsolutePath());
            return;
        }

        Config conf = new ConfigurationParser().parse(file);
        conf.projectTemplate(projectTemplate);

        new ProjectGenerator().generate(conf);

        List<String> sqls = new SqlGenerator().generate(conf);
        for (String sql : sqls) {
            System.out.println(sql);
        }

        JooqGenerator jooq = new JooqGenerator(conf);
        if(createTable) {
            jooq.executeDDL(conf.jooqConfig(), sqls);
        }



        if(jooqCodegen) {
            jooq.generate();
        }

        new TypeGenerator().generate(conf);

    }
}
