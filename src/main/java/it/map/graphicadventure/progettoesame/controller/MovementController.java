/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.type.Room;
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
        Room current = model.getCurrentRoom();
        Room nextRoom = current.getExit(direction);

        if (nextRoom != null) {
            model.setCurrentRoom(nextRoom);
            if (nextRoom.isLocked()) {
                return "Questa stanza è bloccata. Forse ti serve una chiave per aprirla..."; 
            }
            return "Ti sposti verso " + direction.toUpperCase() + "...\nSei in: " + nextRoom.getName();
        } else {
            return "Non puoi andare in quella direzione.";
        }
    }
}
