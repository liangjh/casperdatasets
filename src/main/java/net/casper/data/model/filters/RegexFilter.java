package net.casper.data.model.filters;

import java.util.regex.Pattern;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Regex filter. Matches rows where column value (as String) matches any of the given patterns.
 */
public class RegexFilter extends CDataFilter {

    private final String[] regexps;
    private final Pattern[] regexpPatterns;
    private final boolean caseInsensitive;

    public RegexFilter(String columnName, String regexp) throws CDataGridException {
        this(columnName, new String[] { regexp }, false);
    }

    public RegexFilter(String columnName, String[] regexps, boolean caseInsensitive)
            throws CDataGridException {
        super(columnName);
        if (regexps == null || regexps.length < 1)
            throw new CDataGridException("Must pass at least one regular expression.");
        this.regexps = regexps;
        this.caseInsensitive = caseInsensitive;

        try {
            this.regexpPatterns = new Pattern[regexps.length];
            for (int i = 0; i < regexps.length; i++) {
                String expr = caseInsensitive ? regexps[i].toLowerCase() : regexps[i];
                regexpPatterns[i] = Pattern.compile(expr);
            }
        } catch (Exception ex) {
            throw new CDataGridException("Failed to compile regex: " + ex, ex);
        }
    }

    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();
        if (regexpPatterns == null || regexpPatterns.length < 1) return true;

        try {
            Object rowValue = row.getValue(columnIndex);
            if (rowValue == null) return false;

            String str = caseInsensitive ? rowValue.toString().toLowerCase() : rowValue.toString();
            for (Pattern p : regexpPatterns) {
                if (p.matcher(str).matches()) return true;
            }
        } catch (Exception ex) {
            throw new CDataGridException("Failed regex match", ex);
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("RegexFilter :: where ");
        sb.append(columnName).append(" (").append(columnIndex).append(") in: [");
        for (int i = 0; i < regexps.length; i++) {
            sb.append(regexps[i]);
            if (i < regexps.length - 1) sb.append(", ");
        }
        return sb.append("], caseInsensitive: ").append(caseInsensitive).toString();
    }
}
