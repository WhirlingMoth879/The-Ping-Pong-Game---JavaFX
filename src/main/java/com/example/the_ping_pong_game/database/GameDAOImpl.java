package com.example.the_ping_pong_game.database;

import com.example.the_ping_pong_game.model.Game;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Data access logic for saving and loading games in PostgreSQL.
 * The table is created automatically the first time it is needed.
 */
public class GameDAOImpl implements GameDAO {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS game (
                id       SERIAL PRIMARY KEY,
                name     VARCHAR(100) NOT NULL,
                p1       VARCHAR(100) NOT NULL,
                p2       VARCHAR(100) NOT NULL,
                s1       INT NOT NULL,
                s2       INT NOT NULL,
                target   INT NOT NULL,
                saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )""";

    private boolean tableReady = false;

    @Override
    public void saveGame(String gameName, Game game) {
        Connection connection = openConnection();
        if (connection == null) return;

        String sql = "INSERT INTO game (name, p1, p2, s1, s2, target) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gameName);
            ps.setString(2, game.getPlayer1().getName());
            ps.setString(3, game.getPlayer2().getName());
            ps.setInt(4, game.getScoreboard().getLeftScore());
            ps.setInt(5, game.getScoreboard().getRightScore());
            ps.setInt(6, game.getTarget());

            ps.executeUpdate();
            showInfo("Game Saved", "Game saved to the database as \"" + gameName + "\".");

        } catch (SQLException e) {
            showError("Save Failed", "Could not save the game:\n" + e.getMessage());
        }
    }

    @Override
    public Game loadGameByName(String gameName) {
        Connection connection = openConnection();
        if (connection == null) return null;

        // If the same name was saved more than once, load the most recent one
        String sql = "SELECT * FROM game WHERE name = ? ORDER BY id DESC LIMIT 1";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, gameName);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Game game = new GameBuilder()
                            .setPlayer1Name(rs.getString("p1"))
                            .setPlayer2Name(rs.getString("p2"))
                            .setScores(rs.getInt("s1"), rs.getInt("s2"))
                            .setTarget(rs.getInt("target"))
                            .build();
                    showInfo("Game Loaded", "Loaded \"" + gameName + "\" from the database.");
                    return game;
                }
            }
            showError("Load Failed", "No saved game called \"" + gameName + "\" was found.");

        } catch (SQLException e) {
            showError("Load Failed", "Could not load the game:\n" + e.getMessage());
        }
        return null;
    }

    /**
     * Gets a connection and makes sure the game table exists.
     * Shows an error and returns null if the database is unavailable.
     */
    private Connection openConnection() {
        Connection connection = DatabaseConnection.getInstance().getConnection();
        if (connection == null) {
            showError("Database Unavailable",
                    "Could not connect to " + DatabaseConnection.describeTarget()
                            + ".\nMake sure PostgreSQL is running (see README).\n"
                            + "You can still save and load using a file.");
            return null;
        }
        if (!tableReady) {
            try (Statement st = connection.createStatement()) {
                st.execute(CREATE_TABLE);
                tableReady = true;
            } catch (SQLException e) {
                showError("Database Error", "Could not create the game table:\n" + e.getMessage());
                return null;
            }
        }
        return connection;
    }

    private void showInfo(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }

    private void showError(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR, msg);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
