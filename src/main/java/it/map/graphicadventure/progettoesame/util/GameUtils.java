package it.map.graphicadventure.progettoesame.util;

import it.map.graphicadventure.progettoesame.model.GameNPC;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.items.Chest;
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;
import it.map.graphicadventure.progettoesame.model.items.UsableObject;
import it.map.graphicadventure.progettoesame.model.items.Weapon;

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
                        String[] parts = line.split(";");
                        if (parts.length >= 3) {
                            int id = Integer.parseInt(parts[0].trim());
                            String name = parts[1].trim();
                            String description = parts[2].trim();
                            
                            // Controlliamo se nella riga del file c'è anche il quarto parametro
                            String backgroundPath = "";
                            if (parts.length >= 4) {
                                backgroundPath = parts[3].trim();
                            }
                            
                            Room room = new Room(id, name, description, backgroundPath);
                            
                            rooms.add(room);
                            roomMap.put(id, room);
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
                    case "[OBJECTS]":
                        // Formato: IdStanza;IdOggetto;TipoClasse;Nome;Descrizione;PathImmagine;X;Y;Larghezza;Altezza
                        String[] objParts = line.split(";");
                        if (objParts.length >= 10) {
                            int roomId = Integer.parseInt(objParts[0].trim());
                            int objId = Integer.parseInt(objParts[1].trim());
                            String objType = objParts[2].trim();
                            String objName = objParts[3].trim();
                            String objDesc = objParts[4].trim();
                            String objImg = objParts[5].trim();
                            int x = Integer.parseInt(objParts[6].trim());
                            int y = Integer.parseInt(objParts[7].trim());
                            int width = Integer.parseInt(objParts[8].trim());
                            int height = Integer.parseInt(objParts[9].trim());

                            // 1. Troviamo a quale stanza aggiungere l'oggetto
                            Room targetRoom = roomMap.get(roomId);
                            
                            if (targetRoom != null) {
                                GameObject newObj = null;
                                
                                System.out.println("[DEBUG] Provo a creare l'oggetto di tipo: [" + objType + "]");
                                // 2. Instanziamo la classe corretta in base al tipo (Factory)
                                switch (objType.trim()) {
                                    case "ObjectContainer":
                                        newObj = new ObjectContainer(objId, objName, objDesc, objImg);
                                        System.out.println("[DEBUG] Baule creato con successo!");
                                        break;
                                    case "Key":
                                        newObj = new Key(objId, objName, objDesc, objImg);
                                        break;
                                    case "Weapon":
                                        // Come default un'arma fa almeno 10 danni
                                        int damage = 10;
                                        // Se la riga ha l'11° parametro (indice 10), leggiamo il danno dal file
                                        if (objParts.length >= 11) {
                                            damage = Integer.parseInt(objParts[10].trim());
                                        }
                                        // Istanziamo l'ogetto di tipo arma con il relativo danno
                                        newObj = new Weapon(objId, objName, objDesc, objImg, damage);
                                        break;
                                    case "Chest": 
                                        int requiredKey = Integer.parseInt(objParts[10].trim());
                                        newObj = new Chest<>(objId, objName, objDesc, objImg, requiredKey);
                                        break;
                                        
                                    // 🟩 REGOLA AGGIUNTA PER IL NEMICO!
                                    case "GameNPC":
                                        newObj = new GameNPC(objId, objName, objDesc, objImg);
                                        break;
                                }

                                // 3. Se l'oggetto è stato creato, applichiamo le coordinate e lo salviamo nella stanza
                                if (newObj != null) {
                                    newObj.setX(x);
                                    newObj.setY(y);
                                    newObj.setWidth(width);
                                    newObj.setHeight(height);
                                    
                                    targetRoom.addObject(newObj);
                                } else {
                                     System.err.println("Impossibile creare l'oggetto: tipo '" + objType + "' non riconosciuto.");
                                }
                            }
                        }
                        break;   
                }
            }
        }
        return rooms;
    }

    public static boolean hasObject(List<GameObject> inventory, int idObject) {
        if (inventory == null) return false;
        
        return inventory.stream().anyMatch(obj -> obj.getId() == idObject);
    }

}