(function () {
    Ext.define('CMDBuildUI.locales.hu.Locales', {
        requires: ['CMDBuildUI.locales.hu.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'hu',
        administration: CMDBuildUI.locales.hu.LocalesAdministration.administration,
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
            add: 'Csatolmány hozzáadása',
            attachmenthistory: '<em>Attachment History</em>',
            author: 'Szerző',
            authordescription: '<em>Author description</em>',
            browse: '<em>Browse &hellip;</em>',
            category: '<em>Category</em>',
            code: '<em>Code</em>',
            creationdate: '<em>Creation date</em>',
            deleteattachment: '<em>Delete attachment</em>',
            deleteattachment_confirmation: '<em>Are you sure that you want to delete this attachment?</em>',
            description: 'Leírás',
            download: 'Letöltés',
            dropfiles: '<em>Drop files here</em>',
            editattachment: '<em>Modify attachment</em>',
            errordms: '<em>The DMS is disabled and it is not possible to save files</em>',
            file: 'Fájl',
            filealreadyinlist: '<em>The file {0} is already in list.</em>',
            filename: 'Fájl neve',
            fileview: '<em>View attachment</em>',
            invalidfiles: '<em>Remove invalid files</em>',
            majorversion: '<em>Major version</em>',
            maxsize: '<em>The maximum allowed file size is {0} MB.</em>',
            metadata: '<em>Metadata</em>',
            modificationdate: 'Módosítás dátuma',
            new: '<em>New attachment</em>',
            nocategory: '<em>Uncategorized</em>',
            preview: '<em>Preview</em>',
            removefile: '<em>Remove file</em>',
            statuses: {
                empty: '<em>Empty file</em>',
                error: '<em>Error</em>',
                extensionNotAllowed: '<em>File extension not allowed</em>',
                loaded: '<em>Loaded</em>',
                ready: '<em>Ready</em>',
                toolarge: '<em>Too large</em>'
            },
            successupload: '<em>{0} attachments uploaded.</em>',
            uploadfile: '<em>Upload file...</em>',
            version: 'Verzija',
            viewhistory: '<em>View attachment history</em>',
            viewmetadata: '<em>View attachment metadata</em>',
            warningmessages: {
                atleast:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects at least {3} attachments.</em>',
                exactlynumber:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects {3} attachments.</em>',
                maxnumber:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects at most {3}  attachments.</em>'
            },
            wrongfileextension: '<em>{0} file extension is not allowed</em>'
        },
        bim: {
            bimViewer: '<em>Bim Viewer</em>',
            card: {
                label: '<em>Card</em>'
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
                label: '<em>Layers</em>',
                menu: {
                    hideAll: '<em>Hide all</em>',
                    hideAllLayers: '<em>Hide All Layers</em>',
                    hideAllXeokit: '<em>Hide all {0}</em>',
                    showAll: '<em>Show all</em>',
                    showAllLayers: '<em>Show All Layers</em>',
                    showAllXeokit: '<em>Show all {0}</em>'
                },
                name: '<em>Name</em>',
                quantity: '<em>Quantity</em>',
                visibility: '<em>Visibility</em>'
            },
            menu: {
                camera: 'Kamera',
                disableslice: '<em>Disable slice objects</em>',
                enableslice: '<em>Enable slice objects</em>',
                frontView: '<em>Front View</em>',
                mod: '<em>Viewer controls</em>',
                ortho: '<em>Switch to Orthogonal View</em>',
                orthographic: '<em>Orthographic Camera</em>',
                pan: '<em>Scroll</em>',
                panXeokit: '<em>Viewer controls: scroll (Shift)</em>',
                perspe: '<em>Switch to Perspective View</em>',
                perspective: '<em>Perspective Camera</em>',
                resetSlice: '<em>Reset slice</em>',
                resetView: '<em>Reset View</em>',
                rotate: 'Forgatás',
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
                sideView: '<em>Side View</em>',
                threeD: '<em>Switch to 3D View</em>',
                topView: '<em>Top View</em>',
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
            showBimCard: '<em>Open 3D viewer</em>',
            tree: {
                arrowTooltip: '<em>Select element</em>',
                label: '<em>Tree</em>',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Kapcsolt kártya megnyitása',
                root: '<em>Ifc Root</em>'
            }
        },
        bulkactions: {
            abort: '<em>Abort selected items</em>',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                '<em>Some attachments will not be downloaded because they have not yet been saved to the server</em>',
            alertnoattachmentsdownload:
                '<em>No attachments will be downloaded because they have not yet been saved to the server</em>',
            cancelselection: '<em>Cancel selection</em>',
            confirmabort: '<em>You are aborting {0} process instances. Are you sure that you want to proceed?</em>',
            confirmdelete: '<em>You are deleting {0} cards. Are you sure that you want to proceed?</em>',
            confirmdeleteattachements:
                '<em>You are deleting {0} attachments. Are you sure that you want to proceed?</em>',
            confirmedit: '<em>You are modifying {0} for {1} cards. Are you sure that you want to proceed?</em>',
            delete: '<em>Delete selected items</em>',
            download: '<em>Download selected attachments</em>',
            downloadall: '<em>Download all attachments</em>',
            edit: '<em>Edit selected items</em>',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: '<em>Please select at least one attribute.</em>',
            selectall: '<em>Select all items</em>'
        },
        calendar: {
            active_expired: '<em>Active/Expired</em>',
            add: '<em>Add schedule</em>',
            advancenotification: '<em>Days advance notification</em>',
            allcategories: '<em>All categories</em>',
            alldates: '<em>All dates</em>',
            associatedcard: '<em>Associated card</em>',
            calculated: '<em>Calculated</em>',
            calendar: '<em>Calendar</em>',
            cancel: '<em>Mark as cancelled</em>',
            category: '<em>Category</em>',
            class: '<em>Class</em>',
            cm_confirmcancel: '<em>Are you sure you want to mark as cancelled selected schedules?</em>',
            cm_confirmcomplete: '<em>Are you sure that you want to mark as completed selected schedules?</em>',
            cm_markcancelled: '<em>Mark as cancelled selected schedules</em>',
            cm_markcomplete: '<em>Mark as completed selected schedules</em>',
            complete: '<em>Mark as done</em>',
            completed: '<em>Completed</em>',
            date: '<em>Date</em>',
            days: '<em>Days</em>',
            delaybeforedeadline: '<em>Delay before deadline</em>',
            delaybeforedeadlinevalue: '<em>Delay before deadline value</em>',
            description: '<em>Description</em>',
            editevent: '<em>Edit schedule</em>',
            enddate: '<em>End Date</em>',
            endtype: '<em>End type</em>',
            event: '<em>Event</em>',
            execapplyfilter: '<em>Can not execute applyAdvanceFilter because there are more than one stores</em>',
            execremovefilter: '<em>Can not execute removeAdvanceFilter because there are more than one stores</em>',
            executiondate: '<em>Execution Date</em>',
            frequency: '<em>Frequency</em>',
            frequencymultiplier: '<em>Frequency multiplier</em>',
            grid: '<em>Grid</em>',
            leftdays: '<em>Days to go</em>',
            londdescription: '<em>Full Description</em>',
            manual: '<em>Manual</em>',
            maxactiveevents: '<em>Max active schedules</em>',
            messagebodydelete: '<em>Would you like to remove schedulers rule?</em>',
            messagebodyplural: '<em>There are {0} schedule rules</em>',
            messagebodyrecalculate: '<em> Would you like to recalculate the schedules rule with the new date?</em>',
            messagebodysingular: '<em>There is {0} schedule rule</em>',
            messagetitle: '<em>Schedule recalculating</em>',
            missingdays: '<em>Missing days</em>',
            next30days: '<em>Next 30 days</em>',
            next7days: '<em>Next 7 days</em>',
            notificationtemplate: '<em>Template used for notification</em>',
            notificationtext: '<em>Notification text</em>',
            occurencies: '<em>Number of occurencies</em>',
            operation: '<em>Operation</em>',
            partecipantgroup: '<em>Participant group</em>',
            partecipantuser: '<em>Participant user</em>',
            priority: '<em>Priority</em>',
            recalculate: '<em>Recalculate</em>',
            referent: '<em>Referent</em>',
            scheduler: '<em>Scheduler</em>',
            sequencepaneltitle: '<em>Generate schedules</em>',
            startdate: '<em>Start Date</em>',
            status: '<em>Status</em>',
            today: '<em>Today</em>',
            type: '<em>Type</em>',
            viewevent: '<em>View schedule</em>',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: '<em>Calculation criterion</em>',
            widgetemails: '<em>Emails</em>',
            widgetgotocard: '<em>Go to source card</em>',
            widgetsourcecard: '<em>Source card</em>'
        },
        chat: {
            title: '<em>Chat</em>'
        },
        classes: {
            cards: {
                addcard: 'Kártya hozzáadása a következőhöz: ',
                clone: 'Másolás',
                clonewithrelations: '<em>Clone card and relations</em>',
                deletebeaware: '<em>Be aware that:</em>',
                deleteblocked:
                    '<em>It is not possible to proceed with the deletion because there are relations with {0}.</em>',
                deletecard: 'Kártya törlése',
                deleteconfirmation: '<em>Are you sure that you want to delete this card?</em>',
                deleterelatedcards: '<em>also {0} related cards will be deleted</em>',
                deleterelations: '<em>relations with {0} cards will be deleted</em>',
                label: 'Kártyák',
                modifycard: 'Kártya módosítása',
                opencard: '<em>Open card</em>',
                print: '<em>Print card</em>'
            },
            simple: 'Egyszerű',
            standard: 'Általános'
        },
        common: {
            actions: {
                add: 'Hozzáadás',
                apply: 'Alkalmaz',
                cancel: 'Mégse',
                clear: '<em>Clear</em>',
                close: 'Bezárás',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Törlés',
                edit: 'Szerkesztés',
                execute: '<em>Execute</em>',
                forward: '<em>Forward</em>',
                help: '<em>Help</em>',
                hide: '<em>Hide</em>',
                load: '<em>Load</em>',
                open: '<em>Open</em>',
                previous: '<em>Previous</em>',
                refresh: '<em>Refresh data</em>',
                remove: 'Eltávolítás',
                save: 'Mentés',
                saveandapply: 'Mentés és alkalmazás',
                saveandclose: '<em>Save and close</em>',
                search: '<em>Search</em>',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: '<em>Search...</em>'
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
                nogroup: '<em>Base data</em>'
            },
            dates: {
                date: '<em>d/m/Y</em>',
                datetime: '<em>d/m/Y H:i:s</em>',
                time: '<em>H:i:s</em>'
            },
            editor: {
                clearhtml: '<em>Clear HTML</em>',
                expand: '<em>Expand editor</em>',
                reduce: '<em>Reduce editor</em>',
                signature: '<em>Add signature</em>',
                unlink: '<em>Unlink</em>',
                unlinkmessage: '<em>Transform the selected hyperlink into text.</em>'
            },
            grid: {
                disablemultiselection: '<em>Disable multi selection</em>',
                enamblemultiselection: '<em>Enable multi selection</em>',
                export: '<em>Export data</em>',
                filterremoved: '<em>The current filter has been removed</em>',
                import: '<em>Import data</em>',
                itemnotfound: 'A tétel nem található',
                lastUpdate: '<em>Last update</em>',
                list: '<em>List</em>',
                opencontextualmenu: '<em>Open contextual menu</em>',
                print: 'Nyomtatás',
                printcsv: '<em>Print as CSV</em>',
                printmap: '<em>Print Map</em>',
                printodt: '<em>Print as ODT</em>',
                printpdf: '<em>Print as PDF</em>',
                row: '<em>Item</em>',
                rows: '<em>Items</em>',
                subtype: '<em>Subtype</em>',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Aktivitás',
                attachment: '<em>Attachment</em>',
                attachments: 'Csatolmányok',
                card: 'Kártya',
                clonerelationmode: '<em>Clone Relations Mode</em>',
                details: '<em>Details</em>',
                emails: '<em>Emails</em>',
                history: 'Történet',
                notes: 'Jegyzetek',
                relations: 'Relációk',
                schedules: '<em>Schedules</em>',
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
                gridhide: '<em>Hide data grid</em>',
                gridshow: '<em>Show data grid</em>',
                openinpopup: '<em>Open in popup</em>',
                reload: '<em>Reload</em>',
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
            addattachmentsfromdocumentarchive: '<em>Add attachments from the document archive</em>',
            alredyexistfile: '<em>Already exists a file with this name</em>',
            archivingdate: 'Archiválás dátuma',
            attachfile: 'Fájl csatolása',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Rejtett másolat',
            cc: 'Másolat',
            composeemail: 'E-mail írása',
            composefromtemplate: 'E-mail írása sablonból',
            delay: 'Késleltetés',
            delays: {
                day1: '1 nap alatt',
                days2: '2 nap alatt',
                days4: '4 nap alatt',
                hour1: '1 óra alatt',
                hours2: '2 óra alatt',
                hours4: '4 óra alatt',
                month1: '1 hónap alatt',
                negativeday1: '<em>1 day before</em>',
                negativedays2: '<em>2 days before</em>',
                negativedays4: '<em>4 days before</em>',
                negativehour1: '<em>1 hour before</em>',
                negativehours2: '<em>2 hours before</em>',
                negativehours4: '<em>4 hours before</em>',
                negativemonth1: '<em>1 month before</em>',
                negativeweek1: '<em>1 week before</em>',
                negativeweeks2: '<em>2 weeks before</em>',
                none: 'Nincs',
                week1: '1 hét alatt',
                weeks2: '2 hét alatt'
            },
            dmspaneltitle: 'Csatolmány választása adatbázisból',
            edit: 'Szerkesztés',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Tőle',
            gridrefresh: 'Rács frissítés',
            groupbystatus: '<em>Grouped by status</em>',
            keepsynchronization: 'Folyamatos sync',
            maxsize: '<em>The maximum total size allowed for attachments is {0}.</em>',
            message: '<em>Message</em>',
            regenerateallemails: 'Minden e-mail újragenerálása',
            regenerateemail: 'E-mail újragenerálása',
            reloadconfirmationmessage: '<em>E-mails in draft status will be deleted. Do you want to continue?</em>',
            remove: 'Eltávolítás',
            remove_confirmation: '<em>Are you sure that you want to delete this email?</em>',
            reply: 'Válasz',
            replyprefix: '<em>On {0}, {1} wrote:</em>',
            searchattachment: '<em>Search Attachment</em>',
            searchcard: '<em>Search card</em>',
            selectaclass: '<em>Select a class</em>',
            sendemail: '<em>Send e-mail</em>',
            sendingemail: '<em>Sendig email.</em>',
            signature: '<em>Signature</em>',
            size: '<em>Size</em>',
            status: '<em>Status</em>',
            statuses: {
                draft: 'Piszkozat',
                error: '<em>Error</em>',
                new: '<em>New</em>',
                outgoing: 'Kimenő',
                received: 'Kapott',
                sent: 'Elküldött',
                skipped: '<em>Skipped</em>'
            },
            subject: 'Tárgy',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Címzett',
            view: 'Nézet'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Hibás felhasználónév vagy jelszó',
            classnotfound: 'Az {0} osztály nem található',
            fieldrequired: '<em>This field is required</em>',
            invalidfilter: '<em>Invalid filter</em>',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'A tétel nem található',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: '<em>Actions</em>',
            addfilter: 'Szűrő hozzáadása',
            any: '<em>At least one</em>',
            attachments: '<em>Attachments</em>',
            attachmentssearchtext: '<em>Attachments search text</em>',
            attribute: 'Válasszon attribútumot',
            attributes: 'Attribútumok',
            clearfilter: 'Szűrő törlése',
            clone: 'Másolás',
            copyof: 'Amelyik másolata',
            currentgroup: '<em>Current group</em>',
            currentuser: '<em>Current user</em>',
            defaultset: '<em>Set as default</em>',
            defaultunset: '<em>Unset from default</em>',
            description: 'Leírás',
            domain: 'Domain',
            errorname: '<em>Filter name already in use</em>',
            filterdata: '<em>Filter data</em>',
            fromfilter: '<em>From filter</em>',
            fromselection: 'Szelektáltakból',
            group: '<em>Group</em>',
            ignore: '<em>Ignore</em>',
            migrate: '<em>Migrate</em>',
            name: 'Név',
            newfilter: '<em>New filter</em>',
            noone: 'Semmi',
            operator: '<em>Operator</em>',
            operators: {
                and: '<em>And</em>',
                beginswith: 'Amivel kezdődik',
                between: 'Között',
                contained: 'Tartalmaz',
                containedorequal: 'Tartalmaz vagy egyenlő',
                contains: 'Tartalmazza',
                containsorequal: 'Tartalmaz vagy egyenlő',
                descriptionbegin: '<em>Description begins with</em>',
                descriptioncontains: '<em>Description contains</em>',
                descriptionends: '<em>Description ends with</em>',
                descriptionnotbegin: '<em>Description does not begins with</em>',
                descriptionnotcontain: '<em>Description does not contain</em>',
                descriptionnotends: '<em>Description does not ends with</em>',
                different: 'Különböző',
                doesnotbeginwith: 'Amivel nem kezdődik ',
                doesnotcontain: 'Nem tartalmazza',
                doesnotendwith: 'Amivel nem fejeződik be',
                endswith: 'Amivel befejeződik',
                equals: 'Egyenlő',
                greaterthan: 'Nagyobb mint',
                isnotnull: 'Nem nulla',
                isnull: 'Nulla',
                lessthan: 'Kisebb mint',
                or: '<em>Or</em>'
            },
            relations: 'Relációk',
            removeattribute: '<em>Remove attribute</em>',
            removeblock: '<em>Remove block</em>',
            type: 'Típus',
            typeinput: 'Bemeneti paraméter',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: '<em>User</em>',
            value: 'Érték'
        },
        gis: {
            card: 'Kártya',
            cardsMenu: '<em>Cards Menu</em>',
            code: '<em>Code</em>',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: '<em>Description</em>',
            extension: {
                errorCall: '<em>Error</em>',
                noResults: '<em>No Results</em>'
            },
            externalServices: 'Külső szolgáltatás',
            geographicalAttributes: 'Földrajzi attribútumok',
            geoserverLayers: 'Geoserver rétegek',
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
            layers: 'Rétegek',
            layersTab: {
                clearPreferences: '<em>Clear layers preferences</em>',
                preferencesCleared: '<em>Layers preferences cleared</em>',
                preferencesSaved: '<em>Layers preferences saved</em>',
                savePreferences: '<em>Save layers preferences</em>',
                updatePreferences: '<em>Update layers preferences</em>'
            },
            list: 'Lista',
            longpresstitle: '<em>Geoelements in area</em>',
            map: 'Térkép',
            mapServices: '<em>Map Services</em>',
            menu: {
                font: '<em>Font size</em>',
                options: '<em>Options</em>'
            },
            position: '<em>Position</em>',
            root: 'Root',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Navigációs fa',
            type: '<em>Type</em>',
            view: 'Nézet',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Aktivitás neve',
            activityperformer: 'Aktivitás végzője',
            attributes: '<em>Attributes ({0})</em>',
            begindate: 'Kezdő dátum',
            enddate: 'Befejező dátum',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: '<em>Other relations</em>',
            processstatus: 'Státusz',
            referencerelation: '<em>Reference relations</em>',
            relation: '<em>Relation</em>',
            system: '<em>System</em>',
            type: '<em>Type</em>',
            user: 'Felhasználó',
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
                uri: '<em>Database URI</em>',
                user: '<em>Database user</em>'
            },
            downloadreport: '<em>Download report</em>',
            emailfailure: '<em>Error occurred while sending email!</em>',
            emailmessage: '<em>Attached import report of file "{0}" in date {1}</em>',
            emailsubject: '<em>Import data report</em>',
            emailsuccess: '<em>The email has been sent successfully!</em>',
            export: '<em>Export</em>',
            exportalldata: '<em>All data</em>',
            exportfiltereddata: '<em>Only data matching the grid filter</em>',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: '<em>The import of shapes is not enabled for this template</em>',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: '<em>Card</em>',
                project: '<em>Project</em>',
                sourcetype: '<em>Import from</em>'
            },
            import: '<em>Import</em>',
            importresponse: '<em>Import response</em>',
            response: {
                created: '<em>Created cards</em>',
                deleted: '<em>Deleted cards</em>',
                errors: '<em>Errors</em>',
                linenumber: '<em>Line number</em>',
                message: '<em>Message</em>',
                modified: '<em>Modified cards</em>',
                notprocessed: '<em>Unprocessed cards</em>',
                processed: '<em>Processed rows</em>',
                recordnumber: '<em>Record number</em>',
                unchanged: '<em>Unchanged cards</em>'
            },
            sendreport: '<em>Send report</em>',
            template: '<em>Template</em>',
            templatedefinition: '<em>Template definition</em>'
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
                login: 'Bejelentkezés',
                logout: '<em>Change user</em>'
            },
            fields: {
                group: '<em>Group</em>',
                language: 'Nyelv',
                password: 'Jelszó',
                tenants: '<em>Tenants</em>',
                username: 'Felhasználónév'
            },
            loggedin: '<em>Logged in</em>',
            sso: {
                loginfailure: '<em>Login failure</em>',
                loginwith: '<em>Login with {0}</em>',
                or: '<em>or</em>',
                usernotenabled: '<em>User not enabled</em>'
            },
            title: 'Bejelentkezés',
            welcome: '<em>Welcome back {0}.</em>'
        },
        main: {
            administrationmodule: 'Adminisztrációs modul',
            alerts: '<em>Alerts</em>',
            baseconfiguration: '<em>Base configuration</em>',
            cardlock: {
                lockedmessage: "<em>You can't edit this card because {0} is editing it.</em>",
                someone: '<em>someone</em>'
            },
            changegroup: '<em>Change group</em>',
            changetenant: '<em>Change {0}</em>',
            confirmchangegroup: '<em>Are you sure you want to change the group?</em>',
            confirmchangetenants: '<em>Are you sure you want to change active tenants?</em>',
            confirmdisabletenant: '<em>Are you sure you want to disable "Ignore tenants" flag?</em>',
            confirmenabletenant: '<em>Are you sure you want to enable "Ignore tenants" flag?</em>',
            description: '<em>Description</em>',
            ignoretenants: '<em>Ignore {0}</em>',
            info: 'Információ',
            logo: {
                cmdbuild: '<em>CMDBuild logo</em>',
                cmdbuildready2use: '<em>CMDBuild READY2USE logo</em>',
                companylogo: '<em>Company logo</em>',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: '<em>openMAINT logo</em>'
            },
            logout: 'Kijelentkezés',
            managementmodule: 'Adatkezelő modul',
            multigroup: 'Több csoport',
            multitenant: '<em>Multi {0}</em>',
            navigation: 'Navigation',
            pagenotfound: '<em>Page not found</em>',
            password: {
                change: 'Jelszó változtatás',
                confirm: 'Jelszó megerősítése',
                email: '<em>E-mail address</em>',
                err_confirm: "<em>Password doesn't match.</em>",
                err_diffprevious: '<em>The password cannot be identical to the previous one.</em>',
                err_diffusername: '<em>The password cannot be identical to the username.</em>',
                err_length: '<em>The password must be at least {0} characters long.</em>',
                err_reqdigit: '<em>The password must contain at least one digit.</em>',
                err_reqlowercase: '<em>The password must contain at least one lowercase character.</em>',
                err_requppercase: '<em>The password must contain at least one uppercase character.</em>',
                expired: '<em>Your password has expired. You must change it now.</em>',
                forgotten: '<em>I forgot my password</em>',
                new: 'Új jelszó',
                old: 'Eddigi jelszó',
                recoverysuccess: '<em>We have sent you an email with instruction to recover your password.</em>',
                reset: '<em>Reset password</em>',
                saved: '<em>Password correctly saved!</em>'
            },
            pleasecorrecterrors: '<em>Please correct indicated errors!</em>',
            preferences: {
                comma: '<em>Comma</em>',
                decimalserror: '<em>Decimals field must be present</em>',
                decimalstousandserror: '<em>Decimals and Thousands separato must be differents</em>',
                default: '<em>Default</em>',
                defaultvalue: 'Alapértelmezett érték',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: '<em>First day of week</em>',
                gridpreferencesclear: '<em>Clear grid preferences</em>',
                gridpreferencescleared: '<em>Grid preferences cleared!</em>',
                gridpreferencessave: '<em>Save grid preferences</em>',
                gridpreferencessaved: '<em>Grid preferences saved!</em>',
                gridpreferencesupdate: '<em>Update grid preferences</em>',
                groupemailsbystatus: '<em>Show emails grouped by status</em>',
                labelcsvseparator: '<em>CSV separator</em>',
                labeldateformat: '<em>Date format</em>',
                labeldecimalsseparator: '<em>Decimals separator</em>',
                labellanguage: '<em>Language</em>',
                labelthousandsseparator: '<em>Thousands separator</em>',
                labeltimeformat: '<em>Time format</em>',
                msoffice: '<em>Microsoft Office</em>',
                no: '<em>No</em>',
                notificationslabel: '<em>Chat & Notifications</em>',
                notificationssound: '<em>Play sound for notifications</em>',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: '<em>Period</em>',
                preferredfilecharset: '<em>CSV encoding</em>',
                preferredofficesuite: '<em>Preferred Office suite</em>',
                sendcancellationperiod: '<em>Email send cancellation period (seconds)</em>',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: '<em>Space</em>',
                thousandserror: '<em>Thousands field must be present</em>',
                timezone: '<em>Timezone</em>',
                twelvehourformat: '<em>12-hour format</em>',
                twentyfourhourformat: '<em>24-hour format</em>',
                yes: '<em>Yes</em>'
            },
            searchinallitems: '<em>Search in all items</em>',
            searchtenant: '<em>Search tenant</em>',
            selectdeselettenants: '<em>Select/Deselect all</em>',
            treenavcontenttitle: '<em>{0} of {1}</em>',
            userpreferences: '<em>Preferences</em>'
        },
        menu: {
            allitems: '<em>All items</em>',
            classes: 'Osztályok',
            custompages: '<em>Custom pages</em>',
            dashboards: '<em>Dashboards</em>',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: 'Folyamatok',
            reports: '<em>Reports</em>',
            setasinitialpage: '<em>Set as initial page</em>',
            views: 'Nézetek'
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
            edit: 'Jegyzet szerkesztése'
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
            attention: 'Figyelem',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Hiba',
            genericerror: '<em>Generic error</em>',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: '<em>Generic info</em>',
            genericwarning: '<em>Generic warning</em>',
            info: 'Információ',
            more: '<em>More info</em>',
            success: '<em>Success</em>',
            warning: '<em>Warning</em>'
        },
        patches: {
            apply: '<em>Apply patches</em>',
            category: '<em>Category</em>',
            description: '<em>Description</em>',
            name: '<em>Name</em>',
            patches: '<em>Patches</em>'
        },
        processes: {
            abortconfirmation: 'Biztos hogy meg akarja szakítani a folyamatot?',
            abortprocess: 'Folyamat megszakítása',
            action: {
                advance: 'Tovább',
                label: '<em>Action</em>'
            },
            activeprocesses: '<em>Active processes</em>',
            allstatuses: '<em>All</em>',
            editactivity: 'Aktivitás szerkesztése',
            openactivity: '<em>Open activity</em>',
            resumeprocesses: '<em>Processes that are not suspended cannot be resumed</em>',
            startworkflow: 'Indítás',
            workflow: 'Folyamat'
        },
        relationGraph: {
            activity: '<em>activity</em>',
            allLabelsOnGraph: '<em>all labels on graph</em>',
            card: 'Kártya',
            cardList: '<em>Card List</em>',
            cardRelations: 'Reláció',
            choosenaviagationtree: '<em>Choose navigation tree</em>',
            class: '<em>Class</em>',
            classList: '<em>Class List</em>',
            compoundnode: '<em>Compound Node</em>',
            disable: '<em>Disable</em>',
            edges: '<em>Edges</em>',
            enable: '<em>Enable</em>',
            labelsOnGraph: '<em>tooltip on graph</em>',
            level: '<em>Level</em>',
            nodes: '<em>Nodes</em>',
            openRelationGraph: 'Relációs gráf megnyitása',
            quantity: '<em>Quantity</em>',
            refresh: '<em>Refresh</em>',
            relation: 'Reláció',
            relationGraph: 'Relációs gráf',
            reopengraph: '<em>Reopen the graph from this node</em>'
        },
        relations: {
            adddetail: 'Részlet hozzáadása',
            addrelations: 'Reláció hozzáadása',
            attributes: 'Attribútumok',
            begindate: '<em>Begin date</em>',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: '<em>Code</em>',
            deletedetail: 'Részlet törlése',
            deleterelation: 'Reláció törlése',
            deleterelationconfirm: '<em>Are you sure you want to delete this relation?</em>',
            description: 'Leírás',
            editcard: 'Kártya módosítása',
            editdetail: 'Részlet szerkesztése',
            editrelation: 'Reláció szerkesztése',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: '<em>Extended data</em>',
            mditems: '<em>items</em>',
            missingattributes: '<em>Missing mandatory attributes</em>',
            opencard: 'Kapcsolt kártya megnyitása',
            opendetail: 'Részlet megjelenítése',
            relationdata: '<em>Relation data</em>',
            type: 'Típus'
        },
        reports: {
            csv: '<em>CSV</em>',
            download: 'Letöltés',
            format: 'Formátum',
            msgs: {
                creationerror: '<em>Something went wrong during the start of the report creation.</em>',
                creationstarted:
                    '<em>Report creation started. You will be notified when it will be ready for the download.</em>'
            },
            odt: '<em>ODT</em>',
            pdf: '<em>PDF</em>',
            print: 'Nyomtatás',
            reload: 'Újratöltés',
            rtf: '<em>RTF</em>'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: '<em>Default</em>'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: '<em>Daily</em>'
                        },
                        monthly: {
                            description: '<em>Monthly</em>'
                        },
                        once: {
                            description: '<em>Once</em>'
                        },
                        weekly: {
                            description: '<em>Weekly</em>'
                        },
                        yearly: {
                            description: '<em>Yearly</em>'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: '<em>Default</em>'
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
            addThematism: '<em>Add Thematism</em>',
            analysisType: '<em>Analysis Type</em>',
            attribute: '<em>Attribute</em>',
            bordersIntervals: '<em>Wrong configuration for intervals limits</em>',
            calculateRules: '<em>Generate style rules</em>',
            clearThematism: '<em>Clear Thematism</em>',
            color: '<em>Color</em>',
            defineLegend: '<em>Legend definition</em>',
            defineThematism: '<em>Thematism definition</em>',
            deletethematism: '<em>Are you sure that you want to delete this thematism?</em>',
            editThematism: '<em>Edit rules configuration</em>',
            function: '<em>Function</em>',
            generate: '<em>generate</em>',
            geoAttribute: '<em>Geographic Attribute</em>',
            graduated: '<em>Graduated</em>',
            highlightSelected: '<em>Highlight selected item</em>',
            intervals: '<em>Intervals</em>',
            legend: '<em>legend</em>',
            name: '<em>name</em>',
            newThematism: '<em>New Thematism</em>',
            punctual: '<em>Punctual</em>',
            quantity: '<em>Quantity</em>',
            segments: '<em>Segments</em>',
            source: '<em>Source</em>',
            table: '<em>Table</em>',
            thematism: '<em>Thematisms</em>',
            value: '<em>Value</em>'
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
                addrow: 'Sor hozzáadása',
                clonerow: '<em>Clone row</em>',
                datanotvalid: '<em>Data not valid</em>',
                deleterow: 'Sor törlése',
                editrow: 'Sor szerkesztése',
                export: 'Exportálás',
                import: '<em>Import</em>',
                importexport: {
                    expattributes: '<em>Data to export</em>',
                    file: '<em>File</em>',
                    filename: '<em>File name</em>',
                    format: '<em>Format</em>',
                    importmode: '<em>Import mode</em>',
                    keyattributes: '<em>Key attributes</em>',
                    missingkeyattr: '<em>Please choose at least one key attribute</em>',
                    modeadd: '<em>Add</em>',
                    modemerge: '<em>Merge</em>',
                    modereplace: '<em>Replace</em>',
                    separator: '<em>Separator</em>'
                },
                refresh: '<em>Refresh to defaults</em>'
            },
            linkcards: {
                checkedonly: '<em>Checked only</em>',
                editcard: '<em>Edit card</em>',
                opencard: '<em>Open card</em>',
                refreshselection: '<em>Apply default selection</em>',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Rács szűrő tiltása',
                togglefilterenabled: 'Rács szűrő engedélyezése'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                '<em>Inline mode is not supported for this widget type. Please fix the widget configuration.</em>',
            required: '<em>This widget is required.</em>'
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
