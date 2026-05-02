package net.casper.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A CDataRowSet represents one or more rows of data, stored in list format.
 * Typically, a query would retrieve all necessary data from the underlying data store
 * (cache or primary), and populate this object to hold the subset of information
 * retrieved from the data store.
 * <br/><br/>
 * WARNING :: this class is *NOT* thread-safe.  You should not share a rowset between
 * multiple threads, this is meant to be used in the context of a single thread / single interaction.
 * The CDataCacheContainer is multi-threaded, and therefore acceptable for persistent storage.
 * <br/><br/>
 * 1. Provides scrolling functionality (akin to java.sql.Resultset)
 * 2. Implements datatype conversions by column (akin to java.sql.Resultset)
 * 3. Sorting is possible on rows in set
 * 4. Simple aggregation can be performed on columns in the rowset.  (sum, average, weighted average)
 *    (Note that for aggregation, the column's type must be a number)
 * <br/><br/>
 * Note that the index of the "cursor" begins at 1, whereas the internal storage
 * of the row in the list begins at 0.  This is an internal detail, if you intend
 * to modify this class.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CDataRowSet implements Serializable {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /**
     * List data structure of all rows.  This list can be reordered / sorted.
     * A cursor variable provides for scrolling functionality on the data.
     */
    private ArrayList list = new ArrayList();

    /** Meta-data object */
    private CRowMetaData metaData = null;

    /** This is used for scrolling thru the set of results */
    private int cursor = 0;

    /**
     * Disallow private instantiation
     */
    private CDataRowSet() {
    }

    /**
     * Initializes rowset meta-data
     *
     * @param metaData - the meta data object configured for this rowset
     * @throws CDataGridException
     */
    public CDataRowSet(CRowMetaData metaData) throws CDataGridException {
        if (metaData == null)
            throw new CDataGridException("Meta data object cannot be null.");
        this.metaData = metaData;
    }

    /**
     * Adds rows to the row set.  If the rows do not match an exception will be thrown.
     * This operation attempts to be "transactional", meaning, if one of the rows fail to qualify,
     * none of the rows are added to the rowset
     *
     * @param rows - array of rows to add to rowset
     * @throws CDataGridException
     */
    public void addData(CDataRow[] rows) throws CDataGridException {
        if (rows == null || rows.length < 1)
            return;

        for (int i = 0; i < rows.length; i++) {
            if (rows[i] == null || rows[i].getNumberColumns() != metaData.getNumberColumns())
                throw new CDataGridException("Column mismatch between meta-data definition and row data.");
        }

        list.ensureCapacity(list.size() + rows.length);
        for (int j = 0; j < rows.length; j++)
            list.add(rows[j]);
    }

    /**
     * Returns meta-data definition for this row/data set
     * @return CRowMetaData - the meta data definition for this rowset
     */
    public CRowMetaData getMetaDefinition() {
        return metaData;
    }

    /**
     * Sorts the collection by the specified column name, (if exists)
     * Flag indicates the order in which the collection should be returned.
     *
     * Note: this should not be called unless the cursor has been reset.  Call: reset()
     * prior to sort. Otherwise, the scrolling order of the entire rowset will be corrupted.
     *
     * @param columnNames - column name to sort by
     * @param ascending - true, if in ascending order
     * @throws CDataGridException
     */
    public void sortByColumn(String[] columnNames, boolean ascending)
            throws CDataGridException {
        if (cursor > 0)
            throw new CDataGridException("Cursor must be reset, before re-sorting.  Scroll order will be corrupted.");

        int[] columnIndices  = metaData.getColumnIndices(columnNames);
        Class[] columnTypes = metaData.getColumnTypes(columnIndices);
        CDataComparator rowComparator = new CDataComparator(columnIndices, columnTypes);

        try {
            Collections.sort(list, rowComparator);
        } catch (RuntimeException e) {
            throw new CDataGridException(e.getMessage(), e);
        }

        if (!ascending)
            Collections.reverse(list);
    }

    /**
     * Returns number of rows in rowset
     * @return cardinality of rowset
     */
    public int getNumberRows() {
        return size();
    }

    /**
     * Returns number of rows in rowset
     * @return cardinality of rowset
     */
    public int size() {
        return list.size();
    }

    /**
     * Returns a collection of column name - column value mappings for this object
     */
    public Map[] toMapArray() throws CDataGridException {
        if (list == null)
            return new HashMap[0];

        ArrayList mapList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            CDataRow row = (CDataRow) mapList.get(i);
            Map rowMap = row.toMap(metaData);
            mapList.add(rowMap);
        }

        Map[] mappedRows = new HashMap[mapList.size()];
        mapList.toArray(mappedRows);
        return mappedRows;
    }

    /**
     * Returns an array of all CDataRow objects held by this row set
     * @return CDataRow[] - array of all rows in rowset
     */
    public CDataRow[] getAllRows() {
        CDataRow[] allRows = new CDataRow[list.size()];
        list.toArray(allRows);
        return allRows;
    }

    /**
     * Retrieves all values within a single column
     *
     * @return
     */
    public Object[] getColumnValues(String columnName) throws CDataGridException {
        CDataRow[] rows = getAllRows();
        if (rows == null || rows.length < 1)
            return new Object[0];

        int colIndex = metaData.getColumnIndex(columnName);
        Object[] colValues = new Object[rows.length];
        for (int i = 0; i < rows.length; i++)
            colValues[i] = rows[i].getValue(colIndex);

        return colValues;
    }

    /**
     * Returns current cursor position.   Note: "1" corresponds to the *first* row
     * @return current position
     */
    public int getCursorPosition() {
        return cursor;
    }

    /**
     * Returns true if the cursor sits before the first row
     * @return true, if the cursor is in initial position
     */
    public boolean isBeforeFirst() {
        return (cursor == 0);
    }

    /**
     * Returns true if the cursor sits after the last row position
     * @return true, if cursor position follows the last row
     */
    public boolean isAfterLast() {
        return (cursor > getNumberRows());
    }

    /**
     * Returns true if the cursor sits at first row position
     * @return true, if current position points to first row
     */
    public boolean isFirst() {
        return (cursor == 1);
    }

    /**
     * Returns true if cursor sits at last row position
     * @return true, if current position points to last row
     */
    public boolean isLast() {
        return (cursor == getNumberRows());
    }

    /**
     * Manually resets the cursor
     */
    public void beforeFirst() {
        cursor = 0;
    }

    /**
     * Manually resets the cursor
     */
    public void reset() {
        cursor = 0;
    }

    /**
     * Manually sets cursor to end of cursor boundary
     */
    public void afterLast() {
        cursor = getNumberRows() + 1;
    }

    /**
     * Manually sets cursor to first row
     * @return true, if successful
     */
    public boolean first() {
        if (getNumberRows() == 0)
            return false;
        cursor = 1;
        return true;
    }

    /**
     * Manually sets cursor to last row
     * @return true, if successful
     */
    public boolean last() {
        if (getNumberRows() == 0)
            return false;
        cursor = getNumberRows();
        return true;
    }

    /**
     * Manually iterates backwards one row
     * @return true, if successful
     */
    public boolean previous() {
        if (isBeforeFirst())
            return false;
        cursor--;
        if (cursor == 0)
            return false;
        return true;
    }

    /**
     * Manually iterates cursor forward one row
     * @return true, if successful
     * @throws CDataGridException
     */
    public boolean next() throws CDataGridException {
        if (isAfterLast())
            return false;
        cursor++;
        if (cursor > getNumberRows())
            return false;
        return true;
    }

    /**
     * Sets cursor to a specific index
     *
     * @param row
     * @return
     * @throws CDataGridException
     */
    public boolean absolute(int row) {
        if (row > getNumberRows() || row < 1) {
            return false;
        }
        cursor = row;
        return true;
    }

    /**
     * Sets cursor to an offset from the current cursor location
     *
     * @param numRows
     * @return
     * @throws CDataGridException
     */
    public boolean relative(int numRows) {
        if ((cursor + numRows) > getNumberRows() ||
            (cursor + numRows) < 1) {
            return false;
        }
        cursor += numRows;
        return true;
    }

    /**
     * Returns the current row pointed to by the cursor in this rowset
     *
     * @return current CDataRow pointed to by cursor
     * @throws CDataGridException
     */
    public CDataRow getCurrentRow() throws CDataGridException {
        return getRowAtCursor(cursor);
    }

    /**
     * Returns the row at the specified cursor
     *
     * @param rowCursorIndex
     * @return CDataRow
     * @throws CDataGridException
     */
    private CDataRow getRowAtCursor(int rowCursorIndex) throws CDataGridException {
        if (rowCursorIndex < 1 || rowCursorIndex > list.size())
            throw new CDataGridException("The cursor position: " + rowCursorIndex + " does not point to a valid row in this dataset");
        return (CDataRow) list.get(rowCursorIndex - 1);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public String getString(String columnName) throws CDataGridException {
        return getString(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Character getChar(String columnName) throws CDataGridException {
        return getChar(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Boolean getBoolean(String columnName) throws CDataGridException {
        return getBoolean(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Byte getByte(String columnName) throws CDataGridException {
        return getByte(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Short getShort(String columnName) throws CDataGridException {
        return getShort(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Integer getInt(String columnName) throws CDataGridException {
        return getInt(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Long getLong(String columnName) throws CDataGridException {
        return getLong(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return String
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Float getFloat(String columnName) throws CDataGridException {
        return getFloat(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Double getDouble(String columnName) throws CDataGridException {
        return getDouble(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.util.Date getDate(String columnName) throws CDataGridException {
        return getDate(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.sql.Time getTime(String columnName) throws CDataGridException {
        return getTime(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.sql.Timestamp getTimestamp(String columnName) throws CDataGridException {
        return getTimestamp(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns Object at given column name.
     * @param columnName
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Object getObject(String columnName) throws CDataGridException {
        return getObject(metaData.getColumnIndex(columnName));
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public String getString(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (String) CDataConverter.convertTo(value, CTypes.STRING);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Character getChar(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Character) CDataConverter.convertTo(value, CTypes.CHARACTER);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Boolean getBoolean(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Boolean) CDataConverter.convertTo(value, CTypes.BOOLEAN);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Byte getByte(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Byte) CDataConverter.convertTo(value, CTypes.BYTE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Short getShort(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Short) CDataConverter.convertTo(value, CTypes.SHORT);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Integer getInt(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Integer) CDataConverter.convertTo(value, CTypes.INTEGER);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Long getLong(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Long) CDataConverter.convertTo(value, CTypes.LONG);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Float getFloat(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Float) CDataConverter.convertTo(value, CTypes.FLOAT);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Double getDouble(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (Double) CDataConverter.convertTo(value, CTypes.DOUBLE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.util.Date getDate(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (java.util.Date) CDataConverter.convertTo(value, CTypes.DATE);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.sql.Time getTime(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (java.sql.Time) CDataConverter.convertTo(value, CTypes.TIME);
    }

    /**
     * Returns type-safe representation of current row at given column.
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public java.sql.Timestamp getTimestamp(int columnIndex) throws CDataGridException {
        Object value = getRowAtCursor(cursor).getValue(columnIndex);
        return (java.sql.Timestamp) CDataConverter.convertTo(value, CTypes.TIMESTAMP);
    }

    /**
     * Returns Object at specified column index.  Note that there is not upcasting done
     * here - this allows for generic handling of data...
     * @param columnIndex column Index
     * @return data
     * @throws CDataGridException if column cannot be accessed as type
     */
    public Object getObject(int columnIndex) throws CDataGridException {
        return getRowAtCursor(cursor).getValue(columnIndex);
    }

    /**
     * Sets value for a particular column, for current data row.
     * @param columnName - column name to set value for
     * @param value - value to set
     * @throws CDataGridException if column cannot be set
     */
    public void setValue(String columnName, Object value) throws CDataGridException {
        setValue(metaData.getColumnIndex(columnName), value);
    }

    /**
     * Sets value for a particular column, for current data row.
     * @param columnIndex column Index
     * @param value
     * @throws CDataGridException
     */
    public void setValue(int columnIndex, Object value) throws CDataGridException {
        CDataRow currentRow = getCurrentRow();
        currentRow.setValue(columnIndex, value);
    }

    /**
     * Outputs contents of the cache.
     * @return rowset contents, in string format
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        try {
            sbuf.append("ROWSET CONTENTS: \n");
            sbuf.append(metaData.toString());
            for (int i = 0; i < list.size(); i++) {
                CDataRow row = (CDataRow) list.get(i);
                sbuf.append(row.toString()).append("\n");
            }
        } catch (Exception ex) {
            System.out.println(ex.toString());
            System.out.println(CDataGridException.getStackTraceAsString(ex));
        }
        return sbuf.toString();
    }

}
