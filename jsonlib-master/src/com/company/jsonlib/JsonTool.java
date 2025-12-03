package com.company.jsonlib;

import com.company.jsonlib.instrospectors.DesrialisationIntrospector;  // ← SANS "e"
import com.company.jsonlib.instrospectors.SerialisationIntrospector;

import java.util.HashMap;

public class JsonTool {

    private HashMap<Class<?>, SerialisationIntrospector> serializationCache;
    private HashMap<Class<?>, DesrialisationIntrospector> deserializationCache;  // ← SANS "e"

    public JsonTool() {
        serializationCache = new HashMap<>();
        deserializationCache = new HashMap<>();
    }

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

    public <T> T toDTO(String jsonString, Class<T> toType) {
        if (jsonString == null || jsonString.trim().equals("null")) {
            return null;
        }

        DesrialisationIntrospector introspector;  // ← SANS "e"
        if (deserializationCache.containsKey(toType)) {
            introspector = deserializationCache.get(toType);
        } else {
            introspector = new DesrialisationIntrospector(toType);  // ← SANS "e"
            deserializationCache.put(toType, introspector);
        }

        return introspector.toDTO(jsonString, toType);
    }

    private boolean isPrimitiveOrString(Object o) {
        return o instanceof String
                || o instanceof Number
                || o instanceof Boolean
                || o instanceof Character;
    }

    private String serializePrimitive(Object o) {
        if (o instanceof String) {
            return "\"" + escapeJson(o.toString()) + "\"";
        } else if (o instanceof Character) {
            return "\"" + escapeJson(o.toString()) + "\"";
        } else {
            return o.toString();
        }
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}