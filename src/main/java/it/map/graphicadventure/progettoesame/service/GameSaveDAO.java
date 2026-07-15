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
 * Implementazione del pattern DAO (Data Access Object).
 * 
 * Fornisce un'interfaccia a oggetti, prendendo in carico la trasformazione 
 * delle query SQL tramite l'API JDBC.
 *
 */
public class GameSaveDAO {

    /** Riferimento alla connessione attiva verso il DBMS. */
    private final Connection connection;

    /**
     * Costruisce il DAO iniettando la connessione attiva.
     *
     * @param connection Oggetto Connection fornito dal {@link DatabaseManager}.
     */
    public GameSaveDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * Classe di supporto (interna) per gestire il salvataggio dello stato 
     * dei singoli oggetti.
     */
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

    /**
     * Esegue il salvataggio completo della sessione di gioco all'interno del Database.
     *
     * @param roomName ID testuale della stanza corrente.
     * @param health Punti vita attuali del giocatore.
     * @param itemIds Lista degli ID degli oggetti nello zaino.
     * @param killedEnemyIds Lista degli ID dei nemici abbattuti.
     * @param unlockedRoomIds Lista delle porte precedentemente bloccate e ora aperte.
     * @param timeRemaining Secondi rimanenti per il timer di gioco.
     * @param powerRestored Stato della corrente elettrica.
     * @param objectStates Lista degli stati per casse e porte (aperto/chiuso).
     * @throws SQLException In caso di errore durante l'esecuzione delle query.
     */
    public void saveGame(String roomName, int health, List<String> itemIds, List<String> killedEnemyIds, List<String> unlockedRoomIds, int timeRemaining, boolean powerRestored, List<ObjectSave> objectStates) throws SQLException {
        // Inizia la transazione
        connection.setAutoCommit(false);

        // Salva i dati base della partita e recupera la chiave primaria generata (gameId)
        PreparedStatement stmGame = connection.prepareStatement("INSERT INTO games(current_room, health, time_remaining, power_restored) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmGame.setString(1, roomName);
        stmGame.setInt(2, health);
        stmGame.setInt(3, timeRemaining);
        stmGame.setBoolean(4, powerRestored);
        stmGame.executeUpdate();

        int gameId = -1;
        ResultSet keys = stmGame.getGeneratedKeys();
        if (keys.next()) {
            gameId = keys.getInt(1);
        }
        keys.close();
        stmGame.close();

        // Salva l'inventario usando executeBatch per ottimizzare la scrittura
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

        // Salva le porte sbloccate
        PreparedStatement stmRooms = connection.prepareStatement("INSERT INTO unlocked_rooms_saves(game_id, room_id) VALUES (?, ?)");
        for (String roomId : unlockedRoomIds) {
            stmRooms.setInt(1, gameId);
            stmRooms.setString(2, roomId);
            stmRooms.addBatch();
        }
        stmRooms.executeBatch();
        stmRooms.close();

        // Salva lo stato degli oggetti interattivi (Bauli, Serrature, ecc.)
        PreparedStatement stmObj = connection.prepareStatement("INSERT INTO object_saves(game_id, object_id, is_locked, is_open) VALUES (?, ?, ?, ?)");
        for (ObjectSave os : objectStates) {
            stmObj.setInt(1, gameId);
            stmObj.setString(2, os.getObjectId());
            stmObj.setInt(3, os.isLocked() ? 1 : 0);
            stmObj.setInt(4, os.isOpen() ? 1 : 0);
            stmObj.addBatch();
        }
        stmObj.executeBatch();
        stmObj.close();

        // Conferma la transazione
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Recupera l'ultimo salvataggio effettuato ricaricando le informazioni da 
     * tutte le tabelle relazionali interessate.
     *
     * Utilizza un oggetto {@link Statement} semplice (senza parametri) per la 
     * query principale di selezione, per poi iterare sul {@link ResultSet} per 
     * navigare le tuple restituite (il puntatore viene mosso tramite {@code rs.next()}).
     *
     *
     * @return L'oggetto {@link SaveData} riempito con lo stato della partita, 
     * oppure {@code null} se non ci sono salvataggi.
     * @throws SQLException Se si verifica un errore durante le query di SELECT.
     */
    public SaveData getLatestSave() throws SQLException {
        Statement stm = connection.createStatement();
        ResultSet rs = stm.executeQuery("SELECT id, current_room, health, time_remaining, power_restored FROM games ORDER BY id DESC LIMIT 1");

        SaveData data = null;
        int gameId = -1;

        if (rs.next()) {
            gameId = rs.getInt("id");
            String room = rs.getString("current_room");
            int health = rs.getInt("health");
            int timeRemaining = rs.getInt("time_remaining");
            boolean powerRestored = rs.getBoolean("power_restored");
            
         
            data = new SaveData(room, health, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), timeRemaining, powerRestored, new ArrayList<>());
        }
        rs.close();
        stm.close();

        // Se un salvataggio principale esiste, procede al caricamento delle relazioni
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

            // Recupera le stanze sbloccate
            PreparedStatement pstmRooms = connection.prepareStatement("SELECT room_id FROM unlocked_rooms_saves WHERE game_id = ?");
            pstmRooms.setInt(1, gameId);
            ResultSet rsRooms = pstmRooms.executeQuery();
            while (rsRooms.next()) {
                data.getUnlockedRoomIds().add(rsRooms.getString("room_id")); 
            }
            rsRooms.close();
            pstmRooms.close();
            
            // Recupera gli stati degli oggetti interattivi (Bauli e Porte)
            PreparedStatement pstmObj = connection.prepareStatement("SELECT object_id, is_locked, is_open FROM object_saves WHERE game_id = ?");
            pstmObj.setInt(1, gameId);
            ResultSet rsObj = pstmObj.executeQuery();
            while (rsObj.next()) {
                data.getObjectStates().add(new ObjectSave(
                        rsObj.getString("object_id"),
                        rsObj.getInt("is_locked") == 1,
                        rsObj.getInt("is_open") == 1
                ));
            }
            rsObj.close();
            pstmObj.close();
        }
        return data;
    }

    /**
     * Inserisce un nuovo record nel log degli eventi del database.
     *
     * @param eventType La categoria dell'evento (es. KILLED, INTERACTED, SYSTEM).
     * @param description Una descrizione discorsiva di cosa è successo.
     * @throws SQLException Se l'inserimento non va a buon fine.
     */
    public void logEvent(String eventType, String description) throws SQLException {
        PreparedStatement stm = connection.prepareStatement("INSERT INTO event_log(event_type, description) VALUES (?, ?)");
        stm.setString(1, eventType.toUpperCase());
        stm.setString(2, description);
        stm.executeUpdate();
        stm.close();
    }

    /**
     * Interroga il database per verificare la presenza di salvataggi pregressi.
     * Viene usato per capire se attivare il tasto "Continua Partita" nel menù.
     *
     * @return {@code true} se la tabella 'games' contiene almeno un record, {@code false} altrimenti.
     * @throws SQLException Se si verifica un errore durante la query di conteggio.
     */
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