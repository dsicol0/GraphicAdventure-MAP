/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;

import it.map.graphicadventure.progettoesame.type.SaveData;
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

    public void saveGame(String roomName, int health, List<String> itemIds) throws SQLException {
        connection.setAutoCommit(false); 

        PreparedStatement stmGame = connection.prepareStatement("INSERT INTO games(current_room, health) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmGame.setString(1, roomName);
        stmGame.setInt(2, health);
        stmGame.executeUpdate();

        int gameId = -1;
        ResultSet keys = stmGame.getGeneratedKeys();
        if (keys.next()) {
            gameId = keys.getInt(1);
        }
        keys.close();
        stmGame.close();

        PreparedStatement stmInv = connection.prepareStatement("INSERT INTO inventory_saves(game_id, item_id) VALUES (?, ?)");
        for (String itemId : itemIds) {
            stmInv.setInt(1, gameId);
            stmInv.setString(2, itemId);
            stmInv.addBatch();
        }
        stmInv.executeBatch();
        stmInv.close();

        connection.commit();
        connection.setAutoCommit(true); 
    }

    public SaveData getLatestSave() throws SQLException {
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id, current_room, health FROM games ORDER BY save_date DESC LIMIT 1");
        
        SaveData data = null;
        int gameId = -1;

        if (rs.next()) {
            gameId = rs.getInt("id");
            String room = rs.getString("current_room");
            int health = rs.getInt("health");
            data = new SaveData(room, health, new ArrayList<>());
        }
        rs.close();
        stm.close();

        // Se abbiamo trovato un salvataggio, recuperiamo l'inventario associato
        if (data != null && gameId != -1) {
            PreparedStatement pstm = connection.prepareStatement("SELECT item_id FROM inventory_saves WHERE game_id = ?");
            pstm.setInt(1, gameId);
            ResultSet rsInv = pstm.executeQuery();
            while (rsInv.next()) {
                data.getItemIds().add(rsInv.getString("item_id"));
            }
            rsInv.close();
            pstm.close();
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
