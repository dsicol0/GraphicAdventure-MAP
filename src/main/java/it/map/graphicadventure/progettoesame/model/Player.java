package it.map.graphicadventure.progettoesame.model;

/**
 * Rappresenta il protagonista dell'avventura grafica.
 *
 * Questa classe mantiene lo stato corrente del giocatore, incapsulando attributi 
 * fondamentali come i punti vita (HP) e il nome.
 *
 */
public class Player {

    private String name;

    // Punti vita di default all'inizio della partita
    private int hp = 100;

    // L'attributo è 'final' per garantire che la reference allo zaino non venga mai persa o sovrascritta
    private final Inventory inventory;

    /**
     * Costruisce un nuovo giocatore.
     * Al momento della creazione, assegna il nome e inizializza un inventario vuoto.
     *
     * @param name Il nome del giocatore (es. "Matricola Disperata").
     */
    public Player(String name) {
        this.name = name;
        this.inventory = new Inventory();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    
    /**
     * Restituisce l'inventario personale del giocatore.
     *
     * @return L'oggetto {@link Inventory} contenente gli strumenti raccolti.
     */
    public Inventory getInventory() {
        return inventory;
    }

    /**
     * Verifica la presenza di un determinato oggetto all'interno dello zaino.
     *
     * Invece di iterare manualmente la collezione, trasforma la lista in uno stream e 
     * valuta dinamicamente una condizione tramite un'espressione Lambda 
     * passata al metodo terminale {@code anyMatch}.
     *
     * @param idObject L'identificativo univoco dell'oggetto da cercare.
     * @return {@code true} se l'oggetto è presente nell'inventario, {@code false} altrimenti.
     */
    public boolean hasObject(int idObject) {
        return this.inventory.getList().stream().anyMatch(obj -> obj.getId() == idObject);
    }
    
}
