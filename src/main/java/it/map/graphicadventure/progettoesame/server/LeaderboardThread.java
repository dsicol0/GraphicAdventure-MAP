/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Gestisce la comunicazione autonoma con un singolo client connesso al server.
 * 
 * Estendendo la classe {@link Thread}, permette al server di essere Multi-Threaded 
 * e di servire richieste concorrenti. Interpreta i messaggi in ingresso tramite 
 * I/O Stream e utilizza le Espressioni Regolari (Regex) per fare 
 * il parsing del protocollo testuale di comunicazione (es. comandi #score, #top).
 *
 */
 public class LeaderboardThread extends Thread {

    private final Socket socket;
    private boolean run = true;
    private final LeaderboardData ld;
    private PrintWriter out = null;

    /**
     * Costruisce il thread dedicato alla singola connessione.
     *
     * @param socket Il socket accettato dal ServerSocket principale.
     * @param ld Il riferimento all'oggetto condiviso (e sincronizzato) che contiene la classifica.
     * @param name L'identificativo assegnato a questo specifico Thread.
     */
    public LeaderboardThread(Socket socket, LeaderboardData ld, String name) {
        this.socket = socket;
        this.ld = ld;
        this.setName(name);
    }

    /**
     * Ciclo di vita del thread. 
     * Inizializza i flussi di Input/Output e cicla continuamente in ascolto di messaggi 
     * finché il client non invia il comando di chiusura.
     * Garantisce una gestione robusta degli errori rilasciando sempre le risorse 
     * di rete all'interno del blocco {@code finally}.
     */
    @Override
    public void run() {
        try {
            System.out.println("Connessione classifica accettata: " + socket);
            
            // Wrapping del flusso di byte in flusso di caratteri bufferizzato per lettura/scrittura ottimizzata
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            
            while (run) {
                String str = in.readLine();
                if (str != null) {
                    str = str.trim();
                    
                    
                    Pattern pattern = Pattern.compile("\\S+");
                    Matcher matcher = pattern.matcher(str);
                    boolean findcmd = matcher.find();
                    
                    // Comando per registrare un nuovo punteggio
                    if (findcmd && matcher.group().equalsIgnoreCase("#score")) {
                        
                        String name = null;
                        String scoreStr = null;
                        
                        // Estrazione iterativa dei gruppi (nome e punteggio)
                        if (matcher.find()) {
                            name = matcher.group();
                            if (matcher.find()) {
                                scoreStr = matcher.group();
                            }
                        }
                        
                        if (name != null && scoreStr != null) {
                            try {
                                int score = Integer.parseInt(scoreStr);
                                ld.addScore(name, score);
                                out.println("#ok Punteggio registrato");
                            } catch (NumberFormatException ex) {
                                out.println("#error Formato punteggio non valido");
                            }
                        } else {
                             out.println("#error Parametri mancanti");
                        }
                        
                    // Comando per richiedere la lettura della classifica
                    } else if (findcmd && matcher.group().equalsIgnoreCase("#top")) {
                        
                        out.println("#start_top");
                        out.println(ld.getTopLeaderboard());
                        out.println("#end_top");
                        
                    // Comando di terminazione connessione
                    } else if (findcmd && matcher.group().equalsIgnoreCase("#exit")) {
                        run = false;
                    } else {
                        out.println("#error Comando sconosciuto");
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println("Errore di I/O nel Thread: " + ex);
        } finally {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                System.err.println("Errore durante la chiusura del socket: " + ex);
            }
        }
    }
}