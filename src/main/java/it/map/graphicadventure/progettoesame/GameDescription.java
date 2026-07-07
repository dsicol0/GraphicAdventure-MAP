package it.map.graphicadventure.progettoesame;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;

import java.util.ArrayList;
import java.util.List;

public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();

    private final List<GameObject> inventory = new ArrayList<>();

    private Room currentRoom;

    public List<Room> getRooms() {
        return rooms;
    }

    public Room getCurrentRoom() {
        return currentRoom;
    }

    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    public List<GameObject> getInventory() {
        return inventory;
    }

    public abstract void init() throws Exception;

    //public abstract void nextMove(ParserOutput p, PrintStream out);

    public abstract String getWelcomeMsg();

}
