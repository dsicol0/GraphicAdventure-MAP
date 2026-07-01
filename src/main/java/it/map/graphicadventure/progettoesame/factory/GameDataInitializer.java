/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.factory;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.type.items.Key;
import it.map.graphicadventure.progettoesame.type.items.Badge;
import it.map.graphicadventure.progettoesame.type.items.Weapon;
import it.map.graphicadventure.progettoesame.type.items.ObjectContainer;

/**
 *
 * @author David
 */
public class GameDataInitializer {
    
    public static void setUpGameData(EsameGame model) {
        
        // 🟩 UNA SOLA RIGA PER OGNI OGGETTO DEL GIOCO! Pulito, leggibile, immediato.
        
        // Nella stanza iniziale, dentro lo Zaino (ID 16), nascondi la Chiave dell'Esame (ID 17)
        // 1. Stanza 2 (Aula 2) -> Zaino (ID 16) -> Chiave d'Oro (ID 17)
        hideObject(model, 2, 16, new Key(17, "Chiave d'Oro dell'Esame", "Una chiave luccicante.", "/key.png"));
        
        // Esempi futuri di quanto sarà facile aggiungere roba:
        // nascondiOggetto(aulaInformatica, 20, new Badge(21, "Badge Amministratore", "Livello 3", "/badge.png"));
        // nascondiOggetto(armadiettoSicurezza, 35, new Weapon(36, "Manganello", "Danno elevato", "/stick.png", 25));
    }

    @SuppressWarnings("unchecked")
    private static void hideObject(EsameGame model, int idStanza, int idContenitore, GameObject oggettoDaNascondere) {
        if (model == null || model.getRooms() == null) return;

        // FASE 1: Cerca la stanza tramite il suo ID numerico
        Room stanzaTrovata = null;
        for (Room r : model.getRooms()) {
            if (r.getId() == idStanza) { // 🟩 Confronto numerico ultra-sicuro
                stanzaTrovata = r;
                break; 
            }
        }

        // FASE 2: Se la stanza esiste, cerca il contenitore al suo interno per ID
        if (stanzaTrovata != null && stanzaTrovata.getObjects() != null) {
            for (GameObject obj : stanzaTrovata.getObjects()) {
                
                if (obj.getId() == idContenitore && obj instanceof ObjectContainer<?>) {
                    ObjectContainer<GameObject> contenitore = (ObjectContainer<GameObject>) obj;
                    
                    // Inserisce l'oggetto nascosto
                    contenitore.getInsideItems().add(oggettoDaNascondere);
                    return; // Oggetto nascosto con successo, chiudiamo il metodo
                }
            }
            System.err.println("[WARNING] Contenitore ID " + idContenitore + " non trovato nella stanza ID: " + idStanza);
        } else {
            System.err.println("[WARNING] Stanza ID " + idStanza + " non trovata nella mappa del gioco.");
        }
    }
}
