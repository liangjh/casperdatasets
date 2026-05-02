package net.casper.data.model;

import java.io.Serializable;

/**
 * Abstract index on a column for optimized lookups. Concrete implementations:
 * unique index and {@link CDataCacheNonUniqueIndex}.
 */
public abstract class CDataCacheIndex implements Serializable {

    protected String columnName;
    protected int columnIndex;

    public CDataCacheIndex(String columnName, int columnIndex) throws CDataGridException {
        if (columnName == null) throw new CDataGridException("Indexed column name cannot be null.");
        if (columnIndex < 0) throw new CDataGridException("Indexed column index must be >= 0.");
        this.columnName = columnName;
        this.columnIndex = columnIndex;
    }

    public abstract CDataRow[] get(Object value) throws CDataGridException;
    public abstract CDataRow[] get(Object[] value) throws CDataGridException;
    public abstract boolean contains(Object value) throws CDataGridException;
    public abstract void update(CDataRow[] rows) throws CDataGridException;
    public abstract void index(CDataRow[] rows) throws CDataGridException;

    public String getColumnName() { return columnName; }
    public int getColumnIndex() { return columnIndex; }
}
