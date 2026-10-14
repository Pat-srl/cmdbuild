(function () {
    Ext.define('CMDBuildUI.locales.el_GR.Locales', {
        requires: ['CMDBuildUI.locales.el_GR.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'el_GR',
        administration: CMDBuildUI.locales.el_GR.LocalesAdministration.administration,
        ai: {
            clearconversation: 'Καθαρισμός συζήτησης',
            clearconversationmessage: 'Είσαι σίγουρος ότι θέλεις να διαγράψεις την τρέχουσα συνομιλία;',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: 'Συγγνώμη, υπήρχαν προβλήματα στην απάντηση',
            headertooltip: 'CMDBuild chat bot',
            introtextbody: 'Για να ξεκινήσετε τη συνομιλία, περιγράψτε το αίτημά σας στο αντίστοιχο πεδίο',
            introtexttitle: 'Πώς μπορώ να σας βοηθήσω;',
            loading: 'Φόρτωση...',
            newchat: 'Νέα συνομιλία',
            newchattooltip: 'Ξεκινήστε μια νέα συζήτηση',
            questionemptytext: 'Γράψτε εδώ την ερώτησή σας...',
            resources: 'Πηγές',
            viewrelatedcard: 'Προβολή σχετικής κάρτας',
            viewresource: 'Προβολή πόρου'
        },
        arialabels: {
            buttonplaceholder: 'Κουμπί',
            iframeplaceholder: 'Επεξεργαστής κειμένου',
            languageselector: 'Επιλογή γλώσσας',
            resizepanel: {
                horizontally: 'Αλλαγή μεγέθους των πλαισίων οριζόντια',
                vertically: 'Αλλαγή μεγέθους των πλαισίων κάθετα'
            },
            usermenu: 'Μενού χρήστη'
        },
        attachments: {
            add: 'Προσθήκη συνημμένου',
            attachmenthistory: 'Ιστορικό συνημμένων',
            author: 'Συγγραφέας',
            authordescription: 'Περιγραφή συγγραφέα',
            browse: 'Περιήγηση &hellip;',
            category: 'Κατηγορία',
            code: 'Κωδικός',
            creationdate: 'Ημερομηνία δημιουργίας',
            deleteattachment: 'Διαγραφή συνημμένου',
            deleteattachment_confirmation: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτό το συνημμένο;',
            description: 'Περιγραφή',
            download: 'Κατέβασμα',
            dropfiles: 'Απόθεση αρχείων εδώ',
            editattachment: 'Τροποποίηση του συνημμένου',
            errordms: 'Το DMS είναι απενεργοποιημένο και δεν μπορεί να αποθηκεύσει αρχεία',
            file: 'Αρχείο',
            filealreadyinlist: 'Το αρχείο {0} είναι ήδη στη λίστα.',
            filename: 'Όνομα αρχείου',
            fileview: 'Προβολή συνημμένου',
            invalidfiles: 'Κατάργηση μη έγκυρων αρχείων',
            majorversion: 'Κύρια έκδοση',
            maxsize: 'Το μέγιστο επιτρεπτό μέγεθος αρχείου είναι {0} MB',
            metadata: 'Μεταδεδομένα',
            modificationdate: 'Ημερομηνία τροποποίησης',
            new: 'Νέο συνημμένο',
            nocategory: 'Αταξινόμητες',
            preview: 'Προεπισκόπηση',
            removefile: 'Κατάργηση αρχείου',
            statuses: {
                empty: 'Κενό αρχείο',
                error: 'Σφάλμα',
                extensionNotAllowed: 'Η επέκταση αρχείου δεν επιτρέπεται',
                loaded: 'Φόρτωσε',
                ready: 'Έτοιμο',
                toolarge: 'Πολύ μεγάλο'
            },
            successupload: '{0} συνημμένα που έχουν μεταφορτωθεί.',
            uploadfile: 'Ανέβασμα αρχείου...',
            version: 'Έκδοση',
            viewhistory: 'Προβολή ιστορικού συνημμένων',
            viewmetadata: 'Προβολή μεταδεδομένων συνημμένων',
            warningmessages: {
                atleast:
                    '{0}: έχουν φορτωθεί {1} συνημμένα τύπου "{2}". Η κατηγορία αυτή αναμένει τουλάχιστον {3} συνημμένα',
                exactlynumber: '{0}: έχουν φορτωθεί {1} συνημμένα τύπου "{2}". Αυτή η κατηγορία αναμένει {3} συνημμένα',
                maxnumber:
                    '{0}: έχουν φορτωθεί {1} συνημμένα τύπου "{2}". Αυτή η κατηγορία αναμένει το πολύ {3} συνημμένα'
            },
            wrongfileextension: '{0} επέκταση αρχείου δεν επιτρέπεται'
        },
        bim: {
            bimViewer: 'Bim Viewer',
            card: {
                label: 'Κάρτα'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Οριακό μέγεθος κουτιού κατά μήκος του άξονα X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Οριακό μέγεθος κουτιού κατά μήκος του άξονα Υ',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Οριακό μέγεθος κουτιού κατά μήκος του άξονα Ζ',
                calculated: 'Υπολογισμένο',
                description: 'Περιγραφή',
                guid: 'GUID',
                label: 'Ιδιότητες',
                name: 'Όνομα',
                type: 'Τύπος',
                uuid: 'UUID'
            },
            layers: {
                label: 'Επίπεδα',
                menu: {
                    hideAll: 'Απόκρυψη όλων',
                    hideAllLayers: 'Απόκρυψη όλων των επιπέδων',
                    hideAllXeokit: 'Απόκρυψη όλων {0}',
                    showAll: 'Εμφάνιση όλων',
                    showAllLayers: 'Εμφάνιση όλων των επιπέδων',
                    showAllXeokit: 'Εμφάνιση όλων {0}'
                },
                name: 'Όνομα',
                quantity: 'Ποσότητα',
                visibility: 'Ορατότητα'
            },
            menu: {
                camera: 'Φωτογραφική μηχανή',
                disableslice: 'Απενεργοποίηση αντικειμένου τομής',
                enableslice: 'Ενεργοποίηση αντικειμένου τομής',
                frontView: 'Εμπρόσθια όψη',
                mod: 'Έλεγχοι προβολής',
                ortho: 'Μετάβαση σε ορθογώνια προβολή',
                orthographic: 'Ορθογραφική κάμερα',
                pan: 'Μετακινηθείτε',
                panXeokit: 'Στοιχεία ελέγχου προβολής: κύλιση (Shift)',
                perspe: 'Μετάβαση σε προοπτική προβολή',
                perspective: 'Προοπτική κάμερα',
                resetSlice: 'Επαναφορά κομματιού',
                resetView: 'Επαναφορά προβολής',
                rotate: 'Περιστροφή',
                rotateXeokit: 'Στοιχεία ελέγχου προβολής: περιστροφή',
                save: 'Αποθήκευση προβολής',
                shortcuts: {
                    down: 'Περιστροφή προς τα κάτω',
                    left: 'Περιστροφή προς τα αριστερά',
                    right: 'Περιστροφή προς τα δεξιά',
                    title: 'Συντομεύσεις πληκτρολογίου',
                    up: 'Περιστροφή προς τα πάνω',
                    zoomin: 'Μεγέθυνση',
                    zoomout: 'Σμίκρυνση'
                },
                sideView: 'Πλευρική όψη',
                threeD: 'Μετάβαση σε προβολή 3D',
                topView: 'Κάτοψη',
                twoD: 'Μετάβαση σε προβολή 2D'
            },
            navCube: {
                back: 'Πίσω',
                bottom: 'Κάτω μέρος',
                front: 'Εμπρός',
                left: 'Αριστερά',
                right: 'Δεξιά',
                top: 'Επάνω'
            },
            showBimCard: 'Ανοίξτε το πρόγραμμα προβολής 3D',
            tree: {
                arrowTooltip: 'Επιλέξτε στοιχείο',
                label: 'Δέντρο',
                multipleElements: 'Επιλέξτε στοιχεία',
                open_card: 'Άνοιγμα σχετικής κάρτας',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Ματαίωση επιλεγμένων στοιχείων',
            addfields: 'Προσθήκη πεδίων',
            alertdownloadattachments: 'Δεν θα γίνει λήψη ορισμένων συνημμένων',
            alertnoattachmentsdownload: 'Δεν θα γίνει λήψη συνημμένων',
            cancelselection: 'Ακύρωση επιλογής',
            confirmabort: 'Ματαιώνετε {0}  διεργασίες. Είστε βέβαιοι ότι θέλετε να συνεχίσετε;',
            confirmdelete: 'Διαγράφετε {0} κάρτες. Είστε βέβαιοι ότι θέλετε να συνεχίσετε;',
            confirmdeleteattachements: 'Διαγράφετε {0} συνημμένα. Είστε βέβαιοι ότι θέλετε να συνεχίσετε;',
            confirmedit: 'Τροποποιείτε {0} για {1} κάρτες. Είστε βέβαιοι ότι θέλετε να συνεχίσετε;',
            delete: 'Διαγραφή επιλεγμένων στοιχείων',
            download: 'Λήψη επιλεγμένων συνημμένων',
            downloadall: 'Λήψη όλων των συνημμένων',
            edit: 'Επεξεργασία επιλεγμένων στοιχείων',
            missingfields: 'Τα επόμενα πεδία πρέπει να προστεθούν για τη σωστή επικύρωση της φόρμας: {0}',
            noattributeselected: 'Επιλέξτε τουλάχιστον ένα χαρακτηριστικό.',
            selectall: 'Επιλέξτε όλα τα στοιχεία'
        },
        calendar: {
            active_expired: 'Ενεργή/Ληγμένη',
            add: 'Προσθήκη χρονοδιαγράμματος',
            advancenotification: 'Γνωστοποίηση ημέρες πριν',
            allcategories: 'Όλες οι κατηγορίες',
            alldates: 'Όλες οι ημερομηνίες',
            associatedcard: 'Συσχετισμένη κάρτα',
            calculated: 'Υπολογίστηκε',
            calendar: 'Ημερολόγιο',
            cancel: 'Σήμανση ως ακυρωμένου',
            category: 'Κατηγορία',
            class: 'Κατηγορία',
            cm_confirmcancel: 'Είστε βέβαιοι ότι θέλετε να επισημάνετε ως ακυρωμένα τα επιλεγμένα χρονοδιαγράμματα;',
            cm_confirmcomplete:
                'Είστε βέβαιοι ότι θέλετε να επισημάνετε ως ολοκληρωμένα τα επιλεγμένα χρονοδιαγράμματα;',
            cm_markcancelled: 'Σήμανση ως ακυρωμένων των επιλεγμένων χρονοδιαγραμμάτων',
            cm_markcomplete: 'Σήμανση ως ολοκληρωμένων των επιλεγμένων χρονοδιαγραμμάτων',
            complete: 'Ολοκληρώθηκε',
            completed: 'Ολοκληρωμένο',
            date: 'Ημερομηνία',
            days: 'Ημέρες',
            delaybeforedeadline: 'Καθυστέρηση πριν από την προθεσμία',
            delaybeforedeadlinevalue: 'Καθυστέρηση πριν από την τιμή προθεσμίας',
            description: 'Περιγραφή',
            editevent: 'Επεξεργασία χρονοδιαγράμματος',
            enddate: 'Ημερομηνία λήξης',
            endtype: 'Τύπος τέλους',
            event: 'Χρονοδιάγραμμα',
            execapplyfilter:
                'Δεν είναι δυνατή η εκτέλεση του applyAdvanceFilter γιατί υπάρχουν περισσότερα του ενός καταστημάτων',
            execremovefilter:
                'Δεν είναι δυνατή η εκτέλεση του removeAdvanceFilter γιατί υπάρχουν περισσότερα του ενός  καταστημάτων',
            executiondate: 'Ημερομηνία εκτέλεσης',
            frequency: 'Συχνότητα',
            frequencymultiplier: 'Πολλαπλασιαστής συχνότητας',
            grid: 'Πλέγμα',
            leftdays: 'Μέρες που απομένουν',
            londdescription: 'Πλήρης περιγραφή',
            manual: 'Εγχειρίδιο',
            maxactiveevents: 'Μέγιστα ανοικτά χρονοδιαγράμματα',
            messagebodydelete: 'Θα θέλατε να διαγράψετε τον κανόνα χρονοδιαγράμματος;',
            messagebodyplural: 'Υπάρχουν {0} κανόνες χρονοδιαγραμμάτων',
            messagebodyrecalculate: 'Θα θέλατε να υπολογίσετε ξανά τον κανόνα χρονοδιαγραμμάτων με τη νέα ημερομηνία;',
            messagebodysingular: 'Υπάρχει {0} κανόνας/ες χρονοδιαγραμμάτων',
            messagetitle: 'Επαναϋπολογισμός χρονοδιαγράμματος',
            missingdays: 'Ημέρες που λείπουν',
            next30days: 'Επόμενες 30 ημέρες',
            next7days: 'Επόμενες 7 ημέρες',
            notificationtemplate: 'Πρότυπο που χρησιμοποιείται για την ειδοποίηση',
            notificationtext: 'Κείμενο ειδοποίησης',
            occurencies: 'Αριθμός εμφανίσεων',
            operation: 'Λειτουργία',
            partecipantgroup: 'Ομάδα συμμετεχόντων',
            partecipantuser: 'Συμμετέχων χρήστης',
            priority: 'Προτεραιότητα',
            recalculate: 'Επαναϋπολογισμός',
            referent: 'Αναφερόμενο',
            scheduler: 'Χρονοδιάγραμμα εργασιών',
            sequencepaneltitle: 'Δημιουργία χρονοδιαγραμμάτων',
            startdate: 'Ημερομηνία έναρξης',
            status: 'Κατάσταση',
            today: 'Σήμερα',
            type: 'Τύπος',
            viewevent: 'Προβολή χρονοδιαγράμματος',
            waiteventgeneration: 'Δημιουργία συμβάντων. Παρακαλώ περιμένετε μέχρι να ολοκληρωθούν.',
            widgetcriterion: 'Κριτήριο υπολογισμού',
            widgetemails: 'Μηνύματα ηλεκτρονικού ταχυδρομείου',
            widgetgotocard: 'Μετάβαση στην κάρτα πηγής',
            widgetsourcecard: 'Κάρτα προέλευσης'
        },
        chat: {
            title: 'Συνομιλία'
        },
        classes: {
            cards: {
                addcard: 'Προσθήκη κάρτας',
                clone: 'Κλώνος',
                clonewithrelations: 'Κλωνοποιήστε την κάρτα και τις σχέσεις',
                deletebeaware: 'Πρέπει να γνωρίζετε ότι:',
                deleteblocked: 'Δεν είναι δυνατόν να προχωρήσουμε με τη διαγραφή',
                deletecard: 'Διαγραφή κάρτας',
                deleteconfirmation: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτήν την κάρτα;',
                deleterelatedcards: 'επίσης {0} σχετικές κάρτες θα διαγραφούν',
                deleterelations: 'συσχετίσεις με {0} κάρτες θα διαγραφούν',
                label: 'Κάρτες',
                modifycard: 'Τροποποίηση κάρτας',
                opencard: 'Άνοιγμα κάρτας',
                print: 'Εκτύπωση κάρτας'
            },
            simple: 'Απλό',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Προσθήκη',
                apply: 'Εφαρμογή',
                cancel: 'Ακύρωση',
                clear: 'Εκκαθάριση',
                close: 'Κλείσιμο',
                copy: 'Αντιγραφή',
                copyerrormessage: 'Αντιγράψτε το μήνυμα σφάλματος στο πρόχειρο',
                delete: 'Διαγραφή',
                edit: 'Επεξεργασία',
                execute: 'Εκτέλεση',
                forward: 'Προώθηση',
                help: 'Βοήθεια',
                hide: 'Απόκρυψη',
                load: 'Φόρτωση',
                open: 'Ανοικτό',
                previous: 'Προηγούμενο',
                refresh: 'Ανανέωση δεδομένων',
                remove: 'Αφαίρεση',
                save: 'Αποθήκευση',
                saveandapply: 'Αποθήκευση και εφαρμογή',
                saveandclose: 'Αποθήκευση και κλείσιμο',
                search: 'Αναζήτηση',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Αναζήτηση…'
            },
            and: 'και',
            attributes: {
                boolfalse: 'ΨΕΥΔΕΣ',
                booltrue: 'ΑΛΗΘΕΣ',
                link: {
                    errlabel: 'Απαιτείται ετικέτα',
                    errurl: 'Το Url απαιτείται όταν η ετικέτα δεν είναι κενή',
                    label: 'Ετικέτα',
                    url: 'Url'
                },
                nogroup: 'Τα βασικά δεδομένα'
            },
            dates: {
                date: 'd / m / Y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Διαγραφή HTML',
                expand: 'Ανάπτυξη προγράμματος επεξεργασίας',
                reduce: 'Μείωση προγράμματος επεξεργασίας',
                signature: 'Προσθήκη υπογραφής',
                unlink: 'Αποσύνδεση',
                unlinkmessage: 'Μετατροπή επιλεγμένου υπερσυνδέσμου σε κείμενο'
            },
            grid: {
                disablemultiselection: 'Απενεργοποιήστε την πολλαπλή επιλογή',
                enamblemultiselection: 'Ενεργοποιήστε την επιλογή πολλαπλών επιλογών',
                export: 'Εξαγωγή δεδομένων',
                filterremoved: 'Το τρέχον φίλτρο έχει αφαιρεθεί',
                import: 'Εισαγωγή δεδομένων',
                itemnotfound: 'Δεν βρέθηκε το αντικείμενο',
                lastUpdate: '<em>Last update</em>',
                list: 'Λίστα',
                opencontextualmenu: 'Ανοίξτε το μενού πλαισίου',
                print: 'Εκτύπωση',
                printcsv: 'Εκτύπωση ως CSV',
                printmap: 'Εκτύπωση χάρτη',
                printodt: 'Εκτύπωση ως ODT',
                printpdf: 'Εκτύπωση ως PDF',
                row: 'Στοιχείο',
                rows: 'Αντικείμενα',
                subtype: 'Υποτύπος',
                thematism: 'Θεματισμός'
            },
            tabs: {
                activity: 'Δραστηριότητα',
                attachment: 'Συνημμένο',
                attachments: 'Συνημμένα αρχεία',
                card: 'Κάρτα',
                clonerelationmode: 'Λειτουργία συσχετίσεων κλώνου',
                details: 'Λεπτομέρειες',
                emails: 'Emails',
                history: 'Ιστορικό',
                notes: 'Σημειώσεις',
                relations: 'Συσχετίσεις',
                schedules: 'Χρονοδιαγράμματα',
                sources: 'Πηγές'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: 'Εμφάνιση όλων',
                showingnitems: 'Εμφάνιση: {0} στοιχεία'
            },
            pareto: {
                cumulative: 'Συνολικό',
                cumulativeperc: 'Σωρευτικό ποσοστό %'
            },
            tools: {
                download: 'Μεταφόρτωση διαγράμματος',
                downloaddata: 'Λήψη δεδομένων γραφήματος',
                gridhide: 'Απόκρυψη πλέγματος δεδομένων',
                gridshow: 'Εμφάνιση πλέγματος δεδομένων',
                openinpopup: 'Άνοιγμα σε αναδυόμενο παράθυρο',
                reload: 'Επαναφόρτωση',
                showall: 'Εμφάνιση όλων'
            }
        },
        dependencytree: {
            cardinfo: 'Κάρτα',
            filteredid: 'Η τρέχουσα κάρτα δεν ανήκει σε αυτό το δέντρο',
            gerarchicview: 'Ιεραρχική προβολή',
            name: 'Δέντρο εξαρτήσεων',
            nocardselected: 'Δεν έχει επιλεγεί κάρτα',
            opendependencytree: 'Άνοιγμα δέντρου εξαρτήσεων',
            printjpeg: 'Εκτύπωση ως JPEG',
            printpdf: 'Εκτύπωση ως PDF',
            printpng: 'Εκτύπωση ως PNG',
            printsvg: 'Εκτύπωση ως SVG',
            reopengraph: 'Επαναφορά του γραφήματος με νέα ρίζα',
            selectcard: 'Επιλέξτε τον αρχικό κόμβο',
            wrongid: 'Το ID που εισαγάγατε είναι λανθασμένο'
        },
        emails: {
            abort: 'Ακύρωση',
            addattachmentsfromdocumentarchive: 'Προσθήκη συνημμένων από την αρχειοθήκη εγγράφων',
            alredyexistfile: 'Υπάρχει ήδη ένα αρχείο με αυτό το όνομα',
            archivingdate: 'Ημερομηνία αρχειοθέτησης',
            attachfile: 'Επισύναψη αρχείου',
            attachmentsmessage:
                'Το πρότυπο απαιτεί να επισυνάψετε συνημμένα σε μορφή κάρτας. Δεν θα μπορείτε να το χρησιμοποιήσετε μέχρι να αποθηκεύσετε τη διαδικασία',
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Σύνθεση e-mail',
            composefromtemplate: 'Σύνθεση από το πρότυπο',
            delay: 'Καθυστέρηση',
            delays: {
                day1: 'Σε 1 ημέρα',
                days2: 'Σε 2 ημέρες',
                days4: 'Σε 4 ημέρες',
                hour1: 'Σε 1 ώρα',
                hours2: 'Σε 2 ώρες',
                hours4: 'Σε 4 ώρες',
                month1: 'Σε 1 μήνα',
                negativeday1: 'Πριν 1 ημέρα',
                negativedays2: 'Πριν 2 ημέρες',
                negativedays4: 'Πριν 4 ημέρες',
                negativehour1: 'Πριν 1 ώρα',
                negativehours2: 'Πριν 2 ώρες',
                negativehours4: 'Πριν 4 ώρες',
                negativemonth1: 'Πριν 1 μήνα',
                negativeweek1: 'Πριν 1 εβδομάδα',
                negativeweeks2: 'Πριν 2 εβδομάδες',
                none: 'Κανένα',
                week1: 'Σε 1 εβδομάδα',
                weeks2: 'Σε 2 εβδομάδες'
            },
            dmspaneltitle: 'Επιλογή των συνημμένων από τη Βάση Δεδομένων',
            edit: 'Επεξεργασία',
            fileattributesmessage:
                'Υπάρχει εξάρτηση από ένα χαρακτηριστικό τύπου αρχείου. Δεν θα μπορείτε να το χρησιμοποιήσετε μέχρι να αποθηκεύσετε τη διαδικασία',
            from: 'Από',
            gridrefresh: 'Ανανέωση πλέγματος',
            groupbystatus: 'Ομαδοποιημένα κατά κατάσταση',
            keepsynchronization: 'Διατήρηση συγχρονισμού',
            maxsize: 'Το μέγιστο επιτρεπτό συνολικό μέγεθος συννημένων είναι {0} MB',
            message: 'Μήνυμα',
            regenerateallemails: 'Ξαναδημιουργία όλων των e-mail',
            regenerateemail: 'Ξαναδημιουργία e-mail',
            reloadconfirmationmessage:
                'Τα μηνύματα ηλεκτρονικού ταχυδρομείου σε πρόχειρη κατάσταση θα διαγραφούν. Θέλετε να συνεχίσετε;',
            remove: 'Αφαίρεση',
            remove_confirmation: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτό το email;',
            reply: 'Απάντηση',
            replyprefix: 'On {0}',
            searchattachment: 'Αναζήτηση Συννημένου',
            searchcard: 'Αναζλητηση κάρτας',
            selectaclass: 'Επιλέξτε μια κατηγορία',
            sendemail: 'Αποστολή e-mail',
            sendingemail: 'Αποστολή e-mail',
            signature: 'Υπογραφή',
            size: 'Μέγεθος',
            status: 'Κατάσταση',
            statuses: {
                draft: 'Πρόχειρο',
                error: 'Σφάλμα',
                new: 'Νέο',
                outgoing: 'Εξόδου',
                received: 'Ληφθέντα',
                sent: 'Απεσταλμένα',
                skipped: 'Παράλειψη'
            },
            subject: 'Θέμα',
            templateattachments:
                'Αυτό το μήνυμα ηλεκτρονικού ταχυδρομείου περιέχει συνημμένα που ανεβαίνουν αυτόματα από το πρότυπο',
            to: 'Προς',
            view: 'Προβολή'
        },
        errors: {
            actionnotallowed: 'Μη επιτρεπόμενη ενέργεια',
            autherror: 'Λάθος όνομα χρήστη ή κωδικός πρόσβασης',
            classnotfound: 'Η κατηγορία {0} δεν βρέθηκε',
            fieldrequired: 'Αυτό το πεδίο απαιτείται',
            invalidfilter: 'Μη έγκυρο φίλτρο',
            nodatatoexport: 'Το διάγραμμα δεν περιέχει δεδομένα προς εξαγωγή',
            notfound: 'Δεν βρέθηκε το αντικείμενο',
            value3chars: 'Η τιμή πρέπει να έχει μήκος τουλάχιστον 3 χαρακτήρες.'
        },
        filters: {
            actions: 'Ενέργειες',
            addfilter: 'Προσθήκη φίλτρου',
            any: 'Τουλάχιστον ένα',
            attachments: 'Συνημμένα',
            attachmentssearchtext: 'Εύρεση κειμένου σε συνημμένα',
            attribute: 'Επιλέξτε χαρακτηριστικό',
            attributes: 'Χαρακτηριστικά',
            clearfilter: 'Καθαρισμός φίλτρου',
            clone: 'Κλώνος',
            copyof: 'Αντίγραφο του',
            currentgroup: 'Τρέχον group',
            currentuser: 'Τρέχον χρήστης',
            defaultset: 'Θέστε ως προεπιλογή',
            defaultunset: 'Ακύρωση από προεπιλογή',
            description: 'Περιγραφή',
            domain: 'Πεδίο ορισμού',
            errorname: 'Το όνομα φίλτρου χρησιμοποιείται ήδη',
            filterdata: 'Φιλτράρισμα δεδομένων',
            fromfilter: 'Από φίλτρο',
            fromselection: 'Από επιλογή',
            group: 'Ομάδα',
            ignore: 'Αγνοήστε',
            migrate: 'Μεταναστεύει',
            name: 'Όνομα',
            newfilter: 'Νέο φίλτρο',
            noone: 'Κανένα',
            operator: 'Χειριστής',
            operators: {
                and: 'Και',
                beginswith: 'Ξεκινήστε με',
                between: 'Ανάμεσα',
                contained: 'Περιέχονται',
                containedorequal: 'Περιλαμβανόμενο ή ίσο',
                contains: 'Περιλαμβάνει',
                containsorequal: 'Περιλαμβάνει ή είναι ίσο',
                descriptionbegin: 'Η περιγραφή ξεκινάει με',
                descriptioncontains: 'Η περιγραφή περιέχει',
                descriptionends: 'Η περιγραφή τελειώνει με',
                descriptionnotbegin: 'Η περιγραφή δεν ξεκινάει με',
                descriptionnotcontain: 'Η περιγραφή δεν περιέχει',
                descriptionnotends: 'Η περιγραφή δεν τελειώνει με',
                different: 'Διαφορετικό',
                doesnotbeginwith: 'Δεν ξεκινάει με',
                doesnotcontain: 'Δεν περιέχει',
                doesnotendwith: 'Δεν τελειώνει με',
                endswith: 'Τελειώνει με',
                equals: 'Ισούται',
                greaterthan: 'Μεγαλότερο από',
                isnotnull: 'Δεν είναι κενό',
                isnull: 'Είναι κενό',
                lessthan: 'Λιγότερο από',
                or: 'ή'
            },
            relations: 'Συσχετίσεις',
            removeattribute: 'Αφαίρεση χαρακτηριστικού',
            removeblock: 'Αφαίρεση μπλοκ',
            type: 'Τύπος',
            typeinput: 'Παράμετρος εισαγωγής',
            typeorchoosetheattributename: 'Πληκτρολογήστε ή επιλέξτε το όνομα του χαρακτηριστικού',
            user: 'Χρήστης',
            value: 'Τιμή'
        },
        gis: {
            card: 'Κάρτα',
            cardsMenu: 'Μενού Καρτών',
            code: 'Κωδικός',
            contextmenu: {
                coordinates: 'Λήψη συντεταγμένων',
                coordinatescopiedmessage: 'Συντεταγμένες αντιγράφηκαν στο πρόχειρο'
            },
            description: 'Περιγραφή',
            extension: {
                errorCall: 'Σφάλμα',
                noResults: 'Κανένα αποτέλεσμα'
            },
            externalServices: 'Εξωτερικές υπηρεσίες',
            geographicalAttributes: 'Γεωγραφικά χαρακτηριστικά',
            geoserverLayers: 'Επίπεδα Geoserver',
            labels: {
                all: 'Όλα',
                alltitle: 'Εμφάνιση όλων των ετικετών',
                alltitleclass: 'Εμφάνιση όλων των ετικετών της κατηγορίας',
                clear: 'Εκκαθάριση αναζήτησης',
                hidden: 'Κρυμμένος',
                hiddentitle: 'Απόκρυψη ετικετών',
                hideinfowindow: 'Απόκρυψη παραθύρου πληροφοριών',
                label: 'Ετικέτες',
                search: 'Αναζήτηση στον χάρτη',
                selected: 'Επιλεγμένο',
                selectedtitle: 'Εμφάνιση ετικέτας για επιλεγμένο στοιχείο',
                showinfowindow: 'Εμφάνιση παραθύρου πληροφοριών'
            },
            layers: 'Επίπεδα',
            layersTab: {
                clearPreferences: 'Εκκαθάριση προτιμήσεων επιπέδων',
                preferencesCleared: 'Οι προτιμήσεις επιπέδων διαγράφηκαν',
                preferencesSaved: 'Οι προτιμήσεις επιπέδων αποθηκεύτηκαν',
                savePreferences: 'Αποθήκευση προτιμήσεων επιπέδων',
                updatePreferences: 'Ενημέρωση προτιμήσεων επιπέδων'
            },
            list: 'Λίστα',
            longpresstitle: 'Γεωστοιχεία στην περιοχή',
            map: 'Χάρτης',
            mapServices: 'Υπηρεσίες Χάρτη',
            menu: {
                font: 'Μέγεθος γραμματοσειράς',
                options: 'Επιλογές'
            },
            position: 'Θέση',
            root: 'Αρχή',
            sourceMap: {
                default: 'Χρησιμοποιήστε την προεπιλεγμένη προβολή',
                satellite: 'Χρησιμοποιήστε την δορυφορική προβολή'
            },
            tree: 'Δέντρο',
            type: 'Τύπος',
            view: 'Προβολή',
            zoom: 'Μεγέθυνση'
        },
        history: {
            activityname: 'Όνομα δραστηριότητας',
            activityperformer: 'Εκτελεστής δραστηριότητας',
            attributes: 'Χαρακτηριστικά  ({0})',
            begindate: 'Ημερομηνία έναρξης',
            enddate: 'Ημερομηνία λήξης',
            fieldname: 'Όνομα πεδίου',
            fulldata: 'Πλήρη στοιχεία',
            onlychanges: 'Μονο αλλαγές',
            otherrelations: 'Άλλες συσχετίσεις',
            processstatus: 'Κατάσταση',
            referencerelation: 'Συσχετίσεις αναφοράς',
            relation: 'Συσχέτιση',
            system: 'Σύστημα',
            type: 'Τύπος',
            user: 'Χρήστης',
            userdescription: 'Περιγραφή χρήστη',
            value: 'Τιμή',
            viewmode: 'Λειτουργία προβολής'
        },
        homepage: {
            search: {
                airequestproblem:
                    '<em>A problem occurred while processing the request. Please try again later or contact the administrator.</em>',
                aisearcherror: '<em>Unable to complete the search</em>',
                goodafternoon: 'Καλησπέρα',
                goodevening: 'Καλό βράδι',
                goodmorning: 'Καλημέρα',
                goodnight: 'Καληνύχτα',
                noresults: '<em>No results found</em>',
                noresultshint: '<em>Try different keywords or check the spelling</em>',
                resultsfor: '<em>Results for: {0}</em>'
            }
        },
        iconscategories: {
            accessibility: 'Προσβασιμότητα',
            animals: 'Ζώα',
            arrows: 'Βέλη',
            audiovideo: 'Βίντεο με ήχο',
            automotive: 'Αυτοκίνητο',
            autumn: 'Φθινόπωρο',
            beverage: 'Αναψυκτικό',
            buildings: 'Κτίρια',
            business: 'Επιχειρήσεις',
            charity: 'Φιλανθρωπία',
            chat: 'Συνομιλία',
            chess: 'Σκάκι',
            childhood: 'Παιδική ηλικία',
            code: 'Κωδικός',
            communication: 'Επικοινωνία',
            computers: 'Υπολογιστές',
            construction: 'Κατασκευή',
            currency: 'Νόμισμα',
            datetime: 'Ημερομηνία και ώρα',
            design: 'Σχέδιο',
            editors: 'Συντάκτες',
            education: 'Εκπαίδευση',
            emoji: 'Emoji',
            energy: 'Ενέργεια',
            files: 'Αρχεία',
            finance: 'Οικονομικά',
            food: 'Τροφή',
            fruitvegetable: 'Φρούτο λαχανικό',
            games: 'Παιχνίδια',
            gamingtabletop: 'Επιτραπέζιο παιχνίδι',
            gender: 'Γένος',
            general: 'Γενικά',
            halloween: 'Halloween',
            hands: 'Χέρια',
            hotel: 'Ξενοδοχείο',
            household: 'Νοικοκυριό',
            images: 'Εικόνες',
            interfaces: 'Διεπαφές',
            logistics: 'Επιμελητεία',
            maps: 'Χάρτες',
            maritime: 'Θαλάσσιος',
            marketing: 'Εμπορία',
            mathematics: 'Μαθηματικά',
            medical: 'Ιατρικά',
            moving: 'Κίνηση',
            music: 'Μουσική',
            objects: 'Δέντρο',
            paymentsshopping: 'Αγορές πληρωμών',
            political: 'Πολιτικός',
            religion: 'Θρησκεία',
            sciencefiction: 'Επιστημονική φαντασία',
            security: 'Ασφάλεια',
            shapes: 'Σχήματα',
            shopping: 'Αγορές',
            social: 'Κοινωνικά',
            spinners: 'Περιστρεφόμενα',
            sports: 'Αθλητισμός',
            spring: 'Άνοιξη',
            status: 'Κατάσταση',
            toggle: 'Αλλάζω θέση',
            travel: 'Ταξίδι',
            userspeople: 'Χρήστες',
            vehicles: 'Οχήματα',
            weather: 'Καιρός',
            winter: 'Χειμώνας'
        },
        importexport: {
            database: {
                uri: 'URI βάσης δεδομένων',
                user: 'Χρήστης βάσης δεδομένων'
            },
            downloadreport: 'Λήψη αναφοράς',
            emailfailure: 'Παρουσιάστηκε σφάλμα κατά την αποστολή email',
            emailmessage: 'Επισυνάπτεται αναφορά εισαγωγής του αρχείου "{0}" στην ημερομηνία {1}',
            emailsubject: 'Εισαγωγή αναφοράς δεδομένων',
            emailsuccess: 'Το μήνυμα ηλεκτρονικού ταχυδρομείου έχει σταλεί με επιτυχία!',
            export: 'Εξαγωγή',
            exportalldata: 'Όλα τα δεδομένα',
            exportfiltereddata: 'Μόνο δεδομένα που ταιριάζουν με το φίλτρο πλέγματος',
            gis: {
                importconfiguration: 'Εισαγωγή διαμόρφωσης',
                shapeimportdisabled: 'Η εισαγωγή σχημάτων δεν είναι ενεργοποιημένη για αυτό το πρότυπο',
                shapeimportenabled: 'Η εισαγωγή σχημάτων είναι ενεργοποιημένη για αυτό το πρότυπο'
            },
            ifc: {
                card: 'Κάρτα',
                project: 'Έργο',
                sourcetype: 'Εισαγωγή από'
            },
            import: 'Εισαγωγή',
            importresponse: 'Απόκριση εισαγωγής',
            response: {
                created: 'Δημιουργημένα στοιχεία',
                deleted: 'Διεγραμμένα αντικείμενα',
                errors: 'Λάθη',
                linenumber: 'Αριθμός γραμμής',
                message: 'Μήνυμα',
                modified: 'Τροποποιημένα στοιχεία',
                notprocessed: 'Μη επεξεργασμένα στοιχεία',
                processed: 'Επεξεργασμένες γραμμές',
                recordnumber: 'Αριθμός εγγραφής',
                unchanged: 'Αμετάβλητα στοιχεία'
            },
            sendreport: 'Αποστολή αναφοράς',
            template: 'Πρότυπο',
            templatedefinition: 'Ορισμός προτύπου'
        },
        joinviews: {
            active: 'Ενεργό',
            addview: 'Προσθήκη προβολής',
            alias: 'Ψευδώνυμο',
            attribute: 'Χαρακτηριστικό',
            attributecustomization: 'Προσαμογές χαρακτηριστικών',
            attributes: 'Χαρακτηριστικά',
            attributeschoice: 'Επιλογή χαρακτηριστικών',
            attributesof: 'Χαρακτηριστικά του: {0}',
            createview: 'Δημιουργία προβολής',
            datasorting: 'Διαλογή δεδομένων',
            delete: 'Διαγραφή',
            deleteview: 'Διαγραφή προβολής',
            deleteviewconfirm: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτή την προβολή;',
            description: 'Περιγραφή',
            disable: 'Απενεργοποίηση',
            domainalias: 'Ψευδώνυμο τομέα',
            domainsof: 'Τομείς του {0}',
            edit: 'Επεξεργασία',
            editview: 'Επεξεργασία ρύθμισης προβολής',
            enable: 'Ενεργοποίηση',
            fieldsets: 'Πεδία',
            filters: 'Φίλτρα',
            generalproperties: 'Γενικές ιδιότητες',
            group: 'Γκρουπ',
            innerjoin: 'Εσωτερική σύνδεση',
            jointype: 'Τύπος σύνδεσης',
            joinview: 'Προβολή από συμμετοχή',
            klass: 'Τάξη',
            manageview: 'Διαχείρηση προβολής',
            masterclass: 'Κύρια τάξη',
            masterclassalias: 'Ψευδώνυμο κύριας τάξης',
            name: 'Όνομα',
            newjoinview: 'Νέα προβολή από συμμετοχή',
            outerjoin: 'Εξωτερική σύνδεση',
            pleaseseleceavalidmasterclass: 'Επιλέξτε μια έγκυρη κύρια τάξη',
            refreshafteredit: 'Θέλετε να ανανεώσετε τη σελίδα για να δείτε τις αλλαγές;',
            selectatleastoneattribute: 'Επιλέξτε τουλάχιστον ένα χαρακτηριστικό.',
            selectdisplayattributegrid:
                'Επιλέξτε τουλάχιστον ένα χαρακτηριστικό για εμφάνιση στο πλέγμα και ένα στο μειωμένο πλέγμα.',
            showingrid: 'Προβολή στο πλέγμα',
            showinreducedgrid: 'Προβολή στο μειωμένο πλέγμα',
            targetalias: 'Ψευδώνυμο κατηγορίας στόχου'
        },
        login: {
            buttons: {
                login: 'Σύνδεση',
                logout: 'Αλλαγή χρήστη'
            },
            fields: {
                group: 'Ομάδα',
                language: 'Γλώσσα',
                password: 'Κωδικός πρόσβασης',
                tenants: 'Οι μισθωτές',
                username: 'Όνομα χρήστη'
            },
            loggedin: 'Συνδεδεμένοι',
            sso: {
                loginfailure: 'Αποτυχία σύνδεσης',
                loginwith: 'Σύνδεση με {0}',
                or: 'ή',
                usernotenabled: 'Ο χρήστης δεν είναι ενεργοποιημένος'
            },
            title: 'Σύνδεση',
            welcome: 'Καλώς ορίσατε πίσω {0}.'
        },
        main: {
            administrationmodule: 'Ενότητα Διαχείρισης',
            alerts: 'Ειδοποιήσεις',
            baseconfiguration: 'Διαμόρφωση βάσης',
            cardlock: {
                lockedmessage: 'Δεν μπορείτε να επεξεργαστείτε αυτήν την κάρτα επειδή ο {0} την επεξεργάζεται.',
                someone: 'Κάποιος'
            },
            changegroup: 'Αλλαγή ομάδας',
            changetenant: 'Αλλαγή {0}',
            confirmchangegroup: 'Είστε βέβαιοι ότι θέλετε να αλλάξετε την ομάδα;',
            confirmchangetenants: 'Είστε βέβαιοι ότι θέλετε να αλλάξετε ενεργούς ενοικιαστές;',
            confirmdisabletenant: 'Είστε βέβαιοι ότι θέλετε να απενεργοποιήσετε τη σημαία "Αγνόηση ενοικιαστών";',
            confirmenabletenant: 'Είστε βέβαιοι ότι θέλετε να ενεργοποιήσετε τη σημαία "Αγνόηση ενοικιαστών";',
            description: 'Περιγραφή',
            ignoretenants: 'Παράβλεψη {0}',
            info: 'Πληροφορίες',
            logo: {
                cmdbuild: 'Λογότυπο CMDBuild',
                cmdbuildready2use: 'Λογότυπο CMDBuild READY2USE',
                companylogo: 'Λογότυπο Εταιρείας',
                hdaitsm: 'HDA ITSM',
                openmaint: 'το λογότυπο openMAINT'
            },
            logout: 'Αποσύνδεση',
            managementmodule: 'Ενότητα διαχείρησης δεδομένων',
            multigroup: 'Πολλαπλή ομάδα',
            multitenant: 'Πολλαπλό {0}',
            navigation: 'Πλοήγηση',
            pagenotfound: 'Η σελίδα δεν βρέθηκε',
            password: {
                change: 'Αλλαγή κωδικού πρόσβασης',
                confirm: 'Επιβεβαίωση κωδικού πρόσβασης',
                email: 'Διεύθυνση email',
                err_confirm: 'Οι κωδικοί πρόσβασης δεν ταιριάζουν.',
                err_diffprevious: 'Ο νέος κωδικός πρόσβασης δεν μπορεί να είναι ίδιος με τον προηγούμενο.',
                err_diffusername: 'Ο κωδικός πρόσβασης δεν μπορεί να είναι ίδιος με το όνομα χρήστη.',
                err_length: 'Ο κωδικός πρόσβασης πρέπει να έχει μήκος τουλάχιστον {0} χαρακτήρων.',
                err_reqdigit: 'Ο κωδικός πρόσβασης πρέπει να περιέχει τουλάχιστον ένα ψηφίο.',
                err_reqlowercase: 'Ο κωδικός πρόσβασης πρέπει να περιέχει τουλάχιστον ένα πεζό γράμμα.',
                err_requppercase: 'Ο κωδικός πρόσβασης πρέπει να περιέχει τουλάχιστον ένα κεφαλαίο γράμμα.',
                expired: 'Ο κωδικός πρόσβασης σας έχει λήξει. Αλλάξτε τον τώρα.',
                forgotten: 'Ξέχασα των κωδικό πρόσβασης μου',
                new: 'Νέος κωδικός πρόσβασης',
                old: 'Παλιός κωδικός πρόσβασης',
                recoverysuccess: 'Σας στείλαμε email με οδηγίες ανάκτησης του κωδικού πρόσβασης',
                reset: 'Επαναφορά κωδικού πρόσβασης',
                saved: 'Ο κωδικός πρόσβασης αποθηκεύτηκε επιτυχώς!'
            },
            pleasecorrecterrors: 'Παρακαλούμε διορθώστε τα υποδεικνυόμενα!',
            preferences: {
                comma: 'Κόμμα',
                decimalserror: 'Πρέπει να υπάρχει πεδίο δεκαδικών',
                decimalstousandserror: 'Τα δεκαδικά ψηφία και οι διαχωριστές χιλιάδων πρέπει να είναι διαφορετικά',
                default: 'Προκαθορισμένο',
                defaultvalue: 'Προεπιλεγμένη τιμή',
                favouritemenulocation: 'Τοποθεσία μενού αγαπημένων',
                firstdayofweek: 'Πρώτη μέρα της εβδομάδας',
                gridpreferencesclear: 'Καθαρισμός προτιμήσεων πλέγματος',
                gridpreferencescleared: 'Σβήστηκαν οι προτιμήσεις πλέγματος!',
                gridpreferencessave: 'Αποθήκευση προτιμήσεων πλέγματος',
                gridpreferencessaved: 'Αποθηκεύτηκαν οι προτιμήσεις πλέγματος!',
                gridpreferencesupdate: 'Ενημέρωση προτιμήσεων πλέγματος',
                groupemailsbystatus: 'Εμφάνιση αλληλογραφίας ομαδοποιημένης κατά κατάσταση',
                labelcsvseparator: 'Διαχωριστικό CSV',
                labeldateformat: 'Μορφή ημερομηνίας',
                labeldecimalsseparator: 'Διαχωριστής δεκαδικών',
                labellanguage: 'Γλώσσα',
                labelthousandsseparator: 'Διαχωριστικό Χιλιάδων',
                labeltimeformat: 'Μορφή ώρας',
                msoffice: 'Microsoft Office',
                no: 'Όχι',
                notificationslabel: 'Συνομιλία και ειδοποιήσεις',
                notificationssound: 'Αναπαραγωγή ήχου για ειδοποιήσεις',
                notificationssoundvolume: 'Όγκος ειδοποιήσεων',
                period: 'Περίοδος',
                preferredfilecharset: 'Κωδικοποίηση CSV',
                preferredofficesuite: 'Προτιμώμενη σουίτα του Office',
                sendcancellationperiod: 'Περίοδος ακύρωσης αποστολής ηλεκτρονικού ταχυδρομείου (δευτερόλεπτα)',
                showcollapsed: 'Ελαχιστοποίηση εμφάνισης',
                showfovouritesafter: 'Εμφάνιση μετά το μενού πλοήγησης',
                showfovouritesbefore: 'Εμφάνιση πριν το μενού πλοήγησης',
                space: 'Χώρος',
                thousandserror: 'Το πεδίο χιλιάδων πρέπει να είναι υπάρχει',
                timezone: 'Ζώνη ώρας',
                twelvehourformat: 'Μορφή 12 ωρών',
                twentyfourhourformat: 'Μορφή 24 ωρών',
                yes: 'Ναι'
            },
            searchinallitems: 'Αναζήτηση σε όλα τα στοιχεία',
            searchtenant: 'Αναζήτηση ενοίκου',
            selectdeselettenants: 'Επιλογή/Αποεπιλογή όλων',
            treenavcontenttitle: '{0} από {1}',
            userpreferences: 'Προτιμήσεις'
        },
        menu: {
            allitems: 'Ολα τα αντικείμενα',
            classes: 'Κατηγορίες',
            custompages: 'Προσαρμοσμένες σελίδες',
            dashboards: 'Ταμπλό',
            dependencytrees: 'Δέντρα εξαρτήσεων',
            favourites: 'Αγαπημένα',
            favouritesadd: 'Προσθήκη στα αγαπημένα',
            favouritesremove: 'Διαγραφή από τα αγαπημένα',
            homepage: 'Αρχική σελίδα',
            processes: 'Διαδικασίες',
            reports: 'Αναφορές',
            setasinitialpage: 'Ορισμός ως αρχικής σελίδας',
            views: 'Προβολές'
        },
        mobile: {
            config: {
                configure: 'Παραμετροποίηση κινητής συσκευής',
                customercode: 'Κωδικός πελάτη',
                devicename: 'Όνομα συσκευής',
                regenerate: 'Επανέκδοση',
                serverurl: 'URL εξυπηρετητή'
            }
        },
        notes: {
            edit: 'Τροποποίηση σημειώσεων'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Διαγραφή ειδοποίησης',
            emptymessage: 'Δεν υπάρχουν ειδοποιήσεις για εμφάνιση.',
            label: 'Ειδοποιήσεις',
            viewall: 'Προβολή όλων'
        },
        notifier: {
            attention: 'Προσοχή',
            copied: 'Αντιγραμμένο',
            copiedtotheclipboard: 'Το κείμενο αντιγράφηκε στο πρόχειρο',
            error: 'Σφάλμα',
            genericerror: 'Γενικό σφάλμα',
            genericerrormessage: 'Για περισσότερες πληροφορίες, παρακαλώ ελέγξτε τα αρχεία καταγραφής του συστήματος.',
            genericinfo: 'Γενικές πληροφορίες',
            genericwarning: 'Γενική προειδοποίηση',
            info: 'Πληροφορίες',
            more: 'Περισσότερες πληροφορίες',
            success: 'Επιτυχία',
            warning: 'Προειδοποίηση'
        },
        patches: {
            apply: 'Εφαρμογή επιδιορθώσεων',
            category: 'Κατηγορία',
            description: 'Περιγραφή',
            name: 'Ονομα',
            patches: 'Επιδιορθώσεις λογισμικού'
        },
        processes: {
            abortconfirmation: 'Είστε σίγουροι ότι θέλετε να εγκαταλείψετε αυτή τη διαδικασία;',
            abortprocess: 'Ακύρωση διαδικασίας',
            action: {
                advance: 'Προκαταβολή',
                label: 'Ενέργεια'
            },
            activeprocesses: 'Ενεργές διαδικασίες',
            allstatuses: 'Όλα',
            editactivity: 'Τροποποίηση δραστηριότητας',
            openactivity: 'Ανοικτή δραστηριότητα',
            resumeprocesses: 'Διαδικασίες που δεν έχουν ανασταλεί',
            startworkflow: 'Έναρξη',
            workflow: 'Διαδικασία'
        },
        relationGraph: {
            activity: 'Δραστηριότητα',
            allLabelsOnGraph: 'όλες οι ετικέτες στο γράφημα',
            card: 'Κάρτα',
            cardList: 'Λίστα κάρτας',
            cardRelations: 'Συσχετίσεις καρτών',
            choosenaviagationtree: 'Επιλέξτε ένα δέντρο πλοήγησης',
            class: 'Κατηγορία',
            classList: 'Λίστα κατηγορίας',
            compoundnode: 'Σύνθετος κόμβος',
            disable: 'Απενεργοποίηση',
            edges: 'Γωνίες',
            enable: 'Ενεργοποίηση',
            labelsOnGraph: 'tooltip σε γράφημα',
            level: 'Επίπεδο',
            nodes: 'Κόμβοι',
            openRelationGraph: 'Άνοιγμα γραφήματος συσχετισης',
            quantity: 'Ποσότητα',
            refresh: 'Ανανέωση',
            relation: 'συσχετισμός',
            relationGraph: 'Γράφημα συσχετισης',
            reopengraph: 'Ανοίξτε ξανά το γράφημα από αυτόν τον κόμβο'
        },
        relations: {
            adddetail: 'Προσθήκη λεπτομέρειας',
            addrelations: 'Προσθήκη συσχετίσεων',
            attributes: 'Χαρακτηριστικά',
            begindate: 'Ημερομηνία έναρξης',
            cardnotpresentmessage:
                'Η σχετική κάρτα δεν είναι πλέον διαθέσιμη. Για να προχωρήσετε στην τροποποίηση θα χρειαστεί να επιλέξετε άλλη κάρτα.',
            code: 'Κωδικός',
            deletedetail: 'Διαγραφή λεπτομέρειας',
            deleterelation: 'Διαγραφή συσχετισμού',
            deleterelationconfirm: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτήν τη σχέση;',
            description: 'Περιγραφή',
            editcard: 'Επεξεργασία κάρτας',
            editdetail: 'Επεξεργασία λεπτομέρειας',
            editrelation: 'Επεξεργασία συσχετισμού',
            errorexisting:
                'Η σχέση "{0}" υπάρχει ήδη. Επεξεργαστείτε την υπάρχουσα ή διαγράψτε την πριν δημιουργήσετε μια νέα σχέση.',
            extendeddata: 'Εκτεταμένα δεδομένα',
            mditems: 'Αντικείμενα',
            missingattributes: 'Ελλειπή υποχρεωτικά χαρακτηριστικά',
            opencard: 'Άνοιγμα σχετικής κάρτας',
            opendetail: 'Εμφάνιση λεπτομερειών',
            relationdata: 'Δεδομένα συσχέτισης',
            type: 'Τύπος'
        },
        reports: {
            csv: 'CSV',
            download: 'Κατέβασμα',
            format: 'Μορφή',
            msgs: {
                creationerror: 'Παρουσιάστηκε σφάλμα κατά την έναρξη της διαδικασίας δημιουργίας της αναφοράς',
                creationstarted:
                    'Η διαδικασία δημιουργίας της αναφοράς ξεκίνησε. Θα ειδοποιηθείτε όταν θα είναι έτοιμη για λήψη.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Εκτύπωση',
            reload: 'Επαναφόρτωση',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Προεπιλογή'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Καθημερινά'
                        },
                        monthly: {
                            description: 'Μηνιαία'
                        },
                        once: {
                            description: 'Μόλις'
                        },
                        weekly: {
                            description: 'Εβδομαδιαία'
                        },
                        yearly: {
                            description: 'Ετήσια'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Προεπιλογή'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Κανένας'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Προσθήκη θεματικής',
            analysisType: 'Τύπος ανάλυσης',
            attribute: 'Χαρακτηριστικό',
            bordersIntervals: 'Λάνθασμένη παραμετροποίηση γα τα όρια των διαστημάτων',
            calculateRules: 'Δημιουργία κανόνων στυλ',
            clearThematism: 'Καθαρισμός θεματικής',
            color: 'Χρώμα',
            defineLegend: 'Ορισμός λεζάντας',
            defineThematism: 'Ορισμός θεματικής',
            deletethematism: 'Είστε βέβαιοι ότι θέλετε να διαγράψετε αυτόν το θεματισμό;',
            editThematism: 'Επεξεργασία διαμόρφωσης κανόνων',
            function: 'Λειτουργία',
            generate: 'Δημιουργήστε',
            geoAttribute: 'Γεωγραφική ιδιότητα',
            graduated: 'Αποφοίτησε',
            highlightSelected: 'Επισημάνετε το επιλεγμένο στοιχείο',
            intervals: 'Διαστήματα',
            legend: 'Λεζάντα',
            name: 'Όνομα',
            newThematism: 'Νέα θεματική',
            punctual: 'Ακριβής',
            quantity: 'Ποσότητα',
            segments: 'Τμήματα',
            source: 'Πηγή',
            table: 'Πίνακας',
            thematism: 'Θεματισμοί',
            value: 'Τιμή'
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
                    exitwithoutsave: 'Έξοδος χωρίς αποθήκευση',
                    stayinedit: 'Πίσω στην επεξεργασία'
                },
                savebeforeexit:
                    'Υπάρχουν μη αποθηκευμένες αλλαγές στα συνημμένα. Θέλετε να τα αποθηκεύσετε πριν βγείτε;',
                saveprocessmessage:
                    'Συνημμένα μπορούν να αποθηκευτούν μόνο σε υπάρχουσες κάρτες. Πατώντας save γίνεται η αποθήκευση.'
            },
            calendar: {
                error: 'Η παράμετρος {0} δεν είναι ορισμένη',
                errorwrong: 'Η παράμετρος {0} δεν είναι ορισμένη σωστά'
            },
            customform: {
                addrow: 'Προσθήκη γραμμής',
                clonerow: 'Κλωνοποίηση γραμμής',
                datanotvalid: 'Τα δεδομένα δεν είναι έγκυρα',
                deleterow: 'Διαγραφή γραμμής',
                editrow: 'Επεξεργασία γραμμής',
                export: 'Εξαγωγή',
                import: 'Εισαγωγή',
                importexport: {
                    expattributes: 'Δεδομένα προς εξαγωγή',
                    file: 'Αρχείο',
                    filename: 'Όνομα αρχείου',
                    format: 'Μορφή',
                    importmode: 'Λειτουργία εισαγωγής',
                    keyattributes: 'Βασικά χαρακτηριστικά',
                    missingkeyattr: 'Επιλέξτε τουλάχιστον ένα βασικό χαρακτηριστικό',
                    modeadd: 'Προσθήκη',
                    modemerge: 'Συγχώνευση',
                    modereplace: 'Αντικατάσταση',
                    separator: 'Διαχωριστικό'
                },
                refresh: 'Ανανέωση στις προεπιλογές'
            },
            linkcards: {
                checkedonly: 'Ελέγχθηκε μόνο',
                editcard: 'Επεξεργασία κάρτας',
                opencard: 'Άνοιγμα κάρτας',
                refreshselection: 'Εφαρμογή προεπιλογής',
                removefilterconfirm: 'Με αυτή την ενέργεια θα καταργηθεί το τρέχον φίλτρο. Θέλετε να συνεχίσετε;',
                selectioncleared: 'Η επιλογή ακυρώθηκε',
                togglefilterdisabled: 'Απενεργοποίηση φίλτρου πλέγματος',
                togglefilterenabled: 'Ενεργοποίηση φίλτρου πλέγματος'
            },
            notimplemented: 'Το widget <strong>{0}</strong> δεν έχει υλοποιηθεί!',
            notsupportedinline:
                'Η λειτουργία ενσωματωμένης λειτουργίας δεν υποστηρίζεται για αυτόν τον τύπο widget. Διορθώστε τη διαμόρφωση του widget.',
            required: 'Αυτό το widget είναι απαραίτητο.'
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
