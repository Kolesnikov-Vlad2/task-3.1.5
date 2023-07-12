package com.itm.space.backendresources;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import static com.itm.space.backendresources.utils.PropertyConstants.DATASOURCE_PASSWORD;
import static com.itm.space.backendresources.utils.PropertyConstants.DATASOURCE_URL;
import static com.itm.space.backendresources.utils.PropertyConstants.DATASOURCE_USERNAME;

@Testcontainers
public class PostgresInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {


    public static final PostgreSQLContainer<?> container = new PostgreSQLContainer<>("postgres:15.1")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withReuse(false);

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
        if (container.isCreated()) {
            tearDown();
        }
        container.start();
        TestPropertyValues.of(
                DATASOURCE_URL + container.getJdbcUrl(),
                DATASOURCE_USERNAME + container.getUsername(),
                DATASOURCE_PASSWORD + container.getPassword()
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

    public void tearDown() {
        try (Connection connection = DriverManager.getConnection(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        )) {
            try (Statement statement = connection.createStatement()) {
                statement.executeUpdate("DROP SCHEMA public CASCADE;");
                statement.executeUpdate("CREATE SCHEMA public;");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

