package org.cooder.jooq.mate;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.BeanUtils;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

public abstract class AbstractRecord<T> {
    private Field[] fields;
    private Object[] values;
    private Object[] originals;
    private BitSet changed;
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
            setValue(pojo, name, get(name));
        }
        return pojo;
    }

    public AbstractRecord<T> fromPojo(T pojo) {
        for (int i = 0; i < fields.length; i++) {
            Object value = getValue(pojo, fields[i].name);
            iset(i, value);
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

    private void iset(int index, Object value) {
        this.originals[index] = value;
        this.values[index] = value;
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

    @Data
    @SuperBuilder
    @NoArgsConstructor
    public static class Field {
        String name;
        String desc;
        Class<?> type;
    }

    private static Object getValue(Object pojo, String fieldName) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(pojo.getClass(), fieldName);
        if(pd == null) {
            return null;
        }

        try {
            return pd.getReadMethod().invoke(pojo);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void setValue(Object pojo, String fieldName, Object value) {
        PropertyDescriptor pd = BeanUtils.getPropertyDescriptor(pojo.getClass(), fieldName);

        try {
            if(pd != null) {
                pd.getWriteMethod().invoke(pojo, value);
            }
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new IllegalStateException(e);
        }
    }
}
