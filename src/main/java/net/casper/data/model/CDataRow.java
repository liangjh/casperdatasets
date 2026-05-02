package net.casper.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A single row of data -- wrapper around Object[].
 */
public class CDataRow implements Serializable {

    private static final long serialVersionUID = 1L;

    private Object[] row = new Object[0];

    public CDataRow() {
    }

    public CDataRow(int numColumns) throws CDataGridException {
        if (numColumns < 1)
            throw new CDataGridException("Number of columns in row must be greater than 0.");
        this.row = new Object[numColumns];
    }

    public CDataRow(Object[] row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row values are null.");
        this.row = row;
    }

    public void ensureCardinality(int size) {
        if (size < 1 || size < row.length)
            return;
        Object[] ensuredRow = new Object[size];
        System.arraycopy(row, 0, ensuredRow, 0, row.length);
        this.row = ensuredRow;
    }

    public int getNumberColumns() {
        return row.length;
    }

    public Object getValue(int columnIndex) throws CDataGridException {
        checkArrayBounds(columnIndex);
        return row[columnIndex];
    }

    public void setValue(int columnIndex, Object value) throws CDataGridException {
        checkArrayBounds(columnIndex);
        row[columnIndex] = value;
    }

    public Object[] getRawData() {
        return row;
    }

    public void setRawData(Object[] row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row values are null.");
        this.row = row;
    }

    private void checkArrayBounds(int columnIndex) throws CDataGridException {
        if (row.length <= columnIndex || columnIndex < 0)
            throw new CDataGridException("Array out of bounds: row length = " + row.length + ", requested index: " + columnIndex);
    }

    public Map<String, Object> toMap(CRowMetaData metaData) throws CDataGridException {
        if (metaData == null)
            return new HashMap<>();

        String[] columnNames = metaData.getColumnNames();
        HashMap<String, Object> map = new HashMap<>();
        for (String columnName : columnNames) {
            try {
                map.put(columnName, row[metaData.getColumnIndex(columnName)]);
            } catch (Exception ex) {
                // skip columns that can't be mapped
            }
        }
        return map;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            sb.append(row[i] == null ? "" : row[i].toString());
            if (i < row.length - 1) sb.append("\t");
        }
        return sb.toString();
    }
}
