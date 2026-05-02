package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.casper.data.model.filters.CDataFilterClause;
import net.casper.data.model.filters.EqualsFilter;

@DisplayName("CDataCacheContainer")
class CDataCacheContainerTest {

    private CDataCacheContainer container;

    @BeforeEach
    void setUp() throws CDataGridException {
        CRowMetaData meta = new CRowMetaData(
            new String[]{"id", "name", "age"},
            new Class[]{Integer.class, String.class, Integer.class},
            new String[]{"id"}
        );
        container = new CDataCacheContainer("testCache", meta);
        container.addSingleRow(new Object[]{1, "Alice", 30});
        container.addSingleRow(new Object[]{2, "Bob", 25});
        container.addSingleRow(new Object[]{3, "Charlie", 35});
    }

    @Nested
    @DisplayName("Construction")
    class Construction {

        @Test
        @DisplayName("should create container with name and metadata")
        void createWithNameAndMetadata() {
            assertEquals("testCache", container.getCacheName());
            assertEquals(3, container.size());
            assertEquals(3, container.getNumberRows());
        }

        @Test
        @DisplayName("should create container using convenience constructor")
        void createWithConvenienceConstructor() throws CDataGridException {
            CDataCacheContainer c = new CDataCacheContainer(
                "test", "col1,col2",
                new Class[]{String.class, Integer.class},
                "col1"
            );
            assertEquals("test", c.getCacheName());
            assertEquals(0, c.size());
            assertNotNull(c.getMetaDefinition());
            assertEquals(2, c.getMetaDefinition().getNumberColumns());
        }

        @Test
        @DisplayName("should throw when column names are null in convenience constructor")
        void throwOnNullColumnNames() {
            assertThrows(CDataGridException.class, () ->
                new CDataCacheContainer("test", null,
                    new Class[]{String.class}, "col1")
            );
        }

        @Test
        @DisplayName("should throw when metadata is null")
        void throwOnNullMetadata() {
            assertThrows(CDataGridException.class, () ->
                new CDataCacheContainer("test", null)
            );
        }

        @Test
        @DisplayName("should throw when data map is null")
        void throwOnNullDataMap() {
            assertThrows(CDataGridException.class, () -> {
                CRowMetaData meta = new CRowMetaData(
                    new String[]{"id"}, new Class[]{Integer.class}, new String[]{"id"});
                new CDataCacheContainer("test", meta, null);
            });
        }

