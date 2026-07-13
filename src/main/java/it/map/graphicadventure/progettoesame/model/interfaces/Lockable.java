/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

/**
 * Interfaccia che conferisce a un oggetto la proprietà di avere una serratura.
 * Gli oggetti che implementano {@code Lockable} (come casseforti o porte) 
 * possono essere bloccati e sbloccati nel corso del gioco.
 *
 */
public interface Lockable {
    
    /**
     * Verifica lo stato corrente della serratura.
     * * @return true se l'oggetto è chiuso a chiave e inaccessibile, false se è sbloccato.
     */
    boolean isLocked();
    
    /**
     * Modifica lo stato della serratura dell'oggetto.
     * * @param locked true per chiudere l'oggetto a chiave, false per sbloccarlo.
     */
    void setLocked(boolean locked);
}
