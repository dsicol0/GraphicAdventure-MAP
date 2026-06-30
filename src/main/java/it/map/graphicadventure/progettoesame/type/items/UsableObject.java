/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type.items;

import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.interfaces.Usable;

/**
 * Classe concreta per gli oggetti statici d'interazione ambientale 
 * (es. terminali, leve, pulsanti) che non sono né armi né chiavi.
 */
public class UsableObject extends GameObject implements Usable {
    
    public UsableObject(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
    }
    
    @Override
    public boolean use(GameObject target) {
        // Per ora facciamo in modo che l'uso vada sempre a buon fine
        return true; 
    }
    
}
