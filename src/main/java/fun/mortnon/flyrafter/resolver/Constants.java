package fun.mortnon.flyrafter.resolver;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
public interface Constants {
    /**
     * 类加载协议-文件
     */
    String FILE_PROTOCOL = "file";

    /**
     * 类加载协议-jar包
     */
    String JAR_PROTOCOL = "jar";

    /**
     * class 文件后续
     */
    String CLASS_SUFFIX = ".class";

    /**
     * 分隔符 .
     */
    char SPLIT_DOT = '.';

    /**
     * 编码-UTF8
     */
    String UTF8 = "UTF-8";

    /**
     * MD5
     */
    String MD5 = "MD5";

    /**
     * 系统换行符属性 key
     */
    String LINE_SEPARATOR_KEY = "line.separator";

    /**
     * 字符串的 .
     */
    String SPLIT_DOT_STR = String.valueOf(SPLIT_DOT);

    /**
     * 用于正则的点分隔符
     */
    String REGEX_SPLIT_DOT = "\\.";

    /**
     * 默认 SQL 脚本后缀
     */
    String DEFAULT_SQL_SUFFIX = ".sql";

    /**
     * 资源路径前缀
     */
    String CLASSPATH = "classpath:";

    /**
     * 查询 flyway 版本记录表数据的 sql
     */
    String SELECT_FLYWAY_TABLE = "SELECT script FROM %s WHERE script LIKE 'V%%' AND  success = 1 ORDER BY installed_rank DESC LIMIT 1;";

    /**
     * flyway 版本记录表 sql 文件名字段
     */
    String FLYWAY_SCRIPT_COLUMN = "script";

    /**
     * 文件路径协议头-filesystem
     */
    String FILE_SYSTEM = "filesystem";

    /**
     * 字符串属性默认的字段长度
     */
    int STRING_DEFAULT_LENGTH = 256;

    /**
     * sql 文件默认前缀
     */
    String DEFAULT_SQL_PREFIX = "V";

    /**
     * sql 文件默认分隔符
     */
    String DEFAULT_SQL_SEPARATOR = "__";

    /**
     * sql 文件默认目录
     */
    String DEFAULT_LOCATION = "classpath:db/migration";

    /**
     * 默认的 sql 文件备份目录
     */
    String DEFAULT_BACKUP = "backup";

    /**
     * 下划线符号
     */
    Character UNDERSCORE = '_';

    /**
     * 需要忽略的 flyway 表
     */
    String IGNORE_FLYWAY_TABLE = "flyway_schema_history";

    /**
     * 编译目标目录标识
     */
    String TARGET_PATH = "target";

    /**
     * URL 分隔符 /
     */
    char URL_SEPARATOR = '/';
}
