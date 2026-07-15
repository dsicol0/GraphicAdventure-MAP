/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model;


import it.map.graphicadventure.progettoesame.service.GameSaveDAO;
import java.util.List;

/**
 * Rappresenta un'istantanea (snapshot) dello stato attuale della partita.
 *
 * Questa classe agisce strutturalmente come un DTO (Data Transfer Object).
 * Il suo scopo è raccogliere le informazioni vitali del gioco (salute, stanza, oggetti, timer)
 * in un unico contenitore, disaccoppiando il motore logico complesso ({@code EsameGame}) 
 * dal livello di persistenza (il Database). In questo modo il DAO (Data Access Object) 
 * lavora solo con dati primitivi e collezioni base, senza dipendere direttamente dagli 
 * oggetti di business complessi.
 *
 */
public class SaveData {

    private final String currentRoom;
    private final int health;
    private final List<String> itemIds;
    private final List<String> killedEnemyIds;
    private final List<String> unlockedRoomIds;
    private int timeRemaining;
    private boolean powerRestored;
    
    private final List<GameSaveDAO.ObjectSave> objectStates;

    /**
     * Costruisce un nuovo oggetto contenente i dati di salvataggio.
     * I dati passati rappresentano lo stato esatto del giocatore e dell'ambiente 
     * in un preciso istante di tempo.
     *
     * @param currentRoom Il nome (o ID testuale) della stanza in cui si trova il giocatore.
     * @param health I punti vita attuali del giocatore.
     * @param itemIds La lista degli identificativi degli oggetti presenti nell'inventario.
     * @param killedEnemyIds La lista degli ID dei nemici che sono già stati sconfitti.
     * @param unlockedRoomIds La lista delle stanze precedentemente chiuse e ora accessibili.
     * @param timeRemaining Il tempo rimanente (in secondi) prima dello spegnimento del generatore.
     */
    public SaveData(String currentRoom, int health, List<String> itemIds, List<String> killedEnemyIds, List<String> unlockedRoomIds, int timeRemaining, boolean powerRestored, List<GameSaveDAO.ObjectSave> objectStates) {
        this.currentRoom = currentRoom;
        this.health = health;
        this.itemIds = itemIds;
        this.killedEnemyIds = killedEnemyIds;
        this.unlockedRoomIds = unlockedRoomIds;
        this.timeRemaining = timeRemaining;
        this.powerRestored = powerRestored;
        this.objectStates = objectStates;
    }

    /**
     * Restituisce l'identificativo della stanza salvata.
     * @return Il nome della stanza.
     */
    public String getRoomName() {
        return currentRoom;
    }

    /**
     * Restituisce i punti vita salvati del giocatore.
     * @return L'ammontare degli HP.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Restituisce la lista degli ID degli oggetti nell'inventario.
     * @return La lista di identificativi sotto forma di stringhe.
     */
    public List<String> getItemIds() {
        return itemIds;
    }

    /**
     * Restituisce la lista degli ID dei nemici sconfitti.
     * @return La lista degli ID per non far "respawnare" gli zombie uccisi.
     */
    public List<String> getKilledEnemyIds() {
        return killedEnemyIds;
    }

    /**
     * Restituisce la lista degli ID delle stanze sbloccate.
     * @return Le porte che non necessitano più di chiavi.
     */
    public List<String> getUnlockedRoomIds() {
        return unlockedRoomIds;
    }

    /**
     * Restituisce il tempo rimasto al momento del salvataggio.
     * @return I secondi mancanti al Game Over.
     */
    public int getTimeRemaining() {
        return timeRemaining;
    }

    /**
     * Aggiorna il tempo rimanente nel salvataggio.
     * Utilizzato spesso per gli autosalvataggi silenziosi che avvengono periodicamente.
     * @param timeRemaining Il nuovo valore in secondi da salvare.
     */
    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }
    
    public boolean isPowerRestored() { return powerRestored; }
    
    public List<GameSaveDAO.ObjectSave> getObjectStates() { return objectStates; }
}
