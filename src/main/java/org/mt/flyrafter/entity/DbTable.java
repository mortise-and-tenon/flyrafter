package org.mt.flyrafter.entity;

import lombok.Data;
import org.mt.flyrafter.enums.ActionEnum;

import java.util.LinkedHashSet;

/**
 * 数据库表
 *
 * @author Moon Wu
 * @date 2021/4/22
 */
@Data
public class DbTable {
    private String name;
    private LinkedHashSet<DbColumn> columnSet;
    private ActionEnum action = ActionEnum.ADD;
}
