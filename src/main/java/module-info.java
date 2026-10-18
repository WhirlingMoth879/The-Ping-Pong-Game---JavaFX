module com.example.the_ping_pong_game {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;

    opens com.example.the_ping_pong_game to javafx.fxml;

    exports com.example.the_ping_pong_game;
    exports com.example.the_ping_pong_game.model;
    exports com.example.the_ping_pong_game.manager;
    exports com.example.the_ping_pong_game.database;
}
