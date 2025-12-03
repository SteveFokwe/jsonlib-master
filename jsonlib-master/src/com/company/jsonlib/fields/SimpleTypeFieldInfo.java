package com.company.jsonlib.fields;

import java.lang.reflect.Field;

public class SimpleTypeFieldInfo extends FieldInfo {

    public SimpleTypeFieldInfo(Field field) {
        super(field);
    }

    @Override
    public void fillField(Object instance, Object value) throws Exception {
        if (value == null) {
            if (field.getType().isPrimitive()) {
                throw new IllegalArgumentException("Ne peut attribuer null au type primitif: " + field.getName());
            }
            field.set(instance, null);
            return;
        }

        Class<?> type = field.getType();

        try {
            if (type == int.class || type == Integer.class) {
                field.set(instance, convertToInt(value));
            } else if (type == double.class || type == Double.class) {
                field.set(instance, convertToDouble(value));
            } else if (type == boolean.class || type == Boolean.class) {
                field.set(instance, convertToBoolean(value));
            } else if (type == long.class || type == Long.class) {
                field.set(instance, convertToLong(value));
            } else if (type == float.class || type == Float.class) {
                field.set(instance, convertToFloat(value));
            } else if (type == char.class || type == Character.class) {
                field.set(instance, convertToChar(value));
            } else if (type == short.class || type == Short.class) {
                field.set(instance, convertToShort(value));
            } else if (type == byte.class || type == Byte.class) {
                field.set(instance, convertToByte(value));
            } else if (type == String.class) {
                field.set(instance, value.toString());
            } else {
                throw new UnsupportedOperationException("Type non supporté: " + type);
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Impossible de convertir: " + value + " au type: " + type, e);
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

        if (value instanceof Number || value instanceof Boolean) {
            return "\"" + fieldName + "\": " + value;
        } else {
            return "\"" + fieldName + "\": \"" + escapeJson(value.toString()) + "\"";
        }
    }

    private int convertToInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(value.toString());
    }

    private double convertToDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(value.toString());
    }

    private boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return Boolean.parseBoolean(value.toString());
    }

    private long convertToLong(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        return Long.parseLong(value.toString());
    }

    private float convertToFloat(Object value) {
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        return Float.parseFloat(value.toString());
    }

    private char convertToChar(Object value) {
        String str = value.toString();
        if (str.length() != 1) {
            throw new IllegalArgumentException("Caractère invalide: " + value);
        }
        return str.charAt(0);
    }

    private short convertToShort(Object value) {
        if (value instanceof Number) {
            return ((Number) value).shortValue();
        }
        return Short.parseShort(value.toString());
    }

    private byte convertToByte(Object value) {
        if (value instanceof Number) {
            return ((Number) value).byteValue();
        }
        return Byte.parseByte(value.toString());
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}