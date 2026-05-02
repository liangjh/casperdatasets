package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Numeric range filter. Matches rows where column value falls within [lbound, ubound].
 */
public class RangeFilter extends CDataFilter {

    private final double lbound;
    private final double ubound;
    private final boolean inclusive;

    /** Inclusive range by default. */
    public RangeFilter(String columnName, double lbound, double ubound) throws CDataGridException {
        this(columnName, lbound, ubound, true);
    }

    public RangeFilter(String columnName, double lbound, double ubound, boolean inclusive)
            throws CDataGridException {
        super(columnName);
        if (lbound >= ubound)
            throw new CDataGridException("Lower bound must be less than upper bound.");
        this.lbound = lbound;
        this.ubound = ubound;
        this.inclusive = inclusive;
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();
        try {
            Number numVal = (Number) row.getValue(columnIndex);
            if (numVal == null) return false;
            double v = numVal.doubleValue();
            return inclusive ? (v >= lbound && v <= ubound) : (v > lbound && v < ubound);
        } catch (Exception ex) {
            throw new CDataGridException("Could not match row value: " + ex, ex);
        }
    }

    @Override
    public String toString() {
        return "RangeFilter :: where " + columnName + " (" + columnIndex + ") in ("
                + lbound + ".." + ubound + "), " + (inclusive ? "inclusive" : "exclusive");
    }
}
