package it.map.graphicadventure.progettoesame.impl;
import it.map.graphicadventure.progettoesame.GameDescription;
import it.map.graphicadventure.progettoesame.type.Player;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.type.GameObject;
// Importa anche eventuali tue sottoclassi come Weapon, ObjectContainer ecc.

import java.io.PrintStream;
import java.util.List;

public class EsameGame extends GameDescription {

    // Aggiungiamo il nostro Player alla struttura del prof
    private Player studente;

    @Override
    public void init() throws Exception {
        // 1. INIZIALIZZA IL GIOCATORE
        studente = new Player("Matricola Disperata", 100);

        // 3. CREAZIONE STANZE
        Room aulaStudio = new Room(1, "Aula Studio", "Sei circondato da appunti. La porta a NORD conduce all'atrio.");
        Room atrio = new Room(2, "Atrio Principale", "L'uscita è sbarrata. Il lettore badge è spento. C'è odore di bruciato.");

        // 4. COLLEGAMENTI (La Mappa)
        aulaStudio.setExit("nord", atrio);
        atrio.setExit("sud", aulaStudio);

        // 5. CREAZIONE OGGETTI
        //GameObject libretto = new GameObject(10, "Libretto", "Il tuo prezioso libretto. Potrebbe distrarre qualche prof...");
        //libretto.setTakeable(true);
        //aulaStudio.addObject(libretto);

        // 6. SETUP FINALE
        getRooms().add(aulaStudio);
        getRooms().add(atrio);
        setCurrentRoom(aulaStudio);
    }

    // Un getter per recuperare facilmente il giocatore durante la partita
    public Player getPlayer() {
        return studente;
    }

    @Override
    public List<GameObject> getInventory() {
        // Nota: se il tuo Player usa GameObject e il prof usa AdvObject,
        // dovrai assicurarti che combacino, altrimenti puoi tenere separati
        // gli inventari o adattare le classi.
        return super.getInventory();
    }

    @Override
    public String getWelcomeMsg() {
        return "La testa ti pulsa. Ti sei addormentato sul manuale di Java.\n" +
                "Guardi l'orologio: sono le 3:00 del mattino.\n" +
                "Dal corridoio senti un lamento: 'Dov'è il tuo libretttooooo...'\n" +
                "Devi uscire di qui. Ora.\n";
    }

    /*
    @Override
    public void nextMove(ParserOutput p, PrintStream out) {
        // Qui dentro andrà la logica degli Observer del professore.
        // Per ora stampiamo un messaggio di test.
        if (p.getCommand() != null) {
            out.println("Azione ricevuta: " + p.getCommand().getName());
        }
    }
     */
}