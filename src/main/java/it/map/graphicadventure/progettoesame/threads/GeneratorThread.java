/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.threads;

import it.map.graphicadventure.progettoesame.controller.GameController;
import it.map.graphicadventure.progettoesame.view.GamePanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author antoniostilla
 */
public class GeneratorThread extends Thread {
    
    private int timeRemaining; // Tempo in secondi
    private boolean running;
    private final GamePanel gamePanel;
    private final GameController controller;

    public GeneratorThread(int startMinutes, GamePanel gamePanel, GameController controller) {
        this.timeRemaining = startMinutes * 60;
        this.gamePanel = gamePanel;
        this.controller = controller;
        this.running = true;
    }

    public void stopTimer() {
        this.running = false;
    }

    @Override
    public void run() {
        while (running && timeRemaining > 0) {
            try {
                // Aspetta 1 secondo (1000 millisecondi)
                Thread.sleep(1000);
                timeRemaining--;

                // Calcola minuti e secondi
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                
                // Formatta il tempo come "MM:SS" (es. 14:05)
                String timeString = String.format("%02d:%02d", minutes, seconds);

                // AGGIORNA LA GRAFICA IN MODO SICURO!
                SwingUtilities.invokeLater(() -> {
                    gamePanel.updateTimerLabel(timeString);
                });

            } catch (InterruptedException e) {
                System.err.println("GeneratorThread interrotto.");
                running = false;
            }
        }

        // Se esce dal ciclo e il tempo è 0, il generatore è morto
        if (timeRemaining <= 0 && running) {
            SwingUtilities.invokeLater(() -> {
                controller.handleGeneratorDeath();
            });
        }
    }
}
