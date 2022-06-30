package org.cooder.jooq.mate;

import java.io.File;

import org.cooder.jooq.mate.TypeGeneratorStrategy.TableStrategy;
import org.cooder.jooq.mate.db.Tables;
import org.jooq.tools.StringUtils;
import org.junit.Assert;
import org.junit.Test;

import picocli.CommandLine;

public class JooqGeneratorMateTest {

    @Test
    public final void testGenerate() {
        String directory = "./target/generated-sources/";
        String packageName = "org.cooder.jooq";

        boolean hasException = false;
        try {
            TableStrategy houseStategy = new TableStrategy()
                    .setSubPackageName(".house")
                    .setGeneratedInterfaceSuperInterfaces(
                            new String[] { "java.util.List<>", "java.lang.Serializable", "java.util.function.Function<String,String>" })
                    .setGeneratedPojoSuperClass("org.cooder.type.pojos.EntityBase");

            TypeGeneratorStrategy strategy = new TypeGeneratorStrategy()
                    .withDirectory(directory)
                    .withPackageName(packageName)
                    .generatePojoWithLombok(true)
                    .generateInterface(true)
                    .generatePojo(true)
                    .generateRecord(true)
                    .withInterfaceNameConverter((sg, tableName) -> {
                        String name = StringUtils.toCamelCase(tableName);
                        if(name.equals("Space")) {
                            return "HouseSpace";
                        }

                        return name;
                    })
                    .withPojoNameConverter((sg, tableName) -> {
                        return sg.interfaceClazzName(tableName) + "Entity";
                    })
                    .withRecordNameConverter((sg, tableName) -> {
                        return sg.interfaceClazzName(tableName) + "Record";
                    })
                    .ignoreFieldNames("id", "cuid", "cu_name", "muid", "mu_name", "ctime", "mtime")
                    .includeTableNames(
                            Tables.HOUSE_LAYOUT.getName(),
                            Tables.SPACE.getName())
                    .withTableStrategy(Tables.HOUSE_LAYOUT.getName(), houseStategy)
                    .withTableStrategy(Tables.SPACE.getName(), houseStategy);
            strategy.setJooqPackageName("org.cooder.jooq.db");
            TypeGenerator generator = new TypeGenerator(strategy);

            generator.generateTables(Tables.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
            hasException = true;
        }

        Assert.assertEquals(false, hasException);
        checkFileExistAndRemove(directory + P(packageName + ".type.house"), "HouseLayout.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.records.house"), "HouseLayoutRecord.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.pojos.house"), "HouseLayoutEntity.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.house"), "HouseSpace.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.records.house"), "HouseSpaceRecord.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.pojos.house"), "HouseSpaceEntity.java");
    }

    private void checkFileExistAndRemove(String dir, String name) {
        File f = new File(dir + File.separator + name);
        Assert.assertEquals(true, f.exists());
        f.delete();
    }

    private String P(String packageName) {
        return packageName.replace('.', '/');
    }

    @Test
    public final void testGenerateByConfig() {
        String directory = "./target/generated-sources/";
        String packageName = "org.cooder.jooq";

        String resourceName = "jooq-mate-config.xlsx";

        String path = getClass().getClassLoader().getResource(resourceName).getFile();

        File file = new File(path);

        String[] args = new String[] { file.getAbsolutePath() };
        new CommandLine(new MateGeneratorTool()).execute(args);

        checkFileExistAndRemove(directory + P(packageName + ".type.house"), "HouseLayoutTest.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.records.house"), "HouseLayoutTestRecord.java");
        checkFileExistAndRemove(directory + P(packageName + ".type.pojos.house"), "HouseLayoutTestEntity.java");
        checkFileExistAndRemove(directory + P(packageName + ".repo.house"), "HouseLayoutTestRepo.java");
    }
}
