/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JPanel.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.controller.GameController;
import it.map.graphicadventure.progettoesame.model.GameObject;
import java.util.List;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.items.Weapon;
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
        
        // NECESSARI PER NON AVERE IL BRUTTO DESIGN DEL SISTEMA OPERATIVO (NON TOGLIERE)
        jbNorth.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        jbSouth.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        jbEast.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        jbWest.setUI(new javax.swing.plaf.basic.BasicButtonUI());
        jbInventory.setUI(new javax.swing.plaf.basic.BasicButtonUI());
    }
    
    public void animatedText(String testo) {
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
        // 1. Aggiorna il testo della descrizione solo se la stanza è diversa dalla precedente
        if (isInventoryVisible) {
            toggleInventory(null);
        }

        if (this.currentRenderedRoom != room) {
            animatedText(room.getDescription());
            this.currentRenderedRoom = room;
        }

        // 2. Aggiorna l'immagine di sfondo della stanza
        String imagePath = room.getBackgroundPath();
        if (imagePath != null && !imagePath.trim().isEmpty()) {
            java.net.URL imgURL = getClass().getResource(imagePath);
            if (imgURL != null) {
                int larghezza = jpPlayingArea.getWidth() > 0 ? jpPlayingArea.getWidth() : 800;
                int altezza = jpPlayingArea.getHeight() > 0 ? jpPlayingArea.getHeight() : 450;

                // La label dello sfondo ora occupa solo la sua area di gioco, non va più sotto l'HUD!
                jlBackground.setBounds(0, 0, larghezza, altezza);

                javax.swing.ImageIcon originalImage = new javax.swing.ImageIcon(imgURL);
                java.awt.Image scaledImage = originalImage.getImage().getScaledInstance(larghezza, altezza, java.awt.Image.SCALE_SMOOTH);
                jlBackground.setIcon(new javax.swing.ImageIcon(scaledImage));
            } else {
                System.err.println("Risorsa non trovata: " + imagePath);
                jlBackground.setIcon(null);
            }
        } else {
            jlBackground.setIcon(null);
        }

        // Svuota lo sfondo dai vecchi oggetti della stanza precedente
        jlBackground.removeAll();
        jlBackground.setLayout(null);

        // 3. DISEGNO DEI BOTTONI DEGLI OGGETTI (Cassa, Chiave, ecc.)
        if (room.getObjects() != null) {
            for (it.map.graphicadventure.progettoesame.model.GameObject obj : room.getObjects()) {

                javax.swing.JButton objectButton = new javax.swing.JButton();
                objectButton.setOpaque(false);
                objectButton.setContentAreaFilled(false);
                objectButton.setFocusPainted(false);

                if (obj.getImagePath() != null && !obj.getImagePath().isEmpty()) {
                    java.net.URL objUrl = getClass().getResource(obj.getImagePath());
                    if (objUrl != null) {
                        javax.swing.ImageIcon objIcon = new javax.swing.ImageIcon(objUrl);
                        java.awt.Image objScaled = objIcon.getImage().getScaledInstance(obj.getWidth(), obj.getHeight(), java.awt.Image.SCALE_SMOOTH);
                        objectButton.setIcon(new javax.swing.ImageIcon(objScaled));
                    }
                }

                objectButton.setBounds(obj.getX(), obj.getY(), obj.getWidth(), obj.getHeight());
                objectButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

                // Click sull'oggetto con richiesta di conferma modale
                objectButton.addActionListener(e -> {
                    java.awt.Frame parentFrame = (java.awt.Frame) javax.swing.SwingUtilities.getWindowAncestor(this);
                    
                    String message;
                    if (obj instanceof it.map.graphicadventure.progettoesame.model.interfaces.Openable) {
                        message = "Vuoi davvero aprire " + obj.getName() + "?";
                    } else if (obj instanceof it.map.graphicadventure.progettoesame.model.interfaces.Takeable) {
                        message = "Vuoi davvero raccogliere " + obj.getName() + "?";
                    } else if (obj instanceof it.map.graphicadventure.progettoesame.model.interfaces.Usable) {
                        message = "Vuoi davvero usare " + obj.getName() + "?";
                    } else {
                        message = "Vuoi interagire con " + obj.getName() + "?";
                    }

                    ConfirmDialog cd = new ConfirmDialog(parentFrame, true, message);
                    cd.setVisible(true);

                    if (cd.isConfirmed()) {
                        if (controller != null) {
                            String r = controller.handleObjectInteraction(obj);
                            animatedText(r);
                            renderRoom(room); // Ridisegna per far sparire l'oggetto raccolto
                        }
                    }
                });

                jlBackground.add(objectButton);
            }
        }

        // 4. AGGIORNAMENTO STATO DELLE FRECCE (DAL DESIGN)
        // Leggiamo le direzioni e accendiamo/spegniamo i bottoni già esistenti sul pannello
        java.util.Set<String> directions = new java.util.HashSet<>();
        if (room.getAvailableDirections() != null) {
            for (String dir : room.getAvailableDirections()) {
                if (dir != null) {
                    directions.add(dir.trim().toUpperCase());
                }
            }
        }

        jbNorth.setEnabled(directions.contains("NORD"));
        jbWest.setEnabled(directions.contains("OVEST"));
        jbEast.setEnabled(directions.contains("EST"));
        jbSouth.setEnabled(directions.contains("SUD"));

        // 5. REFRESH GENERALE DELLA SCHERMATA
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
        this.revalidate();
        this.repaint();
    }
    
    public void updateJlHealth() {
        if (controller != null && controller.getPlayer() != null) {
            Player player = controller.getPlayer();

            // Prendiamo gli HP attuali
            int playerHp = player.getHp();

            // Aggiorniamo il testo della label (mostrerà solo il numero, es: "100")
            jlHealth.setText(String.valueOf(playerHp));

            // 🎨 Controllo colore accademico: se ha poca vita diventa rosso, altrimenti verde neon coerente!
            if (player.isDead()) {
                jlHealth.setForeground(java.awt.Color.RED);
                jlHealth.setText("0"); // Evita numeri negativi se muore male
            } else if (playerHp <= 30) {
                jlHealth.setForeground(java.awt.Color.RED); // Stato di pericolo
            } else {
                jlHealth.setForeground(new java.awt.Color(50, 255, 50)); // Verde neon coordinato
            }
        }
    }

    public void toggleInventory(List<GameObject> items) {
        
        // Calcoliamo larghezza e altezza per passarla alle AbsoluteConstraints
        int w = jpPlayingArea.getWidth() > 0 ? jpPlayingArea.getWidth() : 800;
        int h = jpPlayingArea.getHeight() > 0 ? jpPlayingArea.getHeight() : 450;

        if (isInventoryVisible) {
            // ==========================================
            // CHIUSURA INVENTARIO
            // ==========================================
            if (jpInventoryView != null) {
                jpPlayingArea.remove(jpInventoryView);
            }
            
            // 🟩 CORREZIONE: Usiamo la sintassi di NetBeans invece di BorderLayout.CENTER
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
                
                jpInventoryView.add(gridPanel, java.awt.BorderLayout.CENTER);
            } else {
                // Inventario con Oggetti (Griglia fissa a 4 colonne)
                gridPanel.setLayout(new java.awt.GridLayout(0, 4, 15, 15)); 
                for (it.map.graphicadventure.progettoesame.model.GameObject item : items) {
                    gridPanel.add(createItemSlot(item));
                }
                
                // Il tuo trucco definitivo con il FlowLayout
                javax.swing.JPanel flowWrapper = new javax.swing.JPanel(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 30, 30));
                flowWrapper.setOpaque(false);
                flowWrapper.add(gridPanel);
                
                jpInventoryView.add(flowWrapper, java.awt.BorderLayout.CENTER);
            }
            
            // 🟩 CORREZIONE: Sintassi corretta per inserire l'inventario nell'Absolute Layout
            jpPlayingArea.add(jpInventoryView, java.awt.BorderLayout.CENTER);
            isInventoryVisible = true;
        }

        // Forza Java a ricalcolare la grafica
        jpPlayingArea.revalidate();
        jpPlayingArea.repaint();
    }

    private javax.swing.JPanel createItemSlot(it.map.graphicadventure.progettoesame.model.GameObject item) {
        javax.swing.JPanel itemSlot = new javax.swing.JPanel(new java.awt.BorderLayout());
        itemSlot.setBackground(new java.awt.Color(30, 30, 30));
        itemSlot.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(50, 255, 50), 1));
        
        // 🟩 BLOCCHIAMO LE MISURE DA TUTTI I LATI (Minima, Massima e Preferita):
        itemSlot.setPreferredSize(new java.awt.Dimension(110, 110));
        itemSlot.setMinimumSize(new java.awt.Dimension(110, 110));
        itemSlot.setMaximumSize(new java.awt.Dimension(110, 110));

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
                StringBuilder sb = new StringBuilder();
                sb.append("> Inventario: ").append(item.getName().toUpperCase()).append(".\n");
                sb.append(item.getDescription());
                
                // Se l'oggetto è un'istanza di Weapon stampiamo anche il danno
                if (item instanceof Weapon) {
                    Weapon weapon = (Weapon) item;
                    sb.append("\nPotenza d'attacco: ").append(weapon.getDamage()).append(" PT");
                }
                animatedText(sb.toString());
            }
        });

        return itemSlot;
    }
    
    /**
     * Gestisce il tentativo di movimento in una direzione specifica.
     */
    private void movePlayer(String direction) {
        if (controller == null) return;
        
        // Memorizziamo la stanza attuale prima del movimento
        Room previousRoom = controller.getCurrentRoom();

        // Chiediamo il movimento al controller (ci restituirà il testo dell'azione o dell'errore)
        String response = controller.handleMovement(direction);

        // Recuperiamo la stanza dopo che il controller ha agito
        Room newRoom = controller.getCurrentRoom();
        
        // IL GIOCATORE HA CAMBIATO STANZA
        if(previousRoom != newRoom) {
            String finalMessage = newRoom.getDescription();
            
            if (response != null && !response.trim().isEmpty()) {
                finalMessage = response + "\n\n" + finalMessage;
            }
            
            this.currentRenderedRoom = newRoom;
            // Avviamo l'animazione UNA SOLA VOLTA con tutto il testo unito
            animatedText(finalMessage);
            
            renderRoom(newRoom);
        } else {
            if (response != null && !response.trim().isEmpty()) {
                // Viene stampato semplicemente il messaggio restituito dal controller
                animatedText(response);
            }
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
        jpControls = new javax.swing.JPanel();
        jlHealth = new javax.swing.JLabel();
        jbInventory = new javax.swing.JButton();
        jpExits = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jbNorth = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jbWest = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jbEast = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jbSouth = new javax.swing.JButton();
        jLabel5 = new javax.swing.JLabel();
        jpPlayingArea = new javax.swing.JPanel();
        jlBackground = new javax.swing.JLabel();

        setMaximumSize(new java.awt.Dimension(850, 600));
        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(new java.awt.BorderLayout());

        jtaDialogs.setEditable(false);
        jtaDialogs.setBackground(new java.awt.Color(31, 25, 18));
        jtaDialogs.setColumns(20);
        jtaDialogs.setFont(new java.awt.Font("Times New Roman", 1, 18)); // NOI18N
        jtaDialogs.setForeground(new java.awt.Color(210, 195, 160));
        jtaDialogs.setLineWrap(true);
        jtaDialogs.setRows(6);
        jtaDialogs.setWrapStyleWord(true);
        jtaDialogs.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(255, 255, 255), 3));
        jScrollPane1.setViewportView(jtaDialogs);

        add(jScrollPane1, java.awt.BorderLayout.PAGE_START);

        jpControls.setBackground(new java.awt.Color(31, 25, 18));
        jpControls.setPreferredSize(new java.awt.Dimension(800, 80));

        jlHealth.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jlHealth.setForeground(new java.awt.Color(255, 255, 255));
        jlHealth.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jlHealth.setIcon(new javax.swing.ImageIcon(getClass().getResource("/hearth.png"))); // NOI18N
        jlHealth.setText("100");
        jlHealth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        jbInventory.setBackground(new java.awt.Color(85, 70, 50));
        jbInventory.setFont(new java.awt.Font("Monospaced", 1, 14)); // NOI18N
        jbInventory.setForeground(new java.awt.Color(210, 195, 160));
        jbInventory.setText("[ INVENTARIO ]");
        jbInventory.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(20, 15, 10), 2));
        jbInventory.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbInventory.setFocusPainted(false);
        jbInventory.setPreferredSize(new java.awt.Dimension(124, 5));
        jbInventory.addActionListener(this::jbInventoryActionPerformed);

        jpExits.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 18));
        jpExits.setOpaque(false);
        jpExits.setPreferredSize(new java.awt.Dimension(110, 110));
        jpExits.setLayout(new java.awt.GridLayout(3, 3, 2, 2));
        jpExits.add(jLabel1);

        jbNorth.setBackground(new java.awt.Color(85, 70, 50));
        jbNorth.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jbNorth.setForeground(new java.awt.Color(210, 195, 160));
        jbNorth.setText("▲");
        jbNorth.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(20, 15, 10), 2));
        jbNorth.setBorderPainted(false);
        jbNorth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbNorth.setFocusPainted(false);
        jbNorth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbNorth.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jbNorth.addActionListener(this::jbNorthActionPerformed);
        jpExits.add(jbNorth);
        jpExits.add(jLabel2);

        jbWest.setBackground(new java.awt.Color(85, 70, 50));
        jbWest.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jbWest.setForeground(new java.awt.Color(210, 195, 160));
        jbWest.setText("◄");
        jbWest.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(20, 15, 10), 2));
        jbWest.setBorderPainted(false);
        jbWest.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbWest.setFocusPainted(false);
        jbWest.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbWest.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jbWest.addActionListener(this::jbWestActionPerformed);
        jpExits.add(jbWest);
        jpExits.add(jLabel3);

        jbEast.setBackground(new java.awt.Color(85, 70, 50));
        jbEast.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jbEast.setForeground(new java.awt.Color(210, 195, 160));
        jbEast.setText("►");
        jbEast.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(20, 15, 10), 2));
        jbEast.setBorderPainted(false);
        jbEast.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbEast.setFocusPainted(false);
        jbEast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbEast.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jbEast.addActionListener(this::jbEastActionPerformed);
        jpExits.add(jbEast);
        jpExits.add(jLabel4);

        jbSouth.setBackground(new java.awt.Color(85, 70, 50));
        jbSouth.setFont(new java.awt.Font("Monospaced", 1, 18)); // NOI18N
        jbSouth.setForeground(new java.awt.Color(210, 195, 160));
        jbSouth.setText("▼");
        jbSouth.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(20, 15, 10), 2));
        jbSouth.setBorderPainted(false);
        jbSouth.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jbSouth.setFocusPainted(false);
        jbSouth.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jbSouth.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jbSouth.addActionListener(this::jbSouthActionPerformed);
        jpExits.add(jbSouth);
        jpExits.add(jLabel5);

        javax.swing.GroupLayout jpControlsLayout = new javax.swing.GroupLayout(jpControls);
        jpControls.setLayout(jpControlsLayout);
        jpControlsLayout.setHorizontalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpControlsLayout.createSequentialGroup()
                .addComponent(jbInventory, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jlHealth, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(478, 478, 478)
                .addComponent(jpExits, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jpControlsLayout.setVerticalGroup(
            jpControlsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jbInventory, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(jpExits, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpControlsLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jlHealth)
                .addContainerGap())
        );

        add(jpControls, java.awt.BorderLayout.SOUTH);

        jpPlayingArea.setPreferredSize(new java.awt.Dimension(800, 450));
        jpPlayingArea.setLayout(new java.awt.BorderLayout());

        jlBackground.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jpPlayingArea.add(jlBackground, java.awt.BorderLayout.CENTER);

        add(jpPlayingArea, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jbInventoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbInventoryActionPerformed
        if (controller != null) {
            controller.handleInventoryToggle();
        }
    }//GEN-LAST:event_jbInventoryActionPerformed

    private void jbSouthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbSouthActionPerformed
        movePlayer("SUD");
    }//GEN-LAST:event_jbSouthActionPerformed

    private void jbNorthActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbNorthActionPerformed
        movePlayer("NORD");
    }//GEN-LAST:event_jbNorthActionPerformed

    private void jbWestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbWestActionPerformed
        movePlayer("OVEST");
    }//GEN-LAST:event_jbWestActionPerformed

    private void jbEastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbEastActionPerformed
        movePlayer("EST");
    }//GEN-LAST:event_jbEastActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton jbEast;
    private javax.swing.JButton jbInventory;
    private javax.swing.JButton jbNorth;
    private javax.swing.JButton jbSouth;
    private javax.swing.JButton jbWest;
    private javax.swing.JLabel jlBackground;
    private javax.swing.JLabel jlHealth;
    private javax.swing.JPanel jpControls;
    private javax.swing.JPanel jpExits;
    private javax.swing.JPanel jpPlayingArea;
    private javax.swing.JTextArea jtaDialogs;
    // End of variables declaration//GEN-END:variables
}
