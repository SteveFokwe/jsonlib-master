package com.company.jsonlib.fields;

import com.company.jsonlib.JsonTool;

import java.lang.reflect.Field;

// FieldInfo pour objets (types complexes)
public class ObjectTypeFieldInfo extends FieldInfo {

    public ObjectTypeFieldInfo(Field field) {
        super(field);
    }

    // Assigne un objet (si compatible)
    @Override
    public void fillField(Object instance, Object value) throws Exception {
        if (value == null) {
            field.set(instance, null);
            return;
        }

        if (field.getType().isInstance(value)) {
            field.set(instance, value);
        } else {
            throw new IllegalArgumentException(
                    "Le type de la valeur (" + value.getClass() + ") ne correspond pas au type du champ (" + field.getType() + ")"
            );
        }
    }

    // Sérialise l'objet imbriqué en JSON
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

        JsonTool jsonTool = new JsonTool();
        String nestedJson = jsonTool.toJson(value);

        return "\"" + fieldName + "\": " + nestedJson;
    }
}