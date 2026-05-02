package net.casper.data.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * A non-unique index, will construct a map of key values to
 * a list of row object arrays. <br/>
 * You should not need to utilize this object directly, unless you wish to perform more advanced
 * operations.  See CDataCacheContainer.addNonUniqueIndex(columnName), which will create the
 * index for you.
 *
 * @since 1.0
 * @author Jonathan H. Liang
 */
public class CDataCacheNonUniqueIndex
        extends CDataCacheIndex implements Serializable {

    /** Map of arraylists (since this is nonunique */
    private HashMap indexMap = new HashMap();

    private int numElements = 0;

    /**
     * Builds a unique index from a
     *
     * @param columnName
     * @param columnIndex
     * @throws CDataGridException
     */
    public CDataCacheNonUniqueIndex(String columnName, int columnIndex)
            throws CDataGridException {
        super(columnName, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean contains(Object key) throws CDataGridException {
        if (indexMap == null)
            return false;
        return indexMap.containsKey(key);
    }

    /**
     * {@inheritDoc}
     */
    public CDataRow[] get(Object key) throws CDataGridException {
        if (key == null)
            return new CDataRow[0];

        LinkedList list = (LinkedList) indexMap.get(key);
        if (list == null)
            return new CDataRow[0];

        CDataRow[] rows = new CDataRow[list.size()];
        list.toArray(rows);
        return rows;
    }

    /**
     * {@inheritDoc}
     */
    public CDataRow[] get(Object[] keys) throws CDataGridException {
        if (keys == null || keys.length < 1)
            return new CDataRow[0];

        LinkedList allMatches = new LinkedList();
        for (int i = 0; i < keys.length; i++) {
            LinkedList list = (LinkedList) indexMap.get(keys[i]);
            if (list != null)
                allMatches.addAll(list);
        }

        if (allMatches.size() < 1)
            return new CDataRow[0];

        CDataRow[] allRows = new CDataRow[allMatches.size()];
        allMatches.toArray(allRows);
        return allRows;
    }

    /**
     * This is an iterative build of the index
     * (adds rows set in parameter to index)
     * {@inheritDoc}
     */
    public void index(CDataRow[] rows) throws CDataGridException {
        if (indexMap == null)
            indexMap = new HashMap();
        if (rows == null || rows.length < 1)
            return;

        for (int i = 0; i < rows.length; i++) {
            CDataRow currentRow = rows[i];
            Object keyVal = currentRow.getValue(columnIndex);
            LinkedList list = (LinkedList) indexMap.get(keyVal);

            if (list == null) {
                list = new LinkedList();
                indexMap.put(keyVal, list);
            }
            list.add(currentRow);
        }
        numElements += rows.length;
    }

    /**
     * This is a full re-build of the index
     * (clears the index
     * {@inheritDoc}
     */
    public void update(CDataRow[] rows) throws CDataGridException {
        indexMap.clear();
        numElements = 0;
        index(rows);
    }

}
