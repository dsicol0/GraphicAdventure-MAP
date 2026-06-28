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

        // 3. Forza il ridisegno
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
        this.revalidate();
        this.repaint();
    }
    
    public void toggleInventory(java.util.List<it.map.graphicadventure.progettoesame.type.GameObject> items) {
        // 1. Forziamo il pannello centrale a usare un BorderLayout (se non lo fa già)
        if (jpPlayingArea.getLayout() instanceof javax.swing.GroupLayout) {
             jpPlayingArea.setLayout(new java.awt.BorderLayout());
        }

        if (isInventoryVisible) {
            // CHIUSURA INVENTARIO
            if (jpInventoryView != null) {
                jpPlayingArea.remove(jpInventoryView);
            }
            jpPlayingArea.add(jlBackground, java.awt.BorderLayout.CENTER);
            isInventoryVisible = false;
        } else {
            // APERTURA INVENTARIO
            jpPlayingArea.remove(jlBackground); // Nascondiamo la stanza

            if (jpInventoryView == null) {
                jpInventoryView = new javax.swing.JPanel();
                jpInventoryView.setBackground(new java.awt.Color(15, 15, 15));
            }
            jpInventoryView.removeAll();
            jpInventoryView.setLayout(new java.awt.GridLayout(0, 4, 10, 10)); // Griglia 4 colonne
            jpInventoryView.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

            if (items == null || items.isEmpty()) {
                javax.swing.JLabel jlEmpty = new javax.swing.JLabel("Your backpack is empty.", javax.swing.SwingConstants.CENTER);
                jlEmpty.setForeground(java.awt.Color.WHITE);
                jpInventoryView.add(jlEmpty);
            } else {
                for (it.map.graphicadventure.progettoesame.type.GameObject item : items) {
                    jpInventoryView.add(createItemSlot(item));
                }
            }

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

        jbInventory.setText("[ INVENTARIO ]");
        jbInventory.addActionListener(this::jbInventoryActionPerformed);

        javax.swing.GroupLayout jpControlsLayout = new javax.swing.GroupLayout(jpControls);
        jpControls.setLayout(jpControlsLayout);
        jpControlsLayout.setHorizontalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jbInventory)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpControlsLayout.setVerticalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpControlsLayout.createSequentialGroup()
                .addContainerGap(41, Short.MAX_VALUE)
                .addComponent(jbInventory)
                .addGap(36, 36, 36))
        );

        javax.swing.GroupLayout jpPlayingAreaLayout = new javax.swing.GroupLayout(jpPlayingArea);
        jpPlayingArea.setLayout(jpPlayingAreaLayout);
        jpPlayingAreaLayout.setHorizontalGroup(
            jpPlayingAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPlayingAreaLayout.createSequentialGroup()
                .addComponent(jpControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 664, Short.MAX_VALUE))
        );
        jpPlayingAreaLayout.setVerticalGroup(
            jpPlayingAreaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jlBackground, javax.swing.GroupLayout.DEFAULT_SIZE, 466, Short.MAX_VALUE)
            .addGroup(jpPlayingAreaLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jpControls, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        add(jpPlayingArea, java.awt.BorderLayout.CENTER);
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
    private javax.swing.JPanel jpPlayingArea;
    private javax.swing.JTextArea jtaDialogs;
    // End of variables declaration//GEN-END:variables
}
