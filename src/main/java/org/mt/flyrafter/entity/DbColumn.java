package org.mt.flyrafter.entity;

import lombok.Data;
import org.mt.flyrafter.enums.ActionEnum;

/**
 * 数据库表字段
 *
 * @author Moon Wu
 * @date 2021/4/22
 */
@Data
public class DbColumn {
    private String name;
    private String definition;
    private Boolean primaryKey = false;
    private ActionEnum action = ActionEnum.ADD;
}
