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

/**
 * Classe di utilità per caricare la mappa di gioco da un file di testo.
 * Analizza il file riga per riga e istanzia le stanze, i collegamenti (uscite) 
 * e gli oggetti fisici all'interno del mondo.
 */
public class FileMapParser {

    /**
     * Legge il file di configurazione della mappa e costruisce le stanze.
     * Utilizza un BufferedReader in un blocco try-with-resources per garantire 
     * la chiusura automatica dello stream alla fine della lettura.
     *
     * @param filePath Il percorso del file di testo da leggere (es. "src/.../map.txt").
     * @return La lista completa delle stanze generate e popolate.
     * @throws IOException Se si verifica un errore di lettura o se il file non viene trovato.
     */
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

                processLine(currentSection, line, rooms, roomMap);
            }
        }
        return rooms;
    }

    /**
     * Smista la riga letta al metodo di parsing corretto in base alla sezione
     * attuale del file (es. stanze, uscite o oggetti).
     *
     * @param section La sezione corrente del file (es. "[ROOMS]").
     * @param line La riga di testo da analizzare.
     * @param rooms La lista globale delle stanze.
     * @param roomMap Una mappa di supporto per trovare velocemente le stanze tramite il loro ID.
     */
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

    /**
     * Crea un'istanza di Room partendo da una riga di testo delimitata da punto e virgola.
     *
     * @param line La riga contenente i dati della stanza.
     * @param rooms La lista in cui aggiungere la nuova stanza.
     * @param roomMap La mappa in cui registrare la stanza usando l'ID come chiave.
     */
    private static void parseRoom(String line, List<Room> rooms, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";");
        if (parts.length < 3) return;

        int id = Integer.parseInt(parts[0].trim());
        String name = parts[1].trim();
        String description = parts[2].trim();
        String backgroundPath = parts.length >= 4 ? parts[3].trim() : "";

        Room room = new Room(id, name, description, backgroundPath);
        rooms.add(room);
        roomMap.put(id, room);
    }

    /**
     * Collega due stanze tra loro impostando le uscite (es. da stanza 1 a est vai a stanza 2).
     *
     * @param line La riga contenente ID origine, direzione e ID destinazione.
     * @param roomMap La mappa per recuperare rapidamente le istanze delle stanze.
     */
    private static void parseExit(String line, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";", 3);
        if (parts.length < 3) return;

        int fromId = Integer.parseInt(parts[0].trim());
        String direction = parts[1].trim();
        int toId = Integer.parseInt(parts[2].trim());

        Room fromRoom = roomMap.get(fromId);
        Room toRoom = roomMap.get(toId);

        if (fromRoom != null && toRoom != null) {
            fromRoom.setExit(direction, toRoom);
        }
    }

    /**
     * Legge una riga di configurazione di un oggetto e lo aggiunge alla stanza corretta.
     *
     * @param line La stringa con i dati dell'oggetto (ID, tipo, nome, coordinate, ecc.).
     * @param roomMap La mappa per recuperare la stanza a cui assegnare l'oggetto.
     */
    private static void parseObject(String line, Map<Integer, Room> roomMap) {
        String[] parts = line.split(";");
        if (parts.length < 10) return;

        int roomId = Integer.parseInt(parts[0].trim());
        Room targetRoom = roomMap.get(roomId);
        if (targetRoom == null) return;

        int objId = Integer.parseInt(parts[1].trim());
        String objType = parts[2].trim();
        String objName = parts[3].trim();
        String objDesc = parts[4].trim();
        String objImg = parts[5].trim();
        int x = Integer.parseInt(parts[6].trim());
        int y = Integer.parseInt(parts[7].trim());
        int width = Integer.parseInt(parts[8].trim());
        int height = Integer.parseInt(parts[9].trim());

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
     * Implementazione del pattern Factory Method.
     * Valuta la stringa del tipo (es. "Key" o "Weapon") e istanzia la sottoclasse
     * corretta di GameObject sfruttando il polimorfismo.
     *
     * @param type Il tipo di oggetto letto dal file.
     * @param id L'identificativo numerico dell'oggetto.
     * @param name Il nome dell'oggetto.
     * @param desc La descrizione dell'oggetto.
     * @param img Il percorso dell'immagine dell'oggetto.
     * @param parts L'array completo di informazioni (usato per estrarre attributi extra come i danni).
     * @return L'istanza concreta dell'oggetto creato, o null se il tipo non esiste.
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