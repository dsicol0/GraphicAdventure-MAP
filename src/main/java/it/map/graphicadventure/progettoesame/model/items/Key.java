/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.model.interfaces.Usable;

/**
 * Rappresenta una chiave all'interno del gioco.
 * Estende {@link GameObject} per essere posizionata nella mappa e implementa
 * {@link Takeable} per poter essere raccolta nell'inventario e {@link Usable} 
 * per interagire con altri oggetti (nello specifico, per aprire i forzieri).
 *
 */
public class Key extends GameObject implements Takeable, Usable {

    private boolean takeable;

    /**
     * Costruisce una nuova chiave.
     * Di default, l'oggetto appena creato viene impostato come raccoglibile.
     *
     * @param id L'identificativo univoco della chiave.
     * @param name Il nome della chiave (es. "Chiave d'oro").
     * @param description La descrizione mostrata esaminando la chiave.
     * @param imagePath Il percorso dell'immagine associata.
     */
    public Key(int id, String name, String description, String imagePath) {

        super(id, name, description, imagePath);

        this.takeable = true;
    }

    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    /**
     * Tenta di usare la chiave su un altro oggetto del gioco.
     * Utilizza l'operatore {@code instanceof} (RTTI) per 
     * verificare a run-time se il bersaglio è effettivamente un forziere ({@link Chest}).
     * In caso affermativo, effettua un cast (downcasting) e prova a sbloccarlo passando
     * se stessa al forziere.
     *
     * @param target L'oggetto su cui il giocatore sta provando ad usare la chiave.
     * @return {@code true} se la chiave ha aperto con successo il bersaglio, 
     * {@code false} se la chiave è sbagliata o se il bersaglio non è un forziere.
     */
    @Override
    public boolean use(GameObject target) {
        if (target instanceof Chest chest) {
            
            // Proviamo a sbloccarla passando questa stessa chiave (this)
            if (chest.unlock(this)) {
                System.out.println("Hai sbloccato la " + chest.getName() + "!");
                return true;
            } else {
                System.out.println("Questa chiave non va bene per questa serratura.");
                return false;
            }
        }
        
        System.out.println("Non puoi usare la chiave qui.");
        return false;
    }
    
    
}
