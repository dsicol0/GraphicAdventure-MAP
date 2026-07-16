/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller dedicato alla serializzazione logica dello stato del gioco.
 *
 * Questa classe funge da "ponte" (o Service) tra la struttura a grafo del mondo
 * di gioco ({@link EsameGame}) e lo stato di persistenza ({@link GameSaveDAO}).
 * Estrae i dati vitali dal Model, li converte in formati primitivi (o liste di
 * ID) e li passa al DAO per la scrittura su DB. Al contrario, in fase di
 * caricamento, effettua il ripristino di tutti gli oggetti nelle corrette
 * stanze o nell'inventario del giocatore.
 *
 */
public class SaveManager {

    private final GameSaveDAO saveDao;

    /**
     * Costruisce il gestore dei salvataggi.
     *
     * @param saveDao L'oggetto DAO già connesso al database.
     */
    public SaveManager(GameSaveDAO saveDao) {
        this.saveDao = saveDao;
    }

    /**
     * Estrae lo stato corrente (snapshot) del gioco e comanda al DAO di
     * salvarlo. Sfrutta le Stream API (Paradigma Funzionale) per trasformare la
     * lista di oggetti fisici nell'inventario in una semplice lista di stringhe
     * (ID).
     *
     * @param model L'istanza principale del motore di gioco.
     * @throws SQLException Se si verifica un problema di scrittura nel
     * database.
     */
    public void saveGame(EsameGame model) throws SQLException {
        if (model.getCurrentRoom() == null || model.getPlayer() == null) {
            return;
        }

        List<String> itemIds = model.getInventory().stream()
                .map(obj -> String.valueOf(obj.getId()))
                .collect(Collectors.toList());

        List<GameSaveDAO.ObjectSave> objStates = new ArrayList<>();
        for (Room room : model.getRooms()) {
            if (room.getObjects() != null) {
                for (GameObject obj : room.getObjects()) {
                    if (obj instanceof Lockable || obj instanceof Chest) {
                        boolean isLocked = false;
                        boolean isOpen = false;

                        if (obj instanceof Lockable lockableObj) {
                            isLocked = lockableObj.isLocked();
                        }
                        if (obj instanceof Chest chestObj) {
                            isLocked = chestObj.isLocked();
                            isOpen = !chestObj.isLocked();
                        }

                        objStates.add(new GameSaveDAO.ObjectSave(String.valueOf(obj.getId()), isLocked, isOpen));
                    }
                }
            }
        }

        saveDao.saveGame(
                model.getCurrentRoom().getName(),
                model.getPlayer().getHp(),
                itemIds,
                model.getDeadZombies(),
                model.getUnlockedRooms(),
                model.getTimeRemaining(),
                model.isPowerRestored(),
                objStates
        );
    }

    /**
     * Tenta di caricare l'ultimo salvataggio disponibile nel database e di
     * ripristinare lo stato del Model (inventario, vita, nemici, porte).
     *
     * @param model L'istanza del gioco da sovrascrivere con i dati vecchi.
     * @return {@code true} se il caricamento ha avuto successo, {@code false}
     * se non ci sono salvataggi o si è verificato un errore.
     */
    public boolean loadGame(EsameGame model) {
        SaveData data;
        try {
            data = saveDao.getLatestSave();
        } catch (SQLException e) {
            System.err.println("Error during save recovery: " + e.getMessage());
            return false;
        }

        if (data == null) {
            return false;
        }

        model.setTimeRemaining(data.getTimeRemaining());
        model.setPowerRestored(data.isPowerRestored());

        restoreHealth(model, data);
        restoreCurrentRoom(model, data);
        restoreInventory(model, data);
        restoreDeadZombies(model, data);

        restoreUnlockedRoomsAndFixes(model, data);
        restoreInteractiveObjects(model, data);

        return true;
    }

    private void restoreHealth(EsameGame model, SaveData data) {
        model.getPlayer().setHp(data.getHealth());
    }

    /**
     * Riposiziona il giocatore nella stanza corretta.
     */
    private void restoreCurrentRoom(EsameGame model, SaveData data) {
        model.getRooms().stream()
                .filter(room -> room.getName().trim().equalsIgnoreCase(data.getRoomName().trim()))
                .findFirst()
                .ifPresent(model::setCurrentRoom);
    }

    /**
     * Ricarica gli oggetti nell'inventario prelevandoli dal mondo di gioco (per
     * evitare duplicati).
     */
    private void restoreInventory(EsameGame model, SaveData data) {
        model.getInventory().clear();
        for (String itemId : data.getItemIds()) {
            GameObject foundObj = findAndRemoveItemFromWorld(model, itemId);
            if (foundObj != null) {
                model.getInventory().add(foundObj);
            }
        }
    }

