package net.casper.data.model.filters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.casper.data.model.CDataCacheContainer;
import net.casper.data.model.CDataCacheIndex;
import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;
import net.casper.data.model.CRowMetaData;

/**
 * Combines multiple filters with AND logic. Filters are evaluated in order.
 * OR semantics are supported within individual filters via multi-value constructors.
 */
public class CDataFilterClause {

    private final ArrayList<CDataFilter> filterList = new ArrayList<>();
    private CRowMetaData metaDef = null;
    private CDataCacheContainer container = null;
    private EqualsFilter primaryKeyFilter = null;
    private final Map<String, EqualsFilter> indexFilters = new HashMap<>();

    public CDataFilterClause() {
    }

    public int size() {
        int n = filterList.size();
        if (primaryKeyFilter != null) n++;
        return n;
    }

    public void addFilter(CDataFilter filter) throws CDataGridException {
        if (filter == null) throw new CDataGridException("Filter cannot be null.");
        filterList.add(filter);
    }

    public void setMetaDefinition(CRowMetaData metaDef) {
        this.metaDef = metaDef;
    }

    public CDataRow[] match(CDataRow[] crows) throws CDataGridException {
        CDataRow[] rows = crows;

        if (primaryKeyFilter == null && filterList.isEmpty()) return rows;

        // Use PK or index optimization if available
        if (primaryKeyFilter != null) {
            rows = container.getPrimaryKeyMatches(primaryKeyFilter.getMatchValues());
        } else if (!indexFilters.isEmpty()) {
            EqualsFilter indexedFilter = indexFilters.values().iterator().next();
            CDataCacheIndex index = container.getCacheIndexByColumnName(indexedFilter.getColumnName());
            rows = index.get(indexedFilter.getMatchValues());
        }

        // Apply remaining filters
        List<CDataRow> list = new LinkedList<>();
        for (CDataRow row : rows) {
            boolean matches = true;
            for (CDataFilter filter : filterList) {
                filter.setMetaDefinition(metaDef);
                if (!filter.doesMatch(row)) {
                    matches = false;
                    break;
                }
            }
            if (matches) list.add(row);
        }
        return list.toArray(new CDataRow[0]);
    }

    /**
     * Optimizes filter order: PK equality filters are extracted and run first,
     * followed by indexed equality filters.
     */
    public void setCacheContainerCallbackOptimization(CDataCacheContainer container)
            throws CDataGridException {
        this.container = container;
        optimizeOrder();
    }

    private void optimizeOrder() throws CDataGridException {
        if (primaryKeyFilter != null) return;

        String[] pkColumns = metaDef.getPrimaryKeyColumns();
        boolean optimizePk = (pkColumns.length == 1);

        if (!filterList.isEmpty()) {
            // Extract PK filter if applicable
            if (optimizePk) {
                for (int i = 0; i < filterList.size(); i++) {
                    CDataFilter f = filterList.get(i);
                    if (f instanceof EqualsFilter && f.getColumnName().equals(pkColumns[0])) {
                        primaryKeyFilter = (EqualsFilter) filterList.remove(i);
                        break;
                    }
                }
            }

            // Identify indexed filters
            for (CDataFilter filter : filterList) {
                if (filter instanceof EqualsFilter) {
                    CDataCacheIndex idx = container.getCacheIndexByColumnName(filter.getColumnName());
                    if (idx != null) indexFilters.put(filter.getColumnName(), (EqualsFilter) filter);
                }
            }
        }
    }

    public CDataFilter getFilter(int idx) throws CDataGridException {
        if (idx >= filterList.size())
            throw new CDataGridException("Filter index out of bounds.");
        return filterList.get(idx);
    }

    @SuppressWarnings("unchecked")
    public CDataFilter[] getAllFilters() {
        ArrayList<CDataFilter> list = (ArrayList<CDataFilter>) filterList.clone();
        if (primaryKeyFilter != null) list.add(0, primaryKeyFilter);
        return list.toArray(new CDataFilter[0]);
    }

    public CDataFilter getPrimaryKeyFilter() { return primaryKeyFilter; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Filter Clause: {");
        if (primaryKeyFilter != null)
            sb.append("PK Filter -> ").append(primaryKeyFilter).append(", ");
        for (int i = 0; i < filterList.size(); i++) {
            sb.append(filterList.get(i));
            if (i < filterList.size() - 1) sb.append(", ");
        }
        return sb.append("}").toString();
    }
}
