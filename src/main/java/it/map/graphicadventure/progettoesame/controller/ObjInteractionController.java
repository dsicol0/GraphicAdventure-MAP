/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.controller;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Openable;
import it.map.graphicadventure.progettoesame.model.interfaces.Lockable;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;
import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.items.Chest;
import it.map.graphicadventure.progettoesame.model.items.Chip;
import it.map.graphicadventure.progettoesame.model.items.ElectricPanel;
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author David
 */
public class ObjInteractionController extends BaseController {
    
    public ObjInteractionController(EsameGame model, GameMainFrame view) {
        super(model, view);
    }

    public String handleObjectInteraction(GameObject clickedObject) {
        if (clickedObject == null) {
            return "Non c'è niente di interessante qui.";
        }

        StringBuilder response = new StringBuilder();
        response.append("Esamini: ").append(clickedObject.getName()).append(".\n");

        // 1. Raccogliere (Takeable)
        if (isTakeable(clickedObject)) {
            performTake(clickedObject);
            response.append("Hai raccolto ").append(clickedObject.getName()).append("!");
            return response.toString();
        }

        boolean interactionPerformed = false;

        // 2. Aprire, Sbloccare e Svuotare (Openable)
        if (clickedObject instanceof Openable) {
            Openable openableObj = (Openable) clickedObject;
            
            if (openableObj.isOpen()) {
                response.append("La cassa è già aperta e l'hai già svuotata.");
                return response.toString();
            }
            
            // Tentativo di sblocco (se fallisce, interrompiamo qui)
            if (!handleUnlockAttempt(openableObj, response)) {
                return response.toString();
            }

            // Apertura effettiva
            handleOpening(openableObj, clickedObject, response);

            // Svuotamento (se è un contenitore)
            handleContainerLoot(clickedObject, response);
            
            interactionPerformed = true;
        }
        
        if (clickedObject instanceof ElectricPanel) {

            // Cerchiamo se c'è il Chip nell'inventario del giocatore usando gli Stream
            Chip chipInInventory = model.getInventory().stream()
                    .filter(obj -> obj instanceof Chip)
                    .map(obj -> (Chip) obj)
                    .findFirst()
                    .orElse(null);

            // Se il giocatore possiede il chip
            if (chipInInventory != null) {

                // 💳 Delegiamo la logica specifica alla classe Chip passando il pannello come target
                if (chipInInventory.use(clickedObject)) {

                    // Rimuoviamo il chip consumato dall'inventario
                    model.getInventory().remove(chipInInventory);

                    // Ritorniamo il testo trionfale alla View
                    return "Inserisci il **Chip di Sicurezza** nella fessura del pannello...\n"
                            + "I sistemi si riavviano con un forte ronzio elettronico!\n"
                            + "Le luci dell'edificio si accendono. La corrente è tornata!\n\n"
                            + "🏆 COMPLIMENTI! HAI RIPRISTINATO LA CORRENTE E SUPERATO L'ESAME! HAI VINTO! 🏆";
                }
            } else {
                // Messaggio di fallback se il giocatore clicca sul pannello ma non ha il chip
                return "Esamini: " + clickedObject.getName() + ".\n"
                        + "Lo schermo mostra una luce rossa lampeggiante: 'ACCESSO NEGATO'.\n"
                        + "Ti serve un chip di sicurezza per riattivare l'interruttore generale.";
            }
        }
        // 3. Se non abbiamo fatto azioni speciali, mostriamo solo la descrizione
        if (!interactionPerformed) {
            response.append(clickedObject.getDescription());
        }

        return response.toString();
    }

    // ==========================================
    // METODI PRIVATI (Per il Clean Code e l'SRP)
    // ==========================================

    private boolean isTakeable(GameObject obj) {
        return obj instanceof Takeable && ((Takeable) obj).isTakeable();
    }

    private void performTake(GameObject obj) {
        model.getInventory().add(obj);
        model.getCurrentRoom().removeObject(obj);
    }

    /**
     * Gestisce lo sblocco. Ritorna TRUE se l'oggetto è aperto o è stato appena sbloccato.
     * Ritorna FALSE se l'oggetto è chiuso a chiave e il giocatore non ha la chiave.
     */
    private boolean handleUnlockAttempt(Openable openableObj, StringBuilder response) {
        if (!(openableObj instanceof Lockable)) {
            return true; // Non c'è serratura, si può procedere
        }

        Lockable lockableObj = (Lockable) openableObj;
        if (!lockableObj.isLocked()) {
            return true; // Ha una serratura ma è già sbloccato
        }

        // L'oggetto è chiuso: cerchiamo la chiave
        int requiredKeyId = (openableObj instanceof Chest) ? ((Chest<?>) openableObj).getRequiredKeyId() : -1;
        
        GameObject matchingKey = model.getInventory().stream()
                .filter(obj -> obj.getId() == requiredKeyId)
                .findFirst()
                .orElse(null);

        if (matchingKey != null) {
            boolean unlocked = false;
            if (openableObj instanceof Chest && matchingKey instanceof Key) {
                unlocked = ((Chest<?>) openableObj).unlock((Key) matchingKey);
            } else {
                lockableObj.setLocked(false);
                unlocked = true;
            }

            if (unlocked) {
                response.append("Usi **").append(matchingKey.getName()).append("** sulla serratura. Senti un netto 'clack'!\n");
                model.getInventory().remove(matchingKey);
                return true;
            }
        }

        // Chiave non trovata o sblocco fallito
        response.append("È chiuso a chiave. Ti serve la chiave adatta per aprirlo.");
        return false;
    }

    private void handleOpening(Openable openableObj, GameObject clickedObject, StringBuilder response) {
        if (!openableObj.isOpen()) {
            openableObj.open();
            response.append("Apri ").append(clickedObject.getName()).append(".\n");
        } else {
            response.append("È già aperto.\n");
        }
    }

    private void handleContainerLoot(GameObject clickedObject, StringBuilder response) {
        if (!(clickedObject instanceof ObjectContainer)) {
            return;
        }

        ObjectContainer<?> container = (ObjectContainer<?>) clickedObject;

        if (container.getInsideItems() != null && !container.getInsideItems().isEmpty()) {
            response.append("Dentro trovi e raccogli immediatamente:\n");

            List<GameObject> itemsToLoot = new ArrayList<>(container.getInsideItems());

            for (GameObject objDentro : itemsToLoot) {
                response.append("- **").append(objDentro.getName()).append("**\n");
                model.getInventory().add(objDentro);
                container.getInsideItems().remove(objDentro);
            }
            
            // Il contenitore è vuoto: lo facciamo sparire
            model.getCurrentRoom().removeObject(clickedObject);
            
        } else {
            response.append("Non c'è niente dentro, è già vuoto.");
            model.getCurrentRoom().removeObject(clickedObject);
        }
    }
}