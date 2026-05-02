package net.casper.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Type conversion between supported data types, and JDBC ResultSet readers
 * that return null-safe boxed types.
 */
public final class CDataConverter implements Serializable {

    private static final long serialVersionUID = 1L;

    private CDataConverter() {
    }

    public static Object convertTo(Object data, int type) throws CDataGridException {
        if (data == null) return null;

        if (data instanceof Byte) {
            Byte b = (Byte) data;
            switch (type) {
                case CTypes.BOOLEAN:
                    if (b == 0) return Boolean.FALSE;
                    if (b == 1) return Boolean.TRUE;
                    throw new CDataGridException("Cannot convert Byte " + b + " to Boolean");
                case CTypes.BYTE:      return b;
                case CTypes.DOUBLE:    return (double) b;
                case CTypes.FLOAT:     return (float) b;
                case CTypes.INTEGER:   return (int) b;
                case CTypes.LONG:      return (long) b;
                case CTypes.SHORT:     return (short) b;
                case CTypes.STRING:    return b.toString();
                default: throw new CDataGridException("Invalid conversion for Byte: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof Number) {
            Number n = (Number) data;
            switch (type) {
                case CTypes.BOOLEAN:
                    if (n.intValue() == 0) return Boolean.FALSE;
                    if (n.intValue() == 1) return Boolean.TRUE;
                    throw new CDataGridException("Cannot convert " + n + " to Boolean");
                case CTypes.BYTE:      return n.byteValue();
                case CTypes.DOUBLE:    return n.doubleValue();
                case CTypes.FLOAT:     return n.floatValue();
                case CTypes.INTEGER:   return n.intValue();
                case CTypes.LONG:      return n.longValue();
                case CTypes.SHORT:     return n.shortValue();
                case CTypes.STRING:    return n.toString();
                default: throw new CDataGridException("Invalid conversion for Number: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof String) {
            String s = (String) data;
            switch (type) {
                case CTypes.BOOLEAN:   return Boolean.valueOf(s);
                case CTypes.BYTE:      return Byte.valueOf(s);
                case CTypes.DOUBLE:    return Double.valueOf(s);
                case CTypes.FLOAT:     return Float.valueOf(s);
                case CTypes.INTEGER:   return Integer.valueOf(s);
                case CTypes.LONG:      return Long.valueOf(s);
                case CTypes.SHORT:     return Short.valueOf(s);
                case CTypes.STRING:    return s;
                default: throw new CDataGridException("Invalid conversion for String: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof Timestamp) {
            Timestamp ts = (Timestamp) data;
            switch (type) {
                case CTypes.DATE:      return new java.sql.Date(ts.getTime());
                case CTypes.STRING:    return ts.toString();
                case CTypes.TIME:      return new Time(ts.getTime());
                case CTypes.TIMESTAMP: return ts;
                default: throw new CDataGridException("Invalid conversion for Timestamp: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof Boolean) {
            Boolean b = (Boolean) data;
            int v = b ? 1 : 0;
            switch (type) {
                case CTypes.BOOLEAN:   return b;
                case CTypes.BYTE:      return (byte) v;
                case CTypes.DOUBLE:    return (double) v;
                case CTypes.FLOAT:     return (float) v;
                case CTypes.INTEGER:   return v;
                case CTypes.LONG:      return (long) v;
                case CTypes.SHORT:     return (short) v;
                case CTypes.STRING:    return b.toString();
                default: throw new CDataGridException("Invalid conversion for Boolean: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof java.util.Date) {
            java.util.Date d = (java.util.Date) data;
            switch (type) {
                case CTypes.STRING:    return d.toString();
                case CTypes.DATE:      return d;
                case CTypes.TIME:      return new Time(d.getTime());
                case CTypes.TIMESTAMP: return new Timestamp(d.getTime());
                default: throw new CDataGridException("Invalid conversion for Date: " + CTypes.getConvTypeDesc(type));
            }
        }

        if (data instanceof Character) {
            Character c = (Character) data;
            switch (type) {
                case CTypes.CHARACTER: return c;
                case CTypes.STRING:    return c.toString();
                default: throw new CDataGridException("Invalid conversion for Character: " + CTypes.getConvTypeDesc(type));
            }
        }

        throw new CDataGridException("Cannot convert type: " + data.getClass().getName());
    }

    // --- JDBC ResultSet helpers (null-safe) ---

    public static String getString(ResultSet rs, int col) throws SQLException {
        String data = rs.getString(col);
        return rs.wasNull() ? null : (data == null ? null : data.trim());
    }

    public static String getString(ResultSet rs, String col) throws SQLException {
        String data = rs.getString(col);
        return rs.wasNull() ? null : (data == null ? null : data.trim());
    }

    public static Short getShort(ResultSet rs, int col) throws SQLException {
        short data = rs.getShort(col);
        return rs.wasNull() ? null : data;
    }

    public static Integer getInt(ResultSet rs, int col) throws SQLException {
        int data = rs.getInt(col);
        return rs.wasNull() ? null : data;
    }

    public static Long getLong(ResultSet rs, int col) throws SQLException {
        long data = rs.getLong(col);
        return rs.wasNull() ? null : data;
    }

    public static Float getFloat(ResultSet rs, int col) throws SQLException {
        float data = rs.getFloat(col);
        return rs.wasNull() ? null : data;
    }

    public static Double getDouble(ResultSet rs, int col) throws SQLException {
        double data = rs.getDouble(col);
        return rs.wasNull() ? null : data;
    }

    public static String getCurrency(ResultSet rs, int col) throws SQLException {
        BigDecimal data = rs.getBigDecimal(col);
        return rs.wasNull() ? null : data.setScale(2).toString();
    }

    public static Byte getByte(ResultSet rs, int col) throws SQLException {
        byte data = rs.getByte(col);
        return rs.wasNull() ? null : data;
    }

    public static Boolean getBoolean(ResultSet rs, int col) throws SQLException {
        boolean data = rs.getBoolean(col);
        return rs.wasNull() ? null : data;
    }

    public static Timestamp getTimestamp(ResultSet rs, int col) throws SQLException {
        Timestamp data = rs.getTimestamp(col);
        return rs.wasNull() ? null : data;
    }

    public static java.util.Date getDate(ResultSet rs, int col) throws SQLException {
        java.sql.Date data = rs.getDate(col);
        return rs.wasNull() ? null : new java.util.Date(data.getTime());
    }

    public static String nullValue() {
        return "null";
    }
}
