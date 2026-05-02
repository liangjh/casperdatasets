package net.casper.data.model;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

/**
 * This is a JDBC ResultSet-compliant implementation of the casper rowset.
 * The implementation of this wrapper will allow applications that rely upon JDBC resultsets
 * to plug into the casper datasets framework more or less seamlessly.
 * <br/><br/>
 * Note: there are a number of methods that have been left as no-op or return "null" values.
 * Please feel free to flesh out the implementation.
 *
 * Requires Java 1.4 (or later).
 *
 * @since v1.0
 * @author Jonathan Liang
 */
public class CDataResultSet implements ResultSet, Serializable {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /** The underlying CDataRowSet object. */
    private CDataRowSet rowset = null;

    /**
     * The implementation must wrap a CDataRowSet,
     * Do not allow empty instantiation
     */
    private CDataResultSet() {
    }

    /**
     * Instantiates an instance of CDataResultSet by wrapping a CDataRowSet object.
     * @param rowset
     */
    public CDataResultSet(CDataRowSet rowset) {
        this.rowset = rowset;
    }

    /**
     * This is necessary because the column index numbers that are expected begin with 1..n, whereas
     * the columns that is actually stored in this meta data implementation are stored within arrays,
     * beginning at index 0.
     *
     * @param column
     * @return transformed column index
     */
    private int transformColumnToArrayIndex(int column) {
        return (column - 1);
    }

