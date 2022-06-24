package org.cooder.jooq.mate.utils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.springframework.beans.BeanUtils;

public class TypeUtils {
    public static Object getValue(Object pojo, String fieldName) {
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

    public static void setValue(Object pojo, String fieldName, Object value) {
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
