package it.map.graphicadventure.progettoesame;

import it.map.graphicadventure.progettoesame.type.GameObject;

import java.util.List;

public class GameUtils {

    public static GameObject getObjectFromInventory(List<GameObject> inventory, int id) {
        for (GameObject o : inventory) {
            if (o.getId() == id) {
                return o;
            }
        }
        return null;
    }

}