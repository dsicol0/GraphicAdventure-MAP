/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package it.map.graphicadventure.progettoesame.model.items;

import it.map.graphicadventure.progettoesame.model.GameObject;
import it.map.graphicadventure.progettoesame.model.interfaces.Lockable;

/**
 * Rappresenta un contenitore chiuso a chiave (es. un forziere o una cassaforte).
 * Estende {@link ObjectContainer} mantenendone la natura generica (vincolata a GameObject),
 * ma aggiunge il comportamento di {@link Lockable} per impedire l'apertura senza 
 * la chiave corretta.
 *
 * @param <T> Il tipo di oggetti contenuti nella cassa (deve essere un GameObject o sottoclasse).
 */
public class Chest<T extends GameObject> extends ObjectContainer<T> implements Lockable {
    
    private boolean locked;
    private final int requiredKeyId;
    
    /**
     * Costruisce un nuovo forziere chiuso a chiave.
     *
     * @param id L'identificativo univoco del forziere.
     * @param name Il nome del forziere (es. "Cassa di metallo").
     * @param description La descrizione mostrata quando viene esaminato.
     * @param imagePath Il percorso dell'immagine associata.
     * @param requiredKeyId L'ID della {@link Key} necessaria per sbloccare questa serratura.
     */
    public Chest(int id, String name, String description, String imagePath, int requiredKeyId) {
       
        super(id, name, description, imagePath);
        
        // Di default, ogni Chest appena creata è chiusa a chiave
        this.locked = true; 
        this.requiredKeyId = requiredKeyId;
    }

    /**
     * Restituisce l'ID della chiave necessaria per aprire questo forziere.
     * @return L'identificativo numerico della chiave corretta.
     */
    public int getRequiredKeyId() {
        return requiredKeyId;
    }
    
    @Override
    public boolean isLocked() {
        return this.locked;
    }
    
    @Override
    public void setLocked(boolean locked) {
        this.locked = locked;
    }
    
    /**
     * Tenta di sbloccare la serratura utilizzando una specifica chiave.
     *
     * @param keyUsed L'oggetto Key che il giocatore sta provando ad usare.
     * @return {@code true} se la chiave corrisponde all'ID richiesto ed è riuscita 
     * a sbloccare il forziere, {@code false} altrimenti.
     */
    public boolean unlock(Key keyUsed) {
        if (keyUsed != null && keyUsed.getId() == this.requiredKeyId) {
            this.locked = false;
            return true;
        }
        return false;
    }
    
    /**
     * Tenta di aprire il forziere.
     * Esegue un override del comportamento base di {@link ObjectContainer}: l'apertura 
     * viene inoltrata alla classe padre (tramite {@code super.open()}) solo ed 
     * esclusivamente se la serratura non è più bloccata.
     */
    @Override
    public void open() {
        if (!locked) {
            super.open(); // Chiama l'open() del padre (ObjectContainer), che imposta open = true
        } else {
            // Se è chiusa a chiave non fa nulla. Sarà il controller a stampare il messaggio di errore.
        }
    }
}