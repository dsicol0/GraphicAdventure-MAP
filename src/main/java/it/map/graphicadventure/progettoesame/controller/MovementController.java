/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.GameUtils;
import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.view.ConfirmDialog;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;

/**
 *
 * @author David
 */
public class MovementController extends BaseController {

    public MovementController(EsameGame model, GameMainFrame view) {
        super(model, view);
    }
    
    public String handleMovement(String direction) {
        Room nextRoom = model.getCurrentRoom().getExit(direction);

        if (nextRoom != null) {

            // CASO 1: La stanza è chiusa a chiave
            if (nextRoom.isLocked()) {

                // Controlliamo se è l'Aula 2 (ID 2) e se il giocatore ha la chiave (ID 35)
                if (nextRoom.getId() == 2 && GameUtils.hasObject(model.getInventory(), 8)) {
                    
                    ConfirmDialog cd = new ConfirmDialog(view, true, "Hai la Chiave dell'Aula 2. Vuoi usarla per aprire la porta?");
                    cd.setVisible(true);

                    if (cd.isConfirmed()) {
                        nextRoom.setLocked(false);

                        // Consumiamo la chiave dall'inventario
                        model.getInventory().removeIf(obj -> obj.getId() == 8);

                        // Spostiamo il giocatore
                        model.setCurrentRoom(nextRoom);
                        
                        // Stampiamo l'uso della chiave E POI la descrizione della stanza!
                        return "> Hai usato Chiave dell'Aula 2.\nSenti lo scatto della serratura e la porta si spalanca!";
                    } else {
                        return "> Decidi di conservare la chiave. La porta dell'Aula 2 resta sbarrata.";
                    }
                }

                // Fallback se non ha la chiave
                return "> La porta che conduce a " + nextRoom.getName() + " è serrata dall'interno. Ti serve la chiave corretta.";
            }

            // 🚪 CASO 2: La stanza è aperta, ci spostiamo normalmente
            model.setCurrentRoom(nextRoom);
            return null;
        }

        // CASO 3: Muro
        return "> Non puoi andare in quella direzione.";
    }
}
