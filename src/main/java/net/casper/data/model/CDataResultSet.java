package net.casper.data.model;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.Map;

/**
 * JDBC ResultSet wrapper around a {@link CDataRowSet}.
 * Unsupported operations return null / throw SQLException as appropriate.
 */
public class CDataResultSet implements ResultSet, Serializable {

    private static final long serialVersionUID = 1L;
    private final CDataRowSet rowset;

    public CDataResultSet(CDataRowSet rowset) {
        this.rowset = rowset;
    }

    private int toIdx(int column) { return column - 1; }

    private SQLException wrap(Exception ex) {
        return new SQLException("Operation failed: " + ex.toString());
    }

    // --- Core navigation ---

    public boolean next() throws SQLException {
        try { return rowset.next(); } catch (Exception ex) { throw wrap(ex); }
    }
    public void close() {}
    public boolean wasNull() { return false; }

    // --- Typed getters by index (1-based) ---

    public String getString(int col) throws SQLException {
        try { return rowset.getString(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public boolean getBoolean(int col) throws SQLException {
        try { return rowset.getBoolean(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public byte getByte(int col) throws SQLException {
        try { return rowset.getByte(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public short getShort(int col) throws SQLException {
        try { return rowset.getShort(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public int getInt(int col) throws SQLException {
        try { return rowset.getInt(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public long getLong(int col) throws SQLException {
        try { return rowset.getLong(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public float getFloat(int col) throws SQLException {
        try { return rowset.getFloat(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public double getDouble(int col) throws SQLException {
        try { return rowset.getDouble(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }
    public Date getDate(int col) throws SQLException {
        try { return new Date(rowset.getDate(toIdx(col)).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Time getTime(int col) throws SQLException {
        try { return new Time(rowset.getDate(toIdx(col)).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Timestamp getTimestamp(int col) throws SQLException {
        try { return new Timestamp(rowset.getDate(toIdx(col)).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Object getObject(int col) throws SQLException {
        try { return rowset.getObject(toIdx(col)); } catch (Exception ex) { throw wrap(ex); }
    }

    // --- Typed getters by name ---

    public String getString(String col) throws SQLException {
        try { return rowset.getString(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public boolean getBoolean(String col) throws SQLException {
        try { return rowset.getBoolean(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public byte getByte(String col) throws SQLException {
        try { return rowset.getByte(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public short getShort(String col) throws SQLException {
        try { return rowset.getShort(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public int getInt(String col) throws SQLException {
        try { return rowset.getInt(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public long getLong(String col) throws SQLException {
        try { return rowset.getLong(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public float getFloat(String col) throws SQLException {
        try { return rowset.getFloat(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public double getDouble(String col) throws SQLException {
        try { return rowset.getDouble(col); } catch (Exception ex) { throw wrap(ex); }
    }
    public Date getDate(String col) throws SQLException {
        try { return new Date(rowset.getDate(col).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Time getTime(String col) throws SQLException {
        try { return new Time(rowset.getDate(col).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Timestamp getTimestamp(String col) throws SQLException {
        try { return new Timestamp(rowset.getDate(col).getTime()); } catch (Exception ex) { throw wrap(ex); }
    }
    public Object getObject(String col) throws SQLException {
        try { return rowset.getObject(col); } catch (Exception ex) { throw wrap(ex); }
    }

    // --- Cursor / metadata ---

    public ResultSetMetaData getMetaData() { return rowset.getMetaDefinition(); }
    public int findColumn(String col) throws SQLException {
        try { return ((CRowMetaData) getMetaData()).getColumnIndex(col) + 1; }
        catch (Exception ex) { throw wrap(ex); }
    }
    public boolean isBeforeFirst() { return rowset.isBeforeFirst(); }
    public boolean isAfterLast() { return rowset.isAfterLast(); }
    public boolean isFirst() { return rowset.isFirst(); }
    public boolean isLast() { return rowset.isLast(); }
    public void beforeFirst() { rowset.beforeFirst(); }
    public void afterLast() { rowset.afterLast(); }
    public boolean first() { return rowset.first(); }
    public boolean last() { return rowset.last(); }
    public int getRow() { return rowset.getCursorPosition(); }
    public boolean absolute(int row) { return rowset.absolute(row); }
    public boolean relative(int rows) { return rowset.relative(rows); }
    public boolean previous() { return rowset.previous(); }

    // --- Unsupported / no-op stubs ---

    public BigDecimal getBigDecimal(int col, int scale) { return null; }
    public byte[] getBytes(int col) { return null; }
    public InputStream getAsciiStream(int col) { return null; }
    public InputStream getUnicodeStream(int col) { return null; }
    public InputStream getBinaryStream(int col) { return null; }
    public BigDecimal getBigDecimal(String col, int s) { return null; }
    public byte[] getBytes(String col) { return null; }
    public InputStream getAsciiStream(String col) { return null; }
    public InputStream getUnicodeStream(String col) { return null; }
    public InputStream getBinaryStream(String col) { return null; }
    public SQLWarning getWarnings() { return null; }
    public void clearWarnings() {}
    public String getCursorName() { return null; }
    public Reader getCharacterStream(int col) { return null; }
    public Reader getCharacterStream(String col) { return null; }
    public BigDecimal getBigDecimal(int col) { return null; }
    public BigDecimal getBigDecimal(String col) { return null; }
    public void setFetchDirection(int d) {}
    public int getFetchDirection() { return 0; }
    public void setFetchSize(int s) {}
    public int getFetchSize() { return 0; }
    public int getType() { return 0; }
    public int getConcurrency() { return 0; }
    public boolean rowUpdated() { return false; }
    public boolean rowInserted() { return false; }
    public boolean rowDeleted() { return false; }
    public Statement getStatement() { return null; }

    // --- All update/insert/delete stubs ---
    public void updateNull(int c) {}
    public void updateBoolean(int c, boolean v) {}
    public void updateByte(int c, byte v) {}
    public void updateShort(int c, short v) {}
    public void updateInt(int c, int v) {}
    public void updateLong(int c, long v) {}
    public void updateFloat(int c, float v) {}
    public void updateDouble(int c, double v) {}
    public void updateBigDecimal(int c, BigDecimal v) {}
    public void updateString(int c, String v) {}
    public void updateBytes(int c, byte[] v) {}
    public void updateDate(int c, Date v) {}
    public void updateTime(int c, Time v) {}
    public void updateTimestamp(int c, Timestamp v) {}
    public void updateAsciiStream(int c, InputStream v, int l) {}
    public void updateBinaryStream(int c, InputStream v, int l) {}
    public void updateCharacterStream(int c, Reader v, int l) {}
    public void updateObject(int c, Object v, int s) {}
    public void updateObject(int c, Object v) {}
    public void updateNull(String c) {}
    public void updateBoolean(String c, boolean v) {}
    public void updateByte(String c, byte v) {}
    public void updateShort(String c, short v) {}
    public void updateInt(String c, int v) {}
    public void updateLong(String c, long v) {}
    public void updateFloat(String c, float v) {}
    public void updateDouble(String c, double v) {}
    public void updateBigDecimal(String c, BigDecimal v) {}
    public void updateString(String c, String v) {}
    public void updateBytes(String c, byte[] v) {}
    public void updateDate(String c, Date v) {}
    public void updateTime(String c, Time v) {}
    public void updateTimestamp(String c, Timestamp v) {}
    public void updateAsciiStream(String c, InputStream v, int l) {}
    public void updateBinaryStream(String c, InputStream v, int l) {}
    public void updateCharacterStream(String c, Reader v, int l) {}
    public void updateObject(String c, Object v, int s) {}
    public void updateObject(String c, Object v) {}
    public void insertRow() {}
    public void updateRow() {}
    public void deleteRow() {}
    public void refreshRow() {}
    public void cancelRowUpdates() {}
    public void moveToInsertRow() {}
    public void moveToCurrentRow() {}

    public Object getObject(int c, Map m) { return null; }
    public Ref getRef(int c) { return null; }
    public Blob getBlob(int c) { return null; }
    public Clob getClob(int c) { return null; }
    public Array getArray(int c) { return null; }
    public Object getObject(String c, Map m) { return null; }
    public Ref getRef(String c) { return null; }
    public Blob getBlob(String c) { return null; }
    public Clob getClob(String c) { return null; }
    public Array getArray(String c) { return null; }
    public Date getDate(int c, Calendar cal) { return null; }
    public Date getDate(String c, Calendar cal) { return null; }
    public Time getTime(int c, Calendar cal) { return null; }
    public Time getTime(String c, Calendar cal) { return null; }
    public Timestamp getTimestamp(int c, Calendar cal) { return null; }
    public Timestamp getTimestamp(String c, Calendar cal) { return null; }
    public URL getURL(int c) { return null; }
    public URL getURL(String c) { return null; }
    public void updateRef(int c, Ref v) {}
    public void updateRef(String c, Ref v) {}
    public void updateBlob(int c, Blob v) {}
    public void updateBlob(String c, Blob v) {}
    public void updateClob(int c, Clob v) {}
    public void updateClob(String c, Clob v) {}
    public void updateArray(int c, Array v) {}
    public void updateArray(String c, Array v) {}

    private static final String NOT_SUPPORTED = "Not supported";
    public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public boolean isWrapperFor(Class<?> iface) { return false; }
    public <T> T getObject(int c, Class<T> t) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public <T> T getObject(String c, Class<T> t) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public RowId getRowId(int c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public RowId getRowId(String c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateRowId(int c, RowId v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateRowId(String c, RowId v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public int getHoldability() { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }
    public boolean isClosed() { return false; }
    public void updateNString(int c, String v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNString(String c, String v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(int c, NClob v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(String c, NClob v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public NClob getNClob(int c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public NClob getNClob(String c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public SQLXML getSQLXML(int c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public SQLXML getSQLXML(String c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateSQLXML(int c, SQLXML v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateSQLXML(String c, SQLXML v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public String getNString(int c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public String getNString(String c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public Reader getNCharacterStream(int c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public Reader getNCharacterStream(String c) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNCharacterStream(int c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNCharacterStream(String c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateAsciiStream(int c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBinaryStream(int c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateCharacterStream(int c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateAsciiStream(String c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBinaryStream(String c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateCharacterStream(String c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBlob(int c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBlob(String c, InputStream v, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateClob(int c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateClob(String c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(int c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(String c, Reader r, long l) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNCharacterStream(int c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNCharacterStream(String c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateAsciiStream(int c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBinaryStream(int c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateCharacterStream(int c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateAsciiStream(String c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBinaryStream(String c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateCharacterStream(String c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBlob(int c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateBlob(String c, InputStream v) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateClob(int c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateClob(String c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(int c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
    public void updateNClob(String c, Reader r) throws SQLException { throw new SQLException(NOT_SUPPORTED); }
}
