package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CRowMetaData")
class CRowMetaDataTest {

    private CRowMetaData meta;

    @BeforeEach
    void setUp() throws CDataGridException {
        meta = new CRowMetaData(
            new String[]{"id", "name", "age", "score"},
            new Class[]{Integer.class, String.class, Integer.class, Double.class},
            new String[]{"id"}
        );
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should create metadata with valid inputs")
        void createValid() {
            assertEquals(4, meta.getNumberColumns());
            assertEquals(4, meta.getColumnCount());
        }

        @Test
        @DisplayName("should throw when column names are null")
        void throwOnNullNames() {
            assertThrows(CDataGridException.class, () ->
                new CRowMetaData(null, new Class[]{Integer.class}, new String[]{"id"})
            );
        }

        @Test
        @DisplayName("should throw when column types are null")
        void throwOnNullTypes() {
            assertThrows(CDataGridException.class, () ->
                new CRowMetaData(new String[]{"id"}, null, new String[]{"id"})
            );
        }

        @Test
        @DisplayName("should throw when names and types arrays differ in size")
        void throwOnSizeMismatch() {
            assertThrows(CDataGridException.class, () ->
                new CRowMetaData(
                    new String[]{"a", "b"},
                    new Class[]{Integer.class},
                    new String[]{"a"}
                )
            );
        }

        @Test
        @DisplayName("should throw when PK column does not exist")
        void throwOnInvalidPK() {
            assertThrows(CDataGridException.class, () ->
                new CRowMetaData(
                    new String[]{"a"},
                    new Class[]{Integer.class},
                    new String[]{"nonexistent"}
                )
            );
        }

        @Test
        @DisplayName("should allow null primary key columns")
        void allowNullPK() throws CDataGridException {
            CRowMetaData m = new CRowMetaData(
                new String[]{"a"}, new Class[]{Integer.class}, null
            );
            assertNull(m.getPrimaryKeyColumns());
        }
    }

    @Nested
    @DisplayName("Column lookup")
    class ColumnLookup {

        @Test
        @DisplayName("should return correct column index")
        void columnIndex() throws CDataGridException {
            assertEquals(0, meta.getColumnIndex("id"));
            assertEquals(1, meta.getColumnIndex("name"));
            assertEquals(2, meta.getColumnIndex("age"));
            assertEquals(3, meta.getColumnIndex("score"));
        }

        @Test
        @DisplayName("should throw when column name does not exist")
        void throwOnMissingColumn() {
            assertThrows(CDataGridException.class, () -> meta.getColumnIndex("missing"));
        }

        @Test
        @DisplayName("containsColumn should return true for existing columns")
        void containsExisting() {
            assertTrue(meta.containsColumn("id"));
            assertTrue(meta.containsColumn("name"));
        }

        @Test
        @DisplayName("containsColumn should return false for missing columns")
        void doesNotContainMissing() {
            assertFalse(meta.containsColumn("missing"));
            assertFalse(meta.containsColumn(null));
        }

        @Test
        @DisplayName("should return column names array")
        void getColumnNames() {
            String[] names = meta.getColumnNames();
            assertEquals(4, names.length);
            assertEquals("id", names[0]);
        }

        @Test
        @DisplayName("should return column types array")
        void getColumnTypes() {
            Class[] types = meta.getColumnTypes();
            assertEquals(4, types.length);
            assertEquals(Integer.class, types[0]);
            assertEquals(String.class, types[1]);
        }

        @Test
        @DisplayName("should return column type by name")
        void getColumnTypeByName() throws CDataGridException {
            assertEquals(Integer.class, meta.getColumnType("id"));
            assertEquals(String.class, meta.getColumnType("name"));
            assertEquals(Double.class, meta.getColumnType("score"));
        }

        @Test
        @DisplayName("should return column type by index")
        void getColumnTypeByIndex() throws CDataGridException {
            assertEquals(Integer.class, meta.getColumnTypeCls(0));
            assertEquals(String.class, meta.getColumnTypeCls(1));
        }

