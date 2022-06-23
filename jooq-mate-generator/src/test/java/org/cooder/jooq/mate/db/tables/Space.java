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
import org.cooder.jooq.mate.db.tables.records.SpaceRecord;
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
 * 不同视图下户型的组成单元，既包含单空间也包含组合空间
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Space extends TableImpl<SpaceRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>space</code>
     */
    public static final Space SPACE = new Space();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<SpaceRecord> getRecordType() {
        return SpaceRecord.class;
    }

    /**
     * The column <code>space.id</code>. 主键id
     */
    public final TableField<SpaceRecord, Long> ID = createField(DSL.name("id"), SQLDataType.BIGINT.nullable(false).identity(true), this, "主键id");

    /**
     * The column <code>space.space_code</code>. 空间code
     */
    public final TableField<SpaceRecord, String> SPACE_CODE = createField(DSL.name("space_code"), SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "空间code");

    /**
     * The column <code>space.space_name</code>. 空间名称
     */
    public final TableField<SpaceRecord, String> SPACE_NAME = createField(DSL.name("space_name"), SQLDataType.VARCHAR(128).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "空间名称");

    /**
     * The column <code>space.house_layout_code</code>. 户型code
     */
    public final TableField<SpaceRecord, String> HOUSE_LAYOUT_CODE = createField(DSL.name("house_layout_code"), SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "户型code");

    /**
     * The column <code>space.space_usage</code>. 单空间：1-客厅，3-餐厅，4-主卧，5-次卧，6-书房，7-厨房，8-卫生间，9-阳台，10-卫生间干区，20-厨房阳台，21-居室阳台，32-次卫，34-1.5卫卫生间，35-客厅阳台; 组合空间：101-主卧及阳台，102-客厅、餐厅及阳台，103-厨房，104-卫生间，105-次卧，106-书房，107-次卫，108-全屋，109-1.5卫卫生间，110-卫生间干区
     */
    public final TableField<SpaceRecord, Integer> SPACE_USAGE = createField(DSL.name("space_usage"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "单空间：1-客厅，3-餐厅，4-主卧，5-次卧，6-书房，7-厨房，8-卫生间，9-阳台，10-卫生间干区，20-厨房阳台，21-居室阳台，32-次卫，34-1.5卫卫生间，35-客厅阳台; 组合空间：101-主卧及阳台，102-客厅、餐厅及阳台，103-厨房，104-卫生间，105-次卧，106-书房，107-次卫，108-全屋，109-1.5卫卫生间，110-卫生间干区");

    /**
     * The column <code>space.is_regular</code>. 是否规则空间：0-否，1-是
     */
    public final TableField<SpaceRecord, Integer> IS_REGULAR = createField(DSL.name("is_regular"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "是否规则空间：0-否，1-是");

    /**
     * The column <code>space.is_combo</code>. 是否组合空间：0-否，1-是
     */
    public final TableField<SpaceRecord, Integer> IS_COMBO = createField(DSL.name("is_combo"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "是否组合空间：0-否，1-是");

    /**
     * The column <code>space.floor</code>. 楼层
     */
    public final TableField<SpaceRecord, Integer> FLOOR = createField(DSL.name("floor"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("1", SQLDataType.INTEGER)), this, "楼层");

    /**
     * The column <code>space.perimeter_value</code>. 周长
     */
    public final TableField<SpaceRecord, BigDecimal> PERIMETER_VALUE = createField(DSL.name("perimeter_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "周长");

    /**
     * The column <code>space.perimeter_unit</code>. 周长单位
     */
    public final TableField<SpaceRecord, String> PERIMETER_UNIT = createField(DSL.name("perimeter_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "周长单位");

    /**
     * The column <code>space.ground_area_value</code>. 地面面积
     */
    public final TableField<SpaceRecord, BigDecimal> GROUND_AREA_VALUE = createField(DSL.name("ground_area_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "地面面积");

    /**
     * The column <code>space.ground_area_unit</code>. 地面面积单位
     */
    public final TableField<SpaceRecord, String> GROUND_AREA_UNIT = createField(DSL.name("ground_area_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "地面面积单位");

    /**
     * The column <code>space.wall_area_value</code>. 墙面面积
     */
    public final TableField<SpaceRecord, BigDecimal> WALL_AREA_VALUE = createField(DSL.name("wall_area_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "墙面面积");

    /**
     * The column <code>space.wall_area_unit</code>. 墙面面积单位
     */
    public final TableField<SpaceRecord, String> WALL_AREA_UNIT = createField(DSL.name("wall_area_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "墙面面积单位");

    /**
     * The column <code>space.length_value</code>. 长
     */
    public final TableField<SpaceRecord, BigDecimal> LENGTH_VALUE = createField(DSL.name("length_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "长");

    /**
     * The column <code>space.length_unit</code>. 长单位
     */
    public final TableField<SpaceRecord, String> LENGTH_UNIT = createField(DSL.name("length_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "长单位");

    /**
     * The column <code>space.width_value</code>. 宽
     */
    public final TableField<SpaceRecord, BigDecimal> WIDTH_VALUE = createField(DSL.name("width_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "宽");

    /**
     * The column <code>space.width_unit</code>. 宽单位
     */
    public final TableField<SpaceRecord, String> WIDTH_UNIT = createField(DSL.name("width_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "宽单位");

    /**
     * The column <code>space.height_value</code>. 层高
     */
    public final TableField<SpaceRecord, BigDecimal> HEIGHT_VALUE = createField(DSL.name("height_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "层高");

    /**
     * The column <code>space.height_unit</code>. 层高单位
     */
    public final TableField<SpaceRecord, String> HEIGHT_UNIT = createField(DSL.name("height_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "层高单位");

    /**
     * The column <code>space.window_linear_meters_value</code>. 窗户延米
     */
    public final TableField<SpaceRecord, BigDecimal> WINDOW_LINEAR_METERS_VALUE = createField(DSL.name("window_linear_meters_value"), SQLDataType.DECIMAL(10, 2).nullable(false).defaultValue(DSL.inline("0.00", SQLDataType.DECIMAL)), this, "窗户延米");

    /**
     * The column <code>space.window_linear_meters_unit</code>. 窗户延米单位
     */
    public final TableField<SpaceRecord, String> WINDOW_LINEAR_METERS_UNIT = createField(DSL.name("window_linear_meters_unit"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "窗户延米单位");

    /**
     * The column <code>space.order</code>. 空间的显示顺序
     */
    public final TableField<SpaceRecord, Integer> ORDER = createField(DSL.name("order"), SQLDataType.INTEGER.nullable(false).defaultValue(DSL.inline("0", SQLDataType.INTEGER)), this, "空间的显示顺序");

    /**
     * The column <code>space.parent_code</code>. 空间具有组合关系时，存储父空间code
     */
    public final TableField<SpaceRecord, String> PARENT_CODE = createField(DSL.name("parent_code"), SQLDataType.VARCHAR(64).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "空间具有组合关系时，存储父空间code");

    /**
     * The column <code>space.cu_name</code>. 创建人
     */
    public final TableField<SpaceRecord, String> CU_NAME = createField(DSL.name("cu_name"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "创建人");

    /**
     * The column <code>space.cuid</code>. 创建人ucid
     */
    public final TableField<SpaceRecord, Long> CUID = createField(DSL.name("cuid"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "创建人ucid");

    /**
     * The column <code>space.mu_name</code>. 更新人
     */
    public final TableField<SpaceRecord, String> MU_NAME = createField(DSL.name("mu_name"), SQLDataType.VARCHAR(32).nullable(false).defaultValue(DSL.inline("", SQLDataType.VARCHAR)), this, "更新人");

    /**
     * The column <code>space.muid</code>. 更新人ucid
     */
    public final TableField<SpaceRecord, Long> MUID = createField(DSL.name("muid"), SQLDataType.BIGINT.nullable(false).defaultValue(DSL.inline("0", SQLDataType.BIGINT)), this, "更新人ucid");

    /**
     * The column <code>space.ctime</code>. 创建时间 
     */
    public final TableField<SpaceRecord, LocalDateTime> CTIME = createField(DSL.name("ctime"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "创建时间 ");

    /**
     * The column <code>space.mtime</code>. 更新时间
     */
    public final TableField<SpaceRecord, LocalDateTime> MTIME = createField(DSL.name("mtime"), SQLDataType.LOCALDATETIME(0).nullable(false).defaultValue(DSL.field("CURRENT_TIMESTAMP", SQLDataType.LOCALDATETIME)), this, "更新时间");

    private Space(Name alias, Table<SpaceRecord> aliased) {
        this(alias, aliased, null);
    }

    private Space(Name alias, Table<SpaceRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment("不同视图下户型的组成单元，既包含单空间也包含组合空间"), TableOptions.table());
    }

    /**
     * Create an aliased <code>space</code> table reference
     */
    public Space(String alias) {
        this(DSL.name(alias), SPACE);
    }

    /**
     * Create an aliased <code>space</code> table reference
     */
    public Space(Name alias) {
        this(alias, SPACE);
    }

    /**
     * Create a <code>space</code> table reference
     */
    public Space() {
        this(DSL.name("space"), null);
    }

    public <O extends Record> Space(Table<O> child, ForeignKey<O, SpaceRecord> key) {
        super(child, key, SPACE);
    }

    @Override
    public Schema getSchema() {
        return DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<SpaceRecord, Long> getIdentity() {
        return (Identity<SpaceRecord, Long>) super.getIdentity();
    }

    @Override
    public UniqueKey<SpaceRecord> getPrimaryKey() {
        return Keys.KEY_SPACE_PRIMARY;
    }

    @Override
    public List<UniqueKey<SpaceRecord>> getKeys() {
        return Arrays.<UniqueKey<SpaceRecord>>asList(Keys.KEY_SPACE_PRIMARY, Keys.KEY_SPACE_UNIQ_IDX_SPACE_CODE);
    }

    @Override
    public Space as(String alias) {
        return new Space(DSL.name(alias), this);
    }

    @Override
    public Space as(Name alias) {
        return new Space(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Space rename(String name) {
        return new Space(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Space rename(Name name) {
        return new Space(name, null);
    }
}