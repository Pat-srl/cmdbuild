(function () {
    Ext.define('CMDBuildUI.locales.nl.Locales', {
        requires: ['CMDBuildUI.locales.nl.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'nl',
        administration: CMDBuildUI.locales.nl.LocalesAdministration.administration,
        ai: {
            clearconversation: '<em>Clear conversation</em>',
            clearconversationmessage: '<em>Are you sure you want to clear the current conversation?</em>',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: '<em>Sorry, there where problems in the answer</em>',
            headertooltip: '<em>CMDBuild chat bot</em>',
            introtextbody: '<em>To start chatting, describe your request in the appropriate field</em>',
            introtexttitle: '<em>How I can help you?</em>',
            loading: '<em>Loading ...</em>',
            newchat: '<em>New chat</em>',
            newchattooltip: '<em>Start a new conversation</em>',
            questionemptytext: '<em>Insert here your question ...</em>',
            resources: '<em>Sources</em>',
            viewrelatedcard: '<em>View related card</em>',
            viewresource: '<em>View resource</em>'
        },
        arialabels: {
            buttonplaceholder: '<em>Button</em>',
            iframeplaceholder: '<em>Text editor</em>',
            languageselector: '<em>Language selector</em>',
            resizepanel: {
                horizontally: '<em>Resize panels horizontally</em>',
                vertically: '<em>Resize panels vertically</em>'
            },
            usermenu: '<em>User menu</em>'
        },
        attachments: {
            add: 'Bijlage toevoegen',
            attachmenthistory: 'Bijlage geschiedenis',
            author: 'Schrijver',
            authordescription: 'Schrijver beschrijving',
            browse: 'Bladeren &hellip;',
            category: 'Categorie',
            code: 'Code',
            creationdate: 'Aanmaak datum',
            deleteattachment: 'Verwijder bijlage',
            deleteattachment_confirmation: 'Weet  u zeker dat u deze bijlage wilt verwijderen?',
            description: 'Omschrijving',
            download: 'Ophalen',
            dropfiles: 'Zet bestanden hier neer',
            editattachment: 'Modificeer bijlage',
            errordms: 'De DMS is uitgeschakeld en het is niet mogelijk bestanden op te slaan',
            file: 'Bestand',
            filealreadyinlist: 'Het bestand {0} bestaat al.',
            filename: 'Bestandsnaam',
            fileview: 'Bekijk bijlage',
            invalidfiles: 'Verwijder ongeldige bestanden',
            majorversion: 'Hoofd versie',
            maxsize: 'De maximum toegestane bestand grootte is {0} MB.',
            metadata: 'Extra gegevens',
            modificationdate: 'Modificatie datum',
            new: 'Nieuwe bijlage',
            nocategory: 'Niet gecategoriseerd',
            preview: 'Voorbeeld',
            removefile: 'Verwijder bestand',
            statuses: {
                empty: 'Leeg bestand',
                error: 'Fout',
                extensionNotAllowed: 'Bestands extensie niet toegestaan',
                loaded: 'Geladen',
                ready: 'Klaar',
                toolarge: 'Te groot'
            },
            successupload: '{0} bijlagen opgehaald',
            uploadfile: 'Ophalen bestand…',
            version: 'Versie',
            viewhistory: 'Toon bijlage geschiedenis',
            viewmetadata: 'Toon bijgevoegde metadata',
            warningmessages: {
                atleast: '{0}: geladen {1} bijlagen van type "{2}". Deze categorie verwacht minimaal {3} bijlagen ',
                exactlynumber: '{0}: geladen {1} bijlagen van type "{2}". Deze categorie verwacht {3} bijlagen ',
                maxnumber: '{0}: geladen {1} bijlagen van type "{2}". Deze categorie verwacht maximaal {3} bijlagen '
            },
            wrongfileextension: '{0} bestands extensie is niet toegestaan'
        },
        bim: {
            bimViewer: 'Bim afbeeldingsweergave',
            card: {
                label: 'Kaart'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Begrenzende box afmeting langs X-as',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Begrenzende box afmeting langs Y-as',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Begrenzende box afmeting langs Z-as',
                calculated: 'Berekend',
                description: 'Omschrijving',
                guid: 'GUID',
                label: 'Eigenschappen',
                name: 'Naam',
                type: 'Type',
                uuid: 'UUID'
            },
            layers: {
                label: 'Lagen',
                menu: {
                    hideAll: 'Verberg alles',
                    hideAllLayers: 'Verberg alle lagen',
                    hideAllXeokit: 'Verberg elke {0}',
                    showAll: 'Toon alles',
                    showAllLayers: 'Toon alle lagen',
                    showAllXeokit: 'Toon elke {0}'
                },
                name: 'Naam',
                quantity: 'Hoeveelheid',
                visibility: 'Zichtbaarheid'
            },
            menu: {
                camera: 'Camera',
                disableslice: 'Blokkeer laag object',
                enableslice: 'Toestaan laag object',
                frontView: 'Vooraanzicht',
                mod: 'Afbeeldingsweergave instellingen',
                ortho: 'Omschakelen naar Orthogonaal weergave',
                orthographic: 'Orthografische camera',
                pan: 'Schuiven',
                panXeokit: 'Weergave controle: schuiven (Shift)',
                perspe: 'Omschakelen naar perspectief weergave',
                perspective: 'Perspectief camera',
                resetSlice: 'Reset laag',
                resetView: 'Reset weergave',
                rotate: 'Draaien',
                rotateXeokit: 'Weergave controle: draaien',
                save: 'Bewaar weergave',
                shortcuts: {
                    down: 'Draai omlaag',
                    left: 'Draai linksom',
                    right: 'Draai rechtsom',
                    title: 'Toetsenbord versnellingen',
                    up: 'Draai omhoog',
                    zoomin: 'In zoomen',
                    zoomout: 'Uit zoomen'
                },
                sideView: 'Zijaanzicht',
                threeD: 'Omschakelen naar 3D weergave',
                topView: 'Bovenaanzicht',
                twoD: 'Omschakelen naar 2D weergave'
            },
            navCube: {
                back: 'Terug',
                bottom: 'Onderkant',
                front: 'Voorkant',
                left: 'Links',
                right: 'Rechts',
                top: 'Bovenkant'
            },
            showBimCard: 'Open 3D afbeeldingsweergave',
            tree: {
                arrowTooltip: 'Selecteer element',
                label: 'Boom',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Open gerelateerde kaart',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Afbreken geselecteerde items',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                'Sommige bijlage kunnen niet worden opgehaald omdat ze nog niet zijn opgeslagen op de server',
            alertnoattachmentsdownload:
                'Geen enkele bijlage wordt opgehaald omdat ze nog niet zijn opgeslagen op de server',
            cancelselection: 'Afbreken selectie',
            confirmabort: 'U stopt {0} proces instanties. Wilt u dat echt?',
            confirmdelete: 'U verwijdert {0} kaarten. Wilt u dat echt?',
            confirmdeleteattachements: 'U verwijdert {0} bijlagen. Wilt u dat echt?',
            confirmedit: 'U wijzigt {0} van {1} kaarten. Wilt u dat echt?',
            delete: 'Verwijder geselecteerde items',
            download: 'Download geselecteerde bijlagen',
            downloadall: 'Haal alle bijlage op',
            edit: 'Wijzig geselecteerde items',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: 'Selecteer a.u.b. minstens één attribuut',
            selectall: 'Selecteer alle items'
        },
        calendar: {
            active_expired: 'Actief/Verlopen',
            add: 'Toevoegen schema',
            advancenotification: 'Aantal dagen vooraf notificatie',
            allcategories: 'Alle categorieën\n',
            alldates: 'Alle data',
            associatedcard: 'Geassocieerde kaart',
            calculated: 'Berekend',
            calendar: 'Kalender',
            cancel: 'Markeer als afgebroken',
            category: 'Categorie',
            class: 'Klas',
            cm_confirmcancel: 'Weet u zeker dat u het geselecteerde schema als afgebroken wilt markeren?',
            cm_confirmcomplete: 'Weet u zeker dat u het geselecteerde schema als voltooid wilt markeren?',
            cm_markcancelled: 'Markeer geselecteerd schema als afgebroken',
            cm_markcomplete: 'Markeer geselecteerd schema als voltooid',
            complete: 'Markeer als gedaan',
            completed: 'Voltooid',
            date: 'Datum',
            days: 'Dagen',
            delaybeforedeadline: 'Uitstel voor deadline',
            delaybeforedeadlinevalue: 'Uitstel voor deadline waarde',
            description: 'Omschrijving',
            editevent: 'Wijzig schema',
            enddate: 'Einddatum',
            endtype: 'Eindtype',
            event: 'Schema',
            execapplyfilter: 'Kan “toepassen Uitgebreid Filter” niet uitvoeren omdat er meer dan één data bron is',
            execremovefilter: 'Kan “verwijderen Uitgebreid Filter” niet uitvoeren omdat er meer dan één data bron is',
            executiondate: 'Uitvoeringsdatum',
            frequency: 'Frequentie',
            frequencymultiplier: 'Frequentie vermenigvuldiger',
            grid: 'Rooster',
            leftdays: 'Dagen te gaan',
            londdescription: 'Uitgebreide omschrijving',
            manual: 'Handmatig',
            maxactiveevents: 'Max actieve schema’s',
            messagebodydelete: 'Wilt u de schemaregel verwijderen?',
            messagebodyplural: 'Er zijn {0} schemaregels',
            messagebodyrecalculate: 'Wilt u de schemaregel herberekenen met de nieuwe datum?',
            messagebodysingular: 'Er is {0} schemaregel',
            messagetitle: 'Schema herberekening',
            missingdays: 'Ontbrekende dagen',
            next30days: 'Volgende 30 dagen',
            next7days: 'Volgende 7 dagen',
            notificationtemplate: 'Sjabloon voor notificatie',
            notificationtext: 'Notificatietekst',
            occurencies: 'Aantal gebeurtenissen',
            operation: 'Operatie',
            partecipantgroup: 'Participant groep',
            partecipantuser: 'Participant gebruiker',
            priority: 'Prioriteit',
            recalculate: 'Herberekenen',
            referent: 'Referent',
            scheduler: 'Planner',
            sequencepaneltitle: 'Genereer schema',
            startdate: 'Startdatum',
            status: 'Status',
            today: 'Vandaag',
            type: 'Type',
            viewevent: 'Toon schema',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Berekeningscriterium',
            widgetemails: 'E-mails',
            widgetgotocard: 'Ga naar bron kaart',
            widgetsourcecard: 'Bronkaart'
        },
        chat: {
            title: 'Bespreken'
        },
        classes: {
            cards: {
                addcard: 'Kaart toevoegen',
                clone: 'Dupliceer',
                clonewithrelations: 'Dupliceer kaart en relaties',
                deletebeaware: 'Let op dat:',
                deleteblocked:
                    'Het is niet mogelijk verder te gaan met de verwijdering omdat er relaties zijn met {0}.',
                deletecard: 'Verwijder kaart',
                deleteconfirmation: 'Weet u zeker dat u deze kaart wilt verwijderen?',
                deleterelatedcards: 'ook {0} gerelateerde kaarten zullen verwijderd worden',
                deleterelations: 'relaties met {0} kaarten zullen verwijderd worden',
                label: 'Kaarten',
                modifycard: 'Kaart aanpassen',
                opencard: 'Kaart openen',
                print: 'Kaart afdrukken'
            },
            simple: 'Eenvoudig',
            standard: 'Standaard'
        },
        common: {
            actions: {
                add: 'Toevoegen',
                apply: 'Toepassen',
                cancel: 'Annuleren',
                clear: 'Verwijderen',
                close: 'Sluiten',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Verwijderen',
                edit: 'Modificeer',
                execute: 'Uitvoeren',
                forward: '<em>Forward</em>',
                help: 'Help',
                hide: '<em>Hide</em>',
                load: 'Laad',
                open: 'Open',
                previous: '<em>Previous</em>',
                refresh: 'Ververs gegevens',
                remove: 'Verwijderen',
                save: 'Opslaan',
                saveandapply: 'Opslaan en toepassen',
                saveandclose: 'Opslaan en sluiten',
                search: 'Zoeken',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Zoeken…'
            },
            and: '<em>and</em>',
            attributes: {
                boolfalse: 'Niet waar',
                booltrue: 'waar',
                link: {
                    errlabel: 'Label is vereist',
                    errurl: 'Url is vereist wanneer het label niet leeg is',
                    label: 'Label',
                    url: 'Url'
                },
                nogroup: 'Basis gegevens'
            },
            dates: {
                date: 'd/m/j',
                datetime: 'd/m/j h:m:s',
                time: 'h:m:s'
            },
            editor: {
                clearhtml: 'Verwijder HTML',
                expand: 'Expandeer editor',
                reduce: 'Verklein editor',
                signature: 'Handtekening toevoegen',
                unlink: 'Loskoppelen',
                unlinkmessage: 'Zet de geselecteerde hyperlink om in tekst.'
            },
            grid: {
                disablemultiselection: 'Blokkeer meerdere selecties',
                enamblemultiselection: 'Toestaan meerdere selecties',
                export: 'Exporteer gegevens',
                filterremoved: 'Het huidige filter is verwijderd',
                import: 'Importeer gegevens',
                itemnotfound: 'Item niet gevonden',
                lastUpdate: '<em>Last update</em>',
                list: 'Lijst',
                opencontextualmenu: 'Open context menu',
                print: 'Afdrukken',
                printcsv: 'Afdrukken als CSV',
                printmap: 'Map afdrukken',
                printodt: 'Afdrukken als ODT',
                printpdf: 'Afdrukken als PDF',
                row: 'Onderdeel',
                rows: 'Onderdelen',
                subtype: 'Onderliggend Type',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Activiteit',
                attachment: 'Bijlage',
                attachments: 'Bijlagen',
                card: 'Kaart',
                clonerelationmode: 'Kloon Relaties Mode',
                details: 'Details',
                emails: 'E-mails',
                history: 'Geschiedenis',
                notes: 'Notities',
                relations: 'Relaties',
                schedules: 'Planningen',
                sources: 'Bronnen'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: '<em>Show all</em>',
                showingnitems: '<em>Showing: {0} items</em>'
            },
            pareto: {
                cumulative: '<em>Cumulative</em>',
                cumulativeperc: '<em>Cumulative %</em>'
            },
            tools: {
                download: 'Haal kaart op',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Verberg data raster',
                gridshow: 'Toon data raster',
                openinpopup: 'Open in pop-up',
                reload: 'Herladen',
                showall: '<em>Show all</em>'
            }
        },
        dependencytree: {
            cardinfo: '<em>Card</em>',
            filteredid: '<em>Current card does not belong to this tree</em>',
            gerarchicview: '<em>Hierarchical view</em>',
            name: '<em>Dependency Tree</em>',
            nocardselected: '<em>No card selected</em>',
            opendependencytree: '<em>Open Dependency Tree</em>',
            printjpeg: '<em>Print as JPEG</em>',
            printpdf: '<em>Print as PDF</em>',
            printpng: '<em>Print as PNG</em>',
            printsvg: '<em>Print as SVG</em>',
            reopengraph: '<em>Reopen graph with new root</em>',
            selectcard: '<em>Select starting node</em>',
            wrongid: '<em>The ID you entered is incorrect</em>'
        },
        emails: {
            abort: 'Annuleren',
            addattachmentsfromdocumentarchive: 'Voeg een bijlage toe uit het documentarchief',
            alredyexistfile: 'Er bestaat al een bestand met deze naam',
            archivingdate: 'Archiveringsdatum',
            attachfile: 'Bestand toevoegen',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Samenstellen email',
            composefromtemplate: 'Samenstellen vanaf sjabloon',
            delay: 'Vertraging',
            delays: {
                day1: 'In 1 dag',
                days2: 'In 2 dagen',
                days4: 'In 4 dagen',
                hour1: '1 uur',
                hours2: '2 uren',
                hours4: '4 uren',
                month1: 'In 1 maand',
                negativeday1: '1 dag eerder',
                negativedays2: '2 dagen eerder',
                negativedays4: '4 dagen eerder',
                negativehour1: '1 uur eerder',
                negativehours2: '2 uur eerder',
                negativehours4: '4 uur eerder',
                negativemonth1: '1 maand eerder',
                negativeweek1: '1 week eerder',
                negativeweeks2: '2 weken eerder',
                none: 'Geen',
                week1: 'In 1 week',
                weeks2: 'In 2 weken'
            },
            dmspaneltitle: 'Kies bijlages uit database',
            edit: 'Modificeer',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Van',
            gridrefresh: 'Raster verversen',
            groupbystatus: 'Gegroepeerd per status',
            keepsynchronization: 'Behoud synchronisatie',
            maxsize: 'De totale maximum afmeting toegestaan voor bijlages is {0}.',
            message: 'Bericht',
            regenerateallemails: 'Regenereer alle e-mails',
            regenerateemail: 'Regenereer e-mail',
            reloadconfirmationmessage: 'E-mails in ontwerp status worden verwijderd.  Wilt u doorgaan?',
            remove: 'Verwijderen',
            remove_confirmation: 'Weet u zeker dat u deze email wilt verwijderen?',
            reply: 'Reactie',
            replyprefix: 'On {0}, {1} schreef:',
            searchattachment: 'Zoek bijlage',
            searchcard: 'Zoek kaart',
            selectaclass: 'Selecteer een klasse',
            sendemail: 'Verstuur e-mail',
            sendingemail: 'Versturen e-mail',
            signature: 'Handtekening',
            size: 'Afmeting',
            status: 'Status',
            statuses: {
                draft: 'Klad',
                error: 'Fout',
                new: 'Nieuw',
                outgoing: 'Uitgaand',
                received: 'Ontvangen',
                sent: 'verstuurd',
                skipped: 'Overgeslagen'
            },
            subject: 'Onderwerp',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Aan',
            view: 'Zicht'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Verkeerde gebruikersnaam of wachtwoord',
            classnotfound: 'Klas {0} niet gevonden',
            fieldrequired: 'Dit veld is vereist',
            invalidfilter: 'Ongeldig filter',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Item niet gevonden',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Acties',
            addfilter: 'Filter toevoegen',
            any: '<em>At least one</em>',
            attachments: 'Bijlage',
            attachmentssearchtext: 'Bijlage zoek tekst',
            attribute: 'Kies een attribuut',
            attributes: 'Attributen',
            clearfilter: 'Leegmaken filter',
            clone: 'Dupliceer',
            copyof: 'Kopie van',
            currentgroup: 'Huidige groep',
            currentuser: 'Huidige gebruiker',
            defaultset: 'Instellen als standaard',
            defaultunset: 'Deselecteer van standaard',
            description: 'Omschrijving',
            domain: 'Domein',
            errorname: 'Filternaam bestaat al',
            filterdata: 'Filter gegevens',
            fromfilter: 'Van filter',
            fromselection: 'Van Selectie',
            group: 'Groep',
            ignore: 'Negeer',
            migrate: 'Migreren',
            name: 'Naam',
            newfilter: 'Nieuw filter',
            noone: 'Geen enkele',
            operator: 'Bewerking',
            operators: {
                and: 'En',
                beginswith: 'Begint met',
                between: 'Tussen',
                contained: 'Bevat',
                containedorequal: 'Bevat of is gelijk aan',
                contains: 'Bevat',
                containsorequal: 'Bevat of is gelijk aan',
                descriptionbegin: 'Omschrijving begint met',
                descriptioncontains: 'Omschrijving bevat',
                descriptionends: 'Omschrijving eindigt met',
                descriptionnotbegin: 'Omschrijving begint niet met',
                descriptionnotcontain: 'Omschrijving bevat niet',
                descriptionnotends: 'Omschrijving eindigt niet met',
                different: 'Verschillend',
                doesnotbeginwith: 'Begint niet met',
                doesnotcontain: 'Bevat geen',
                doesnotendwith: 'Eindigt niet op',
                endswith: 'Eindigt op',
                equals: 'Gelijk',
                greaterthan: 'Groter dan',
                isnotnull: 'Is niet leeg',
                isnull: 'Is leeg',
                lessthan: 'Kleiner dan',
                or: 'Of'
            },
            relations: 'Relaties',
            removeattribute: 'Verwijder attribuut',
            removeblock: 'Verwijder blok',
            type: 'Soort',
            typeinput: 'Input Parameter',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Gebruiker',
            value: 'Waarde'
        },
        gis: {
            card: 'Kaart',
            cardsMenu: 'Kaartenmenu',
            code: 'Code',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Omschrijving',
            extension: {
                errorCall: 'Fout',
                noResults: 'Geen resultaat'
            },
            externalServices: 'Externe Services',
            geographicalAttributes: 'Geografische attributen',
            geoserverLayers: 'Geoserver lagen',
            labels: {
                all: 'Alles',
                alltitle: 'Toon alle labels',
                alltitleclass: 'Toon alle label in klas',
                clear: '<em>Clear search</em>',
                hidden: 'Verborgen',
                hiddentitle: 'Verberg labels',
                hideinfowindow: 'Verberg info venster',
                label: 'Labels',
                search: '<em>Search on the map</em>',
                selected: 'Geselecteerd',
                selectedtitle: 'Toon label van geselecteerde item',
                showinfowindow: 'Toon info venster'
            },
            layers: 'Lagen',
            layersTab: {
                clearPreferences: 'Wis lagen voorkeuren',
                preferencesCleared: 'Lagen voorkeuren gewist',
                preferencesSaved: 'Lagen voorkeuren opgeslagen',
                savePreferences: 'Bewaar lagen voorkeuren',
                updatePreferences: 'Bijwerken lagen voorkeuren'
            },
            list: 'Lijst',
            longpresstitle: 'Geo-elementen in het gebied',
            map: 'Kaart',
            mapServices: 'Kaart services',
            menu: {
                font: 'Lettertype afmeting',
                options: 'Opties'
            },
            position: 'Positie',
            root: 'Begin',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Navigatie boomstructuur',
            type: 'Type',
            view: 'Zicht',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Activiteit naam',
            activityperformer: 'Activiteit uitvoerder',
            attributes: '<em>Attributes ({0})</em>',
            begindate: 'Begin datum',
            enddate: 'Eind datum',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: 'Andere relaties',
            processstatus: 'Status',
            referencerelation: 'Referentie relaties',
            relation: 'Relatie',
            system: 'Systeem',
            type: 'Soort',
            user: 'Gebruiker',
            userdescription: 'Gebruiker beschrijving',
            value: '<em>Value</em>',
            viewmode: '<em>View mode</em>'
        },
        homepage: {
            search: {
                airequestproblem:
                    '<em>A problem occurred while processing the request. Please try again later or contact the administrator.</em>',
                aisearcherror: '<em>Unable to complete the search</em>',
                goodafternoon: '<em>Good afternoon</em>',
                goodevening: '<em>Good evening</em>',
                goodmorning: '<em>Good morning</em>',
                goodnight: '<em>Good night</em>',
                noresults: '<em>No results found</em>',
                noresultshint: '<em>Try different keywords or check the spelling</em>',
                resultsfor: '<em>Results for: {0}</em>'
            }
        },
        iconscategories: {
            accessibility: '<em>Accessibility</em>',
            animals: '<em>Animals</em>',
            arrows: '<em>Arrows</em>',
            audiovideo: '<em>Audio video</em>',
            automotive: '<em>Automotive</em>',
            autumn: '<em>Autumn</em>',
            beverage: '<em>Beverage</em>',
            buildings: '<em>Buildings</em>',
            business: '<em>Business</em>',
            charity: '<em>Charity</em>',
            chat: '<em>Chat</em>',
            chess: '<em>Chess</em>',
            childhood: '<em>Childhood</em>',
            code: '<em>Code</em>',
            communication: '<em>Communication</em>',
            computers: '<em>Computers</em>',
            construction: '<em>Construction</em>',
            currency: '<em>Currency</em>',
            datetime: '<em>Date time</em>',
            design: '<em>Design</em>',
            editors: '<em>Editors</em>',
            education: '<em>Education</em>',
            emoji: '<em>Emoji</em>',
            energy: '<em>Energy</em>',
            files: '<em>Files</em>',
            finance: '<em>Finance</em>',
            food: '<em>Food</em>',
            fruitvegetable: '<em>Fruit vegetable</em>',
            games: '<em>Games</em>',
            gamingtabletop: '<em>Gaming tabletop</em>',
            gender: '<em>Gender</em>',
            general: '<em>General</em>',
            halloween: '<em>Halloween</em>',
            hands: '<em>Hands</em>',
            hotel: '<em>Hotel</em>',
            household: '<em>Household</em>',
            images: '<em>Images</em>',
            interfaces: '<em>Interfaces</em>',
            logistics: '<em>Logistics</em>',
            maps: '<em>Maps</em>',
            maritime: '<em>Maritime</em>',
            marketing: '<em>Marketing</em>',
            mathematics: '<em>Mathematics</em>',
            medical: '<em>Medical</em>',
            moving: '<em>Moving</em>',
            music: '<em>Music</em>',
            objects: '<em>Objects</em>',
            paymentsshopping: '<em>Payments shopping</em>',
            political: '<em>Political</em>',
            religion: '<em>Religion</em>',
            sciencefiction: '<em>Science fiction</em>',
            security: '<em>Security</em>',
            shapes: '<em>Shapes</em>',
            shopping: '<em>Shopping</em>',
            social: '<em>Social</em>',
            spinners: '<em>Spinners</em>',
            sports: '<em>Sports</em>',
            spring: '<em>Spring</em>',
            status: '<em>Status</em>',
            toggle: '<em>Toggle</em>',
            travel: '<em>Travel</em>',
            userspeople: '<em>Users people</em>',
            vehicles: '<em>Vehicles</em>',
            weather: '<em>Weather</em>',
            winter: '<em>Winter</em>'
        },
        importexport: {
            database: {
                uri: 'Database URI',
                user: 'Database gebruiker'
            },
            downloadreport: 'Download rapport',
            emailfailure: 'Fout opgetreden tijdens verzenden email',
            emailmessage: 'Bijgevoegd importrapport van bestand "{0}" op datum {1}',
            emailsubject: 'Importeer data rapport',
            emailsuccess: 'De email is met succes verzonden!',
            export: 'Exporteer',
            exportalldata: 'Alle gegevens',
            exportfiltereddata: 'Alleen gegevens die matchen met het raster filter',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Het importeren van vormen is niet ingesteld voor dit sjabloon',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: 'Kaart',
                project: 'Project',
                sourcetype: 'Import van'
            },
            import: 'Importeer',
            importresponse: 'Importeer response',
            response: {
                created: 'Maak kaarten',
                deleted: 'Verwijder kaarten',
                errors: 'Fouten',
                linenumber: 'Regelnummer',
                message: 'Melding',
                modified: 'Aangepaste kaarten',
                notprocessed: 'Niet verwerkte kaarten',
                processed: 'Verwerkte rijen',
                recordnumber: 'Recordnummer',
                unchanged: 'Ongewijzigde kaarten'
            },
            sendreport: 'Verzend rapport',
            template: 'Sjabloon',
            templatedefinition: 'Sjabloon definitie'
        },
        joinviews: {
            active: 'Actief',
            addview: 'Weergave toevoegen',
            alias: 'Alias',
            attribute: 'Attribuut',
            attributecustomization: 'Attribuut aanpassingen',
            attributes: 'Attributen',
            attributeschoice: 'Attribuut keuze',
            attributesof: 'Attributen van: {0}',
            createview: 'Maak weergave',
            datasorting: 'Data sorteringen',
            delete: 'Verwijder',
            deleteview: 'Verwijder weergave',
            deleteviewconfirm: 'Weet u zeker dat u deze weergave wilt verwijderen?',
            description: 'Omschrijving',
            disable: 'Blokkeer',
            domainalias: 'Domein alias',
            domainsof: 'Domeinen van {0}',
            edit: 'Bewerk',
            editview: 'Bewerk weergave configuratie',
            enable: 'Toestaan',
            fieldsets: 'Veldsets',
            filters: 'Filters',
            generalproperties: 'Algemene eigenschappen',
            group: 'Groep',
            innerjoin: 'interne samenvoeging',
            jointype: 'samenvoegingstype',
            joinview: 'Weergave van samenvoeging',
            klass: 'Klas',
            manageview: 'Beheer weergave',
            masterclass: 'Hoofd klas',
            masterclassalias: 'Hoofd klas alias',
            name: 'Naam',
            newjoinview: 'Nieuwe weergave van samenvoeging',
            outerjoin: 'Externe samenvoeging',
            pleaseseleceavalidmasterclass: 'Selecteer AUB een geldige hoofd klas',
            refreshafteredit: 'Wilt u de bladzijde verversen om de wijzigingen te zien?',
            selectatleastoneattribute: 'Selecteer AUB ten minste één attribuut',
            selectdisplayattributegrid:
                'Selecteer AUB ten minste één attribuut om te tonen in rasteren één in gereduceerd raster',
            showingrid: 'Weergeven in raster',
            showinreducedgrid: 'Weergeven in gereduceerd raster',
            targetalias: 'Doel klas alias'
        },
        login: {
            buttons: {
                login: 'Login',
                logout: 'Modificeer gebruiker'
            },
            fields: {
                group: 'Groep',
                language: 'Taal',
                password: 'Wachtwoord',
                tenants: 'Leden',
                username: 'Gebruikersnaam'
            },
            loggedin: 'Aangemeld',
            sso: {
                loginfailure: 'Inlog fout',
                loginwith: 'Inloggen met {0}',
                or: 'of',
                usernotenabled: 'Gebruiker is niet geactiveerd'
            },
            title: 'Login',
            welcome: 'Welkom terug {0}.'
        },
        main: {
            administrationmodule: 'Administratie module',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Basis configuratie',
            cardlock: {
                lockedmessage: 'U kunt deze kaart niet aanpassen omdat {0} deze aan het aanpassen is',
                someone: 'iemand'
            },
            changegroup: 'Wijzig groep',
            changetenant: 'Wijzig {0}',
            confirmchangegroup: 'Weet u zeker dat de groep gewijzigd moet worden',
            confirmchangetenants: 'Weet u zeker dat de actieve leden gewijzigd moeten worden',
            confirmdisabletenant: 'Weet u zeker dat de “negeer leden” vlag uitgezet moet worden',
            confirmenabletenant: 'Weet u zeker dat de “negeer leden” vlag aangezet moet worden',
            description: 'Beschrijving',
            ignoretenants: 'Negeer {0}',
            info: 'Info',
            logo: {
                cmdbuild: 'CMDBuild logo',
                cmdbuildready2use: 'CMDBuild READY2USE logo',
                companylogo: 'Bedrijfslogo',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'openMAINT logo'
            },
            logout: 'Uitloggen',
            managementmodule: 'Gegevens beheer module',
            multigroup: 'Meervoudige groep',
            multitenant: 'Multi {0}',
            navigation: 'Navigatie',
            pagenotfound: 'Pagina niet gevonden',
            password: {
                change: 'Wachtwoord veranderen',
                confirm: 'Bevestig wachtwoord',
                email: 'E-mail adres',
                err_confirm: 'Wachtwoord komt niet overeen.',
                err_diffprevious: 'Het wachtwoord kan niet overeenkomen met het vorige.',
                err_diffusername: 'Het wachtwoord kan niet gelijk zijn aan de gebruikersnaam.',
                err_length: 'Het wachtwoord moet minimaal {0} karakters lang zijn.',
                err_reqdigit: 'Het wachtwoord dient minimaal een cijfer te bevatten',
                err_reqlowercase: 'Het wachtwoord dient minimaal een kleine letter te bevatten.',
                err_requppercase: 'Het wachtwoord dient minimaal een hoofdletter te bevatten.',
                expired: 'Uw wachtwoord is verlopen. U moet het nu wijzigen.',
                forgotten: 'Ik ben mijn wachtwoord vergeten',
                new: 'Nieuw wachtwoord',
                old: 'Oud wachtwoord',
                recoverysuccess: 'We hebben u een email gestuurd met instructies voor het instellen van uw wachtwoord.',
                reset: 'Reset wachtwoord',
                saved: 'Wachtwoord correct opgeslagen!'
            },
            pleasecorrecterrors: 'Wijzig a.u.b. de aangegeven fouten!',
            preferences: {
                comma: 'Komma',
                decimalserror: 'Decimaal veld moet aanwezig zijn',
                decimalstousandserror: 'Decimaal en Duizendtal scheidingsteken moeten verschillen',
                default: 'Standaard',
                defaultvalue: 'Standaard waarde',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: 'Eerste dag van de week',
                gridpreferencesclear: 'Wis rastervoorkeuren',
                gridpreferencescleared: 'Rastervoorkeuren gewist!',
                gridpreferencessave: 'Opslaan rastervoorkeuren',
                gridpreferencessaved: 'Rastervoorkeuren opgeslagen',
                gridpreferencesupdate: 'Wijzig rastervoorkeuren',
                groupemailsbystatus: 'Toon e-mails gegroepeerd per status',
                labelcsvseparator: 'CSV scheidingsteken',
                labeldateformat: 'Datum formaat',
                labeldecimalsseparator: 'Decimaal scheidingsteken',
                labellanguage: 'Taal',
                labelthousandsseparator: 'Duizendtal scheidingsteken',
                labeltimeformat: 'Tijd formaat',
                msoffice: 'Microsoft Office',
                no: 'Nee',
                notificationslabel: 'Besproken en Meldingen',
                notificationssound: 'Speel geluid voor meldingen',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Punt',
                preferredfilecharset: 'CSV codering',
                preferredofficesuite: 'Voorkeur Office-suite',
                sendcancellationperiod: 'Annuleringstermijn e-mail verzenden (seconden)',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: 'Spatie',
                thousandserror: 'Duizendtal veld moet aanwezig zijn',
                timezone: 'Tijdzone',
                twelvehourformat: '12-uurs formaat',
                twentyfourhourformat: '24-uurs formaat',
                yes: 'Ja'
            },
            searchinallitems: 'Zoek in alle onderdelen',
            searchtenant: 'Zoek huurder',
            selectdeselettenants: 'Selecteer / deselecteer alles',
            treenavcontenttitle: '{0} van {1}',
            userpreferences: 'Voorkeuren'
        },
        menu: {
            allitems: 'Alle onderdelen',
            classes: 'Klassen',
            custompages: 'Gebruikers bladzijden',
            dashboards: 'Instrumenten paneel',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: 'Processen',
            reports: 'Rapporten',
            setasinitialpage: '<em>Set as initial page</em>',
            views: 'Vensters'
        },
        mobile: {
            config: {
                configure: '<em>Configure mobile device</em>',
                customercode: '<em>Customer code</em>',
                devicename: '<em>Device name</em>',
                regenerate: '<em>Regenerate</em>',
                serverurl: '<em>Server URL</em>'
            }
        },
        notes: {
            edit: 'Notitie aanpassen'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Verwijder melding',
            emptymessage: 'Er zijn geen meldingen weer te geven',
            label: 'Meldingen',
            viewall: 'Toon alles'
        },
        notifier: {
            attention: 'Attentie',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Fout',
            genericerror: 'Algemene fout',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Algemene informatie',
            genericwarning: 'Algemene waarschuwing',
            info: 'Info',
            more: '<em>More info</em>',
            success: 'Succes',
            warning: 'Waarschuwing'
        },
        patches: {
            apply: 'Patches toepassen',
            category: 'Categorie',
            description: 'Omschrijving',
            name: 'Naam',
            patches: 'Patches'
        },
        processes: {
            abortconfirmation: 'Weet je zeker dat je dit proces wilt afbreken?',
            abortprocess: 'Proces afbreken',
            action: {
                advance: 'Doorschuiven',
                label: 'Actie'
            },
            activeprocesses: 'Actieve processen',
            allstatuses: 'Alles',
            editactivity: 'Activiteit aanpassen',
            openactivity: 'Activiteit openen',
            resumeprocesses: 'Processen die niet zijn gepauzeerd kunnen niet worden voortgezet',
            startworkflow: 'Start',
            workflow: 'Procesgang'
        },
        relationGraph: {
            activity: 'activiteit',
            allLabelsOnGraph: 'alle labels op grafiek',
            card: 'Kaart',
            cardList: 'Kaart lijst',
            cardRelations: 'Relatie',
            choosenaviagationtree: 'Kies navigatie boom',
            class: 'Klas',
            classList: 'Klassen lijst',
            compoundnode: 'Samengesteld knooppunt',
            disable: 'Uitschakelen',
            edges: 'Randen',
            enable: 'Inschakelen',
            labelsOnGraph: 'Tooltip op grafiek',
            level: 'Niveau',
            nodes: 'Knooppunten',
            openRelationGraph: 'Open relatie grafiek',
            quantity: 'Hoeveelheid',
            refresh: 'Ververs',
            relation: 'Relatie',
            relationGraph: 'Relatie grafiek',
            reopengraph: 'Heropen de grafiek van dit knooppunt'
        },
        relations: {
            adddetail: 'Detail toevoegen',
            addrelations: 'Relaties toevoegen',
            attributes: 'Attributen',
            begindate: 'Begin datum',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Code',
            deletedetail: 'Detail verwijderen',
            deleterelation: 'Verwijder relatie',
            deleterelationconfirm: 'Weet u zeker dat u deze relatie wilt verwijderen?',
            description: 'Omschrijving',
            editcard: 'Kaart aanpassen',
            editdetail: 'Detail aanpassen',
            editrelation: 'Aanpassen relatie',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Uitgebreide gegevens',
            mditems: 'onderdelen',
            missingattributes: 'Ontbrekende verplichtte attributen',
            opencard: 'Open gerelateerde kaart',
            opendetail: 'Toon detail',
            relationdata: '<em>Relation data</em>',
            type: 'Soort'
        },
        reports: {
            csv: 'CSV',
            download: 'Ophalen',
            format: 'Formaat',
            msgs: {
                creationerror: 'Iets ging verkeerd tijdens de start van het maken van een rapport',
                creationstarted: 'Rapport maken is gestart. U krijgt bericht zodra het klaar is voor ophalen.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Afdrukken',
            reload: 'Herladen',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Standaard'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Dagelijks'
                        },
                        monthly: {
                            description: 'Maandelijks'
                        },
                        once: {
                            description: 'Eenmalig'
                        },
                        weekly: {
                            description: 'Wekelijks'
                        },
                        yearly: {
                            description: 'Jaarlijks'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Standaard'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Niemand'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Voeg onderwerp toe',
            analysisType: 'Analyse-type',
            attribute: 'Attribuut',
            bordersIntervals: 'Foutieve configuratie voor interval grenzen',
            calculateRules: 'Genereer stijlregels',
            clearThematism: 'Wis onderwerp',
            color: 'Kleur',
            defineLegend: 'Legenda definitie',
            defineThematism: 'Onderwerp definitie',
            deletethematism: 'Weet u zeker dat u dit onderwerp wilt verwijderen',
            editThematism: 'Bewerk configuratie regels',
            function: 'Functie',
            generate: 'Genereer',
            geoAttribute: 'Geografisch attribuut',
            graduated: 'Geslaagd',
            highlightSelected: 'Markeer het geselecteerde item',
            intervals: 'Intervallen',
            legend: 'Legenda',
            name: 'naam',
            newThematism: 'Nieuw onderwerp',
            punctual: 'Punctueel',
            quantity: 'Aantal',
            segments: 'Segmenten',
            source: 'Bron',
            table: 'Tabel',
            thematism: 'Onderwerpen',
            value: 'Waarde'
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
                    exitwithoutsave: 'Uitloggen zonder op te slaan',
                    stayinedit: 'Terug naar aanpassen'
                },
                savebeforeexit:
                    'Er zijn niet opgeslagen wijzigingen in de bijlage. Wilt u die opslaan voordat u uitlogt',
                saveprocessmessage:
                    'Bijlage kunnen alleen in bestaande kaarten worden opgeslagen.  Klik op opslaan, de kaart zal worden gegeneerd inclusief de gegevens uit het formulier.'
            },
            calendar: {
                error: 'De parameter {0} is niet ingesteld',
                errorwrong: 'De parameter {0} is niet correct ingesteld'
            },
            customform: {
                addrow: 'Rij toevoegen',
                clonerow: 'Kloon rij',
                datanotvalid: 'Gegevens niet valide',
                deleterow: 'Rij verwijderen',
                editrow: 'Rij aanpassen',
                export: 'Export',
                import: 'Importeer',
                importexport: {
                    expattributes: 'Te exporteren gegevens',
                    file: 'Bestand',
                    filename: 'Bestandsnaam',
                    format: 'Format',
                    importmode: 'Importeer mode',
                    keyattributes: 'Sleutelattributen',
                    missingkeyattr: 'Kies alstublieft minimaal een sleutelattribuut',
                    modeadd: 'Toevoegen',
                    modemerge: 'Samenvoegen',
                    modereplace: 'Vervangen',
                    separator: 'Scheidingsteken'
                },
                refresh: 'Terug naar standaard'
            },
            linkcards: {
                checkedonly: 'Alleen aangevinkte',
                editcard: 'Kaart aanpassen',
                opencard: 'Kaart openen',
                refreshselection: 'Standaard selectie toepassen',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Verwijder raster filter',
                togglefilterenabled: 'Instellen raster filter'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                'Inline mode wordt niet ondersteund voor dit type widget.  Corrigeer a.u.b. de widget configuratie.',
            required: 'Deze widget is verplicht.'
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
