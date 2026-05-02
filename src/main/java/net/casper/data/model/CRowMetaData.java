package net.casper.data.model;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Column names, types, and primary key configuration for a dataset.
 * Implements ResultSetMetaData for JDBC compatibility.
 */
public class CRowMetaData implements Serializable, Cloneable, ResultSetMetaData {

    private static final long serialVersionUID = 1L;

    public static final String COMPOSITE_KEY_DELIMITER = ":";
    public static final String IDENTITY_PK = "!IDENTITY_PK!";

    private String[] columnNames = new String[0];
    private Class<?>[] columnTypes = new Class[0];
    private String[] primaryKeyColumns = new String[0];
    private HashMap<String, Integer> labelMap = null;

    private CRowMetaData() {
    }

    public CRowMetaData(String[] columnNames, Class<?>[] columnTypes, String[] primaryKeyColumns)
            throws CDataGridException {
        if (columnNames == null || columnTypes == null)
            throw new CDataGridException("Column Names and Column Types must be provided.");
        if (columnNames.length != columnTypes.length)
            throw new CDataGridException("Arrays: columnNames, and columnTypes not equivalent.");

        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.primaryKeyColumns = primaryKeyColumns;

        labelMap = new HashMap<>();
        for (int i = 0; i < columnNames.length; i++)
            labelMap.put(columnNames[i], i);

        if (primaryKeyColumns != null) {
            for (String pk : primaryKeyColumns) {
                if (!labelMap.containsKey(pk))
                    throw new CDataGridException("Primary key column does not exist: " + pk);
            }
        }
    }

    public String[] getColumnNames() { return columnNames; }
    public Class<?>[] getColumnTypes() { return columnTypes; }
    public String[] getPrimaryKeyColumns() { return primaryKeyColumns; }
    public int getNumberColumns() { return columnNames.length; }

    public boolean containsColumn(String columnName) {
        return labelMap != null && columnName != null && labelMap.containsKey(columnName);
    }

    public int getColumnIndex(String columnName) throws CDataGridException {
        if (!containsColumn(columnName))
            throw new CDataGridException("Column not found: " + columnName);
        return labelMap.get(columnName);
    }

