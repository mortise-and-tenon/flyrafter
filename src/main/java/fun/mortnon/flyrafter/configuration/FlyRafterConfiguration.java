package fun.mortnon.flyrafter.configuration;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import fun.mortnon.flyrafter.resolver.Constants;
import fun.mortnon.flyrafter.resolver.FlyRafterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/4/28
 */
@Configuration
@ConfigurationProperties(prefix = "flyrafter")
@Data
@Slf4j
public class FlyRafterConfiguration {
    @Autowired
    private FlywayProperties flywayProperties;

    /**
     * Whether to enable flyrafter.
     */
    private boolean enabled = true;

    /**
     * Version pattern for flyRafter sql file.
     */
    private String versionPattern = "0";

    /**
     * Comma-separated list sql file location.
     */
    private List<String> locations;

    /**
     * Sql file backup folder.sample:
     * 1. "filesystem:/var/flyrafter-backup"
     * 2. "classpath:debug/flyrafter-backup"
     */
    private String backup = "";

    /**
     * Whether to map name to underscore style in sql file.
     */
    private boolean mapToUnderscore = true;

    /**
     * Whether to copy generate sql file to source code resources folder.
     */
    private boolean copyToSource = true;

    /**
     * flyway 记录表名
     *
     * @return
     */
    public String getTableName() {
        if (null != flywayProperties) {
            return flywayProperties.getTable();
        }
        return "";
    }

    /**
     * 获取 sql 目录
     *
     * @return
     */
    public List<String> getLocations() {
        if (null != flywayProperties && flywayProperties.getLocations().size() > 0) {
            return flywayProperties.getLocations();
        }
        if (null != locations && locations.size() > 0) {
            return locations;
        }

        return new ArrayList<String>() {
            {
                add(Constants.DEFAULT_LOCATION);
            }
        };
    }

    public String getBackup() {
        return backup;
    }

    /**
     * sql 文件前缀
     *
     * @return
     */
    public String getPrefix() {
        if (null != flywayProperties) {
            return flywayProperties.getSqlMigrationPrefix();
        }
        return Constants.DEFAULT_SQL_PREFIX;
    }

    /**
     * sql 版本与描述分隔符
     *
     * @return
     */
    public String getSeparator() {
        if (null != flywayProperties) {
            return flywayProperties.getSqlMigrationSeparator();
        }
        return Constants.DEFAULT_SQL_SEPARATOR;
    }

    /**
     * sql 文件名
     *
     * @return
     */
    public String getSuffix() {
        if (null != flywayProperties && flywayProperties.getSqlMigrationSuffixes().size() > 0) {
            return flywayProperties.getSqlMigrationSuffixes().get(0);
        }

        return Constants.DEFAULT_SQL_SUFFIX;
    }

    /**
     * 获取 flyway sql 文件名模板
     *
     * @return
     */
    public String getFileTemplate() {
        return String.format("%sT%sT_flyrafter%s", getPrefix(), getSeparator(), getSuffix()).replaceAll("T", "%s");
    }
}
