/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;

/**
 *
 * @author David
 */
public abstract class BaseController {
    protected final EsameGame model;
    protected final GameMainFrame view;
    
    public BaseController(EsameGame model, GameMainFrame view) {
        this.model = model;
        this.view = view;
    }
    
    // Serve quando il controller deve far apparire un testo nella JTextArea all'improvviso, 
    // senza aspettare che un metodo arrivi al suo return
    protected void showMessageLog(String messaggio) {
        if (view != null && view.getGamePanel() != null) {
            // Mandiamo il testo direttamente sul pannello di gioco grafico!
            view.getGamePanel().animatedText(messaggio);
        }
    }
}
