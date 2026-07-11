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
 *
 * @author antoniostilla
 */
public class NetworkService {

    public int calculateFinalScore(int minutesTaken, int itemsCollected, int zombiesDefeated) {
        int baseScore = 1000;
        int timePenalty = minutesTaken * 10;
        int itemBonus = itemsCollected * 50;
        int combatBonus = zombiesDefeated * 200;
        return Math.max(0, baseScore - timePenalty + itemBonus + combatBonus);
    }

    public String fetchOnlyLeaderboard() {
        StringBuilder leaderboard = new StringBuilder();
        try (Socket socket = new Socket("localhost", 6666); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

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

    public String sendAndGetLeaderboard(String playerName, int finalScore) {
        StringBuilder leaderboard = new StringBuilder();
        try (Socket socket = new Socket("localhost", 6666); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
             PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true)) {

            out.println("#score " + playerName + " " + finalScore);
            in.readLine();

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
