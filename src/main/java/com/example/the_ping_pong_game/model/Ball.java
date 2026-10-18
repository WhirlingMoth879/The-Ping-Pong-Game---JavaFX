package com.example.the_ping_pong_game.model;

import com.example.the_ping_pong_game.Resizable;

import java.io.Serializable;

/**
 * Represents the ball used in the game.
 * Stores its position, speed, size, and the game it belongs to.
 */
public class Ball extends PosAndSpeed implements Resizable, Serializable {
    private static final long serialVersionUID = 1L;

    private Game game;
    private double radius;

    /**
     * Creates a new ball.
     * @param game the game the ball belongs to
     * @param posX the x position of the ball
     * @param posY the y position of the ball
     * @param radius the radius of the ball
     * @param speedX the horizontal speed
     * @param speedY the vertical speed
     */
    public Ball(Game game,
                double posX,
                double posY,
                double radius,
                double speedX,
                double speedY) {

        super(posX, posY, speedX, speedY);
        this.game = game;
        this.radius = radius;
    }

    /**
     * Moves the ball by the given x and y values.
     * @param dx movement along the x axis
     * @param dy movement along the y axis
     */
    public void move(double dx, double dy) {
        posX += dx;
        posY += dy;
    }

    /**
     * Resizes the ball horizontally.
     * @param factor the resize factor
     */
    @Override
    public void resizeX(double factor) {
        posX *= factor;
        radius *= factor;
        speedX *= factor;
    }

    /**
     * Resizes the ball vertically.
     * @param factor the resize factor
     */
    @Override
    public void resizeY(double factor) {
        posY *= factor;
        radius *= factor;
        speedY *= factor;
    }

    // getters and setters
    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
}

