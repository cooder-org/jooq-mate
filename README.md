# jooq-mate

![build](https://github.com/cooder-org/jooq-mate/actions/workflows/maven.yml/badge.svg)
[![codecov](https://codecov.io/gh/cooder-org/jooq-mate/branch/main/graph/badge.svg?token=0L2AU184LV)](https://codecov.io/gh/cooder-org/jooq-mate)
[![Maven Central](https://img.shields.io/maven-central/v/org.cooder/jooq-mate.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.cooder%22%20AND%20a:%22jooq-mate%22)

## quick start

0、config (maven)  
add `jooq-mate-types` for using generated code.

```xml
<dependency>
  <groupId>org.cooder</groupId>
  <artifactId>jooq-mate-types</artifactId>
  <version>${version}</version>
</dependency>
```

add `jooq-mate-generator` for programmatic generating code.
```xml
<dependency>
  <groupId>org.cooder</groupId>
  <artifactId>jooq-mate-generator</artifactId>
  <version>${version}</version>
</dependency>
```

1、usage

- config your data models and jooq-mate settings. [config file template](./jooq-mate-generator/src/test/resources/jooq-mate-config.xlsx)

- generate db tables and code. 
```sh

java -jar jooq-mate-generator.jar -ct -jt <file>

//      <file>              Config file path
//      -ct, --createTable  Create tables in the specified database
//      -jc, --jooqCodegen  Generate jooq's Dao, Record, Pojo...

```





