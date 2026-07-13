/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

/**
 * Interfaccia che definisce il comportamento degli oggetti che possono essere aperti e chiusi.
 * Le classi che implementano {@code Openable} (come contenitori, zaini o bauli) 
 * permettono al giocatore di ispezionarne il contenuto o interagirvi.
 *
 */
public interface Openable {
    
    /**
     * Cambia lo stato dell'oggetto rendendolo aperto.
     */
    void open();
    
    /**
     * Cambia lo stato dell'oggetto rendendolo chiuso.
     */
    void close();
    
    /**
     * Verifica lo stato corrente dell'oggetto.
     * * @return true se l'oggetto è attualmente aperto, false se è chiuso.
     */
    boolean isOpen();

}