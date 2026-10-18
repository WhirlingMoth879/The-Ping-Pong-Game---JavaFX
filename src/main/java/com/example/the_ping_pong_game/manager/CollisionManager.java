package com.example.the_ping_pong_game.manager;

import com.example.the_ping_pong_game.model.Ball;
import com.example.the_ping_pong_game.model.Racket;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;

/**
 * Provides methods for checking collisions between the ball,
 * rackets, borders, and goal areas.
 */
public class CollisionManager {

    /**
     * Checks whether the ball intersects a racket.
     * @param ball the ball being checked
     * @param racket the racket being checked
     * @return true if the ball collides with the racket, false otherwise
     */
    public static boolean isRacketCollision(Ball ball, Racket racket) {
        return new Rectangle(
                racket.getPosX(),
                racket.getPosY(),
                racket.getWidth(),
                racket.getHeight()
        ).getBoundsInParent().intersects(
                new Circle(
                        ball.getPosX() + ball.getRadius(),
                        ball.getPosY() + ball.getRadius(),
                        ball.getRadius()
                ).getBoundsInParent()
        );
    }

    /**
     * Checks whether the ball has hit the top border.
     * @param ball the ball being checked
     * @return true if the ball touches or crosses the top border
     */
    public static boolean isTopBorderCollision(Ball ball) {
        return ball.getPosY() <= 0;
    }

    /**
     * Checks whether the ball has hit the bottom border.
     * @param ball the ball being checked
     * @param canvasHeight the height of the game canvas
     * @return true if the ball touches or crosses the bottom border
     */
    public static boolean isBottomBorderCollision(Ball ball, double canvasHeight) {
        return ball.getPosY() + ball.getRadius() * 2 >= canvasHeight;
    }

    /**
     * Checks whether the ball has entered the left goal.
     * @param ball the ball being checked
     * @return true if the ball crosses the left edge
     */
    public static boolean isLeftGoal(Ball ball) {
        return ball.getPosX() <= 0;
    }

    /**
     * Checks whether the ball has entered the right goal.
     * @param ball the ball being checked
     * @param canvasWidth the width of the game canvas
     * @return true if the ball crosses the right edge
     */
    public static boolean isRightGoal(Ball ball, double canvasWidth) {
        return ball.getPosX() + ball.getRadius() * 2 >= canvasWidth;
    }
}

