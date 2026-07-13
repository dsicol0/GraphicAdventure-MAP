/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.view;

import it.map.graphicadventure.progettoesame.model.GameObject;
import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * Finestra di dialogo personalizzata per la selezione 
 * di un oggetto dall'inventario.
 *
 * Progettata per essere invocata principalmente durante le fasi come il combattimento,
 * questa View è una finestra modale. L'interfaccia 
 * viene costruita dinamicamente a run-time iterando sulla collezione degli oggetti 
 * posseduti dal giocatore.
 *
 */
public class InventoryDialog extends JDialog {

    private JPanel jpItemsContainer;
    private GameObject selectedItem = null;

    /**
     * Costruisce la finestra dell'inventario.
     * Invoca il costruttore della superclasse impostando il flag {@code modal} a true,
     * impedendo così all'utente di cliccare altrove finché non ha effettuato una scelta 
     * o chiuso la finestra.
     *
     * @param parentFrame Il frame padre su cui centrare il pop-up.
     * @param items La lista dei {@link GameObject} da mostrare graficamente.
     */
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
     * Costruisce graficamente un singolo "Slot" (Pannello quadrato) per l'oggetto,
     * caricandone l'icona e impostando i listener per l'interazione.
     *
     * Sfrutta l'implementazione di una Classe Anonima
     * per gestire gli eventi di passaggio del mouse (effetto hover) e il click 
     * di selezione,.
     *
     *
     * @param item L'oggetto del Modello di cui generare la rappresentazione visiva.
     * @return Il {@link JPanel} completamente configurato.
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

    /**
     * Permette alla classe chiamante di recuperare in modo sicuro (tramite incapsulamento) 
     * l'oggetto selezionato dal giocatore una volta che la finestra è stata chiusa.
     *
     * @return L'istanza di {@link GameObject} scelta, oppure {@code null} se 
     * la finestra è stata chiusa senza selezioni.
     */
    public GameObject getSelectedItem() {
        return selectedItem;
    }
}
