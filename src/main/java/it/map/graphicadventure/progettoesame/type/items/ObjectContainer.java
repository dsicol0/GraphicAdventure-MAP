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

// consigliato da gemini!!!
/*
Dato che all'inizio mi hai detto che la Programmazione Generica è un requisito obbligatorio per passare l'esame, questa classe è il candidato perfetto per smarcarlo con zero sforzo e fare un'ottima figura.

Invece di lasciare che ObjectContainer contenga una normale lista di GameObject misti, potresti trasformare l'intera classe in un generico: public class ObjectContainer<T extends GameObject> extends GameObject implements Openable.

In questo modo:

Un armadietto del pronto soccorso diventerebbe ObjectContainer<Medicikit>.

Una cassetta degli attrezzi diventerebbe ObjectContainer<Weapon>.

Il professore vedrebbe che sai usare i Generics per garantire la "Type Safety" (sicurezza dei tipi) nel tuo codice.

 */