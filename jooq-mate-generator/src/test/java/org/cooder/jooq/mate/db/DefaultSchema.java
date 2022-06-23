/*
 * This file is generated by jOOQ.
 */
package org.cooder.jooq.mate.db;


import java.util.Arrays;
import java.util.List;

import org.cooder.jooq.mate.db.tables.HouseLayout;
import org.cooder.jooq.mate.db.tables.Space;
import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.impl.SchemaImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class DefaultSchema extends SchemaImpl {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>DEFAULT_SCHEMA</code>
     */
    public static final DefaultSchema DEFAULT_SCHEMA = new DefaultSchema();

    /**
     * 户型就是房子的结构与形状，一般与设计方案相关，不同的设计会产出不同的户型，只要数据有调整，就会新生成一个户型
     */
    public final HouseLayout HOUSE_LAYOUT = HouseLayout.HOUSE_LAYOUT;

    /**
     * 不同视图下户型的组成单元，既包含单空间也包含组合空间
     */
    public final Space SPACE = Space.SPACE;

    /**
     * No further instances allowed
     */
    private DefaultSchema() {
        super("", null);
    }


    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        return Arrays.<Table<?>>asList(
            HouseLayout.HOUSE_LAYOUT,
                Space.SPACE);
    }
}