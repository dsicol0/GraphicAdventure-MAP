package it.map.graphicadventure.progettoesame.model;

public class Player {

    private String name;

    // Gestione della salute per l'aspetto "Survival"
    private int hp;
    private int maxHp;

    // Inventario personale del giocatore
    private final Inventory inventory;

    public Player(String name, int maxHp) {
        this.name = name;
        this.maxHp = maxHp;
        this.hp = maxHp; // All'inizio il giocatore ha la vita al massimo
        this.inventory = new Inventory();
    }

    // =========================================================================
    // GETTER & SETTER BASE
    // =========================================================================
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
        // Evitiamo che la vita superi il massimo consentito
        this.hp = Math.min(hp, maxHp);
    }

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    public Inventory getInventory() {
        return inventory;
    }

    // =========================================================================
    // METODI DI UTILITA' PER IL COMBATTIMENTO E LA SOPRAVVIVENZA
    // =========================================================================

    /**
     * Infligge danni al giocatore.
     * @param damage La quantità di danni subiti.
     */
    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    /**
     * Cura il giocatore (es. bevendo un caffè).
     * @param amount La quantità di punti vita ripristinati.
     */
    public void heal(int amount) {
        this.hp += amount;
        if (this.hp > this.maxHp) {
            this.hp = this.maxHp;
        }
    }

    /**
     * Controlla se il giocatore è morto.
     * @return true se gli HP sono 0, false altrimenti.
     */
    public boolean isDead() {
        return this.hp <= 0;
    }
}
