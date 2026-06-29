package it.map.graphicadventure.progettoesame.impl;
import it.map.graphicadventure.progettoesame.GameDescription;
import it.map.graphicadventure.progettoesame.GameUtils;
import it.map.graphicadventure.progettoesame.type.Player;
import it.map.graphicadventure.progettoesame.type.Room;
import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.items.ObjectContainer;

import java.io.PrintStream;
import java.util.List;

public class EsameGame extends GameDescription {

    // Aggiungiamo il nostro Player alla struttura del prof
    private Player player;

    @Override
    public void init() throws Exception {
        // 1. INIZIALIZZA IL GIOCATORE
        player = new Player("Matricola Disperata", 100);

        // 2. CARICAMENTO DINAMICO DA FILE
        String pathMappa = "src/main/resources/map/map.txt";
        List<Room> stanzeCaricate = GameUtils.loadMapFromFile(pathMappa);

        // REQUISITO LAMBDA: Usiamo il forEach per aggiungere tutte le stanze caricate
        stanzeCaricate.forEach(room -> getRooms().add(room));

        // REQUISITO STREAM & PIPELINE: Cerchiamo la stanza iniziale (Aula Studio con ID 1)
        Room stanzaIniziale = getRooms().stream()
                .filter(room -> room.getId() == 1)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Errore critico: Aula Studio (ID 1) non trovata nel file di configurazione!"));

       System.out.println("[DEBUG INIT] Oggetti nell'Aula Studio: " + stanzaIniziale.getObjects().size());        
        // Imposta la stanza corrente
        setCurrentRoom(stanzaIniziale);
    }

    // Un getter per recuperare facilmente il giocatore durante la partita
    public Player getPlayer() {
        return player;
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