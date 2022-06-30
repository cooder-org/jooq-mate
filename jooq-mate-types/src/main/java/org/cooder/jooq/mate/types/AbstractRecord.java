package org.cooder.jooq.mate.types;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.cooder.jooq.mate.utils.TypeUtils;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

public abstract class AbstractRecord<T> {
    private Field[] fields;
    private Object[] values;
    private Object[] originals;
    private BitSet changed;
    private boolean dirty;
    private final Class<T> pojoClass;

    private final Map<String, Integer> indexMap = new HashMap<>();

    protected AbstractRecord(Class<T> pojoClass, Field[] fields) {
        int size = fields.length;
        this.pojoClass = pojoClass;
        this.fields = fields;
        this.values = new Object[size];
        this.originals = new Object[size];
        this.changed = new BitSet(size);

        for (int i = 0; i < fields.length; i++) {
            indexMap.put(fields[i].name, i);
        }
    }

    public Field[] fileds() {
        return fields;
    }

    public Object get(String name) {
        return get(index(name));
    }

    public void set(String name, Object value) {
        int index = index(name);
        set(index, value);
    }

    public boolean changed() {
        return !changed.isEmpty();
    }

    public boolean changed(String name) {
        return changed.get(index(name));
    }

    public Object get(int index) {
        return this.values[index];
    }

    public void set(int index, Object value) {
        if(!Objects.equals(this.values[index], value)) {
            this.values[index] = value;
        }

        if(!Objects.equals(this.values[index], this.originals[index])) {
            this.changed.set(index);
        } else {
            this.changed.clear(index);
        }
    }

    public T toPojo() {
        T pojo = newPojo();
        for (Field f : fileds()) {
            String name = f.getName();
            TypeUtils.setValue(pojo, name, get(name));
        }
        return pojo;
    }

    public AbstractRecord<T> fromPojo(T pojo) {
        if(!dirty) {
            for (int i = 0; i < fields.length; i++) {
                Object value = TypeUtils.getValue(pojo, fields[i].name);
                iset(i, value);
            }
        } else {
            for (int i = 0; i < fields.length; i++) {
                Object value = TypeUtils.getValue(pojo, fields[i].name);
                set(i, value);
            }
        }
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.getClass().getName()).append(" {\n");
        for (int i = 0; i < fields.length; i++) {
            String str = String.format("  %s (old: %s, new: %s, changed: %s)", fields[i].name, originals[i], values[i], changed.get(i));
            sb.append(str).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }

    public String diff() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if(changed.get(i)) {
                Field f = fields[i];
                String str = String.format("`%s` changed, from `%s` to `%s`\n", f.name, originals[i], values[i]);
                sb.append(str);
            }
        }
        return sb.toString();
    }

    private void iset(int index, Object value) {
        this.originals[index] = value;
        this.values[index] = value;
        this.dirty = true;
    }

    private int index(String name) {
        return indexMap.get(name);
    }

    private T newPojo() {
        T pojo = null;
        try {
            pojo = pojoClass.newInstance();
        } catch (Exception e) {
            // ignore
        }
        return pojo;
    }

    @Getter
    @SuperBuilder
    public static class Field {
        private final String name;
        private final String desc;
        private final Class<?> type;
        private boolean uniqKey;
    }
}
