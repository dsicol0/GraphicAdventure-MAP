package it.map.graphicadventure.progettoesame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    
    private final int id;
    private String name;
    private String description;
    private String backgroundPath; 
    private boolean locked;

    // Mappa delle uscite: la chiave è la direzione ("nord"), il valore è la Stanza di destinazione
    private final Map<String, Room> exits = new HashMap<>();

    // Lista degli oggetti presenti nella stanza
    private final List<GameObject> objects = new ArrayList<>();

   
    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.backgroundPath = "";
    }

    
    public Room(int id, String name, String description, String backgroundPath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.backgroundPath = backgroundPath;
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    
    public String getBackgroundPath() { 
        return backgroundPath; 
    }
    
    public void setBackgroundPath(String backgroundPath) { 
        this.backgroundPath = backgroundPath; 
    }

    
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    
    public List<GameObject> getObjects() {
        return objects;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    public GameObject findObjectByName(String name) {
        return objects.stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Restituisce un Set con i nomi di tutte le direzioni disponibili da questa stanza.
     */
    public java.util.Set<String> getAvailableDirections() {
        return this.exits.keySet();
    }
    
    public boolean isLocked() {
        return this.locked;
    }
    
    public void setLocked(boolean locked) {
        this.locked = locked;
    }

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
        return java.util.Objects.hash(id);
    }
}
