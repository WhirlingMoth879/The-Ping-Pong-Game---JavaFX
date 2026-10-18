package com.example.the_ping_pong_game.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Handles the connection to the PostgreSQL database.
 * Uses the Singleton pattern (one shared connection).
 *
 * Connection settings can be overridden with the environment variables
 * PONG_DB_URL, PONG_DB_USER and PONG_DB_PASSWORD.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection connection;

    private static final String URL =
            env("PONG_DB_URL", "jdbc:postgresql://localhost:5432/pong_db");
    private static final String USER = env("PONG_DB_USER", "postgres");
    private static final String PASSWORD = env("PONG_DB_PASSWORD", "postgres");

    private DatabaseConnection() {
    }

    /**
     * Returns the single instance (Singleton).
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Returns an open connection, connecting (or reconnecting) if needed.
     * @return the connection, or null if the database cannot be reached
     */
    public synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connected to PostgreSQL database.");
            }
            return connection;
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            connection = null;
            return null;
        }
    }

    /**
     * @return a short description of where the game is trying to connect, for error messages
     */
    public static String describeTarget() {
        return URL + " (user: " + USER + ")";
    }

    /**
     * Closes the connection safely.
     */
    public synchronized void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.out.println("Could not close database connection: " + e.getMessage());
        }
    }

    private static String env(String name, String fallback) {
        String value = System.getenv(name);
        return value == null || value.isBlank() ? fallback : value;
    }
}
