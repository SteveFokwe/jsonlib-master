package com.company.jsonlib.fields;

import com.company.jsonlib.JsonTool;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class CollectionTypeFieldInfo extends FieldInfo {

    public CollectionTypeFieldInfo(Field field) {
        super(field);
    }

    @Override
    public void fillField(Object instance, Object value) throws Exception {
        if (value == null) {
            field.set(instance, null);
            return;
        }

        if (value instanceof Collection) {
            if (field.getType().isAssignableFrom(ArrayList.class)) {
                field.set(instance, new ArrayList<>((Collection<?>) value));
            } else if (field.getType().isAssignableFrom(HashSet.class)) {
                field.set(instance, new HashSet<>((Collection<?>) value));
            } else {
                field.set(instance, value);
            }
        } else if (value.getClass().isArray()) {
            field.set(instance, value);
        } else {
            throw new IllegalArgumentException(
                    "La valeur n'est pas une collection: " + value.getClass()
            );
        }
    }

    @Override
    public String toJson(Object instance) {
        if (isIgnored()) {
            return null;
        }

        Object value = getValue(instance);
        String fieldName = getName();

        if (value == null) {
            return "\"" + fieldName + "\": null";
        }

        StringBuilder jsonArray = new StringBuilder("[");
        JsonTool jsonTool = new JsonTool();
        boolean first = true;

        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            for (Object item : collection) {
                if (!first) {
                    jsonArray.append(", ");
                }
                jsonArray.append(jsonTool.toJson(item));
                first = false;
            }
        } else if (value.getClass().isArray()) {
            Object[] array = (Object[]) value;
            for (Object item : array) {
                if (!first) {
                    jsonArray.append(", ");
                }
                jsonArray.append(jsonTool.toJson(item));
                first = false;
            }
        }

        jsonArray.append("]");
        return "\"" + fieldName + "\": " + jsonArray.toString();
    }

    public Class<?> getGenericType() {
        Type genericType = field.getGenericType();
        if (genericType instanceof ParameterizedType) {
            ParameterizedType paramType = (ParameterizedType) genericType;
            Type[] typeArguments = paramType.getActualTypeArguments();
            if (typeArguments.length > 0) {
                return (Class<?>) typeArguments[0];
            }
        }
        return Object.class;
    }
}