/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

/**
 *
 * @author David
 */
public interface Takeable {
    
    /* 
        Serve esclusivamente per "mettere un'etichetta" su una classe, per dire 
        al compilatore che questa classe fa parte della categoria delle 
        COSE RACCOGLIBILI.
    */

    /**
     * @return true se l'oggetto può essere raccolto in questo momento.
     */
    boolean isTakeable();

    /**
     * Imposta lo stato dell'oggetto.
     * @param takeable true per renderlo raccoglibile, false per bloccarlo.
     */
    void setTakeable(boolean takeable);
}
