(function () {
    Ext.define('CMDBuildUI.locales.ko.Locales', {
        requires: ['CMDBuildUI.locales.ko.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ko',
        administration: CMDBuildUI.locales.ko.LocalesAdministration.administration,
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
            add: '첨부 추가',
            attachmenthistory: '첨부이력',
            author: '생성자',
            authordescription: '<em>Author description</em>',
            browse: '<em>Browse &hellip;</em>',
            category: '카테고리',
            code: '<em>Code</em>',
            creationdate: '날짜 생성',
            deleteattachment: '첨부 삭제',
            deleteattachment_confirmation: '이 첨부를 삭제 하시겠습니까?',
            description: '설명',
            download: '다운로드',
            dropfiles: '파일을 여기에 추가하세요',
            editattachment: '첨부 수정',
            errordms: '<em>The DMS is disabled and it is not possible to save files</em>',
            file: '파일',
            filealreadyinlist: '파일 {0}는 이미 목록에 있습니다.',
            filename: '파일명',
            fileview: '<em>View attachment</em>',
            invalidfiles: '잘못된 파일 제거',
            majorversion: '메이저버전',
            maxsize: '<em>The maximum allowed file size is {0} MB.</em>',
            metadata: '<em>Metadata</em>',
            modificationdate: '갱신일',
            new: '신규 첨부',
            nocategory: '분류되지 않음',
            preview: '미리보기',
            removefile: '파일제거',
            statuses: {
                empty: '빈파일',
                error: '오류',
                extensionNotAllowed: '파일 확장자가 허용되지 않음',
                loaded: '로드됨',
                ready: '준비',
                toolarge: '<em>Too large</em>'
            },
            successupload: '{0} 첨부가 업로드됨',
            uploadfile: '파일 업로드',
            version: '버전',
            viewhistory: '첨부이력 보기',
            viewmetadata: '<em>View attachment metadata</em>',
            warningmessages: {
                atleast:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects at least {3} attachments.</em>',
                exactlynumber:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects {3} attachments.</em>',
                maxnumber:
                    '<em>{0}: {1} attachments of type "{2}" have been loaded. This category expects at most {3}  attachments.</em>'
            },
            wrongfileextension: '파일 확장자 {0}가 허용되지 않음'
        },
        bim: {
            bimViewer: 'BIM 뷰어',
            card: {
                label: '카드'
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
                label: '레이어',
                menu: {
                    hideAll: '모두 숨김',
                    hideAllLayers: '<em>Hide All Layers</em>',
                    hideAllXeokit: '<em>Hide all {0}</em>',
                    showAll: '모두 표시',
                    showAllLayers: '<em>Show All Layers</em>',
                    showAllXeokit: '<em>Show all {0}</em>'
                },
                name: '이름',
                quantity: '<em>Quantity</em>',
                visibility: '가시성'
            },
            menu: {
                camera: '카메라',
                disableslice: '<em>Disable slice objects</em>',
                enableslice: '<em>Enable slice objects</em>',
                frontView: '정면도',
                mod: '모드(뷰어 컨트롤)',
                ortho: '<em>Switch to Orthogonal View</em>',
                orthographic: '직교 카메라',
                pan: '팬 (스크롤)',
                panXeokit: '<em>Viewer controls: scroll (Shift)</em>',
                perspe: '<em>Switch to Perspective View</em>',
                perspective: '투시형 카메라',
                resetSlice: '<em>Reset slice</em>',
                resetView: '보기 재설정',
                rotate: '회전',
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
                sideView: '측면도',
                threeD: '<em>Switch to 3D View</em>',
                topView: '상면도',
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
            showBimCard: '3D 뷰어 열기',
            tree: {
                arrowTooltip: '구성요소 선택',
                label: '트리',
                multipleElements: '<em>Select elements</em>',
                open_card: '관련 카드 열기',
                root: 'IFC ROOT"IFC ROOT'
            }
        },
        bulkactions: {
            abort: '선택항목 중단',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments:
                '<em>Some attachments will not be downloaded because they have not yet been saved to the server</em>',
            alertnoattachmentsdownload:
                '<em>No attachments will be downloaded because they have not yet been saved to the server</em>',
            cancelselection: '선택 취소',
            confirmabort: '<em>You are aborting {0} process instances. Are you sure that you want to proceed?</em>',
            confirmdelete: '<em>You are deleting {0} cards. Are you sure that you want to proceed?</em>',
            confirmdeleteattachements:
                '<em>You are deleting {0} attachments. Are you sure that you want to proceed?</em>',
            confirmedit: '<em>You are modifying {0} on {1} cards. Are you sure that you want to proceed?</em>',
            delete: '선택 항목 삭제',
            download: '선택 항목 다운로드',
            downloadall: '<em>Download all attachments</em>',
            edit: '선택 항목 편집',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: '<em>Please select at least one attribute.</em>',
            selectall: '모든 항목 선택'
        },
        calendar: {
            active_expired: '활성/만기',
            add: '추가',
            advancenotification: '고급 알림',
            allcategories: '모든 카테고리',
            alldates: '모든 날짜',
            associatedcard: '<em>Associated card</em>',
            calculated: '계산됨',
            calendar: '달력',
            cancel: '취소됨 표기',
            category: '카테고리',
            class: '<em>Class</em>',
            cm_confirmcancel: '선택 취소된 스케줄로 표시하시겠습니까?',
            cm_confirmcomplete: '선택한 일정 순응으로 표시하시겠습니까?',
            cm_markcancelled: '선택된 스케줄 취소로 표시',
            cm_markcomplete: '선택된 스케줄 완료로 표시',
            complete: '완성',
            completed: '완료됨',
            date: '날짜',
            days: '날짜',
            delaybeforedeadline: '기한 이전 지연',
            delaybeforedeadlinevalue: '기한 이전 지연 값',
            description: '설명',
            editevent: '일정 수정',
            enddate: '종료일',
            endtype: '종료 유형',
            event: '일정',
            execapplyfilter: '<em>Can not execute applyAdvanceFilter because there are more than one stores</em>',
            execremovefilter: '<em>Can not execute removeAdvanceFilter because there are more than one stores</em>',
            executiondate: '실행 일자',
            frequency: '발생빈도',
            frequencymultiplier: '<em>Frequency multiplier</em>',
            grid: '그리드',
            leftdays: '이전 일자',
            londdescription: '전체 설명',
            manual: '수동',
            maxactiveevents: '최대 활성화된 이벤트',
            messagebodydelete: '스케쥴러 규칙을 제거하시겠습니까?',
            messagebodyplural: '{0}가지 일정 규칙들이 있습니다.',
            messagebodyrecalculate: '신규 날짜로 일정 규칙을 다시 계산하시겠습니까?',
            messagebodysingular: '{0}가지 일정 규칙이 있습니다.',
            messagetitle: '일정 다시 계산중',
            missingdays: '결손 일수',
            next30days: '이후 한달',
            next7days: '이후 일주일',
            notificationtemplate: '<em>Template used for notification</em>',
            notificationtext: '<em>Notification text</em>',
            occurencies: '발생횟수',
            operation: '운영',
            partecipantgroup: '참석 그룹',
            partecipantuser: '참석자',
            priority: '우선권',
            recalculate: '다시계산',
            referent: '관련항목',
            scheduler: '일정편집기',
            sequencepaneltitle: '일정 생성',
            startdate: '시작일',
            status: '상태',
            today: '오늘',
            type: '유형',
            viewevent: '일정 보기',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: '계산기준',
            widgetemails: '이메일',
            widgetgotocard: '<em>Go to source card</em>',
            widgetsourcecard: '점수카드'
        },
        chat: {
            title: '<em>Chat</em>'
        },
        classes: {
            cards: {
                addcard: '카드 추가',
                clone: '복제',
                clonewithrelations: '카드 및 관계 복제',
                deletebeaware: '다음 사항 유의:',
                deleteblocked: '{0}와 관계가 있기 때문에 삭제할 수 없습니다.',
                deletecard: '카드 삭제',
                deleteconfirmation: '이 카드를 삭제 하시겠습니까?',
                deleterelatedcards: '또한 {0}개의 관련 카드가 삭제됨',
                deleterelations: '{0}개 카드와의 관계가 삭제됨',
                label: '카드',
                modifycard: '카드 수정',
                opencard: '카드 열기',
                print: '카드 출력'
            },
            simple: '단순',
            standard: '표준'
        },
        common: {
            actions: {
                add: '추가',
                apply: '적용',
                cancel: '취소',
                clear: '<em>Clear</em>',
                close: '닫기',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: '삭제',
                edit: '편집',
                execute: '실행',
                forward: '<em>Forward</em>',
                help: '도움',
                hide: '<em>Hide</em>',
                load: '탑재',
                open: '열기',
                previous: '<em>Previous</em>',
                refresh: '데이터 새로고침',
                remove: '삭제',
                save: '저장',
                saveandapply: '저장하고 적용',
                saveandclose: '저장하고 닫기',
                search: '검색',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: '검색'
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
                nogroup: '기초 데이터'
            },
            dates: {
                date: '일/월/연',
                datetime: '일/월/연 시:분:초',
                time: '시:분:초'
            },
            editor: {
                clearhtml: 'HTML 정리',
                expand: '확장편집기',
                reduce: '간단편집기',
                signature: '<em>Add signature</em>',
                unlink: '<em>Unlink</em>',
                unlinkmessage: '<em>Transform the selected hyperlink into text.</em>'
            },
            grid: {
                disablemultiselection: '복수 선택 불가',
                enamblemultiselection: '복수 선택 가능',
                export: '데이터 익스포트',
                filterremoved: '현재 필터가 제거되었습니다.',
                import: '데이터 임포트',
                itemnotfound: '아이템이 발견되지 않았습니다.',
                lastUpdate: '<em>Last update</em>',
                list: '목록',
                opencontextualmenu: '컨텍스트메뉴 열기',
                print: '인쇄',
                printcsv: 'CSV로 출력',
                printmap: '<em>Print Map</em>',
                printodt: 'ODT로 출력',
                printpdf: 'PDF로 출력',
                row: '아이템',
                rows: '아이템',
                subtype: '서브타입',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: '액티비티',
                attachment: '첨부',
                attachments: '첨부',
                card: '카드',
                clonerelationmode: '관계복사 모드',
                details: '상세',
                emails: '이메일',
                history: '이력',
                notes: '노트',
                relations: '관계',
                schedules: '일정',
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
                gridhide: '데이터 그리드 숨기기',
                gridshow: '데이터 그리도 보이기',
                openinpopup: '<em>Open in popup</em>',
                reload: '다시 탑재하기',
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
            addattachmentsfromdocumentarchive: '문서 저장소로부터 첨부 추가',
            alredyexistfile: '동일 이름의 파일 존재',
            archivingdate: '보관 일자',
            attachfile: '파일 첨부',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: '숨은참조',
            cc: '참조',
            composeemail: 'email 작성',
            composefromtemplate: '템플릿에서 작성',
            delay: '지연',
            delays: {
                day1: '하루',
                days2: '이틀',
                days4: '나흘',
                hour1: '1시간',
                hours2: '2시간',
                hours4: '4시간',
                month1: '1개월',
                negativeday1: '하루 전',
                negativedays2: '이틀 전',
                negativedays4: '나흘 전',
                negativehour1: '한시간 전',
                negativehours2: '두시간 전',
                negativehours4: '네시간 전',
                negativemonth1: '한달 전',
                negativeweek1: '한주 전',
                negativeweeks2: '이주 전',
                none: 'None',
                week1: '1주일',
                weeks2: '2주일'
            },
            dmspaneltitle: '데이터베이스에서 첨부 선택',
            edit: '편집',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: '발신',
            gridrefresh: '그리드 새로고침',
            groupbystatus: '<em>Grouped by status</em>',
            keepsynchronization: '동기화 유지',
            maxsize: '<em>The maximum total size allowed for attachments is {0}.</em>',
            message: '메시지',
            regenerateallemails: '모든 이메일 다시 생성',
            regenerateemail: '이메일 다시 작성',
            reloadconfirmationmessage: '<em>E-mails in draft status will be deleted. Do you want to continue?</em>',
            remove: '삭제',
            remove_confirmation: '이 이메일을 삭제 하시겠습니까?',
            reply: '답장',
            replyprefix: '<em>On {0}, {1} wrote:</em>',
            searchattachment: '<em>Search Attachment</em>',
            searchcard: '<em>Search card</em>',
            selectaclass: '클래스 선택',
            sendemail: '이메일 보내기',
            sendingemail: '<em>Sendig email.</em>',
            signature: '<em>Signature</em>',
            size: '<em>Size</em>',
            status: '<em>Status</em>',
            statuses: {
                draft: '초안',
                error: '오류',
                new: '<em>New</em>',
                outgoing: '발신',
                received: '수신',
                sent: '송신',
                skipped: '<em>Skipped</em>'
            },
            subject: '제목',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: '수신',
            view: '보기'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: '잘못된 사용자명 혹은 비밀번호',
            classnotfound: '클래스 {0}가 발견되지 않았습니다.',
            fieldrequired: '이 값은 필수입니다.',
            invalidfilter: '잘못된 필터',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: '아이템이 발견되지 않습니다',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: '액션',
            addfilter: '필터 추가',
            any: '<em>At least one</em>',
            attachments: '<em>Attachments</em>',
            attachmentssearchtext: '<em>Attachments search text</em>',
            attribute: '속성 선택',
            attributes: '속성',
            clearfilter: '필터 제거',
            clone: '복제',
            copyof: '복사',
            currentgroup: '현재 그룹',
            currentuser: '현재 사용자',
            defaultset: '기본값으로 설정',
            defaultunset: '기본값으로 설정 해제',
            description: '설명',
            domain: '도메인',
            errorname: '<em>Filter name already in use</em>',
            filterdata: '필터 데이터',
            fromfilter: '<em>From filter</em>',
            fromselection: '선택',
            group: '그룹',
            ignore: '무시',
            migrate: '이송',
            name: '이름',
            newfilter: '새로운 필터',
            noone: '아무것도 없음',
            operator: '운영자',
            operators: {
                and: '<em>And</em>',
                beginswith: '~로 시작',
                between: '간(사이)',
                contained: '포함',
                containedorequal: '포함 또는 동일',
                contains: '포함',
                containsorequal: '포함 또는 동일',
                descriptionbegin: '<em>Description begins with</em>',
                descriptioncontains: '<em>Description contains</em>',
                descriptionends: '<em>Description ends with</em>',
                descriptionnotbegin: '<em>Description does not begins with</em>',
                descriptionnotcontain: '<em>Description does not contain</em>',
                descriptionnotends: '<em>Description does not ends with</em>',
                different: '다른',
                doesnotbeginwith: '~로 시작하지 않는',
                doesnotcontain: '포함하지 않는',
                doesnotendwith: '끝나지 않는',
                endswith: '~로 끝나는',
                equals: '동일',
                greaterthan: '크게',
                isnotnull: 'null값 아님',
                isnull: 'null 값',
                lessthan: '보다 작은',
                or: '<em>Or</em>'
            },
            relations: '관계',
            removeattribute: '<em>Remove attribute</em>',
            removeblock: '<em>Remove block</em>',
            type: '타입',
            typeinput: '파라미터 입력',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: '사용자',
            value: '값'
        },
        gis: {
            card: '카드',
            cardsMenu: '카드 메뉴',
            code: '코드',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: '설명',
            extension: {
                errorCall: '오류',
                noResults: '결과 없음'
            },
            externalServices: '외부 서비스',
            geographicalAttributes: '지리적 속성',
            geoserverLayers: 'Geo서버 레이어',
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
            layers: '레이어',
            layersTab: {
                clearPreferences: '<em>Clear layers preferences</em>',
                preferencesCleared: '<em>Layers preferences cleared</em>',
                preferencesSaved: '<em>Layers preferences saved</em>',
                savePreferences: '<em>Save layers preferences</em>',
                updatePreferences: '<em>Update layers preferences</em>'
            },
            list: '목록',
            longpresstitle: '영역 내 지리요소',
            map: '지도',
            mapServices: '지도 서비스',
            menu: {
                font: '<em>Font size</em>',
                options: '<em>Options</em>'
            },
            position: '위치',
            root: '루트',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: '조회 트리',
            type: '유형',
            view: '보기',
            zoom: '줌'
        },
        history: {
            activityname: '액티비티 이름',
            activityperformer: '액티비티 실행자',
            attributes: '<em>Attributes ({0})</em>',
            begindate: '시작일',
            enddate: '종료일',
            fieldname: '<em>Field name</em>',
            fulldata: '<em>Full data</em>',
            onlychanges: '<em>Only changes</em>',
            otherrelations: '<em>Other relations</em>',
            processstatus: '상태',
            referencerelation: '<em>Reference relations</em>',
            relation: '<em>Relation</em>',
            system: '<em>System</em>',
            type: '<em>Type</em>',
            user: '사용자',
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
            downloadreport: '보고서 다운로드',
            emailfailure: '이메일 전송 오류',
            emailmessage: '<em>Attached import report of file "{0}" on date {1}</em>',
            emailsubject: '데이터 임포트 보고서',
            emailsuccess: '이메일 전송 성공',
            export: '익스포트',
            exportalldata: '모든 데이터',
            exportfiltereddata: '그리드 필터와 일치하는 데이터만',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: '이 템플릿에 대해 도형 가져오기를 사용할 수 없습니다.',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: '<em>Card</em>',
                project: '프로젝트',
                sourcetype: '~로부터 임포트'
            },
            import: '임포트',
            importresponse: '임포트 응답',
            response: {
                created: '<em>Created cards</em>',
                deleted: '<em>Deleted cards</em>',
                errors: '오류',
                linenumber: '라인 번호',
                message: '메시지',
                modified: '<em>Modified cards</em>',
                notprocessed: '<em>Unprocessed cards</em>',
                processed: '처리된 행',
                recordnumber: '레코드 번호',
                unchanged: '<em>Unchanged cards</em>'
            },
            sendreport: '보고서 보내기',
            template: '양식',
            templatedefinition: '양식 정의'
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
                login: '로그인',
                logout: '사용자 변경'
            },
            fields: {
                group: '그룹',
                language: '언어',
                password: '비밀번호',
                tenants: '사용자',
                username: '사용자명'
            },
            loggedin: '로그인 됨',
            sso: {
                loginfailure: '<em>Login failure</em>',
                loginwith: '<em>Login with {0}</em>',
                or: '<em>or</em>',
                usernotenabled: '<em>User not enabled</em>'
            },
            title: '로그인',
            welcome: '{0}님 다시 오신 걸 환영합니다.'
        },
        main: {
            administrationmodule: '관리자 모듈',
            alerts: '<em>Alerts</em>',
            baseconfiguration: '기본 구성',
            cardlock: {
                lockedmessage: '{0}이 편집 중이므로 이 카드를 편집할 수 없습니다.',
                someone: '특정사용자'
            },
            changegroup: '그룹 변경',
            changetenant: '{0} 변경',
            confirmchangegroup: '그룹을 변경하시겠습니까?',
            confirmchangetenants: '현재 사용자를 변경하시겠습니다?',
            confirmdisabletenant: ' "사용자 무시" 표시를 사용하지 않겠습니까?',
            confirmenabletenant: ' "사용자 무시" 표시를 사용하겠습니까?',
            description: '<em>Description</em>',
            ignoretenants: '{0} 무시',
            info: '정보',
            logo: {
                cmdbuild: 'CMDBuild 로고',
                cmdbuildready2use: 'READY2USE 로고',
                companylogo: '회사 로고',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'openMAINT 로고'
            },
            logout: '로그아웃',
            managementmodule: '데이터관리모듈',
            multigroup: '복수 그룹',
            multitenant: '{0} 다수',
            navigation: '조회',
            pagenotfound: '페이지를 찾을 수 없음',
            password: {
                change: '비밀번호 변경',
                confirm: '비밀 번호 확인',
                email: '이메일주소',
                err_confirm: '비밀번호가 일치하지 않습니다.',
                err_diffprevious: '이전 비밀번호와 동일하면 안됩니다.',
                err_diffusername: '비밀번호는 사용자명과 동일할 수 없습니다.',
                err_length: '비밀번호는 최소 {0}자 이상이어야 합니다.',
                err_reqdigit: '비밀번호는 1개 이상의 숫자, 1개 이상의 소문자, 1개 이상의 대문자를 포함해야합니다.',
                err_reqlowercase: '비밀번호는 1개 이상의 숫자, 1개 이상의 소문자, 1개 이상의 대문자를 포함해야합니다.',
                err_requppercase: '비밀번호는 1개 이상의 숫자, 1개 이상의 소문자, 1개 이상의 대문자를 포함해야합니다.',
                expired: '비밀번호가 만료됐습니다. 지금 변경해야합니다.',
                forgotten: '비밀번호를 분실했습니다.',
                new: '새로운 비밀번호',
                old: '기존 비밀번호',
                recoverysuccess: '비밀번호 재설정을 위한 이메일을 보냈습니다.',
                reset: '비밀번호 재설정',
                saved: '비밀번호가 제대로 저장됐습니다.'
            },
            pleasecorrecterrors: '제시된 오류를 해결하세요',
            preferences: {
                comma: '콤마',
                decimalserror: '소수(Decimals) 필드는 반드시 존재해야 합니다.',
                decimalstousandserror: '소수 분리기호와 천단위 분리기호는 달라야 합니다.',
                default: '기본',
                defaultvalue: '기본 값',
                favouritemenulocation: '<em>Favourites menu location</em>',
                firstdayofweek: '<em>First day of week</em>',
                gridpreferencesclear: '그리드 기본설정 초기화',
                gridpreferencescleared: '그리드 기본설정 초기화됨',
                gridpreferencessave: '그리드 기본설정 저장',
                gridpreferencessaved: '그리드 기본설정 저장됨',
                gridpreferencesupdate: '그리드 설정 저장',
                groupemailsbystatus: '<em>Show emails grouped by status</em>',
                labelcsvseparator: 'CSV 분리기호',
                labeldateformat: '날짜 형식',
                labeldecimalsseparator: '소수 분리기호',
                labellanguage: '언어',
                labelthousandsseparator: '천단위 분리기호',
                labeltimeformat: '시간 형식',
                msoffice: 'MS 오피스',
                no: '<em>No</em>',
                notificationslabel: '<em>Chat & Notifications</em>',
                notificationssound: '<em>Play sound for notifications</em>',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: '마침표',
                preferredfilecharset: '<em>CSV encoding</em>',
                preferredofficesuite: '선호하는 Officesuite',
                sendcancellationperiod: '<em>Email send cancellation period (seconds)</em>',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: '<em>Show after navigation menu</em>',
                showfovouritesbefore: '<em>Show before navigation menu</em>',
                space: '공백',
                thousandserror: '천단위 필드는 반드시 존재해야 합니다.',
                timezone: '시간대',
                twelvehourformat: '12시간 형식',
                twentyfourhourformat: '24시간 형식',
                yes: '<em>Yes</em>'
            },
            searchinallitems: '모든 아이템에서 검색',
            searchtenant: '<em>Search tenant</em>',
            selectdeselettenants: '<em>Select/Deselect all</em>',
            treenavcontenttitle: '<em>{0} of {1}</em>',
            userpreferences: '사용자 설정'
        },
        menu: {
            allitems: '모든 아이템',
            classes: '클래스',
            custompages: '커스텀 페이지',
            dashboards: '대시보드',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: '<em>Favourites</em>',
            favouritesadd: '<em>Add to favourites</em>',
            favouritesremove: '<em>Remove from favourites</em>',
            homepage: '<em>Homepage</em>',
            processes: '프로세스',
            reports: '보고서',
            setasinitialpage: '<em>Set as initial page</em>',
            views: '보기'
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
            edit: '노트 수정'
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
            attention: '주의',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: '에러',
            genericerror: '일반 오류',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: '일반 정보',
            genericwarning: '일반 경고',
            info: '정보',
            more: '<em>More info</em>',
            success: '성공',
            warning: '경고'
        },
        patches: {
            apply: '패치 적용',
            category: '카테고리',
            description: '설명',
            name: '이름',
            patches: '패치'
        },
        processes: {
            abortconfirmation: '이 프로세스를 중단하시겠습니까?',
            abortprocess: '프로세스 중단',
            action: {
                advance: '진행',
                label: '실행'
            },
            activeprocesses: '프로세스 활성화',
            allstatuses: '모두',
            editactivity: '액티비티 수정',
            openactivity: '액티비티 열기',
            resumeprocesses: '<em>Processes that are not suspended cannot be resumed</em>',
            startworkflow: '시작',
            workflow: '워크플로우'
        },
        relationGraph: {
            activity: '활동',
            allLabelsOnGraph: '그래프의 모든 레이블',
            card: '카드',
            cardList: '카드 목록',
            cardRelations: '카드 관계',
            choosenaviagationtree: '조회 트리 선택',
            class: '클래스',
            classList: '클래스 목록',
            compoundnode: '복합노드',
            disable: '비활성화',
            edges: '<em>Edges</em>',
            enable: '활성화',
            labelsOnGraph: '그래프에 툴팁 표시',
            level: '레벨',
            nodes: '<em>Nodes</em>',
            openRelationGraph: '관계그래프 열기',
            quantity: '<em>Quantity</em>',
            refresh: '새로고침',
            relation: '관계',
            relationGraph: '관계그래프',
            reopengraph: '이 노드로부터 그래프 다시 열기'
        },
        relations: {
            adddetail: '상세 추가',
            addrelations: '관계 추가',
            attributes: '속성',
            begindate: '<em>Begin date</em>',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: '코드',
            deletedetail: '상세 삭제',
            deleterelation: '관계 삭제',
            deleterelationconfirm: '이 관계를 지울건가요?',
            description: '설명',
            editcard: '카드 편집',
            editdetail: '상세 편집',
            editrelation: '관계 편집',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: '확장데이터',
            mditems: '아이템',
            missingattributes: '필수 속성이 비어있습니다.',
            opencard: '관련 카드 열기',
            opendetail: '상세 보기',
            relationdata: '<em>Relation data</em>',
            type: '타입'
        },
        reports: {
            csv: 'CSV',
            download: '다운로드',
            format: '포맷',
            msgs: {
                creationerror: '<em>Something went wrong during the start of the report creation.</em>',
                creationstarted:
                    '<em>Report creation started. You will be notified when it will be ready for the download.</em>'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: '인쇄',
            reload: '다시 읽기',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: '기본'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: '매일'
                        },
                        monthly: {
                            description: '매월'
                        },
                        once: {
                            description: '한번'
                        },
                        weekly: {
                            description: '매주'
                        },
                        yearly: {
                            description: '매년'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: '기본'
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
            addThematism: '주제 추가',
            analysisType: '분석 유형',
            attribute: '속성',
            bordersIntervals: '<em>Wrong configuration for intervals limits</em>',
            calculateRules: '스타일 규칙 생성',
            clearThematism: '주제 지우기',
            color: '색상',
            defineLegend: '범례 정의',
            defineThematism: '주제 정의',
            deletethematism: '<em>Are you sure that you want to delete this thematism?</em>',
            editThematism: '<em>Edit rules configuration</em>',
            function: '기능',
            generate: '생성',
            geoAttribute: '지형적 속성',
            graduated: '마치다',
            highlightSelected: '선택항목 강조 표시',
            intervals: '간격',
            legend: '범례',
            name: '이름',
            newThematism: '신규 주제',
            punctual: '시간엄수',
            quantity: '셈',
            segments: '부분',
            source: '소스',
            table: '테이블',
            thematism: '테마티즘',
            value: '값'
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
                addrow: '행 추가',
                clonerow: '행 복사',
                datanotvalid: '<em>Data not valid</em>',
                deleterow: '행 삭제',
                editrow: '행 편집',
                export: '내보내기',
                import: '불러오기',
                importexport: {
                    expattributes: '내보낼 데이터',
                    file: '파일',
                    filename: '파일명',
                    format: '포맷',
                    importmode: '임포트 모드',
                    keyattributes: '키 속성',
                    missingkeyattr: '최소 하나의 키 속성을 선택하세요. ',
                    modeadd: '추가',
                    modemerge: '합치기',
                    modereplace: '대채',
                    separator: '분리기호'
                },
                refresh: '기본으로 새로고침'
            },
            linkcards: {
                checkedonly: '<em>Checked only</em>',
                editcard: '카드 폍집',
                opencard: '카드 열기',
                refreshselection: '기본 선택 적용',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: '그리드 필터 유효화',
                togglefilterenabled: '그리드 필터 무효화'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                '<em>Inline mode is not supported for this widget type. Please fix the widget configuration.</em>',
            required: '이 위젯은 필요합니다.'
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
