package org.mt.flyrafter;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mt.flyrafter.configuration.FlyRafterConfiguration;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
class FlyRafterTest {
    static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";
    static final String DB_URL = "jdbc:mysql://localhost:3306/mortnon?useSSL=false&serverTimezone=UTC";
    static final String USER = "root";
    static final String PWD = "123456";
    private static DataSource dataSource;
    private static FlyRafterConfiguration configuration;

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

        configuration = new FlyRafterConfiguration();
        configuration.setBackup("filesystem:C:\\projects\\mortnon\\test");

    }

    @Test
    void migrateTest() {
        FlyRafter flyRafter = new FlyRafter(configuration, dataSource);
        flyRafter.startup();
    }
}