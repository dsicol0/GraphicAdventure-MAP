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
 *
 * @author antoniostilla
 */
public class LeaderboardServer {
    public static void main(String[] args) throws IOException {
        LeaderboardData ld = new LeaderboardData();
        ServerSocket s = new ServerSocket(6666);
        System.out.println("Leaderboard Server avviato: " + s);
        try {
            while (true) {
                Socket socket = s.accept();
                Thread t = new LeaderboardThread(socket, ld, UUID.randomUUID().toString());
                t.start();
            }
        } finally {
            s.close();
        }
    }
}
