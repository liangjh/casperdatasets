package net.casper.data.model;

/**
 * Unchecked exception for dataset operations.
 */
public class CDataRuntimeException extends RuntimeException {

    private static final long serialVersionUID = -7171935382264183199L;

    public CDataRuntimeException(String message) {
        super(message);
    }

    public CDataRuntimeException(Throwable cause) {
        super(cause);
    }

    public CDataRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }
}
