package net.casper.data.model;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Represents the generic exception for all data-grid based functionality
 *
 * @since 1.0
 * @author Jonathan Liang
 */
public class CDataGridException extends Exception {

    /** Required for serializable */
    private static final long serialVersionUID = 1L;

    /**
     * Exceptions must be thrown with some reason !!
     */
    private CDataGridException() {
    }

    /**
     * Throw exception with message / reason
     * @param message
     */
    public CDataGridException(String message) {
        super(message);
    }

    /**
     * Throw exception with previous (chained) error)
     * @param message
     * @param previous
     */
    public CDataGridException(String message, Throwable previous) {
        super(message, previous);
    }

    /**
     * Throw exception with previous (chained) error).
     * @param previous
     */
    public CDataGridException(Throwable previous) {
        super(previous);
    }

    /**
     * Return cause
     * @return cause
     */
    public Throwable getPreviousError() {
        return getCause();
    }

    /**
     * Get string representation of trace
     * @param thrown
     * @return string representation of stack trace
     */
    public static String getStackTraceAsString(Throwable thrown) {
        StringWriter buffer = new StringWriter();
        PrintWriter ppw = new PrintWriter(buffer);

        int depth = 0;
        Throwable ex = thrown;
        while (ex != null) {
            if (depth > 0)
                ppw.write("Caused by: \n");
            ex.printStackTrace(ppw);
            ex = ex.getCause();
            depth++;
        }

        ppw.flush();
        ppw.close();

        return buffer.toString();
    }

}
