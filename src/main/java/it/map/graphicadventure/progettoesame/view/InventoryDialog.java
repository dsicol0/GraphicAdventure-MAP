/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.type.GameObject;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryDialog extends JDialog {
    
    private JPanel jpItemsContainer;

    public InventoryDialog(Frame parentFrame, List<GameObject> items) {
        // "true" rende il dialog modale (blocca i click sul gioco sottostante finché non si chiude)
        super(parentFrame, "Inventory", true); 
        initComponents(items);
    }

    private void initComponents(List<GameObject> items) {
        setSize(500, 400);
        setLocationRelativeTo(getParent()); // Lo centra rispetto alla finestra principale
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 15, 15));

        // Titolo
        JLabel jlTitle = new JLabel("INVENTORY", SwingConstants.CENTER);
        jlTitle.setForeground(new Color(50, 255, 50));
        jlTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jlTitle, BorderLayout.NORTH);

        // Contenitore per gli oggetti
        jpItemsContainer = new JPanel();
        jpItemsContainer.setBackground(new Color(15, 15, 15));
        // Crea una griglia dinamica: n righe, 4 colonne, con 10px di spazio tra gli oggetti
        jpItemsContainer.setLayout(new GridLayout(0, 4, 10, 10));
        jpItemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Popoliamo la griglia leggendo la lista
        if (items == null || items.isEmpty()) {
            JLabel jlEmpty = new JLabel("Your backpack is empty.");
            jlEmpty.setForeground(Color.WHITE);
            jpItemsContainer.add(jlEmpty);
        } else {
            for (GameObject item : items) {
                jpItemsContainer.add(createItemSlot(item));
            }
        }

        JScrollPane scrollPane = new JScrollPane(jpItemsContainer);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Crea il singolo quadratino dell'inventario per un oggetto
     */
    private JPanel createItemSlot(GameObject item) {
        JPanel itemSlot = new JPanel();
        itemSlot.setLayout(new BorderLayout());
        itemSlot.setBackground(new Color(30, 30, 30));
        itemSlot.setBorder(BorderFactory.createLineBorder(new Color(50, 255, 50), 1));

        // Caricamento Immagine
        JLabel jlIcon = new JLabel("", SwingConstants.CENTER);
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                java.net.URL imgURL = getClass().getResource(item.getImagePath());
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);
                    // Rimpiccioliamo le icone per farle stare bene nello zaino
                    Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    jlIcon.setIcon(new ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                System.err.println("Icon missing for: " + item.getName());
            }
        }
        itemSlot.add(jlIcon, BorderLayout.CENTER);

        // Nome sotto l'immagine
        JLabel jlName = new JLabel(item.getName(), SwingConstants.CENTER);
        jlName.setForeground(Color.WHITE);
        jlName.setFont(new Font("Monospaced", Font.PLAIN, 10));
        itemSlot.add(jlName, BorderLayout.SOUTH);

        // Effetto Hover (Mouse)
        itemSlot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        itemSlot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                itemSlot.setBackground(new Color(50, 255, 50));
                jlName.setForeground(Color.BLACK);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                itemSlot.setBackground(new Color(30, 30, 30));
                jlName.setForeground(Color.WHITE);
            }
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Per ora stampiamo solo il log. Qui poi aggiungeremo l'uso dell'oggetto!
                System.out.println("Selected item: " + item.getName());
            }
        });

        return itemSlot;
    }
}