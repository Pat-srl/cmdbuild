Questo modulo è il *core* per gestire il *diff* sul modello di CMDBuild, sia esso:
- *diff* sui dati (`data`, ossia `Card`, `Process` e `View` e valore dei relativi `Attribute`), necessaria per [#7995 -- backend: Mobile - manage data offline](http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/7995); 
- *diff* sullo `schema` del modello (`Classe`/`Process`/`Domain`/`Lookup`/*DMS Model*/*DMS Categories*), necessaria per [backend: support to synchronization of CMDBuild model](http://gitlab.example.com/cmdbuild/cmdbuild/-/issues/7996).

Per gestire il *diff* in modo generico sono state modellate le seguenti classi:
- `CmModelNode` -- è l'interfaccia di un *nodo* (di tipo `T`) che aggrega oggetti del *modello di CMDBuild* (rappresentato dal tipo `U`), siano essi propriamente `data` o `schema`; 
  - può essere a livello singolo (una *radice*) oppure contenere anche una lista di componenti (quindi una *radice* più un primo livello di *foglie*);
  - può essere un oggetto reale del *modello di CMDBuild* piuttosto che una sua rappresentazione Json;
  - siccome è il *nodo* che ha conoscenza di come vengono aggregati oggetti, nel suo metodo `calculateDiff()` viene implementata la relativa logica di composizione dei *delta*, che rappresentano appunto le differenze tra due *nodi*:
    Esempio di codice:

```
    public CmDeltaList calculateDiff(CmDifferRepository visitorRepository, KnownModelRootNode rightModelNode) {
        CmDeltaList deltaList = new CmDeltaList();
        deltaList.addAll(visitorRepository.diff(this, rightModelNode));

        // Note: visit for diff ignores list of FirstLevel objects contained in Root model, instead this
        // ModelNode permits to aggregate KnownModelItem object (through their respective nodes)

        // Application of visitor to respective (aggregated by node) components
        deltaList.addAllAsComponentsDiff(visitorRepository.diffComposed(aggregatedComponents, rightModelNode.aggregatedComponents));

        return deltaList;
    }
```  
 
In questo caso:        
* l'eventuale *delta* su `KnownRootNode` è messo come *delta* a livello *root*;
* gli eventuali *delta* sugli oggetti aggregati di tipo `KnownModelItem` vengono messi come lista di *delta* primo livello nel `CmDeltaList` restituito, mantenendo quindi questo innestamento anche nei *delta*;
* occorre implementare anche l'overwrite di *distinguishing name* eventualmente blank (siano essi stringa vuota o `0` per gli id; se invece le nuove istanze aggiunte devono essere identificabili, ad esempio esistono relazioni tra di esse da gestire, non si potrà usare stringa vuota o `0`,
ma occorrerà usare un UUID: è questo il caso per le `Card`, che hanno *refrence* e *foreign key* tra di esse).

```
    protected String fakeDistinguishingName;

    /**
     * To handle items (model data/schema) that hasn't a set distinguishing name
     * because newly inserted.
     *
     * <p>
     * Couldn't do this in a abstract intermediate class due to type erasure...
     *
     * @param fakeDistinguishingName
     */
    @Override
    public void overwriteDistinguishingName(String fakeDistinguishingName) {
        this.fakeDistinguishingName = fakeDistinguishingName;
    }

    @Override
    public String getDistinguishingName() {
        // Couldn't do this in a abstract intermediate class due to type erasure...
        return firstNotNull(fakeDistinguishingName,
                ...valore calcolato... ex. item.getId()
        );
    }
```

   Per problemi di *type erasure*, non è stato possibile fare ciò in una classe astratta da cui derivare le implementazioni di ogni nodo di tipo `T`, e va quindi implementato nelle singole classi.


- `AbstractCmModelNodeDiffer` -- classe astratta da cui derivare per implementare il *diff* tra due nodi del tipo `T` specificato, riguardanti un oggetto di tipo `U` del *modello di CMDBuild* (o sua rappresentazione Json); il *diff* viene implementato dall'omonimo metodo `diff()`, che vaglia i valori di due *nodi* del tipo `T` per definire se sia da generare:
  - una situazione di *equal*, invocando l'omonimo metodo `equal()`, e restituendo un oggetto `CmEqualDelta`;
  - una sistuazione di *change*, invocando il metodo `changed()`, e resituendo un oggetto `CmChangeDelta`;
  - viene già implementato il *diff* di una lista omogenea di *nodi* di tipo `T` gestendo un `distinguishing name`: è il *nodo* stesso ad avere conoscenza di **come estrarre effettivamente questo valore**, e serve per avere un confronto efficiente tra dati omogenei.
    - nel caso di *insert*, il *nodo* potrebbe non avere informazioni (ad esempio un `code` vuoto o un `id` a `0`) e viene quindi generato un *distinguishing name* fittizio, nella forma `<new_n>` dove `n` è un opportuno incrementale (vedi metodo `AbstractCmModelNodeDiffer.distinguishingNameFill()`). Ciò rappresenta comuque il fatto che qualcosa di prima sconosciuto è stato creato. In tutti gli altri casi (*equal*, *change* e *remove*) tale *distinguishing name* **deve** essere coerentemente valorizato.

   Esempio di codice:
```
     @Override
    public <T extends CmModelNode<T, U>, U> CmDeltaList diff(T left, T right) {
        final KnownModelItem leftItem = ((KnownModelAggregatedItemNode) left).getModelObj();
        final KnownModelItem rightItem = ((KnownModelAggregatedItemNode) right).getModelObj();

        if (leftItem.getName().equals(rightItem.getName())
                && leftItem.getValue().equals(rightItem.getValue())) {
            return equal(left.getDistinguishingName(), left);
        } else {
            return changed(left.getDistinguishingName(), left, right);
        }

        // inserted and removed are handled in lists of nodes
    }
```

- `CmDeltaList` -- per uniforrmità e semplicità di utlizzo, si rappresenta un *delta* su un *nodo* come un oggetto `CmDeltaList`, che può contenere uno o più `AbstractCmDelta` come *root* e/o un `CmDeltaList` per oggetti componenti di primo livello.

- `CmDiffRepository` -- *repository* in cui inserire tutti i *differ*, con chiave la classe nel *nodo* che sono in grado di gestire; viene passato l'intero *repository* quando si calcola il *diff* su un nodo, in quanto è il *nodo* stesso ad aver responsabilità di invocare il *diff* anche su (eventuali) *nodi* componenti;

- nel package `synch` sono presenti tutte le classi che fanno da *wrapper* a *servizi di CMDBuild* per creare, aggiornare e rimuovere `data`/`schema` del *modello di CMDBuild*;
- `AbstractCmApplier` -- classe astratta che rappresenta l'applicazione ad un *nodo* di tipo `T` di un *delta* tramite dei *synch* opportuni, producendo oggetti del *modello di CMDBuild* di tipo `U`;
  - nelle implementazioni occorre implementare le 3 operazioni:
     - `applyChange()` per un *delta* `CmChangeDelta` -- tramite gli opportuni wrapper *synch*, invoca i *servizi di CMDBuild* per aggiornare oggetti di modello;
     - `applyInsert()` per un *delta* `CmInsertDelta` -- tramite gli opportuni wrapper *synch*, invoca i *servizi di CMDBuild* per creare oggetti di modello;
     - `applyRemove()` per un *delta* `CmRemoceDelta` -- tramite gli opportuni wrapper *synch*, invoca i *servizi di CMDBuild* per cancellare (rendere non attivi) oggetti di modello;
