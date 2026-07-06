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

    public SaveData(String roomName, int health, List<String> itemIds) {
        this.roomName = roomName;
        this.health = health;
        this.itemIds = itemIds;
    }

    public String getRoomName() { return roomName; }
    public int getHealth() { return health; }
    public List<String> getItemIds() { return itemIds; }
}
