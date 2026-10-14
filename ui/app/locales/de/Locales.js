(function () {
    Ext.define('CMDBuildUI.locales.de.Locales', {
        requires: ['CMDBuildUI.locales.de.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'de',
        administration: CMDBuildUI.locales.de.LocalesAdministration.administration,
        ai: {
            clearconversation: 'Unterhaltung löschen',
            clearconversationmessage: 'Sind Sie sicher, dass Sie die aktuelle Unterhaltung löschen möchten?',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: 'Entschuldigen Sie, es gab Fehler in der Antwort',
            headertooltip: 'CMDBuild Chat-Bot',
            introtextbody: 'Um den Chat zu starten, beschreiben Sie Ihre Anfrage im entsprechenden Feld',
            introtexttitle: 'Wie kann ich Ihnen helfen?',
            loading: 'Wird geladen...',
            newchat: 'Neuer Chat',
            newchattooltip: 'Neue Unterhaltung starten',
            questionemptytext: 'Geben Sie Ihre Frage hier ein...',
            resources: 'Quellen',
            viewrelatedcard: 'Zugehörige Karte ansehen',
            viewresource: 'Ressource ansehen'
        },
        arialabels: {
            buttonplaceholder: 'Button',
            iframeplaceholder: 'Texteditor',
            languageselector: 'Sprachauswahl',
            resizepanel: {
                horizontally: 'Paneele horizontal in der Größe ändern',
                vertically: 'Paneele vertikal in der Größe ändern'
            },
            usermenu: 'Benutzermenü'
        },
        attachments: {
            add: 'Anlage hinzufügen',
            attachmenthistory: 'Anhangsverlauf',
            author: 'Autor',
            authordescription: 'Autor-Beschreibung',
            browse: 'Durchsuchen &hellip;',
            category: 'Kategorie',
            code: 'Code',
            creationdate: 'Erstellungsdatum',
            deleteattachment: 'Anlage löschen',
            deleteattachment_confirmation: 'Möchten Sie die Anlage wirklich löschen?',
            description: 'Beschreibung',
            download: 'Herunterladen',
            dropfiles: 'Hier Dateien ziehen',
            editattachment: 'Anlage bearbeiten',
            errordms: 'Das DMS ist deaktiviert und es ist nicht möglich Dateien zu speichern.',
            file: 'Datei',
            filealreadyinlist: 'Die Datei {0} ist bereits in der Liste.',
            filename: 'Datei-Name',
            fileview: 'Anlage zeigen',
            invalidfiles: 'ungültige Dateien löschen',
            majorversion: 'Hauptversion',
            maxsize: 'Die maximal zulässige Dateigröße ist {0} MB.',
            metadata: 'Metadaten',
            modificationdate: 'Änderungsdatum',
            new: 'Neue Anlage',
            nocategory: 'Unkategorisiert',
            preview: 'Voransicht',
            removefile: 'Datei löschen',
            statuses: {
                empty: 'leere Datei',
                error: 'Fehler',
                extensionNotAllowed: 'Dateierweiterung nicht erlaubt',
                loaded: 'geladen',
                ready: 'Fertig',
                toolarge: 'Zu groß'
            },
            successupload: '{0} Anlagen hochgeladen',
            uploadfile: 'Datei hochladen…',
            version: 'Version',
            viewhistory: 'Anhangsverlauf anzeigen',
            viewmetadata: 'Anhang-Metadaten anzeigen',
            warningmessages: {
                atleast:
                    '{0}: {1} Anlagen vom Typ "{2}" wurden hochgeladen. Diese Kategorie erwartet zumindest {3} Anlagen',
                exactlynumber:
                    '{0}: {1} Anlagen vom Typ "{2}" wurden hochgeladen. Diese Kategorie erwartet {3} Anlagen',
                maxnumber:
                    '{0}: {1} Anlagen vom Typ "{2}" wurden hochgeladen. Diese Kategorie erwartet höchstens {3} Anlagen'
            },
            wrongfileextension: '{0} Erweiterung ist nicht erlaubt'
        },
        bim: {
            bimViewer: 'Bim Viewer',
            card: {
                label: 'Karte'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Größe des Begrenzungsrahmens der Achse X entlang',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Größe des Begrenzungsrahmens der Achse Y entlang',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Größe des Begrenzungsrahmens der Achse Z entlang',
                calculated: 'Berechnet',
                description: 'Beschreibung',
                guid: 'GUID',
                label: 'Eigenschaften',
                name: 'Name',
                type: 'Typ',
                uuid: 'UUID'
            },
            layers: {
                label: 'Ebenen',
                menu: {
                    hideAll: 'Alle verstecken',
                    hideAllLayers: 'Alle Ebenen verstecken',
                    hideAllXeokit: 'Alle {0} verstecken',
                    showAll: 'Alle zeigen',
                    showAllLayers: 'Zeige alle Layer',
                    showAllXeokit: 'Zeige alle {0}'
                },
                name: 'Name',
                quantity: 'Anzahl',
                visibility: 'Sichtbarkeit'
            },
            menu: {
                camera: 'Kamera',
                disableslice: 'Schnittprofilobjekt deaktivieren',
                enableslice: 'Schnittprofilobjekt aktivieren',
                frontView: 'Vorderansicht',
                mod: 'Viewer-Steuerung',
                ortho: 'Zu Orthogonalansicht wechseln',
                orthographic: 'Orthogonale Kamera',
                pan: 'Schwenken',
                panXeokit: 'Viewer-Steuerung: scrollen (Shift)',
                perspe: 'Zu Persepktivansicht wechseln',
                perspective: 'Perspektivenkamera',
                resetSlice: 'Schnittprofil zurücksetzen',
                resetView: 'Ansicht zurücksetzen',
                rotate: 'Rotieren',
                rotateXeokit: 'Viewer-Steuerung: rotieren',
                save: 'Ansicht speichern',
                shortcuts: {
                    down: 'Nach unten drehen',
                    left: 'Nach links drehen',
                    right: 'Nach rechts drehen',
                    title: 'Tastaturkürzel',
                    up: 'Nach oben drehen',
                    zoomin: 'Herein zoomen',
                    zoomout: 'Heraus zoomen'
                },
                sideView: 'Seitenansicht',
                threeD: 'Zu 3D-Ansicht wechseln',
                topView: 'Draufsicht',
                twoD: 'Zu 2D-Ansicht wechseln'
            },
            navCube: {
                back: 'Rückseite',
                bottom: 'Unten',
                front: 'Vorderseite',
                left: 'Left',
                right: 'Rechts',
                top: 'Oben'
            },
            showBimCard: '3D-Viewer öffnen',
            tree: {
                arrowTooltip: 'Element auswählen',
                label: 'Baum',
                multipleElements: 'Elemente auswählen',
                open_card: 'Verbundene Karte öffnen',
                root: 'Ifc-Wurzel'
            }
        },
        bulkactions: {
            abort: 'ausgewählte Vorgänge abbrechen',
            addfields: 'Felder hinzufügen',
            alertdownloadattachments:
                'Einige Anlagen werden nicht heruntergeladen, da sie noch nicht auf dem Server gespeichert wurden.',
            alertnoattachmentsdownload:
                'Keine Anlagen werden heruntergeladen, da sie noch nicht auf dem Server gespeichert wurden.',
            cancelselection: 'Auswahl abbrechen',
            confirmabort: '{0} Prozessinstanzen werden abgebrochen. Möchten Sie wirklich fortfahren?',
            confirmdelete: '{0} Karten werden gelöscht. Möchten Sie wirklich fortfahren?',
            confirmdeleteattachements: '{0} Anlagen werden gelöscht. Möchten Sie wirklich fortfahren?',
            confirmedit: '{0} wird bei {1} Karten werden geändert. Möchten Sie wirklich fortfahren?',
            delete: 'ausgewählte Elemente löschen',
            download: 'Ausgewählte Anlagen herunterladen',
            downloadall: 'Alle Anlagen herunterladen',
            edit: 'ausgewählte Elemente bearbeiten',
            missingfields: 'Die folgenden Felder müssen für die korrekte Formular-Validierung hinzugefügt werden: {0}',
            noattributeselected: 'Wählen Sie bitte mindestens ein Attribut.',
            selectall: 'Alle Elemente auswählen'
        },
        calendar: {
            active_expired: 'Aktive/Abgelaufene',
            add: 'Hinzufügen',
            advancenotification: 'Voranmeldung',
            allcategories: 'Alle Kategorien',
            alldates: 'Alle Daten',
            associatedcard: 'Zugehörige Karte',
            calculated: 'Berechnet',
            calendar: 'Kalender',
            cancel: 'Als gelöscht markieren',
            category: 'Kategorie',
            class: 'Klasse',
            cm_confirmcancel: 'Möchten Sie wirklich die ausgewählten Zeitpläne als gelöscht markieren?',
            cm_confirmcomplete: 'Möchten Sie wirklich die ausgewählten Zeitpläne als vollendet markieren?',
            cm_markcancelled: 'Ausgewählte Zeitpläne als gelöscht markieren',
            cm_markcomplete: 'Ausgewählte Zeitpläne als abgeschlossen markieren',
            complete: 'Als erledigt markieren',
            completed: 'Abgeschlossen',
            date: 'Datum',
            days: 'Tage',
            delaybeforedeadline: 'Verzögerung vor dem Termin',
            delaybeforedeadlinevalue: 'Verzögerung vor dem Terminwert',
            description: 'Beschreibung',
            editevent: 'Zeitplan bearbeiten',
            enddate: 'Enddatum',
            endtype: 'Endtyp',
            event: 'Zeitplan',
            execapplyfilter: 'applyAdvanceFilter kann nicht ausgeführt werden, da es mehr als einen Speicher gibt',
            execremovefilter: 'removeAdvanceFilter kann nicht ausgeführt werden, da es mehr als einen Speicher gibt',
            executiondate: 'Ausführungsdatum',
            frequency: 'Häufigkeit',
            frequencymultiplier: 'Frequenzvervielfacher',
            grid: 'Raster',
            leftdays: 'Tage übrig',
            londdescription: 'Volle Beschreibung',
            manual: 'Manuell',
            maxactiveevents: 'Max. aktive Ereignisse',
            messagebodydelete: 'Möchten Sie die Regeln der Planer entfernen?',
            messagebodyplural: 'Es gibt {0} Planungsregeln',
            messagebodyrecalculate: 'Möchten Sie die Planungsregeln mit dem neuen Datum erneut berechnen?',
            messagebodysingular: 'Es gibt {0} Planungsregeln',
            messagetitle: 'Zeitplan erneut berechnen',
            missingdays: 'Fehlende Tage',
            next30days: 'Nächste 30 Tage',
            next7days: 'Nächste 7 Tage',
            notificationtemplate: 'Template für Benachrichtigung',
            notificationtext: 'Benachrichtigungstext',
            occurencies: 'Anzahl der Ereignisse',
            operation: 'Betrieb',
            partecipantgroup: 'Teilnehmergruppe',
            partecipantuser: 'Teilnehmerbenutzer',
            priority: 'Priorität',
            recalculate: 'Erneut berechnen',
            referent: 'Referent',
            scheduler: 'Planer',
            sequencepaneltitle: 'Zeitpläne erzeugen',
            startdate: 'Startdatum',
            status: 'Status',
            today: 'Heute',
            type: 'Typ',
            viewevent: 'Zeitplan ansehen',
            waiteventgeneration: 'Ereignisse werden generiert. Bitte warten Sie, bis der Vorgang abgeschlossen ist.',
            widgetcriterion: 'Berechnungskriterium',
            widgetemails: 'E-mails',
            widgetgotocard: 'Gehe zur Quell-Karte',
            widgetsourcecard: 'Quell-Karte'
        },
        chat: {
            title: 'Chat'
        },
        classes: {
            cards: {
                addcard: 'Karte hinzufügen',
                clone: 'Klonen',
                clonewithrelations: 'Karte und entsprechende Beziehungen klonen',
                deletebeaware: 'Beachten Sie dass:',
                deleteblocked:
                    'Da es eine Beziehungen mit {0} gibt, ist es nicht möglich, mit der Karten-Löschung fortzufahren.',
                deletecard: 'Karte löschen',
                deleteconfirmation: 'Möchten Sie diese Karte wirklich löschen?',
                deleterelatedcards: 'Auch die zugehörige {0} Karten werden gelöscht',
                deleterelations: 'Beziehungen mit {0} Karten werden gelöscht',
                label: 'Datenkarten',
                modifycard: 'Karte bearbeiten',
                opencard: 'Karte offen',
                print: 'Karte drucken'
            },
            simple: 'Einfach',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Hinzufügen',
                apply: 'Anwenden',
                cancel: 'Abbrechen',
                clear: 'Leeren',
                close: 'Schließen',
                copy: 'Kopieren',
                copyerrormessage: 'Nachricht in die Zwischenlage kopieren',
                delete: 'Löschen',
                edit: 'Bearbeiten',
                execute: 'Ausführen',
                forward: 'Vorwärts',
                help: 'Hilfe',
                hide: 'Verstecken',
                load: 'laden',
                open: 'Öffnen',
                previous: 'Vohergehende',
                refresh: 'Daten aktualisieren',
                remove: 'Löschen',
                save: 'Speichern',
                saveandapply: 'Speichern und Anwenden',
                saveandclose: 'Speichern und schließen',
                search: 'Suchen',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Suchen...'
            },
            and: 'und',
            attributes: {
                boolfalse: 'Falsch',
                booltrue: 'true',
                link: {
                    errlabel: 'Label ist verbindlich',
                    errurl: 'Url ist verpflichtend, wenn das Label nicht leer ist',
                    label: 'Label',
                    url: 'Url'
                },
                nogroup: 'Basisdaten'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'HTML löschen',
                expand: 'Editor erweitern',
                reduce: 'Editor reduzieren',
                signature: 'Unterschrift hinzufügen',
                unlink: 'Link entfernen',
                unlinkmessage: 'Den ausgewählten Link in Text verwandeln'
            },
            grid: {
                disablemultiselection: 'Mehrfachauswahl deaktivieren',
                enamblemultiselection: 'Mehrfachauswahl aktivieren',
                export: 'Exportieren von Daten',
                filterremoved: 'Dieser aktuelle Filter wurde entfernt',
                import: 'Importieren von Daten',
                itemnotfound: 'Element nicht gefunden',
                lastUpdate: '<em>Last update</em>',
                list: 'Liste',
                opencontextualmenu: 'Kontextmenü öffnen',
                print: 'Drucken',
                printcsv: 'Als CSV drucken',
                printmap: 'Karte drucken',
                printodt: 'Als ODT drucken',
                printpdf: 'Als PDF drucken',
                row: 'Element',
                rows: 'Elemente',
                subtype: 'Untertyp',
                thematism: 'Thematisierung'
            },
            tabs: {
                activity: 'Aktivität',
                attachment: 'Anlage',
                attachments: 'Anlagen',
                card: 'Karte',
                clonerelationmode: 'Beziehung-Modus klonen',
                details: 'Details',
                emails: 'E-mails',
                history: 'Chronologie',
                notes: 'Notizen',
                relations: 'Beziehungen',
                schedules: 'Zeitpläne',
                sources: 'Quellen'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: 'Alle anzeigen',
                showingnitems: 'Anzeige: {0} Elemente'
            },
            pareto: {
                cumulative: 'Kumulativ',
                cumulativeperc: 'Kumulativ %'
            },
            tools: {
                download: 'Diagramm herunterladen',
                downloaddata: 'Diagramm-Daten herunterladen',
                gridhide: 'Daten-Tabelle verstecken',
                gridshow: 'Zeige Daten-Raster',
                openinpopup: 'In Popup öffnen',
                reload: 'Neu laden',
                showall: 'Alle anzeigen'
            }
        },
        dependencytree: {
            cardinfo: 'Karte',
            filteredid: 'Die aktuelle Karte gehört nicht zu diesen Baum',
            gerarchicview: 'Hierarchische Ansicht',
            name: 'Abhängigkeitsbaum',
            nocardselected: 'Keine Karte ausgewählt',
            opendependencytree: 'Abhängigkeitsbaum öffnen',
            printjpeg: 'Als JPEG drucken',
            printpdf: 'Als PDF drucken',
            printpng: 'Als PNG drucken',
            printsvg: 'Als SVG drucken',
            reopengraph: 'Graf mit neuem Wurzelknoten  erneut öffnen',
            selectcard: 'Startknoten auswählen',
            wrongid: 'Die angegebene ID ist falsch'
        },
        emails: {
            abort: 'Abbrechen',
            addattachmentsfromdocumentarchive: 'vom Archiv hochladen',
            alredyexistfile: 'Eine Datei mit diesem Namen existiert schon',
            archivingdate: 'Archivierungsdatum',
            attachfile: 'File beilegen',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'E-Mail schreiben',
            composefromtemplate: 'Vom Template bilden',
            delay: 'Verzögerung',
            delays: {
                day1: 'In 1 Tag',
                days2: 'In 2 Tagen',
                days4: 'In 4 Tagen',
                hour1: '1 Stunde',
                hours2: '2 Stunden',
                hours4: '4 Stunden',
                month1: 'In 1 Monat',
                negativeday1: '1 Tag bevor',
                negativedays2: '2 Tage bevor',
                negativedays4: '4 Tage bevor',
                negativehour1: '1 Stunde bevor',
                negativehours2: '2 Stunden bevor',
                negativehours4: '4 Stunden bevor',
                negativemonth1: '1 Monat bevor',
                negativeweek1: '1 Woche bevor',
                negativeweeks2: '2 Wochen bevor',
                none: 'Keine',
                week1: 'In 1 Woche',
                weeks2: 'In 2 Wochen'
            },
            dmspaneltitle: 'Anhang aus Datenbank auswählen',
            edit: 'Bearbeiten',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Von',
            gridrefresh: 'Raster aktualisieren',
            groupbystatus: 'Gruppiert nach Status',
            keepsynchronization: 'Synchronisierung halten',
            maxsize: 'Die maximale Gesamtgröße für Anhänge ist {0}.',
            message: 'Nachricht',
            regenerateallemails: 'Alle E-Mails regenerien',
            regenerateemail: 'E-Mail regenerieren',
            reloadconfirmationmessage: 'E-Mails im Entwurf-Status werden gelöscht. Möchten Sie fortfahren?',
            remove: 'Löschen',
            remove_confirmation: 'Möchten Sie diese E-Mail wirklich löschen?',
            reply: 'Antworten',
            replyprefix: 'Am {0} schrieb {1}:',
            searchattachment: 'Anhang suchen',
            searchcard: 'Karte suchen',
            selectaclass: 'Wählen Sie eine Klasse aus',
            sendemail: 'E-Mail senden',
            sendingemail: 'E-Mail wird gesendet',
            signature: 'Unterschrift',
            size: 'Größe',
            status: 'Status',
            statuses: {
                draft: 'Entwürfe',
                error: 'Fehler',
                new: 'Neu',
                outgoing: 'Ausgehende',
                received: 'Eingehende',
                sent: 'Gesendete',
                skipped: 'Abgebrochen'
            },
            subject: 'Betreff',
            templateattachments: 'Diese E-Mail enthält Anhänge, die vom Template automatisch hochgeladen wurden',
            to: 'An',
            view: 'Ansichten'
        },
        errors: {
            actionnotallowed: 'Aktion nicht erlaubt',
            autherror: 'ID oder Passwort ungültig',
            classnotfound: 'Keine Klasse {0} gefunden',
            fieldrequired: 'Dieses Feld ist erforderlich',
            invalidfilter: 'Ungültiger Filter',
            nodatatoexport: 'Das Diagramm hat keine Daten zum Exportieren',
            notfound: 'Element nicht gefunden',
            value3chars: 'Der Wert muss mindestens 3 Zeichen lang sein.'
        },
        filters: {
            actions: 'Aktionen',
            addfilter: 'Filter hinzufügen',
            any: 'Mindestens eines',
            attachments: 'Anlagen',
            attachmentssearchtext: 'Anlagen-Suchtext',
            attribute: 'Ein Attribut auswählen',
            attributes: 'Attribute',
            clearfilter: 'Filter leeren',
            clone: 'Klonen',
            copyof: 'Kopie von',
            currentgroup: 'Aktuelle Gruppe',
            currentuser: 'Aktueller Benutzer',
            defaultset: 'Als Standard festlegen',
            defaultunset: 'Standardwert nicht setzen',
            description: 'Beschreibung',
            domain: 'Domain',
            errorname: 'Ein Filter mit demselben Name existiert schon',
            filterdata: 'Datei filtern',
            fromfilter: 'Von Filter',
            fromselection: 'Von Auswahl',
            group: 'Gruppe',
            ignore: 'Ignorieren',
            migrate: 'Migrieren',
            name: 'Name',
            newfilter: 'Neuer Filter',
            noone: 'Keine',
            operator: 'Operator',
            operators: {
                and: 'Und',
                beginswith: 'Beginnt mit',
                between: 'Zwischen',
                contained: 'Enthalten',
                containedorequal: 'Enthalten oder gleich',
                contains: 'Enthält',
                containsorequal: 'Enthält oder gleich',
                descriptionbegin: 'Die Beschreibung beginnt mit',
                descriptioncontains: 'die Beschreibung enthält',
                descriptionends: 'Die Beschreibung endet mit',
                descriptionnotbegin: 'Die Beschreibung beginnt nicht mit',
                descriptionnotcontain: 'Die Beschreibung enthält nicht',
                descriptionnotends: 'Die Beschreibung endet nicht mit',
                different: 'Verschieden',
                doesnotbeginwith: 'Beginnt nicht mit',
                doesnotcontain: 'Enthält nicht',
                doesnotendwith: 'Endet nicht mit',
                endswith: 'Endet mit',
                equals: 'Gleich',
                greaterthan: 'Höher',
                isnotnull: 'Not null',
                isnull: 'Null',
                lessthan: 'Niedriger',
                or: 'Oder'
            },
            relations: 'Beziehungen',
            removeattribute: 'Attribut löschen',
            removeblock: 'Block löschen',
            type: 'Typ',
            typeinput: 'Input Parameter',
            typeorchoosetheattributename: 'Attribut tippen oder auswählen',
            user: 'Benutzer',
            value: 'Wert'
        },
        gis: {
            card: 'Karte',
            cardsMenu: 'Karten-Menü',
            code: 'Code',
            contextmenu: {
                coordinates: 'Erhalte Koordinaten',
                coordinatescopiedmessage: 'Koordinaten in die Zwischenablage kopiert'
            },
            description: 'Beschreibung',
            extension: {
                errorCall: 'Fehler',
                noResults: 'Keine Ergebnisse'
            },
            externalServices: 'Externe Dienste',
            geographicalAttributes: 'Geographische Attribute',
            geoserverLayers: 'Ebenen von Geoserver',
            labels: {
                all: 'Alle',
                alltitle: 'Zeige alle Label',
                alltitleclass: 'Zeige alle Label der Klasse',
                clear: 'Suche zurücksetzen',
                hidden: 'Versteckt',
                hiddentitle: 'Label verstecken',
                hideinfowindow: 'Info-Fenster verstecken',
                label: 'Labels',
                search: 'Auf der Karte suchen',
                selected: 'Ausgewählt',
                selectedtitle: 'Zeige Label für ausgewähltes Element',
                showinfowindow: 'Zeige Info-Fenster'
            },
            layers: 'Ebenen',
            layersTab: {
                clearPreferences: 'Layer-Einstellungen leeren',
                preferencesCleared: 'Ebenen Auswahl gelöscht',
                preferencesSaved: 'Ebenen Auswahl gespeichert',
                savePreferences: 'Layer-Einstellungen speichern',
                updatePreferences: 'Schicht-Einstellungen aktualisieren'
            },
            list: 'Liste',
            longpresstitle: 'Geoelemente im Bereich',
            map: 'Karte',
            mapServices: 'Karten-Services',
            menu: {
                font: 'Schriftgröße',
                options: 'Optionen'
            },
            position: 'Stelle',
            root: 'Quelle',
            sourceMap: {
                default: 'Standard-Ansicht verwenden',
                satellite: 'Satelliten-Ansicht verwenden'
            },
            tree: 'Baumansicht',
            type: 'Typ',
            view: 'Ansichten',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Aktivitätsname',
            activityperformer: 'Ausführende der Aktivität',
            attributes: 'Attribute ({0})',
            begindate: 'Anfangsdatum',
            enddate: 'Enddatum',
            fieldname: 'Feldname',
            fulldata: 'vollständige Daten',
            onlychanges: 'Nur Änderungen',
            otherrelations: 'Andere Beziehungen',
            processstatus: 'Status',
            referencerelation: 'Beziehungen referenzieren',
            relation: 'Beziehung',
            system: 'System',
            type: 'Typ',
            user: 'Benutzer',
            userdescription: 'Benutzerbeschreibung',
            value: 'Wert',
            viewmode: 'Ansichtsmodus'
        },
        homepage: {
            search: {
                airequestproblem:
                    '<em>A problem occurred while processing the request. Please try again later or contact the administrator.</em>',
                aisearcherror: '<em>Unable to complete the search</em>',
                goodafternoon: 'Guten Tag',
                goodevening: 'Guten Abend',
                goodmorning: 'Guten Morgen',
                goodnight: 'Gute Nacht',
                noresults: '<em>No results found</em>',
                noresultshint: '<em>Try different keywords or check the spelling</em>',
                resultsfor: '<em>Results for: {0}</em>'
            }
        },
        iconscategories: {
            accessibility: 'Barrierefreiheit',
            animals: 'Tiere',
            arrows: 'Pfeile',
            audiovideo: 'Audio Video',
            automotive: 'Automobilindustrie',
            autumn: 'Herbst',
            beverage: 'Zwischen',
            buildings: 'Gebäude',
            business: 'Geschäft',
            charity: 'Wohltätigkeit',
            chat: 'Chat',
            chess: 'Schach',
            childhood: 'Kindheit',
            code: 'Code',
            communication: 'Kommunikation',
            computers: 'Computer',
            construction: 'Bau',
            currency: 'Währung',
            datetime: 'Datum Uhrzeit',
            design: 'Design',
            editors: 'Editoren',
            education: 'Bildung',
            emoji: 'Emoji',
            energy: 'Energie',
            files: 'Datei',
            finance: 'Finanzen',
            food: 'Lebensmittel',
            fruitvegetable: 'Obst Gemüse',
            games: 'Spiele',
            gamingtabletop: 'Spieltisch',
            gender: 'Geschlecht',
            general: 'Allgemein',
            halloween: 'Halloween',
            hands: 'Hände',
            hotel: 'Hotel',
            household: 'Haushalt',
            images: 'Bilder',
            interfaces: 'Schnittstellen',
            logistics: 'Logistik',
            maps: 'Karten',
            maritime: 'Seefahrt',
            marketing: 'Marketing',
            mathematics: 'Mathematik',
            medical: 'Medizin',
            moving: 'Umzug',
            music: 'Musik',
            objects: 'Baum',
            paymentsshopping: 'Zahlungen Einkaufen',
            political: 'Politisch',
            religion: 'Religion',
            sciencefiction: 'Science Fiction',
            security: 'Sicherheit',
            shapes: 'Formen',
            shopping: 'Shopping',
            social: 'Sozial',
            spinners: 'Spinner',
            sports: 'Sport',
            spring: 'Frühling',
            status: 'Status',
            toggle: 'Umschalten',
            travel: 'Reise',
            userspeople: 'Benutzer Personen',
            vehicles: 'Fahrzeuge',
            weather: 'Wetter',
            winter: 'Winter'
        },
        importexport: {
            database: {
                uri: 'URI Database',
                user: 'Benutzer Database'
            },
            downloadreport: 'Bericht herunterladen',
            emailfailure: 'Beim Senden der E-Mail ist ein Fehler aufgetreten!',
            emailmessage: 'Im Anhang Importieren von Berichtvorlagen vom File "{0}" in Datum {1}',
            emailsubject: 'Datenbericht importieren',
            emailsuccess: 'Die E-Mail wurde erfolgreich gesendet!',
            export: 'Exportieren',
            exportalldata: 'Alle Dateien',
            exportfiltereddata: 'Nur Daten, die dem Netzfilter entsprechen',
            gis: {
                importconfiguration: 'Konfiguration importieren',
                shapeimportdisabled: 'Der Shape-Import ist nicht für dieses Template aktiviert',
                shapeimportenabled: 'Der Shape-Import ist für dieses Template aktiviert'
            },
            ifc: {
                card: 'Karte',
                project: 'Projekt',
                sourcetype: 'Importieren von'
            },
            import: 'Importieren',
            importresponse: 'Einfuhrentscheidung',
            response: {
                created: 'Erzeugte Karten',
                deleted: 'Gelöschte Karten',
                errors: 'Fehler',
                linenumber: 'Zeilennummer',
                message: 'Nachricht',
                modified: 'Geänderte Karten',
                notprocessed: 'Nicht verarbeitete Karten',
                processed: 'Verarbeitete Zeilen',
                recordnumber: 'Aktennummer',
                unchanged: 'Unveränderte Karten'
            },
            sendreport: 'Bericht senden',
            template: 'Template',
            templatedefinition: 'Template-Definition'
        },
        joinviews: {
            active: 'Aktiv',
            addview: 'Ansicht hinzufügen',
            alias: 'Alias',
            attribute: 'Attribut',
            attributecustomization: 'Attribut-Anpassungen',
            attributes: 'Attribute',
            attributeschoice: 'Attribut-Auswahl',
            attributesof: 'Attribute von: {0}',
            createview: 'Ansicht erzeugen',
            datasorting: 'Datensortierung',
            delete: 'Löschen',
            deleteview: 'Ansicht löschen',
            deleteviewconfirm: 'Möchten Sie diese Ansicht wirklich löschen?',
            description: 'Beschreibung',
            disable: 'Deaktivieren',
            domainalias: 'Domain alias',
            domainsof: 'Domains von {0}',
            edit: 'Bearbeiten',
            editview: 'Ansicht-Konfiguration bearbeiten',
            enable: 'Aktivieren',
            fieldsets: 'Feldgruppen',
            filters: 'Filtern',
            generalproperties: 'Allgemeine Eigenschaften',
            group: 'Gruppe',
            innerjoin: 'Inner join',
            jointype: 'Join-Typ',
            joinview: 'Von join ansehen',
            klass: 'Klasse',
            manageview: 'Ansicht verwalten',
            masterclass: 'Hauptklasse',
            masterclassalias: 'Alias der Hauptklasse',
            name: 'Name',
            newjoinview: 'Neue Ansicht von Join',
            outerjoin: 'Outer join',
            pleaseseleceavalidmasterclass: 'Wählen Sie bitte eine gültige Hauptklasse aus',
            refreshafteredit: 'Möchten Sie die Seite aktualisieren, um die Änderungen zu sehen?',
            selectatleastoneattribute: 'Wählen Sie bitte mindestens ein Attribut',
            selectdisplayattributegrid:
                'Wählen Sie bitte mindestens ein Attribut, das im Raster und eines, das im reduzierten Raster angezeigt werden soll.',
            showingrid: 'Im Raster zeigen',
            showinreducedgrid: 'Im reduzierten Raster zeigen',
            targetalias: 'Alias der Zielklasse'
        },
        login: {
            buttons: {
                login: 'Anmelden',
                logout: 'Benutzer wechseln'
            },
            fields: {
                group: 'Gruppe',
                language: 'Sprache',
                password: 'Passwort',
                tenants: 'Mandanten',
                username: 'Benutzername'
            },
            loggedin: 'Angemeldet',
            sso: {
                loginfailure: 'Anmeldefehler',
                loginwith: 'Als {0} anmelden',
                or: 'oder',
                usernotenabled: 'Benutzer ist nicht aktiviert'
            },
            title: 'Anmelden',
            welcome: 'Willkommen zurück {0}.'
        },
        main: {
            administrationmodule: 'Verwaltungsformular',
            alerts: 'Warnungen',
            baseconfiguration: 'Basiskonfiguration',
            cardlock: {
                lockedmessage: 'Sie dürfen diese Karte nicht bearbeiten. Sie wird gerade von {0} bearbeitet.',
                someone: 'jemand'
            },
            changegroup: 'Gruppe wechseln',
            changetenant: '{0} wechseln',
            confirmchangegroup: 'Möchten Sie Gruppe wirklich wechseln?',
            confirmchangetenants: 'Möchten Sie aktive Mandanten wirklich wechseln?',
            confirmdisabletenant: 'Möchten Sie das Flag "Mandanten ignorieren" wirklich deaktivieren?',
            confirmenabletenant: 'Möchten Sie das Flag "Mandanten ignorieren" wirklich aktivieren?',
            description: 'Beschreibung',
            ignoretenants: '{0} ignorieren',
            info: 'Information',
            logo: {
                cmdbuild: 'CMDBuild Logo',
                cmdbuildready2use: 'CMDBuild READY2USE Logo',
                companylogo: 'Firmenlogo',
                hdaitsm: 'HDA ITSM',
                openmaint: 'openMAINT Logo'
            },
            logout: 'Abmelden',
            managementmodule: 'Datenmanagement-Modul',
            multigroup: 'Multigruppe',
            multitenant: 'Multi {0}',
            navigation: 'Navigieren',
            pagenotfound: 'Seite nicht gefunden',
            password: {
                change: 'Passwort ändern',
                confirm: 'Passwort bestätigen',
                email: 'E-Mail Adresse',
                err_confirm: 'Passwort stimmt nicht überein.',
                err_diffprevious: 'Das Passwort darf nicht mit dem vorherigen identisch sein.',
                err_diffusername: 'Das Passwort darf nicht mit dem Benutzernamen identisch sein.',
                err_length: 'Das Passwort muss zumindest {0} Zeichen lang sein.',
                err_reqdigit: 'Das Passwort muss zumindest eine Ziffer enthalten.',
                err_reqlowercase: 'Das Passwort muss zumindest einen Kleinbuchstaben enthalten.',
                err_requppercase: 'Das Passwort muss zumindest einen Großbuchstaben enthalten.',
                expired: 'Ihr Passwort ist abgelaufen. Sie müssen es ändern.',
                forgotten: 'Ich habe mein Passwort vergessen',
                new: 'Neues Passwort',
                old: 'Altes Passwort',
                recoverysuccess:
                    'Wir haben Ihnen eine E-Mail mit der Anweisung geschickt, Ihr Passwort wiederherzustellen',
                reset: 'Passwort zurücksetzen',
                saved: 'Passwort korrekt gespeichert!'
            },
            pleasecorrecterrors: 'Bitte korrigieren Sie die angezeigten Fehler!',
            preferences: {
                comma: 'Komma',
                decimalserror: 'Dezimalfeld muss vorhanden sein',
                decimalstousandserror: 'Dezimal- und Tausendertrennzeichen müssen unterschiedlich sein',
                default: 'Standard',
                defaultvalue: 'Default-Wert',
                favouritemenulocation: 'Ort des Favoritenmenüs',
                firstdayofweek: 'Erster Tag der Woche',
                gridpreferencesclear: 'Raster-Einstellungen leeren',
                gridpreferencescleared: 'Raster-Einstellungen geleert!',
                gridpreferencessave: 'Raster-Einstellungen speichern',
                gridpreferencessaved: 'Raster-Einstellungen gespeichert!',
                gridpreferencesupdate: 'Raster-Einstellungen aktualisieren',
                groupemailsbystatus: 'Zeige Emails nach Status gruppiert',
                labelcsvseparator: 'CSV Trennzeichen',
                labeldateformat: 'Datumsformat',
                labeldecimalsseparator: 'Dezimaltrennzeichen',
                labellanguage: 'Sprache',
                labelthousandsseparator: 'Tausendertrennzeichen',
                labeltimeformat: 'Zeitformat',
                msoffice: 'Microsoft Office',
                no: 'Nein',
                notificationslabel: 'Chat & Nachrichten',
                notificationssound: 'Benachrichtigungston abspielen',
                notificationssoundvolume: 'Benachrichtigungslautstärke',
                period: 'Periode',
                preferredfilecharset: 'CSV Encoding',
                preferredofficesuite: 'Bevorzugte Office Suite',
                sendcancellationperiod: 'Widerrufsfrist der E-Mail-Sendung (Sekunden)',
                showcollapsed: 'Zeige ausgeblendete',
                showfovouritesafter: 'Zeige nach Navigationsmenü',
                showfovouritesbefore: 'Zeige vor Navigationsmenü',
                space: 'Raum',
                thousandserror: 'Tausenderfeld muss vorhanden sein',
                timezone: 'Zeitzone',
                twelvehourformat: '12-Stunden-Format',
                twentyfourhourformat: '24-Stunden-Format',
                yes: 'Ja'
            },
            searchinallitems: 'In allen Elementen suchen',
            searchtenant: 'Mandant suchen',
            selectdeselettenants: 'Alle aus-/abwählen',
            treenavcontenttitle: '{0} von {1}',
            userpreferences: 'Einstellungen'
        },
        menu: {
            allitems: 'Alle Elemente',
            classes: 'Klassen',
            custompages: 'Benutzerdefinierte Seiten',
            dashboards: 'Dashboards',
            dependencytrees: 'Abhängigkeitsbäume',
            favourites: 'Favoriten',
            favouritesadd: 'Zu Favoriten hinzufügen',
            favouritesremove: 'Aus Favoriten entfernen',
            homepage: 'Startseite',
            processes: 'Vorgänge',
            reports: 'Berichte',
            setasinitialpage: 'Als Startseite festlegen',
            views: 'Ansichten'
        },
        mobile: {
            config: {
                configure: 'Mobilgerät konfigurieren',
                customercode: 'Kundennummer',
                devicename: 'Gerätename',
                regenerate: 'Regenerieren',
                serverurl: 'Server URL'
            }
        },
        notes: {
            edit: 'Notizen bearbeiten'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Nachricht löschen',
            emptymessage: 'Es gibt keine anzeigbaren Benachrichtigungen.',
            label: 'Benachrichtigungen',
            viewall: 'Alle anzeigen'
        },
        notifier: {
            attention: 'Achtung',
            copied: 'Kopiert',
            copiedtotheclipboard: 'Text in die Zwischenablage kopiert',
            error: 'Fehler',
            genericerror: 'Allgemeiner Fehler',
            genericerrormessage: 'Für mehr Informationen überprüfen Sie bitte sie System-Logs',
            genericinfo: 'Allgemeine Info',
            genericwarning: 'Allgemeine Warnmeldung',
            info: 'Information',
            more: 'Mehr Information',
            success: 'Erfolg',
            warning: 'Achtung'
        },
        patches: {
            apply: 'Patches anwenden',
            category: 'Kategorie',
            description: 'Beschreibung',
            name: 'Name',
            patches: 'Patches'
        },
        processes: {
            abortconfirmation: 'Sind Sie sicher, dass Sie diesen Vorgang abbrechen möchten?',
            abortprocess: 'Vorgang abbrechen',
            action: {
                advance: 'Weiter',
                label: 'Aktion'
            },
            activeprocesses: 'Aktive Prozesse',
            allstatuses: 'Alle',
            editactivity: 'Aktivität bearbeiten',
            openactivity: 'Aktivität öffnen',
            resumeprocesses: 'Vorgänge, die nicht angehalten wurden, können nicht fortgesetzt werden',
            startworkflow: 'Starten',
            workflow: 'Prozesse'
        },
        relationGraph: {
            activity: 'Aktivität',
            allLabelsOnGraph: 'alle Etiketten',
            card: 'Karte',
            cardList: 'Kartenliste',
            cardRelations: 'Beziehung',
            choosenaviagationtree: 'Navigationsbaum auswählen',
            class: 'Klasse',
            classList: 'Klassenliste',
            compoundnode: 'Knoten-Verbindung',
            disable: 'Deaktivieren',
            edges: 'Kanten',
            enable: 'Aktivieren',
            labelsOnGraph: 'Tooltip auf Graph',
            level: 'Ebene',
            nodes: 'Knoten',
            openRelationGraph: 'Beziehungsgraph öffnen',
            quantity: 'Anzahl',
            refresh: 'Aktualisieren',
            relation: 'Beziehung',
            relationGraph: 'Beziehungsgraph',
            reopengraph: 'Graph von diesem Knoten erneut öffnen'
        },
        relations: {
            adddetail: 'Detail hinzufügen',
            addrelations: 'Beziehungen hinzufügen',
            attributes: 'Attribute',
            begindate: 'Anfangsdatum',
            cardnotpresentmessage:
                'Die zugehörige Karte ist nicht mehr verfügbar. Um die Bearbeitung fortzusetzen muss eine andere Karte ausgewählt werden.',
            code: 'Code',
            deletedetail: 'Detail löschen',
            deleterelation: 'Beziehung löschen',
            deleterelationconfirm: 'Möchten Sie diese Beziehung wirklich löschen?',
            description: 'Beschreibung',
            editcard: 'Karte bearbeiten',
            editdetail: 'Detail bearbeiten',
            editrelation: 'Beziehung bearbeiten',
            errorexisting:
                'Beziehung „{0}“ existiert bereits. Bearbeiten Sie die existierende oder löschen Sie sie bevor Sie eine neue Beziehung erstellen.',
            extendeddata: 'Erweiterte Daten',
            mditems: 'Elemente',
            missingattributes: 'Fehlende obligatorische Attribute',
            opencard: 'Verbundene Karte öffnen',
            opendetail: 'Zeige Details',
            relationdata: 'Daten der Beziehung',
            type: 'Typ'
        },
        reports: {
            csv: 'CSV',
            download: 'Herunterladen',
            format: 'Format',
            msgs: {
                creationerror: 'Während der Reporterstellung ist ein Fehler aufgetreten.',
                creationstarted:
                    'Erstellung des Report gestartet. Sie werden benachrichtigt sobald er zum Download bereit steht.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Drucken',
            reload: 'Neu laden',
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
                            description: 'Täglich'
                        },
                        monthly: {
                            description: 'Monatlich'
                        },
                        once: {
                            description: 'Einmal'
                        },
                        weekly: {
                            description: 'Wöchentlich'
                        },
                        yearly: {
                            description: 'Jährlich'
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
                        description: 'Nobody'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Thematisierung hinzufügen',
            analysisType: 'Analysetyp',
            attribute: 'Attribut',
            bordersIntervals: 'Falsche Konfiguration für Intervall-Grenzen',
            calculateRules: 'Stilregel erzeugen',
            clearThematism: 'Thematisierung leeren',
            color: 'Farbe',
            defineLegend: 'Definition der Legende',
            defineThematism: 'Thematisierung-Definition',
            deletethematism: 'Möchten Sie diese Thematisierung wirklich löschen?',
            editThematism: 'Regel-Konfiguration bearbeiten',
            function: 'Funktion',
            generate: 'Erzeugen',
            geoAttribute: 'Geographisches Attribut',
            graduated: 'Graduell',
            highlightSelected: 'Ausgewähltes Element markieren',
            intervals: 'Abständen',
            legend: 'Legende',
            name: 'Name',
            newThematism: 'Neue Thematisierung',
            punctual: 'Pünktlich',
            quantity: 'Menge',
            segments: 'Segmente',
            source: 'Quelle',
            table: 'Tabelle',
            thematism: 'Thematisierungen',
            value: 'Wert'
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
                    exitwithoutsave: 'Das Fenster schließen, ohne zu speichern',
                    stayinedit: 'Zurück zur Bearbeitung'
                },
                savebeforeexit:
                    'Es gibt Bearbeitungen, die in den Anlagen nicht gespeichert wurden. Möchten Sie sie vor dem Beenden speichern?',
                saveprocessmessage:
                    'Anlagen können nur in bestehenden Karten gespeichert werden. Wenn man auf Speichern klicken, wird die Prozessinstanz mit den im Formular anwesenden Daten erzeugt.'
            },
            calendar: {
                error: 'Der Parameter {0} ist nicht gesetzt.',
                errorwrong: 'Der Parameter {0} ist nicht korrekt gesetzt.'
            },
            customform: {
                addrow: 'Zeile hinzufügen',
                clonerow: 'Zeile klonen',
                datanotvalid: 'nicht gültige Dateien',
                deleterow: 'Zeile löschen',
                editrow: 'Zeile verändern',
                export: 'Exportieren',
                import: 'Importieren',
                importexport: {
                    expattributes: 'zu exportierenden Daten',
                    file: 'Datei',
                    filename: 'Datei-Name',
                    format: 'Format',
                    importmode: 'Importmodus',
                    keyattributes: 'Schlüsselattribute',
                    missingkeyattr: 'Mindestens ein Schlüsselattribut auswählen',
                    modeadd: 'Hinzufügen',
                    modemerge: 'Zusammenführen',
                    modereplace: 'ersetzen',
                    separator: 'Trennzeichen'
                },
                refresh: 'Standardeinstellungen wiederherstellen'
            },
            linkcards: {
                checkedonly: 'Nur ausgewählte',
                editcard: 'Karte bearbeiten',
                opencard: 'Karte öffnen',
                refreshselection: 'Default Auswahl anwenden',
                removefilterconfirm: 'Durch diese Aktion wird der aktuelle Filter entfernt. Möchten Sie fortfahren?',
                selectioncleared: 'Auswahl zurückgesetzt',
                togglefilterdisabled: 'Filter des Rasters deaktivieren',
                togglefilterenabled: 'Filter Raster aktivieren'
            },
            notimplemented: 'Widget <strong>{0}</strong> nicht implementiert!',
            notsupportedinline:
                'Inline-Mode wird für diese Widgetsart nicht unterstützt. Bitte die Widget-Konfiguration korrigieren',
            required: 'Dieses Widget ist erforderlich'
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
