/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.threads;

import it.map.graphicadventure.progettoesame.controller.GameController;
import it.map.graphicadventure.progettoesame.view.GamePanel;
import javax.swing.SwingUtilities;

/**
 * Thread responsabile della gestione del timer globale di gioco (il Generatore).
 *
 * Sfruttando i concetti della Programmazione Concorrente (estensione della classe 
 * {@link Thread}), questa classe permette di eseguire un conto alla rovescia in parallelo 
 * senza bloccare l'esecuzione del thread principale del gioco. Mantiene un riferimento 
 * diretto al controller e all'interfaccia grafica per poterne effettuare gli aggiornamenti 
 * e le condizioni di sconfitta.
 *
 */
public class GeneratorThread extends Thread {
    
    private int timeRemaining; // Tempo in secondi
    private boolean running;
    private final GamePanel gamePanel;
    private final GameController controller;

    /**
     * Costruisce il thread del timer.
     *
     * @param startMinutes Il tempo di partenz
     * a disposizione del giocatore.
     * @param gamePanel Il pannello grafico (View) che contiene la JLabel del timer da aggiornare.
     * @param controller Il controller logico a cui notificare l'eventuale fine del tempo.
     */
    public GeneratorThread(int startMinutes, GamePanel gamePanel, GameController controller) {
        this.timeRemaining = startMinutes;
        this.gamePanel = gamePanel;
        this.controller = controller;
        this.running = true;
    }
    
    public int getTimeRemaining() {
        return this.timeRemaining;
    }

    /**
     * Interrompe in modo pulito il ciclo vitale del thread.
     * Modificando la flag {@code running}
     */
    public void stopTimer() {
        this.running = false;
    }

    /**
     * Codice eseguito concorrentemente all'avvio del thread (tramite il metodo {@code start()}).
     *
     * Utilizza Thread.sleep() per sospendere l'esecuzione per un secondo esatto.
     * Essendo un'applicazione Swing, l'aggiornamento dei componenti grafici non è 
     * Thread-Safe se fatto al di fuori dell'Event Dispatch Thread (EDT). Per ovviare al problema, 
     * viene utilizzato {@link SwingUtilities#invokeLater(Runnable)} in combinazione con una 
     * Espressione Lambda, garantendo un aggiornamento della UI totalmente sicura.
     * 
     */
    @Override
    public void run() {
        while (running && timeRemaining > 0) {
            try {
                // Mette in pausa il thread per 1000 millisecondi (1 secondo)
                Thread.sleep(1000);
                timeRemaining--;

                // Calcolo della formattazione standard minuti:secondi
                int minutes = timeRemaining / 60;
                int seconds = timeRemaining % 60;
                
                String timeString = String.format("%02d:%02d", minutes, seconds);

                // Passa il compito di aggiornare la JLabel grafica al Thread di Swing (EDT)
                SwingUtilities.invokeLater(() -> {
                    gamePanel.updateTimerLabel(timeString);
                });

            } catch (InterruptedException e) {
                System.err.println("GeneratorThread interrotto.");
                running = false;
            }
        }

        // Se il tempo è scaduto (e il timer non era stato fermato esplicitamente per una vittoria),
        // notifica al controller la morte per spegnimento del generatore
        if (timeRemaining <= 0 && running) {
            SwingUtilities.invokeLater(() -> {
                controller.handleGeneratorDeath();
            });
        }
    }
}
