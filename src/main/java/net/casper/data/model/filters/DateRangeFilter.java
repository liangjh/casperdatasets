package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Date range filter. Either or both bounds may be null for open-ended ranges.
 */
public class DateRangeFilter extends CDataFilter {

    private final java.util.Date lbound;
    private final java.util.Date ubound;

    public DateRangeFilter(String columnName, java.util.Date lbound, java.util.Date ubound)
            throws CDataGridException {
        super(columnName);
        if (lbound != null && ubound != null && lbound.compareTo(ubound) >= 0)
            throw new CDataGridException("Lower bound cannot be >= upper bound.");
        this.lbound = lbound;
        this.ubound = ubound;
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        java.util.Date dateValue = (java.util.Date) row.getValue(columnIndex);
        if (dateValue == null) return false;

        if (lbound != null && ubound != null)
            return lbound.compareTo(dateValue) <= 0 && ubound.compareTo(dateValue) >= 0;
        if (lbound == null)
            return ubound.compareTo(dateValue) >= 0;
        return lbound.compareTo(dateValue) <= 0;
    }

    @Override
    public String toString() {
        return "DateRangeFilter :: where " + columnName + " (" + columnIndex + ") in ("
                + lbound + ".." + ubound + ")";
    }
}
