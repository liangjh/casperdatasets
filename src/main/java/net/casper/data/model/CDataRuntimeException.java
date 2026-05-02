package net.casper.data.model;

/**
 * Exception with a dataset during runtime.
 *
 * @author Oliver Mannion
 */
public class CDataRuntimeException extends RuntimeException {

    /**
     * Serialization ID.
     */
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
