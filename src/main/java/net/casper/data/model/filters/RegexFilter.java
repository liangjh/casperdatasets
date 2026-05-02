package net.casper.data.model.filters;

import java.util.regex.Pattern;

import net.casper.data.model.CDataGridException;
import net.casper.data.model.CDataRow;

/**
 * Performs a regular-expression based filtering match.
 * This regex filter will return "true" if any of the regular expressions passed into
 * the constructor results in a match on the target data.
 * <br/><br/>
 * Note that a regular expression filter only matches on Strings, so if the target
 * value of the row is anything else, errant or unexpected behavior may result.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class RegexFilter extends CDataFilter {

    /** Regular expressions */
    private String[] regexps = null;

    /** Compiled regular expression patterns */
    private Pattern[] regexpPatterns = null;

    /**
     * If this setting is turned ON, then we will need to take the lower case form of the regex,
     * as well as of all rows against which the regex is compared
     */
    private boolean caseInsensitive = false;

    /**
     * Creates a regular-expression based match
     *
     * @param columnName
     * @param regexps - array of regular expressions (any that match will result in "true" evaluation)
     * @param caseInsensitive
     */
    public RegexFilter(String columnName, String[] regexps, boolean caseInsensitive)
            throws CDataGridException {
        super(columnName);
        if (regexps == null || regexps.length < 1)
            throw new CDataGridException("Must pass in at least one valid regular expression.");
        this.regexps = regexps;
        this.caseInsensitive  = caseInsensitive;

        try {
            this.regexpPatterns = new Pattern[regexps.length];
            for (int i = 0; i < regexps.length; i++) {
                String expr = regexps[i];
                if (caseInsensitive)
                    expr = expr.toLowerCase();
                regexpPatterns[i] = Pattern.compile(expr);
            }
        } catch (Exception ex) {
            throw new CDataGridException("Failed to compile regexp list: " + ex.toString() , ex);
        }
    }

    /**
     * Performs a match for this filter
     *
     * @return true, if this filter matches
     * @throws CDataGridException
     */
    public boolean doesMatch(CDataRow row) throws CDataGridException {
        checkColumnIndexInitialized();

        if (regexpPatterns == null || regexpPatterns.length < 1)
            return true;

        try {
            Object rowValue = row.getValue(columnIndex);
            if (rowValue == null)
                return false;

            String rowValueStr = rowValue.toString();
            if (caseInsensitive)
                rowValueStr = rowValueStr.toLowerCase();

            for (int i = 0; i < regexpPatterns.length; i++) {
                if (regexpPatterns[i].matcher(rowValueStr).matches())
                    return true;
            }
        } catch (Exception ex) {
            throw new CDataGridException("Failed to perform regex match ");
        }

        return false;
    }

    /**
     * Returns string representation of this object
     * @return string
     */
    public String toString() {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("RegexFilter :: where ");
        sbuf.append(columnName).append(" (").append(columnIndex).append(") in: ");
        sbuf.append("[");
        if (regexps != null) {
            for (int i = 0; i < regexps.length; i++) {
                sbuf.append(regexps[i]);
                if (i < regexps.length - 1)
                    sbuf.append(", ");
            }
        }
        sbuf.append("]");
        sbuf.append(", CaseIns: ").append(caseInsensitive).append(".");
        return sbuf.toString();
    }

}
