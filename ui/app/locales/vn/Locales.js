(function () {
    Ext.define('CMDBuildUI.locales.vn.Locales', {
        requires: ['CMDBuildUI.locales.vn.LocalesAdministration'],
        override: 'CMDBuildUI.locales.Locales',
        singleton: true,
        localization: 'vn',
        administration: CMDBuildUI.locales.vn.LocalesAdministration.administration,
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
            add: 'Thêm tập tin đính kèm',
            attachmenthistory: 'Lịch sử tập tin đính kèm',
            author: 'Người cài đặt',
            authordescription: 'Mô tả cài đặt',
            browse: 'Duyệt qua',
            category: 'Danh mục',
            code: 'Mã',
            creationdate: 'Tạo ngày',
            deleteattachment: 'Xóa tệp đính kèm',
            deleteattachment_confirmation: 'Bạn có chắc là bạn muốn xóa tệp này?',
            description: 'Mô tả',
            download: 'Tải xuống',
            dropfiles: 'Thả các tập tin ở đây',
            editattachment: 'Chỉnh sửa tệp đính kèm',
            errordms: 'DMS sẽ bị tắt và không thể lưu tệp',
            file: 'Tập tin',
            filealreadyinlist: 'Tệp tin {0} đã có trong danh sách',
            filename: 'Tên tệp',
            fileview: 'Chế độ xem  tệp đính kèm',
            invalidfiles: 'Loại bỏ tệp tin không hợp lệ',
            majorversion: 'Phiên bản chính',
            maxsize: 'Kích thước tệp tối đa là {0} MB.',
            metadata: 'Metadata',
            modificationdate: 'sửa đổi ngày tháng',
            new: 'Tệp đính kèm mới',
            nocategory: 'Chưa được phân loại',
            preview: 'Xem trước',
            removefile: 'Loại bỏ tệp tin',
            statuses: {
                empty: 'Tệp tin trống',
                error: 'Lỗi',
                extensionNotAllowed: 'Tệp mở rộng không được cho phép',
                loaded: 'Đã tải',
                ready: 'Sẵn sàng',
                toolarge: 'Qúa lớn'
            },
            successupload: '{0} tệp đính kèm được tải lên',
            uploadfile: 'Tải tệp tin',
            version: 'Phiên bản',
            viewhistory: 'Chế độ xem lịch sử tệp đính kèm',
            viewmetadata: 'Chế độ xem tệp đính kèm metadata',
            warningmessages: {
                atleast:
                    '{0}: {1} tệp đính kèm thuộc loại "{2}" đã được tải. Danh mục này cần ít nhất {3} tệp đính kèm.',
                exactlynumber:
                    '{0}: {1} tệp đính kèm thuộc loại "{2}" đã được tải. Danh mục này yêu cầu {3} tệp đính kèm',
                maxnumber:
                    '{0}: {1} tệp đính kèm thuộc loại "{2}" đã được tải. Danh mục này yêu cầu tối đa {3} tệp đính kèm.'
            },
            wrongfileextension: '{0} tệp mở rộng không được phép'
        },
        bim: {
            bimViewer: 'Chế độ xem Bim',
            card: {
                label: 'Thẻ'
            },
            ifcproperties: {
                BOUNDING_BOX_SIZE_ALONG_X: 'Kích thước hộp giới hạn theo trục X',
                BOUNDING_BOX_SIZE_ALONG_Y: 'Kích thước hộp giới hạn theo trục Y',
                BOUNDING_BOX_SIZE_ALONG_Z: 'Kích thước hộp giới hạn theo trục Z',
                calculated: 'Tính toán',
                description: 'Mô tả',
                guid: 'GUID',
                label: 'Đặc tính',
                name: 'Tên',
                type: 'Loại',
                uuid: 'UUID'
            },
            layers: {
                label: 'Các lớp',
                menu: {
                    hideAll: 'Ẩn tất cả',
                    hideAllLayers: 'Ẩn tất cả các lớp',
                    hideAllXeokit: 'Ẩn tất cả {0}',
                    showAll: 'Hiển thị tất cả',
                    showAllLayers: 'Hiển thị tất cả các lớp',
                    showAllXeokit: 'Hiển thị tất cả {0}'
                },
                name: 'Tên',
                quantity: 'Số lượng',
                visibility: 'Hiển thị'
            },
            menu: {
                camera: 'Máy ảnh',
                disableslice: 'Vô hiệu slice object',
                enableslice: 'Cho phép slice object',
                frontView: 'Chế độ xem trước',
                mod: 'Kiểm soát người xem',
                ortho: 'Chuyển qua chế độ xem trực giao',
                orthographic: 'Máy ảnh chỉnh hình',
                pan: 'Cuộn',
                panXeokit: 'Điều khiển người xem : Cuộn ( Shift)',
                perspe: 'Chuyển qua chế độ xem phối cảnh',
                perspective: 'Máy ảnh phối cảnh',
                resetSlice: 'Cài lại mặt cắt',
                resetView: 'Cài lại chế độ xem',
                rotate: 'Quay',
                rotateXeokit: 'Điều khiển người xem : Xoay',
                save: 'Lưu chế độ xem',
                shortcuts: {
                    down: 'Quay xuống',
                    left: 'Quay trái',
                    right: 'Quay phải',
                    title: 'Các phím tắt bàn phím',
                    up: 'Quay lên',
                    zoomin: 'Phóng to',
                    zoomout: 'Phóng nhỏ'
                },
                sideView: 'Chế độ xem bên',
                threeD: 'Chuyển qua chế độ xem 3D',
                topView: 'Chế đồ xem đầu',
                twoD: 'Chuyển qua chế độ xem 2D'
            },
            navCube: {
                back: 'Trở lại',
                bottom: 'Ở dưới',
                front: 'Trước',
                left: 'Rời',
                right: 'Đúng/ bên phải',
                top: 'Đầu'
            },
            showBimCard: 'Mở chế độ xem 3D',
            tree: {
                arrowTooltip: 'Chọn yếu tố',
                label: 'Các đối tượng',
                multipleElements: '<em>Select elements</em>',
                open_card: 'Mở thẻ liên quan',
                root: 'Ifc Root'
            }
        },
        bulkactions: {
            abort: 'Hủy bỏ mục chọn',
            addfields: '<em>Add fields</em>',
            alertdownloadattachments: 'Một số tệp đính kèm sẽ không được tải xuống vì chúng chưa được lưu vào máy chủ',
            alertnoattachmentsdownload:
                'Không có tệp đính kèm nào sẽ được tải xuống vì chúng chưa được lưu vào máy chủ',
            cancelselection: 'Hủy bỏ lựa chọn',
            confirmabort: 'Bạn đang hủy bỏ {0} phiên bản quy trình. Bạn có chắc chắn muốn tiếp tục không?',
            confirmdelete: 'Bạn đang xóa thẻ {0}. Bạn có chắc chắn muốn tiếp tục không?',
            confirmdeleteattachements: 'Bạn đang xóa {0} tệp đính kèm. Bạn có chắc chắn muốn tiếp tục không?',
            confirmedit: 'Bạn đang sửa đổi thẻ {0} trên {1}. Bạn có chắc chắn muốn tiếp tục không?',
            delete: 'Xóa sản phẩm đã chọn',
            download: 'Tải xuống các tệp đính kèm đã chọn',
            downloadall: 'Tải xuống tất cả các tệp đính kèm',
            edit: 'Chỉnh sửa sản phẩm đã chọn',
            missingfields: '<em>The following fields must be added for correct form validation: {0}</em>',
            noattributeselected: 'Vui lòng chọn ít nhất một thuộc tính',
            selectall: 'Chọn tất cả các mục'
        },
        calendar: {
            active_expired: 'Hoạt động / Hết hạn',
            add: 'Thêm lịch',
            advancenotification: 'Thông báo trước ngày',
            allcategories: 'Tất cả danh mục',
            alldates: 'Tất cả các ngày',
            associatedcard: 'Thẻ liên kết',
            calculated: 'Tính toán',
            calendar: 'Lịch',
            cancel: 'Đánh dấu là đã hủy',
            category: 'Danh mục',
            class: 'Lớp',
            cm_confirmcancel: 'Bạn có chắc là bạn muốn chọn hủy tất cả các lịch đã chọn?',
            cm_confirmcomplete: 'Bạn có chắc là bạn muốn chọn hoàn thành hết các lịch đã chọn?',
            cm_markcancelled: 'Đánh dấu là đã hủy các lịch đã chọn',
            cm_markcomplete: 'Đánh dấu đã hoàn thành các lịch đã chọn',
            complete: 'Đánh dấu đã xong',
            completed: 'Hoàn thành',
            date: 'Ngày',
            days: 'Ngày',
            delaybeforedeadline: 'Hoãn trước hạn',
            delaybeforedeadlinevalue: 'Hoãn trước giá trị hạn',
            description: 'Mô tả',
            editevent: 'Chỉnh sửa lịch trình',
            enddate: 'Ngày kết thúc',
            endtype: 'Loại kết thúc',
            event: 'Lịch biểu',
            execapplyfilter: 'Không thể thực hiện áp dụng bộ lọc vì có nhiều hơn một ứng dụng',
            execremovefilter: 'Không thể thực hiện loại bỏ bộ lọc vì có nhiều hơn một ứng dụng',
            executiondate: 'Ngày hoàn thành',
            frequency: 'Thường xuyên',
            frequencymultiplier: 'Tần số',
            grid: 'Lưới',
            leftdays: 'Đến ngày',
            londdescription: 'Mô tả đầy đủ',
            manual: 'Thủ công',
            maxactiveevents: 'Lịch hoạt động tối đa',
            messagebodydelete: 'Bạn có muốn xóa quy tắc lên lịch không?',
            messagebodyplural: 'Có {0} quy tắc lịch biểu',
            messagebodyrecalculate: 'Bạn có muốn tính toán lại quy tắc lịch trình với ngày mới không?',
            messagebodysingular: 'Có {0} quy tắc lịch biểu',
            messagetitle: 'Lên lịch tính toán lại',
            missingdays: 'Những ngày thất lạc',
            next30days: '30 ngày tới',
            next7days: '7 ngày tới',
            notificationtemplate: 'Mẫu dùng cho thông báo',
            notificationtext: 'Văn bản thông báo',
            occurencies: 'Số lần xuất hiện',
            operation: 'Vận hành hoạt động',
            partecipantgroup: 'Nhóm tham gia',
            partecipantuser: 'Nhóm người dùng',
            priority: 'Quyền ưu tiên',
            recalculate: 'Tính toán lại',
            referent: 'Tham chiếu',
            scheduler: 'Người lên lịch',
            sequencepaneltitle: 'Tạo lịch biểu',
            startdate: 'Ngày bắt đầu',
            status: 'Trạng thái',
            today: 'Hôm nay',
            type: 'Loại',
            viewevent: 'Chế độ xem lịch',
            waiteventgeneration: '<em>Generating events. Please wait for them to complete</em>',
            widgetcriterion: 'Tiêu chí tính toán',
            widgetemails: 'E-mails',
            widgetgotocard: 'Chuyển đến thẻ nguồn',
            widgetsourcecard: 'Nguồn thẻ'
        },
        chat: {
            title: 'Trò chuyện'
        },
        classes: {
            cards: {
                addcard: 'Thêm thẻ',
                clone: 'Sao chép',
                clonewithrelations: 'Sao chép thẻ và các mối liên hệ',
                deletebeaware: 'Lưu ý rằng',
                deleteblocked: 'Không thể tiếp tục xóa vì có quan hệ với {0}.',
                deletecard: 'Xóa thẻ',
                deleteconfirmation: 'Bạn có chắc là bạn muốn xóa thẻ này?',
                deleterelatedcards: 'Cũng như {0} các thẻ liên quan sẽ bị xóa',
                deleterelations: 'Mối quan hệ với thẻ {0} bị xóa',
                label: 'Các thẻ',
                modifycard: 'Sửa đổi thẻ',
                opencard: 'Mở thẻ',
                print: 'In thẻ'
            },
            simple: 'Ðơn giản',
            standard: 'Tiêu chuẩn'
        },
        common: {
            actions: {
                add: 'Thêm',
                apply: 'Áp dụng',
                cancel: 'Hủy bỏ',
                clear: 'Làm sạch',
                close: 'Đóng',
                copy: '<em>Copy</em>',
                copyerrormessage: '<em>Copy the error message to the clipboard</em>',
                delete: 'Xóa',
                edit: 'Sửa',
                execute: 'Hoàn thành',
                forward: '<em>Forward</em>',
                help: 'Giúp',
                hide: '<em>Hide</em>',
                load: 'Tải',
                open: 'Mở',
                previous: '<em>Previous</em>',
                refresh: 'Làm mới dữ liệu',
                remove: 'Gỡ bỏ',
                save: 'Lưu',
                saveandapply: 'Lưu và áp dụng',
                saveandclose: 'Lưu và đóng',
                search: 'Tìm kiếm',
                searchaitext: '<em>Search with AI...</em>',
                searchtext: 'Tìm kiếm…'
            },
            and: 'Và',
            attributes: {
                boolfalse: 'Sai',
                booltrue: 'Đúng',
                link: {
                    errlabel: 'Nhãn yêu cầu',
                    errurl: 'Url là bắt buộc khi nhãn không trống',
                    label: 'Nhãn',
                    url: 'Url'
                },
                nogroup: 'Cơ sở dữ liệu'
            },
            dates: {
                date: 'Ngày/tháng/năm',
                datetime: 'Ngày/tháng/năm giờ:phút:giây',
                time: 'H:i:s'
            },
            editor: {
                clearhtml: 'Xóa HTML',
                expand: 'Mở rộng trình chỉnh sửa',
                reduce: 'Giảm chỉnh sửa',
                signature: 'Thêm chữ ký',
                unlink: 'Hủy liên kết',
                unlinkmessage: 'Chuyển liên kết nối thành văn bản'
            },
            grid: {
                disablemultiselection: 'Vô hiệu hóa đa lựa chọn',
                enamblemultiselection: 'Cho phép đa lựa chọn',
                export: 'Xuất dữ liệu',
                filterremoved: 'Bộ lọc hiện tại sẽ bị loại bỏ',
                import: 'Nhập dữ liệu',
                itemnotfound: 'Mục không tìm thấy',
                lastUpdate: '<em>Last update</em>',
                list: 'Danh sách',
                opencontextualmenu: 'Mở trình đơn giao diện đồ hoạ',
                print: 'In',
                printcsv: 'In như CSV',
                printmap: 'In bản đồ',
                printodt: 'In như ODT',
                printpdf: 'In như PDF',
                row: 'Mục',
                rows: 'Các mục',
                subtype: 'Loại phụ',
                thematism: '<em>Thematism</em>'
            },
            tabs: {
                activity: 'Hoạt động',
                attachment: 'Tập tin đính kèm',
                attachments: 'Những tập tin đính kèm',
                card: 'Thẻ',
                clonerelationmode: 'Sao chép chế độ liên hệ',
                details: 'Chi tiết',
                emails: 'E-mails',
                history: 'Lịch sử',
                notes: 'Các ghi chú',
                relations: 'Các mối quan hệ',
                schedules: 'Những lịch biểu',
                sources: 'Các nguồn'
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
                download: 'Tải đồ thị',
                downloaddata: '<em>Download chart data</em>',
                gridhide: 'Ẩn dữ liệu lưới',
                gridshow: 'Hiển thị lưới dữ liệu',
                openinpopup: 'Mở trong cửa sổ bật lên',
                reload: 'Tải lại',
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
            abort: 'Hủy bỏ',
            addattachmentsfromdocumentarchive: 'Thêm tập đính kèm từ tài liệu đã lưu',
            alredyexistfile: 'Đã tồn tại tệp với tên này',
            archivingdate: 'Ngày lưu trữ',
            attachfile: 'Đính kèm tập tin',
            attachmentsmessage:
                "<em>The template requires you to attach card attachments. You won't be able to use it until you save the process</em>",
            bcc: 'Bcc',
            cc: 'Cc',
            composeemail: 'Soạn email',
            composefromtemplate: 'Soạn từ mẫu sẵn',
            delay: 'Hoãn',
            delays: {
                day1: 'Trong 1 ngày',
                days2: 'Trong 2 ngày',
                days4: 'Trong 4 ngày',
                hour1: '1 giờ',
                hours2: '2 giờ',
                hours4: '4 giờ',
                month1: 'Trong 1 tháng',
                negativeday1: '1 ngày trước',
                negativedays2: '2 ngày trước',
                negativedays4: '4 ngày trước',
                negativehour1: '1 giờ trước',
                negativehours2: '2 giờ trước',
                negativehours4: '4 giờ trước',
                negativemonth1: '1 tháng trước',
                negativeweek1: '1 tuần trước',
                negativeweeks2: '2 tuần trước',
                none: 'Không có gì',
                week1: 'Trong 1 tuần',
                weeks2: 'Trong 2 tuần'
            },
            dmspaneltitle: 'Chọn tệp đính kèm từ cơ sở dữ liệu',
            edit: 'Sửa',
            fileattributesmessage:
                "<em>There is a dependency on a file type attribute. You won't be able to use it until you save the process</em>",
            from: 'Từ',
            gridrefresh: 'Làm mới lưới',
            groupbystatus: 'Nhóm theo trạng thái',
            keepsynchronization: 'Giữ đồng bộ hóa',
            maxsize: 'Kích thước tối đa được phép cho các tệp đính kèm là {0}.',
            message: 'Tin nhắn',
            regenerateallemails: 'Tạo lại tất cả các e-mail',
            regenerateemail: 'Tái tạo e-mail',
            reloadconfirmationmessage: 'Bản thảo e-mail sẽ bị hủy. Ban có muốn tiếp tục?',
            remove: 'Gỡ bỏ',
            remove_confirmation: 'Bạn có chắc là bạn muốn xóa thư này?',
            reply: 'Trả lời',
            replyprefix: 'trên {0}, {1} viết:',
            searchattachment: 'Tìm kiếm tệp đính kèm',
            searchcard: 'Tìm kiếm thẻ',
            selectaclass: 'Chọn 1 lớp',
            sendemail: 'Gửi e-mail',
            sendingemail: 'Đang gửi e-mail',
            signature: 'Chữ ký',
            size: 'Kích thước',
            status: 'Trạng thái',
            statuses: {
                draft: 'Dự thảo',
                error: 'Lỗi',
                new: 'Mới',
                outgoing: 'Hộp gửi đi',
                received: 'Nhận được',
                sent: 'Gửi',
                skipped: 'Bỏ qua'
            },
            subject: 'Chủ đề',
            templateattachments: '<em>This email contains attachments uploaded automatically by the template</em>',
            to: 'Đến',
            view: 'Chế độ xem'
        },
        errors: {
            actionnotallowed: '<em>Action not allowed</em>',
            autherror: 'Sai Tên người dùng hay Mật khẩu',
            classnotfound: 'Lớp  {0} không tìm thấy',
            fieldrequired: 'Vùng này là bắt buộc',
            invalidfilter: 'Bộ lọc không hợp lệ',
            nodatatoexport: '<em>The chart has no data to export</em>',
            notfound: 'Mục không tìm thấy',
            value3chars: '<em>Value must be at least 3 chars long.</em>'
        },
        filters: {
            actions: 'Những hành động',
            addfilter: 'Thêm bộ lọc',
            any: '<em>At least one</em>',
            attachments: 'Những tập tin đính kèm',
            attachmentssearchtext: 'TÌm kiếm tệp đính kèm bằng văn bản',
            attribute: 'Chọn một thuộc tính',
            attributes: 'Các thuộc tính',
            clearfilter: 'Làm sạch bộ lọc',
            clone: 'Sao chép',
            copyof: 'Sao chép của',
            currentgroup: 'Nhóm hiện tại',
            currentuser: 'Người dùng hiện tại',
            defaultset: 'Đặt làm mặc định',
            defaultunset: 'Bỏ đặt từ mặc định',
            description: 'Mô tả',
            domain: 'Tên miền',
            errorname: 'Tên bộ lọc đã được sử dụng',
            filterdata: 'Lọc dữ liệu',
            fromfilter: 'Từ bộ lọc',
            fromselection: 'Lựa chọn từ',
            group: 'Nhóm',
            ignore: 'Không chấp nhận',
            migrate: 'Chuyển đổi',
            name: 'Tên',
            newfilter: 'Bộ lọc mới',
            noone: 'Không có',
            operator: 'Nhà điều hành',
            operators: {
                and: 'Và',
                beginswith: 'Bắt đầu với',
                between: 'Ở giữa',
                contained: 'Bao gồm',
                containedorequal: 'Bao gồm hoặc bằng',
                contains: 'Bao gồm',
                containsorequal: 'Bao gồm hoặc bằng',
                descriptionbegin: 'Mô tả bắt đầu với',
                descriptioncontains: 'Mô tả chứa',
                descriptionends: 'Mô tả kết thúc với',
                descriptionnotbegin: 'Mô tả không bắt đầu với',
                descriptionnotcontain: 'Mô tả không chứa',
                descriptionnotends: 'Mô tả không kết thúc với',
                different: 'Khác biệt',
                doesnotbeginwith: 'Không bắt đầu với',
                doesnotcontain: 'Không chứa',
                doesnotendwith: 'Không kết thúc với',
                endswith: 'Kết thúc tại',
                equals: 'Cân bằng',
                greaterthan: 'Nhiều hơn',
                isnotnull: 'Có phải vô hiệu lực',
                isnull: 'Vô hiệu lực',
                lessthan: 'Ít hơn',
                or: 'Hoặc'
            },
            relations: 'Các mối quan hệ',
            removeattribute: 'Loại bỏ thuộc tính',
            removeblock: 'Loại bỏ chế độ khóa',
            type: 'Loại',
            typeinput: 'Thông số đầu vào',
            typeorchoosetheattributename: '<em>Type or choose the attribute name</em>',
            user: 'Người dùng',
            value: 'Gía trị'
        },
        gis: {
            card: 'Thẻ',
            cardsMenu: 'Bảng kê thẻ',
            code: 'Mã',
            contextmenu: {
                coordinates: '<em>Get coordinates</em>',
                coordinatescopiedmessage: '<em>Coordinates copied to clipboard</em>'
            },
            description: 'Mô tả',
            extension: {
                errorCall: 'Lỗi',
                noResults: 'Không có kết quả'
            },
            externalServices: 'Các dịch vụ ngoài',
            geographicalAttributes: 'Thuộc tính địa lí',
            geoserverLayers: 'Các lớp của máy chủ địa lý',
            labels: {
                all: 'Tất cả',
                alltitle: 'Hiển thị tất cả nhãn',
                alltitleclass: 'Hiển thị tất cả nhãn của lớp',
                clear: '<em>Clear search</em>',
                hidden: 'Ẩn',
                hiddentitle: 'Ẩn nhãn',
                hideinfowindow: 'Ẩn thông tin cửa sổ',
                label: 'Các nhãn',
                search: '<em>Search on the map</em>',
                selected: 'Chọn',
                selectedtitle: 'Hiển thị nhãn của các mục đã chọn',
                showinfowindow: 'Hiển thị thông tin cửa sổ'
            },
            layers: 'Các lớp',
            layersTab: {
                clearPreferences: 'Xóa tùy chọn lớp',
                preferencesCleared: 'Đã xóa tùy chọn lớp',
                preferencesSaved: 'Đã lưu tùy chọn lớp',
                savePreferences: 'Lưu tùy chọn lớp',
                updatePreferences: 'Cập nhật tùy chọn lớp'
            },
            list: 'Danh sách',
            longpresstitle: 'Địa lý trong khu vực',
            map: 'Bản đồ',
            mapServices: 'Dịch vụ bản đồ',
            menu: {
                font: 'Cỡ chữ',
                options: 'Tùy chọn'
            },
            position: 'Chức vụ',
            root: 'Nguồn gốc',
            sourceMap: {
                default: '<em>Use default view</em>',
                satellite: '<em>Use satellite view</em>'
            },
            tree: 'Cây',
            type: 'Loại',
            view: 'Chế độ xem',
            zoom: 'Phóng'
        },
        history: {
            activityname: 'Tên hoạt động',
            activityperformer: 'Người thực hiện hoạt động',
            attributes: 'Thuộc tính ({0})',
            begindate: 'Ngày bắt đầu',
            enddate: 'Ngày kết thúc',
            fieldname: 'Tên miền',
            fulldata: 'Dữ liệu đầy đủ',
            onlychanges: 'Chỉ thay đổi',
            otherrelations: 'Mối liên hệ khác',
            processstatus: 'Trạng thái',
            referencerelation: 'Quan hệ tham chiếu',
            relation: 'Mối quan hệ',
            system: 'Hệ thống',
            type: 'Loại',
            user: 'Người dùng',
            userdescription: 'Mô tả người dùng',
            value: 'Gía trị',
            viewmode: 'Chế độ xem'
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
                uri: 'Cơ sở dữ liệu URI',
                user: 'Cơ sở dữ liệu người dùng'
            },
            downloadreport: 'Tải xuống báo cáo',
            emailfailure: 'Đã xảy ra lỗi khi gửi email',
            emailmessage: 'Đính kèm  báo cáo quan trọng của tập tin "{0}" vào ngày {1}',
            emailsubject: 'Nhập dữ liệu báo cáo',
            emailsuccess: 'E-mail đã được gửi thành công!',
            export: 'Xuất',
            exportalldata: 'Tất cả dữ liệu',
            exportfiltereddata: 'Chỉ dữ liệu phù hợp với bộ lọc lưới',
            gis: {
                importconfiguration: '<em>Import configuration</em>',
                shapeimportdisabled: 'Tính năng nhập hình dạng không được kích hoạt cho mẫu này',
                shapeimportenabled: '<em>The import of shapes is enabled for this template</em>'
            },
            ifc: {
                card: 'Thẻ',
                project: 'Dự án',
                sourcetype: 'Nhập mẫu'
            },
            import: 'Nhập',
            importresponse: 'Nhập phản hồi',
            response: {
                created: 'Tạo các thẻ',
                deleted: 'Xóa thẻ',
                errors: 'Lỗi',
                linenumber: 'Số dòng',
                message: 'Tin nhắn',
                modified: 'Thẻ đã sửa đổi',
                notprocessed: 'Thẻ chưa qua xử lý',
                processed: 'Các hàng đã xử lý',
                recordnumber: 'Con số kỷ lục',
                unchanged: 'Thẻ không thay đổi'
            },
            sendreport: 'Gửi báo cáo',
            template: 'Mẫu',
            templatedefinition: 'Định nghĩa mẫu'
        },
        joinviews: {
            active: 'Hoạt động',
            addview: 'Thêm lượt xem',
            alias: 'Bí danh',
            attribute: 'Thuộc tính',
            attributecustomization: 'Các tùy chỉnh thuộc tính',
            attributes: 'Các thuộc tính',
            attributeschoice: 'Lựa chọn các thuộc tính',
            attributesof: 'Thuộc tính: {0}',
            createview: 'Tạo chế độ xem',
            datasorting: 'Sắp xếp dữ liệu',
            delete: 'Xóa',
            deleteview: 'Xóa chế độ xem',
            deleteviewconfirm: 'Bạn có chắc là bạn muốn xóa chế độ xem này?',
            description: 'Mô tả',
            disable: 'Vô hiệu hóa',
            domainalias: 'Bí danh miền',
            domainsof: 'Tên miền của  {0}',
            edit: 'Sửa',
            editview: 'Chỉnh sửa cấu hình xem',
            enable: 'Cho phép',
            fieldsets: 'Lĩnh vực',
            filters: 'Bộ lọc',
            generalproperties: 'Thuộc tính chung',
            group: 'Nhóm',
            innerjoin: 'Tham gia bên trong',
            jointype: 'Loại tham gia',
            joinview: 'Chế độ xem từ tham gia',
            klass: 'Lớp',
            manageview: 'Quản lý chế độ xem',
            masterclass: 'Lớp chủ',
            masterclassalias: 'Lớp chủ khác',
            name: 'Tên',
            newjoinview: 'Ché độ xem mới từ tham gia',
            outerjoin: 'Tham gia bên ngoài',
            pleaseseleceavalidmasterclass: 'Vui lòng chọn một lớp học chính hợp lệ',
            refreshafteredit: 'Bạn có muốn làm mới trang để xem các thay đổi không?',
            selectatleastoneattribute: 'Vui lòng chọn ít nhất một thuộc tính',
            selectdisplayattributegrid:
                'Vui lòng chọn ít nhất một thuộc tính để hiển thị dạng lưới và một dạng lưới rút gọn',
            showingrid: 'Hiển thị trong lưới',
            showinreducedgrid: 'Hiển thị trong lưới giảm',
            targetalias: 'Hướng đến Lớp bí danh'
        },
        login: {
            buttons: {
                login: 'Đăng nhập',
                logout: 'Thay đổi người dùng'
            },
            fields: {
                group: 'Nhóm',
                language: 'Ngôn ngữ',
                password: 'Mật khẩu',
                tenants: 'Người dùng',
                username: 'Tên người dùng'
            },
            loggedin: 'Đăng nhập',
            sso: {
                loginfailure: 'Đăng nhập thất bại',
                loginwith: 'Đăng nhập với {0}',
                or: 'Hoặc',
                usernotenabled: 'Người dùng không cho phép'
            },
            title: 'Đăng nhập',
            welcome: 'Chào mừng trở lại {0}.'
        },
        main: {
            administrationmodule: 'Mô-đun Quản lý',
            alerts: '<em>Alerts</em>',
            baseconfiguration: 'Cấu hình cơ sở',
            cardlock: {
                lockedmessage: 'Bạn không thể chỉnh sửa thẻ này vì {0} đang chỉnh sửa thẻ.',
                someone: 'Ai đó'
            },
            changegroup: 'Đổi nhóm',
            changetenant: 'Thay đổi  {0}',
            confirmchangegroup: 'Bạn có chắc là bạn muốn thay đổi nhóm?',
            confirmchangetenants: 'Bạn có chắc là bạn muốn thay đổi người dùng?',
            confirmdisabletenant: 'Bạn có chắc là bạn muốn tắt cờ "không chấp nhận người dùng"?',
            confirmenabletenant: 'Bạn có chắc là bạn muốn bật cờ " không chấp nhận người dùng"?',
            description: 'Mô tả',
            ignoretenants: 'Không chấp nhận {0}',
            info: 'Thông tin',
            logo: {
                cmdbuild: 'CMDBuild logo',
                cmdbuildready2use: 'CMDBuild READY2USE logo',
                companylogo: 'Logo Công ty',
                hdaitsm: '<em>HDA ITSM</em>',
                openmaint: 'Mở MAINT logo'
            },
            logout: 'Đăng xuất',
            managementmodule: 'Danh mục quản lí dữ liệu',
            multigroup: 'Nhiều nhóm',
            multitenant: 'Nhiều {0}',
            navigation: 'Hướng',
            pagenotfound: 'Trang không tìm thấy',
            password: {
                change: 'Đổi mật khẩu',
                confirm: 'Xác nhận mật khẩu',
                email: 'Địa chỉ e-mail',
                err_confirm: 'Mật khẩu không khớp',
                err_diffprevious: 'Mật khẩu không được trùng với mật khẩu trước đó.',
                err_diffusername: 'Mật khẩu không được trùng với tên sử dụng',
                err_length: 'Mật khẩu phải dài ít nhất {0} ký tự.',
                err_reqdigit: 'Mật khẩu phải chứa ít nhất một chữ số.',
                err_reqlowercase: 'Mật khẩu phải chứa ít nhất một ký tự viết thường.',
                err_requppercase: 'Mật khẩu phải chứa ít nhất một ký tự viết hoa.',
                expired: 'Mật khẩu của bạn đã hết hạn. Bạn cần phải đổi mật khẩu ngay',
                forgotten: 'Tôi đã quên mật khẩu',
                new: 'Mật khẩu mới',
                old: 'Mật khẩu cũ',
                recoverysuccess: 'Chúng tôi đã gửi cho bạn một email với hướng dẫn khôi phục mật khẩu của bạn.',
                reset: 'Cài lại mật khẩu',
                saved: 'Mật khẩu đúng đã được lưu'
            },
            pleasecorrecterrors: 'Vui lòng sửa lỗi được chỉ định!',
            preferences: {
                comma: 'Dấu phẩy',
                decimalserror: 'Phải thể hiện số thập phân',
                decimalstousandserror: 'Dấu phân cách số thập phân và hàng nghìn phải khác nhau',
                default: 'Mặc định',
                defaultvalue: 'Giá trị mặc định',
                favouritemenulocation: 'Vị trí menu yêu thích',
                firstdayofweek: 'Ngày đầu tiên của tuần',
                gridpreferencesclear: 'Xóa tùy chọn',
                gridpreferencescleared: 'Đã xóa tùy chọn lưới!',
                gridpreferencessave: 'Lưu tùy chọn lưới',
                gridpreferencessaved: 'Đã lưu tùy chọn lưới!',
                gridpreferencesupdate: 'Cập nhật tùy chọn lưới',
                groupemailsbystatus: 'Hiển thị các nhóm email bằng qua trạng thái',
                labelcsvseparator: 'Phân cách CSV',
                labeldateformat: 'Định dạng ngày',
                labeldecimalsseparator: 'Dấu phân cách số thập phân',
                labellanguage: 'Ngôn ngữ',
                labelthousandsseparator: 'Hàng nghìn phân cách',
                labeltimeformat: 'Định dạng thời gian',
                msoffice: 'Microsoft Office',
                no: 'Không',
                notificationslabel: 'Trò chuyện và thông báo',
                notificationssound: 'Phát âm thanh để nhận thông báo',
                notificationssoundvolume: '<em>Notification volume</em>',
                period: 'Giai đoạn',
                preferredfilecharset: 'Mã hóa CSV',
                preferredofficesuite: 'Bộ văn phòng ưu tiên',
                sendcancellationperiod: 'E-mail gửi lệnh hủy theo giây',
                showcollapsed: '<em>Show collapsed</em>',
                showfovouritesafter: 'Hiển thị sau menu điều hướng',
                showfovouritesbefore: 'Hiển thị trước menu điều hướng',
                space: 'Không gian',
                thousandserror: 'Hàng nghìn vùng sẽ được hiển thị',
                timezone: 'Vùng thời gian',
                twelvehourformat: 'định dạng 12 giờ',
                twentyfourhourformat: 'định dạng 24 giờ',
                yes: 'Đồng ý'
            },
            searchinallitems: 'Tìm kiểm tất cả các mục',
            searchtenant: 'Tìm kiếm người dùng',
            selectdeselettenants: 'Chọn/ Bỏ tất cả',
            treenavcontenttitle: '{0} của  {1}',
            userpreferences: 'Sở thích'
        },
        menu: {
            allitems: 'Tất cả các sản phẩm',
            classes: 'Các lớp',
            custompages: 'Các trang tùy chỉnh',
            dashboards: 'Bảng điều khiển',
            dependencytrees: '<em>Dependency trees</em>',
            favourites: 'Yêu thích',
            favouritesadd: 'Thêm vào mục yêu thích',
            favouritesremove: 'Loại bỏ khỏi mục yêu thích',
            homepage: '<em>Homepage</em>',
            processes: 'Các quy trình',
            reports: 'Các báo cáo',
            setasinitialpage: 'Đặt làm trang ban đầu',
            views: 'Lượt xem'
        },
        mobile: {
            config: {
                configure: 'Cấu hình thiết bị di động',
                customercode: 'Mã khách hàng',
                devicename: 'Tên thiết bị',
                regenerate: 'Tái tạo',
                serverurl: 'Máy chủ URL'
            }
        },
        notes: {
            edit: 'Chỉnh sửa ghi chú'
        },
        notifications: {
            clearall: '<em>Clear all</em>',
            clearallSuccess: '<em>All notification have been deleted</em>',
            delete: 'Xóa thông báo',
            emptymessage: 'Không có thông báo nào để hiển thị.',
            label: 'Thông báo',
            viewall: 'Chế độ xem tất cả'
        },
        notifier: {
            attention: 'Chú ý',
            copied: '<em>Copied</em>',
            copiedtotheclipboard: '<em>Text copied to the clipboard</em>',
            error: 'Lỗi',
            genericerror: 'Lỗi chung',
            genericerrormessage: '<em>For more information, please check the system logs.</em>',
            genericinfo: 'Thông tin chung',
            genericwarning: 'Cảnh báo chung',
            info: 'Thông tin',
            more: '<em>More info</em>',
            success: 'Thành công',
            warning: 'Cảnh báo'
        },
        patches: {
            apply: 'Áp dụng các bản vá lỗi',
            category: 'Danh mục',
            description: 'Mô tả',
            name: 'Tên',
            patches: 'Bản vá lỗi'
        },
        processes: {
            abortconfirmation: 'Bạn có chắc là bạn muốn hủy bỏ quá trình?',
            abortprocess: 'Hủy bỏ quá trình',
            action: {
                advance: 'Nâng cao',
                label: 'hành động'
            },
            activeprocesses: 'Qúa trình hoạt động',
            allstatuses: 'Tất cả',
            editactivity: 'Chỉnh sửa hoạt động',
            openactivity: 'Mở chế độ hoạt động',
            resumeprocesses: 'Không thể tiếp tục các quy trình không bị tạm ngừng',
            startworkflow: 'Bắt đầu',
            workflow: 'Quy trình làm việc'
        },
        relationGraph: {
            activity: 'Hoạt động',
            allLabelsOnGraph: 'Tất cả các nhãn trên biểu đồ',
            card: 'Thẻ',
            cardList: 'Danh sách thẻ',
            cardRelations: 'Các thẻ liên quan',
            choosenaviagationtree: 'Chọn thanh điều hướng',
            class: 'Lớp',
            classList: 'Danh sách lớp',
            compoundnode: 'Nút tổ hợp',
            disable: 'Vô hiệu hóa',
            edges: 'Các cạnh',
            enable: 'Cho phép',
            labelsOnGraph: 'Chú giải công cụ trên đồ thị',
            level: 'Mức độ',
            nodes: 'Điểm giao',
            openRelationGraph: 'Mở rộng đồ thị mối quan hệ',
            quantity: 'Số lượng',
            refresh: 'Làm mới',
            relation: 'Mối quan hệ',
            relationGraph: 'Đồ thị mối quan hệ',
            reopengraph: 'Mở lại biểu đồ từ nút này'
        },
        relations: {
            adddetail: 'Thêm chi tiết',
            addrelations: 'Thêm các mối quan hệ',
            attributes: 'Các thuộc tính',
            begindate: 'Ngày bắt đầu',
            cardnotpresentmessage:
                '<em>The related card is no longer available. Proceeding with the modification will require choosing another card.</em>',
            code: 'Mã',
            deletedetail: 'Xóa chi tiết',
            deleterelation: 'Xóa mối quan hệ',
            deleterelationconfirm: 'Bạn có chắc là bạn muốn xóa mối liên hệ này?',
            description: 'Mô tả',
            editcard: 'Sửa đổi thẻ',
            editdetail: 'Chỉnh sửa chi tiết',
            editrelation: 'Chỉnh sửa mối quan hệ',
            errorexisting:
                '<em>Relation "{0}" already exists. Edit the existing one or delete it before create a new relation.</em>',
            extendeddata: 'Mở rộng dữ liệu',
            mditems: 'Các mục',
            missingattributes: 'Thiếu các thuộc tính bắt buộc',
            opencard: 'Mở thẻ liên quan',
            opendetail: 'Hiện thị chi tiết',
            relationdata: 'Dữ liệu liên quan',
            type: 'Loại'
        },
        reports: {
            csv: 'CSV',
            download: 'Tải xuống',
            format: 'Mẫu',
            msgs: {
                creationerror: 'Đã xảy ra sự cố khi bắt đầu tạo báo cáo.',
                creationstarted: 'Đã bắt đầu tạo báo cáo. Bạn sẽ được thông báo khi nó sẵn sàng để tải xuống'
            },
            odt: 'ODT',
            pdf: 'PDF',
            print: 'In',
            reload: 'Tải lại',
            rtf: 'RTF'
        },
        system: {
            data: {
                lookup: {
                    CalendarCategory: {
                        default: {
                            description: 'Mặc định'
                        }
                    },
                    CalendarFrequency: {
                        daily: {
                            description: 'Hằng ngày'
                        },
                        monthly: {
                            description: 'Hàng tháng'
                        },
                        once: {
                            description: 'Một lần'
                        },
                        weekly: {
                            description: 'Hàng tuần'
                        },
                        yearly: {
                            description: 'Hàng năm'
                        }
                    },
                    CalendarPriority: {
                        default: {
                            description: 'Mặc định'
                        }
                    }
                },
                role: {
                    nobody: {
                        description: 'Không có ai'
                    }
                }
            }
        },
        thematism: {
            addThematism: 'Thêm chủ đề',
            analysisType: 'Loại phân tích',
            attribute: 'Thuộc tính',
            bordersIntervals: 'Cấu hình sai cho giới hạn khoảng thời gian',
            calculateRules: 'Tạo quy tắc',
            clearThematism: 'Xóa chủ đề',
            color: 'Màu',
            defineLegend: 'Định nghĩa chú giải',
            defineThematism: 'Định nghĩa chủ đề',
            deletethematism: 'Bạn có chắc là bạn muốn xóa chủ đề này?',
            editThematism: 'Chỉnh sửa quy tắc cấu hình',
            function: 'Cơ năng',
            generate: 'Tạo',
            geoAttribute: 'Thuộc tính địa lý',
            graduated: 'Hoàn tất',
            highlightSelected: 'Làm nổi sản phẩm đã chọn',
            intervals: 'Khoảng thời gian',
            legend: 'Chú giải',
            name: 'Tên',
            newThematism: 'Chủ đề mới',
            punctual: 'Đúng giờ',
            quantity: 'Đếm',
            segments: 'Phân đoạn',
            source: 'Nguồn',
            table: 'Bàn',
            thematism: 'Chủ đề',
            value: 'Giá trị'
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
                    exitwithoutsave: 'Thoát nhưng không lưu',
                    stayinedit: 'Trở lại để sửa'
                },
                savebeforeexit:
                    'Có những thay đổi chưa được lưu trong tệp đính kèm. Bạn có muốn lưu chúng trước khi thoát không?',
                saveprocessmessage:
                    'Các tệp tin đính kèm chỉ được lưu trong các thẻ hiện có. Nhấn vào lưu, quá trình sẽ tạo với dữ liệu được nhập vào biểu mẫu'
            },
            calendar: {
                error: 'Tham số {0} không được đặt',
                errorwrong: 'Tham số {0} không được đặt chính xác'
            },
            customform: {
                addrow: 'Thêm hàng',
                clonerow: 'Sao chép dòng',
                datanotvalid: 'Dữ liệu không hợp lệ',
                deleterow: 'Xóa hàng',
                editrow: 'Chỉnh sửa hàng',
                export: 'Xuất',
                import: 'Nhập',
                importexport: {
                    expattributes: 'Dữ liệu để xuất',
                    file: 'Tập tin',
                    filename: 'Tên tệp',
                    format: 'Mẫu',
                    importmode: 'Nhập chế độ',
                    keyattributes: 'Những điểm chính',
                    missingkeyattr: 'Vui lòng chọn ít nhất một từ khóa',
                    modeadd: 'Thêm',
                    modemerge: 'Hợp nhất',
                    modereplace: 'Thay thế',
                    separator: 'Dấu phân cách'
                },
                refresh: 'Làm mới mặc định'
            },
            linkcards: {
                checkedonly: 'Chỉ kiểm tra',
                editcard: 'Sửa đổi thẻ',
                opencard: 'Mở thẻ',
                refreshselection: 'Áp dụng lựa chọn mặc định',
                removefilterconfirm: '<em>This action will remove the current filter. Do you want to continue?</em>',
                selectioncleared: '<em>Selection cleared</em>',
                togglefilterdisabled: 'Vô hiệu hóa mạng lưới bộ lọc',
                togglefilterenabled: 'Cho phép lưới lọc'
            },
            notimplemented: '<em>Widget <strong>{0}</strong> not implemented!</em>',
            notsupportedinline:
                'Chế độ nội tuyến không được hỗ trợ cho loại tiện ích này. Vui lòng sửa cấu hình Widget',
            required: 'Tiện ích này là bắt buộc'
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
