/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.service;

import it.map.graphicadventure.progettoesame.impl.EsameGame;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.SaveData;
import it.map.graphicadventure.progettoesame.model.interfaces.Lockable;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;
import it.map.graphicadventure.progettoesame.model.interfaces.Openable;
import it.map.graphicadventure.progettoesame.model.items.Chest;
import it.map.graphicadventure.progettoesame.model.items.Chip;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author antoniostilla
 */
public class SaveManager {

    private final GameSaveDAO saveDao;

    public SaveManager(GameSaveDAO saveDao) {
        this.saveDao = saveDao;
    }

    /**
     * Salva lo stato attuale del gioco nel database.
     */
    public void saveGame(EsameGame model) throws SQLException {
        if (model.getCurrentRoom() == null || model.getPlayer() == null) {
            return;
        }

        // 1. Estrazione ID tramite Stream (1 riga invece di 4)
        List<String> itemIds = model.getInventory().stream()
                .map(obj -> String.valueOf(obj.getId()))
                .collect(Collectors.toList());

        // 2. Salvataggio
        saveDao.saveGame(
                model.getCurrentRoom().getName(), 
                model.getPlayer().getHp(), 
                itemIds, 
                model.getDeadZombies(), 
                model.getUnlockedRooms(),
                model.getTimeRemaining()
        );
    }

    /**
     * Tenta di caricare l'ultimo salvataggio e di ripristinare lo stato del Model.
     * Restituisce true se il caricamento ha avuto successo, false altrimenti.
     */
    public boolean loadGame(EsameGame model) {
        SaveData data;
        try {
            data = saveDao.getLatestSave();
        } catch (SQLException e) {
            System.err.println("Errore durante il recupero del salvataggio: " + e.getMessage());
            return false;
        }

        if (data == null) {
            return false; // Nessun salvataggio trovato
        }

        // MVC e CLEAN CODE: Il metodo ora è una lista di passaggi chiari e leggibili!
        restoreHealth(model, data);
        restoreCurrentRoom(model, data);
        restoreInventory(model, data);
        restoreDeadZombies(model, data);
        restoreUnlockedRoomsAndFixes(model, data);
        
        model.setTimeRemaining(data.getTimeRemaining());

        return true;
    }
    
    // ==========================================
    // METODI PRIVATI DI SUPPORTO (SRP applicato)
    // ==========================================

    private void restoreHealth(EsameGame model, SaveData data) {
        model.getPlayer().setHp(data.getHealth());
    }

    private void restoreCurrentRoom(EsameGame model, SaveData data) {
        model.getRooms().stream()
                .filter(r -> r.getName().trim().equalsIgnoreCase(data.getRoomName().trim()))
                .findFirst()
                .ifPresent(model::setCurrentRoom); // Imposta la stanza se la trova
    }

    private void restoreInventory(EsameGame model, SaveData data) {
        model.getInventory().clear();
        for (String itemId : data.getItemIds()) {
            GameObject foundObj = findAndRemoveItemFromWorld(model, itemId);
            if (foundObj != null) {
                model.getInventory().add(foundObj);
            }
        }
    }

    private void restoreDeadZombies(EsameGame model, SaveData data) {
        model.getDeadZombies().clear();
        model.getDeadZombies().addAll(data.getKilledEnemyIds());
        
        for (String deadId : model.getDeadZombies()) {
            model.getRooms().forEach(r -> {
                if (r.getObjects() != null) {
                    r.getObjects().removeIf(obj -> String.valueOf(obj.getId()).equals(deadId));
                }
            });
        }
    }

    private void restoreUnlockedRoomsAndFixes(EsameGame model, SaveData data) {
        model.getUnlockedRooms().clear();
        if (data.getUnlockedRoomIds() != null) {
            model.getUnlockedRooms().addAll(data.getUnlockedRoomIds());
        }

        // 1. Sblocca le stanze salvate
        for (String idOrName : model.getUnlockedRooms()) {
            model.getRooms().forEach(r -> {
                if (String.valueOf(r.getId()).equals(idOrName) || r.getName().trim().equalsIgnoreCase(idOrName.trim())) {
                    r.setLocked(false);
                }
            });
        }

        // 2. Fix specifici della mappa (Nascondere porte vecchie o zaini aperti)
        applyMapSpecificFixes(model, data);
    }

    private void applyMapSpecificFixes(EsameGame model, SaveData data) {
        boolean aula2Sbloccata = model.getUnlockedRooms().contains("2") || model.getUnlockedRooms().contains("Aula 2");

        
        boolean hasChipInInventory = model.getPlayer().hasObject(19);

        // Giriamo tra le stanze con un ciclo classico
        for (Room r : model.getRooms()) {
            if (r.getObjects() != null) {

                // 🛑 FASE 1: LE RIMOZIONI
                r.getObjects().removeIf(obj -> {
                    // Se l'aula è aperta, via la porta chiusa (ID 8)
                    if (aula2Sbloccata && obj.getId() == 8) {
                        return true;
                    }

                    // Se lo zaino è un ObjectContainer senza serratura
                    if (obj instanceof ObjectContainer && !(obj instanceof Lockable)) {
                        // Usiamo direttamente la tua variabile originale senza errori!
                        if (hasChipInInventory) {
                            return true;
                        }
                        
                        ObjectContainer<?> container = (ObjectContainer<?>) obj;
                        return container.getInsideItems() == null || container.getInsideItems().isEmpty();
                    }
                    return false;
                });

                // 🧰 FASE 2: LE MODIFICHE (Per la Chest)
                for (GameObject obj : r.getObjects()) {
                    if (obj instanceof Chest) {
                        Chest f = (Chest) obj;

                        if (f.getInsideItems() == null || f.getInsideItems().isEmpty() || hasChipInInventory) {
                            f.setLocked(false);
                            f.open();
                        }
                    }
                }

            }
        }
    }
    
    private boolean hasChip(EsameGame model) {
        if (model.getInventory() != null) {
            for (GameObject item : model.getInventory()) {
                if (item instanceof Chip) {
                    return true; // Trovato, esce immediatamente restituendo true
                }
            }
        }
        return false; // Se il ciclo finisce senza trovare nulla
    }

    /**
     * Ricerca complessa degli oggetti nel mondo, anche dentro i contenitori.
     */
    private GameObject findAndRemoveItemFromWorld(EsameGame model, String itemId) {
        for (Room r : model.getRooms()) {
            if (r.getObjects() == null) continue;

            for (int i = 0; i < r.getObjects().size(); i++) {
                GameObject obj = r.getObjects().get(i);

                // Caso A: L'oggetto è per terra
                if (String.valueOf(obj.getId()).equals(itemId)) {
                    return r.getObjects().remove(i);
                }

                // Caso B: L'oggetto è nascosto in un contenitore
                if (obj instanceof ObjectContainer) {
                    ObjectContainer<?> container = (ObjectContainer<?>) obj;
                    if (container.getInsideItems() != null) {
                        for (int j = 0; j < container.getInsideItems().size(); j++) {
                            Object nested = container.getInsideItems().get(j);
                            if (nested instanceof GameObject && String.valueOf(((GameObject) nested).getId()).equals(itemId)) {
                                return (GameObject) container.getInsideItems().remove(j); // Rimuove e restituisce l'oggetto in un colpo solo
                            }
                        }
                    }
                }
            }
        }

        // Fallback: cerca negli oggetti globali
        return model.getAllObjects().stream()
                .filter(obj -> String.valueOf(obj.getId()).equals(itemId))
                .findFirst()
                .orElse(null);
    }
}
