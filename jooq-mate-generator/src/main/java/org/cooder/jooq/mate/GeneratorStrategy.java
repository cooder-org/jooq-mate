package org.cooder.jooq.mate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratorStrategy {
    protected String directory = "./target/generated-sources/";
}

class RepoGeneratorStrategy extends GeneratorStrategy {
}

class ServiceGeneratorStrategy extends GeneratorStrategy {

}
