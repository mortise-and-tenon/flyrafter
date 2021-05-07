package org.mt.flyrafter.resolver;

import org.mt.flyrafter.entity.DbTable;

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

    public SQLConvertor(AnnotationProcessor annotationProcessor, DataSource dataSource) {
        this.annotationProcessor = annotationProcessor;
        this.dataSource = dataSource;
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
