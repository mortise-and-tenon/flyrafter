package org.mt.flyrafter.resolver.template;

/**
 * 创建表相关模板
 *
 * @author Moon Wu
 * @date 2021/4/23
 */
public interface CreateSQLTemplate {
    String TABLE_PREFIX = "CREATE TABLE `%S` (";
    String COLUMN = " `%s` %s";
    String PRIMARY_KEY = "PRIMARY KEY (`%s`)";
    String TABLE_SUFFIX = ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin;";
}
