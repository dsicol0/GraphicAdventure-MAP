/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.model.GameObject;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InventoryDialog extends JDialog {

    private JPanel jpItemsContainer;
    private GameObject selectedItem = null;

    public InventoryDialog(Frame parentFrame, List<GameObject> items) {

        super(parentFrame, "Inventory", true);
        initComponents(items);
    }

    private void initComponents(List<GameObject> items) {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(15, 15, 15));

        JLabel jlTitle = new JLabel("INVENTORY", SwingConstants.CENTER);
        jlTitle.setForeground(new Color(50, 255, 50));
        jlTitle.setFont(new Font("Monospaced", Font.BOLD, 24));
        jlTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(jlTitle, BorderLayout.NORTH);

        jpItemsContainer = new JPanel();
        jpItemsContainer.setBackground(new Color(15, 15, 15));

        jpItemsContainer.setLayout(new GridLayout(0, 4, 10, 10));
        jpItemsContainer.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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

        JLabel jlIcon = new JLabel("", SwingConstants.CENTER);
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            try {
                java.net.URL imgURL = getClass().getResource(item.getImagePath());
                if (imgURL != null) {
                    ImageIcon originalIcon = new ImageIcon(imgURL);

                    Image scaledImage = originalIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
                    jlIcon.setIcon(new ImageIcon(scaledImage));
                }
            } catch (Exception e) {
                System.err.println("Icon missing for: " + item.getName());
            }
        }
        itemSlot.add(jlIcon, BorderLayout.CENTER);

        JLabel jlName = new JLabel(item.getName(), SwingConstants.CENTER);
        jlName.setForeground(Color.WHITE);
        jlName.setFont(new Font("Monospaced", Font.PLAIN, 10));
        itemSlot.add(jlName, BorderLayout.SOUTH);

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

                System.out.println("Selected item: " + item.getName());
                InventoryDialog.this.selectedItem = item;
                dispose();
            }
        });

        return itemSlot;
    }

    public GameObject getSelectedItem() {
        return selectedItem;
    }
}
