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
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
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
        response.append("Esamini: ").append(clickedObject.getName()).append(". \n");

        // 1. Controllo se si può raccogliere (Takeable)
        if (clickedObject instanceof Takeable && ((Takeable) clickedObject).isTakeable()) {
            model.getInventory().add(clickedObject);
            model.getCurrentRoom().removeObject(clickedObject);
            response.append("Hai raccolto ").append(clickedObject.getName()).append("!");
            return response.toString();
        }

        // 2. Controllo se si può aprire (Openable)
        if (clickedObject instanceof Openable) {
            Openable openableObj = (Openable) clickedObject;

            // FASE 1: TENTATIVO DI SBLOCCO (Solo se Lockable e BLOCCATO)
            if (openableObj instanceof Lockable && ((Lockable) openableObj).isLocked()) {
                Lockable lockableObj = (Lockable) openableObj;
                int requiredKeyId = -1;

                if (clickedObject instanceof Chest) {
                    requiredKeyId = ((Chest<?>) clickedObject).getRequiredKeyId();
                }

                final int targetId = requiredKeyId;
                GameObject matchingKey = model.getInventory().stream()
                        .filter(obj -> obj.getId() == targetId)
                        .findFirst()
                        .orElse(null);

                if (matchingKey != null) {
                    boolean unlocked = false;
                    if (clickedObject instanceof Chest && matchingKey instanceof Key) {
                        unlocked = ((Chest<?>) clickedObject).unlock((Key) matchingKey);
                    } else {
                        lockableObj.setLocked(false);
                        unlocked = true;
                    }

                    if (unlocked) {
                        response.append("Usi **").append(matchingKey.getName()).append("** sulla serratura. Senti un netto 'clack'!\n");
                        model.getInventory().remove(matchingKey);
                    }
                } else {
                    response.append("È chiuso a chiave. Ti serve la chiave adatta per aprirlo.");
                    return response.toString();
                }
            }

            // FASE 2: APERTURA EFFETTIVA
            if (!openableObj.isOpen()) {
                openableObj.open();
                response.append("Apri ").append(clickedObject.getName()).append(".\n");
            } else {
                response.append("È già aperto.\n");
            }

            // FASE 3: SVUOTAMENTO CONTENITORE E RIMOZIONE DELLO ZAINO
            // 🟩 Usiamo ObjectContainer così siamo sicuri al 100% che legga lo Zaino!
            if (clickedObject instanceof ObjectContainer) {
                ObjectContainer<?> container = (ObjectContainer<?>) clickedObject;

                if (container.getInsideItems() != null && !container.getInsideItems().isEmpty()) {
                    response.append("Dentro trovi e raccogli immediatamente:\n");

                    // Creiamo una lista di copia per evitare errori di modifica concorrente
                    java.util.List<GameObject> itemsDaPrendere = new java.util.ArrayList<>(container.getInsideItems());

                    for (GameObject objDentro : itemsDaPrendere) {
                        response.append("- **").append(objDentro.getName()).append("**\n");

                        // 🎒 Mettiamo l'oggetto nell'inventario del giocatore
                        model.getInventory().add(objDentro);

                        // ❌ Lo togliamo dal contenitore
                        container.getInsideItems().remove(objDentro);
                    }

                    // 💥 IL TRUCCO PER FARLO SCOMPARIRE:
                    // Ora che è vuoto, lo rimuoviamo dalla stanza corrente così sparisce dalla vista!
                    model.getCurrentRoom().removeObject(clickedObject);

                } else {
                    response.append("Non c'è niente dentro, è già vuoto.");
                    // Se per caso è vuoto, lo rimuoviamo comunque per non lasciare l'interazione attiva
                    model.getCurrentRoom().removeObject(clickedObject);
                }
            }
        }

        if (response.toString().equals("Esamini: " + clickedObject.getName() + ". \n")) {
            response.append(clickedObject.getDescription());
        }

        return response.toString();
    }
}
