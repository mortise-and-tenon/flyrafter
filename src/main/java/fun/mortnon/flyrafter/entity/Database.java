package fun.mortnon.flyrafter.entity;

import lombok.Data;

import java.util.LinkedHashSet;

/**
 * 数据库
 *
 * @author Moon Wu
 * @date 2021/4/22
 */
@Data
public class Database {
    private String name;
    private LinkedHashSet<DbTable> tableSet;
}
