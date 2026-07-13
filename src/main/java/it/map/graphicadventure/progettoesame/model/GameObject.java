package it.map.graphicadventure.progettoesame.model;

/**
 * Classe astratta che rappresenta l'entità base di tutto il mondo di gioco.
 * Qualsiasi elemento fisico (Armi, Chiavi, Stanze, Nemici) eredita da questa classe.
 
 * Implementa l'incapsulamento degli attributi base (id, nome, posizione) e fornisce 
 * un criterio di uguaglianza basato sull'ID univoco, fondamentale per la corretta 
 * gestione degli oggetti all'interno delle Collection (List, Set, Map).
 *
 */
public abstract class GameObject {

    private final int id;
    private String name;
    private String description;
    private String imagePath;
    
    private int x;
    private int y;
    private int width;
    private int height;

    /**
     * Costruisce un nuovo oggetto di gioco base.
     *
     * @param id L'identificativo univoco dell'oggetto.
     * @param name Il nome dell'oggetto.
     * @param description La descrizione testuale mostrata al giocatore.
     * @param imagePath Il percorso relativo dell'immagine dell'oggetto.
     */
    public GameObject(int id, String name, String description, String imagePath) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public int getWidth() { return width; }
    public void setWidth(int width) { this.width = width; }

    public int getHeight() { return height; }
    public void setHeight(int height) { this.height = height; }

    /**
     * Confronta questo oggetto con un altro per verificarne l'uguaglianza.
     * Sovrascrive il metodo di default di Object per basare l'uguaglianza 
     * unicamente sull'ID dell'oggetto.
     *
     * @param o L'oggetto da confrontare.
     * @return {@code true} se l'oggetto passato è un GameObject e ha lo stesso ID, {@code false} altrimenti.
     */
    @Override
    public boolean equals(Object o) {
        if(o instanceof GameObject) {
            return (((GameObject)o).getId() == this.getId());
        } else {
            return false;
        }
    }
    
    /**
     * Genera un codice hash per l'oggetto, mantenendo la coerenza con il metodo {@code equals}.
     * Indispensabile per far funzionare correttamente l'oggetto all'interno di 
     * strutture dati basate su hash (come HashMap o HashSet).
     *
     * @return L'hash code calcolato sull'ID dell'oggetto.
     */
    @Override
    public int hashCode() {
        return java.util.Objects.hash(id);
    }
}
