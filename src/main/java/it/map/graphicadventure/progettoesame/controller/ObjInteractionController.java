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
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.items.Chest;
import it.map.graphicadventure.progettoesame.model.items.Chip;
import it.map.graphicadventure.progettoesame.model.items.ElectricPanel;
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.view.GameMainFrame;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller delegato alla gestione di tutte le interazioni con gli oggetti del gioco.
 * Valuta dinamicamente i comportamenti degli oggetti (se si possono raccogliere, 
 * aprire o sbloccare) sfruttando il polimorfismo e le interfacce.
 *
 */
public class ObjInteractionController extends BaseController {
    
    /**
     * Costruttore del controller per le interazioni con gli oggetti.
     *
     * @param model La partita in corso.
     * @param view  L'interfaccia grafica.
     */
    public ObjInteractionController(EsameGame model, GameMainFrame view) {
        super(model, view);
    }

    /**
     * Metodo principale che valuta l'oggetto cliccato dal giocatore e scatena
     * la serie di azioni corrispondenti (raccogliere, sbloccare, esaminare, vincere).
     *
     * @param clickedObject L'oggetto selezionato dal giocatore nella stanza.
     * @return Una stringa che descrive l'esito dell'interazione da stampare a schermo.
     */
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
        if (clickedObject instanceof Openable openableObj) {

            // Mostra il messaggio specifico per la Chest rimasta nella stanza
            if (openableObj.isOpen()) {
                response.append(clickedObject.getName()).append(" è già aperto e non c'è niente dentro.");
                return response.toString();
            }

            // Prova a sbloccare, nel caso fallisse si ferma qui l'interazione
            if (!handleUnlockAttempt(openableObj, response)) {
                return response.toString();
            }

            
            handleOpening(openableObj, clickedObject, response);

            // Lo rimuove dal inventario
            handleContainerLoot(clickedObject, response);

            interactionPerformed = true;
        }

        // 3. Gestione per il pannello elettrico (Condizione di Vittoria)
        if (clickedObject instanceof ElectricPanel) {

            Chip chipInInventory = model.getInventory().stream()
                    .filter(obj -> obj instanceof Chip)
                    .map(obj -> (Chip) obj)
                    .findFirst()
                    .orElse(null);

            if (chipInInventory != null) {
                if (chipInInventory.use(clickedObject)) {
                    
                    model.getInventory().remove(chipInInventory);
                    
                    model.setPowerRestored(true);
                    
                    for (Room r : model.getRooms()) {
                        if (r.getId() == 1) { // L'Atrio principale
                            r.setExit("EST", null);
                            break;
                        }
                    }

                    return """
                           Inserisci il **Chip di Sicurezza** nella fessura del pannello...
                           I sistemi si riavviano con un forte ronzio elettronico! Le luci si accendono.
                           All'improvviso senti un tonfo metallico provenire dal piano di sotto...
                           
                           Le enormi porte a EST dell'atrio principale si sono sbloccate! Corri all'uscita!""";
                }
            } else {
                return "Esamini: " + clickedObject.getName() + ".\n"
                        + "Lo schermo mostra una luce rossa lampeggiante: 'ACCESSO NEGATO'.\n"
                        + "Ti serve un chip di sicurezza per riattivare l'interruttore generale.";
            }
        }
        
        // Se non è successo nulla, stampa solo la descrizione 
        if (!interactionPerformed) {
            response.append(clickedObject.getDescription());
        }

