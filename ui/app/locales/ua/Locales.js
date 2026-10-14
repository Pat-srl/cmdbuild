(function () {
    Ext.define('CMDBuildUI.locales.ua.Locales', {
        requires: ['CMDBuildUI.locales.ua.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ua',
        administration: CMDBuildUI.locales.ua.LocalesAdministration.administration,
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
            add: 'Додати вкладення',
            attachmenthistory: 'Історія вкладень',
            author: 'Автор',
            authordescription: 'Опис автора',
            browse: 'Переглянути &hellip',
            category: 'Категорія',
            code: 'Код',
            creationdate: 'Дата створення',
            deleteattachment: 'Видалити вкладення',
            deleteattachment_confirmation: 'Ви впевнені, що хочете видалити це вкладення?',
            description: 'Опис',
            download: 'Завантажити',
            dropfiles: 'Перетягніть файл сюди',
            editattachment: 'Змінити вкладення',
            errordms: 'DMS вимкнено і неможливо зберегти файл',
            file: 'Файл',
            filealreadyinlist: 'Файл {0} вже є в списку',
            filename: "Ім'я файлу",
            fileview: 'Переглянути вкладення',
            invalidfiles: 'Видалити некоректні файли',
            majorversion: 'Основна версія',
            maxsize: 'Максимально допустимий розмір файлу {0} МБ.',
            metadata: 'Метадані',
            modificationdate: 'Дата зміни',
            new: 'Нове вкладення',
            nocategory: 'Без категорії',
            preview: 'Попередній перегляд',
            removefile: 'Видалити файл',
            statuses: {
                empty: 'Пустий файл',
                error: 'Помилка',
                extensionNotAllowed: 'Розширення файлу не допускається',
                loaded: 'Завантажено',
                ready: 'Готово',
                toolarge: 'Завеликий'
            },
            successupload: '{0} вкладень оновлено.',
            uploadfile: 'Завантажити файл...',
            version: 'Версія',
            viewhistory: 'Перегляд історії вкладень',
            viewmetadata: 'Переглянути метадані вкладення',
            warningmessages: {
                atleast:
                    '{0}: було завантажено {1} вкладення типу "{2}". Ця категорія потребує щонайменше {3} вкладень.',
                exactlynumber: '{0}: було завантажено {1} вкладення типу "{2}". Ця категорія потребує {3} вкладень.',
                maxnumber:
                    '{0}: було завантажено {1} вкладення типу "{2}". Ця категорія потребує щонайбільше {3} вкладень.'
            },
            wrongfileextension: 'Файл {0} має недозволене розширення'
        },
        bim: {
            bimViewer: 'Переглядач Bim',
            card: {
                label: 'Картка'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Розмір межі вздовж вісі X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Розмір межі вздовж вісі Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Розмір межі вздовж вісі Z',
                calculated: 'Розраховано',
                description: 'Опис',
                guid: 'GUID',
                label: 'Властивості',
                name: 'Назва',
                type: 'Тип',
                uuid: 'UUID'
            },
            layers: {
                label: 'Шари',
                menu: {
                    hideAll: 'Приховати все',
                    hideAllLayers: 'Приховати всі шари',
                    hideAllXeokit: 'Приховати всі {0}',
                    showAll: 'Показати все',
                    showAllLayers: 'Показати всі шари',
                    showAllXeokit: 'Показати всі {0}'
                },
                name: 'Назва',
                quantity: 'Кількість',
                visibility: 'Видимість'
            },
            menu: {
                camera: 'Камера',
                disableslice: 'Вимкнути ріжучий об’єкт',
                enableslice: 'Увімкнути ріжучий об’єкт',
                frontView: 'Вид спереду',
                mod: 'елементи керування перегляду',
                ortho: 'Перемкнути на ортогональний вид',
                orthographic: 'Ортогональна камера',
                pan: 'панорамування',
                panXeokit: 'Елементи керування переглядача: прокручування (Shift)',
                perspe: 'Перемкнути на вид у перспективі',
                perspective: 'Камера перспективи',
                resetSlice: 'Скинути нарізання',
                resetView: 'Відновити вид',
                rotate: 'повернути',
                rotateXeokit: 'Елементи керування переглядача: повертання',
                save: 'Зберегти вид',
                shortcuts: {
                    down: 'Повернути вниз',
                    left: 'Повернути вліво',
                    right: 'Повернути вправо',
                    title: 'Гарячі клавіши',
                    up: 'Повернути вгору',
                    zoomin: 'Наблизити',
                    zoomout: 'Віддалити'
                },
                sideView: 'Вид збоку',
                threeD: 'Перемкнути на 3D вид',
                topView: 'Вид зверху',
                twoD: 'Перемкнути на 2D вид'
            },
            navCube: {
                back: 'Ззаду',
                bottom: 'Знизу',
                front: 'Перед',
                left: 'Зліва',
                right: 'Справа',
                top: 'Згори'
            },
            showBimCard: 'Відкрити 3D-переглядач',
            tree: {
                arrowTooltip: 'Вибрати елемент',
                label: 'Дерево',
                multipleElements: '<em>Select elements</em>',
                open_card: "Відкрити пов'язану картку",
                root: 'Ifc Корінь'
            }
        },
        bulkactions: {
            abort: 'Відмінити для вибраних елементів',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                'Деякі вкладення не будуть завантажені, оскільки вони ще не збереглись на сервер.',
            alertnoattachmentsdownload: 'Вкладення не будуть завантажені, оскільки вони ще не збереглись на сервер.',
            cancelselection: 'Знати виділення',
            confirmabort: 'Ви відміняєте {0} процесів. Ви впевнені, що хочете продовжити?',
            confirmdelete: 'Ви видаляєте {0} карток. Ви впевнені, що хочете продовжити?',
            confirmdeleteattachements: 'Ви видаляєте {0} вкладень. Ви впевнені, що хочете продовжити?',
            confirmedit: 'Ви замінюєте {0} карток на {1}. Ви впевнені, що хочете продовжити?',
            delete: 'Видалити вибрані елементи',
            download: 'Завантажити вибрані вкладення',
            downloadall: 'Завантажити всі вкладення',
            edit: 'Редагувати вибрані елементи',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: 'Будь ласка виберіть принаймні один атрибут.',
            selectall: 'Вибрати всі елементи'
        },
        calendar: {
            active_expired: 'Активні/Прострочені',
            add: 'Додати розклад',
            advancenotification: 'Днів до попередження',
            allcategories: 'Всі категорії',
            alldates: 'Всі дати',
            associatedcard: 'Пов’язана картка',
            calculated: 'Розраховано',
            calendar: 'Календар',
            cancel: 'Позначити як відмінене',
            category: 'Категорія',
            class: 'Клас',
            cm_confirmcancel: 'Ви впевнені, що хочете позначити як відмінені виділені розклади?',
            cm_confirmcomplete: 'Ви впевнені, що хочете позначити як виконані виділені розклади?',
            cm_markcancelled: 'Позначити як відмінені виділені розклади',
            cm_markcomplete: 'Позначити як виконане виділені розклади',
            complete: 'Позначити як виконане',
            completed: 'Завершено',
            date: 'Дата',
            days: 'Дні',
            delaybeforedeadline: 'Затримка перед кінцевим терміном',
            delaybeforedeadlinevalue: 'Значення затримки перед кінцевим терміном',
            description: 'Опис',
            editevent: 'Редагувати розклад',
            enddate: 'Кінцева дата',
            endtype: 'Тип закінчення',
            event: 'Розклад',
            execapplyfilter: 'Неможливо виконати applyAdvanceFilter бо існує більше одного сховища',
            execremovefilter: 'Неможливо виконати removeAdvanceFilter бо існує більше одного сховища',
            executiondate: 'Дата виконання',
            frequency: 'Частота',
            frequencymultiplier: 'Множник частоти',
            grid: 'Таблиця',
            leftdays: 'Днів до початку',
            londdescription: 'Повний опис',
            manual: 'Вручну',
            maxactiveevents: 'Максимальна кількість активних розкладів',
            messagebodydelete: 'Бажаєте видалити правило розкладу?',
            messagebodyplural: 'Задано {0} правил розкладу',
            messagebodyrecalculate: 'Бажаєте перерозрахувати розклади з новою датою?',
            messagebodysingular: 'Задано {0} правило розкладу',
            messagetitle: 'Перерахунок розкладу',
            missingdays: 'Дні що випали',
            next30days: 'Наступні 30 днів',
            next7days: 'Наступні 7 днів',
            notificationtemplate: 'Шаблон для сповіщення',
            notificationtext: 'Текст повідомлення',
            occurencies: 'Кількість випадків',
            operation: 'Операція',
            partecipantgroup: 'Група учасників',
            partecipantuser: 'Користувач-учасник',
            priority: 'Пріоритет',
            recalculate: 'Перерозрахувати',
            referent: 'Референт',
            scheduler: 'Планувальник',
            sequencepaneltitle: 'Згенерувати розклад',
            startdate: 'Початкова дата',
            status: 'Статус',
            today: 'Сьогодні',
            type: 'Тип',
            viewevent: 'Переглянути розклад',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Критерій розрахунку',
            widgetemails: 'Emails',
            widgetgotocard: 'Перейти до картки джерела',
            widgetsourcecard: 'Картка-джерело'
        },
        chat: {
            title: 'Чат'
        },
        classes: {
            cards: {
                addcard: 'Додати картку',
                clone: 'Клонувати',
                clonewithrelations: "Клонувати картку та зв'язки",
                deletebeaware: 'Зауважте що:',
                deleteblocked: 'Неможливо видалити оскільки існують зв’язки з {0}',
                deletecard: 'Видалити картку',
                deleteconfirmation: 'Ви впевнені, що хочете видалити цю карту?',
                deleterelatedcards: 'разом з цим будуть видалені {0} пов’язаних карток',
                deleterelations: 'зв’язки з {0} картками будуть видалені',
                label: 'Картки',
                modifycard: 'Змінити картку',
                opencard: 'Відкрити картку',
                print: 'Друкувати картку'
            },
            simple: 'Простий',
            standard: 'Стандартний'
        },
        common: {
            actions: {
                add: 'Додати',
                apply: 'Застосувати',
                cancel: 'Скасувати',
                clear: 'Очистити',
                close: 'Закрити',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Видалити',
                edit: 'Редагувати',
                execute: 'Виконати',
                forward: '<em>Forward</em>',
                help: 'Довідка',
                hide: '<em>Hide</em>',
                load: 'Завантажити',
                open: 'Відкрити',
                previous: '<em>Previous</em>',
                refresh: 'Оновити дані',
                remove: 'Видалити',
                save: 'Зберегти',
                saveandapply: 'Зберегти і застосувати',
                saveandclose: 'Зберегти та закрити',
                search: 'Пошук',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Пошук…'
            },
            and: 'і',
            attributes: {
                boolfalse: 'ні',
                booltrue: 'так',
                link: {
                    errlabel: 'Підпис обов’язковий',
                    errurl: 'URL має бути заповнений, якщо підпис не пустий',
                    label: 'Підпис',
                    url: 'URL'
                },
                nogroup: 'Базові дані'
            },
            dates: {
                date: 'д/м/р',
                datetime: 'д/м/р г:х:с',
                time: 'г:х:с'
            },
            editor: {
                clearhtml: 'Очистити HTML',
                expand: 'Розгорнути редактор',
                reduce: 'Зменшити редактор',
                signature: 'Додати підпис',
                unlink: 'Відв’язати',
                unlinkmessage: 'Перетворити обране посилання на текст'
            },
            grid: {
                disablemultiselection: 'Вимкнути множинний вибір',
                enamblemultiselection: 'Увімкнути множинний вибір',
                export: 'Експортувати дані',
                filterremoved: 'Поточний фільтр був видалений',
                import: 'Імпортувати дані',
                itemnotfound: 'Елемент не знайдено',
                lastUpdate: '<em>Last update</em>',
                list: 'Список',
                opencontextualmenu: 'Відкрити контекстне меню',
                print: 'Друк',
                printcsv: 'Друкувати як CSV',
                printmap: 'Друкувати мапу',
                printodt: 'Друкувати як ODT',
                printpdf: 'Друкувати як PDF',
                row: 'Елемент',
                rows: 'Елементи',
                subtype: 'Підтип',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Активність',
                attachment: 'Вкладення',
                attachments: 'Вкладення',
                card: 'Картка',
                clonerelationmode: 'Режим клонування зв’язків',
                details: 'Подробиці',
                emails: 'Електронні листи',
                history: 'Історія',
                notes: 'Замітки',
                relations: "Зв'язки",
                schedules: 'Розклади',
                sources: 'Джерела'
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
                download: 'Завантажити діаграму',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Сховати таблицю з даними',
                gridshow: 'Показати таблицю з даними',
                openinpopup: 'Відкрити у вікні',
                reload: 'Перезавантажити',
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
            abort: 'Відміна',
            addattachmentsfromdocumentarchive: 'Додати вкладення з архіву документів',
            alredyexistfile: 'Файл з такою назвою вже існує',
            archivingdate: 'Дата архівації',
            attachfile: 'Прикріпити файл',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Прихована копія',
            cc: 'Копія',
            composeemail: 'Створити електронний лист',
            composefromtemplate: 'Створити з шаблону',
            delay: 'Затримка',
            delays: {
                day1: 'За 1 день',
                days2: 'За 2 дні',
                days4: 'За 4 дні',
                hour1: '1 година',
                hours2: '2 години',
                hours4: '4 години',
                month1: 'За 1 місяць',
                negativeday1: '1 день тому',
                negativedays2: '2 дні тому',
                negativedays4: '4 дні тому',
                negativehour1: '1 годину тому',
                negativehours2: '2 години тому',
                negativehours4: '4 години тому',
                negativemonth1: '1 місяць тому',
                negativeweek1: '1 тиждень тому',
                negativeweeks2: '2 тижні тому',
                none: 'Ніяка',
                week1: 'За 1 тиждень',
                weeks2: 'За 2 тижні'
            },
            dmspaneltitle: 'Обрати вкладення з бази даних',
            edit: 'Редагувати',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Від',
            gridrefresh: 'Оновити таблицю',
            groupbystatus: 'Згрупувати за статусом',
            keepsynchronization: 'Постійно синхронізувати',
            maxsize: 'Максимально допустимий розмір вкладення {0}.',
            message: 'Повідомлення',
            regenerateallemails: 'Відновити всі електронні адреси',
            regenerateemail: 'Відновити електронну адресу',
            reloadconfirmationmessage: 'Електронні листи в статусі чернетки будуть видалені. Хочете продовжити?',
            remove: 'Видалити',
            remove_confirmation: 'Точно видалити цей електронний лист?',
            reply: 'Відповісти',
            replyprefix: '{0} {1} писав:',
            searchattachment: 'Шукати вкладення',
            searchcard: 'Шукати картку',
            selectaclass: 'Виберіть клас',
            sendemail: 'Надіслати електронного листа',
            sendingemail: 'Відправка email.',
            signature: 'Підпис',
            size: 'Розмір',
            status: 'Статус',
            statuses: {
                draft: 'Чернетки',
                error: 'Помилки',
                new: 'Нове',
                outgoing: 'Вихідні',
                received: 'Отримані',
                sent: 'Відправлені',
                skipped: 'Пропущено'
            },
            subject: 'Тема',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'До',
            view: 'Переглянути'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: "Невірне ім'я користувача чи пароль",
            classnotfound: 'Клас {0} не знайдено',
            fieldrequired: "Це обов'язкове поле",
            invalidfilter: 'Некоректний фільтр',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Елемент не знайдено',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Дії',
            addfilter: 'Додати фільтр',
            any: '<em>At least one</em>',
            attachments: 'Вкладення',
            attachmentssearchtext: 'Пошук тексту у вкладеннях',
            attribute: 'Вибрати атрибут',
            attributes: 'Атрибути',
            clearfilter: 'Очистити фільтр',
            clone: 'Клонувати',
            copyof: 'Копія з',
            currentgroup: 'Поточна група',
            currentuser: 'Поточний користувач',
            defaultset: 'Встановити за замовчуванням',
            defaultunset: 'Відмінити за замовчуванням',
            description: 'Опис',
            domain: 'Домен',
            errorname: 'Назва фільтру вже використовуються',
            filterdata: 'Фільтрувати дані',
            fromfilter: 'З фільтру',
            fromselection: 'Із виділеного',
            group: 'Група',
            ignore: 'Ігнорувати',
            migrate: 'Мігрує',
            name: "Ім'я",
            newfilter: 'Новий фільтр',
            noone: 'Жодного',
            operator: 'Оператор',
            operators: {
                and: 'Та',
                beginswith: 'Починається з',
                between: 'Між',
                contained: 'Міститься',
                containedorequal: 'Міститься або рівний',
                contains: 'Містить',
                containsorequal: 'Містить або рівні',
                descriptionbegin: 'Опис починається з',
                descriptioncontains: 'Опис містить',
                descriptionends: 'Опис закінчується на',
                descriptionnotbegin: 'Опис не починається з',
                descriptionnotcontain: 'Опис не містить',
                descriptionnotends: 'Опис не закінчується на',
                different: 'Різний',
                doesnotbeginwith: 'Не починається з',
                doesnotcontain: 'Не містить',
                doesnotendwith: 'Не закінчується на',
                endswith: 'Закінчується на',
                equals: 'Рівний',
                greaterthan: 'Більше ніж',
                isnotnull: 'Не пусте',
                isnull: 'Пусте',
                lessthan: 'Менше ніж',
                or: 'Або'
            },
            relations: "Зв'язки",
            removeattribute: 'Прибрати атрибут',
            removeblock: 'Прибрати блок',
            type: 'Тип',
            typeinput: 'Вхідний параметр',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Користувач',
            value: 'Значення'
        },
        gis: {
            card: 'Картка',
            cardsMenu: 'Меню карток',
            code: 'Код',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Опис',
            extension: {
                errorCall: 'Помилка',
                noResults: 'Немає результатів'
            },
            externalServices: 'Зовнішні сервіси',
            geographicalAttributes: 'Географічні атрибути',
            geoserverLayers: 'Географічні шари',
            labels: {
                all: 'Всі',
                alltitle: 'Показати всі підписи',
                alltitleclass: 'Показати всі підписи класу',
                clear: '<em>Clear search</em>',
                hidden: 'Приховані',
                hiddentitle: 'Приховати підписи',
                hideinfowindow: 'Приховати вікно інформації',
                label: 'Підписи',
                search: '<em>Search on the map</em>',
                selected: 'Вибрані',
                selectedtitle: 'Показати підписи для обраних елементів',
                showinfowindow: 'Показати вікно інформації'
            },
            layers: 'Шари',
            layersTab: {
                clearPreferences: 'Скинути налаштування шарів',
                preferencesCleared: 'Налаштування шарів скинуто',
                preferencesSaved: 'Налаштування шарів збережено',
                savePreferences: 'Зберегти налаштування шарів',
                updatePreferences: 'Оновити налаштування шарів'
            },
            list: 'Список',
            longpresstitle: 'Геоелементи в області',
            map: 'Мапа',
            mapServices: 'Сервіси мапи',
            menu: {
                font: 'Розмір шрифту',
                options: 'Параметри'
            },
            position: 'Позиція',
            root: 'Корінь',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Дерево',
            type: 'Тип',
            view: 'Вид',
            zoom: 'Масштаб'
        },
        history: {
            activityname: 'Назва активності',
            activityperformer: 'Виконавець активності',
            attributes: 'Атрибути ({0})',
            begindate: 'Дата початку',
            enddate: 'Дата закінчення',
            fieldname: 'Назва поля',
            fulldata: 'Повні дані',
            onlychanges: 'Лише зміни',
            otherrelations: 'Інші відношення',
            processstatus: 'Статус',
            referencerelation: 'Референтні відношення',
            relation: 'відношення',
            system: 'Система',
            type: 'Тип',
            user: 'Користувач',
            userdescription: 'Опис користувача',
            value: 'Значення',
            viewmode: 'Режим перегляду'
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
                uri: 'URI бази даних',
                user: 'Користувач бази даних'
            },
            downloadreport: 'Звіт завантаження',
            emailfailure: 'Виникла помилка під час відправлення email!',
            emailmessage: 'Вкладений звіт імпорту файлу {0} на дату {1}',
            emailsubject: 'Звіт з імпорту даних',
            emailsuccess: 'Email було успішно відправлено!',
            export: 'Експортувати',
            exportalldata: 'Всі дані',
            exportfiltereddata: 'Лише дані, що відповідають фільтру таблиці',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Імпорт фігур не передбачено для цього шаблону',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: 'Картка',
                project: 'Проєкт',
                sourcetype: 'Імпортувати з'
            },
            import: 'Імпортувати',
            importresponse: 'Відповідь імпорту',
            response: {
                created: 'Створені картки',
                deleted: 'Видалені картки',
                errors: 'Помилки',
                linenumber: 'Номер рядка',
                message: 'Повідомлення',
                modified: 'Змінені картки',
                notprocessed: 'Неопрацьовані картки',
                processed: 'Опрацьовані рядки',
                recordnumber: 'Номер запису',
                unchanged: 'Не змінені картки'
            },
            sendreport: 'Відправити звіт',
            template: 'Шаблон',
            templatedefinition: 'Визначення шаблону'
        },
        joinviews: {
            active: 'Активний',
            addview: 'Додати представлення',
            alias: 'Псевдо',
            attribute: 'Атрибут',
            attributecustomization: 'Налаштування атрибутів',
            attributes: 'Атрибути',
            attributeschoice: 'Вибір атрибутів',
            attributesof: 'Атрибути: {0}',
            createview: 'Створити представлення',
            datasorting: 'Сортування даних',
            delete: 'Видалити',
            deleteview: 'Видалити представлення',
            deleteviewconfirm: 'Ви впевнені, що хочете видалити представлення?',
            description: 'Опис',
            disable: 'Відключити',
            domainalias: 'Псевдо домену',
            domainsof: 'Домени {0}',
            edit: 'Редагувати',
            editview: 'Налаштування вікна редагування',
            enable: 'Увімкнути',
            fieldsets: 'Набір полів',
            filters: 'Фільтри',
            generalproperties: 'Основні властивості',
            group: 'Група',
            innerjoin: 'Внутрішнє об’єднання',
            jointype: 'Тип об’єднання',
            joinview: 'Представлення з об’єднання',
            klass: 'Клас',
            manageview: 'Керувати представленням',
            masterclass: 'Головний клас',
            masterclassalias: 'Псевдо головного класу',
            name: 'Назва',
            newjoinview: 'Нове представлення з об’єднання',
            outerjoin: 'Зовнішнє об’єднання',
            pleaseseleceavalidmasterclass: 'Будь ласка оберіть коректний головний клас',
            refreshafteredit: 'Бажаєте оновити сторінку щоб побачити зміни?',
            selectatleastoneattribute: 'Будь ласка, виберіть принаймні один атрибут',
            selectdisplayattributegrid:
                'Будь ласка, виберіть принаймні один атрибут для відображення в таблиці і один в скороченій таблиці',
            showingrid: 'Показувати в таблиці',
            showinreducedgrid: 'Показувати в стислій таблиці',
            targetalias: 'Псевдо цільового класу'
        },
        login: {
            buttons: {
                login: 'Увійти',
                logout: 'Змінити користувача'
            },
            fields: {
                group: 'Група',
                language: 'Мова',
                password: 'Пароль',
                tenants: 'Орендарі',
                username: "Ім'я користувача"
            },
            loggedin: 'Авторизовано',
            sso: {
                loginfailure: 'Помилка входу в систему',
                loginwith: 'Увійти як {0}',
                or: 'або',
                usernotenabled: 'Користувач не активний'
            },
            title: 'Вхід',
            welcome: 'З поверненням, {0}.'
        },
        main: {
            administrationmodule: 'Модуль адміністрування',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Базова конфігурація',
            cardlock: {
                lockedmessage: 'Ви не можете редагувати цю картку, оскільки її редагує {0}.',
                someone: 'хтось'
            },
            changegroup: 'Змінити групу',
            changetenant: 'Змінити {0}',
            confirmchangegroup: 'Ви точно хочете змінити групу?',
            confirmchangetenants: 'Ви точно хочете змінити активних орендарів?',
            confirmdisabletenant: 'Ви точно хочете вимкнути прапорець "Ігнорувати орендарів"?',
            confirmenabletenant: 'Ви точно хочете ввімкнути прапорець "Ігнорувати орендарів"?',
            description: 'Опис',
            ignoretenants: 'Ігнорувати {0}',
            info: 'Інформація',
            logo: {
                cmdbuild: 'Логотип CMDBuild',
                cmdbuildready2use: 'Логотип CMDBuild READY2USE',
                companylogo: 'Логотип компанії',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'Логотип openMAINT'
            },
            logout: 'Вийти',
            managementmodule: 'Модуль керування даними',
            multigroup: 'Множинна група',
            multitenant: 'Множинна {0}',
            navigation: 'Навігація',
            pagenotfound: 'Сторінку не знайдено',
            password: {
                change: 'Змінити пароль',
                confirm: 'Підтвердити пароль',
                email: 'E-mail адреса',
                err_confirm: 'Паролі не збігаються',
                err_diffprevious: 'Пароль не може збігатися з попереднім',
                err_diffusername: 'Пароль не може збігатися з іменем користувача',
                err_length: 'Пароль повинен містити не менше {0} символів',
                err_reqdigit: 'Пароль повинен містити принаймні одну цифру',
                err_reqlowercase: 'Пароль повинен містити принаймні одну малу літеру',
                err_requppercase: 'Пароль повинен містити принаймні одну велику літеру',
                expired: 'Час дії паролю сплив. Ви маєте змінити його зараз',
                forgotten: 'Я забув свій пароль',
                new: 'Новий пароль',
                old: 'Старий пароль',
                recoverysuccess: 'Ми відправили вам email з інструкціями для відновлення паролю.',
                reset: 'Скинути пароль',
                saved: 'Пароль успішно збережено!'
            },
            pleasecorrecterrors: 'Будь ласка, виправте вказані помилки!',
            preferences: {
                comma: 'Кома',
                decimalserror: 'Поле десяткових знаків має бути присутнім',
                decimalstousandserror: 'Роздільники груп розрядів і десяткового дробу мають відрізнятися',
                default: 'За замовчуванням',
                defaultvalue: 'Значення за замовчанням',
                favouritemenulocation: 'Обрані пункти меню',
                firstdayofweek: 'Перший день тижня',
                gridpreferencesclear: 'Очистити налаштування таблиці',
                gridpreferencescleared: 'Налаштування таблиці очищено!',
                gridpreferencessave: 'Зберегти налаштування таблиці',
                gridpreferencessaved: 'Налаштування таблиці збережено!',
                gridpreferencesupdate: 'Оновити налаштування таблиці',
                groupemailsbystatus: 'Показати листи згруповані за статусом',
                labelcsvseparator: 'CSV розділювач',
                labeldateformat: 'Формат дати',
                labeldecimalsseparator: 'Роздільник десяткового дробу',
                labellanguage: 'Мова',
                labelthousandsseparator: 'Роздільник груп розрядів',
                labeltimeformat: 'Формат часу',
                msoffice: 'Microsoft Office',
                no: 'Ні',
                notificationslabel: 'Чат і Сповіщення',
                notificationssound: 'Програвати звук для сповіщень',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Крапка',
                preferredfilecharset: 'CSV кодування',
                preferredofficesuite: 'Бажаний офісний пакет',
                sendcancellationperiod: 'Час відміни відправки email (секунди)',
                showcollapsed: 'Показати згорнуто',
                showfovouritesafter: 'Показати після меню навігації',
                showfovouritesbefore: 'Показати перед меню навігації',
                space: 'Пробіл',
                thousandserror: 'Поле тисячних знаків має бути присутнім',
                timezone: 'Часовий пояс',
                twelvehourformat: '12-годинний формат',
                twentyfourhourformat: '24-годинний формат',
                yes: 'Так'
            },
            searchinallitems: 'Пошук в усіх елементах',
            searchtenant: 'Шукати наймача',
            selectdeselettenants: 'Вибрати все/Зняти виділення',
            treenavcontenttitle: '{0} з {1}',
            userpreferences: 'Налаштування'
        },
        menu: {
            allitems: 'Всі елементи',
            classes: 'Класи',
            custompages: 'Спеціальні сторінки',
            dashboards: 'Інформаційні панелі',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Обране',
            favouritesadd: 'Додоати до обраного',
            favouritesremove: 'Видалити з обраного',
            homepage: '<em>Homepage</em>',
            processes: 'Процеси',
            reports: 'Звіти',
            setasinitialpage: 'Встановити початковою сторінкою',
            views: 'Вигляди'
        },
        mobile: {
            config: {
                configure: 'Налаштувати мобільний пристрій',
                customercode: 'Код споживача',
                devicename: 'Назва пристрою',
                regenerate: 'Перегенерувати',
                serverurl: 'URL сервера'
            }
        },
        notes: {
            edit: 'Змінити замітку'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Видалити сповіщення',
            emptymessage: 'Немає сповіщень для відображення',
            label: 'Сповіщення',
            viewall: 'Відобразити всі'
        },
        notifier: {
            attention: 'Увага',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Помилка',
            genericerror: 'Загальна помилка',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Загальне повідомлення',
            genericwarning: 'Загальне попередження',
            info: 'Повідомлення',
            more: '<em>More info</em>',
            success: 'Успіх',
            warning: 'Попередження'
        },
        patches: {
            apply: 'Застосувати латки',
            category: 'Категорія',
            description: 'Опис',
            name: 'Назва',
            patches: 'Латки'
        },
        processes: {
            abortconfirmation: 'Ви впевнені, що хочете скасувати цей процес?',
            abortprocess: 'Скасувати процес',
            action: {
                advance: 'Просування',
                label: 'Дія'
            },
            activeprocesses: 'Активні процеси',
            allstatuses: 'Всі',
            editactivity: 'Змінити активність',
            openactivity: 'Відкрити активність',
            resumeprocesses: 'Процес, який не зупиняли, не може бути продовжений',
            startworkflow: 'Запустити',
            workflow: 'Робочий процес'
        },
        relationGraph: {
            activity: 'активність',
            allLabelsOnGraph: 'всі підписи на графі',
            card: 'Картка',
            cardList: 'Список карток',
            cardRelations: 'Відношення картки',
            choosenaviagationtree: 'Обрати навігаційне дерево',
            class: 'Клас',
            classList: 'Список класів',
            compoundnode: 'Складений вузол',
            disable: 'Вимкнути',
            edges: 'Грані',
            enable: 'Увімкнути',
            labelsOnGraph: 'підказка на графі',
            level: 'Рівень',
            nodes: 'Вузли',
            openRelationGraph: 'Відкрити граф відношень',
            quantity: 'Кількість',
            refresh: 'Оновити',
            relation: 'відношення',
            relationGraph: 'Граф відношень',
            reopengraph: 'Перевідкрити граф із цього вузла'
        },
        relations: {
            adddetail: 'Додати детальне',
            addrelations: 'Додати відношення',
            attributes: 'Атрибути',
            begindate: 'Дата початку',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Код',
            deletedetail: 'Видалити детальне',
            deleterelation: 'Видалити відношення',
            deleterelationconfirm: 'Ви впевнені, що хочете видалити цей зв’язок?',
            description: 'Опис',
            editcard: 'Редагувати картку',
            editdetail: 'Редагувати детальне',
            editrelation: 'Редагувати відношення',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Розширені дані',
            mditems: 'елементи',
            missingattributes: 'Відсутні обов’язкові атрибути',
            opencard: "Відкрити пов'язану картку",
            opendetail: 'Показати детальне',
            relationdata: 'Дані відношення',
            type: 'Тип'
        },
        reports: {
            csv: 'CSV',
            download: 'Завантажити',
            format: 'Формат',
            msgs: {
                creationerror: 'Щось пішло не так під час старту формування звіту',
                creationstarted:
                    'Створення звіту розпочато. Вас буде повідомленно, коли він буде готовий до вивантаження'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Друк',
            reload: 'Перезавантажити',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'За замовчуванням'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Щодня'
                        },
                        monthly: {
                            description: 'Щомісяця'
                        },
                        once: {
                            description: 'Один раз'
                        },
                        weekly: {
                            description: 'Щотижня'
                        },
                        yearly: {
                            description: 'Щороку'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'За замовчуванням'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Жоден'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Додати тематизм',
            analysisType: 'Тип аналізу',
            attribute: 'Атрибут',
            bordersIntervals: 'Неправильні налаштування меж інтервалів',
            calculateRules: 'Генерувати правила стилів',
            clearThematism: 'Очистити тематизм',
            color: 'Колір',
            defineLegend: 'Визначення легенди',
            defineThematism: 'Визначення тематизму',
            deletethematism: 'Ви впевнені, що хочете видалити цей тематизм?',
            editThematism: 'Конфігурація редагування правил',
            function: 'Функція',
            generate: 'Генерувати',
            geoAttribute: 'Географічний атрибут',
            graduated: 'Градуйований',
            highlightSelected: 'Підсвітити виділений елемент',
            intervals: 'Інтервальний',
            legend: 'Легенда',
            name: "Ім'я",
            newThematism: 'Новий тематизм',
            punctual: 'Точковий',
            quantity: 'Кількість',
            segments: 'Сегменти',
            source: 'Джерело',
            table: 'Таблиця',
            thematism: 'Тематизми',
            value: 'Значення'
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
                    exitwithoutsave: 'Вийти без збереження',
                    stayinedit: 'Повернутися до редагування'
                },
                savebeforeexit: 'У вкладеннях є незбережені зміни. Бажаєте зберегти перед виходом?',
                saveprocessmessage:
                    'Вкладення можна зберегти лише у наявні картки. Натискаючи на зберегти буде створено екземпляр процесу з даними введеними у формі.'
            },
            calendar: {
                error: 'Параметр {0} не встановлено',
                errorwrong: 'Параметр {0} встановлено не правильно'
            },
            customform: {
                addrow: 'Додати рядок',
                clonerow: 'Клонувати рядок',
                datanotvalid: 'Некоректні дані',
                deleterow: 'Видалити рядок',
                editrow: 'Редагувати рядок',
                export: 'Експортувати',
                import: 'Імпортувати',
                importexport: {
                    expattributes: 'Дані для експорту',
                    file: 'Файл',
                    filename: "Ім'я файлу",
                    format: 'Формат',
                    importmode: 'Режим імпорту',
                    keyattributes: 'Ключові атрибути',
                    missingkeyattr: 'Будь ласка, оберіть щонайменше один ключовий атрибут',
                    modeadd: 'Додати',
                    modemerge: 'Злити',
                    modereplace: 'Замінити',
                    separator: 'Розділювач'
                },
                refresh: 'Оновити до налаштувань за замовчанням'
            },
            linkcards: {
                checkedonly: 'Лише перевірені',
                editcard: 'Змінити картку',
                opencard: 'Відкрити картку',
                refreshselection: 'Застосувати вибір за замовчуванням',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Вимкнути фільтр в таблиці',
                togglefilterenabled: 'Увімкнути фільтр в таблиці'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                'Режим в-рядок не підтримується для цього типу віджетів. Будь ласка виправте налаштування віджету.',
            required: 'Цей віджет потрібний.'
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
