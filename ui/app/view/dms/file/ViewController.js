Ext.define('CMDBuildUI.view.dms.file.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.dms-file-view',

    control: {
        '#': {
            afterrender: 'onBeforeRender'
        },
        '#downloadbtn': {
            click: 'onDownloadBtnClick'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.dms.file.View} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const fileType = view.getFileType();
        if (
            fileType === CMDBuildUI.view.dms.file.View.types.text ||
            fileType === CMDBuildUI.view.dms.file.View.types.pdf
        ) {
            view.add({
                xtype: 'uxiframe',
                width: '100%',
                height: '100%',
                src: view.getFileUrl(),
                ariaAttributes: {
                    role: 'document'
                }
            });
        } else if (fileType === CMDBuildUI.view.dms.file.View.types.image) {
            view.add({
                xtype: 'image',
                alt: CMDBuildUI.locales.Locales.attachments.preview,
                localized: {
                    alt: 'CMDBuildUI.locales.Locales.attachments.preview'
                },
                src: view.getFileUrl(),
                style: {
                    maxWidth: '100%',
                    maxHeight: '100%'
                }
            });
        } else {
            this.onDownloadBtnClick();
        }
    },

    /**
     * 
     * @param {Ext.button.Button} btn 
     * @param {Ext.Event} event 
     * @param {Object} eOpts 
     */
    onDownloadBtnClick: function (btn, event, eOpts) {
        const view = this.getView();
        CMDBuildUI.util.File.download(
            view.getFileUrl(),
            view.getFileName(),
            undefined,
            undefined,
            {
                skipUrlEncode: true
            }
        ).then(
            function () {
                view.closePopup();
            },
            function () { },
            Ext.emptyFn,
            this);
    }
});