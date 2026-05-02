package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import net.casper.data.model.filters.*;

@DisplayName("Filters")
class FiltersTest {

    private CDataCacheContainer container;

    @BeforeEach
    void setUp() throws CDataGridException {
        container = new CDataCacheContainer(
            "testData", "id,name,age,score",
            new Class[]{Integer.class, String.class, Integer.class, Double.class},
            "id"
        );
        container.addSingleRow(new Object[]{1, "Alice", 30, 95.5});
        container.addSingleRow(new Object[]{2, "Bob", 25, 82.0});
        container.addSingleRow(new Object[]{3, "Charlie", 35, 88.5});
        container.addSingleRow(new Object[]{4, "Diana", 28, 91.0});
        container.addSingleRow(new Object[]{5, "Eve", 32, 76.0});
    }

    @Nested
    @DisplayName("EqualsFilter")
    class EqualsFilterTests {

        @Test
        @DisplayName("should match single value")
        void matchSingleValue() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Alice"}));
            CDataRowSet rs = container.get(clause);
            assertEquals(1, rs.getNumberRows());
            rs.next();
            assertEquals("Alice", rs.getString("name"));
        }

        @Test
        @DisplayName("should match multiple values (OR semantics)")
        void matchMultipleValues() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Alice", "Bob", "Eve"}));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows());
        }

        @Test
        @DisplayName("should match by primary key column")
        void matchByPK() throws CDataGridException {
            CDataRowSet rs = container.get("id", new Object[]{2});
            assertEquals(1, rs.getNumberRows());
            rs.next();
            assertEquals("Bob", rs.getString("name"));
        }

        @Test
        @DisplayName("should return empty when no match")
        void noMatch() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Nobody"}));
            CDataRowSet rs = container.get(clause);
            assertEquals(0, rs.getNumberRows());
        }

        @Test
        @DisplayName("negated filter should return non-matching rows")
        void negatedFilter() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Alice"}, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(4, rs.getNumberRows());
        }

        @Test
        @DisplayName("should match null values in data")
        void matchNullValues() throws CDataGridException {
            container.addSingleRow(new Object[]{6, null, 20, 60.0});
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{null}));
            CDataRowSet rs = container.get(clause);
            assertEquals(1, rs.getNumberRows());
        }

        @Test
        @DisplayName("should throw on null column name")
        void throwOnNullColumnName() {
            assertThrows(CDataGridException.class, () ->
                new EqualsFilter(null, new Object[]{"x"})
            );
        }

        @Test
        @DisplayName("should throw on null match values")
        void throwOnNullMatchValues() {
            assertThrows(CDataGridException.class, () ->
                new EqualsFilter("name", null)
            );
        }

        @Test
        @DisplayName("should throw on empty match values")
        void throwOnEmptyMatchValues() {
            assertThrows(CDataGridException.class, () ->
                new EqualsFilter("name", new Object[0])
            );
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            EqualsFilter f = new EqualsFilter("name", new Object[]{"Alice", "Bob"});
            String str = f.toString();
            assertTrue(str.contains("EqualsFilter"));
            assertTrue(str.contains("name"));
        }
    }

    @Nested
    @DisplayName("RangeFilter")
    class RangeFilterTests {

        @Test
        @DisplayName("inclusive range should match boundary values")
        void inclusiveRange() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RangeFilter("age", 25, 30, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows()); // Bob(25), Diana(28), Alice(30)
        }

        @Test
        @DisplayName("exclusive range should exclude boundary values")
        void exclusiveRange() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RangeFilter("age", 25, 35, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows()); // Alice(30), Diana(28), Eve(32)
        }

        @Test
        @DisplayName("should match score range with doubles")
        void doubleRange() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RangeFilter("score", 80.0, 92.0, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows()); // Bob(82), Charlie(88.5), Diana(91)
        }

        @Test
        @DisplayName("should throw when lbound >= ubound")
        void throwOnInvalidBounds() {
            assertThrows(CDataGridException.class, () ->
                new RangeFilter("age", 30, 30, true)
            );
            assertThrows(CDataGridException.class, () ->
                new RangeFilter("age", 35, 25, true)
            );
        }

        @Test
        @DisplayName("should not match null values")
        void noMatchOnNull() throws CDataGridException {
            container.addSingleRow(new Object[]{6, "Frank", null, 70.0});
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RangeFilter("age", 20, 40, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(5, rs.getNumberRows()); // Frank excluded
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            RangeFilter f = new RangeFilter("age", 10, 50, true);
            String str = f.toString();
            assertTrue(str.contains("RangeFilter"));
            assertTrue(str.contains("age"));
            assertTrue(str.contains("inclusive"));
        }
    }

    @Nested
    @DisplayName("RegexFilter")
    class RegexFilterTests {

        @Test
        @DisplayName("should match regex pattern")
        void matchRegex() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{"A.*"}, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(1, rs.getNumberRows());
            rs.next();
            assertEquals("Alice", rs.getString("name"));
        }

        @Test
        @DisplayName("should match multiple regex patterns (OR)")
        void matchMultiplePatterns() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{"A.*", "B.*"}, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(2, rs.getNumberRows());
        }

        @Test
        @DisplayName("case-insensitive match")
        void caseInsensitive() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{"alice"}, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(1, rs.getNumberRows());
        }

        @Test
        @DisplayName("case-sensitive should not match wrong case")
        void caseSensitiveNoMatch() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{"alice"}, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(0, rs.getNumberRows());
        }

        @Test
        @DisplayName("should match partial patterns with .* prefix")
        void partialPattern() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{".*li.*"}, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(2, rs.getNumberRows()); // Alice, Charlie
        }

        @Test
        @DisplayName("should not match null values")
        void noMatchOnNull() throws CDataGridException {
            container.addSingleRow(new Object[]{6, null, 20, 60.0});
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new RegexFilter("name", new String[]{".*"}, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(5, rs.getNumberRows()); // null excluded
        }

        @Test
        @DisplayName("should throw on null regex array")
        void throwOnNullRegex() {
            assertThrows(CDataGridException.class, () ->
                new RegexFilter("name", null, false)
            );
        }

        @Test
        @DisplayName("should throw on empty regex array")
        void throwOnEmptyRegex() {
            assertThrows(CDataGridException.class, () ->
                new RegexFilter("name", new String[0], false)
            );
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            RegexFilter f = new RegexFilter("name", new String[]{"A.*"}, false);
            String str = f.toString();
            assertTrue(str.contains("RegexFilter"));
            assertTrue(str.contains("name"));
        }
    }

    @Nested
    @DisplayName("GEFilter (greater-than)")
    class GEFilterTests {

        @Test
        @DisplayName("inclusive GE should match values >= bound")
        void inclusiveGE() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new GEFilter("age", 30, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows()); // Alice(30), Charlie(35), Eve(32)
        }

        @Test
        @DisplayName("exclusive GE should match values > bound")
        void exclusiveGE() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new GEFilter("age", 30, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(2, rs.getNumberRows()); // Charlie(35), Eve(32)
        }

        @Test
        @DisplayName("should not match null values")
        void noMatchOnNull() throws CDataGridException {
            container.addSingleRow(new Object[]{6, "Frank", null, 70.0});
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new GEFilter("age", 0, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(5, rs.getNumberRows());
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            GEFilter f = new GEFilter("age", 10, true);
            String str = f.toString();
            assertTrue(str.contains("GEFilter"));
            assertTrue(str.contains("age"));
        }
    }

    @Nested
    @DisplayName("LEFilter (less-than)")
    class LEFilterTests {

        @Test
        @DisplayName("inclusive LE should match values <= bound")
        void inclusiveLE() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new LEFilter("age", 30, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(3, rs.getNumberRows()); // Bob(25), Diana(28), Alice(30)
        }

        @Test
        @DisplayName("exclusive LE should match values < bound")
        void exclusiveLE() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new LEFilter("age", 30, false));
            CDataRowSet rs = container.get(clause);
            assertEquals(2, rs.getNumberRows()); // Bob(25), Diana(28)
        }

        @Test
        @DisplayName("should not match null values")
        void noMatchOnNull() throws CDataGridException {
            container.addSingleRow(new Object[]{6, "Frank", null, 70.0});
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new LEFilter("age", 100, true));
            CDataRowSet rs = container.get(clause);
            assertEquals(5, rs.getNumberRows());
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            LEFilter f = new LEFilter("age", 50, false);
            String str = f.toString();
            assertTrue(str.contains("LEFilter"));
            assertTrue(str.contains("age"));
        }
    }

    @Nested
    @DisplayName("DateRangeFilter")
    class DateRangeFilterTests {

        // Note: DateRangeFilter.doesMatch() does not call checkColumnIndexInitialized(),
        // so the columnIndex must be resolved before doesMatch is called. When used via
        // the container's get() method, the filter clause sets the meta definition and
        // other filters resolve their index via checkColumnIndexInitialized(). Since
        // DateRangeFilter lacks this call, we test it directly with manual index init.

        @Test
        @DisplayName("should match dates within range (direct filter test)")
        void matchWithinRange() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "created"},
                new Class[]{Integer.class, Date.class},
                new String[]{"id"}
            );
            DateRangeFilter filter = new DateRangeFilter("created",
                new Date(1500000L), new Date(3500000L));
            filter.setMetaDefinition(meta);
            filter.checkColumnIndexInitialized();

            assertTrue(filter.doesMatch(new CDataRow(new Object[]{1, new Date(2000000L)})));
            assertTrue(filter.doesMatch(new CDataRow(new Object[]{2, new Date(3000000L)})));
            assertFalse(filter.doesMatch(new CDataRow(new Object[]{3, new Date(1000000L)})));
            assertFalse(filter.doesMatch(new CDataRow(new Object[]{4, new Date(4000000L)})));
        }

        @Test
        @DisplayName("should match with inclusive boundaries")
        void matchInclusiveBoundaries() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "created"},
                new Class[]{Integer.class, Date.class},
                new String[]{"id"}
            );
            DateRangeFilter filter = new DateRangeFilter("created",
                new Date(2000000L), new Date(3000000L));
            filter.setMetaDefinition(meta);
            filter.checkColumnIndexInitialized();

            assertTrue(filter.doesMatch(new CDataRow(new Object[]{1, new Date(2000000L)})));
            assertTrue(filter.doesMatch(new CDataRow(new Object[]{2, new Date(3000000L)})));
            assertFalse(filter.doesMatch(new CDataRow(new Object[]{3, new Date(1000000L)})));
        }

        @Test
        @DisplayName("should match with only lower bound (null upper)")
        void matchLowerBoundOnly() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "created"},
                new Class[]{Integer.class, Date.class},
                new String[]{"id"}
            );
            DateRangeFilter filter = new DateRangeFilter("created",
                new Date(3000000L), null);
            filter.setMetaDefinition(meta);
            filter.checkColumnIndexInitialized();

            assertTrue(filter.doesMatch(new CDataRow(new Object[]{1, new Date(3000000L)})));
            assertTrue(filter.doesMatch(new CDataRow(new Object[]{2, new Date(4000000L)})));
            assertFalse(filter.doesMatch(new CDataRow(new Object[]{3, new Date(2000000L)})));
        }

        @Test
        @DisplayName("should match with only upper bound (null lower)")
        void matchUpperBoundOnly() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "created"},
                new Class[]{Integer.class, Date.class},
                new String[]{"id"}
            );
            DateRangeFilter filter = new DateRangeFilter("created",
                null, new Date(2000000L));
            filter.setMetaDefinition(meta);
            filter.checkColumnIndexInitialized();

            assertTrue(filter.doesMatch(new CDataRow(new Object[]{1, new Date(1000000L)})));
            assertTrue(filter.doesMatch(new CDataRow(new Object[]{2, new Date(2000000L)})));
            assertFalse(filter.doesMatch(new CDataRow(new Object[]{3, new Date(3000000L)})));
        }

        @Test
        @DisplayName("should not match null date values")
        void noMatchOnNull() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "created"},
                new Class[]{Integer.class, Date.class},
                new String[]{"id"}
            );
            DateRangeFilter filter = new DateRangeFilter("created",
                new Date(0L), new Date(5000000L));
            filter.setMetaDefinition(meta);
            filter.checkColumnIndexInitialized();

            assertFalse(filter.doesMatch(new CDataRow(new Object[]{1, null})));
        }

        @Test
        @DisplayName("should throw when lower bound >= upper bound")
        void throwOnInvalidBounds() {
            assertThrows(CDataGridException.class, () ->
                new DateRangeFilter("created", new Date(5000000L), new Date(1000000L))
            );
        }

        @Test
        @DisplayName("should throw when lower bound equals upper bound")
        void throwOnEqualBounds() {
            assertThrows(CDataGridException.class, () ->
                new DateRangeFilter("created", new Date(1000000L), new Date(1000000L))
            );
        }

        @Test
        @DisplayName("toString should contain filter info")
        void toStringContainsInfo() throws CDataGridException {
            DateRangeFilter f = new DateRangeFilter("created",
                new Date(1000000L), new Date(2000000L));
            String str = f.toString();
            assertTrue(str.contains("DateRangeFilter"));
            assertTrue(str.contains("created"));
        }
    }

    @Nested
    @DisplayName("CDataFilterClause (combined filters)")
    class FilterClauseTests {

        @Test
        @DisplayName("multiple filters should combine with AND")
        void andCombination() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new GEFilter("age", 28, true));
            clause.addFilter(new LEFilter("score", 92.0, true));
            CDataRowSet rs = container.get(clause);
            // age>=28 AND score<=92: Alice(30,95.5-no), Diana(28,91-yes), Charlie(35,88.5-yes), Eve(32,76-yes)
            assertEquals(3, rs.getNumberRows());
        }

        @Test
        @DisplayName("should report correct size")
        void clauseSize() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            assertEquals(0, clause.size());
            clause.addFilter(new EqualsFilter("name", new Object[]{"x"}));
            assertEquals(1, clause.size());
            clause.addFilter(new GEFilter("age", 10, true));
            assertEquals(2, clause.size());
        }

        @Test
        @DisplayName("should throw when adding null filter")
        void throwOnNullFilter() {
            CDataFilterClause clause = new CDataFilterClause();
            assertThrows(CDataGridException.class, () -> clause.addFilter(null));
        }

        @Test
        @DisplayName("empty filter clause should return all rows")
        void emptyClauseReturnsAll() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            CDataRowSet rs = container.get(clause);
            assertEquals(5, rs.getNumberRows());
        }

        @Test
        @DisplayName("toString should contain clause info")
        void toStringContainsInfo() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"Alice"}));
            String str = clause.toString();
            assertTrue(str.contains("Filter Clause"));
        }

        @Test
        @DisplayName("getFilter should return filter at index")
        void getFilterByIndex() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            EqualsFilter f = new EqualsFilter("name", new Object[]{"x"});
            clause.addFilter(f);
            assertSame(f, clause.getFilter(0));
        }

        @Test
        @DisplayName("getFilter should throw for out-of-range index")
        void getFilterOutOfRange() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            assertThrows(CDataGridException.class, () -> clause.getFilter(0));
        }

        @Test
        @DisplayName("getAllFilters should return all filters")
        void getAllFilters() throws CDataGridException {
            CDataFilterClause clause = new CDataFilterClause();
            clause.addFilter(new EqualsFilter("name", new Object[]{"x"}));
            clause.addFilter(new GEFilter("age", 10, true));
            CDataFilter[] filters = clause.getAllFilters();
            assertEquals(2, filters.length);
        }
    }

    @Nested
    @DisplayName("Sorting with filter results")
    class SortingWithFilters {

        @Test
        @DisplayName("should sort filtered results ascending")
        void sortAscending() throws CDataGridException {
            CDataRowSet rs = container.get("name",
                new Object[]{"Alice", "Charlie", "Eve"},
                new String[]{"age"}, true);
            assertTrue(rs.next());
            assertEquals("Alice", rs.getString("name")); // age 30
            assertTrue(rs.next());
            assertEquals("Eve", rs.getString("name")); // age 32
            assertTrue(rs.next());
            assertEquals("Charlie", rs.getString("name")); // age 35
        }

        @Test
        @DisplayName("should sort filtered results descending")
        void sortDescending() throws CDataGridException {
            CDataRowSet rs = container.get("name",
                new Object[]{"Alice", "Charlie", "Eve"},
                new String[]{"age"}, false);
            assertTrue(rs.next());
            assertEquals("Charlie", rs.getString("name")); // age 35
        }
    }
}
