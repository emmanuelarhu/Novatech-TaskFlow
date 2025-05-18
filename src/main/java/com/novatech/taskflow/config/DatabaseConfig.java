package com.novatech.taskflow.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Database configuration and connection management
 */
public class DatabaseConfig {

    private static final String CONFIG_FILE = "/db/dbconfig.properties";
    private static Properties properties = new Properties();

    // Initialize properties on class load
    static {
        try (InputStream input = DatabaseConfig.class.getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                // If config file not found, use default values for development
                properties.setProperty("jdbc.url", "jdbc:mysql://127.0.0.1:3306/novatech");
                properties.setProperty("jdbc.username", "Emmanuel Arhu");
                properties.setProperty("jdbc.password", "Password");
                properties.setProperty("jdbc.driver", "com.mysql.cj.jdbc.Driver");
            } else {
                properties.load(input);
            }

            // Load JDBC driver
            try {
                Class.forName(properties.getProperty("jdbc.driver"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException("Failed to load JDBC driver: " + e.getMessage(), e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load database configuration: " + e.getMessage(), e);
        }
    }

    /**
     * Get a database connection
     * @return A new database connection
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                properties.getProperty("jdbc.url"),
                properties.getProperty("jdbc.username"),
                properties.getProperty("jdbc.password")
        );
    }

    /**
     * Close a database connection safely
     * @param connection The connection to close
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                // Log this error but don't rethrow
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}