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
import it.map.graphicadventure.progettoesame.type.Player;
import java.util.List;

/**
 *
 * @author David
 */
public class GameController extends BaseController {
    // Riferimenti ai due sotto-controller
    private final MovementController movementController;
    private final ObjInteractionController interactionController;

    public GameController(EsameGame model, GameMainFrame view) {
        super(model, view);
        // Li inizializziamo passandogli il modello e la view
        this.movementController = new MovementController(model, view);
        this.interactionController = new ObjInteractionController(model, view);
    }

    public void startNewGame() {
        try {
            model.init();
            
            GameDataInitializer.setUpGameData(model);
            
            Room initialRoom = model.getCurrentRoom();
            if (initialRoom != null) {
                view.showGamePanel();
                view.getGamePanel().renderRoom(initialRoom);
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
        return movementController.handleMovement(direction);
    }

    // 🟩 Passa l'azione direttamente al controller delle interazioni
    public String handleObjectInteraction(GameObject clickedObject) {
        return interactionController.handleObjectInteraction(clickedObject);
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
}
