# Final Exam — Roadmap

---

## Fase 1 — Design del gioco

- [x] Scegliere il nome del gioco e del gruppo
- [x] Scrivere la descrizione dell'avventura (trama, obiettivo, ambientazione)
- [ ] Definire le stanze dell'edificio (corridoi, aule, laboratori, ufficio del direttore, ecc.)
- [ ] Definire le connessioni tra stanze (quali porte portano dove)
- [ ] Disegnare/raccogliere le immagini di sfondo per ogni stanza

---

## Fase 2 — Oggetti e interazioni

- [x] Creare gli oggetti raccoglibili (chiavi, armi, oggetti generici)
- [x] Creare i contenitori (casse, armadietti) che nascondono oggetti
- [x] Creare il badge del Direttore (oggetto obiettivo finale)
- [x] Implementare le interazioni: raccogliere, aprire, usare
- [ ] Implementare la saracinesca di uscita (si sblocca solo col badge)

---

## Fase 3 — Interfaccia grafica (SWING)

- [x] Creare il menu principale
- [x] Creare la finestra di gioco principale
- [x] Creare il pannello stanza (immagine di sfondo + aree cliccabili sugli oggetti)
- [x] Creare il pannello inventario (mostra gli oggetti raccolti)
- [ ] Creare il box testo (descrizioni e risultati delle azioni)

---

## Fase 4 — Zombie

- [ ] Definire il comportamento degli zombie (statici o in pattuglia tra stanze)
- [ ] Implementare cosa succede quando il giocatore incontra uno zombie
- [ ] Implementare armi o oggetti per neutralizzarli

---

## Fase 5 — Salvataggio e caricamento (File + Database)

- [x] Caricare la mappa e gli oggetti da file esterni (no dati hardcoded)
- [ ] Implementare il salvataggio della partita su database
- [ ] Implementare il caricamento di una partita salvata
- [ ] Tenere un log degli eventi (oggetti raccolti, stanze visitate, zombie incontrati)

---

## Fase 6 — Timer e zombie in movimento (Thread)

- [ ] Aggiungere un timer visibile (es. il generatore si esaurisce entro X minuti)
- [ ] Gestire la scadenza del timer (game over)
- [ ] Far muovere gli zombie in background senza bloccare l'interfaccia

---

## Fase 7 — Leaderboard (Socket / REST)

- [ ] Calcolare il punteggio finale (tempo, oggetti raccolti, zombie evitati)
- [ ] Inviare il punteggio a un server al termine della partita
- [ ] Visualizzare la classifica dei migliori punteggi

---

## Fase 8 — Documentazione

- [ ] Scrivere la descrizione dell'avventura nel documento
- [ ] Inserire il diagramma delle classi (porzione significativa) e commentarlo
- [ ] Scrivere la specifica algebrica di una struttura dati usata nel progetto
- [ ] Compilare la sezione "Dettagli implementativi" per ognuno dei 7 argomenti:
  - [ ] Programmazione generica
  - [ ] File
  - [ ] Database (JDBC)
  - [ ] Lambda Expression, stream e pipeline
  - [ ] SWING
  - [ ] Thread e programmazione concorrente
  - [ ] Socket e/o REST
- [ ] Documentare il codice (Javadoc sui metodi principali)

---

## Fase 9 — Consegna e orale

- [ ] Test finale: qualcuno esterno al gruppo gioca dall'inizio alla fine senza istruzioni
- [ ] Preparare la demo live (max 20 minuti)
- [ ] Preparare eventuali slide per la presentazione
- [ ] Consegnare zip/link via mail con tutti i membri del gruppo in evidenza
- [ ] Consegnare **5 giorni prima** della prova orale
