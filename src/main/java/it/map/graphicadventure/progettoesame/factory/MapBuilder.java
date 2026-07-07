/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.factory;

/**
 *
 * @author David
 */

import it.map.graphicadventure.progettoesame.util.GameUtils;
import it.map.graphicadventure.progettoesame.model.Room;
import java.util.List;

public class MapBuilder {

    public Room buildWorld() {
        
        try {
            // Leggiamo la mappa dal file esterno usando la tua GameUtils!
            List<Room> stanze = GameUtils.loadMapFromFile("src/main/resources/map/map.txt");
            
            // Assumiamo che la prima stanza della lista (indice 0) sia quella iniziale
            return stanze.get(0); 
            
        } catch (Exception e) {
            System.out.println("Errore fatale nel caricamento della mappa: " + e.getMessage());
            return null;
        }
    }
}