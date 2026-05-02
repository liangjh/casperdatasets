package net.casper.data.model;

/**
 * This class contains a limited set of aggregation functionality,
 * which includes the following: (1) Sum, (2) WeightedSum, (3) Average, (4) WeightedAverage
 * The rowset and column name upon which to aggregate are the two expected
 * parameters for this aggregator
 *
 * @since v1.0
 * @author Jonathan Liang
 */
public final class CDataRowSetAggregator {

    /**
     * Disallow empty instantiation
     */
    private CDataRowSetAggregator() {
    }

    /**
     * Returns the max value from a given column
     *
     * @param rowset
     * @param columnName
     * @return
     * @throws CDataGridException
     */
    public static Double max(CDataRowSet rowset, String columnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData meta = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        int columnIndex  = meta.getColumnIndex(columnName);
        checkNumericColumn(meta, columnIndex);

        double max = 0.0;
        for (int i = 0; i < rows.length; i++) {
            try {
                Number number = (Number) ((CDataRow) rows[i]).getValue(columnIndex);
                if (number == null)
                    continue;
                double currVal = number.doubleValue();
                if (currVal > max)
                    max = currVal;
            } catch (Exception ex) {
            }
        }
        return Double.valueOf(max);
    }

    /**
     * Returns the min value from a given column
     *
     * @param rowset
     * @param columnName
     * @return
     * @throws CDataGridException
     */
    public static Double min(CDataRowSet rowset, String columnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData meta = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        int columnIndex  = meta.getColumnIndex(columnName);
        checkNumericColumn(meta, columnIndex);

        double min = 0.0;
        for (int i = 0; i < rows.length; i++) {
            try {
                Number number = (Number) ((CDataRow) rows[i]).getValue(columnIndex);
                if (number == null)
                    continue;
                double currVal = number.doubleValue();
                if (currVal < min)
                    min = currVal;
            } catch (Exception ex) {
            }
        }
        return Double.valueOf(min);
    }

    /**
     * Performs a very simple SUM on a column of data in the rowset
     *
     * @param rowset
     * @param columnName
     * @return result
     * @throws CDataGridException
     */
    public static Double sum(CDataRowSet rowset, String columnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData meta = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        int columnIndex  = meta.getColumnIndex(columnName);
        checkNumericColumn(meta, columnIndex);

        double sum = 0.0;
        for (int i = 0; i < rows.length; i++) {
            try {
                Number number = (Number) ((CDataRow) rows[i]).getValue(columnIndex);
                if (number == null)
                    continue;
                sum += number.doubleValue();
            } catch (Exception ex) {
            }
        }
        return Double.valueOf(sum);
    }

    /**
     * Returns a weighted sum on a particular column for the current rowset.
     *
     * @param rowset
     * @param valueColumnName
     * @param weightColumnName
     * @return result
     * @throws CDataGridException
     */
    public static Double weightedSum(CDataRowSet rowset, String valueColumnName, String weightColumnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData metaData = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        if (rows.length < 1)
            throw new CDataGridException("Cannot take weighted average on 0 cardinality list.  (Div by zero).");

        int valColumnIndex  = metaData.getColumnIndex(valueColumnName);
        checkNumericColumn(metaData, valColumnIndex);

        int weightColumnIndex  = metaData.getColumnIndex(weightColumnName);
        checkNumericColumn(metaData, weightColumnIndex);

        double sum = 0.0;
        for (int i = 0; i < rows.length; i++) {
            try {
                Number weight = (Number) ((CDataRow) rows[i]).getValue(valColumnIndex);
                Number value  = (Number) ((CDataRow) rows[i]).getValue(weightColumnIndex);
                if (weight == null || value == null)
                    continue;
                sum += (weight.doubleValue() * value.doubleValue());
            } catch (Exception ex) {
            }
        }
        return Double.valueOf(sum);
    }

    /**
     * Performs a very simple AVERAGE on a column of data in the rowset
     *
     * @param rowset
     * @param columnName
     * @return result
     * @throws CDataGridException
     */
    public static Double average(CDataRowSet rowset, String columnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData meta = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        if (rows.length < 1)
            throw new CDataGridException("Cannot take average on 0 cardinality list.  (Div by zero).");

        int columnIndex  = meta.getColumnIndex(columnName);
        checkNumericColumn(meta, columnIndex);

        double sum = 0.0;
        for (int i = 0; i < rows.length; i++) {
            try {
                Number number = (Number) ((CDataRow) rows[i]).getValue(columnIndex);
                if (number == null)
                    continue;
                sum += number.doubleValue();
            } catch (Exception ex) {
            }
        }

        double average = (sum / rows.length);
        return Double.valueOf(average);
    }

    /**
     * Performs a weighted average on a column of data in the rowset
     *
     * @param rowset
     * @param valueColumnName - the column to take average of
     * @param weightColumnName - the column containing the weights to perform the weighted average
     * @return result
     * @throws CDataGridException
     */
    public static Double weightedAverage(CDataRowSet rowset, String valueColumnName, String weightColumnName)
            throws CDataGridException {
        checkInput(rowset);

        CRowMetaData metaData = rowset.getMetaDefinition();
        CDataRow[] rows = rowset.getAllRows();

        if (rows.length < 1)
            throw new CDataGridException("Cannot take weighted average on 0 cardinality list.  (Div by zero).");

        int valColumnIndex  = metaData.getColumnIndex(valueColumnName);
        checkNumericColumn(metaData, valColumnIndex);

        int weightColumnIndex  = metaData.getColumnIndex(weightColumnName);
        checkNumericColumn(metaData, weightColumnIndex);

        double sum = 0.0;
        double wgtSum = 0.0;

        for (int i = 0; i < rows.length; i++) {
            try {
                Number val  = (Number) ((CDataRow)rows[i]).getValue(valColumnIndex);
                Number wght = (Number) ((CDataRow)rows[i]).getValue(weightColumnIndex);
                if (val == null || wght == null)
                    continue;
                sum += (val.doubleValue() * wght.doubleValue());
                wgtSum += wght.doubleValue();
            } catch (Exception ex) {
            }
        }

        double wghtAvg = 0.0;
        if (wgtSum != 0.0)
            wghtAvg = (sum / wgtSum);

        return Double.valueOf(wghtAvg);
    }

    /**
     * Checks if a column is a numeric.
     *
     * @param metaData
     * @param columnIndex
     * @throws CDataGridException
     */
    private static void checkNumericColumn(CRowMetaData metaData, int columnIndex)
            throws CDataGridException {
        Class columnType = metaData.getColumnTypeCls(columnIndex);
        if (columnType != Number.class && columnType.getSuperclass() != Number.class)
            throw new CDataGridException("Column must be numeric.  Cannot perform aggregation operation.");
    }

    /**
     * Check rowset input for invalidity, mostly null checks
     *
     * @param rowset
     * @throws CDataGridException
     */
    private static void checkInput(CDataRowSet rowset) throws CDataGridException {
        if (rowset == null)
            throw new CDataGridException("Rowset cannot be null.");
        if (rowset.getMetaDefinition() == null)
            throw new CDataGridException("Meta definition missing from rowset object.");
    }

}
