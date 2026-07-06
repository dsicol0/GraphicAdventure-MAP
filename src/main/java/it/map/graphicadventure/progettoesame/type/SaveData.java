/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type;

import java.util.List;

/**
 *
 * @author antoniostilla
 */
public class SaveData {
    private String roomName;
    private int health;
    private List<String> itemIds;
    private List<String> killedEnemyIds;

    public SaveData(String roomName, int health, List<String> itemIds, List<String> killedEnemyIds) {
        this.roomName = roomName;
        this.health = health;
        this.itemIds = itemIds;
        this.killedEnemyIds = killedEnemyIds;
    }

    public String getRoomName() { return roomName; }
    public int getHealth() { return health; }
    public List<String> getItemIds() { return itemIds; }
    public List<String> getKilledEnemyIds() { return killedEnemyIds; }
}
