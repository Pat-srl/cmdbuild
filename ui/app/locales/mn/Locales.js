(function () {
    Ext.define('CMDBuildUI.locales.mn.Locales', {
        requires: ['CMDBuildUI.locales.mn.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'mn',
        administration: CMDBuildUI.locales.mn.LocalesAdministration.administration,
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
            add: 'Хавсралт нэмэх',
            attachmenthistory: 'Хавсралтын түүх',
            author: 'Үүсгэгч',
            authordescription: '<em>Author description</em>',
            browse: 'Хайх & hellip;',
            category: 'Ангилал',
            code: 'Код',
            creationdate: 'Үүсгэсэн огноо',
            deleteattachment: 'Хавсралтыг устгах',
            deleteattachment_confirmation: 'Та энэ хавсралтыг устгахдаа итгэлтэй байна уу?',
            description: 'Тодорхойлолт',
            download: 'Татаж авах',
            dropfiles: 'Файлуудыг энд буулгах',
            editattachment: 'Хавсралтыг өөрчлөх',
            errordms: '<em>The DMS is disabled and it is not possible to save files</em>',
            file: 'Файл',
            filealreadyinlist: '{0} файл аль хэдийн жагсаалтад орсон байна.',
            filename: 'Файлын нэр',
            fileview: 'Хавсралтыг харах',
            invalidfiles: 'Хүчингүй файлуудыг устгах',
            majorversion: 'Гол хувилбар',
            maxsize: 'Зөвшөөрөгдсөн файлын хэмжээ хамгийн ихдээ {0} МБ байна.',
            metadata: '<em>Metadata</em>',
            modificationdate: 'Өөрчлөлтийн огноо',
            new: 'Шинэ хавсралт',
            nocategory: 'Ангилалгүй',
            preview: 'Урьдчилан харах',
            removefile: 'Файлыг устгах',
            statuses: {
                empty: 'Хоосон файл',
                error: 'Алдаа',
                extensionNotAllowed: 'Файлын өргөтгөлийг зөвшөөрөөгүй байна',
                loaded: 'Ачаалагдсан',
                ready: 'Бэлэн',
                toolarge: 'Хэт том'
            },
            successupload: '{0} хавсралтыг байршуулсан.',
            uploadfile: 'Файлыг байршуулах ...',
            version: 'Хувилбар',
            viewhistory: 'Хавсралтын түүхийг харах',
            viewmetadata: '<em>View attachment metadata</em>',
            warningmessages: {
                atleast: '{0}: "{2}" төрлийн {1} хавсралтыг ачаалав. Энэ ангилалд дор хаяж {3} хавсралт байх ёстой.',
                exactlynumber: '{0}: "{2}" төрлийн {1} хавсралтыг ачаалав. Энэ ангилалд {3} хавсралтыг хүлээж байна.',
                maxnumber:
                    '{0}: "{2}" төрлийн {1} хавсралтыг ачаалав. Энэ ангилалд хамгийн ихдээ {3} хавсралт байх ёстой.'
            },
            wrongfileextension: '{0} файлын өргөтгөл зөвшөөрөгдөөгүй байна'
        },
        bim: {
            bimViewer: 'Bim үзүүлэгч',
            card: {
                label: 'Карт'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Х тэнхлэгийн дагуух хайрцагны хэмжээ',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Y тэнхлэгийн дагуух хайрцагны хэмжээ',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Z тэнхлэгийн дагуух хайрцагны хэмжээ',
                calculated: 'Тооцоолсон',
                description: 'Тодорхойлолт',
                guid: 'GUID',
                label: 'Шинж чанар',
                name: 'Нэр',
                type: '<em>Type</em>',
                uuid: 'UUID'
            },
            layers: {
                label: 'Давхарга',
                menu: {
                    hideAll: 'Бүгдийг нуух',
                    hideAllLayers: '<em>Hide All Layers</em>',
                    hideAllXeokit: '<em>Hide all {0}</em>',
                    showAll: 'Бүгдийг харуулах',
                    showAllLayers: '<em>Show All Layers</em>',
                    showAllXeokit: '<em>Show all {0}</em>'
                },
                name: 'Нэр',
                quantity: '<em>Quantity</em>',
                visibility: 'Харагдац'
            },
            menu: {
                camera: 'Камер',
                disableslice: '<em>Disable slice objects</em>',
                enableslice: '<em>Enable slice objects</em>',
                frontView: 'Урд талыг харах',
                mod: 'Үзэгчийн хяналт',
                ortho: '<em>Switch to Orthogonal View</em>',
                orthographic: 'Орфографийн камер',
                pan: 'Гүйлгэх',
                panXeokit: '<em>Viewer controls: scroll (Shift)</em>',
                perspe: '<em>Switch to Perspective View</em>',
                perspective: 'Алсын камер',
                resetSlice: '<em>Reset slice</em>',
                resetView: 'Харах байдлыг сэргээх',
                rotate: 'эргүүлэх',
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
                sideView: 'Талын харах',
                threeD: '<em>Switch to 3D View</em>',
                topView: 'Шилдгийг харах',
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
            showBimCard: '3D үзүүлэгчийг нээх',
            tree: {
                arrowTooltip: 'Элемент сонгох',
                label: 'Бүтэц',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Холбогдох картыг нээх',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Сонгосон зүйлийг цуцлах',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                '<em>Some attachments will not be downloaded because they have not yet been saved to the server</em>',
            alertnoattachmentsdownload:
                '<em>No attachments will be downloaded because they have not yet been saved to the server</em>',
            cancelselection: 'Сонголтыг цуцлах',
            confirmabort:
                'Та процессын {0} инстанцийг цуцалж байна. Та үргэлжлүүлэхийг хүсэж байгаадаа итгэлтэй байна уу?',
            confirmdelete: 'Та {0} картыг устгаж байна. Та үргэлжлүүлэхийг хүсэж байгаадаа итгэлтэй байна уу?',
            confirmdeleteattachements: 'Та {0} хавсралтыг устгаж байна. Та үргэлжлүүлэхдээ итгэлтэй байна уу?',
            confirmedit: 'Та {0} -г {1} картанд өөрчилж байна. Та үргэлжлүүлэхдээ итгэлтэй байна уу?',
            delete: 'Сонгосон зүйлийг устгах',
            download: 'Сонгосон хавсралтыг татаж авах',
            downloadall: '<em>Download all attachments</em>',
            edit: 'Сонгосон зүйлийг засах',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: '<em>Please select at least one attribute.</em>',
            selectall: 'Бүгдийг сонгох'
        },
        calendar: {
            active_expired: 'Идэвхтэй / хугацаа дууссан',
            add: 'Хуваарь нэмэх',
            advancenotification: 'Өдрийн урьдчилсан мэдэгдэл',
            allcategories: 'Бүх ангилал',
            alldates: 'Бүх огноо',
            associatedcard: '<em>Associated card</em>',
            calculated: 'Тооцоолсон',
            calendar: 'Календар',
            cancel: 'Цуцалсан гэж тэмдэглэх',
            category: 'Ангилал',
            class: '<em>Class</em>',
            cm_confirmcancel: 'Та цуцлагдсан хуваарийг тэмдэглэхдээ итгэлтэй байна уу?',
            cm_confirmcomplete: 'Хуваарийг дууссан гэж тэмдэглэхдээ итгэлтэй байна уу?',
            cm_markcancelled: 'Сонгосон хуваарийг цуцалсан гэж тэмдэглэх',
            cm_markcomplete: 'Сонгосон хуваарь дууссан гэж тэмдэглэх',
            complete: 'Дууссан гэж тэмдэглэх',
            completed: 'Дууссан',
            date: 'Огноо',
            days: 'Өдөр',
            delaybeforedeadline: 'Хугацаа дуусахаас өмнө хойшлуулах',
            delaybeforedeadlinevalue: 'Хугацаа дуусахаас өмнө хойшлуулах утга',
            description: 'Тодорхойлолт',
            editevent: 'Хуваарийг засах',
            enddate: 'Дуусах огноо',
            endtype: 'Дуусах төрөл',
            event: 'Хуваарь',
            execapplyfilter: '<em>Can not execute applyAdvanceFilter because there are more than one stores</em>',
            execremovefilter: '<em>Can not execute removeAdvanceFilter because there are more than one stores</em>',
            executiondate: 'Гүйцэтгэх огноо',
            frequency: 'Давтамж',
            frequencymultiplier: 'Давтамжийн үржүүлэгч',
            grid: 'Сүлжээ',
            leftdays: 'Үлдэх хоногууд байна',
            londdescription: 'Бүрэн тодорхойлолт',
            manual: 'Гар ажиллагаатай',
            maxactiveevents: 'Хамгийн их идэвхтэй хуваарь',
            messagebodydelete: 'Та хуваарь гаргагчийн дүрмийг хасмаар байна уу?',
            messagebodyplural: '{0} хуваарийн дүрэм байна',
            messagebodyrecalculate: 'Та хуваарийн дүрмийг шинэ огноонд дахин тооцоолохыг хүсэж байна уу?',
            messagebodysingular: '{0} хуваарийн дүрэм байна',
            messagetitle: 'Хуваарийг дахин тооцоолох',
            missingdays: 'Байхгүй өдрүүд',
            next30days: 'Дараагийн 30 хоног',
            next7days: 'Дараагийн 7 хоног',
            notificationtemplate: 'Мэдэгдэл өгөхөд ашиглах загвар',
            notificationtext: 'Мэдэгдлийн текст',
            occurencies: 'Үйл явдлын тоо',
            operation: 'Үйл ажиллагаа',
            partecipantgroup: 'Оролцогчийн групп',
            partecipantuser: 'Оролцогч хэрэглэгч',
            priority: 'Түвшин',
            recalculate: 'Дахин тооцоолох',
            referent: 'Лавлагаа',
            scheduler: 'Хуваарь гаргагч',
            sequencepaneltitle: 'Хуваарь гаргах',
            startdate: 'Эхлэх огноо',
            status: 'Төлөв',
            today: 'Өнөөдөр',
            type: 'Төрөл',
            viewevent: 'Хуваарийг харах',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Тооцооллын шалгуур',
            widgetemails: 'Имэйл',
            widgetgotocard: '<em>Go to source card</em>',
            widgetsourcecard: 'Эх карт'
        },
        chat: {
            title: '<em>Chat</em>'
        },
        classes: {
            cards: {
                addcard: 'Карт нэмэх',
                clone: 'Клон',
                clonewithrelations: 'Клон карт ба харилцаа холбоо',
                deletebeaware: 'Үүнийг анхаарах хэрэгтэй:',
                deleteblocked: '{0} -тай холбоотой тул устгах боломжгүй байна.',
                deletecard: 'Картыг устгах',
                deleteconfirmation: 'Та энэ картыг устгахдаа итгэлтэй байна уу?',
                deleterelatedcards: '{0} холбоотой картыг устгах болно',
                deleterelations: '{0} картын холбоог устгах болно',
                label: 'Карт',
                modifycard: 'Картыг өөрчлөх',
                opencard: 'Нээлттэй карт',
                print: 'Картыг хэвлэх'
            },
            simple: 'Энгийн',
            standard: 'Стандарт'
        },
        common: {
            actions: {
                add: 'Нэмэх',
                apply: 'Хэрэгжүүлэх',
                cancel: 'Цуцлах',
                clear: '<em>Clear</em>',
                close: 'Хаах',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Устгах',
                edit: 'Засах',
                execute: 'Гүйцэтгэх',
                forward: '<em>Forward</em>',
                help: 'Тусламж',
                hide: '<em>Hide</em>',
                load: 'Ачаалал',
                open: 'Нээлттэй',
                previous: '<em>Previous</em>',
                refresh: 'Өгөгдлийг шинэчлэх',
                remove: 'Устгах',
                save: 'Хадгалах',
                saveandapply: 'Хадгалж хэрэгжүүлэх',
                saveandclose: 'Хадгалж хаах',
                search: 'Хайх',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Хайх ...'
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
                nogroup: 'Үндсэн өгөгдөл'
            },
            dates: {
                date: 'ө/с/Ж',
                datetime: 'ө/с/Ж Ц:м:с',
                time: 'Ц:м:с'
            },
            editor: {
                clearhtml: 'HTML-г арилгах',
                expand: 'Редакторыг өргөжүүлэх',
                reduce: 'Редакторыг багасгах',
                signature: '<em>Add signature</em>',
                unlink: 'Холбоосгүй',
                unlinkmessage: 'Сонгосон холбоосыг текст болгон хувиргана уу.'
            },
            grid: {
                disablemultiselection: 'Олон сонголтыг идэвхгүй болгох',
                enamblemultiselection: 'Олон сонголтыг идэвхжүүлэх',
                export: 'Өгөгдлийг экспортлох',
                filterremoved: 'Одоогийн шүүлтүүрийг устгасан',
                import: 'Мэдээллийг импортлох',
                itemnotfound: 'Юу ч олдсонгүй',
                lastUpdate: '<em>Last update</em>',
                list: 'Жагсаалт',
                opencontextualmenu: 'Цэсийн агуулгыг нээх',
                print: 'Хэвлэх',
                printcsv: 'CSV хэлбэрээр хэвлэх',
                printmap: '<em>Print Map</em>',
                printodt: 'ODT хэлбэрээр хэвлэх',
                printpdf: 'PDF хэлбэрээр хэвлэх',
                row: 'Зүйл',
                rows: 'Эд зүйлс',
                subtype: 'Дэд төрөл',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Үйл ажиллагаа',
                attachment: 'Хавсралт',
                attachments: 'Хавсралтууд',
                card: 'Карт',
                clonerelationmode: 'Клон харилцааны горим',
                details: 'Дэлгэрэнгүй мэдээлэл',
                emails: 'Имэйл',
                history: 'Түүх',
                notes: 'Тэмдэглэл',
                relations: 'Харилцаа холбоо',
                schedules: 'Хуваарь',
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
                download: 'Диаграммыг татаж авах',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Өгөгдлийн сүлжээг нуух',
                gridshow: 'Өгөгдлийн сүлжээг харуулах',
                openinpopup: 'Попап дээр нээх',
                reload: 'Дахин ачаалах',
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
            addattachmentsfromdocumentarchive: 'Баримт бичгийн архиваас хавсралт нэмэх',
            alredyexistfile: 'Ийм нэртэй файл аль хэдийн байна',
            archivingdate: 'Архивлах огноо',
            attachfile: 'Файл хавсаргах',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Имэйл бичих',
            composefromtemplate: 'Загвараас бичих',
            delay: 'Саатал',
            delays: {
                day1: '1 өдрийн дараа',
                days2: '2 хоногийн дараа',
                days4: '4 хоногийн дараа',
                hour1: '1 цаг',
                hours2: '2 цаг',
                hours4: '4 цаг',
                month1: '1 сарын дараа',
                negativeday1: '1 өдрийн өмнө',
                negativedays2: '2 хоногийн өмнө',
                negativedays4: '4 хоногийн өмнө',
                negativehour1: '1 цагийн өмнө',
                negativehours2: '2 цагийн өмнө',
                negativehours4: '4 цагийн өмнө',
                negativemonth1: '1 сарын өмнө',
                negativeweek1: '1 долоо хоногийн өмнө',
                negativeweeks2: '2 долоо хоногийн өмнө',
                none: 'Аль нь ч биш',
                week1: '1 долоо хоногт',
                weeks2: '2 долоо хоногийн дараа'
            },
            dmspaneltitle: 'Мэдээллийн сангаас хавсралтыг сонгох',
            edit: 'Засах',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Хаанаас',
            gridrefresh: 'Сүлжээний шинэчлэлт',
            groupbystatus: '<em>Grouped by status</em>',
            keepsynchronization: 'Синхрончлолыг үргэлжлүүлэх',
            maxsize: '<em>The maximum total size allowed for attachments is {0}.</em>',
            message: 'Мессеж',
            regenerateallemails: 'Бүх имэйлийг сэргээх',
            regenerateemail: 'И-мэйлийг сэргээх',
            reloadconfirmationmessage: '<em>E-mails in draft status will be deleted. Do you want to continue?</em>',
            remove: 'Устгах',
            remove_confirmation: 'Та энэ имэйлийг устгахдаа итгэлтэй байна уу?',
            reply: 'хариу',
            replyprefix: '{0} дээр {1} бичсэн:',
            searchattachment: '<em>Search Attachment</em>',
            searchcard: '<em>Search card</em>',
            selectaclass: 'Класс сонгох',
            sendemail: 'И-мэйл илгээх',
            sendingemail: '<em>Sendig email.</em>',
            signature: '<em>Signature</em>',
            size: '<em>Size</em>',
            status: '<em>Status</em>',
            statuses: {
                draft: 'Ноорог',
                error: 'Алдаа',
                new: '<em>New</em>',
                outgoing: 'Гарах',
                received: 'Хүлээн авсан',
                sent: 'Илгээсэн',
                skipped: '<em>Skipped</em>'
            },
            subject: 'Сэдэв',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Хэнд',
            view: 'Харах'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Хэрэглэгчийн нэр эсвэл нууц үг буруу байна',
            classnotfound: 'Класс {0} олдсонгүй',
            fieldrequired: 'Энэ талбар заавал шаардлагатай',
            invalidfilter: 'Буруу шүүлтүүр',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Олдсонгүй',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Үйлдэл',
            addfilter: 'Шүүлтүүр нэмэх',
            any: '<em>At least one</em>',
            attachments: '<em>Attachments</em>',
            attachmentssearchtext: 'Хавсралт текст хайх',
            attribute: 'Шинж чанарыг өөрчлөх',
            attributes: 'Шинж чанар',
            clearfilter: 'Классын шүүлтүүр',
            clone: 'Клон',
            copyof: 'Хуулбар',
            currentgroup: 'Одоогийн групп',
            currentuser: 'Одоогийн хэрэглэгч',
            defaultset: 'Анхдагчаар тохируулах',
            defaultunset: 'Анхдагчаас тохируулах',
            description: 'Тодорхойлолт',
            domain: 'Домэйн',
            errorname: '<em>Filter name already in use</em>',
            filterdata: 'Өгөгдлийг шүүх',
            fromfilter: 'Шүүлтүүрээс',
            fromselection: 'Сонголтоос',
            group: 'Групп',
            ignore: 'Алгасах',
            migrate: 'Шилжин суурьших',
            name: 'Нэр',
            newfilter: 'Шинэ шүүлтүүр',
            noone: 'Хэн ч биш',
            operator: 'Оператор',
            operators: {
                and: '<em>And</em>',
                beginswith: 'Эхэлдэг',
                between: 'Хооронд',
                contained: 'Агуулсан',
                containedorequal: 'Агуулсан эсвэл тэнцүү',
                contains: 'Агуулсан',
                containsorequal: 'Агуулсан эсвэл тэнцүү',
                descriptionbegin: 'Тодорхойлолт нь эхэлнэ',
                descriptioncontains: 'Тодорхойлолтыг агуулна',
                descriptionends: 'Тодорхойлолт дуусна',
                descriptionnotbegin: 'Тодорхойлолт эхлэхгүй',
                descriptionnotcontain: 'Тодорхойлолт агуулаагүй байна',
                descriptionnotends: 'Тодорхойлолт үүгээр дуусахгүй',
                different: 'Өөр',
                doesnotbeginwith: 'Эхлээгүй',
                doesnotcontain: 'Агуулаагүй байна',
                doesnotendwith: 'Үүгээр дуусахгүй',
                endswith: 'Дуусах',
                equals: 'Тэнцүү',
                greaterthan: 'Үүнээс их',
                isnotnull: 'Хүчингүй байна',
                isnull: 'Хоосон байна',
                lessthan: 'Түүнээс бага',
                or: '<em>Or</em>'
            },
            relations: 'Харилцаа холбоо',
            removeattribute: '<em>Remove attribute</em>',
            removeblock: '<em>Remove block</em>',
            type: 'Төрөл',
            typeinput: 'Оролтын параметр',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Хэрэглэгч',
            value: 'Утга'
        },
        gis: {
            card: 'Карт',
            cardsMenu: 'Картын цэг',
            code: 'Код',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Тодорхойлолт',
            extension: {
                errorCall: 'Алдаа',
                noResults: 'Үр дүнгүй'
            },
            externalServices: 'Гадаад үйлчилгээ',
            geographicalAttributes: 'Гео шинж чанарууд',
            geoserverLayers: 'Геосерверийн давхаргууд',
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
            layers: 'Давхарга',
            layersTab: {
                clearPreferences: '<em>Clear layers preferences</em>',
                preferencesCleared: '<em>Layers preferences cleared</em>',
                preferencesSaved: '<em>Layers preferences saved</em>',
                savePreferences: '<em>Save layers preferences</em>',
                updatePreferences: '<em>Update layers preferences</em>'
            },
            list: 'Жагсаалт',
            longpresstitle: 'Талбай дахь геоэлементүүд',
            map: 'Газрын зураг',
            mapServices: 'Газрын зургийн үйлчилгээ',
            menu: {
                font: '<em>Font size</em>',
                options: '<em>Options</em>'
            },
            position: 'Байрлал',
            root: 'Root',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Бүтэц',
            type: 'Төрөл',
            view: 'Харах',
            zoom: 'Томруулах'
        },
        history: {
            activityname: 'Үйл ажиллагааны нэр',
            activityperformer: 'Үйл ажиллагаа гүйцэтгэгч',
            attributes: '<em>Attributes ({0})</em>',
            begindate: 'Эхлэх огноо',
            enddate: 'Дуусах огноо',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: '<em>Other relations</em>',
            processstatus: 'Төлөв',
            referencerelation: '<em>Reference relations</em>',
            relation: '<em>Relation</em>',
            system: '<em>System</em>',
            type: '<em>Type</em>',
            user: 'Хэрэглэгч',
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
                uri: 'Мэдээллийн сангийн URI',
                user: 'Мэдээллийн сангийн хэрэглэгч'
            },
            downloadreport: 'Тайланг татаж авах',
            emailfailure: 'Имэйл илгээх явцад алдаа гарлаа!',
            emailmessage: '{0} файлын {1} огноонд импортын тайланг хавсаргав',
            emailsubject: 'Мэдээллийн тайланг импортлох',
            emailsuccess: 'Имэйлийг амжилттай илгээв!',
            export: 'Экспортлох',
            exportalldata: 'Бүх өгөгдөл',
            exportfiltereddata: 'Зөвхөн сүлжээний шүүлтүүрт тохирох өгөгдөл',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Энэ загварт дүрс импортлох идэвхгүй байна',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: 'Карт',
                project: 'Төсөл',
                sourcetype: 'Импортлох'
            },
            import: 'Импорт',
            importresponse: 'Импортын хариу',
            response: {
                created: '<em>Created cards</em>',
                deleted: '<em>Deleted cards</em>',
                errors: 'Алдаа',
                linenumber: 'Шугамын дугаар',
                message: 'Мессеж',
                modified: '<em>Modified cards</em>',
                notprocessed: '<em>Unprocessed cards</em>',
                processed: 'Боловсруулсан мөр',
                recordnumber: 'Бичлэгийн дугаар',
                unchanged: '<em>Unchanged cards</em>'
            },
            sendreport: 'Тайлан илгээх',
            template: 'Загвар',
            templatedefinition: 'Загварын тодорхойлолт'
        },
        joinviews: {
            active: 'Идэвхтэй',
            addview: 'Үзэлт нэмэх',
            alias: 'Алиас',
            attribute: 'Шинж чанар',
            attributecustomization: '<em>Attributes customizations</em>',
            attributes: 'Шинж чанар',
            attributeschoice: '<em>Attributes choice</em>',
            attributesof: 'Шинж чанарууд: {0}',
            createview: 'Үзэгдэл үүсгэх',
            datasorting: 'Мэдээллийн ангилал',
            delete: 'Устгах',
            deleteview: 'Үзэлтийг устгах',
            deleteviewconfirm: 'Та энэ харагдацыг устгахдаа итгэлтэй байна уу?',
            description: 'Тодорхойлолт',
            disable: 'Идэвхгүй болгох',
            domainalias: 'Домэйн алиас',
            domainsof: ' {0}-н домэйн',
            edit: 'Засах',
            editview: 'Харах тохиргоог засах',
            enable: 'Идэвхжүүлэх',
            fieldsets: 'Талбайнууд',
            filters: 'Шүүлтүүр',
            generalproperties: 'Ерөнхий шинж чанар',
            group: 'Групп',
            innerjoin: 'Дотоод нэгдэл',
            jointype: 'Нэгдэх төрөл',
            joinview: 'Нэгдэлээс харах',
            klass: 'Класс',
            manageview: 'Үзэлтийг удирдах',
            masterclass: 'Мастер класс',
            masterclassalias: 'Мастер класс алиас',
            name: 'Нэр',
            newjoinview: 'Нэгдэх шинэ харагдац',
            outerjoin: 'Гадаад нэгдэл',
            pleaseseleceavalidmasterclass: 'Зөв мастер класс сонгоно уу',
            refreshafteredit: 'Өөрчлөлтийг харахын тулд хуудсыг дахин шинэчлэхийг хүсч байна уу?',
            selectatleastoneattribute: '<em>Please select at least one attribute</em>',
            selectdisplayattributegrid:
                '<em>Please select at least one attribute to display in grid and one in reduced grid</em>',
            showingrid: 'Сүлжээнд харуулах',
            showinreducedgrid: 'Багасгасан сүлжээнд харуулах',
            targetalias: 'Зорилтот классын нэр'
        },
        login: {
            buttons: {
                login: 'Нэвтрэх',
                logout: 'Хэрэглэгч өөрчлөх'
            },
            fields: {
                group: 'Групп',
                language: 'Хэл',
                password: 'Нууц үг',
                tenants: 'Түрээслэгчид',
                username: 'Хэрэглэгчийн нэр'
            },
            loggedin: 'Нэвтэрсэн',
            sso: {
                loginfailure: '<em>Login failure</em>',
                loginwith: '<em>Login with {0}</em>',
                or: '<em>or</em>',
                usernotenabled: '<em>User not enabled</em>'
            },
            title: 'Нэвтрэх',
            welcome: 'Эргээд тавтай морилно уу {0}.'
        },
        main: {
            administrationmodule: 'Удирдлагын модул',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Үндсэн тохиргоо',
            cardlock: {
                lockedmessage: '{0} үүнийг засаж байгаа тул та энэ картыг засах боломжгүй.',
                someone: 'хэн нэгэн'
            },
            changegroup: 'Группийг өөрчлөх',
            changetenant: 'Өөрчлөх {0}',
            confirmchangegroup: 'Та группээ өөрчлөхдөө итгэлтэй байна уу?',
            confirmchangetenants: 'Та идэвхтэй түрээслэгчдийг солихдоо итгэлтэй байна уу?',
            confirmdisabletenant: 'Та "Түрээслэгчээс татгалзах" флагийг идэвхгүй болгохдоо итгэлтэй байна уу?',
            confirmenabletenant: 'Та "Түрээслэгчээс татгалзах" флагийг идэвхжүүлэхдээ итгэлтэй байна уу?',
            description: '<em>Description</em>',
            ignoretenants: '{0} -г алгасах',
            info: 'Мэдээлэл',
            logo: {
                cmdbuild: 'CMDBuild лого',
                cmdbuildready2use: 'CMDBuild READY2USE лого',
                companylogo: 'Компаний лого',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'openMAINT лого'
            },
            logout: 'Гарах',
            managementmodule: 'Өгөгдлийн менежментийн модул',
            multigroup: 'Олон групп',
            multitenant: 'Олон {0}',
            navigation: 'Навигаци',
            pagenotfound: 'Хуудас олдсонгүй',
            password: {
                change: 'Нууц үг солих',
                confirm: 'Нууц үгээ баталгаажуулна уу',
                email: 'Имэйл хаяг',
                err_confirm: 'Нууц үг таарахгүй байна.',
                err_diffprevious: 'Нууц үг өмнөх үгтэй ижил байж болохгүй.',
                err_diffusername: 'Нууц үг нь хэрэглэгчийн нэртэй ижил байж болохгүй.',
                err_length: 'Нууц үг нь дор хаяж {0} тэмдэгт байх ёстой.',
                err_reqdigit: 'Нууц үг дор хаяж нэг оронтой байх ёстой.',
                err_reqlowercase: 'Нууц үгэнд дор хаяж нэг жижиг үсэг байх ёстой.',
                err_requppercase: 'Нууц үгэнд дор хаяж нэг том үсэг байх ёстой.',
                expired: 'Таны нууц үгийн хугацаа дууссан. Та одоо нууц үгээ өөрчлөх шаардлагатай.',
                forgotten: 'Нууц үгээ мартсан',
                new: 'Шинэ нууц үг',
                old: 'Хуучин нууц үг',
                recoverysuccess: 'Бид танд нууц үгээ сэргээх заавар бүхий имэйл илгээсэн.',
                reset: 'Нууц үгээ сэргээх',
                saved: 'Нууц үгийг зөв хадгалсан!'
            },
            pleasecorrecterrors: 'Заасан алдааг засна уу!',
            preferences: {
                comma: 'Таслал',
                decimalserror: 'Аравтын бутархайн талбар заавал байх ёстой',
                decimalstousandserror: 'Аравтын бутархай ба мянгатын тусгаарлагч нь ялгаатай байх ёстой',
                default: 'Анхдагч',
                defaultvalue: 'Анхдагч утга',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: '7 хоногийн эхний өдөр',
                gridpreferencesclear: 'Сүлжээний тохиргоог арилгах',
                gridpreferencescleared: 'Сүлжээний тохиргоог арилгасан!',
                gridpreferencessave: 'Сүлжээний тохиргоог хадгалах',
                gridpreferencessaved: 'Сүлжээний тохиргоог хадгаллаа!',
                gridpreferencesupdate: 'Сүлжээний тохиргоог шинэчлэх',
                groupemailsbystatus: '<em>Show emails grouped by status</em>',
                labelcsvseparator: 'CSV тусгаарлагч',
                labeldateformat: 'Огнооны формат',
                labeldecimalsseparator: 'Аравтын бутархай',
                labellanguage: 'Хэл',
                labelthousandsseparator: 'Мянгатын тусгаарлагч',
                labeltimeformat: 'Цагийн формат',
                msoffice: 'Microsoft Office',
                no: '<em>No</em>',
                notificationslabel: '<em>Chat & Notifications</em>',
                notificationssound: '<em>Play sound for notifications</em>',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Хугацаа',
                preferredfilecharset: 'CSV кодчилол',
                preferredofficesuite: 'Сонгосон Office багц',
                sendcancellationperiod: '<em>Email send cancellation period (seconds)</em>',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: 'Орон зай',
                thousandserror: 'Мянгатын талбар байх ёстой',
                timezone: 'Цагийн бүс',
                twelvehourformat: '12 цагийн формат',
                twentyfourhourformat: '24 цагийн формат',
                yes: '<em>Yes</em>'
            },
            searchinallitems: 'Бүх зүйлийг хайх',
            searchtenant: '<em>Search tenant</em>',
            selectdeselettenants: 'Бүгдийг сонгох/сонгохгүй байх',
            treenavcontenttitle: '{1} -с {0}',
            userpreferences: 'Тохиргоо'
        },
        menu: {
            allitems: 'Бүх зүйл',
            classes: 'Класс',
            custompages: 'Захиалгат хуудаснууд',
            dashboards: 'Хяналтын самбар',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: 'Үйл явц',
            reports: 'Тайлан',
            setasinitialpage: '<em>Set as initial page</em>',
            views: 'Үзсэн тоо'
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
            edit: 'Тэмдэглэлийг өөрчлөх'
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
            attention: 'Анхаар',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Алдаа',
            genericerror: 'Ерөнхий алдаа',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Ерөнхий мэдээлэл',
            genericwarning: 'Ерөнхий анхааруулга',
            info: 'Мэдээлэл',
            more: '<em>More info</em>',
            success: 'Амжилт',
            warning: 'Анхааруулга'
        },
        patches: {
            apply: 'Хэрэгжүүлэх хэсэг',
            category: 'Ангилал',
            description: 'Тодорхойлолт',
            name: 'Нэр',
            patches: 'Хэсэг'
        },
        processes: {
            abortconfirmation: 'Та энэ процессыг цуцлахдаа итгэлтэй байна уу?',
            abortprocess: 'Зогсоох үйл явц',
            action: {
                advance: 'Урьдчилгаа',
                label: 'Үйлдэл'
            },
            activeprocesses: 'Идэвхтэй процессууд',
            allstatuses: 'Бүгд',
            editactivity: 'Үйл ажиллагааг өөрчлөх',
            openactivity: 'Нээлттэй үйл ажиллагаа',
            resumeprocesses: '<em>Processes that are not suspended cannot be resumed</em>',
            startworkflow: 'Эхлэх',
            workflow: 'Ажлын урсгал'
        },
        relationGraph: {
            activity: 'үйл ажиллагаа',
            allLabelsOnGraph: 'график дээрх бүх шошго',
            card: 'Карт',
            cardList: 'Картын жагсаалт',
            cardRelations: 'Картын холбоо',
            choosenaviagationtree: 'Навигацийн бүтцийг сонгоно уу',
            class: 'Класс',
            classList: 'Классын жагсаалт',
            compoundnode: 'Нийлмэл хэсэг',
            disable: 'Идэвхгүй болгох',
            edges: 'Ирмэгүүд',
            enable: 'Идэвхжүүлэх',
            labelsOnGraph: 'график дээрх зөвлөмж',
            level: 'Түвшин',
            nodes: 'Гол хэсэг',
            openRelationGraph: 'Нээлттэй харилцааны график',
            quantity: '<em>Quantity</em>',
            refresh: 'Сэргээх',
            relation: 'харилцаа',
            relationGraph: 'Харилцааны график',
            reopengraph: 'Гол хэсгээс графикийг дахин нээх'
        },
        relations: {
            adddetail: 'Тайлбар нэмэх',
            addrelations: 'Холбоос нэмэх',
            attributes: 'Шинж чанар',
            begindate: 'Эхлэх огноо',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Код',
            deletedetail: 'Дэлгэрэнгүйг устгах',
            deleterelation: 'Харилцаа холбоог устгах',
            deleterelationconfirm: 'Та энэ холболтыг устгахдаа итгэлтэй байна уу?',
            description: 'Тодорхойлолт',
            editcard: 'Картыг засах',
            editdetail: 'Дэлгэрэнгүйг засах',
            editrelation: 'Холбоосыг засах',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Өргөтгөсөн мэдээлэл',
            mditems: 'Эд зүйлс',
            missingattributes: 'Шаардлагатай шинж чанарууд алга байна',
            opencard: 'Холбогдох картыг нээх',
            opendetail: 'Дэлгэрэнгүйг харуулах',
            relationdata: '<em>Relation data</em>',
            type: 'Төрөл'
        },
        reports: {
            csv: 'CSV',
            download: 'Татаж авах',
            format: 'Формат',
            msgs: {
                creationerror: '<em>Something went wrong during the start of the report creation.</em>',
                creationstarted:
                    '<em>Report creation started. You will be notified when it will be ready for the download.</em>'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Хэвлэх',
            reload: 'Дахин ачаалах',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Анхдагч'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Өдөр бүр'
                        },
                        monthly: {
                            description: 'Сар бүр'
                        },
                        once: {
                            description: 'Нэг удаа'
                        },
                        weekly: {
                            description: 'Долоо хоног бүр'
                        },
                        yearly: {
                            description: 'Жил бүр'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Анхдагч'
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
            addThematism: 'Thematism нэмэх',
            analysisType: 'Шинжилгээний төрөл',
            attribute: 'Шинж чанар',
            bordersIntervals: '<em>Wrong configuration for intervals limits</em>',
            calculateRules: 'Загварын дүрмийг бий болгох',
            clearThematism: ' Thematism-г арилгах',
            color: 'Өнгө',
            defineLegend: 'Домогт тодорхойлолт',
            defineThematism: ' Thematism-н тодорхойлолт',
            deletethematism: '<em>Are you sure that you want to delete this thematism?</em>',
            editThematism: '<em>Edit rules configuration</em>',
            function: 'Чиг үүрэг',
            generate: 'Үүсгэх',
            geoAttribute: 'Газарзүйн шинж чанар',
            graduated: 'Төгссөн',
            highlightSelected: 'Сонгосон зүйлийг тодруулах',
            intervals: 'Завсарлага',
            legend: 'Домог',
            name: 'нэр',
            newThematism: 'Шинэ thematism',
            punctual: 'Тогтмол',
            quantity: 'Тооцох',
            segments: 'Сегментүүд',
            source: 'Эх үүсвэр',
            table: 'Хүснэгт',
            thematism: 'Сэдэвчилсэн байдал',
            value: 'Утга'
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
                addrow: 'Мөр нэмэх',
                clonerow: 'Клоны мар',
                datanotvalid: 'Өгөгдөл буруу байна',
                deleterow: 'Мөрийг устгах',
                editrow: 'Мөрийг засах',
                export: 'Экспортлох',
                import: 'Импорт',
                importexport: {
                    expattributes: 'Экспортлох өгөгдөл',
                    file: 'Файл',
                    filename: 'Файлын нэр',
                    format: 'Формат',
                    importmode: 'Импортын горим',
                    keyattributes: 'Гол шинж чанарууд',
                    missingkeyattr: 'Нэг гол шинж чанарыг сонгоно уу',
                    modeadd: 'Нэмэх',
                    modemerge: 'Нэгдэх',
                    modereplace: 'Солих',
                    separator: 'Тусгаарлагч'
                },
                refresh: 'Анхдагчаар сэргээх'
            },
            linkcards: {
                checkedonly: 'Нэгийг л шалгах',
                editcard: 'Картыг засах',
                opencard: 'Нээлттэй карт',
                refreshselection: 'Анхны сонголтыг хэрэгжүүлэх',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Сүлжээний шүүлтүүрийг идэвхгүй болгох',
                togglefilterenabled: 'Сүлжээний шүүлтүүрийг идэвхжүүлэх'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                '<em>Inline mode is not supported for this widget type. Please fix the widget configuration.</em>',
            required: 'Энэ виджет шаардлагатай.'
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
