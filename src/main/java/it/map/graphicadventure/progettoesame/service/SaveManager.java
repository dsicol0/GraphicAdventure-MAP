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

        
        List<String> itemIds = model.getInventory().stream()
                .map(obj -> String.valueOf(obj.getId()))
                .collect(Collectors.toList());

        
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
            return false;
        }

        
        restoreHealth(model, data);
        restoreCurrentRoom(model, data);
        restoreInventory(model, data);
        restoreDeadZombies(model, data);
        restoreUnlockedRoomsAndFixes(model, data);
        
        model.setTimeRemaining(data.getTimeRemaining());

        return true;
    }
    
    
    private void restoreHealth(EsameGame model, SaveData data) {
        model.getPlayer().setHp(data.getHealth());
    }

    private void restoreCurrentRoom(EsameGame model, SaveData data) {
        model.getRooms().stream()
                .filter(r -> r.getName().trim().equalsIgnoreCase(data.getRoomName().trim()))
                .findFirst()
                .ifPresent(model::setCurrentRoom);
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

        
        for (String idOrName : model.getUnlockedRooms()) {
            model.getRooms().forEach(r -> {
                if (String.valueOf(r.getId()).equals(idOrName) || r.getName().trim().equalsIgnoreCase(idOrName.trim())) {
                    r.setLocked(false);
                }
            });
        }

        
        applyMapSpecificFixes(model, data);
    }

    private void applyMapSpecificFixes(EsameGame model, SaveData data) {
        boolean aula2Sbloccata = model.getUnlockedRooms().contains("2") || model.getUnlockedRooms().contains("Aula 2");

        
        boolean hasChipInInventory = model.getPlayer().hasObject(19);

        
        for (Room r : model.getRooms()) {
            if (r.getObjects() != null) {

                
                r.getObjects().removeIf(obj -> {
                    
                    if (aula2Sbloccata && obj.getId() == 8) {
                        return true;
                    }

                    
                    if (obj instanceof ObjectContainer && !(obj instanceof Lockable)) {
                        
                        if (hasChipInInventory) {
                            return true;
                        }
                        
                        ObjectContainer<?> container = (ObjectContainer<?>) obj;
                        return container.getInsideItems() == null || container.getInsideItems().isEmpty();
                    }
                    return false;
                });

                
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
                    return true;
                }
            }
        }
        return false;
    }

    
    private GameObject findAndRemoveItemFromWorld(EsameGame model, String itemId) {
        for (Room r : model.getRooms()) {
            if (r.getObjects() == null) continue;

            for (int i = 0; i < r.getObjects().size(); i++) {
                GameObject obj = r.getObjects().get(i);

                
                if (String.valueOf(obj.getId()).equals(itemId)) {
                    return r.getObjects().remove(i);
                }

                
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

        
        return model.getAllObjects().stream()
                .filter(obj -> String.valueOf(obj.getId()).equals(itemId))
                .findFirst()
                .orElse(null);
    }
}
