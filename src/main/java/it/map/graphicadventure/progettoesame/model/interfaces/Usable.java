/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

import it.map.graphicadventure.progettoesame.model.GameObject;

/**
 * Interfaccia che definisce il comportamento degli oggetti utilizzabili in modo attivo.
 * Le classi che implementano {@code Usable} (come Chiavi, Armi o Chip) possono 
 * interagire con altri oggetti specifici presenti nell'ambiente di gioco.
 *
 */
public interface Usable {
    
    /**
     * Applica l'effetto dell'oggetto corrente su un bersaglio (target).
     * La logica specifica di interazione viene definita dalla classe concreta 
     * (es. infliggere danni se l'oggetto è un'arma, oppure sbloccare una serratura 
     * se l'oggetto è una chiave).
     *
     * @param target L'oggetto del mondo di gioco su cui si tenta di usare questo strumento.
     * @return {@code true} se l'interazione ha avuto successo (es. la chiave ha aperto la porta), 
     * {@code false} se non è successo nulla (es. si è provato a usare la chiave su un muro).
     */
    boolean use(GameObject target);
}
