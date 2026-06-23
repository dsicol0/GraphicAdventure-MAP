/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type;
import java.util.Random;

/**
 *
 * @author David
 */
public class GameNPC extends GameObject {
    
    private int life;
    private int damage;
    
    public GameNPC(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        
        // 2. Generazione della vita casuale
        Random rand = new Random();
        // Vita casuale tra 50 e 100
        this.life = rand.nextInt(51) + 50;
        // Danno casuale tra 15 e 35
        this.damage = rand.nextInt(21) + 15;
    }

    // --- Getter e Setter ---

    public int getLife() { return life; }
    public void setLife(int life) { this.life = life; }

    public int getDamage() { return damage; }
    // Di solito il danno base non si cambia, ma puoi mettere il setter se prevedi dei "malus" o "potenziamenti" per i nemici
    public void setDamage(int damage) { this.damage = damage; }
    
    // Metodo per far subire danni all'NPC
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
