package com.example.the_ping_pong_game.database;

import com.example.the_ping_pong_game.model.*;

/**
 * Rebuilds a Game object from a database record (Builder pattern).
 * Only names, scores and target are stored, so positions are reset to their defaults.
 */
public class GameBuilder {

    private String player1Name = "Player 1";
    private String player2Name = "Player 2";

    private int leftScore = 0;
    private int rightScore = 0;

    private int target = 5;

    private double width = 800;
    private double height = 600;

    //setters
    public GameBuilder setPlayer1Name(String name) {
        this.player1Name = name;
        return this;
    }

    public GameBuilder setPlayer2Name(String name) {
        this.player2Name = name;
        return this;
    }

    public GameBuilder setScores(int left, int right) {
        this.leftScore = left;
        this.rightScore = right;
        return this;
    }

    public GameBuilder setTarget(int target) {
        this.target = target;
        return this;
    }

    public Game build() {

        // Creates game without labels
        Game game = new Game(width, height, null, null);

        // Creates rackets
        Racket leftRacket = new Racket(20, 200, 30, 125, 0, Racket.DEFAULT_SPEED);
        Racket rightRacket = new Racket(750, 200, 30, 125, 0, Racket.DEFAULT_SPEED);

        // Creates players
        Player p1 = new Player(player1Name, leftRacket);
        Player p2 = new Player(player2Name, rightRacket);
        game.setPlayers(p1, p2);

        // Sets scores and target
        game.getScoreboard().setLeftScore(leftScore);
        game.getScoreboard().setRightScore(rightScore);
        game.setTarget(target);

        // Ball resets to center
        Ball ball = new Ball(game, 400, 300, 20, 2, 2);
        ball.setGame(game);
        game.setBall(ball);

        return game;
    }
}
