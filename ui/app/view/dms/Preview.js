Ext.define('CMDBuildUI.view.dms.Preview', {
    extend: 'Ext.Img',
    alias: 'widget.dms-preview',

    config: {
        /**
         * @cfg {String} attachmentUrl
         */
        attachmentUrl: null,

        /**
         * @cfg {String} proxyUrl
         */
        proxyUrl: null,

        /**
         * @cfg {String} attachmentId
         */
        attachmentId: null,

        /**
         * @cfg {String} DMSCategoryType
         */
        DMSCategoryType: null,

        /**
         * @cfg {Numeric} DMSCategoryTypeValue
         */
        DMSCategoryTypeValue: null,

        /**
         * @cfg {String} fileName
         */
        fileName: null,

        /**
         * @cfg {String} fileMimeType
         */
        fileMimeType: null
    },

    maxHeight: 75,
    maxWidth: 75,

    src: null,
    genericSrc: CMDBuildUI.util.helper.AttachmentsHelper.genericPreview,
    alt: CMDBuildUI.locales.Locales.attachments.preview,

    viewModel: {},

    /**
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateAttachmentUrl: function (newValue, oldValue) {
        const me = this;
        const vm = this.getViewModel();
        const record = vm.getData().record;
        const previewLimit = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.previewLimit);

        if (
            newValue &&
            (previewLimit == 0 || !vm.get('attachments') || vm.get('attachments').getTotalCount() <= previewLimit)
        ) {
            CMDBuildUI.util.helper.AttachmentsHelper.getPreview(
                vm.get('objectType'),
                vm.get('objectTypeName'),
                vm.get('objectId'),
                record.getId(),
                record.get('_category_name'),
                newValue
            ).then(function (src) {
                me.setSrc(src);
                me.updateLayout();
                me.on({
                    click: {
                        element: 'el', //bind to the underlying el property on the panel
                        fn: 'clickhandler',
                        scope: me
                    }
                });
            });
        } else {
            me.setSrc(me.genericSrc);
            me.setStyle('cursor', 'auto');
        }
    },

    /**
     *
     */
    clickhandler: function () {
        if (this.getAttachmentUrl()) {
            const metatype = this.getFileMimeType(),
                fileName = this.getFileName(),
                fileUrl =
                    this.getAttachmentUrl() +
                    '/' +
                    encodeURI(fileName)
                        .match(/(?:"[^"]*"|^[^"]*$)/)[0]
                        .replace(/"/g, '')
                        .replace(/\+/g, ' '),
                popupTitle = Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.attachments.fileview, fileName);

            const openFile = function (type) {
                const popup = CMDBuildUI.util.Utilities.openPopup(null, popupTitle, {
                    xtype: 'dms-file-view',
                    fileUrl: fileUrl,
                    fileType: type,
                    fileName: fileName,
                    // close popup fn
                    closePopup: function () {
                        popup.close();
                    }
                });
            };

            if (Ext.String.startsWith(metatype, 'image/', true)) {
                openFile(CMDBuildUI.view.dms.file.View.types.image);
            } else if (Ext.String.startsWith(metatype, 'text/', true)) {
                openFile(CMDBuildUI.view.dms.file.View.types.text);
            } else if (metatype === 'application/pdf') {
                openFile(CMDBuildUI.view.dms.file.View.types.pdf);
            } else {
                CMDBuildUI.util.File.download(fileUrl, fileName, undefined, undefined, {
                    skipUrlEncode: true
                });
            }
        }
    },

    /**
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateProxyUrl: function (newValue, oldValue) {
        this.setAttachmentUrlFromId(this.getAttachmentId());
    },

    /**
     *
     * @param {String} value
     * @param {String} oldValue
     *
     * This function sets the attachmentid config only if the vm record (passed by the rowWidget) has a valid id.
     * Useful when expanding the row and collapsing the parent grouping.
     */
    applyAttachmentId: function (value, oldValue) {
        return value;
    },

    /**
     *
     * @param {String} newValue
     * @param {String} oldValue
     */
    updateAttachmentId: function (newValue, oldValue) {
        this.setAttachmentUrlFromId(newValue);
    },

    /**
     *
     * @returns
     */
    initComponent: function () {
        if (Ext.isEmpty(this.src)) {
            this.setSrc(this.genericSrc);
        }
        return this.callParent(arguments);
    },

    privates: {
        /**
         *
         * @param {String} proxyUrl
         * @param {String} attachmentId
         * @returns
         */
        composeAttachmentUrl: function (proxyUrl, attachmentId) {
            return CMDBuildUI.util.Config.baseUrl + proxyUrl + '/' + attachmentId;
        },

        /**
         *
         * @param {String} attachmentId
         */
        setAttachmentUrlFromId: function (attachmentId) {
            const record = this.lookupViewModel().get('record');
            if (!Ext.isEmpty(attachmentId) && record && !record.phantom) {
                const proxyUrl = this.getProxyUrl();
                if (!Ext.isEmpty(proxyUrl)) {
                    const attachmentUrl = this.composeAttachmentUrl(proxyUrl, attachmentId);
                    this.setAttachmentUrl(attachmentUrl);
                    this.setStyle('cursor', 'pointer');
                }
            } else {
                this.setAttachmentUrl();
            }
        }
    }
});
