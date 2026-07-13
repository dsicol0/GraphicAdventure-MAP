/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Gestisce la comunicazione di rete lato Client verso il server della classifica.
 *
 * Questa classe implementa la componente Client in un'architettura Client-Server.
 * Si occupa di aprire un {@link Socket} verso l'indirizzo del server e di gestire lo 
 * scambio di messaggi attraverso il protocollo di rete stabilito. Sfrutta inoltre 
 * l'I/O Stream a caratteri per leggere e scrivere stringhe di testo anziché byte.
 * 
 */
public class NetworkService {

    /**
     * Calcola il punteggio finale della partita basandosi sulle prestazioni del giocatore.
     *
     * @param minutesTaken Il numero di minuti impiegati per completare o perdere il gioco.
     * @param itemsCollected Il numero di oggetti raccolti (incentiva l'esplorazione).
     * @param zombiesDefeated Il numero di nemici uccisi (incentiva il combattimento).
     * @return Il punteggio finale (garantito per non essere mai inferiore a 0 tramite Math.max).
     */
    public int calculateFinalScore(int minutesTaken, int itemsCollected, int zombiesDefeated) {
        int baseScore = 1000;
        int timePenalty = minutesTaken * 10;
        int itemBonus = itemsCollected * 50;
        int combatBonus = zombiesDefeated * 200;
        return Math.max(0, baseScore - timePenalty + itemBonus + combatBonus);
    }

    /**
     * Richiede al server l'elenco dei migliori punteggi registrati.
     *
     * Utilizza un costrutto try-with-resources per garantire l'Auto-Close
     * del Socket e degli Stream I/O.
     *
     * @return Una stringa formattata contenente la classifica, oppure un messaggio di errore.
     */
    public String fetchOnlyLeaderboard() {
        StringBuilder leaderboard = new StringBuilder();
        
        // Il try-with-resources chiude in automatico socket, in e out al termine del blocco
        try (Socket socket = new Socket("localhost", 6666); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // Invia il comando di richiesta classifica
            out.println("#top"); 
            String response = in.readLine();
            
            // Se il server conferma l'inizio dei dati, legge il flusso fino al tag di chiusura
            if ("#start_top".equals(response)) {
                String line;
                while ((line = in.readLine()) != null && !line.equals("#end_top")) {
                    leaderboard.append(line).append("\n");
                }
            }
            // Invia il comando di chiusura pulita
            out.println("#exit");
            
        } catch (Exception e) {
            return "Errore di connessione al server: " + e.getMessage();
        }
        
        return leaderboard.toString();
    }

    /**
     * Invia il punteggio appena ottenuto dal giocatore al server e, subito dopo,
     * richiede la classifica aggiornata.
     *
     * Esegue il "wrapping" dell'InputStream nativo in un {@link BufferedReader} 
     * per ottimizzare le letture di riga (readLine) sulle stringhe.
     *
     *
     * @param playerName Il nome utente da registrare.
     * @param finalScore I punti ottenuti.
     * @return La classifica globale aggiornata.
     */
    public String sendAndGetLeaderboard(String playerName, int finalScore) {
        StringBuilder leaderboard = new StringBuilder();
        
        // Apertura connessione con try-with-resources
        try (Socket socket = new Socket("localhost", 6666); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            // Invia il comando composito per registrare il punteggio
            out.println("#score " + playerName + " " + finalScore);
            
            // Legge la risposta del server (es. "#ok Punteggio registrato")
            in.readLine();

            // Richiede la classifica aggiornata
            out.println("#top");
            String response = in.readLine();
            if ("#start_top".equals(response)) {
                String line;
                while ((line = in.readLine()) != null && !line.equals("#end_top")) {
                    leaderboard.append(line).append("\n");
                }
            }
            out.println("#exit");

        } catch (Exception e) {
            return "Errore di connessione al server: " + e.getMessage();
        }
        return leaderboard.toString();
    }
}
