package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;
import net.casper.data.model.CRowMetaData;

/**
 * Base class for all dataset filters. Subclasses implement {@link #doesMatch(CDataRow)}.
 */
public abstract class CDataFilter {

    protected String columnName;
    protected int columnIndex = -1;
    protected CRowMetaData metaDefinition;

    public CDataFilter(String columnName) throws CDataGridException {
        if (columnName == null)
            throw new CDataGridException("Column name cannot be null.");
        this.columnName = columnName;
    }

    public abstract boolean doesMatch(CDataRow row) throws CDataGridException;

    public void checkColumnIndexInitialized() throws CDataGridException {
        if (columnIndex < 0) {
            if (columnName == null) throw new CDataGridException("Column name not initialized");
            if (metaDefinition == null) throw new CDataGridException("Meta definition not initialized");
            columnIndex = metaDefinition.getColumnIndex(columnName);
        }
    }

    public String getColumnName() { return columnName; }
    public int getColumnIndex() { return columnIndex; }
    public CRowMetaData getMetaDefinition() { return metaDefinition; }
    public void setMetaDefinition(CRowMetaData metaDefinition) { this.metaDefinition = metaDefinition; }
}
