package net.casper.data.model.filters;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Equality filter. Matches rows where column value equals any of the given values.
 * Supports negation.
 */
public class EqualsFilter extends CDataFilter {

    private final Object[] matchValues;
    private final boolean negated;

    public EqualsFilter(String columnName, Object[] matchValues) throws CDataGridException {
        this(columnName, matchValues, false);
    }

    public EqualsFilter(String columnName, Object[] matchValues, boolean negated) throws CDataGridException {
        super(columnName);
        if (matchValues == null || matchValues.length < 1)
            throw new CDataGridException("Match values cannot be null or empty.");
        this.matchValues = matchValues;
        this.negated = negated;
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();
        Object rowVal = row.getValue(columnIndex);
        boolean match = false;
        for (Object val : matchValues) {
            if (val == null ? rowVal == null : val.equals(rowVal)) {
                match = true;
                break;
            }
        }
        return negated ? !match : match;
    }

    public Object[] getMatchValues() { return matchValues; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("EqualsFilter :: where ");
        sb.append(columnName).append(" (").append(columnIndex).append(") in: (");
        for (int i = 0; i < matchValues.length; i++) {
            sb.append(matchValues[i]);
            if (i < matchValues.length - 1) sb.append(", ");
        }
        return sb.append(")").toString();
    }
}
