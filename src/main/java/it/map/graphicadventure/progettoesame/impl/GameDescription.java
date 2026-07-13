package it.map.graphicadventure.progettoesame.impl;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe astratta che definisce la struttura di base.
 * Fornisce le variabili di stato fondamentali (stanze, inventario base, stanza corrente)
 * e dichiara i metodi astratti che ogni gioco specifico dovrà implementare.
 *
 */
public abstract class GameDescription {

    private final List<Room> rooms = new ArrayList<>();

    private final List<GameObject> inventory = new ArrayList<>();

    private Room currentRoom;

    /**
     * Restituisce la lista completa di tutte le stanze presenti nel gioco.
     *
     * @return La lista degli oggetti Room.
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * Restituisce la stanza in cui si trova attualmente il giocatore.
     *
     * @return La stanza corrente.
     */
    public Room getCurrentRoom() {
        return currentRoom;
    }

    /**
     * Imposta la stanza attuale del giocatore.
     * Viene usato tipicamente durante i movimenti o i caricamenti di un salvataggio.
     *
     * @param currentRoom La nuova stanza in cui spostare il giocatore.
     */
    public void setCurrentRoom(Room currentRoom) {
        this.currentRoom = currentRoom;
    }

    /**
     * Restituisce l'inventario globale del gioco.
     *
     * @return La lista degli oggetti posseduti.
     */
    public List<GameObject> getInventory() {
        return inventory;
    }

    /**
     * Metodo astratto che gestisce la logica di preparazione della partita.
     * Le sottoclassi dovranno implementarlo per caricare le mappe, posizionare 
     * il giocatore e inizializzare i dati prima di iniziare a giocare.
     *
     * @throws Exception Se si verifica un problema durante il caricamento (es. file non trovato).
     */
    public abstract void init() throws Exception;

    /**
     * Metodo astratto che definisce il messaggio introduttivo del gioco.
     *
     * @return La stringa contenente il prologo o il messaggio di benvenuto.
     */
    public abstract String getWelcomeMsg();

}