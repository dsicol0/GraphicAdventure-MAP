package it.map.graphicadventure.progettoesame.impl;

import it.map.graphicadventure.progettoesame.factory.FileMapParser;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.GameObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementazione concreta dell'avventura grafica.
 * Estende {@link GameDescription} per mantenere lo stato globale della partita in corso.
 * Gestisce i dati del giocatore, il timer del generatore e tiene traccia 
 * degli eventi persistenti (come i nemici sconfitti e le stanze sbloccate).
 *
 */
public class EsameGame extends GameDescription {

    private Player player;
    private List<GameObject> allObjects = new ArrayList<>();
    private List<String> deadZombies = new ArrayList<>();
    private List<String> unlockedRooms = new ArrayList<>();
    private int timeRemaining = 900;
    
    private boolean powerRestored = false;

    public boolean isPowerRestored() {
        return powerRestored;
    }

    public void setPowerRestored(boolean powerRestored) {
        this.powerRestored = powerRestored;
    }

    /**
     * Inizializza la partita creando il giocatore e caricando la mappa da file.
     *
     * @throws Exception Se si verifica un errore durante la lettura del file di mappa 
     * o se la stanza di partenza (ID 1) non esiste.
     */
    @Override
    public void init() throws Exception {
        
        player = new Player("Matricola Disperata");

        String pathMappa = "src/main/resources/map/map.txt";
        List<Room> stanzeCaricate = FileMapParser.loadMapFromFile(pathMappa);

        // Aggiunge tutte le stanze caricate alla lista principale ereditata dal padre
        stanzeCaricate.forEach(room -> getRooms().add(room));

        stanzeCaricate.stream()
                .filter(room -> room.getObjects() != null)
                .flatMap(room -> room.getObjects().stream())
                .forEach(obj -> allObjects.add(obj));

        // Trova la stanza iniziale (ID 1). Lancia un'eccezione se il file mappa è corrotto.
        Room stanzaIniziale = getRooms().stream()
                .filter(room -> room.getId() == 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore critico: Aula Studio (ID 1) non trovata nel file di configurazione!"));
        
        setCurrentRoom(stanzaIniziale);

        // Blocca preventivamente la porta dell'Aula 2 (ID 2)
        getRooms().stream()
                .filter(room -> room.getId() == 2)
                .findFirst()
                .ifPresent(room -> room.setLocked(true));
    }

    /**
     * Restituisce l'entità del giocatore.
     * @return L'oggetto Player.
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Restituisce una lista piatta contenente tutti gli oggetti presenti nel gioco.
     * @return La lista completa dei GameObject.
     */
    public List<GameObject> getAllObjects() {
        return allObjects;
    }

    /**
     * Restituisce la lista degli ID dei nemici sconfitti.
     * Usata per evitare che gli zombie ricompaiano dopo un caricamento.
     * @return La lista degli ID dei morti.
     */
    public List<String> getDeadZombies() {
        return deadZombies;
    }

    /**
     * Restituisce la lista degli ID (o nomi) delle stanze sbloccate.
     * @return La lista delle stanze aperte.
     */
    public List<String> getUnlockedRooms() {
        return unlockedRooms;
    }

    /**
     * Restituisce i secondi rimanenti prima che il generatore si spenga.
     * @return Il tempo rimanente in secondi.
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Aggiorna il tempo rimanente del generatore.
     * @param timeRemaining I nuovi secondi rimanenti.
     */
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    /**
     * Restituisce l'inventario in uso.
     * Sovrascrive il metodo di base per puntare direttamente all'inventario
     * specifico dell'oggetto {@link Player}.
     *
     * @return La lista degli oggetti attualmente posseduti dal giocatore.
     */
    @Override
    public List<GameObject> getInventory() {

        if (player != null && player.getInventory() != null) {
            return player.getInventory().getList();
        }
        
        return super.getInventory();
    }

    /**
     * Fornisce il testo narrativo iniziale mostrato all'avvio di una nuova partita.
     *
     * @return La stringa introduttiva della trama.
     */
    @Override
    public String getWelcomeMsg() {
        return "La testa ti pulsa. Ti sei addormentato sul manuale di Java.\n"
                + "Guardi l'orologio: sono le 3:00 del mattino.\n"
                + "Dal corridoio senti un lamento: 'Dov'è il tuo libretttooooo...'\n"
                + "Devi uscire di qui. Ora.\n";
    }
}
