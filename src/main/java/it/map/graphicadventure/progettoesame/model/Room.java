package it.map.graphicadventure.progettoesame.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Rappresenta un singolo ambiente (stanza) all'interno della mappa di gioco.
 *
 * Dal punto di vista architetturale, l'intera mappa può essere vista come un grafo, 
 * in cui ogni {@code Room} è un nodo e le uscite (exits) sono gli archi diretti che 
 * collegano le stanze. 
 * Fa uso delle Collections: una {@link List} per gestire 
 * gli oggetti contenuti nella stanza e una {@link Map} per la gestione efficiente 
 * delle uscite.
 *
 */
public class Room {
    
    private final int id;
    private String name;
    private String description;
    private String backgroundPath; 
    private boolean locked;

    /**
     * Mappa delle uscite. 
     * Associa una direzione cardinale (chiave di tipo String, es. "nord") alla 
     * corrispondente stanza di destinazione (valore di tipo Room). 
     */
    private final Map<String, Room> exits = new HashMap<>();

    /**
     * Lista dinamica che tiene traccia di tutti i {@link GameObject} presenti 
     * fisicamente in questa stanza (lasciati a terra o depositati dal giocatore).
     */
    private final List<GameObject> objects = new ArrayList<>();

   
    /**
     * Costruisce una nuova stanza senza specificare un'immagine di sfondo.
     *
     * @param id L'identificativo numerico univoco della stanza.
     * @param name Il nome della stanza (es. "Aula 1").
     * @param description La descrizione narrativa dell'ambiente.
     */
    public Room(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.backgroundPath = "";
    }

    /**
     * Costruisce una nuova stanza specificando anche l'immagine di sfondo per la GUI.
     *
     * @param id L'identificativo numerico univoco della stanza.
     * @param name Il nome della stanza.
     * @param description La descrizione narrativa dell'ambiente.
     * @param backgroundPath Il percorso del file immagine da caricare come sfondo.
     */
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

    /**
     * Crea un collegamento (uscita) da questa stanza verso un'altra.
     * I nomi delle direzioni vengono salvati in minuscolo per evitare errori di 
     * digitazione durante la ricerca.
     *
     * @param direction La stringa che rappresenta la direzione (es. "nord", "sud").
     * @param room L'oggetto Room di destinazione.
     */
    public void setExit(String direction, Room room) {
        exits.put(direction.toLowerCase(), room);
    }

    /**
     * Recupera la stanza adiacente situata nella direzione specificata.
     *
     * @param direction La direzione verso cui il giocatore vuole spostarsi.
     * @return La {@code Room} di destinazione, oppure {@code null} se non c'è nessuna porta.
     */
    public Room getExit(String direction) {
        return exits.get(direction.toLowerCase());
    }

    public Map<String, Room> getExits() {
        return exits;
    }

    /**
     * Restituisce la lista degli oggetti presenti nella stanza.
     * @return La {@code List<GameObject>} degli oggetti.
     */
    public List<GameObject> getObjects() {
        return objects;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    /**
     * Cerca un oggetto all'interno della stanza a partire dal suo nome.
     * Filtrare in modo dichiarativo gli elementi della collezione ignorando le differenze tra maiuscole e minuscole.
     *
     * @param name Il nome esatto dell'oggetto da cercare.
     * @return L'istanza del {@code GameObject} trovato, oppure {@code null} se non esiste.
     */
    public GameObject findObjectByName(String name) {
        return objects.stream()
                .filter(obj -> obj.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * Restituisce un Set contenente i nomi di tutte le direzioni d'uscita disponibili.
     * Sfrutta il metodo {@code keySet()} della mappa per ottenere i riferimenti.
     *
     * @return Un {@code Set<String>} con le chiavi (direzioni).
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

    /**
     * Confronta questa stanza con un altro oggetto per determinarne l'uguaglianza.
     * Il confronto valuta in primis l'istanza e successivamente l'ID univoco.
     *
     * @param o L'oggetto da confrontare.
     * @return {@code true} se le stanze hanno lo stesso ID, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof Room) {
            return (((Room)o).getId() == this.getId());
        } else {
            return false;
        }
    }
    
    /**
     * Calcola l'hash code per questa stanza, basandosi sul suo ID univoco.
     * Garantisce il corretto posizionamento di questo oggetto all'interno di Hash Table.
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
