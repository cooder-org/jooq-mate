package org.cooder.jooq.mate.types.db;

/**
 * Common fields of DB table
 */
public interface Operator extends Timed {
    
    String getCuName();

    void setCuName(String cuName);

    Long getCuid();

    void setCuid(Long cuid);

    String getMuName();

    void setMuName(String muName);

    Long getMuid();

    void setMuid(Long muid);
}
