package com.company.jsonlib.instrospectors;

import com.company.jsonlib.fields.CollectionTypeFieldInfo;
import com.company.jsonlib.fields.FieldInfo;
import com.company.jsonlib.fields.ObjectTypeFieldInfo;
import com.company.jsonlib.fields.SimpleTypeFieldInfo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;

public class SerialisationIntrospector extends Introspector {

    public SerialisationIntrospector(Object instance) {
        this.dtoType = instance.getClass();
        this.simpleFields = new ArrayList<>();
        this.objectFields = new ArrayList<>();
        this.collectionFields = new ArrayList<>();

        analyzeFields();
    }

    private void analyzeFields() {
        Field[] fields = dtoType.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            if (isSimpleType(field)) {
                simpleFields.add(new SimpleTypeFieldInfo(field));
            } else if (isCollectionType(field)) {
                collectionFields.add(new CollectionTypeFieldInfo(field));
            } else {
                objectFields.add(new ObjectTypeFieldInfo(field));
            }
        }
    }

    private boolean isSimpleType(Field field) {
        Class<?> type = field.getType();
        return type.isPrimitive()
                || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class
                || type == Character.class;
    }

    private boolean isCollectionType(Field field) {
        Class<?> type = field.getType();
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public String toJson(Object instance) {
        if (instance == null) {
            return "null";
        }

        StringBuilder json = new StringBuilder("{");
        boolean first = true;

        for (FieldInfo fieldInfo : simpleFields) {
            String fieldJson = fieldInfo.toJson(instance);
            if (fieldJson != null) {
                if (!first) {
                    json.append(", ");
                }
                json.append(fieldJson);
                first = false;
            }
        }

        for (FieldInfo fieldInfo : objectFields) {
            String fieldJson = fieldInfo.toJson(instance);
            if (fieldJson != null) {
                if (!first) {
                    json.append(", ");
                }
                json.append(fieldJson);
                first = false;
            }
        }

        for (FieldInfo fieldInfo : collectionFields) {
            String fieldJson = fieldInfo.toJson(instance);
            if (fieldJson != null) {
                if (!first) {
                    json.append(", ");
                }
                json.append(fieldJson);
                first = false;
            }
        }

        json.append("}");
        return json.toString();
    }
}