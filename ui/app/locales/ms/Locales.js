(function () {
    Ext.define('CMDBuildUI.locales.ms.Locales', {
        requires: ['CMDBuildUI.locales.ms.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ms',
        administration: CMDBuildUI.locales.ms.LocalesAdministration.administration,
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
            add: 'Tambah lampiran',
            attachmenthistory: 'Sejarah lampiran',
            author: 'Pengarang',
            authordescription: 'Penerangan pengarang',
            browse: 'Layari &hellip;',
            category: 'Kategori',
            code: 'Kod',
            creationdate: 'Tarikh Dibuat',
            deleteattachment: 'Padam lampiran',
            deleteattachment_confirmation: 'Adakah anda pasti mahu memadamkan lampiran ini?',
            description: 'Keterangan',
            download: 'Muat turun',
            dropfiles: 'Letakkan fail di sini',
            editattachment: 'Ubat suai lampiran',
            errordms: 'DMS telah dinyahaktifkan dan mustahil untuk simpan fail-fail',
            file: 'Fail',
            filealreadyinlist: 'Fail {0} sudah ada dalam senarai.',
            filename: 'Nama fail',
            fileview: 'Lihat lampiran',
            invalidfiles: 'Buang fail yang tidak sah',
            majorversion: 'Versi utama',
            maxsize: 'Saiz fail maksimum yang dibenarkan ialah {0} MB.',
            metadata: 'Metadata',
            modificationdate: 'Tarikh ubah suai',
            new: 'Lampuran baru',
            nocategory: 'Tidak dikategorikan',
            preview: 'Pratonton',
            removefile: 'Buang fail',
            statuses: {
                empty: 'Fail kosong',
                error: 'Ralat',
                extensionNotAllowed: 'Fail lanjutan tidak dibenarkan',
                loaded: 'Dimuatkan',
                ready: 'Sedia',
                toolarge: 'Terlalu besar'
            },
            successupload: '{0} lampiran dimuat naik.',
            uploadfile: 'Muatnaik fail...',
            version: 'Versi',
            viewhistory: 'Lihat sejarah lampiran',
            viewmetadata: 'Lihat lampiran metadata',
            warningmessages: {
                atleast:
                    '{0}: {1} lampiran jenis "{2}" telah dimuatkan. Kategori ini mengharapkan sekurang-kurangnya {3} lampiran.',
                exactlynumber: '{0}: {1} lampiran jenis "{2}" ada',
                maxnumber:
                    '{0}: {1} lampiran jenis "{2}" telah dimuatkan. Kategori ini menjangkakan paling banyak {3} lampiran.'
            },
            wrongfileextension: '{0} lanjutan fail tidak dibenarkan'
        },
        bim: {
            bimViewer: 'Paparan Bim',
            card: {
                label: 'Kad'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Saiz kotak terikat sepanjang paksi X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Saiz kotak terikat sepanjang paksi Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Saiz kotak terikat sepanjang paksi Z',
                calculated: 'Dikira',
                description: 'Penerangan',
                guid: 'GUID',
                label: 'Ciri-ciri',
                name: 'Nama',
                type: 'Jenis',
                uuid: 'UUID'
            },
            layers: {
                label: 'Lapisan-lapisan',
                menu: {
                    hideAll: 'Sembunyi Semua',
                    hideAllLayers: 'Sembunyikan semua lapisan',
                    hideAllXeokit: 'Sembunyikan semua {0}',
                    showAll: 'Tunjuk Semua',
                    showAllLayers: 'Tunjukkan semua lapisan',
                    showAllXeokit: 'Tunjukkan semua {0}'
                },
                name: 'Nama',
                quantity: 'Kuantiti',
                visibility: 'Keterlihatan'
            },
            menu: {
                camera: 'Kamera',
                disableslice: 'Nyahaktifkan objek hirisan',
                enableslice: 'Aktifkan objek hirisan',
                frontView: 'Padangan Depan',
                mod: 'Kawalan pandangan',
                ortho: 'Tukar kepada pandangan ortogon',
                orthographic: 'Kamera Ortografi',
                pan: 'Tatal',
                panXeokit: 'Pemapar Kawalan: tatal (Shift)',
                perspe: 'Tukar kepada pandangan perspektif',
                perspective: 'Kamera Perspektif',
                resetSlice: 'Tetapkan semula slice',
                resetView: 'Tetapan semula pandangan',
                rotate: 'Putar',
                rotateXeokit: 'Pemapar Kawalan: putar',
                save: 'Simpan paparan',
                shortcuts: {
                    down: 'Putar ke bawah',
                    left: 'Putar ke kiri',
                    right: 'Putar ke kanan',
                    title: 'Pintasan papan kekunci',
                    up: 'Putar ke atas',
                    zoomin: 'Zum masuk',
                    zoomout: 'Zum keluar'
                },
                sideView: 'Padangan Sisi',
                threeD: 'Tukar kepada paparan 3D',
                topView: 'Padangan Atas',
                twoD: 'Tukar kepada paparan 2D'
            },
            navCube: {
                back: 'Belakang',
                bottom: 'Bawah',
                front: 'Depan',
                left: 'Kiri',
                right: 'Kanan',
                top: 'Atas'
            },
            showBimCard: 'Buka paparan 3D',
            tree: {
                arrowTooltip: 'Pilih elemen',
                label: 'Cabang',
                multipleElements: 'Pilih elemen-elemen',
                open_card: 'Buka kad berkaitan',
                root: 'Akar Ifc'
            }
        },
        bulkactions: {
            abort: 'Batalkan item yang dipilih',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                'Beberapa lampiran tidak akan dimuat turun kerana masih belum disimpan ke pelayan',
            alertnoattachmentsdownload:
                'Tidak ada lampiran yang tidak akan dimuat turun kerana belum disimpan ke pelayan',
            cancelselection: 'Batalkan pilihan',
            confirmabort: 'Anda membatalkan {0} contoh proses.',
            confirmdelete: 'Anda memadamkan {0} kad. Adakah anda pasti mahu meneruskan?',
            confirmdeleteattachements: 'Anda memadamkan {0} lampiran. Adakah anda pasti mahu meneruskan?',
            confirmedit: 'Anda mengubah suai kad {0} pada {1}. Adakah anda pasti mahu meneruskan?',
            delete: 'Padamkan item yang dipilih',
            download: 'Muat turun lampiran terpilih',
            downloadall: 'Muat turun semua lampiran',
            edit: 'Ubah item yang dipilih',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: 'Sila pilih sekurang-kurangnya satu atribut.',
            selectall: 'Pilih semua item'
        },
        calendar: {
            active_expired: 'Aktif / Tamat Tempoh',
            add: 'Tambah',
            advancenotification: 'Pemberitahuan Awal',
            allcategories: 'Semua kategori',
            alldates: 'Semua Tarikh',
            associatedcard: 'Kad bersekutu',
            calculated: 'Dikira',
            calendar: 'Kalendar',
            cancel: 'Tandakan sebagai dibatalkan',
            category: 'Kategori',
            class: 'Kelas',
            cm_confirmcancel: 'Adakah anda pasti mahu menandakan sebagai jadual terpilih yang dibatalkan?',
            cm_confirmcomplete: 'Adakah anda pasti mahu menandakan sebagai jadual terpilih yang telah selesai?',
            cm_markcancelled: 'Tandakan sebagai jadual terpilih yang dibatalkan',
            cm_markcomplete: 'Tandakan sebagai jadual terpilih yang telah siap',
            complete: 'Lengkap',
            completed: 'Selesai',
            date: 'Tarikh',
            days: 'Hari',
            delaybeforedeadline: 'Kelewatan sebelum tarikh akhir',
            delaybeforedeadlinevalue: 'Kelewatan sebelum nilai tarikh akhir',
            description: 'Keterangan',
            editevent: 'Ubah jadual',
            enddate: 'Tarikh Akhir',
            endtype: 'Jenis akhir',
            event: 'Jadual',
            execapplyfilter: 'Tidak dapat melaksanakan applyAdvanceFilter kerana terdapat lebih daripada satu tempat',
            execremovefilter: 'Tidak dapat melaksanakan removeAdvanceFilter kerana terdapat lebih daripada satu',
            executiondate: 'Tarikh Pelaksanaan',
            frequency: 'Kekerapan',
            frequencymultiplier: 'Pengganda frekuensi',
            grid: 'Grid',
            leftdays: 'Beberapa hari lagi',
            londdescription: 'Keterangan Penuh',
            manual: 'manual',
            maxactiveevents: 'Acara aktif Maxima',
            messagebodydelete: 'Adakah anda mahu mengalih keluar peraturan penjadual?',
            messagebodyplural: 'Terdapat {0} peraturan jadual',
            messagebodyrecalculate: 'Adakah anda ingin mengira semula peraturan jadual dengan tarikh baru?',
            messagebodysingular: 'Terdapat {0} peraturan jadual',
            messagetitle: 'Jadual semula',
            missingdays: 'Hari-hari yang hilang',
            next30days: '30 hari seterusnya',
            next7days: '7 hari seterusnya',
            notificationtemplate: 'Templat digunakan untuk pemberitahuan',
            notificationtext: 'Teks pemberitahuan',
            occurencies: 'Bilangan kejadian',
            operation: 'Operasi',
            partecipantgroup: 'Kumpulan peserta',
            partecipantuser: 'Pengguna peserta',
            priority: 'Keutamaan',
            recalculate: 'Kiraan semula',
            referent: 'Rujukan',
            scheduler: 'Penjadual',
            sequencepaneltitle: 'Menjana jadual',
            startdate: 'Tarikh Mula',
            status: 'Taraf',
            today: 'Hari ini',
            type: 'Jenis',
            viewevent: 'Lihat jadual',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Kriteria pengiraan',
            widgetemails: 'Emel',
            widgetgotocard: 'Pergi kepada kad sumber',
            widgetsourcecard: 'Kad Sumber'
        },
        chat: {
            title: 'Sembang'
        },
        classes: {
            cards: {
                addcard: 'Tambah kad',
                clone: 'Klon',
                clonewithrelations: 'Klon Kad dan hubungan',
                deletebeaware: 'Ketahuilah bahawa:',
                deleteblocked: 'Tidak mungkin untuk meneruskan penghapusan kerana terdapat hubungan dengan {0}.',
                deletecard: 'Padam kad',
                deleteconfirmation: 'Adakah anda pasti mahu padamkan kad ini?',
                deleterelatedcards: 'juga {0} kad yang berkaitan akan dipadamkan',
                deleterelations: 'hubungan dengan {0} kad akan dipadamkan',
                label: 'Kad',
                modifycard: 'Ubah suai kad',
                opencard: 'Buka kad',
                print: 'Cetak kad'
            },
            simple: 'Mudah',
            standard: 'Piawaian'
        },
        common: {
            actions: {
                add: 'Tambah',
                apply: 'Guna',
                cancel: 'Batal',
                clear: 'Kosongkan',
                close: 'Tutup',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Padam',
                edit: 'Ubah',
                execute: 'Laksana',
                forward: '<em>Forward</em>',
                help: 'Bantuan',
                hide: '<em>Hide</em>',
                load: 'Muatnaik',
                open: 'Buka',
                previous: '<em>Previous</em>',
                refresh: 'Pembaharuan Data',
                remove: 'Buang',
                save: 'Simpan',
                saveandapply: 'Simpan dan guna',
                saveandclose: 'Simpan dan tutup',
                search: 'Cari',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Cari...'
            },
            and: 'dan',
            attributes: {
                boolfalse: 'salah',
                booltrue: 'benar',
                link: {
                    errlabel: 'Label diperlukan',
                    errurl: 'Url diperlukan apabila label tidak kosong',
                    label: 'Label',
                    url: 'Url'
                },
                nogroup: 'Data Asal'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Kosongkan HTML',
                expand: 'Buka Editor',
                reduce: 'Tutup Editor',
                signature: 'Tambah tanda tangan',
                unlink: 'Nyahpaut',
                unlinkmessage: 'Ubah pautan terpilih menjadi'
            },
            grid: {
                disablemultiselection: 'Nyahaktifkan pelbagai pilihan',
                enamblemultiselection: 'Aktifkan pelbagai pemilihan',
                export: 'Eksport Data',
                filterremoved: 'Penapis semasa telah dikeluarkan',
                import: 'Import data',
                itemnotfound: 'Item tidak dijumpai',
                lastUpdate: '<em>Last update</em>',
                list: 'Senarai',
                opencontextualmenu: 'Buka menu kontekstual',
                print: 'Cetak',
                printcsv: 'Cetak sebagai CSV',
                printmap: 'Cetak Peta',
                printodt: 'Cetak sebagai ODT',
                printpdf: 'Cetak sebagai PDF',
                row: 'Item',
                rows: 'Item-item',
                subtype: 'Subjenis',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Aktiviti',
                attachment: 'Lampiran',
                attachments: 'Lampiran-lampiran',
                card: 'Kad',
                clonerelationmode: 'Mod Hubungan Klon',
                details: 'Perincian',
                emails: 'Emel',
                history: 'Sejarah',
                notes: 'Nota',
                relations: 'Hubungan',
                schedules: 'Jadual-jadual',
                sources: 'Sumber-sumber'
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
                download: 'Muat turun carta',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Sembunyikan grid data',
                gridshow: 'Tunjukkan grid data',
                openinpopup: 'Buka dalam pop-timbul',
                reload: 'Tambah semula',
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
            abort: 'Batal',
            addattachmentsfromdocumentarchive: 'Tambahkan lampiran dari arkib dokumen',
            alredyexistfile: 'Sudah ada fail dengan nama ini',
            archivingdate: 'Tarikh pengarkiban',
            attachfile: 'Lampirkan fail',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Karang e-mel',
            composefromtemplate: 'Karang daripada templat',
            delay: 'Tangguh',
            delays: {
                day1: 'Dalam masa 1 hari',
                days2: 'Dalam masa 2 hari',
                days4: 'Dalam masa 4 hari',
                hour1: '1 jam',
                hours2: '2 jam',
                hours4: '4 jam',
                month1: 'Dalam masa 1 bulan',
                negativeday1: '1 hari sebelum',
                negativedays2: '2 hari sebelum',
                negativedays4: '4 hari sebelum',
                negativehour1: '1 jam sebelum',
                negativehours2: '2 jam sebelum',
                negativehours4: '4 jam sebelum',
                negativemonth1: '1 bulan sebelum',
                negativeweek1: '1 minggu sebelum',
                negativeweeks2: '2 minggu sebelum',
                none: 'Tiada',
                week1: 'Dalam masa 1 minggu',
                weeks2: 'Dalam masa 2 minggu'
            },
            dmspaneltitle: 'Pilih lampiran dari Pangkalan Data',
            edit: 'Ubah',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Dari',
            gridrefresh: 'Memulihkan grid',
            groupbystatus: 'Dikelompokkan mengikut status',
            keepsynchronization: 'Simpan penyegerakan',
            maxsize: 'Jumlah saiz maksimum yang dibenarkan untuk lampiran ialah {0}.',
            message: 'Pesanan',
            regenerateallemails: 'Menjana semula semua e-mel',
            regenerateemail: 'Menjana semula e-mel',
            reloadconfirmationmessage: 'E-mel dalam status draf akan dipadamkan. Adakah anda mahu meneruskan?',
            remove: 'Buang',
            remove_confirmation: 'Adakah anda pasti mahu memadam e-mel ini?',
            reply: 'balas',
            replyprefix: 'On {0}, {1} wrote:',
            searchattachment: 'Cari lampiran',
            searchcard: 'Cari Kad',
            selectaclass: 'Pilih kelas',
            sendemail: 'Hantar e-mel',
            sendingemail: 'Hantar emel',
            signature: 'Tandatangan',
            size: 'Saiz',
            status: 'Status',
            statuses: {
                draft: 'Rangka',
                error: 'Ralat',
                new: 'Baru',
                outgoing: 'Menghantar',
                received: 'Diterima',
                sent: 'Dihantar',
                skipped: 'Dilangkau'
            },
            subject: 'Tajuk',
            templateattachments: 'E-mel ini mengandungi lampiran yang dimuat naik secara automatik oleh templat',
            to: 'Kepada',
            view: 'Lihat'
        },
        errors: {
            actionnotallowed: 'Tindakan tidak dibenarkan',
            autherror: 'Nama pengguna atau kata laluan salah',
            classnotfound: 'Kelas {0} tidak dijumpai',
            fieldrequired: 'Ruang ini perlu diisi',
            invalidfilter: 'Penapis tidak sah',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Item tidak dijumpai',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Tindakan',
            addfilter: 'Tambah penapis',
            any: '<em>At least one</em>',
            attachments: 'Lampiran',
            attachmentssearchtext: 'Teks carian lampiran',
            attribute: 'Pilih sifat',
            attributes: 'Sifat-sifat',
            clearfilter: 'Kosongkan penapis',
            clone: 'Klon',
            copyof: 'Salinan dari',
            currentgroup: 'Kumpulan Sedia ada',
            currentuser: 'Pengguna Sedia ada',
            defaultset: 'Tetapkan ke asal',
            defaultunset: 'Ubah dari asal',
            description: 'Keterangan',
            domain: 'Domain',
            errorname: 'Nama penapis sudah digunakan',
            filterdata: 'Tapis data',
            fromfilter: 'Dari penapis',
            fromselection: 'Pilih dari',
            group: 'Kumpulan',
            ignore: 'Abai',
            migrate: 'Pindah',
            name: 'Nama',
            newfilter: 'Penapis baru',
            noone: 'Tiada sesiapa',
            operator: 'Pengendali',
            operators: {
                and: 'Dan',
                beginswith: 'Mula dengan',
                between: 'Antara',
                contained: 'Terkawal',
                containedorequal: 'Terkawal atau sama',
                contains: 'Mengandungi',
                containsorequal: 'Mengandungi atau sama',
                descriptionbegin: 'Penerangan bermula dengan',
                descriptioncontains: 'Penerangan mengandungi',
                descriptionends: 'Penerangan berakhir dengan',
                descriptionnotbegin: 'Penerangan tidak bermula dengan',
                descriptionnotcontain: 'Penerangan tidak mengandungi',
                descriptionnotends: 'Penerangan tidak berakhir dengan',
                different: 'Berbeza',
                doesnotbeginwith: 'Tidak bermula dengan',
                doesnotcontain: 'Tidak mengandungi',
                doesnotendwith: 'Tidak berakhir dengan',
                endswith: 'Berakhir dengan',
                equals: 'Sama',
                greaterthan: 'Lebih besar dari',
                isnotnull: 'Bukan nol',
                isnull: 'Nol',
                lessthan: 'Lebih kecil dari',
                or: 'Atau'
            },
            relations: 'Hubungan',
            removeattribute: 'Buang Sifat',
            removeblock: 'Buang blok',
            type: 'Jenis',
            typeinput: 'Masukkan tetapan',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Pengguna',
            value: 'Nilai'
        },
        gis: {
            card: 'Kad',
            cardsMenu: 'Menu Kad',
            code: 'Kod',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Keterangan',
            extension: {
                errorCall: 'Ralat',
                noResults: 'Tiada keputusan'
            },
            externalServices: 'Khidmat luaran',
            geographicalAttributes: 'Sifat Geo',
            geoserverLayers: 'Lapisan Geoserver',
            labels: {
                all: 'Semua',
                alltitle: 'Tunjukkan semua label',
                alltitleclass: 'Tunjukkan semua label kelas',
                clear: '<em>Clear search</em>',
                hidden: 'Tersembunyi',
                hiddentitle: 'Sembunyikan label',
                hideinfowindow: 'Sembunyikan tetingkap maklumat',
                label: 'Label',
                search: '<em>Search on the map</em>',
                selected: 'Dipilih',
                selectedtitle: 'Tunjukkan label untuk item yang dipilih',
                showinfowindow: 'Tunjukkan tetingkap maklumat'
            },
            layers: 'Lapisan',
            layersTab: {
                clearPreferences: 'Kosongkan pilihan lapisan',
                preferencesCleared: 'Pilihan lapisan dikosongkan',
                preferencesSaved: 'Pilihan lapisan disimpan',
                savePreferences: 'Simpan pilihan lapisan',
                updatePreferences: 'Kemas kini pilihan lapisan'
            },
            list: 'Senarai',
            longpresstitle: 'Geoelements di kawasan',
            map: 'Peta',
            mapServices: 'Khidmat Peta',
            menu: {
                font: 'Saiz huruf',
                options: 'Pilihan'
            },
            position: 'Kedudukan',
            root: 'Akar',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Cabang',
            type: 'Jenis',
            view: 'Lihat',
            zoom: 'Zum'
        },
        history: {
            activityname: 'Nama aktiviti',
            activityperformer: 'Pelaku aktiviti',
            attributes: 'Sifat-sifat ({0})',
            begindate: 'Tarikh mula',
            enddate: 'Tarikh akhir',
            fieldname: 'Nama medan',
            fulldata: 'Data penuh',
            onlychanges: 'Hanya perubahan',
            otherrelations: 'Hubungan-hubungan lain',
            processstatus: 'Status',
            referencerelation: 'Hubungan-hubungan rujukan',
            relation: 'Hubungan',
            system: 'Sistem',
            type: 'Jenis',
            user: 'Pengguna',
            userdescription: 'Penerangan pengguna',
            value: 'Nilai',
            viewmode: 'Mod Papar'
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
                uri: 'Pangkalan data URI',
                user: 'Pangkalan data pengguna'
            },
            downloadreport: 'Muat turun laporan',
            emailfailure: 'Ralat berlaku semasa menghantar e-mel!',
            emailmessage: 'Melampirkan laporan import fail "{0}" dalam tarikh {1}',
            emailsubject: 'Import laporan data',
            emailsuccess: 'E-mel telah berjaya dihantar!',
            export: 'Eksport',
            exportalldata: 'Semua maklumat',
            exportfiltereddata: 'Hanya data yang sepadan dengan penapis grid',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Import bentuk tidak diaktifkan untuk templat ini',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: 'Kad',
                project: 'Projek',
                sourcetype: 'Impot dari'
            },
            import: 'Import',
            importresponse: 'Maklum balas import',
            response: {
                created: 'Kad yang dibuat',
                deleted: 'Kad yang dipadamkan',
                errors: 'Ralat',
                linenumber: 'nombor baris',
                message: 'Pesanan',
                modified: 'Kad yang diubah suai',
                notprocessed: 'Kad yang belum diproses',
                processed: 'Baris diproses',
                recordnumber: 'nombor rekod',
                unchanged: 'Kad tidak berubah'
            },
            sendreport: 'Hantar laporan',
            template: 'Templat',
            templatedefinition: 'Definisi templat'
        },
        joinviews: {
            active: 'Aktif',
            addview: 'Tambah paparan',
            alias: 'Nama samaran',
            attribute: 'Sifat',
            attributecustomization: 'Penyesuaian sifat-sifat',
            attributes: 'Sifat-sifat',
            attributeschoice: 'Sifat-sifat pilihan',
            attributesof: 'Sifat-sifat dari: {0}',
            createview: 'Buat paparan',
            datasorting: 'Penyusun data',
            delete: 'Padam',
            deleteview: 'Padam paparan',
            deleteviewconfirm: 'Adakah anda pasti mahu memadamkan paparan ini?',
            description: 'Keterangan',
            disable: 'Nyahaktifkan',
            domainalias: 'Nama samaran Domain',
            domainsof: 'Kawasan dari {0}',
            edit: 'Ubah',
            editview: 'Ubah konfigurasi paparan',
            enable: 'Aktifkan',
            fieldsets: 'Kumpulan medan',
            filters: 'Tapisan',
            generalproperties: 'Ciri-ciri umum',
            group: 'Kumpulan',
            innerjoin: 'Bergabung dalaman',
            jointype: 'Jenis Gabungan',
            joinview: 'Paparan dari gabungan',
            klass: 'Kelas',
            manageview: 'Urus paparan',
            masterclass: 'Kelas induk',
            masterclassalias: 'Nama samaran kelas induk',
            name: 'Nama',
            newjoinview: 'Paparan baru dari gabungan',
            outerjoin: 'Gabungan Luaran',
            pleaseseleceavalidmasterclass: 'Sila pilih kelas induk yang sah',
            refreshafteredit: 'Adakah anda mahu memuat semula halaman untuk melihat perubahannya?',
            selectatleastoneattribute: 'Sila pilih sekurang-kurangnya satu sifat',
            selectdisplayattributegrid:
                'Sila pilih sekurang-kurangnya satu sifat untuk dipaparkan dalam grid dan satu dalam grid berkurangan',
            showingrid: 'Tunjukkan dalam grid',
            showinreducedgrid: 'Tunjukkan dalam grid yang dikurangkan',
            targetalias: 'Nama samaran kelas sasaran'
        },
        login: {
            buttons: {
                login: 'Log masuk',
                logout: 'Tukar pengguna'
            },
            fields: {
                group: 'Kumpulan',
                language: 'Bahasa',
                password: 'Kata laluan',
                tenants: 'Penyewa',
                username: 'Nama pengguna'
            },
            loggedin: 'telah log masuk',
            sso: {
                loginfailure: 'Log masuk gagal',
                loginwith: 'Log masuk dengan (0)',
                or: 'atau',
                usernotenabled: 'Pengguna tidak diaktifkan'
            },
            title: 'Log masuk',
            welcome: 'Selamat kembali {0}.'
        },
        main: {
            administrationmodule: 'Modul Pentadbiran',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Konfigurasi Asas',
            cardlock: {
                lockedmessage: 'Anda tidak boleh mengedit kad ini kerana {0} sedang mengeditnya.',
                someone: 'seseorang'
            },
            changegroup: 'Tukar Kumpulan',
            changetenant: 'Tukar {0}',
            confirmchangegroup: 'Adakah anda pasti mahu menukar kumpulan itu?',
            confirmchangetenants: 'Adakah anda pasti mahu menukar penyewa aktif?',
            confirmdisabletenant: 'Adakah anda pasti mahu menyahaktifkan tanda "Abaikan penyewa"?',
            confirmenabletenant: 'Adakah anda pasti mahu mengaktifkan tanda "Abaikan penyewa"?',
            description: 'Penerangan',
            ignoretenants: 'Abaikan {0}',
            info: 'Maklumat',
            logo: {
                cmdbuild: 'logo CMDBuild',
                cmdbuildready2use: 'logo CMDBuild READY2USE',
                companylogo: 'logo Syarikat',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'logo openMAINT'
            },
            logout: 'Log keluar',
            managementmodule: 'Modul pengurusan data',
            multigroup: 'Pelbagai kumpulan',
            multitenant: 'Pelbagai {0}',
            navigation: 'Kemudi',
            pagenotfound: 'Laman tidak dijumpai',
            password: {
                change: 'Tukar kata laluan',
                confirm: 'Sahkan kata laluan',
                email: 'Alamat emel',
                err_confirm: 'Kata laluan tidak sepadan.',
                err_diffprevious: 'Kata laluan tidak boleh sama dengan yang sebelumnya.',
                err_diffusername: 'Kata laluan tidak boleh sama dengan nama pengguna.',
                err_length: 'Kata laluan mestilah sekurang-kurangnya {0}',
                err_reqdigit: 'Kata laluan mesti mengandungi sekurang-kurangnya satu digit.',
                err_reqlowercase: 'Kata laluan mesti mengandungi sekurang-kurangnya satu huruf kecil.',
                err_requppercase: 'Kata laluan mesti mengandungi sekurang-kurangnya satu aksara huruf besar.',
                expired: 'Kata laluan anda telah tamat tempoh. Anda mesti mengubahnya sekarang.',
                forgotten: 'Saya terlupa kata laluan saya',
                new: 'Kata laluan baharu',
                old: 'Kata laluan lama',
                recoverysuccess: 'Kami telah menghantar e-mel kepada anda untuk mendapatkan semula kata laluan anda.',
                reset: 'Menetapkan semula kata laluan',
                saved: 'Kata laluan disimpan dengan betul!'
            },
            pleasecorrecterrors: 'Sila betulkan ralat yang ditunjukkan!',
            preferences: {
                comma: 'Koma',
                decimalserror: 'Medan perpuluhan mesti ada',
                decimalstousandserror: 'Perpuluhan dan pemisah Ribuan mestilah berbeza',
                default: 'Asal',
                defaultvalue: 'Nilai tetapan asal',
                favouritemenulocation: 'Lokasi menu kegemaran',
                firstdayofweek: 'Hari pertama dalam seminggu',
                gridpreferencesclear: 'Kosongkan grid pilihan',
                gridpreferencescleared: 'Grid Keutamaan dikosongkan!',
                gridpreferencessave: 'Simpan pilihan grid',
                gridpreferencessaved: 'Grid Keutamaan disimpan!',
                gridpreferencesupdate: 'Kemas kini grid pilihan',
                groupemailsbystatus: 'Tunjukkan e-mel yang dikumpulkan mengikut status',
                labelcsvseparator: 'Pembahagi CSV',
                labeldateformat: 'Format tarikh',
                labeldecimalsseparator: 'Pemisah perpuluhan',
                labellanguage: 'Bahasa',
                labelthousandsseparator: 'Pemisah ribu',
                labeltimeformat: 'Format masa',
                msoffice: 'Microsoft Office',
                no: 'Tidak',
                notificationslabel: 'Sembang & Pemberitahuan',
                notificationssound: 'Mainkan bunyi untuk pemberitahuan',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Titik',
                preferredfilecharset: 'pengekodan CSV',
                preferredofficesuite: 'Pilihan Office suite',
                sendcancellationperiod: 'Tempoh pembatalan penghantaran e-mel (saat)',
                showcollapsed: 'Paparan runtuh',
                showfovouritesafter: 'Tunjukkan selepas menu navigasi',
                showfovouritesbefore: 'Tunjukkan sebelum menu navigasi',
                space: 'Ruang',
                thousandserror: 'Medan seribu mesti ada',
                timezone: 'Zone Masa',
                twelvehourformat: 'format 12-jam',
                twentyfourhourformat: 'format 24-jam',
                yes: 'Ya'
            },
            searchinallitems: 'Cari dalam semua item',
            searchtenant: 'Cari penyewa',
            selectdeselettenants: 'Pilih / Nyahpilih semua',
            treenavcontenttitle: '{0} daripada {1}',
            userpreferences: 'Keutamaan'
        },
        menu: {
            allitems: 'Semua items',
            classes: 'Kelas',
            custompages: 'Laman Boleh ubah',
            dashboards: 'Papan pemuka',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Kegemaran',
            favouritesadd: 'Tambah pada Kegemaran',
            favouritesremove: 'Buang dari kegemaran',
            homepage: '<em>Homepage</em>',
            processes: 'Proses',
            reports: 'Laporan',
            setasinitialpage: 'Tetapkan sebagai halaman awal',
            views: 'Lihat'
        },
        mobile: {
            config: {
                configure: 'Konfigurasikan peranti mudah alih',
                customercode: 'Kod pelanggan',
                devicename: 'Nama peranti',
                regenerate: 'Menjana semula',
                serverurl: 'URL Pelayan'
            }
        },
        notes: {
            edit: 'Ubah suai nota'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Padamkan pemberitahuan',
            emptymessage: 'Tiada pemberitahuan untuk dipaparkan.',
            label: 'Pemberitahuan',
            viewall: 'Papar semua'
        },
        notifier: {
            attention: 'Perhatian',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Ralat',
            genericerror: 'Ralat umum',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Maklumat umum',
            genericwarning: 'Amaran umum',
            info: 'Maklumat',
            more: '<em>More info</em>',
            success: 'Berjaya',
            warning: 'Amaran'
        },
        patches: {
            apply: 'Guna pembaikan',
            category: 'Kategori',
            description: 'Keterangan',
            name: 'Nama',
            patches: 'Pembaikan'
        },
        processes: {
            abortconfirmation: 'Adakah anda pasti mahu membatalkan proses ini?',
            abortprocess: 'Batalkan proses',
            action: {
                advance: 'Maju',
                label: 'Tindakan'
            },
            activeprocesses: 'Proses aktif',
            allstatuses: 'Semua',
            editactivity: 'Ubah suai aktiviti',
            openactivity: 'Buka aktiviti',
            resumeprocesses: 'Proses yang tidak digantung tidak boleh disambung semula',
            startworkflow: 'Mula',
            workflow: 'Aliran Kerja'
        },
        relationGraph: {
            activity: 'aktiviti',
            allLabelsOnGraph: 'semua label pada grafik',
            card: 'Kad',
            cardList: 'Senarai Kad',
            cardRelations: 'Kad Hubungan',
            choosenaviagationtree: 'Pilih cabang kemudi',
            class: 'Kelas',
            classList: 'Senarai Kelas',
            compoundnode: 'Nod Kompaun',
            disable: 'Nyahaktifkan',
            edges: 'Sudut Tepi',
            enable: 'Aktifkan',
            labelsOnGraph: 'maklumat tambahan pada grafik',
            level: 'Tahap',
            nodes: 'Nod-nod',
            openRelationGraph: 'Buka Graf Perhubungan',
            quantity: 'Kuantiti',
            refresh: 'Tetapan semula',
            relation: 'hubungan',
            relationGraph: 'Graf Hubungan',
            reopengraph: 'Buka semula grafik dari nod ini'
        },
        relations: {
            adddetail: 'Tambah perincian',
            addrelations: 'Tambah hubungan',
            attributes: 'Sifat',
            begindate: 'Tarikh Mula',
            cardnotpresentmessage:
                'Kad yang berkaitan tidak lagi tersedia. Meneruskan dengan pengubahsuaian akan memerlukan pemilihan kad yang lain.',
            code: 'Kod',
            deletedetail: 'Padam perincian',
            deleterelation: 'Padam hubungan',
            deleterelationconfirm: 'Adakah anda pasti mahu memadamkan hubungan ini?',
            description: 'Keterangan',
            editcard: 'Ubah kad',
            editdetail: 'Ubah perincian',
            editrelation: 'Ubah hubungan',
            errorexisting:
                'Hubungan "{0}" sudah wujud. Ubah yang sedia ada atau padamkannya sebelum mencipta hubungan baharu.',
            extendeddata: 'Data lanjutan',
            mditems: 'items',
            missingattributes: 'Hilang sifat wajib',
            opencard: 'Buka kad hubungan',
            opendetail: 'Tunjuk perincian',
            relationdata: 'Data perkaitan',
            type: 'Jenis'
        },
        reports: {
            csv: 'CSV',
            download: 'Muat turun',
            format: 'Format',
            msgs: {
                creationerror: 'Kesilapan telah berlaku semasa permulaan pembuatan laporan.',
                creationstarted:
                    'Penciptaan laporan dimulakan. Anda akan dimaklumkan apabila ia sedia untuk dimuat turun.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Cetak',
            reload: 'Tambah semula',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Tetapan Asal'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Harian'
                        },
                        monthly: {
                            description: 'Bulanan'
                        },
                        once: {
                            description: 'Sekali'
                        },
                        weekly: {
                            description: 'Mingguan'
                        },
                        yearly: {
                            description: 'Tahunan'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Tetapan Asal'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Tiada sesiapa'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Tambah Thematism',
            analysisType: 'Jenis Analisis',
            attribute: 'Sifat',
            bordersIntervals: 'Konfigurasi yang salah untuk had selang waktu',
            calculateRules: 'Jana gaya peraturan',
            clearThematism: 'Kosongkan Thematism',
            color: 'Warna',
            defineLegend: 'Takrifan lejen',
            defineThematism: 'Takrifan Thematism',
            deletethematism: 'Adakah anda pasti mahu memadamkan thematism ini?',
            editThematism: 'Edit konfigurasi peraturan',
            function: 'Fungsi',
            generate: 'Jana',
            geoAttribute: 'Sifat Geographic',
            graduated: 'Layak',
            highlightSelected: 'Serlahkan item yang dipilih',
            intervals: 'selang masa',
            legend: 'Lejen',
            name: 'nama',
            newThematism: 'Thematism baru',
            punctual: 'Tepat masa',
            quantity: 'Kira',
            segments: 'Bahagian',
            source: 'Sumber',
            table: 'Jadual',
            thematism: 'Thematisms',
            value: 'Nilai'
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
                    exitwithoutsave: 'Keluar tanpa menyimpan',
                    stayinedit: 'Kembali untuk mengedit'
                },
                savebeforeexit:
                    'Terdapat perubahan yang tidak disimpan dalam lampiran. Adakah anda mahu menyimpan mereka',
                saveprocessmessage:
                    'Lampiran hanya dapat disimpan dalam kad yang ada. Menekan pada simpan, contoh proses akan dibuat dengan data yang dimasukkan dalam borang.'
            },
            calendar: {
                error: 'Parameter {0} tidak ditetapkan',
                errorwrong: 'Parameter {0} tidak ditetapkan dengan betul'
            },
            customform: {
                addrow: 'Tambah baris',
                clonerow: 'Klon baris',
                datanotvalid: 'Data tidak sah',
                deleterow: 'Padam baris',
                editrow: 'Ubah baris',
                export: 'Eksport',
                import: 'Import',
                importexport: {
                    expattributes: 'Data untuk dieksport',
                    file: 'Fail',
                    filename: 'Nama fail',
                    format: 'format',
                    importmode: 'Mod import',
                    keyattributes: 'Sifat utama',
                    missingkeyattr: 'Pilih sekurang-kurangnya satu sifat utama',
                    modeadd: 'Tambah',
                    modemerge: 'Gabung',
                    modereplace: 'Tukar',
                    separator: 'Pembahagi'
                },
                refresh: 'Tetapkan ke asal'
            },
            linkcards: {
                checkedonly: 'Periksa sahaja',
                editcard: 'Ubah kad',
                opencard: 'Buka kad',
                refreshselection: 'Guna pilihan asal',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Nyahaktifkan penapisan grid',
                togglefilterenabled: 'Aktifkan penapis grid'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline: 'Mod sebaris tidak disokong untuk jenis widget ini. Sila betulkan widget',
            required: 'Widget ini diperlukan.'
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
