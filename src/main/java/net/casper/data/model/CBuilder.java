package net.casper.data.model;

import java.io.IOException;
import java.util.Map;

/**
 * Interface specifying elements required to build a CDataCacheContainer.
 * These methods may be implemented to build a CDataCacheContainer from
 * any source, e.g.: file, JDBC data source, user interface etc.
 *
 * @author Oliver Mannion
 */
public interface CBuilder {

    /**
     * Name of the built dataset.
     *
     * @return String
     */
    String getName();

    /**
     * Column names of the built dataset.
     *
     * @return String[]
     */
    String[] getColumnNames();

    /**
     * Column types of the built dataset.
     *
     * @return Class[]
     */
    Class[] getColumnTypes();

    /**
     * Primary key columns of the built dataset.
     *
     * @return String[]
     */
    String[] getPrimaryKeyColumns();

    /**
     * Map used to store container rows.
     *
     * @return Map
     */
    Map getConcreteMap();

    /**
     * Initialisation before building begins. Called once
     * before getting cacheName, columnNames, columnTypes
     * and primaryKeyColumns and before {@link #readRow()}.
     * Useful for initialisation that relies on (optional)
     * parameters that may be set after construction.
     *
     * @throws IOException if export destination cannot be opened.
     */
    void open() throws IOException;

    /**
     * Returns row one at time until dataset built.
     * Returns <code>null</code> to indicate no more
     * rows are to be built.
     *
     * @return Object[] row
     * @throws IOException if problem reading row.
     */
    Object[] readRow() throws IOException;

    /**
     * Close the data builder source. This is called
     * after all rows are read, or if there an exception
     * occurs during {@link #readRow()}.
     *
     * @throws IOException
     */
    void close();

}
