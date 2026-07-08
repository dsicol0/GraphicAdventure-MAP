/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;

import it.map.graphicadventure.progettoesame.model.SaveData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antoniostilla
 */
public class GameSaveDAO {

    private final Connection connection;

    public GameSaveDAO(Connection connection) {
        this.connection = connection;
    }

    // CLASSE DI SUPPORTO INTERNA PER LO STATO DEGLI OGGETTI
    public static class ObjectSave {

        private final String objectId;
        private final boolean locked;
        private final boolean open;

        public ObjectSave(String objectId, boolean locked, boolean open) {
            this.objectId = objectId;
            this.locked = locked;
            this.open = open;
        }

        public String getObjectId() {
            return objectId;
        }

        public boolean isLocked() {
            return locked;
        }

        public boolean isOpen() {
            return open;
        }
    }

    // AGGIORNATO: Accetta anche la lista degli stati degli oggetti
    public void saveGame(String roomName, int health, List<String> itemIds, List<String> killedEnemyIds, List<String> unlockedRoomIds, int timeRemaining, boolean ambushActive) throws SQLException {
        connection.setAutoCommit(false);

        PreparedStatement stmGame = connection.prepareStatement("INSERT INTO games(current_room, health, time_remaining, ambush_happening) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmGame.setString(1, roomName);
        stmGame.setInt(2, health);
        stmGame.setInt(3, timeRemaining);
        stmGame.setInt(4, ambushActive ? 1 : 0); // Trasforma boolean in 1 o 0
        stmGame.executeUpdate();

        int gameId = -1;
        ResultSet keys = stmGame.getGeneratedKeys();
        if (keys.next()) {
            gameId = keys.getInt(1);
        }
        keys.close();
        stmGame.close();

        // Salva inventario
        PreparedStatement stmInv = connection.prepareStatement("INSERT INTO inventory_saves(game_id, item_id) VALUES (?, ?)");
        for (String itemId : itemIds) {
            stmInv.setInt(1, gameId);
            stmInv.setString(2, itemId);
            stmInv.addBatch();
        }
        stmInv.executeBatch();
        stmInv.close();

        // Salva i nemici sconfitti
        PreparedStatement stmKilled = connection.prepareStatement("INSERT INTO killed_enemies_saves(game_id, enemy_id) VALUES (?, ?)");
        for (String enemyId : killedEnemyIds) {
            stmKilled.setInt(1, gameId);
            stmKilled.setString(2, enemyId);
            stmKilled.addBatch();
        }
        stmKilled.executeBatch();
        stmKilled.close();

        PreparedStatement stmRooms = connection.prepareStatement("INSERT INTO unlocked_rooms_saves(game_id, room_id) VALUES (?, ?)");
        for (String roomId : unlockedRoomIds) {
            stmRooms.setInt(1, gameId);
            stmRooms.setString(2, roomId);
            stmRooms.addBatch();
        }
        stmRooms.executeBatch();
        stmRooms.close();

        connection.commit();
        connection.setAutoCommit(true);
    }

    public SaveData getLatestSave() throws SQLException {
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id, current_room, health, time_remaining, ambush_happening FROM games ORDER BY save_date DESC LIMIT 1");

        SaveData data = null;
        int gameId = -1;

        if (rs.next()) {
            gameId = rs.getInt("id");
            String room = rs.getString("current_room");
            int health = rs.getInt("health");
            int timeRemaining = rs.getInt("time_remaining");
            boolean ambushActive = rs.getInt("ambush_happening") == 1;

            data = new SaveData(room, health, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), timeRemaining, ambushActive);
        }
        rs.close();
        stm.close();

        if (data != null && gameId != -1) {
            // Recupera inventario
            PreparedStatement pstm = connection.prepareStatement("SELECT item_id FROM inventory_saves WHERE game_id = ?");
            pstm.setInt(1, gameId);
            ResultSet rsInv = pstm.executeQuery();
            while (rsInv.next()) {
                data.getItemIds().add(rsInv.getString("item_id"));
            }
            rsInv.close();
            pstm.close();

            // Recupera i nemici sconfitti
            PreparedStatement pstmKilled = connection.prepareStatement("SELECT enemy_id FROM killed_enemies_saves WHERE game_id = ?");
            pstmKilled.setInt(1, gameId);
            ResultSet rsKilled = pstmKilled.executeQuery();
            while (rsKilled.next()) {
                data.getKilledEnemyIds().add(rsKilled.getString("enemy_id"));
            }
            rsKilled.close();
            pstmKilled.close();

            PreparedStatement pstmRooms = connection.prepareStatement("SELECT room_id FROM unlocked_rooms_saves WHERE game_id = ?");
            pstmRooms.setInt(1, gameId);
            ResultSet rsRooms = pstmRooms.executeQuery();
            while (rsRooms.next()) {
                data.getUnlockedRoomIds().add(rsRooms.getString("room_id")); // Assicurati di avere questo getter/lista in SaveData
            }
            rsRooms.close();
            pstmRooms.close();
        }
        return data;
    }

    public void logEvent(String eventType, String description) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("INSERT INTO event_log(event_type, description) VALUES (?, ?)");
        stm.setString(1, eventType.toUpperCase());
        stm.setString(2, description);
        stm.executeUpdate();
        stm.close();
    }

    public boolean hasSavedGame() throws SQLException {
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT COUNT(id) FROM games");
        boolean hasSaves = false;
        if (rs.next()) {
            hasSaves = rs.getInt(1) > 0;
        }
        rs.close();
        stm.close();
        return hasSaves;
    }
}
