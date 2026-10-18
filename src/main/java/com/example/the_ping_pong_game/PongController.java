package com.example.the_ping_pong_game;

import com.example.the_ping_pong_game.database.GameDAO;
import com.example.the_ping_pong_game.database.GameDAOImpl;
import com.example.the_ping_pong_game.manager.BallManager;
import com.example.the_ping_pong_game.manager.GameDataManager;
import com.example.the_ping_pong_game.model.Ball;
import com.example.the_ping_pong_game.model.Game;
import com.example.the_ping_pong_game.model.Player;
import com.example.the_ping_pong_game.model.Racket;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.util.EnumSet;
import java.util.Optional;
import java.util.Set;

/**
 * Controller for the main game window.
 * Handles UI setup, menu actions, keyboard input, saving/loading and game restart logic.
 */
public class PongController {

    @FXML private Menu playersMenu;
    @FXML private Menu settingsMenu;
    @FXML private Menu gameMenu;

    @FXML private MenuItem restartItem;
    @FXML private MenuItem exitItem;
    @FXML private MenuItem saveFileItem;
    @FXML private MenuItem loadFileItem;
    @FXML private MenuItem saveDbItem;
    @FXML private MenuItem loadDbItem;
    @FXML private MenuItem setNamesLeftItem;
    @FXML private MenuItem setNamesRightItem;
    @FXML private MenuItem setSpeedItem;
    @FXML private MenuItem setRacketSizeItem;
    @FXML private MenuItem setWinningScoreItem;
    @FXML private MenuItem setSpeedIncreaseItem;

    @FXML private AnchorPane rootPane;

    @FXML private Label leftName;
    @FXML private Label rightName;
    @FXML private Label leftScoreLabel;
    @FXML private Label rightScoreLabel;

    /** Keys that move a racket while held down. */
    private static final Set<KeyCode> MOVEMENT_KEYS =
            EnumSet.of(KeyCode.W, KeyCode.S, KeyCode.UP, KeyCode.DOWN);

    /** Racket speed is stored in pixels per 1/60 s; this converts it to pixels per second. */
    private static final double FRAMES_PER_SECOND = 60;

    private GameCanvas canvas;
    private Game game;

    private Ball ball;
    private Racket leftRacket;
    private Racket rightRacket;

    private double ballSpeed = 2;
    private double speedIncrease = 1.1;
    private int winningScore = 5;
    private double racketHeight = 125;

    private BallManager manager;
    private boolean openingDialog = false;

    /** Every movement key currently held down, so both players can move at the same time. */
    private final Set<KeyCode> pressedKeys = EnumSet.noneOf(KeyCode.class);

    private final GameDAO gameDAO = new GameDAOImpl();

    /**
     * Initialises the game objects, the game loop, menu actions, and keyboard controls.
     */
    @FXML
    public void initialize() {
        game = new Game(800, 600, leftScoreLabel, rightScoreLabel);

        leftRacket = new Racket(20, 200, 30, racketHeight, 0, Racket.DEFAULT_SPEED);
        rightRacket = new Racket(750, 200, 30, racketHeight, 0, Racket.DEFAULT_SPEED);

        Player p1 = new Player("Player 1", leftRacket);
        Player p2 = new Player("Player 2", rightRacket);
        game.setPlayers(p1, p2);

        ball = new Ball(game, 400, 300, 20, ballSpeed, ballSpeed);
        game.setBall(ball);
        game.setTarget(winningScore);

        createCanvas();

        leftName.setText(p1.getName());
        rightName.setText(p2.getName());
        leftScoreLabel.setText("0");
        rightScoreLabel.setText("0");

        restartItem.setOnAction(e -> restartGame());
        exitItem.setOnAction(e -> exitGame());
        saveFileItem.setOnAction(e -> handleSaveToFile());
        loadFileItem.setOnAction(e -> handleLoadFromFile());
        saveDbItem.setOnAction(e -> handleSaveToDB());
        loadDbItem.setOnAction(e -> handleLoadFromDB());
        setNamesLeftItem.setOnAction(e -> setPlayerLeft());
        setNamesRightItem.setOnAction(e -> setPlayerRight());
        setSpeedItem.setOnAction(e -> setBallSpeed());
        setRacketSizeItem.setOnAction(e -> setRacketSize());
        setWinningScoreItem.setOnAction(e -> setWinningScore());
        setSpeedIncreaseItem.setOnAction(e -> setSpeedIncrease());

        setupMenuPause(gameMenu);
        setupMenuPause(playersMenu);
        setupMenuPause(settingsMenu);

        // Keyboard input is handled at scene level so it keeps working no matter
        // which node has focus, and after the canvas is replaced by a loaded game.
        rootPane.sceneProperty().addListener((obs, oldScene, scene) -> {
            if (scene != null) installKeyHandlers(scene);
        });
        startRacketLoop();

        canvas.draw();
        manager = new BallManager(canvas, game, speedIncrease);
        manager.start();
    }

