/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.UUID;

/**
 * Classe principale per l'applicazione Server della classifica globale.
 * 
 * Implementa il pattern architetturale di un Server Multi-Threaded tramite l'uso 
 * dei Socket. Invece di gestire le richieste 
 * sequenzialmente (bloccando l'attesa per gli altri client), il server delega 
 * la comunicazione di ogni singolo giocatore a un nuovo Thread dedicato, 
 * permettendo l'elaborazione concorrente.
 *
 */
public class LeaderboardServer {
    
    /**
     * Metodo main che avvia il server e resta costantemente in ascolto di nuove connessioni.
     *
     * Sfrutta un oggetto {@link ServerSocket} vincolato alla porta 6666. Il metodo 
     * {@code accept()} è per natura bloccante: l'esecuzione si mette in pausa finché 
     * un nuovo client non richiede di connettersi. Appena la connessione viene stabilita, 
     * viene generato un ID univoco universale per la sessione 
     * e viene avviato un {@code LeaderboardThread} per servire la richiesta.
     *
     * * @param args Argomenti passati da riga di comando (non utilizzati).
     * @throws IOException Se la porta 6666 è già in uso o se si verifica un errore di I/O.
     */
    public static void main(String[] args) throws IOException {
        
        // Struttura dati condivisa tra tutti i thread per memorizzare la classifica
        LeaderboardData ld = new LeaderboardData();
        
        ServerSocket s = new ServerSocket(6666);
        System.out.println("Leaderboard Server avviato: " + s);
        
        try {
            // Loop infinito per accettare continuamente le connessioni in ingresso
            while (true) {
                // Si mette in attesa di un client (metodo bloccante)
                Socket socket = s.accept();
                
                // Delega del carico di lavoro a un Thread separato (concorrenza)
                Thread t = new LeaderboardThread(socket, ld, UUID.randomUUID().toString());
                t.start();
            }
        } finally {
            // Il blocco finally garantisce il corretto rilascio delle risorse di rete
            // nel caso in cui il server venga interrotto da un'eccezione non gestita
            s.close();
        }
    }
}
