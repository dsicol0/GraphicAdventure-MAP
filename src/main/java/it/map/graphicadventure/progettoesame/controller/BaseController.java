/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;

/**
 * Classe astratta che fa da base per i controller del gioco.
 * Mantiene i riferimenti al modello (la logica della partita) e alla vista (l'interfaccia grafica),
 * rendendoli disponibili a tutte le classi figlie per rispettare il pattern MVC.
 *
 */
public abstract class BaseController {
    
    /**
     * Riferimento al modello principale del gioco.
     */
    protected final EsameGame model;
    
    /**
     * Riferimento alla finestra principale dell'interfaccia grafica.
     */
    protected final GameMainFrame view;
    
    /**
     * Costruttore della classe base.
     *
     * @param model L'istanza della partita in corso.
     * @param view  L'interfaccia grafica principale.
     */
    public BaseController(EsameGame model, GameMainFrame view) {
        this.model = model;
        this.view = view;
    }
    
    /**
     * Stampa un messaggio direttamente nell'area di testo dell'interfaccia grafica.
     * 
     * @param messaggio Il testo da mostrare al giocatore a schermo.
     */
    protected void showMessageLog(String messaggio) {
        if (view != null && view.getGamePanel() != null) {
            view.getGamePanel().animatedText(messaggio);
        }
    }
}
