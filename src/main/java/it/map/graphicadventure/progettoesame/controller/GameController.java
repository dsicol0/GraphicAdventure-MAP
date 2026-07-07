/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
import it.map.graphicadventure.progettoesame.factory.GameDataInitializer;
import it.map.graphicadventure.progettoesame.service.DatabaseManager;
import it.map.graphicadventure.progettoesame.service.GameSaveDAO;
import it.map.graphicadventure.progettoesame.model.GameNPC;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.SaveData;
import it.map.graphicadventure.progettoesame.model.interfaces.Lockable;
import it.map.graphicadventure.progettoesame.model.interfaces.Openable;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class GameController extends BaseController {

    // Riferimenti ai due sotto-controller
    private final MovementController movementController;
    private final ObjInteractionController interactionController;

    private DatabaseManager dbManager;
    private GameSaveDAO saveDao;

    public GameController(EsameGame model, GameMainFrame view) {
        super(model, view);
        this.movementController = new MovementController(model, view);
        this.interactionController = new ObjInteractionController(model, view);

        try {
            this.dbManager = new DatabaseManager();
            java.sql.Connection conn = dbManager.getConnection();
            this.saveDao = new GameSaveDAO(conn);
            this.view.setContinueButtonEnabled(saveDao.hasSavedGame());

        } catch (SQLException ex) {
            System.err.println("Errore di inizializzazione Database: " + ex.getMessage());
            this.view.setContinueButtonEnabled(false);
        }
    }

    public void startNewGame() {
        try {
            model.init();
            GameDataInitializer.setUpGameData(model);

            Room initialRoom = model.getCurrentRoom();
            if (initialRoom != null) {
                view.showGamePanel();
                view.getGamePanel().renderRoom(initialRoom);

                try {
                    saveDao.logEvent("SYSTEM", "Iniziata nuova partita. Stanza iniziale: " + initialRoom.getName());
                } catch (SQLException e) {
                    System.err.println("Errore durante il logging: " + e.getMessage());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public Room getCurrentRoom() {
        return model.getCurrentRoom();
    }

    public Player getPlayer() {
        return model.getPlayer();
    }

    // Gestione movimento pulita (senza imboscate automatiche dello zombie)
    public String handleMovement(String direction) {
        String response = movementController.handleMovement(direction);
        Room currentRoom = model.getCurrentRoom();

        if (currentRoom != null) {
            try {
                saveDao.logEvent("VISITED", "Il giocatore si è mosso a " + direction + " entrando in: " + currentRoom.getName());
            } catch (java.sql.SQLException e) {
                System.err.println("Errore: " + e.getMessage());
            }

            silentAutosave();
        }

        return response;
    }

    // Gestione interazione: il combattimento parte SOLO quando clicchi fisicamente lo zombie
    public String handleObjectInteraction(GameObject clickedObject) {

        if (clickedObject instanceof GameNPC) {
            GameNPC enemy = (GameNPC) clickedObject;
            
            // MVC PURO: Chiediamo alla View di gestire la finestra di combattimento
            // e ci facciamo restituire un semplice intero che rappresenta l'esito:
            // 1 = Vittoria, 2 = Fuga, 3 = Morte/Sconfitta
            int combatResult = view.showCombatWindow(enemy, model.getPlayer(), model.getInventory());
            view.getGamePanel().updateJlHealth();

            if (combatResult == 1) {
                // 1. Lo togliamo dalla stanza corrente nella sessione attuale
                model.getCurrentRoom().getObjects().remove(enemy);

                // 2. REGISTRIAMO L'ID NELLA LISTA DEI MORTI DEL MODELLO
                if (!model.getDeadZombies().contains(String.valueOf(enemy.getId()))) {
                    model.getDeadZombies().add(String.valueOf(enemy.getId()));
                }

                // 3. Logghiamo l'evento nel DB
                try {
                    saveDao.logEvent("KILLED", "Il giocatore ha sconfitto lo zombie: " + enemy.getName());
                } catch (java.sql.SQLException e) {
                    System.err.println("Errore nel salvataggio dell'uccisione nel DB: " + e.getMessage());
                }

                // 4. Autosave immediato per blindare il salvataggio
                silentAutosave();

                // 5. FINE PARTITA - VITTORIA
                int punteggio = calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size());
                String classifica = sendAndGetLeaderboard("Matricola", punteggio);
                
                return "Hai sconfitto " + enemy.getName() + "!";
                
            } else if (combatResult == 2) {
                return "Sei fuggito dal combattimento in preda al panico!";
                
            } else if (combatResult == 3 || model.getPlayer().getHp() <= 0) {
                // 5. FINE PARTITA - SCONFITTA (GAME OVER)
                int punteggio = calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size());
                String classifica = sendAndGetLeaderboard("Matricola_Bocciata", punteggio / 2);

                view.showMainMenu();
                
                return "Sei morto... Ricarica un salvataggio dal menù principale.";
            }
            return "Combattimento interrotto.";
        }

        // Se non è un nemico, prosegui con la normale interazione oggetti
        String response = interactionController.handleObjectInteraction(clickedObject);

        try {
            saveDao.logEvent("INTERACTED", "Il giocatore ha interagito con l'oggetto: " + clickedObject.getName());
        } catch (java.sql.SQLException e) {
            System.err.println("Errore durante il logging dell'interazione: " + e.getMessage());
        }

        view.getGamePanel().updateJlHealth();
        silentAutosave();
        return response;
    }

    public String showInventory() {
        List<GameObject> inventory = model.getInventory();
        if (inventory == null || inventory.isEmpty()) {
            return "Il tuo zaino è vuoto.";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("--- INVENTARIO ---\n");
        for (GameObject obj : inventory) {
            sb.append("> ").append(obj.getName()).append("\n");
        }
        return sb.toString();
    }

    public void handleInventoryToggle() {
        List<GameObject> inventoryItems = model.getInventory();
        view.getGamePanel().toggleInventory(inventoryItems);
    }


    public void saveCurrentGame() {
        String currentRoomName = model.getCurrentRoom().getName();
        int health = model.getPlayer().getHp();

        List<String> itemIds = new ArrayList<>();
        for (GameObject obj : model.getInventory()) {
            itemIds.add(String.valueOf(obj.getId()));
        }

        try {
            // 🟩 Sinergia perfetta: riutilizziamo il metodo helper condiviso!
            saveDao.saveGame(currentRoomName, health, itemIds, model.getDeadZombies(), model.getUnlockedRooms());

            // Opzionale: un feedback visivo per rassicurare il giocatore
            view.getGamePanel().animatedText("> Salvataggio completato con successo.");
        } catch (SQLException e) {
            view.getGamePanel().animatedText("[ERRORE] Impossibile salvare la partita.");
            System.err.println("Errore nel salvataggio esplicito: " + e.getMessage());
        }
    }

    public void loadSavedGame() {
        SaveData data = null;
        try {
            data = saveDao.getLatestSave();
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del salvataggio: " + e.getMessage());
        }

        if (data != null) {
            // 1. Ripristina salute
            model.getPlayer().setHp(data.getHealth());

            // 2. Ripristina stanza corrente
            Room savedRoom = null;
            for (Room r : model.getRooms()) {
                if (r.getName().trim().equalsIgnoreCase(data.getRoomName().trim())) {
                    savedRoom = r;
                    break;
                }
            }

            if (savedRoom != null) {
                model.setCurrentRoom(savedRoom);
            } else {
                System.err.println("[WARNING] Stanza salvata '" + data.getRoomName() + "' non trovata.");
            }

            // 3. 🎒 RIPRISTINA INVENTARIO (Cerca gli oggetti anche DENTRO lo zaino!)
            model.getInventory().clear();
            for (String itemId : data.getItemIds()) {
                GameObject foundObj = null;

                for (Room r : model.getRooms()) {
                    if (r.getObjects() != null) {
                        for (int i = 0; i < r.getObjects().size(); i++) {
                            GameObject obj = r.getObjects().get(i);

                            // Caso A: L'oggetto è per terra nella stanza
                            if (String.valueOf(obj.getId()).equals(itemId)) {
                                foundObj = obj;
                                r.getObjects().remove(i);
                                break;
                            }

                            // Caso B: L'oggetto è nascosto dentro un contenitore (es. lo zaino)
                            if (obj instanceof ObjectContainer) {
                                ObjectContainer<?> container = (ObjectContainer<?>) obj;
                                if (container.getInsideItems() != null) {
                                    for (int j = 0; j < container.getInsideItems().size(); j++) {
                                        Object nested = container.getInsideItems().get(j);
                                        if (nested instanceof GameObject && String.valueOf(((GameObject) nested).getId()).equals(itemId)) {
                                            foundObj = (GameObject) nested;
                                            container.getInsideItems().remove(j); // Lo estrae dal contenitore
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (foundObj != null) {
                        break;
                    }
                }

                // Fallback di sicurezza se non viene trovato dinamicamente
                if (foundObj == null) {
                    for (GameObject obj : model.getAllObjects()) {
                        if (String.valueOf(obj.getId()).equals(itemId)) {
                            foundObj = obj;
                            break;
                        }
                    }
                }

                if (foundObj != null) {
                    model.getInventory().add(foundObj);
                }
            }

            // 4. Ripristina la lista dei morti e cancellali dal mondo rigenerato
            model.getDeadZombies().clear();
            model.getDeadZombies().addAll(data.getKilledEnemyIds());

            for (String deadId : model.getDeadZombies()) {
                for (Room r : model.getRooms()) {
                    if (r.getObjects() != null) {
                        r.getObjects().removeIf(obj -> String.valueOf(obj.getId()).equals(deadId));
                    }
                }
            }

            // 5. Sincronizzazione stanze sbloccate
            model.getUnlockedRooms().clear();
            if (data.getUnlockedRoomIds() != null) {
                model.getUnlockedRooms().addAll(data.getUnlockedRoomIds());
            }

            for (String idOrName : model.getUnlockedRooms()) {
                for (Room r : model.getRooms()) {
                    if (String.valueOf(r.getId()).equals(idOrName) || r.getName().trim().equalsIgnoreCase(idOrName.trim())) {
                        r.setLocked(false);
                    }
                }
            }

            // Se l'Aula 2 risulta già sbloccata, eliminiamo la chiave (ID 8) dalle stanze
            if (model.getUnlockedRooms().contains("2") || model.getUnlockedRooms().contains("Aula 2")) {
                for (Room r : model.getRooms()) {
                    if (r.getObjects() != null) {
                        r.getObjects().removeIf(obj -> obj.getId() == 8);
                    }
                }
            }

            // 🟩 DISINTEGRAZIONE ZAINO FANTASMA (ID 16)
            // Se nel database risulta che hai già addosso la chiave (17) o l'accendino (11),
            // significa che hai già preso lo zaino! Lo cancelliamo dalle stanze per non farlo riapparire.
            if (data.getItemIds().contains("17") || data.getItemIds().contains("11")) {
                for (Room r : model.getRooms()) {
                    if (r.getObjects() != null) {
                        r.getObjects().removeIf(obj -> obj.getId() == 16);
                    }
                }
            }

            // Aggiorna la vista grafica
            view.getGamePanel().renderRoom(model.getCurrentRoom());
            view.getGamePanel().animatedText("Salvataggio caricato. Bentornato nella sessione.");
        } else {
            view.getGamePanel().animatedText("Nessun salvataggio trovato nel database.");
        }
    }

    public void continueSavedGame() {
        try {
            model.init();
            GameDataInitializer.setUpGameData(model);
            view.showGamePanel();
            loadSavedGame(); // Innesca il caricamento e la pulizia dei nemici morti

        } catch (Exception ex) {
            System.err.println("[ERRORE CRITICO] Fallimento durante il caricamento del mondo.");
            ex.printStackTrace();
        }
    }

    private void silentAutosave() {
        if (model.getCurrentRoom() == null || model.getPlayer() == null) {
            return;
        }

        String currentRoomName = model.getCurrentRoom().getName();
        int health = model.getPlayer().getHp();

        List<String> itemIds = new java.util.ArrayList<>();
        for (GameObject obj : model.getInventory()) {
            itemIds.add(String.valueOf(obj.getId()));
        }

        try {
            // 🟩 Pulito, leggibile e riutilizzabile!
            saveDao.saveGame(currentRoomName, health, itemIds, model.getDeadZombies(), model.getUnlockedRooms());
        } catch (SQLException e) {
            System.err.println("Autosave fallito: " + e.getMessage());
        }
    }

    public int calculateFinalScore(int minutesTaken, int itemsCollected, int zombiesDefeated) {
        int baseScore = 1000;
        // Malus tempo: -10 punti per ogni minuto passato
        int timePenalty = minutesTaken * 10;
        // Bonus: 50 punti per oggetto, 200 per ogni professore-zombie sconfitto
        int itemBonus = itemsCollected * 50;
        int combatBonus = zombiesDefeated * 200;

        return Math.max(0, baseScore - timePenalty + itemBonus + combatBonus);
    }

    public String fetchOnlyLeaderboard() {
        StringBuilder leaderboard = new StringBuilder();
        try (java.net.Socket socket = new java.net.Socket("localhost", 6666); java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(socket.getInputStream())); java.io.PrintWriter out = new java.io.PrintWriter(new java.io.OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("#top"); // Chiediamo solo la classifica
            String response = in.readLine();
            if ("#start_top".equals(response)) {
                String line;
                while ((line = in.readLine()) != null && !line.equals("#end_top")) {
                    leaderboard.append(line).append("\n");
                }
            }
            out.println("#exit");
        } catch (Exception e) {
            return "Errore di connessione al server: " + e.getMessage();
        }
        return leaderboard.toString();
    }

    public String sendAndGetLeaderboard(String playerName, int finalScore) {
        StringBuilder leaderboard = new StringBuilder();
        try (Socket socket = new Socket("localhost", 6666); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // 1. Invio il punteggio
            out.println("#score " + playerName + " " + finalScore);
            System.out.println(in.readLine()); // Leggo la risposta (dovrebbe essere #ok)

            // 2. Richiedo la classifica
            out.println("#top");
            String response = in.readLine();
            if ("#start_top".equals(response)) {
                String line;
                while ((line = in.readLine()) != null && !line.equals("#end_top")) {
                    leaderboard.append(line).append("\n");
                }
            }

            // 3. Chiudo garbatamente
            out.println("#exit");

        } catch (Exception e) {
            return "Errore di connessione al server: " + e.getMessage();
        }

        return leaderboard.toString();
    }
   
}
