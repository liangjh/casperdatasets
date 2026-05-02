package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Less-than (or equal) filter for numeric columns.
 */
public class LEFilter extends CDataFilter {

    private final double ubound;
    private final boolean inclusive;

    public LEFilter(String columnName, double ubound, boolean inclusive) throws CDataGridException {
        super(columnName);
        this.ubound = ubound;
        this.inclusive = inclusive;
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();
        try {
            Number numVal = (Number) row.getValue(columnIndex);
            if (numVal == null) return false;
            double v = numVal.doubleValue();
            return inclusive ? v <= ubound : v < ubound;
        } catch (Exception ex) {
            throw new CDataGridException("Could not match row value: " + ex, ex);
        }
    }

    @Override
    public String toString() {
        return "LEFilter :: where " + columnName + " (" + columnIndex + ") "
                + (inclusive ? "<=" : "<") + " " + ubound;
    }
}
