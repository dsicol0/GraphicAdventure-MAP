package it.map.graphicadventure.progettoesame.impl;

import it.map.graphicadventure.progettoesame.GameDescription;
import it.map.graphicadventure.progettoesame.factory.FileMapParser;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.GameObject;
import java.util.ArrayList;
import java.util.List;

public class EsameGame extends GameDescription {

    private Player player;
    private List<GameObject> allObjects = new ArrayList<>();
    private List<String> deadZombies = new ArrayList<>();
    private List<String> unlockedRooms = new ArrayList<>();
    private int timeRemaining = 900;
    private boolean ambushActive = false;

    @Override
    public void init() throws Exception {
        
        player = new Player("Matricola Disperata");

        
        String pathMappa = "src/main/resources/map/map.txt";
        List<Room> stanzeCaricate = FileMapParser.loadMapFromFile(pathMappa);

        
        stanzeCaricate.forEach(room -> getRooms().add(room));

        stanzeCaricate.stream()
                .filter(room -> room.getObjects() != null)
                .flatMap(room -> room.getObjects().stream())
                .forEach(obj -> allObjects.add(obj));

        
        Room stanzaIniziale = getRooms().stream()
                .filter(room -> room.getId() == 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore critico: Aula Studio (ID 1) non trovata nel file di configurazione!"));
        
        setCurrentRoom(stanzaIniziale);

        getRooms().stream()
                .filter(room -> room.getId() == 2)
                .findFirst()
                .ifPresent(room -> room.setLocked(true));
    }

    
    public Player getPlayer() {
        return player;
    }

    
    public List<GameObject> getAllObjects() {
        return allObjects;
    }

    public List<String> getDeadZombies() {
        return deadZombies;
    }

    public List<String> getUnlockedRooms() {
        return unlockedRooms;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public boolean isAmbushActive() {
        return ambushActive;
    }

    public void setAmbushActive(boolean ambushActive) {
        this.ambushActive = ambushActive;
    }

    @Override
    public List<GameObject> getInventory() {

        if (player != null && player.getInventory() != null) {
            return player.getInventory().getList();
        }
        
        return super.getInventory();
    }

    @Override
    public String getWelcomeMsg() {
        return "La testa ti pulsa. Ti sei addormentato sul manuale di Java.\n"
                + "Guardi l'orologio: sono le 3:00 del mattino.\n"
                + "Dal corridoio senti un lamento: 'Dov'è il tuo libretttooooo...'\n"
                + "Devi uscire di qui. Ora.\n";
    }
}
