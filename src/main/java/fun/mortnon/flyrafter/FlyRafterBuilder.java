package fun.mortnon.flyrafter;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;

import javax.sql.DataSource;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
public class FlyRafterBuilder {
    private FlyRafterConfiguration configuration;
    private DataSource dataSource;

    /**
     *
     * @param configuration
     * @param dataSource
     */
    public FlyRafterBuilder(FlyRafterConfiguration configuration, DataSource dataSource) {
        this.configuration = configuration;
        this.dataSource = dataSource;
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
     * 生成 FlyRafter 实例
     *
     * @return
     */
    public FlyRafter build() {
        //TODO 判断对象不为空
        if (null == dataSource) {
            throw new NullPointerException("datasource is null.");
        }

        return new FlyRafter(configuration, dataSource);
    }
}