    public boolean next() throws SQLException {
        try {
            return rowset.next();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public void close() throws SQLException {
        return;
    }

    public boolean wasNull() throws SQLException {
        return false;
    }

    public String getString(int column) throws SQLException {
        try { return rowset.getString(transformColumnToArrayIndex(column)); }
        catch (Exception ex){ throw new SQLException("Operation failed: " + ex.toString()); }
    }

    public boolean getBoolean(int column) throws SQLException {
        try {
            Boolean val = rowset.getBoolean(transformColumnToArrayIndex(column));
            return val.booleanValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public byte getByte(int column) throws SQLException {
        try {
            Byte val = rowset.getByte(transformColumnToArrayIndex(column));
            return val.byteValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public short getShort(int column) throws SQLException {
        try {
            Short val = rowset.getShort(transformColumnToArrayIndex(column));
            return val.shortValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public int getInt(int column) throws SQLException {
        try {
            Integer val = rowset.getInt(transformColumnToArrayIndex(column));
            return val.intValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public long getLong(int column) throws SQLException {
        try {
            Long val = rowset.getLong(transformColumnToArrayIndex(column));
            return val.longValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public float getFloat(int column) throws SQLException {
        try {
            Float val = rowset.getFloat(transformColumnToArrayIndex(column));
            return val.floatValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public double getDouble(int column) throws SQLException {
        try {
            Double val = rowset.getDouble(transformColumnToArrayIndex(column));
            return val.doubleValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public BigDecimal getBigDecimal(int column, int scale) throws SQLException {
        return null;
    }

    public byte[] getBytes(int column) throws SQLException {
        return null;
    }

    public Date getDate(int column) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(transformColumnToArrayIndex(column));
            return new java.sql.Date(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Time getTime(int column) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(transformColumnToArrayIndex(column));
            return new java.sql.Time(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Timestamp getTimestamp(int column) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(transformColumnToArrayIndex(column));
            return new java.sql.Timestamp(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public InputStream getAsciiStream(int column) throws SQLException {
        return null;
    }

    public InputStream getUnicodeStream(int column) throws SQLException {
        return null;
    }

    public InputStream getBinaryStream(int column) throws SQLException {
        return null;
    }

    public String getString(String columnName) throws SQLException {
        try {
            return rowset.getString(columnName);
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public boolean getBoolean(String columnName) throws SQLException {
        try {
            Boolean val = rowset.getBoolean(columnName);
            return val.booleanValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public byte getByte(String columnName) throws SQLException {
        try {
            Byte val = rowset.getByte(columnName);
            return val.byteValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public short getShort(String columnName) throws SQLException {
        try {
            Short val = rowset.getShort(columnName);
            return val.shortValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public int getInt(String columnName) throws SQLException {
        try {
            Integer val = rowset.getInt(columnName);
            return val.intValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public long getLong(String columnName) throws SQLException {
        try {
            Long val = rowset.getLong(columnName);
            return val.longValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public float getFloat(String columnName) throws SQLException {
        try {
            Float val = rowset.getFloat(columnName);
            return val.floatValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public double getDouble(String columnName) throws SQLException {
        try {
            Double val = rowset.getDouble(columnName);
            return val.doubleValue();
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public BigDecimal getBigDecimal(String columnName, int arg1) throws SQLException {
        return null;
    }

    public byte[] getBytes(String columnName) throws SQLException {
        return null;
    }

    public Date getDate(String columnName) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(columnName);
            return new Date(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Time getTime(String columnName) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(columnName);
            return new Time(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Timestamp getTimestamp(String columnName) throws SQLException {
        try {
            java.util.Date val = rowset.getDate(columnName);
            return new Timestamp(val.getTime());
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public InputStream getAsciiStream(String arg0) throws SQLException {
        return null;
    }

    public InputStream getUnicodeStream(String arg0) throws SQLException {
        return null;
    }

    public InputStream getBinaryStream(String arg0) throws SQLException {
        return null;
    }

    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    public void clearWarnings() throws SQLException {
    }

    public String getCursorName() throws SQLException {
        return null;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return rowset.getMetaDefinition();
    }

    public Object getObject(int column) throws SQLException {
        try {
            return rowset.getObject(transformColumnToArrayIndex(column));
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Object getObject(String columnName) throws SQLException {
        try {
            return rowset.getObject(columnName);
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public int findColumn(String columnName) throws SQLException {
        try {
            CRowMetaData meta = (CRowMetaData) getMetaData();
            return (meta.getColumnIndex(columnName) + 1);
        } catch (Exception ex) {
            throw new SQLException("Operation failed: " + ex.toString());
        }
    }

    public Reader getCharacterStream(int column) throws SQLException {
        return null;
    }

    public Reader getCharacterStream(String columnName) throws SQLException {
        return null;
    }

    public BigDecimal getBigDecimal(int column) throws SQLException {
        return null;
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException {
        return null;
    }

    public boolean isBeforeFirst() throws SQLException {
        return rowset.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException {
        return rowset.isAfterLast();
    }

    public boolean isFirst() throws SQLException {
        return rowset.isFirst();
    }

    public boolean isLast() throws SQLException {
        return rowset.isLast();
    }

    public void beforeFirst() throws SQLException {
        rowset.beforeFirst();
    }

    public void afterLast() throws SQLException {
        rowset.afterLast();
    }

    public boolean first() throws SQLException {
        return rowset.first();
    }

    public boolean last() throws SQLException {
        return rowset.last();
    }

    public int getRow() throws SQLException {
        return rowset.getCursorPosition();
    }

    public boolean absolute(int row) throws SQLException {
        return rowset.absolute(row);
    }

    public boolean relative(int numRows) throws SQLException {
        return rowset.relative(numRows);
    }

    public boolean previous() throws SQLException {
        return rowset.previous();
    }

    public void setFetchDirection(int arg0) throws SQLException {
        return;
    }

    public int getFetchDirection() throws SQLException {
        return 0;
    }

    public void setFetchSize(int size) throws SQLException {
        return;
    }

    public int getFetchSize() throws SQLException {
        return 0;
    }

    public int getType() throws SQLException {
        return 0;
    }

    public int getConcurrency() throws SQLException {
        return 0;
    }

    public boolean rowUpdated() throws SQLException {
        return false;
    }

    public boolean rowInserted() throws SQLException {
        return false;
    }

    public boolean rowDeleted() throws SQLException {
        return false;
    }

    public void updateNull(int arg0) throws SQLException { return; }
    public void updateBoolean(int arg0, boolean arg1) throws SQLException { return; }
    public void updateByte(int arg0, byte arg1) throws SQLException { return; }
    public void updateShort(int arg0, short arg1) throws SQLException { return; }
    public void updateInt(int arg0, int arg1) throws SQLException { return; }
    public void updateLong(int arg0, long arg1) throws SQLException { return; }
    public void updateFloat(int arg0, float arg1) throws SQLException { return; }
    public void updateDouble(int arg0, double arg1) throws SQLException { return; }
    public void updateBigDecimal(int arg0, BigDecimal arg1) throws SQLException { return; }
    public void updateString(int arg0, String arg1) throws SQLException { return; }
    public void updateBytes(int arg0, byte[] arg1) throws SQLException { return; }
    public void updateDate(int arg0, Date arg1) throws SQLException { return; }
    public void updateTime(int arg0, Time arg1) throws SQLException { return; }
    public void updateTimestamp(int arg0, Timestamp arg1) throws SQLException { return; }
    public void updateAsciiStream(int arg0, InputStream arg1, int arg2) throws SQLException { return; }
    public void updateBinaryStream(int arg0, InputStream arg1, int arg2) throws SQLException { return; }
    public void updateCharacterStream(int arg0, Reader arg1, int arg2) throws SQLException { return; }
    public void updateObject(int arg0, Object arg1, int arg2) throws SQLException { return; }
    public void updateObject(int arg0, Object arg1) throws SQLException { return; }
    public void updateNull(String arg0) throws SQLException { return; }
    public void updateBoolean(String arg0, boolean arg1) throws SQLException { return; }
    public void updateByte(String arg0, byte arg1) throws SQLException { return; }
    public void updateShort(String arg0, short arg1) throws SQLException { return; }
    public void updateInt(String arg0, int arg1) throws SQLException { return; }
    public void updateLong(String arg0, long arg1) throws SQLException { return; }
    public void updateFloat(String arg0, float arg1) throws SQLException { return; }
    public void updateDouble(String arg0, double arg1) throws SQLException { return; }
    public void updateBigDecimal(String arg0, BigDecimal arg1) throws SQLException { return; }
    public void updateString(String arg0, String arg1) throws SQLException { return; }
    public void updateBytes(String arg0, byte[] arg1) throws SQLException { return; }
    public void updateDate(String arg0, Date arg1) throws SQLException { return; }
    public void updateTime(String arg0, Time arg1) throws SQLException { return; }
    public void updateTimestamp(String arg0, Timestamp arg1) throws SQLException { return; }
    public void updateAsciiStream(String arg0, InputStream arg1, int arg2) throws SQLException { return; }
    public void updateBinaryStream(String arg0, InputStream arg1, int arg2) throws SQLException { return; }
    public void updateCharacterStream(String arg0, Reader arg1, int arg2) throws SQLException { return; }
    public void updateObject(String arg0, Object arg1, int arg2) throws SQLException { return; }
    public void updateObject(String arg0, Object arg1) throws SQLException { return; }
    public void insertRow() throws SQLException { return; }
    public void updateRow() throws SQLException { return; }
    public void deleteRow() throws SQLException { return; }
    public void refreshRow() throws SQLException { return; }
    public void cancelRowUpdates() throws SQLException { return; }
    public void moveToInsertRow() throws SQLException { return; }
    public void moveToCurrentRow() throws SQLException { return; }

    public Statement getStatement() throws SQLException {
        return null;
    }

    public Object getObject(int arg0, Map arg1) throws SQLException { return null; }
    public Ref getRef(int column) throws SQLException { return null; }
    public Blob getBlob(int column) throws SQLException { return null; }
    public Clob getClob(int column) throws SQLException { return null; }
    public Array getArray(int column) throws SQLException { return null; }
    public Object getObject(String arg0, Map arg1) throws SQLException { return null; }
    public Ref getRef(String columnName) throws SQLException { return null; }
    public Blob getBlob(String columnName) throws SQLException { return null; }
    public Clob getClob(String columnName) throws SQLException { return null; }
    public Array getArray(String columnName) throws SQLException { return null; }
    public Date getDate(int arg0, Calendar arg1) throws SQLException { return null; }
    public Date getDate(String arg0, Calendar arg1) throws SQLException { return null; }
    public Time getTime(int arg0, Calendar arg1) throws SQLException { return null; }
    public Time getTime(String arg0, Calendar arg1) throws SQLException { return null; }
    public Timestamp getTimestamp(int arg0, Calendar arg1) throws SQLException { return null; }
    public Timestamp getTimestamp(String arg0, Calendar arg1) throws SQLException { return null; }
    public URL getURL(int column) throws SQLException { return null; }
    public URL getURL(String arg0) throws SQLException { return null; }
    public void updateRef(int arg0, Ref arg1) throws SQLException { return; }
    public void updateRef(String arg0, Ref arg1) throws SQLException { return; }
    public void updateBlob(int arg0, Blob arg1) throws SQLException { return; }
    public void updateBlob(String arg0, Blob arg1) throws SQLException { return; }
    public void updateClob(int arg0, Clob arg1) throws SQLException { return; }
    public void updateClob(String arg0, Clob arg1) throws SQLException { return; }
    public void updateArray(int arg0, Array arg1) throws SQLException { return; }
    public void updateArray(String arg0, Array arg1) throws SQLException { return; }

    public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException("Not supported"); }
    public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException { throw new SQLException("Not supported"); }
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException { throw new SQLException("Not supported"); }

    public java.sql.RowId getRowId(int columnIndex) throws SQLException { throw new SQLException("Not supported"); }
    public java.sql.RowId getRowId(String columnLabel) throws SQLException { throw new SQLException("Not supported"); }
    public void updateRowId(int columnIndex, java.sql.RowId x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateRowId(String columnLabel, java.sql.RowId x) throws SQLException { throw new SQLException("Not supported"); }
    public int getHoldability() throws SQLException { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }
    public boolean isClosed() throws SQLException { return false; }
    public void updateNString(int columnIndex, String nString) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNString(String columnLabel, String nString) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(int columnIndex, java.sql.NClob nClob) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(String columnLabel, java.sql.NClob nClob) throws SQLException { throw new SQLException("Not supported"); }
    public java.sql.NClob getNClob(int columnIndex) throws SQLException { throw new SQLException("Not supported"); }
    public java.sql.NClob getNClob(String columnLabel) throws SQLException { throw new SQLException("Not supported"); }
    public java.sql.SQLXML getSQLXML(int columnIndex) throws SQLException { throw new SQLException("Not supported"); }
    public java.sql.SQLXML getSQLXML(String columnLabel) throws SQLException { throw new SQLException("Not supported"); }
    public void updateSQLXML(int columnIndex, java.sql.SQLXML xmlObject) throws SQLException { throw new SQLException("Not supported"); }
    public void updateSQLXML(String columnLabel, java.sql.SQLXML xmlObject) throws SQLException { throw new SQLException("Not supported"); }
    public String getNString(int columnIndex) throws SQLException { throw new SQLException("Not supported"); }
    public String getNString(String columnLabel) throws SQLException { throw new SQLException("Not supported"); }
    public Reader getNCharacterStream(int columnIndex) throws SQLException { throw new SQLException("Not supported"); }
    public Reader getNCharacterStream(String columnLabel) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNCharacterStream(String columnLabel, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateCharacterStream(String columnLabel, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNCharacterStream(String columnLabel, Reader reader) throws SQLException { throw new SQLException("Not supported"); }
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException { throw new SQLException("Not supported"); }
    public void updateCharacterStream(String columnLabel, Reader reader) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException { throw new SQLException("Not supported"); }
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException { throw new SQLException("Not supported"); }
    public void updateClob(int columnIndex, Reader reader) throws SQLException { throw new SQLException("Not supported"); }
    public void updateClob(String columnLabel, Reader reader) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(int columnIndex, Reader reader) throws SQLException { throw new SQLException("Not supported"); }
    public void updateNClob(String columnLabel, Reader reader) throws SQLException { throw new SQLException("Not supported"); }

}
