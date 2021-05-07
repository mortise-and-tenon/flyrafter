package org.mt.flyrafter.configuration;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.mt.flyrafter.resolver.Constants;
import org.mt.flyrafter.resolver.FlyRafterUtils;
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
     * 是否启用 flyrafter.
     */
    private boolean enabled = true;

    /**
     * Version pattern for flyRafter sql file.
     * flyRafter 生成 sql 文件版本号样式
     */
    private String versionPattern = "0";

    /**
     * Comma-separated list sql file location.
     * 逗号分隔的 sql 目录
     * 目录支持两种类型：
     * <ul>
     *     <li><b>filesystem:</b> 指定系统目录，示例：<i>filesystem:/root/db/migration</i></li>
     *     <li><b>classpath:</b> 指定的classpath下目录，示例：<i>classpath:db/migration</i></li>
     * </ul>
     */
    private List<String> locations;

    /**
     * Sql file backup folder.
     */
    private String backup;

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
        if (StringUtils.isBlank(backup)) {
            return String.format("%s:%s%s%s", Constants.FILE_SYSTEM,
                    FlyRafterUtils.currentLocation(), File.separator, Constants.DEFAULT_BACKUP);
        }

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
