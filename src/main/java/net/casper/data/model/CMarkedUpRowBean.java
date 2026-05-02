package net.casper.data.model;

/**
 * Interface for beans that can hold a {@link CMarkedUpRow}.
 */
public interface CMarkedUpRowBean {

    void setMarkedUpRow(CMarkedUpRow row) throws CDataGridException;

    CMarkedUpRow getMarkedUpRow();
}
