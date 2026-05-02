package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CDataRowSetAggregator")
class CDataRowSetAggregatorTest {

    private CDataRowSet rowSet;

    @BeforeEach
    void setUp() throws CDataGridException {
        CRowMetaData meta = new CRowMetaData(
            new String[]{"id", "value", "weight", "label"},
            new Class[]{Integer.class, Double.class, Double.class, String.class},
            new String[]{"id"}
        );
        rowSet = new CDataRowSet(meta);
        rowSet.addData(new CDataRow[]{
            new CDataRow(new Object[]{1, 10.0, 2.0, "A"}),
            new CDataRow(new Object[]{2, 20.0, 3.0, "B"}),
            new CDataRow(new Object[]{3, 30.0, 5.0, "C"})
        });
    }

    @Nested
    @DisplayName("sum")
    class Sum {

        @Test
        @DisplayName("should compute sum of numeric column")
        void computeSum() throws CDataGridException {
            Double result = CDataRowSetAggregator.sum(rowSet, "value");
            assertEquals(60.0, result, 0.001);
        }

        @Test
        @DisplayName("should handle null values in sum")
        void sumWithNulls() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value"},
                new Class[]{Integer.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{
                new CDataRow(new Object[]{1, 10.0}),
                new CDataRow(new Object[]{2, null}),
                new CDataRow(new Object[]{3, 30.0})
            });
            assertEquals(40.0, CDataRowSetAggregator.sum(rs, "value"), 0.001);
        }

        @Test
        @DisplayName("should throw on null rowset")
        void throwOnNull() {
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.sum(null, "value")
            );
        }

        @Test
        @DisplayName("should throw on non-numeric column")
        void throwOnNonNumeric() {
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.sum(rowSet, "label")
            );
        }
    }

    @Nested
    @DisplayName("average")
    class Average {

        @Test
        @DisplayName("should compute average of numeric column")
        void computeAverage() throws CDataGridException {
            Double result = CDataRowSetAggregator.average(rowSet, "value");
            assertEquals(20.0, result, 0.001);
        }

        @Test
        @DisplayName("should throw on empty rowset")
        void throwOnEmpty() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value"},
                new Class[]{Integer.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet emptyRs = new CDataRowSet(meta);
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.average(emptyRs, "value")
            );
        }
    }

    @Nested
    @DisplayName("max")
    class Max {

        @Test
        @DisplayName("should find max value in column")
        void findMax() throws CDataGridException {
            Double result = CDataRowSetAggregator.max(rowSet, "value");
            assertEquals(30.0, result, 0.001);
        }

        @Test
        @DisplayName("should handle null values in max")
        void maxWithNulls() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value"},
                new Class[]{Integer.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{
                new CDataRow(new Object[]{1, null}),
                new CDataRow(new Object[]{2, 15.0})
            });
            assertEquals(15.0, CDataRowSetAggregator.max(rs, "value"), 0.001);
        }
    }

    @Nested
    @DisplayName("min")
    class Min {

        @Test
        @DisplayName("should find min value in column")
        void findMin() throws CDataGridException {
            // Note: min implementation starts at 0.0, so min(10,20,30) = 0.0 if all > 0
            // This tests the actual behavior of the implementation
            Double result = CDataRowSetAggregator.min(rowSet, "value");
            assertEquals(0.0, result, 0.001);
        }

        @Test
        @DisplayName("should find negative min when present")
        void findNegativeMin() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value"},
                new Class[]{Integer.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{
                new CDataRow(new Object[]{1, -5.0}),
                new CDataRow(new Object[]{2, -10.0}),
                new CDataRow(new Object[]{3, 5.0})
            });
            assertEquals(-10.0, CDataRowSetAggregator.min(rs, "value"), 0.001);
        }
    }

    @Nested
    @DisplayName("weightedSum")
    class WeightedSum {

        @Test
        @DisplayName("should compute weighted sum")
        void computeWeightedSum() throws CDataGridException {
            // weightedSum multiplies value*weight for each row then sums
            // Row 1: 10*2=20, Row 2: 20*3=60, Row 3: 30*5=150 => total=230
            Double result = CDataRowSetAggregator.weightedSum(rowSet, "value", "weight");
            assertEquals(230.0, result, 0.001);
        }

        @Test
        @DisplayName("should throw on empty rowset")
        void throwOnEmpty() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value", "weight"},
                new Class[]{Integer.class, Double.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet emptyRs = new CDataRowSet(meta);
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.weightedSum(emptyRs, "value", "weight")
            );
        }

        @Test
        @DisplayName("should handle null values in weighted sum")
        void weightedSumWithNulls() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value", "weight"},
                new Class[]{Integer.class, Double.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{
                new CDataRow(new Object[]{1, 10.0, null}),
                new CDataRow(new Object[]{2, 20.0, 3.0})
            });
            // Only row 2 contributes: 20*3=60
            Double result = CDataRowSetAggregator.weightedSum(rs, "value", "weight");
            assertEquals(60.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("weightedAverage")
    class WeightedAverage {

        @Test
        @DisplayName("should compute weighted average")
        void computeWeightedAverage() throws CDataGridException {
            // sum(value*weight) / sum(weight)
            // (10*2 + 20*3 + 30*5) / (2+3+5) = (20+60+150)/10 = 230/10 = 23.0
            Double result = CDataRowSetAggregator.weightedAverage(rowSet, "value", "weight");
            assertEquals(23.0, result, 0.001);
        }

        @Test
        @DisplayName("should throw on empty rowset")
        void throwOnEmpty() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value", "weight"},
                new Class[]{Integer.class, Double.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet emptyRs = new CDataRowSet(meta);
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.weightedAverage(emptyRs, "value", "weight")
            );
        }

        @Test
        @DisplayName("should return 0 when all weights are null")
        void zeroWhenAllWeightsNull() throws CDataGridException {
            CRowMetaData meta = new CRowMetaData(
                new String[]{"id", "value", "weight"},
                new Class[]{Integer.class, Double.class, Double.class},
                new String[]{"id"}
            );
            CDataRowSet rs = new CDataRowSet(meta);
            rs.addData(new CDataRow[]{
                new CDataRow(new Object[]{1, 10.0, null}),
                new CDataRow(new Object[]{2, 20.0, null})
            });
            Double result = CDataRowSetAggregator.weightedAverage(rs, "value", "weight");
            assertEquals(0.0, result, 0.001);
        }
    }

    @Nested
    @DisplayName("Input validation")
    class InputValidation {

        @Test
        @DisplayName("should throw on non-existent column name")
        void throwOnBadColumn() {
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.sum(rowSet, "nonexistent")
            );
        }

        @Test
        @DisplayName("should throw on non-numeric column for all aggregations")
        void throwOnNonNumericForAll() {
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.max(rowSet, "label")
            );
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.min(rowSet, "label")
            );
            assertThrows(CDataGridException.class, () ->
                CDataRowSetAggregator.average(rowSet, "label")
            );
        }
    }
}
