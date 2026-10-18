package com.example.the_ping_pong_game.model;

import com.example.the_ping_pong_game.Resizable;
import javafx.scene.control.Label;

import java.io.Serializable;

/**
 * Represents the main game model.
 * Stores players, ball, dimensions, target score, and scoreboard data.
 */
public class Game implements Resizable, Serializable {
    private static final long serialVersionUID = 1L;

    int target = 0;
    private Player player1, player2;
    private Ball ball;
    double dimensionX, dimensionY;
    Scoreboard scoreboard = new Scoreboard();

    private transient Label leftScoreLabel;
    private transient Label rightScoreLabel;

    /**
     * Creates a new game.
     * @param dimensionX the initial width of the game
     * @param dimensionY the initial height of the game
     * @param leftScoreLabel the label showing the left player's score
     * @param rightScoreLabel the label showing the right player's score
     */
    public Game(double dimensionX, double dimensionY, Label leftScoreLabel, Label rightScoreLabel) {
        this.dimensionX = dimensionX;
        this.dimensionY = dimensionY;
        this.leftScoreLabel = leftScoreLabel;
        this.rightScoreLabel = rightScoreLabel;
    }

    /**
     * Sets both players in the game.
     * @param player1 the left player
     * @param player2 the right player
     */
    public void setPlayers(Player player1, Player player2){
        this.player1 = player1;
        this.player2 = player2;
    }

    public void setScoreLabels(Label left, Label right) {
        this.leftScoreLabel = left;
        this.rightScoreLabel = right;
    }

    //Increases the left player's score and updates the label
    public void goalLeft() {
        scoreboard.setLeftScore(scoreboard.getLeftScore() + 1);
        if (leftScoreLabel != null) {
            leftScoreLabel.setText(String.valueOf(scoreboard.getLeftScore()));
        }
    }

    //Increases the right player's score and updates the label
    public void goalRight() {
        scoreboard.setRightScore(scoreboard.getRightScore() + 1);
        if (rightScoreLabel != null) {
            rightScoreLabel.setText(String.valueOf(scoreboard.getRightScore()));
        }
    }

    //Checks whether a player has reached the target score
    public boolean hasWinner() {
        return scoreboard.getLeftScore() >= target || scoreboard.getRightScore() >= target;
    }

    /**
     * Returns a winner message for the player who reached the target score.
     * @return the winner message, or an empty string if there is no winner
     */
    public String getWinnerMessage() {
        if (scoreboard.getLeftScore() >= target) {
            return player1.getName() + " wins!";
        }
        if (scoreboard.getRightScore() >= target) {
            return player2.getName() + " wins!";
        }
        return "";
    }

    /**
     * Resizes all x-based game values.
     * @param factor the resize factor
     */
    @Override
    public void resizeX(double factor) {

        dimensionX *= factor;

        if (ball != null) ball.resizeX(factor);
        if (player1 != null) player1.resizeX(factor);
        if (player2 != null) player2.resizeX(factor);
    }

    /**
     * Resizes all y-based game values.
     * @param factor the resize factor
     */
    @Override
    public void resizeY(double factor) {

        dimensionY *= factor;

        if (ball != null) ball.resizeY(factor);
        if (player1 != null) player1.resizeY(factor);
        if (player2 != null) player2.resizeY(factor);
    }

    //getters and setters

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public Ball getBall() {
        return ball;
    }

    public void setBall(Ball ball) {
        this.ball = ball;
    }

    public double getDimensionX() {
        return dimensionX;
    }

    public void setDimensionX(double dimensionX) {
        this.dimensionX = dimensionX;
    }

    public double getDimensionY() {
        return dimensionY;
    }

    public void setDimensionY(double dimensionY) {
        this.dimensionY = dimensionY;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

}
