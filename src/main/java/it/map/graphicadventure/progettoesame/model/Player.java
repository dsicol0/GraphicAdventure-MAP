package it.map.graphicadventure.progettoesame.model;

import java.util.List;

public class Player {

    private String name;

    // Gestione della salute per l'aspetto "Survival"
    private int hp = 100;

    // Inventario personale del giocatore
    private final Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }

    // =================================
    //      GETTER & SETTER BASE
    // =================================
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        // Evitiamo che la vita superi il massimo consentito
        this.hp = hp;
    }
    
    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasObject(int idObject) {
        return this.inventory.getList().stream().anyMatch(obj -> obj.getId() == idObject);
    }
    
}
