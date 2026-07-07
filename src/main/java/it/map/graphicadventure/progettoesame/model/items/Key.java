/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.model.interfaces.Usable;

/**
 *
 * @author David
 */
public class Key extends GameObject implements Takeable, Usable {

    private boolean takeable;

    public Key(int id, String name, String description, String imagePath) {

        super(id, name, description, imagePath);

        this.takeable = true;
    }

    // metodo di Takeable
    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    // metodo di Usable
    @Override
    public boolean use(GameObject target) {
        // Se proviamo a usare la chiave su una Cassa...
        if (target instanceof Chest) {
            Chest chest = (Chest) target;
            
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
