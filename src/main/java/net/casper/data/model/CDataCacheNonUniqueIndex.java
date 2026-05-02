package net.casper.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Non-unique index mapping column values to lists of matching rows.
 */
public class CDataCacheNonUniqueIndex extends CDataCacheIndex implements Serializable {

    private HashMap<Object, LinkedList<CDataRow>> indexMap = new HashMap<>();
    private int numElements = 0;

    public CDataCacheNonUniqueIndex(String columnName, int columnIndex) throws CDataGridException {
        super(columnName, columnIndex);
    }

    public boolean contains(Object key) {
        return indexMap != null && indexMap.containsKey(key);
    }

    public CDataRow[] get(Object key) throws CDataGridException {
        if (key == null) return new CDataRow[0];
        LinkedList<CDataRow> list = indexMap.get(key);
        return (list == null) ? new CDataRow[0] : list.toArray(new CDataRow[0]);
    }

    public CDataRow[] get(Object[] keys) throws CDataGridException {
        if (keys == null || keys.length < 1) return new CDataRow[0];
        LinkedList<CDataRow> allMatches = new LinkedList<>();
        for (Object key : keys) {
            LinkedList<CDataRow> list = indexMap.get(key);
            if (list != null) allMatches.addAll(list);
        }
        return allMatches.toArray(new CDataRow[0]);
    }

    public void index(CDataRow[] rows) throws CDataGridException {
        if (indexMap == null) indexMap = new HashMap<>();
        if (rows == null || rows.length < 1) return;

        for (CDataRow row : rows) {
            Object keyVal = row.getValue(columnIndex);
            indexMap.computeIfAbsent(keyVal, k -> new LinkedList<>()).add(row);
        }
        numElements += rows.length;
    }

    public void update(CDataRow[] rows) throws CDataGridException {
        indexMap.clear();
        numElements = 0;
        index(rows);
    }
}
