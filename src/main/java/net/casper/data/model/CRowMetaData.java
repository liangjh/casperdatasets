package net.casper.data.model;

import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.util.HashMap;

/**
 * Contains meta-data / configuration information for a "table" or "rowset" in cdataset.
 * This includes the column names types, and mappings between the two associations
 * Also contains primary key configuration for the dataset.
 * <br/><br/>
 * Because the current interface implements the ResultSetMetaData interface, it should be compatible,
 * for the most part, with a sufficient set of core JDBC functionality.
 *
 * Requires Java 1.4 (or later).
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CRowMetaData
        implements Serializable, Cloneable, ResultSetMetaData {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /** Delimiter for composite pk's */
    public static final String COMPOSITE_KEY_DELIMITER = ":";

    public static final String IDENTITY_PK = "!IDENTITY_PK!";

    /** Column Names */
    private String[] columnNames = new String[0];

    /** Column Types */
    private Class[] columnTypes = new Class[0];

    /** Column Names of primary keys for this row set type */
    private String[] primaryKeyColumns = new String[0];

    /** Column Names -> Index */
    private HashMap labelMap = null;

    /**
     * Empty instantiation not allowed
     */
    private CRowMetaData() {
    }

    /**
     * Instantiation of meta data object, with column names / types, PK fields.
     *
     * @param columnNames
     * @param columnTypes
     * @param primaryKeyColumns
     * @throws CDataGridException
     */
    public CRowMetaData(String[] columnNames, Class[] columnTypes, String[] primaryKeyColumns)
            throws CDataGridException {
        if (columnNames == null || columnTypes == null)
            throw new CDataGridException("Column Names and Column Types must be provided.");
        if (columnNames.length != columnTypes.length)
            throw new CDataGridException("Arrays: columnNames, and columnTypes not equivalent.  ");

        this.columnNames        = columnNames;
        this.columnTypes        = columnTypes;
        this.primaryKeyColumns  = primaryKeyColumns;

        labelMap = new HashMap();
        for (int i = 0; i < columnNames.length; i++)
            labelMap.put(columnNames[i], Integer.valueOf(i));

        if (primaryKeyColumns != null) {
            for (int j = 0; j < primaryKeyColumns.length; j++) {
                if (!labelMap.containsKey(primaryKeyColumns[j]))
                    throw new CDataGridException("A primary key specified for this cache: " + primaryKeyColumns[j] + " does not exist. ");
            }
        }
    }

    /**
     * Returns column names
     * @return column names
     */
    public String[] getColumnNames() {
        return this.columnNames;
    }

    /**
     * Returns column types
     * @return column types
     */
    public Class[] getColumnTypes() {
        return this.columnTypes;
    }

    /**
     * Returns column names of the primary key for this cache
     * @return primary key column(s) (multiple, if composite)
     */
    public String[] getPrimaryKeyColumns() {
        return this.primaryKeyColumns;
    }

    /**
     * Returns number of columns in this dataset type
     * @return number of columns
     */
    public int getNumberColumns() {
        return columnNames.length;
    }

    /**
     * Returns true, if the column name exists in the meta-data object
     * @param columnName
     * @return true, if column name exists in this dataset
     */
    public boolean containsColumn(String columnName) {
        if (labelMap == null || columnName == null)
            return false;
        return labelMap.containsKey(columnName);
    }

    /**
     * Retrieves index for current column
     * @param columnName
     * @return index for columnName in Object[]
     * @throws CDataGridException
     */
    public int getColumnIndex(String columnName) throws CDataGridException {
        if (!containsColumn(columnName))
            throw new CDataGridException("Rowset meta-data does not contain column with name: " + columnName);
        Integer idx = (Integer) labelMap.get(columnName);
        return idx.intValue();
    }

    /**
     * For a list of column names, return all column indices associated with this
     * @param columnNames
     * @return array of data values
     * @throws CDataGridException
     */
    public int[] getColumnIndices(String[] columnNames) throws CDataGridException {
        if (columnNames == null || columnNames.length < 1)
            return new int[0];

        int[] idxs = new int[columnNames.length];
        for (int i = 0; i < columnNames.length; i++) {
            idxs[i] = getColumnIndex(columnNames[i]);
        }
        return idxs;
    }

    /**
     * Returns indices of the primary key columns
     * @return int[] of primary key indices
     * @throws CDataGridException
     */
    public int[] getPrimaryKeyColumnIndices() throws CDataGridException {
        int[] pkIndices = new int[primaryKeyColumns.length];
        for (int i = 0; i < primaryKeyColumns.length; i++)
            pkIndices[i] = getColumnIndex(primaryKeyColumns[i]);
        return pkIndices;
    }

    /**
     * Retrieves type for current column name
     * @param columnName
     * @return column type
     * @throws CDataGridException
     */
    public Class getColumnType(String columnName) throws CDataGridException {
        int columnIndex = getColumnIndex(columnName);
        return getColumnTypeCls(columnIndex);
    }

    /**
     * Retrieves type for current column index
     * @param columnIndex
     * @return column type
     * @throws CDataGridException
     */
    public Class getColumnTypeCls(int columnIndex) throws CDataGridException {
        return columnTypes[columnIndex];
    }

    /**
     * @param columnIndices
     * @return
     * @throws CDataGridException
     */
    public Class[] getColumnTypes(int[] columnIndices) throws CDataGridException {
        if (columnIndices == null || columnIndices.length < 1)
            return new Class[0];

        Class[] types = new Class[columnIndices.length];
        for (int i = 0; i < types.length; i++) {
            types[i] = getColumnTypeCls(columnIndices[i]);
        }
        return types;
    }

    /**
     * Given the meta-data configuration, create the primary key.
     * For a single-column PK, we just return the value at that particular data row.
     * For a multi-column (composite) PK, we need to concatenate the stringified
     * values of the multi-key configuration
     *
     * @param row
     * @return primary key
     * @throws CDataGridException
     */
    public Object createPrimaryKey(CDataRow row) throws CDataGridException {
        if (row == null)
            throw new CDataGridException("Row is null, could not create primary key.");

        if (primaryKeyColumns.length == 1) {
            int columnIndex = getColumnIndex(primaryKeyColumns[0]);
            return row.getValue(columnIndex);
        } else {
            StringBuffer sbuf = new StringBuffer();
            for (int i = 0; i < primaryKeyColumns.length; i++) {
                int columnIndex = getColumnIndex(primaryKeyColumns[i]);
                Object value = row.getValue(columnIndex);
                if (value != null) {
                    sbuf.append(value.toString());
                    if (i < (primaryKeyColumns.length - 1))
                        sbuf.append(COMPOSITE_KEY_DELIMITER);
                }
            }
            return sbuf.toString();
        }
    }

    /**
     * Returns true, if the input object is equivalent to the current object
     * (Must also be of the same type)
     *
     * @param obj
     * @return true, if objects are equivalent
     */
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof CRowMetaData))
            return false;

        CRowMetaData targetMeta = (CRowMetaData) obj;
        String[] targetColNames = targetMeta.getColumnNames();
        Class[] targetColTypes  = targetMeta.getColumnTypes();
        String[] targetPkColumns = targetMeta.getPrimaryKeyColumns();

        if (targetColNames == null || targetColTypes == null || targetPkColumns == null)
            return false;
        if (targetColNames.length != columnNames.length || targetColTypes.length != columnTypes.length || targetPkColumns.length != primaryKeyColumns.length)
            return false;

        boolean equiv = true;
        for (int i = 0; i < targetColNames.length; i++) {
            try {
                if (!targetColNames[i].equals(columnNames[i]) ||
                    !targetColTypes[i].equals(columnTypes[i])) {
                    equiv = false;
                    break;
                }
            } catch (Exception ex) {
                equiv = false;
                break;
            }
        }

        for (int i = 0; i < targetPkColumns.length; i++) {
            try {
                if (!targetPkColumns[i].equals(primaryKeyColumns[i])) {
                    equiv = false;
                    break;
                }
            } catch (Exception ex) {
                equiv = false;
                break;
            }
        }
        return equiv;
    }

    /**
     * Adds new column(s) to the meta definition.
     * If the column already exists, then it will be ignored.
     * Synchronized, to prevent multiple threads from clashing.
     *
     * @param addColumnNames
     * @param addColumnTypes
     * @throws CDataGridException
     */
    public synchronized void addColumns(String[] addColumnNames, Class[] addColumnTypes)
            throws CDataGridException {
        if (addColumnNames == null || addColumnTypes == null || addColumnNames.length != addColumnTypes.length)
            throw new CDataGridException("Column name and column type are both required.");

        for (int i = 0; i < addColumnNames.length; i++) {
            if (labelMap.containsKey(addColumnNames[i]))
                throw new CDataGridException("Column: " + addColumnNames[i] + " already exists.");
        }

        String[] newColumnNames = new String[columnNames.length + addColumnNames.length];
        Class[]  newColumnTypes = new Class[columnTypes.length + addColumnTypes.length];

        System.arraycopy(columnNames, 0, newColumnNames, 0, columnNames.length);
        System.arraycopy(addColumnNames, 0, newColumnNames, columnNames.length, addColumnNames.length);

        System.arraycopy(columnTypes, 0, newColumnTypes, 0, columnTypes.length);
        System.arraycopy(addColumnTypes, 0, newColumnTypes, columnTypes.length, addColumnTypes.length);

        HashMap newLabelMap = (HashMap) labelMap.clone();
        for (int i = 0; i < newColumnNames.length; i++)
            newLabelMap.put(newColumnNames[i], Integer.valueOf(i));

        columnNames = newColumnNames;
        columnTypes = newColumnTypes;
        labelMap = newLabelMap;
    }

    /**
     * Returns string representation of this meta-data object
     * @return string
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        try {
            sbuf.append("----------- METADATA DEFINITION ----------").append("\n");
            for (int i = 0; i < columnNames.length; i++) {
                String colNameV = columnNames[i];
                String colTypeV = getColumnType(colNameV).getName();
                sbuf.append("{").append(colNameV).append(":").append(colTypeV).append("}").append("\t");
            }
            sbuf.append("\n");
            sbuf.append("------------------------------------------").append("\n");
        } catch (Exception ex) {
            System.out.println(ex.toString());
            System.out.println(CDataGridException.getStackTraceAsString(ex));
        }
        return sbuf.toString();
    }

    /**
     * Creates a replica of itself.
     * @return Object
     */
    public Object clone() throws CloneNotSupportedException {
        CRowMetaData metaClone = (CRowMetaData) super.clone();
        metaClone.columnNames = (String[])this.columnNames.clone();
        metaClone.columnTypes = (Class[])this.columnTypes.clone();
        metaClone.primaryKeyColumns = (String[])this.primaryKeyColumns.clone();
        metaClone.labelMap = (HashMap)this.labelMap.clone();
        return metaClone;
    }

    /**
     * This is necessary because the column index numbers that are expected begin with 1..n, whereas
     * the columns that is actually stored in this meta data implementation are stored within arrays,
     * beginning at index 0.
     *
     * @param column
     * @return transformed column index
     */
    private int transformColumnToArrayIndex(int column) {
        return (column - 1);
    }

    public String getCatalogName(int column) {
        return null;
    }

    public String getColumnClassName(int column) {
        Class cls = null;
        try { cls = getColumnTypeCls(transformColumnToArrayIndex(column)); } catch (Exception ex) {}
        return cls.getName();
    }

    public int getColumnCount() {
        return getNumberColumns();
    }

    public int getColumnDisplaySize(int column) {
        return 100;
    }

    public String getColumnLabel(int column) {
        int transCol = transformColumnToArrayIndex(column);
        if (columnNames == null || columnNames.length < column)
            return null;
        return columnNames[transCol];
    }

    public String getColumnName(int column) {
        return getColumnLabel(column);
    }

    public int getColumnType(int column) {
        int transCol = transformColumnToArrayIndex(column);
        Class type = null;
        try { type = getColumnTypeCls(transCol); } catch (Exception ex) {}
        return CTypes.getJavaObjType(type);
    }

    public String getColumnTypeName(int column) {
        return null;
    }

    public int getPrecision(int column) {
        return 5;
    }

    public int getScale(int column) {
        return 5;
    }

    public String getSchemaName(int column) {
        return null;
    }

    public String getTableName(int column) {
        return null;
    }

    public boolean isAutoIncrement(int column) {
        return false;
    }

    public boolean isCaseSensitive(int column) {
        return true;
    }

    public boolean isCurrency(int column) {
        return false;
    }

    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    public int isNullable(int column) {
        return ResultSetMetaData.columnNullableUnknown;
    }

    public boolean isReadOnly(int column) {
        return true;
    }

    public boolean isSearchable(int column) {
        return false;
    }

    public boolean isSigned(int column) {
        return false;
    }

    public boolean isWritable(int column) {
        return false;
    }

    public <T> T unwrap(Class<T> iface) throws java.sql.SQLException {
        throw new java.sql.SQLException("Not supported");
    }

    public boolean isWrapperFor(Class<?> iface) throws java.sql.SQLException {
        return false;
    }

}
