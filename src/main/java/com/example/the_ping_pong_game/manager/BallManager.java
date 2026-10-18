package com.example.the_ping_pong_game.manager;

import com.example.the_ping_pong_game.GameCanvas;
import com.example.the_ping_pong_game.model.Ball;
import com.example.the_ping_pong_game.model.Game;
import com.example.the_ping_pong_game.model.Racket;
import javafx.application.Platform;

/**
 * Manages ball movement, collisions, scoring, and game state updates.
 * Runs on a separate thread and updates the JavaFX UI using Platform.runLater.
 */
public class BallManager implements Runnable {

    private final GameCanvas canvas;
    private final Game game;
    private final Ball ball;
    private final double speedIncrease;

    // volatile: these are changed on the JavaFX thread and read by the game loop thread
    private volatile boolean running = true;
    private volatile boolean paused = false;

    private int bounceCount = 0;
    private final int bouncesBeforeIncrease = 5;

    /**
     * Creates a new BallManager for controlling ball movement.
     *
     * @param canvas the canvas used to draw the game
     * @param game the game model
     * @param speedIncrease the factor used to increase ball speed
     */
    public BallManager(GameCanvas canvas, Game game, double speedIncrease) {
        this.canvas = canvas;
        this.game = game;
        this.ball = game.getBall();
        this.speedIncrease = speedIncrease;
    }

    // starts the game loop on a daemon thread
    public void start() {
        Thread t = new Thread(this);
        t.setDaemon(true);
        t.start();
    }

    public void stop() {
        running = false;
    }

    public void pause() {
        paused = true;
    }

    public void resume() {
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    /**
     * main game loop. Updates ball movement, collision handling,
     * scoring, winner declaration, and drawing.
     */
    @Override
    public void run() {
        while (running) {
            if (!paused) {
                Platform.runLater(() -> {
                    if (!running || paused) return; // state may have changed since this was queued

                    ball.move(ball.getSpeedX(), ball.getSpeedY());

                    Racket leftRacket = game.getPlayer1().getRacket();
                    Racket rightRacket = game.getPlayer2().getRacket();

                    if (CollisionManager.isTopBorderCollision(ball))
                        ball.setSpeedY(Math.abs(ball.getSpeedY()));
                    if (CollisionManager.isBottomBorderCollision(ball, canvas.getHeight()))
                        ball.setSpeedY(-Math.abs(ball.getSpeedY()));
                    if (CollisionManager.isRacketCollision(ball, leftRacket)) {
                        ball.setSpeedX(Math.abs(ball.getSpeedX()));
                        ball.setPosX(leftRacket.getPosX() + leftRacket.getWidth());
                        registerBounce();
                    }

                    if (CollisionManager.isRacketCollision(ball, rightRacket)) {
                        ball.setSpeedX(-Math.abs(ball.getSpeedX()));
                        ball.setPosX(rightRacket.getPosX() - ball.getRadius() * 2);
                        registerBounce();
                    }
                    if (CollisionManager.isLeftGoal(ball)) {
                        game.goalRight();
                        showMessage(game.getPlayer2().getName() + " Scored!");
                        resetBall();
                    }
                    if (CollisionManager.isRightGoal(ball, canvas.getWidth())) {
                        game.goalLeft();
                        showMessage(game.getPlayer1().getName() + " Scored!");
                        resetBall();
                    }
                    if (game.hasWinner()) {
                        canvas.setMessage(game.getWinnerMessage()); // stays until restart
                        stop();
                    }
                    canvas.draw();
                });
            }
            try { Thread.sleep(20); } catch (InterruptedException ignored) {}
        }
    }

    // increases ball speed after a fixed number of bounces
    private void registerBounce() {
        bounceCount++;
        if (bounceCount % bouncesBeforeIncrease == 0) {
            ball.setSpeedX(ball.getSpeedX() * speedIncrease);
            ball.setSpeedY(ball.getSpeedY() * speedIncrease);
        }
    }

    // shows a message on the canvas for 2 seconds
    private void showMessage(String message) {
        canvas.setMessage(message);

        Thread clear = new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) {
            }
            // only clear it if nothing newer (e.g. the winner message) replaced it
            Platform.runLater(() -> {
                if (message.equals(canvas.getMessage())) canvas.setMessage("");
            });
        });
        clear.setDaemon(true);
        clear.start();
    }

    // resets the ball to the centre, keeping its current speed
    private void resetBall() {
        bounceCount = 0;
        ball.setPosX(canvas.getWidth() / 2 - ball.getRadius());
        ball.setPosY(canvas.getHeight() / 2 - ball.getRadius());

        double direction = Math.random() < 0.5 ? -1 : 1;
        ball.setSpeedX(ballSpeedMagnitude() * direction);
        ball.setSpeedY(ballSpeedMagnitude());
    }

    private double ballSpeedMagnitude() {
        return Math.abs(ball.getSpeedX());
    }
}
