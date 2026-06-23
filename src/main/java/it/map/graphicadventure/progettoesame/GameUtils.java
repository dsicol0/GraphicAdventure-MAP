package it.map.graphicadventure.progettoesame;

import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.Room;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameUtils {

    public static List<Room> loadMapFromFile(String filePath) throws IOException {
        List<Room> rooms = new ArrayList<>();
        Map<Integer, Room> roomMap = new HashMap<>();

        // Il try-with-resources garantisce la chiusura del file anche in caso di eccezioni
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSection = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Salta le righe vuote e i commenti
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                // Identifica il cambio di sezione
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.toUpperCase();
                    continue;
                }

                // Elabora la riga in base alla sezione in cui si trova
                switch (currentSection) {
                    case "[ROOMS]":
                        // Formato atteso: id;nome;descrizione
                        String[] roomParts = line.split(";", 3);
                        if (roomParts.length == 3) {
                            int id = Integer.parseInt(roomParts[0].trim());
                            String name = roomParts[1].trim();
                            String description = roomParts[2].trim();

                            Room room = new Room(id, name, description);
                            rooms.add(room);
                            roomMap.put(id, room); // Mappa di supporto per i collegamenti successivi
                        }
                        break;

                    case "[EXITS]":
                        // Formato atteso: idStanzaPartenza;direzione;idStanzaDestinazione
                        String[] exitParts = line.split(";", 3);
                        if (exitParts.length == 3) {
                            int fromId = Integer.parseInt(exitParts[0].trim());
                            String direction = exitParts[1].trim();
                            int toId = Integer.parseInt(exitParts[2].trim());

                            Room fromRoom = roomMap.get(fromId);
                            Room toRoom = roomMap.get(toId);

                            // Se entrambe le stanze esistono, crea il collegamento bivalente/monovalente
                            if (fromRoom != null && toRoom != null) {
                                fromRoom.setExit(direction, toRoom);
                            }
                        }
                        break;
                }
            }
        }
        return rooms;
    }

    public static GameObject getObjectFromInventory(List<GameObject> inventory, int id) {
        return inventory.stream()
                .filter(o -> o.getId() == id)
                .findFirst()
                .orElse(null);
    }

}