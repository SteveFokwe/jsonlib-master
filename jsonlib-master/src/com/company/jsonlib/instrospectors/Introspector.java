package com.company.jsonlib.instrospectors;

import com.company.jsonlib.fields.FieldInfo;

import java.util.List;

// Base pour les introspecteurs (métadonnées de champs)
public abstract class Introspector {
    protected Class<?> dtoType;
    protected List<FieldInfo> simpleFields;
    protected List<FieldInfo> objectFields;
    protected List<FieldInfo> collectionFields;

    public Class<?> getDtoType() {
        return dtoType;
    }

    public List<FieldInfo> getSimpleFields() {
        return simpleFields;
    }

    public List<FieldInfo> getObjectFields() {
        return objectFields;
    }

    public List<FieldInfo> getCollectionFields() {
        return collectionFields;
    }
}