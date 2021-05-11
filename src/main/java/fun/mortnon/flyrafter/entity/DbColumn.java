package fun.mortnon.flyrafter.entity;

import fun.mortnon.flyrafter.enums.ActionEnum;
import lombok.Data;

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
