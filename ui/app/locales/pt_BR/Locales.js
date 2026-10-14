(function () {
    Ext.define('CMDBuildUI.locales.pt_BR.Locales', {
        requires: ['CMDBuildUI.locales.pt_BR.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'pt_BR',
        administration: CMDBuildUI.locales.pt_BR.LocalesAdministration.administration,
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
            add: 'Adicionar anexo',
            attachmenthistory: 'Histórico de anexos',
            author: 'Autor',
            authordescription: '<em>Author description</em>',
            browse: 'Procurar &hellip;',
            category: 'Categoria',
            code: 'Código',
            creationdate: 'Data de criação',
            deleteattachment: 'Apagar Anexo',
            deleteattachment_confirmation: 'Tem certeza que deseja apagar este anexo?',
            description: 'Descrição',
            download: 'Baixar',
            dropfiles: 'Soltar arquivos aqui',
            editattachment: 'Editar anexos',
            errordms: '<em>The DMS is disabled and it is not possible to save files</em>',
            file: 'Arquivo',
            filealreadyinlist: 'O arquivo {0} já está na lista.',
            filename: 'Nome do arquivo',
            fileview: '<em>View attachment</em>',
            invalidfiles: 'Remover arquivos inválidos',
            majorversion: 'Versão principal',
            maxsize: '<em>The maximum allowed file size is {0} MB.</em>',
            metadata: '<em>Metadata</em>',
            modificationdate: 'Data de modificação',
            new: 'Novo anexo',
            nocategory: 'Uncategorized',
            preview: 'Visualizar',
            removefile: 'Remover arquivo',
            statuses: {
                empty: 'Arquivo vazio',
                error: 'Erro',
                extensionNotAllowed: 'Extensão de arquivo não é permitida',
                loaded: 'Carregado',
                ready: 'Pronto',
                toolarge: '<em>Too large</em>'
            },
            successupload: '{0} anexos carregados.',
            uploadfile: 'Transferir arquivo',
            version: 'Versão',
            viewhistory: 'Exibir histórico de anexos',
            viewmetadata: '<em>View attachment metadata</em>',
            warningmessages: {
                atleast: '{0}: foi carregado {1} anexos do tipo "{2}". Esta categoria espera pelo menos {3} anexos ',
                exactlynumber: '{0}: foi carregado {1} anexos do tipo "{2}". Esta categoria espera {3} anexos',
                maxnumber: '{0}: foi carregado {1} anexo do tipo "{2}". Esta categoria espera no máximo {3} anexos'
            },
            wrongfileextension: '{0} extensão de arquivo não é permitida'
        },
        bim: {
            bimViewer: 'Visualizador BIM',
            card: {
                label: 'Cartão'
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
                label: 'Camadas',
                menu: {
                    hideAll: 'Esconder tudo',
                    hideAllLayers: '<em>Hide All Layers</em>',
                    hideAllXeokit: '<em>Hide all {0}</em>',
                    showAll: 'Exibir tudo',
                    showAllLayers: '<em>Show All Layers</em>',
                    showAllXeokit: '<em>Show all {0}</em>'
                },
                name: 'Nome',
                quantity: '<em>Quantity</em>',
                visibility: 'Visibilidade'
            },
            menu: {
                camera: 'Câmara',
                disableslice: '<em>Disable slice objects</em>',
                enableslice: '<em>Enable slice objects</em>',
                frontView: 'Visualização Frontal',
                mod: 'Controlos de visualização',
                ortho: '<em>Switch to Orthogonal View</em>',
                orthographic: 'Câmera ortográfica',
                pan: 'Rolagem',
                panXeokit: '<em>Viewer controls: scroll (Shift)</em>',
                perspe: '<em>Switch to Perspective View</em>',
                perspective: 'Câmera de perspectiva',
                resetSlice: '<em>Reset slice</em>',
                resetView: 'Reiniciar visualização',
                rotate: 'Rotacionar',
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
                sideView: 'Visualização lateral',
                threeD: '<em>Switch to 3D View</em>',
                topView: 'Visualização de cima',
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
            showBimCard: 'Abrir visualizador 3D',
            tree: {
                arrowTooltip: 'Selecionar elemento',
                label: 'Árvore',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Abrir cartão relacionado',
                root: 'Raiz Ifc'
            }
        },
        bulkactions: {
            abort: 'Abortar itens selecionados',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                '<em>Some attachments will not be downloaded because they have not yet been saved to the server</em>',
            alertnoattachmentsdownload:
                '<em>No attachments will be downloaded because they have not yet been saved to the server</em>',
            cancelselection: 'Cancelar a seleção',
            confirmabort: 'Você está abortando {0} casos de processo. Tem certeza que quer prosseguir?',
            confirmdelete: 'Você está excluindo {0} cartas. Tem certeza que quer prosseguir?',
            confirmdeleteattachements: 'Você está excluindo {0} anexos. Tem certeza que quer prosseguir?',
            confirmedit: 'Você está modificando {0} para cartões {1}. Tem certeza que quer prosseguir?',
            delete: 'Excluir itens selecionados',
            download: 'Baixe anexos selecionados',
            downloadall: '<em>Download all attachments</em>',
            edit: 'Editar itens selecionados',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: '<em>Please select at least one attribute.</em>',
            selectall: 'Selecione todos os itens'
        },
        calendar: {
            active_expired: 'Ativo/Expirado',
            add: 'Adicionar cronograma',
            advancenotification: 'Notificação antecipada de dias',
            allcategories: 'Todas as categorias',
            alldates: 'Todas as datas',
            associatedcard: '<em>Associated card</em>',
            calculated: 'Calculado',
            calendar: 'Calendário',
            cancel: 'Marca como cancelada',
            category: 'Categoria',
            class: '<em>Class</em>',
            cm_confirmcancel: 'Tem certeza que deseja marcar como horários selecionados cancelados?',
            cm_confirmcomplete: 'Tem certeza que deseja marcar como horários selecionados?',
            cm_markcancelled: 'Marque como horários selecionados cancelados',
            cm_markcomplete: 'Marque como cronogramas selecionados completos',
            complete: 'Marque como feito',
            completed: 'Concluído',
            date: 'Data',
            days: 'Dias',
            delaybeforedeadline: 'Atraso antes do prazo final',
            delaybeforedeadlinevalue: 'Atraso antes do valor do prazo',
            description: 'Descrição',
            editevent: 'Editar cronograma',
            enddate: 'Data de término',
            endtype: 'Tipo final',
            event: 'Agenda',
            execapplyfilter: '<em>Can not execute applyAdvanceFilter because there are more than one stores</em>',
            execremovefilter: '<em>Can not execute removeAdvanceFilter because there are more than one stores</em>',
            executiondate: 'Data da execução',
            frequency: 'Freqüência',
            frequencymultiplier: 'Multiplicador de frequência',
            grid: 'Grade',
            leftdays: 'Dias para ir',
            londdescription: 'Descrição completa',
            manual: 'Manual',
            maxactiveevents: 'Horários ativos máximos',
            messagebodydelete: 'Gostaria de remover a regra dos agendadores?',
            messagebodyplural: 'Existem regras de horário {0}',
            messagebodyrecalculate: ' Gostaria de recalcular a regra dos horários com a nova data?',
            messagebodysingular: 'Há {0} regra do cronograma',
            messagetitle: 'Cronograma recalculado',
            missingdays: 'Dias perdidos',
            next30days: 'Nos próximos 30 dias',
            next7days: 'Próximos 7 dias',
            notificationtemplate: 'Modelo usado para notificação',
            notificationtext: 'Texto de notificação',
            occurencies: 'Número de ocorrências',
            operation: 'Operação',
            partecipantgroup: 'Grupo partecipante',
            partecipantuser: 'Usuário partecipante',
            priority: 'Prioridade',
            recalculate: 'Recalcular',
            referent: 'Referente',
            scheduler: 'Agendador',
            sequencepaneltitle: 'Gerar horários',
            startdate: 'Data de início',
            status: 'Status',
            today: 'Hoje',
            type: 'Tipo',
            viewevent: 'Ver programação',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Critério de cálculo',
            widgetemails: 'E-mails',
            widgetgotocard: '<em>Go to source card</em>',
            widgetsourcecard: 'Cartão de origem'
        },
        chat: {
            title: '<em>Chat</em>'
        },
        classes: {
            cards: {
                addcard: 'Adicionar cartão',
                clone: 'Clonar',
                clonewithrelations: 'Clonar cartão e relações',
                deletebeaware: 'Esteja ciente de que:',
                deleteblocked: 'Não é possível prosseguir com a supressão porque há relações com {0}.',
                deletecard: 'Apagar cartão',
                deleteconfirmation: 'Tem certeza que deseja apagar este cartão?',
                deleterelatedcards: 'também {0} cartões relacionados serão excluídos',
                deleterelations: 'relações com {0} cartões serão excluídos',
                label: 'Cartões',
                modifycard: 'Editar cartão',
                opencard: 'Abrir cartão',
                print: 'Imprimir cartão'
            },
            simple: 'Simples',
            standard: 'Standard'
        },
        common: {
            actions: {
                add: 'Adicionar',
                apply: 'Aplicar',
                cancel: 'Cancelar',
                clear: '<em>Clear</em>',
                close: 'Fechar',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Apagar',
                edit: 'Editar',
                execute: 'Executar',
                forward: '<em>Forward</em>',
                help: 'Ajuda',
                hide: '<em>Hide</em>',
                load: 'Carga',
                open: 'Aberto',
                previous: '<em>Previous</em>',
                refresh: 'Atualizar dados',
                remove: 'Apagar',
                save: 'Salvar',
                saveandapply: 'Salvar e aplicar',
                saveandclose: 'Salvar e fechar',
                search: 'Pesquisar',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Pesquisar….'
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
                nogroup: 'Dados base'
            },
            dates: {
                date: 'dd/mm/yyyy',
                datetime: 'd/m/Y H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Limpar HTML',
                expand: 'Expandir editor',
                reduce: 'Reduzir editor',
                signature: '<em>Add signature</em>',
                unlink: '<em>Unlink</em>',
                unlinkmessage: '<em>Transform the selected hyperlink into text.</em>'
            },
            grid: {
                disablemultiselection: 'Desativar seleção múltipla',
                enamblemultiselection: 'Ativar seleção múltipla',
                export: 'Exportar dados',
                filterremoved: 'O filtro atual foi removido',
                import: 'Importar dados',
                itemnotfound: 'Item não localizado',
                lastUpdate: '<em>Last update</em>',
                list: 'Lista',
                opencontextualmenu: 'Abrir menu de contexto',
                print: 'Imprimir',
                printcsv: 'Imprimir como CSV',
                printmap: '<em>Print Map</em>',
                printodt: 'Imprimir como ODT',
                printpdf: 'Imprimir como PDF',
                row: 'Item',
                rows: 'Itens',
                subtype: 'Sub Tipo',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Atividade',
                attachment: 'Anexo',
                attachments: 'Anexos',
                card: 'Cartão',
                clonerelationmode: 'Modo de Relações com Clones',
                details: 'Detalhes',
                emails: 'Emails',
                history: 'Histórico',
                notes: 'Anotações',
                relations: 'Relações',
                schedules: 'Horários',
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
                gridhide: 'Ocultar grade de dados',
                gridshow: 'Mostrar grade de dados',
                openinpopup: '<em>Open in popup</em>',
                reload: 'Recarregar',
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
            addattachmentsfromdocumentarchive: 'Adicionar anexos do arquivo de documentos',
            alredyexistfile: 'Já existe um arquivo com este nome',
            archivingdate: 'Data de arquivo',
            attachfile: 'Anexar arquivo',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Escrever email',
            composefromtemplate: 'Escrever a partir de um modelo',
            delay: 'Atraso',
            delays: {
                day1: 'Em 1 dia',
                days2: 'Em 2 dias',
                days4: 'Em 4 dias',
                hour1: '1 hora',
                hours2: '2 horas',
                hours4: '4 horas',
                month1: 'Em 1 mês',
                negativeday1: '1 dia antes',
                negativedays2: '2 dias antes',
                negativedays4: '4 dias antes',
                negativehour1: '1 hora antes',
                negativehours2: '2 horas antes',
                negativehours4: '4 horas antes',
                negativemonth1: '1 mês antes',
                negativeweek1: '1 semana antes',
                negativeweeks2: '2 semanas antes',
                none: 'Nenhum',
                week1: 'Em 1 semana',
                weeks2: 'Em 2 semanas'
            },
            dmspaneltitle: 'Selecionar anexos via banco de dados',
            edit: 'Editar',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'De',
            gridrefresh: 'Atualizar grade',
            groupbystatus: '<em>Grouped by status</em>',
            keepsynchronization: 'Manter sincronizado',
            maxsize: '<em>The maximum total size allowed for attachments is {0}.</em>',
            message: 'Mensagem',
            regenerateallemails: 'Recriar todos os emails',
            regenerateemail: 'Recriar email',
            reloadconfirmationmessage: '<em>E-mails in draft status will be deleted. Do you want to continue?</em>',
            remove: 'Apagar',
            remove_confirmation: 'Tem certeza que deseja apagar este email?',
            reply: 'Responder',
            replyprefix: 'Em {0}, {1} escreveu:',
            searchattachment: '<em>Search Attachment</em>',
            searchcard: '<em>Search card</em>',
            selectaclass: 'Selecione uma classe',
            sendemail: 'Enviar email',
            sendingemail: '<em>Sendig email.</em>',
            signature: '<em>Signature</em>',
            size: '<em>Size</em>',
            status: '<em>Status</em>',
            statuses: {
                draft: 'Rascunho',
                error: 'Erro',
                new: '<em>New</em>',
                outgoing: 'Enviando',
                received: 'Recebido',
                sent: 'Enviado',
                skipped: '<em>Skipped</em>'
            },
            subject: 'Assunto',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Para',
            view: 'Exibir'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Usuário ou senha incorretos',
            classnotfound: 'Classe {0} não encontrada',
            fieldrequired: 'Este campo é necessário',
            invalidfilter: 'Filtro inválido',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Item não encontrado',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Ações',
            addfilter: 'Adicionar filtro',
            any: '<em>At least one</em>',
            attachments: '<em>Attachments</em>',
            attachmentssearchtext: '<em>Attachments search text</em>',
            attribute: 'Escolher um atributo',
            attributes: 'Atributos',
            clearfilter: 'Limpar filtro',
            clone: 'Clonar',
            copyof: 'Cópia de',
            currentgroup: 'Grupo atual',
            currentuser: 'Usuário atual',
            defaultset: 'Definido como padrão',
            defaultunset: 'Desafinido do padrão',
            description: 'Descrição',
            domain: 'Domínio',
            errorname: '<em>Filter name already in use</em>',
            filterdata: 'Filtrar data',
            fromfilter: '<em>From filter</em>',
            fromselection: 'A partir da seleção',
            group: 'Grupo',
            ignore: 'Ignorar',
            migrate: 'Migrar',
            name: 'Nome',
            newfilter: 'Novo filtro',
            noone: 'Ninguém',
            operator: 'Operador',
            operators: {
                and: '<em>And</em>',
                beginswith: 'Inicia em',
                between: 'Entre',
                contained: 'Incluído',
                containedorequal: 'Incluído ou igual',
                contains: 'Contêm',
                containsorequal: 'Contêm ou igual',
                descriptionbegin: '<em>Description begins with</em>',
                descriptioncontains: 'Descrição contém',
                descriptionends: '<em>Description ends with</em>',
                descriptionnotbegin: '<em>Description does not begins with</em>',
                descriptionnotcontain: '<em>Description does not contain</em>',
                descriptionnotends: '<em>Description does not ends with</em>',
                different: 'Diferente',
                doesnotbeginwith: 'Não começa em',
                doesnotcontain: 'Não inclúi',
                doesnotendwith: 'Não termina em',
                endswith: 'Termina em',
                equals: 'Igual',
                greaterthan: 'Maior que',
                isnotnull: 'Não é nulo',
                isnull: 'É nulo',
                lessthan: 'Menor que',
                or: '<em>Or</em>'
            },
            relations: 'Relações',
            removeattribute: '<em>Remove attribute</em>',
            removeblock: '<em>Remove block</em>',
            type: 'Tipo',
            typeinput: 'Parâmetro de entrada',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Usuário',
            value: 'Valor'
        },
        gis: {
            card: 'Cartão',
            cardsMenu: 'Menu de cartões',
            code: 'Código',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Descrição',
            extension: {
                errorCall: 'Erro',
                noResults: 'Sem resultados'
            },
            externalServices: 'Serviços externos',
            geographicalAttributes: 'Atributos geo',
            geoserverLayers: 'Camadas Geoserver',
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
            layers: 'Camadas',
            layersTab: {
                clearPreferences: '<em>Clear layers preferences</em>',
                preferencesCleared: '<em>Layers preferences cleared</em>',
                preferencesSaved: '<em>Layers preferences saved</em>',
                savePreferences: '<em>Save layers preferences</em>',
                updatePreferences: '<em>Update layers preferences</em>'
            },
            list: 'Lista',
            longpresstitle: 'Geoelements na área',
            map: 'Mapa',
            mapServices: 'Serviços de mapa',
            menu: {
                font: '<em>Font size</em>',
                options: '<em>Options</em>'
            },
            position: 'Posição',
            root: 'Raiz',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Árvore',
            type: 'Tipo',
            view: 'Vista',
            zoom: 'Ampliação'
        },
        history: {
            activityname: 'Nome da atividade',
            activityperformer: 'Executante da atividade',
            attributes: '<em>Attributes ({0})</em>',
            begindate: 'Data de início',
            enddate: 'Data de fim',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: '<em>Other relations</em>',
            processstatus: 'Estado',
            referencerelation: '<em>Reference relations</em>',
            relation: '<em>Relation</em>',
            system: '<em>System</em>',
            type: '<em>Type</em>',
            user: 'Usuário',
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
                uri: 'Banco de dados URI',
                user: 'Usuário de banco de dados'
            },
            downloadreport: 'Baixar relatório',
            emailfailure: 'Um problema ocorreu ao enviar o email',
            emailmessage: 'Relatório de importação anexado do arquivo "{0}" em data {1}',
            emailsubject: 'Importar relatório de dados',
            emailsuccess: 'O email foi enviado com sucesso!',
            export: 'Exportar',
            exportalldata: 'Todos os dados',
            exportfiltereddata: 'Apenas dados que correspondem ao filtro de grade',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'A importação de formas não está habilitada para este modelo',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: '<em>Card</em>',
                project: 'Projeto',
                sourcetype: 'Importação de'
            },
            import: 'Importar',
            importresponse: 'Importar resposta',
            response: {
                created: '<em>Created cards</em>',
                deleted: '<em>Deleted cards</em>',
                errors: 'Erros',
                linenumber: 'Número da linha',
                message: 'Mensagem',
                modified: '<em>Modified cards</em>',
                notprocessed: '<em>Unprocessed cards</em>',
                processed: 'Linhas processadas',
                recordnumber: 'Número de registro',
                unchanged: '<em>Unchanged cards</em>'
            },
            sendreport: 'Enviar relatório',
            template: 'Template',
            templatedefinition: 'Definição de modelo'
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
                login: 'Entrar',
                logout: 'Alterar usuário'
            },
            fields: {
                group: 'Grupo',
                language: 'Idioma',
                password: 'Senha',
                tenants: 'Tenants',
                username: 'Usuário'
            },
            loggedin: 'Entrou',
            sso: {
                loginfailure: '<em>Login failure</em>',
                loginwith: '<em>Login with {0}</em>',
                or: '<em>or</em>',
                usernotenabled: '<em>User not enabled</em>'
            },
            title: 'Entrar',
            welcome: 'Bem vindo novamente'
        },
        main: {
            administrationmodule: 'Módulo de Administração',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Configuração base',
            cardlock: {
                lockedmessage: 'Não é possível editar este cartão pois {0} está o editando',
                someone: 'Alguém'
            },
            changegroup: 'Alterar grupo',
            changetenant: 'Alterar {0}',
            confirmchangegroup: 'Tem certeza que deseja alterar o grupo?',
            confirmchangetenants: 'Tem certeza que deseja alterar os tenants ativos?',
            confirmdisabletenant: 'Tem certeza que deseja desabilitar a flag "ignorar tenants"?',
            confirmenabletenant: 'Tem certeza que deseja habilitar a flag "ignorar tenants"?',
            description: '<em>Description</em>',
            ignoretenants: 'Ignorar {0}',
            info: 'Informação',
            logo: {
                cmdbuild: 'Logotipo CMDBBuild',
                cmdbuildready2use: 'Logotipo CMDBBuild READY2USE',
                companylogo: 'Logotipo da empresa',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'logotipo openMAINT'
            },
            logout: 'Sair',
            managementmodule: 'Módulo de Gerenciamento de Dados',
            multigroup: 'Multi grupo',
            multitenant: 'Multi {0}',
            navigation: 'Navegação',
            pagenotfound: 'Página não encontrada',
            password: {
                change: 'Alterar senha',
                confirm: 'Confirmar senha',
                email: 'Endereço de e-mail',
                err_confirm: 'A senha não bate.',
                err_diffprevious: 'A senha não pode ser idêntica à anterior.',
                err_diffusername: 'A senha não pode ser idêntica ao nome de usuário.',
                err_length: 'A senha deve ter pelo menos {0} caracteres.',
                err_reqdigit: 'A senha deve conter pelo menos um dígito.',
                err_reqlowercase: 'A senha deve conter pelo menos um caractere minúsculo.',
                err_requppercase: 'A senha deve conter pelo menos um caractere maiústo.',
                expired: 'Sua senha expirou. Você deve mudá-lo agora.',
                forgotten: 'Esqueci minha senha.',
                new: 'Nova senah',
                old: 'Senha Antiga',
                recoverysuccess: 'Enviamos um e-mail com instruções para recuperar sua senha.',
                reset: 'Redefinir senha',
                saved: 'Senha corretamente salva!'
            },
            pleasecorrecterrors: 'Por favor corrija os erros indicados ',
            preferences: {
                comma: 'Vírgula',
                decimalserror: 'O campo de decimais é necessário',
                decimalstousandserror: 'Os separadores decimais e de milhares precisam ser diferentes',
                default: 'Padrão',
                defaultvalue: 'Valor padrão',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: '<em>First day of week</em>',
                gridpreferencesclear: 'Preferências claras de grade',
                gridpreferencescleared: 'Preferências de grade limpas!',
                gridpreferencessave: 'Salvar preferências de grade',
                gridpreferencessaved: 'Preferências de grade salvas!',
                gridpreferencesupdate: 'Atualizar preferências de grade',
                groupemailsbystatus: '<em>Show emails grouped by status</em>',
                labelcsvseparator: 'Separador CSV',
                labeldateformat: 'Formato da data',
                labeldecimalsseparator: 'Separador decimal',
                labellanguage: 'Idioma',
                labelthousandsseparator: 'Separador de milhares',
                labeltimeformat: 'formato da hora',
                msoffice: 'Microsoft Office',
                no: '<em>No</em>',
                notificationslabel: '<em>Chat & Notifications</em>',
                notificationssound: '<em>Play sound for notifications</em>',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Ponto',
                preferredfilecharset: 'Codificação CSV',
                preferredofficesuite: 'Ferramenta Office preferida',
                sendcancellationperiod: '<em>Email send cancellation period (seconds)</em>',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: 'Espaço',
                thousandserror: 'O campo de milhares é necessário',
                timezone: 'Fuso horário',
                twelvehourformat: 'Formato 12 horas',
                twentyfourhourformat: 'Formato 24 horas',
                yes: '<em>Yes</em>'
            },
            searchinallitems: 'Pesquisar em todos os itens',
            searchtenant: '<em>Search tenant</em>',
            selectdeselettenants: '<em>Select/Deselect all</em>',
            treenavcontenttitle: '{0} de {1}',
            userpreferences: 'Preferências'
        },
        menu: {
            allitems: 'Todos os itens',
            classes: 'Classes',
            custompages: 'Páginas personalizadas',
            dashboards: 'Dashboards',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: 'Processos',
            reports: 'Relatórios',
            setasinitialpage: '<em>Set as initial page</em>',
            views: 'Visualizações'
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
            edit: 'Editar anotação'
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
            attention: 'Atenção',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Erro',
            genericerror: 'Erro genérico',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Informação genérica',
            genericwarning: 'Aviso genérico',
            info: 'Informação',
            more: '<em>More info</em>',
            success: 'Sucesso',
            warning: 'Aviso'
        },
        patches: {
            apply: 'Aplicar patches',
            category: 'Categoria',
            description: 'Descrição',
            name: 'Nome',
            patches: 'Patches'
        },
        processes: {
            abortconfirmation: 'Tem certeza que deseja interromper este processo?',
            abortprocess: 'Abortar processo',
            action: {
                advance: 'Avançar',
                label: 'Ação'
            },
            activeprocesses: 'Processos ativos',
            allstatuses: 'Todos',
            editactivity: 'Editar atividade',
            openactivity: 'Abrir atividade',
            resumeprocesses: '<em>Processes that are not suspended cannot be resumed</em>',
            startworkflow: 'Iniciar',
            workflow: 'Fluxo de trabalho'
        },
        relationGraph: {
            activity: 'atividade',
            allLabelsOnGraph: 'todos os rótulos em gráfico',
            card: 'Cartão',
            cardList: 'Lista de cartões',
            cardRelations: 'Relação entre Cartões',
            choosenaviagationtree: 'Alterar árvore de navegação',
            class: 'Classe',
            classList: 'Lista de Classes',
            compoundnode: 'Nó composto',
            disable: 'Desativar',
            edges: '<em>Edges</em>',
            enable: 'Permitir',
            labelsOnGraph: 'ponta de ferramenta no gráfico',
            level: 'Nível',
            nodes: '<em>Nodes</em>',
            openRelationGraph: 'Abrir gráfico de relacionamentos',
            quantity: '<em>Quantity</em>',
            refresh: 'Atualizar',
            relation: 'Relação',
            relationGraph: 'Gráfico de relacionamento',
            reopengraph: 'Reabrir o gráfico deste nó'
        },
        relations: {
            adddetail: 'Adicionar detalhe',
            addrelations: 'Adicionar relacionamentos',
            attributes: 'Atributos',
            begindate: '<em>Begin date</em>',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Código',
            deletedetail: 'Apagar detalhe',
            deleterelation: 'Apagar relacionamento',
            deleterelationconfirm: 'Tem certeza que quer excluir essa relação?',
            description: 'Descrição',
            editcard: 'Editar cartão',
            editdetail: 'Editar detalhe',
            editrelation: 'Editar relacionamento',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Dados estendidos',
            mditems: 'Itens',
            missingattributes: 'Faltam atributos obrigatórios',
            opencard: 'Abrir cartão relacionado',
            opendetail: 'Exibir detalhe',
            relationdata: '<em>Relation data</em>',
            type: 'Tipo'
        },
        reports: {
            csv: 'CSV',
            download: 'Baixar',
            format: 'Formato',
            msgs: {
                creationerror: '<em>Something went wrong during the start of the report creation.</em>',
                creationstarted:
                    '<em>Report creation started. You will be notified when it will be ready for the download.</em>'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'Imprimir',
            reload: 'Recarregar',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Padrão'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Diária'
                        },
                        monthly: {
                            description: 'Mensal'
                        },
                        once: {
                            description: 'Quando'
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
                            description: 'Padrão'
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
            addThematism: 'Adicionar o hematismo',
            analysisType: 'Tipo de análise',
            attribute: 'Atributo',
            bordersIntervals: '<em>Wrong configuration for intervals limits</em>',
            calculateRules: 'Gerar regras de estilo',
            clearThematism: 'Thematismo Claro',
            color: 'Cor',
            defineLegend: 'Definição de legenda',
            defineThematism: 'Definição de thematismo',
            deletethematism: '<em>Are you sure that you want to delete this thematism?</em>',
            editThematism: '<em>Edit rules configuration</em>',
            function: 'Função',
            generate: 'Gerar',
            geoAttribute: 'Atributo geográfico',
            graduated: 'Graduado',
            highlightSelected: 'Marcar item selecionado',
            intervals: 'Intervalos',
            legend: 'legenda',
            name: 'Nome',
            newThematism: 'Novo hematismo',
            punctual: 'Pontual',
            quantity: 'Quantidade',
            segments: 'Segmentos',
            source: 'Fonte',
            table: 'Tabela',
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
                addrow: 'Adicionar linha',
                clonerow: 'Clonar linha',
                datanotvalid: 'Dados não válidos',
                deleterow: 'Apagar linha',
                editrow: 'Editar linha',
                export: 'Exportar',
                import: 'Importar',
                importexport: {
                    expattributes: 'Dados para exportação',
                    file: 'Arquivo',
                    filename: 'Nome do arquivo',
                    format: 'Formato',
                    importmode: 'Modo de importação',
                    keyattributes: 'Principais atributos',
                    missingkeyattr: 'Por favor, escolha pelo menos um atributo-chave',
                    modeadd: 'Adicionar',
                    modemerge: 'Mesclagem',
                    modereplace: 'Substituir',
                    separator: 'Separador'
                },
                refresh: 'Atualizar para dados padrão'
            },
            linkcards: {
                checkedonly: 'Verificado apenas',
                editcard: 'Editar cartão',
                opencard: 'Abrir cartão',
                refreshselection: 'Aplicar seleção padrão',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Ativar filtro de grade',
                togglefilterenabled: 'Ativar filtro de grade'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                '<em>Inline mode is not supported for this widget type. Please fix the widget configuration.</em>',
            required: 'Este widget é necessário.'
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
