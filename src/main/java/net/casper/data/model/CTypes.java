package net.casper.data.model;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * This class provides the various data types supported in data conversion.
 * The reason all of the data types are created as static integers
 * is an optimization to allow for *FAST* switching.  The alternative to
 * this approach to is utilize the "instanceof" operation to determine type equality
 * which is known to be performance intensive, in large quantities.  Since type conversion
 * is used frequently, the optimization makes sense.
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CTypes implements Serializable {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /** Constant to retrieve value as Boolean */
    public static final int BOOLEAN = 2;

    /** Constant to retrieve value as Byte */
    public static final int BYTE = 4;

    /** Constant to retrieve value as Date */
    public static final int DATE = 6;

    /** Constant to retrieve value as Double */
    public static final int DOUBLE = 8;

    /** Constant to retrieve value as Float */
    public static final int FLOAT = 10;

    /** Constant to retrieve value as Integer */
    public static final int INTEGER = 12;

    /** Constant to retrieve value as Long */
    public static final int LONG = 14;

    /** Constant to retrieve value as Short */
    public static final int SHORT = 16;

    /** Constant to retrieve value as String */
    public static final int STRING = 18;

    /** Constant to retrieve value as Time */
    public static final int TIME = 20;

    /** Constant to retrieve value as Timestamp */
    public static final int TIMESTAMP = 22;

    /** Constant to retrieve value as Character */
    public static final int CHARACTER = 22;

    /**
     * No instantiation allowed.
     */
    protected CTypes() {
    }

    /**
     * Retrieves the descrption for the convertsion data type.
     *
     * @param type the type value.
     * @return a string description
     */
    public static String getConvTypeDesc(int type) {
        if (type == DOUBLE)             return "DOUBLE";
        else if (type == FLOAT)         return "FLOAT";
        else if (type == INTEGER)       return "INTEGER";
        else if (type == LONG)          return "LONG";
        else if (type == SHORT)         return "SHORT";
        else if (type == BOOLEAN)       return "BOOLEAN";
        else if (type == BYTE)          return "BYTE";
        else if (type == DATE)          return "DATE";
        else if (type == STRING)        return "STRING";
        else if (type == TIME)          return "TIME";
        else if (type == TIMESTAMP)     return "TIMESTAMP";
        else if (type == CHARACTER)     return "CHARACTER";
        else                            return "Invalid Type Value";
    }

    /**
     * Given a class object, return the corresponding JAVA-represented class
     * (or in the java.sql package)
     *
     * @param object
     * @return
     */
    public static int getJavaObjType(Object object) {
        if (object == null)
            return -1;

        Class cls = object.getClass();

        if (cls.equals(Boolean.class))              return Types.BOOLEAN;
        else if (cls.equals(Byte.class))            return Types.CHAR;
        else if (cls.equals(java.sql.Date.class))   return Types.DATE;
        else if (cls.equals(java.util.Date.class))  return Types.DATE;
        else if (cls.equals(Double.class))           return Types.DOUBLE;
        else if (cls.equals(Float.class))            return Types.FLOAT;
        else if (cls.equals(Integer.class))          return Types.INTEGER;
        else if (cls.equals(Long.class))             return Types.NUMERIC;
        else if (cls.equals(Short.class))            return Types.NUMERIC;
        else if (cls.equals(String.class))           return Types.VARCHAR;
        else if (cls.equals(Time.class))             return Types.TIME;
        else if (cls.equals(Timestamp.class))        return Types.TIMESTAMP;
        else if (cls.equals(Character.class))        return Types.CHAR;
        else    return -1;
    }

}
