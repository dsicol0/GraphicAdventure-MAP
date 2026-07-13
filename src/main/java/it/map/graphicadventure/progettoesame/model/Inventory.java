package it.map.graphicadventure.progettoesame.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta l'inventario del giocatore.
 *
 * Agisce come una classe "Wrapper". Sfrutta l'implementazione {@link ArrayList} e 
 * l'uso dei Generics ({@code List<GameObject>}) per garantire la Type Safety 
 * a tempo di compilazione.
 *
 */
public class Inventory {
    
    /**
     * Struttura dati interna che memorizza gli oggetti raccolti dal giocatore.
     */
    private List<GameObject> list = new ArrayList<>();

    /**
     * Restituisce la lista completa degli oggetti attualmente contenuti nell'inventario.
     * @return La collezione {@code List} degli oggetti di gioco.
     */
    public List<GameObject> getList() {
        return list;
    }

    /**
     * Sostituisce l'intera lista dell'inventario con una nuova.
     * Questo metodo è fondamentale durante le fasi di deserializzazione o caricamento 
     * di un salvataggio preesistente dal database.
     *
     * @param list La nuova lista di {@link GameObject} da assegnare.
     */
    public void setList(List<GameObject> list) {
        this.list = list;
    }

    /**
     * Aggiunge un nuovo elemento all'inventario.
     * Delega l'operazione di inserimento al metodo {@code add()} della Collection sottostante.
     *
     * @param o L'oggetto (Arma, Chiave, Cibo, ecc.) da aggiungere.
     */
    public void add(GameObject o) {
        list.add(o);
    }

    /**
     * Rimuove un elemento dall'inventario.
     * L'operazione di ricerca e rimozione viene eseguita in modo preciso e senza bug 
     * grazie all'override del metodo {@code equals()} basato sull'ID, precedentemente 
     * definito nella classe astratta {@link GameObject}.
     *
     * @param o L'oggetto specifico da rimuovere dallo zaino.
     */
    public void remove(GameObject o) {
        list.remove(o);
    }
}
