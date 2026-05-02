package net.casper.data.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.casper.data.model.filters.CDataFilterClause;
import net.casper.data.model.filters.EqualsFilter;

/**
 * Thread-safe in-memory container for 2-D tabular data, indexed by primary key.
 * Supports add, remove, filter, merge, sort, and export operations.
 * Optimized for high-volume reads with low-volume writes.
 */
public class CDataCacheContainer implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map dataRowMap = null;
    private String cacheName = null;
    private CRowMetaData metaData = null;
    private Map indexMap = new HashMap();
    private transient Object lock = new Object();
    private int identityPK = 0;

    private CDataCacheContainer() {
    }

    public CDataCacheContainer(CBuilder builder) throws CDataGridException {
        try {
            builder.open();
            init(builder.getName(), new CRowMetaData(
                    builder.getColumnNames(), builder.getColumnTypes(),
                    builder.getPrimaryKeyColumns()), builder.getConcreteMap());

            List<CDataRow> cRows = new LinkedList<>();
            Object[] nextRow;
            while ((nextRow = builder.readRow()) != null)
                cRows.add(new CDataRow(nextRow));

            addData(cRows.toArray(new CDataRow[0]));
            builder.close();
        } catch (IOException e) {
            builder.close();
            throw new CDataGridException("Error building container", e);
        }
    }

    public CDataCacheContainer(String cacheName, CRowMetaData metaData)
            throws CDataGridException {
        this(cacheName, metaData, new HashMap());
    }

    public CDataCacheContainer(String cacheName, CRowMetaData metaData, Map dataRowMap)
            throws CDataGridException {
        init(cacheName, metaData, dataRowMap);
    }

    public CDataCacheContainer(String cacheName, String columnNames,
            Class[] columnTypes, String primaryKeys)
            throws CDataGridException {
        if (columnNames == null)
            throw new CDataGridException("Cannot create container: column names are null");
        CRowMetaData metaDef = new CRowMetaData(columnNames.split(","), columnTypes,
                (primaryKeys == null) ? null : primaryKeys.split(","));
        init(cacheName, metaDef, new HashMap());
    }

    private void init(String cacheName, CRowMetaData metaData, Map dataRowMap)
            throws CDataGridException {
        if (metaData == null) throw new CDataGridException("Meta data object cannot be null.");
        if (dataRowMap == null) throw new CDataGridException("Data map object cannot be null.");
        this.cacheName = cacheName;
        this.metaData = metaData;
        this.dataRowMap = dataRowMap;
    }

    public static CDataCacheContainer newInsertionOrdered(String cacheName,
            String columnNames, Class[] columnTypes) throws CDataGridException {
        CRowMetaData metaDef = new CRowMetaData(columnNames.split(","), columnTypes, null);
        return new CDataCacheContainer(cacheName, metaDef, new LinkedHashMap());
    }

    public int size() { return dataRowMap == null ? 0 : dataRowMap.size(); }
    public int getNumberRows() { return size(); }
    public String getCacheName() { return cacheName; }
    public CRowMetaData getMetaDefinition() { return metaData; }

    public Object export(CExporter exporter) throws CDataGridException {
        try {
            exporter.setName(getCacheName());
            exporter.setColumnNames(metaData.getColumnNames());
            exporter.setColumnTypes(metaData.getColumnTypes());
            exporter.setPrimaryKeyColumns(metaData.getPrimaryKeyColumns());
            exporter.open();

            CDataRowSet rowset = getAll();
            while (rowset.next())
                exporter.writeRow(rowset.getCurrentRow().getRawData());
        } catch (IOException e) {
            exporter.close();
            throw new CDataGridException("Error exporting container: " + e.getMessage(), e);
        }
        return exporter.close();
    }

    // --- Query methods ---

    public CDataRowSet get(String columnName, Object[] values) throws CDataGridException {
        return get(columnName, values, null, true);
    }

    public CDataRowSet get(String columnName, Object[] values, String[] sortColumnNames, boolean ascending)
            throws CDataGridException {
        EqualsFilter filter = new EqualsFilter(columnName, values);
        CDataFilterClause clause = new CDataFilterClause();
        clause.addFilter(filter);
        return get(clause, sortColumnNames, ascending);
    }

    public CDataRowSet get(CDataFilterClause filterClause) throws CDataGridException {
        return get(filterClause, null, true);
    }

    public CDataRowSet get(CDataFilterClause filterClause, String[] sortColumnNames, boolean ascending)
            throws CDataGridException {
        if (filterClause.size() < 1) return getAll(sortColumnNames, ascending);

        CDataRow[] rows = new CDataRow[dataRowMap.size()];
        dataRowMap.values().toArray(rows);

        filterClause.setMetaDefinition(metaData);
        filterClause.setCacheContainerCallbackOptimization(this);
        rows = filterClause.match(rows);

        CDataRowSet rowset = new CDataRowSet(metaData);
        rowset.addData(rows);
        if (sortColumnNames != null && sortColumnNames.length > 0)
            rowset.sortByColumn(sortColumnNames, ascending);
        return rowset;
    }

    public CDataRowSet getAll() throws CDataGridException {
        return getAll(metaData.getPrimaryKeyColumns(), true);
    }

    public CDataRowSet getAll(String[] sortColumnNames, boolean ascending) throws CDataGridException {
        CDataRow[] rows = new CDataRow[dataRowMap.size()];
        dataRowMap.values().toArray(rows);

        CDataRowSet rowset = new CDataRowSet(metaData);
        rowset.addData(rows);
        if (sortColumnNames != null && sortColumnNames.length > 0)
            rowset.sortByColumn(sortColumnNames, ascending);
        return rowset;
    }

    public CDataRow[] getAllRows() {
        CDataRow[] rows = new CDataRow[dataRowMap.size()];
        dataRowMap.values().toArray(rows);
        return rows;
    }

    // --- Data modification ---

    public int addData(CDataCacheContainer dataContainer) throws CDataGridException {
        return addData(dataContainer, true);
    }

    public int addData(CDataCacheContainer dataContainer, boolean updateIndices) throws CDataGridException {
        if (dataContainer.getMetaDefinition() == null || !metaData.equals(dataContainer.getMetaDefinition()))
            throw new CDataGridException("Meta definitions of containers are not equivalent.");
        return addData(dataContainer.getAllRows(), updateIndices);
    }

    public int addData(CDataRow[] dataRows) throws CDataGridException {
        return addData(dataRows, true);
    }

    public void addSingleRow(Object[] oRow) throws CDataGridException {
        CDataRow cRow = new CDataRow();
        cRow.setRawData(oRow);
        addData(new CDataRow[] { cRow });
    }

    public int addData(CDataRow[] dataRows, boolean updateIndices) throws CDataGridException {
        if (dataRows == null || dataRows.length < 1) return 0;

        for (CDataRow row : dataRows) {
            if (row == null || row.getNumberColumns() != metaData.getNumberColumns())
                throw new CDataGridException("Data row is corrupt: columns do not match meta definition.");
        }

        int additionCount = 0;
        checkLock();
        synchronized (lock) {
            for (CDataRow row : dataRows) {
                Object primaryKey;
                if (metaData.getPrimaryKeyColumns() == null) {
                    primaryKey = ++identityPK;
                } else {
                    primaryKey = metaData.createPrimaryKey(row);
                }
                dataRowMap.put(primaryKey, row);
                additionCount++;
            }
            if (updateIndices) updateIndices();
        }
        return additionCount;
    }

    public int removeData(String columnName, Object[] values, boolean updateIndices) throws CDataGridException {
        if (values == null || values.length < 1) return 0;
        EqualsFilter filter = new EqualsFilter(columnName, values);
        CDataFilterClause clause = new CDataFilterClause();
        clause.addFilter(filter);
        return removeData(clause, updateIndices);
    }

    public int removeData(CDataFilterClause filterClause, boolean updateIndices) throws CDataGridException {
        if (filterClause == null || filterClause.size() < 1) return 0;

        CDataRowSet candidates = get(filterClause, null, true);
        CDataRow[] rows = candidates.getAllRows();
        if (rows.length == 0) return 0;

        checkLock();
        synchronized (lock) {
            for (CDataRow row : rows)
                dataRowMap.remove(metaData.createPrimaryKey(row));
            if (updateIndices) updateIndices();
        }
        return rows.length;
    }

    public int removeAll() throws CDataGridException {
        if (dataRowMap == null || dataRowMap.isEmpty()) return 0;
        checkLock();
        synchronized (lock) {
            int count = dataRowMap.size();
            dataRowMap.clear();
            updateIndices();
            return count;
        }
    }

    // --- Merge ---

    public int merge(CDataCacheContainer mergeFrom, String[] joinColumns) throws CDataGridException {
        if (mergeFrom == null) throw new CDataGridException("Data container to merge cannot be null.");
        return merge(mergeFrom.getAll(), joinColumns);
    }

    public int merge(CDataRowSet mergeFrom, String[] joinColumns) throws CDataGridException {
        if (mergeFrom == null) throw new CDataGridException("Data rowset to merge cannot be null.");
        if (joinColumns == null || joinColumns.length < 1) throw new CDataGridException("Must join on at least one column.");
        if (mergeFrom.getNumberRows() < 1) return 0;

        int rowsUpdated = 0;
        CRowMetaData fromMetaDef = mergeFrom.getMetaDefinition();
        String[] fromColumnNames = fromMetaDef.getColumnNames();
        String[] destPrimaryKeys = metaData.getPrimaryKeyColumns();

        HashSet<String> nonOverwrites = new HashSet<>();
        for (String pk : destPrimaryKeys) nonOverwrites.add(pk);
        for (String jc : joinColumns) nonOverwrites.add(jc);

        checkLock();
        synchronized (lock) {
            mergeFrom.reset();
            while (mergeFrom.next()) {
                CDataFilterClause clause = new CDataFilterClause();
                for (String jc : joinColumns) {
                    Object val = mergeFrom.getObject(jc);
                    clause.addFilter(new EqualsFilter(jc, new Object[] { val }));
                }

                CDataRowSet results = get(clause, null, false);
                if (results != null && results.getNumberRows() > 0) {
                    while (results.next()) {
                        for (String col : fromColumnNames) {
                            if (nonOverwrites.contains(col)) continue;
                            if (metaData.containsColumn(col) &&
                                    metaData.getColumnType(col).equals(fromMetaDef.getColumnType(col))) {
                                results.setValue(col, mergeFrom.getObject(col));
                            }
                        }
                    }
                }
                rowsUpdated += (results == null ? 0 : results.getNumberRows());
            }
        }
        return rowsUpdated;
    }

    // --- Column operations ---

    public void clearColumn(String columnName) throws CDataGridException {
        setColumnValue(columnName, null);
    }

    public void setColumnValue(String columnName, Object columnValue) throws CDataGridException {
        if (columnName == null || columnName.trim().isEmpty())
            throw new CDataGridException("Column name cannot be null or empty.");
        if (!metaData.containsColumn(columnName))
            throw new CDataGridException("Column not found: " + columnName);

        int idx = metaData.getColumnIndex(columnName);
        synchronized (lock) {
            for (Object row : dataRowMap.values())
                ((CDataRow) row).setValue(idx, columnValue);
        }
    }

    // --- Index management ---

    public void addUniqueIndex(String columnName) throws CDataGridException {
        if (indexMap.containsKey(columnName)) return;
        if (!metaData.containsColumn(columnName))
            throw new CDataGridException("Invalid column for index: " + columnName);
    }

    public void addNonUniqueIndex(String columnName) throws CDataGridException {
        if (indexMap.containsKey(columnName)) return;
        if (!metaData.containsColumn(columnName))
            throw new CDataGridException("Invalid column for index: " + columnName);

        CDataCacheNonUniqueIndex index = new CDataCacheNonUniqueIndex(columnName, metaData.getColumnIndex(columnName));
        index.index(getAllRows());
        indexMap.put(columnName, index);
    }

    public CDataRow[] getPrimaryKeyMatches(Object[] values) {
        List<CDataRow> list = new LinkedList<>();
        for (Object value : values) {
            if (value == null) continue;
            CDataRow row = (CDataRow) dataRowMap.get(value);
            if (row != null) list.add(row);
        }
        return list.toArray(new CDataRow[0]);
    }

    public CDataCacheIndex getCacheIndexByColumnName(String columnName) {
        return (indexMap == null) ? null : (CDataCacheIndex) indexMap.get(columnName);
    }

    public String[] getIndexColumnNames() {
        if (indexMap == null) return new String[0];
        return (String[]) indexMap.keySet().toArray(new String[0]);
    }

    public void updateIndices() throws CDataGridException {
        if (indexMap == null || indexMap.isEmpty()) return;
        CDataRow[] allRows = getAllRows();
        for (Object val : indexMap.values())
            ((CDataCacheIndex) val).update(allRows);
    }

    private synchronized void checkLock() {
        if (lock == null) lock = new Object();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CACHE CONTENTS: ").append(cacheName)
          .append(" (Cardinality: ").append(dataRowMap.size()).append(")\n");
        sb.append(metaData.toString());
        for (Object row : dataRowMap.values())
            sb.append(row.toString()).append("\n");
        return sb.toString();
    }
}
