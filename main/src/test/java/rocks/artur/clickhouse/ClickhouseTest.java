package rocks.artur.clickhouse;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;






@ActiveProfiles("clickhouse")
@Testcontainers
@SpringBootTest
public class ClickhouseTest {


    @Container
    private static final ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("clickhouse/clickhouse-server:latest");

    @DynamicPropertySource
    static void registerClickHouseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", clickHouseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", clickHouseContainer::getUsername);
        registry.add("spring.datasource.password", clickHouseContainer::getPassword);
    }



    private static Connection connection;

    @BeforeAll
    public static void setUp() throws Exception {
        clickHouseContainer.start();
        connection = DriverManager.getConnection(
                clickHouseContainer.getJdbcUrl(),
                clickHouseContainer.getUsername(),
                clickHouseContainer.getPassword()
        );
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (connection != null) {
            connection.close();
        }
        clickHouseContainer.stop();
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void testInsertAndSelect() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS test_table (id Int32, name String) ENGINE = Memory");
        jdbcTemplate.execute("INSERT INTO test_table (id, name) VALUES (1, 'Hello')");

        String name = jdbcTemplate.queryForObject("SELECT name FROM test_table WHERE id=1", String.class);
        assertEquals("Hello",name);
    }

    @Test
    public void testDatabaseConnection() throws Exception {
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE test_table (id Int32, name String) ENGINE = Memory;");
        statement.execute("INSERT INTO test_table (id, name) VALUES (1, 'Test');");

        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_table;");
        resultSet.next();
        int count = resultSet.getInt(1);

        assertEquals(1, count);
    }
}