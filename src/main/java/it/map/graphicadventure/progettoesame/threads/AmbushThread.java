/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.threads;

import it.map.graphicadventure.progettoesame.controller.GameController;
import javax.swing.SwingUtilities;

/**
 *
 * @author antoniostilla
 */
public class AmbushThread extends Thread {
    
    private int idleSeconds;
    private final int ambushLimit = 5; // Secondi prima dell'attacco
    private boolean running;
    private boolean hasAttacked;
    private final GameController controller;

    public AmbushThread(GameController controller) {
        this.idleSeconds = 0;
        this.controller = controller;
        this.running = true;
        this.hasAttacked = false;
    }

    // Da chiamare ogni volta che il giocatore cambia stanza
    public void resetTimer() {
        this.idleSeconds = 0;
        this.hasAttacked = false;
    }

    public void stopAmbush() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(1000); // Conta 1 secondo
                idleSeconds++;

                if (idleSeconds >= ambushLimit && !hasAttacked) {
                    // Agguato!
                    SwingUtilities.invokeLater(() -> {
                        controller.triggerAmbush();
                    });
                    
                    // Resetta il timer per il prossimo agguato
                    idleSeconds = 0; 
                    this.hasAttacked = true;
                }
            } catch (InterruptedException e) {
                System.err.println("AmbushThread interrotto.");
                running = false;
            }
        }
    }
}
