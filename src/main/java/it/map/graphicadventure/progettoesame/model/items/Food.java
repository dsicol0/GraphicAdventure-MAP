/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Player;
import it.map.graphicadventure.progettoesame.model.interfaces.Healable;
import it.map.graphicadventure.progettoesame.model.interfaces.Takeable;

/**
 *
 * @author David
 */
public class Food extends GameObject implements Healable, Takeable{
    
    private final int healAmount;
    private boolean takeable = true;
    
    public Food(int id, String name, String description, String pathImage, int healAmount) {
        super(id, name, description, pathImage);
        this.healAmount = healAmount;
    }
    
    @Override
    public void heal(Player player) {
        int newHp = player.getHp() + this.healAmount;
        // Limite massimo standard a 100 HP
        if (newHp > 100) {
            newHp = 100;
        }
        
        player.setHp(newHp);
    }

    @Override
    public int getHealAmount() {
        return this.healAmount;
    }

    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }
    
}
