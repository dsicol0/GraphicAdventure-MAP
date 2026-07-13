package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Openable;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un contenitore generico all'interno del gioco (es. uno zaino, un cassetto).
 * 
 * Sfrutta i Generics con un limite superiore (Upper Bound {@code <T extends GameObject>}) 
 * per garantire a tempo di compilazione che all'interno di questo contenitore possano 
 * essere inseriti esclusivamente oggetti fisici del gioco, prevenendo errori di tipo (Type Safety).
 * 
 * Inoltre, implementa l'interfaccia {@link Openable} per consentire l'interazione di 
 * apertura e chiusura.
 *
 * @param <T> Il tipo generico degli oggetti contenuti (deve essere un GameObject o sue sottoclassi).
 */
public class ObjectContainer<T extends GameObject> extends GameObject implements Openable {
    
    /**
     * Struttura dati (Collection) che memorizza gli oggetti contenuti.
     */
    private final List<T> items = new ArrayList<>();
    
    private boolean open;

    /**
     * Costruisce un nuovo contenitore di oggetti.
     * Di default, il contenitore viene creato nello stato "chiuso".
     *
     * @param id L'identificativo univoco del contenitore.
     * @param name Il nome del contenitore (es. "Zaino abbandonato").
     * @param description La descrizione visibile al giocatore.
     * @param imagePath Il percorso dell'immagine dell'oggetto per la UI.
     */
    public ObjectContainer(int id, String name, String description, String imagePath) {
        super(id, name, description, imagePath);
        
        this.open = false;
    }

    /**
     * Restituisce la lista degli oggetti attualmente presenti all'interno del contenitore.
     * @return La collezione di oggetti di tipo {@code T}.
     */
    public List<T> getInsideItems() {
        return items;
    }

    /**
     * Imposta manualmente lo stato di apertura del contenitore.
     * @param open {@code true} per indicare che è aperto, {@code false} se è chiuso.
     */
    public void setOpen(boolean open) {
        this.open = open;
    }
    
    // Implementazione dell'interfaccia Openable
    
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
