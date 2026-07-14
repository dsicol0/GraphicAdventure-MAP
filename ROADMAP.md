# Final Exam — Roadmap

---

## Fase 1 — Design del gioco

- [x] Scegliere il nome del gioco e del gruppo
- [x] Scrivere la descrizione dell'avventura (trama, obiettivo, ambientazione)
- [x] Definire le stanze dell'edificio (corridoi, aule, laboratori, ufficio del direttore, ecc.)
- [x] Definire le connessioni tra stanze (quali porte portano dove)
- [x] Disegnare/raccogliere le immagini di sfondo per ogni stanza

---

## Fase 2 — Oggetti e interazioni

- [x] Creare gli oggetti raccoglibili (chiavi, armi, oggetti generici)
- [x] Creare i contenitori (casse, armadietti) che nascondono oggetti
- [x] Creare il badge del Direttore (oggetto obiettivo finale)
- [x] Implementare le interazioni: raccogliere, aprire, usare
- [x] Implementare la saracinesca di uscita (si sblocca solo col badge)

---

## Fase 3 — Interfaccia grafica (SWING)

- [x] Creare il menu principale
- [x] Creare la finestra di gioco principale
- [x] Creare il pannello stanza (immagine di sfondo + aree cliccabili sugli oggetti)
- [x] Creare il pannello inventario (mostra gli oggetti raccolti)
- [x] Creare il box testo (descrizioni e risultati delle azioni)

---

## Fase 4 — Zombie

- [x] Definire il comportamento degli zombie (statici o in pattuglia tra stanze)
- [x] Implementare cosa succede quando il giocatore incontra uno zombie
- [x] Implementare armi o oggetti per neutralizzarli

---

## Fase 5 — Salvataggio e caricamento (File + Database)

- [x] Caricare la mappa e gli oggetti da file esterni (no dati hardcoded)
- [x] Implementare il salvataggio della partita su database
- [x] Implementare il caricamento di una partita salvata
- [x] Tenere un log degli eventi (oggetti raccolti, stanze visitate, zombie incontrati)

---

## Fase 6 — Timer e zombie in movimento (Thread)

- [x] Aggiungere un timer visibile (es. il generatore si esaurisce entro X minuti)
- [x] Gestire la scadenza del timer (game over)

---

## Fase 7 — Leaderboard (Socket / REST)

- [x] Calcolare il punteggio finale (tempo, oggetti raccolti, zombie evitati)
- [x] Inviare il punteggio a un server al termine della partita
- [x] Visualizzare la classifica dei migliori punteggi

---

## Fase 8 — Documentazione

- [x] Scrivere la descrizione dell'avventura nel documento
- [ ] Inserire il diagramma delle classi (porzione significativa) e commentarlo
- [ ] Scrivere la specifica algebrica di una struttura dati usata nel progetto
- [x] Compilare la sezione "Dettagli implementativi" per ognuno dei 7 argomenti:
  - [x] Programmazione generica
  - [x] File
  - [x] Database (JDBC)
  - [x] Lambda Expression, stream e pipeline
  - [x] SWING
  - [x] Thread e programmazione concorrente
  - [x] Socket e/o REST
- [x] RIMUOVERE TUTTI I COMMENTI DI AI
- [x] Documentare il codice (Javadoc sui metodi principali)

---

## Fase 9 — Consegna e orale

- [ ] Test finale: qualcuno esterno al gruppo gioca dall'inizio alla fine senza istruzioni
- [ ] Preparare la demo live (max 20 minuti)
- [ ] Preparare eventuali slide per la presentazione
- [ ] Consegnare zip/link via mail con tutti i membri del gruppo in evidenza
- [ ] Consegnare **5 giorni prima** della prova orale
