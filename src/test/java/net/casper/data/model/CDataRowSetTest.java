package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CDataRowSet")
class CDataRowSetTest {

    private CDataRowSet rowSet;
    private CRowMetaData meta;

    @BeforeEach
    void setUp() throws CDataGridException {
        meta = new CRowMetaData(
            new String[]{"id", "name", "score", "active"},
            new Class[]{Integer.class, String.class, Double.class, Boolean.class},
            new String[]{"id"}
        );
        rowSet = new CDataRowSet(meta);
        rowSet.addData(new CDataRow[]{
            new CDataRow(new Object[]{1, "Alice", 95.5, true}),
            new CDataRow(new Object[]{2, "Bob", 82.3, false}),
            new CDataRow(new Object[]{3, "Charlie", 88.7, true})
        });
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should throw when metadata is null")
        void throwOnNullMeta() {
            assertThrows(CDataGridException.class, () -> new CDataRowSet(null));
        }

        @Test
        @DisplayName("should have correct size after construction")
        void correctSize() {
            assertEquals(3, rowSet.size());
            assertEquals(3, rowSet.getNumberRows());
        }

        @Test
        @DisplayName("should return metadata")
        void returnMetadata() {
            assertSame(meta, rowSet.getMetaDefinition());
        }
    }

    @Nested
    @DisplayName("Adding data")
    class AddingData {

        @Test
        @DisplayName("should reject rows with wrong cardinality")
        void rejectWrongCardinality() {
            assertThrows(CDataGridException.class, () ->
                rowSet.addData(new CDataRow[]{new CDataRow(new Object[]{1, "X"})})
            );
        }

        @Test
        @DisplayName("should handle null or empty row arrays gracefully")
        void handleNullOrEmpty() throws CDataGridException {
            int sizeBefore = rowSet.size();
            rowSet.addData(null);
            rowSet.addData(new CDataRow[0]);
            assertEquals(sizeBefore, rowSet.size());
        }
    }

    @Nested
    @DisplayName("Cursor navigation")
    class CursorNavigation {

