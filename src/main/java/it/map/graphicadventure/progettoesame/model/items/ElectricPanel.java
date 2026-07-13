/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;

/**
 * Rappresenta il quadro elettrico principale dell'edificio.
 * È un oggetto statico della mappa (estende {@link GameObject}) e rappresenta 
 * l'obiettivo finale del gioco. La sua funzione principale è fungere da "bersaglio" 
 * per il {@link Chip} di sicurezza, innescando così la condizione di vittoria.
 *
 */
public class ElectricPanel extends GameObject {

    /**
     * Costruisce il pannello elettrico.
     * Richiama direttamente il costruttore della superclasse {@link GameObject}.
     *
     * @param id L'identificativo univoco dell'oggetto.
     * @param name Il nome del pannello.
     * @param description La descrizione testuale mostrata esaminando il pannello.
     * @param imagePath Il percorso dell'immagine mostrata nell'interfaccia grafica.
     */
    public ElectricPanel(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
    }
    
}