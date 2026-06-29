/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.controller.GameController;
import it.map.graphicadventure.progettoesame.type.Room;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author David
 */
public class GamePanel extends javax.swing.JPanel {
    
    private Timer timerText;  
    private GameController controller;
    
    private Room currentRenderedRoom = null;
    
    private JPanel jpInventoryView = null;
    private boolean isInventoryVisible = false;
    
    public void setController(GameController controller) {
        this.controller = controller;
    }

    /**
     * Creates new form GamePanel
     */
    public GamePanel() {
        initComponents();
        
        // 1. Stacchiamo i controlli dal layout standard a blocchi
        this.remove(jpControls);
        
        // 2. Rendiamo lo sfondo dell'HUD invisibile (si vedranno solo i bottoni!)
        jbInventory.setOpaque(true);
        jbInventory.setContentAreaFilled(true);
        jbInventory.setBackground(new java.awt.Color(10, 10, 10));
        jbInventory.setForeground(new java.awt.Color(50, 255, 50));
        jbInventory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 255, 50), 2));
        jbInventory.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16));
        jbInventory.setFocusPainted(false);
        jbInventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
    }
    
    private void animatedText(String testo) {
        // 1. Se c'è un'altra frase che sta ancora scorrendo, la fermiamo
        if (timerText != null && timerText.isRunning()) {
            timerText.stop();
        }

        // 2. Puliamo la text area e mettiamo il cursore iniziale per la nuova stanza
        jtaDialogs.setText("> ");

        // 3. Usiamo un array come "contatore" per ricordarci a quale lettera siamo
        int[] index = {0};

        // 4. Creiamo il Timer
        timerText = new javax.swing.Timer(20, e -> {
            // Finché ci sono lettere da scrivere...
            if (index[0] < testo.length()) {
                // Aggiungiamo una singola lettera alla tua jtaDialogs reale
                jtaDialogs.append(String.valueOf(testo.charAt(index[0])));
                index[0]++;
            } else {
                // Frase finita, fermiamo il Timer
                timerText.stop();
            }
        });

        // 5. Facciamo partire l'animazione!
        timerText.start();
    }
    
    public void renderRoom(Room room) {
        // 1. Aggiorna il testo della descrizione solo se la stanza è diversa dalla precdenete.
        
        if (isInventoryVisible) {
            toggleInventory(null);
        }
        
        if (this.currentRenderedRoom != room) {
            animatedText(room.getDescription());
            this.currentRenderedRoom = room;
        }

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
                        
                        if (controller != null) {
                            // 1. Chiediamo al controller l'esito dell'interazione
                            String r = controller.handleObjectInteraction(obj);
                            
                            // 2. Stampiamo la risposta nel terminale verde (con l'animazione hacker!)
                            animatedText(r);
                            
                            // 3. Ridisegniamo la stanza. 
                            // Questo è un trucco magico: se l'oggetto è stato raccolto, 
                            // il controller lo ha rimosso dalla stanza. Ridisegnando, l'oggetto scomparirà dallo schermo!
                            renderRoom(room);
                        }
                    }
                });
                
                // Aggiungiamo l'oggetto DENTRO l'immagine di sfondo
                jlBackground.add(objectLabel);
            }
        }
        
        // 1. Svuotiamo il pannello dalle vecchie uscite della stanza precedente
        jpExits.removeAll();
        
        // Impostiamo una griglia 3x3 rigida, con 5 pixel di spazio tra un bottone e l'altro
        jpExits.setLayout(new java.awt.GridLayout(3, 3, 2, 2));
        
        jpExits.setPreferredSize(new java.awt.Dimension(110, 110));
        
        java.util.Set<String> directions = new java.util.HashSet<>();
        if (room.getAvailableDirections() != null) {
            for (String dir : room.getAvailableDirections()) {
                if (dir != null) {
                    directions.add(dir.trim().toUpperCase());
                }
            }
        }
        
        // Generiamo SEMPRE i 4 bottoni direzionali, ma controlliamo se sono abilitati
        for (int i = 0; i < 9; i++) {
            if (i == 1) {
                // NORD
                boolean isOpen = directions.contains("NORD");
                jpExits.add(directionButton("NORD", "▲", isOpen));
            } 
            else if (i == 3) {
                // OVEST
                boolean isOpen = directions.contains("OVEST");
                jpExits.add(directionButton("OVEST", "◄", isOpen));
            } 
            else if (i == 5) {
                // EST
                boolean isOpen = directions.contains("EST");
                jpExits.add(directionButton("EST", "►", isOpen));
            } 
            else if (i == 7) {
                // SUD
                boolean isOpen = directions.contains("SUD");
                jpExits.add(directionButton("SUD", "▼", isOpen));
            } 
            else {
                // Cella vuota per formare la croce
                jpExits.add(new javax.swing.JLabel());
            }
        }
        
        // Aggiungiamo l'HUD galleggiante in modalità Absolute sopra lo sfondo
        // ==========================================
        // HUD GALLEGGIANTE (Posizionamento e Dimensioni)
        // ==========================================
        
        // Assicuriamoci che anche il pannello del D-Pad sia trasparente
       jpControls.setOpaque(false);
        jpControls.setBackground(new java.awt.Color(0, 0, 0, 0));
        
        jpExits.setOpaque(true);
        jpExits.setBackground(new java.awt.Color(0, 0, 0, 200));
        jpExits.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 255, 50), 2));
        
        // 2. Layout
        jpControls.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 150, 10));
        
        int w = jlBackground.getWidth() > 0 ? jlBackground.getWidth() : 800;
        int h = jlBackground.getHeight() > 0 ? jlBackground.getHeight() : 450;
        
        jpControls.setBounds(0, h - 120, w, 120);
        jlBackground.add(jpControls);

        // 3. Forza il ridisegno
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
        this.revalidate();
        this.repaint();
    }
    
    
    
    public void toggleInventory(java.util.List<it.map.graphicadventure.progettoesame.type.GameObject> items) {
        // 1. Forziamo il pannello centrale a usare un BorderLayout
        if (jpPlayingArea.getLayout() instanceof javax.swing.GroupLayout) {
             jpPlayingArea.setLayout(new java.awt.BorderLayout());
        }

        if (isInventoryVisible) {
            // ==========================================
            // CHIUSURA INVENTARIO
            // ==========================================
            if (jpInventoryView != null) {
                jpPlayingArea.remove(jpInventoryView);
            }
            jpPlayingArea.add(jlBackground, java.awt.BorderLayout.CENTER);
            isInventoryVisible = false;
            
        } else {
            // ==========================================
            // APERTURA INVENTARIO
            // ==========================================
            jpPlayingArea.remove(jlBackground); // Nascondiamo la stanza

            if (jpInventoryView == null) {
                jpInventoryView = new javax.swing.JPanel();
            }
            
            // Svuotiamo tutto il pannello per ricostruirlo aggiornato
            jpInventoryView.removeAll();
            jpInventoryView.setLayout(new java.awt.BorderLayout());
            jpInventoryView.setBackground(new java.awt.Color(15, 15, 15));

            // --- 1. BARRA SUPERIORE CON LA "X" ---
            javax.swing.JPanel topBar = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
            topBar.setOpaque(false); // Sfondo trasparente
            
            javax.swing.JButton btnClose = new javax.swing.JButton("X CHIUDI");
            btnClose.setUI(new javax.swing.plaf.basic.BasicButtonUI()); // Disattiva lo stile Mac
            btnClose.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 16));
            btnClose.setForeground(new java.awt.Color(255, 50, 50));
            btnClose.setBackground(new java.awt.Color(10, 10, 10));
            btnClose.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 50, 50), 2));
            btnClose.setFocusPainted(false);
            btnClose.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            
            // Effetto Hover del tasto X
            btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btnClose.setBackground(new java.awt.Color(255, 50, 50));
                    btnClose.setForeground(java.awt.Color.BLACK);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btnClose.setBackground(new java.awt.Color(10, 10, 10));
                    btnClose.setForeground(new java.awt.Color(255, 50, 50));
                }
            });
            
            btnClose.addActionListener(e -> toggleInventory(null));
            topBar.add(btnClose);
            
            // Aggiungiamo la barra al NORD dell'inventario
            jpInventoryView.add(topBar, java.awt.BorderLayout.NORTH);

            // --- 2. GRIGLIA DEGLI OGGETTI AL CENTRO ---
            javax.swing.JPanel gridPanel = new javax.swing.JPanel();
            gridPanel.setOpaque(false); // Fa vedere il colore di fondo scuro
            gridPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

            if (items == null || items.isEmpty()) {
                // Inventario Vuoto
                gridPanel.setLayout(new java.awt.BorderLayout());
                javax.swing.JLabel jlEmpty = new javax.swing.JLabel("Il tuo zaino è vuoto.", javax.swing.SwingConstants.CENTER);
                jlEmpty.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 18));
                jlEmpty.setForeground(java.awt.Color.WHITE);
                gridPanel.add(jlEmpty, java.awt.BorderLayout.CENTER);
            } else {
                // Inventario con Oggetti (Griglia 4 colonne)
                gridPanel.setLayout(new java.awt.GridLayout(0, 4, 10, 10)); 
                for (it.map.graphicadventure.progettoesame.type.GameObject item : items) {
                    gridPanel.add(createItemSlot(item));
                }
            }

            // Aggiungiamo la griglia al CENTRO dell'inventario
            jpInventoryView.add(gridPanel, java.awt.BorderLayout.CENTER);
            
            // Mostriamo l'inventario sulla schermata di gioco
            jpPlayingArea.add(jpInventoryView, java.awt.BorderLayout.CENTER);
            isInventoryVisible = true;
        }

        // Forza Java a ricalcolare la grafica
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
    }

    private javax.swing.JPanel createItemSlot(it.map.graphicadventure.progettoesame.type.GameObject item) {
        javax.swing.JPanel itemSlot = new javax.swing.JPanel(new java.awt.BorderLayout());
        itemSlot.setBackground(new java.awt.Color(30, 30, 30));
        itemSlot.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 255, 50), 1));

        javax.swing.JLabel jlIcon = new javax.swing.JLabel("", javax.swing.SwingConstants.CENTER);
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                java.net.URL imgURL = getClass().getResource(item.getImagePath());
                if (imgURL != null) {
                    javax.swing.ImageIcon originalIcon = new javax.swing.ImageIcon(imgURL);
                    java.awt.Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, java.awt.Image.SCALE_SMOOTH);
                    jlIcon.setIcon(new javax.swing.ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                System.err.println("Icon missing for: " + item.getName());
            }
        }
        itemSlot.add(jlIcon, java.awt.BorderLayout.CENTER);

        javax.swing.JLabel jlName = new javax.swing.JLabel(item.getName(), javax.swing.SwingConstants.CENTER);
        jlName.setForeground(java.awt.Color.WHITE);
        jlName.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 10));
        itemSlot.add(jlName, java.awt.BorderLayout.SOUTH);

        itemSlot.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        itemSlot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemSlot.setBackground(new java.awt.Color(50, 255, 50));
                jlName.setForeground(java.awt.Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                itemSlot.setBackground(new java.awt.Color(30, 30, 30));
                jlName.setForeground(java.awt.Color.WHITE);
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                System.out.println("Selected item: " + item.getName());
            }
        });

        return itemSlot;
    }
    
    /**
     * Gestisce il tentativo di movimento in una direzione specifica.
     */
    private void movePlayer(String direction) {
        if (controller != null) {
            // 1. Diciamo al controller di provare a muovere il giocatore e ci facciamo dare il testo di risposta
            String response = controller.handleMovement(direction);
            
            // 2. Stampiamo la risposta nel terminale in basso ("Ti sposti..." oppure "Non puoi...")
            animatedText(response);
            
            // 3. Ridisegniamo la stanza. 
            // Se il controller ha cambiato la stanza, renderRoom caricherà la nuova immagine e i nuovi oggetti!
            // Se il movimento è fallito, renderRoom semplicemente non farà nulla di nuovo.
            renderRoom(controller.getCurrentRoom());
        }
    }
    
    /**
     * Crea un bottone direzionale stilizzato per il D-Pad.
     */
    private javax.swing.JButton directionButton(String command, String symbol, boolean isOpen) {
        javax.swing.JButton btn = new javax.swing.JButton(symbol);
        
        // Font in stile terminale
        btn.setFont(new java.awt.Font("Monospaced", java.awt.Font.BOLD, 18));
        
        // FORZIAMO IL QUADRATINO NERO
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBackground(new java.awt.Color(10, 10, 10)); // Nero puro
        btn.setFocusPainted(false);
        
        if (isOpen) {
            // STILE ATTIVO (Bordi e testo verdi)
            btn.setForeground(new java.awt.Color(50, 255, 50)); 
            btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 255, 50), 1));
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            
            // Effetto hover: si inverte quando passi il mouse
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(new java.awt.Color(50, 255, 50));
                    btn.setForeground(java.awt.Color.BLACK);
                }
                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(new java.awt.Color(10, 10, 10));
                    btn.setForeground(new java.awt.Color(50, 255, 50));
                }
            });
            
            // L'azione di movimento
            btn.addActionListener(e -> movePlayer(command)); // Assicurati di usare il nome corretto del tuo metodo (es. movePlayer)
        } else {
            // STILE DISABILITATO (Il quadratino c'è, ma è spento)
            btn.setForeground(new java.awt.Color(40, 40, 40)); // Grigio scuro
            btn.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(30, 30, 30), 1));
            btn.setEnabled(false);
        }
        
        return btn;
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
        jpControls = new javax.swing.JPanel();
        jbInventory = new javax.swing.JButton();
        jpExits = new javax.swing.JPanel();

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

        jpPlayingArea.setLayout(new java.awt.BorderLayout());
        jpPlayingArea.add(jlBackground, java.awt.BorderLayout.CENTER);

        add(jpPlayingArea, java.awt.BorderLayout.CENTER);

        jbInventory.setText("[ INVENTARIO ]");
        jbInventory.addActionListener(this::jbInventoryActionPerformed);

        javax.swing.GroupLayout jpControlsLayout = new javax.swing.GroupLayout(jpControls);
        jpControls.setLayout(jpControlsLayout);
        jpControlsLayout.setHorizontalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbInventory)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpExits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jpControlsLayout.setVerticalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpControlsLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jbInventory)
                .addGap(36, 36, 36))
            .addGroup(jpControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jpExits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        add(jpControls, java.awt.BorderLayout.PAGE_END);
    }// </editor-fold>//GEN-END:initComponents

    private void jbInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInventoryActionPerformed
        if (controller != null) {
            controller.handleInventoryToggle();
        }
    }//GEN-LAST:event_jbInventoryActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbInventory;
    private javax.swing.JLabel jlBackground;
    private javax.swing.JPanel jpControls;
    private javax.swing.JPanel jpExits;
    private javax.swing.JPanel jpPlayingArea;
    private javax.swing.JTextArea jtaDialogs;
    // End of variables declaration//GEN-END:variables
}
