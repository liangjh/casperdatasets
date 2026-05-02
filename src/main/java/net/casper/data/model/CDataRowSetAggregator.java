package net.casper.data.model;

/**
 * Static aggregation methods: sum, average, min, max, weighted sum/average.
 * Column must be numeric.
 */
public final class CDataRowSetAggregator {

    private CDataRowSetAggregator() {
    }

    public static Double max(CDataRowSet rowset, String columnName) throws CDataGridException {
        checkInput(rowset);
        int idx = resolveNumericColumn(rowset, columnName);
        CDataRow[] rows = rowset.getAllRows();

        double max = Double.NEGATIVE_INFINITY;
        for (CDataRow row : rows) {
            Number n = (Number) row.getValue(idx);
            if (n != null && n.doubleValue() > max) max = n.doubleValue();
        }
        return max;
    }

    public static Double min(CDataRowSet rowset, String columnName) throws CDataGridException {
        checkInput(rowset);
        int idx = resolveNumericColumn(rowset, columnName);
        CDataRow[] rows = rowset.getAllRows();

        double min = Double.POSITIVE_INFINITY;
        for (CDataRow row : rows) {
            Number n = (Number) row.getValue(idx);
            if (n != null && n.doubleValue() < min) min = n.doubleValue();
        }
        return min;
    }

    public static Double sum(CDataRowSet rowset, String columnName) throws CDataGridException {
        checkInput(rowset);
        int idx = resolveNumericColumn(rowset, columnName);

        double sum = 0.0;
        for (CDataRow row : rowset.getAllRows()) {
            Number n = (Number) row.getValue(idx);
            if (n != null) sum += n.doubleValue();
        }
        return sum;
    }

    public static Double average(CDataRowSet rowset, String columnName) throws CDataGridException {
        checkInput(rowset);
        CDataRow[] rows = rowset.getAllRows();
        if (rows.length < 1) throw new CDataGridException("Cannot average on empty rowset (div by zero).");

        int idx = resolveNumericColumn(rowset, columnName);
        double sum = 0.0;
        for (CDataRow row : rows) {
            Number n = (Number) row.getValue(idx);
            if (n != null) sum += n.doubleValue();
        }
        return sum / rows.length;
    }

    public static Double weightedSum(CDataRowSet rowset, String valueColumnName, String weightColumnName)
            throws CDataGridException {
        checkInput(rowset);
        CDataRow[] rows = rowset.getAllRows();
        if (rows.length < 1) throw new CDataGridException("Cannot take weighted sum on empty rowset.");

        int valIdx = resolveNumericColumn(rowset, valueColumnName);
        int wgtIdx = resolveNumericColumn(rowset, weightColumnName);

        double sum = 0.0;
        for (CDataRow row : rows) {
            Number val = (Number) row.getValue(valIdx);
            Number wgt = (Number) row.getValue(wgtIdx);
            if (val != null && wgt != null) sum += val.doubleValue() * wgt.doubleValue();
        }
        return sum;
    }

    public static Double weightedAverage(CDataRowSet rowset, String valueColumnName, String weightColumnName)
            throws CDataGridException {
        checkInput(rowset);
        CDataRow[] rows = rowset.getAllRows();
        if (rows.length < 1) throw new CDataGridException("Cannot take weighted average on empty rowset.");

        int valIdx = resolveNumericColumn(rowset, valueColumnName);
        int wgtIdx = resolveNumericColumn(rowset, weightColumnName);

        double sum = 0.0, wgtSum = 0.0;
        for (CDataRow row : rows) {
            Number val = (Number) row.getValue(valIdx);
            Number wgt = (Number) row.getValue(wgtIdx);
            if (val != null && wgt != null) {
                sum += val.doubleValue() * wgt.doubleValue();
                wgtSum += wgt.doubleValue();
            }
        }
        return (wgtSum != 0.0) ? sum / wgtSum : 0.0;
    }

    private static int resolveNumericColumn(CDataRowSet rowset, String columnName) throws CDataGridException {
        CRowMetaData meta = rowset.getMetaDefinition();
        int idx = meta.getColumnIndex(columnName);
        Class<?> type = meta.getColumnTypeCls(idx);
        if (type != Number.class && type.getSuperclass() != Number.class)
            throw new CDataGridException("Column must be numeric for aggregation.");
        return idx;
    }

    private static void checkInput(CDataRowSet rowset) throws CDataGridException {
        if (rowset == null) throw new CDataGridException("Rowset cannot be null.");
        if (rowset.getMetaDefinition() == null) throw new CDataGridException("Meta definition missing from rowset.");
    }
}
