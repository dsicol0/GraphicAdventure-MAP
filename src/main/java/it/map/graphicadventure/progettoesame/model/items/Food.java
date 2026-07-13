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
 * Rappresenta un oggetto consumabile all'interno del gioco (es. una porzione di cibo o un medikit).
 * Estende la classe base {@link GameObject} per far parte della mappa e implementa 
 * le interfacce {@link Healable} (fornisce cure) e {@link Takeable} (può finire nello zaino).
 *
 */
public class Food extends GameObject implements Healable, Takeable{
    
    private final int healAmount;
    private boolean takeable = true;
    
    /**
     * Costruisce un nuovo oggetto curativo.
     * * @param id L'identificativo univoco dell'oggetto.
     * @param name Il nome del consumabile (es. "Mela", "Barretta energetica").
     * @param description La descrizione testuale dell'oggetto.
     * @param pathImage Il percorso del file immagine da mostrare nell'interfaccia.
     * @param healAmount La quantità di punti vita (HP) ripristinati al momento dell'uso.
     */
    public Food(int id, String name, String description, String pathImage, int healAmount) {
        super(id, name, description, pathImage);
        this.healAmount = healAmount;
    }
    
    /**
     * Applica l'effetto curativo al giocatore specificato.
     * La logica implementa un controllo dei limiti (boundary check) per garantire 
     * che la salute del giocatore non superi mai il tetto massimo consentito (100 HP).
     *
     * @param player L'entità del giocatore che deve essere curata.
     */
    @Override
    public void heal(Player player) {
        int newHp = player.getHp() + this.healAmount;
        
        if (newHp > 100) {
            newHp = 100;
        }
        
        player.setHp(newHp);
    }

    /**
     * Restituisce l'ammontare di salute che questo oggetto è in grado di curare.
     * * @return Il valore numerico degli HP ripristinati.
     */
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
