package net.casper.data.model;

import java.io.IOException;

/**
 * Interface for exporting a Casper data container/rowset. These methods may be
 * implemented to export a casper data to any destination, e.g.: file, JDBC data
 * source, user interface etc.
 *
 * Like the Builder design pattern this separates allows the export of a dataset
 * via its individual elements. This separates the export from the particular
 * representation of a casper dataset, meaning the exporter does not depend on
 * the casper dataset library.
 *
 * @author Oliver Mannion
 */
public interface CExporter {

    /**
     * Set the name of the dataset.
     *
     * @param name
     *            dataset name
     *
     * @throws IOException
     *             if problem setting name
     */
    void setName(String name) throws IOException;

    /**
     * Set the column names of the dataset.
     *
     * @param columnNames
     *            column names
     *
     * @throws IOException
     *             if problem setting column names
     */
    void setColumnNames(String[] columnNames) throws IOException;

    /**
     * Set the column types of the dataset.
     *
     * @param columnTypes
     *            column types of the dataset
     *
     * @throws IOException
     *             if problem setting column types
     */
    void setColumnTypes(Class[] columnTypes) throws IOException;

    /**
     * Set the primary key columns of the dataset.
     *
     * @param primaryKeyColumns
     *            primary key columns of the dataset
     *
     * @throws IOException
     *             if problem setting primary key columns
     */
    void setPrimaryKeyColumns(String[] primaryKeyColumns) throws IOException;

    /**
     * Initialisation before exporting begins. Called once after setting
     * cacheName, columnNames, columnTypes and primaryKeyColumns and before
     * {@link #writeRow(Object[])}.
     *
     * @throws IOException
     *             if export destination cannot be opened.
     */
    void open() throws IOException;

    /**
     * Export the data, one row at a time.
     *
     * @param row
     *            single row of data.
     * @throws IOException
     *             if problem writing row.
     */
    void writeRow(Object[] row) throws IOException;

    /**
     * Tidy up after export has completed. Called even if export fails. Can
     * return a value specific to the exporter.
     *
     * @return a value specific to the exporter, or <code>null</code> if
     *         nothing.
     * @throws IOException
     */
    Object close();

}
