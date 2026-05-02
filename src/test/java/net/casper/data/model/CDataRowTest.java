package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CDataRow")
class CDataRowTest {

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should create empty row with default constructor")
        void emptyRow() {
            CDataRow row = new CDataRow();
            assertEquals(0, row.getNumberColumns());
        }

        @Test
        @DisplayName("should create row with specified number of columns")
        void rowWithColumnCount() throws CDataGridException {
            CDataRow row = new CDataRow(5);
            assertEquals(5, row.getNumberColumns());
            for (int i = 0; i < 5; i++) {
                assertNull(row.getValue(i));
            }
        }

        @Test
        @DisplayName("should throw when column count is less than 1")
        void throwOnZeroColumns() {
            assertThrows(CDataGridException.class, () -> new CDataRow(0));
            assertThrows(CDataGridException.class, () -> new CDataRow(-1));
        }

        @Test
        @DisplayName("should create row from Object array")
        void rowFromArray() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"hello", 42, true});
            assertEquals(3, row.getNumberColumns());
            assertEquals("hello", row.getValue(0));
            assertEquals(42, row.getValue(1));
            assertEquals(true, row.getValue(2));
        }

        @Test
        @DisplayName("should throw when Object array is null")
        void throwOnNullArray() {
            assertThrows(CDataGridException.class, () -> new CDataRow((Object[]) null));
        }
    }

    @Nested
    @DisplayName("getValue and setValue")
    class GetSetValue {

        @Test
        @DisplayName("should get and set values by index")
        void getAndSetByIndex() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a", "b", "c"});
            assertEquals("b", row.getValue(1));
            row.setValue(1, "modified");
            assertEquals("modified", row.getValue(1));
        }

        @Test
        @DisplayName("should set value to null")
        void setNull() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a", "b"});
            row.setValue(0, null);
            assertNull(row.getValue(0));
        }

        @Test
        @DisplayName("should throw on out-of-bounds get")
        void throwOnGetOutOfBounds() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a"});
            assertThrows(CDataGridException.class, () -> row.getValue(1));
            assertThrows(CDataGridException.class, () -> row.getValue(-1));
        }

        @Test
        @DisplayName("should throw on out-of-bounds set")
        void throwOnSetOutOfBounds() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a"});
            assertThrows(CDataGridException.class, () -> row.setValue(1, "x"));
            assertThrows(CDataGridException.class, () -> row.setValue(-1, "x"));
        }
    }

    @Nested
    @DisplayName("ensureCardinality")
    class EnsureCardinality {

        @Test
        @DisplayName("should expand row to larger size")
        void expandRow() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a", "b"});
            row.ensureCardinality(5);
            assertEquals(5, row.getNumberColumns());
            assertEquals("a", row.getValue(0));
            assertEquals("b", row.getValue(1));
            assertNull(row.getValue(2));
            assertNull(row.getValue(3));
            assertNull(row.getValue(4));
        }

        @Test
        @DisplayName("should not shrink row")
        void noShrink() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a", "b", "c"});
            row.ensureCardinality(2);
            assertEquals(3, row.getNumberColumns());
        }

        @Test
        @DisplayName("should ignore size less than 1")
        void ignoreInvalidSize() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a"});
            row.ensureCardinality(0);
            row.ensureCardinality(-1);
            assertEquals(1, row.getNumberColumns());
        }

        @Test
        @DisplayName("should do nothing when size equals current")
        void noChangeForEqual() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a", "b"});
            row.ensureCardinality(2);
            assertEquals(2, row.getNumberColumns());
        }
    }

    @Nested
    @DisplayName("Raw data")
    class RawData {

        @Test
        @DisplayName("getRawData should return underlying array")
        void getRawData() throws CDataGridException {
            Object[] data = {"x", 1, 3.14};
            CDataRow row = new CDataRow(data);
            assertSame(data, row.getRawData());
        }

        @Test
        @DisplayName("setRawData should replace underlying array")
        void setRawData() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"old"});
            Object[] newData = {"new1", "new2"};
            row.setRawData(newData);
            assertEquals(2, row.getNumberColumns());
            assertEquals("new1", row.getValue(0));
        }

        @Test
        @DisplayName("setRawData should throw on null")
        void setRawDataNull() {
            CDataRow row = new CDataRow();
            assertThrows(CDataGridException.class, () -> row.setRawData(null));
        }
    }

    @Nested
    @DisplayName("toMap")
    class ToMap {

        @Test
        @DisplayName("should convert row to map using metadata")
        void convertToMap() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"name", "age"},
                new Class[]{String.class, Integer.class},
                new String[]{"name"}
            );
            CDataRow row = new CDataRow(new Object[]{"Alice", 30});
            Map map = row.toMap(meta);
            assertEquals("Alice", map.get("name"));
            assertEquals(30, map.get("age"));
        }

        @Test
        @DisplayName("should return empty map when metadata is null")
        void emptyMapOnNullMeta() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{"a"});
            Map map = row.toMap(null);
            assertTrue(map.isEmpty());
        }
    }

    @Test
    @DisplayName("toString should contain tab-separated values")
    void toStringFormat() throws CDataGridException {
        CDataRow row = new CDataRow(new Object[]{"Alice", 30});
        String str = row.toString();
        assertTrue(str.contains("Alice"));
        assertTrue(str.contains("30"));
        assertTrue(str.contains("\t"));
    }

    @Test
    @DisplayName("toString should handle null values")
    void toStringWithNulls() throws CDataGridException {
        CDataRow row = new CDataRow(new Object[]{null, "hello", null});
        String str = row.toString();
        assertNotNull(str);
        assertTrue(str.contains("hello"));
    }
}
