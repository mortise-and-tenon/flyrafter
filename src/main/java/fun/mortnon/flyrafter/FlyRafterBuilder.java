package fun.mortnon.flyrafter;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.net.URLClassLoader;
import java.util.List;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
@Slf4j
public class FlyRafterBuilder {
    private FlyRafterConfiguration configuration;
    private DataSource dataSource;
    private URLClassLoader classLoader;
    private List<String> excludePackages;

    /**
     * @param configuration
     * @param dataSource
     */
    public FlyRafterBuilder(FlyRafterConfiguration configuration, DataSource dataSource) {
        this.configuration = configuration;
        this.dataSource = dataSource;
    }

    public FlyRafterBuilder(FlyRafterConfiguration configuration, DataSource dataSource, URLClassLoader classLoader) {
        this(configuration, dataSource);
        this.classLoader = classLoader;
    }

    public FlyRafterBuilder(FlyRafterConfiguration configuration, DataSource dataSource, URLClassLoader classLoader, List<String> excludePackages) {
        this(configuration, dataSource);
        this.classLoader = classLoader;
        this.excludePackages = excludePackages;
    }

    public FlyRafterBuilder() {

    }

    /**
     * 设置 FlyRafter 配置类
     *
     * @param configuration
     * @return
     */
    public FlyRafterBuilder bindConfiguration(FlyRafterConfiguration configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * 设置数据源
     *
     * @param dataSource
     * @return
     */
    public FlyRafterBuilder bindDatasource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    /**
     * 设置类加载器
     *
     * @param classLoader
     * @return
     */
    public FlyRafterBuilder bindClassLoader(URLClassLoader classLoader) {
        this.classLoader = classLoader;
        return this;
    }

    public FlyRafterBuilder excludePackages(List<String> excludePackages) {
        this.excludePackages = excludePackages;
        return this;
    }

    /**
     * 生成 FlyRafter 实例
     *
     * @return
     */
    public FlyRafter build() {
        if (null == dataSource) {
            log.info("datasource is null.");
        }

        return new FlyRafter(configuration, dataSource, classLoader, excludePackages);
    }
}
