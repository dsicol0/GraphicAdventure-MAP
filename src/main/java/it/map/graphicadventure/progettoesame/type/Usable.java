/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type;

/**
 *
 * @author David
 */
public interface Usable {
    /* 
        Il metodo restituisce 'true' se l'uso ha avuto successo (es. la chiave ha aperto la porta),
        'false' se non è successo nulla (es. hai usato la chiave su un muro).
    */ 
    boolean use(GameObject target);
}
