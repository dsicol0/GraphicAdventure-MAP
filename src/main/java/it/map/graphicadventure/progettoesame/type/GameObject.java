package it.map.graphicadventure.progettoesame.type;

import java.util.Objects;

public abstract class GameObject {

    private final int id;
    private String name;
    private String description;
    private String imagePath;

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


    @Override
    public boolean equals(Object o) {
        if(o instanceof GameObject) {
            return (((GameObject)o).getId() == this.getId());
        } else {
            return false;
        }
    }
    
    @Override
    public int hashCode() {
        // Genera un hash basato sull'ID.
        return java.util.Objects.hash(id);
    }
}
