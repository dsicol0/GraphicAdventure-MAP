package it.map.graphicadventure.progettoesame.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Room {
    
    private final int id;
    private String name;
    private String description;
    private String imagePath;

    // Mappa delle uscite: la chiave è la direzione ("nord"), il valore è la Stanza di destinazione
    private final Map<String,Room> exits = new HashMap<>();

    // Lista degli oggetti presenti nella stanza
    private final List<GameObject> objects = new ArrayList<>();

    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    // Utile se la descrizione cambia (es. "La stanza è buia" -> "La stanza è illuminata")
    public void setDescription(String description) { this.description = description; }

    // Metodi per gestire le uscite
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    // Ritorna la mappa intera: utilissimo per controllare quante uscite ci sono
    // o per stampare a video "Puoi andare a: nord, ovest".
    public Map<String, Room> getExits() {
        return exits;
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

    public GameObject findObjectByName(String name) {
        return objects.stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
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
        // Genera un hash basato sull'ID.
        return java.util.Objects.hash(id);
    }
}
