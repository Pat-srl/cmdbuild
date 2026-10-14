(function () {
    Ext.define('CMDBuildUI.locales.ru.Locales', {
        requires: ['CMDBuildUI.locales.ru.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ru',
        administration: CMDBuildUI.locales.ru.LocalesAdministration.administration,
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
            buttonplaceholder: 'Кнопка',
            iframeplaceholder: 'Текстовый редактор',
            languageselector: 'Выбор языка',
            resizepanel: {
                horizontally: 'Изменить размер панелей по горизонтали',
                vertically: 'Изменить вертикальный размер панелей'
            },
            usermenu: 'Меню пользователя'
        },
        attachments: {
            add: 'Вложить файл',
            attachmenthistory: 'История Привязанности',
            author: 'Автор',
            authordescription: 'Описание автора',
            browse: 'Просмотр и ад;',
            category: 'Категория',
            code: 'Код',
            creationdate: 'Дата создания',
            deleteattachment: 'Удаление вложения',
            deleteattachment_confirmation: 'Вы уверены, что хотите удалить это вложение?',
            description: 'Описание',
            download: 'Скачать',
            dropfiles: 'Удаление файлов здесь',
            editattachment: 'Изменить вложение',
            errordms: 'DMS отключен и невозможно сохранять файлы',
            file: 'Файл',
            filealreadyinlist: 'Файл {0} уже в списке.',
            filename: 'Имя файла',
            fileview: 'Просмотреть вложение',
            invalidfiles: 'Удаление недействительных файлов',
            majorversion: 'Основная версия',
            maxsize: 'Максимально допустимый размер файла составляет {0} МБ.',
            metadata: 'Метаданные',
            modificationdate: 'Дата изменения',
            new: 'Новое вложение',
            nocategory: 'Без категории',
            preview: 'Предварительный просмотр',
            removefile: 'Удалить файл',
            statuses: {
                empty: 'Пустой файл',
                error: 'Ошибка',
                extensionNotAllowed: 'Расширение файла не допускается',
                loaded: 'Загружен',
                ready: 'Готовый',
                toolarge: 'Слишком большой'
            },
            successupload: '{0} загруженных вложений.',
            uploadfile: 'Загрузить файл…',
            version: 'Версия',
            viewhistory: 'Просмотр истории вложений',
            viewmetadata: 'Просмотр метаданных прикрепленных файлов',
            warningmessages: {
                atleast:
                    '{0}: был загружен {1} вложения типа "{2}". Эта категория ожидает, по крайней мере, {3} вложений',
                exactlynumber: '{0}: был загружен {1} вложения типа "{2}". Эта категория ожидает {3} вложений',
                maxnumber:
                    '{0}: было загружено {1} вложение типа "{2}". Эта категория ожидает в большинстве {3} вложений'
            },
            wrongfileextension: '{0} расширение файла не допускается'
        },
        bim: {
            bimViewer: 'Bim Зритель',
            card: {
                label: 'Карта'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Размер ограничивающей рамки по оси X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Размер ограничивающей рамки по оси Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Размер ограничивающей рамки по оси Z',
                calculated: 'Рассчитано',
                description: 'Описание',
                guid: 'GUID',
                label: 'Свойства',
                name: 'Имя',
                type: 'Тип',
                uuid: 'UUID'
            },
            layers: {
                label: 'Слои',
                menu: {
                    hideAll: 'Скрыть все',
                    hideAllLayers: 'Скрыть все слои',
                    hideAllXeokit: 'Скрыть все {0}',
                    showAll: 'Показать все',
                    showAllLayers: 'Показать все слои',
                    showAllXeokit: 'Показать все {0}'
                },
                name: 'Имя',
                quantity: 'Количество',
                visibility: 'Видимость'
            },
            menu: {
                camera: 'Камера',
                disableslice: 'Отключить объект среза',
                enableslice: 'Включить объект среза',
                frontView: 'Вид спереди',
                mod: 'Средство просмотра',
                ortho: 'Перейти к ортогональному виду',
                orthographic: 'Ортографическая камера',
                pan: 'Переместить',
                panXeokit: 'Управление программой просмотра: прокрутка (Shift)',
                perspe: 'Перейти в перспективный вид',
                perspective: 'Перспективная камера',
                resetSlice: 'Сбросить срез',
                resetView: 'Сбросить Представление',
                rotate: 'Повернуть',
                rotateXeokit: 'Средство просмотра: повернуть',
                save: 'Сохранить вид',
                shortcuts: {
                    down: 'Повернуть вниз',
                    left: 'Поверните влево',
                    right: 'Повернуть вправо',
                    title: 'Горячие клавиши',
                    up: 'Повернуть вверх',
                    zoomin: 'Увеличить',
                    zoomout: 'Уменьшить'
                },
                sideView: 'Вид сбоку',
                threeD: 'Переключиться на 3D-вид',
                topView: 'Вид сверху',
                twoD: 'Перейти в 2D-вид'
            },
            navCube: {
                back: 'Назад',
                bottom: 'В низу',
                front: 'Фронт',
                left: 'Левый',
                right: 'Правильно',
                top: 'Сверху'
            },
            showBimCard: 'Открытый 3D-зритель',
            tree: {
                arrowTooltip: 'Выберите элемент',
                label: 'Объекты',
                multipleElements: 'Выберите элементы',
                open_card: 'Открыть соответствующую карту',
                root: 'Корень Ifc'
            }
        },
        bulkactions: {
            abort: 'Прерывание выбранных элементов',
            addfields: 'Добавить поля',
            alertdownloadattachments:
                'Некоторые вложения не будут загружены, потому что они еще не были сохранены на сервере.',
            alertnoattachmentsdownload:
                'Никакие вложения не будут загружены, потому что они еще не были сохранены на сервере',
            cancelselection: 'Отмена выбора',
            confirmabort: 'Вы прерываете {0} экземплярах процесса. Вы уверены, что хотите продолжить?',
            confirmdelete: 'Вы удаляете {0} карты. Вы уверены, что хотите продолжить?',
            confirmdeleteattachements: 'Вы удаляете {0} вложения. Вы уверены, что хотите продолжить?',
            confirmedit: 'Вы изменяете {0} для {1} карт. Вы уверены, что хотите продолжить?',
            delete: 'Удаление выбранных элементов',
            download: 'Скачать выбранные вложения',
            downloadall: 'Скачать все вложения',
            edit: 'Редактировать выбранные элементы',
            missingfields: 'Для корректной проверки формы необходимо добавить следующие поля: {0}',
            noattributeselected: 'Пожалуйста, выберите хотя бы один атрибут.',
            selectall: 'Выберите все элементы'
        },
        calendar: {
            active_expired: 'Активный/Просроченный',
            add: 'Добавить',
            advancenotification: 'Предварительное уведомление',
            allcategories: 'Все категории',
            alldates: 'Все даты',
            associatedcard: 'Связанная карта',
            calculated: 'Рассчитывается',
            calendar: 'Календарь',
            cancel: 'Отметка как отмененная',
            category: 'Категории',
            class: 'Класс',
            cm_confirmcancel: 'Вы уверены, что хотите отметить как отмененные выбранные расписания?',
            cm_confirmcomplete: 'Вы уверены, что хотите отметить как выполненные выбранные графики?',
            cm_markcancelled: 'Отметка как отмененные выбранные расписания',
            cm_markcomplete: 'Отметьте как заполненные выбранные расписания',
            complete: 'Полный',
            completed: 'Завершена',
            date: 'Дата',
            days: 'Дней',
            delaybeforedeadline: 'Задержка до крайнего срока',
            delaybeforedeadlinevalue: 'Задержка до срока значение',
            description: 'Описание',
            editevent: 'Изменить расписание',
            enddate: 'Дата окончания',
            endtype: 'Конечный тип',
            event: 'Расписание',
            execapplyfilter: 'Невозможно выполнить applyAdvanceFilter, так как существует более одного хранилища',
            execremovefilter: 'Невозможно выполнить removeAdvanceFilter, так как существует более одного хранилища',
            executiondate: 'Дата исполнения',
            frequency: 'Частота',
            frequencymultiplier: 'Множитель частоты',
            grid: 'Сетки',
            leftdays: 'Дни, чтобы пойти',
            londdescription: 'Полное описание',
            manual: 'Вручную',
            maxactiveevents: 'Макс активных событий',
            messagebodydelete: 'Хотите удалить правило планировщиков?',
            messagebodyplural: 'Есть правила {0} расписания',
            messagebodyrecalculate: 'Хотите пересчитать правило расписаний с новой датой?',
            messagebodysingular: 'Существует правило {0} расписания',
            messagetitle: 'Перерасчет расписания',
            missingdays: 'Отсутствующие дни',
            next30days: 'Следующие 30 дней',
            next7days: 'Следующие 7 дней',
            notificationtemplate: 'Шаблон, используемый для уведомления',
            notificationtext: 'Текст уведомления',
            occurencies: 'Количество происшествий',
            operation: 'Операции',
            partecipantgroup: 'Группа участников',
            partecipantuser: 'Пользователь-участник',
            priority: 'Приоритет',
            recalculate: 'Пересчитать',
            referent: 'Референт',
            scheduler: 'Планировщик',
            sequencepaneltitle: 'Создание расписаний',
            startdate: 'Дата начала',
            status: 'Статус',
            today: 'Сегодня',
            type: 'Тип',
            viewevent: 'Посмотреть расписание',
            waiteventgeneration: 'Генерация событий. Пожалуйста, подождите, пока они завершатся.',
            widgetcriterion: 'Критерий расчета',
            widgetemails: 'Электронные письма',
            widgetgotocard: 'Перейти к исходной карте',
            widgetsourcecard: 'Исходное удостоверение'
        },
        chat: {
            title: 'Чат'
        },
        classes: {
            cards: {
                addcard: 'Добавить запись',
                clone: 'Дублировать',
                clonewithrelations: 'Карта клонов и отношения',
                deletebeaware: 'Имейте в виду, что:',
                deleteblocked: 'Продолжить удаление невозможно, так как есть отношения с {0}.',
                deletecard: 'Удалить запись',
                deleteconfirmation: 'Вы уверены, что хотите удалить эту карту?',
                deleterelatedcards: 'также {0}, связанные карты будут удалены',
                deleterelations: 'отношения с {0} картами будут удалены',
                label: 'Конфигурационные единицы',
                modifycard: 'Редактировать запись',
                opencard: 'Открыть карты',
                print: 'Печать карты'
            },
            simple: 'Простой',
            standard: 'Стандартный'
        },
        common: {
            actions: {
                add: 'Добавить',
                apply: 'Применить',
                cancel: 'Отменить',
                clear: 'Сброс',
                close: 'Закрыть',
                copy: 'Копия',
                copyerrormessage: 'Скопируйте сообщение об ошибке в буфер обмена.',
                delete: 'Удаление',
                edit: 'Редактировать',
                execute: 'Выполнить',
                forward: 'Вперед',
                help: 'Справка',
                hide: '<em>Hide</em>',
                load: 'Нагрузки',
                open: 'Открыть',
                previous: 'Предыдущий',
                refresh: 'Обновление данных',
                remove: 'Удалить',
                save: 'Сохранить',
                saveandapply: 'Сохранить и применить',
                saveandclose: 'Сохранить и закрыть',
                search: 'Поиск',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Поиск...'
            },
            and: 'и',
            attributes: {
                boolfalse: 'ЛОЖЬ',
                booltrue: 'ПРАВДА',
                link: {
                    errlabel: 'Этикетка обязательна',
                    errurl: 'URL-адрес требуется, если метка не пуста',
                    label: 'Этикетка',
                    url: 'Url'
                },
                nogroup: 'Базовые данные'
            },
            dates: {
                date: 'д/м/y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Очистить HTML',
                expand: 'Расширить редактор',
                reduce: 'Уменьшить редактор',
                signature: 'Добавить подпись',
                unlink: 'Отменить связь',
                unlinkmessage: 'Преобразуйте выделенную гиперссылку в текст.'
            },
            grid: {
                disablemultiselection: 'Отключить многофункциональный выбор',
                enamblemultiselection: 'Включить многофункциональный выбор',
                export: 'Данные по экспорту',
                filterremoved: 'Текущий фильтр удален',
                import: 'Данные по импорту',
                itemnotfound: 'Элемент не найден',
                lastUpdate: '<em>Last update</em>',
                list: 'Список',
                opencontextualmenu: 'Открытое контекстное меню',
                print: 'Печати',
                printcsv: 'Печать как CSV',
                printmap: 'Распечатать карту',
                printodt: 'Печать как ODT',
                printpdf: 'Печать как PDF',
                row: 'Элемента',
                rows: 'Элементы',
                subtype: 'Подтип',
                thematism: 'Тематический слой'
            },
            tabs: {
                activity: 'Процесс',
                attachment: 'Вложения',
                attachments: 'Вложенные файлы',
                card: 'Данные',
                clonerelationmode: 'Режим отношений клонов',
                details: 'Подробно',
                emails: 'Электронные письма',
                history: 'История',
                notes: 'Комментарии',
                relations: 'Связи',
                schedules: 'Расписание',
                sources: 'Источники'
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
                download: 'Скачать диаграмму',
                downloaddata: 'Скачать данные диаграммы',
                gridhide: 'Скрытие сетки данных',
                gridshow: 'Показать сетку данных',
                openinpopup: 'Открыть во всплывающем окне',
                reload: 'Перезагрузить',
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
            abort: 'Отменить',
            addattachmentsfromdocumentarchive: 'Добавление вложений из архива документов',
            alredyexistfile: 'Уже существует файл с этим именем',
            archivingdate: 'Дата',
            attachfile: 'Вложить файл',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Скрытая копия',
            cc: 'Копия',
            composeemail: 'Составление сообщения',
            composefromtemplate: 'Составить из шаблона',
            delay: 'Задержка',
            delays: {
                day1: 'Через 1 день',
                days2: 'Через 2 дня',
                days4: 'Через 4 дня',
                hour1: '1 час',
                hours2: '2 часа',
                hours4: '4 часа',
                month1: 'Через 1 месяц',
                negativeday1: '1 день до',
                negativedays2: 'За 2 дня до',
                negativedays4: 'За 4 дня до',
                negativehour1: '1 час до',
                negativehours2: '2 часа до',
                negativehours4: 'За 4 часа до',
                negativemonth1: '1 месяц до',
                negativeweek1: 'За 1 неделю до',
                negativeweeks2: '2 недели до',
                none: 'Никто',
                week1: 'Через 1 неделю',
                weeks2: 'Через 2 недели'
            },
            dmspaneltitle: 'Выберите вложение из базы данных',
            edit: 'Редактировать',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'От',
            gridrefresh: 'Обновление сетки',
            groupbystatus: 'Сгруппировано по статусу',
            keepsynchronization: 'Сохранять синхронизацию',
            maxsize: 'Максимально допустимый общий размер вложений: {0}.',
            message: 'Сообщение',
            regenerateallemails: 'Восстановить все электронные письма',
            regenerateemail: 'Повторить генерацию почтовых сообщений',
            reloadconfirmationmessage: 'Электронные письма в статусе черновика будут удалены. Вы хотите продолжить?',
            remove: 'Удалить',
            remove_confirmation: 'Вы уверены, что хотите удалить это письмо?',
            reply: 'Ответить',
            replyprefix: 'На {0}, {1} написал:',
            searchattachment: 'Поиск вложения',
            searchcard: 'Карточка поиска',
            selectaclass: 'Выберите класс',
            sendemail: 'Отправить электронное письмо',
            sendingemail: 'Отправка электронной почты.',
            signature: 'Подпись',
            size: 'Размер',
            status: 'Статус',
            statuses: {
                draft: 'Черновики',
                error: 'Ошибка',
                new: 'Новый',
                outgoing: 'Исходящие',
                received: 'Входящие',
                sent: 'Отправленные',
                skipped: 'Пропущено'
            },
            subject: 'Тема',
            templateattachments: 'Это электронное письмо содержит вложения, автоматически загруженные шаблоном.',
            to: 'До',
            view: 'Представление данных'
        },
        errors: {
            actionnotallowed: 'Действие не разрешено',
            autherror: 'Неверное имя пользователя или пароль',
            classnotfound: 'Класс {0} не найден',
            fieldrequired: 'Это поле требуется',
            invalidfilter: 'Недействительный фильтр',
            nodatatoexport: 'Диаграмма не содержит данных для экспорта.',
            notfound: 'Элемент не найден',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Действия',
            addfilter: 'Создать фильтр',
            any: 'По крайней мере один',
            attachments: 'Вложения',
            attachmentssearchtext: 'Текст поиска вложений',
            attribute: 'Выберите атрибут',
            attributes: 'Атрибуты',
            clearfilter: 'Сбросить фильтр',
            clone: 'Дублировать',
            copyof: 'Копия',
            currentgroup: 'Текущая группа',
            currentuser: 'Текущий пользователь',
            defaultset: 'Установить по умолчанию',
            defaultunset: 'Выгрузка из дефолта',
            description: 'Описание',
            domain: 'Домен',
            errorname: 'Название фильтра уже используется',
            filterdata: 'Данные о фильтре',
            fromfilter: 'Из фильтра',
            fromselection: 'Из отбора',
            group: 'Группы',
            ignore: 'Игнорировать',
            migrate: 'Переносит',
            name: 'Имя',
            newfilter: 'Новый фильтр',
            noone: 'Ни один из',
            operator: 'Оператор',
            operators: {
                and: 'И',
                beginswith: 'Начинается с',
                between: 'Между',
                contained: 'Содержится',
                containedorequal: 'Содержится или равен',
                contains: 'Содержит',
                containsorequal: 'Содержит или равно',
                descriptionbegin: 'Описание начинается с',
                descriptioncontains: 'Описание содержит',
                descriptionends: 'Описание заканчивается на',
                descriptionnotbegin: 'Описание не начинается с',
                descriptionnotcontain: 'Описание не содержит',
                descriptionnotends: 'Описание не заканчивается на',
                different: 'Различных',
                doesnotbeginwith: 'Не начинается с',
                doesnotcontain: 'Не содержит',
                doesnotendwith: 'Не заканчивается',
                endswith: 'Окончание с',
                equals: 'Равно',
                greaterthan: 'Больше',
                isnotnull: 'Не является недействительным',
                isnull: 'Является недействительным',
                lessthan: 'Менее',
                or: 'Или же'
            },
            relations: 'Связи',
            removeattribute: 'Удалить атрибут',
            removeblock: 'Удалить блок',
            type: 'Тип',
            typeinput: 'Переменный параметр',
            typeorchoosetheattributename: 'Введите или выберите имя атрибута',
            user: 'Пользователя',
            value: 'Значение'
        },
        gis: {
            card: 'Данные',
            cardsMenu: 'Меню карт',
            code: 'Код',
            contextmenu: {
                coordinates: 'Получить координаты',
                coordinatescopiedmessage: 'Координаты скопированы в буфер обмена.'
            },
            description: 'Описание',
            extension: {
                errorCall: 'Ошибка',
                noResults: 'Нет результатов'
            },
            externalServices: 'Внешние сервисы',
            geographicalAttributes: 'Гео. атрибуты',
            geoserverLayers: 'Слои Geoserver',
            labels: {
                all: 'Все',
                alltitle: 'Показать все ярлыки',
                alltitleclass: 'Показать все ярлыки класса',
                clear: 'Очистить поиск',
                hidden: 'Скрытый',
                hiddentitle: 'Скрыть ярлыки',
                hideinfowindow: 'Скрыть информационное окно',
                label: 'Этикетки',
                search: 'Поиск на карте',
                selected: 'Выбрано',
                selectedtitle: 'Показать ярлык для выбранного элемента',
                showinfowindow: 'Показать информационное окно'
            },
            layers: 'Слои',
            layersTab: {
                clearPreferences: 'Очистить настройки слоев',
                preferencesCleared: 'Настройки слоев очищены',
                preferencesSaved: 'Настройки слоев сохранены',
                savePreferences: 'Сохранить настройки слоев',
                updatePreferences: 'Обновить настройки слоев'
            },
            list: 'Список',
            longpresstitle: 'Геоэлементы в районе',
            map: 'Карта',
            mapServices: 'Картографические услуги',
            menu: {
                font: 'Размер шрифта',
                options: 'Опции'
            },
            position: 'Положение',
            root: 'Корень',
            sourceMap: {
                default: 'Использовать вид по умолчанию',
                satellite: 'Использовать вид со спутника'
            },
            tree: 'Навигационное дерево',
            type: 'Тип',
            view: 'Представление данных',
            zoom: 'Масштабировать'
        },
        history: {
            activityname: 'Действие',
            activityperformer: 'Исполнитель',
            attributes: 'Атрибуты ({0})',
            begindate: 'Начальная дата',
            enddate: 'Конечная дата',
            fieldname: 'Имя поля',
            fulldata: 'Полные данные',
            onlychanges: 'Только изменения',
            otherrelations: 'Другие отношения',
            processstatus: 'Состояние',
            referencerelation: 'Ссылочные отношения',
            relation: 'Связь',
            system: 'Система',
            type: 'Тип',
            user: 'Пользователь',
            userdescription: 'Описание пользователя',
            value: 'Значение',
            viewmode: 'Режим просмотра'
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
            accessibility: 'Доступность',
            animals: 'Животные',
            arrows: 'Стрелки',
            audiovideo: 'Аудио видео',
            automotive: 'Автомобильная',
            autumn: 'Осень',
            beverage: 'Напиток',
            buildings: 'Здания',
            business: 'Бизнес',
            charity: 'Благотворительность',
            chat: 'Чат',
            chess: 'Шахматы',
            childhood: 'Детство',
            code: 'Код',
            communication: 'Комуникации',
            computers: 'Компьютеры',
            construction: 'Строительство',
            currency: 'Валюта',
            datetime: 'Дата время',
            design: 'Дизайн',
            editors: 'Редакторы',
            education: 'Образование',
            emoji: 'Эмодзи',
            energy: 'Энергия',
            files: 'Файлы',
            finance: 'Финансы',
            food: 'Еда',
            fruitvegetable: 'Фрукты овощи',
            games: 'Игры',
            gamingtabletop: 'Игровые настольные',
            gender: 'Пол',
            general: 'Общие',
            halloween: 'Хэллоуин',
            hands: 'Руки',
            hotel: 'Отель',
            household: 'Бытовая',
            images: 'Изображения',
            interfaces: 'Интерфейсы',
            logistics: 'Логистика',
            maps: 'Карты',
            maritime: 'Морские',
            marketing: 'Маркетинг',
            mathematics: 'Математика',
            medical: 'Медицинский',
            moving: 'Движущийся',
            music: 'Музыка',
            objects: 'Объекты',
            paymentsshopping: 'Платежи покупки',
            political: 'Политический',
            religion: 'Религия',
            sciencefiction: 'Научная фантастика',
            security: 'Безопасность',
            shapes: 'Формы',
            shopping: 'Покупки',
            social: 'Социальный',
            spinners: 'Спиннеры',
            sports: 'Спорт',
            spring: 'Весна',
            status: 'Статус',
            toggle: 'Переключать',
            travel: 'Путешествовать',
            userspeople: 'Пользователи люди',
            vehicles: 'Транспортные средства',
            weather: 'Времето',
            winter: 'Зима'
        },
        importexport: {
            database: {
                uri: 'База данных URI',
                user: 'Пользователь базы данных'
            },
            downloadreport: 'Скачать отчет',
            emailfailure: 'Ошибка произошла при отправке электронной почты!',
            emailmessage: 'Приложенный отчет об импорте файла "{0}" в {1}',
            emailsubject: 'Отчет об данных об импорте',
            emailsuccess: 'Письмо было отправлено успешно!',
            export: 'Экспорт',
            exportalldata: 'Все данные',
            exportfiltereddata: 'Только данные, соответствующие фильтру сетки',
            gis: {
                importconfiguration: 'Импорт конфигурации',
                shapeimportdisabled: 'Импорт форм не включен для этого шаблона',
                shapeimportenabled: 'Для этого шаблона включен импорт фигур.'
            },
            ifc: {
                card: 'Карта',
                project: 'Проекта',
                sourcetype: 'Импорт из'
            },
            import: 'Импортировать',
            importresponse: 'Реакция на импорт',
            response: {
                created: 'Созданные карты',
                deleted: 'Удаленные карты',
                errors: 'Ошибки',
                linenumber: 'Номер строки',
                message: 'Сообщение',
                modified: 'Измененные карты',
                notprocessed: 'Необработанные карты',
                processed: 'Обработанные строки',
                recordnumber: 'Номер записи',
                unchanged: 'Неизмененные карты'
            },
            sendreport: 'Отправить отчет',
            template: 'Шаблон',
            templatedefinition: 'Определение шаблона'
        },
        joinviews: {
            active: 'Активный',
            addview: 'Добавить вид',
            alias: 'Псевдоним',
            attribute: 'Атрибут',
            attributecustomization: 'Настройки атрибутов',
            attributes: 'Атрибуты',
            attributeschoice: 'Выбор атрибутов',
            attributesof: 'Атрибуты: {0}',
            createview: 'Создать представление',
            datasorting: 'Сортировка данных',
            delete: 'Удалять',
            deleteview: 'Удалить представление',
            deleteviewconfirm: 'Вы уверены, что хотите удалить это представление?',
            description: 'Описание',
            disable: 'Отключить',
            domainalias: 'Псевдоним домена',
            domainsof: 'Домены {0}',
            edit: 'Редактировать',
            editview: 'Изменить конфигурацию просмотра',
            enable: 'Включить',
            fieldsets: 'Наборы полей',
            filters: 'Фильтры',
            generalproperties: 'Общие свойства',
            group: 'Группа',
            innerjoin: 'Внутреннее соединение',
            jointype: 'Тип соединения',
            joinview: 'Вид из присоединения',
            klass: 'Класс',
            manageview: 'Управление просмотром',
            masterclass: 'Мастер класс',
            masterclassalias: 'Псевдоним мастер-класса',
            name: 'Имя',
            newjoinview: 'Новый вид из присоединения',
            outerjoin: 'Внешнее соединение',
            pleaseseleceavalidmasterclass: 'Выберите действующий мастер-класс',
            refreshafteredit: 'Вы хотите обновить страницу, чтобы увидеть изменения?',
            selectatleastoneattribute: 'Пожалуйста, выберите хотя бы один атрибут',
            selectdisplayattributegrid:
                'Выберите хотя бы один атрибут для отображения в сетке и один в уменьшенной сетке.',
            showingrid: 'Показать в сетке',
            showinreducedgrid: 'Показать в уменьшенной сетке',
            targetalias: 'Псевдоним целевого класса'
        },
        login: {
            buttons: {
                login: 'Вход в систему',
                logout: 'Сменить пользователя'
            },
            fields: {
                group: 'Группы',
                language: 'Язык',
                password: 'Пароль',
                tenants: 'Арендаторов',
                username: 'Имя пользователя'
            },
            loggedin: 'Зарегистрирован в',
            sso: {
                loginfailure: 'Ошибка входа',
                loginwith: 'Войти с {0}',
                or: 'или же',
                usernotenabled: 'Пользователь не включен'
            },
            title: 'Вход в систему',
            welcome: 'С возвращением {0}.'
        },
        main: {
            administrationmodule: 'Администрирование',
            alerts: 'Оповещения',
            baseconfiguration: 'Базовая конфигурация',
            cardlock: {
                lockedmessage: 'Вы не можете редактировать эту карту, потому что она редактируется.',
                someone: 'кто-нибудь'
            },
            changegroup: 'Группа изменений',
            changetenant: 'Изменить {0}',
            confirmchangegroup: 'Вы уверены, что хотите изменить группу?',
            confirmchangetenants: 'Вы уверены, что хотите сменить активных арендаторов?',
            confirmdisabletenant: 'Вы уверены, что хотите отключить флаг "Игнорировать жильцов"?',
            confirmenabletenant: 'Вы уверены, что хотите включить флаг "Игнорировать арендаторов"?',
            description: 'Описание',
            ignoretenants: 'Игнорировать {0}',
            info: 'Информация',
            logo: {
                cmdbuild: 'Логотип CMDBuild',
                cmdbuildready2use: 'Логотип CMDBuild READY2USE',
                companylogo: 'Логотип компании',
                hdaitsm: 'HDA ITSM',
                openmaint: 'Логотип OpenMAINT'
            },
            logout: 'Выход',
            managementmodule: 'Управление данными',
            multigroup: 'Несколько групп',
            multitenant: 'Мульти {0}',
            navigation: 'Навигация',
            pagenotfound: 'Страница не найдена',
            password: {
                change: 'Изменить пароль',
                confirm: 'Подтверждение пароля',
                email: 'Адрес электронной почты',
                err_confirm: 'Пароль не совпадает.',
                err_diffprevious: 'Пароль не может быть идентичен предыдущему.',
                err_diffusername: 'Пароль не может быть идентичен имени пользователя.',
                err_length: 'Пароль должен быть не менее {0} символов долго.',
                err_reqdigit: 'Пароль должен содержать по крайней мере одну цифру.',
                err_reqlowercase: 'Пароль должен содержать по крайней мере один символ нижнего регистра.',
                err_requppercase: 'Пароль должен содержать по крайней мере один символ верхнего регистра.',
                expired: 'Срок действия пароля истек. Вы должны изменить его сейчас.',
                forgotten: 'Я забыл свой пароль',
                new: 'Новый пароль',
                old: 'Старый пароль',
                recoverysuccess: 'Мы отправили вам электронное письмо с инструкцией по восстановлению вашего пароля.',
                reset: 'Сбросить пароль',
                saved: 'Пароль правильно сохранен!'
            },
            pleasecorrecterrors: 'Пожалуйста, исправьте указанные ошибки!',
            preferences: {
                comma: 'Запятой',
                decimalserror: 'Десятичные поля должны присутствовать',
                decimalstousandserror: 'Десятичные и тысячи сепаратор должны быть разными',
                default: 'По умолчанию',
                defaultvalue: 'Значение по умолчанию',
                favouritemenulocation: 'Расположение меню избранного',
                firstdayofweek: 'Первый день недели',
                gridpreferencesclear: 'Четкие предпочтения сетки',
                gridpreferencescleared: 'Сетка предпочтения очищены!',
                gridpreferencessave: 'Сохранение предпочтений сетки',
                gridpreferencessaved: 'Сетка предпочтения сохранены!',
                gridpreferencesupdate: 'Обновление предпочтений сетки',
                groupemailsbystatus: 'Показать электронные письма, сгруппированные по статусу',
                labelcsvseparator: 'Сепаратор CSV',
                labeldateformat: 'Формат даты',
                labeldecimalsseparator: 'Сепаратор десятичных знаков',
                labellanguage: 'Язык',
                labelthousandsseparator: 'Тысячи сепараторов',
                labeltimeformat: 'Формат времени',
                msoffice: 'Офис Майкрософт',
                no: 'Нет',
                notificationslabel: 'Чат и уведомления',
                notificationssound: 'Воспроизвести звук для уведомлений',
                notificationssoundvolume: 'Громкость звука уведомлений',
                period: 'Период',
                preferredfilecharset: 'Кодирование CSV',
                preferredofficesuite: 'Предпочтительный офисный пакет',
                sendcancellationperiod: 'Период отмены отправки электронного письма (секунды)',
                showcollapsed: 'Показать свернуто',
                showfovouritesafter: 'Показать после меню навигации',
                showfovouritesbefore: 'Показать перед меню навигации',
                space: 'Пространство',
                thousandserror: 'Тысячи полей должны присутствовать',
                timezone: 'Часовой пояс',
                twelvehourformat: '12-часовой формат',
                twentyfourhourformat: '24-часовой формат',
                yes: 'Да'
            },
            searchinallitems: 'Поиск по всем элементам',
            searchtenant: 'Поиск арендатора',
            selectdeselettenants: 'Выбрать / Отменить выбор всех',
            treenavcontenttitle: '{0} от {1}',
            userpreferences: 'Предпочтения'
        },
        menu: {
            allitems: 'Все предметы',
            classes: 'Классы',
            custompages: 'Пользовательские страницы',
            dashboards: 'Панели мониторинга',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Избранное',
            favouritesadd: 'Добавить в избранное',
            favouritesremove: 'Удалить из избранного',
            homepage: '<em>Homepage</em>',
            processes: 'Рабочие процессы',
            reports: 'Отчеты',
            setasinitialpage: 'Сделать начальной страницей',
            views: 'Представления'
        },
        mobile: {
            config: {
                configure: 'Настроить мобильное устройство',
                customercode: 'Клиентский код',
                devicename: 'Имя устройства',
                regenerate: 'Регенерировать',
                serverurl: 'URL-адрес сервера'
            }
        },
        notes: {
            edit: 'Редактировать комментарий'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Удалить уведомление',
            emptymessage: 'Нет уведомлений для отображения.',
            label: 'Уведомления',
            viewall: 'Посмотреть все'
        },
        notifier: {
            attention: 'Внимание',
            copied: 'Скопировано.',
            copiedtotheclipboard: 'Текст скопирован в буфер обмена',
            error: 'Ошибка',
            genericerror: 'Общая ошибка',
            genericerrormessage: 'Для получения более подробной информации проверьте системные журналы.',
            genericinfo: 'Общая информация',
            genericwarning: 'Общее предупреждение',
            info: 'Информация',
            more: '<em>More info</em>',
            success: 'Успешно выполнено',
            warning: 'Предупреждение'
        },
        patches: {
            apply: 'Применить патчи',
            category: 'Категории',
            description: 'Описание',
            name: 'Имя',
            patches: 'Патчи'
        },
        processes: {
            abortconfirmation: 'Действительно удалить процесс?',
            abortprocess: 'Прервать процесс',
            action: {
                advance: 'Продолжить',
                label: 'Действий'
            },
            activeprocesses: 'Активные процессы',
            allstatuses: 'Все',
            editactivity: 'Редактировать процесс',
            openactivity: 'Открытая деятельность',
            resumeprocesses: 'Процессы, которые не приостановлены, не могут быть возобновлены',
            startworkflow: 'Начало',
            workflow: 'Процесс'
        },
        relationGraph: {
            activity: 'Деятельности',
            allLabelsOnGraph: 'все этикетки на графике',
            card: 'Данные',
            cardList: 'Список Карт',
            cardRelations: 'Связь с Карт',
            choosenaviagationtree: 'Выберите навигационное дерево',
            class: 'Класса',
            classList: 'Список Классов',
            compoundnode: 'Соединение узла',
            disable: 'Отключить',
            edges: 'Края',
            enable: 'Включить',
            labelsOnGraph: 'инструмент на графике',
            level: 'Уровень',
            nodes: 'Узлы',
            openRelationGraph: 'Показать диаграмму связей',
            quantity: 'Количество',
            refresh: 'Обновить',
            relation: 'Связь',
            relationGraph: 'Отображение связей',
            reopengraph: 'Повторное открытие графика из этого узла'
        },
        relations: {
            adddetail: 'Добавить элемент',
            addrelations: 'Добавить связь',
            attributes: 'Атрибуты',
            begindate: 'Дата начала',
            cardnotpresentmessage:
                'Соответствующая карта больше не доступна. Для продолжения модификации потребуется выбрать другую карту.',
            code: 'Код',
            deletedetail: 'Удалить элемент',
            deleterelation: 'Удалить связь',
            deleterelationconfirm: 'Вы уверены, что хотите удалить это отношение?',
            description: 'Описание',
            editcard: 'Редактировать запись',
            editdetail: 'Редактировать элемент',
            editrelation: 'Редактировать связь',
            errorexisting:
                'Отношение "{0}" уже существует. Отредактируйте существующее или удалите его, прежде чем создавать новое отношение.',
            extendeddata: 'Расширенные данные',
            mditems: 'Элементы',
            missingattributes: 'Отсутствующие обязательные атрибуты',
            opencard: 'Открыть связанную запись',
            opendetail: 'Показать элемент',
            relationdata: 'Данные отношения',
            type: 'Тип'
        },
        reports: {
            csv: 'CSV',
            download: 'Скачать',
            format: 'Формат',
            msgs: {
                creationerror: 'Что-то пошло не так в начале создания отчета.',
                creationstarted: 'Начато создание отчета. Вы будете уведомлены, когда он будет готов для загрузки.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Распечатать',
            reload: 'Обновить',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'По умолчанию'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Ежедневно'
                        },
                        monthly: {
                            description: 'Ежемесячно'
                        },
                        once: {
                            description: 'Раз'
                        },
                        weekly: {
                            description: 'Еженедельно'
                        },
                        yearly: {
                            description: 'Годовой'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'По умолчанию'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Никто'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Добавить тематизм',
            analysisType: 'Тип анализа',
            attribute: 'Атрибут',
            bordersIntervals: 'Неправильная конфигурация ограничений интервалов',
            calculateRules: 'Создание правил стиля',
            clearThematism: 'Очистить тематизм',
            color: 'Цвет',
            defineLegend: 'Определение легенды',
            defineThematism: 'Определение слоя',
            deletethematism: 'Вы уверены, что хотите удалить этот тематизм?',
            editThematism: 'Изменить конфигурацию правил',
            function: 'Функции',
            generate: 'Создать',
            geoAttribute: 'Географический атрибут',
            graduated: 'Окончил',
            highlightSelected: 'Выделение выбранного элемента',
            intervals: 'Интервалы',
            legend: 'Легенда',
            name: 'Имя',
            newThematism: 'Новый тематизм',
            punctual: 'Пунктуальны',
            quantity: 'Рассчитывать',
            segments: 'Сегментов',
            source: 'Источник',
            table: 'Таблице',
            thematism: 'Тематические слои',
            value: 'Значение'
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
                    exitwithoutsave: 'Выход без сохранения',
                    stayinedit: 'Назад к редактированию'
                },
                savebeforeexit: 'Во вложениях есть несохраненные изменения. Вы хотите сохранить их перед выходом?',
                saveprocessmessage:
                    'Вложения могут быть сохранены только в существующих карточках. При нажатии на кнопку «Сохранить» будет создан экземпляр процесса с данными, введенными в форму.'
            },
            calendar: {
                error: 'Параметр {0} не задан',
                errorwrong: 'Параметр {0} задан неправильно'
            },
            customform: {
                addrow: 'Добавить строку',
                clonerow: 'Строка клонов',
                datanotvalid: 'Данные недействительны',
                deleterow: 'Удалить строку',
                editrow: 'Редактировать строку',
                export: 'Экспорт',
                import: 'Импортировать',
                importexport: {
                    expattributes: 'Данные для экспорта',
                    file: 'Файл',
                    filename: 'Имя файла',
                    format: 'Формат',
                    importmode: 'Режим импорта',
                    keyattributes: 'Ключевые атрибуты',
                    missingkeyattr: 'Пожалуйста, выберите по крайней мере один ключевой атрибут',
                    modeadd: 'Добавить',
                    modemerge: 'Объединить',
                    modereplace: 'Заменить',
                    separator: 'Разделитель'
                },
                refresh: 'Обновление по умолчании'
            },
            linkcards: {
                checkedonly: 'Только проверено',
                editcard: 'Редактировать карту',
                opencard: 'Открыт карту',
                refreshselection: 'Применить выбор по умолчанию',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Отключить фильтр сетки',
                togglefilterenabled: 'Включить фильтр сетки'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                'Встроенный режим не поддерживается для этого типа виджета. Исправьте конфигурацию виджета.',
            required: 'Этот виджет требуется.'
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
