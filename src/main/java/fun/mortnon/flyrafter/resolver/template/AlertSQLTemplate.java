package fun.mortnon.flyrafter.resolver.template;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
public interface AlertSQLTemplate {
    String ALTER_PREFIX = "ALTER TABLE `%s` ";
    String MODIFY_COLUMN = "MODIFY COLUMN `%s` %s";
    String ADD_COLUMN = "ADD `%s` %s";
    String ADD_PRIMARY_KEY = "ADD PRIMARY KEY(`%s`)";
    String DROP_COLUMN = "DROP COLUMN `%s`";

}