    public int[] getColumnIndices(String[] columnNames) throws CDataGridException {
        if (columnNames == null || columnNames.length < 1) return new int[0];
        int[] idxs = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++)
            idxs[i] = getColumnIndex(columnNames[i]);
        return idxs;
    }

    public int[] getPrimaryKeyColumnIndices() throws CDataGridException {
        int[] pkIndices = new int[primaryKeyColumns.length];
        for (int i = 0; i < primaryKeyColumns.length; i++)
            pkIndices[i] = getColumnIndex(primaryKeyColumns[i]);
        return pkIndices;
    }

    public Class<?> getColumnType(String columnName) throws CDataGridException {
        return getColumnTypeCls(getColumnIndex(columnName));
    }

    public Class<?> getColumnTypeCls(int columnIndex) throws CDataGridException {
        return columnTypes[columnIndex];
    }

    public Class<?>[] getColumnTypes(int[] columnIndices) throws CDataGridException {
        if (columnIndices == null || columnIndices.length < 1) return new Class[0];
        Class<?>[] types = new Class[columnIndices.length];
        for (int i = 0; i < types.length; i++)
            types[i] = getColumnTypeCls(columnIndices[i]);
        return types;
    }

    /**
     * Creates primary key from a row. Single-column PK returns the value directly;
     * composite PK concatenates values with {@link #COMPOSITE_KEY_DELIMITER}.
     */
    public Object createPrimaryKey(CDataRow row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row is null, could not create primary key.");

        if (primaryKeyColumns.length == 1)
            return row.getValue(getColumnIndex(primaryKeyColumns[0]));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < primaryKeyColumns.length; i++) {
            Object value = row.getValue(getColumnIndex(primaryKeyColumns[i]));
            if (value != null) {
                sb.append(value);
                if (i < primaryKeyColumns.length - 1) sb.append(COMPOSITE_KEY_DELIMITER);
            }
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof CRowMetaData)) return false;
        CRowMetaData other = (CRowMetaData) obj;
        return Arrays.equals(columnNames, other.columnNames)
            && Arrays.equals(columnTypes, other.columnTypes)
            && Arrays.equals(primaryKeyColumns, other.primaryKeyColumns);
    }

    @Override
    public int hashCode() {
        int result = Arrays.hashCode(columnNames);
        result = 31 * result + Arrays.hashCode(columnTypes);
        result = 31 * result + Arrays.hashCode(primaryKeyColumns);
        return result;
    }

    @SuppressWarnings("unchecked")
    public synchronized void addColumns(String[] addColumnNames, Class<?>[] addColumnTypes)
            throws CDataGridException {
        if (addColumnNames == null || addColumnTypes == null || addColumnNames.length != addColumnTypes.length)
            throw new CDataGridException("Column name and column type are both required.");
        for (String name : addColumnNames) {
            if (labelMap.containsKey(name))
                throw new CDataGridException("Column already exists: " + name);
        }

        String[] newColumnNames = new String[columnNames.length + addColumnNames.length];
        Class<?>[] newColumnTypes = new Class[columnTypes.length + addColumnTypes.length];

        System.arraycopy(columnNames, 0, newColumnNames, 0, columnNames.length);
        System.arraycopy(addColumnNames, 0, newColumnNames, columnNames.length, addColumnNames.length);
        System.arraycopy(columnTypes, 0, newColumnTypes, 0, columnTypes.length);
        System.arraycopy(addColumnTypes, 0, newColumnTypes, columnTypes.length, addColumnTypes.length);

        HashMap<String, Integer> newLabelMap = (HashMap<String, Integer>) labelMap.clone();
        for (int i = 0; i < newColumnNames.length; i++)
            newLabelMap.put(newColumnNames[i], i);

        columnNames = newColumnNames;
        columnTypes = newColumnTypes;
        labelMap = newLabelMap;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("----------- METADATA DEFINITION ----------\n");
        for (int i = 0; i < columnNames.length; i++)
            sb.append("{").append(columnNames[i]).append(":").append(columnTypes[i].getName()).append("}\t");
        sb.append("\n------------------------------------------\n");
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CRowMetaData copy = (CRowMetaData) super.clone();
        copy.columnNames = columnNames.clone();
        copy.columnTypes = columnTypes.clone();
        copy.primaryKeyColumns = primaryKeyColumns.clone();
        copy.labelMap = new HashMap<>(labelMap);
        return copy;
    }

    // --- ResultSetMetaData implementation (1-based column indices) ---

    private int toIdx(int column) { return column - 1; }

    public String getCatalogName(int column) { return null; }

    public String getColumnClassName(int column) {
        try { return getColumnTypeCls(toIdx(column)).getName(); }
        catch (Exception ex) { return null; }
    }

    public int getColumnCount() { return getNumberColumns(); }
    public int getColumnDisplaySize(int column) { return 100; }

    public String getColumnLabel(int column) {
        int idx = toIdx(column);
        return (columnNames != null && idx < columnNames.length) ? columnNames[idx] : null;
    }

    public String getColumnName(int column) { return getColumnLabel(column); }

    public int getColumnType(int column) {
        try { return CTypes.getJavaObjType(getColumnTypeCls(toIdx(column))); }
        catch (Exception ex) { return 0; }
    }

    public String getColumnTypeName(int column) { return null; }
    public int getPrecision(int column) { return 5; }
    public int getScale(int column) { return 5; }
    public String getSchemaName(int column) { return null; }
    public String getTableName(int column) { return null; }
    public boolean isAutoIncrement(int column) { return false; }
    public boolean isCaseSensitive(int column) { return true; }
    public boolean isCurrency(int column) { return false; }
    public boolean isDefinitelyWritable(int column) { return false; }
    public int isNullable(int column) { return ResultSetMetaData.columnNullableUnknown; }
    public boolean isReadOnly(int column) { return true; }
    public boolean isSearchable(int column) { return false; }
    public boolean isSigned(int column) { return false; }
    public boolean isWritable(int column) { return false; }
    public <T> T unwrap(Class<T> iface) throws java.sql.SQLException { throw new java.sql.SQLException("Not supported"); }
    public boolean isWrapperFor(Class<?> iface) { return false; }
}
