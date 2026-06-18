package it.map.graphicadventure.progettoesame.type;

public class GameObject {
    private final int id;
    private String name;
    private String description;
    private boolean takeable; // Indica se il giocatore può metterlo nell'inventario

    public GameObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.takeable = true; // Di default un oggetto è prendibile
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public boolean isTakeable() { return takeable; }
    public void setTakeable(boolean takeable) { this.takeable = takeable; }
}
