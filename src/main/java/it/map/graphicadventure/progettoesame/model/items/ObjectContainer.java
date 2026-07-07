package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Openable;
import java.util.ArrayList;
import java.util.List;

public class ObjectContainer<T extends GameObject> extends GameObject implements Openable {
    
    // La lista ora è fortemente tipizzata con 'T'
    private final List<T> items = new ArrayList<>();
    private boolean open;

    public ObjectContainer(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        
        this.open = false;
    }

    // Restituisce una lista del tipo specifico (es. List<Key>)
    public List<T> getInsideItems() {
        return items;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }
    
    @Override
    public void open() {
        this.open = true;
    }
    
    @Override
    public void close() {
        this.open = false;
    }
    
    @Override
    public boolean isOpen() {
        return this.open;
    }
}
