package it.map.graphicadventure.progettoesame.factory;

import it.map.graphicadventure.progettoesame.model.Zombie;
import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.Room;
import it.map.graphicadventure.progettoesame.model.items.Chest;
import it.map.graphicadventure.progettoesame.model.items.Chip;
import it.map.graphicadventure.progettoesame.model.items.ElectricPanel;
import it.map.graphicadventure.progettoesame.model.items.Food;
import it.map.graphicadventure.progettoesame.model.items.Key;
import it.map.graphicadventure.progettoesame.model.items.ObjectContainer;
import it.map.graphicadventure.progettoesame.model.items.Weapon;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileMapParser {

    public static List<Room> loadMapFromFile(String filePath) throws IOException {
        List<Room> rooms = new ArrayList<>();
        Map<Integer, Room> roomMap = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            String currentSection = "";

            while ((line = br.readLine()) != null) {
                line = line.trim();

                // Salta righe vuote e commenti
                if (line.isEmpty() || line.startsWith("#")) continue;

                // Cambio di sezione
                if (line.startsWith("[") && line.endsWith("]")) {
                    currentSection = line.toUpperCase();
                    continue;
                }

                // Deleghiamo il parsing della singola riga al metodo dedicato
                processLine(currentSection, line, rooms, roomMap);
            }
        }
        return rooms;
    }
    
    // ==========================================
    // METODI PRIVATI DI SUPPORTO (SRP & CLEAN CODE)
    // ==========================================

    private static void processLine(String section, String line, List<Room> rooms, Map<Integer, Room> roomMap) {
        switch (section) {
            case "[ROOMS]":
                parseRoom(line, rooms, roomMap);
                break;
            case "[EXITS]":
                parseExit(line, roomMap);
                break;
            case "[OBJECTS]":
                parseObject(line, roomMap);
                break;
        }
    }

    private static void parseRoom(String line, List<Room> rooms, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";");
        if (parts.length < 3) return; // Guard clause

        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String description = parts[2].trim();
        String backgroundPath = parts.length >= 4 ? parts[3].trim() : "";

        Room room = new Room(id, name, description, backgroundPath);
        rooms.add(room);
        roomMap.put(id, room);
    }

    private static void parseExit(String line, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";", 3);
        if (parts.length < 3) return; // Guard clause

        int fromId = Integer.parseInt(parts[0].trim());
        String direction = parts[1].trim();
        int toId = Integer.parseInt(parts[2].trim());

        Room fromRoom = roomMap.get(fromId);
        Room toRoom = roomMap.get(toId);

        if (fromRoom != null && toRoom != null) {
            fromRoom.setExit(direction, toRoom);
        }
    }

    private static void parseObject(String line, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";");
        if (parts.length < 10) return; // Guard clause

        int roomId = Integer.parseInt(parts[0].trim());
        Room targetRoom = roomMap.get(roomId);
        if (targetRoom == null) return; // La stanza deve esistere

        int objId = Integer.parseInt(parts[1].trim());
        String objType = parts[2].trim();
        String objName = parts[3].trim();
        String objDesc = parts[4].trim();
        String objImg = parts[5].trim();
        int x = Integer.parseInt(parts[6].trim());
        int y = Integer.parseInt(parts[7].trim());
        int width = Integer.parseInt(parts[8].trim());
        int height = Integer.parseInt(parts[9].trim());

        // Deleghiamo la creazione fisica dell'oggetto ad un Factory Method
        GameObject newObj = createObjectInstance(objType, objId, objName, objDesc, objImg, parts);

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

    /**
     * Factory Method: si occupa solo di istanziare la classe giusta in base alla stringa.
     */
    private static GameObject createObjectInstance(String type, int id, String name, String desc, String img, String[] parts) {
        switch (type.trim()) {
            case "ObjectContainer":
                return new ObjectContainer<>(id, name, desc, img);
            case "Key":
                return new Key(id, name, desc, img);
            case "Weapon":
                int damage = parts.length >= 11 ? Integer.parseInt(parts[10].trim()) : 10;
                return new Weapon(id, name, desc, img, damage);
            case "Chest":
                int requiredKey = Integer.parseInt(parts[10].trim());
                return new Chest<>(id, name, desc, img, requiredKey);
            case "Zombie":
                return new Zombie(id, name, desc, img);
            case "ElectricPanel":
                return new ElectricPanel(id, name, desc, img);
            case "Chip":
                return new Chip(id, name, desc, img);
            case "Food":
                int healAmount = parts.length >= 11 ? Integer.parseInt(parts[10].trim()) : 20;
                return new Food(id, name, desc, img, healAmount);
            default:
                return null;
        }
    }

}