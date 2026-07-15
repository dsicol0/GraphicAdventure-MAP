/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.model.interfaces.Usable;

/**
 * Rappresenta il Chip di Sicurezza (o badge del Direttore), ovvero l'oggetto 
 * obiettivo finale del gioco.
 * Estende {@link GameObject} e implementa le interfacce {@link Takeable} 
 * (per poter essere inserito nello zaino) e {@link Usable} (per interagire con l'ambiente).
 *
 */
public class Chip extends GameObject implements Takeable, Usable {

    private boolean takeable;

    /**
     * Costruisce il Chip di sicurezza.
     * Di default, l'oggetto viene impostato come raccoglibile.
     *
     * @param id L'identificativo univoco dell'oggetto.
     * @param name Il nome del chip.
     * @param description La descrizione testuale mostrata al giocatore.
     * @param imagePath Il percorso dell'icona da mostrare nell'interfaccia grafica.
     */
    public Chip(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        this.takeable = true;
    }

    // Implementazione di Takeable
    
    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    // Implementazione di Usable
    
    /**
     * Tenta di utilizzare il chip su un oggetto della stanza.
     * Utilizza l'identificazione dei tipi a run-time (RTTI) tramite l'operatore 
     * {@code instanceof} per verificare la compatibilità del bersaglio.
     *
     * @param target L'oggetto su cui il giocatore sta cercando di usare il chip.
     * @return {@code true} se il bersaglio è il pannello elettrico ({@link ElectricPanel}), 
     * {@code false} altrimenti (l'uso fallisce).
     */
    @Override
    public boolean use(GameObject target) {
        return target instanceof ElectricPanel; 
    }
}