        return response.toString();
    }

    /**
     * Metodo per verificare se un oggetto può essere raccolto.
     * * @param obj L'oggetto da controllare.
     * @return true se implementa Takeable ed è effettivamente raccoglibile.
     */
    private boolean isTakeable(GameObject obj) {
        return obj instanceof Takeable && ((Takeable) obj).isTakeable();
    }

    /**
     * Esegue fisicamente la raccolta, spostando l'oggetto dalla stanza all'inventario.
     * * @param obj L'oggetto da raccogliere.
     */
    private void performTake(GameObject obj) {
        model.getInventory().add(obj);
        model.getCurrentRoom().removeObject(obj);
    }

    /**
     * Tenta di sbloccare un oggetto chiuso a chiave.
     * Cerca nell'inventario la chiave corrispondente all'ID richiesto dalla serratura.
     *
     * @param openableObj L'oggetto che si sta cercando di aprire.
     * @param response    Lo StringBuilder per accodare i messaggi di testo.
     * @return true se l'oggetto non ha serratura, era già sbloccato o è stato appena sbloccato;
     * false se è chiuso a chiave e manca la chiave giusta.
     */
    private boolean handleUnlockAttempt(Openable openableObj, StringBuilder response) {
        if (!(openableObj instanceof Lockable)) {
            return true; // Non c'è serratura, si può procedere
        }

        Lockable lockableObj = (Lockable) openableObj;
        if (!lockableObj.isLocked()) {
            return true; // Ha una serratura ma è già sbloccato
        }

        // L'oggetto è chiuso: cerchiamo la chiave giusta nell'inventario
        int requiredKeyId = (openableObj instanceof Chest) ? ((Chest<?>) openableObj).getRequiredKeyId() : -1;

        GameObject matchingKey = model.getInventory().stream()
                .filter(obj -> obj.getId() == requiredKeyId)
                .findFirst()
                .orElse(null);

        if (matchingKey != null) {
            boolean unlocked = false;
            // Se è una Chest usiamo il metodo unlock, altrimenti sblocco base
            if (openableObj instanceof Chest && matchingKey instanceof Key) {
                unlocked = ((Chest<?>) openableObj).unlock((Key) matchingKey);
            } else {
                lockableObj.setLocked(false);
                unlocked = true;
            }

            if (unlocked) {
                response.append("Usi ").append(matchingKey.getName()).append(" sulla serratura. Senti un netto 'clack'!\n");
                model.getInventory().remove(matchingKey);
                return true;
            }
        }

        // Chiave non trovata o sblocco fallito
        response.append("È chiuso a chiave. Ti serve la chiave adatta per aprirlo.");
        return false;
    }

    /**
     * Apre fisicamente l'oggetto. Se si tratta di un oggetto base senza serratura (es. zaino),
     * dopo l'apertura viene rimosso dalla stanza. Se è una cassa fissa, resta visibile.
     *
     * @param openableObj   L'interfaccia di apertura dell'oggetto.
     * @param clickedObject L'entità base dell'oggetto cliccato.
     * @param response      Lo StringBuilder per i messaggi.
     */
    private void handleOpening(Openable openableObj, GameObject clickedObject, StringBuilder response) {
        if (!openableObj.isOpen()) {
            openableObj.open();
            response.append("Apri ").append(clickedObject.getName()).append(".\n");

            // Rimuoviamo dalla stanza solo se NON è Lockable (lo Zaino sparisce, la Chest resta)
            if (!(clickedObject instanceof Lockable)) {
                model.getCurrentRoom().getObjects().remove(clickedObject);
            }
        } else {
            response.append("È già aperto.\n");
        }
    }

    /**
     * Svuota gli oggetti presenti all'interno di un contenitore, trasferendoli
     * automaticamente nell'inventario del giocatore.
     *
     * @param clickedObject L'oggetto cliccato (che dovrebbe essere un ObjectContainer).
     * @param response      Lo StringBuilder per i messaggi visivi.
     */
    private void handleContainerLoot(GameObject clickedObject, StringBuilder response) {
        if (!(clickedObject instanceof ObjectContainer)) {
            return;
        }

        ObjectContainer<?> container = (ObjectContainer<?>) clickedObject;

        if (container.getInsideItems() != null && !container.getInsideItems().isEmpty()) {
            response.append("Dentro trovi e raccogli immediatamente:\n");

            List<GameObject> itemsToLoot = new ArrayList<>(container.getInsideItems());

            for (GameObject objDentro : itemsToLoot) {
                response.append("-").append(objDentro.getName()).append("\n");
                model.getInventory().add(objDentro);
                container.getInsideItems().remove(objDentro);
            }

            // Se uno Zaino viene svuotato, lo facciamo sparire
            if (!(clickedObject instanceof Lockable)) {
                model.getCurrentRoom().removeObject(clickedObject);
            }

        } else {
            response.append("Non c'è niente dentro, è già vuoto.");

            // Se un oggetto già vuoto viene cliccato, sparisce solo se non è Lockable
            if (!(clickedObject instanceof Lockable)) {
                model.getCurrentRoom().removeObject(clickedObject);
            }
        }
    }
}
