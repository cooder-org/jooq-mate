/*
 * This file is generated by jOOQ.
 */
package org.cooder.jooq.mate.db.tables;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.cooder.jooq.mate.db.DefaultSchema;
import org.cooder.jooq.mate.db.Keys;
import org.cooder.jooq.mate.db.tables.records.HouseLayoutRecord;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.UniqueKey;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * 户型就是房子的结构与形状，一般与设计方案相关，不同的设计会产出不同的户型，只要数据有调整，就会新生成一个户型
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class HouseLayout extends TableImpl<HouseLayoutRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>house_layout</code>
     */
    public static final HouseLayout HOUSE_LAYOUT = new HouseLayout();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<HouseLayoutRecord> getRecordType() {
        return HouseLayoutRecord.class;
    }

    /**
     * The column <code>house_layout.id</code>. 主键id
     */
    public final TableField<HouseLayoutRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键id");

    /**
     * The column <code>house_layout.house_layout_code</code>. 户型code
     */
    public final TableField<HouseLayoutRecord, String> HOUSE_LAYOUT_CODE = createField(DSL.name("house_layout_code"), SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型code");

    /**
     * The column <code>house_layout.from_house_layout_code</code>. 户型来源code，如果户型是由别的户型编辑而来，记录来源code
     */
    public final TableField<HouseLayoutRecord, String> FROM_HOUSE_LAYOUT_CODE = createField(DSL.name("from_house_layout_code"), SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型来源code，如果户型是由别的户型编辑而来，记录来源code");

    /**
     * The column <code>house_layout.living_room_num</code>. 客厅数量
     */
    public final TableField<HouseLayoutRecord, Integer> LIVING_ROOM_NUM = createField(DSL.name("living_room_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "客厅数量");

    /**
     * The column <code>house_layout.bedroom_num</code>. 居室数量
     */
    public final TableField<HouseLayoutRecord, Integer> BEDROOM_NUM = createField(DSL.name("bedroom_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "居室数量");

    /**
     * The column <code>house_layout.kitchen_num</code>. 厨房数量
     */
    public final TableField<HouseLayoutRecord, Integer> KITCHEN_NUM = createField(DSL.name("kitchen_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "厨房数量");

    /**
     * The column <code>house_layout.toilet_num</code>. 卫生间数量
     */
    public final TableField<HouseLayoutRecord, Integer> TOILET_NUM = createField(DSL.name("toilet_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "卫生间数量");

    /**
     * The column <code>house_layout.dry_toilet_num</code>. 卫生间干区数量
     */
    public final TableField<HouseLayoutRecord, Integer> DRY_TOILET_NUM = createField(DSL.name("dry_toilet_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "卫生间干区数量");

    /**
     * The column <code>house_layout.balcony_num</code>. 阳台数量
     */
    public final TableField<HouseLayoutRecord, Integer> BALCONY_NUM = createField(DSL.name("balcony_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "阳台数量");

    /**
     * The column <code>house_layout.storeroom_num</code>. 储物间数量
     */
    public final TableField<HouseLayoutRecord, Integer> STOREROOM_NUM = createField(DSL.name("storeroom_num"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "储物间数量");

    /**
     * The column <code>house_layout.inside_area_value</code>. 套内面积
     */
    public final TableField<HouseLayoutRecord, BigDecimal> INSIDE_AREA_VALUE = createField(DSL.name("inside_area_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "套内面积");

    /**
     * The column <code>house_layout.inside_area_unit</code>. 套内面积单位
     */
    public final TableField<HouseLayoutRecord, String> INSIDE_AREA_UNIT = createField(DSL.name("inside_area_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "套内面积单位");

    /**
     * The column <code>house_layout.house_area_value</code>. 房屋面积
     */
    public final TableField<HouseLayoutRecord, BigDecimal> HOUSE_AREA_VALUE = createField(DSL.name("house_area_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "房屋面积");

    /**
     * The column <code>house_layout.house_area_unit</code>. 房屋面积单位
     */
    public final TableField<HouseLayoutRecord, String> HOUSE_AREA_UNIT = createField(DSL.name("house_area_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "房屋面积单位");

    /**
     * The column <code>house_layout.wall_inside_area_value</code>. 外墙内框面积
     */
    public final TableField<HouseLayoutRecord, BigDecimal> WALL_INSIDE_AREA_VALUE = createField(DSL.name("wall_inside_area_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "外墙内框面积");

    /**
     * The column <code>house_layout.wall_inside_area_unit</code>. 外墙内框面积单位
     */
    public final TableField<HouseLayoutRecord, String> WALL_INSIDE_AREA_UNIT = createField(DSL.name("wall_inside_area_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "外墙内框面积单位");

    /**
     * The column <code>house_layout.house_layout_file_url</code>. 户型数据详情（坐标json文件地址）
     */
    public final TableField<HouseLayoutRecord, String> HOUSE_LAYOUT_FILE_URL = createField(DSL.name("house_layout_file_url"), SQLDataType.VARCHAR(256).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型数据详情（坐标json文件地址）");

    /**
     * The column <code>house_layout.house_layout_image</code>. 户型设计图文件地址
     */
    public final TableField<HouseLayoutRecord, String> HOUSE_LAYOUT_IMAGE = createField(DSL.name("house_layout_image"), SQLDataType.VARCHAR(256).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型设计图文件地址");

    /**
     * The column <code>house_layout.marked_house_layout_image</code>. 户型标注设计图文件地址
     */
    public final TableField<HouseLayoutRecord, String> MARKED_HOUSE_LAYOUT_IMAGE = createField(DSL.name("marked_house_layout_image"), SQLDataType.VARCHAR(256).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型标注设计图文件地址");

    /**
     * The column <code>house_layout.status</code>. 户型数据状态，0-无效，1-草稿，2-正式
     */
    public final TableField<HouseLayoutRecord, Integer> STATUS = createField(DSL.name("status"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "户型数据状态，0-无效，1-草稿，2-正式");

    /**
     * The column <code>house_layout.cu_name</code>. 创建人
     */
    public final TableField<HouseLayoutRecord, String> CU_NAME = createField(DSL.name("cu_name"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "创建人");

    /**
     * The column <code>house_layout.cuid</code>. 创建人ucid
     */
    public final TableField<HouseLayoutRecord, Long> CUID = createField(DSL.name("cuid"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "创建人ucid");

    /**
     * The column <code>house_layout.mu_name</code>. 更新人
     */
    public final TableField<HouseLayoutRecord, String> MU_NAME = createField(DSL.name("mu_name"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "更新人");

    /**
     * The column <code>house_layout.muid</code>. 更新人ucid
     */
    public final TableField<HouseLayoutRecord, Long> MUID = createField(DSL.name("muid"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "更新人ucid");

    /**
     * The column <code>house_layout.ctime</code>. 创建时间 
     */
    public final TableField<HouseLayoutRecord, LocalDateTime> CTIME = createField(DSL.name("ctime"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间 ");

    /**
     * The column <code>house_layout.mtime</code>. 更新时间
     */
    public final TableField<HouseLayoutRecord, LocalDateTime> MTIME = createField(DSL.name("mtime"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    private HouseLayout(Name alias, Table<HouseLayoutRecord> aliased) {
        this(alias, aliased, null);
    }

    private HouseLayout(Name alias, Table<HouseLayoutRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("户型就是房子的结构与形状，一般与设计方案相关，不同的设计会产出不同的户型，只要数据有调整，就会新生成一个户型"), TableOptions.table());
    }

    /**
     * Create an aliased <code>house_layout</code> table reference
     */
    public HouseLayout(String alias) {
        this(DSL.name(alias), HOUSE_LAYOUT);
    }

    /**
     * Create an aliased <code>house_layout</code> table reference
     */
    public HouseLayout(Name alias) {
        this(alias, HOUSE_LAYOUT);
    }

    /**
     * Create a <code>house_layout</code> table reference
     */
    public HouseLayout() {
        this(DSL.name("house_layout"), null);
    }

    public <O extends Record> HouseLayout(Table<O> child, ForeignKey<O, HouseLayoutRecord> key) {
        super(child, key, HOUSE_LAYOUT);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<HouseLayoutRecord, Long> getIdentity() {
        return (Identity<HouseLayoutRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<HouseLayoutRecord> getPrimaryKey() {
        return Keys.KEY_HOUSE_LAYOUT_PRIMARY;
    }

    @Override
    public List<UniqueKey<HouseLayoutRecord>> getKeys() {
        return Arrays.<UniqueKey<HouseLayoutRecord>>asList(Keys.KEY_HOUSE_LAYOUT_PRIMARY, Keys.KEY_HOUSE_LAYOUT_UNIQ_IDX_CODE);
    }

    @Override
    public HouseLayout as(String alias) {
        return new HouseLayout(DSL.name(alias), this);
    }

    @Override
    public HouseLayout as(Name alias) {
        return new HouseLayout(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public HouseLayout rename(String name) {
        return new HouseLayout(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public HouseLayout rename(Name name) {
        return new HouseLayout(name, null);
    }
}
