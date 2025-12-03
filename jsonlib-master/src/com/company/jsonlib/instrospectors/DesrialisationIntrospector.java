package com.company.jsonlib.instrospectors;

import com.company.jsonlib.JsonTool;
import com.company.jsonlib.fields.CollectionTypeFieldInfo;
import com.company.jsonlib.fields.FieldInfo;
import com.company.jsonlib.fields.ObjectTypeFieldInfo;
import com.company.jsonlib.fields.SimpleTypeFieldInfo;

import java.lang.reflect.Field;
import java.util.*;

public class DesrialisationIntrospector extends Introspector {

    public DesrialisationIntrospector(Class<?> dtoType) {
        this.dtoType = dtoType;
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
        return type.isPrimitive() || type == String.class
                || Number.class.isAssignableFrom(type)
                || type == Boolean.class || type == Character.class;
    }

    private boolean isCollectionType(Field field) {
        Class<?> type = field.getType();
        return type.isArray() || Collection.class.isAssignableFrom(type);
    }

    public <T> T toDTO(String json, Class<T> dtoType) {
        try {
            if (json == null || json.trim().equals("null")) return null;
            T instance = dtoType.getDeclaredConstructor().newInstance();
            Map<String, Object> jsonMap = parseJsonToMap(json);

            for (FieldInfo fieldInfo : simpleFields) {
                fillSimpleField(instance, fieldInfo, jsonMap);
            }
            for (FieldInfo fieldInfo : objectFields) {
                fillObjectField(instance, fieldInfo, jsonMap);
            }
            for (FieldInfo fieldInfo : collectionFields) {
                fillCollectionField(instance, fieldInfo, jsonMap);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Erreur de désérialisation: " + e.getMessage(), e);
        }
    }

    private void fillSimpleField(Object instance, FieldInfo fieldInfo, Map<String, Object> jsonMap) throws Exception {
        String jsonName = fieldInfo.getName();
        String realName = fieldInfo.getRealName();
        if (jsonMap.containsKey(jsonName)) {
            fieldInfo.fillField(instance, jsonMap.get(jsonName));
        } else if (jsonMap.containsKey(realName)) {
            fieldInfo.fillField(instance, jsonMap.get(realName));
        }
    }

    private void fillObjectField(Object instance, FieldInfo fieldInfo, Map<String, Object> jsonMap) throws Exception {
        String jsonName = fieldInfo.getName();
        String realName = fieldInfo.getRealName();
        Object jsonValue = jsonMap.containsKey(jsonName) ? jsonMap.get(jsonName) : jsonMap.get(realName);

        if (jsonValue instanceof Map) {
            JsonTool jsonTool = new JsonTool();
            String nestedJson = convertMapToJson((Map<String, Object>) jsonValue);
            Object nestedObject = jsonTool.toDTO(nestedJson, fieldInfo.getField().getType());
            fieldInfo.fillField(instance, nestedObject);
        }
    }

    private void fillCollectionField(Object instance, FieldInfo fieldInfo, Map<String, Object> jsonMap) throws Exception {
        String jsonName = fieldInfo.getName();
        String realName = fieldInfo.getRealName();
        Object jsonValue = jsonMap.containsKey(jsonName) ? jsonMap.get(jsonName) : jsonMap.get(realName);

        if (jsonValue instanceof List) {
            List<Object> jsonList = (List<Object>) jsonValue;
            List<Object> collection = new ArrayList<>();
            CollectionTypeFieldInfo collectionInfo = (CollectionTypeFieldInfo) fieldInfo;
            Class<?> elementType = collectionInfo.getGenericType();
            JsonTool jsonTool = new JsonTool();

            for (Object item : jsonList) {
                if (item instanceof Map) {
                    String itemJson = convertMapToJson((Map<String, Object>) item);
                    collection.add(jsonTool.toDTO(itemJson, elementType));
                } else {
                    collection.add(item);
                }
            }
            fieldInfo.fillField(instance, collection);
        }
    }

    private Map<String, Object> parseJsonToMap(String json) {
        Map<String, Object> result = new HashMap<>();
        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1).trim();
        }
        if (json.isEmpty()) return result;

