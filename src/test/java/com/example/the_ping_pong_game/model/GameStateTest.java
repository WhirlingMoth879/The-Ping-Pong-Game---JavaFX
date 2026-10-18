package com.example.the_ping_pong_game.model;

import com.example.the_ping_pong_game.GameCanvas;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import static org.junit.jupiter.api.Assertions.*;

public class GameStateTest {

    private static Game newGame() {
        Game game = new Game(800, 600, null, null);
        Racket left = new Racket(20, 200, 30, 125, 0, Racket.DEFAULT_SPEED);
        Racket right = new Racket(750, 200, 30, 125, 0, Racket.DEFAULT_SPEED);
        game.setPlayers(new Player("Alice", left), new Player("Bob", right));
        game.setBall(new Ball(game, 400, 300, 20, 2, 2));
        game.setTarget(7);
        return game;
    }

    @Test
    void gameCanBeSavedAndLoadedWhileAttachedToACanvas() throws Exception {
        Game game = newGame();
        GameCanvas canvas = new GameCanvas(game);
        game.getPlayer1().getRacket().setCanvas(canvas);
        game.getPlayer2().getRacket().setCanvas(canvas);
        game.goalLeft();
        game.goalLeft();
        game.goalRight();

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(game);
        }
        Game loaded;
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            loaded = (Game) in.readObject();
        }

        assertEquals("Alice", loaded.getPlayer1().getName());
        assertEquals("Bob", loaded.getPlayer2().getName());
        assertEquals(2, loaded.getScoreboard().getLeftScore());
        assertEquals(1, loaded.getScoreboard().getRightScore());
        assertEquals(7, loaded.getTarget());
        assertSame(loaded, loaded.getBall().getGame());
    }

    @Test
    void bothRacketsMoveIndependently() {
        Game game = newGame();
        GameCanvas canvas = new GameCanvas(game);
        Racket left = game.getPlayer1().getRacket();
        Racket right = game.getPlayer2().getRacket();
        left.setCanvas(canvas);
        right.setCanvas(canvas);

        left.move(-50);
        right.move(50);

        assertEquals(150, left.getPosY());
        assertEquals(250, right.getPosY());
    }

    @Test
    void racketStopsAtTheEdgesOfTheCanvas() {
        Game game = newGame();
        GameCanvas canvas = new GameCanvas(game);
        Racket racket = game.getPlayer1().getRacket();
        racket.setCanvas(canvas);

        racket.move(-10_000);
        assertEquals(0, racket.getPosY());

        racket.move(10_000);
        assertEquals(600 - racket.getHeight(), racket.getPosY());
    }

    @Test
    void gameBuilderRestoresSavedValues() {
        Game game = new com.example.the_ping_pong_game.database.GameBuilder()
                .setPlayer1Name("Ann")
                .setPlayer2Name("Ben")
                .setScores(3, 4)
                .setTarget(10)
                .build();

        assertEquals("Ann", game.getPlayer1().getName());
        assertEquals("Ben", game.getPlayer2().getName());
        assertEquals(3, game.getScoreboard().getLeftScore());
        assertEquals(4, game.getScoreboard().getRightScore());
        assertEquals(10, game.getTarget());
        assertFalse(game.hasWinner());
    }
}
