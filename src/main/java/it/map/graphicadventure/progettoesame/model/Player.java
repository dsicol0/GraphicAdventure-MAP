package it.map.graphicadventure.progettoesame.model;

public class Player {

    private String name;

    
    private int hp = 100;

    
    private final Inventory inventory;

    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }


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
        this.hp = hp;
    }
    
    public Inventory getInventory() {
        return inventory;
    }

    public boolean hasObject(int idObject) {
        return this.inventory.getList().stream().anyMatch(obj -> obj.getId() == idObject);
    }
    
}
