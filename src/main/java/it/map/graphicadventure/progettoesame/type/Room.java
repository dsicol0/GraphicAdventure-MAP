package it.map.graphicadventure.progettoesame.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    private String imagePath;
    
    private final int id;
    private String name;
    private String description;

    // Mappa delle uscite: es. "nord" -> Stanza numero 2
    private final Map<String,Room> exits = new HashMap<>();

    // Lista degli oggetti presenti nella stanza
    private final List<GameObject> objects = new ArrayList<>();

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    // Metodi per gestire le uscite
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    // Metodi per gestire gli oggetti nella stanza
    public List<GameObject> getObjects() {
        return objects;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    
    @Override
    public boolean equals(Object o) {
        if(o instanceof Room) {
            return (((Room)o).getId() == this.getId());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        // Genera un hash basato sull'ID.
        return java.util.Objects.hash(id);
    }
}
