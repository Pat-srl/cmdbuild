(function () {
    Ext.define('CMDBuildUI.locales.fr.Locales', {
        requires: ['CMDBuildUI.locales.fr.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'fr',
        administration: CMDBuildUI.locales.fr.LocalesAdministration.administration,
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
            buttonplaceholder: 'Bouton',
            iframeplaceholder: 'Editeur de texte',
            languageselector: 'Secteur de langue',
            resizepanel: {
                horizontally: 'Redimensionner les panneaux horizontalement',
                vertically: 'Redimensionner les panneaux verticalement'
            },
            usermenu: 'Menu utilisateur'
        },
        attachments: {
            add: 'Ajouter des pièces jointes',
            attachmenthistory: 'Historique des pièces jointes',
            author: 'Auteur',
            authordescription: "Description de l'auteur",
            browse: 'Chercher &hellip;',
            category: 'Catégorie',
            code: 'Code',
            creationdate: 'Date de création',
            deleteattachment: 'Supprimer la pièce jointe',
            deleteattachment_confirmation: 'Êtes-vous sûr de vouloir supprimer cette pièce jointe ?',
            description: 'Description',
            download: 'Télécharger',
            dropfiles: 'Déposez les fichiers ici',
            editattachment: 'Modifier la pièce jointe',
            errordms: "Le DMS est désactivé et il n'est pas possible d'enregistrer des fichiers",
            file: 'Fichier',
            filealreadyinlist: 'Le fichier {0} existe déjà',
            filename: 'Nom de fichier',
            fileview: 'Voir pièce jointe',
            invalidfiles: 'Supprimer les fichiers invalides',
            majorversion: 'Version majeure',
            maxsize: 'La taille de fichier maximale autorisée est de {0} Mo.',
            metadata: 'Métadonnées',
            modificationdate: 'Date de modification',
            new: 'Nouvelle pièce jointe',
            nocategory: 'Non classé',
            preview: 'Aperçu',
            removefile: 'Supprimer le fichier',
            statuses: {
                empty: 'Fichier vide',
                error: 'Erreur',
                extensionNotAllowed: 'Extension de fichier non autorisée',
                loaded: 'Chargé',
                ready: 'Prêt',
                toolarge: 'Volume maximum atteint'
            },
            successupload: '{0} pièce(s) jointe(s) importée(s)',
            uploadfile: 'Téléverser un fichier ...',
            version: 'Version',
            viewhistory: "Afficher l'historique des pièces jointes",
            viewmetadata: 'Voir les métadonnées de la pièce jointe',
            warningmessages: {
                atleast:
                    '{0}: {1} pièces jointes de type "{2}" ont été téléchargées. Cette catégorie comporte au moins {3} pièces jointes',
                exactlynumber:
                    '{0}: {1} pièces jointes de type "{2}" ont été téléchargées. Cette catégorie comporte {3} pièces jointes',
                maxnumber:
                    '{0}: {1} pièces jointes de type "{2}" ont été téléchargées. Cette catégorie comporte un maximum de {3} pièces jointes'
            },
            wrongfileextension: "L'extension de fichier {0} n'est pas autorisée"
        },
        bim: {
            bimViewer: 'Bim Viewer',
            card: {
                label: 'Fiche'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: "Taille du cadre de sélection le long de l'axe X",
                BOUNDING_BOX_SIZE_ALONG_Y: "Taille du cadre de sélection le long de l'axe Y",
                BOUNDING_BOX_SIZE_ALONG_Z: "Taille du cadre de sélection le long de l'axe Z",
                calculated: 'Calculé',
                description: 'Description',
                guid: 'GUID',
                label: 'Propriétés',
                name: 'Nom',
                type: 'Genre',
                uuid: 'UUID'
            },
            layers: {
                label: 'Calques',
                menu: {
                    hideAll: 'Tout cacher',
                    hideAllLayers: 'Masquer tous les calques',
                    hideAllXeokit: 'Tout cacher {0}',
                    showAll: 'Montrer tout',
                    showAllLayers: 'Afficher tous les calques',
                    showAllXeokit: 'Afficher tout {0}'
                },
                name: 'Nom',
                quantity: 'Quantité',
                visibility: 'Visibilité'
            },
            menu: {
                camera: 'Caméra',
                disableslice: "Désactiver le rognage d'objet",
                enableslice: "Activer l'objet sélection",
                frontView: 'Vue de face',
                mod: 'Commandes de la visionneuse',
                ortho: 'Basculer vers la vue orthogonale',
                orthographic: 'Caméra orthographique',
                pan: 'Panoramique',
                panXeokit: "Mode d'affichage : décalage",
                perspe: 'Passer à la vue en perspective',
                perspective: 'Caméra perspective',
                resetSlice: 'Supprimer la coupe',
                resetView: 'Réinitialiser la vue',
                rotate: 'Pivoter',
                rotateXeokit: "Mode d'affichage : rotation",
                save: 'Enregistrer la vue',
                shortcuts: {
                    down: 'Pivoter en bas',
                    left: 'Pivoter à gauche',
                    right: 'Pivoter à droite',
                    title: 'Raccourcis clavier',
                    up: 'Pivoter en haut',
                    zoomin: 'Zoomer',
                    zoomout: 'Dézoomer'
                },
                sideView: 'Vue de côté',
                threeD: 'Passer à la vue 3D',
                topView: 'Vue de dessus',
                twoD: 'Passer à la vue 2D'
            },
            navCube: {
                back: 'Retour',
                bottom: 'En dessous',
                front: 'Face',
                left: 'À gauche',
                right: 'Droit',
                top: 'Dessus'
            },
            showBimCard: 'Ouvrir la visionneuse 3D',
            tree: {
                arrowTooltip: 'Sélectionner un élément',
                label: 'Arbre',
                multipleElements: 'Sélectionner des éléments',
                open_card: 'Ouvrir la fiche associée',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Arrêter les processus sélectionnés',
            addfields: 'Ajouter des champs',
            alertdownloadattachments:
                "Certaines pièces jointes ne seront pas téléchargées car elles n'ont pas encore été enregistrées sur le serveur",
            alertnoattachmentsdownload:
                "Aucune pièce jointe ne sera téléchargée car elle n'a pas encore été enregistrée sur le serveur",
            cancelselection: 'Annuler la sélection',
            confirmabort:
                'Vous supprimez {0} instances de processus. Êtes-vous sûr de vouloir procéder aux modifications ?',
            confirmdelete: 'Vous supprimez {0} cartes. Êtes-vous sûr de vouloir procéder aux modifications ?',
            confirmdeleteattachements:
                'Vous supprimez {0} pièces jointes. Êtes-vous sûr de vouloir procéder aux modifications ?',
            confirmedit: 'Vous modifiez {0} pour {1} cartes. Êtes-vous sûr de vouloir procéder aux modifications ?',
            delete: 'Supprimer les éléments sélectionnés',
            download: 'Téléchargez les pièces jointes sélectionnées',
            downloadall: 'Télécharger toutes les pièces jointes',
            edit: 'Modifier les éléments sélectionnés',
            missingfields:
                'Les champs suivants doivent être ajoutés pour que la validation du formulaire fonctionne : {0}',
            noattributeselected: 'Veuillez sélectionner au moins un attribut',
            selectall: 'Sélectionnez tous les articles'
        },
        calendar: {
            active_expired: 'Actif / expiré',
            add: 'Ajouter un planning',
            advancenotification: 'Notification avant',
            allcategories: 'Toutes catégories',
            alldates: 'Toutes les dates',
            associatedcard: 'Carte associée',
            calculated: 'Calculé',
            calendar: 'calendrier',
            cancel: 'Marquer comme annulé',
            category: 'catégorie',
            class: 'Classe',
            cm_confirmcancel: 'Voulez-vous vraiment marquer les dates limites sélectionnées comme annulées ?',
            cm_confirmcomplete: 'Voulez-vous vraiment marquer les dates limites sélectionnés comme terminées ?',
            cm_markcancelled: 'Marquer les dates limites sélectionnées comme annulées',
            cm_markcomplete: 'Marquer les dates limites sélectionnées comme terminées',
            complete: 'Terminé(e)',
            completed: 'Terminé',
            date: 'Date',
            days: 'Jours',
            delaybeforedeadline: 'Retard avant la date limite',
            delaybeforedeadlinevalue: 'Retard avant la date limite',
            description: 'Description',
            editevent: 'Modifier la date limite',
            enddate: 'Date de fin',
            endtype: 'Type de fin',
            event: 'Date limite',
            execapplyfilter: "Impossible d'exécuter applyAdvanceFilter car il existe plusieurs magasins",
            execremovefilter: "Impossible d'exécuter removeAdvanceFilter car il existe plusieurs magasins",
            executiondate: "Date d'exécution",
            frequency: 'Fréquence',
            frequencymultiplier: 'Multiplicateur de fréquence',
            grid: 'Grille',
            leftdays: 'Jours restants',
            londdescription: 'Description complète',
            manual: 'Manuel',
            maxactiveevents: 'Nombre maximum de dates limites actives',
            messagebodydelete: 'Voulez-vous supprimer les règles de dates limites ?',
            messagebodyplural: 'Il y a {0} règles de dates limites',
            messagebodyrecalculate:
                'Souhaitez-vous recalculer la règle des plannings en fonction de la nouvelle date ?',
            messagebodysingular: 'Il y a {0} règle de délai',
            messagetitle: 'Recalcul de la date limite',
            missingdays: 'Jours manquants',
            next30days: '31 prochains jours',
            next7days: '8 prochains jours',
            notificationtemplate: 'Modèle de notification',
            notificationtext: 'Texte de notification',
            occurencies: "Nombre d'occurrences",
            operation: 'Opération',
            partecipantgroup: 'Groupe de référence',
            partecipantuser: 'Utilisateur référent',
            priority: 'Priorité',
            recalculate: 'Recalculer',
            referent: 'Contact',
            scheduler: 'Dates limites',
            sequencepaneltitle: 'Générer des emplois du temps',
            startdate: 'Date de début',
            status: 'Statut',
            today: "aujourd'hui",
            type: 'genre',
            viewevent: 'Voir la date limite',
            waiteventgeneration: "Génération des évènements. Merci d'attendre leur complétion.",
            widgetcriterion: 'Critère de calcul',
            widgetemails: 'Emails',
            widgetgotocard: 'Aller à la carte source',
            widgetsourcecard: 'Carte source'
        },
        chat: {
            title: 'Tchat'
        },
        classes: {
            cards: {
                addcard: 'Ajouter une fiche',
                clone: 'Copier',
                clonewithrelations: 'Carte de clonage et relations',
                deletebeaware: 'Veuillez noter que :',
                deleteblocked:
                    "Puisqu'il existe des relations avec {0}, il n'est pas possible de procéder à la suppression des cartes.",
                deletecard: 'Supprimer la fiche',
                deleteconfirmation: 'Êtes-vous sûr de vouloir supprimer cette carte ?',
                deleterelatedcards: 'les cartes associées {0} seront supprimées',
                deleterelations: 'les relations avec les cartes {0} seront supprimées',
                label: 'Fiches',
                modifycard: 'Modifier la fiche',
                opencard: 'Carte ouverte',
                print: "Carte d'impression"
            },
            simple: 'Simple',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Ajouter',
                apply: 'Appliquer',
                cancel: 'Abandonner',
                clear: 'Supprimer',
                close: 'Fermer',
                copy: 'Copier',
                copyerrormessage: "Copier le message d'erreur dans le presse-papier",
                delete: 'Supprimer',
                edit: 'Modifier',
                execute: 'Exécuter',
                forward: 'Transférer',
                help: 'Aide',
                hide: '<em>Hide</em>',
                load: 'Charger',
                open: 'Ouvrir',
                previous: 'Précédent',
                refresh: 'Actualiser les données',
                remove: 'Supprimer',
                save: 'Enregistrer',
                saveandapply: 'Enregistrer et appliquer',
                saveandclose: 'Enregistrer et fermer',
                search: 'Chercher',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Chercher...'
            },
            and: 'et',
            attributes: {
                boolfalse: 'Faux',
                booltrue: 'Vrai',
                link: {
                    errlabel: "L'étiquette est obligatoire",
                    errurl: "L'Url est obligatoire si le libellé n'est pas vide",
                    label: 'Étiqueter',
                    url: 'Url'
                },
                nogroup: 'Données de base'
            },
            dates: {
                date: 'j / m / A',
                datetime: 'j / m / A H: i: s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Supprimer HTML',
                expand: 'Agrandir la fenêtre d’édition',
                reduce: "Réduire l'éditeur",
                signature: 'Ajouter une signature',
                unlink: 'Supprimer le lien',
                unlinkmessage: 'Transformez le lien sélectionné en texte'
            },
            grid: {
                disablemultiselection: 'Désactiver la sélection multiple',
                enamblemultiselection: 'Activer la sélection multiple',
                export: 'Exporter des données',
                filterremoved: 'Le filtre actuel a été supprimé',
                import: 'Importer des données',
                itemnotfound: 'Objet non trouvé',
                lastUpdate: '<em>Last update</em>',
                list: 'Liste',
                opencontextualmenu: 'Ouvrir le menu contextuel',
                print: 'Imprimer',
                printcsv: 'Imprimer au format CSV',
                printmap: 'Imprimer la carte',
                printodt: 'Imprimer en ODT',
                printpdf: 'Imprimer en PDF',
                row: 'Objet',
                rows: 'Objets',
                subtype: 'Sous-type',
                thematism: 'Thématisme'
            },
            tabs: {
                activity: 'Opération',
                attachment: 'Pièce jointe',
                attachments: 'Pièces jointes',
                card: 'Fiche',
                clonerelationmode: 'Mode de clonage de relation',
                details: 'Informations',
                emails: 'Emails',
                history: 'Historique',
                notes: 'Notes',
                relations: 'Relations',
                schedules: 'Les dates limites',
                sources: 'Sources'
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
                download: 'Téléchargez le graphique',
                downloaddata: 'Télécharger les données du graphique',
                gridhide: 'Masquer le tableau de données',
                gridshow: 'Afficher le tableau de données',
                openinpopup: 'Ouvrir dans une fenêtre contextuelle',
                reload: 'Recharger',
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
            abort: 'Abandonner',
            addattachmentsfromdocumentarchive: 'Ajouter des pièces jointes à partir des archives de documents',
            alredyexistfile: 'Il existe déjà un fichier à ce nom',
            archivingdate: "Date d'archivage",
            attachfile: 'Joindre un fichier',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Cci',
            cc: 'Cc',
            composeemail: 'Ecrire un email',
            composefromtemplate: "Créer à partir d'un modèle",
            delay: 'Retard',
            delays: {
                day1: 'Dans 1 jour',
                days2: 'Dans 2 jours',
                days4: 'Dans 4 jours',
                hour1: '1 heure',
                hours2: '2 heures',
                hours4: '4 heures',
                month1: 'Dans 1 mois',
                negativeday1: '1 jour avant',
                negativedays2: '2 jours avant',
                negativedays4: '4 jours avant',
                negativehour1: '1 heure plus tôt',
                negativehours2: '2 heures avant',
                negativehours4: '4 heures avant',
                negativemonth1: '1 mois avant',
                negativeweek1: '1 semaine avant',
                negativeweeks2: '2 semaines avant',
                none: 'Aucun',
                week1: 'Dans 1 semaine',
                weeks2: 'Dans 2 semaines'
            },
            dmspaneltitle: 'Choisir des pièces jointes depuis la base de données',
            edit: 'Modifier',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'De',
            gridrefresh: 'Rafraîchir la grille',
            groupbystatus: 'Grouper par statut',
            keepsynchronization: 'Rester synchronisé',
            maxsize: 'La taille totale autorisée pour les pièces jointes est de {0} Mo.',
            message: 'Message',
            regenerateallemails: 'Regénérer tous les emails',
            regenerateemail: 'Regénérer les emails',
            reloadconfirmationmessage: 'Les brouillons des e-mails seront supprimés. Voulez-vous continuer ?',
            remove: 'Supprimer',
            remove_confirmation: 'Êtes-vous sûr de vouloir supprimer cet email ?',
            reply: 'Répondre',
            replyprefix: 'Sur {0}, {1} a écrit:',
            searchattachment: 'Chercher une pièce jointe',
            searchcard: 'Chercher une carte',
            selectaclass: 'Sélectionnez une classe',
            sendemail: 'Envoyer un email',
            sendingemail: "Envoi de l'email en cours",
            signature: 'Signature',
            size: 'Taille',
            status: 'Statut',
            statuses: {
                draft: 'Brouillon',
                error: 'Erreur',
                new: 'Nouveau',
                outgoing: 'Sortant',
                received: 'Reçu',
                sent: 'Envoyé',
                skipped: 'Passer'
            },
            subject: 'Sujet',
            templateattachments: 'Cet email contient des pièces jointes automatiquement téléchargées par le modèle',
            to: 'À',
            view: 'Vue'
        },
        errors: {
            actionnotallowed: 'Action non autorisée',
            autherror: 'Mauvais utilisateur ou mot de passe',
            classnotfound: 'Classe {0} non touvée',
            fieldrequired: 'Ce champ est obligatoire',
            invalidfilter: 'Filtre non valide',
            nodatatoexport: "Le graphique n'a pas de données à exporter",
            notfound: 'Objet non trouvé',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'actes',
            addfilter: 'Ajouter un filtre',
            any: 'Au moins un',
            attachments: 'Pièces jointes',
            attachmentssearchtext: 'Rechercher dans les pièces jointes',
            attribute: 'Choisir un attribut',
            attributes: 'Attributs',
            clearfilter: 'Supprimer les filtres',
            clone: 'Copier',
            copyof: 'Copie de',
            currentgroup: 'Groupe sélectionné',
            currentuser: 'Utilisateur sélectionné',
            defaultset: 'Définir par défaut',
            defaultunset: 'Supprimer par défaut',
            description: 'Description',
            domain: 'Domaine',
            errorname: 'Un filtre du même nom existe déjà',
            filterdata: 'Filtrer les données',
            fromfilter: 'Du filtre',
            fromselection: 'À partir de la sélection',
            group: 'Groupe',
            ignore: 'Ignorer',
            migrate: 'Migre',
            name: 'Nom',
            newfilter: 'Nouveau filtre',
            noone: 'Aucun',
            operator: 'Opérateur',
            operators: {
                and: 'Et',
                beginswith: 'Commence par',
                between: 'Entre',
                contained: 'Contenu',
                containedorequal: 'Contenu ou égal',
                contains: 'Contient',
                containsorequal: 'Contient ou est égal',
                descriptionbegin: 'La description commence par',
                descriptioncontains: 'La description contient',
                descriptionends: 'La description se termine par',
                descriptionnotbegin: 'La description ne commence pas par',
                descriptionnotcontain: 'La description ne contient pas',
                descriptionnotends: 'La description ne se termine pas par',
                different: 'Différent',
                doesnotbeginwith: 'Ne commence pas par',
                doesnotcontain: 'Ne contient pas',
                doesnotendwith: 'Ne se termine pas par',
                endswith: 'Se termine par',
                equals: 'Égal à',
                greaterthan: 'Majeur',
                isnotnull: "N'est pas nul",
                isnull: 'Est nul',
                lessthan: 'Moins que',
                or: 'OU'
            },
            relations: 'Relations',
            removeattribute: "Supprimer l'attribut",
            removeblock: 'Supprimer le bloc',
            type: 'Type du serveur de fichiers',
            typeinput: "Paramètre d'entrée des données",
            typeorchoosetheattributename: "Ecrire ou choisir le nom de l'attribut",
            user: 'Utilisateur',
            value: 'Valeur'
        },
        gis: {
            card: 'Fiche',
            cardsMenu: 'Menu des fiches',
            code: 'Code',
            contextmenu: {
                coordinates: 'Obtenir les coordonnées',
                coordinatescopiedmessage: 'Coordonnées copiées vers le presse-papier'
            },
            description: 'Description',
            extension: {
                errorCall: 'Erreur',
                noResults: 'Aucun résultat'
            },
            externalServices: 'Services externes',
            geographicalAttributes: 'Attributs géographiques',
            geoserverLayers: 'Calques du Geoserver',
            labels: {
                all: 'Toutes',
                alltitle: 'Afficher toutes les étiquettes',
                alltitleclass: 'Afficher toutes les étiquettes de classe',
                clear: 'Effacer la recherche',
                hidden: 'Cachées',
                hiddentitle: 'Masquer les étiquettes',
                hideinfowindow: "Masquer la fenêtre d'informations",
                label: 'Étiquettes',
                search: 'Chercher sur la carte',
                selected: 'Sélectionné(e)',
                selectedtitle: "Afficher les étiquettes de l'élément sélectionné",
                showinfowindow: "Afficher la fenêtre d'informations"
            },
            layers: 'Calques',
            layersTab: {
                clearPreferences: 'Supprimer les préférences de calque',
                preferencesCleared: 'Préférences de calque supprimées',
                preferencesSaved: 'Préférences de calque enregistrées',
                savePreferences: 'Enregistrer les préférences de calque',
                updatePreferences: 'Mettre à jour les préférences de calque'
            },
            list: 'Liste',
            longpresstitle: 'Éléments géographiques de la zone',
            map: 'Carte',
            mapServices: 'Services de carte',
            menu: {
                font: 'Taille de la police',
                options: 'Options'
            },
            position: 'Position',
            root: 'Racine',
            sourceMap: {
                default: 'Utiliser la vue par défaut',
                satellite: 'Utiliser la vue satéllite'
            },
            tree: 'Arbre de navigation',
            type: 'Type',
            view: 'Vue',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Nom de l’opération',
            activityperformer: 'Lanceur d’opération',
            attributes: 'Attributs ({0})',
            begindate: 'Date de début',
            enddate: 'Date de fin',
            fieldname: 'Nom du champ',
            fulldata: 'Toutes les données',
            onlychanges: 'Seuls les changements',
            otherrelations: 'Autres relations',
            processstatus: 'Statut',
            referencerelation: 'Relations de la référence',
            relation: 'Relation',
            system: 'Système',
            type: 'Type',
            user: 'Utilisateur',
            userdescription: "Description de l'utilisateur",
            value: 'Valeur',
            viewmode: "Mode d'affichage"
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
            accessibility: 'Accessibilité',
            animals: 'Animaux',
            arrows: 'Flèches',
            audiovideo: 'Audio video',
            automotive: 'Automobile',
            autumn: 'Automne',
            beverage: 'Boisson',
            buildings: 'Bâtiments',
            business: 'Entreprise',
            charity: 'Charité',
            chat: 'Tchat',
            chess: 'Echecs',
            childhood: 'Enfance',
            code: 'Code',
            communication: 'Communication',
            computers: 'Ordinateurs',
            construction: 'Construction',
            currency: 'Devise',
            datetime: 'Date et heure',
            design: 'Design',
            editors: 'Editeurs',
            education: 'Education',
            emoji: 'Emoji',
            energy: 'Energie',
            files: 'Fichiers',
            finance: 'Finance',
            food: 'Nourriture',
            fruitvegetable: 'Fruits et légumes',
            games: 'Jeux',
            gamingtabletop: 'Jeux de plateau',
            gender: 'Genre',
            general: 'Général',
            halloween: 'Halloween',
            hands: 'Mains',
            hotel: 'Hôtel',
            household: 'Foyer',
            images: 'Images',
            interfaces: 'Interfaces',
            logistics: 'Logistique',
            maps: 'Cartes',
            maritime: 'Maritime',
            marketing: 'Marketing',
            mathematics: 'Mathématiques',
            medical: 'Médical',
            moving: 'Déménagement',
            music: 'Musique',
            objects: 'Arbre',
            paymentsshopping: 'Paiement shopping',
            political: 'Politique',
            religion: 'Religion',
            sciencefiction: 'Science fiction',
            security: 'Sécurité',
            shapes: 'Formes',
            shopping: 'Shopping',
            social: 'Social',
            spinners: 'Toupies',
            sports: 'Sports',
            spring: 'Printemps',
            status: 'Statut',
            toggle: 'Bascule',
            travel: 'Voyage',
            userspeople: 'Utilisateurs',
            vehicles: 'Véhicules',
            weather: 'Météo',
            winter: 'Hiver'
        },
        importexport: {
            database: {
                uri: 'Base de données URI',
                user: 'Utilisateur de la base de données'
            },
            downloadreport: 'Télécharger le rapport',
            emailfailure: "Une erreur est survenue lors de l'envoi du courrier électronique.",
            emailmessage: 'Vous trouverez ci-joint le rapport d\'importation du fichier "{0}" du {1}',
            emailsubject: "Rapport de données d'importation",
            emailsuccess: "L'email a été envoyé avec succès !",
            export: 'Export',
            exportalldata: 'Toutes les données',
            exportfiltereddata: 'Seules les données correspondant au filtre de la grille',
            gis: {
                importconfiguration: 'Importer configuration',
                shapeimportdisabled: "L'importation de formes n'est pas activée pour ce modèle",
                shapeimportenabled: "L'importation de formes est activée pour ce modèle"
            },
            ifc: {
                card: 'Fiche',
                project: 'Projet',
                sourcetype: 'Importer depuis'
            },
            import: 'Import',
            importresponse: "Réponse d'importation",
            response: {
                created: 'Cartes créées',
                deleted: 'Cartes supprimées',
                errors: 'Erreurs',
                linenumber: 'Numéro de ligne',
                message: 'Message',
                modified: 'Cartes modifiées',
                notprocessed: 'Cartes non traitées',
                processed: 'Lignes traitées',
                recordnumber: "Numéro d'enregistrement",
                unchanged: 'Fiches non modifiées'
            },
            sendreport: 'Envoyer un rapport',
            template: 'Modèle',
            templatedefinition: 'Modèle de définition'
        },
        joinviews: {
            active: 'Actif',
            addview: 'Ajouter une vue',
            alias: 'Alias',
            attribute: 'Attribut',
            attributecustomization: 'Customisation des attributs',
            attributes: 'Attributs',
            attributeschoice: 'Choix des attributs',
            attributesof: 'Attributs de: {0}',
            createview: 'Créer une vue',
            datasorting: 'Tri des données',
            delete: 'Supprimer',
            deleteview: 'Supprimer la vue',
            deleteviewconfirm: 'Êtes-vous sûr de vouloir supprimer cette vue?',
            description: 'Description',
            disable: 'Désactiver',
            domainalias: 'Alias ​​de domaine',
            domainsof: 'Domaines de {0}',
            edit: 'Modifier',
            editview: 'Modifier les configurations de vue',
            enable: 'Activer',
            fieldsets: 'Fieldsets',
            filters: 'Filtres',
            generalproperties: 'Propriétés générales',
            group: 'Groupe',
            innerjoin: 'Inner join',
            jointype: 'Type de pièce jointe',
            joinview: 'Vue depuis la jointure',
            klass: 'Classe',
            manageview: 'Gestion de vue',
            masterclass: 'Classe principale',
            masterclassalias: 'Alias ​​de classe principale',
            name: 'Nom',
            newjoinview: 'Nouvelle vue de join',
            outerjoin: 'Jointure externe',
            pleaseseleceavalidmasterclass: 'Veuillez sélectionner une classe principale valide',
            refreshafteredit: 'Recharger la page pour voir les changements ?',
            selectatleastoneattribute: 'Veuillez sélectionner au moins un attribut',
            selectdisplayattributegrid:
                'Veuillez sélectionner au moins un attribut à afficher dans la grille et un en grille réduite',
            showingrid: 'Afficher sur la grille',
            showinreducedgrid: 'Afficher en grille réduite',
            targetalias: 'Alias ​​de classe cible'
        },
        login: {
            buttons: {
                login: 'Se connecter',
                logout: "Changer d'utilisateur"
            },
            fields: {
                group: 'Groupe',
                language: 'Langue',
                password: 'Mot de passe',
                tenants: 'Les locataires',
                username: "Nom d'utilisateur"
            },
            loggedin: 'Connecté',
            sso: {
                loginfailure: "Erreur d'authentification",
                loginwith: 'Connectez-vous avec {0}',
                or: 'ou alors',
                usernotenabled: 'Utilisateur non activé'
            },
            title: 'Se connecter',
            welcome: 'Bienvenue à nouveau {0}.'
        },
        main: {
            administrationmodule: "Module d'administration",
            alerts: 'Alertes',
            baseconfiguration: 'Configuration de base',
            cardlock: {
                lockedmessage: 'Vous ne pouvez pas modifier cette carte car {0} la modifie actuellement.',
                someone: "Quelqu'un"
            },
            changegroup: 'Changer de groupe',
            changetenant: 'Changer de {0}',
            confirmchangegroup: 'Êtes-vous sûr de vouloir changer de groupe ?',
            confirmchangetenants: 'Êtes-vous sûr de vouloir changer de locataire en activité ?',
            confirmdisabletenant: 'Êtes-vous sûr de vouloir désactiver l\'indicateur "Ignorer les locataires" ?',
            confirmenabletenant: 'Êtes-vous sûr de vouloir activer le drapeau "Ignorer les locataires" ?',
            description: 'Description',
            ignoretenants: 'Ignorer de {0}',
            info: 'Info',
            logo: {
                cmdbuild: 'Logo CMDBuild',
                cmdbuildready2use: 'CMDBuild READY2USE logo',
                companylogo: "Logo d'entreprise",
                hdaitsm: 'HDA ITSM',
                openmaint: 'logo openMAINT'
            },
            logout: 'Déconnexion',
            managementmodule: 'Module de gestion des données',
            multigroup: 'Multi-groupes',
            multitenant: 'Multi-{0}',
            navigation: 'Navigation',
            pagenotfound: 'Page non trouvée',
            password: {
                change: 'Changer de mot de passe',
                confirm: 'Confirmer le mot de passe',
                email: 'Adresse e-mail',
                err_confirm: 'Les mots de passe ne correspondent pas.',
                err_diffprevious: 'Le mot de passe ne peut pas être le même que le précédent.',
                err_diffusername: "Le mot de passe ne peut pas être le même que le nom d'utilisateur '.",
                err_length: 'Votre mot de passe doit comporter au moins {0} caractères. </em>',
                err_reqdigit: 'Le mot de passe doit contenir au moins un chiffre.',
                err_reqlowercase: 'Le mot de passe doit contenir au moins un caractère minuscule.',
                err_requppercase: 'Le mot de passe doit contenir au moins un caractère majuscule.',
                expired: 'Votre mot de passe a expiré. Changez-le maintenant.',
                forgotten: "J'ai oublié mon mot de passe",
                new: 'Nouveau mot de passe',
                old: 'Ancien mot de passe',
                recoverysuccess:
                    'Nous vous avons envoyé un e-mail avec des instructions pour réinitialiser votre mot de passe.',
                reset: 'Réinitialiser le mot de passe',
                saved: 'Mot de passe enregistré correctement!'
            },
            pleasecorrecterrors: 'Veuillez corriger les erreurs indiquées !',
            preferences: {
                comma: 'Virgule',
                decimalserror: 'Le champ décimal doit être présent',
                decimalstousandserror: 'Les  séparateurs des décimales et des milliers doivent être différents',
                default: 'Par défaut',
                defaultvalue: 'Valeur par défaut',
                favouritemenulocation: 'Emplacement du menu des favoris',
                firstdayofweek: 'Premier jour de la semaine',
                gridpreferencesclear: 'Supprimer les préférences de grille',
                gridpreferencescleared: 'Préférences de grille supprimées !',
                gridpreferencessave: 'Enregistrer les préférences de grille',
                gridpreferencessaved: 'Préférences de grille enregistrées !',
                gridpreferencesupdate: 'Mettre à jour les préférences de grille',
                groupemailsbystatus: 'Afficher les emails par statut',
                labelcsvseparator: 'Séparateur CSV',
                labeldateformat: 'Format de la date',
                labeldecimalsseparator: 'Séparateur des décimales',
                labellanguage: 'Langue',
                labelthousandsseparator: 'Séparateur de milliers',
                labeltimeformat: "Format de l'heure",
                msoffice: 'Microsoft Office',
                no: 'Non',
                notificationslabel: 'Tchat et notifications',
                notificationssound: 'Jouer le son de notification',
                notificationssoundvolume: 'Volume des notifications',
                period: 'Période',
                preferredfilecharset: 'Codage CSV',
                preferredofficesuite: 'Suite Office préférée',
                sendcancellationperiod: "Délai d'annulation des emails",
                showcollapsed: 'Afficher en cascade',
                showfovouritesafter: 'Afficher après le menu de navigation',
                showfovouritesbefore: 'Afficher avant le menu de navigation',
                space: 'Espace',
                thousandserror: 'Le champ milliers doit être présent',
                timezone: 'Fuseau horaire',
                twelvehourformat: 'Format de 12 heures',
                twentyfourhourformat: 'Format 24 heures',
                yes: 'Oui'
            },
            searchinallitems: 'Rechercher dans tous les articles',
            searchtenant: 'Chercher un locataire',
            selectdeselettenants: 'Tout sélectionner / désélectionner',
            treenavcontenttitle: '{0} sur {1}',
            userpreferences: 'Préférences'
        },
        menu: {
            allitems: 'Tous les items',
            classes: 'Classes',
            custompages: 'Pages personnalisées',
            dashboards: 'Tableaux de bord',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Favoris',
            favouritesadd: 'Ajouter aux favoris',
            favouritesremove: 'Supprimer des favoris',
            homepage: '<em>Homepage</em>',
            processes: 'Processus',
            reports: 'Rapports',
            setasinitialpage: 'Définir comme page initiale',
            views: 'Vues'
        },
        mobile: {
            config: {
                configure: "Configurer l'appareil mobile",
                customercode: 'Code client',
                devicename: "Nom de l'appareil",
                regenerate: 'Régénérer',
                serverurl: 'URL du serveur'
            }
        },
        notes: {
            edit: 'Modifier la note'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Supprimer la notification',
            emptymessage: "Il n'y a aucune notification à afficher.",
            label: 'Notifications',
            viewall: 'Voir tout'
        },
        notifier: {
            attention: 'Attention',
            copied: 'Copié',
            copiedtotheclipboard: 'Texte copié vers le presse-papier',
            error: 'Erreur',
            genericerror: 'Erreur générique',
            genericerrormessage: "Pour plus d'informations, vérifier les journaux système.",
            genericinfo: 'Informations génériques',
            genericwarning: 'Avertissement générique',
            info: 'Info',
            more: '<em>More info</em>',
            success: 'Succès',
            warning: 'Attention'
        },
        patches: {
            apply: 'Appliquer des patchs',
            category: 'Catégorie',
            description: 'Description',
            name: 'Prénom',
            patches: 'Patchs'
        },
        processes: {
            abortconfirmation: 'Êtes-vous sûr de vouloir abandonner le processus ?',
            abortprocess: 'Abandonner le processus',
            action: {
                advance: 'Continuer',
                label: 'Action'
            },
            activeprocesses: 'Processus actifs',
            allstatuses: 'Tous',
            editactivity: "Modifier l'action",
            openactivity: 'Activité ouverte',
            resumeprocesses: 'Les processus qui ne sont pas en pause ne peuvent pas être relancés',
            startworkflow: 'Démarrer',
            workflow: 'Processus'
        },
        relationGraph: {
            activity: 'Opération',
            allLabelsOnGraph: 'Toutes les étiquettes',
            card: 'Fiche',
            cardList: 'Liste des fiches',
            cardRelations: 'Relation des fiches',
            choosenaviagationtree: "Choisir l'arbre de navigation",
            class: 'Classe',
            classList: 'Liste de classe',
            compoundnode: 'Nœud composé',
            disable: 'Désactiver',
            edges: 'Bordures',
            enable: 'Activer',
            labelsOnGraph: 'Outils du graphe',
            level: 'Niveau',
            nodes: 'Noeuds',
            openRelationGraph: 'Ouvrir le graphe des relations',
            quantity: 'Quantité',
            refresh: 'Rafraîchir',
            relation: 'Relation',
            relationGraph: 'Graphe des relations',
            reopengraph: 'Rouvrir le graphe de ce noeud'
        },
        relations: {
            adddetail: 'Ajouter une information',
            addrelations: 'Ajouter des relations',
            attributes: 'Attributs',
            begindate: 'Date de début',
            cardnotpresentmessage:
                'La fiche correspondante n’est plus disponible. Choisir une autre fiche pour poursuivre la modification.',
            code: 'Code',
            deletedetail: 'Supprimer l’information',
            deleterelation: 'Supprimer la relation',
            deleterelationconfirm: 'Êtes-vous sûr de vouloir supprimer cette relation ?',
            description: 'Description',
            editcard: 'Modifier la fiche',
            editdetail: 'Modifier l’information',
            editrelation: 'Modifier la relation',
            errorexisting:
                'La relation "{0}" existe déjà. Modifiez l’existante ou supprimez łà avant d’en créer une nouvelle.',
            extendeddata: 'Données étendues',
            mditems: 'Objets',
            missingattributes: 'Attributs requis manquants',
            opencard: 'Ouvrir la fiche associée',
            opendetail: 'Voir en détails',
            relationdata: 'Données relatives',
            type: 'Type du serveur de fichiers'
        },
        reports: {
            csv: 'CSV',
            download: 'Télécharger',
            format: 'Format',
            msgs: {
                creationerror: 'Une erreur s’est produite pendant la création du rapport',
                creationstarted:
                    'La création du rapport a commencé. Vous serez notifié quand le téléchargement sera prêt.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Imprimer',
            reload: 'Recharger',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Par défaut'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Quotidiennement'
                        },
                        monthly: {
                            description: 'Mensuel'
                        },
                        once: {
                            description: 'Une fois'
                        },
                        weekly: {
                            description: 'hebdomadaire'
                        },
                        yearly: {
                            description: 'Annuel'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Par défaut'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Personne'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Ajouter un thématisme',
            analysisType: "Type d'analyse",
            attribute: 'Attribut',
            bordersIntervals: "Mauvaise configuration des limites d'intervalles",
            calculateRules: 'Générer des règles de style',
            clearThematism: 'Effacer le thématisme',
            color: 'Couleur',
            defineLegend: 'Définition de la légende',
            defineThematism: 'Thématisme',
            deletethematism: 'Êtes-vous sûr de vouloir supprimer ce thématisme ?',
            editThematism: 'Modifier la configuration des règles',
            function: 'Fonction',
            generate: 'Générer',
            geoAttribute: 'geoAttribute',
            graduated: 'Gradué',
            highlightSelected: "Mettre en surbrillance l'élément sélectionné",
            intervals: 'Intervalles',
            legend: 'Légende',
            name: 'prénom',
            newThematism: 'Nouveau thématisme',
            punctual: 'Ponctuel',
            quantity: 'Quantité',
            segments: 'segments',
            source: 'Source',
            table: 'Table',
            thematism: 'Thématismes',
            value: 'Valeur'
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
                    exitwithoutsave: 'Quitter sans sauvegarder',
                    stayinedit: "Retour à l'édition"
                },
                savebeforeexit:
                    'Il y a des modifications non enregistrées dans les pièces jointes. Voulez-vous les enregistrer avant de quitter ?',
                saveprocessmessage:
                    "Les pièces jointes ne peuvent être enregistrées que dans les onglets existants. En appuyant sur Enregistrer, l'instance de processus sera créée avec les données présentes dans le formulaire."
            },
            calendar: {
                error: "Le paramètre {0} n'est pas configuré.",
                errorwrong: "Le paramètre {0} n'est pas configuré correctement."
            },
            customform: {
                addrow: 'Ajouter une ligne',
                clonerow: 'Cloner la ligne',
                datanotvalid: 'Données invalides',
                deleterow: 'Supprimer la ligne',
                editrow: 'Modifier la ligne',
                export: 'Exporter',
                import: 'Importer',
                importexport: {
                    expattributes: 'Données à exporter',
                    file: 'Fichier',
                    filename: 'Nom de fichier',
                    format: 'Format',
                    importmode: "Mode d'importation",
                    keyattributes: 'Attributs des clés',
                    missingkeyattr: 'Choisissez au moins un attribut clé',
                    modeadd: 'Ajouter',
                    modemerge: 'Fusionner',
                    modereplace: 'Remplacer',
                    separator: 'Séparateur'
                },
                refresh: 'Actualiser aux valeurs par défaut'
            },
            linkcards: {
                checkedonly: 'Seulement sélectionné',
                editcard: 'Modifier la fiche',
                opencard: 'Carte ouverte',
                refreshselection: 'Appliquer la sélection par défaut',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Désactiver le filtre de la grille',
                togglefilterenabled: 'Activer le filtre de table'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                "Le mode en ligne n'est pas pris en charge pour ce type de widget. Veuillez corriger la configuration du widget",
            required: 'Ce widget est obligatoire'
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
