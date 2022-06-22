package org.cooder.jooq;

import org.cooder.jooq.mate.TypeGenerator;
import org.junit.Test;

public class JooqGeneratorMateTest {

    @Test
    public final void testGenerate() {
        boolean hasException = false;
        TypeGenerator generator = new TypeGenerator();
        try {
            generator.generateTables("org.cooder.db.Tables");
        } catch (Exception e) {
            System.err.println(e);
            hasException = true;
        }

        // Assert.assertEquals(false, hasException);
    }

}
