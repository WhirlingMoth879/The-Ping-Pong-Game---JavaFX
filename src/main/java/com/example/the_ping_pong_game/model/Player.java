package com.example.the_ping_pong_game.model;

import com.example.the_ping_pong_game.Resizable;

import java.io.Serializable;

/**
 * Represents a player in the game.
 * Each player has a name and a racket.
 */
public class Player implements Resizable, Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private Racket racket;

    /**
     * Creates a new player.
     * @param name the player's name
     * @param racket the player's racket
     */
    public Player(String name, Racket racket) {
        this.name = name;
        this.racket = racket;
    }

    /**
     * Resizes the player's racket horizontally.
     * @param factor the resize factor
     */
    @Override
    public void resizeX(double factor) {
        if (racket != null) racket.resizeX(factor);
    }

    /**
     * Resizes the player's racket vertically.
     * @param factor the resize factor
     */
    @Override
    public void resizeY(double factor) {
        if (racket != null) racket.resizeY(factor);
    }

    //getters and setters

    public Racket getRacket() {
        return racket;
    }

    public void setRacket(Racket racket) {
        this.racket = racket;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}