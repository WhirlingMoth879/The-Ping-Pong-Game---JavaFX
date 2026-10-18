package com.example.the_ping_pong_game.manager;

import com.example.the_ping_pong_game.model.Game;
import javafx.scene.control.Alert;

import java.io.*;

/**
 * Saves and loads the whole game state to a local file using Java serialization.
 * Singleton: one shared instance.
 */
public class GameDataManager {

    private static GameDataManager instance;
    private static final String FILE_NAME = "pong_save.dat";

    private GameDataManager() {}

    public static GameDataManager getInstance() {
        if (instance == null) {
            instance = new GameDataManager();
        }
        return instance;
    }

    // SAVE
    public void saveGame(Game game) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(game);
            showInfo("Game Saved", "Game state saved to file successfully.");
        } catch (FileNotFoundException e) {
            showError("Save Failed", "Could not find or create save file:\n" + e.getMessage());
        } catch (IOException e) {
            showError("Save Failed", "An error occurred while saving:\n" + e.getMessage());
        }
    }

    // LOAD
    public Game loadGame() {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(FILE_NAME))) {
            Game game = (Game) ois.readObject();
            showInfo("Game Loaded", "Game state loaded from file successfully.");
            return game;
        } catch (FileNotFoundException e) {
            showError("Load Failed", "No save file found. Please save a game first.");
        } catch (IOException e) {
            showError("Load Failed", "An error occurred while loading:\n" + e.getMessage());
        } catch (ClassNotFoundException e) {
            showError("Load Failed", "Save file is corrupted or incompatible:\n" + e.getMessage());
        }
        return null;
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
