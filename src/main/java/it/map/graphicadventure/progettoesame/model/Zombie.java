/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model;
import java.util.Random;

/**
 * Rappresenta un nemico (NPC ostile) all'interno del gioco.
 *
 * Questa classe estende {@link GameObject}, ereditandone le proprietà fisiche e di 
 * base (nome, descrizione, ID). Aggiunge attributi specifici per il combattimento 
 * (vita e danno) che vengono inizializzati in modo dinamico sfruttando la classe 
 * di Java, Random, per garantire imprevedibilità.
 *
 */
public class Zombie extends GameObject {
    
    private int life;
    private int damage;
    
    /**
     * Costruisce un nuovo nemico di tipo Zombie.
     * Le sue statistiche (vita e danni) non sono fisse, ma vengono calcolate 
     * in modo pseudo-casuale al momento dell'istanziazione.
     *
     * @param id L'identificativo univoco dello zombie.
     * @param name Il nome del nemico.
     * @param description La descrizione visibile al giocatore.
     * @param imagePath Il percorso dell'immagine (sprite) associata.
     */
    public Zombie(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        
        Random rand = new Random();
        // Genera una quantità di vita casuale (da 50 a 110)
        this.life = rand.nextInt(61) + 50;
        // Genera un ammontare di danno casuale (da 15 a 39)
        this.damage = rand.nextInt(25) + 15;
    }

    public int getLife() { return life; }
    public void setLife(int life) { this.life = life; }

    public int getDamage() { return damage; }
    
    public void setDamage(int damage) { this.damage = damage; }
    
    /**
     * Riduce i punti vita dello zombie a seguito di un attacco.
     * Implementa un boundary check per assicurarsi che i punti vita 
     * non scendano mai sotto lo zero, evitando bug logici nel sistema di combattimento.
     *
     * @param damageTaken La quantità di danno subita dall'attacco del giocatore.
     */
    public void takeDamage(int damageTaken) {
        this.life -= damageTaken;
        if (this.life < 0) {
            this.life = 0; 
        }
    }
    
    /**
     * Verifica se lo zombie è stato definitivamente sconfitto.
     *
     * @return {@code true} se la vita è scesa a 0, {@code false} se è ancora in vita.
     */
    public boolean isDead() {
        return this.life == 0;
    }
}
