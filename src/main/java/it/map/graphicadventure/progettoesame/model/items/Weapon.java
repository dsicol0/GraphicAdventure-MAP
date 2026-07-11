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
 *
 * @author David
 */
public class Weapon extends GameObject implements Usable, Takeable {
    
    private final int damage;
    private boolean takeable;

    public Weapon(int id, String name, String description, String imagePath, int damage) {
        super(id, name, description, imagePath);
        this.damage = damage;
        this.takeable = true;
    }

    public int getDamage() {
        return damage;
    }

    // metodi di Takeable
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
