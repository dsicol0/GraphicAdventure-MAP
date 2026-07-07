/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author antoniostilla
 */
public class LeaderboardData {

    // Una semplice classe interna per tenere nome e punteggio
    private static class ScoreRecord implements Comparable<ScoreRecord> {
        String username;
        int score;

        public ScoreRecord(String username, int score) {
            this.username = username;
            this.score = score;
        }

        @Override
        public int compareTo(ScoreRecord o) {
            return Integer.compare(o.score, this.score); // Ordine decrescente
        }
    }

    private final List<ScoreRecord> scores = new ArrayList<>();

    // Metodo synchronized come fa il prof
    public synchronized void addScore(String username, int score) {
        scores.add(new ScoreRecord(username, score));
        Collections.sort(scores); // Tiene la classifica sempre ordinata
    }

    public synchronized String getTopLeaderboard() {
        if (scores.isEmpty()) {
            return "Nessun punteggio registrato.";
        }
        StringBuilder sb = new StringBuilder();
        int limit = Math.min(10, scores.size()); // Prende la Top 10
        for (int i = 0; i < limit; i++) {
            ScoreRecord sr = scores.get(i);
            sb.append((i + 1)).append(". ").append(sr.username).append(" - ").append(sr.score).append(" pt\n");
        }
        return sb.toString();
    }
}
