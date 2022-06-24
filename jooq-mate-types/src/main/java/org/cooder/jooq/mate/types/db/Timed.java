package org.cooder.jooq.mate.types.db;

import java.time.LocalDateTime;

/**
 * Common fields of DB table
 */
public interface Timed {
    LocalDateTime getCtime();

    void setCtime(LocalDateTime ctime);

    LocalDateTime getMtime();

    void setMtime(LocalDateTime mtime);
}
