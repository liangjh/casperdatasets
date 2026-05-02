package net.casper.data.model;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * Supported data types for type conversion. Static int constants enable fast switching
 * in {@link CDataConverter} without instanceof checks.
 */
public class CTypes implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int BOOLEAN   = 2;
    public static final int BYTE      = 4;
    public static final int DATE      = 6;
    public static final int DOUBLE    = 8;
    public static final int FLOAT     = 10;
    public static final int INTEGER   = 12;
    public static final int LONG      = 14;
    public static final int SHORT     = 16;
    public static final int STRING    = 18;
    public static final int TIME      = 20;
    public static final int TIMESTAMP = 22;
    public static final int CHARACTER = 24;

    protected CTypes() {
    }

    public static String getConvTypeDesc(int type) {
        switch (type) {
            case BOOLEAN:   return "BOOLEAN";
            case BYTE:      return "BYTE";
            case DATE:      return "DATE";
            case DOUBLE:    return "DOUBLE";
            case FLOAT:     return "FLOAT";
            case INTEGER:   return "INTEGER";
            case LONG:      return "LONG";
            case SHORT:     return "SHORT";
            case STRING:    return "STRING";
            case TIME:      return "TIME";
            case TIMESTAMP: return "TIMESTAMP";
            case CHARACTER: return "CHARACTER";
            default:        return "Invalid Type Value";
        }
    }

    public static int getJavaObjType(Object object) {
        if (object == null) return -1;
        Class<?> cls = object.getClass();

        if (cls.equals(Boolean.class))             return Types.BOOLEAN;
        if (cls.equals(Byte.class))                return Types.CHAR;
        if (cls.equals(java.sql.Date.class))       return Types.DATE;
        if (cls.equals(java.util.Date.class))      return Types.DATE;
        if (cls.equals(Double.class))              return Types.DOUBLE;
        if (cls.equals(Float.class))               return Types.FLOAT;
        if (cls.equals(Integer.class))             return Types.INTEGER;
        if (cls.equals(Long.class))                return Types.NUMERIC;
        if (cls.equals(Short.class))               return Types.NUMERIC;
        if (cls.equals(String.class))              return Types.VARCHAR;
        if (cls.equals(Time.class))                return Types.TIME;
        if (cls.equals(Timestamp.class))           return Types.TIMESTAMP;
        if (cls.equals(Character.class))           return Types.CHAR;
        return -1;
    }
}
