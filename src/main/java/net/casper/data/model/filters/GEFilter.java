package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Greater-than (or equal) filter for numeric columns.
 */
public class GEFilter extends CDataFilter {

    private final double lbound;
    private final boolean inclusive;

    public GEFilter(String columnName, double lbound, boolean inclusive) throws CDataGridException {
        super(columnName);
        this.lbound = lbound;
        this.inclusive = inclusive;
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();
        try {
            Number numVal = (Number) row.getValue(columnIndex);
            if (numVal == null) return false;
            double v = numVal.doubleValue();
            return inclusive ? v >= lbound : v > lbound;
        } catch (Exception ex) {
            throw new CDataGridException("Could not match row value: " + ex, ex);
        }
    }

    @Override
    public String toString() {
        return "GEFilter :: where " + columnName + " (" + columnIndex + ") "
                + (inclusive ? ">=" : ">") + " " + lbound;
    }
}
