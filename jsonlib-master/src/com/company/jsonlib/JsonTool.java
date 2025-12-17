package com.company.jsonlib;

import com.company.jsonlib.instrospectors.DesrialisationIntrospector;
import com.company.jsonlib.instrospectors.SerialisationIntrospector;

import java.util.HashMap;

// Outil simple pour sérialiser/désérialiser des objets en JSON
public class JsonTool {

    // Cache des introspecteurs de sérialisation par type
    private HashMap<Class<?>, SerialisationIntrospector> serializationCache;

    // Cache des introspecteurs de désérialisation par type
    private HashMap<Class<?>, DesrialisationIntrospector> deserializationCache;

    // Initialise les caches
    public JsonTool() {
        serializationCache = new HashMap<>();
        deserializationCache = new HashMap<>();
    }

    // Sérialise un objet en JSON (primitives, chaînes ou objets complexes)
    public String toJson(Object o) {
        if (o == null) {
            return "null";
        }

        if (isPrimitiveOrString(o)) {
            return serializePrimitive(o);
        }

        Class<?> type = o.getClass();

        SerialisationIntrospector introspector;
        if (serializationCache.containsKey(type)) {
            introspector = serializationCache.get(type);
        } else {
            introspector = new SerialisationIntrospector(o);
            serializationCache.put(type, introspector);
        }

        return introspector.toJson(o);
    }

    // Désérialise une chaîne JSON en instance d'une classe donnée
    public <T> T toDTO(String jsonString, Class<T> toType) {
        if (jsonString == null || jsonString.trim().equals("null")) {
            return null;
        }

        DesrialisationIntrospector introspector;
        if (deserializationCache.containsKey(toType)) {
            introspector = deserializationCache.get(toType);
        } else {
            introspector = new DesrialisationIntrospector(toType);
            deserializationCache.put(toType, introspector);
        }

        return introspector.toDTO(jsonString, toType);
    }

    // Vérifie si l'objet est une valeur simple gérée (String, Number, Boolean, Character)
    private boolean isPrimitiveOrString(Object o) {
        return o instanceof String
                || o instanceof Number
                || o instanceof Boolean
                || o instanceof Character;
    }

    // Sérialise une valeur primitive ou une String (avec échappement)
    private String serializePrimitive(Object o) {
        if (o instanceof String) {
            return "\"" + escapeJson(o.toString()) + "\"";
        } else if (o instanceof Character) {
            return "\"" + escapeJson(o.toString()) + "\"";
        } else {
            return o.toString();
        }
    }

    // Échappe les caractères spéciaux pour l'inclusion en JSON
    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}