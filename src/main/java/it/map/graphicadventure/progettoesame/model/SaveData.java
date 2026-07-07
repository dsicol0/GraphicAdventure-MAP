/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model;

import it.map.graphicadventure.progettoesame.service.GameSaveDAO;
import java.util.List;

/**
 *
 * @author antoniostilla
 */
public class SaveData {
    private String currentRoom;
    private int health;
    private List<String> itemIds;
    private List<String> killedEnemyIds;
    private final List<String> unlockedRoomIds; 

    // 🟩 2. AGGIORNA IL COSTRUTTORE PER ACCETTARE IL QUINTO PARAMETRO
    public SaveData(String currentRoom, int health, List<String> itemIds, List<String> killedEnemyIds, List<String> unlockedRoomIds) {
        this.currentRoom = currentRoom;
        this.health = health;
        this.itemIds = itemIds;
        this.killedEnemyIds = killedEnemyIds;
        this.unlockedRoomIds = unlockedRoomIds;
    }

    public String getRoomName() { return currentRoom; }
    public int getHealth() { return health; }
    public List<String> getItemIds() { return itemIds; }
    public List<String> getKilledEnemyIds() { return killedEnemyIds; }
    
    
    public List<String> getUnlockedRoomIds() {
        return unlockedRoomIds;
    }
}
