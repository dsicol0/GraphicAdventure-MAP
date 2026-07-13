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
 * Classe di supporto per configurare lo stato iniziale del mondo di gioco.
 * Inserisce dinamicamente gli oggetti chiave all'interno dei vari contenitori 
 * presenti sulla mappa (es. chiavi negli zaini, chip nelle casse).
 *
 */
public class GameDataInitializer {
    
    /**
     * Inizializza i dati hardcoded del gioco una volta che la mappa è stata caricata.
     * Serve a nascondere gli oggetti importanti all'interno di altri oggetti (contenitori).
     *
     * @param model Il motore di gioco su cui applicare le modifiche.
     */
    public static void setUpGameData(EsameGame model) {
        
        // 1. Stanza 2 (Aula 2) -> Zaino (ID 9) -> Chiave d'Oro (ID 17) e Accendino (ID 18)
        hideObject(model, 2, 9, new Key(17, "Una semplice Chiave", "Una chiave luccicante. Potrebbe servire per aprire qualcosa...", "/items/key.png"));
        hideObject(model, 2, 9, new Weapon(18, "Accendino", "Un'accendino zippo. Probabilmente funziona ancora...", "/items/lighter.png", 10));
        
        // 2. Stanza 6 -> Forziere (ID 14) -> Chip Elettronico (ID 19)
        hideObject(model, 6, 14, new Chip(19,"Chip elettronico", "Un chip di memoria a stato solido recuperato dal forziere. Potrebbe essere la chiave software necessaria per sbloccare il quadro elettrico","/items/electronicBoard.png"));
        
    }

    /**
     * Cerca una specifica stanza, trova al suo interno un determinato contenitore 
     * e ci "nasconde" dentro un oggetto.
     *
     * @param model Il modello di gioco corrente.
     * @param idStanza L'ID della stanza in cui cercare.
     * @param idContenitore L'ID del contenitore (es. zaino o cassa) in cui inserire l'oggetto.
     * @param oggettoDaNascondere L'oggetto vero e proprio da inserire nel contenitore.
     */
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

        // Cerca il contenitore al suo interno per ID, verificando che sia un ObjectContainer
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
