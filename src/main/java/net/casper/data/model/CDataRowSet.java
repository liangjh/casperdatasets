package net.casper.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Cursor-based rowset for iterating over query results.
 * NOT thread-safe -- use within a single thread. The cursor is 1-based (like JDBC ResultSet).
 */
public class CDataRowSet implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ArrayList<CDataRow> list = new ArrayList<>();
    private final CRowMetaData metaData;
    private int cursor = 0;

    public CDataRowSet(CRowMetaData metaData) throws CDataGridException {
        if (metaData == null)
            throw new CDataGridException("Meta data object cannot be null.");
        this.metaData = metaData;
    }

    public void addData(CDataRow[] rows) throws CDataGridException {
        if (rows == null || rows.length < 1) return;
        for (CDataRow row : rows) {
            if (row == null || row.getNumberColumns() != metaData.getNumberColumns())
                throw new CDataGridException("Column mismatch between meta-data and row data.");
        }
        list.ensureCapacity(list.size() + rows.length);
        for (CDataRow row : rows) list.add(row);
    }

    public CRowMetaData getMetaDefinition() { return metaData; }

    public void sortByColumn(String[] columnNames, boolean ascending) throws CDataGridException {
        if (cursor > 0)
            throw new CDataGridException("Cursor must be reset before re-sorting.");
        int[] columnIndices = metaData.getColumnIndices(columnNames);
        Class<?>[] columnTypes = metaData.getColumnTypes(columnIndices);
        try {
            Collections.sort(list, new CDataComparator(columnIndices, columnTypes));
        } catch (RuntimeException e) {
            throw new CDataGridException(e.getMessage(), e);
        }
        if (!ascending) Collections.reverse(list);
    }

    public int getNumberRows() { return list.size(); }
    public int size() { return list.size(); }

    @SuppressWarnings("unchecked")
    public Map<String, Object>[] toMapArray() throws CDataGridException {
        ArrayList<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            mapList.add(list.get(i).toMap(metaData));
        }
        return mapList.toArray(new HashMap[0]);
    }

    public CDataRow[] getAllRows() {
        return list.toArray(new CDataRow[0]);
    }

    public Object[] getColumnValues(String columnName) throws CDataGridException {
        if (list.isEmpty()) return new Object[0];
        int colIndex = metaData.getColumnIndex(columnName);
        Object[] values = new Object[list.size()];
        for (int i = 0; i < list.size(); i++)
            values[i] = list.get(i).getValue(colIndex);
        return values;
    }

    // --- Cursor navigation ---

    public int getCursorPosition() { return cursor; }
    public boolean isBeforeFirst() { return cursor == 0; }
    public boolean isAfterLast() { return cursor > list.size(); }
    public boolean isFirst() { return cursor == 1; }
    public boolean isLast() { return cursor == list.size(); }
    public void beforeFirst() { cursor = 0; }
    public void reset() { cursor = 0; }
    public void afterLast() { cursor = list.size() + 1; }

    public boolean first() {
        if (list.isEmpty()) return false;
        cursor = 1;
        return true;
    }

    public boolean last() {
        if (list.isEmpty()) return false;
        cursor = list.size();
        return true;
    }

    public boolean previous() {
        if (isBeforeFirst()) return false;
        cursor--;
        return cursor > 0;
    }

    public boolean next() throws CDataGridException {
        if (isAfterLast()) return false;
        cursor++;
        return cursor <= list.size();
    }

    public boolean absolute(int row) {
        if (row < 1 || row > list.size()) return false;
        cursor = row;
        return true;
    }

    public boolean relative(int numRows) {
        int target = cursor + numRows;
        if (target < 1 || target > list.size()) return false;
        cursor = target;
        return true;
    }

    public CDataRow getCurrentRow() throws CDataGridException {
        return getRowAtCursor(cursor);
    }

    private CDataRow getRowAtCursor(int pos) throws CDataGridException {
        if (pos < 1 || pos > list.size())
            throw new CDataGridException("Cursor position " + pos + " is invalid");
        return list.get(pos - 1);
    }

    // --- Typed accessors by column name ---

    public String getString(String col) throws CDataGridException { return getString(metaData.getColumnIndex(col)); }
    public Character getChar(String col) throws CDataGridException { return getChar(metaData.getColumnIndex(col)); }
    public Boolean getBoolean(String col) throws CDataGridException { return getBoolean(metaData.getColumnIndex(col)); }
    public Byte getByte(String col) throws CDataGridException { return getByte(metaData.getColumnIndex(col)); }
    public Short getShort(String col) throws CDataGridException { return getShort(metaData.getColumnIndex(col)); }
    public Integer getInt(String col) throws CDataGridException { return getInt(metaData.getColumnIndex(col)); }
    public Long getLong(String col) throws CDataGridException { return getLong(metaData.getColumnIndex(col)); }
    public Float getFloat(String col) throws CDataGridException { return getFloat(metaData.getColumnIndex(col)); }
    public Double getDouble(String col) throws CDataGridException { return getDouble(metaData.getColumnIndex(col)); }
    public java.util.Date getDate(String col) throws CDataGridException { return getDate(metaData.getColumnIndex(col)); }
    public java.sql.Time getTime(String col) throws CDataGridException { return getTime(metaData.getColumnIndex(col)); }
    public java.sql.Timestamp getTimestamp(String col) throws CDataGridException { return getTimestamp(metaData.getColumnIndex(col)); }
    public Object getObject(String col) throws CDataGridException { return getObject(metaData.getColumnIndex(col)); }

    // --- Typed accessors by column index ---

    public String getString(int idx) throws CDataGridException {
        return (String) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.STRING);
    }
    public Character getChar(int idx) throws CDataGridException {
        return (Character) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.CHARACTER);
    }
    public Boolean getBoolean(int idx) throws CDataGridException {
        return (Boolean) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.BOOLEAN);
    }
    public Byte getByte(int idx) throws CDataGridException {
        return (Byte) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.BYTE);
    }
    public Short getShort(int idx) throws CDataGridException {
        return (Short) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.SHORT);
    }
    public Integer getInt(int idx) throws CDataGridException {
        return (Integer) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.INTEGER);
    }
    public Long getLong(int idx) throws CDataGridException {
        return (Long) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.LONG);
    }
    public Float getFloat(int idx) throws CDataGridException {
        return (Float) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.FLOAT);
    }
    public Double getDouble(int idx) throws CDataGridException {
        return (Double) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.DOUBLE);
    }
    public java.util.Date getDate(int idx) throws CDataGridException {
        return (java.util.Date) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.DATE);
    }
    public java.sql.Time getTime(int idx) throws CDataGridException {
        return (java.sql.Time) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.TIME);
    }
    public java.sql.Timestamp getTimestamp(int idx) throws CDataGridException {
        return (java.sql.Timestamp) CDataConverter.convertTo(getRowAtCursor(cursor).getValue(idx), CTypes.TIMESTAMP);
    }
    public Object getObject(int idx) throws CDataGridException {
        return getRowAtCursor(cursor).getValue(idx);
    }

    // --- Mutator ---

    public void setValue(String columnName, Object value) throws CDataGridException {
        setValue(metaData.getColumnIndex(columnName), value);
    }

    public void setValue(int columnIndex, Object value) throws CDataGridException {
        getCurrentRow().setValue(columnIndex, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ROWSET CONTENTS: \n");
        sb.append(metaData.toString());
        for (CDataRow row : list) sb.append(row.toString()).append("\n");
        return sb.toString();
    }
}
