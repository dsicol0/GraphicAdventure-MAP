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

        // 2. Controllo se si può aprire (Openable / Lockable)
        if (clickedObject instanceof Openable) {
            Openable openableObj = (Openable) clickedObject;

            // Usiamo Lockable per vedere se è chiuso a chiave
            if (openableObj instanceof Lockable && ((Lockable) openableObj).isLocked()) {
                response.append("È chiuso a chiave. Serve qualcosa per aprirlo.");
            } else if (openableObj.isOpen()) {
                response.append("È già aperto.");
            } else {
                openableObj.open(); // Metodo corretto dell'interfaccia
                response.append("Lo hai aperto!\n");

                // Se è un contenitore generico (Zaino), sparpaglia gli oggetti nella stanza
                if (clickedObject instanceof ObjectContainer<?>) {
                    ObjectContainer<?> container = (ObjectContainer<?>) clickedObject;
                    List<?> contenuti = container.getInsideItems();
                    if (!contenuti.isEmpty()) {
                        for (Object item : contenuti) {
                            GameObject gameItem = (GameObject) item;
                            model.getInventory().add(gameItem);
                            response.append("Nello zaino trovi ").append(gameItem.getName()).append(". Decidi di prenderlo.\n");
                        }
                        model.getCurrentRoom().removeObject(container);
                    } else {
                        response.append("Ma dentro è completamente vuoto.");
                    }
                }
            }
            return response.toString();
        }

        // Descrizione base se non fa nulla
        if (response.toString().equals("Esamini: " + clickedObject.getName() + ". \n")) {
            response.append(clickedObject.getDescription());
        }

        return response.toString();
    }
}
