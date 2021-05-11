package fun.mortnon.flyrafter.resolver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Moon Wu
 * @date 2021/4/25
 */
class FileCreatorTest {

    private static FlyRafterConfiguration configuration;

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


        configuration = new FlyRafterConfiguration();
        FlywayProperties flywayProperties = new FlywayProperties();
        flywayProperties.setTable("flyway_schema_history");

        configuration.setVersionPattern("1.0.0");
        configuration.setFlywayProperties(flywayProperties);
    }

    @Test
    void createSQLFile() {
        FileCreator fileCreator = new FileCreator();
        boolean sqlFile = fileCreator.createSQLFile("show tables;", "V123_.sql", configuration);
        assertTrue(sqlFile);
    }
}