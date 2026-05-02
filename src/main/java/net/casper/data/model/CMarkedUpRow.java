package net.casper.data.model;

/**
 * A single {@link CDataRow} paired with its {@link CRowMetaData}, providing
 * typed accessors by column name and index.
 */
public class CMarkedUpRow {

    private final CDataRow row;
    private final CRowMetaData metaData;

    public CMarkedUpRow(CDataRow row, CRowMetaData metaData) {
        this.row = row;
        this.metaData = metaData;
    }

    public CRowMetaData getMetaDefinition() { return metaData; }

    // --- By column name ---

    public String getString(String columnName) throws CDataGridException {
        return getString(metaData.getColumnIndex(columnName));
    }

    public Character getChar(String columnName) throws CDataGridException {
        return getChar(metaData.getColumnIndex(columnName));
    }

    public Boolean getBoolean(String columnName) throws CDataGridException {
        return getBoolean(metaData.getColumnIndex(columnName));
    }

    public Byte getByte(String columnName) throws CDataGridException {
        return getByte(metaData.getColumnIndex(columnName));
    }

    public Short getShort(String columnName) throws CDataGridException {
        return getShort(metaData.getColumnIndex(columnName));
    }

    public Integer getInt(String columnName) throws CDataGridException {
        return getInt(metaData.getColumnIndex(columnName));
    }

    public Long getLong(String columnName) throws CDataGridException {
        return getLong(metaData.getColumnIndex(columnName));
    }

    public Float getFloat(String columnName) throws CDataGridException {
        return getFloat(metaData.getColumnIndex(columnName));
    }

    public Double getDouble(String columnName) throws CDataGridException {
        return getDouble(metaData.getColumnIndex(columnName));
    }

    public java.util.Date getDate(String columnName) throws CDataGridException {
        return getDate(metaData.getColumnIndex(columnName));
    }

    public java.sql.Time getTime(String columnName) throws CDataGridException {
        return getTime(metaData.getColumnIndex(columnName));
    }

    public java.sql.Timestamp getTimestamp(String columnName) throws CDataGridException {
        return getTimestamp(metaData.getColumnIndex(columnName));
    }

    public Object getObject(String columnName) throws CDataGridException {
        return getObject(metaData.getColumnIndex(columnName));
    }

    // --- By column index ---

    public String getString(int idx) throws CDataGridException {
        return (String) CDataConverter.convertTo(row.getValue(idx), CTypes.STRING);
    }

    public Character getChar(int idx) throws CDataGridException {
        return (Character) CDataConverter.convertTo(row.getValue(idx), CTypes.CHARACTER);
    }

    public Boolean getBoolean(int idx) throws CDataGridException {
        return (Boolean) CDataConverter.convertTo(row.getValue(idx), CTypes.BOOLEAN);
    }

    public Byte getByte(int idx) throws CDataGridException {
        return (Byte) CDataConverter.convertTo(row.getValue(idx), CTypes.BYTE);
    }

    public Short getShort(int idx) throws CDataGridException {
        return (Short) CDataConverter.convertTo(row.getValue(idx), CTypes.SHORT);
    }

    public Integer getInt(int idx) throws CDataGridException {
        return (Integer) CDataConverter.convertTo(row.getValue(idx), CTypes.INTEGER);
    }

    public Long getLong(int idx) throws CDataGridException {
        return (Long) CDataConverter.convertTo(row.getValue(idx), CTypes.LONG);
    }

    public Float getFloat(int idx) throws CDataGridException {
        return (Float) CDataConverter.convertTo(row.getValue(idx), CTypes.FLOAT);
    }

    public Double getDouble(int idx) throws CDataGridException {
        return (Double) CDataConverter.convertTo(row.getValue(idx), CTypes.DOUBLE);
    }

    public java.util.Date getDate(int idx) throws CDataGridException {
        return (java.util.Date) CDataConverter.convertTo(row.getValue(idx), CTypes.DATE);
    }

    public java.sql.Time getTime(int idx) throws CDataGridException {
        return (java.sql.Time) CDataConverter.convertTo(row.getValue(idx), CTypes.TIME);
    }

    public java.sql.Timestamp getTimestamp(int idx) throws CDataGridException {
        return (java.sql.Timestamp) CDataConverter.convertTo(row.getValue(idx), CTypes.TIMESTAMP);
    }

    public Object getObject(int idx) throws CDataGridException {
        return row.getValue(idx);
    }
}
