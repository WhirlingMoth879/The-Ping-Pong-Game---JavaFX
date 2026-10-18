package com.example.the_ping_pong_game.database;

import com.example.the_ping_pong_game.model.Game;

/**
 * Data Access Object for storing games in a database.
 */
public interface GameDAO {
    void saveGame(String gameName, Game game);
    Game loadGameByName(String gameName);
}
