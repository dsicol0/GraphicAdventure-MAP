/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Lockable;
import it.map.graphicadventure.progettoesame.model.items.Key;

/**
 *
 * @author David
 */
public class Chest<T extends GameObject> extends ObjectContainer<T> implements Lockable {
    
    private boolean locked;
    private final int requiredKeyId;
    
    public Chest(int id, String name, String description, String imagePath, int requiredKeyId) {
       
        super(id, name, description, imagePath);
        
        this.locked = true; 
        this.requiredKeyId = requiredKeyId;
    }

    public int getRequiredKeyId() {
        return requiredKeyId;
    }
    
    @Override
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    
    public boolean unlock(Key keyUsed) {
        if (keyUsed != null && keyUsed.getId() == this.requiredKeyId) {
            this.locked = false;
            return true;
        }
        return false;
    }
    
    
    @Override
    public void open() {
        if (!locked) {
            super.open(); // Chiama l'open() del padre (ObjectContainer), che imposta open = true
        } else {
            // Se è chiusa a chiave non fa nulla. Sarà il controller a stampare il messaggio di errore.
        }
    }
}