package com.example.the_ping_pong_game.model;

import java.io.Serializable;

/**
 * Stores a position and speed using x and y values.
 * Used as a base class for moving game objects.
 */
public class PosAndSpeed implements Serializable {
    private static final long serialVersionUID = 1L;

    protected double posX, posY;
    protected double speedX, speedY;

    /**
     * Creates a new position and speed object.
     * @param posX the x position
     * @param posY the y position
     * @param speedX the x speed
     * @param speedY the y speed
     */
    public PosAndSpeed(double posX, double posY, double speedX, double speedY) {
        this.posX = posX;
        this.posY = posY;
        this.speedX = speedX;
        this.speedY = speedY;
    }

    //getters and setters

    public double getPosX() {
        return posX;
    }

    public void setPosX(double posX) {
        this.posX = posX;
    }

    public double getPosY() {
        return posY;
    }

    public void setPosY(double posY) {
        this.posY = posY;
    }

    public double getSpeedX() {
        return speedX;
    }

    public void setSpeedX(double speedX) {
        this.speedX = speedX;
    }

    public double getSpeedY() {
        return speedY;
    }

    public void setSpeedY(double speedY) {
        this.speedY = speedY;
    }
}
