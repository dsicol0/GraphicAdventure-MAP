/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.type.items;

import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.interfaces.Takeable;
import it.map.graphicadventure.progettoesame.type.interfaces.Usable;

/**
 *
 * @author antoniostilla
 */

/**
 * Il badge del Direttore. Oggetto obiettivo finale del gioco.
 * Una volta raccolto, permette di sbloccare la saracinesca di uscita.
 */
public class Badge extends GameObject implements Takeable, Usable {

    private boolean takeable;

    public Badge(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        // Il badge si può sempre raccogliere una volta trovato
        this.takeable = true;
    }

    // --- Implementazione di Takeable ---

    @Override
    public boolean isTakeable() {
        return this.takeable;
    }

    @Override
    public void setTakeable(boolean takeable) {
        this.takeable = takeable;
    }

    // --- Implementazione di Usable (Condizione di Vittoria) ---

    @Override
    public boolean use(GameObject target) {
        
        // Supponiamo che il lettore della saracinesca sia un GameObject 
        // con un nome specifico, o magari hai creato una classe 'Scanner' apposita.
        if (target != null && target.getName().equalsIgnoreCase("Lettore Badge")) {
            
            System.out.println("Strisci il Badge del Direttore con le mani che tremano...");
            System.out.println("*BEEP*");
            System.out.println("La spia diventa verde! La saracinesca metallica si alza cigolando.");
            System.out.println("Sei fuori. Sei salvo. E hai anche salvato il tuo libretto.");
            
            // Qui potrai inserire la logica per terminare il gioco, 
            // ad esempio lanciare un evento di vittoria al GameController
            // o salvare il punteggio nel Database JDBC.
            
            return true; // Uso riuscito (Vittoria)
            
        } else if (target instanceof Chest) {
            System.out.println("Questo è un badge magnetico, non apre i bauli fisici.");
            return false;
        }

        System.out.println("Non ha senso usare il badge su questo oggetto.");
        return false;
    }
}
