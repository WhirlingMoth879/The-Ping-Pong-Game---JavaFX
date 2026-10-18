package com.example.the_ping_pong_game.model;

import java.io.Serializable;

/**
 * Stores the scores for both players.
 */
public class Scoreboard implements Serializable {
    private static final long serialVersionUID = 1L;

    int leftScore = 0;
    int rightScore = 0;

    //getters and setters

    public int getLeftScore() {
        return leftScore;
    }

    public void setLeftScore(int leftScore) {
        this.leftScore = leftScore;
    }

    public int getRightScore() {
        return rightScore;
    }

    public void setRightScore(int rightScore) {
        this.rightScore = rightScore;
    }

    // resets scores back to 0
    public void reset() {
        leftScore = 0;
        rightScore = 0;
    }

}
