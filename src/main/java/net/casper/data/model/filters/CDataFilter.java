package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;
import net.casper.data.model.CRowMetaData;

/**
 * Filters in Casper datasets are used to match entries in a CDataCacheContainer.
 * A filter clause can be created, which will comprise a number of concrete filters.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public abstract class CDataFilter {

    /** Name of column to match on */
    protected String columnName = null;

    /** Index of column to match on */
    protected int columnIndex = -1;

    /** Meta definition */
    protected CRowMetaData metaDefinition = null;

    /**
     * Disallow empty instantiation
     */
    private CDataFilter() {
    }

    /**
     * Create new filter
     *
     * @param columnName
     * @throws CDataGridException
     */
    public CDataFilter(String columnName) throws CDataGridException {
        if (columnName == null)
            throw new CDataGridException("Column name to match upon cannot be null.");
        this.columnName = columnName;
    }

    /**
     * Performs a match for this filter
     *
     * @return true, if this filter matches
     * @throws CDataGridException
     */
    public abstract boolean doesMatch(CDataRow row) throws CDataGridException;

    /**
     * Checks if column has been initialized or not
     * @throws CDataGridException
     */
    public void checkColumnIndexInitialized() throws CDataGridException {
        if (columnIndex < 0) {
            if (columnName == null)
                throw new CDataGridException("Column name not properly initialized");
            if (metaDefinition == null)
                throw new CDataGridException("Meta definition not initialized");
            columnIndex = metaDefinition.getColumnIndex(columnName);
        }
    }

    /**
     * Returns name of column to match on
     * @return name
     */
    public String getColumnName() {
        return this.columnName;
    }

    /**
     * Returns index of column to match on
     * @return int
     */
    public int getColumnIndex() {
        return this.columnIndex;
    }

    /**
     * Return meta definition
     * @return
     */
    public CRowMetaData getMetaDefinition() {
        return this.metaDefinition;
    }

    /**
     * Sets meta definition
     * @param metaDefinition
     */
    public void setMetaDefinition(CRowMetaData metaDefinition) {
        this.metaDefinition = metaDefinition;
    }

}
