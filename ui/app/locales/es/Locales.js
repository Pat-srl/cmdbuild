(function () {
    Ext.define('CMDBuildUI.locales.es.Locales', {
        requires: ['CMDBuildUI.locales.es.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'es',
        administration: CMDBuildUI.locales.es.LocalesAdministration.administration,
        ai: {
            clearconversation: 'Borrar conversación',
            clearconversationmessage: '¿Está seguro de que desea borrar la conversación actual?',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: 'Lo siento, hubo problemas con la respuesta.',
            headertooltip: 'CMDBuild chat bot',
            introtextbody: 'Para comenzar a chatear, describa su solicitud en el campo correspondiente.',
            introtexttitle: '¿Cómo puedo ayudarle?',
            loading: 'Cargando...',
            newchat: 'Nuevo chat',
            newchattooltip: 'Iniciar una nueva conversación',
            questionemptytext: 'Escribe aquí tu pregunta...',
            resources: 'Fuentes',
            viewrelatedcard: 'Ver tarjeta relacionada',
            viewresource: 'Ver recurso'
        },
        arialabels: {
            buttonplaceholder: 'Botón',
            iframeplaceholder: 'Editor de texto',
            languageselector: 'Selector de idioma',
            resizepanel: {
                horizontally: 'Cambiar el tamaño de los paneles horizontalmente',
                vertically: 'Cambiar el tamaño de los paneles verticalmente'
            },
            usermenu: 'Menú de usuario'
        },
        attachments: {
            add: 'Agregar adjunto',
            attachmenthistory: 'Historia adjunto',
            author: 'Autor',
            authordescription: 'Descripción del autor',
            browse: 'Navegar &hellip;',
            category: 'Categoria',
            code: 'Código',
            creationdate: 'Fecha de creación',
            deleteattachment: 'Eliminar adjunto',
            deleteattachment_confirmation: '¿Estás seguro de que quieres eliminar este adjunto?',
            description: 'Descripción',
            download: 'Descargar',
            dropfiles: 'Suelta los archivos aquí',
            editattachment: 'Modificar adjunto',
            errordms: 'El DMS está deshabilitado y no es posible guardar archivos',
            file: 'Archivo',
            filealreadyinlist: 'El archivo {0} ya está en la lista.',
            filename: 'Nombre archivo',
            fileview: 'Ver adjunto',
            invalidfiles: 'Eliminar archivos inválidos',
            majorversion: 'Versión principal',
            maxsize: 'El tamaño de archivo máximo permitido es {0} MB.',
            metadata: 'Metadatos',
            modificationdate: 'Fecha de modificación',
            new: 'Nuevo adjunto',
            nocategory: 'Sin categorizar',
            preview: 'Avance',
            removefile: 'Eliminar archivo',
            statuses: {
                empty: 'Archivo vacío',
                error: 'Error',
                extensionNotAllowed: 'Extensión de archivo no permitida',
                loaded: 'Cargado',
                ready: 'Listo',
                toolarge: 'Demasiado grande'
            },
            successupload: '{0} adjuntos cargados.',
            uploadfile: 'Cargar archivio…',
            version: 'Versión',
            viewhistory: 'Ver historia adjunto',
            viewmetadata: 'Ver los metadatos adjuntos',
            warningmessages: {
                atleast: '{0}: se han cargado {1} adjuntos de tipo "{2}". Esta categoría espera al menos {3} adjuntos',
                exactlynumber: '{0}: se han cargado {1} adjuntos de tipo "{2}". Esta categoría espera {3} adjuntos',
                maxnumber:
                    '{0}: se han cargado {1} adjunto de tipo "{2}". Esta categoría espera como máximo {3} adjuntos.'
            },
            wrongfileextension: 'La extensión de archivo {0} no está permitida'
        },
        bim: {
            bimViewer: 'Visor BIM',
            card: {
                label: 'Tarjeta'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Tamaño del cuadro delimitador a lo largo del eje X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Tamaño del cuadro delimitador a lo largo del eje Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Tamaño del cuadro delimitador a lo largo del eje Z',
                calculated: 'Calculado',
                description: 'Descripción',
                guid: 'GUID',
                label: 'Propiedades',
                name: 'Nombre',
                type: 'Tipo',
                uuid: 'UUID'
            },
            layers: {
                label: 'Capas',
                menu: {
                    hideAll: 'Ocultar todo',
                    hideAllLayers: 'Ocultar todas las capas',
                    hideAllXeokit: 'Ocultar todo {0}',
                    showAll: 'Mostrar todo',
                    showAllLayers: 'Mostrar todos los niveles',
                    showAllXeokit: 'Mostrar todo {0}'
                },
                name: 'Nombre',
                quantity: 'Cantidad',
                visibility: 'Visibilidad'
            },
            menu: {
                camera: 'Cámara',
                disableslice: 'Deshabilitar corte de objetos',
                enableslice: 'Habilitar corte de objetos',
                frontView: 'Vista frontal',
                mod: 'Controles del visor',
                ortho: 'Cambiar a vista ortográfica',
                orthographic: 'Camara ortografica',
                pan: 'Desplazar',
                panXeokit: 'Controles del visor: desplazamiento (Shift)',
                perspe: 'Cambiar a vista en perspectiva',
                perspective: 'Cámara de perspectiva',
                resetSlice: 'Eliminar corte',
                resetView: 'Reestablecer vista',
                rotate: 'Rota',
                rotateXeokit: 'Controles del visor: rotar',
                save: 'Guardar vista',
                shortcuts: {
                    down: 'Rotar hacia abajo',
                    left: 'Rotar a la izquierda',
                    right: 'Rotar a la derecha',
                    title: 'Atajos de teclado',
                    up: 'Rotar hacia arriba',
                    zoomin: 'Acercar',
                    zoomout: 'Alejar'
                },
                sideView: 'Vista lateral',
                threeD: 'Cambiar a vista 3D',
                topView: 'Vista superior',
                twoD: 'Cambiar a vista 2D'
            },
            navCube: {
                back: 'Atrás',
                bottom: 'Debajo',
                front: 'Frente',
                left: 'Izquierda',
                right: 'Derecha',
                top: 'Encima'
            },
            showBimCard: 'Abrir visualizador 3D',
            tree: {
                arrowTooltip: 'Seleccionar elemento',
                label: 'Vista de árbol',
                multipleElements: 'Seleccionar elementos',
                open_card: 'Abrir tarjeta relacionada',
                root: 'Raíz de IFC'
            }
        },
        bulkactions: {
            abort: 'Interrumpir procesos seleccionados',
            addfields: 'Agregar campos',
            alertdownloadattachments:
                'Algunos archivos adjuntos no se descargarán porque aún no se han guardado en el servidor.',
            alertnoattachmentsdownload:
                'No se descargarán archivos adjuntos porque aún no se han guardado en el servidor',
            cancelselection: 'Cancelar selección',
            confirmabort: 'Estás abortando {0} instancias de proceso. ¿Estás seguro de que quieres continuar?',
            confirmdelete: 'Estás eliminando {0} tarjetas. ¿Estás seguro de que quieres continuar?',
            confirmdeleteattachements: 'Estás eliminando {0} adjuntos. ¿Estás seguro de que quieres continuar?',
            confirmedit: 'Estás modificando {0} por {1} tarjetas. ¿Estás seguro de que quieres continuar?',
            delete: 'Eliminar elementos seleccionados',
            download: 'Descargar adjuntos seleccionados',
            downloadall: 'Descarga todos los adjuntos',
            edit: 'Editar elementos seleccionados',
            missingfields: 'Para una correcta validación del formulario se deben añadir los siguientes campos: {0}',
            noattributeselected: 'Seleccione al menos un atributo',
            selectall: 'Seleccionar todos los elementos'
        },
        calendar: {
            active_expired: 'Activo/Caducado',
            add: 'Agregar',
            advancenotification: 'Notificación anticipada',
            allcategories: 'Todas las categorias',
            alldates: 'Todas las fechas',
            associatedcard: 'Tarjeta asociada',
            calculated: 'Calculado',
            calendar: 'Calendario',
            cancel: 'Marcar como cancelado',
            category: 'Categoria',
            class: 'Clase',
            cm_confirmcancel: '¿Está seguro de que desea marcar como canceladas las programaciones seleccionadas?',
            cm_confirmcomplete: '¿Está seguro de que desea marcar como completadas las programaciones seleccionadas?',
            cm_markcancelled: 'Marcar como cancelados los horarios seleccionados',
            cm_markcomplete: 'Marcar como completos los horarios seleccionados',
            complete: 'Completo',
            completed: 'Terminado',
            date: 'Fecha',
            days: 'Dias',
            delaybeforedeadline: 'Retraso antes de la fecha límite',
            delaybeforedeadlinevalue: 'Retraso antes del valor límite',
            description: 'Descripción',
            editevent: 'Editar calendario',
            enddate: 'Fecha de finalización',
            endtype: 'Tipo final',
            event: 'Calendario',
            execapplyfilter: 'No se puede ejecutar applyAdvanceFilter porque hay más de una fuente de datos',
            execremovefilter: 'No se puede ejecutar removeAdvanceFilter porque hay más de una fuente de datos',
            executiondate: 'Fecha de ejecución',
            frequency: 'Frecuencia',
            frequencymultiplier: 'Multiplicador de frecuencia',
            grid: 'Cuadrícula',
            leftdays: 'Días para ir',
            londdescription: 'Descripción completa',
            manual: 'Manual',
            maxactiveevents: 'Max eventos activos',
            messagebodydelete: '¿Desea eliminar la regla de calendario?',
            messagebodyplural: 'Hay {0} reglas de calendario',
            messagebodyrecalculate: '¿Desea volver a calcular la regla del calendario con la nueva fecha?',
            messagebodysingular: 'Hay {0} regla de calendario',
            messagetitle: 'Recalcular calendario',
            missingdays: 'Días que faltan',
            next30days: 'Próximos 30 días',
            next7days: 'Próximos 7 días',
            notificationtemplate: 'Plantilla utilizada para notificación',
            notificationtext: 'Texto de notificación',
            occurencies: 'Numero de incidentes',
            operation: 'Operador',
            partecipantgroup: 'Grupo de participantes',
            partecipantuser: 'Usuario participante',
            priority: 'Prioridad',
            recalculate: 'Recalcular',
            referent: 'Referente',
            scheduler: 'Programador',
            sequencepaneltitle: 'Generar calendarios',
            startdate: 'Fecha de inicio',
            status: 'Estado',
            today: 'Hoy',
            type: 'Tipo',
            viewevent: 'Ver calendario',
            waiteventgeneration: 'Generando eventos. Por favor, espere a que finalicen.',
            widgetcriterion: 'Criterio de cálculo',
            widgetemails: 'Emails',
            widgetgotocard: 'Ir a la tarjeta de origen',
            widgetsourcecard: 'Tarjeta fuente'
        },
        chat: {
            title: 'Chat'
        },
        classes: {
            cards: {
                addcard: 'Agregar tarjeta',
                clone: 'Clonar',
                clonewithrelations: 'Clonar tarjeta y relaciones',
                deletebeaware: 'Tenga en cuenta que:',
                deleteblocked: 'No es posible continuar con la eliminación porque existen relaciones con {0}.',
                deletecard: 'Eliminar tarjeta',
                deleteconfirmation: '¿Estás seguro de que quieres eliminar esta tarjeta?',
                deleterelatedcards: 'también se eliminarán {0} tarjetas relacionadas',
                deleterelations: 'Se eliminarán las relaciones con {0} tarjetas.',
                label: 'Tarjetas de datos',
                modifycard: 'Modificar tarjeta',
                opencard: 'Abrir tarjeta',
                print: 'Imprimir tarjeta'
            },
            simple: 'Simple',
            standard: 'Estándar'
        },
        common: {
            actions: {
                add: 'Agregar',
                apply: 'Aplicar',
                cancel: 'Cancelar',
                clear: 'Borrar',
                close: 'Cerrar',
                copy: 'Copiar',
                copyerrormessage: 'Copia el mensaje de error en el portapapeles',
                delete: 'Eliminar',
                edit: 'Editar',
                execute: 'Ejecutar',
                forward: 'Adelante',
                help: 'Ayuda',
                hide: 'Ocultar',
                load: 'Carga',
                open: 'Abrir',
                previous: 'Previo',
                refresh: 'Actualizar datos',
                remove: 'Eliminar',
                save: 'Guardar',
                saveandapply: 'Guardar y aplicar',
                saveandclose: 'Guardar y cerrar',
                search: 'Buscar',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Buscar...'
            },
            and: 'y',
            attributes: {
                boolfalse: 'Falso',
                booltrue: 'Verdadero',
                link: {
                    errlabel: 'La etiqueta es obligatoria',
                    errurl: 'La URL es obligatoria si la etiqueta no está vacía',
                    label: 'Etiqueta',
                    url: 'URL'
                },
                nogroup: 'Base de datos'
            },
            dates: {
                date: 'd/m/Y',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Borrar HTML',
                expand: 'Expandir editor',
                reduce: 'Reducir editor',
                signature: 'Agregar firma',
                unlink: 'Remover enlace',
                unlinkmessage: 'Convierte el enlace seleccionado en texto.'
            },
            grid: {
                disablemultiselection: 'Deshabilitar selección múltiple',
                enamblemultiselection: 'Habilitar selección múltiple',
                export: 'Exportar datos',
                filterremoved: 'El filtro actual ha sido eliminado',
                import: 'Importar datos',
                itemnotfound: 'Elemento no encontrado',
                lastUpdate: '<em>Last update</em>',
                list: 'Lista',
                opencontextualmenu: 'Abrir menú contextual',
                print: 'Imprimir',
                printcsv: 'Imprimir como CSV',
                printmap: 'Imprimir mapa',
                printodt: 'Imprimir como ODT',
                printpdf: 'Imprimir como PDF',
                row: 'Elemento',
                rows: 'Elementos',
                subtype: 'Subtipo',
                thematism: 'Tematismo'
            },
            tabs: {
                activity: 'Actividad',
                attachment: 'Adjunto',
                attachments: 'Adjuntos',
                card: 'Tarjeta',
                clonerelationmode: 'Clonar modo de relaciones',
                details: 'Detalles',
                emails: 'Emails',
                history: 'Historia',
                notes: 'Notas',
                relations: 'Relaciones',
                schedules: 'Calendarios',
                sources: 'Fuentes'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: 'Mostrar todo',
                showingnitems: 'Mostrando: {0} elementos'
            },
            pareto: {
                cumulative: 'Acumulado',
                cumulativeperc: 'Porcentaje acumulado'
            },
            tools: {
                download: 'Descargar gráfico',
                downloaddata: 'Descargar datos del gráfico',
                gridhide: 'Ocultar cuadrícula de datos',
                gridshow: 'Mostrar cuadrícula de datos',
                openinpopup: 'Abrir en ventana emergente',
                reload: 'Recargar',
                showall: 'Mostrar todo'
            }
        },
        dependencytree: {
            cardinfo: 'Tarjeta',
            filteredid: 'La tarjeta actual no pertenece a este árbol',
            gerarchicview: 'Vista jerárquica',
            name: 'Árbol de dependencias',
            nocardselected: 'No se ha seleccionado ninguna tarjeta',
            opendependencytree: 'Abrir árbol de dependencias',
            printjpeg: 'Imprimir como JPEG',
            printpdf: 'Imprimir como PDF',
            printpng: 'Imprimir como PNG',
            printsvg: 'Imprimir como SVG',
            reopengraph: 'Volver a abrir el grafo con una nueva raíz',
            selectcard: 'Seleccionar nodo de inicio',
            wrongid: 'El ID que ingresó es incorrecto.'
        },
        emails: {
            abort: 'Cancelar',
            addattachmentsfromdocumentarchive: 'Agregar adjuntos del archivo de documentos',
            alredyexistfile: 'Ya existe un archivo con este nombre',
            archivingdate: 'Fecha de archivo',
            attachfile: 'Adjunta archivo',
            attachmentsmessage:
                'La plantilla requiere que adjuntes archivos a la tarjeta. No podrás utilizarla hasta que guardes el proceso.',
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Crear email',
            composefromtemplate: 'Crear email desde plantilla',
            delay: 'Retrasar',
            delays: {
                day1: 'En 1 día',
                days2: 'En 2 días',
                days4: 'En 4 días',
                hour1: '1 hora',
                hours2: '2 horas',
                hours4: '4 horas',
                month1: 'En 1 mes',
                negativeday1: '1 día antes',
                negativedays2: '2 días antes',
                negativedays4: '4 días antes',
                negativehour1: '1 hora antes',
                negativehours2: '2 horas antes',
                negativehours4: '4 horas antes',
                negativemonth1: '1 mes antes',
                negativeweek1: '1 semana antes',
                negativeweeks2: '2 semanas antes',
                none: 'Ninguno',
                week1: 'En 1 semana',
                weeks2: 'En 2 semanas'
            },
            dmspaneltitle: 'Elegir adjuntos desde la base de datos',
            edit: 'Editar',
            fileattributesmessage:
                'Existe una dependencia de un atributo de tipo de archivo. No podrá utilizarlo hasta que guarde el proceso.',
            from: 'De',
            gridrefresh: 'Recargar cuadrícula',
            groupbystatus: 'Agrupar por estado',
            keepsynchronization: 'Mantener sincronizado',
            maxsize: 'El tamaño total máximo permitido para los adjuntos es {0}.',
            message: 'Mensaje',
            regenerateallemails: 'Regenerar todos los correos',
            regenerateemail: 'Regenerar email',
            reloadconfirmationmessage:
                'Los correos electrónicos en estado de borrador serán eliminados. ¿Quieres continuar?',
            remove: 'Eliminar',
            remove_confirmation: '¿Estás seguro de que quieres eliminar este email?',
            reply: 'Contestar',
            replyprefix: 'En {0}, {1} escribió:',
            searchattachment: 'Buscar adjunto',
            searchcard: 'Buscar tarjeta',
            selectaclass: 'Seleccionar clase',
            sendemail: 'Enviar correo electrónico',
            sendingemail: 'Enviar correo electrónico',
            signature: 'Firma',
            size: 'Tamaño',
            status: 'Estado',
            statuses: {
                draft: 'Borradores',
                error: 'Error',
                new: 'Nueva',
                outgoing: 'Salida',
                received: 'Recibidos',
                sent: 'Enviados',
                skipped: 'Omitida'
            },
            subject: 'Subjeto',
            templateattachments:
                'Este correo electrónico contiene archivos adjuntos cargados automáticamente por la plantilla.',
            to: 'A',
            view: 'Vista'
        },
        errors: {
            actionnotallowed: 'Acción no permitida',
            autherror: 'Nombre de usuario o contraseña incorrectos',
            classnotfound: 'Clase {0} no encontrada',
            fieldrequired: 'Este campo es requerido',
            invalidfilter: 'Filtro inválido',
            nodatatoexport: 'El gráfico no tiene datos para exportar',
            notfound: 'Elemento no encontrado',
            value3chars: 'El valor debe tener una longitud mínima de 3 caracteres.'
        },
        filters: {
            actions: 'Acciones',
            addfilter: 'Agregar filtro',
            any: 'Al menos uno',
            attachments: 'Adjuntos',
            attachmentssearchtext: 'Texto de búsqueda de adjuntos',
            attribute: 'Elegir un atributo',
            attributes: 'Atributos',
            clearfilter: 'Borrar filtro',
            clone: 'Clonar',
            copyof: 'Copia de',
            currentgroup: 'Grupo actual',
            currentuser: 'Usuario actual',
            defaultset: 'Establecer por defecto',
            defaultunset: 'Desarmado por defecto',
            description: 'Descripción',
            domain: 'Dominio',
            errorname: 'Ya existe un filtro con el mismo nombre',
            filterdata: 'Filtrar datos',
            fromfilter: 'Desde filtro',
            fromselection: 'Desde la selección',
            group: 'Grupo',
            ignore: 'Ignorar',
            migrate: 'Emigrar',
            name: 'Nombre',
            newfilter: 'Nuevo filtro',
            noone: 'Ninguno',
            operator: 'Operador',
            operators: {
                and: 'Y',
                beginswith: 'Inicia con',
                between: 'Entre',
                contained: 'Contenido',
                containedorequal: 'Contenido o igual',
                contains: 'Contiene',
                containsorequal: 'Contiene o igual',
                descriptionbegin: 'La descripción comienza con',
                descriptioncontains: 'La descripción contiene',
                descriptionends: 'La descripción termina con',
                descriptionnotbegin: 'La descripción no comienza con',
                descriptionnotcontain: 'La descripción no contiene',
                descriptionnotends: 'La descripción no termina con',
                different: 'Diferente',
                doesnotbeginwith: 'No inicia con',
                doesnotcontain: 'No contiene',
                doesnotendwith: 'No termina con',
                endswith: 'Termina con',
                equals: 'Iguales',
                greaterthan: 'Mayor',
                isnotnull: 'No es nulo',
                isnull: 'Es nulo',
                lessthan: 'Menor',
                or: 'O'
            },
            relations: 'Relaciones',
            removeattribute: 'Eliminar atributo',
            removeblock: 'Eliminar bloque',
            type: 'Tipo',
            typeinput: 'Parámetro de input',
            typeorchoosetheattributename: 'Escriba o elija el nombre del atributo',
            user: 'Usuario',
            value: 'Valor'
        },
        gis: {
            card: 'Tarjeta',
            cardsMenu: 'Menú de tarjetas',
            code: 'Código',
            contextmenu: {
                coordinates: 'Obtener coordenadas',
                coordinatescopiedmessage: 'Coordenadas copiadas al clipboard'
            },
            description: 'Descripción',
            extension: {
                errorCall: 'Error',
                noResults: 'No hay resultados'
            },
            externalServices: 'Servicios externos',
            geographicalAttributes: 'Atributos geográficos',
            geoserverLayers: 'Capas del Geoserver',
            labels: {
                all: 'Todas',
                alltitle: 'Mostrar todas las etiquetas',
                alltitleclass: 'Mostrar todas las etiquetas de clase',
                clear: 'Borrar búsqueda',
                hidden: 'Oculto',
                hiddentitle: 'Ocultar etiquetas',
                hideinfowindow: 'Ocultar ventana de información',
                label: 'Etiquetas',
                search: 'Buscar en el mapa',
                selected: 'Seleccionado',
                selectedtitle: 'Mostrar las etiquetas del elemento seleccionado',
                showinfowindow: 'Mostrar ventana de información'
            },
            layers: 'Capas',
            layersTab: {
                clearPreferences: 'Borrar preferencias de capas',
                preferencesCleared: 'Preferencias de capa eliminadas',
                preferencesSaved: 'Preferencias de capa guardadas',
                savePreferences: 'Guardar preferencias de capa',
                updatePreferences: 'Actualizar preferencias de capa'
            },
            list: 'Lista',
            longpresstitle: 'Geoelementos en el área',
            map: 'Mapa',
            mapServices: 'Servicios de mapas',
            menu: {
                font: 'Tamaño de fuente',
                options: 'Opciones'
            },
            position: 'Posición',
            root: 'Raíz',
            sourceMap: {
                default: 'Usar vista predeterminada',
                satellite: 'Usar vista satelital'
            },
            tree: 'Vista de árbol',
            type: 'Tipo',
            view: 'Vista',
            zoom: 'Zoom'
        },
        history: {
            activityname: 'Nombre actividad',
            activityperformer: 'Ejecutor actividades',
            attributes: 'Atributos ({0})',
            begindate: 'Fecha de inicio',
            enddate: 'Fecha de finalización',
            fieldname: 'Nombre del campo',
            fulldata: 'Datos completos',
            onlychanges: 'Solo cambios',
            otherrelations: 'Otras relaciones',
            processstatus: 'Estado',
            referencerelation: 'Relaciones de referencia',
            relation: 'Relación',
            system: 'Sistema',
            type: 'Tipo',
            user: 'Usuario',
            userdescription: 'Descripción de usuario',
            value: 'Valor',
            viewmode: 'Modo de vista'
        },
        homepage: {
            search: {
                airequestproblem:
                    '<em>A problem occurred while processing the request. Please try again later or contact the administrator.</em>',
                aisearcherror: '<em>Unable to complete the search</em>',
                goodafternoon: 'Buenas tardes',
                goodevening: 'Buenas tardes',
                goodmorning: 'Buenos días',
                goodnight: 'Buenas noches',
                noresults: '<em>No results found</em>',
                noresultshint: '<em>Try different keywords or check the spelling</em>',
                resultsfor: '<em>Results for: {0}</em>'
            }
        },
        iconscategories: {
            accessibility: 'Accesibilidad',
            animals: 'Animales',
            arrows: 'Flechas',
            audiovideo: 'Audio vídeo',
            automotive: 'Automotor',
            autumn: 'Otoño',
            beverage: 'Bebida',
            buildings: 'Edificios',
            business: 'Negocio',
            charity: 'Caridad',
            chat: 'Chat',
            chess: 'Ajedrez',
            childhood: 'Infancia',
            code: 'Código',
            communication: 'Comunicación',
            computers: 'Computadoras',
            construction: 'Construcción',
            currency: 'Moneda',
            datetime: 'Fecha y hora',
            design: 'Diseño',
            editors: 'Editores',
            education: 'Educación',
            emoji: 'Emoji',
            energy: 'Energía',
            files: 'Archivos',
            finance: 'Finanzas',
            food: 'Alimento',
            fruitvegetable: 'Frutas y verduras',
            games: 'Juegos',
            gamingtabletop: 'Juegos de mesa',
            gender: 'Género',
            general: 'General',
            halloween: 'Halloween',
            hands: 'Manos',
            hotel: 'Hotel',
            household: 'Familia',
            images: 'Imágenes',
            interfaces: 'Interfaces',
            logistics: 'Logística',
            maps: 'Mapas',
            maritime: 'Marítimos',
            marketing: 'Marketing',
            mathematics: 'Matemáticas',
            medical: 'Sanitario',
            moving: 'Movimiento',
            music: 'Música',
            objects: 'Vista de árbol',
            paymentsshopping: 'Pagos de compras',
            political: 'Político',
            religion: 'Religión',
            sciencefiction: 'Ciencia ficción',
            security: 'Seguridad',
            shapes: 'Formas',
            shopping: 'Compras',
            social: 'Social',
            spinners: 'Spinners',
            sports: 'Deportes',
            spring: 'Primavera',
            status: 'Estado',
            toggle: 'Activar',
            travel: 'Viajar',
            userspeople: 'Usuarios personas',
            vehicles: 'Vehículos',
            weather: 'Clima',
            winter: 'Invierno'
        },
        importexport: {
            database: {
                uri: 'Base de datos URI',
                user: 'Usuario de la base de datos'
            },
            downloadreport: 'Descargar informe',
            emailfailure: '¡Se produjo un error al enviar un correo electrónico!',
            emailmessage: 'Se adjunta el informe de importación del archivo "{0}" en la fecha {1}',
            emailsubject: 'Importar informe de datos',
            emailsuccess: '¡El correo electrónico ha sido enviado con éxito!',
            export: 'Exportar',
            exportalldata: 'Toda la información',
            exportfiltereddata: 'Solo datos que coinciden con el filtro de cuadrícula',
            gis: {
                importconfiguration: 'Importar configuración',
                shapeimportdisabled: 'La importación de formas no está habilitada para esta plantilla',
                shapeimportenabled: 'La importación de formas está habilitada para esta plantilla.'
            },
            ifc: {
                card: 'Tarjeta',
                project: 'Proyecto',
                sourcetype: 'Importar de'
            },
            import: 'Importar',
            importresponse: 'Importar respuesta',
            response: {
                created: 'Tarjetas creadas',
                deleted: 'Tarjetas eliminadas',
                errors: 'Errores',
                linenumber: 'Número de línea',
                message: 'Mensaje',
                modified: 'Tarjetas modificadas',
                notprocessed: 'Tarjetas no procesadas',
                processed: 'Filas procesadas',
                recordnumber: 'Número de registro',
                unchanged: 'Tarjetas no modificadas'
            },
            sendreport: 'Enviar informe',
            template: 'Plantilla',
            templatedefinition: 'Definición de plantilla'
        },
        joinviews: {
            active: 'Activa',
            addview: 'Agregar vista',
            alias: 'Alias',
            attribute: 'Atributo',
            attributecustomization: 'Personalizaciones de atributos',
            attributes: 'Atributos',
            attributeschoice: 'Elección de atributos',
            attributesof: 'Atributos de: {0}',
            createview: 'Crear vista',
            datasorting: 'Clasificación de datos',
            delete: 'Eliminar',
            deleteview: 'Eliminar vista',
            deleteviewconfirm: '¿Estás seguro de que quieres eliminar esta vista?',
            description: 'Descripción',
            disable: 'Deshabilitar',
            domainalias: 'Alias de dominio',
            domainsof: 'Dominios de {0}',
            edit: 'Editar',
            editview: 'Editar configuración de vista',
            enable: 'Habilitar',
            fieldsets: 'Fieldsets',
            filters: 'Filtros',
            generalproperties: 'Propiedades generales',
            group: 'Grupo',
            innerjoin: 'Inner join',
            jointype: 'Tipo de join',
            joinview: 'Vista de join',
            klass: 'Clase',
            manageview: 'Administrar vista',
            masterclass: 'Clase principal',
            masterclassalias: 'Alias ​​de la clase principal',
            name: 'Nombre',
            newjoinview: 'Nueva vista de join',
            outerjoin: 'Outer join',
            pleaseseleceavalidmasterclass: 'Seleccione una clase principal válida',
            refreshafteredit: '¿Quieres actualizar la página para ver los cambios?',
            selectatleastoneattribute: 'Seleccione al menos un atributo',
            selectdisplayattributegrid:
                'Seleccione al menos un atributo para mostrar en la cuadrícula y uno en la cuadrícula reducida',
            showingrid: 'Mostrar en cuadrícula',
            showinreducedgrid: 'Mostrar en cuadrícula reducida',
            targetalias: 'Alias ​​de clase de destino'
        },
        login: {
            buttons: {
                login: 'Iniciar sesión',
                logout: 'Cambiar usuario'
            },
            fields: {
                group: 'Grupo',
                language: 'Idioma',
                password: 'Contraseña',
                tenants: "'Tenants'",
                username: 'Nombre de usuario'
            },
            loggedin: 'Registrado',
            sso: {
                loginfailure: 'Error de autenticación',
                loginwith: 'Iniciar sesión con {0}',
                or: 'o',
                usernotenabled: 'Usuario no habilitado'
            },
            title: 'Iniciar sesión',
            welcome: 'Bienvenido de nuevo {0}.'
        },
        main: {
            administrationmodule: 'Módulo de Administración',
            alerts: 'Alertas',
            baseconfiguration: 'Base de configuracion',
            cardlock: {
                lockedmessage: 'No puedes editar esta tarjeta porque {0} lo está editando.',
                someone: 'Alguien'
            },
            changegroup: 'Cambiar grupo',
            changetenant: 'Cambiar {0}',
            confirmchangegroup: '¿Estás seguro de que quieres cambiar el grupo?',
            confirmchangetenants: "¿Estás seguro de que quieres cambiar 'tenant' activo?",
            confirmdisabletenant: '¿Estás seguro de que quieres deshabilitar la marca "Ignorar \'tenants\'"?',
            confirmenabletenant: '¿Estás seguro de que quieres habilitar la marca "Ignorar \'tenants\'"?',
            description: 'Descripción',
            ignoretenants: 'Ignorar {0}',
            info: 'Información',
            logo: {
                cmdbuild: 'CMDBuild logo',
                cmdbuildready2use: 'CMDBuild READY2USE logo',
                companylogo: 'Logo de la compañía',
                hdaitsm: 'HDA ITSM',
                openmaint: 'openMAINT logo'
            },
            logout: 'Cerrar sesión',
            managementmodule: 'Módulo gestión de datos',
            multigroup: 'Multi grupo',
            multitenant: 'Multi {0}',
            navigation: 'Navegación',
            pagenotfound: 'Página no encontrada',
            password: {
                change: 'Cambiar clave',
                confirm: 'Confirmar clave',
                email: 'Dirección de correo electrónico',
                err_confirm: 'La contraseña no coincide.',
                err_diffprevious: 'La contraseña no puede ser idéntica a la anterior.',
                err_diffusername: 'La contraseña no puede ser idéntica al nombre de usuario.',
                err_length: 'La contraseña debe tener al menos {0} caracteres de longitud.',
                err_reqdigit: 'La contraseña debe contener al menos un dígito.',
                err_reqlowercase: 'La contraseña debe contener al menos un carácter en minúscula.',
                err_requppercase: 'La contraseña debe contener al menos un carácter en mayúscula.',
                expired: 'Tu contraseña ha expirado. Debes cambiarlo ahora.',
                forgotten: 'Olvidé mi contraseña',
                new: 'Nueva contraseña',
                old: 'Contraseña anterior',
                recoverysuccess:
                    'Le hemos enviado un correo electrónico con instrucciones para recuperar su contraseña.',
                reset: 'Restablecer la contraseña',
                saved: 'Contraseña guardada correctamente!'
            },
            pleasecorrecterrors: 'Por favor, corrija los errores indicados!',
            preferences: {
                comma: 'Coma',
                decimalserror: 'Campo decimales debe estar presente',
                decimalstousandserror: 'Decimales y miles deben ser diferentes',
                default: 'Por defecto',
                defaultvalue: 'Valor por defecto',
                favouritemenulocation: 'Ubicación del menú de favoritos',
                firstdayofweek: 'Primer dia de la semana',
                gridpreferencesclear: 'Borrar preferencias de la cuadrícula',
                gridpreferencescleared: '¡Preferencias de cuadrícula borradas!',
                gridpreferencessave: 'Guardar preferencias de cuadrícula',
                gridpreferencessaved: '¡Preferencias de cuadrícula guardadas!',
                gridpreferencesupdate: 'Actualizar preferencias de cuadrícula',
                groupemailsbystatus: 'Mostrar correos electrónicos agrupados por estado',
                labelcsvseparator: 'Separador CSV',
                labeldateformat: 'Formato de fecha',
                labeldecimalsseparator: 'Separador de decimales',
                labellanguage: 'Idioma',
                labelthousandsseparator: 'Separador de miles',
                labeltimeformat: 'Formato de tiempo',
                msoffice: 'Microsoft Office',
                no: 'No',
                notificationslabel: 'Chat y notificaciones',
                notificationssound: 'Reproducir sonido de notificación',
                notificationssoundvolume: 'Volumen de notificación',
                period: 'Punto',
                preferredfilecharset: 'Codificación CSV',
                preferredofficesuite: 'Office suite preferida',
                sendcancellationperiod: 'Período de cancelación de envío de correo electrónico (segundos)',
                showcollapsed: 'Mostrar colapsado',
                showfovouritesafter: 'Mostrar después del menú de navegación',
                showfovouritesbefore: 'Mostrar antes del menú de navegación',
                space: 'Espacio',
                thousandserror: 'Campo miles debe estar presente',
                timezone: 'Zona horaria',
                twelvehourformat: 'Formato de 12 horas',
                twentyfourhourformat: 'Formato de 24 horas',
                yes: 'Sí'
            },
            searchinallitems: 'Buscar en todos los artículos',
            searchtenant: 'Buscar tenant',
            selectdeselettenants: 'Seleccionar/Deseleccionar todo',
            treenavcontenttitle: '{0} de {1}',
            userpreferences: 'Preferencias'
        },
        menu: {
            allitems: 'Todos los artículos',
            classes: 'Clases',
            custompages: 'Páginas personalizadas',
            dashboards: 'Tableros',
            dependencytrees: 'Árboles de dependencias',
            favourites: 'Favoritos',
            favouritesadd: 'Agregar a favoritos',
            favouritesremove: 'Eliminar de favoritos',
            homepage: 'Página principal',
            processes: 'Procesos',
            reports: 'Informes',
            setasinitialpage: 'Establecer como página inicial',
            views: 'Vistas'
        },
        mobile: {
            config: {
                configure: 'Configurar dispositivo móvil',
                customercode: 'Código de cliente',
                devicename: 'Nombre del dispositivo',
                regenerate: 'Regenerar',
                serverurl: 'URL del servidor'
            }
        },
        notes: {
            edit: 'Modifica nota'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Eliminar notificación',
            emptymessage: 'No hay notificaciones para mostrar.',
            label: 'Notificaciones',
            viewall: 'Ver todo'
        },
        notifier: {
            attention: 'Atención',
            copied: 'Copiado',
            copiedtotheclipboard: 'Texto copiado al portapapeles',
            error: 'Error',
            genericerror: 'Error generico',
            genericerrormessage: 'Para obtener más información, consulte los registros del sistema.',
            genericinfo: 'Información genérica',
            genericwarning: 'Advertencia genérica',
            info: 'Información',
            more: 'Más información',
            success: 'Éxito',
            warning: 'Atención'
        },
        patches: {
            apply: 'Aplicar parches',
            category: 'Categoria',
            description: 'Descripción',
            name: 'Nombre',
            patches: 'Parches'
        },
        processes: {
            abortconfirmation: '¿Estás seguro de que quieres interrumpir este proceso?',
            abortprocess: 'Interrumpir proceso',
            action: {
                advance: 'Continúa',
                label: 'Acción'
            },
            activeprocesses: 'Procesos activos',
            allstatuses: 'Todos',
            editactivity: 'Modificar actividad',
            openactivity: 'Actividad abierta',
            resumeprocesses: 'Los procesos que no están suspendidos no se pueden reactivar',
            startworkflow: 'Inicio',
            workflow: 'Flujo de trabajo'
        },
        relationGraph: {
            activity: 'Actividad',
            allLabelsOnGraph: 'todas las etiquetas en el gráfico',
            card: 'Tarjeta',
            cardList: 'Lista de tarjetas',
            cardRelations: 'Relaciones de tarjeta',
            choosenaviagationtree: 'Elegir el árbol de navegación',
            class: 'Clase',
            classList: 'Lista de clase',
            compoundnode: 'Nodo Compuesto',
            disable: 'Deshabilitar',
            edges: 'Arcos',
            enable: 'Habilitar',
            labelsOnGraph: 'Descripción en el gráfico',
            level: 'Nivel',
            nodes: 'Nodos',
            openRelationGraph: 'Abrir gráfico de las relaciones',
            quantity: 'Cantidad',
            refresh: 'Actualizar',
            relation: 'Relación',
            relationGraph: 'Gráfico de las relaciones',
            reopengraph: 'Volver a abrir el grafo desde este nodo'
        },
        relations: {
            adddetail: 'Agregar detalle',
            addrelations: 'Agregar relaciones',
            attributes: 'Atributos',
            begindate: 'Fecha de inicio',
            cardnotpresentmessage:
                'La tarjeta relacionada ya no está disponible. Para proceder a la modificación será necesario elegir otra tarjeta.',
            code: 'Código',
            deletedetail: 'Eliminar detalle',
            deleterelation: 'Eliminar relación',
            deleterelationconfirm: '¿Estás seguro de que quieres eliminar esta relación?',
            description: 'Descripción',
            editcard: 'Editar tarjeta',
            editdetail: 'Editar detalle',
            editrelation: 'Editar relación',
            errorexisting:
                'La relación "{0}" ya existe. Edite la existente o elimínela antes de crear una nueva relación.',
            extendeddata: 'Datos extendidos',
            mditems: 'Elementos',
            missingattributes: 'Faltan atributos obligatorios',
            opencard: 'Abrir tarjeta relacionada',
            opendetail: 'Mostrar detalle',
            relationdata: 'Datos de relación',
            type: 'Tipo'
        },
        reports: {
            csv: 'CSV',
            download: 'Descargar',
            format: 'Formato',
            msgs: {
                creationerror: 'Hubo errores al crear el informe.',
                creationstarted:
                    'Se inició la creación del informe. Se le notificará cuando esté listo para la descarga.'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Imprimir',
            reload: 'Recargar',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Por defecto'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Diario'
                        },
                        monthly: {
                            description: 'Mensual'
                        },
                        once: {
                            description: 'Una vez'
                        },
                        weekly: {
                            description: 'Semanal'
                        },
                        yearly: {
                            description: 'Anual'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Por defecto'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Nadie'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Agregar tematismo',
            analysisType: 'Tipo de analisis',
            attribute: 'Atributo',
            bordersIntervals: 'Configuración incorrecta para los límites de intervalos',
            calculateRules: 'Generar reglas de estilo',
            clearThematism: 'Borrar tematismo',
            color: 'Color',
            defineLegend: 'Definición de leyenda',
            defineThematism: 'Definición de tematismo',
            deletethematism: '¿Estás seguro de que quieres eliminar este tematismo?',
            editThematism: 'Editar configuración de reglas',
            function: 'Función',
            generate: 'Generar',
            geoAttribute: 'Atributo geográfico',
            graduated: 'Graduado',
            highlightSelected: 'Resaltar el elemento seleccionado',
            intervals: 'Intervalos',
            legend: 'Leyenda',
            name: 'Nombre',
            newThematism: 'Nuevo tematismo',
            punctual: 'Puntual',
            quantity: 'Cantidad',
            segments: 'Segmentos',
            source: 'Fuente',
            table: 'Mesa',
            thematism: 'Tematismos',
            value: 'Valor'
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
                    exitwithoutsave: 'Salir sin guardar',
                    stayinedit: 'Volver a editar'
                },
                savebeforeexit: 'Hay cambios sin guardar en los adjuntos. ¿Quieres guardarlos antes de irte?',
                saveprocessmessage:
                    'Los archivos adjuntos solo se pueden guardar en tarjetas existentes. Al presionar en guardar, la instancia de proceso se creará con los datos presentes en el formulario.'
            },
            calendar: {
                error: 'El parámetro {0} no está configurado',
                errorwrong: 'El parámetro {0} no está configurado correctamente'
            },
            customform: {
                addrow: 'Agregar fila',
                clonerow: 'Clonar fila',
                datanotvalid: 'Datos no validos',
                deleterow: 'Eliminar fila',
                editrow: 'Editar fila',
                export: 'Exportar',
                import: 'Importar',
                importexport: {
                    expattributes: 'Datos para exportar',
                    file: 'Archivo',
                    filename: 'Nombre archivo',
                    format: 'Formato',
                    importmode: 'Modo de importación',
                    keyattributes: 'Atributos claves',
                    missingkeyattr: 'Elija al menos un atributo clave',
                    modeadd: 'Agregar',
                    modemerge: 'Fusionar',
                    modereplace: 'Reemplazar',
                    separator: 'Separador'
                },
                refresh: 'Actualizar a los valores predeterminados'
            },
            linkcards: {
                checkedonly: 'Solo marcado',
                editcard: 'Editar tarjeta',
                opencard: 'Abrir tarjeta',
                refreshselection: 'Aplicar selección por defecto',
                removefilterconfirm: 'Esta acción eliminará el filtro actual. ¿Desea continuar?',
                selectioncleared: 'Selección borrada',
                togglefilterdisabled: 'Deshabilitar filtro de cuadrícula',
                togglefilterenabled: 'Habilitar filtro de cuadrícula'
            },
            notimplemented: '¡El widget <strong>{0}</strong> no está implementado!',
            notsupportedinline:
                'El modo en línea no es compatible con este tipo de widget. Corrija la configuración del widget.',
            required: 'Este widget es obligatorio.'
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
