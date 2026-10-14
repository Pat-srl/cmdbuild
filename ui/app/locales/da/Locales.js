(function () {
    Ext.define('CMDBuildUI.locales.da.Locales', {
        requires: ['CMDBuildUI.locales.da.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'da',
        administration: CMDBuildUI.locales.da.LocalesAdministration.administration,
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
            add: 'Tilføj vedhæftede filer',
            attachmenthistory: 'Vedhæftningsoversigt',
            author: 'Forfatter',
            authordescription: '<em>Author description</em>',
            browse: 'Gennemse &hellip;',
            category: 'Kategori',
            code: 'Kode',
            creationdate: 'Oprettelsesdato',
            deleteattachment: 'Slet vedhæftet fil',
            deleteattachment_confirmation: 'Er du sikker på, at du vil slette denne vedhæftede fil?',
            description: 'Beskrivelse',
            download: 'Hent',
            dropfiles: 'Slip filer her',
            editattachment: 'Ændr vedhæftet fil',
            errordms: '<em>The DMS is disabled and it is not possible to save files</em>',
            file: 'Fil',
            filealreadyinlist: 'Filen {0} er allerede på listen.',
            filename: 'filnavn',
            fileview: '<em>View attachment</em>',
            invalidfiles: 'Fjern ugyldige filer',
            majorversion: 'Nyeste version',
            maxsize: '<em>The maximum allowed file size is {0} MB.</em>',
            metadata: '<em>Metadata</em>',
            modificationdate: 'Ændringsdato',
            new: 'Ny vedhæftet fil',
            nocategory: 'Ikke kategoriseret',
            preview: 'Eksempel',
            removefile: 'Fjern fil',
            statuses: {
                empty: 'Tom fil',
                error: 'Fejl',
                extensionNotAllowed: 'Filudvidelse er ikke tilladt',
                loaded: 'Load færdig',
                ready: 'Klar',
                toolarge: '<em>Too large</em>'
            },
            successupload: '{0} vedhæftede filer er uploadet.',
            uploadfile: 'Upload fil...',
            version: 'Version',
            viewhistory: 'Se vedhæftelseshistorik',
            viewmetadata: '<em>View attachment metadata</em>',
            warningmessages: {
                atleast:
                    '{0}: er blevet indlæst {1} vedhæftede filer af typen "{2}". Denne kategori forventer mindst {3} vedhæftede filer',
                exactlynumber:
                    '{0}: er blevet indlæst {1} vedhæftede filer af typen "{2}". Denne kategori forventer {3} vedhæftede filer',
                maxnumber:
                    '{0}: er blevet indlæst {1} vedhæftet fil af typen "{2}". Denne kategori forventer højst {3} vedhæftede filer'
            },
            wrongfileextension: '{0} filtypenavn er ikke tilladt'
        },
        bim: {
            bimViewer: 'Bim Viewer',
            card: {
                label: 'Kort'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: '<em>Bounding box size along X axis</em>',
                BOUNDING_BOX_SIZE_ALONG_Y: '<em>Bounding box size along Y axis</em>',
                BOUNDING_BOX_SIZE_ALONG_Z: '<em>Bounding box size along Z axis</em>',
                calculated: '<em>Calculated</em>',
                description: '<em>Description</em>',
                guid: '<em>GUID</em>',
                label: '<em>Properties</em>',
                name: '<em>Name</em>',
                type: '<em>Type</em>',
                uuid: '<em>UUID</em>'
            },
            layers: {
                label: 'Lag',
                menu: {
                    hideAll: 'Skjul alle',
                    hideAllLayers: '<em>Hide All Layers</em>',
                    hideAllXeokit: '<em>Hide all {0}</em>',
                    showAll: 'Vis alle',
                    showAllLayers: '<em>Show All Layers</em>',
                    showAllXeokit: '<em>Show all {0}</em>'
                },
                name: 'Navn',
                quantity: '<em>Quantity</em>',
                visibility: 'Synlighed'
            },
            menu: {
                camera: 'Kamera',
                disableslice: '<em>Disable slice objects</em>',
                enableslice: '<em>Enable slice objects</em>',
                frontView: 'Forfra visning',
                mod: 'Viewer kontroller',
                ortho: '<em>Switch to Orthogonal View</em>',
                orthographic: 'Ortografisk kamera',
                pan: 'Rulle op/ned',
                panXeokit: '<em>Viewer controls: scroll (Shift)</em>',
                perspe: '<em>Switch to Perspective View</em>',
                perspective: 'perspektiv kamera',
                resetSlice: '<em>Reset slice</em>',
                resetView: 'Nulstil visning',
                rotate: 'rotere',
                rotateXeokit: '<em>Viewer controls: rotate</em>',
                save: '<em>Save View</em>',
                shortcuts: {
                    down: '<em>Rotate down</em>',
                    left: '<em>Rotate left</em>',
                    right: '<em>Rotate right</em>',
                    title: '<em>Keyboard shortcuts</em>',
                    up: '<em>Rotate up</em>',
                    zoomin: '<em>Zoom in</em>',
                    zoomout: '<em>Zoom out</em>'
                },
                sideView: 'Side visning',
                threeD: '<em>Switch to 3D View</em>',
                topView: 'Ovnfra visning',
                twoD: '<em>Switch to 2D View</em>'
            },
            navCube: {
                back: '<em>Back</em>',
                bottom: '<em>Bottom</em>',
                front: '<em>Front</em>',
                left: '<em>Left</em>',
                right: '<em>Right</em>',
                top: '<em>Top</em>'
            },
            showBimCard: 'Åben 3D-viewer',
            tree: {
                arrowTooltip: 'Vælg element',
                label: 'Filstruktur',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Åben relateret kort',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Afbryd valgte elementer',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                '<em>Some attachments will not be downloaded because they have not yet been saved to the server</em>',
            alertnoattachmentsdownload:
                '<em>No attachments will be downloaded because they have not yet been saved to the server</em>',
            cancelselection: 'Annuller valg',
            confirmabort: 'Du afbryder {0} procesforekomster. Er du sikker på, at du vil fortsætte?',
            confirmdelete: 'Du sletter {0} kort. Er du sikker på, at du vil fortsætte?',
            confirmdeleteattachements: 'Du sletter {0} vedhæftede filer. Er du sikker på, at du vil fortsætte?',
            confirmedit: 'Du ændrer {0} for {1} kort. Er du sikker på, at du vil fortsætte?',
            delete: 'Slet valgte emner',
            download: 'Download valgte vedhæftede filer',
            downloadall: '<em>Download all attachments</em>',
            edit: 'Rediger valgte emner',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: '<em>Please select at least one attribute.</em>',
            selectall: 'Vælg alle emner'
        },
        calendar: {
            active_expired: 'Aktiv / udløbet',
            add: 'Tilføj',
            advancenotification: 'Forhåndsmeddelelse',
            allcategories: 'Alle kategorier',
            alldates: 'Alle datoer',
            associatedcard: '<em>Associated card</em>',
            calculated: 'Beregnet',
            calendar: 'Kalender',
            cancel: 'Marker som annulleret',
            category: 'Kategori',
            class: '<em>Class</em>',
            cm_confirmcancel: 'Er du sikker på, at du vil markere de valgte tidsplaner som annullerede?',
            cm_confirmcomplete: 'Er du sikker på, at du vil markere de udfyldte som valgte tidsplaner?',
            cm_markcancelled: 'Marker som annullerede valgte tidsplaner',
            cm_markcomplete: 'Marker som udfyldte valgte tidsplaner',
            complete: 'Komplet',
            completed: 'Afsluttet',
            date: 'Dato',
            days: 'Dage',
            delaybeforedeadline: 'Forsinkelse inden deadline',
            delaybeforedeadlinevalue: 'Forsinkelse inden fristens værdi',
            description: 'Beskrivelse',
            editevent: 'Rediger tidsplan',
            enddate: 'Slut dato',
            endtype: 'Afslutningstype',
            event: 'Tidsplan',
            execapplyfilter: '<em>Can not execute applyAdvanceFilter because there are more than one stores</em>',
            execremovefilter: '<em>Can not execute removeAdvanceFilter because there are more than one stores</em>',
            executiondate: 'Udførelsesdato',
            frequency: 'Frekvens',
            frequencymultiplier: 'Frekvensmultiplikator',
            grid: 'Gitter',
            leftdays: 'Dage der skal gå',
            londdescription: 'Full Description',
            manual: 'brugervejledning',
            maxactiveevents: 'Maks aktive begivenheder',
            messagebodydelete: 'Ønsker du at fjerne planlægningsreglen?',
            messagebodyplural: 'Der er {0} tidsplanregler',
            messagebodyrecalculate: 'Ønsker du at omberegne tidsplanreglen med den nye dato?',
            messagebodysingular: 'Der er {0} tidsplanregel',
            messagetitle: 'Planlæg omberegning',
            missingdays: 'Manglende dage',
            next30days: 'De næste 30 dage',
            next7days: 'De næste 7 dage',
            notificationtemplate: 'Skabelon, der bruges til underretning',
            notificationtext: 'Meddelelsestekst',
            occurencies: 'Antal forekomster',
            operation: 'Operation',
            partecipantgroup: 'Deltagergruppe',
            partecipantuser: 'Deltagerbruger',
            priority: 'Prioritet',
            recalculate: 'Genberegner',
            referent: 'Referent',
            scheduler: 'planlæggere',
            sequencepaneltitle: 'Generer tidsplaner',
            startdate: 'Start dato',
            status: 'Status',
            today: 'I dag',
            type: 'Type',
            viewevent: 'Se tidsplan',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Beregningskriterium',
            widgetemails: 'E-mails',
            widgetgotocard: '<em>Go to source card</em>',
            widgetsourcecard: 'Kildekort'
        },
        chat: {
            title: '<em>Chat</em>'
        },
        classes: {
            cards: {
                addcard: 'Tilføj kort',
                clone: 'Duplikere',
                clonewithrelations: 'Duplikere kort og relationer',
                deletebeaware: 'Vær opmærksom på, at:',
                deleteblocked: 'Det er ikke muligt at fortsætte med sletningen, fordi der er relationer til {0}.',
                deletecard: 'Slet kort',
                deleteconfirmation: 'Er du sikker på, at du vil slette dette kort?',
                deleterelatedcards: 'også {0} relaterede kort slettes',
                deleterelations: 'relationer med {0} kort slettes',
                label: 'Kort',
                modifycard: 'Ændre kort',
                opencard: 'Åben kort',
                print: 'Print kort'
            },
            simple: 'Enkel',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Tilføj',
                apply: 'Anvend',
                cancel: 'Annullere',
                clear: '<em>Clear</em>',
                close: 'Luk',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Slet',
                edit: 'Redigere',
                execute: 'Udfør',
                forward: '<em>Forward</em>',
                help: 'Hjælp',
                hide: '<em>Hide</em>',
                load: 'Load',
                open: 'Åben',
                previous: '<em>Previous</em>',
                refresh: 'Opdater data',
                remove: 'Fjern',
                save: 'Gem',
                saveandapply: 'Gem og anvend',
                saveandclose: 'Gem og luk',
                search: 'Søg',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Søg…'
            },
            and: '<em>and</em>',
            attributes: {
                boolfalse: '<em>false</em>',
                booltrue: '<em>true</em>',
                link: {
                    errlabel: '<em>Label is required</em>',
                    errurl: '<em>Url is required when the label is not empty</em>',
                    label: '<em>Label</em>',
                    url: '<em>Url</em>'
                },
                nogroup: 'Basis data'
            },
            dates: {
                date: 'dd/mm/åå',
                datetime: 'dd/mm/åå tt:mm:ss',
                time: 'tt;mm:ss'
            },
            editor: {
                clearhtml: 'Slet HTML',
                expand: 'Udvid editor',
                reduce: '<em>Reduce editor</em>',
                signature: '<em>Add signature</em>',
                unlink: '<em>Unlink</em>',
                unlinkmessage: '<em>Transform the selected hyperlink into text.</em>'
            },
            grid: {
                disablemultiselection: 'Deaktiver multi-valg',
                enamblemultiselection: 'Aktivér multi-valg',
                export: 'Eksport data',
                filterremoved: 'Det nuværende filter er blevet fjernet',
                import: 'Import data',
                itemnotfound: 'Emnet blev ikke fundet',
                lastUpdate: '<em>Last update</em>',
                list: 'List',
                opencontextualmenu: 'Åben kontekstmenu',
                print: 'Print',
                printcsv: 'Print som CSV',
                printmap: '<em>Print Map</em>',
                printodt: 'Print som ODT',
                printpdf: 'Print som PDF',
                row: 'Undertype',
                rows: 'Emne',
                subtype: 'Undertype',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Aktivitet',
                attachment: 'Vedhæftet fil',
                attachments: 'Vedhæftede filer',
                card: 'Kort',
                clonerelationmode: 'Duplikere forholdstilstand',
                details: 'Detaljer',
                emails: 'E-mails',
                history: 'Historik',
                notes: 'Noter',
                relations: 'Relationer',
                schedules: 'Tidsplaner',
                sources: '<em>Sources</em>'
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
                download: '<em>Download chart</em>',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Skjul datagitter',
                gridshow: 'Vis datagitter',
                openinpopup: '<em>Open in popup</em>',
                reload: 'Opdater',
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
            abort: '<em>Cancel</em>',
            addattachmentsfromdocumentarchive: 'Tilføj vedhæftede filer fra dokumentarkivet',
            alredyexistfile: 'Der findes allerede en fil med dette navn',
            archivingdate: 'Arkiveringsdato',
            attachfile: 'Vedhæft fil',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Opret e-mail',
            composefromtemplate: 'Opret fra skabelon',
            delay: 'Forsinke',
            delays: {
                day1: 'In 1 day',
                days2: 'Om 2 dage',
                days4: 'Om 4 dage',
                hour1: '1 time',
                hours2: '2 timer',
                hours4: '4 timer',
                month1: 'om 1 måned',
                negativeday1: '1 dag før',
                negativedays2: '2 dage før',
                negativedays4: '4 dage før',
                negativehour1: '1 time før',
                negativehours2: '2 timer før',
                negativehours4: '4 timer før',
                negativemonth1: '1 måned før',
                negativeweek1: '1 uge før',
                negativeweeks2: '2 uger før',
                none: 'Ingen',
                week1: 'Om 1 uge',
                weeks2: 'Om 2 uger'
            },
            dmspaneltitle: 'Vælg vedhæftede filer fra databasen',
            edit: 'Redigerer',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Fra',
            gridrefresh: 'Gitter opdatering',
            groupbystatus: '<em>Grouped by status</em>',
            keepsynchronization: 'Forsøg synkronisering',
            maxsize: '<em>The maximum total size allowed for attachments is {0}.</em>',
            message: 'Besked',
            regenerateallemails: 'Regenerér alle e-mails',
            regenerateemail: 'Regenerere e-mail',
            reloadconfirmationmessage: '<em>E-mails in draft status will be deleted. Do you want to continue?</em>',
            remove: 'Fjern',
            remove_confirmation: 'Er du sikker på, at du vil slette denne email?',
            reply: 'svar',
            replyprefix: 'På {0} skrev {1}:',
            searchattachment: '<em>Search Attachment</em>',
            searchcard: '<em>Search card</em>',
            selectaclass: 'Vælg en klasse',
            sendemail: 'Send e-mail',
            sendingemail: '<em>Sendig email.</em>',
            signature: '<em>Signature</em>',
            size: '<em>Size</em>',
            status: '<em>Status</em>',
            statuses: {
                draft: 'Klade',
                error: 'Fejl',
                new: '<em>New</em>',
                outgoing: 'Udgående',
                received: 'Modtaget',
                sent: 'Sendt',
                skipped: '<em>Skipped</em>'
            },
            subject: 'Emne',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Til',
            view: 'Oversigt'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Forkert brugernavn eller adgangskode',
            classnotfound: 'Klasse {0} ikke fundet',
            fieldrequired: 'dette felt er påkrævet',
            invalidfilter: 'Ugyldigt filter',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Item blev ikke fundet',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Handlinger',
            addfilter: 'Tilføj filter',
            any: '<em>At least one</em>',
            attachments: '<em>Attachments</em>',
            attachmentssearchtext: '<em>Attachments search text</em>',
            attribute: 'Vælg en egenskab',
            attributes: 'Egenskaber',
            clearfilter: 'Ryd filter',
            clone: 'Duplikere',
            copyof: 'Kopi af',
            currentgroup: 'Nuværende gruppe',
            currentuser: 'Nuværende bruger',
            defaultset: 'Indstillet som standard',
            defaultunset: 'Frakoblet fra standard',
            description: 'Beskrivelse',
            domain: 'Domæne',
            errorname: '<em>Filter name already in use</em>',
            filterdata: 'Filtrer data',
            fromfilter: '<em>From filter</em>',
            fromselection: 'Fra markering',
            group: 'Gruppe',
            ignore: 'Ignorere',
            migrate: 'Migrerer',
            name: 'Navn',
            newfilter: 'Ny filter',
            noone: 'Ingen',
            operator: 'Operatør',
            operators: {
                and: '<em>And</em>',
                beginswith: 'Start med',
                between: 'Mellem',
                contained: 'Indeholdt',
                containedorequal: 'Indeholder eller lige',
                contains: 'Indeholder',
                containsorequal: 'Indeholder eller lige',
                descriptionbegin: '<em>Description begins with</em>',
                descriptioncontains: 'Beskrivelse indeholder',
                descriptionends: '<em>Description ends with</em>',
                descriptionnotbegin: '<em>Description does not begins with</em>',
                descriptionnotcontain: '<em>Description does not contain</em>',
                descriptionnotends: '<em>Description does not ends with</em>',
                different: 'Forskellige',
                doesnotbeginwith: 'Begynder ikke med',
                doesnotcontain: 'Indeholder ikke',
                doesnotendwith: 'Slutter ikke med',
                endswith: 'Slutter med',
                equals: 'Lige med',
                greaterthan: 'større end',
                isnotnull: 'Er ikke null',
                isnull: 'Er null',
                lessthan: 'Mindre end',
                or: '<em>Or</em>'
            },
            relations: 'Relationer',
            removeattribute: '<em>Remove attribute</em>',
            removeblock: '<em>Remove block</em>',
            type: 'Type',
            typeinput: 'Input Parameter',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Bruger',
            value: 'Værdi'
        },
        gis: {
            card: 'Kort',
            cardsMenu: 'Kort Menu',
            code: 'Kode',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Beskrivelse',
            extension: {
                errorCall: 'Fejl',
                noResults: 'Ingen resultater'
            },
            externalServices: 'Eksterne tjenester',
            geographicalAttributes: 'Geografiske egenskab',
            geoserverLayers: 'Geoserver lag',
            labels: {
                all: '<em>All</em>',
                alltitle: '<em>Show all labels</em>',
                alltitleclass: '<em>Show all labels of the class</em>',
                clear: '<em>Clear search</em>',
                hidden: '<em>Hidden</em>',
                hiddentitle: '<em>Hide labels</em>',
                hideinfowindow: '<em>Hide info window</em>',
                label: '<em>Labels</em>',
                search: '<em>Search on the map</em>',
                selected: '<em>Selected</em>',
                selectedtitle: '<em>Show label for selected item</em>',
                showinfowindow: '<em>Show info window</em>'
            },
            layers: 'Lag',
            layersTab: {
                clearPreferences: '<em>Clear layers preferences</em>',
                preferencesCleared: '<em>Layers preferences cleared</em>',
                preferencesSaved: '<em>Layers preferences saved</em>',
                savePreferences: '<em>Save layers preferences</em>',
                updatePreferences: '<em>Update layers preferences</em>'
            },
            list: 'List',
            longpresstitle: 'Geo-elementer i området',
            map: 'Kort',
            mapServices: 'Korttjenester',
            menu: {
                font: '<em>Font size</em>',
                options: '<em>Options</em>'
            },
            position: 'Position',
            root: 'Rod',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Filstruktur',
            type: 'Type',
            view: 'Oversigt',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Aktivitets navn',
            activityperformer: 'Aktivitet udfører',
            attributes: '<em>Attributes ({0})</em>',
            begindate: 'Start dato',
            enddate: 'Slut dato',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: '<em>Other relations</em>',
            processstatus: 'Status',
            referencerelation: '<em>Reference relations</em>',
            relation: '<em>Relation</em>',
            system: '<em>System</em>',
            type: '<em>Type</em>',
            user: 'Bruger',
            userdescription: '<em>User description</em>',
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
                user: 'Database bruger'
            },
            downloadreport: 'Hent rapport',
            emailfailure: 'Der opstod en fejl under afsendelse af e-mail!',
            emailmessage: 'Vedhæftet importrapport af filen "{0}" i dato {1}',
            emailsubject: 'Import rapport data',
            emailsuccess: 'E-mailen er blevet sendt med succes!',
            export: 'Eksport',
            exportalldata: 'Alle data',
            exportfiltereddata: 'Kun data, der matcher gitterfilteret',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Importen af figurer er ikke aktiveret for denne skabelon',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: '<em>Card</em>',
                project: 'Projekt',
                sourcetype: 'Import fra'
            },
            import: 'Import',
            importresponse: 'Import svar',
            response: {
                created: '<em>Created cards</em>',
                deleted: '<em>Deleted cards</em>',
                errors: 'Fejl',
                linenumber: 'Linjenummer',
                message: 'Besked',
                modified: '<em>Modified cards</em>',
                notprocessed: '<em>Unprocessed cards</em>',
                processed: 'Behandlede rækker',
                recordnumber: 'Rekordnumre',
                unchanged: '<em>Unchanged cards</em>'
            },
            sendreport: 'Send rapport',
            template: 'Skabelon',
            templatedefinition: 'Skabelon definition'
        },
        joinviews: {
            active: '<em>Active</em>',
            addview: '<em>Add view</em>',
            alias: '<em>Alias</em>',
            attribute: '<em>Attribute</em>',
            attributecustomization: '<em>Attributes customizations</em>',
            attributes: '<em>Attributes</em>',
            attributeschoice: '<em>Attributes choice</em>',
            attributesof: '<em>Attributes of: {0}</em>',
            createview: '<em>Create view</em>',
            datasorting: '<em>Data sortings</em>',
            delete: '<em>Delete</em>',
            deleteview: '<em>Delete view</em>',
            deleteviewconfirm: '<em>Are you sure that you want to delete this view?</em>',
            description: '<em>Description</em>',
            disable: '<em>Disable</em>',
            domainalias: '<em>Domain alias</em>',
            domainsof: '<em>Domains of {0}</em>',
            edit: '<em>Edit</em>',
            editview: '<em>Edit view configuration</em>',
            enable: '<em>Enable</em>',
            fieldsets: '<em>Fieldsets</em>',
            filters: '<em>Filters</em>',
            generalproperties: '<em>General properties</em>',
            group: '<em>Group</em>',
            innerjoin: '<em>Inner join</em>',
            jointype: '<em>Join type</em>',
            joinview: '<em>view from join</em>',
            klass: '<em>Class</em>',
            manageview: '<em>Manage view</em>',
            masterclass: '<em>Master class</em>',
            masterclassalias: '<em>Master class alias</em>',
            name: '<em>Name</em>',
            newjoinview: '<em>New view from join</em>',
            outerjoin: '<em>Outer join</em>',
            pleaseseleceavalidmasterclass: '<em>Please select a valid master class</em>',
            refreshafteredit: '<em>Do you want to refresh the page to see the changes?</em>',
            selectatleastoneattribute: '<em>Please select at least one attribute</em>',
            selectdisplayattributegrid:
                '<em>Please select at least one attribute to display in grid and one in reduced grid</em>',
            showingrid: '<em>Show in grid</em>',
            showinreducedgrid: '<em>Show in reduced grid</em>',
            targetalias: '<em>Target class alias</em>'
        },
        login: {
            buttons: {
                login: 'Log på',
                logout: 'Skift bruger'
            },
            fields: {
                group: 'Gruppe',
                language: 'Sprog',
                password: 'Kodeord',
                tenants: 'Tenants',
                username: 'Brugernavn'
            },
            loggedin: 'Logget ind',
            sso: {
                loginfailure: '<em>Login failure</em>',
                loginwith: '<em>Login with {0}</em>',
                or: '<em>or</em>',
                usernotenabled: '<em>User not enabled</em>'
            },
            title: 'Log på',
            welcome: 'Velkommen tilbage {0}.'
        },
        main: {
            administrationmodule: 'Administrationsmodul',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Basis konfiguration',
            cardlock: {
                lockedmessage: 'Du kan ikke redigere dette kort, fordi {0} redigerer i det.',
                someone: 'nogen'
            },
            changegroup: 'Skift gruppe',
            changetenant: 'Skift {0}',
            confirmchangegroup: 'Er du sikker på, at du vil ændre gruppen?',
            confirmchangetenants: 'Er du sikker på, at du vil ændre de aktive tenants?',
            confirmdisabletenant: 'Er du sikker på, at du vil indaktivere "Ignorere tenants" flag?',
            confirmenabletenant: 'Er du sikker på, at du vil aktivere "Ignorere tenants" flag?',
            description: '<em>Description</em>',
            ignoretenants: 'Indaktivere {0}',
            info: 'Info',
            logo: {
                cmdbuild: 'CMDBuild logo',
                cmdbuildready2use: 'CMDBuild READY2USE logo',
                companylogo: 'Virksomheds logo',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'openMAINT logo'
            },
            logout: 'log ud',
            managementmodule: 'Datastyringsmodul',
            multigroup: 'Multi-gruppe',
            multitenant: 'Multi-{0}',
            navigation: 'Navigation',
            pagenotfound: 'Siden blev ikke fundet',
            password: {
                change: 'Skift kodeord',
                confirm: 'Godkend kodeord',
                email: 'Email adresse',
                err_confirm: 'Adgangskoden stemmer ikke overens.',
                err_diffprevious: 'Adgangskoden kan ikke være identisk med den forrige.',
                err_diffusername: 'Adgangskoden kan ikke være identisk med brugernavnet.',
                err_length: 'Adgangskoden skal være mindst {0} tegn lang.',
                err_reqdigit: 'Adgangskoden skal indeholde mindst et ciffer.',
                err_reqlowercase: 'Adgangskoden skal indeholde mindst et lille bogstav.',
                err_requppercase: 'Adgangskoden skal indeholde mindst et stort bogstav.',
                expired: 'Din adgangskode er udløbet. Du skal ændre det nu.',
                forgotten: 'jeg har glemt mit kodeord',
                new: 'Ny kodeord',
                old: 'Tidligere kodeord',
                recoverysuccess: 'Vi har sendt dig en e-mail med instruktion om at gendanne din adgangskode.',
                reset: 'Nulstille kodeord',
                saved: 'Adgangskode korrekt gemt!'
            },
            pleasecorrecterrors: 'Ret venligst angivne fejl!',
            preferences: {
                comma: 'Komma',
                decimalserror: 'Decimalfelt skal være til stede',
                decimalstousandserror: 'Decimal og tusind separator skal være forskellige',
                default: 'Standard',
                defaultvalue: 'Standard værdi',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: '<em>First day of week</em>',
                gridpreferencesclear: 'Ryd gitterpræferencer',
                gridpreferencescleared: 'Gitter-præferencer ryddet!',
                gridpreferencessave: 'Gem gitter-præferencer',
                gridpreferencessaved: 'Gitter-præferencer gemt!',
                gridpreferencesupdate: 'Opdater gitter-præferencer',
                groupemailsbystatus: '<em>Show emails grouped by status</em>',
                labelcsvseparator: 'CSV-separator',
                labeldateformat: 'Datoformat',
                labeldecimalsseparator: 'Decimal separator',
                labellanguage: 'Sprog',
                labelthousandsseparator: 'Tusind separator',
                labeltimeformat: 'Tidsformat',
                msoffice: 'Microsoft Office',
                no: '<em>No</em>',
                notificationslabel: '<em>Chat & Notifications</em>',
                notificationssound: '<em>Play sound for notifications</em>',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Periode',
                preferredfilecharset: 'CSV-kodning',
                preferredofficesuite: 'Foretrukken Office-pakke',
                sendcancellationperiod: '<em>Email send cancellation period (seconds)</em>',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: 'Plads',
                thousandserror: 'Tusindfelt skal være til stede',
                timezone: 'Tidszone',
                twelvehourformat: '12-timers format',
                twentyfourhourformat: '24-timers format',
                yes: '<em>Yes</em>'
            },
            searchinallitems: 'Søg i alle emner',
            searchtenant: '<em>Search tenant</em>',
            selectdeselettenants: '<em>Select/Deselect all</em>',
            treenavcontenttitle: '{0} of {1}',
            userpreferences: 'Indstillinger'
        },
        menu: {
            allitems: 'Alle Emner',
            classes: 'Klasser',
            custompages: 'Brugerdefinerede sider',
            dashboards: 'Dashboards',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: 'Processer',
            reports: 'Rapporter',
            setasinitialpage: '<em>Set as initial page</em>',
            views: 'Oversigt'
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
            edit: 'Ændr noter'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: '<em>Delete notification</em>',
            emptymessage: '<em>There are no notifications to display.</em>',
            label: '<em>Notifications</em>',
            viewall: '<em>View all</em>'
        },
        notifier: {
            attention: 'Bemærk',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Fejl',
            genericerror: 'Generisk fejl',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Generisk info',
            genericwarning: 'Generisk advarsel',
            info: 'Info',
            more: '<em>More info</em>',
            success: 'Succes',
            warning: 'Advarsel'
        },
        patches: {
            apply: 'Påfør patches',
            category: 'Kategori',
            description: 'Beskrivelse',
            name: 'Navn',
            patches: 'Patches'
        },
        processes: {
            abortconfirmation: 'Er du sikker på, at du vil afbryde denne proces?',
            abortprocess: 'Afbryd processen',
            action: {
                advance: 'Fortsæt',
                label: 'Handling'
            },
            activeprocesses: 'Aktivitets processer',
            allstatuses: 'Alle',
            editactivity: 'Ændr aktivitet',
            openactivity: 'Åben aktivitet',
            resumeprocesses: '<em>Processes that are not suspended cannot be resumed</em>',
            startworkflow: 'Start',
            workflow: 'Arbejdsgang'
        },
        relationGraph: {
            activity: 'Aktivitet',
            allLabelsOnGraph: 'alle etiketter på grafen',
            card: 'Kort',
            cardList: 'Kortlist',
            cardRelations: 'Kortrelationer',
            choosenaviagationtree: 'Vælg navigationstræ',
            class: 'Klasse',
            classList: 'Klass-liste',
            compoundnode: 'Forbindelsesnode',
            disable: 'Deaktiver',
            edges: '<em>Edges</em>',
            enable: 'Aktiver',
            labelsOnGraph: 'værktøjstip på grafen',
            level: 'Niveau',
            nodes: '<em>Nodes</em>',
            openRelationGraph: 'Åben relationsgrafik',
            quantity: '<em>Quantity</em>',
            refresh: 'Opdater',
            relation: 'forhold',
            relationGraph: 'Relation Graph',
            reopengraph: 'Genåbn grafen fra dette punkt'
        },
        relations: {
            adddetail: 'Tilføj kort',
            addrelations: 'Tilføj detaljer',
            attributes: 'Egenskaber',
            begindate: '<em>Begin date</em>',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Kode',
            deletedetail: 'Slet detaljer',
            deleterelation: 'Slet relation',
            deleterelationconfirm: 'Er du sikker på, at du vil slette denne relation?',
            description: 'Beskrivelse',
            editcard: 'Redigere kort',
            editdetail: 'Redigere detaljer',
            editrelation: 'Redigere relation',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Udvidede data',
            mditems: 'Emner',
            missingattributes: 'Manglende obligatoriske attributter',
            opencard: 'Åben relateret kort',
            opendetail: 'Vis detaljer',
            relationdata: '<em>Relation data</em>',
            type: 'Type'
        },
        reports: {
            csv: 'CSV',
            download: 'Hent',
            format: 'Format',
            msgs: {
                creationerror: '<em>Something went wrong during the start of the report creation.</em>',
                creationstarted:
                    '<em>Report creation started. You will be notified when it will be ready for the download.</em>'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Print',
            reload: 'Opdater',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Standard'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Daglig'
                        },
                        monthly: {
                            description: 'Månedlige'
                        },
                        once: {
                            description: 'Enkelt gang'
                        },
                        weekly: {
                            description: 'Ugentlig'
                        },
                        yearly: {
                            description: 'Årligt'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Standard'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: '<em>Nobody</em>'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Tilføj tematisme',
            analysisType: 'Analyse Type',
            attribute: 'Egenskab',
            bordersIntervals: '<em>Wrong configuration for intervals limits</em>',
            calculateRules: 'Generer stilregler',
            clearThematism: 'Ryd tematisme',
            color: 'Farve',
            defineLegend: 'Legend definition',
            defineThematism: 'Thematisme definition',
            deletethematism: '<em>Are you sure that you want to delete this thematism?</em>',
            editThematism: '<em>Edit rules configuration</em>',
            function: 'funktion',
            generate: 'Generere',
            geoAttribute: 'Geografisk attribut',
            graduated: 'Gradueret',
            highlightSelected: 'Fremhæv markeret element',
            intervals: 'intervaller',
            legend: 'legende',
            name: 'Navn',
            newThematism: 'Ny tematisme',
            punctual: 'Punktlig',
            quantity: 'Quantity',
            segments: 'segmenter',
            source: 'Kilde',
            table: 'Tabel',
            thematism: 'Thematisms',
            value: 'Værdi'
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
                    exitwithoutsave: '<em>Exit without saving</em>',
                    stayinedit: '<em>Back to edit</em>'
                },
                savebeforeexit:
                    '<em>There are unsaved changes in the attachments. Do you want to save them before you exit?</em>',
                saveprocessmessage:
                    '<em>Attachments can only be saved in existing cards. Pressing on save, the process instance will be created with the data entered in the form.</em>'
            },
            calendar: {
                error: '<em>The parameter {0} is not set</em>',
                errorwrong: '<em>The parameter {0} is not set correctly</em>'
            },
            customform: {
                addrow: 'Tilføj række',
                clonerow: 'Dubliker række',
                datanotvalid: 'Data er ikke gyldige',
                deleterow: 'Slet række',
                editrow: 'Rediger række',
                export: 'Eksport',
                import: 'Import',
                importexport: {
                    expattributes: 'Data, der skal eksporteres',
                    file: 'Fil',
                    filename: 'filnavn',
                    format: 'Format',
                    importmode: 'Importtilstand',
                    keyattributes: 'Nøgleegenskaber',
                    missingkeyattr: 'Vælg mindst én nøgleattribut',
                    modeadd: 'Tilføj',
                    modemerge: 'flet',
                    modereplace: 'Erstatte',
                    separator: 'Separator'
                },
                refresh: 'Andvend standardindstillinger'
            },
            linkcards: {
                checkedonly: 'Kun kontrolleret',
                editcard: 'Redigere kort',
                opencard: 'Åben kort',
                refreshselection: 'Anvend standardvalg',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Aktivér gitterfilter',
                togglefilterenabled: 'Deaktiver gitterfilter'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                '<em>Inline mode is not supported for this widget type. Please fix the widget configuration.</em>',
            required: 'Denne widget er påkrævet.'
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
