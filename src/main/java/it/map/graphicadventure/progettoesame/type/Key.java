/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type;

/**
 *
 * @author David
 */
public class Key extends GameObject implements Takeable, Usable {

    public Key(int id, String name, String description, String imagePath) {

        super(id, name, description, imagePath);
    }
    
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