        @Test
        @DisplayName("should return column indices for array of names")
        void getColumnIndices() throws CDataGridException {
            int[] indices = meta.getColumnIndices(new String[]{"name", "score"});
            assertEquals(2, indices.length);
            assertEquals(1, indices[0]);
            assertEquals(3, indices[1]);
        }

        @Test
        @DisplayName("should return empty array for null column names")
        void emptyIndicesForNull() throws CDataGridException {
            int[] indices = meta.getColumnIndices(null);
            assertEquals(0, indices.length);
        }

        @Test
        @DisplayName("should return column types for array of indices")
        void getColumnTypesByIndices() throws CDataGridException {
            Class[] types = meta.getColumnTypes(new int[]{0, 3});
            assertEquals(2, types.length);
            assertEquals(Integer.class, types[0]);
            assertEquals(Double.class, types[1]);
        }

        @Test
        @DisplayName("should return empty array for null indices")
        void emptyTypesForNull() throws CDataGridException {
            Class[] types = meta.getColumnTypes(null);
            assertEquals(0, types.length);
        }
    }

    @Nested
    @DisplayName("Primary keys")
    class PrimaryKeys {

        @Test
        @DisplayName("should return primary key columns")
        void getPKColumns() {
            String[] pkCols = meta.getPrimaryKeyColumns();
            assertEquals(1, pkCols.length);
            assertEquals("id", pkCols[0]);
        }

        @Test
        @DisplayName("should return primary key column indices")
        void getPKIndices() throws CDataGridException {
            int[] pkIndices = meta.getPrimaryKeyColumnIndices();
            assertEquals(1, pkIndices.length);
            assertEquals(0, pkIndices[0]);
        }

        @Test
        @DisplayName("should create single-column primary key")
        void createSinglePK() throws CDataGridException {
            CDataRow row = new CDataRow(new Object[]{42, "test", 25, 90.0});
            Object pk = meta.createPrimaryKey(row);
            assertEquals(42, pk);
        }

        @Test
        @DisplayName("should create composite primary key")
        void createCompositePK() throws CDataGridException {
            CRowMetaData compositeMeta = new CRowMetaData(
                new String[]{"first", "last", "age"},
                new Class[]{String.class, String.class, Integer.class},
                new String[]{"first", "last"}
            );
            CDataRow row = new CDataRow(new Object[]{"John", "Doe", 30});
            Object pk = compositeMeta.createPrimaryKey(row);
            assertEquals("John" + CRowMetaData.COMPOSITE_KEY_DELIMITER + "Doe", pk.toString());
        }

        @Test
        @DisplayName("should throw when creating PK from null row")
        void throwOnNullRow() {
            assertThrows(CDataGridException.class, () -> meta.createPrimaryKey(null));
        }
    }

    @Nested
    @DisplayName("addColumns")
    class AddColumns {

        @Test
        @DisplayName("should add new columns to metadata")
        void addNewColumns() throws CDataGridException {
            meta.addColumns(
                new String[]{"email", "phone"},
                new Class[]{String.class, String.class}
            );
            assertEquals(6, meta.getNumberColumns());
            assertTrue(meta.containsColumn("email"));
            assertTrue(meta.containsColumn("phone"));
            assertEquals(4, meta.getColumnIndex("email"));
            assertEquals(5, meta.getColumnIndex("phone"));
        }

        @Test
        @DisplayName("should throw when adding existing column name")
        void throwOnDuplicate() {
            assertThrows(CDataGridException.class, () ->
                meta.addColumns(new String[]{"name"}, new Class[]{String.class})
            );
        }

        @Test
        @DisplayName("should throw when names and types mismatch")
        void throwOnMismatch() {
            assertThrows(CDataGridException.class, () ->
                meta.addColumns(
                    new String[]{"a", "b"},
                    new Class[]{Integer.class}
                )
            );
        }

        @Test
        @DisplayName("should throw when arguments are null")
        void throwOnNull() {
            assertThrows(CDataGridException.class, () ->
                meta.addColumns(null, null)
            );
        }
    }

    @Nested
    @DisplayName("equals")
    class Equals {

