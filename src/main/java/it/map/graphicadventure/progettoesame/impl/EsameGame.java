package it.map.graphicadventure.progettoesame.impl;

import it.map.graphicadventure.progettoesame.GameDescription;
import it.map.graphicadventure.progettoesame.util.GameUtils;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class EsameGame extends GameDescription {

    // Aggiungiamo il nostro Player alla struttura del prof
    private Player player;
    private List<GameObject> allObjects = new ArrayList<>();
    private List<String> deadZombies = new ArrayList<>();
    private List<String> unlockedRooms = new ArrayList<>();
    private int timeRemaining = 900;
    private boolean ambushActive = false;

    @Override
    public void init() throws Exception {
        // 1. INIZIALIZZA IL GIOCATORE
        player = new Player("Matricola Disperata", 100);

        // 2. CARICAMENTO DINAMICO DA FILE
        String pathMappa = "src/main/resources/map/map.txt";
        List<Room> stanzeCaricate = GameUtils.loadMapFromFile(pathMappa);

        // REQUISITO LAMBDA: Usiamo il forEach per aggiungere tutte le stanze caricate
        stanzeCaricate.forEach(room -> getRooms().add(room));

        stanzeCaricate.stream()
                .filter(room -> room.getObjects() != null)
                .flatMap(room -> room.getObjects().stream())
                .forEach(obj -> allObjects.add(obj));

        // REQUISITO STREAM & PIPELINE: Cerchiamo la stanza iniziale (Aula Studio con ID 1)
        Room stanzaIniziale = getRooms().stream()
                .filter(room -> room.getId() == 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore critico: Aula Studio (ID 1) non trovata nel file di configurazione!"));

        System.out.println("[DEBUG INIT] Oggetti nell'Aula Studio: " + stanzaIniziale.getObjects().size());
        System.out.println("[DEBUG INIT] Oggetti totali registrati nel gioco: " + allObjects.size());
        // Imposta la stanza corrente
        setCurrentRoom(stanzaIniziale);

        getRooms().stream()
                .filter(room -> room.getId() == 2) // Trova l'Aula 2
                .findFirst()
                .ifPresent(room -> room.setLocked(true)); // La blocca di default a inizio storia
    }

    // Un getter per recuperare facilmente il giocatore durante la partita
    public Player getPlayer() {
        return player;
    }

    // GETTER PER L'ANAGRAFE GLOBALE
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
        // Fallback di sicurezza sulla struttura del prof
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
