package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.exception.NoColumnException;
import fun.mortnon.flyrafter.exception.NoPrimaryKeyException;
import fun.mortnon.flyrafter.resolver.template.AlertSQLTemplate;
import fun.mortnon.flyrafter.resolver.template.CreateSQLTemplate;
import fun.mortnon.flyrafter.resolver.template.DropSQLTemplate;
import lombok.extern.slf4j.Slf4j;
import fun.mortnon.flyrafter.enums.ActionEnum;
import fun.mortnon.flyrafter.entity.DbColumn;
import fun.mortnon.flyrafter.entity.DbTable;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 创建表 SQL 转换器
 *
 * @author Moon Wu
 * @date 2021/4/23
 */
@Slf4j
public class BasicSQLConvertor extends SQLConvertor {
    private boolean ignorePrimaryKey = true;

    public BasicSQLConvertor(AnnotationProcessor annotationProcessor, DataSource dataSource, FlyRafterConfiguration configuration) {
        super(annotationProcessor, dataSource, configuration);
    }

    @Override
    protected StringBuffer internalConvert(List<DbTable> tableList) {
        return generateSql(tableList);
    }

    /**
     * 生成表操作 SQL
     *
     * @param tableList
     * @return
     */
    private StringBuffer generateSql(List<DbTable> tableList) {
        StringBuffer sql = new StringBuffer();
        try {
            parseDb(tableList);
        } catch (SQLException e) {
            log.error("parse db data fail for ", e);
            return sql;
        }


        for (DbTable table : tableList) {
            sql.append(lineSeparator());
            switch (table.getAction()) {
                case MODIFY:
                    sql.append(alertSQL(table));
                    break;
                case REMOVE:
                    sql.append(dropSQL(table));
                    break;
                default:
                    sql.append(createSQL(table));
            }
        }

        return sql;
    }

    private void parseDb(List<DbTable> tableList) throws SQLException {
        //如果数据源为空，不进行更多处理，按默认的操作类型
        if (null == dataSource) {
            return;
        }

        List<String> dbTableList = new ArrayList<>();

        Connection connection = dataSource.getConnection();
        DatabaseMetaData metaData = connection.getMetaData();
        ResultSet tables = metaData.getTables(connection.getCatalog(), null, "%", null);
        while (tables.next()) {
            String tableName = tables.getString(3);
            if (IGNORE_FLYWAY_TABLE.equalsIgnoreCase(tableName)) {
                continue;
            }

            dbTableList.add(tableName);


            List<String> dbColumnList = new ArrayList<>();
            ResultSet columns = metaData.getColumns(connection.getCatalog(), "%", tableName, "%");
            while (columns.next()) {
                String columnName = columns.getString("COLUMN_NAME");
                dbColumnList.add(columnName);
            }

            //TODO: 细化检测定义是否变更，并调整表名、字段名的驼峰等风格不一格的判定
            //如果是已有列，标记为修改，否则默认为添加
            tableList.stream().filter(k -> k.getName().equalsIgnoreCase(tableName))
                    .findAny()
                    .ifPresent(j -> j.getColumnSet().stream().forEach(m -> {
                        if (dbColumnList.contains(m.getName())) {
                            m.setAction(ActionEnum.MODIFY);
                        }
                    }));

            //如果实体属性不存在，标记删除列
            tableList.stream().filter(j -> tableName.equalsIgnoreCase(convertName(j.getName())))
                    .findAny().ifPresent(t -> {
                dbColumnList.stream().filter(k -> t.getColumnSet().stream().noneMatch(m -> convertName(m.getName()).equalsIgnoreCase(k)))
                        .forEach(n -> {
                            DbColumn delColumn = new DbColumn();
                            delColumn.setName(n);
                            delColumn.setAction(ActionEnum.REMOVE);
                            t.getColumnSet().add(delColumn);
                        });
            });


        }


        //如果库中表已存在，标记表为修改
        tableList.stream().filter(k -> dbTableList.contains(convertName(k.getName())))
                .forEach(table -> table.setAction(ActionEnum.MODIFY));

        //如果实体不存在，标记删除表
        dbTableList.stream().filter(k -> tableList.stream().noneMatch(j -> convertName(j.getName()).equalsIgnoreCase(k)))
                .forEach(t -> {
                    DbTable delTable = new DbTable();
                    delTable.setName(t);
                    delTable.setAction(ActionEnum.REMOVE);
                    tableList.add(delTable);
                });
    }

    private String convertName(String name) {
        if (configuration.getMapToUnderscore()) {
            return FlyRafterUtils.convertToUnderscore(name);
        }
        return name;
    }


    /**
     * 实体对应表不存在的情况，全新创建表
     *
     * @param table
     * @return
     */
    private StringBuffer createSQL(DbTable table) {
        StringBuffer tableSql = new StringBuffer();
        tableSql.append(String.format(CreateSQLTemplate.TABLE_PREFIX, convertName(table.getName())));

        List<String> columnSqlList = table.getColumnSet().stream()
                .map(column -> String.format(CreateSQLTemplate.COLUMN, convertName(column.getName()), column.getDefinition()))
                .collect(Collectors.toList());

        //如果列全为空，抛出异常
        if (columnSqlList.size() == 0) {
            throw new NoColumnException(table.getName());
        }
        tableSql.append(String.join(",", columnSqlList));

        if (!ignorePrimaryKey) {
            DbColumn dbColumn = table.getColumnSet().stream().filter(column -> column.getPrimaryKey()).findAny().orElseThrow(() -> new NoPrimaryKeyException(table.getName()));

            tableSql.append(String.format(CreateSQLTemplate.PRIMARY_KEY, convertName(dbColumn.getName())));
        }

        tableSql.append(CreateSQLTemplate.TABLE_SUFFIX);

        return tableSql;
    }

    /**
     * 没有实体对应的表，删除
     *
     * @param table
     * @return
     */
    private StringBuffer dropSQL(DbTable table) {
        StringBuffer sql = new StringBuffer();
        sql.append(String.format(DropSQLTemplate.DROP_TABLE, convertName(table.getName())));
        sql.append(lineSeparator());
        return sql;
    }

    /**
     * 实体对应的表字段有变化
     * 删除无实体属性的列
     * 添加表不存在的实体属性
     * 修改实体属性定义变化的列
     *
     * @param table
     * @return
     */
    private StringBuffer alertSQL(DbTable table) {
        StringBuffer sql = new StringBuffer();
        sql.append(lineSeparator());
        sql.append(String.format(AlertSQLTemplate.ALTER_PREFIX, table.getName()));
        for (DbColumn column : table.getColumnSet()) {
            String name = convertName(column.getName());
            switch (column.getAction()) {
                case REMOVE:
                    sql.append(String.format(AlertSQLTemplate.DROP_COLUMN, name));
                    break;
                case MODIFY:
                    sql.append(String.format(AlertSQLTemplate.MODIFY_COLUMN, name, column.getDefinition()));
                    break;
                default:
                    sql.append(String.format(AlertSQLTemplate.ADD_COLUMN, name, column.getDefinition()));
                    break;
            }
            sql.append(",");
        }
        sql.replace(sql.length() - 1, sql.length(), ";");
        return sql;
    }
}
