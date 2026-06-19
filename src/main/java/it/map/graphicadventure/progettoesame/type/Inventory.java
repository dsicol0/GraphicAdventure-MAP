package it.map.graphicadventure.progettoesame.type;

import java.util.ArrayList;
import java.util.List;

public class Inventory {
    private List<GameObject> list = new ArrayList<>();

    public List<GameObject> getList() {
        return list;
    }

    public void setList(List<GameObject> list) {
        this.list = list;
    }

    public void add(GameObject o) {
        list.add(o);
    }

    public void remove(GameObject o) {
        list.remove(o);
    }
}
