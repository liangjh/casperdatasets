package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * @since 1.0
 * @author Jonathan Liang
 */
public class DateRangeFilter extends CDataFilter {

    private java.util.Date lbound = null;
    private java.util.Date ubound = null;

    /**
     * Creates a range filter.
     * Note that all comparisons will be done via the java.util.Date datatype.
     *
     * @param columnName
     * @param lbound
     * @param ubound
     * @throws CDataGridException
     */
    public DateRangeFilter(String columnName, java.util.Date lbound, java.util.Date ubound)
            throws CDataGridException {
        super(columnName);
        if (lbound != null && ubound != null && lbound.compareTo(ubound) >= 0)
            throw new CDataGridException("Lower bound cannot be greater than or equal to upper bound.");
        this.lbound = lbound;
        this.ubound = ubound;
    }

    /**
     * Compares to this date range.
     * Can be within a range, or GE, or LE.
     *
     * @param row
     * @return true, if the current data row  matches this date range
     * @throws CDataGridException
     */
    public boolean doesMatch(CDataRow row) throws CDataGridException {
        java.util.Date dateValue = (java.util.Date) row.getValue(columnIndex);
        if (dateValue == null)
            return false;

        boolean matches = false;
        if (lbound != null && ubound != null) {
             matches = (lbound.compareTo(dateValue) <= 0 && ubound.compareTo(dateValue) >= 0);
        } else {
            if (lbound == null) {
                matches = (ubound.compareTo(dateValue) >= 0);
            } else if (ubound == null) {
                matches = (lbound.compareTo(dateValue) <= 0);
            }
        }
        return matches;
    }

    /**
     * Returns string representation of this filter
     * @return string
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("DateRangeFilter :: where ");
        sbuf.append(columnName).append(" (").append(columnIndex).append(") in (");
        sbuf.append(String.valueOf(lbound)).append("..").append(String.valueOf(ubound)).append("). ");
        return sbuf.toString();
    }

}
