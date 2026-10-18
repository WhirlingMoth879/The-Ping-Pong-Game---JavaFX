package com.example.the_ping_pong_game.manager;

import com.example.the_ping_pong_game.model.Ball;
import com.example.the_ping_pong_game.model.Game;
import com.example.the_ping_pong_game.model.Racket;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class CollisionTest {
    @Test
    void testRacketCollision() {
        Game game = new Game(800, 600,null,null);
        Ball ball = new Ball(game, 40, 100, 10, 2, 0);
        Racket racket = new Racket(20, 90, 30, 100, 0, 0);

        assertEquals(true, CollisionManager.isRacketCollision(ball, racket));
    }

    @Test
    void testTopBorderCollision() {
        Game game = new Game(800, 600,null,null);
        Ball ball = new Ball(game, 100, 0, 10, 2, -2);
        assertEquals(true, CollisionManager.isTopBorderCollision(ball));
    }

    @Test
    void testBottomBorderCollision() {
        Game game = new Game(800, 600,null,null);
        Ball ball = new Ball(game, 100, 580, 10, 2, 2);
        assertEquals(true, CollisionManager.isBottomBorderCollision(ball, 600));
    }

    @Test
    void testLeftGoal() {
        Game game = new Game(800, 600,null,null);
        Ball ball = new Ball(game, 0, 100, 10, -2, 0);

        assertEquals(true, CollisionManager.isLeftGoal(ball));
    }

    @Test
    void testRightGoal() {
        Game game = new Game(800, 600,null,null);
        Ball ball = new Ball(game, 780, 100, 10, 2, 0);

        assertEquals(true, CollisionManager.isRightGoal(ball, 800));
    }
}

