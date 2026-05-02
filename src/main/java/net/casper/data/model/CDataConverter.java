package net.casper.data.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Provides functionality to convert data from one type to another
 * in the dataset implementation.  This is primarily called from
 * within the RowSet.  Should be used in conjunction with CTypes.java,
 * which serve as an internal conversion optimization
 * <br/><br/>
 * The other major suite of conversion functionality involves the conversion of
 * resultset data, into java object types.  JDBC api returns primitives when
 * possible, but we need to work with object types within casper datasets.
 * <br/><br/>
 *
 * @since v1.0
 * @author Jonathan Liang
 */
public final class CDataConverter extends CTypes implements Serializable {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /**
     * Do not allow instantiation of this object
     */
    private CDataConverter() {
    }

    /**
     * Converts columnar data to the specified type
     * (the types are derived from datatypes in CType.java
     *
     * @param data - the data object to convert
     * @param type - the type to convert the data obj to
     * @return the converted object
     * @throws CDataGridException
     */
    public static Object convertTo(Object data, int type)
            throws CDataGridException {
        if (data == null)
            return null;

        if (data instanceof Byte) {
            Byte actualData = (Byte) data;
            switch (type) {
                case BOOLEAN:
                    if (actualData.byteValue() == 0)        return Boolean.FALSE;
                    else if (actualData.byteValue() == 1)   return Boolean.TRUE;
                    else throw new CDataGridException("Invalid conversion type requested for Byte column data to Boolean for value: " + actualData.byteValue());
                case BYTE:      return actualData;
                case DOUBLE:    return Double.valueOf(actualData.doubleValue());
                case FLOAT:     return Float.valueOf(actualData.floatValue());
                case INTEGER:   return Integer.valueOf(actualData.intValue());
                case LONG:      return Long.valueOf(actualData.longValue());
                case SHORT:     return Short.valueOf(actualData.shortValue());
                case STRING:    return actualData.toString();
                default:        throw new CDataGridException("Invalid conversion type requested for Byte column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof Number) {
            Number actualData = (Number) data;
            switch (type) {
                case BOOLEAN:
                    if (actualData.intValue() == 0)
                        return Boolean.FALSE;
                    else if (actualData.intValue() == 1)
                        return Boolean.TRUE;
                    else
                        throw new CDataGridException("Invalid conversion type requested for Numeric column data to Boolean for value: " + actualData.intValue());
                case BYTE:
                    return Byte.valueOf(actualData.byteValue());
                case DOUBLE:
                    return Double.valueOf(actualData.doubleValue());
                case FLOAT:
                    return Float.valueOf(actualData.floatValue());
                case INTEGER:
                    return Integer.valueOf(actualData.intValue());
                case LONG:
                    return Long.valueOf(actualData.longValue());
                case SHORT:
                    return Short.valueOf(actualData.shortValue());
                case STRING:
                    return actualData.toString();
                default:
                    throw new CDataGridException("Invalid conversion type requested for Integer column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof String) {
            String actualData = (String) data;
            switch (type) {
                case BOOLEAN:
                    return Boolean.valueOf(actualData);
                case BYTE:
                    return Byte.valueOf(actualData);
                case DOUBLE:
                    return Double.valueOf(actualData);
                case FLOAT:
                    return Float.valueOf(actualData);
                case INTEGER:
                    return Integer.valueOf(actualData);
                case LONG:
                    return Long.valueOf(actualData);
                case SHORT:
                    return Short.valueOf(actualData);
                case STRING:
                    return actualData;
                default:
                    throw new CDataGridException("Invalid conversion type requested for String column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof Timestamp) {
            Timestamp actualData = (Timestamp) data;
            switch (type) {
                case DATE:
                    return new java.sql.Date(actualData.getTime());
                case STRING:
                    return actualData.toString();
                case TIME:
                    return new java.sql.Time(actualData.getTime());
                case TIMESTAMP:
                    return actualData;
                default:
                    throw new CDataGridException("Invalid conversion type requested for Timestamp column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof Boolean) {
            Boolean actualData = (Boolean) data;
            switch (type) {
                case BOOLEAN:
                    return actualData;
                case BYTE:
                    return Byte.valueOf((byte) toInteger(actualData));
                case DOUBLE:
                    return Double.valueOf(toInteger(actualData));
                case FLOAT:
                    return Float.valueOf(toInteger(actualData));
                case INTEGER:
                    return Integer.valueOf(toInteger(actualData));
                case LONG:
                    return Long.valueOf(toInteger(actualData));
                case SHORT:
                    return Short.valueOf((short) toInteger(actualData));
                case STRING:
                    return actualData.toString();
                default:
                    throw new CDataGridException("Invalid conversion type requested for Boolean column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof java.util.Date) {
            java.util.Date actualData = (java.util.Date) data;
            switch (type) {
                case STRING:
                    return data.toString();
                case DATE:
                    return data;
                case TIME:
                    return new Time(actualData.getTime());
                case TIMESTAMP:
                    return new Timestamp(actualData.getTime());
                default:
                    throw new CDataGridException("Invalid conversion type requested for java.util.Date column data: " + CTypes.getConvTypeDesc(type));
            }
        } else if (data instanceof Character) {
            Character actualData = (Character) data;
            switch (type) {
                case CHARACTER:
                    return actualData;
                case STRING:
                    return actualData.toString();
                default:
                    throw new CDataGridException("Invalid conversion type requested for Character column data: " + CTypes.getConvTypeDesc(type));
            }
        } else {
            throw new CDataGridException("Object of type '" + data.getClass().getName() + "' can not be converted.");
        }
    }

    /**
     * Converts a boolean to a 0 or 1 depending on the value.
     *
     * @param value value to be converted.
     * @return a 0 or 1
     */
    private static int toInteger(Boolean value) {
        if (value == null)
            return 0;
        if (value.booleanValue())
            return 1;
        else
            return 0;
    }

    /**
     * Reads column as a string using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static String getString(ResultSet resultSet, int columnNumber)
            throws SQLException {
        String data = resultSet.getString(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return (data == null ? null : data.trim());
    }

    /**
     * Reads column as string using column name.
     *
     * @param resultSet
     * @param columnName
     * @return value
     * @throws java.sql.SQLException
     */
    public static String getString(ResultSet resultSet, String columnName)
            throws SQLException {
        String data = resultSet.getString(columnName);
        if (resultSet.wasNull())
            return null;
        else
            return (data == null ? null : data.trim());
    }

    /**
     * Reads column as a short using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Short getShort(ResultSet resultSet, int columnNumber)
            throws SQLException {
        short data = resultSet.getShort(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Short.valueOf(data);
    }

    /**
     * Reads column as an integer using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Integer getInt(ResultSet resultSet, int columnNumber)
            throws SQLException {
        int data = resultSet.getInt(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Integer.valueOf(data);
    }

    /**
     * Reads column as a long using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Long getLong(ResultSet resultSet, int columnNumber)
            throws SQLException {
        long data = resultSet.getLong(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Long.valueOf(data);
    }

    /**
     * Reads column as a float using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Float getFloat(ResultSet resultSet, int columnNumber)
            throws SQLException {
        float data = resultSet.getFloat(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Float.valueOf(data);
    }

    /**
     * Reads column as a double using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Double getDouble(ResultSet resultSet, int columnNumber)
            throws SQLException {
        double data = resultSet.getDouble(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Double.valueOf(data);
    }

    /**
     * Reads column as a currency value (floating point value with 2 decimal places) using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static String getCurrency(ResultSet resultSet, int columnNumber)
            throws SQLException {
        BigDecimal data = resultSet.getBigDecimal(columnNumber);
        if (resultSet.wasNull()) {
            return null;
        } else {
            data = data.setScale(2);
            return data.toString();
        }
    }

    /**
     * Reads column as a Byte using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Byte getByte(ResultSet resultSet, int columnNumber)
            throws SQLException {
        byte data = resultSet.getByte(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Byte.valueOf(data);
    }

    /**
     * Reads column as a boolean value using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Boolean getBoolean(ResultSet resultSet, int columnNumber)
            throws SQLException {
        boolean data = resultSet.getBoolean(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return Boolean.valueOf(data);
    }

    /**
     * Reads column as a timestamp using column index.
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static Timestamp getTimestamp(ResultSet resultSet, int columnNumber)
            throws SQLException {
        Timestamp columnData = resultSet.getTimestamp(columnNumber);
        if (resultSet.wasNull())
            return null;
        else
            return columnData;
    }

    /**
     * Converts to java.util.Date.  java.sql.Date may have conversion problems
     *
     * @param resultSet
     * @param columnNumber
     * @return value
     * @throws java.sql.SQLException
     */
    public static java.util.Date getDate(ResultSet resultSet, int columnNumber)
            throws SQLException {
        java.sql.Date data = resultSet.getDate(columnNumber);
        if (resultSet.wasNull()) {
            return null;
        } else {
            java.util.Date utilDate = new java.util.Date(data.getTime());
            return utilDate;
        }
    }

    /**
     * Returns string to indicate database null
     *
     * @return string value indicating SQL null.
     */
    public static String nullValue() {
        return "null";
    }

}
