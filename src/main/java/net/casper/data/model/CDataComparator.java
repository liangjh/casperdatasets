package net.casper.data.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

/**
 * Multi-column, type-aware comparator for sorting CDataRow arrays.
 */
public class CDataComparator implements Comparator<Object>, Serializable {

    private static final int UNCOMPARED_STATE = -100;

    private final int[] columnIndices;
    private final Class<?>[] columnTypes;

    public CDataComparator(int[] columnIndices, Class<?>[] columnTypes)
            throws CDataGridException {
        if (columnIndices == null || columnIndices.length < 1 || columnTypes == null
                || columnIndices.length != columnTypes.length)
            throw new CDataGridException("columnIndices and columnTypes must be non-null and equal length.");

        for (int idx : columnIndices)
            if (idx < 0) throw new CDataGridException("Column index must be >= 0");
        for (Class<?> type : columnTypes)
            if (type == null) throw new CDataGridException("Column types cannot be null.");

        this.columnIndices = columnIndices;
        this.columnTypes = columnTypes;
    }

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null && o2 == null) return 0;
        if (o1 == null) return -1;
        if (o2 == null) return 1;

        int cmpResult = UNCOMPARED_STATE;

        for (int i = 0; i < columnIndices.length; i++) {
            Object v1 = null, v2 = null;
            try {
                CDataRow r1 = (CDataRow) o1;
                CDataRow r2 = (CDataRow) o2;
                v1 = r1.getValue(columnIndices[i]);
                v2 = r2.getValue(columnIndices[i]);

                if (v1 == null && v2 == null) return (cmpResult == UNCOMPARED_STATE) ? 0 : cmpResult;
                if (v1 == null) return -1;
                if (v2 == null) return 1;

                Class<?> type = columnTypes[i];
                if (type.equals(String.class)) {
                    cmpResult = ((String) v1).compareTo((String) v2);
                } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
                    boolean b1 = (Boolean) v1, b2 = (Boolean) v2;
                    cmpResult = (b1 == b2) ? 0 : (b1 ? 1 : -1);
                } else if (type.equals(Integer.class) || type.equals(int.class)) {
                    cmpResult = ((Integer) v1).compareTo((Integer) v2);
                } else if (type.equals(Double.class) || type.equals(double.class)) {
                    cmpResult = ((Double) v1).compareTo((Double) v2);
                } else if (type.equals(Float.class) || type.equals(float.class)) {
                    cmpResult = ((Float) v1).compareTo((Float) v2);
                } else if (type.equals(Date.class)) {
                    cmpResult = ((Date) v1).compareTo((Date) v2);
                } else if (type.equals(Timestamp.class)) {
                    cmpResult = ((Timestamp) v1).compareTo((Timestamp) v2);
                } else if (type.equals(Byte.class) || type.equals(byte.class)) {
                    cmpResult = ((Byte) v1).compareTo((Byte) v2);
                } else {
                    cmpResult = 0;
                }

                if (cmpResult != 0) break;
            } catch (Exception ex) {
                throw new RuntimeException("Sort comparison failed for columnIndex: "
                        + columnIndices[i] + ", type: " + columnTypes[i]
                        + ", v1: " + v1 + ", v2: " + v2 + ": " + ex);
            }
        }
        return cmpResult;
    }
}
