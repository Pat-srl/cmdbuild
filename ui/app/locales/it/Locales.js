(function () {
    Ext.define('CMDBuildUI.locales.it.Locales', {
        requires: ['CMDBuildUI.locales.it.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'it',
        administration: CMDBuildUI.locales.it.LocalesAdministration.administration,
        ai: {
            clearconversation: 'Elimina conversazione',
            clearconversationmessage: 'Sei sicuro di voler eliminare la conversazione corrente?',
            copiedtoclipboard: 'Messaggio copiato negli appunti',
            erroranswer: 'Mi dispiace, ci sono stati dei problemi nella risposta',
            headertooltip: 'CMDBuild chat bot',
            introtextbody: "Per iniziare a chattare, descrivi la tua richiesta nell'apposito campo",
            introtexttitle: 'Come posso aiutarti?',
            loading: 'Caricamento ...',
            newchat: 'Nuova chat',
            newchattooltip: 'Inizia una nuova conversazione',
            questionemptytext: 'Inserisci qua la tua domanda ...',
            resources: 'Sorgenti',
            viewrelatedcard: 'Vedi scheda relativa',
            viewresource: 'Vedi risorsa'
        },
        arialabels: {
            buttonplaceholder: 'Pulsante',
            iframeplaceholder: 'Editor di testo',
            languageselector: 'Selettore della lingua',
            resizepanel: {
                horizontally: 'Ridimensiona i pannelli orizzontalmente',
                vertically: 'Ridimensiona i pannelli verticalmente'
            },
            usermenu: 'Menu utente'
        },
        attachments: {
            add: 'Aggiungi allegato',
            attachmenthistory: "Storia dell'allegato",
            author: 'Autore',
            authordescription: 'Descrizione autore',
            browse: 'Sfoglia &hellip;',
            category: 'Categoria',
            code: 'Codice',
            creationdate: 'Data di creazione',
            deleteattachment: 'Elimina allegato',
            deleteattachment_confirmation: 'Sei sicuro di voler eliminare questo allegato?',
            description: 'Descrizione',
            download: 'Scarica',
            dropfiles: 'Trascina i file qui',
            editattachment: 'Modifica allegato',
            errordms: 'Il DMS è disabilitato e non è possibile salvare file',
            file: 'File',
            filealreadyinlist: 'Il file {0} è già presente',
            filename: 'Nome del file',
            fileview: 'Vedi allegato',
            invalidfiles: 'Rimuovi i file invalidi',
            majorversion: 'Versione principale',
            maxsize: 'La dimensione massima consentita per i file è {0} MB.',
            metadata: 'Metadati',
            modificationdate: 'Data modifica',
            new: 'Nuovo allegato',
            nocategory: 'Non categorizzato',
            preview: 'Anteprima',
            removefile: 'Rimuovi il file',
            statuses: {
                empty: 'File vuoto',
                error: 'Errore',
                extensionNotAllowed: 'Estensione file non ammessa',
                loaded: 'Caricato',
                ready: 'Pronto',
                toolarge: 'Dimensione eccessiva'
            },
            successupload: '{0} allegati caricati.',
            uploadfile: 'Carica file...',
            version: 'Versione',
            viewhistory: "Vedi la storia dell'allegato",
            viewmetadata: "Vedi i metadati dell'allegato",
            warningmessages: {
                atleast:
                    '{0}: sono stati caricati {1} allegati di tipo "{2}". Questa categoria prevede almeno {3} allegati',
                exactlynumber:
                    '{0}: sono stati caricati {1} allegati di tipo "{2}". Questa categoria prevede {3} allegati',
                maxnumber:
                    '{0}: sono stati caricati {1} allegati di tipo "{2}". Questa categoria prevede al massimo {3} allegati'
            },
            wrongfileextension: 'Non è permesso caricare file con estensione {0}'
        },
        bim: {
            bimViewer: 'Visualizzatore Bim',
            card: {
                label: 'Scheda'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: "Dimensione del riquadro di delimitazione lungo l'asse X",
                BOUNDING_BOX_SIZE_ALONG_Y: "Dimensione del riquadro di delimitazione lungo l'asse Y",
                BOUNDING_BOX_SIZE_ALONG_Z: "Dimensione del riquadro di delimitazione lungo l'asse Z",
                calculated: 'Calcolati',
                description: 'Descrizione',
                guid: 'GUID',
                label: 'Proprietà',
                name: 'Nome',
                type: 'Tipo',
                uuid: 'UUID'
            },
            layers: {
                label: 'Livelli',
                menu: {
                    hideAll: 'Nascondi Tutto',
                    hideAllLayers: 'Nascondi tutti i livelli',
                    hideAllXeokit: 'Nascondi tutti {0}',
                    showAll: 'Mostra Tutto',
                    showAllLayers: 'Mostra tutti i livelli',
                    showAllXeokit: 'Mostra tutti {0}'
                },
                name: 'Nome',
                quantity: 'Quantità',
                visibility: 'Visibilità'
            },
            menu: {
                camera: 'Camera',
                disableslice: 'Disabilita taglio oggetto',
                enableslice: 'Abilita taglio oggetto',
                frontView: 'Vista frontale',
                mod: 'Modalità di visualizzazione',
                ortho: 'Passa alla vista ortogonale',
                orthographic: 'Vista Ortografica',
                pan: 'Sposta',
                panXeokit: 'Modalità di visualizzazione: sposta (Shift)',
                perspe: 'Passa alla vista prospettica',
                perspective: 'Vista Prospettica',
                resetSlice: 'Elimina taglio',
                resetView: 'Reset della Vista',
                rotate: 'Ruota',
                rotateXeokit: 'Modalità di visualizzazione: ruota',
                save: 'Salva vista',
                shortcuts: {
                    down: 'Ruota in basso',
                    left: 'Ruota a sinistra',
                    right: 'Ruota a destra',
                    title: 'Scorciatoie da tastiera',
                    up: 'Ruota in alto',
                    zoomin: 'Ingrandisci',
                    zoomout: 'Rimpicciolisci'
                },
                sideView: 'Vista laterale',
                threeD: 'Passa alla vista 3D',
                topView: "Vista dall'alto",
                twoD: 'Passa alla vista 2D'
            },
            navCube: {
                back: 'Retro',
                bottom: 'Sotto',
                front: 'Fronte',
                left: 'Sinistra',
                right: 'Destra',
                top: 'Sopra'
            },
            showBimCard: 'Mostra scheda Bim',
            tree: {
                arrowTooltip: 'Seleziona elemento',
                label: 'Oggetti',
                multipleElements: 'Seleziona elementi',
                open_card: 'Apri scheda collegata',
                root: 'Radice Ifc'
            }
        },
        bulkactions: {
            abort: 'Interrompi i processi selezionati',
            addfields: 'Aggiungi campi',
            alertdownloadattachments:
                'Alcuni allegati non verranno scaricati perché non sono stati ancora salvati sul server',
            alertnoattachmentsdownload:
                'Nessun allegato verrà scaricato perché non sono stati ancora salvati sul server',
            cancelselection: 'Annulla selezione',
            confirmabort: 'Stai interrompendo {0} istanze di processo. Sicuro di voler procedere con le modifiche?',
            confirmdelete: 'Stai eliminando {0} schede. Sicuro di voler procedere con le modifiche?',
            confirmdeleteattachements: 'Stai eliminando {0} allegati. Sicuro di voler procedere con le modifiche?',
            confirmedit: 'Stai modificando {0} per {1} schede. Sicuro di voler procedere con le modifiche?',
            delete: 'Elimina gli elementi selezionati',
            download: 'Scarica gli allegati selezionati',
            downloadall: 'Scarica tutti gli allegati',
            edit: 'Modifica gli elementi selezionati',
            missingfields: 'È necessario aggiungere i seguenti campi per la corretta validazione della form: {0}',
            noattributeselected: 'Per favore seleziona almeno un attributo',
            selectall: 'Seleziona tutti gli elementi'
        },
        calendar: {
            active_expired: 'Attive/Scadute',
            add: 'Aggiungi scadenza',
            advancenotification: 'Notifica con giorni di anticipo',
            allcategories: 'Tutte le categorie',
            alldates: 'Tutte le date',
            associatedcard: 'Scheda associata',
            calculated: 'Calcolato',
            calendar: 'Calendario',
            cancel: 'Segna come annullata',
            category: 'Categoria',
            class: 'Classe',
            cm_confirmcancel: 'Sei sicuro di voler segnare le scadenze selezionate come annullate?',
            cm_confirmcomplete: 'Sei sicuro di voler segnare le scadenze selezionate come completate?',
            cm_markcancelled: 'Segna scadenze selezionate come annullate',
            cm_markcomplete: 'Segna scadenze selezionate come completate',
            complete: 'Completa',
            completed: 'Completata',
            date: 'Data',
            days: 'Giorni',
            delaybeforedeadline: 'Anticipo notifica',
            delaybeforedeadlinevalue: 'Valore anticipo notifica',
            description: 'Descrizione',
            editevent: 'Modifica scadenza',
            enddate: 'Data di fine',
            endtype: 'Tipo di fine',
            event: 'Scadenza',
            execapplyfilter: 'applyAdvanceFilter non può essere eseguito perché è presente più di una sorgente dati',
            execremovefilter: 'removeAdvanceFilter non può essere eseguito perché è presente più di una sorgente dati',
            executiondate: 'Data di esecuzione',
            frequency: 'Frequenza',
            frequencymultiplier: 'Moltiplicatore frequenza',
            grid: 'Griglia',
            leftdays: 'Giorni mancanti',
            londdescription: 'Descrizione estesa',
            manual: 'Manuale',
            maxactiveevents: 'Numero massimo di scadenze attive',
            messagebodydelete: 'Vuoi rimuovere le regole scadenze?',
            messagebodyplural: 'Ci sono {0} regole scadenze',
            messagebodyrecalculate: 'Vuoi ricalcolare le regole scadenze con la nuova data?',
            messagebodysingular: "C'è {0} regola scadenza",
            messagetitle: 'Ricalcolo scadenze',
            missingdays: 'Giorni mancanti',
            next30days: 'Prossimi 30 giorni',
            next7days: 'Prossimi 7 giorni',
            notificationtemplate: 'Template della notifica',
            notificationtext: 'Testo di notifica',
            occurencies: 'Numero di occorrenze',
            operation: 'Operazione',
            partecipantgroup: 'Gruppo referente',
            partecipantuser: 'Utente referente',
            priority: 'Priorità',
            recalculate: 'Ricalcola',
            referent: 'Referente',
            scheduler: 'Scadenzario',
            sequencepaneltitle: 'Genera scadenze',
            startdate: 'Data di inizio',
            status: 'Stato',
            today: 'Oggi',
            type: 'Tipo',
            viewevent: 'Visualizza scadenza',
            waiteventgeneration: 'Generazione eventi in corso. Attendere il completamento',
            widgetcriterion: 'Criteri di calcolo',
            widgetemails: 'Email',
            widgetgotocard: 'Vai alla scheda di origine',
            widgetsourcecard: 'Scheda di origine'
        },
        chat: {
            title: 'Chat'
        },
        classes: {
            cards: {
                addcard: 'Aggiungi scheda',
                clone: 'Clona',
                clonewithrelations: 'Clona scheda e relazioni',
                deletebeaware: 'Attenzione che:',
                deleteblocked:
                    'Poiché sono presenti relazioni con {0} non è possibile procedere con la cancellazione delle schede.',
                deletecard: 'Cancella scheda',
                deleteconfirmation: 'Sei sicuro di voler cancellare questa scheda?',
                deleterelatedcards: 'verranno cancellate le schede {0} relazionate',
                deleterelations: 'verranno cancellate le relazioni con le schede {0}',
                label: 'Schede dati',
                modifycard: 'Modifica scheda',
                opencard: 'Apri scheda',
                print: 'Stampa scheda'
            },
            simple: 'Semplice',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Aggiungi',
                apply: 'Applica',
                cancel: 'Annulla',
                clear: 'Pulisci',
                close: 'Chiudi',
                copy: 'Copia',
                copyerrormessage: "Copia l'errore nella clipboard",
                delete: 'Cancella',
                edit: 'Modifica',
                execute: 'Esegui',
                forward: 'Successivo',
                help: 'Aiuto',
                hide: 'Nascondi',
                load: 'Carica',
                open: 'Apri',
                previous: 'Precedente',
                refresh: 'Ricarica dati',
                remove: 'Rimuovi',
                save: 'Salva',
                saveandapply: 'Salva e applica',
                saveandclose: 'Salva e chiudi',
                search: 'Cerca',
                searchaitext: "Cerca con l'AI...",
                searchtext: 'Cerca...'
            },
            and: 'e',
            attributes: {
                boolfalse: 'falso',
                booltrue: 'vero',
                link: {
                    errlabel: 'La label è obbligatoria',
                    errurl: "L'Url è obbligatorio se la label non è vuota",
                    label: 'Label',
                    url: 'Url'
                },
                nogroup: 'Dati di base'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Pulisci HTML',
                expand: 'Espandi editor',
                reduce: 'Riduci editor',
                signature: 'Aggiungi firma',
                unlink: 'Rimuovi link',
                unlinkmessage: 'Trasforma il link selezionato in testo.'
            },
            grid: {
                disablemultiselection: 'Disabilita selezione multipla',
                enamblemultiselection: 'Abilita selezione multipla',
                export: 'Esporta dati',
                filterremoved: 'Il filtro impostato è stato rimosso',
                import: 'Importa dati',
                itemnotfound: 'Elemento non trovato',
                lastUpdate: 'Ultimo aggiornamento',
                list: 'Lista',
                opencontextualmenu: 'Apri menu contestuale',
                print: 'Stampa',
                printcsv: 'Stampa come CSV',
                printmap: 'Stampa Mappa',
                printodt: 'Stampa come ODT',
                printpdf: 'Stampa come PDF',
                row: 'Elemento',
                rows: 'Elementi',
                subtype: 'Sottotipo',
                thematism: 'Tematismo'
            },
            tabs: {
                activity: 'Attività',
                attachment: 'Allegato',
                attachments: 'Allegati',
                card: 'Scheda',
                clonerelationmode: 'Modalità Clonazione Relazioni',
                details: 'Dettagli',
                emails: 'Email',
                history: 'Storia',
                notes: 'Note',
                relations: 'Relazioni',
                schedules: 'Scadenze',
                sources: 'Sorgenti'
            }
        },
        dashboards: {
            filters: 'Filtri',
            list: {
                showall: 'Mostra tutti',
                showingnitems: 'Mostra: {0} elementi'
            },
            pareto: {
                cumulative: 'Cumulativo',
                cumulativeperc: 'Cumulativo %'
            },
            tools: {
                download: 'Scarica il grafico',
                downloaddata: 'Scarica i dati del grafico',
                gridhide: 'Nascondi la tabella dei dati',
                gridshow: 'Mostra la tabella dei dati',
                openinpopup: 'Apri in popup',
                reload: 'Ricarica',
                showall: 'Mostra tutti'
            }
        },
        dependencytree: {
            cardinfo: 'Scheda',
            filteredid: 'La scheda corrente non appartiene a questo albero',
            gerarchicview: 'Vista gerarchica',
            name: 'Albero delle dipendenze',
            nocardselected: 'Nessuna card selezionata',
            opendependencytree: 'Apri albero delle dipendenze',
            printjpeg: 'Stampa come JPEG',
            printpdf: 'Stampa come PDF',
            printpng: 'Stampa come PNG',
            printsvg: 'Stampa come SVG',
            reopengraph: 'Riapri grafo con una nuova radice',
            selectcard: 'Seleziona nodo di partenza',
            wrongid: "L'ID immesso non è corretto"
        },
        emails: {
            abort: 'Annulla',
            addattachmentsfromdocumentarchive: 'Carica da archivio documenti',
            alredyexistfile: 'Esiste già un file con lo stesso nome',
            archivingdate: 'Data di archiviazione',
            attachfile: 'Allega file',
            attachmentsmessage:
                'Il template richiede di allegare gli allegati della card. Fino a quando non salvi il processo non sarà possibile utilizzarlo',
            bcc: 'Ccn',
            cc: 'Cc',
            composeemail: 'Componi e-mail',
            composefromtemplate: 'Componi dal template',
            delay: 'Ritardo',
            delays: {
                day1: '1 giorno',
                days2: '2 giorni',
                days4: '4 giorni',
                hour1: '1 ora',
                hours2: '2 ore',
                hours4: '4 ore',
                month1: '1 mese',
                negativeday1: '1 giorno prima',
                negativedays2: '2 giorni prima',
                negativedays4: '4 giorni prima',
                negativehour1: '1 ora prima',
                negativehours2: '2 ore prima',
                negativehours4: '4 ore prima',
                negativemonth1: '1 mese prima',
                negativeweek1: '1 settimana prima',
                negativeweeks2: '2 settimane prima',
                none: 'Nessuno',
                week1: '1 settimana',
                weeks2: '2 settimane'
            },
            dmspaneltitle: 'Seleziona allegati da Database',
            edit: 'Modifica',
            fileattributesmessage:
                'È presente una dipendenza da un attributo di tipo file. Fino a quando non salvi il processo non sarà possibile utilizzarlo',
            from: 'Da',
            gridrefresh: 'Refresh griglia',
            groupbystatus: 'Raggruppa per stato',
            keepsynchronization: 'Mantieni sync',
            maxsize: 'La dimensione totale massima consentita per gli allegati è {0}.',
            message: 'Messaggio',
            regenerateallemails: 'Rigenera tutte le e-mail',
            regenerateemail: 'Rigenera e-mail',
            reloadconfirmationmessage: 'Le e-mail in bozza verranno eliminate. Vuoi continuare?',
            remove: 'Rimuovi',
            remove_confirmation: 'Sei sicuro di voler eliminare questa email?',
            reply: 'Rispondi',
            replyprefix: 'Il {0} {1} ha scritto',
            searchattachment: 'Ricerca allegato',
            searchcard: 'Ricerca scheda',
            selectaclass: 'Seleziona una classe',
            sendemail: 'Invio e-mail',
            sendingemail: 'Invio email',
            signature: 'Firma',
            size: 'Dimensione',
            status: 'Stato',
            statuses: {
                draft: 'Bozze',
                error: 'Errore',
                new: 'Nuova',
                outgoing: 'In uscita',
                received: 'Ricevute',
                sent: 'Inviate',
                skipped: 'Scartata'
            },
            subject: 'Oggetto',
            templateattachments: 'Questa email contiene allegati caricati automaticamente dal template',
            to: 'A',
            view: 'Vista'
        },
        errors: {
            actionnotallowed: 'Azione non consentita',
            autherror: 'Utente o password sbagliati',
            classnotfound: 'Classe {0} non trovata',
            fieldrequired: 'Questo campo è obbligatorio',
            invalidfilter: 'Filtro non valido',
            nodatatoexport: 'Il grafico non ha dati da esportare',
            notfound: 'Elemento non trovato',
            value3chars: 'Il valore deve contenere almeno 3 caratteri'
        },
        filters: {
            actions: 'Azioni',
            addfilter: 'Aggiungi filtro',
            any: 'Almeno una',
            attachments: 'Allegati',
            attachmentssearchtext: 'Testo di ricerca allegati',
            attribute: 'Scegli un attributo',
            attributes: 'Attributi',
            clearfilter: 'Cancella Filtro',
            clone: 'Clona',
            copyof: 'Copia di',
            currentgroup: 'Gruppo corrente',
            currentuser: 'Utente corrente',
            defaultset: 'Imposta come predefinito',
            defaultunset: 'Rimuovi da predefinito',
            description: 'Descrizione',
            domain: 'Dominio',
            errorname: 'È già presente un filtro con lo stesso nome',
            filterdata: 'Dati filtro',
            fromfilter: 'Da filtro',
            fromselection: 'Dalla selezione',
            group: 'Gruppo',
            ignore: 'Ignora',
            migrate: 'Migra',
            name: 'Nome',
            newfilter: 'Nuovo filtro',
            noone: 'Nessuna',
            operator: 'Operatore',
            operators: {
                and: 'And',
                beginswith: 'Inizia con',
                between: 'Compreso',
                contained: 'Contenuto',
                containedorequal: 'Contenuto o uguale',
                contains: 'Contiene',
                containsorequal: 'Contiene o uguale',
                descriptionbegin: 'La descrizione inizia con',
                descriptioncontains: 'La descrizione contiene',
                descriptionends: 'La descrizione finisce con',
                descriptionnotbegin: 'La descrizione non inizia con',
                descriptionnotcontain: 'La descrizione non contiene',
                descriptionnotends: 'La descrizione non finisce con',
                different: 'Diverso',
                doesnotbeginwith: 'Non inizia con',
                doesnotcontain: 'Non contiene',
                doesnotendwith: 'Non finisce con',
                endswith: 'Finisce con',
                equals: 'Uguale',
                greaterthan: 'Maggiore',
                isnotnull: 'Non è nullo',
                isnull: 'È nullo',
                lessthan: 'Minore',
                or: 'Or'
            },
            relations: 'Relazioni',
            removeattribute: 'Rimuovi attributo',
            removeblock: 'Rimuovi blocco',
            type: 'Tipo',
            typeinput: 'Parametro di input',
            typeorchoosetheattributename: "Scrivi o scegli il nome dell'attributo",
            user: 'Utente',
            value: 'Valore'
        },
        gis: {
            card: 'Scheda',
            cardsMenu: 'Menu Mappa',
            code: 'Codice',
            contextmenu: {
                coordinates: 'Ottieni coordinate',
                coordinatescopiedmessage: 'Coordinate copiate negli appunti'
            },
            description: 'Descrizione',
            extension: {
                errorCall: 'Errore',
                noResults: 'Nessun risultato'
            },
            externalServices: 'Servizi esterni',
            geographicalAttributes: 'Attributi geografici',
            geoserverLayers: 'Layers di Geoserver',
            labels: {
                all: 'Tutte',
                alltitle: 'Mostra tutte le etichette',
                alltitleclass: 'Mostra tutte le etichette della classe',
                clear: 'Cancella la ricerca',
                hidden: 'Nascoste',
                hiddentitle: 'Nascondi le etichette',
                hideinfowindow: 'Nascondi info window',
                label: 'Etichette',
                search: 'Cerca sulla mappa',
                selected: 'Selezionata',
                selectedtitle: "Mostra l'etichetta per l'elemento selezionato",
                showinfowindow: 'Mostra info window'
            },
            layers: 'Livelli',
            layersTab: {
                clearPreferences: 'Rimuovi preferenze layer',
                preferencesCleared: 'Preferenze layer rimosse',
                preferencesSaved: 'Preferenze layer salvate',
                savePreferences: 'Salva preferenze layer',
                updatePreferences: 'Aggiorna preferenze layer'
            },
            list: 'Lista',
            longpresstitle: "Elementi geografici nell'area",
            map: 'Mappa',
            mapServices: 'Servizi geografici',
            menu: {
                font: 'Grandezza del font',
                options: 'Opzioni'
            },
            position: 'Posizione',
            root: 'Root',
            sourceMap: {
                default: 'Usa vista predefinita',
                satellite: 'Usa vista satellite'
            },
            tree: 'Albero di navigazione',
            type: 'Tipo',
            view: 'Vista',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Nome attività',
            activityperformer: 'Esecutore attività',
            attributes: 'Attributi ({0})',
            begindate: 'Data inizio',
            enddate: 'Data fine',
            fieldname: 'Nome del campo',
            fulldata: 'Tutti i dati',
            onlychanges: 'Solo modifiche',
            otherrelations: 'Altre relazioni',
            processstatus: 'Stato',
            referencerelation: 'Relazioni reference',
            relation: 'Relazione',
            system: 'Sistema',
            type: 'Tipo',
            user: 'Utente',
            userdescription: 'Descrizione utente',
            value: 'Valore',
            viewmode: 'Modalità di visualizzazione'
        },
        homepage: {
            search: {
                airequestproblem:
                    "Si è verificato un problema durante la richiesta. Riprova più tardi o contatta l'amministratore.",
                aisearcherror: 'Non è stato possibile completare la ricerca',
                goodafternoon: 'Buon pomeriggio',
                goodevening: 'Buonasera',
                goodmorning: 'Buongiorno',
                goodnight: 'Buonanotte',
                noresults: 'Nessun risultato trovato',
                noresultshint: "Prova parole chiave diverse o controlla l'ortografia",
                resultsfor: 'Risultati per: {0}'
            }
        },
        iconscategories: {
            accessibility: 'Accessibilità',
            animals: 'Animali',
            arrows: 'Frecce',
            audiovideo: 'Audio video',
            automotive: 'Automotive',
            autumn: 'Autunno',
            beverage: 'Bevanda',
            buildings: 'Edifici',
            business: 'Business',
            charity: 'Beneficenza',
            chat: 'Chat',
            chess: 'Scacchi',
            childhood: 'Infanzia',
            code: 'Codice',
            communication: 'Comunicazione',
            computers: 'Computers',
            construction: 'Costruzioni',
            currency: 'Valuta',
            datetime: 'Appuntamento',
            design: 'Design',
            editors: 'Editoria',
            education: 'Educazione',
            emoji: 'Emoji',
            energy: 'Energia',
            files: 'File',
            finance: 'Finanza',
            food: 'Cibo',
            fruitvegetable: 'Frutta e verdura',
            games: 'Giochi',
            gamingtabletop: 'Giochi da tavolo',
            gender: 'Genere',
            general: 'Generale',
            halloween: 'Halloween',
            hands: 'Mani',
            hotel: 'Hotel',
            household: 'Casa',
            images: 'Immagini',
            interfaces: 'Interfacce',
            logistics: 'Logistica',
            maps: 'Mappe',
            maritime: 'Marittima',
            marketing: 'Marketing',
            mathematics: 'Matematica',
            medical: 'Medicina',
            moving: 'Movimento',
            music: 'Musica',
            objects: 'Oggetti',
            paymentsshopping: 'Metodi di pagamento',
            political: 'Politica',
            religion: 'Religione',
            sciencefiction: 'Fantascienza',
            security: 'Sicurezza',
            shapes: 'Forme',
            shopping: 'Shopping',
            social: 'Sociale',
            spinners: 'Spinner',
            sports: 'Sport',
            spring: 'Primavera',
            status: 'Temperatura',
            toggle: 'Stella',
            travel: 'Viaggio',
            userspeople: 'Utenti e persone',
            vehicles: 'Veicoli',
            weather: 'Tempo',
            winter: 'Inverno'
        },
        importexport: {
            database: {
                uri: 'Database URI',
                user: 'Database user'
            },
            downloadreport: 'Scarica report',
            emailfailure: 'Si sono verificati dei problemi durante l’invio dell’email!',
            emailmessage: 'In allegato il report dell\'import del file "{0}" in data {1}',
            emailsubject: 'Report import dati',
            emailsuccess: 'L’email è stata inviata correttamente!',
            export: 'Esporta',
            exportalldata: 'Tutti i dati',
            exportfiltereddata: 'Solo i dati corrispondenti al filtro della griglia',
            gis: {
                importconfiguration: 'Configurazioni di import',
                shapeimportdisabled: "L'import degli shape non è abilitato per questo template",
                shapeimportenabled: "L'import degli shape è abilitato per questo template"
            },
            ifc: {
                card: 'Scheda',
                project: 'Progetto',
                sourcetype: 'Importa da'
            },
            import: 'Importa',
            importresponse: 'Risposta import',
            response: {
                created: ' Schede create',
                deleted: 'Schede cancellate',
                errors: 'Errori',
                linenumber: 'Numero linea',
                message: 'Messaggio',
                modified: 'Schede modificate',
                notprocessed: 'Schede non processate',
                processed: 'Righe processate',
                recordnumber: 'Numero elemento',
                unchanged: 'Schede non modificate'
            },
            sendreport: 'Invia report',
            template: 'Template',
            templatedefinition: 'Definizione template'
        },
        joinviews: {
            active: 'Attiva',
            addview: 'Aggiungi vista',
            alias: 'Alias',
            attribute: 'Attributo',
            attributecustomization: 'Personalizzazione degli attributi',
            attributes: 'Attributi',
            attributeschoice: 'Scelta degli attributi',
            attributesof: 'Attributi di: {0}',
            createview: 'Crea vista',
            datasorting: 'Ordinamento dati',
            delete: 'Elimina',
            deleteview: 'Elimina vista',
            deleteviewconfirm: 'Sei sicuro di voler eliminare questa vista?',
            description: 'Descrizione',
            disable: 'Disabilita',
            domainalias: 'Alias dominio',
            domainsof: 'Domini di {0}',
            edit: 'Modifica',
            editview: 'Modifica configurazioni vista',
            enable: 'Abilita',
            fieldsets: 'Fieldset',
            filters: 'Filtri',
            generalproperties: 'Proprietà generali',
            group: 'Gruppo',
            innerjoin: 'Inner join',
            jointype: 'Tipo di join',
            joinview: 'Vista da join',
            klass: 'Classe',
            manageview: 'Gestione vista',
            masterclass: 'Classe principale',
            masterclassalias: 'Alias classe principale',
            name: 'Nome',
            newjoinview: 'Nuova vista da join',
            outerjoin: 'Outer join',
            pleaseseleceavalidmasterclass: 'Per favore seleziona una classe principale valida',
            refreshafteredit: 'Ricaricare la pagina per vedere le modifiche?',
            selectatleastoneattribute: 'Seleziona almeno un attributo',
            selectdisplayattributegrid:
                'Seleziona almeno un attributo da mostrare nella griglia e uno nella griglia ridotta',
            showingrid: 'Mostra in griglia',
            showinreducedgrid: 'Mostra in griglia ridotta',
            targetalias: 'Alias classe destinazione'
        },
        login: {
            buttons: {
                login: 'Accedi',
                logout: 'Cambia utente'
            },
            fields: {
                group: 'Gruppo',
                language: 'Lingua',
                password: 'Password',
                tenants: 'Tenant',
                username: 'Username'
            },
            loggedin: 'Autenticato',
            sso: {
                loginfailure: 'Errore di autenticazione',
                loginwith: 'Accedi con {0}',
                or: 'oppure',
                usernotenabled: 'Utente non abilitato'
            },
            title: 'Accedi',
            welcome: 'Bentornato {0}.'
        },
        main: {
            administrationmodule: 'Modulo di Amministrazione',
            alerts: 'Avvisi',
            baseconfiguration: 'Configurazioni base',
            cardlock: {
                lockedmessage: 'Non puoi modificare questa scheda perché la sta modificando {0}.',
                someone: 'qualcuno'
            },
            changegroup: 'Cambia gruppo',
            changetenant: 'Cambia {0}',
            confirmchangegroup: 'Vuoi davvero cambiare il gruppo?',
            confirmchangetenants: 'Vuoi davvero cambiare i tenant attivi?',
            confirmdisabletenant: 'Vuoi davvero disablitare il flag "Ignora tenant"?',
            confirmenabletenant: 'Vuoi davvero abilitare il flag "Ignora tenant"?',
            description: 'Descrizione',
            ignoretenants: 'Ignora {0}',
            info: 'Informazioni',
            logo: {
                cmdbuild: 'Logo CMDBuild',
                cmdbuildready2use: 'Logo CMDBuild READY2USE',
                companylogo: 'Logo dell’azienda',
                hdaitsm: 'HDA ITSM',
                openmaint: 'Logo openMAINT'
            },
            logout: 'Esci',
            managementmodule: 'Modulo gestione dati',
            multigroup: 'Multi gruppo',
            multitenant: 'Multi {0}',
            navigation: 'Navigazione',
            pagenotfound: 'Pagina non trovata',
            password: {
                change: 'Cambia password',
                confirm: 'Conferma password',
                email: 'Indirizzo email',
                err_confirm: 'Le password non corrispondono.',
                err_diffprevious: 'La password non può essere uguale alla precedente.',
                err_diffusername: "La password non può essere uguale all'username'.",
                err_length: 'La password deve contenere almeno {0} caratteri.',
                err_reqdigit: 'La password deve contenere almeno un numero.',
                err_reqlowercase: 'La password deve contenere almeno un carattere minuscolo.',
                err_requppercase: 'La password deve contenere almeno un carattere maiuscolo.',
                expired: 'La tua password è scaduta. Modificala ora.',
                forgotten: 'Ho dimenticato la password',
                new: 'Nuova password',
                old: 'Vecchia password',
                recoverysuccess: 'Ti abbiamo inviato una email con le istruzioni per il ripristino della password.',
                reset: 'Reimposta password',
                saved: 'Password salvata correttamente!'
            },
            pleasecorrecterrors: 'Correggi gli errori evidenziati!',
            preferences: {
                comma: 'Virgola',
                decimalserror: 'Il campo separatore decimali è obbligatorio',
                decimalstousandserror: 'I campi separatore migliaia e decimali devono essere diversi',
                default: 'Predefinito',
                defaultvalue: 'Valore di default',
                favouritemenulocation: 'Posizione menu preferiti',
                firstdayofweek: 'Primo giorno della settimana',
                gridpreferencesclear: 'Rimuovi preferenze griglia',
                gridpreferencescleared: 'Preferenze griglia rimosse!',
                gridpreferencessave: 'Salva preferenze griglia',
                gridpreferencessaved: 'Preferenze griglia salvate!',
                gridpreferencesupdate: 'Aggiorna preferenze griglia',
                groupemailsbystatus: 'Mostra email raggruppate per status',
                labelcsvseparator: 'Separatore CSV',
                labeldateformat: 'Formato data',
                labeldecimalsseparator: 'Separatore decimali',
                labellanguage: 'Lingua',
                labelthousandsseparator: 'Separatore migliaia',
                labeltimeformat: 'Formato ora',
                msoffice: 'Microsoft Office',
                no: 'No',
                notificationslabel: 'Chat & Notifiche',
                notificationssound: 'Esegui suono per le notifiche',
                notificationssoundvolume: 'Volume delle notifiche',
                period: 'Punto',
                preferredfilecharset: 'Codifica CSV',
                preferredofficesuite: 'Suite Office preferita',
                sendcancellationperiod: "Periodo di cancellazione dell'invio dell'email (secondi)",
                showcollapsed: 'Mostra chiuso',
                showfovouritesafter: 'Mostra sotto il menu di navigazione',
                showfovouritesbefore: 'Mostra sopra il menu di navigazione',
                space: 'Spazio',
                thousandserror: 'Il campo separatore migliaia è obbligatorio',
                timezone: 'Fuso orario',
                twelvehourformat: 'Formato 12 ore',
                twentyfourhourformat: 'Formato 24 ore',
                yes: 'Si'
            },
            searchinallitems: 'Cerca in tutti gli elementi',
            searchtenant: 'Ricerca tenant',
            selectdeselettenants: 'Seleziona/Deseleziona tutti',
            treenavcontenttitle: '{0} di {1}',
            userpreferences: 'Preferenze'
        },
        menu: {
            allitems: 'Tutti gli elementi',
            classes: 'Classi',
            custompages: 'Pagine custom',
            dashboards: 'Dashboard',
            dependencytrees: 'Alberi delle dipendenze',
            favourites: 'Preferiti',
            favouritesadd: 'Aggiungi ai preferiti',
            favouritesremove: 'Rimuovi dai preferiti',
            homepage: 'Homepage',
            processes: 'Processi',
            reports: 'Report',
            setasinitialpage: 'Imposta come pagina iniziale',
            views: 'Viste'
        },
        mobile: {
            config: {
                configure: 'Configura dispositivo mobile',
                customercode: 'Codice cliente',
                devicename: 'Nome device',
                regenerate: 'Rigenera',
                serverurl: 'URL server'
            }
        },
        notes: {
            edit: 'Modifica nota'
        },
        notifications: {
            clearall: 'Elimina tutte',
            clearallSuccess: 'Tutte le notifiche sono state eliminate',
            delete: 'Elimina notifica',
            emptymessage: 'Non ci sono notifiche da mostrare.',
            label: 'Notifiche',
            viewall: 'Vedi tutte'
        },
        notifier: {
            attention: 'Attenzione',
            copied: 'Copiato',
            copiedtotheclipboard: 'Testo copiato negli appunti',
            error: 'Errore',
            genericerror: 'Errore generico',
            genericerrormessage: 'Per maggiori informazioni consultare i log del sistema.',
            genericinfo: 'Info generico',
            genericwarning: 'Warning generico',
            info: 'Informazione',
            more: 'Maggiori informazioni',
            success: 'Successo',
            warning: 'Attenzione'
        },
        patches: {
            apply: 'Applica patch',
            category: 'Categoria',
            description: 'Descrizione',
            name: 'Nome',
            patches: 'Patch'
        },
        processes: {
            abortconfirmation: 'Sicuro di voler interrompere questo processo?',
            abortprocess: 'Interrompi processo',
            action: {
                advance: 'Continua',
                label: 'Azione'
            },
            activeprocesses: 'Processi attivi',
            allstatuses: 'Tutti',
            editactivity: 'Modifica attività',
            openactivity: 'Apri attività',
            resumeprocesses: 'Non è possibile riattivare processi che non sono sospesi',
            startworkflow: 'Avvia',
            workflow: 'Processo'
        },
        relationGraph: {
            activity: 'Processo',
            allLabelsOnGraph: 'tutte le etichette',
            card: 'Scheda',
            cardList: 'Lista schede',
            cardRelations: 'Relazioni',
            choosenaviagationtree: 'Scegli albero di navigazione',
            class: 'Classe',
            classList: 'Lista classi',
            compoundnode: 'Raggruppamento di nodi',
            disable: 'Nascondi',
            edges: 'Archi',
            enable: 'Mostra',
            labelsOnGraph: 'tooltip',
            level: 'Livello',
            nodes: 'Nodi',
            openRelationGraph: 'Apri grafo delle relazioni',
            quantity: 'Quantità',
            refresh: 'Ricarica',
            relation: 'Relazioni',
            relationGraph: 'Grafo delle relazioni',
            reopengraph: 'Riapri il grafo da questo nodo'
        },
        relations: {
            adddetail: 'Aggiungi Dettaglio',
            addrelations: 'Aggiungi relazioni',
            attributes: 'Attributi',
            begindate: 'Data inizio',
            cardnotpresentmessage:
                "La card correlata non è più disponibile. Procedendo con la modifica sarà necessario scegliere un'altra card.",
            code: 'Codice',
            deletedetail: 'Elimina dettaglio',
            deleterelation: 'Cancella relazione',
            deleterelationconfirm: 'Sei sicuro di voler cancellare questa relazione?',
            description: 'Descrizione',
            editcard: 'Modifica scheda',
            editdetail: 'Modifica dettaglio',
            editrelation: 'Modifica relazione',
            errorexisting:
                'La relazione "{0}" è già presente. Modificare quella esistente oppure eliminarla prima di crearne una nuova.',
            extendeddata: 'Dati estesi',
            mditems: 'elementi',
            missingattributes: 'Attributi obbligatori mancanti',
            opencard: 'Apri scheda collegata',
            opendetail: 'Visualizza dettaglio',
            relationdata: 'Dati della relazione',
            type: 'Tipo'
        },
        reports: {
            csv: 'CSV',
            download: 'Scarica',
            format: 'Formato',
            msgs: {
                creationerror: 'Ci sono stati degli errori durante la creazione del report.',
                creationstarted:
                    'La generazione del report è iniziata. Riceverai una notifica non appena sarà pronto per il download.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Stampa',
            reload: 'Ricarica',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Default'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Giornaliera'
                        },
                        monthly: {
                            description: 'Mensile'
                        },
                        once: {
                            description: 'Singola occorrenza'
                        },
                        weekly: {
                            description: 'Settimanale'
                        },
                        yearly: {
                            description: 'Annuale'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Default'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Nessuno'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Aggiungi Tematismo',
            analysisType: 'Tipo di Analisi',
            attribute: 'Attributi',
            bordersIntervals: "Configurazione errata dei limiti dell'intervallo",
            calculateRules: 'Genera regole di stile',
            clearThematism: 'Cancella Tematismo',
            color: 'Colore',
            defineLegend: 'Definizione Legenda',
            defineThematism: 'Definizione Tematismo',
            deletethematism: 'Sicuro di voler eliminare questo Tematismo?',
            editThematism: 'Modifica configurazione regole',
            function: 'Funzione',
            generate: 'Genera',
            geoAttribute: 'Attributo Geografico',
            graduated: 'Graduato',
            highlightSelected: 'Evidenzia elemento selezionato',
            intervals: 'Intervalli',
            legend: 'Legenda',
            name: 'Nome',
            newThematism: 'Nuovo Tematismo',
            punctual: 'Puntuale',
            quantity: 'Quantità',
            segments: 'Segmenti',
            source: 'Fonte Dati',
            table: 'Tabella',
            thematism: 'Tematismi',
            value: 'Valore'
        },
        widgets: {
            ai: {
                copiedtoattribute: "Messaggio copiato nell'attributo {0}",
                copytoattribute: "Copia nell'attributo",
                errors: {
                    attributenotfound: 'Attributo con nome {0} non trovato',
                    wrongtype: 'Il messaggio può essere copiato solo in un attributo di tipo String o Text'
                },
                overrideattribute: 'Questa azione sovrascriverà il valore esistente. Sei sicuro?'
            },
            attachment: {
                actions: {
                    exitwithoutsave: 'Esci senza salvare',
                    stayinedit: 'Torna alla modifica'
                },
                savebeforeexit: 'Ci sono delle modifiche non salvate negli allegati. Vuoi salvarle prima di uscire?',
                saveprocessmessage:
                    "Gli allegati possono essere salvati solo in schede esistenti. Premendo su salva, verrà creata l'istanza di processo con i dati presenti nella form."
            },
            calendar: {
                error: 'Il parametro {0} non è settato',
                errorwrong: 'Il parametro {0} non è settato correttamente'
            },
            customform: {
                addrow: 'Aggiungi riga',
                clonerow: 'Clona riga',
                datanotvalid: 'Dati non validi',
                deleterow: 'Cancella riga',
                editrow: 'Modifica riga',
                export: 'Esporta',
                import: 'Importa',
                importexport: {
                    expattributes: 'Dati da esportare',
                    file: 'File',
                    filename: 'Nome file',
                    format: 'Formato',
                    importmode: 'Modalità di import',
                    keyattributes: 'Attributi chiave',
                    missingkeyattr: 'Scegli almeno un attributo chiave',
                    modeadd: 'Aggiungi',
                    modemerge: 'Aggiorna',
                    modereplace: 'Sostituisci',
                    separator: 'Separatore'
                },
                refresh: 'Aggiorna ai valori predefiniti'
            },
            linkcards: {
                checkedonly: 'Solo selezionati',
                editcard: 'Modifica scheda',
                opencard: 'Apri scheda',
                refreshselection: 'Applica selezione di default',
                removefilterconfirm: "L'azione rimuoverà il filtro attuale. Vuoi procedere?",
                selectioncleared: 'Selezione pulita',
                togglefilterdisabled: 'Disabilita filtro griglia',
                togglefilterenabled: 'Abilita filtro griglia'
            },
            notimplemented: 'Widget <strong>{0}</strong> non implementato',
            notsupportedinline:
                'La modalità inline non è supportata per questo tipo di widget. Per favore correggi la configurazione del widget',
            required: 'Questo widget è obbligatorio'
        }
    });

    function cleardata(obj) {
        for (var key in obj) {
            if (typeof obj[key] === 'string') {
                obj[key] = obj[key].replace(/^<em>(.+)<\/em>$/, '$1');
            } else if (typeof obj[key] === 'object') {
                cleardata(obj[key]);
            }
        }
    }
    cleardata(CMDBuildUI.locales.Locales);
})();
