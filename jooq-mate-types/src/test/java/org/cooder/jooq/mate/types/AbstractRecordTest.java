package org.cooder.jooq.mate.types;

import org.cooder.jooq.mate.types.AbstractRecord.Field;
import org.junit.Assert;
import org.junit.Test;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public class AbstractRecordTest {

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class RecordEntity {
        String houseLayoutCode;
    }

    public static class RecordImpl extends AbstractRecord<RecordEntity> {
        public static final AbstractRecord.Field[] FIELDS = new Field[] {
                AbstractRecord.Field.builder().name("houseLayoutCode").type(String.class).desc("户型code").uniqKey(true).build(),
        };

        public RecordImpl() {
            super(RecordEntity.class, FIELDS);
        }

        /**
         * 获取`户型code`
         */
        public String getHouseLayoutCode() {
            return (String) get(0);
        }

        /**
         * 设置`户型code`
         */
        public void setHouseLayoutCode(String houseLayoutCode) {
            set(0, houseLayoutCode);
        }
    }

    @Test
    public final void testAbstractRecord() {
        Assert.assertEquals(true, RecordImpl.FIELDS[0].isUpdatable());

        RecordImpl rec = new RecordImpl();
        RecordEntity pojo = RecordEntity.builder().houseLayoutCode("1001").build();
        rec.fromPojo(pojo);

        Assert.assertEquals("1001", rec.getHouseLayoutCode());
        Assert.assertEquals(pojo, rec.toPojo());
        System.out.println(rec);

        rec.setHouseLayoutCode("1002");
        Assert.assertEquals(true, rec.changed());
        Assert.assertEquals(true, rec.changed("houseLayoutCode"));
        Assert.assertEquals("1002", rec.getHouseLayoutCode());
        System.out.println(rec);

        rec.set("houseLayoutCode", "1001");
        Assert.assertEquals(false, rec.changed());
        Assert.assertEquals(false, rec.changed("houseLayoutCode"));
        Assert.assertEquals("1001", rec.getHouseLayoutCode());
        System.out.println(rec);

        Field[] fields = rec.fileds();
        Assert.assertEquals(1, fields.length);
        Assert.assertEquals(RecordImpl.FIELDS[0].getName(), fields[0].getName());
        Assert.assertEquals(RecordImpl.FIELDS[0].getDesc(), fields[0].getDesc());
        Assert.assertEquals(RecordImpl.FIELDS[0].getType(), fields[0].getType());

        Field field = rec.field("houseLayoutCode");
        Assert.assertEquals(RecordImpl.FIELDS[0].getName(), field.getName());
        Assert.assertEquals(RecordImpl.FIELDS[0].getDesc(), field.getDesc());
        Assert.assertEquals(RecordImpl.FIELDS[0].getType(), field.getType());
    }

    @Test
    public final void testAbstractRecordDirty() {
        RecordImpl rec = new RecordImpl();
        RecordEntity pojo = RecordEntity.builder().houseLayoutCode("1001").build();
        rec.fromPojo(pojo);

        RecordEntity pojo2 = RecordEntity.builder().houseLayoutCode("1002").build();
        rec.fromPojo(pojo2);
        Assert.assertEquals(true, rec.changed());
        Assert.assertEquals(true, rec.changed("houseLayoutCode"));
        Assert.assertEquals("1002", rec.getHouseLayoutCode());
        System.out.println(rec);
        System.out.println(rec.diff());
    }

}
