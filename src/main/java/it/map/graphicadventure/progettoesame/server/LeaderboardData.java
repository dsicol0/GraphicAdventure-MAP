/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gestisce i dati della classifica globale sul server.
 *
 * Poiché il server gestisce richieste concorrenti da parte di più client (Thread), 
 * questa classe è progettata per essere Thread-Safe: tutti i metodi 
 * pubblici che accedono o modificano la struttura dati interna sono sincronizzati.
 *
 */
public class LeaderboardData {

    /**
     * Classe interna (Inner Class) statica e privata che rappresenta una singola 
     * voce della classifica.
     *
     * Implementa l'interfaccia {@link Comparable} definendo l'ordinamento naturale 
     * degli oggetti in base al punteggio decrescente (dal più alto al più basso).
     * Questo approccio migliora l'incapsulamento, nascondendo i dettagli 
     * implementativi del record all'esterno.
     *
     */
    private static class ScoreRecord implements Comparable<ScoreRecord> {
        String username;
        int score;

        public ScoreRecord(String username, int score) {
            this.username = username;
            this.score = score;
        }

        /**
         * Sovrascrive il metodo compareTo per stabilire l'ordinamento.
         * Invertendo l'ordine dei parametri in Integer.compare (o.score vs this.score), 
         * si ottiene un ordinamento decrescente.
         */
        @Override
        public int compareTo(ScoreRecord o) {
            return Integer.compare(o.score, this.score);
        }
    }

    // Struttura dati (Collection) che memorizza tutti i record dei punteggi
    private final List<ScoreRecord> scores = new ArrayList<>();

    /**
     * Aggiunge un nuovo punteggio alla classifica e la riordina.
     *
     * L'uso della keyword {@code synchronized} garantisce l'accesso in mutua esclusione:
     * se più thread (client) provano a inserire un record contemporaneamente, 
     * il monitor dell'oggetto farà in modo che vengano gestiti uno alla volta, 
     * evitando la corruzione della lista.
     *
     *
     * @param username Il nome (o matricola) del giocatore.
     * @param score Il punteggio finale ottenuto.
     */
    public synchronized void addScore(String username, int score) {
        scores.add(new ScoreRecord(username, score));
        // Sfrutta l'algoritmo di ordinamento della libreria standard basato sul compareTo()
        Collections.sort(scores);
    }

    /**
     * Restituisce una rappresentazione testuale formattata dei migliori punteggi.
     * Anche questo metodo è {@code synchronized} per evitare che un thread tenti 
     * di leggere la lista proprio mentre un altro thread sta aggiungendo/ordinando elementi.
     *
     * @return Una stringa contenente la Top 10 della classifica.
     */
    public synchronized String getTopLeaderboard() {
        if (scores.isEmpty()) {
            return "Nessun punteggio registrato.";
        }
        
        
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(10, scores.size()); 
        
        for (int i = 0; i < limit; i++) {
            ScoreRecord sr = scores.get(i);
            sb.append((i + 1)).append(". ").append(sr.username).append(" - ").append(sr.score).append(" pt\n");
        }
        
        return sb.toString();
    }
}