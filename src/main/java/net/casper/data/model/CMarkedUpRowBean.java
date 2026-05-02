package net.casper.data.model;

/**
 * Classes implementing this interface can have their {@link CMarkedUpRow} set.
 *
 * @author Oliver Mannion
 */
public interface CMarkedUpRowBean {

    /**
     * Set this bean's {@link CMarkedUpRow}.
     *
     * @param row row
     * @throws CDataGridException if problem reading <code>row</code>
     */
    void setMarkedUpRow(CMarkedUpRow row) throws CDataGridException;

    /**
     * Get this bean's {@link CMarkedUpRow}.
     *
     * @return a {@link CMarkedUpRow}
     */
    CMarkedUpRow getMarkedUpRow();

}