        @Test
        @DisplayName("should be equal to identical metadata")
        void equalToIdentical() throws CDataGridException {
            CRowMetaData other = new CRowMetaData(
                new String[]{"id", "name", "age", "score"},
                new Class[]{Integer.class, String.class, Integer.class, Double.class},
                new String[]{"id"}
            );
            assertTrue(meta.equals(other));
        }

        @Test
        @DisplayName("should not be equal when column names differ")
        void notEqualDifferentNames() throws CDataGridException {
            CRowMetaData other = new CRowMetaData(
                new String[]{"id", "label", "age", "score"},
                new Class[]{Integer.class, String.class, Integer.class, Double.class},
                new String[]{"id"}
            );
            assertFalse(meta.equals(other));
        }

        @Test
        @DisplayName("should not be equal when column types differ")
        void notEqualDifferentTypes() throws CDataGridException {
            CRowMetaData other = new CRowMetaData(
                new String[]{"id", "name", "age", "score"},
                new Class[]{Integer.class, String.class, String.class, Double.class},
                new String[]{"id"}
            );
            assertFalse(meta.equals(other));
        }

        @Test
        @DisplayName("should not be equal when PK differs")
        void notEqualDifferentPK() throws CDataGridException {
            CRowMetaData other = new CRowMetaData(
                new String[]{"id", "name", "age", "score"},
                new Class[]{Integer.class, String.class, Integer.class, Double.class},
                new String[]{"name"}
            );
            assertFalse(meta.equals(other));
        }

        @Test
        @DisplayName("should not be equal when column count differs")
        void notEqualDifferentCount() throws CDataGridException {
            CRowMetaData other = new CRowMetaData(
                new String[]{"id", "name"},
                new Class[]{Integer.class, String.class},
                new String[]{"id"}
            );
            assertFalse(meta.equals(other));
        }

        @Test
        @DisplayName("should not be equal to null")
        void notEqualToNull() {
            assertFalse(meta.equals(null));
        }

        @Test
        @DisplayName("should not be equal to different type")
        void notEqualToDifferentType() {
            assertFalse(meta.equals("not metadata"));
        }
    }

    @Nested
    @DisplayName("clone")
    class Clone {

        @Test
        @DisplayName("should create independent copy")
        void independentCopy() throws Exception {
            CRowMetaData cloned = (CRowMetaData) meta.clone();
            assertNotSame(meta, cloned);
            assertTrue(meta.equals(cloned));
            assertEquals(meta.getNumberColumns(), cloned.getNumberColumns());
            assertEquals(meta.getColumnNames().length, cloned.getColumnNames().length);
        }

        @Test
        @DisplayName("clone should not share arrays")
        void cloneDoesNotShareArrays() throws Exception {
            CRowMetaData cloned = (CRowMetaData) meta.clone();
            assertNotSame(meta.getColumnNames(), cloned.getColumnNames());
            assertNotSame(meta.getColumnTypes(), cloned.getColumnTypes());
        }
    }

    @Nested
    @DisplayName("ResultSetMetaData interface")
    class ResultSetMetaDataInterface {

        @Test
        @DisplayName("getColumnLabel returns column name (1-based)")
        void getColumnLabel() {
            assertEquals("id", meta.getColumnLabel(1));
            assertEquals("name", meta.getColumnLabel(2));
        }

        @Test
        @DisplayName("getColumnName returns column name (1-based)")
        void getColumnName() {
            assertEquals("id", meta.getColumnName(1));
        }

        @Test
        @DisplayName("getColumnCount returns number of columns")
        void getColumnCount() {
            assertEquals(4, meta.getColumnCount());
        }

        @Test
        @DisplayName("getColumnClassName returns class name")
        void getColumnClassName() {
            assertEquals("java.lang.Integer", meta.getColumnClassName(1));
            assertEquals("java.lang.String", meta.getColumnClassName(2));
        }
    }

    @Test
    @DisplayName("toString should contain column info")
    void toStringContainsInfo() {
        String str = meta.toString();
        assertNotNull(str);
        assertTrue(str.contains("METADATA DEFINITION"));
        assertTrue(str.contains("id"));
        assertTrue(str.contains("name"));
    }
}
