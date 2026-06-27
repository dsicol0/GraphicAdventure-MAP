/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.type.Room;
import javax.swing.Timer;

/**
 *
 * @author David
 */
public class GamePanel extends javax.swing.JPanel {
    
    private Timer timerText;

    /**
     * Creates new form GamePanel
     */
    public GamePanel() {
        initComponents();
    }
    
    private void scriviTestoAnimato(String testo) {
        // 1. Se c'è un'altra frase che sta ancora scorrendo, la fermiamo
        if (timerText != null && timerText.isRunning()) {
            timerText.stop();
        }

        // 2. Puliamo la text area e mettiamo il cursore iniziale per la nuova stanza
        jtaDialogs.setText("> ");

        // 3. Usiamo un array come "contatore" per ricordarci a quale lettera siamo
        int[] indice = {0};

        // 4. Creiamo il Timer (35 millisecondi per lettera)
        timerText = new javax.swing.Timer(35, e -> {
            // Finché ci sono lettere da scrivere...
            if (indice[0] < testo.length()) {
                // Aggiungiamo una singola lettera alla tua jtaDialogs reale
                jtaDialogs.append(String.valueOf(testo.charAt(indice[0])));
                indice[0]++;
            } else {
                // Frase finita, fermiamo il Timer
                timerText.stop();
            }
        });

        // 5. Facciamo partire l'animazione!
        timerText.start();
    }
    
    public void renderRoom(Room room) {
        // 1. Aggiorna il testo della descrizione
 
        scriviTestoAnimato(room.getDescription());;

        // 2. Aggiorna l'immagine di sfondo
        String imagePath = room.getBackgroundPath();

        if (imagePath != null && !imagePath.trim().isEmpty()) {
            java.net.URL imgURL = getClass().getResource(imagePath);

            if (imgURL != null) {
                // Calcoliamo le dimensioni del pannello reale
                int larghezza = jpPlayingArea.getWidth() > 0 ? jpPlayingArea.getWidth() : 800;
                int altezza = jpPlayingArea.getHeight() > 0 ? jpPlayingArea.getHeight() : 450;
                jlBackground.setBounds(0, 0, larghezza, altezza);
                
                // Carica l'icona originale
                javax.swing.ImageIcon originalImage = new javax.swing.ImageIcon(imgURL);
                
                java.awt.Image scaledImage = originalImage.getImage().getScaledInstance(larghezza, altezza, java.awt.Image.SCALE_SMOOTH);

                // Imposta la NUOVA immagine scalata sulla label
                jlBackground.setIcon(new javax.swing.ImageIcon(scaledImage));
                // =======================================================================

            } else {
                System.err.println("Risorsa non trovata: " + imagePath);
                jlBackground.setIcon(null);
            }
        } else {
            jlBackground.setIcon(null);
        }
        
        jlBackground.removeAll();
        jlBackground.setLayout(null);
       
        if (room.getObjects() != null) {
            for (it.map.graphicadventure.progettoesame.type.GameObject obj : room.getObjects()) {
                
                System.out.println("[DEBUG] Disegno oggetto: " + obj.getName() + " in X:" + obj.getX() + " Y:" + obj.getY());
                
                // Crea una nuova Label per l'oggetto
                javax.swing.JLabel objectLabel = new javax.swing.JLabel();
                
                // Carichiamo l'icona dell'oggetto (es. la png della cassa)
                if (obj.getImagePath() != null && !obj.getImagePath().isEmpty()) {
                    java.net.URL objUrl = getClass().getResource(obj.getImagePath());
                    if (objUrl != null) {
                        javax.swing.ImageIcon objIcon = new javax.swing.ImageIcon(objUrl);
                        java.awt.Image objScaled = objIcon.getImage().getScaledInstance(obj.getWidth(), obj.getHeight(), java.awt.Image.SCALE_SMOOTH);
                        objectLabel.setIcon(new javax.swing.ImageIcon(objScaled));
                    }
                }
                
                // Posizioniamo l'oggetto esattamente dove hai deciso
                objectLabel.setBounds(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                objectLabel.setBorder(javax.swing.BorderFactory.createLineBorder(java.awt.Color.RED, 2));
                
                
                // Facciamo comparire la "manina" quando ci passi sopra
                objectLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
                
                // AGGIUNGIAMO IL LISTENER PER IL CLICK!
                objectLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        // Qui passerai l'oggetto cliccato al GameController!
                        // Esempio: String risposta = controller.handleObjectInteraction(obj);
                        // jtaDialogs.setText(risposta);
                        System.out.println("Hai cliccato su: " + obj.getName());
                    }
                });
                
                // Aggiungiamo l'oggetto DENTRO l'immagine di sfondo
                jlBackground.add(objectLabel);
            }
        }

        // 3. Forza il ridisegno
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
        this.revalidate();
        this.repaint();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jtaDialogs = new javax.swing.JTextArea();
        jpPlayingArea = new javax.swing.JPanel();
        jlBackground = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(850, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(new java.awt.BorderLayout());

        jtaDialogs.setEditable(false);
        jtaDialogs.setBackground(new java.awt.Color(0, 0, 0));
        jtaDialogs.setColumns(20);
        jtaDialogs.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtaDialogs.setForeground(new java.awt.Color(255, 255, 255));
        jtaDialogs.setLineWrap(true);
        jtaDialogs.setRows(6);
        jtaDialogs.setWrapStyleWord(true);
        jtaDialogs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        jScrollPane1.setViewportView(jtaDialogs);

        add(jScrollPane1, java.awt.BorderLayout.PAGE_START);

        javax.swing.GroupLayout jpPlayingAreaLayout = new javax.swing.GroupLayout(jpPlayingArea);
        jpPlayingArea.setLayout(jpPlayingAreaLayout);
        jpPlayingAreaLayout.setHorizontalGroup(
            jpPlayingAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        jpPlayingAreaLayout.setVerticalGroup(
            jpPlayingAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
        );

        add(jpPlayingArea, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlBackground;
    private javax.swing.JPanel jpPlayingArea;
    private javax.swing.JTextArea jtaDialogs;
    // End of variables declaration//GEN-END:variables
}
