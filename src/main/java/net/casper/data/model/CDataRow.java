package net.casper.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a row of data.  This is essentially a wrapper around an array (Object[]).
 * A rowset or cache container will store a collection of these objects, either in
 * list or map format.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CDataRow implements Serializable {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /** The underlying row object */
    private Object[] row = new Object[0];

    /**
     * Beware of empty instantiation, as ArrayIndexOutOfBounds
     * exception will be thrown unless the array is instantiated.
     */
    public CDataRow() {
    }

    /**
     * Creates new row object w/ specified number of columns
     * @param numColumns
     */
    public CDataRow(int numColumns) throws CDataGridException {
        if (numColumns < 1)
            throw new CDataGridException("Number of columns in row must be greater than 0.");
        this.row = new Object[numColumns];
    }

    /**
     * Sets row object
     * @param row - the array of objects in this row, by column
     * @throws CDataGridException
     */
    public CDataRow(Object[] row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row values are null - cannot be set into data row object.");
        this.row = row;
    }

    /**
     * To -expand- the size of the row object only.  will not truncate.
     * @param size
     */
    public void ensureCardinality(int size) {
        if (size < 1 || size < row.length)
            return;

        Object[] ensuredRow = new Object[size];
        System.arraycopy(row, 0, ensuredRow, 0, row.length);
        this.row = ensuredRow;
    }

    /**
     * Returns the number of columns in this dataset
     * @return number of columns in this row
     */
    public int getNumberColumns() {
        return row.length;
    }

    /**
     * Return value at specified index
     * @return the value at a given index in this row
     * @throws CDataGridException
     */
    public Object getValue(int columnIndex) throws CDataGridException {
        checkArrayBounds(columnIndex);
        return row[columnIndex];
    }

    /**
     * Set value in a particular column
     *
     * @param columnIndex - the index of the column, value we are setting
     * @param value - the value at a given column
     * @throws CDataGridException
     */
    public void setValue(int columnIndex, Object value) throws CDataGridException {
        checkArrayBounds(columnIndex);
        this.row[columnIndex] = value;
    }

    /**
     * Return raw Object[] row object
     * @return row
     */
    public Object[] getRawData() {
        return row;
    }

    /**
     * Sets a raw Object[] representing this row.
     * Warning - its possible to *completely* corrupt your row (and all dataset operations)
     * by using this operation.
     *
     * @param row
     * @throws CDataGridException
     */
    public void setRawData(Object[] row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row values are null - cannot be set into data row object.");
        this.row = row;
    }

    /**
     * Throws an exception if the specified array index is out of bounds
     *
     * @param columnIndex - index of column we are checking bounds for
     * @throws CDataGridException
     */
    private void checkArrayBounds(int columnIndex) throws CDataGridException {
        if (row.length <= columnIndex || columnIndex < 0)
            throw new CDataGridException("Array out of bounds: row length = " + row.length + ", requested column index: " + columnIndex);
    }

    /**
     * Given a meta-data object, returns a map representation
     * This is a *silent* implementation.  If there are fields that don't exist, it will just
     * ignore and try its best to fill in as much data as possible.
     *
     * @param metaData
     * @return
     */
    public Map toMap(CRowMetaData metaData) throws CDataGridException {
        if (metaData == null)
            return new HashMap();

        String[] columnNames = metaData.getColumnNames();
        HashMap map = new HashMap();
        for (int i = 0; i < columnNames.length; i++) {
            try {
                map.put(columnNames[i], row[metaData.getColumnIndex(columnNames[i])]);
            } catch (Exception ex) {
            }
        }
        return map;
    }

    /**
     * For debugging purposes, returns string
     * representation of this object
     *
     * @return string representation of this object
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        if (row != null) {
            for (int i = 0; i < row.length; i++) {
                sbuf.append(row[i] == null ? "" : row[i].toString());
                if (i < row.length - 1)
                    sbuf.append("\t");
            }
        }
        return sbuf.toString();
    }

}
