package com.example.the_ping_pong_game;

/**
 * Implemented by game objects that scale with the window size.
 */
public interface Resizable {
    void resizeX(double factor);
    void resizeY(double factor);
}
