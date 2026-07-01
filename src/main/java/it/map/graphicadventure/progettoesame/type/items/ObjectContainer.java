package it.map.graphicadventure.progettoesame.type.items;

import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.interfaces.Openable;
import java.util.ArrayList;
import java.util.List;

public class ObjectContainer extends GameObject implements Openable {
    private final List<GameObject> list = new ArrayList<>();

    private boolean open;

    public ObjectContainer(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);

        // Di default, un contenitore nasce chiuso
        this.open = false;
    }

    public List<GameObject> getList() {
        return list;
    }

    public void add(GameObject obj) {
        list.add(obj);
    }

    public void remove(GameObject obj) {
        list.remove(obj);
    }

    @Override
    public void open() {
        this.open = true;
        System.out.println("Hai aperto " + getName() + ".");
    }

    @Override
    public void close() {
        this.open = false;
        System.out.println("Hai chiuso " + getName() + ".");
    }

    @Override
    public boolean isOpen() {
        return this.open;
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void setOpen(boolean open) {
        this.open = open;
    }
}
