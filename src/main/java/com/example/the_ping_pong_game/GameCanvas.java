package com.example.the_ping_pong_game;

import com.example.the_ping_pong_game.model.Ball;
import com.example.the_ping_pong_game.model.Game;
import com.example.the_ping_pong_game.model.Racket;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Custom canvas used to draw the game.
 * For resizing it redraws all game objects.
 */
public class GameCanvas extends Canvas {
    private final Game game;
    private double previousWidth;
    private double previousHeight;
    private String message = "";

    /**
     * Creates a new game canvas.
     * @param game the game model to draw, with getDimensionX() and getDimensionY()
     */
    public GameCanvas(Game game) {
        super(game.getDimensionX(), game.getDimensionY());
        this.game = game;
        this.previousWidth = game.getDimensionX();
        this.previousHeight = game.getDimensionY();
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double minWidth(double height) {
        return 1;
    }

    @Override
    public double minHeight(double width) {
        return 1;
    }

    /**
     * Resizes the canvas and all game objects using scale factors.
     * @param width the new width
     * @param height the new height
     */
    @Override
    public void resize(double width, double height) {
        double factorX = width / previousWidth;
        double factorY = height / previousHeight;

        game.resizeX(factorX);
        game.resizeY(factorY);

        previousWidth = width;
        previousHeight = height;
        super.setWidth(width);
        super.setHeight(height);
        draw();
    }

    // Sets a message to be drawn in the middle of the canvas ("" hides it).
    public void setMessage(String message) {
        this.message = message;
        draw();
    }

    public String getMessage() {
        return message;
    }

    //draws the background, ball, rackets, and active messages
    public void draw(){

        GraphicsContext gc = getGraphicsContext2D();
        gc.clearRect(0,0,getWidth(),getHeight());
        gc.setFill(Color.CHOCOLATE);
        gc.fillRect(0,0,getWidth(),getHeight());

        drawBall(gc, game.getBall());
        drawRacket(gc, game.getPlayer1().getRacket());
        drawRacket(gc, game.getPlayer2().getRacket());

        if (!message.isEmpty()) {
            gc.setFill(Color.WHITE);
            gc.setFont(javafx.scene.text.Font.font(28));
            gc.setTextAlign(javafx.scene.text.TextAlignment.CENTER);
            gc.fillText(message, getWidth() / 2, getHeight() / 2);
        }
    }

    /**
     * Draws the ball on the canvas.
     * @param gc the graphics context
     * @param ball the ball to draw
     */
    public void drawBall(GraphicsContext gc, Ball ball) {
        double diameter = ball.getRadius()*2;
        gc.setFill(Color.LIGHTGREEN);
        gc.fillOval(ball.getPosX(),ball.getPosY(),diameter, diameter);
    }

    /**
     * Draws a racket on the canvas.
     * @param gc the graphics context
     * @param racket the racket to draw
     */
    public void drawRacket(GraphicsContext gc, Racket racket) {
        gc.setFill(Color.YELLOW);
        gc.fillRect(racket.getPosX(),racket.getPosY(),racket.getWidth(),racket.getHeight());
    }



}

