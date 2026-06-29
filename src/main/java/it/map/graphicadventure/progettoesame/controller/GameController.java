/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.MapBuilder;
import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.Player;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.type.interfaces.Openable;
import it.map.graphicadventure.progettoesame.type.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.type.interfaces.Usable;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
import java.util.List;

/**
 *
 * @author David
 */
public class GameController {
    private final EsameGame model;
    private final GameMainFrame view;
    private Player player;

    // Potremmo passare anche un riferimento alla View (es. GameFrame)
    // se il controller deve dirgli di aggiornare l'interfaccia o mostrare un popup.

    public GameController(EsameGame model, GameMainFrame view) {
        this.model = model;
        this.view = view;
    }
    
    /**
     * Restituisce la stanza attualmente salvata nel Model.
     * Serve alla View per sapere cosa disegnare dopo un movimento.
     */
    public Room getCurrentRoom() {
        return model.getCurrentRoom();
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    public void startNewGame() {
        try {
            // 1. Chiediamo al MODEL di preparare tutto (leggere il txt, mettere gli oggetti, ecc.)
            model.init(); 

            // 2. Recuperiamo la stanza iniziale che il Model ha appena preparato
            Room initialRoom = model.getCurrentRoom();
            
            System.out.println("[DEBUG] Stanza iniziale: " + initialRoom.getName() + " | Oggetti dentro: " + initialRoom.getObjects().size());
            
            if (initialRoom != null) {
                // 3. Mostra il pannello di gioco sul Frame
                view.showGamePanel();

                // 4. PASSA LA STANZA ALLA GRAFICA PER DISEGNARLA!
                view.getGamePanel().renderRoom(initialRoom);
            } else {
                System.err.println("Errore: la stanza iniziale dal Model è null!");
            }
        } catch (Exception ex) {
            System.err.println("Errore durante l'inizializzazione del gioco: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Gestisce il movimento del giocatore tra le stanze.
     * @param direction "nord", "sud", "est", "ovest"
     * @return un messaggio di testo con l'esito del movimento (es. per aggiornare la UI)
     */
    public String handleMovement(String direction) {
        Room current = model.getCurrentRoom();
        Room nextRoom = current.getExit(direction);

        if (nextRoom != null) {
            model.setCurrentRoom(nextRoom);
            return "Ti sposti verso " + direction.toUpperCase() + "...\nSei in: " + nextRoom.getName();
        } else {
            return "Non puoi andare in quella direzione.";
        }
    }

    /**
     * Gestisce il clic su un oggetto all'interno della stanza.
     * Viene chiamato dal RoomPanel quando la Hitbox intercetta il mouse.
     */
    public String handleObjectInteraction(GameObject clickedObject) {
        if (clickedObject == null) {
            return "Non c'è niente di interessante qui.";
        }

        StringBuilder response = new StringBuilder();
        response.append("Esamini: ").append(clickedObject.getName()).append(". \n");

        // 1. Controllo se l'oggetto si può raccogliere
        if (clickedObject instanceof Takeable) {
            if (((Takeable) clickedObject).isTakeable()) {
                model.getInventory().add(clickedObject);
                model.getCurrentRoom().removeObject(clickedObject);
                response.append("Hai raccolto ").append(clickedObject.getName()).append("!");
                return response.toString(); // Termina qui, l'oggetto è nell'inventario
            }
        }

        // 2. Controllo se l'oggetto si può aprire (es. Chest/Baule)
        if (clickedObject instanceof Openable) {
            Openable openableObj = (Openable) clickedObject;
            if (openableObj.isOpen()) {
                response.append("È già aperto.");
            } else if (openableObj.isLocked()) {
                response.append("È chiuso a chiave. Serve qualcosa per aprirlo.");
            } else {
                openableObj.setOpen(true);
                response.append("Lo hai aperto!");
                // Qui potresti estrarre gli oggetti dal contenitore e metterli nella stanza
            }
        }

        // 3. Controllo se l'oggetto è usabile sul posto (es. Lettore Badge)
        if (clickedObject instanceof Usable) {
            response.append("\nPremi per usare l'oggetto... (logica da implementare)");
        }

        // Se non è raccoglibile, né apribile, restituiamo solo la sua descrizione base
        if (response.toString().equals("Esamini: " + clickedObject.getName() + ". \n")) {
            response.append(clickedObject.getDescription());
        }

        return response.toString();
    }
    
    /**
     * Recupera l'inventario dal Model e lo formatta come testo per la View.
     */
    public String showInventory() {
        // Recuperiamo la lista degli oggetti dal Model (EsameGame)
        java.util.List<GameObject> inventory = model.getInventory();
        
        // Se è vuoto, diamo un feedback immediato
        if (inventory == null || inventory.isEmpty()) {
            return "Il tuo zaino è vuoto.";
        }
        
        // Altrimenti, costruiamo una stringa con l'elenco degli oggetti
        StringBuilder sb = new StringBuilder();
        sb.append("--- INVENTARIO ---\n");
        
        for (GameObject obj : inventory) {
            sb.append("> ").append(obj.getName()).append("\n");
        }
        
        return sb.toString();
    }
    
    /**
     * Apre l'interfaccia dell'inventario.
     */
    public void handleInventoryToggle() {
        List<GameObject> inventoryItems = model.getInventory();
        view.getGamePanel().toggleInventory(inventoryItems);
    }
}
