(function () {
    Ext.define('CMDBuildUI.locales.ro.Locales', {
        requires: ['CMDBuildUI.locales.ro.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ro',
        administration: CMDBuildUI.locales.ro.LocalesAdministration.administration,
        ai: {
            clearconversation: 'Ștergeți conversația',
            clearconversationmessage: 'Sunteți sigur că doriți să goliți conversația curentă?',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: 'Ne pare rău, au apărut probleme la răspuns.',
            headertooltip: 'CMDBuild chat bot',
            introtextbody: 'Pentru a începe conversația, descrie cererea ta în câmpul dedicat.',
            introtexttitle: 'Cu ce vă pot ajuta?',
            loading: 'Încărcare ...',
            newchat: 'Conversație nouă',
            newchattooltip: 'Începeți o nouă conversație',
            questionemptytext: 'Scrieți întrebarea aici ...',
            resources: 'Surse',
            viewrelatedcard: 'Vizualizați cardul asociat',
            viewresource: 'Vizualizați resursa'
        },
        arialabels: {
            buttonplaceholder: 'Buton',
            iframeplaceholder: 'Editor de text',
            languageselector: 'Selectare limbă',
            resizepanel: {
                horizontally: 'Redimensionați panourile pe orizontală',
                vertically: 'Redimensionați panourile pe verticală'
            },
            usermenu: 'Meniu utilizator'
        },
        attachments: {
            add: 'Adauga atasament',
            attachmenthistory: 'Istoricul atașamentelor',
            author: 'Autor',
            authordescription: 'Descrierea autorului',
            browse: 'Răsfoiți &hellip;',
            category: 'Categorie',
            code: 'Cod',
            creationdate: 'Data crearii',
            deleteattachment: 'Ștergeți atașamentul',
            deleteattachment_confirmation: 'Sunteți sigur că doriți să ștergeți acest atașament?',
            description: 'Descriere',
            download: 'Descarca',
            dropfiles: 'Plasați cardurile aici',
            editattachment: 'Modificați atașamentul',
            errordms: 'DMS este dezactivat și nu este posibil salvarea fișierelor',
            file: 'Fişier',
            filealreadyinlist: 'Fișierul {0} există deja',
            filename: 'Nume de fișier',
            fileview: 'Vedeți atașamentul',
            invalidfiles: 'Eliminați fișierele nevalide',
            majorversion: 'Versiune majoră',
            maxsize: 'Dimensiunea maximă permisă a fișierului este de {0} MB.',
            metadata: 'Metadate',
            modificationdate: 'Data modificării',
            new: 'Atașament nou',
            nocategory: 'Necategorizat',
            preview: 'previzualizare',
            removefile: 'Șterge fișier',
            statuses: {
                empty: 'Fișier gol',
                error: 'Eroare',
                extensionNotAllowed: 'Extensia de fișier nu este permisă',
                loaded: 'Încărcat',
                ready: 'Gata',
                toolarge: 'Prea mare'
            },
            successupload: '{0} atașamente încărcate.',
            uploadfile: 'Incarca fisier...',
            version: 'Versiune',
            viewhistory: 'Vedeți istoricul atașamentelor',
            viewmetadata: 'Vedeți metadatele atașamentului',
            warningmessages: {
                atleast:
                    '{0}: au fost încărcate {1} atașamente de tip „{2}”. Această categorie are cel puțin {3} atașamente',
                exactlynumber:
                    '{0}: au fost încărcate {1} atașamente de tip „{2}”. Această categorie are {3} atașamente',
                maxnumber:
                    '{0}: au fost încărcate {1} atașamente de tip „{2}”. Această categorie are maximum {3} atașamente'
            },
            wrongfileextension: 'Nu este permisă încărcarea fișierelor cu extensia {0}'
        },
        bim: {
            bimViewer: 'Vizualizator Bim',
            card: {
                label: 'Card'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Dimensiunea casetei de delimitare de-a lungul axei X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Dimensiunea casetei de delimitare de-a lungul axei Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Dimensiunea casetei de delimitare de-a lungul axei Z',
                calculated: 'Calculat',
                description: 'Descriere',
                guid: 'GUID',
                label: 'Proprietăți',
                name: 'Nume',
                type: 'Tip',
                uuid: 'UUID'
            },
            layers: {
                label: 'Straturi',
                menu: {
                    hideAll: 'Ascunde tot',
                    hideAllLayers: 'Ascunde toate straturile',
                    hideAllXeokit: 'Ascundeți-le pe toate {0}',
                    showAll: 'Arătați tot',
                    showAllLayers: 'Arătați toate straturile',
                    showAllXeokit: 'Arătați toate {0}'
                },
                name: 'Nume',
                quantity: 'Cantitate',
                visibility: 'Vizibilitate'
            },
            menu: {
                camera: 'Cameră',
                disableslice: 'Dezactivează obiectul slice',
                enableslice: 'Activați obiectul slice',
                frontView: 'Vedere din față',
                mod: 'Controale pentru vizualizare',
                ortho: 'Comutați la vizualizarea ortogonală',
                orthographic: 'Cameră ortografică',
                pan: 'Mutați',
                panXeokit: 'Comenzi pentru vizualizare: derulați (Shift)',
                perspe: 'Comutați la vizualizarea în perspectivă',
                perspective: 'Cameră de perspectivă',
                resetSlice: 'Șterge tăierea',
                resetView: 'Resetează vederea',
                rotate: 'Rotiți',
                rotateXeokit: 'Comenzile vizualizatorului: rotiți',
                save: 'Salvați vizualizarea',
                shortcuts: {
                    down: 'Rotiți în jos',
                    left: 'Rotiți la stânga',
                    right: 'Rotiți la dreapta',
                    title: 'Comenzi rapide de la tastatură',
                    up: 'Rotiți în sus',
                    zoomin: 'Măriți',
                    zoomout: 'Micșorați'
                },
                sideView: 'Vedere laterală',
                threeD: 'Comutați la vizualizarea 3D',
                topView: 'Vedere de sus',
                twoD: 'Comutați la vizualizarea 2D'
            },
            navCube: {
                back: 'Înapoi',
                bottom: 'Partea de jos',
                front: 'Față',
                left: 'Stânga',
                right: 'Dreapta',
                top: 'Top'
            },
            showBimCard: 'Deschideți vizualizatorul 3D',
            tree: {
                arrowTooltip: 'Selectați elementul',
                label: 'Obiecte',
                multipleElements: 'Selectați elemente',
                open_card: 'Deschideți cardul aferent',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Anulați articolele selectate',
            addfields: 'Adăugați câmpuri',
            alertdownloadattachments:
                'Unele atașamente nu vor fi descărcate deoarece nu au fost încă salvate pe server',
            alertnoattachmentsdownload:
                'Niciun atașament nu va fi descărcat deoarece nu au fost încă salvate pe server',
            cancelselection: 'Anulați selecția',
            confirmabort: 'Anulați {0} instanțe de proces. Sunteți sigur că doriți să continuați?',
            confirmdelete: 'Ștergeți {0} carduri. Sunteți sigur că doriți să continuați?',
            confirmdeleteattachements: 'Ștergeți {0} atașamente. Sunteți sigur că doriți să continuați?',
            confirmedit: 'Modificați {0} pe {1} carduri. Sunteți sigur că doriți să continuați?',
            delete: 'Ștergeți elementele selectate',
            download: 'Descărcați atașamentele selectate',
            downloadall: 'Descărcați toate atașamentele',
            edit: 'Editați elementele selectate',
            missingfields: 'Este necesar să adăugați următoarele câmpuri pentru validarea corectă a formularului: {0}',
            noattributeselected: 'Vă rugăm să selectați cel puțin un atribut.',
            selectall: 'Selectați toate elementele'
        },
        calendar: {
            active_expired: 'Activ/Expirat',
            add: 'Adăugați program',
            advancenotification: 'Notificare cu zile în avans',
            allcategories: 'Toate categoriile',
            alldates: 'Toate datele',
            associatedcard: 'Card asociat',
            calculated: 'Calculat',
            calendar: 'Calendar',
            cancel: 'Marcați ca anulat',
            category: 'Categorie',
            class: 'Clasă',
            cm_confirmcancel: 'Sunteți sigur că doriți să marcați programele selectate ca anulate?',
            cm_confirmcomplete: 'Sunteți sigur că doriți să marcați programele selectate ca finalizate?',
            cm_markcancelled: 'Marcați programele selectate ca anulate',
            cm_markcomplete: 'Marcați programele selectate ca finalizate',
            complete: 'Marcați ca gata',
            completed: 'Efectuat',
            date: 'Data',
            days: 'Zile',
            delaybeforedeadline: 'Întârziere înainte de termenul limită',
            delaybeforedeadlinevalue: 'Întârziere înainte de valoarea termenului limită',
            description: 'Descriere',
            editevent: 'Editați programul',
            enddate: 'Data de încheiere',
            endtype: 'Tip final',
            event: 'Programa',
            execapplyfilter: 'Nu se poate executa applyAdvanceFilter deoarece există mai multe surse de date',
            execremovefilter: 'Nu se poate executa removeAdvanceFilter deoarece există mai multe surse de date',
            executiondate: 'Data executiei',
            frequency: 'Frecvență',
            frequencymultiplier: 'Multiplicator de frecventa',
            grid: 'Grilă',
            leftdays: 'Zile ramase',
            londdescription: 'Descriere completa',
            manual: 'Manual',
            maxactiveevents: 'Numărul maxim de expirări active',
            messagebodydelete: 'Doriți să eliminați regulile de termen?',
            messagebodyplural: 'Există {0} reguli de programare',
            messagebodyrecalculate: 'Doriți să recalculați regula programelor cu noua dată?',
            messagebodysingular: 'Există {0} regulă de programare',
            messagetitle: 'Programează recalcularea',
            missingdays: 'Zile lipsă',
            next30days: 'Următoarele 30 de zile',
            next7days: 'Următoarele 7 zile',
            notificationtemplate: 'Șablon folosit pentru notificare',
            notificationtext: 'Text de notificare',
            occurencies: 'Numărul de apariții',
            operation: 'Operațiune',
            partecipantgroup: 'Grupul de participanți',
            partecipantuser: 'Utilizator participant',
            priority: 'Prioritate',
            recalculate: 'Recalculați',
            referent: 'Referent',
            scheduler: 'Programator',
            sequencepaneltitle: 'Generați programari',
            startdate: 'Data de început',
            status: 'Status',
            today: 'Astăzi',
            type: 'Tip',
            viewevent: 'Vezi programul',
            waiteventgeneration: 'Se generează evenimentele. Te rugăm să aștepți finalizarea lor',
            widgetcriterion: 'Criteriul de calcul',
            widgetemails: 'E-mail',
            widgetgotocard: 'Accesați cardul sursă',
            widgetsourcecard: 'Card sursă'
        },
        chat: {
            title: 'Chat'
        },
        classes: {
            cards: {
                addcard: 'Adăugați card',
                clone: 'Clonează',
                clonewithrelations: 'Clonează cardul și relațiile',
                deletebeaware: 'Fiți conștienți de faptul că:',
                deleteblocked: 'Nu este posibil să continuați cu ștergerea deoarece există relații cu {0}.',
                deletecard: 'Ștergeți cardul',
                deleteconfirmation: 'Sunteți sigur că doriți să ștergeți acest card?',
                deleterelatedcards: 'de asemenea, {0} carduri asociate vor fi șterse',
                deleterelations: 'relațiile cu {0} carduri vor fi șterse',
                label: 'Carduri',
                modifycard: 'Modificați cardul',
                opencard: 'Deschide cardul',
                print: 'Imprimați cardul'
            },
            simple: 'Simplu',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Adăuga',
                apply: 'Aplicați',
                cancel: 'Anulați',
                clear: 'Curăță',
                close: 'Închide',
                copy: 'Copiază',
                copyerrormessage: 'Copiați mesajul de eroare în clipboard',
                delete: 'Șterge',
                edit: 'Editați',
                execute: 'A executa',
                forward: 'Înainte',
                help: 'Ajutor',
                hide: 'Hide',
                load: 'Sarcină',
                open: 'Deschis',
                previous: 'Anterior',
                refresh: 'Actualizează datele',
                remove: 'Eliminați',
                save: 'Salvați',
                saveandapply: 'Salvați și aplicați',
                saveandclose: 'Salveaza si inchide',
                search: 'Căutați',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Căutare...'
            },
            and: 'și',
            attributes: {
                boolfalse: 'fals',
                booltrue: 'Adevărat',
                link: {
                    errlabel: 'Eticheta este necesară',
                    errurl: 'Url este obligatoriu atunci când eticheta nu este goală',
                    label: 'Eticheta',
                    url: 'Url'
                },
                nogroup: 'Date de bază'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H: i: s',
                time: 'A lui'
            },
            editor: {
                clearhtml: 'Ștergeți HTML',
                expand: 'Extinde editorul',
                reduce: 'Reducere editor',
                signature: 'Adăugați semnătura',
                unlink: 'Deconectați',
                unlinkmessage: 'Transformați hyperlinkul selectat în text.'
            },
            grid: {
                disablemultiselection: 'Dezactivați selecția multiplă',
                enamblemultiselection: 'Activați selecția multiplă',
                export: 'Export de date',
                filterremoved: 'Filtrul setat a fost eliminat',
                import: 'Importă date',
                itemnotfound: 'Element nu a fost găsit',
                lastUpdate: '<em>Last update</em>',
                list: 'Listă',
                opencontextualmenu: 'Deschideți meniul contextual',
                print: 'Imprimare',
                printcsv: 'Imprimați ca CSV',
                printmap: 'Imprimați harta',
                printodt: 'Imprimați ca ODT',
                printpdf: 'Imprimați ca PDF',
                row: 'Articol',
                rows: 'Articole',
                subtype: 'Subtip',
                thematism: 'Nu îl veți putea utiliza până când procesul nu este salvat.'
            },
            tabs: {
                activity: 'Activitate',
                attachment: 'Atașament',
                attachments: 'Atașamente',
                card: 'Card',
                clonerelationmode: 'Modul de clonare a relației',
                details: 'Detalii',
                emails: 'E-mail',
                history: 'Istorie',
                notes: 'Note',
                relations: 'Relaţii',
                schedules: 'Programe',
                sources: 'Surse'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: 'Afișați toate',
                showingnitems: 'Elemente afișate: {0}'
            },
            pareto: {
                cumulative: 'Cumulativ',
                cumulativeperc: '% cumulativ'
            },
            tools: {
                download: 'Descărcați graficul',
                downloaddata: 'Descarcați datele graficului',
                gridhide: 'Ascundeți grila de date',
                gridshow: 'Arătați grila de date',
                openinpopup: 'Deschide în pop-up',
                reload: 'Reîncărcați',
                showall: 'Afișați toate'
            }
        },
        dependencytree: {
            cardinfo: 'Card',
            filteredid: 'Cardul curent nu aparține acestui arbore',
            gerarchicview: 'Vizualizare ierarhică',
            name: 'Arbore de dependențe',
            nocardselected: 'Niciun card selectat',
            opendependencytree: 'Deschideți arborele de dependențe',
            printjpeg: 'Imprimați ca JPEG',
            printpdf: 'Imprimați ca PDF',
            printpng: 'Imprimați ca PNG',
            printsvg: 'Imprimați ca SVG',
            reopengraph: 'Redeschideți graful cu noua bază',
            selectcard: 'Selectați nodul de pornire',
            wrongid: 'ID-ul introdus este incorect.'
        },
        emails: {
            abort: 'Anulați',
            addattachmentsfromdocumentarchive: 'Adăugați atașamente din arhiva documentelor',
            alredyexistfile: 'Există deja un card cu acest nume',
            archivingdate: 'Data arhivării',
            attachfile: 'Ataseaza fisier',
            attachmentsmessage:
                'Șablonul necesită atașarea documentelor cardului. Nu îl veți putea utiliza până când salvați procesul.',
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Scrieți un email',
            composefromtemplate: 'Compuneți din șablon',
            delay: 'Întârziere',
            delays: {
                day1: 'În 1 zi',
                days2: 'În 2 zile',
                days4: 'În 4 zile',
                hour1: '1 oră',
                hours2: '2 ore',
                hours4: '4 ore',
                month1: 'În 1 luna',
                negativeday1: 'cu 1 zi inainte',
                negativedays2: 'cu 2 zile inainte',
                negativedays4: 'cu 4 zile inainte',
                negativehour1: 'cu 1 oră înainte',
                negativehours2: 'cu 2 ore inainte',
                negativehours4: 'cu 4 ore înainte',
                negativemonth1: 'cu 1 luna inainte',
                negativeweek1: 'cu 1 saptamana inainte',
                negativeweeks2: 'cu 2 saptamani inainte',
                none: 'Nici unul',
                week1: 'În 1 saptamana',
                weeks2: 'În 2 săptămâni'
            },
            dmspaneltitle: 'Selectați atașamentele din baza de date',
            edit: 'Editați',
            fileattributesmessage:
                'Există o dependență de un atribut de tip file. Până când salvați procesul, nu îl veți putea utiliza.',
            from: 'Din',
            gridrefresh: 'Reîmprospătare grilă',
            groupbystatus: 'Grupează pe status',
            keepsynchronization: 'Păstrați sincronizarea',
            maxsize: 'Dimensiunea totală maximă permisă pentru atașamente este {0}.',
            message: 'Mesaj',
            regenerateallemails: 'Regenerați toate e-mailurile',
            regenerateemail: 'Regenerați e-mailul',
            reloadconfirmationmessage: 'E-mailurile nefinalizate vor fi șterse. Vrei sa continui?',
            remove: 'Eliminați',
            remove_confirmation: 'Sunteți sigur că doriți să ștergeți acest e-mail?',
            reply: 'răspuns',
            replyprefix: 'Pe {0}, {1} a scris:',
            searchattachment: 'Căutați atașament',
            searchcard: 'Căutați card',
            selectaclass: 'Selectați o clasă',
            sendemail: 'Trimiteți email',
            sendingemail: 'Trimiteți e-mail',
            signature: 'Semnătură',
            size: 'Dimensiune',
            status: 'Status',
            statuses: {
                draft: 'Ciorne',
                error: 'Eroare',
                new: 'Nou',
                outgoing: 'Ieșire',
                received: 'Primit',
                sent: 'Trimis',
                skipped: 'Omis'
            },
            subject: 'Subiect',
            templateattachments: 'Acest e-mail conține atașamente încărcate în automat prin șablon',
            to: 'La',
            view: 'Vedere'
        },
        errors: {
            actionnotallowed: 'Acțiunea nu este permisă',
            autherror: 'Nume de utilizator sau parolă greșite',
            classnotfound: 'Clasa {0} nu a fost găsită',
            fieldrequired: 'Acest câmp este obligatoriu',
            invalidfilter: 'Filtru nevalid',
            nodatatoexport: 'Graficul nu are date de exportat',
            notfound: 'Element nu a fost găsit',
            value3chars: 'Valoarea trebuie să conțină cel puțin 3 caractere.'
        },
        filters: {
            actions: 'Acțiuni',
            addfilter: 'Adăugați filtru',
            any: 'Cel puțin unul',
            attachments: 'Atașamente',
            attachmentssearchtext: 'Textul de căutare în atașamente',
            attribute: 'Alegeți un atribut',
            attributes: 'Atribute',
            clearfilter: 'Șterge filtrul',
            clone: 'Clonează',
            copyof: 'Copia',
            currentgroup: 'Grupul curent',
            currentuser: 'Utilizator curent',
            defaultset: 'Setați ca implicit',
            defaultunset: 'Deconectat de la implicit',
            description: 'Descriere',
            domain: 'Domeniu',
            errorname: 'Numele filtrului este deja utilizat',
            filterdata: 'Filtrați datele',
            fromfilter: 'Din filtru',
            fromselection: 'Din selecție',
            group: 'grup',
            ignore: 'Ignora',
            migrate: 'Migrează',
            name: 'Nume',
            newfilter: 'Filtru nou',
            noone: 'Nici unul',
            operator: 'Operator',
            operators: {
                and: 'Și',
                beginswith: 'Începe cu',
                between: 'Între',
                contained: 'Conţinut',
                containedorequal: 'Conținut sau egal',
                contains: 'Conține',
                containsorequal: 'Conține sau egal',
                descriptionbegin: 'Descrierea începe cu',
                descriptioncontains: 'Descrierea conține',
                descriptionends: 'Descrierea se termină cu',
                descriptionnotbegin: 'Descrierea nu începe cu',
                descriptionnotcontain: 'Descrierea nu conține',
                descriptionnotends: 'Descrierea nu se termină cu',
                different: 'Diferit',
                doesnotbeginwith: 'Nu începe cu',
                doesnotcontain: 'Nu contine',
                doesnotendwith: 'Nu se termină cu',
                endswith: 'Se termină cu',
                equals: 'Egal',
                greaterthan: 'Mai mare ca',
                isnotnull: 'Nu este nulă',
                isnull: 'Este null',
                lessthan: 'Mai puțin decât',
                or: 'Sau'
            },
            relations: 'Relaţii',
            removeattribute: 'Eliminați atributul',
            removeblock: 'Eliminați bloc',
            type: 'Tip',
            typeinput: 'Parametru de intrare',
            typeorchoosetheattributename: 'Tastați sau alegeți numele atributului',
            user: 'Utilizator',
            value: 'Valoare'
        },
        gis: {
            card: 'Card',
            cardsMenu: 'Meniu Carduri',
            code: 'Cod',
            contextmenu: {
                coordinates: 'Obțineți coordonatele',
                coordinatescopiedmessage: 'Coordonatele au fost copiate în clipboard'
            },
            description: 'Descriere',
            extension: {
                errorCall: 'Eroare',
                noResults: 'Nici un rezultat'
            },
            externalServices: 'Servicii externe',
            geographicalAttributes: 'Atribute geografice',
            geoserverLayers: 'Straturi geoserver',
            labels: {
                all: 'Toate',
                alltitle: 'Arătați toate etichetele',
                alltitleclass: 'Arătați toate etichetele claselor',
                clear: 'Ștergeți căutarea',
                hidden: 'Ascuns',
                hiddentitle: 'Ascunde etichetele',
                hideinfowindow: 'Ascundeți fereastra de informații',
                label: 'Etichete',
                search: 'Căutați pe hartă',
                selected: 'Selectat',
                selectedtitle: 'Arătați eticheta articolului selectat',
                showinfowindow: 'Arătați fereastra de informații'
            },
            layers: 'Straturi',
            layersTab: {
                clearPreferences: 'Eliminați preferințele de strat',
                preferencesCleared: 'Preferințele pentru straturi au fost șterse',
                preferencesSaved: 'Preferințele pentru straturi au fost salvate',
                savePreferences: 'Salvați preferințele pentru straturi',
                updatePreferences: 'Actualizați preferințele pentru straturi'
            },
            list: 'Listă',
            longpresstitle: 'Elemente geografice din zonă',
            map: 'Hartă',
            mapServices: 'Servicii de hartă',
            menu: {
                font: 'Marimea fontului',
                options: 'Opțiuni'
            },
            position: 'Poziţie',
            root: 'Root',
            sourceMap: {
                default: 'Folosiți vizualizarea implicită',
                satellite: 'Folosiți vizualizarea prin satelit'
            },
            tree: 'Arborele de navigație',
            type: 'Tip',
            view: 'Vedere',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Numele activității',
            activityperformer: 'Realizator de activitate',
            attributes: 'Atribute ({0})',
            begindate: 'Data de început',
            enddate: 'Data de încheiere',
            fieldname: 'Numele câmpului',
            fulldata: 'Toate datele',
            onlychanges: 'Doar modificările',
            otherrelations: 'Alte relații',
            processstatus: 'Status',
            referencerelation: 'Relații reference',
            relation: 'Relație',
            system: 'Sistem',
            type: 'Tip',
            user: 'Utilizator',
            userdescription: 'Descrierea utilizatorului',
            value: 'Valoare',
            viewmode: 'Modul de vizualizare'
        },
        homepage: {
            search: {
                airequestproblem:
                    '<em>A problem occurred while processing the request. Please try again later or contact the administrator.</em>',
                aisearcherror: '<em>Unable to complete the search</em>',
                goodafternoon: 'Bună după-amiaza',
                goodevening: 'Bună seara',
                goodmorning: 'Bună dimineața',
                goodnight: 'Noapte bună',
                noresults: '<em>No results found</em>',
                noresultshint: '<em>Try different keywords or check the spelling</em>',
                resultsfor: '<em>Results for: {0}</em>'
            }
        },
        iconscategories: {
            accessibility: 'Accesibilitate',
            animals: 'Animale',
            arrows: 'Săgeți',
            audiovideo: 'Audio video',
            automotive: 'Automobile',
            autumn: 'Toamnă',
            beverage: 'Băuturi',
            buildings: 'Clădiri',
            business: 'Afaceri',
            charity: 'Caritate',
            chat: 'Chat',
            chess: 'Șah',
            childhood: 'Copilărie',
            code: 'Cod',
            communication: 'Comunicație',
            computers: 'Calculatoare',
            construction: 'Construcții',
            currency: 'Monedă',
            datetime: 'Data și ora',
            design: 'Design',
            editors: 'Editori',
            education: 'Educație',
            emoji: 'Emoji',
            energy: 'Energie',
            files: 'Fișiere',
            finance: 'Finanțe',
            food: 'Alimente',
            fruitvegetable: 'Legume fructe',
            games: 'Jocuri',
            gamingtabletop: 'Jocuri de masă',
            gender: 'Gen',
            general: 'General',
            halloween: 'Halloween',
            hands: 'Mâini',
            hotel: 'Hotel',
            household: 'Gospodărie',
            images: 'Imagini',
            interfaces: 'Interfețe',
            logistics: 'Logistică',
            maps: 'Hartă',
            maritime: 'Maritim',
            marketing: 'Marketing',
            mathematics: 'Matematică',
            medical: 'Medical',
            moving: 'Mișcare',
            music: 'Muzică',
            objects: 'Obiecte',
            paymentsshopping: 'Metode de plată',
            political: 'Politică',
            religion: 'Religie',
            sciencefiction: 'Ficțiune',
            security: 'Securitate',
            shapes: 'Forme',
            shopping: 'Cumpărături',
            social: 'Social',
            spinners: 'Spinners',
            sports: 'Sporturi',
            spring: 'Primăvara',
            status: 'Status',
            toggle: 'Comutare',
            travel: 'Călătorie',
            userspeople: 'Utilizatori și persoane',
            vehicles: 'Vehicule',
            weather: 'Vremea',
            winter: 'Iarnă'
        },
        importexport: {
            database: {
                uri: 'Baza de date URI',
                user: 'Utilizator baze de date'
            },
            downloadreport: 'Descărcați raportul',
            emailfailure: 'A apărut o eroare la trimiterea e-mailului!',
            emailmessage: 'Atașat raportul de import al fișierului „{0}” la data {1}',
            emailsubject: 'Raport de import de date',
            emailsuccess: 'E-mailul a fost trimis cu succes!',
            export: 'Export',
            exportalldata: 'Toate datele',
            exportfiltereddata: 'Doar datele care se potrivesc cu filtrul de grilă',
            gis: {
                importconfiguration: 'Importați configurațiile',
                shapeimportdisabled: 'Importul formelor nu este activat pentru acest șablon',
                shapeimportenabled: 'Importul shape-urilor este activat pentru acest șablon'
            },
            ifc: {
                card: 'Card',
                project: 'Proiect',
                sourcetype: 'Importat din'
            },
            import: 'Import',
            importresponse: 'Import răspuns',
            response: {
                created: 'Carduri create',
                deleted: 'Carduri șterse',
                errors: 'Erori',
                linenumber: 'Numărul liniei',
                message: 'Mesaj',
                modified: 'Carduri modificate',
                notprocessed: 'Carduri neprocesate',
                processed: 'Rânduri prelucrate',
                recordnumber: 'Numar record',
                unchanged: 'Cardurile nu au fost modificate'
            },
            sendreport: 'Trimiteți raport',
            template: 'Șablon',
            templatedefinition: 'Definiție șablon'
        },
        joinviews: {
            active: 'Activati',
            addview: 'Adăugați vizualizare',
            alias: 'Alias',
            attribute: 'Atribut',
            attributecustomization: 'Personalizarea atributerlor',
            attributes: 'Atribute',
            attributeschoice: 'Alegerea atributelor',
            attributesof: 'Atribute ale: {0}',
            createview: 'Creați vizualizare',
            datasorting: 'Sortarea datelor',
            delete: 'Șterge',
            deleteview: 'Șterge vizualizarea',
            deleteviewconfirm: 'Sunteți sigur că doriți să ștergeți această vizualizare?',
            description: 'Descriere',
            disable: 'Dezactivați',
            domainalias: 'Alias ​​de domeniu',
            domainsof: 'Domeniile lui {0}',
            edit: 'Editați',
            editview: 'Editați configurația vizualizării',
            enable: 'Permite',
            fieldsets: 'Seturi de câmpuri',
            filters: 'Filtre',
            generalproperties: 'Proprietăți generale',
            group: 'grup',
            innerjoin: 'Îmbinare interioară',
            jointype: 'Tip de alăturare',
            joinview: 'Vedeți din join',
            klass: 'Clasă',
            manageview: 'Gestionați vizualizarea',
            masterclass: 'Master-class',
            masterclassalias: 'Alias ​​de master class',
            name: 'Nume',
            newjoinview: 'Vizualizare nouă de la înscriere',
            outerjoin: 'Îmbinare exterioară',
            pleaseseleceavalidmasterclass: 'Vă rugăm să selectați o clasă de master validă',
            refreshafteredit: 'Doriți să reîmprospătați pagina pentru a vedea modificările?',
            selectatleastoneattribute:
                'Vă rugăm să selectați cel puțin un atribut de afișat în grilă și în grilă redusă la pasul 4.',
            selectdisplayattributegrid:
                'Vă rugăm să selectați cel puțin un atribut de afișat în grilă și unul în grilă redusă',
            showingrid: 'Arătați în grilă',
            showinreducedgrid: 'Arătați în grilă redusă',
            targetalias: 'Alias ​​de clasă țintă'
        },
        login: {
            buttons: {
                login: 'Autentificare',
                logout: 'Schimbă utilizatorul'
            },
            fields: {
                group: 'grup',
                language: 'Limbă',
                password: 'Parola',
                tenants: 'Chiriaş',
                username: 'Nume de utilizator'
            },
            loggedin: 'Autentificat',
            sso: {
                loginfailure: 'Eroare de autentificare',
                loginwith: 'Conectați-vă cu {0}',
                or: 'sau',
                usernotenabled: 'Utilizator neautorizat'
            },
            title: 'Autentificare',
            welcome: 'Bine ați revenit {0}.'
        },
        main: {
            administrationmodule: 'Modul de administrare',
            alerts: 'Alerte',
            baseconfiguration: 'Configurație de bază',
            cardlock: {
                lockedmessage: 'Nu puteți edita acest card deoarece {0} îl editează.',
                someone: 'cineva'
            },
            changegroup: 'Schimbați grupul',
            changetenant: 'Schimbați {0}',
            confirmchangegroup: 'Sunteți sigur că vrei să schimbi grupul?',
            confirmchangetenants: 'Sunteți sigur că doriți să schimbați chiriașii activi?',
            confirmdisabletenant: 'Sunteți sigur că doriți să dezactivați indicatorul „Ignorați chiriașii”?',
            confirmenabletenant: 'Sunteți sigur că doriți să activați indicatorul „Ignorați chiriașii”?',
            description: 'Descriere',
            ignoretenants: 'Ignorați {0}',
            info: 'Info',
            logo: {
                cmdbuild: 'Logo CMDBuild',
                cmdbuildready2use: 'Logo CMDBuild READY2USE',
                companylogo: 'Logoul companiei',
                hdaitsm: 'HDA ITSM',
                openmaint: 'Logo openMAINT'
            },
            logout: 'Deconectare',
            managementmodule: 'Modul de gestionare a datelor',
            multigroup: 'Grupuri multiple',
            multitenant: 'Multi {0}',
            navigation: 'Navigare',
            pagenotfound: 'Pagina nu a fost gasita',
            password: {
                change: 'Schimbaţi parola',
                confirm: 'Confirmă parola',
                email: 'Adresa de e-mail',
                err_confirm: 'Parolele nu se potrivesc.',
                err_diffprevious: 'Parola nu poate fi identică cu cea anterioară.',
                err_diffusername: 'Parola nu poate fi identică cu numele de utilizator.',
                err_length: 'Parola trebuie să aibă cel puțin {0} caractere.',
                err_reqdigit: 'Parola trebuie să conțină cel puțin o cifră.',
                err_reqlowercase: 'Parola trebuie să conțină cel puțin un caracter mic.',
                err_requppercase: 'Parola trebuie să conțină cel puțin o litera mare',
                expired: 'Parola ta a expirat. Trebuie schimbata acum.',
                forgotten: 'Am uitat parola',
                new: 'Parolă Nouă',
                old: 'Parola veche',
                recoverysuccess: 'V-am trimis un e-mail cu instrucțiuni pentru resetarea parolei.',
                reset: 'Reseteaza parola',
                saved: 'Parola salvată corect!'
            },
            pleasecorrecterrors: 'Vă rugăm să corectați erorile indicate!',
            preferences: {
                comma: 'Virgulă',
                decimalserror: 'Câmpul separator zecimal este obligatoriu',
                decimalstousandserror: 'Separatorul de mii și câmpurile zecimale trebuie să fie diferite',
                default: 'Default',
                defaultvalue: 'Valoare implicită',
                favouritemenulocation: 'Poziția meniul Favorites',
                firstdayofweek: 'Prima zi a săptămânii',
                gridpreferencesclear: 'Eliminați preferințele grilei',
                gridpreferencescleared: 'Preferințele grilei au fost șterse!',
                gridpreferencessave: 'Salvați preferințele grilei',
                gridpreferencessaved: 'Preferințele grilei au fost salvate!',
                gridpreferencesupdate: 'Actualizați preferințele grilei',
                groupemailsbystatus: 'Arătați emailurile grupat după status',
                labelcsvseparator: 'Separator CSV',
                labeldateformat: 'Formatul datei',
                labeldecimalsseparator: 'Separator zecimal',
                labellanguage: 'Limbă',
                labelthousandsseparator: 'Separator de mii',
                labeltimeformat: 'Format de timp',
                msoffice: 'Microsoft Office',
                no: 'Nu',
                notificationslabel: 'Chat și notificări',
                notificationssound: 'Redați sunet pentru notificări',
                notificationssoundvolume: 'Volumul notificărilor',
                period: 'Perioadă',
                preferredfilecharset: 'Codificare CSV',
                preferredofficesuite: 'Suita Office preferată',
                sendcancellationperiod: 'Perioada de anulare prin e-mail (secunde)',
                showcollapsed: 'Arătați listă restrânsă de meniu',
                showfovouritesafter: 'Arătați meniul de navigare',
                showfovouritesbefore: 'Arătați sub meniul de navigare',
                space: 'Spaţiu',
                thousandserror: 'Câmpul separator de mii este obligatoriu',
                timezone: 'Fus orar',
                twelvehourformat: 'format de 12 ore',
                twentyfourhourformat: 'format de 24 de ore',
                yes: 'Da'
            },
            searchinallitems: 'Căutați în toate articolele',
            searchtenant: 'Căutați tenant',
            selectdeselettenants: 'Selectați/Deselectați toate',
            treenavcontenttitle: '{0} din {1}',
            userpreferences: 'Preferințe'
        },
        menu: {
            allitems: 'Toate obiectele',
            classes: 'Clase',
            custompages: 'Pagini personalizate',
            dashboards: 'Tablouri de bord',
            dependencytrees: 'Arbori de dependențe',
            favourites: 'Favorite',
            favouritesadd: 'Adăugați la favorite',
            favouritesremove: 'Eliminați din favorite',
            homepage: 'Homepage',
            processes: 'Procesele',
            reports: 'Rapoarte',
            setasinitialpage: 'Setați ca pagină inițială',
            views: 'Vizualizări'
        },
        mobile: {
            config: {
                configure: 'Configurați dispozitivul mobil',
                customercode: 'Codul de client',
                devicename: 'Numele dispozitivului',
                regenerate: 'Regeneraţi',
                serverurl: 'Server URL'
            }
        },
        notes: {
            edit: 'Modificați notele'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Ștergeți notificarea',
            emptymessage: 'Nu există notificări de afișat.',
            label: 'Notificări',
            viewall: 'Vedeti tot'
        },
        notifier: {
            attention: 'Atenţie',
            copied: 'Copiat',
            copiedtotheclipboard: 'Text copiat în clipboard',
            error: 'Eroare',
            genericerror: 'Eroare generică',
            genericerrormessage: 'Pentru mai multe informații, verificați jurnalele sistemului',
            genericinfo: 'Informații generice',
            genericwarning: 'Avertisment generic',
            info: 'Info',
            more: 'Detalii suplimentare',
            success: 'Succes',
            warning: 'Avertizare'
        },
        patches: {
            apply: 'Aplicați patch',
            category: 'Categorie',
            description: 'Descriere',
            name: 'Nume',
            patches: 'Patch'
        },
        processes: {
            abortconfirmation: 'Sunteți sigur că doriți să opriți acest proces?',
            abortprocess: 'Anulați procesul',
            action: {
                advance: 'Avans',
                label: 'Acțiune'
            },
            activeprocesses: 'Procese active',
            allstatuses: 'Toate',
            editactivity: 'Modificați activitatea',
            openactivity: 'Activitate deschisă',
            resumeprocesses: 'Procesele care nu sunt suspendate nu pot fi reluate',
            startworkflow: 'Start',
            workflow: 'Fluxul de lucru'
        },
        relationGraph: {
            activity: 'activitate',
            allLabelsOnGraph: 'toate etichetele din grafic',
            card: 'Card',
            cardList: 'Lista de carduri',
            cardRelations: 'Relații',
            choosenaviagationtree: 'Alegeți arborele de navigare',
            class: 'Clasă',
            classList: 'Lista claselor',
            compoundnode: 'Gruparea nodurilor',
            disable: 'Dezactivați',
            edges: 'Arcurile',
            enable: 'Permite',
            labelsOnGraph: 'tooltip',
            level: 'Nivel',
            nodes: 'Noduri',
            openRelationGraph: 'Deschideți Grafic Relație',
            quantity: 'Cantitate',
            refresh: 'Reîmprospătati',
            relation: 'relație',
            relationGraph: 'Graficul de relații',
            reopengraph: 'Redeschideți graficul din acest nod'
        },
        relations: {
            adddetail: 'Adăugați detalii',
            addrelations: 'Adăugați relații',
            attributes: 'Atribute',
            begindate: 'Data de început',
            cardnotpresentmessage:
                'Acest card nu mai este disponibil. Pentru a continua modificarea va fi necesară alegerea unui alt card.',
            code: 'Cod',
            deletedetail: 'Ștergeți detaliu',
            deleterelation: 'Ștergeți relația',
            deleterelationconfirm: 'Sunteți sigur că doriți să ștergeți această relație?',
            description: 'Descriere',
            editcard: 'Editați cardul',
            editdetail: 'Editați detaliile',
            editrelation: 'Editați relația',
            errorexisting:
                'Relația “{0}” este deja prezentă. Editați sau ștergeți relația existentă înainte de a crea una nouă',
            extendeddata: 'Date extinse',
            mditems: 'articole',
            missingattributes: 'Lipsesc atribute obligatorii',
            opencard: 'Deschideți cardul aferent',
            opendetail: 'Arătați detalii',
            relationdata: 'Datelor relaţiei',
            type: 'Tip'
        },
        reports: {
            csv: 'CSV',
            download: 'Descarca',
            format: 'Format',
            msgs: {
                creationerror: 'Au fost erori la crearea raportului.',
                creationstarted: 'Crearea raportului a început. Veți fi anunțat când va fi gata pentru descărcare'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Imprimare',
            reload: 'Reîncărcați',
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
                            description: 'Zilnic'
                        },
                        monthly: {
                            description: 'Lunar'
                        },
                        once: {
                            description: 'O singura data'
                        },
                        weekly: {
                            description: 'Săptămânal'
                        },
                        yearly: {
                            description: 'Anual'
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
                        description: 'Nimeni'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Adăugați tematism',
            analysisType: 'Tipul de analiză',
            attribute: 'Atribut',
            bordersIntervals: 'Configurare greșită pentru limite de intervale',
            calculateRules: 'Generați reguli de stil',
            clearThematism: 'Anulează tematismul',
            color: 'Culoare',
            defineLegend: 'Definiția legendei',
            defineThematism: 'Definirea tematicii',
            deletethematism: 'Sunteți sigur că vrei să ștergi acest tematism?',
            editThematism: 'Editați configurarea regulilor',
            function: 'Funcţie',
            generate: 'Generează',
            geoAttribute: 'Atribut geografic',
            graduated: 'A absolvit',
            highlightSelected: 'Evidențiați elementul selectat',
            intervals: 'Intervale',
            legend: 'Legendă',
            name: 'Nume',
            newThematism: 'Noua tematică',
            punctual: 'Punctual',
            quantity: 'Cantitate',
            segments: 'Segmente',
            source: 'Sursă',
            table: 'Masa',
            thematism: 'Tematisme',
            value: 'Valoare'
        },
        widgets: {
            ai: {
                copiedtoattribute: '<em>Message copied in {0} attribute</em>',
                copytoattribute: '<em>Copy to attribute</em>',
                errors: {
                    attributenotfound: '<em>Attribute with name {0} not found</em>',
                    wrongtype: '<em>The message can only be copied only to a String or Text attribute</em>'
                },
                overrideattribute: '<em>This action will override the existing value. Are you sure?</em>'
            },
            attachment: {
                actions: {
                    exitwithoutsave: 'Iesire fara salvare',
                    stayinedit: 'Înapoi la editare'
                },
                savebeforeexit: 'Există modificări nesalvate în atașamente. Doriți să le salvați înainte de a ieși?',
                saveprocessmessage:
                    'Atașamentele pot fi salvate numai pe carduri existente. Apăsând pe salvare, instanța procesului va fi creată cu datele introduse în formular.'
            },
            calendar: {
                error: 'Parametrul {0} nu este setat',
                errorwrong: 'Parametrul {0} nu este setat corect'
            },
            customform: {
                addrow: 'Adăugați rând',
                clonerow: 'Clonează rândul',
                datanotvalid: 'Datele nu sunt valide',
                deleterow: 'Ștergeți rândul',
                editrow: 'Editați rândul',
                export: 'Export',
                import: 'Import',
                importexport: {
                    expattributes: 'Date de exportat',
                    file: 'Fişier',
                    filename: 'Nume de fișier',
                    format: 'Format',
                    importmode: 'Modul de import',
                    keyattributes: 'Atributele cheie',
                    missingkeyattr: 'Vă rugăm să alegeți cel puțin un atribut cheie',
                    modeadd: 'Adăuga',
                    modemerge: 'Combina',
                    modereplace: 'A inlocui',
                    separator: 'Separator'
                },
                refresh: 'Actualizați la valorile implicite'
            },
            linkcards: {
                checkedonly: 'Doar selectat',
                editcard: 'Editați cardul',
                opencard: 'Deschide cardul',
                refreshselection: 'Aplicați selecția implicită',
                removefilterconfirm: 'Această acțiune va elimina filtrul curent. Doriți să continuați?',
                selectioncleared: 'Selecția a fost eliminată',
                togglefilterdisabled: 'Dezactivați filtrul grilă',
                togglefilterenabled: 'Activați filtrul grilă'
            },
            notimplemented: 'Widgetul <strong>{0}</strong> nu este implementat',
            notsupportedinline:
                'Modul inline nu este acceptat pentru acest tip de widget. Vă rugăm să remediați configurația widgetului.',
            required: 'Acest widget este necesar.'
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
