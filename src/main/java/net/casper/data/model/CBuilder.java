package net.casper.data.model;

import java.io.IOException;
import java.util.Map;

/**
 * Interface for building a {@link CDataCacheContainer} from any data source
 * (file, JDBC, UI, etc).
 */
public interface CBuilder {

    String getName();

    String[] getColumnNames();

    Class<?>[] getColumnTypes();

    String[] getPrimaryKeyColumns();

    Map<?, ?> getConcreteMap();

    /** Called once before reading rows. */
    void open() throws IOException;

    /** Returns one row at a time; null signals end of data. */
    Object[] readRow() throws IOException;

    /** Called after all rows are read, or on error. */
    void close();
}
