package net.casper.data.model;

import static org.junit.jupiter.api.Assertions.*;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("CDataConverter")
class CDataConverterTest {

    @Test
    @DisplayName("should return null when input is null")
    void nullReturnsNull() throws CDataGridException {
        assertNull(CDataConverter.convertTo(null, CTypes.STRING));
        assertNull(CDataConverter.convertTo(null, CTypes.INTEGER));
    }

    @Nested
    @DisplayName("Number conversions")
    class NumberConversions {

        @Test
        @DisplayName("Integer to String")
        void intToString() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.STRING);
            assertEquals("42", result);
        }

        @Test
        @DisplayName("Integer to Double")
        void intToDouble() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.DOUBLE);
            assertEquals(42.0, result);
            assertTrue(result instanceof Double);
        }

        @Test
        @DisplayName("Integer to Float")
        void intToFloat() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.FLOAT);
            assertEquals(42.0f, result);
            assertTrue(result instanceof Float);
        }

        @Test
        @DisplayName("Integer to Long")
        void intToLong() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.LONG);
            assertEquals(42L, result);
            assertTrue(result instanceof Long);
        }

        @Test
        @DisplayName("Integer to Short")
        void intToShort() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.SHORT);
            assertEquals((short) 42, result);
            assertTrue(result instanceof Short);
        }

        @Test
        @DisplayName("Integer to Byte")
        void intToByte() throws CDataGridException {
            Object result = CDataConverter.convertTo(42, CTypes.BYTE);
            assertEquals((byte) 42, result);
            assertTrue(result instanceof Byte);
        }

        @Test
        @DisplayName("Integer to Boolean (0=false, 1=true)")
        void intToBoolean() throws CDataGridException {
            assertEquals(Boolean.FALSE, CDataConverter.convertTo(0, CTypes.BOOLEAN));
            assertEquals(Boolean.TRUE, CDataConverter.convertTo(1, CTypes.BOOLEAN));
        }

        @Test
        @DisplayName("Integer to Boolean should throw for non-0/1")
        void intToBooleanInvalid() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo(5, CTypes.BOOLEAN)
            );
        }

        @Test
        @DisplayName("Double to Integer")
        void doubleToInt() throws CDataGridException {
            Object result = CDataConverter.convertTo(3.7, CTypes.INTEGER);
            assertEquals(3, result);
        }

        @Test
        @DisplayName("Double to String")
        void doubleToString() throws CDataGridException {
            Object result = CDataConverter.convertTo(3.14, CTypes.STRING);
            assertTrue(result instanceof String);
            assertTrue(((String) result).startsWith("3.14"));
        }

        @Test
        @DisplayName("Long to Integer")
        void longToInt() throws CDataGridException {
            Object result = CDataConverter.convertTo(100L, CTypes.INTEGER);
            assertEquals(100, result);
            assertTrue(result instanceof Integer);
        }

        @Test
        @DisplayName("Number to invalid type should throw")
        void numberToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo(42, CTypes.DATE)
            );
        }
    }

    @Nested
    @DisplayName("Byte conversions")
    class ByteConversions {

        @Test
        @DisplayName("Byte to Integer")
        void byteToInt() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 10, CTypes.INTEGER);
            assertEquals(10, result);
            assertTrue(result instanceof Integer);
        }

        @Test
        @DisplayName("Byte to String")
        void byteToString() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 65, CTypes.STRING);
            assertEquals("65", result);
        }

        @Test
        @DisplayName("Byte to Double")
        void byteToDouble() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 5, CTypes.DOUBLE);
            assertEquals(5.0, result);
            assertTrue(result instanceof Double);
        }

        @Test
        @DisplayName("Byte to Boolean (0=false, 1=true)")
        void byteToBoolean() throws CDataGridException {
            assertEquals(Boolean.FALSE, CDataConverter.convertTo((byte) 0, CTypes.BOOLEAN));
            assertEquals(Boolean.TRUE, CDataConverter.convertTo((byte) 1, CTypes.BOOLEAN));
        }

        @Test
        @DisplayName("Byte to Boolean should throw for non-0/1")
        void byteToBooleanInvalid() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo((byte) 5, CTypes.BOOLEAN)
            );
        }

        @Test
        @DisplayName("Byte to Byte identity")
        void byteToByteIdentity() throws CDataGridException {
            Byte input = (byte) 42;
            Object result = CDataConverter.convertTo(input, CTypes.BYTE);
            assertEquals(input, result);
        }

        @Test
        @DisplayName("Byte to Float")
        void byteToFloat() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 7, CTypes.FLOAT);
            assertEquals(7.0f, result);
        }

        @Test
        @DisplayName("Byte to Long")
        void byteToLong() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 3, CTypes.LONG);
            assertEquals(3L, result);
        }

        @Test
        @DisplayName("Byte to Short")
        void byteToShort() throws CDataGridException {
            Object result = CDataConverter.convertTo((byte) 9, CTypes.SHORT);
            assertEquals((short) 9, result);
        }

        @Test
        @DisplayName("Byte to invalid type should throw")
        void byteToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo((byte) 1, CTypes.DATE)
            );
        }
    }

    @Nested
    @DisplayName("String conversions")
    class StringConversions {

        @Test
        @DisplayName("String to String identity")
        void stringToString() throws CDataGridException {
            assertEquals("hello", CDataConverter.convertTo("hello", CTypes.STRING));
        }

        @Test
        @DisplayName("String to Integer")
        void stringToInt() throws CDataGridException {
            Object result = CDataConverter.convertTo("42", CTypes.INTEGER);
            assertEquals(42, result);
        }

        @Test
        @DisplayName("String to Double")
        void stringToDouble() throws CDataGridException {
            Object result = CDataConverter.convertTo("3.14", CTypes.DOUBLE);
            assertEquals(3.14, result);
        }

        @Test
        @DisplayName("String to Float")
        void stringToFloat() throws CDataGridException {
            Object result = CDataConverter.convertTo("2.5", CTypes.FLOAT);
            assertEquals(2.5f, result);
        }

        @Test
        @DisplayName("String to Long")
        void stringToLong() throws CDataGridException {
            Object result = CDataConverter.convertTo("100", CTypes.LONG);
            assertEquals(100L, result);
        }

        @Test
        @DisplayName("String to Short")
        void stringToShort() throws CDataGridException {
            Object result = CDataConverter.convertTo("10", CTypes.SHORT);
            assertEquals((short) 10, result);
        }

        @Test
        @DisplayName("String to Byte")
        void stringToByte() throws CDataGridException {
            Object result = CDataConverter.convertTo("7", CTypes.BYTE);
            assertEquals((byte) 7, result);
        }

        @Test
        @DisplayName("String to Boolean")
        void stringToBoolean() throws CDataGridException {
            assertEquals(Boolean.TRUE, CDataConverter.convertTo("true", CTypes.BOOLEAN));
            assertEquals(Boolean.FALSE, CDataConverter.convertTo("false", CTypes.BOOLEAN));
        }

        @Test
        @DisplayName("String to invalid type should throw")
        void stringToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo("hello", CTypes.DATE)
            );
        }
    }

    @Nested
    @DisplayName("Boolean conversions")
    class BooleanConversions {

        @Test
        @DisplayName("Boolean to Boolean identity")
        void boolToBool() throws CDataGridException {
            assertEquals(Boolean.TRUE, CDataConverter.convertTo(true, CTypes.BOOLEAN));
            assertEquals(Boolean.FALSE, CDataConverter.convertTo(false, CTypes.BOOLEAN));
        }

        @Test
        @DisplayName("Boolean to Integer (true=1, false=0)")
        void boolToInt() throws CDataGridException {
            assertEquals(1, CDataConverter.convertTo(true, CTypes.INTEGER));
            assertEquals(0, CDataConverter.convertTo(false, CTypes.INTEGER));
        }

        @Test
        @DisplayName("Boolean to Double")
        void boolToDouble() throws CDataGridException {
            assertEquals(1.0, CDataConverter.convertTo(true, CTypes.DOUBLE));
            assertEquals(0.0, CDataConverter.convertTo(false, CTypes.DOUBLE));
        }

        @Test
        @DisplayName("Boolean to Long")
        void boolToLong() throws CDataGridException {
            assertEquals(1L, CDataConverter.convertTo(true, CTypes.LONG));
            assertEquals(0L, CDataConverter.convertTo(false, CTypes.LONG));
        }

        @Test
        @DisplayName("Boolean to String")
        void boolToString() throws CDataGridException {
            assertEquals("true", CDataConverter.convertTo(true, CTypes.STRING));
            assertEquals("false", CDataConverter.convertTo(false, CTypes.STRING));
        }

        @Test
        @DisplayName("Boolean to Byte")
        void boolToByte() throws CDataGridException {
            assertEquals((byte) 1, CDataConverter.convertTo(true, CTypes.BYTE));
            assertEquals((byte) 0, CDataConverter.convertTo(false, CTypes.BYTE));
        }

        @Test
        @DisplayName("Boolean to Float")
        void boolToFloat() throws CDataGridException {
            assertEquals(1.0f, CDataConverter.convertTo(true, CTypes.FLOAT));
            assertEquals(0.0f, CDataConverter.convertTo(false, CTypes.FLOAT));
        }

        @Test
        @DisplayName("Boolean to Short")
        void boolToShort() throws CDataGridException {
            assertEquals((short) 1, CDataConverter.convertTo(true, CTypes.SHORT));
            assertEquals((short) 0, CDataConverter.convertTo(false, CTypes.SHORT));
        }

        @Test
        @DisplayName("Boolean to invalid type should throw")
        void boolToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo(true, CTypes.DATE)
            );
        }
    }

    @Nested
    @DisplayName("Timestamp conversions")
    class TimestampConversions {

        @Test
        @DisplayName("Timestamp to Timestamp identity")
        void tsToTs() throws CDataGridException {
            Timestamp ts = new Timestamp(System.currentTimeMillis());
            assertSame(ts, CDataConverter.convertTo(ts, CTypes.TIMESTAMP));
        }

        @Test
        @DisplayName("Timestamp to Date")
        void tsToDate() throws CDataGridException {
            Timestamp ts = new Timestamp(1000000L);
            Object result = CDataConverter.convertTo(ts, CTypes.DATE);
            assertTrue(result instanceof java.sql.Date);
            assertEquals(1000000L, ((java.sql.Date) result).getTime());
        }

        @Test
        @DisplayName("Timestamp to String")
        void tsToString() throws CDataGridException {
            Timestamp ts = new Timestamp(1000000L);
            Object result = CDataConverter.convertTo(ts, CTypes.STRING);
            assertTrue(result instanceof String);
        }

        @Test
        @DisplayName("Timestamp to Time")
        void tsToTime() throws CDataGridException {
            Timestamp ts = new Timestamp(1000000L);
            Object result = CDataConverter.convertTo(ts, CTypes.TIME);
            assertTrue(result instanceof java.sql.Time);
        }

        @Test
        @DisplayName("Timestamp to invalid type should throw")
        void tsToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo(new Timestamp(0), CTypes.INTEGER)
            );
        }
    }

    @Nested
    @DisplayName("java.util.Date conversions")
    class DateConversions {

        @Test
        @DisplayName("Date to Date identity")
        void dateToDate() throws CDataGridException {
            Date date = new Date(1000000L);
            assertSame(date, CDataConverter.convertTo(date, CTypes.DATE));
        }

        @Test
        @DisplayName("Date to Timestamp")
        void dateToTimestamp() throws CDataGridException {
            Date date = new Date(1000000L);
            Object result = CDataConverter.convertTo(date, CTypes.TIMESTAMP);
            assertTrue(result instanceof Timestamp);
            assertEquals(1000000L, ((Timestamp) result).getTime());
        }

        @Test
        @DisplayName("Date to String")
        void dateToString() throws CDataGridException {
            Date date = new Date(1000000L);
            Object result = CDataConverter.convertTo(date, CTypes.STRING);
            assertTrue(result instanceof String);
            assertFalse(((String) result).isEmpty());
        }

        @Test
        @DisplayName("Date to Time")
        void dateToTime() throws CDataGridException {
            Date date = new Date(1000000L);
            Object result = CDataConverter.convertTo(date, CTypes.TIME);
            assertTrue(result instanceof java.sql.Time);
        }

        @Test
        @DisplayName("Date to invalid type should throw")
        void dateToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo(new Date(), CTypes.INTEGER)
            );
        }
    }

    @Nested
    @DisplayName("Character conversions")
    class CharacterConversions {

        @Test
        @DisplayName("Character to Character identity")
        void charToChar() throws CDataGridException {
            Character c = 'A';
            // Note: CHARACTER and TIMESTAMP share same constant value (22)
            // so convertTo with CHARACTER works as identity
            Object result = CDataConverter.convertTo(c, CTypes.CHARACTER);
            assertEquals(c, result);
        }

        @Test
        @DisplayName("Character to String")
        void charToString() throws CDataGridException {
            Object result = CDataConverter.convertTo('Z', CTypes.STRING);
            assertEquals("Z", result);
        }

        @Test
        @DisplayName("Character to invalid type should throw")
        void charToInvalidType() {
            assertThrows(CDataGridException.class, () ->
                CDataConverter.convertTo('A', CTypes.INTEGER)
            );
        }
    }

    @Test
    @DisplayName("unsupported object type should throw")
    void unsupportedTypeThrows() {
        assertThrows(CDataGridException.class, () ->
            CDataConverter.convertTo(new Object(), CTypes.STRING)
        );
    }

    @Test
    @DisplayName("nullValue should return 'null' string")
    void nullValueString() {
        assertEquals("null", CDataConverter.nullValue());
    }
}
