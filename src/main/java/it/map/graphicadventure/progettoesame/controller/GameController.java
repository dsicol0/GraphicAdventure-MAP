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
import it.map.graphicadventure.progettoesame.model.Zombie;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.service.NetworkService;
import it.map.graphicadventure.progettoesame.service.SaveManager;
import it.map.graphicadventure.progettoesame.threads.GeneratorThread;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author David
 */
public class GameController extends BaseController {

    // Riferimenti ai due sotto-controller
    private final MovementController movementController;
    private final ObjInteractionController interactionController;
    private final NetworkService networkService;

    private DatabaseManager dbManager;
    private GameSaveDAO saveDao;
    private SaveManager saveManager;
    
    private GeneratorThread generatorThread;

    public GameController(EsameGame model, GameMainFrame view) {
        super(model, view);
        this.movementController = new MovementController(model, view);
        this.interactionController = new ObjInteractionController(model, view);
        this.networkService = new NetworkService();

        try {
            this.dbManager = new DatabaseManager();
            Connection conn = dbManager.getConnection();
            this.saveDao = new GameSaveDAO(conn);
            this.saveManager = new SaveManager(this.saveDao);
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
            
            startThreads(600); // 15 min * 60 sec
            
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

    // Gestione movimento
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

    // Gestione interazione
    public String processInteraction(GameObject clickedObject) {

        if (clickedObject instanceof Zombie) {
            Zombie enemy = (Zombie) clickedObject;
            
            // MVC PURO: Chiediamo alla View di gestire la finestra di combattimento
            // e ci facciamo restituire un semplice intero che rappresenta l'esito:
            // 1 = Vittoria, 2 = Fuga, 3 = Morte/Sconfitta
            int combatResult = view.showCombatWindow(enemy, model.getPlayer(), model.getInventory());
            view.getGamePanel().updateJlHealth();

            if (combatResult == 1) {
                // 1. Lo togliamo dalla stanza corrente nella sessione attuale
                model.setAmbushActive(false);
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
                
                silentAutosave();

                // 5. FINE PARTITA - VITTORIA
                int punteggio = networkService.calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size());
                String classifica = networkService.sendAndGetLeaderboard("Matricola", punteggio);
                
                return "Hai sconfitto " + enemy.getName() + "!";
                
            } else if (combatResult == 2) {
                
                silentAutosave();
                
                return "Sei fuggito dal combattimento in preda al panico!";
                
            } else if (combatResult == 3 || model.getPlayer().getHp() <= 0) {
                // 5. FINE PARTITA - SCONFITTA (GAME OVER)
                int punteggio = networkService.calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size());
                String classifica = networkService.sendAndGetLeaderboard("Matricola_Bocciata", punteggio / 2);

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


    public void loadSavedGame() {
        boolean success = saveManager.loadGame(model);
        
        if (success) {
            view.getGamePanel().renderRoom(model.getCurrentRoom());
            
            view.getGamePanel().updateJlHealth();
            
            view.getGamePanel().animatedText("Salvataggio caricato. Bentornato nella sessione.");
        } else {
            view.getGamePanel().animatedText("Nessun salvataggio trovato o errore nel caricamento.");
        }
    }

    public void continueSavedGame() {
        try {
            model.init();
            GameDataInitializer.setUpGameData(model);
            view.showGamePanel();
            loadSavedGame(); // Innesca il caricamento e la pulizia dei nemici morti

            int savedSeconds = model.getTimeRemaining(); 
            if (savedSeconds <= 0) savedSeconds = 900; // Fallback di sicurezza

            startThreads(savedSeconds);
            
        } catch (Exception ex) {
            System.err.println("[ERRORE CRITICO] Fallimento durante il caricamento del mondo.");
            ex.printStackTrace();
        }
    }

    private void silentAutosave() {
        // 🟩 AGGIUNGI QUESTA RIGA PER DEBUG:
        System.out.println("[DEBUG] Tentativo di autosave! Vita attuale del player nel model: " + model.getPlayer().getHp());
        
        try {
            int timeToSave = (generatorThread != null) ? generatorThread.getTimeRemaining() : 900;
            model.setTimeRemaining(timeToSave);
            saveManager.saveGame(model);
        } catch (SQLException e) {
            System.err.println("Autosave fallito: " + e.getMessage());
        }
    }

    public String fetchOnlyLeaderboard() {
        return networkService.fetchOnlyLeaderboard();
    } 
    
    private void startThreads(int minutiGeneratore) {
        // Se c'erano thread vecchi, li fermiamo per sicurezza
        if (generatorThread != null) {
            generatorThread.stopTimer();
        }

        generatorThread = new it.map.graphicadventure.progettoesame.threads.GeneratorThread(minutiGeneratore, view.getGamePanel(), this);
        generatorThread.start();
        
    }
    
    public void handleGeneratorDeath() {
        // Ferma anche i mostri
        view.getGamePanel().animatedText("CLACK! Il generatore si è spento. Il buio ti avvolge... non puoi più sfuggire ai professori.");
        
        // Punteggio dimezzato per morte
        int punteggio = networkService.calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size()) / 2;
        String classifica = networkService.sendAndGetLeaderboard("Matricola_Al_Buio", punteggio);
        
        view.showLeaderboardDialog(classifica, "GAME OVER - GENERATORE ESAURITO");
        view.showMainMenu();
    }
}
