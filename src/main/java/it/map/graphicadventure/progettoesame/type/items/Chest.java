/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type.items;

import it.map.graphicadventure.progettoesame.type.items.Key;

/**
 *
 * @author David
 */
public class Chest extends ObjectContainer {
    
    private boolean locked;
    private final int requiredKeyId;
    
    public Chest(int id, String name, String description, String imagePath, int requiredKeyId) {
        super(id, name, description, imagePath);
        
        this.locked = true; // La cassa nasce chiusa a chiave
        this.requiredKeyId = requiredKeyId;
    }
    
    public boolean isLocked() {
        return locked;
    }
    
    // Il metodo per tentare di sbloccarla passando un oggetto Key!
    public boolean unlock(Key keyUsed) {
        if (keyUsed != null && keyUsed.getId() == this.requiredKeyId) {
            this.locked = false; // Serratura scattata
            return true;
        }
        return false; // Chiave sbagliata
    }
    
    @Override
    public void open() {
        if (!locked) {
            super.open(); // Chiama l'open() del padre
        } else {
            // Se è chiusa, non si apre! Sarà la GUI a stampare il messaggio di errore.
        }
    }
    
    
}
