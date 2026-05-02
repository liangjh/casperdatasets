package net.casper.data.model;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.LinkedList;
import java.util.Map;

/**
 * Loads a {@link CDataCacheContainer} from a JDBC {@link ResultSet}.
 * If no primary key is provided, the first column is assumed to be the PK.
 */
public final class CDataCacheDBAdapter {

    private CDataCacheDBAdapter() {
    }

    public static CDataCacheContainer loadData(ResultSet rs, String cacheName,
            String[] columnNames, String[] primaryKeys, Map concreteMap)
            throws CDataGridException {
        if (rs == null) throw new CDataGridException("ResultSet cannot be null.");
        if (columnNames == null || columnNames.length < 1 || primaryKeys == null || primaryKeys.length < 1)
            throw new CDataGridException("Column names and primary key(s) cannot be null.");

        try {
            ResultSetMetaData meta = rs.getMetaData();
            Class<?>[] trsColumnTypes = new Class[columnNames.length];
            for (int i = 0; i < columnNames.length; i++) {
                int rsColIndex = rs.findColumn(columnNames[i]);
                trsColumnTypes[i] = getTransfClassType(meta.getColumnType(rsColIndex));
            }
            CRowMetaData metaDef = new CRowMetaData(columnNames, trsColumnTypes, primaryKeys);
            return loadData(rs, cacheName, metaDef, concreteMap);
        } catch (Exception ex) {
            throw new CDataGridException(ex.toString(), ex);
        }
    }

    public static CDataCacheContainer loadData(ResultSet rs, String cacheName,
            String[] primaryKeys, Map concreteMap) throws CDataGridException {
        if (rs == null) throw new CDataGridException("ResultSet cannot be null.");

        try {
            ResultSetMetaData meta = rs.getMetaData();
            int numColumns = meta.getColumnCount();
            String[] columnNames = new String[numColumns];
            Class<?>[] trsColumnTypes = new Class[numColumns];

            for (int i = 0; i < numColumns; i++) {
                columnNames[i] = meta.getColumnName(i + 1);
                trsColumnTypes[i] = getTransfClassType(meta.getColumnType(i + 1));
            }
            CRowMetaData metaDef = new CRowMetaData(columnNames, trsColumnTypes, primaryKeys);
            return loadData(rs, cacheName, metaDef, concreteMap);
        } catch (Exception ex) {
            throw new CDataGridException(ex.toString(), ex);
        }
    }

    public static CDataCacheContainer loadData(ResultSet rs, String cacheName,
            CRowMetaData metaDefinition, Map concreteMap) throws CDataGridException {
        if (rs == null) throw new CDataGridException("ResultSet cannot be null.");
        if (metaDefinition == null) throw new CDataGridException("Meta definition cannot be null.");

        try {
            String[] columnNames = metaDefinition.getColumnNames();
            Class<?>[] columnTypes = metaDefinition.getColumnTypes();
            LinkedList<CDataRow> list = new LinkedList<>();

            while (rs.next()) {
                CDataRow row = new CDataRow(columnNames.length);
                for (int i = 0; i < columnNames.length; i++) {
                    try {
                        int jdbcCol = rs.findColumn(columnNames[i]);
                        if (jdbcCol >= 0) {
                            Object data = readColumn(rs, jdbcCol, columnTypes[i]);
                            row.setValue(i, data);
                        }
                    } catch (SQLException ignored) {
                    }
                }
                list.add(row);
            }

            CDataCacheContainer container = new CDataCacheContainer(cacheName, metaDefinition, concreteMap);
            container.addData(list.toArray(new CDataRow[0]));
            return container;
        } catch (Exception ex) {
            throw new CDataGridException(ex.toString(), ex);
        }
    }

    private static Object readColumn(ResultSet rs, int jdbcCol, Class<?> type) throws SQLException {
        if (type == Boolean.class)             return CDataConverter.getBoolean(rs, jdbcCol);
        if (type == Byte.class)                return CDataConverter.getByte(rs, jdbcCol);
        if (type == Short.class)               return CDataConverter.getShort(rs, jdbcCol);
        if (type == Integer.class)             return CDataConverter.getInt(rs, jdbcCol);
        if (type == Float.class)               return CDataConverter.getFloat(rs, jdbcCol);
        if (type == Long.class)                return CDataConverter.getLong(rs, jdbcCol);
        if (type == Double.class)              return CDataConverter.getDouble(rs, jdbcCol);
        if (type == String.class)              return CDataConverter.getString(rs, jdbcCol);
        if (type == java.sql.Timestamp.class)  return CDataConverter.getDate(rs, jdbcCol);
        if (type == java.sql.Date.class)       return CDataConverter.getDate(rs, jdbcCol);
        if (type == java.util.Date.class)      return CDataConverter.getDate(rs, jdbcCol);
        return null;
    }

    public static Class<?> getTransfClassType(int columnType) {
        switch (columnType) {
            case Types.BIT:                          return Boolean.class;
            case Types.TINYINT:
            case Types.INTEGER:                      return Integer.class;
            case Types.SMALLINT:                     return Short.class;
            case Types.REAL:                         return Float.class;
            case Types.FLOAT:
            case Types.DOUBLE:                       return Double.class;
            case Types.CHAR:
            case Types.LONGVARCHAR:
            case Types.VARCHAR:                      return String.class;
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:                    return java.util.Date.class;
            case Types.NULL:                         return null;
            default:                                 return String.class;
        }
    }
}
