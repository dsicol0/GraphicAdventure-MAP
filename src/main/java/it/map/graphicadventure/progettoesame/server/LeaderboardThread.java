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
 *
 * @author antoniostilla
 */
 public class LeaderboardThread extends Thread {

    private final Socket socket;
    private boolean run = true;
    private final LeaderboardData ld;
    private PrintWriter out = null;

    public LeaderboardThread(Socket socket, LeaderboardData ld, String name) {
        this.socket = socket;
        this.ld = ld;
        this.setName(name);
    }

    @Override
    public void run() {
        try {
            System.out.println("Connessione classifica accettata: " + socket);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            
            while (run) {
                String str = in.readLine();
                if (str != null) {
                    str = str.trim();
                    // Utilizzo esatto della regex del professore
                    Pattern pattern = Pattern.compile("\\S+");
                    Matcher matcher = pattern.matcher(str);
                    boolean findcmd = matcher.find();
                    
                    if (findcmd && matcher.group().equalsIgnoreCase("#score")) {
                        // Mi aspetto: #score Username 1500
                        String name = null;
                        String scoreStr = null;
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
                        
                    } else if (findcmd && matcher.group().equalsIgnoreCase("#top")) {
                        // Restituisce la classifica formattata
                        out.println("#start_top");
                        out.println(ld.getTopLeaderboard());
                        out.println("#end_top");
                        
                    } else if (findcmd && matcher.group().equalsIgnoreCase("#exit")) {
                        run = false;
                    } else {
                        out.println("#error Comando sconosciuto");
                    }
                }
            }
        } catch (IOException ex) {
            System.err.println(ex);
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }
    }
}