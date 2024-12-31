package rocks.artur;

import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ClickHouseContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import rocks.artur.clickhouse.CharacterisationResultGatewayClickhouseImpl;
import rocks.artur.domain.CharacterisationResult;
import rocks.artur.domain.Property;
import rocks.artur.domain.statistics.PropertyValueStatistic;
import rocks.artur.utils.CharacterisationResultGenerator;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;






@ActiveProfiles("clickhouse")
@Disabled
@Testcontainers
@SpringBootTest
public class ClickhouseTest {


    @Container
    private static final ClickHouseContainer clickHouseContainer =
            new ClickHouseContainer("clickhouse/clickhouse-server:24.12");

    @DynamicPropertySource
    static void registerClickHouseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", clickHouseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", clickHouseContainer::getUsername);
        registry.add("spring.datasource.password", clickHouseContainer::getPassword);
        registry.add("spring.profiles.active", () -> "clickhouse");
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

    @Autowired
    CharacterisationResultGatewayClickhouseImpl characterisationResultGatewaySqlImpl;

    void generate() {
        if (generated == null) {
            generated = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                generated.add(CharacterisationResultGenerator.generate());
            }
        }
    }

    List<CharacterisationResult> generated;

    @Test
    void propValDistributionTest() {
        generate();
        long count = generated.stream().filter(item -> item.getProperty().equals(Property.FORMAT)).count();
        characterisationResultGatewaySqlImpl.addCharacterisationResults(generated, "propVal");

        List<PropertyValueStatistic> distribution = characterisationResultGatewaySqlImpl.getPropertyValueDistribution(Property.FORMAT, null, "propVal");
        Long reduce = distribution.stream().map(item -> item.getCount()).reduce(0L, Long::sum);
        Assert.assertEquals(0L, count - reduce);
    }



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
        statement.execute("CREATE TABLE test_table2 (id Int32, name String) ENGINE = Memory;");
        statement.execute("INSERT INTO test_table2 (id, name) VALUES (1, 'Test');");

        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_table2;");
        resultSet.next();
        int count = resultSet.getInt(1);

        assertEquals(1, count);
    }


    @Test
    public void deleteDatasetTest() {

        List<String> strings = characterisationResultGatewaySqlImpl.listDatasets();
        System.out.println(strings);
        assertEquals(1, strings.size());

        if (generated == null) {
            generated = new ArrayList<>();
            for (int i = 0; i < 1000000; i++) {
                generated.add(CharacterisationResultGenerator.generate());
            }
        }
        long count = generated.stream().filter(item -> item.getProperty().equals(Property.FORMAT)).count();
        characterisationResultGatewaySqlImpl.addCharacterisationResults(generated, "todelete");

        strings = characterisationResultGatewaySqlImpl.listDatasets();
        System.out.println(strings);
        assertEquals(2, strings.size());


        characterisationResultGatewaySqlImpl.removeDataset("todelete");

        strings = characterisationResultGatewaySqlImpl.listDatasets();
        System.out.println(strings);
        assertEquals(1, strings.size());

    }
}