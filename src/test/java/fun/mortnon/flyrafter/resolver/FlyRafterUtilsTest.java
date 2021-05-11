package fun.mortnon.flyrafter.resolver;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.StringUtils;
import fun.mortnon.flyrafter.configuration.FlyRafterConfiguration;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;

import javax.sql.DataSource;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Moon Wu
 * @date 2021/4/26
 */
class FlyRafterUtilsTest {
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
    void generateFileName() {
        String create_table = new FlyRafterUtils(dataSource, configuration).generateFileName("create table");
        assertTrue(create_table.startsWith("V1.2__"));
    }

    @Test
    void sqlFolderTest() {
        String s = FlyRafterUtils.fullPath("classpath:db/migration");
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    void sqlFolderTest2() {
        URL resource = this.getClass().getClassLoader().getResource("");
        String path = resource.getPath().replaceFirst("/", Constants.FILE_SYSTEM + ":");
        String s = FlyRafterUtils.fullPath(path + "db/migration");
        assertTrue(StringUtils.isNotBlank(s));
    }

    @Test
    void applicationPathTest() {
        String s = FlyRafterUtils.currentLocation();
        assertTrue(true);
    }

    private void convertUnderscore(String name, String expect) {
        String s = FlyRafterUtils.convertToUnderscore(name);
        assertEquals(expect, s);
    }

    private Map<String, String> nameMap = new HashMap<>();

    void initNameMap() {
        nameMap.put("aaBb", "aa_bb");
        nameMap.put("aaBB", "aa_b_b");
        nameMap.put("aaBbCc", "aa_bb_cc");
        nameMap.put("aa_Bb", "aa__bb");
        nameMap.put("AaBb", "Aa_bb");
        nameMap.put("_aBC", "_a_b_c");
        nameMap.put("aBC","a_b_c");
        nameMap.put("ABC", "A_b_c");
        nameMap.put("_ABC","__a_b_c");
    }

    @Test
    void convertToUnderscoreTest() {
        initNameMap();
        nameMap.forEach((k, v) -> convertUnderscore(k, v));
    }

    private void convertCamel(String name, String expect) {
        String s = FlyRafterUtils.convertToCamelcase(name);
        assertEquals(expect, s);
    }

    @Test
    void convertToCamelTest() {
        initNameMap();
        nameMap.forEach((k, v) -> convertCamel(v, k));
    }

    @Test
    void nameEqualsTest() {
        initNameMap();
        nameMap.forEach((k,v)->{
            assertTrue(FlyRafterUtils.nameEquals(k, v));
            assertTrue(FlyRafterUtils.nameEquals(k, k));
            assertTrue(FlyRafterUtils.nameEquals(v, v));
            assertTrue(FlyRafterUtils.nameEquals(v, k));
        });
    }
}