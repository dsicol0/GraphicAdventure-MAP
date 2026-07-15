/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

/**
 * Interfaccia che identifica gli oggetti di gioco raccoglibili.
 * Funge da "etichetta" per il compilatore e per i controller: se una classe 
 * implementa {@code Takeable} (come una Chiave o un'Arma), significa che 
 * il giocatore può prenderla e metterla nel proprio inventario.
 *
 */
public interface Takeable {
    
    /**
     * Verifica se l'oggetto può essere raccolto in questo preciso momento.
     * Utile per oggetti che magari diventano raccoglibili solo dopo un certo evento.
     * @return true se l'oggetto può essere preso, false altrimenti.
     */
    boolean isTakeable();

    /**
     * Modifica lo stato dell'oggetto, rendendolo raccoglibile o bloccandolo.
     * @param takeable true per permettere al giocatore di raccoglierlo, false per impedirglielo.
     */
    void setTakeable(boolean takeable);
}
