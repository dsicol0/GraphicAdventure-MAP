/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model;
import java.util.Random;

/**
 *
 * @author David
 */
public class Zombie extends GameObject {
    
    private int life;
    private int damage;
    
    public Zombie(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        
        
        Random rand = new Random();
        // Vita casuale tra 60 e 100
        this.life = rand.nextInt(61) + 50;
        // Danno casuale tra 15 e 25
        this.damage = rand.nextInt(25) + 15;
    }

    public int getLife() { return life; }
    public void setLife(int life) { this.life = life; }

    public int getDamage() { return damage; }
    
    public void setDamage(int damage) { this.damage = damage; }
    
    
    public void takeDamage(int damageTaken) {
        this.life -= damageTaken;
        if (this.life < 0) {
            this.life = 0; 
        }
    }
    
    public boolean isDead() {
        return this.life == 0;
    }
}