        StringTokenizer tokenizer = new StringTokenizer(json, ",:{}[]\"", true);
        String key = null;

        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty()) continue;

            if (token.equals("\"")) {
                StringBuilder sb = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    String next = tokenizer.nextToken();
                    if (next.equals("\"")) break;
                    sb.append(next);
                }
                if (key == null) {
                    key = sb.toString();
                } else {
                    result.put(key, sb.toString());
                    key = null;
                }
            } else if (token.equals("{")) {
                StringBuilder nestedJson = new StringBuilder(token);
                int braceCount = 1;
                while (tokenizer.hasMoreTokens() && braceCount > 0) {
                    String next = tokenizer.nextToken();
                    nestedJson.append(next);
                    if (next.equals("{")) braceCount++;
                    else if (next.equals("}")) braceCount--;
                }
                result.put(key, parseJsonToMap(nestedJson.toString()));
                key = null;
            } else if (token.equals("[")) {
                StringBuilder arrayJson = new StringBuilder(token);
                int bracketCount = 1;
                while (tokenizer.hasMoreTokens() && bracketCount > 0) {
                    String next = tokenizer.nextToken();
                    arrayJson.append(next);
                    if (next.equals("[")) bracketCount++;
                    else if (next.equals("]")) bracketCount--;
                }
                result.put(key, parseJsonToList(arrayJson.toString()));
                key = null;
            } else if (!token.equals(":") && !token.equals(",")) {
                if (key != null) {
                    result.put(key, parsePrimitive(token));
                    key = null;
                }
            }
        }
        return result;
    }

    private List<Object> parseJsonToList(String json) {
        List<Object> result = new ArrayList<>();
        json = json.trim();
        if (json.startsWith("[") && json.endsWith("]")) {
            json = json.substring(1, json.length() - 1).trim();
        }
        if (json.isEmpty()) return result;

        StringTokenizer tokenizer = new StringTokenizer(json, ",{}[]\"", true);
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.isEmpty() || token.equals(",")) continue;

            if (token.equals("\"")) {
                StringBuilder sb = new StringBuilder();
                while (tokenizer.hasMoreTokens()) {
                    String next = tokenizer.nextToken();
                    if (next.equals("\"")) break;
                    sb.append(next);
                }
                result.add(sb.toString());
            } else if (token.equals("{")) {
                StringBuilder nestedJson = new StringBuilder(token);
                int braceCount = 1;
                while (tokenizer.hasMoreTokens() && braceCount > 0) {
                    String next = tokenizer.nextToken();
                    nestedJson.append(next);
                    if (next.equals("{")) braceCount++;
                    else if (next.equals("}")) braceCount--;
                }
                result.add(parseJsonToMap(nestedJson.toString()));
            } else {
                result.add(parsePrimitive(token));
            }
        }
        return result;
    }

    private Object parsePrimitive(String token) {
        if (token.equals("true")) return true;
        if (token.equals("false")) return false;
        if (token.equals("null")) return null;
        try {
            if (token.contains(".")) return Double.parseDouble(token);
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            return token;
        }
    }

    private String convertMapToJson(Map<String, Object> map) {
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (!first) json.append(", ");
            json.append("\"").append(entry.getKey()).append("\": ");
            Object value = entry.getValue();
            if (value == null) {
                json.append("null");
            } else if (value instanceof String) {
                json.append("\"").append(value).append("\"");
            } else if (value instanceof Map) {
                json.append(convertMapToJson((Map<String, Object>) value));
            } else if (value instanceof List) {
                json.append(convertListToJson((List<Object>) value));
            } else {
                json.append(value);
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }

    private String convertListToJson(List<Object> list) {
        StringBuilder json = new StringBuilder("[");
        boolean first = true;
        for (Object item : list) {
            if (!first) json.append(", ");
            if (item == null) {
                json.append("null");
            } else if (item instanceof String) {
                json.append("\"").append(item).append("\"");
            } else if (item instanceof Map) {
                json.append(convertMapToJson((Map<String, Object>) item));
            } else if (item instanceof List) {
                json.append(convertListToJson((List<Object>) item));
            } else {
                json.append(item);
            }
            first = false;
        }
        json.append("]");
        return json.toString();
    }
}