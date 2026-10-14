(function () {
    Ext.define('CMDBuildUI.locales.bg.Locales', {
        requires: ['CMDBuildUI.locales.bg.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'bg',
        administration: CMDBuildUI.locales.bg.LocalesAdministration.administration,
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
            buttonplaceholder: 'Бутон',
            iframeplaceholder: 'Текстов редактор',
            languageselector: 'Селектор на език',
            resizepanel: {
                horizontally: 'Преоразмеряване на панелите хоризонтално',
                vertically: 'Вертикално преоразмеряване на панелите'
            },
            usermenu: 'Потребителско меню'
        },
        attachments: {
            add: 'Добавяне на прикачен файл',
            attachmenthistory: 'Хронология на прикачените файлове',
            author: 'Автор',
            authordescription: 'Описание на автора',
            browse: 'Преглед &hellip;',
            category: 'Категория',
            code: 'Код',
            creationdate: 'Дата на създаване',
            deleteattachment: 'Изтриване на прикачен файл',
            deleteattachment_confirmation: 'Наистина ли искате да изтриете този прикачен файл?',
            description: 'Описание',
            download: 'Изтегляне',
            dropfiles: 'Пуснете файловете тук',
            editattachment: 'Модифициране на прикачен файл',
            errordms: 'DMS е деактивиран и не е възможно да се записват файлове',
            file: 'Файл',
            filealreadyinlist: 'Файлът {0} вече е в списъка.',
            filename: 'Име на файл',
            fileview: 'Преглед на прикачения файл',
            invalidfiles: 'Премахване на невалидни файлове',
            majorversion: 'Основна версия',
            maxsize: 'Максималният позволен размер на файла е {0} МБ.',
            metadata: 'Метаданни',
            modificationdate: 'Дата на модификация',
            new: 'Нов прикачен файл',
            nocategory: 'Не категоризиран',
            preview: 'Преглед',
            removefile: 'Премахване на файл',
            statuses: {
                empty: 'Празен файл',
                error: 'Грешка',
                extensionNotAllowed: 'Не е разрешено разширение на файл',
                loaded: 'Заредени',
                ready: 'Готов',
                toolarge: 'Твърде голям'
            },
            successupload: '{0} прикачени файлове.',
            uploadfile: 'Качване на файл...',
            version: 'Версия',
            viewhistory: 'Преглед на хронологията за прикачените файлове',
            viewmetadata: 'Преглед на метаданните за прикачения файл',
            warningmessages: {
                atleast:
                    '{0}: е зареден {1} приставки от тип "{2}". Тази категория очаква най-малко {3} прикачени файлове ',
                exactlynumber: '{0}: е зареден {1} приставки от тип "{2}". Тази категория очаква {3} прикачени файлове',
                maxnumber:
                    '{0}: е зареден {1} привързаност от тип "{2}". Тази категория очаква най-много {3} прикачени файлове'
            },
            wrongfileextension: '{0} разширение на файла не е позволено'
        },
        bim: {
            bimViewer: 'Bim Viewer',
            card: {
                label: 'Карта'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Размер на ограничителната кутия по оста X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Размер на ограничителната кутия по оста Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Размер на ограничителната кутия по оста Z',
                calculated: 'Изчислено',
                description: 'Описание',
                guid: 'GUID',
                label: 'Имоти',
                name: 'Име',
                type: 'Тип',
                uuid: 'UUID'
            },
            layers: {
                label: 'Слоеве',
                menu: {
                    hideAll: 'Скрий всичко',
                    hideAllLayers: 'Скрийте всички слоеве',
                    hideAllXeokit: 'Скриване на всички {0}',
                    showAll: 'Покажи всичко',
                    showAllLayers: 'Показване на всички слоеве',
                    showAllXeokit: 'Покажи всички {0}'
                },
                name: 'Име',
                quantity: 'Количество',
                visibility: 'Видимост'
            },
            menu: {
                camera: 'Камера',
                disableslice: 'Деактивиране на обект с отрязък',
                enableslice: 'Разрешаване на срез обект',
                frontView: 'Изглед отпред',
                mod: 'Контроли на зрителя',
                ortho: 'Превключете към ортогонален изглед',
                orthographic: 'Ортографска камера',
                pan: 'Превъртете',
                panXeokit: 'Контроли на вюъра: превъртане (Shift)',
                perspe: 'Превключете към изглед в перспектива',
                perspective: 'Перспективна камера',
                resetSlice: 'Нулиране на отрязък',
                resetView: 'Нулиране на изгледа',
                rotate: 'Завъртане',
                rotateXeokit: 'Контроли на вюъра: завъртане',
                save: 'Запазване на изглед',
                shortcuts: {
                    down: 'Завъртете надолу',
                    left: 'Завърти наляво',
                    right: 'Завъртете надясно',
                    title: 'Комбинация от клавиши',
                    up: 'Завъртете нагоре',
                    zoomin: 'Увеличаване',
                    zoomout: 'Намаляване'
                },
                sideView: 'Страничен изглед',
                threeD: 'Превключете към 3D изглед',
                topView: 'Изглед отгоре',
                twoD: 'Превключете към 2D изглед'
            },
            navCube: {
                back: 'Назад',
                bottom: 'Отдолу',
                front: 'Отпред',
                left: 'Наляво',
                right: 'Правилно',
                top: 'Отгоре'
            },
            showBimCard: 'Отворете 3D визуализатора',
            tree: {
                arrowTooltip: 'Изберете елемент',
                label: 'Обекти',
                multipleElements: 'Изберете елементи',
                open_card: 'Отворете свързаната карта',
                root: 'Основен на Ifc'
            }
        },
        bulkactions: {
            abort: 'Прекъсване на избраните елементи',
            addfields: 'Добавяне на полета',
            alertdownloadattachments:
                'Някои прикачени файлове няма да бъдат изтеглени, защото все още не са запазени на сървъра',
            alertnoattachmentsdownload:
                'Няма да бъдат изтеглени прикачени файлове, тъй като те все още не са запазени на сървъра',
            cancelselection: 'Отмяна на селекцията',
            confirmabort: 'Прекратявате {0} процес екземпляри. Наистина ли искате да продължите?',
            confirmdelete: 'Изтривате {0} карти. Наистина ли искате да продължите?',
            confirmdeleteattachements: 'Изтривате {0} прикачени файлове. Наистина ли искате да продължите?',
            confirmedit: 'Модифицирате {0} за {1} карти. Наистина ли искате да продължите?',
            delete: 'Изтриване на избраните елементи',
            download: 'Изтегляне на избраните прикачени файлове',
            downloadall: 'Изтеглете всички прикачени файлове',
            edit: 'Редактиране на избраните елементи',
            missingfields: 'Следните полета трябва да бъдат добавени за правилно валидиране на формуляра: {0}',
            noattributeselected: 'Моля, изберете поне един атрибут.',
            selectall: 'Изберете всички елементи'
        },
        calendar: {
            active_expired: 'Активен/Изтекъл',
            add: 'Добавяне',
            advancenotification: 'Предварително уведомление',
            allcategories: 'Всички категории',
            alldates: 'Всички дати',
            associatedcard: 'Свързана карта',
            calculated: 'Изчислява',
            calendar: 'Календар',
            cancel: 'Маркирай като отменен',
            category: 'Категория',
            class: 'Клас',
            cm_confirmcancel: 'Наистина ли искате да маркирате като отменени избрани графици?',
            cm_confirmcomplete: 'Наистина ли искате да маркирате като компилиран избрани графици?',
            cm_markcancelled: 'Маркиране като отменени избрани графици',
            cm_markcomplete: 'Маркиране като завършени избрани графици',
            complete: 'Пълна',
            completed: 'Завършен',
            date: 'Дата',
            days: 'Дни',
            delaybeforedeadline: 'Забавяне преди крайния срок',
            delaybeforedeadlinevalue: 'Забавяне преди стойността на крайния срок',
            description: 'Описание',
            editevent: 'Редактиране на график',
            enddate: 'Крайна дата',
            endtype: 'Тип на края',
            event: 'График',
            execapplyfilter: 'Не може да се изпълни applyAdvanceFilter, защото има повече от едно хранилище',
            execremovefilter: 'Не може да се изпълни removeAdvanceFilter, защото има повече от едно хранилище',
            executiondate: 'Дата на изпълнение',
            frequency: 'Честота',
            frequencymultiplier: 'Мултипликатор на честотата',
            grid: 'Мрежа',
            leftdays: 'Оставащи дни',
            londdescription: 'Пълно описание',
            manual: 'Ръководство',
            maxactiveevents: 'Max активни събития',
            messagebodydelete: 'Искате ли да премахнете графика за планиране?',
            messagebodyplural: 'Има {0} правила за график',
            messagebodyrecalculate: 'Искате ли да преизчислите правилото за графика с новата дата?',
            messagebodysingular: 'Има {0} правило за график',
            messagetitle: 'График за преизчисляване',
            missingdays: 'Липсващи дни',
            next30days: 'Следващите 30 дни',
            next7days: 'Следващите 7 дни',
            notificationtemplate: 'Шаблон, използван за уведомяване',
            notificationtext: 'Текст на известие',
            occurencies: 'Брой събития',
            operation: 'Операция',
            partecipantgroup: 'Група за разделяне на части',
            partecipantuser: 'Потребител на разделител',
            priority: 'Приоритет',
            recalculate: 'Преизчисляване',
            referent: 'Референтен',
            scheduler: 'Списък',
            sequencepaneltitle: 'Генериране на графици',
            startdate: 'Начална дата',
            status: 'Статус',
            today: 'Днес',
            type: 'Тип',
            viewevent: 'Преглед на графика',
            waiteventgeneration: 'Генериране на събития. Моля, изчакайте да завършат.',
            widgetcriterion: 'Критерий за изчисляване',
            widgetemails: 'Имейли',
            widgetgotocard: 'Отидете на изходната карта',
            widgetsourcecard: 'Карта източник'
        },
        chat: {
            title: 'Чат'
        },
        classes: {
            cards: {
                addcard: 'Добавяне на карта',
                clone: 'Клонинг',
                clonewithrelations: 'Клониране на карта и връзки',
                deletebeaware: 'Имайте предвид, че:',
                deleteblocked: 'Не е възможно да се продължи с изтриването, защото има връзки с {0}.',
                deletecard: 'Изтриване на карта',
                deleteconfirmation: 'Наистина ли искате да изтриете тази карта?',
                deleterelatedcards: '{0} карти ще бъдат изтрити',
                deleterelations: 'връзките с {0} карти ще бъдат изтрити',
                label: 'Карти',
                modifycard: 'Модифициране на карта',
                opencard: 'Отваряне на карта',
                print: 'Печат на карта'
            },
            simple: 'Прост',
            standard: 'Стандартна'
        },
        common: {
            actions: {
                add: 'Добавете',
                apply: 'Приложете',
                cancel: 'Отменете',
                clear: 'Изчистване',
                close: 'Затваряне',
                copy: 'Копиране',
                copyerrormessage: 'Копирайте съобщението за грешка в клипборда',
                delete: 'Изтриване',
                edit: 'Редактиране',
                execute: 'Изпълнение',
                forward: 'Напред',
                help: 'Помощ',
                hide: '<em>Hide</em>',
                load: 'Зареждане',
                open: 'Отваряне',
                previous: 'Предишен',
                refresh: 'Обновяване на данни',
                remove: 'Премахване',
                save: 'Запис',
                saveandapply: 'Запис и прилагане',
                saveandclose: 'Запис и затваряне',
                search: 'Търсене',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Търсене...'
            },
            and: 'и',
            attributes: {
                boolfalse: 'НЕВЯРНО',
                booltrue: 'ВЯРНО',
                link: {
                    errlabel: 'Етикетът е задължителен',
                    errurl: 'URL адресът е задължителен, когато етикетът не е празен',
                    label: 'Етикет',
                    url: 'Url'
                },
                nogroup: 'База данни'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H:i: s',
                time: 'H:i: s'
            },
            editor: {
                clearhtml: 'Изчистване на HTML',
                expand: 'Разгъване на редактора',
                reduce: 'Намаляване на редактора',
                signature: 'Добавете подпис',
                unlink: 'Премахване на връзката',
                unlinkmessage: 'Преобразувайте избраната хипервръзка в текст.'
            },
            grid: {
                disablemultiselection: 'Забраняване на мулти избор',
                enamblemultiselection: 'Разрешаване на мулти избор',
                export: 'Експортиране на данни',
                filterremoved: 'Текущият филтър е премахнат',
                import: 'Импортиране на данни',
                itemnotfound: 'Елементът не е намерен',
                lastUpdate: '<em>Last update</em>',
                list: 'Списък',
                opencontextualmenu: 'Отваряне на контекстно меню',
                print: 'Печат',
                printcsv: 'Отпечатване като CSV файл',
                printmap: 'Отпечатайте карта',
                printodt: 'Отпечатване като ODT',
                printpdf: 'Отпечатване като PDF',
                row: 'Елемент',
                rows: 'Елементи',
                subtype: 'Подтип',
                thematism: 'Тематичен слой'
            },
            tabs: {
                activity: 'Дейност',
                attachment: 'Прикачен',
                attachments: 'Прикачени',
                card: 'Карта',
                clonerelationmode: 'Режим на Връзки за Клониране',
                details: 'Подробности',
                emails: 'Имейли',
                history: 'История',
                notes: 'Бележки',
                relations: 'Отношения',
                schedules: 'Графици',
                sources: 'Източници'
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
                download: 'Изтегляне на диаграма',
                downloaddata: 'Изтегляне на данни от диаграмата',
                gridhide: 'Скриване на мрежата от данни',
                gridshow: 'Показване на мрежата от данни',
                openinpopup: 'Отваряне в изскачащ прозорец',
                reload: 'Презареди',
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
            abort: 'Отменете',
            addattachmentsfromdocumentarchive: 'Добавяне на прикачени файлове от архива на документа',
            alredyexistfile: 'Вече съществува файл с това име',
            archivingdate: 'Дата на архивиране',
            attachfile: 'Прикачване на файл',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Ск',
            cc: 'Cc',
            composeemail: 'Съставяне на имейл',
            composefromtemplate: 'Композиране от шаблон',
            delay: 'Забавяне',
            delays: {
                day1: 'За 1 ден',
                days2: 'След 2 дни',
                days4: 'За 4 дни',
                hour1: '1 час',
                hours2: '2 часа',
                hours4: '4 часа',
                month1: 'За 1 месец',
                negativeday1: '1 ден преди',
                negativedays2: '2 дни преди',
                negativedays4: '4 дни преди',
                negativehour1: '1 час преди',
                negativehours2: '2 часа преди',
                negativehours4: '4 часа преди',
                negativemonth1: '1 месец преди',
                negativeweek1: '1 седмица преди',
                negativeweeks2: '2 седмици преди',
                none: 'Никой',
                week1: 'След 1 седмица',
                weeks2: 'След 2 седмици'
            },
            dmspaneltitle: 'Избор на прикачени файлове от база данни',
            edit: 'Редактиране',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'От',
            gridrefresh: 'Обновяване на мрежата',
            groupbystatus: 'Групирани по статус',
            keepsynchronization: 'Запазване синхронизацията',
            maxsize: 'Максималният общ разрешен размер за прикачените файлове е {0}.',
            message: 'Съобщение',
            regenerateallemails: 'Генерирай всички имейли',
            regenerateemail: 'Генериране на имейл',
            reloadconfirmationmessage: 'Имейлите в чернова ще бъдат изтрити. Искаш ли да продължиш?',
            remove: 'Премахнете',
            remove_confirmation: 'Наистина ли искате да изтриете този имейл?',
            reply: 'Отговор',
            replyprefix: '{1} wrote:',
            searchattachment: 'Търсене на прикачен файл',
            searchcard: 'Карта за търсене',
            selectaclass: 'Избор на клас',
            sendemail: 'Изпращане на имейл',
            sendingemail: 'Изпращане на имейл.',
            signature: 'Подпис',
            size: 'Размер',
            status: 'Статус',
            statuses: {
                draft: 'Проект',
                error: 'Грешка',
                new: 'Нов',
                outgoing: 'Изходящи',
                received: 'Получени',
                sent: 'Изпратени',
                skipped: 'Пропуснато'
            },
            subject: 'Предмет',
            templateattachments: 'Този имейл съдържа прикачени файлове, качени автоматично от шаблона',
            to: 'До',
            view: 'Разгледай'
        },
        errors: {
            actionnotallowed: 'Действието не е разрешено',
            autherror: 'Грешно потребителско име или парола',
            classnotfound: 'Клас {0} не е намерен',
            fieldrequired: 'Това поле е задължително',
            invalidfilter: 'Невалиден филтър',
            nodatatoexport: 'Диаграмата няма данни за експортиране',
            notfound: 'Артикулът не е намерен',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Действия',
            addfilter: 'Добавяне на филтър',
            any: 'Поне един',
            attachments: 'Прикачени файлове',
            attachmentssearchtext: 'Текст за търсене на прикачени файлове',
            attribute: 'Избор на атрибут',
            attributes: 'Атрибути',
            clearfilter: 'Изчистване на филтъра',
            clone: 'Клониране',
            copyof: 'Копие на',
            currentgroup: 'Текуща група',
            currentuser: 'Текущ потребител',
            defaultset: 'Задай по подразбиране',
            defaultunset: 'Изключено от подразбиране',
            description: 'Описание',
            domain: 'Домейн',
            errorname: 'Името на филтъра вече се използва',
            filterdata: 'Филтриране на данни',
            fromfilter: 'От филтъра',
            fromselection: 'От селекцията',
            group: 'Група',
            ignore: 'Игнорира',
            migrate: 'Мигриране',
            name: 'Име',
            newfilter: 'Нов филтър',
            noone: 'Нито един от',
            operator: 'Оператор',
            operators: {
                and: 'И',
                beginswith: 'Започва с',
                between: 'Между',
                contained: 'Съдържа',
                containedorequal: 'Съдържащи се или равни',
                contains: 'Съдържа',
                containsorequal: 'Съдържа или е равно на',
                descriptionbegin: 'Описанието започва с',
                descriptioncontains: 'Описанието съдържа',
                descriptionends: 'Описание завършва с',
                descriptionnotbegin: 'Описанието не започва с',
                descriptionnotcontain: 'Описание не съдържа',
                descriptionnotends: 'Описание не завършва с',
                different: 'Различни',
                doesnotbeginwith: 'Не започва с',
                doesnotcontain: 'Не съдържа',
                doesnotendwith: 'Не свършва с',
                endswith: 'Завършва с',
                equals: 'Равно на',
                greaterthan: 'По-голямо от',
                isnotnull: 'Не е празно',
                isnull: 'Е празно',
                lessthan: 'По-малко от',
                or: 'Или'
            },
            relations: 'Отношения',
            removeattribute: 'Премахване на атрибут',
            removeblock: 'Премахване на блок',
            type: 'Тип',
            typeinput: 'Входен Параметър',
            typeorchoosetheattributename: 'Въведете или изберете името на атрибута',
            user: 'Потребител',
            value: 'Стойност'
        },
        gis: {
            card: 'Карта',
            cardsMenu: 'Меню карти',
            code: 'Код',
            contextmenu: {
                coordinates: 'Получаване на координати',
                coordinatescopiedmessage: 'Координатите са копирани в клипборда'
            },
            description: 'Описание',
            extension: {
                errorCall: 'Грешка',
                noResults: 'Няма резултати'
            },
            externalServices: 'Външни услуги',
            geographicalAttributes: 'Гео атрибути',
            geoserverLayers: 'Слоеве на Геосървъра',
            labels: {
                all: 'Всички',
                alltitle: 'Показване на всички етикети',
                alltitleclass: 'Показване на всички етикети на класа',
                clear: 'Изчистване на търсенето',
                hidden: 'Скрит',
                hiddentitle: 'Скриване на етикети',
                hideinfowindow: 'Скриване на информационния прозорец',
                label: 'Етикети',
                search: 'Търсене на картата',
                selected: 'Избрано',
                selectedtitle: 'Показване на етикета за избрания елемент',
                showinfowindow: 'Показване на информационен прозорец'
            },
            layers: 'Слоеве',
            layersTab: {
                clearPreferences: 'Изчистване предпочитанията за слоеве',
                preferencesCleared: 'Предпочитанията за слоеве са изчистени',
                preferencesSaved: 'Предпочитанията за слоеве са запазени',
                savePreferences: 'Запазване на предпочитанията за слоеве',
                updatePreferences: 'Актуализирайте предпочитанията за слоеве'
            },
            list: 'Списък',
            longpresstitle: 'Геоелементи в областта',
            map: 'Карта',
            mapServices: 'Услуги за карта',
            menu: {
                font: 'Размер на шрифта',
                options: 'Опции'
            },
            position: 'Позиция',
            root: 'Основен',
            sourceMap: {
                default: 'Използвайте изглед по подразбиране',
                satellite: 'Използвайте сателитен изглед'
            },
            tree: 'Дърво',
            type: 'Тип',
            view: 'Разглеждане',
            zoom: 'Увеличаване'
        },
        history: {
            activityname: 'Име на дейност',
            activityperformer: 'Изпълнител на дейността',
            attributes: 'Атрибути ({0})',
            begindate: 'Начална дата',
            enddate: 'Крайна дата',
            fieldname: 'Име на поле',
            fulldata: 'Пълни данни',
            onlychanges: 'Само промени',
            otherrelations: 'Други отношения',
            processstatus: 'Статут',
            referencerelation: 'Референтни отношения',
            relation: 'Отношение',
            system: 'Система',
            type: 'Тип',
            user: 'Потребител',
            userdescription: 'Потребителско описание',
            value: 'Стойност',
            viewmode: 'Режим преглед'
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
            accessibility: 'Достъпност',
            animals: 'Животни',
            arrows: 'Стрелки',
            audiovideo: 'Аудио видео',
            automotive: 'Автомобилна',
            autumn: 'Есента',
            beverage: 'Напитка',
            buildings: 'Сгради',
            business: 'Бизнес',
            charity: 'Благотворителност',
            chat: 'Чат',
            chess: 'Шах',
            childhood: 'Детство',
            code: 'Код',
            communication: 'Комуникации',
            computers: 'Компютри',
            construction: 'Строителство',
            currency: 'Валута',
            datetime: 'Дата час',
            design: 'Дизайн',
            editors: 'Редактори',
            education: 'Образование',
            emoji: 'Емотикони',
            energy: 'Енергия',
            files: 'Файлове',
            finance: 'Финанси',
            food: 'Храна',
            fruitvegetable: 'Плодове зеленчуци',
            games: 'Игри',
            gamingtabletop: 'Настолни игри',
            gender: 'Пол',
            general: 'Основни',
            halloween: 'Хелоуин',
            hands: 'Ръце',
            hotel: 'Хотел',
            household: 'Домакински',
            images: 'Изображения',
            interfaces: 'Интерфейси',
            logistics: 'Логистика',
            maps: 'Карти',
            maritime: 'Морски',
            marketing: 'Маркетинг',
            mathematics: 'Математика',
            medical: 'Медицина',
            moving: 'Преместване',
            music: 'Музика',
            objects: 'Обекти',
            paymentsshopping: 'Плащания на покупки',
            political: 'Политически',
            religion: 'Религия',
            sciencefiction: 'Научна фантастика',
            security: 'Сигурност',
            shapes: 'Форми',
            shopping: 'Пазаруване',
            social: 'Социлани',
            spinners: 'Спинери',
            sports: 'Спорт',
            spring: 'Пролет',
            status: 'Статус',
            toggle: 'Превключване',
            travel: 'Пътуване',
            userspeople: 'Потребители хора',
            vehicles: 'Превозни средства',
            weather: 'Времето',
            winter: 'Зима'
        },
        importexport: {
            database: {
                uri: 'URI на базата данни',
                user: 'Потребител на база данни'
            },
            downloadreport: 'Изтеглете отчет',
            emailfailure: 'Възникна грешка при изпращане на имейл!',
            emailmessage: 'Прикачен отчет за импортиране на файл „{0}“ на дата {1}',
            emailsubject: 'Отчет за импортиране на данни',
            emailsuccess: 'Имейлът е изпратен успешно!',
            export: 'Експорт',
            exportalldata: 'Всички данни',
            exportfiltereddata: 'Само данни, съответстващи на мрежовия филтър',
            gis: {
                importconfiguration: 'Импортиране на конфигурация',
                shapeimportdisabled: 'Импорта на фигури не е активиран за този шаблон',
                shapeimportenabled: 'Импортирането на форми е активирано за този шаблон'
            },
            ifc: {
                card: 'Карта',
                project: 'Проект',
                sourcetype: 'Импорт от'
            },
            import: 'Импортиране',
            importresponse: 'Отговор за импортиране',
            response: {
                created: 'Създадени карти',
                deleted: 'Изтриване на карти',
                errors: 'Грешки',
                linenumber: 'Номер на реда',
                message: 'Съобщение',
                modified: 'Модифицирани карти',
                notprocessed: 'Необработени карти',
                processed: 'Обработени редове',
                recordnumber: 'Номер на записа',
                unchanged: 'Непроменени карти'
            },
            sendreport: 'Изпрати отчет',
            template: 'Шаблон',
            templatedefinition: 'Определение на шаблона'
        },
        joinviews: {
            active: 'Активен',
            addview: 'Добавяне на изглед',
            alias: 'Псевдоним',
            attribute: 'Атрибут',
            attributecustomization: 'Персонализиране на атрибути',
            attributes: 'Атрибути',
            attributeschoice: 'Избор на атрибути',
            attributesof: 'Атрибути на: {0}',
            createview: 'Създаване на изглед',
            datasorting: 'Сортиране на данни',
            delete: 'Изтрий',
            deleteview: 'Изтриване на изгледа',
            deleteviewconfirm: 'Наистина ли искате да изтриете този изглед?',
            description: 'Описание',
            disable: 'Деактивирайте',
            domainalias: 'Псевдоним на домейн',
            domainsof: 'Домейни на {0}',
            edit: 'Редактиране',
            editview: 'Редактиране конфигурацията на изгледа',
            enable: 'Активиране',
            fieldsets: 'Полеви набори',
            filters: 'Филтри',
            generalproperties: 'Общи свойства',
            group: 'Група',
            innerjoin: 'Вътрешно присъединяване',
            jointype: 'Тип присъединяване',
            joinview: 'Изглед от присъединяването',
            klass: 'Клас',
            manageview: 'Управление на изгледа',
            masterclass: 'Мастър клас',
            masterclassalias: 'Мастър клас псевдоним',
            name: 'Име',
            newjoinview: 'Нов изглед от присъединяването',
            outerjoin: 'Външно присъединяване',
            pleaseseleceavalidmasterclass: 'Моля, изберете валиден мастър клас',
            refreshafteredit: 'Искате ли да опресните страницата, за да видите промените?',
            selectatleastoneattribute: 'Моля, изберете поне един атрибут',
            selectdisplayattributegrid: 'Моля, изберете поне един атрибут за показване в мрежа и един в намалена мрежа',
            showingrid: 'Показване в мрежа',
            showinreducedgrid: 'Показване в намалена мрежа',
            targetalias: 'Псевдоним на целевия клас'
        },
        login: {
            buttons: {
                login: 'Вход',
                logout: 'Промяна на потребител'
            },
            fields: {
                group: 'Група',
                language: 'Език',
                password: 'Парола',
                tenants: 'Клиенти',
                username: 'Потребител Име'
            },
            loggedin: 'Влизане в системата',
            sso: {
                loginfailure: 'Неуспешен вход',
                loginwith: 'Влезте с {0}',
                or: 'или',
                usernotenabled: 'Потребителят не е активиран'
            },
            title: 'Вход',
            welcome: 'Добре дошли отново {0}.'
        },
        main: {
            administrationmodule: 'Модул за администриране',
            alerts: 'Сигнали',
            baseconfiguration: 'Базова конфигурация',
            cardlock: {
                lockedmessage: 'Не можете да редактирате тази карта',
                someone: 'Някой'
            },
            changegroup: 'Промяна на група',
            changetenant: 'Промяна {0}',
            confirmchangegroup: 'Наистина ли искате да промените групата?',
            confirmchangetenants: 'Наистина ли искате да промените активните клиенти?',
            confirmdisabletenant: 'Наистина ли искате да забраните флага Игнорирай клиентите?',
            confirmenabletenant: 'Наистина ли искате да разрешите флага Игнориране на клиентите?',
            description: 'Описание',
            ignoretenants: 'Игнорирай {0}',
            info: 'Информация',
            logo: {
                cmdbuild: 'Лого на CMDbuild',
                cmdbuildready2use: 'Лого на CMDbuild READY2USE',
                companylogo: 'Фирмено лого',
                hdaitsm: 'HDA ITSM',
                openmaint: 'Лого на openMAINT'
            },
            logout: 'Изход',
            managementmodule: 'Модул за управление на данни',
            multigroup: 'Мулти група',
            multitenant: 'Мулти {0}',
            navigation: 'Навигация',
            pagenotfound: 'Страницата не е намерена',
            password: {
                change: 'Промяна на паролата',
                confirm: 'Потвърждаване на паролата',
                email: 'Имейл адрес',
                err_confirm: 'Паролата не съвпада.',
                err_diffprevious: 'Паролата не може да е идентична с предишната.',
                err_diffusername: 'Паролата не може да бъде идентична с потребителското име.',
                err_length: 'Паролата трябва да бъде поне {0} знака.',
                err_reqdigit: 'Паролата трябва да съдържа поне една цифра.',
                err_reqlowercase: 'Паролата трябва да съдържа поне един долен знак.',
                err_requppercase: 'Паролата трябва да съдържа поне един главни знаци.',
                expired: 'Паролата ви е изтекла. Трябва да я промените сега.',
                forgotten: 'Забравих си паролата',
                new: 'Нова парола',
                old: 'Стара парола',
                recoverysuccess: 'Изпратихме ви имейл с инструкция за възстановяване на паролата ви.',
                reset: 'Нулиране на паролата',
                saved: 'Паролата е правилно записана!'
            },
            pleasecorrecterrors: 'Моля, коригирайте посочените грешки!',
            preferences: {
                comma: 'Запетая',
                decimalserror: 'Полето десетичен знак трябва да присъства',
                decimalstousandserror: 'Десетичен знак и разделител за хиляди трябва да бъдат различни',
                default: 'По подразбиране',
                defaultvalue: 'Стойност по подразбиране',
                favouritemenulocation: 'Местоположение на менюто "Предпочитани"',
                firstdayofweek: 'Първи ден от седмицата',
                gridpreferencesclear: 'Изчистване на предпочитанията за мрежата',
                gridpreferencescleared: 'Предпочитанията за мрежата са изчистени!',
                gridpreferencessave: 'Запиши предпочитанията за мрежата',
                gridpreferencessaved: 'Предпочитанията за мрежата са съхранени!',
                gridpreferencesupdate: 'Актуализиране на предпочитанията за мрежата',
                groupemailsbystatus: 'Показване на имейли, групирани по състояние',
                labelcsvseparator: 'CSV разделител',
                labeldateformat: 'Формат на датата',
                labeldecimalsseparator: 'Разделител за десетични числа',
                labellanguage: 'Език',
                labelthousandsseparator: 'Разделител за хиляди',
                labeltimeformat: 'Формат на времето',
                msoffice: 'Microsoft Office',
                no: 'Не',
                notificationslabel: 'Чат и известия',
                notificationssound: 'Пуснете звук за известия',
                notificationssoundvolume: 'Силата на звука на известията',
                period: 'Период',
                preferredfilecharset: 'Кодиране на CSV файла',
                preferredofficesuite: 'Предпочитан Офис пакет',
                sendcancellationperiod: 'Период за анулиране на изпращане на имейл (секунди)',
                showcollapsed: 'Показването е свито',
                showfovouritesafter: 'Показване след менюто за навигация',
                showfovouritesbefore: 'Показване преди навигационното меню',
                space: 'Пространство',
                thousandserror: 'Полето хиляди трябва да присъства',
                timezone: 'Часова зона',
                twelvehourformat: '12-часов формат',
                twentyfourhourformat: '24-часов формат',
                yes: 'Да'
            },
            searchinallitems: 'Търсене във всички елементи',
            searchtenant: 'Търсене на наемател',
            selectdeselettenants: 'Изберете / Отменете избора на всички',
            treenavcontenttitle: '{0} на {1}',
            userpreferences: 'Предпочитания'
        },
        menu: {
            allitems: 'Всички елементи',
            classes: 'Класове',
            custompages: 'Персонализирани страници',
            dashboards: 'Табла',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Любими',
            favouritesadd: 'Добави към любими',
            favouritesremove: 'Премахване от любими',
            homepage: '<em>Homepage</em>',
            processes: 'Процеси',
            reports: 'Отчети',
            setasinitialpage: 'Задай като начална страница',
            views: 'Изглед към'
        },
        mobile: {
            config: {
                configure: 'Конфигурирайте мобилно устройство',
                customercode: 'Код на клиента',
                devicename: 'Име на устройството',
                regenerate: 'Регенериране',
                serverurl: 'URL адрес на сървъра'
            }
        },
        notes: {
            edit: 'Модифициране на бележки'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Изтриване на известието',
            emptymessage: 'Няма известия за показване.',
            label: 'Известия',
            viewall: 'Виж всички'
        },
        notifier: {
            attention: 'Внимание',
            copied: 'Копирано',
            copiedtotheclipboard: 'Текстът е копиран в клипборда',
            error: 'Грешка',
            genericerror: 'Обща грешка',
            genericerrormessage: 'За повече информация, моля, проверете системните лог файлове.',
            genericinfo: 'Обща информация',
            genericwarning: 'Общо предупреждение',
            info: 'Информация',
            more: '<em>More info</em>',
            success: 'Успех',
            warning: 'Предупреждение'
        },
        patches: {
            apply: 'Прилагане на корекции',
            category: 'Категория',
            description: 'Описание',
            name: 'Име',
            patches: 'Патчове'
        },
        processes: {
            abortconfirmation: 'Наистина ли искате да прекратите този процес?',
            abortprocess: 'Прекратяване на процеса',
            action: {
                advance: 'Предварително',
                label: 'Действие'
            },
            activeprocesses: 'Активни процеси',
            allstatuses: 'Всички',
            editactivity: 'Промяна на активността',
            openactivity: 'Отворена дейност',
            resumeprocesses: 'Процесите, които не са спрени, не могат да бъдат възобновени',
            startworkflow: 'Започнете',
            workflow: 'Поток'
        },
        relationGraph: {
            activity: 'дейност',
            allLabelsOnGraph: 'всички етикети на графиката',
            card: 'Карта',
            cardList: 'Списък с Карти',
            cardRelations: 'Връзки с Карти',
            choosenaviagationtree: 'Избор на дървовидна лента',
            class: 'Клас',
            classList: 'Списък на Класовете',
            compoundnode: 'Комбиниран Възел',
            disable: 'Забранете',
            edges: 'Ръбове',
            enable: 'Разрешете',
            labelsOnGraph: 'подсказка на графиката',
            level: 'Ниво',
            nodes: 'Възли',
            openRelationGraph: 'Отворена Графика за Връзка',
            quantity: 'Количество',
            refresh: 'Обновяване',
            relation: 'Отношение',
            relationGraph: 'Графика за Връзка',
            reopengraph: 'Повторно отваряне на графиката от този възел'
        },
        relations: {
            adddetail: 'Добавяне на детайл',
            addrelations: 'Добавяне на връзки',
            attributes: 'Атрибути',
            begindate: 'Начална дата',
            cardnotpresentmessage:
                'Свързаната карта вече не е налична. Продължаването с модификацията ще изисква избор на друга карта.',
            code: 'Код',
            deletedetail: 'Изтриване на детайла',
            deleterelation: 'Изтриване на връзка',
            deleterelationconfirm: 'Наистина ли искате да изтриете тази връзка?',
            description: 'Описание',
            editcard: 'Редактиране на карта',
            editdetail: 'Редактиране на детайл',
            editrelation: 'Редактиране на връзка',
            errorexisting:
                'Връзката „{0}“ вече съществува. Редактирайте съществуващата или я изтрийте, преди да създадете нова връзка.',
            extendeddata: 'Разширени данни',
            mditems: 'Елементи',
            missingattributes: 'Липсващи задължителни атрибути',
            opencard: 'Отваряне на сродна карта',
            opendetail: 'Показване на детайлите',
            relationdata: 'Данни за връзка',
            type: 'Тип'
        },
        reports: {
            csv: 'CSV',
            download: 'Изтегли',
            format: 'Формат',
            msgs: {
                creationerror: 'Нещо се обърка при стартирането на създаването на отчета.',
                creationstarted:
                    'Създаването на отчет започна. Ще бъдете уведомени, когато ще бъде готово за изтегляне.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Печат',
            reload: 'Презаредете',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'По подразбиране'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Ежедневно'
                        },
                        monthly: {
                            description: 'Месечен'
                        },
                        once: {
                            description: 'Веднъж'
                        },
                        weekly: {
                            description: 'Седмичен'
                        },
                        yearly: {
                            description: 'Годишно'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'По подразбиране'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Никой'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Добавяне Тематичност',
            analysisType: 'Вид Анализ',
            attribute: 'Атрибут',
            bordersIntervals: 'Грешна конфигурация за ограничения на интервалите',
            calculateRules: 'Генериране на правила за стил',
            clearThematism: 'Изчистване на Тематичност',
            color: 'Цвят',
            defineLegend: 'Дефиниция на легендата',
            defineThematism: 'Дефиниране на слой',
            deletethematism: 'Сигурни ли сте, че искате да изтриете тази тематика?',
            editThematism: 'Редактирайте конфигурацията на правилата',
            function: 'Функция',
            generate: 'Генерира',
            geoAttribute: 'Геоатрибут',
            graduated: 'Завършва',
            highlightSelected: 'Осветяване на избрания елемент',
            intervals: 'Интервали',
            legend: 'Легенда',
            name: 'Име',
            newThematism: 'Нова Тематичност',
            punctual: 'Точни',
            quantity: 'Брой',
            segments: 'Сегменти',
            source: 'Източник',
            table: 'Таблица',
            thematism: 'Тематични слоеве',
            value: 'Стойност'
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
                    exitwithoutsave: 'Излезте, без да съхраните',
                    stayinedit: 'Назад за редактиране'
                },
                savebeforeexit:
                    'Има не запазени промени в прикачените файлове. Искате ли да ги запазите, преди да излезете?',
                saveprocessmessage:
                    'Прикачените файлове могат да се запазват само в съществуващи карти. При натискане на съхрани, записът на процеса ще бъде създаден с данните, въведени във формуляра.'
            },
            calendar: {
                error: 'Параметърът {0} не е зададен',
                errorwrong: 'Параметърът {0} не е зададен правилно'
            },
            customform: {
                addrow: 'Добавяне на ред',
                clonerow: 'Клониране на ред',
                datanotvalid: 'Данните не са валидни',
                deleterow: 'Изтриване на ред',
                editrow: 'Редактиране на ред',
                export: 'Експортиране на данни',
                import: 'Импортиране',
                importexport: {
                    expattributes: 'Данни за експортиране',
                    file: 'Файл',
                    filename: 'Име на файл',
                    format: 'Формат',
                    importmode: 'Режим на импортиране',
                    keyattributes: 'Ключови атрибути',
                    missingkeyattr: 'Моля, изберете поне един ключ атрибут',
                    modeadd: 'Добавяне',
                    modemerge: 'Обединяване',
                    modereplace: 'Замяна',
                    separator: 'Разделител'
                },
                refresh: 'Обнови по подразбиране'
            },
            linkcards: {
                checkedonly: 'Само проверени',
                editcard: 'Редактиране на карта',
                opencard: 'Отваряне на карта',
                refreshselection: 'Прилагане на избор по подразбиране',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Забраняване на филтъра на мрежата',
                togglefilterenabled: 'Разрешаване на филтър на мрежата'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                'Вграденият режим не се поддържа за този тип уиджет. Моля, поправете конфигурацията на уиджета.',
            required: 'Този уиджет е задължителен.'
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
