/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;
import java.sql.*;
import java.util.Properties;

/**
 *
 * @author antoniostilla
 */
public class DatabaseManager {

    
    private static final String TABLE_GAMES = "CREATE TABLE IF NOT EXISTS games ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "save_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "current_room TEXT NOT NULL, "
            + "health INTEGER NOT NULL, " 
            + "time_remaining INTEGER DEFAULT 900)";

    private static final String TABLE_INVENTORY = "CREATE TABLE IF NOT EXISTS inventory_saves ("
            + "game_id INTEGER, "
            + "item_id TEXT NOT NULL, "
            + "FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE)";

    private static final String TABLE_LOG = "CREATE TABLE IF NOT EXISTS event_log ("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
            + "event_type TEXT NOT NULL, "
            + "description TEXT NOT NULL)";


    private static final String TABLE_KILLED_ENEMIES = "CREATE TABLE IF NOT EXISTS killed_enemies_saves ("
            + "game_id INTEGER, "
            + "enemy_id TEXT NOT NULL, "
            + "FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE)";
    

    private static final String TABLE_OBJECT_SAVES = "CREATE TABLE IF NOT EXISTS object_saves ("
            + "game_id INTEGER, "
            + "object_id TEXT NOT NULL, "
            + "is_locked INTEGER NOT NULL, " // 1 = Chiuso a chiave, 0 = Sbloccato
            + "is_open INTEGER NOT NULL, "   // 1 = Aperto, 0 = Chiuso
            + "FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE)";
    
    private static final String TABLE_UNLOCKED_ROOMS = "CREATE TABLE IF NOT EXISTS unlocked_rooms_saves ("
        + "game_id INTEGER, "
        + "room_id TEXT NOT NULL, "
        + "FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE)";

    private Connection conn = null;

    public Connection getConnection() throws SQLException {
        if (conn != null) {
            return conn;
        } else {
            
            Properties dbprops = new Properties();
            conn = DriverManager.getConnection("jdbc:sqlite:final_exam.db", dbprops);
            
            Statement stm = conn.createStatement();
            stm.executeUpdate(TABLE_GAMES);
            stm.executeUpdate(TABLE_INVENTORY);
            stm.executeUpdate(TABLE_LOG);
            stm.executeUpdate(TABLE_KILLED_ENEMIES);
            stm.executeUpdate(TABLE_OBJECT_SAVES);
            stm.executeUpdate(TABLE_UNLOCKED_ROOMS);
            stm.close();
            
            return conn;
        }
    }

    public void closeConnection() throws SQLException {
        if (this.conn != null) {
            conn.close();
            conn = null;
        }
    }
}