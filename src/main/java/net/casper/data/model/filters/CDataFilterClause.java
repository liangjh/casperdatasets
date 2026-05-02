package net.casper.data.model.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.casper.data.model.CDataCacheContainer;
import net.casper.data.model.CDataCacheIndex;
import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;
import net.casper.data.model.CRowMetaData;

/**
 * Stores a list of filters with which to match a given cache.
 * Any number of filters can be added to the filter clause chain.
 * Filters will be evaluated in the order in which they are added to the clause.
 * <br/><br/>
 * Note that filters will be evaluated via an "AND" relationship, which means that
 * all filters must be matched in order for a record to qualify as a match on the clause.
 * "OR"-based filters are accomodated by individual filters that can take multi-values as
 * arguments to concrete constructors.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CDataFilterClause {

    /** List of filters to match upon */
    private ArrayList filterList = new ArrayList();

    /** Meta-definition */
    private CRowMetaData metaDef = null;

    /**
     * Cache container callback (optimization) -- this is used to allow the filter to
     * take advantage of exact matches on datasets with single PK fields (in the event hat this is necessary).
     */
    private CDataCacheContainer container = null;

    /** Primary key filter, in the event that this applies */
    private EqualsFilter primaryKeyFilter = null;

    /** Map of filters that match to any indices created (all equals-based filters) */
    private Map indexFilters = new HashMap();

    /**
     * Initialize w/ meta data
     */
    public CDataFilterClause() {
    }

    /**
     * Return size of filter list
     * @return size of filter list
     */
    public int size() {
        int numFilters = (filterList == null) ? 0 : filterList.size();
        if (primaryKeyFilter != null) numFilters++;
        return numFilters;
    }

    /**
     * Adds a filter to the filter chain.
     * @param filter
     */
    public void addFilter(CDataFilter filter) throws CDataGridException {
        if (filter == null)
            throw new CDataGridException("Filter to add to clause cannot be null.");
        if (!(filter instanceof CDataFilter))
            throw new CDataGridException("Invalid filter.  All filters must implement the interface: " + CDataFilter.class.getName());
        this.filterList.add(filter);
    }

    public void setMetaDefinition(CRowMetaData metaDef) {
        this.metaDef = metaDef;
    }

    /**
     * For a given record, returns "true", if it satifies all of the filters in the filter clause
     *
     * @param crows - to match on
     * @return matching rows
     * @throws CDataGridException
     */
    public CDataRow[] match(CDataRow[] crows) throws CDataGridException {
        CDataRow[] rows = crows;

        if (primaryKeyFilter == null && filterList.size() < 1)
            return rows;

        if (primaryKeyFilter != null) {
            rows = container.getPrimaryKeyMatches(primaryKeyFilter.getMatchValues());
        } else {
            if (indexFilters != null && indexFilters.size() > 0) {
                EqualsFilter indexedFilter = null;
                for (Iterator iter = indexFilters.keySet().iterator(); iter.hasNext(); ) {
                    String colName = (String) iter.next();
                    indexedFilter = (EqualsFilter) indexFilters.get(colName);
                    if (indexedFilter != null)
                        break;
                }

                String columnName = indexedFilter.getColumnName();
                CDataCacheIndex index = (CDataCacheIndex) container.getCacheIndexByColumnName(columnName);
                rows = index.get(indexedFilter.getMatchValues());
            }
        }

        List list = new LinkedList();
        for (int i = 0; i < rows.length; i++) {
            boolean matches = true;
            CDataRow row = rows[i];

            for (int j = 0; j < filterList.size(); j++) {
                CDataFilter filter = (CDataFilter) filterList.get(j);
                filter.setMetaDefinition(metaDef);
                if (!filter.doesMatch(row)) {
                    matches = false;
                    break;
                }
            }

            if (matches)
                list.add(row);
        }

        CDataRow[] finalMatches = new CDataRow[list.size()];
        list.toArray(finalMatches);
        return finalMatches;
    }

    /**
     * Optimizes filter list to allow PK exact matches to be run -first-.  This is a
     * a performance enhancement / optimization in the system.  When this is called,
     * the list of filters is rearranged to allow the PK to come first.
     *
     * @param container
     * @throws CDataGridException
     */
    public void setCacheContainerCallbackOptimization(CDataCacheContainer container)
            throws CDataGridException {
        this.container = container;
        optimizeOrder();
    }

    /**
     * Based on the filters provided, re-arranges the filters in a manner such that
     * any primary-key, equality-based match will be removed from the list.
     * Will also optimize any indices that have already been created.
     *
     * The reason this is done is to allow the filters to perform a primary key -based
     * search on the data first, and then perform searches on additional fields.
     *
     * -- Caveats:
     * -- Note that this optimization is limited in the sense that once a PK,equality filter is
     *    found, the loop breaks and that element is inserted to the front.  So two equals filters
     *    on the primary key will not percolate both to the front.
     * -- The other limitation at this point in time is that composite primary key fields
     *    will not be optimized and will always result in a full table scan.
     *
     * @throws CDataGridException
     */
    private void optimizeOrder() throws CDataGridException {
        if (primaryKeyFilter != null)
            return;

        boolean optimizePk = true;
        String[] pkColumns = metaDef.getPrimaryKeyColumns();
        if (pkColumns.length > 1)
            optimizePk = false;

        if (filterList != null && filterList.size() > 0) {
            CDataFilter pkFilter = null;
            int idx = 0;

            if (optimizePk) {
                while (idx < filterList.size()) {
                    if (filterList.get(idx) instanceof EqualsFilter) {
                        EqualsFilter filter = (EqualsFilter) filterList.get(idx);
                        if (filter.getColumnName().equals(pkColumns[0])) {
                            pkFilter = filter;
                            break;
                        }
                    }
                    idx++;
                }

                if (pkFilter != null) {
                    primaryKeyFilter = (EqualsFilter) filterList.get(idx);
                    filterList.remove(idx);
                }
            }

            for (Iterator iter = filterList.iterator(); iter.hasNext(); ) {
                CDataFilter filter = (CDataFilter) iter.next();
                if (filter instanceof EqualsFilter) {
                    EqualsFilter eqFilter = (EqualsFilter) filter;
                    CDataCacheIndex cacheIndex = container.getCacheIndexByColumnName(filter.getColumnName());
                    if (cacheIndex != null) {
                        indexFilters.put(eqFilter.getColumnName(), eqFilter);
                    }
                }
            }
        }
    }

    /**
     * Returns appropriate filter value
     * @param idx
     * @return CDataFilter
     * @throws CDataGridException
     */
    public CDataFilter getFilter(int idx) throws CDataGridException {
        if (idx >= filterList.size())
            throw new CDataGridException("The filter requested doesn't exist exist in the indices provided.");
        return (CDataFilter) filterList.get(idx);
    }

    /**
     * Get list of all filters, including PK filter, if exists
     * @return
     */
    public CDataFilter[] getAllFilters() {
        ArrayList list = (ArrayList) filterList.clone();
        if (primaryKeyFilter != null) {
            list.add(0, primaryKeyFilter);
        }
        CDataFilter filters[] = new CDataFilter[list.size()];
        list.toArray(filters);
        return filters;
    }

    /**
     * Return primary key equality filter
     * @return
     */
    public CDataFilter getPrimaryKeyFilter() {
        return primaryKeyFilter;
    }

    /**
     * Returns string representation of this object
     * @return string
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("Filter Clause: {");

        if (primaryKeyFilter != null) {
            sbuf.append("PK Filter -> ");
            sbuf.append(primaryKeyFilter.toString());
            sbuf.append(", ");
        }

        for (int i = 0; i < filterList.size(); i++) {
            CDataFilter filter = (CDataFilter) filterList.get(i);
            sbuf.append(filter.toString());
            if (i < filterList.size() - 1)
                sbuf.append(", ");
        }
        sbuf.append("}");
        return sbuf.toString();
    }

}
