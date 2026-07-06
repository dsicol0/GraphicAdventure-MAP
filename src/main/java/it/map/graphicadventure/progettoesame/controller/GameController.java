/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
import it.map.graphicadventure.progettoesame.factory.GameDataInitializer;
import it.map.graphicadventure.progettoesame.service.DatabaseManager;
import it.map.graphicadventure.progettoesame.service.GameSaveDAO;
import it.map.graphicadventure.progettoesame.type.Player;
import it.map.graphicadventure.progettoesame.type.SaveData;
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
        // Li inizializziamo passandogli il modello e la view
        this.movementController = new MovementController(model, view);
        this.interactionController = new ObjInteractionController(model, view);
        
        try {
            // 1. Inizializza il DB e ottiene la connessione persistente
            this.dbManager = new DatabaseManager(); 
            java.sql.Connection conn = dbManager.getConnection();
            
            // 2. Inizializza il DAO passando la connessione (Stile Prof!)
            this.saveDao = new GameSaveDAO(conn);
            
            // 3. Controlla il tasto continua
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
                
                // Nuovo Log con DAO e try-catch
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
        return model.getPlayer(); // Oppure "return this.player;" a seconda di come lo avevi inizializzato
    }

    // 🟩 Passa l'azione direttamente al controller dei movimenti
    public String handleMovement(String direction) {
        String response = movementController.handleMovement(direction);
        
        // Dopo il movimento, registriamo la stanza in cui si trova il giocatore
        Room currentRoom = model.getCurrentRoom();
        if (currentRoom != null) {
            try {
                saveDao.logEvent("VISITED", "Il giocatore si è mosso a " + direction + " entrando in: " + currentRoom.getName());
            } catch (SQLException e) {
                System.err.println("Errore durante il logging del movimento: " + e.getMessage());
            }
        }
        
        silentAutosave();
        
        return response;
    }

    // 🟩 Passa l'azione direttamente al controller delle interazioni
    public String handleObjectInteraction(GameObject clickedObject) {
        String response = interactionController.handleObjectInteraction(clickedObject);
        
        // Registra l'interazione con l'oggetto
        try {
            saveDao.logEvent("INTERACTED", "Il giocatore ha interagito con l'oggetto: " + clickedObject.getName());
        } catch (SQLException e) {
            System.err.println("Errore durante il logging dell'interazione: " + e.getMessage());
        }
        
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
            // Il nuovo DAO lancia eccezione se fallisce
            saveDao.saveGame(currentRoomName, health, itemIds);
            view.getGamePanel().animatedText("Salvataggio completato con successo nel database.");
        } catch (SQLException e) {
            view.getGamePanel().animatedText("[ERRORE] Impossibile salvare la partita.");
            System.err.println("Errore nel salvataggio esplicito: " + e.getMessage());
        }
    }

    public void loadSavedGame() {
        SaveData data = null;
        try {
            // Chiamata aggiornata al metodo del DAO
            data = saveDao.getLatestSave();
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del salvataggio: " + e.getMessage());
        }
        
        if (data != null) {
            // 1. Ripristina salute
            model.getPlayer().setHp(data.getHealth());
            
            // 2. Ripristina stanza in modo BLINDATO (ignora spazi extra e differenze maiuscole)
            Room savedRoom = null;
            for (Room r : model.getRooms()) {
                if (r.getName().trim().equalsIgnoreCase(data.getRoomName().trim())) {
                    savedRoom = r;
                    break;
                }
            }
            
            // 🟩 IL SALVAGENTE: Impostiamo la stanza solo se l'abbiamo trovata!
            if (savedRoom != null) {
                model.setCurrentRoom(savedRoom);
            } else {
                System.err.println("[WARNING] Stanza salvata '" + data.getRoomName() + "' non trovata. Resto nella stanza iniziale.");
            }
            
            // 3. Ripristina Inventario
            model.getInventory().clear();
            for (String itemId : data.getItemIds()) {
                for (GameObject obj : model.getAllObjects()) {
                    if (String.valueOf(obj.getId()).equals(itemId)) {
                        model.getInventory().add(obj);
                        
                        // Rimuoviamo l'oggetto dalla stanza di origine per evitare cloni!
                        for (Room r : model.getRooms()) {
                            r.getObjects().remove(obj);
                        }
                        break;
                    }
                }
            }
            
            // Aggiorna la vista
            view.getGamePanel().renderRoom(model.getCurrentRoom());
            view.getGamePanel().animatedText("Salvataggio caricato. Bentornato nella sessione.");
        } else {
            view.getGamePanel().animatedText("Nessun salvataggio trovato nel database.");
        }
    }
    
    public void continueSavedGame() {
        try {
            // 1. Costruiamo il mondo di gioco base (legge il file .txt e crea mappa/oggetti)
            model.init();
            GameDataInitializer.setUpGameData(model);
            
            // 2. Passiamo dalla schermata del Menù a quella di Gioco
            view.showGamePanel();
            
            // 3. Invece di far partire il render della stanza iniziale (Aula Studio),
            // invochiamo il caricamento dal Database!
            loadSavedGame();
            
        } catch (Exception ex) {
            System.err.println("[ERRORE CRITICO] Fallimento durante il caricamento del mondo di gioco.");
            ex.printStackTrace();
        }
    }
    
    private void silentAutosave() {
        if (model.getCurrentRoom() == null || model.getPlayer() == null) return;
        
        String currentRoomName = model.getCurrentRoom().getName();
        int health = model.getPlayer().getHp(); // Uniformato a getHp() come sopra
        
        java.util.List<String> itemIds = new java.util.ArrayList<>();
        for (GameObject obj : model.getInventory()) {
            itemIds.add(String.valueOf(obj.getId()));
        }

        try {
            saveDao.saveGame(currentRoomName, health, itemIds);
        } catch (SQLException e) {
            System.err.println("Autosave fallito: " + e.getMessage());
        }
    }
}
