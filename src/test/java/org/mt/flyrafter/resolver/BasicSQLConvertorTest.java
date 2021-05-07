package org.mt.flyrafter.resolver;

import ch.qos.logback.core.db.dialect.DBUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import javax.sql.DataSource;

import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Moon Wu
 * @date 2021/4/23
 */
class BasicSQLConvertorTest {
    static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mortnon?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PWD = "123456";
    private static DataSource dataSource;

    @BeforeAll
    static void before() {
        try {
            Class.forName(DB_DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            Connection connection = DriverManager.getConnection(DB_URL, USER, PWD);

            dataSource = mock(DataSource.class);
            when(dataSource.getConnection()).thenReturn(connection);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    @Test
    void testConvert() {
        AnnotationProcessor annotationProcessor = new AnnotationProcessor();
        SQLConvertor convertor = new BasicSQLConvertor(annotationProcessor, dataSource);
        StringBuffer convert = convertor.convert();
        assertNotNull(convert.toString());
    }
}