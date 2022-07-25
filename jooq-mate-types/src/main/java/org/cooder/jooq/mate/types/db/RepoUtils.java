package org.cooder.jooq.mate.types.db;

import static org.jooq.impl.DSL.jsonArray;

import org.cooder.jooq.mate.types.AbstractRecord;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.JSONEntry;
import org.jooq.JSONObjectNullStep;
import org.jooq.Record;
import org.jooq.Table;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.tools.StringUtils;

public final class RepoUtils {

    @SuppressWarnings("rawtypes")
    public static Record newRecord(TableImpl table, Object entity) {
        Record rec = table.newRecord();
        rec.from(entity);
        return rec;
    }

    public static JSONObjectNullStep<JSON> jsonObject(Record rec) {
        return jsonObject(rec.fields());
    }

    @SuppressWarnings("rawtypes")
    public static JSONObjectNullStep<JSON> jsonObject(Table table) {
        return jsonObject(table.fields());
    }

    @SuppressWarnings("rawtypes")
    public static JSONObjectNullStep<JSON> jsonObject(Field<?>[] fs) {
        JSONEntry[] entries = new JSONEntry[fs.length];
        for (int i = 0; i < fs.length; i++) {
            entries[i] = DSL.jsonEntry(StringUtils.toCamelCaseLC(fs[i].getName()), fs[i]);
        }

        return DSL.jsonObject(entries);
    }

    public static Field<JSON> jsonArrayAgg(Table<?> table) {
        return jsonArrayAgg(jsonObject(table));
    }

    public static Field<JSON> jsonArrayAgg(Record rec) {
        return jsonArrayAgg(jsonObject(rec));
    }

    public static Field<JSON> jsonArrayAgg(Field<?>... fs) {
        return jsonArrayAgg(jsonObject(fs));
    }

    public static Field<JSON> jsonArrayAgg(JSONObjectNullStep<JSON> s) {
        return DSL.coalesce(DSL.jsonArrayAgg(s), jsonArray());
    }

    public static Field<JSON> jsonArrayAgg(Table<?> table, AbstractRecord.Field[] fs) {
        return jsonArrayAgg(jsonObject(table, fs));
    }

    @SuppressWarnings("rawtypes")
    public static JSONObjectNullStep<JSON> jsonObject(Table<?> table, AbstractRecord.Field[] fs) {
        JSONEntry[] entries = new JSONEntry[fs.length];
        for (int i = 0; i < fs.length; i++) {
            entries[i] = DSL.jsonEntry(fs[i].getName(), table.field(fs[i].getDbName()));
        }

        return DSL.jsonObject(entries);
    }

    public static Field<?>[] fields(Table<?> table, AbstractRecord.Field[] fs) {
        Field<?>[] fields = new Field<?>[fs.length];
        for (int i = 0; i < fs.length; i++) {
            fields[i] = table.field(fs[i].getDbName());
        }
        return fields;
    }

    private RepoUtils() {
    }
}
