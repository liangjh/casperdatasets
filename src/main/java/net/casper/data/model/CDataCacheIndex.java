package net.casper.data.model;

import java.io.Serializable;

/**
 * This class represents an index on a particular column within a data cache.
 * Indices are created to optimize lookups.
 * <br/><br/>
 * There are two concrete implementations of this index:  (1) Unique, which enforces
 * a single value on a given column, and (2) Non-Unique, which accomodates for
 * duplicates on a given column of data.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public abstract class CDataCacheIndex implements Serializable {

    /** Name of column upon which we are building the index */
    protected String columnName = null;

    /** The index of the column in the rowset */
    protected int columnIndex = -1;

    /**
     * Prevent private instantiation
     */
    private CDataCacheIndex() {
    }

    /**
     * Sets config info for index: (columnName, columnIndex)
     *
     * @param columnName - name of column to create index on
     * @param columnIndex - index of column to create index on
     * @throws CDataGridException
     */
    public CDataCacheIndex(String columnName, int columnIndex)
            throws CDataGridException {
        if (columnName == null)
            throw new CDataGridException("Indexed column name cannot be null.");
        if (columnIndex < 0)
            throw new CDataGridException("Indexed column index must be a valid index number (>0).");

        this.columnName  = columnName;
        this.columnIndex = columnIndex;
    }

    /**
     * Retrieves all matching rows by value
     *
     * @param value - the value to match on
     * @return CDataRow[] - array of matching rows
     * @throws CDataGridException
     */
    public abstract CDataRow[] get(Object value) throws CDataGridException;

    /**
     * Retrieves all matching rows by multi-values
     *
     * @param value - the value to match on
     * @return CDataRow[] - array of matching rows
     * @throws CDataGridException
     */
    public abstract CDataRow[] get(Object[] value) throws CDataGridException;

    /**
     * Returns true, if the index contains the particular data value
     *
     * @param value - true, if cache contains this value
     * @return true, if value exists in the index
     * @throws CDataGridException
     */
    public abstract boolean contains(Object value) throws CDataGridException;

    /**
     * Updates index, given rows.
     * This method rebuilds the index (in contrast to next method)
     *
     * @param rows - rows of CDataRow[] objects to update this index with
     * @throws CDataGridException
     */
    public abstract void update(CDataRow[] rows) throws CDataGridException;

    /**
     * Adds the following rows into the index.
     * This method does not rebuild the index (performs an incremental update, instead)
     *
     * @param rows - rows to add to index
     * @throws CDataGridException
     */
    public abstract void index(CDataRow[] rows) throws CDataGridException;

    /**
     * Return column name
     * @return column name that index is configured for
     */
    public String getColumnName() {
        return columnName;
    }

    /**
     * Return column index
     * @return column index that index is configured for
     */
    public int getColumnIndex() {
        return columnIndex;
    }

}
