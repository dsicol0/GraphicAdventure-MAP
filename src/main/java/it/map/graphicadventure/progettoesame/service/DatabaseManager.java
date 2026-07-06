/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author antoniostilla
 */
public class DatabaseManager {

    private static final String URL = "jdbc:sqlite:final_exam.db";

    public DatabaseManager() {
        // Carica il driver SQLite e inizializza le tabelle
        try {
            Class.forName("org.sqlite.JDBC");
            initDatabase();
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("[DB ERROR] Impossibile inizializzare il database: " + e.getMessage());
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // Creazione delle tabelle se non esistono
    private void initDatabase() throws SQLException {
        String createGamesTable = "CREATE TABLE IF NOT EXISTS games ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "save_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "current_room TEXT NOT NULL, "
                + "health INTEGER NOT NULL"
                + ");";

        String createInventoryTable = "CREATE TABLE IF NOT EXISTS inventory_saves ("
                + "game_id INTEGER, "
                + "item_id TEXT NOT NULL, "
                + "FOREIGN KEY(game_id) REFERENCES games(id) ON DELETE CASCADE"
                + ");";

        String createLogTable = "CREATE TABLE IF NOT EXISTS event_log ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP, "
                + "event_type TEXT NOT NULL, " // VISITED, COLLECTED, ENCOUNTERED
                + "description TEXT NOT NULL"
                + ");";

        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(createGamesTable);
            stmt.execute(createInventoryTable);
            stmt.execute(createLogTable);
        }
    }

    // ==========================================
    // 1. SALVATAGGIO PARTITA
    // ==========================================
    public boolean saveGame(String roomName, int health, List<String> itemIds) {
        String insertGame = "INSERT INTO games (current_room, health) VALUES (?, ?);";
        String insertInventory = "INSERT INTO inventory_saves (game_id, item_id) VALUES (?, ?);";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false); // Transazione per garantire la consistenza

            int gameId;
            try (PreparedStatement pstmtGame = conn.prepareStatement(insertGame, Statement.RETURN_GENERATED_KEYS)) {
                pstmtGame.setString(1, roomName);
                pstmtGame.setInt(2, health);
                pstmtGame.executeUpdate();

                try (ResultSet generatedKeys = pstmtGame.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        gameId = generatedKeys.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            // Salviamo l'inventario associato
            try (PreparedStatement pstmtInv = conn.prepareStatement(insertInventory)) {
                for (String itemId : itemIds) {
                    pstmtInv.setInt(1, gameId);
                    pstmtInv.setString(2, itemId);
                    pstmtInv.addBatch();
                }
                pstmtInv.executeBatch();
            }

            conn.commit();
            logEvent("SYSTEM", "Partita salvata con successo. Slot ID: " + gameId);
            return true;
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Errore durante il salvataggio: " + e.getMessage());
            return false;
        }
    }

    // ==========================================
    // 2. CARICAMENTO PARTITA
    // ==========================================
    public SaveData loadLatestGame() {
        String selectGame = "SELECT * FROM games ORDER BY save_date DESC LIMIT 1;";
        String selectInventory = "SELECT item_id FROM inventory_saves WHERE game_id = ?;";

        SaveData loadedData = null;
        int loadedGameId = -1;

        // Blocco di SOLA LETTURA
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rsGame = stmt.executeQuery(selectGame)) {

            if (rsGame.next()) {
                loadedGameId = rsGame.getInt("id");
                String room = rsGame.getString("current_room");
                int health = rsGame.getInt("health");

                List<String> items = new ArrayList<>();
                try (PreparedStatement pstmtInv = conn.prepareStatement(selectInventory)) {
                    pstmtInv.setInt(1, loadedGameId);
                    try (ResultSet rsInv = pstmtInv.executeQuery()) {
                        while (rsInv.next()) {
                            items.add(rsInv.getString("item_id"));
                        }
                    }
                }
                loadedData = new SaveData(room, health, items);
            }
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Errore durante il caricamento: " + e.getMessage());
        }

        // SCRITTURA DEL LOG: Ora il blocco di lettura è chiuso, quindi SQLite è libero!
        if (loadedData != null) {
            logEvent("SYSTEM", "Caricato salvataggio ID: " + loadedGameId);
        }

        return loadedData;
    }

    // ==========================================
    // 3. LOG DEGLI EVENTI
    // ==========================================
    public void logEvent(String eventType, String description) {
        String query = "INSERT INTO event_log (event_type, description) VALUES (?, ?);";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, eventType.toUpperCase());
            pstmt.setString(2, description);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("[DB ERROR] Impossibile scrivere il log: " + e.getMessage());
        }
    }
    
    // Classe di utility interna per trasportare i dati caricati
    public static class SaveData {
        private final String roomName;
        private final int health;
        private final List<String> itemIds;

        public SaveData(String roomName, int health, List<String> itemIds) {
            this.roomName = roomName;
            this.health = health;
            this.itemIds = itemIds;
        }

        public String getRoomName() { return roomName; }
        public int getHealth() { return health; }
        public List<String> getItemIds() { return itemIds; }
    }
    
    // ==========================================
    // CONTROLLO ESISTENZA SALVATAGGI
    // ==========================================
    public boolean hasSavedGame() {
        String query = "SELECT COUNT(id) FROM games;";
        try (Connection conn = connect(); 
             Statement stmt = conn.createStatement(); 
             ResultSet rs = stmt.executeQuery(query)) {
             
            if (rs.next()) {
                // Ritorna true se il conteggio è maggiore di 0
                return rs.getInt(1) > 0; 
            }
        } catch (SQLException e) {
            // Se la tabella non esiste ancora, ovviamente non ci sono salvataggi
            return false;
        }
        return false;
    }
}
