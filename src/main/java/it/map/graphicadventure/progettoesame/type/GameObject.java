package it.map.graphicadventure.progettoesame.type;

import java.util.HashSet;
import java.util.Set;

public class GameObject {

    private final int id;
    private String name;
    private String description;
    private String imagePath;

    // Gestione degli Alias (es: il nome è "spada", gli alias sono "spadone", "lama")
    private Set<String> aliases;

    // Cosa si può fare con l'oggetto)
    private boolean takeable; // Indica se il giocatore può metterlo nell'inventario
    private boolean pushable;
    private boolean openable;

    // Stato attuale dell'oggetto
    private boolean pushed;
    private boolean opened;

    public GameObject(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.aliases = new HashSet<>();

        this.takeable = true; // Di default un oggetto è prendibile
        this.pushable = false;
        this.openable = false;
        this.pushed = false;
        this.opened = false;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public void setAliases(Set<String> aliases) {
        this.aliases = aliases;
    }

    // Metodo comodo per aggiungere un alias singolo (lo salviamo in minuscolo per facilitare i controlli del parser)
    public void addAlias(String alias) {
        this.aliases.add(alias.toLowerCase());
    }

    // Metodo comodo per aggiungere più alias in una volta sola
    public void addAliases(String... newAliases) {
        for(String a : newAliases) {
            this.aliases.add(a.toLowerCase());
        }
    }

    public int getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public boolean isTakeable() { return takeable; }
    public void setTakeable(boolean takeable) { this.takeable = takeable; }

    public boolean isPushable() { return pushable; }
    public void setPushable(boolean pushable) { this.pushable = pushable; }

    public boolean isOpenable() { return openable; }
    public void setOpenable(boolean openable) { this.openable = openable; }

    public boolean isPushed() { return pushed; }
    public void setPushed(boolean pushed) { this.pushed = pushed; }

    public boolean isOpened() { return opened; }
    public void setOpened(boolean opened) { this.opened = opened; }

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
