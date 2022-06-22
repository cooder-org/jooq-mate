# jooq-mate

![build](https://github.com/cooder-org/jooq-mate/actions/workflows/maven.yml/badge.svg)
[![codecov](https://codecov.io/gh/cooder-org/jooq-mate/branch/main/graph/badge.svg?token=0L2AU184LV)](https://codecov.io/gh/cooder-org/jooq-mate)
[![Maven Central](https://img.shields.io/maven-central/v/org.cooder/jooq-mate.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.cooder%22%20AND%20a:%22jooq-mate%22)

## quick start

0、config (maven)  
```xml
<dependency>
  <groupId>org.cooder</groupId>
  <artifactId>jooq-mate</artifactId>
  <version>${version}</version>
</dependency>
```

1、usage (please refer to test cases for more)
```java
        //
        // Tables.java is jooq:codegen generated class
        //
        TableStrategy houseStategy = new TableStrategy().setSubPackageName(".house");

        GeneratorStrategy strategy = new GeneratorStrategy()
                .withDirectory("./src/codegen/java/")
                .withPackageName("org.cooder.jooqmate.type")
                .generatePojoWithLombok(false)
                .generateInterface(false)
                .generatePojo(false)
                .generateRecord(true)
                .withInterfaceNameConverter(tableName -> {
                    String name = StringUtils.toCamelCase(tableName);
                    if(name.equals("DesignPlan")) {
                        return "Plan";
                    }

                    return name;
                })
                .withPojoNameCoverter(tableName -> {
                    String name = StringUtils.toCamelCase(tableName);
                    if(name.equals("DesignPlan")) {
                        return "Plan";
                    }
                    return name;
                })
                .ignoreFieldNames("id", "cuid", "cu_name", "muid", "mu_name", "ctime", "mtime")
                .includeTableNames(Tables.SPACE.getName())
                .withtableStrategy(Tables.SPACE.getName(), houseStategy);

        TypeGenerator generator = new TypeGenerator(strategy);

        generator.generateTables(Tables.class.getName());
```