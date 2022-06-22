# jooq-mate


## Usage
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