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
import it.map.graphicadventure.progettoesame.model.items.Food;
import it.map.graphicadventure.progettoesame.service.NetworkService;
import it.map.graphicadventure.progettoesame.service.SaveManager;
import it.map.graphicadventure.progettoesame.threads.GeneratorThread;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Controller principale del gioco.
 * Agisce come un orchestratore (Facade) che coordina la comunicazione tra
 * la vista grafica, il modello logico e i servizi esterni (Database e Rete).
 * Smista le azioni utente ai sotto-controller specifici per mantenere il codice modulare.
 *
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

    /**
     * Costruttore del GameController.
     * Inizializza i controller delegati, i servizi di rete e tenta di 
     * instaurare una connessione al database locale per i salvataggi.
     *
     * @param model L'istanza principale della partita in corso.
     * @param view  L'interfaccia grafica del gioco.
     */
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

    /**
     * Avvia una nuova partita.
     * Inizializza la mappa tramite i factory, posiziona il giocatore nella stanza
     * di partenza, registra l'evento nel log del database e fa partire il thread del timer.
     */
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
            
            startThreads(600); // 10 min * 60 sec
            
        } catch (Exception ex) {
        }
    }

    /**
     * Restituisce la stanza in cui si trova attualmente il giocatore.
     * @return L'oggetto Room corrente.
     */
    public Room getCurrentRoom() {
        return model.getCurrentRoom();
    }

    /**
     * Restituisce l'entità del giocatore attuale.
     * @return L'oggetto Player.
     */
    public Player getPlayer() {
        return model.getPlayer();
    }

    /**
     * Gestisce la richiesta di spostamento del giocatore in una specifica direzione.
     * Delega l'esecuzione della logica al {@link MovementController} ed effettua 
     * un salvataggio automatico se lo spostamento va a buon fine.
     *
     * @param direction La direzione verso cui spostarsi (es. "nord", "sud").
     * @return Il messaggio risultante dall'azione da mostrare a schermo.
     */
    public String handleMovement(String direction) {
        
        // Controlloiamo se andiamo a EST, dal corridoio principale, e se la corrente è attiva abbiamo vinto
        if (direction.equalsIgnoreCase("EST") && model.getCurrentRoom().getId() == 1 && model.isPowerRestored()) {
            
            // Ferma il thread del conto alla rovescia
            if (generatorThread != null) {
                generatorThread.stopTimer();
            }
            
            // Avvia la grafica di chiusura
            view.getGamePanel().showEndingSequence();
            
            // Invia i dati in classifica (Punteggio raddoppiato per la vittoria)
            int punteggio = networkService.calculateFinalScore(model.getTimeRemaining() / 60, model.getInventory().size(), model.getDeadZombies().size()) * 2;
            networkService.sendAndGetLeaderboard("Sopravvissuto", punteggio);

            return "";
        }

        
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

    /**
     * Elabora il click su un oggetto della scena.
     * Se l'oggetto è un nemico, avvia il flusso di combattimento e gestisce 
     * le condizioni di vittoria/sconfitta interrogando il server. 
     * Altrimenti delega l'elaborazione dell'oggetto al controller delle interazioni.
     *
     * @param clickedObject L'oggetto con cui il giocatore sta tentando di interagire.
     * @return L'esito testuale dell'interazione da stampare nella UI.
     */
    public String processInteraction(GameObject clickedObject) {

        if (clickedObject instanceof Zombie enemy) {
            
            // 1 = Vittoria, 2 = Fuga, 3 = Morte/Sconfitta
            int combatResult = view.showCombatWindow(enemy, model.getPlayer(), model.getInventory());
            view.getGamePanel().updateJlHealth();

            if (combatResult == 1) {
                model.getCurrentRoom().getObjects().remove(enemy);
                
                if (!model.getDeadZombies().contains(String.valueOf(enemy.getId()))) {
                    model.getDeadZombies().add(String.valueOf(enemy.getId()));
                }
                
                try {
                    saveDao.logEvent("KILLED", "Il giocatore ha sconfitto lo zombie: " + enemy.getName());
                } catch (java.sql.SQLException e) {
                    System.err.println("Errore nel salvataggio dell'uccisione nel DB: " + e.getMessage());
                }
                
                silentAutosave();
                
                return "Hai sconfitto " + enemy.getName() + "!";
                
            } else if (combatResult == 2) {
                
                silentAutosave();
                
                return "Sei fuggito dal combattimento in preda al panico!";
                
            } else if (combatResult == 3 || model.getPlayer().getHp() <= 0) {
                int punteggio = networkService.calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size());

                view.showMainMenu();
                
                return "Sei morto... Ricarica un salvataggio dal menù principale.";
            }
            return "Combattimento interrotto.";
        }
        
        boolean zombieAlive = model.getCurrentRoom().getObjects().stream()
                .anyMatch(obj -> obj instanceof Zombie);
        
        if (zombieAlive) {
            return "Non puoi prendere o usare " + clickedObject.getName() + " adesso!\nIl professore infetto ti sbarra la strada. Devi prima affrontarlo!";
        }

        
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

    /**
     * Genera una stringa riassuntiva del contenuto dell'inventario del giocatore.
     * @return Una stringa formattata con la lista degli oggetti raccolti.
     */
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

    /**
     * Chiede alla vista di mostrare o nascondere la schermata grafica dell'inventario.
     */
    public void handleInventoryToggle() {
        List<GameObject> inventoryItems = model.getInventory();
        view.getGamePanel().toggleInventory(inventoryItems);
    }
    
    /**
     * Utilizza un oggetto consumabile (es. cibo o medikit) presente nell'inventario 
     * per ripristinare i punti vita del giocatore e lo rimuove dalla borsa.
     *
     * @param consumable L'oggetto consumabile da utilizzare.
     * @return Il messaggio di feedback con la quantità di vita curata.
     */
    public String handleInventoryItemUsage(Food consumable) {

        consumable.heal(model.getPlayer());
        
        GameObject item = (GameObject) consumable;
        model.getInventory().remove(item);
        
        return "Consumi " + item.getName() + ".\n"
                + "Hai recuperato " + consumable.getHealAmount()+ "HP! TOT HP: " + model.getPlayer().getHp() + "/100";
    }

    /**
     * Interroga il manager dei salvataggi per caricare l'ultimo stato della partita 
     * dal database e aggiorna i pannelli dell'interfaccia utente.
     */
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

    /**
     * Riprende una partita precedentemente salvata.
     * Inizializza un mondo di gioco vuoto, vi sovrascrive i dati recuperati 
     * dal database e ripristina il conto alla rovescia del generatore di corrente.
     */
    public void continueSavedGame() {
        try {
            model.init();
            GameDataInitializer.setUpGameData(model);
            view.showGamePanel();
            loadSavedGame();

            int savedSeconds = model.getTimeRemaining(); 
            if (savedSeconds <= 0) savedSeconds = 600; 

            startThreads(savedSeconds);
            
        } catch (Exception ex) {
            System.err.println("Fallimento durante il caricamento del mondo.");
        }
    }

    /**
     * Esegue un salvataggio dello stato del gioco in background senza 
     * interrompere il giocatore, sincronizzando anche il tempo rimanente.
     */
    private void silentAutosave() {
        
        try {
            int timeToSave = (generatorThread != null) ? generatorThread.getTimeRemaining() : 900;
            model.setTimeRemaining(timeToSave);
            saveManager.saveGame(model);
        } catch (SQLException e) {
            System.err.println("Autosave fallito: " + e.getMessage());
        }
    }

    /**
     * Richiede l'attuale classifica dei giocatori tramite il servizio di rete.
     * @return La stringa testuale con i punteggi della leaderboard.
     */
    public String fetchOnlyLeaderboard() {
        return networkService.fetchOnlyLeaderboard();
    } 
    
    /**
     * Inizializza e avvia il thread responsabile del conto alla rovescia del 
     * tempo a disposizione del giocatore. Se ne esiste già uno in esecuzione, lo interrompe.
     *
     * @param minutiGeneratore I secondi iniziali da impostare nel timer.
     */
    private void startThreads(int minutiGeneratore) {
        if (generatorThread != null) {
            generatorThread.stopTimer();
        }

        generatorThread = new GeneratorThread(minutiGeneratore, view.getGamePanel(), this);
        generatorThread.start();
        
    }
    
    /**
     * Metodo richiamato dal thread del generatore quando il tempo scade.
     * Mostra il messaggio di sconfitta, penalizza il punteggio e manda la richiesta 
     * al server di rete, riportando poi il giocatore al menù principale.
     */
    public void handleGeneratorDeath() {
        view.getGamePanel().animatedText("CLACK! Il generatore si è spento. Il buio ti avvolge... non puoi più sfuggire ai professori.");
        
        int punteggio = networkService.calculateFinalScore(15, model.getInventory().size(), model.getDeadZombies().size()) / 2;
        String classifica = networkService.sendAndGetLeaderboard("Matricola_Al_Buio", punteggio);
        
        view.showLeaderboardDialog(classifica, "GAME OVER - GENERATORE ESAURITO");
        view.showMainMenu();
    }
}