    /**
     * Creates a canvas for the current game, adds it to the window and links the rackets to it.
     */
    private void createCanvas() {
        if (canvas != null) {
            rootPane.getChildren().remove(canvas);
        }
        canvas = new GameCanvas(game);
        rootPane.getChildren().add(canvas);

        AnchorPane.setTopAnchor(canvas, 25.0);
        AnchorPane.setBottomAnchor(canvas, 0.0);
        AnchorPane.setLeftAnchor(canvas, 80.0);
        AnchorPane.setRightAnchor(canvas, 80.0);

        leftRacket.setCanvas(canvas);
        rightRacket.setCanvas(canvas);
    }

    /**
     * Tracks which movement keys are held down. Using a set instead of reacting to each
     * key-repeat event lets both players hold their keys at the same time.
     */
    private void installKeyHandlers(Scene scene) {
        scene.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            KeyCode code = event.getCode();
            if (MOVEMENT_KEYS.contains(code)) {
                pressedKeys.add(code);
                event.consume(); // stops the arrow keys from moving focus around the window
            } else if (code == KeyCode.ESCAPE) {
                togglePause();
                event.consume();
            }
        });
        scene.addEventFilter(KeyEvent.KEY_RELEASED, event -> {
            if (MOVEMENT_KEYS.contains(event.getCode())) {
                pressedKeys.remove(event.getCode());
                event.consume();
            }
        });
        // Forget held keys when the window loses focus, otherwise a racket keeps moving.
        scene.windowProperty().addListener((obs, oldWindow, window) -> {
            if (window != null) {
                window.focusedProperty().addListener((o, wasFocused, focused) -> {
                    if (!focused) pressedKeys.clear();
                });
            }
        });
    }

    /**
     * Moves the rackets every frame based on the keys currently held down.
     */
    private void startRacketLoop() {
        new AnimationTimer() {
            private long lastFrame = 0;

            @Override
            public void handle(long now) {
                double seconds = lastFrame == 0 ? 0 : (now - lastFrame) / 1_000_000_000.0;
                lastFrame = now;
                if (seconds == 0 || pressedKeys.isEmpty() || isPaused()) return;

                moveRacket(leftRacket, KeyCode.W, KeyCode.S, seconds);
                moveRacket(rightRacket, KeyCode.UP, KeyCode.DOWN, seconds);
                canvas.draw();
            }
        }.start();
    }

    private void moveRacket(Racket racket, KeyCode upKey, KeyCode downKey, double seconds) {
        int direction = 0;
        if (pressedKeys.contains(upKey)) direction--;
        if (pressedKeys.contains(downKey)) direction++;
        if (direction != 0) {
            racket.move(direction * racket.getSpeedY() * FRAMES_PER_SECOND * seconds);
        }
    }

    // ---------------------------------------------------------------- saving & loading

    private void handleSaveToFile() {
        pauseGame();
        GameDataManager.getInstance().saveGame(game);
        resumeGame();
    }

    private void handleLoadFromFile() {
        pauseGame();
        Game loaded = GameDataManager.getInstance().loadGame();
        if (loaded != null) {
            restoreGameState(loaded);
        } else {
            resumeGame();
        }
    }

    private void handleSaveToDB() {
        pauseGame();
        openingDialog = true;
        try {
            TextInputDialog dialog = new TextInputDialog("My Pong Game");
            dialog.setTitle("Save Game");
            dialog.setHeaderText("Enter a name for this save:");
            dialog.setContentText("Save name:");
            dialog.showAndWait()
                    .map(String::trim)
                    .ifPresent(name -> {
                        if (name.isEmpty()) {
                            showWarning("Please enter a save name.");
                        } else {
                            gameDAO.saveGame(name, game);
                        }
                    });
        } finally {
            openingDialog = false;
            resumeGame();
        }
    }

    private void handleLoadFromDB() {
        pauseGame();
        openingDialog = true;
        Game loaded = null;
        try {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Load Game");
            dialog.setHeaderText("Enter the name of the game to load:");
            dialog.setContentText("Save name:");
            Optional<String> name = dialog.showAndWait().map(String::trim);
            if (name.isPresent()) {
                if (name.get().isEmpty()) {
                    showWarning("Please enter a save name.");
                } else {
                    loaded = gameDAO.loadGameByName(name.get());
                }
            }
        } finally {
            openingDialog = false;
        }

        if (loaded != null) {
            restoreGameState(loaded);
        } else {
            resumeGame();
        }
    }

    /**
     * Replaces the running game with a loaded one: rebuilds the canvas,
     * re-links the controller's ball and rackets, updates the labels and restarts the game loop.
     */
    private void restoreGameState(Game loaded) {
        if (manager != null) {
            manager.stop();
        }
        pressedKeys.clear();

        game = loaded;
        game.setScoreLabels(leftScoreLabel, rightScoreLabel);

        // Point the controller at the loaded objects, otherwise restart and the
        // settings menu would keep changing the old game.
        ball = game.getBall();
        ball.setGame(game);
        leftRacket = game.getPlayer1().getRacket();
        rightRacket = game.getPlayer2().getRacket();
        winningScore = game.getTarget();

        createCanvas();

        ball.setPosX(canvas.getWidth() / 2 - ball.getRadius());
        ball.setPosY(canvas.getHeight() / 2 - ball.getRadius());

        leftName.setText(game.getPlayer1().getName());
        rightName.setText(game.getPlayer2().getName());
        leftScoreLabel.setText(String.valueOf(game.getScoreboard().getLeftScore()));
        rightScoreLabel.setText(String.valueOf(game.getScoreboard().getRightScore()));

        canvas.draw();
        manager = new BallManager(canvas, game, speedIncrease);
        manager.start();
    }

    // ---------------------------------------------------------------- pause / restart / exit

    private boolean isPaused() {
        return manager == null || manager.isPaused();
    }

    private void pauseGame() {
        if (manager != null) {
            manager.pause();
        }
    }

    private void resumeGame() {
        if (manager != null) {
            manager.resume();
        }
        if ("Paused".equals(canvas.getMessage())) {
            canvas.setMessage("");
        }
    }

    /** Escape pauses the game, pressing it again continues. */
    private void togglePause() {
        if (manager == null) return;
        if (manager.isPaused()) {
            resumeGame();
        } else {
            pauseGame();
            pressedKeys.clear();
            canvas.setMessage("Paused");
        }
    }

    /**
     * Restarts the game from the start.
     * Resets scores and object positions, but keeps the menu setting changes.
     */
    private void restartGame() {
        if (manager != null) {
            manager.stop();
        }
        pressedKeys.clear();

        game.getScoreboard().reset();
        leftScoreLabel.setText("0");
        rightScoreLabel.setText("0");

        game.setTarget(winningScore);

        leftRacket.setHeight(racketHeight);
        rightRacket.setHeight(racketHeight);

        leftRacket.setPosX(20);
        leftRacket.setPosY(200);

        rightRacket.setPosX(canvas.getWidth() - 50);
        rightRacket.setPosY(200);

        ball.setPosX(canvas.getWidth() / 2 - ball.getRadius());
        ball.setPosY(canvas.getHeight() / 2 - ball.getRadius());
        ball.setSpeedX(ballSpeed);
        ball.setSpeedY(ballSpeed);

        canvas.setMessage("");
        canvas.draw();

        manager = new BallManager(canvas, game, speedIncrease);
        manager.start();
    }

    private void exitGame() {
        pauseGame();
        Platform.exit();
    }

    /**
     * Pauses the game while a menu is open and resumes it when closed.
     * @param menu the menu to attach pause behaviour to
     */
    private void setupMenuPause(Menu menu) {
        menu.setOnShowing(e -> pauseGame());
        menu.setOnHidden(e -> {
            if (!openingDialog) {
                resumeGame();
            }
        });
    }

    // ---------------------------------------------------------------- settings dialogs

    /**
     * Shows a text dialog with the current value pre-filled.
     * @return the entered text, or empty if the dialog was cancelled
     */
    private Optional<String> askFor(String header, String currentValue) {
        pauseGame();
        openingDialog = true;
        try {
            TextInputDialog dialog = new TextInputDialog(currentValue);
            dialog.setHeaderText(header);
            return dialog.showAndWait().map(String::trim).filter(s -> !s.isEmpty());
        } finally {
            openingDialog = false;
        }
    }

    // opens a dialog to change the left player's name
    private void setPlayerLeft() {
        Optional<String> name = askFor("Enter Player 1 Name", leftName.getText());
        if (name.isEmpty()) { resumeGame(); return; }
        leftName.setText(name.get());
        game.getPlayer1().setName(name.get());
        restartGame();
    }

    // opens a dialog to change the right player's name
    private void setPlayerRight() {
        Optional<String> name = askFor("Enter Player 2 Name", rightName.getText());
        if (name.isEmpty()) { resumeGame(); return; }
        rightName.setText(name.get());
        game.getPlayer2().setName(name.get());
        restartGame();
    }

    // opens a dialog to change the ball speed
    private void setBallSpeed() {
        Optional<Double> value = askForNumber("Enter Ball Speed", ballSpeed);
        if (value.isEmpty()) { resumeGame(); return; }
        ballSpeed = value.get();
        restartGame();
    }

    // opens a dialog to change the racket height
    private void setRacketSize() {
        Optional<Double> value = askForNumber("Enter New Racket Height", racketHeight);
        if (value.isEmpty()) { resumeGame(); return; }
        racketHeight = value.get();
        restartGame();
    }

    // opens a dialog to change the score needed to win
    private void setWinningScore() {
        Optional<Double> value = askForNumber("Enter Score Needed To Win", winningScore);
        if (value.isEmpty()) { resumeGame(); return; }
        winningScore = (int) Math.round(value.get());
        restartGame();
    }

    // opens a dialog to change how much the ball speeds up
    private void setSpeedIncrease() {
        Optional<Double> value = askForNumber("Enter Ball Speed Increase Factor", speedIncrease);
        if (value.isEmpty()) { resumeGame(); return; }
        speedIncrease = value.get();
        restartGame();
    }

    /**
     * Asks for a positive number. Shows a warning and returns empty for invalid input.
     */
    private Optional<Double> askForNumber(String header, double currentValue) {
        Optional<String> text = askFor(header, formatNumber(currentValue));
        if (text.isEmpty()) return Optional.empty();
        try {
            double value = Double.parseDouble(text.get());
            if (value > 0) return Optional.of(value);
        } catch (NumberFormatException ignored) {
            // falls through to the warning below
        }
        showWarning("Please enter a number greater than 0.");
        return Optional.empty();
    }

    private static String formatNumber(double value) {
        return value == Math.rint(value) ? String.valueOf((long) value) : String.valueOf(value);
    }

    private void showWarning(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING, message);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
