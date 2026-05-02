package net.casper.data.model;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Generic exception for all dataset operations.
 */
public class CDataGridException extends Exception {

    private static final long serialVersionUID = 1L;

    public CDataGridException(String message) {
        super(message);
    }

    public CDataGridException(String message, Throwable previous) {
        super(message, previous);
    }

    public CDataGridException(Throwable previous) {
        super(previous);
    }

    public static String getStackTraceAsString(Throwable thrown) {
        StringWriter buffer = new StringWriter();
        PrintWriter pw = new PrintWriter(buffer);
        Throwable ex = thrown;
        while (ex != null) {
            if (ex != thrown) pw.write("Caused by: \n");
            ex.printStackTrace(pw);
            ex = ex.getCause();
        }
        pw.flush();
        pw.close();
        return buffer.toString();
    }
}
