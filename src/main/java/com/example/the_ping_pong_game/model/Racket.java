package com.example.the_ping_pong_game.model;

import com.example.the_ping_pong_game.GameCanvas;
import com.example.the_ping_pong_game.Resizable;

import java.io.Serializable;

/**
 * Represents a racket used by a player.
 * Stores its size, position, speed, and canvas reference.
 */
public class Racket extends PosAndSpeed implements Resizable, Serializable {
    private static final long serialVersionUID = 1L;

    /** Default vertical speed, in pixels per 1/60 s. */
    public static final double DEFAULT_SPEED = 7;

    private double width;
    private double height;

    // The canvas is part of the UI, not the game state, so it is not saved to file.
    // It is set again after a game is loaded.
    private transient GameCanvas canvas;

    /**
     * Creates a new racket.
     * @param posX the x position
     * @param posY the y position
     * @param width the racket width
     * @param height the racket height
     * @param speedX the x speed
     * @param speedY the y speed
     */
    public Racket(double posX, double posY, double width, double height, double speedX, double speedY) {
        super(posX, posY, speedX, speedY);
        this.width = width;
        this.height = height;
    }

    //sets canvas for movement boundaries
    public void setCanvas(GameCanvas canvas) {
        this.canvas = canvas;
    }

    /**
     * Moves the racket vertically while keeping it inside the canvas.
     * @param delta the amount to move
     */
    public void move(double delta) {
        double newY = posY + delta;
        if (canvas != null) {
            // clamp so the racket stops flush against the top and bottom edges
            newY = Math.max(0, Math.min(newY, canvas.getHeight() - height));
        }
        posY = newY;
    }

    /**
     * Resizes the racket horizontally.
     * @param factor the resize factor
     */
    @Override
    public void resizeX(double factor) {
        posX *= factor;
        width *= factor;
        speedX *= factor;
    }

    /**
     * Resizes the racket vertically.
     * @param factor the resize factor
     */
    @Override
    public void resizeY(double factor) {
        posY *= factor;
        height *= factor;
        speedY *= factor;
    }

    //getters and setters

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }
}
