package net.casper.data.model;

import java.io.IOException;

/**
 * Interface for exporting a dataset to any destination (file, JDBC, UI, etc).
 */
public interface CExporter {

    void setName(String name) throws IOException;

    void setColumnNames(String[] columnNames) throws IOException;

    void setColumnTypes(Class<?>[] columnTypes) throws IOException;

    void setPrimaryKeyColumns(String[] primaryKeyColumns) throws IOException;

    /** Called once after setting metadata and before writing rows. */
    void open() throws IOException;

    void writeRow(Object[] row) throws IOException;

    /** Tidy up after export. Returns an exporter-specific value, or null. */
    Object close();
}
