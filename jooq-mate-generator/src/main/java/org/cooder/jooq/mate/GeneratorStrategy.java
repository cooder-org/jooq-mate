package org.cooder.jooq.mate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GeneratorStrategy {
    protected String directory = "./target/generated-sources/";
    protected String packageName = "org.cooder.jooq.generate";


}

class RepoGeneratorStrategy extends GeneratorStrategy {
    public String repoPackage() {
        return getPackageName() + ".repo";
    }
}
