/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.interfaces;

import it.map.graphicadventure.progettoesame.model.Player;

/**
 *
 * @author David
 */
public interface Healable {
    
    void heal(Player player);
    
    int getHealAmount();
}
