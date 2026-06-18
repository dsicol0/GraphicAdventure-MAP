package it.map.graphicadventure.progettoesame.type;

import java.util.ArrayList;
import java.util.List;

public class ObjectContainer extends GameObject {
    private final List<GameObject> list = new ArrayList<>();

    public ObjectContainer(int id, String name, String description) {
        super(id, name, description);
        this.setTakeable(false); // Di solito i grandi contenitori (es. bauli) non si possono prendere
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
}
