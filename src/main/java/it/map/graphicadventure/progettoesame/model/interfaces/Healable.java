/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

import it.map.graphicadventure.progettoesame.model.Player;

/**
 * Interfaccia che definisce il comportamento degli oggetti curativi.
 * Qualsiasi classe che implementa questa interfaccia (come ad esempio il Cibo) 
 * possiede la capacità di ripristinare i punti vita del giocatore.
 *
 */
public interface Healable {
    
    /**
     * Applica l'effetto curativo al giocatore.
     * Incrementa i punti vita attuali senza superare il limite massimo consentito.
     * * @param player Il giocatore a cui ripristinare la salute.
     */
    void heal(Player player);
    
    /**
     * Restituisce la quantità di punti vita che questo oggetto è in grado di curare.
     * * @return Il valore numerico dei punti vita (HP) ripristinati.
     */
    int getHealAmount();
}