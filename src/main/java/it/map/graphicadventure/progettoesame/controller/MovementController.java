/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.view.ConfirmDialog;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;

/**
 *
 * @author David
 */
public class MovementController extends BaseController {
    
    public static final int ID_AULA_2 = 2;
    public static final int ID_CHIAVE_AULA_2 = 13;
    

    public MovementController(EsameGame model, GameMainFrame view) {
        super(model, view);
    }
    
    public String handleMovement(String direction) {
        Room nextRoom = model.getCurrentRoom().getExit(direction);

        if (nextRoom != null) {
            
            if (model.getUnlockedRooms().contains(String.valueOf(nextRoom.getId())) || 
                model.getUnlockedRooms().contains(nextRoom.getName())) {
                nextRoom.setLocked(false);
            }   
            
            // Caso di una stanza chiusa a chiave
            if (nextRoom.isLocked()) {

                // Controlliamo se è l'Aula 2 (ID 2) e se il giocatore ha la chiave (ID 13)
                if (nextRoom.getId() == ID_AULA_2 && model.getPlayer().hasObject(ID_CHIAVE_AULA_2)) {

                    ConfirmDialog cd = new ConfirmDialog(view, true, "Hai la Chiave dell'Aula 2. Vuoi usarla per aprire la porta?");
                    cd.setVisible(true);

                    if (cd.isConfirmed()) {
                        model.getUnlockedRooms().add(String.valueOf(nextRoom.getId()));
                        
                        model.getUnlockedRooms().add(String.valueOf(nextRoom.getId()));
                        model.getInventory().removeIf(obj -> obj.getId() == ID_CHIAVE_AULA_2);
                        model.setCurrentRoom(nextRoom);
                        
                        return "> Hai usato Chiave dell'Aula 2.\nSenti lo scatto della serratura e la porta si spalanca!";
                    } else {
                        return "> Decidi di conservare la chiave. La porta dell'Aula 2 resta sbarrata.";
                    }
                }
                
                return "> La porta che conduce a " + nextRoom.getName() + " è serrata dall'interno. Ti serve la chiave corretta.";
            }

            // La stanza è aperta, ci spostiamo normalmente
            model.setCurrentRoom(nextRoom);
            return null;
        }

        // Muro
        return "> Non puoi andare in quella direzione.";
    }
}
