/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.controller.GameController;
import it.map.graphicadventure.progettoesame.impl.EsameGame;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.BorderFactory;

/**
 *
 * @author David
 */
public class GameMainFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(GameMainFrame.class.getName());
    private GameController controller;
    private EsameGame model;
    private GamePanel gamePanel;

    /**
     * Creates new form GameMainFrame
     */
    public GameMainFrame() {
            // 1. Questo generato da NetBeans deve stare SEMPRE per primo
        initComponents(); 

        // 2. CREIAMO IL MODEL PER PRIMO (La memoria dei dati)
        // Assicurati che ci sia il "new" e che venga salvato nella variabile della classe
        this.model = new EsameGame(); 

        // 3. Creiamo il pannello di gioco
        this.gamePanel = new GamePanel();

        // 4. CREIAMO IL CONTROLLER PASSANDO IL MODEL APPENA INIZIALIZZATO
        // Nota che passiamo 'this.model' (che ora non è più null!) e 'this' (il frame)
        this.controller = new GameController(this.model, this);
        
        this.gamePanel.setController(this.controller);
        
      
        try {
            javax.swing.ImageIcon iconaOriginale = (javax.swing.ImageIcon) jlBackground.getIcon();
            
            if (iconaOriginale != null) {
                java.awt.Image img = iconaOriginale.getImage();
                java.awt.Image imgScalata = img.getScaledInstance(800, 610, java.awt.Image.SCALE_SMOOTH);
                jlBackground.setIcon(new javax.swing.ImageIcon(imgScalata));
            }
        } catch (Exception e) {
            System.out.println("Impossibile ridimensionare lo sfondo: " + e.getMessage());
        }

        
        try {
            java.io.InputStream is = getClass().getResourceAsStream("/fonts/PressStart2P-Regular.ttf");

            // 2. Creiamo l'oggetto Font di Java (formato TrueType)
            Font pixelFontBase = Font.createFont(Font.TRUETYPE_FONT, is);

            // Registriamo il font nell'ambiente grafico di Java
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(pixelFontBase);

            // Creiamo le varianti di dimensione che ci servono
            Font fontTitolo = pixelFontBase.deriveFont(Font.BOLD, 40f);  // Per il titolo
            Font fontBottoni = pixelFontBase.deriveFont(Font.PLAIN, 14f); // Per i pulsanti

            // 5. APPLICHIAMO IL FONT AI COMPONENTI 
            jtfTitle.setFont(fontTitolo);
            jtfTitle.setOpaque(false);
            jtfTitle.setBackground(new Color(0, 0, 0, 0));
            
            Color verde = new Color(102, 255, 0);
            Color rosso = new Color(255,51,51);
            Color rossoHover = new Color(199, 38, 38);
            
            jbNewGame.setFont(fontBottoni);
            jbNewGame.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                jbNewGame.setForeground(verde);
                jbNewGame.setBorder(BorderFactory.createLineBorder(verde, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                jbNewGame.setForeground(Color.WHITE);
                jbNewGame.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
            }
             });
            jbNewGame.setForeground(Color.WHITE); // Fa diventare il testo bianco (o Color.GREEN se lo vuoi verde!)
            jbNewGame.setContentAreaFilled(false); // Rende lo sfondo del bottone trasparente (addio rettangolo grigio!)
            jbNewGame.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2)); // Opzionale: un bel bordo bianco di 2 pixel
            
            jbContinue.setFont(fontBottoni);
            jbContinue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (jbContinue.isEnabled()) {
                        jbContinue.setForeground(verde);
                        jbContinue.setBorder(BorderFactory.createLineBorder(verde, 2));
                    }
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (jbContinue.isEnabled()) {
                        // Nota: qui stai usando Color.WHITE. Se vuoi che torni al colore
                        // "sabbia" di setContinueButtonEnabled, puoi usare:
                        // new java.awt.Color(210, 195, 160) al posto di Color.WHITE!
                        jbContinue.setForeground(Color.WHITE); 
                        jbContinue.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
                    }
            }
             });
            
            jbQuit.setFont(fontBottoni);
            jbQuit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                jbQuit.setForeground(rossoHover);
                jbQuit.setBorder(BorderFactory.createLineBorder(rossoHover, 2));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                jbQuit.setForeground(rosso);
                jbQuit.setBorder(BorderFactory.createLineBorder(rosso, 2));
            }
             });

        } catch (Exception e) {
            System.out.println("Impossibile caricare la font pixel art: " + e.getMessage());
            // Se fallisce, Swing userà automaticamente la font di default senza crashare
        }
    }
    
    public GamePanel getGamePanel() {
        return this.gamePanel;
    }
    
    public void setContinueButtonEnabled(boolean enabled) {
        if (jbContinue != null) {
            jbContinue.setEnabled(enabled);
            
            if (enabled) {
            //jbContinue.setBackground(new java.awt.Color(85, 70, 50));     // Marrone chiaro
            jbContinue.setForeground(new java.awt.Color(255, 255, 255));  // Sabbia lucido
            jbContinue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
            jbContinue.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpMenu = new javax.swing.JPanel();
        jbNewGame = new javax.swing.JButton();
        jtfTitle = new javax.swing.JTextField();
        jbContinue = new javax.swing.JButton();
        jbQuit = new javax.swing.JButton();
        jlBackground = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Final Exam");
        setResizable(false);

        jpMenu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jbNewGame.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbNewGame.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 2));
        jbNewGame.setContentAreaFilled(false);
        jbNewGame.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbNewGame.setFocusPainted(false);
        jbNewGame.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbNewGame.setLabel("▶ NUOVA PARTITA");
        jbNewGame.addActionListener(this::jbNewGameActionPerformed);
        jpMenu.add(jbNewGame, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 320, 240, 40));

        jtfTitle.setEditable(false);
        jtfTitle.setBackground(new java.awt.Color(0, 0, 0));
        jtfTitle.setFont(new java.awt.Font("Noto Serif Bold", 1, 36)); // NOI18N
        jtfTitle.setForeground(new java.awt.Color(102, 255, 0));
        jtfTitle.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        jtfTitle.setText("Final Exam");
        jtfTitle.setBorder(null);
        jtfTitle.setFocusable(false);
        jpMenu.add(jtfTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(100, 40, 590, 70));

        jbContinue.setBackground(new java.awt.Color(35, 30, 25));
        jbContinue.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbContinue.setForeground(new java.awt.Color(90, 85, 75));
        jbContinue.setText(">> CONTINUA");
        jbContinue.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 204, 204), 2));
        jbContinue.setContentAreaFilled(false);
        jbContinue.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jbContinue.setEnabled(false);
        jbContinue.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbContinue.addActionListener(this::jbContinueActionPerformed);
        jpMenu.add(jbContinue, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 380, 240, 40));

        jbQuit.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jbQuit.setForeground(new java.awt.Color(255, 51, 51));
        jbQuit.setText("X ESCI");
        jbQuit.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(204, 0, 0), 2));
        jbQuit.setContentAreaFilled(false);
        jbQuit.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbQuit.addActionListener(this::jbQuitActionPerformed);
        jpMenu.add(jbQuit, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 440, 240, 40));

        jlBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlBackground.setIcon(new javax.swing.ImageIcon(getClass().getResource("/main_menu_background.png"))); // NOI18N
        jlBackground.setToolTipText("");
        jpMenu.add(jlBackground, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 800, 610));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 804, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpMenu, javax.swing.GroupLayout.PREFERRED_SIZE, 608, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jbQuitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbQuitActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jbQuitActionPerformed

    public void showGamePanel() {
        // 1. Sostituisce completamente il menu con il pannello di gioco
        this.setContentPane(gamePanel);

        // 2. Forza Java a ricalcolare la disposizione dei componenti e a ridisegnare la finestra
        this.revalidate();
        this.repaint();

        // 3. Sposta l'attenzione del programma sul nuovo pannello (utile per i tasti)
        gamePanel.requestFocusInWindow(); 
    }
    
    private void jbNewGameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNewGameActionPerformed

        controller.startNewGame();
    }//GEN-LAST:event_jbNewGameActionPerformed

    private void jbContinueActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbContinueActionPerformed
        if (controller != null) {
            // Deleghiamo tutta la logica complessa al GameController
            controller.continueSavedGame();
        } else {
            System.err.println("Attenzione: Controller non collegato alla View!");
        }
    }//GEN-LAST:event_jbContinueActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new GameMainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jbContinue;
    private javax.swing.JButton jbNewGame;
    private javax.swing.JButton jbQuit;
    private javax.swing.JLabel jlBackground;
    private javax.swing.JPanel jpMenu;
    private javax.swing.JTextField jtfTitle;
    // End of variables declaration//GEN-END:variables
}