        @Test
        @DisplayName("cursor starts before first")
        void startsBeforeFirst() {
            assertTrue(rowSet.isBeforeFirst());
            assertEquals(0, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("next should advance cursor")
        void nextAdvances() throws CDataGridException {
            assertTrue(rowSet.next());
            assertEquals(1, rowSet.getCursorPosition());
            assertTrue(rowSet.isFirst());
        }

        @Test
        @DisplayName("should iterate through all rows with next")
        void iterateAll() throws CDataGridException {
            int count = 0;
            while (rowSet.next()) {
                count++;
            }
            assertEquals(3, count);
            assertTrue(rowSet.isAfterLast());
        }

        @Test
        @DisplayName("next should return false after last row")
        void nextReturnsFalseAfterLast() throws CDataGridException {
            rowSet.next(); // 1
            rowSet.next(); // 2
            rowSet.next(); // 3
            assertFalse(rowSet.next()); // past end
            assertFalse(rowSet.next()); // still past end
        }

        @Test
        @DisplayName("previous should move cursor backward")
        void previousMovesBack() throws CDataGridException {
            rowSet.next(); // 1
            rowSet.next(); // 2
            assertTrue(rowSet.previous());
            assertEquals(1, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("previous should return false at beginning")
        void previousAtBeginning() throws CDataGridException {
            assertFalse(rowSet.previous()); // already before first
            rowSet.next(); // cursor = 1
            // previous() decrements cursor to 0, then returns false because cursor == 0
            assertFalse(rowSet.previous());
            assertTrue(rowSet.isBeforeFirst());
        }

        @Test
        @DisplayName("first should set cursor to first row")
        void firstSetsCursor() {
            assertTrue(rowSet.first());
            assertEquals(1, rowSet.getCursorPosition());
            assertTrue(rowSet.isFirst());
        }

        @Test
        @DisplayName("first should return false on empty rowset")
        void firstOnEmpty() throws CDataGridException {
            CDataRowSet empty = new CDataRowSet(meta);
            assertFalse(empty.first());
        }

        @Test
        @DisplayName("last should set cursor to last row")
        void lastSetsCursor() {
            assertTrue(rowSet.last());
            assertEquals(3, rowSet.getCursorPosition());
            assertTrue(rowSet.isLast());
        }

        @Test
        @DisplayName("last should return false on empty rowset")
        void lastOnEmpty() throws CDataGridException {
            CDataRowSet empty = new CDataRowSet(meta);
            assertFalse(empty.last());
        }

        @Test
        @DisplayName("absolute should set cursor to specific row")
        void absoluteSetsCursor() {
            assertTrue(rowSet.absolute(2));
            assertEquals(2, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("absolute should return false for out-of-range")
        void absoluteOutOfRange() {
            assertFalse(rowSet.absolute(0));
            assertFalse(rowSet.absolute(4));
            assertFalse(rowSet.absolute(-1));
        }

        @Test
        @DisplayName("relative should offset cursor from current position")
        void relativeOffsets() throws CDataGridException {
            rowSet.next(); // cursor = 1
            assertTrue(rowSet.relative(2)); // cursor = 3
            assertEquals(3, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("relative should return false when result is out-of-range")
        void relativeOutOfRange() throws CDataGridException {
            rowSet.next(); // cursor = 1
            assertFalse(rowSet.relative(3)); // would be 4
            assertFalse(rowSet.relative(-1)); // would be 0
        }

        @Test
        @DisplayName("reset should move cursor before first")
        void resetCursor() throws CDataGridException {
            rowSet.next();
            rowSet.next();
            rowSet.reset();
            assertTrue(rowSet.isBeforeFirst());
            assertEquals(0, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("beforeFirst should move cursor before first")
        void beforeFirstCursor() throws CDataGridException {
            rowSet.next();
            rowSet.beforeFirst();
            assertEquals(0, rowSet.getCursorPosition());
        }

        @Test
        @DisplayName("afterLast should move cursor after last")
        void afterLastCursor() {
            rowSet.afterLast();
            assertTrue(rowSet.isAfterLast());
        }
    }

    @Nested
    @DisplayName("Typed getters")
    class TypedGetters {

        @Test
        @DisplayName("getString by column name")
        void getStringByName() throws CDataGridException {
            rowSet.next();
            assertEquals("Alice", rowSet.getString("name"));
        }

        @Test
        @DisplayName("getString by column index")
        void getStringByIndex() throws CDataGridException {
            rowSet.next();
            assertEquals("Alice", rowSet.getString(1));
        }

        @Test
        @DisplayName("getInt by column name")
        void getIntByName() throws CDataGridException {
            rowSet.next();
            assertEquals(Integer.valueOf(1), rowSet.getInt("id"));
        }

        @Test
        @DisplayName("getDouble by column name")
        void getDoubleByName() throws CDataGridException {
            rowSet.next();
            assertEquals(Double.valueOf(95.5), rowSet.getDouble("score"));
        }

        @Test
        @DisplayName("getBoolean by column name")
        void getBooleanByName() throws CDataGridException {
            rowSet.next();
            assertEquals(Boolean.TRUE, rowSet.getBoolean("active"));
            rowSet.next();
            assertEquals(Boolean.FALSE, rowSet.getBoolean("active"));
        }

        @Test
        @DisplayName("getObject by column name")
        void getObjectByName() throws CDataGridException {
            rowSet.next();
            Object val = rowSet.getObject("name");
            assertEquals("Alice", val);
        }

        @Test
        @DisplayName("getObject by column index")
        void getObjectByIndex() throws CDataGridException {
            rowSet.next();
            assertEquals(1, rowSet.getObject(0));
        }

        @Test
        @DisplayName("should return null for null values")
        void nullValues() throws CDataGridException {
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{new CDataRow(new Object[]{10, null, null, null})});
            rs.next();
            assertNull(rs.getString("name"));
            assertNull(rs.getDouble("score"));
        }

        @Test
        @DisplayName("should throw when cursor not pointing at valid row")
        void throwWhenCursorInvalid() {
            assertThrows(CDataGridException.class, () -> rowSet.getString("name"));
        }

        @Test
        @DisplayName("should get current row")
        void getCurrentRow() throws CDataGridException {
            rowSet.next();
            CDataRow row = rowSet.getCurrentRow();
            assertNotNull(row);
            assertEquals(1, row.getValue(0));
        }
    }

    @Nested
    @DisplayName("Typed getters with date/time types")
    class DateTimeGetters {

        @Test
        @DisplayName("getDate and getTimestamp by column name")
        void getDateAndTimestamp() throws CDataGridException {
            Date now = new Date();
            Timestamp ts = new Timestamp(now.getTime());
            CRowMetaData dateMeta = new CRowMetaData(
                new String[]{"id", "created", "updated"},
                new Class[]{Integer.class, Date.class, Timestamp.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(dateMeta);
            rs.addData(new CDataRow[]{new CDataRow(new Object[]{1, now, ts})});
            rs.next();
            assertNotNull(rs.getDate("created"));
            assertNotNull(rs.getTimestamp("updated"));
        }
    }

    @Nested
    @DisplayName("Setter")
    class Setter {

        @Test
        @DisplayName("setValue by column name should update current row")
        void setValueByName() throws CDataGridException {
            rowSet.next();
            rowSet.setValue("name", "Alicia");
            assertEquals("Alicia", rowSet.getString("name"));
        }

        @Test
        @DisplayName("setValue by column index should update current row")
        void setValueByIndex() throws CDataGridException {
            rowSet.next();
            rowSet.setValue(1, "Modified");
            assertEquals("Modified", rowSet.getString(1));
        }
    }

    @Nested
    @DisplayName("Sorting")
    class Sorting {

        @Test
        @DisplayName("should sort by column ascending")
        void sortAscending() throws CDataGridException {
            rowSet.sortByColumn(new String[]{"score"}, true);
            rowSet.next();
            assertEquals("Bob", rowSet.getString("name"));
            rowSet.next();
            assertEquals("Charlie", rowSet.getString("name"));
            rowSet.next();
            assertEquals("Alice", rowSet.getString("name"));
        }

        @Test
        @DisplayName("should sort by column descending")
        void sortDescending() throws CDataGridException {
            rowSet.sortByColumn(new String[]{"score"}, false);
            rowSet.next();
            assertEquals("Alice", rowSet.getString("name"));
        }

        @Test
        @DisplayName("should sort by string column")
        void sortByString() throws CDataGridException {
            rowSet.sortByColumn(new String[]{"name"}, true);
            rowSet.next();
            assertEquals("Alice", rowSet.getString("name"));
            rowSet.next();
            assertEquals("Bob", rowSet.getString("name"));
            rowSet.next();
            assertEquals("Charlie", rowSet.getString("name"));
        }

        @Test
        @DisplayName("should throw when sorting with cursor not reset")
        void throwWhenCursorNotReset() throws CDataGridException {
            rowSet.next();
            assertThrows(CDataGridException.class, () ->
                rowSet.sortByColumn(new String[]{"score"}, true)
            );
        }
    }

    @Nested
    @DisplayName("Column values extraction")
    class ColumnValues {

        @Test
        @DisplayName("should extract all values from a column")
        void extractColumnValues() throws CDataGridException {
            Object[] names = rowSet.getColumnValues("name");
            assertEquals(3, names.length);
        }

        @Test
        @DisplayName("should return empty array from empty rowset")
        void emptyColumnValues() throws CDataGridException {
            CDataRowSet empty = new CDataRowSet(meta);
            Object[] vals = empty.getColumnValues("name");
            assertEquals(0, vals.length);
        }
    }

    @Nested
    @DisplayName("getAllRows")
    class GetAllRows {

        @Test
        @DisplayName("should return all row objects")
        void getAllRows() {
            CDataRow[] rows = rowSet.getAllRows();
            assertEquals(3, rows.length);
        }
    }

    @Test
    @DisplayName("toString should produce non-empty output")
    void toStringNotEmpty() {
        String str = rowSet.toString();
        assertNotNull(str);
        assertTrue(str.contains("ROWSET CONTENTS"));
    }
}
