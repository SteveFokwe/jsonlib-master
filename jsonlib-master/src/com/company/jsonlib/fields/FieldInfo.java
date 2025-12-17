package com.company.jsonlib.fields;

import com.company.jsonlib.annotations.FieldName;
import com.company.jsonlib.annotations.Ignore;

import java.lang.reflect.Field;

// Métadonnées d'un champ utilisées pour (dé)sérialiser
public abstract class FieldInfo {

    // Nom réel du champ
    protected String name;

    // Annotation @FieldName si présente
    protected FieldName fieldNameAnnotation;

    // Annotation @Ignore si présente
    protected Ignore ignoreAnnotation;

    // Field réfléchi
    protected Field field;

    public FieldInfo(Field field) {
        this.field = field;
        this.name = field.getName();
        this.fieldNameAnnotation = field.getAnnotation(FieldName.class);
        this.ignoreAnnotation = field.getAnnotation(Ignore.class);
        this.field.setAccessible(true);
    }

    // Nom utilisé dans le JSON (ou override via @FieldName)
    public String getName() {
        if (fieldNameAnnotation != null && !fieldNameAnnotation.override().isEmpty()) {
            return fieldNameAnnotation.override();
        }
        return name;
    }

    // Nom réel Java
    public String getRealName() {
        return name;
    }

    // Indique si le champ est ignoré
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