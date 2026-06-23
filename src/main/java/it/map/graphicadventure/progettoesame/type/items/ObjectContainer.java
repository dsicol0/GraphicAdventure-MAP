package it.map.graphicadventure.progettoesame.type.items;

import it.map.graphicadventure.progettoesame.type.GameObject;
import it.map.graphicadventure.progettoesame.type.interfaces.Openable;
import java.util.ArrayList;
import java.util.List;

public class ObjectContainer extends GameObject implements Openable {
    private final List<GameObject> list = new ArrayList<>();

    public ObjectContainer(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean isOpen() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
