package org.cooder.jooq.mate;

import java.io.File;

import org.cooder.jooq.mate.GeneratorStrategy.TableStrategy;
import org.cooder.jooq.mate.db.Tables;
import org.jooq.tools.StringUtils;
import org.junit.Assert;
import org.junit.Test;

public class JooqGeneratorMateTest {

    @Test
    public final void testGenerate() {
        String directory = "./target/generated-test-sources/";
        String packageName = "org.cooder.jooqmate.type";

        boolean hasException = false;
        try {
            TableStrategy houseStategy = new TableStrategy().setSubPackageName(".house");
            GeneratorStrategy strategy = new GeneratorStrategy()
                    .withDirectory(directory)
                    .withPackageName(packageName)
                    .generatePojoWithLombok(true)
                    .generateInterface(true)
                    .generatePojo(true)
                    .generateRecord(true)
                    .withInterfaceNameConverter(tableName -> {
                        String name = StringUtils.toCamelCase(tableName);
                        if(name.equals("Space")) {
                            return "HouseSpace";
                        }

                        return name;
                    })
                    .ignoreFieldNames("id", "cuid", "cu_name", "muid", "mu_name", "ctime", "mtime")
                    .includeTableNames(
                            Tables.HOUSE_LAYOUT.getName(),
                            Tables.SPACE.getName())
                    .withTableStrategy(Tables.HOUSE_LAYOUT.getName(), houseStategy)
                    .withTableStrategy(Tables.SPACE.getName(), houseStategy);

            TypeGenerator generator = new TypeGenerator(strategy);

            generator.generateTables(Tables.class.getName());
        } catch (Exception e) {
            e.printStackTrace();
            hasException = true;
        }

        Assert.assertEquals(false, hasException);
        checkFileExist(directory + P(packageName + ".house"), "HouseLayout.java");
        checkFileExist(directory + P(packageName + ".records.house"), "HouseLayoutRecord.java");
        checkFileExist(directory + P(packageName + ".pojos.house"), "HouseLayoutEntity.java");
        checkFileExist(directory + P(packageName + ".house"), "HouseSpace.java");
        checkFileExist(directory + P(packageName + ".records.house"), "HouseSpaceRecord.java");
        checkFileExist(directory + P(packageName + ".pojos.house"), "HouseSpaceEntity.java");
    }

    private void checkFileExist(String dir, String name) {
        File f = new File(dir + File.separator + name);
        Assert.assertEquals(true, f.exists());
    }

    private String P(String packageName) {
        return packageName.replace('.', '/');
    }
}