    /**
     * Rimuove fisicamente dalla mappa i nemici che erano già stati uccisi in
     * precedenza.
     */
    private void restoreDeadZombies(EsameGame model, SaveData data) {
        model.getDeadZombies().clear();
        model.getDeadZombies().addAll(data.getKilledEnemyIds());

        for (String deadId : model.getDeadZombies()) {
            model.getRooms().forEach(room -> {
                if (room.getObjects() != null) {
                    room.getObjects().removeIf(obj -> String.valueOf(obj.getId()).equals(deadId));
                }
            });
        }
    }

    /**
     * Sblocca le porte e i contenitori che il giocatore aveva già aperto.
     */
    private void restoreUnlockedRoomsAndFixes(EsameGame model, SaveData data) {
        model.getUnlockedRooms().clear();
        if (data.getUnlockedRoomIds() != null) {
            model.getUnlockedRooms().addAll(data.getUnlockedRoomIds());
        }

        for (String idOrName : model.getUnlockedRooms()) {
            model.getRooms().forEach(room -> {
                if (String.valueOf(room.getId()).equals(idOrName) || room.getName().trim().equalsIgnoreCase(idOrName.trim())) {
                    room.setLocked(false);
                }
            });
        }

        applyMapSpecificFixes(model);
    }

    /**
     * Ripristina lo stato esatto (aperto/chiuso) dei forziere e degli oggetti
     * chiudibili
     */
    private void restoreInteractiveObjects(EsameGame model, SaveData data) {
        if (data.getObjectStates() == null) {
            return;
        }

        for (GameSaveDAO.ObjectSave savedObj : data.getObjectStates()) {
            for (Room room : model.getRooms()) {
                if (room.getObjects() != null) {
                    for (GameObject obj : room.getObjects()) {

                        if (String.valueOf(obj.getId()).equals(savedObj.getObjectId())) {
                            if (obj instanceof Lockable lockableObj) {
                                lockableObj.setLocked(savedObj.isLocked());
                            }
                            if (obj instanceof Chest chestObj) {
                                chestObj.setLocked(savedObj.isLocked());
                                if (!savedObj.isLocked()) {
                                    chestObj.open();
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    /**
     * Esegue controlli specifici (es. sblocco forzieri o chiavi nell'Aula 2).
     */
    private void applyMapSpecificFixes(EsameGame model) {
        boolean isAula2Unlocked = model.getUnlockedRooms().contains("2") || model.getUnlockedRooms().contains("Aula 2");
        boolean hasChipInInventory = model.getPlayer().hasObject(19);
        boolean powerRestored = model.isPowerRestored();

        for (Room room : model.getRooms()) {
            if (room.getObjects() != null) {

                room.getObjects().removeIf(obj -> {

                    if (isAula2Unlocked && (obj.getId() == 8 || obj.getId() == 13)) {
                        return true;
                    }

                    if (obj instanceof ObjectContainer && !(obj instanceof Lockable)) {

                        if (hasChipInInventory || powerRestored) {
                            return true;
                        }

                        ObjectContainer<?> container = (ObjectContainer<?>) obj;
                        return container.getInsideItems() == null || container.getInsideItems().isEmpty();
                    }
                    return false;
                });

                if (powerRestored && room.getId() == 1) {
                    room.setExit("EST", null);
                }
            }
        }
    }

    /**
     * Cerca un oggetto in tutta la mappa (o dentro i contenitori) e lo rimuove
     * fisicamente. Serve a garantire l'integrità: se un oggetto viene caricato
     * nell'inventario dal database, non deve più esistere a terra o nelle
     * casse. Utilizza un approccio iterativo classico per poter eseguire la
     * rimozione sicura senza iteratori.
     *
     * @param model Il motore di gioco.
     * @param itemId L'ID dell'oggetto da cercare e rimuovere.
     * @return L'oggetto trovato, oppure un'istanza generica se non era
     * fisicamente piazzato.
     */
    private GameObject findAndRemoveItemFromWorld(EsameGame model, String itemId) {
        for (Room room : model.getRooms()) {
            if (room.getObjects() == null) {
                continue;
            }

            for (int i = 0; i < room.getObjects().size(); i++) {
                GameObject obj = room.getObjects().get(i);

                if (String.valueOf(obj.getId()).equals(itemId)) {
                    return room.getObjects().remove(i);
                }

                if (obj instanceof ObjectContainer) {
                    ObjectContainer<?> container = (ObjectContainer<?>) obj;
                    if (container.getInsideItems() != null) {
                        for (int j = 0; j < container.getInsideItems().size(); j++) {
                            Object nested = container.getInsideItems().get(j);
                            if (nested instanceof GameObject && String.valueOf(((GameObject) nested).getId()).equals(itemId)) {
                                return (GameObject) container.getInsideItems().remove(j);
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
