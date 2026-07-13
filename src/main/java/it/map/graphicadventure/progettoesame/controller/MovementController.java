/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.view.ConfirmDialog;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;

/**
 * Controller delegato alla gestione degli spostamenti del giocatore tra le stanze.
 * Controlla se le porte sono aperte, chiuse a chiave o inaccessibili.
 *
 */
public class MovementController extends BaseController {
    
    public static final int ID_AULA_2 = 2;
    public static final int ID_CHIAVE_AULA_2 = 13;
    
    /**
     * Costruttore del controller di movimento.
     *
     * @param model Il motore logico della partita.
     * @param view  L'interfaccia grafica principale.
     */
    public MovementController(EsameGame model, GameMainFrame view) {
        super(model, view);
    }
    
    /**
     * Gestisce la logica di spostamento del giocatore in una specifica direzione.
     * Controlla l'esistenza della stanza adiacente e verifica eventuali serrature.
     * Se la stanza è bloccata, verifica se il giocatore possiede la chiave adatta nell'inventario.
     *
     * @param direction La direzione in cui muoversi (es. "nord", "est").
     * @return Una stringa di testo che descrive l'esito dell'azione,
     * oppure null se il movimento avviene senza messaggi particolari.
     */
    public String handleMovement(String direction) {
        // Recupera la stanza di destinazione basandosi sulla direzione scelta
        Room nextRoom = model.getCurrentRoom().getExit(direction);

        if (nextRoom != null) {
            
            // Se la stanza risulta tra quelle già sbloccate in passato, assicurati che sia aperta
            if (model.getUnlockedRooms().contains(String.valueOf(nextRoom.getId())) || 
                model.getUnlockedRooms().contains(nextRoom.getName())) {
                nextRoom.setLocked(false);
            }   
            
            // Caso in cui la stanza di destinazione è chiusa a chiave
            if (nextRoom.isLocked()) {

                // Controlliamo in modo specifico se è l'Aula 2 e se il giocatore ha la sua chiave
                if (nextRoom.getId() == ID_AULA_2 && model.getPlayer().hasObject(ID_CHIAVE_AULA_2)) {
                    
                    
                    ConfirmDialog cd = new ConfirmDialog(view, true, "Hai la Chiave dell'Aula 2. Vuoi usarla per aprire la porta?");
                    cd.setVisible(true);

                    
                    if (cd.isConfirmed()) {
                        // Sblocca la porta e memorizza la stanza tra quelle aperte
                        model.getUnlockedRooms().add(String.valueOf(nextRoom.getId()));
                        
                        // Consuma (e rimuove) la chiave dall'inventario
                        model.getInventory().removeIf(obj -> obj.getId() == ID_CHIAVE_AULA_2);
                        
                        // Muovi il giocatore
                        model.setCurrentRoom(nextRoom);
                        
                        return "> Hai usato Chiave dell'Aula 2.\nSenti lo scatto della serratura e la porta si spalanca!";
                    } else {
                        // Se l'utente clicca "No"
                        return "> Decidi di conservare la chiave. La porta dell'Aula 2 resta sbarrata.";
                    }
                }
                
                // Se non hai la chiave o è un'altra stanza bloccata
                return "> La porta che conduce a " + nextRoom.getName() + " è serrata dall'interno. Ti serve la chiave corretta.";
            }

            // La stanza è aperta, ci spostiamo normalmente senza stampare nulla
            model.setCurrentRoom(nextRoom);
            return null;
        }

        // Se non esiste una via di uscita in quella direzione
        return "> Non puoi andare in quella direzione.";
    }
}