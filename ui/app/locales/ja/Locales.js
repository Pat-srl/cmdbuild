(function () {
    Ext.define('CMDBuildUI.locales.ja.Locales', {
        requires: ['CMDBuildUI.locales.ja.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'ja',
        administration: CMDBuildUI.locales.ja.LocalesAdministration.administration,
        ai: {
            clearconversation: '会話を削除',
            clearconversationmessage: '本当にこの会話を削除しますか？',
            copiedtoclipboard: '<em>Message copied to clipboard</em>',
            erroranswer: 'すみません、回答に不備がありました。',
            headertooltip: 'CMDBuildチャットボット',
            introtextbody: 'チャットを開始するには、適切な入力欄にリクエスト内容をご記入ください',
            introtexttitle: 'お困りですか？',
            loading: 'ローディング',
            newchat: '新規チャット',
            newchattooltip: '新規チャットを開始',
            questionemptytext: '質問を入力してください',
            resources: '元カード',
            viewrelatedcard: '関連カードを表示',
            viewresource: 'リソースを表示'
        },
        arialabels: {
            buttonplaceholder: 'ボタン',
            iframeplaceholder: 'テキストエディター',
            languageselector: '言語選択',
            resizepanel: {
                horizontally: '水平リサイズ',
                vertically: '垂直リサイズ'
            },
            usermenu: 'ユーザーメニュー'
        },
        attachments: {
            add: '添付追加',
            attachmenthistory: '添付履歴',
            author: '作成者',
            authordescription: '作成者名',
            browse: 'ブラウズ・・・',
            category: 'カテゴリー',
            code: 'コード',
            creationdate: '作成日',
            deleteattachment: '添付削除',
            deleteattachment_confirmation: '本当にこの添付を削除しますか?',
            description: '説明',
            download: 'ダウンロード',
            dropfiles: 'ファイルをここにドロップ',
            editattachment: '添付変更',
            errordms: 'DMSが無効です。添付ファイルは保存できません。',
            file: 'ファイル',
            filealreadyinlist: '{0}は既にリストにあります',
            filename: 'ファイル名',
            fileview: '添付表示',
            invalidfiles: '無効なファイル削除',
            majorversion: 'メジャーバージョン',
            maxsize: '許容最大ファイル容量は {0} MBです',
            metadata: 'メタデータ',
            modificationdate: '更新日',
            new: '新規添付',
            nocategory: '未分類',
            preview: 'プレビュー',
            removefile: 'ファイル削除',
            statuses: {
                empty: 'ファイルが空です',
                error: 'エラー',
                extensionNotAllowed: 'ファイル拡張子は許可されません',
                loaded: 'ロード完了',
                ready: '準備完了',
                toolarge: '許容最大ファイル容量を超過'
            },
            successupload: '{0}をアップロードしました',
            uploadfile: 'アップロード中...',
            version: 'バージョン',
            viewhistory: '添付履歴表示',
            viewmetadata: '添付情報表示',
            warningmessages: {
                atleast:
                    '{0}: "{2}"タイプの{1}の添付ファイルがロードされました。このカテゴリは少なくとも {3} の添付ファイルを期待しています',
                exactlynumber:
                    '{0}: "{2}"タイプの{1}の添付ファイルがロードされました。このカテゴリは {3} の添付ファイルを期待しています',
                maxnumber:
                    '{0}: "{2}" タイプの {1} 添付ファイルがロードされました。このカテゴリは、最大でも {3} の添付ファイルを期待しています。'
            },
            wrongfileextension: '{0}ファイル拡張子は許可されません'
        },
        bim: {
            bimViewer: 'BIMビューワー',
            card: {
                label: 'カード'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'X軸の境界ボックスのサイズ',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Y軸の境界ボックスのサイズ',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Z軸の境界ボックスのサイズ',
                calculated: '計算済',
                description: '説明',
                guid: 'GUID',
                label: 'プロパティ',
                name: '名称',
                type: 'タイプ',
                uuid: 'UUID'
            },
            layers: {
                label: 'レイヤー',
                menu: {
                    hideAll: '全て隠す',
                    hideAllLayers: '全レイヤー非表示',
                    hideAllXeokit: '全て隠す {0}',
                    showAll: '全て表示',
                    showAllLayers: '全レイヤー表示',
                    showAllXeokit: '全て表示 {0}'
                },
                name: '名称',
                quantity: '数量',
                visibility: '表示'
            },
            menu: {
                camera: 'カメラ',
                disableslice: 'スライスオブジェクト無効化',
                enableslice: 'スライスオブジェクト有効化',
                frontView: 'フロントビュー',
                mod: 'ビューワーコントロール',
                ortho: '直交投影ビュー',
                orthographic: '直交投影視点',
                pan: 'スクロール',
                panXeokit: 'ビューワーコントロール：スクロール (Shift)',
                perspe: 'パースビュー',
                perspective: 'パース視点',
                resetSlice: 'スライスリセット',
                resetView: 'ビューリセット',
                rotate: '回転',
                rotateXeokit: 'ビューワーコントロール：回転',
                save: 'ビュー保存',
                shortcuts: {
                    down: '下回転',
                    left: '左回転',
                    right: '右回転',
                    title: 'キーボードショートカット',
                    up: '上回転',
                    zoomin: '拡大',
                    zoomout: '縮小'
                },
                sideView: 'サイドビュー',
                threeD: '3Dビュー表示',
                topView: '上面',
                twoD: '2Dビュー表示'
            },
            navCube: {
                back: '背面',
                bottom: '底面',
                front: '前面',
                left: '左面',
                right: '右面',
                top: '上面'
            },
            showBimCard: 'BIM 3D表示',
            tree: {
                arrowTooltip: '選択',
                label: 'ツリー',
                multipleElements: 'エレメントを選択',
                open_card: '関連するカードを開く',
                root: 'IFCルート'
            }
        },
        bulkactions: {
            abort: '選択したアイテムを中止',
            addfields: 'フィールド追加',
            alertdownloadattachments:
                '一部の添付ファイルは、まだサーバーに保存されていないため、ダウンロードできません。',
            alertnoattachmentsdownload: '添付ファイルがまだサーバーに保存されていないため、ダウンロードされません',
            cancelselection: '選択キャンセル',
            confirmabort: '本当に{0}プロセスインスタンスを中止してよろしいですか？',
            confirmdelete: '本当に{0}カードを削除してよろしいですか？',
            confirmdeleteattachements: '本当に{0}添付ファイルを削除してよろしいですか？',
            confirmedit: '本当に{0}を{1}のカードに変更してよろしいですか？',
            delete: '選択したアイテムを削除',
            download: '選択した添付ファイルをダウンロード',
            downloadall: '全ての添付ファイルをダウンロード',
            edit: '選択したアイテムを編集',
            missingfields: '正しいフォームバリデーションのために、次のフィールドを追加する必要があります: {0}',
            noattributeselected: '少なくとも1つの属性を選択してください',
            selectall: '全アイテムを選択'
        },
        calendar: {
            active_expired: '有効/失効',
            add: 'カレンダー追加',
            advancenotification: '事前通知日数',
            allcategories: '全カテゴリー',
            alldates: '全日付',
            associatedcard: '関連するカード',
            calculated: '計算済',
            calendar: 'カレンダー表示',
            cancel: 'キャンセル済',
            category: 'カテゴリー',
            class: 'クラス',
            cm_confirmcancel: '本当にこのスケジュールをキャンセル済にしますか?',
            cm_confirmcomplete: '本当にこのスケジュールを完了済にしますか?',
            cm_markcancelled: 'スケジュールをキャンセル済にします',
            cm_markcomplete: 'スケジュールを完了済にします',
            complete: '完了',
            completed: '完了済',
            date: '日',
            days: '日',
            delaybeforedeadline: '期限前計算',
            delaybeforedeadlinevalue: '期限前計算値',
            description: '説明',
            editevent: 'スケジュール編集',
            enddate: '終了日',
            endtype: '終了タイプ',
            event: 'スケジュール',
            execapplyfilter: '複数のカレンダーがあるため詳細条件が適用できません',
            execremovefilter: '複数のカレンダーがあるため詳細条件が削除できません',
            executiondate: '実行日',
            frequency: '頻度',
            frequencymultiplier: '回数',
            grid: '一覧表示',
            leftdays: '予定日',
            londdescription: '詳細説明',
            manual: 'マニュアル',
            maxactiveevents: '最大有効イベント',
            messagebodydelete: '本当にこのスケジュールを削除しますか？',
            messagebodyplural: '{0} スケジュールがあります',
            messagebodyrecalculate: 'スケジュール再計算しますか？',
            messagebodysingular: '{0} スケジュールがあります',
            messagetitle: 'スケジュール再計算しますか？',
            missingdays: '残日数',
            next30days: '30日間',
            next7days: '7日間',
            notificationtemplate: '通知メールテンプレート',
            notificationtext: '通知内容',
            occurencies: '実行回数',
            operation: 'ステータス',
            partecipantgroup: '表示グループ',
            partecipantuser: '表示ユーザー',
            priority: '優先度',
            recalculate: '再計算',
            referent: '対象',
            scheduler: 'スケジューラー',
            sequencepaneltitle: 'スケジュール作成',
            startdate: '開始日',
            status: 'ステータス',
            today: '本日',
            type: 'タイプ',
            viewevent: 'スケジュール表示',
            waiteventgeneration: 'イベント作成中のためお待ちください',
            widgetcriterion: 'ルール',
            widgetemails: 'メール',
            widgetgotocard: '元カード',
            widgetsourcecard: '対象カード'
        },
        chat: {
            title: 'チャット'
        },
        classes: {
            cards: {
                addcard: 'カード追加',
                clone: '複製',
                clonewithrelations: 'カードとリレーションを複製',
                deletebeaware: '注意：',
                deleteblocked: '{0}とのリレーションがあるので削除できません',
                deletecard: 'カード削除',
                deleteconfirmation: '本当にこのカードを削除しますか?',
                deleterelatedcards: '関連する{0}カードも削除されます',
                deleterelations: '{0}カードとリレーションが削除されます',
                label: 'カード',
                modifycard: 'カード更新',
                opencard: 'カード表示',
                print: 'カード印刷'
            },
            simple: 'シンプルクラス',
            standard: '標準クラス'
        },
        common: {
            actions: {
                add: '追加',
                apply: '適用',
                cancel: 'キャンセル',
                clear: 'クリア',
                close: '閉じる',
                copy: 'コピー',
                copyerrormessage: 'エラーメッセージをクリップボードにコピー',
                delete: '削除',
                edit: '編集',
                execute: '実行',
                forward: '転送',
                help: 'ヘルプ',
                hide: '隠す',
                load: 'ロード',
                open: '開く',
                previous: '前回',
                refresh: 'リフレッシュ',
                remove: '削除',
                save: '保存',
                saveandapply: '保存',
                saveandclose: '保存して閉じる',
                search: '検索',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: '検索中...'
            },
            and: 'and',
            attributes: {
                boolfalse: '0',
                booltrue: '1',
                link: {
                    errlabel: 'ラベルが必要です',
                    errurl: 'ラベルがある場合URLも必須です',
                    label: 'ラベル',
                    url: 'URL'
                },
                nogroup: '基本データ'
            },
            dates: {
                date: 'Y/m/d',
                datetime: 'Y/m/d H:i:s',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'HTMLクリア',
                expand: 'エディターを広げる',
                reduce: 'エディターを狭める',
                signature: '署名追加',
                unlink: 'リンク解除',
                unlinkmessage: 'ハイパーリンクをテキストに変換'
            },
            grid: {
                disablemultiselection: '複数選択無効',
                enamblemultiselection: '複数選択有効',
                export: 'エキスポート',
                filterremoved: 'フィルターは削除されました',
                import: 'インポート',
                itemnotfound: 'アイテムが見つかりません',
                lastUpdate: '<em>Last update</em>',
                list: 'リスト',
                opencontextualmenu: 'コンテキストメニュー',
                print: '印刷',
                printcsv: 'CSV出力',
                printmap: '地図印刷',
                printodt: 'ODT出力',
                printpdf: 'PDF出力',
                row: 'アイテム',
                rows: 'アイテム',
                subtype: 'サブタイプ',
                thematism: 'テーマ'
            },
            tabs: {
                activity: 'アクティビティ',
                attachment: '添付',
                attachments: '添付',
                card: 'カード',
                clonerelationmode: 'リレーションクローンモード',
                details: '詳細',
                emails: 'Eメール',
                history: '履歴',
                notes: 'ノート',
                relations: 'リレーション',
                schedules: 'スケジュール',
                sources: '元カード'
            }
        },
        dashboards: {
            filters: '<em>Filters</em>',
            list: {
                showall: '全て表示',
                showingnitems: '{0} アイテムを表示'
            },
            pareto: {
                cumulative: '累積',
                cumulativeperc: '累積 %'
            },
            tools: {
                download: 'ダウンロードグラフ',
                downloaddata: 'グラフデータダウンロード',
                gridhide: 'データ一覧を閉じる',
                gridshow: 'データ一覧表示',
                openinpopup: 'ポップアップで開く',
                reload: '再ロード',
                showall: '全て表示'
            }
        },
        dependencytree: {
            cardinfo: 'カード',
            filteredid: '現在のカードはこのツリーに属しません',
            gerarchicview: '階層表示',
            name: '依存ツリー',
            nocardselected: 'カードが選択されていません',
            opendependencytree: '依存ツリー表示',
            printjpeg: 'JPEG出力',
            printpdf: 'PDF出力',
            printpng: 'PNG出力',
            printsvg: 'SVG出力',
            reopengraph: '新規ルートからグラフを再表示',
            selectcard: '開始ノードを選択',
            wrongid: '入力されたIDが無効です'
        },
        emails: {
            abort: 'キャンセル',
            addattachmentsfromdocumentarchive: 'ドキュメントから添付追加',
            alredyexistfile: 'この名前のファイルは既に存在します',
            archivingdate: 'アーカイブ日',
            attachfile: 'ファイルを添付',
            attachmentsmessage:
                'テンプレートではカードの添付が必要です。プロセスを保存するまで、使用することはできません。',
            bcc: 'Bcc',
            cc: 'cc',
            composeemail: 'メール作成',
            composefromtemplate: 'テンプレートから作成',
            delay: '後で送信',
            delays: {
                day1: '1日後',
                days2: '2日後',
                days4: '4日後',
                hour1: '1時間後',
                hours2: '２時間後',
                hours4: '4時間後',
                month1: '1カ月後',
                negativeday1: '1日前',
                negativedays2: '2日前',
                negativedays4: '4日前',
                negativehour1: '1時間前',
                negativehours2: '2時間前',
                negativehours4: '4時間前',
                negativemonth1: '1ヶ月前',
                negativeweek1: '1週間前',
                negativeweeks2: '2週間前',
                none: 'なし',
                week1: '1週間後',
                weeks2: '2週間後'
            },
            dmspaneltitle: 'データベースから添付選択',
            edit: '編集',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: '差出人',
            gridrefresh: 'リフレッシュ',
            groupbystatus: 'ステータス別',
            keepsynchronization: '同期を維持',
            maxsize: '添付ファイル合計最大容量 {0}',
            message: '本文',
            regenerateallemails: '全メールを再作成',
            regenerateemail: 'メール再作成',
            reloadconfirmationmessage: '下書き状態のメールは削除されます。このまま続けますか？',
            remove: '削除',
            remove_confirmation: '本当にこのメールを削除しますか?',
            reply: '返信',
            replyprefix: '{0}, {1}が書きました:',
            searchattachment: '添付ファイル検索',
            searchcard: 'カード検索',
            selectaclass: 'クラス選択',
            sendemail: 'メール送信',
            sendingemail: 'メール送信',
            signature: '署名',
            size: 'サイズ',
            status: 'ステータス別',
            statuses: {
                draft: '下書き',
                error: 'エラー',
                new: '新規',
                outgoing: '送信中',
                received: '受信',
                sent: '送信',
                skipped: 'スキップ'
            },
            subject: '件名',
            templateattachments:
                'このメールには、テンプレートによって自動的にアップロードされた添付ファイルが含まれています',
            to: '宛先',
            view: 'ビュー'
        },
        errors: {
            actionnotallowed: '許可されていないアクション',
            autherror: 'ユーザー名またはパスワードに誤りがあります',
            classnotfound: 'クラス{0} は見つかりません',
            fieldrequired: '必須入力',
            invalidfilter: '無効なフィルター',
            nodatatoexport: 'グラフデータはありません',
            notfound: 'アイテムが見つかりません',
            value3chars: '値は3文字以上必要です'
        },
        filters: {
            actions: 'アクション',
            addfilter: 'フィルター追加',
            any: '一つ以上',
            attachments: '添付',
            attachmentssearchtext: '添付全文検索',
            attribute: '属性選択',
            attributes: '属性',
            clearfilter: 'フィルタークリア',
            clone: '複製',
            copyof: 'コピー',
            currentgroup: '現在グループ',
            currentuser: '現在ユーザー',
            defaultset: 'デフォルトに設定',
            defaultunset: 'デフォルト設定から削除',
            description: '説明',
            domain: 'ドメイン',
            errorname: 'ファイル名は既にあります',
            filterdata: 'フィルター',
            fromfilter: 'フィルターから',
            fromselection: '選択から',
            group: 'グループ',
            ignore: '除外',
            migrate: '移行',
            name: '名称',
            newfilter: '新規フィルター',
            noone: '何もない',
            operator: '演算子',
            operators: {
                and: 'AND',
                beginswith: '開始する',
                between: '間',
                contained: '含む',
                containedorequal: '含むまたは同じ',
                contains: '含む',
                containsorequal: '含むまたは同じ',
                descriptionbegin: '説明が開始する',
                descriptioncontains: '説明に含まれる',
                descriptionends: '説明が終了する',
                descriptionnotbegin: '説明が開始しない',
                descriptionnotcontain: '説明に含まない',
                descriptionnotends: '説明が終了しない',
                different: '異なる',
                doesnotbeginwith: '開始しない',
                doesnotcontain: '含まない',
                doesnotendwith: '終了しない',
                endswith: '終了する',
                equals: '同じ',
                greaterthan: '大きい',
                isnotnull: 'null値でない',
                isnull: 'null値',
                lessthan: '小さい',
                or: 'OR'
            },
            relations: 'リレーション',
            removeattribute: '属性削除',
            removeblock: 'ブロック削除',
            type: 'タイプ',
            typeinput: '入力パラメータ',
            typeorchoosetheattributename: '属性名選択または入力',
            user: 'ユーザー',
            value: '値'
        },
        gis: {
            card: 'カード',
            cardsMenu: 'メニュー',
            code: 'コード',
            contextmenu: {
                coordinates: '位置座標を取得',
                coordinatescopiedmessage: '位置座標がクリップボードにコピーされました'
            },
            description: '説明',
            extension: {
                errorCall: 'エラー',
                noResults: '結果がありません'
            },
            externalServices: '外部サービス',
            geographicalAttributes: '地理属性',
            geoserverLayers: 'Geoサーバレイヤー',
            labels: {
                all: '全て',
                alltitle: '全ラベル表示',
                alltitleclass: 'クラスの全ラベルを表示',
                clear: '検索クリア',
                hidden: '隠す',
                hiddentitle: 'ラベル非表示',
                hideinfowindow: '情報ウィンドウ非表示',
                label: 'ラベル',
                search: '地図検索',
                selected: '選択済',
                selectedtitle: '選択アイテムのラベル表示',
                showinfowindow: '情報ウィンドウ表示'
            },
            layers: 'レイヤー',
            layersTab: {
                clearPreferences: 'レイヤー設定クリア',
                preferencesCleared: 'レイヤー設定クリア済',
                preferencesSaved: 'レイヤー設定保存済',
                savePreferences: 'レイヤー設定保存',
                updatePreferences: 'レイヤー設定更新'
            },
            list: 'リスト',
            longpresstitle: 'エリア内ジオエレメント',
            map: 'マップ',
            mapServices: '地図サービス',
            menu: {
                font: 'フォントサイズ',
                options: 'オプション'
            },
            position: '位置',
            root: 'ルート',
            sourceMap: {
                default: 'デフォルトビュー',
                satellite: 'サテライトビュー'
            },
            tree: 'ナビゲーションツリー',
            type: 'タイプ',
            view: 'ビュー',
            zoom: 'ズーム'
        },
        history: {
            activityname: 'アクティビティ名',
            activityperformer: 'アクティビティの実施者',
            attributes: '属性 ({0})',
            begindate: '開始日',
            enddate: '終了日',
            fieldname: 'フィールド名',
            fulldata: '全データ',
            onlychanges: '変更のみ',
            otherrelations: '他リレーション',
            processstatus: 'ステータス',
            referencerelation: '参照リレーション',
            relation: 'リレーション',
            system: 'システム',
            type: 'タイプ',
            user: 'ユーザー',
            userdescription: 'ユーザー名',
            value: '値',
            viewmode: '表示モード'
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
            accessibility: 'アクセシビリティ',
            animals: '動物',
            arrows: '矢印',
            audiovideo: 'ビデオ追加',
            automotive: '自動車',
            autumn: '秋',
            beverage: '飲料',
            buildings: '建物',
            business: 'ビジネス',
            charity: 'チャリティ',
            chat: 'チャット',
            chess: 'チェス',
            childhood: '子供時代',
            code: 'コード',
            communication: 'コミュニケーション',
            computers: 'コンピューター',
            construction: '建設',
            currency: '通貨',
            datetime: '日時',
            design: 'デザイン',
            editors: 'エディター',
            education: '教育',
            emoji: '絵文字',
            energy: 'エネルギー',
            files: 'ファイル',
            finance: 'ファイナンス',
            food: '食品',
            fruitvegetable: '果物',
            games: 'ゲーム',
            gamingtabletop: '卓上ゲーム',
            gender: '性別',
            general: '一般',
            halloween: 'ハロウィン',
            hands: '手',
            hotel: 'ホテル',
            household: '家計',
            images: 'イメージ',
            interfaces: 'インターフェース',
            logistics: 'ロジスティック',
            maps: '地図',
            maritime: '海運',
            marketing: 'マーケティング',
            mathematics: '数学',
            medical: '衛生',
            moving: '移転',
            music: '音楽',
            objects: 'ツリー',
            paymentsshopping: '支払い',
            political: '政治',
            religion: '宗教',
            sciencefiction: 'サイエンスフィクション',
            security: 'セキュリティ',
            shapes: '形',
            shopping: 'ショッピング',
            social: 'ソーシャル',
            spinners: '紡績',
            sports: 'スポーツ',
            spring: '春',
            status: 'ステータス',
            toggle: 'ボタン',
            travel: '旅行',
            userspeople: 'ユーザーの人たち',
            vehicles: '乗り物',
            weather: '天気',
            winter: '冬'
        },
        importexport: {
            database: {
                uri: 'データベースURI',
                user: 'データベースユーザー'
            },
            downloadreport: 'ダウンロードレポート',
            emailfailure: '送信エラー',
            emailmessage: '{1}のインポートレポート"{0}" ファイル添付',
            emailsubject: 'インポートレポート',
            emailsuccess: '送信されました',
            export: 'エキスポート',
            exportalldata: '全データ',
            exportfiltereddata: 'フィルターに一致のみ',
            gis: {
                importconfiguration: 'コンフィグレーションをインポート',
                shapeimportdisabled: 'このテンプレートは図形インポートできません',
                shapeimportenabled: 'このテンプレートでは図形のインポートが有効です'
            },
            ifc: {
                card: 'カード',
                project: 'プロジェクト',
                sourcetype: 'インポート元'
            },
            import: 'インポート',
            importresponse: 'インポート応答',
            response: {
                created: '作成カード',
                deleted: '削除カード',
                errors: 'エラー',
                linenumber: '行番号',
                message: 'メッセージ',
                modified: '更新カード',
                notprocessed: '未処理カード',
                processed: '処理行数',
                recordnumber: 'レコード数',
                unchanged: '変更なしカード'
            },
            sendreport: 'レポート送信',
            template: 'テンプレート',
            templatedefinition: 'テンプレート定義'
        },
        joinviews: {
            active: '有効',
            addview: 'ビュー追加',
            alias: 'エイリアス',
            attribute: '属性',
            attributecustomization: '属性カスタマイズ',
            attributes: '属性',
            attributeschoice: '属性選択',
            attributesof: '{0}の属性',
            createview: 'ビュー作成',
            datasorting: '並び替え',
            delete: '削除',
            deleteview: 'ビュー削除',
            deleteviewconfirm: '本当のこのビューを削除しますか？',
            description: '説明',
            disable: '無効化',
            domainalias: 'ドメインエイリアス',
            domainsof: '{0}のドメイン',
            edit: '編集',
            editview: 'ビュー定義編集',
            enable: '有効化',
            fieldsets: 'フィールドセット',
            filters: 'フィルター',
            generalproperties: '一般属性',
            group: 'グループ',
            innerjoin: 'インナージョイン',
            jointype: 'ジョインタイプ',
            joinview: 'ジョインからのビュー',
            klass: 'クラス',
            manageview: 'ビュー管理',
            masterclass: 'マスタークラス',
            masterclassalias: 'マスタークラスエイリアス',
            name: '名称',
            newjoinview: 'ジョインからの新規ビュー',
            outerjoin: 'アウタージョイン',
            pleaseseleceavalidmasterclass: '有効なマスタークラスを選択してください',
            refreshafteredit: '変更をページに反映しますか？',
            selectatleastoneattribute: '一つ以上の属性を選択してください',
            selectdisplayattributegrid:
                'グリッドに表示する属性と縮小グリッドに表示する属性を最低1つずつ選択してください',
            showingrid: 'グリッド表示',
            showinreducedgrid: '縮小表示',
            targetalias: 'ターゲットクラスエイリアス'
        },
        login: {
            buttons: {
                login: 'ログイン',
                logout: 'ユーザー変更'
            },
            fields: {
                group: 'グループ',
                language: '言語',
                password: 'パスワード',
                tenants: 'テナント',
                username: 'ユーザー名'
            },
            loggedin: 'ログイン',
            sso: {
                loginfailure: 'ログイン失敗',
                loginwith: '{0}でログイン',
                or: 'または',
                usernotenabled: 'ユーザーは無効です'
            },
            title: 'ログイン',
            welcome: 'おかえりなさい {0}'
        },
        main: {
            administrationmodule: '管理モジュール',
            alerts: 'アラート',
            baseconfiguration: '基本設定',
            cardlock: {
                lockedmessage: 'このカードは{0}が編集しているため、編集できません。',
                someone: '誰か'
            },
            changegroup: 'グループ変更',
            changetenant: '{0}に変更',
            confirmchangegroup: '本当にこのグループを変更しますか?',
            confirmchangetenants: '本当に有効なテナントを変更しますか?',
            confirmdisabletenant: '本当に"テナント無視"フラグを無効にしますか?',
            confirmenabletenant: '本当に"テナント無視"フラグを有効にしますか??</em>',
            description: '説明',
            ignoretenants: '{0}を除外',
            info: '情報',
            logo: {
                cmdbuild: 'CMDBuild ロゴ',
                cmdbuildready2use: 'CMDBuild READY2USE ロゴ',
                companylogo: 'ユーザーロゴ',
                hdaitsm: 'HDA ITSM',
                openmaint: 'openMAINT ロゴ'
            },
            logout: 'ログアウト',
            managementmodule: 'データモジュール',
            multigroup: 'マルチグループ',
            multitenant: 'マルチ{0}',
            navigation: 'ナビゲーション',
            pagenotfound: 'ページがありません',
            password: {
                change: 'パスワード変更',
                confirm: 'パスワード確認',
                email: 'メールアドレス',
                err_confirm: 'パスワードに誤りがあります',
                err_diffprevious: '以前のパスワードと同じものは使えません',
                err_diffusername: 'ユーザー名と同じパスワードは使えません',
                err_length: '{0}字以上のパスワードが必要です',
                err_reqdigit: 'パスワードには少なくとも1桁の数字が含まれている必要があります',
                err_reqlowercase: 'パスワードには少なくとも1字の小文字が含まれている必要があります',
                err_requppercase: 'パスワードには少なくとも1字の大文字が含まれている必要があります',
                expired: 'パスワードの有効期限が切れました、変更してください',
                forgotten: 'パスワードを忘れました',
                new: '新パスワード',
                old: '旧パスワード',
                recoverysuccess: 'パスワードリセット案内メールを送信しました',
                reset: 'パスワードリセット案内メールを送信しました',
                saved: 'パスワードが保存されました'
            },
            pleasecorrecterrors: 'エラーを修正してください',
            preferences: {
                comma: 'カンマ',
                decimalserror: '小数点の項目が必要です',
                decimalstousandserror: '小数点と桁区切りは異なる必要があります',
                default: 'デフォルト',
                defaultvalue: 'デフォルト',
                favouritemenulocation: 'お気に入りメニュー場所',
                firstdayofweek: '週の開始日',
                gridpreferencesclear: 'グリッド設定クリア',
                gridpreferencescleared: 'グリッド設定クリアされました',
                gridpreferencessave: 'グリッド設定保存',
                gridpreferencessaved: 'グリッド設定保存されました',
                gridpreferencesupdate: 'グリッド設定更新',
                groupemailsbystatus: 'ステータス別メール表示',
                labelcsvseparator: 'CSVセパレータ',
                labeldateformat: '日付表示',
                labeldecimalsseparator: '小数点記号',
                labellanguage: '言語',
                labelthousandsseparator: '桁区切り記号',
                labeltimeformat: '時刻表示',
                msoffice: 'Microsoft Office',
                no: 'No',
                notificationslabel: 'チャットと通知',
                notificationssound: '通知音あり',
                notificationssoundvolume: '通知音量',
                period: 'ピリオド',
                preferredfilecharset: 'CSVエンコーディング',
                preferredofficesuite: '使用Officeプログラム',
                sendcancellationperiod: 'メール送信キャンセル秒',
                showcollapsed: '折りたたんだ状態で表示',
                showfovouritesafter: 'ナビゲーションメニューの後に表示',
                showfovouritesbefore: 'ナビゲーションメニューの前に表示',
                space: 'スペース',
                thousandserror: '整数の項目が必要です',
                timezone: 'タイムゾーン',
                twelvehourformat: '12時間表示',
                twentyfourhourformat: '24時間表示',
                yes: 'Yes'
            },
            searchinallitems: '全アイテム検索',
            searchtenant: 'テナント検索',
            selectdeselettenants: '選択/選択解除',
            treenavcontenttitle: '{1}の{0}',
            userpreferences: '表示設定'
        },
        menu: {
            allitems: '全アイテム',
            classes: 'クラス',
            custompages: 'カスタムページ',
            dashboards: 'ダッシュボード',
            dependencytrees: '依存ツリー',
            favourites: 'お気に入り',
            favouritesadd: 'お気に入り追加',
            favouritesremove: 'お気に入りから削除',
            homepage: 'ホームページ',
            processes: 'プロセス',
            reports: 'レポート',
            setasinitialpage: '初期ページに設定',
            views: 'ビュー'
        },
        mobile: {
            config: {
                configure: 'モバイルデバイス設定',
                customercode: 'カスタマーコード',
                devicename: 'デバイス名',
                regenerate: '再作成',
                serverurl: 'サーバーURL'
            }
        },
        notes: {
            edit: 'ノート編集'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: '通知削除',
            emptymessage: '表示する通知はありません',
            label: '通知',
            viewall: '全て表示'
        },
        notifier: {
            attention: '注意',
            copied: 'コピーされました',
            copiedtotheclipboard: 'クリップボードにコピー',
            error: 'エラー',
            genericerror: 'エラー',
            genericerrormessage: '詳細はシステムログを確認してください',
            genericinfo: '一般的情報',
            genericwarning: '一般的警告',
            info: '情報',
            more: '詳細情報',
            success: '成功',
            warning: '警告'
        },
        patches: {
            apply: 'パッチ適用',
            category: 'カテゴリー',
            description: '説明',
            name: '名称',
            patches: 'パッチ'
        },
        processes: {
            abortconfirmation: '本当にこのプロセスを中止しますか?',
            abortprocess: 'プロセスを中止',
            action: {
                advance: '次へ',
                label: 'アクション'
            },
            activeprocesses: '有効プロセス',
            allstatuses: '全て',
            editactivity: 'アクティビティ変更',
            openactivity: 'アクティビティ表示',
            resumeprocesses: '中断していないプロセスを再開することはできません',
            startworkflow: '開始',
            workflow: 'ワークフロー'
        },
        relationGraph: {
            activity: 'アクティビティ',
            allLabelsOnGraph: '全ラベル',
            card: 'カード',
            cardList: 'カード一覧',
            cardRelations: 'リレーション',
            choosenaviagationtree: 'ナビゲーション選択',
            class: 'クラス',
            classList: 'クラス一覧',
            compoundnode: '複合ノード',
            disable: '無効化',
            edges: '辺',
            enable: '有効化',
            labelsOnGraph: 'ツールチップ',
            level: 'レベル',
            nodes: 'ノード',
            openRelationGraph: 'リレーショングラフを開く',
            quantity: '数量',
            refresh: 'リフレッシュ',
            relation: 'リレーション',
            relationGraph: 'リレーショングラフ',
            reopengraph: 'このノードから再表示'
        },
        relations: {
            adddetail: '詳細追加',
            addrelations: 'リレーション追加',
            attributes: '属性',
            begindate: '開始日',
            cardnotpresentmessage:
                '関連カードは使用できなくなりました。変更を続けるには、別のカードを選択する必要があります',
            code: 'コード',
            deletedetail: '詳細削除',
            deleterelation: 'リレーション削除',
            deleterelationconfirm: '本当にこのリレーションを削除しますか?',
            description: '説明',
            editcard: 'カード編集',
            editdetail: '詳細編集',
            editrelation: 'リレーション編集',
            errorexisting:
                'リレーション"{0}"はすでに存在します。新しいリレーションを作成する前に、既存のリレーションを編集するか、削除してください',
            extendeddata: '拡張データ',
            mditems: 'アイテム',
            missingattributes: '必須属性がありません',
            opencard: '関連するカードを開く',
            opendetail: '詳細表示',
            relationdata: 'リレーションデータ',
            type: 'タイプ'
        },
        reports: {
            csv: 'CSV',
            download: 'ダウンロード',
            format: 'フォーマット',
            msgs: {
                creationerror: 'レポート作成開始時に問題が発生しました',
                creationstarted: 'レポート作成開始、ダウンロードできるようになったら、通知されます'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: '印刷',
            reload: '再ロード',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'デフォルト'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: '毎日'
                        },
                        monthly: {
                            description: '毎月'
                        },
                        once: {
                            description: '一回'
                        },
                        weekly: {
                            description: '毎週'
                        },
                        yearly: {
                            description: '毎年'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'デフォルト'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Nobody'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'テーマ追加',
            analysisType: '表示タイプ',
            attribute: '属性',
            bordersIntervals: 'インターバル制限の設定が間違っています',
            calculateRules: 'スタイルルール作成',
            clearThematism: 'テーマ消去',
            color: '色',
            defineLegend: '凡例定義',
            defineThematism: 'テーマ定義',
            deletethematism: '本当にこのテーマを削除しますか？',
            editThematism: 'ルール設定編集',
            function: 'ファンクション',
            generate: '作成',
            geoAttribute: '地理属性',
            graduated: '段階',
            highlightSelected: 'ハイライト',
            intervals: '間隔',
            legend: '凡例',
            name: '名称',
            newThematism: '新テーマ',
            punctual: '点',
            quantity: '数',
            segments: 'セグメント',
            source: 'ソース',
            table: 'テーブル',
            thematism: 'テーマ',
            value: '値'
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
                    exitwithoutsave: '保存しないで終了',
                    stayinedit: '編集に戻る'
                },
                savebeforeexit: '添付ファイルに保存されていない変更があります。終了する前に、それらを保存しますか？',
                saveprocessmessage:
                    '添付ファイルは既存のカードにしか保存できません。保存を押すと、フォームに入力されたデータでプロセスインスタンスが作成されます。'
            },
            calendar: {
                error: 'パラメーター {0} が設定されていません',
                errorwrong: 'パラメーター {0} が間違っています'
            },
            customform: {
                addrow: '行追加',
                clonerow: '行を複製',
                datanotvalid: '無効なデータ',
                deleterow: '行削除',
                editrow: '行編集',
                export: 'エキスポート',
                import: 'インポート',
                importexport: {
                    expattributes: 'エキスポートデータ',
                    file: 'ファイル',
                    filename: 'ファイル名',
                    format: 'フォーマット',
                    importmode: 'インポートモード',
                    keyattributes: 'キー属性',
                    missingkeyattr: '一つ以上のキー属性を選択してください',
                    modeadd: '追加',
                    modemerge: 'マージ',
                    modereplace: '置き換え',
                    separator: 'セパレータ'
                },
                refresh: '初期設定に戻す'
            },
            linkcards: {
                checkedonly: '確認のみ',
                editcard: 'カード編集',
                opencard: 'カード表示',
                refreshselection: 'デフォルトを適用',
                removefilterconfirm: 'この操作を行うと、現在のフィルターが解除されます。続行しますか？',
                selectioncleared: '選択解除',
                togglefilterdisabled: 'フィルター無効化',
                togglefilterenabled: 'フィルター有効化'
            },
            notimplemented: 'Widget <strong>{0}</strong> は実装されていません',
            notsupportedinline:
                'このウィジェットタイプでは、インラインモードはサポートされていません。ウィジェットの設定を修正してください。',
            required: 'このウィジェットは必要です'
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
