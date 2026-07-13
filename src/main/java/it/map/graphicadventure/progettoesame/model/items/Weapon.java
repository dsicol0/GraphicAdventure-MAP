/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.Zombie;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.model.interfaces.Usable;

/**
 * Rappresenta un'arma all'interno del gioco.
 * Estende {@link GameObject} e implementa {@link Usable} (per poter attaccare) 
 * e {@link Takeable} (per essere riposta nell'inventario).
 *
 */
public class Weapon extends GameObject implements Usable, Takeable {
    
    private final int damage;
    private boolean takeable;

    /**
     * Costruisce una nuova arma.
     * Di default, le armi appena create sono raccoglibili.
     *
     * @param id L'identificativo univoco dell'arma.
     * @param name Il nome dell'arma (es. "Tubo di ferro", "Accendino").
     * @param description La descrizione testuale mostrata al giocatore.
     * @param imagePath Il percorso dell'immagine associata.
     * @param damage I punti danno che l'arma infligge al bersaglio.
     */
    public Weapon(int id, String name, String description, String imagePath, int damage) {
        super(id, name, description, imagePath);
        this.damage = damage;
        this.takeable = true;
    }

    /**
     * Restituisce il valore di attacco dell'arma.
     * @return I punti di danno inflitti.
     */
    public int getDamage() {
        return damage;
    }

    // Metodi di Takeable
    
    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    // Metodo di Usable
    
    /**
     * Tenta di utilizzare l'arma su un oggetto specifico della scena.
     * Sfrutta l'identificazione dei tipi a run-time (RTTI) per capire la natura
     * del bersaglio.
     *
     * @param target L'oggetto o il nemico su cui il giocatore usa l'arma.
     * @return {@code true} se l'attacco va a buon fine (il bersaglio è un nemico), 
     * {@code false} se si cerca di attaccare un oggetto inanimato.
     */
    @Override
    public boolean use(GameObject target) {
        
        // Se il bersaglio è un nemico, esegue l'attacco
        if (target instanceof Zombie enemy) {
            
            System.out.println("Hai attaccato " + enemy.getName() + " infliggendo " + this.damage + " danni!");
            return true;
            
        } else if (target instanceof Chest) {
            
            
            System.out.println("Hai colpito la cassa, ma è troppo resistente.");
            return false;
        }
        
        
        System.out.println("Non puoi attaccare questo oggetto.");
        return false;
    }
}