        @Test
        @DisplayName("should accept custom map implementation")
        void acceptCustomMapImpl() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "val"},
                new Class[]{Integer.class, String.class},
                new String[]{"id"}
            );
            CDataCacheContainer c = new CDataCacheContainer("test", meta, new LinkedHashMap());
            c.addSingleRow(new Object[]{1, "A"});
            assertEquals(1, c.size());
        }
    }

    @Nested
    @DisplayName("Adding data")
    class AddingData {

        @Test
        @DisplayName("should add single rows via addSingleRow")
        void addSingleRow() throws CDataGridException {
            container.addSingleRow(new Object[]{4, "Dave", 40});
            assertEquals(4, container.size());
        }

        @Test
        @DisplayName("should add array of CDataRow objects")
        void addDataRowArray() throws CDataGridException {
            CDataRow[] rows = {
                new CDataRow(new Object[]{4, "Dave", 40}),
                new CDataRow(new Object[]{5, "Eve", 28})
            };
            int added = container.addData(rows);
            assertEquals(2, added);
            assertEquals(5, container.size());
        }

        @Test
        @DisplayName("should overwrite row with duplicate primary key")
        void overwriteDuplicatePK() throws CDataGridException {
            container.addSingleRow(new Object[]{1, "Alicia", 31});
            assertEquals(3, container.size());

            CDataRowSet rs = container.get("id", new Object[]{1});
            assertTrue(rs.next());
            assertEquals("Alicia", rs.getString("name"));
        }

        @Test
        @DisplayName("should reject row with wrong cardinality")
        void rejectWrongCardinality() {
            assertThrows(CDataGridException.class, () ->
                container.addSingleRow(new Object[]{4, "Dave"})
            );
        }

        @Test
        @DisplayName("should return 0 when adding null or empty array")
        void addNullOrEmpty() throws CDataGridException {
            assertEquals(0, container.addData((CDataRow[]) null));
            assertEquals(0, container.addData(new CDataRow[0]));
        }
    }

    @Nested
    @DisplayName("Removing data")
    class RemovingData {

        @Test
        @DisplayName("should remove rows by column value")
        void removeByColumnValue() throws CDataGridException {
            int removed = container.removeData("name", new Object[]{"Bob"}, true);
            assertEquals(1, removed);
            assertEquals(2, container.size());
        }

        @Test
        @DisplayName("should remove all rows")
        void removeAll() throws CDataGridException {
            int removed = container.removeAll();
            assertEquals(3, removed);
            assertEquals(0, container.size());
        }

        @Test
        @DisplayName("should return 0 when removing null values")
        void removeNullValues() throws CDataGridException {
            assertEquals(0, container.removeData("name", null, true));
            assertEquals(0, container.removeData("name", new Object[0], true));
        }

        @Test
        @DisplayName("should remove rows matching filter clause")
        void removeByFilterClause() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("age", new Object[]{25}));
            int removed = container.removeData(clause, true);
            assertEquals(1, removed);
            assertEquals(2, container.size());
        }

        @Test
        @DisplayName("should return 0 when removing with null or empty filter clause")
        void removeWithEmptyClause() throws CDataGridException {
            assertEquals(0, container.removeData((CDataFilterClause) null, true));
            assertEquals(0, container.removeData(new CDataFilterClause(), true));
        }
    }

    @Nested
    @DisplayName("Querying data")
    class QueryingData {

        @Test
        @DisplayName("should get all rows")
        void getAll() throws CDataGridException {
            CDataRowSet rs = container.getAll();
            assertEquals(3, rs.getNumberRows());
        }

        @Test
        @DisplayName("should get rows by column and values")
        void getByColumnValues() throws CDataGridException {
            CDataRowSet rs = container.get("name", new Object[]{"Alice", "Charlie"});
            assertEquals(2, rs.getNumberRows());
        }

        @Test
        @DisplayName("should get all rows with sorting")
        void getAllSorted() throws CDataGridException {
            CDataRowSet rs = container.getAll(new String[]{"age"}, true);
            assertEquals(3, rs.getNumberRows());
            assertTrue(rs.next());
            assertEquals(25, rs.getInt("age"));
            assertTrue(rs.next());
            assertEquals(30, rs.getInt("age"));
            assertTrue(rs.next());
            assertEquals(35, rs.getInt("age"));
        }

        @Test
        @DisplayName("should get all rows with descending sort")
        void getAllDescending() throws CDataGridException {
            CDataRowSet rs = container.getAll(new String[]{"age"}, false);
            assertTrue(rs.next());
            assertEquals(35, rs.getInt("age"));
        }

        @Test
        @DisplayName("should return all rows using filter clause")
        void getByFilterClause() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Bob"}));
            CDataRowSet rs = container.get(clause);
            assertEquals(1, rs.getNumberRows());
            assertTrue(rs.next());
            assertEquals("Bob", rs.getString("name"));
        }

        @Test
        @DisplayName("should get all raw rows via getAllRows")
        void getAllRawRows() {
            CDataRow[] rows = container.getAllRows();
            assertEquals(3, rows.length);
        }
    }

    @Nested
    @DisplayName("Primary keys")
    class PrimaryKeys {

        @Test
        @DisplayName("should handle composite primary keys")
        void compositePrimaryKey() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"first", "last", "age"},
                new Class[]{String.class, String.class, Integer.class},
                new String[]{"first", "last"}
            );
            CDataCacheContainer c = new CDataCacheContainer("composite", meta);
            c.addSingleRow(new Object[]{"John", "Doe", 30});
            c.addSingleRow(new Object[]{"Jane", "Doe", 28});
            c.addSingleRow(new Object[]{"John", "Smith", 45});
            assertEquals(3, c.size());

            // Same composite key overwrites
            c.addSingleRow(new Object[]{"John", "Doe", 31});
            assertEquals(3, c.size());
        }

        @Test
        @DisplayName("should handle null primary key with identity PK")
        void nullPrimaryKeyUsesIdentity() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"name", "value"},
                new Class[]{String.class, Integer.class},
                null
            );
            CDataCacheContainer c = new CDataCacheContainer("noPK", meta);
            c.addSingleRow(new Object[]{"a", 1});
            c.addSingleRow(new Object[]{"a", 1});
            // With identity PK, duplicate rows are kept
            assertEquals(2, c.size());
        }

        @Test
        @DisplayName("should get primary key matches")
        void getPrimaryKeyMatches() throws CDataGridException {
            CDataRow[] matches = container.getPrimaryKeyMatches(new Object[]{1, 3});
            assertEquals(2, matches.length);
        }

        @Test
        @DisplayName("should skip null values in primary key matches")
        void skipNullInPrimaryKeyMatches() {
            CDataRow[] matches = container.getPrimaryKeyMatches(new Object[]{null, 2});
            assertEquals(1, matches.length);
        }
    }

    @Nested
    @DisplayName("Insertion-ordered container")
    class InsertionOrdered {

        @Test
        @DisplayName("should maintain insertion order")
        void maintainOrder() throws CDataGridException {
            CDataCacheContainer c = CDataCacheContainer.newInsertionOrdered(
                "ordered", "name,value",
                new Class[]{String.class, Integer.class}
            );
            c.addSingleRow(new Object[]{"C", 3});
            c.addSingleRow(new Object[]{"A", 1});
            c.addSingleRow(new Object[]{"B", 2});
            assertEquals(3, c.size());

            // getAllRows should respect insertion order (LinkedHashMap)
            CDataRow[] rows = c.getAllRows();
            assertEquals(3, rows.length);
        }
    }

    @Nested
    @DisplayName("Column operations")
    class ColumnOperations {

        @Test
        @DisplayName("should clear a column")
        void clearColumn() throws CDataGridException {
            container.clearColumn("age");
            CDataRowSet rs = container.getAll();
            while (rs.next()) {
                assertNull(rs.getObject("age"));
            }
        }

        @Test
        @DisplayName("should set column value for all rows")
        void setColumnValue() throws CDataGridException {
            container.setColumnValue("age", 99);
            CDataRowSet rs = container.getAll();
            while (rs.next()) {
                assertEquals(99, rs.getInt("age"));
            }
        }

        @Test
        @DisplayName("should throw when clearing non-existent column")
        void clearNonExistentColumn() {
            assertThrows(CDataGridException.class, () ->
                container.clearColumn("nonexistent")
            );
        }

        @Test
        @DisplayName("should throw when column name is null or empty")
        void setColumnValueNullName() {
            assertThrows(CDataGridException.class, () ->
                container.setColumnValue(null, 1)
            );
            assertThrows(CDataGridException.class, () ->
                container.setColumnValue("  ", 1)
            );
        }
    }

    @Nested
    @DisplayName("Merging containers")
    class MergingContainers {

        @Test
        @DisplayName("should merge data from another container with same meta")
        void addDataFromContainer() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "name", "age"},
                new Class[]{Integer.class, String.class, Integer.class},
                new String[]{"id"}
            );
            CDataCacheContainer other = new CDataCacheContainer("other", meta);
            other.addSingleRow(new Object[]{4, "Dave", 40});
            other.addSingleRow(new Object[]{5, "Eve", 28});

            int added = container.addData(other);
            assertEquals(2, added);
            assertEquals(5, container.size());
        }

        @Test
        @DisplayName("should throw when merging containers with different meta")
        void throwOnDifferentMeta() throws CDataGridException {
            CRowMetaData differentMeta = new CRowMetaData(
                new String[]{"code", "desc"},
                new Class[]{String.class, String.class},
                new String[]{"code"}
            );
            CDataCacheContainer other = new CDataCacheContainer("other", differentMeta);
            other.addSingleRow(new Object[]{"A", "Desc"});

            assertThrows(CDataGridException.class, () -> container.addData(other));
        }

        @Test
        @DisplayName("should merge values from source into destination by join columns")
        void mergeByJoinColumns() throws CDataGridException {
            // Destination already has id=1,2,3
            // Source has overlapping column "age" with different values
            CRowMetaData sourceMeta = new CRowMetaData(
                new String[]{"id", "age"},
                new Class[]{Integer.class, Integer.class},
                new String[]{"id"}
            );
            CDataCacheContainer source = new CDataCacheContainer("source", sourceMeta);
            source.addSingleRow(new Object[]{1, 99});
            source.addSingleRow(new Object[]{2, 88});

            int merged = container.merge(source, new String[]{"id"});
            assertEquals(2, merged);

            CDataRowSet rs = container.get("id", new Object[]{1});
            assertTrue(rs.next());
            assertEquals(99, rs.getInt("age"));
        }

        @Test
        @DisplayName("should throw when merging null container")
        void throwOnNullMergeContainer() {
            assertThrows(CDataGridException.class, () ->
                container.merge((CDataCacheContainer) null, new String[]{"id"})
            );
        }

        @Test
        @DisplayName("should throw when merge join columns are null")
        void throwOnNullJoinColumns() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id"}, new Class[]{Integer.class}, new String[]{"id"});
            CDataCacheContainer source = new CDataCacheContainer("src", meta);
            assertThrows(CDataGridException.class, () ->
                container.merge(source, null)
            );
        }
    }

    @Nested
    @DisplayName("Building from CBuilder")
    class BuildingFromCBuilder {

        @Test
        @DisplayName("should build container from CBuilder")
        void buildFromBuilder() throws CDataGridException {
            CBuilder builder = new CBuilder() {
                private int rowIndex = 0;
                private final Object[][] data = {
                    {1, "X"}, {2, "Y"}, {3, "Z"}
                };
                public String getName() { return "builtCache"; }
                public String[] getColumnNames() { return new String[]{"id", "label"}; }
                public Class[] getColumnTypes() { return new Class[]{Integer.class, String.class}; }
                public String[] getPrimaryKeyColumns() { return new String[]{"id"}; }
                public Map getConcreteMap() { return new HashMap(); }
                public void open() {}
                public Object[] readRow() {
                    if (rowIndex < data.length) return data[rowIndex++];
                    return null;
                }
                public void close() {}
            };

            CDataCacheContainer c = new CDataCacheContainer(builder);
            assertEquals("builtCache", c.getCacheName());
            assertEquals(3, c.size());
        }

        @Test
        @DisplayName("should handle builder that throws IOException")
        void builderThrowsIOException() {
            CBuilder builder = new CBuilder() {
                public String getName() { return "fail"; }
                public String[] getColumnNames() { return new String[]{"id"}; }
                public Class[] getColumnTypes() { return new Class[]{Integer.class}; }
                public String[] getPrimaryKeyColumns() { return new String[]{"id"}; }
                public Map getConcreteMap() { return new HashMap(); }
                public void open() throws IOException { throw new IOException("open failed"); }
                public Object[] readRow() { return null; }
                public void close() {}
            };

            assertThrows(CDataGridException.class, () -> new CDataCacheContainer(builder));
        }
    }

    @Nested
    @DisplayName("Indexing")
    class Indexing {

        @Test
        @DisplayName("should add non-unique index")
        void addNonUniqueIndex() throws CDataGridException {
            container.addNonUniqueIndex("name");
            String[] indexCols = container.getIndexColumnNames();
            assertEquals(1, indexCols.length);
            assertEquals("name", indexCols[0]);
        }

        @Test
        @DisplayName("should throw when adding index on invalid column")
        void throwOnInvalidIndexColumn() {
            assertThrows(CDataGridException.class, () ->
                container.addNonUniqueIndex("nonexistent")
            );
        }

        @Test
        @DisplayName("should return null for non-existent cache index")
        void returnNullForNonExistentIndex() {
            assertNull(container.getCacheIndexByColumnName("nonexistent"));
        }

        @Test
        @DisplayName("should return empty array when no indices configured")
        void emptyIndexColumns() {
            String[] indexCols = container.getIndexColumnNames();
            assertEquals(0, indexCols.length);
        }
    }

    @Test
    @DisplayName("should produce non-empty toString")
    void toStringNotEmpty() {
        String str = container.toString();
        assertNotNull(str);
        assertTrue(str.contains("testCache"));
    }

    @Test
    @DisplayName("should return metadata")
    void getMetaDefinition() {
        CRowMetaData meta = container.getMetaDefinition();
        assertNotNull(meta);
        assertEquals(3, meta.getNumberColumns());
    }
}
