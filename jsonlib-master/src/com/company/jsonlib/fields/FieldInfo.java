package com.company.jsonlib.fields;

import com.company.jsonlib.annotations.FieldName;
import com.company.jsonlib.annotations.Ignore;

import java.lang.reflect.Field;

public abstract class FieldInfo {

    protected String name;
    protected FieldName fieldNameAnnotation;
    protected Ignore ignoreAnnotation;
    protected Field field;

    public FieldInfo(Field field) {
        this.field = field;
        this.name = field.getName();
        this.fieldNameAnnotation = field.getAnnotation(FieldName.class);
        this.ignoreAnnotation = field.getAnnotation(Ignore.class);
        this.field.setAccessible(true);
    }

    public String getName() {
        if (fieldNameAnnotation != null && !fieldNameAnnotation.override().isEmpty()) {
            return fieldNameAnnotation.override();
        }
        return name;
    }

    public String getRealName() {
        return name;
    }

    public boolean isIgnored() {
        return ignoreAnnotation != null;
    }

    public Field getField() {
        return field;
    }

    protected Object getValue(Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Erreur d'accès au champ: " + field.getName(), e);
        }
    }

    public abstract void fillField(Object instance, Object value) throws Exception;

    public abstract String toJson(Object instance);
}