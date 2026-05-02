package net.casper.data.model;

/**
 * Represents a single {@link CDataRow} with its {@link CRowMetaData}. Provides
 * accessors for getting row data via column name and index.
 *
 * @author Oliver Mannion
 */
public class CMarkedUpRow {

    private final CDataRow row;

    private final CRowMetaData metaData;

    /**
     * Construct a {@link CMarkedUpRow}.
     *
     * @param row
     *            row
     * @param metaData
     *            meta data
     */
    public CMarkedUpRow(CDataRow row, CRowMetaData metaData) {
        this.row = row;
        this.metaData = metaData;
    }

    /**
     * Get the row meta data.
     *
     * @return row meta data
     */
    public CRowMetaData getMetaDefinition() {
        return metaData;
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public String getString(String columnName) throws CDataGridException {
        return getString(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Character getChar(String columnName) throws CDataGridException {
        return getChar(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Boolean getBoolean(String columnName) throws CDataGridException {
        return getBoolean(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Byte getByte(String columnName) throws CDataGridException {
        return getByte(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Short getShort(String columnName) throws CDataGridException {
        return getShort(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Integer getInt(String columnName) throws CDataGridException {
        return getInt(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Long getLong(String columnName) throws CDataGridException {
        return getLong(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return String
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Float getFloat(String columnName) throws CDataGridException {
        return getFloat(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Double getDouble(String columnName) throws CDataGridException {
        return getDouble(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.util.Date getDate(String columnName)
            throws CDataGridException {
        return getDate(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.sql.Time getTime(String columnName) throws CDataGridException {
        return getTime(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.sql.Timestamp getTimestamp(String columnName)
            throws CDataGridException {
        return getTimestamp(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns Object at given column name.
     *
     * @param columnName
     *            column Name
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Object getObject(String columnName) throws CDataGridException {
        return getObject(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public String getString(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (String) CDataConverter.convertTo(value, CTypes.STRING);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Character getChar(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Character) CDataConverter.convertTo(value, CTypes.CHARACTER);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Boolean getBoolean(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Boolean) CDataConverter.convertTo(value, CTypes.BOOLEAN);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Byte getByte(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Byte) CDataConverter.convertTo(value, CTypes.BYTE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Short getShort(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Short) CDataConverter.convertTo(value, CTypes.SHORT);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Integer getInt(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Integer) CDataConverter.convertTo(value, CTypes.INTEGER);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Long getLong(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Long) CDataConverter.convertTo(value, CTypes.LONG);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Float getFloat(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Float) CDataConverter.convertTo(value, CTypes.FLOAT);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Double getDouble(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (Double) CDataConverter.convertTo(value, CTypes.DOUBLE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.util.Date getDate(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (java.util.Date) CDataConverter.convertTo(value, CTypes.DATE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.sql.Time getTime(int columnIndex) throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (java.sql.Time) CDataConverter.convertTo(value, CTypes.TIME);
    }

    /**
     * Returns type-safe representation of current row at given column.
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public java.sql.Timestamp getTimestamp(int columnIndex)
            throws CDataGridException {
        Object value = row.getValue(columnIndex);
        return (java.sql.Timestamp) CDataConverter.convertTo(value,
                CTypes.TIMESTAMP);
    }

    /**
     * Returns Object at specified column index. Note that there is not
     * upcasting done here - this allows for generic handling of data...
     *
     * @param columnIndex
     *            column Index
     * @return data
     * @throws CDataGridException
     *             if column cannot be accessed as type
     */
    public Object getObject(int columnIndex) throws CDataGridException {
        return row.getValue(columnIndex);
    }

}
