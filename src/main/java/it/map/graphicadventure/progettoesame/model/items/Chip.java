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
 * @author antoniostilla
 */

/**
 * Il badge del Direttore. Oggetto obiettivo finale del gioco.
 * Una volta raccolto, permette di sbloccare la saracinesca di uscita.
 */
public class Chip extends GameObject implements Takeable, Usable {

    private boolean takeable;

    public Chip(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        this.takeable = true;
    }

    // --- Implementazione di Takeable ---
    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    // --- Implementazione di Usable ---
    @Override
    public boolean use(GameObject target) {
        // Controllo polimorfico pulito: l'oggetto su cui lo usi è il pannello?
        if (target instanceof ElectricPanel) {
            return true; // Uso riuscito! Il controller intercetterà questo true e decreterà la vittoria
        }
        return false; 
    }
}

