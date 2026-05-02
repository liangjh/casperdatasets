package net.casper.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

/**
 * Data sorting functionality - implements comparator.
 * Because we are comparing rows that can take on any data types, the constructor
 * expects the data type of the specified column to be passed, and allows
 * sorting comparisons to be performed on a data type basis.
 * <br/><br/>
 * Composite column sorting has been added, to allow for sorting on multiple
 * columns, in different orders.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CDataComparator implements Comparator, Serializable {

    /**
     * Why do we have this?  In the case where two objects have -NOT- even been compared yet
     * and the value of one object is null, then we cannot just return the "previous" comparison.
     * If two rows have already been compared on this basis, then we return the previous comparison.
     */
    private static final int UNCOMPARED_STATE = -100;

    /** The indices of the column in the row(s) that we want to test for this comparison */
    private int[] columnIndices  = new int[0];

    /**
     * The type of comparison.  This allow us to perform different kinds
     * of sorting / comparisons based on the datatype of a particular column
     */
    private Class[] columnTypes = new Class[0];

    /**
     * Do not allow empty instantiation.
     */
    private CDataComparator() {
    }

    /**
     * Construct this object
     *
     * @param columnIndices - the column that we are comparing against
     * @param columnTypes - the datatype of the column against which we are comparing
     * @throws CDataGridException
     */
    public CDataComparator(int[] columnIndices, Class[] columnTypes)
            throws CDataGridException {
        super();

        if (columnIndices == null || columnIndices.length < 1 || columnTypes == null ||
            columnIndices.length != columnTypes.length) {
            throw new CDataGridException("Size of columnIndices, ascending, and columnTypes *MUST* be equivalent.");
        }

        for (int i = 0; i < columnIndices.length; i++) {
            if (columnIndices[i] < 0)
                throw new CDataGridException("Column index must be > 0, or a valid column name");
        }

        for (int i = 0; i < columnTypes.length; i++) {
            if (columnTypes[i] == null)
                throw new CDataGridException("Passed column types cannot be null.");
        }

        this.columnIndices = columnIndices;
        this.columnTypes = columnTypes;
    }

    /**
     * Compares two rows, returns an order for the comparison.
     * Allows for special comparisons based on datatype.  Otherwise we return
     * a natural ordering of the two types.
     *
     * @param o1 - first object of comparison
     * @param o2 - second object of comparison
     * @return a negative integer if o1 < o2, zero if o1 equals o2, or a positive integer if o1 > o2
     */
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) { return  0; }
        if (o1 == null && o2 != null) { return -1; }
        if (o1 != null && o2 == null) { return  1; }

        int cmpResult = UNCOMPARED_STATE;

        if (o1 != null && o2 != null) {
            for (int i = 0; i < columnIndices.length; i++) {
                Object v1 = null;
                Object v2 = null;

                try {
                    CDataRow r1 = (CDataRow) o1;
                    CDataRow r2 = (CDataRow) o2;

                    v1 = r1.getValue(columnIndices[i]);
                    v2 = r2.getValue(columnIndices[i]);

                    if (cmpResult == UNCOMPARED_STATE) {
                        if (v1 == null && v2 == null) { return  0; }
                        if (v1 == null && v2 != null) { return -1; }
                        if (v1 != null && v2 == null) { return  1; }
                    } else {
                        if (v1 == null && v2 == null) { return cmpResult; }
                        if (v1 == null && v2 != null) { return -1; }
                        if (v1 != null && v2 == null) { return  1; }
                    }

                    if (columnTypes[i].equals(String.class)) {
                        String s1 = (String) v1;
                        String s2 = (String) v2;
                        cmpResult = s1.compareTo(s2);
                    } else if (columnTypes[i].equals(Boolean.class) || columnTypes[i].equals(boolean.class)) {
                        boolean b1 = ((Boolean)v1).booleanValue();
                        boolean b2 = ((Boolean)v2).booleanValue();
                        cmpResult = (b1 == b2) ? 0 : (b1 ? 1 : -1);
                    } else if (columnTypes[i].equals(Integer.class) || columnTypes[i].equals(int.class)) {
                        Integer i1 = (Integer) v1;
                        Integer i2 = (Integer) v2;
                        cmpResult = i1.compareTo(i2);
                    } else if (columnTypes[i].equals(Double.class)  || columnTypes[i].equals(double.class)) {
                        Double d1 = (Double) v1;
                        Double d2 = (Double) v2;
                        cmpResult = d1.compareTo(d2);
                    } else if (columnTypes[i].equals(Float.class)  || columnTypes[i].equals(float.class)) {
                        Float f1 = (Float) v1;
                        Float f2 = (Float) v2;
                        cmpResult = f1.compareTo(f2);
                    } else if (columnTypes[i].equals(Date.class)) {
                        Date dt1 = (Date) v1;
                        Date dt2 = (Date) v2;
                        cmpResult = dt1.compareTo(dt2);
                    } else if (columnTypes[i].equals(Timestamp.class)) {
                        Timestamp ts1 = (Timestamp) v1;
                        Timestamp ts2 = (Timestamp) v2;
                        cmpResult = ts1.compareTo(ts2);
                    } else if (columnTypes[i].equals(Byte.class) || columnTypes[i].equals(byte.class)) {
                        Byte bt1 = (Byte) v1;
                        Byte bt2 = (Byte) v2;
                        cmpResult = bt1.compareTo(bt2);
                    } else {
                        cmpResult = 0;
                    }

                    if (cmpResult != 0)
                        break;
                } catch (Exception ex) {
                    throw new RuntimeException("Failed sort comparison for columnIndex: "
                            + columnIndices[i] + ", columnType: " + columnTypes[i]
                            + ", value1: " + v1
                            + ", value2: " + v2
                            + ", Reason: "
                            + ex.toString());
                }
            }
        }

        return cmpResult;
    }

    /**
     * Not sure why we have this, but FINE...
     */
    public boolean equals(Object o) {
        return super.equals(o);
    }

}
