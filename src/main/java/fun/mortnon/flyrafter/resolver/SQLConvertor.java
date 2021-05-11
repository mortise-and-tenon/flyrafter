package fun.mortnon.flyrafter.resolver;

import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import fun.mortnon.flyrafter.entity.DbTable;

import javax.sql.DataSource;
import java.util.List;

/**
 * SQL语句转换生成器
 *
 * @author Moon Wu
 * @date 2021/4/23
 */
public abstract class SQLConvertor implements Constants {
    private AnnotationProcessor annotationProcessor;
    protected DataSource dataSource;
    protected FlyRafterConfiguration configuration;

    public SQLConvertor(AnnotationProcessor annotationProcessor, DataSource dataSource, FlyRafterConfiguration configuration) {
        this.annotationProcessor = annotationProcessor;
        this.dataSource = dataSource;
        this.configuration = configuration;
    }

    public StringBuffer convert() {
        List<DbTable> tableList = annotationProcessor.process();
        return internalConvert(tableList);
    }

    protected abstract StringBuffer internalConvert(List<DbTable> tableList);

    /**
     * 获取换行符
     *
     * @return
     */
    protected String lineSeparator() {
        return System.getProperty(LINE_SEPARATOR_KEY);
    }
}
