/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameNPC;
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

    // Usiamo l'UNICO metodo dell'interfaccia di Usable
    @Override
    public boolean use(GameObject target) {
        
        // Controlliamo se l'oggetto su cui stiamo usando l'arma è un NPC!
        if (target instanceof GameNPC enemy) {
            
            // Immaginiamo che il nemico abbia un metodo per subire danni
            System.out.println("Hai attaccato " + enemy.getName() + " infliggendo " + this.damage + " danni!");
            // nemico.takeDamage(this.damage); 
            
            return true; // Attacco riuscito!
            
        } else if (target instanceof Chest) {
            // Ehi, potremmo anche permettere di sfondare una cassa invece di usare la chiave!
            System.out.println("Hai colpito la cassa, ma è troppo resistente.");
            return false;
        }
        
        // Se usi l'arma su un muro o un oggetto a caso
        System.out.println("Non puoi attaccare questo oggetto.");
        return false;
    }
}
