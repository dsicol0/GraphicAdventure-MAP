/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.factory;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.items.Chip;
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.model.items.Weapon;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;

/**
 *
 * @author David
 */
public class GameDataInitializer {
    
    public static void setUpGameData(EsameGame model) {
        
        // Nella stanza iniziale, dentro lo Zaino (ID 16), nascondi la Chiave dell'Esame (ID 17)
        // 1. Stanza 2 (Aula 2) -> Zaino (ID 16) -> Chiave d'Oro (ID 17)
        hideObject(model, 2, 9, new Key(17, "Una semplice Chiave", "Una chiave luccicante. Potrebbe servire per aprire qualcosa...", "/items/key.png"));
        hideObject(model, 2, 9, new Weapon(18, "Accendino", "Un'accendino zippo. Probabilmente funziona ancora...", "/items/lighter.png", 10));
        
        hideObject(model, 6, 14, new Chip(19,"Chip elettronico", "Un chip di memoria a stato solido recuperato dal forziere. Potrebbe essere la chiave software necessaria per sbloccare il quadro elettrico","/items/electronicBoard.png"));
        
    }

    @SuppressWarnings("unchecked")
    private static void hideObject(EsameGame model, int idStanza, int idContenitore, GameObject oggettoDaNascondere) {
        if (model == null || model.getRooms() == null) return;


        Room room = model.getRooms().stream()
                .filter(r -> r.getId() == idStanza)
                .findFirst()
                .orElse(null);
        
        // Se la stanza non esiste, stampiamo l'errore ed usciamo
        if (room == null) {
            System.err.println("[WARNING] Stanza ID " + idStanza + " non trovata nella mappa del gioco.");
            return;
        }
        
        // Se la stanza non ha oggetti, non fare niente
        if (room.getObjects() == null) return;

        // Se invece esiste, cerca il contenitore al suo interno per ID
        GameObject targetContainer = room.getObjects().stream()
                .filter(obj -> obj.getId() == idContenitore && obj instanceof ObjectContainer)
                .findFirst()
                .orElse(null);
        
        
        if (targetContainer == null) {
            System.err.println("[WARNING] Contenitore ID " + idContenitore + " non trovato nella stanza ID: " + idStanza);
            return;
        }
        
        
        ObjectContainer<GameObject> container = (ObjectContainer<GameObject>) targetContainer;
        container.getInsideItems().add(oggettoDaNascondere);
    }
}
