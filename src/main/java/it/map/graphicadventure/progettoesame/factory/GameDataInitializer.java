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
        hideObject(model, 2, 16, new Key(17, "Una semplice Chiave", "Una chiave luccicante. Potrebbe servire per aprire qualcosa...", "/items/key.png"));
        hideObject(model, 2, 16, new Weapon(11, "Accendino", "Un'accendino zippo. Probabilmente funziona ancora...", "/items/lighter.png", 10));
        
        hideObject(model, 6, 15, new Chip(19,"Chip elettronico", "Un chip di memoria a stato solido recuperato dal forziere. Potrebbe essere la chiave software necessaria per sbloccare il quadro elettrico","/items/electronicBoard.png"));
        
        // Esempi futuri di quanto sarà facile aggiungere roba:
        // nascondiOggetto(aulaInformatica, 20, new Badge(21, "Badge Amministratore", "Livello 3", "/badge.png"));
        // nascondiOggetto(armadiettoSicurezza, 35, new Weapon(36, "Manganello", "Danno elevato", "/stick.png", 25));
    }

    @SuppressWarnings("unchecked")
    private static void hideObject(EsameGame model, int idStanza, int idContenitore, GameObject oggettoDaNascondere) {
        if (model == null || model.getRooms() == null) return;

        // FASE 1: Cerca la stanza tramite il suo ID numerico
        Room room = model.getRooms().stream()
                .filter(r -> r.getId() == idStanza)
                .findFirst()
                .orElse(null);
        
        // GUARD CLAUSE 2: Se la stanza non esiste, stampiamo l'errore ed usciamo
        if (room == null) {
            System.err.println("[WARNING] Stanza ID " + idStanza + " non trovata nella mappa del gioco.");
            return;
        }
        
        // GUARD CLAUSE 3: Se la stanza non ha oggetti, non c'è nulla da fare
        if (room.getObjects() == null) return;

        // FASE 2: Se la stanza esiste, cerca il contenitore al suo interno per ID
        GameObject targetContainer = room.getObjects().stream()
                .filter(obj -> obj.getId() == idContenitore && obj instanceof ObjectContainer)
                .findFirst()
                .orElse(null);
        
        // GUARD CLAUSE 4: Se il contenitore non c'è, avvisiamo ed usciamo
        if (targetContainer == null) {
            System.err.println("[WARNING] Contenitore ID " + idContenitore + " non trovato nella stanza ID: " + idStanza);
            return;
        }
        
        // FASE 3: Se siamo arrivati qui, è tutto perfetto! Facciamo il cast e inseriamo l'oggetto.
        ObjectContainer<GameObject> container = (ObjectContainer<GameObject>) targetContainer;
        container.getInsideItems().add(oggettoDaNascondere);
    }
}
