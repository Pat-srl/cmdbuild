
Ext.define('CMDBuildUI.view.dms.file.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.dms.file.ViewController'
    ],

    statics: {
        types: {
            image: 'image',
            other: 'other',
            pdf: 'pdf',
            text: 'text'
        }
    },

    alias: 'widget.dms-file-view',
    controller: 'dms-file-view',

    config: {
        /**
         * @cfg {String} fileUrl
         */
        fileUrl: null,

        /**
         * @cfg {String} fileType
         */
        fileType: null,

        /**
         * @cfg {String} fileName
         */
        fileName: null
    },

    tbar: [{
        xtype: 'tbfill'
    }, {
        xtype: 'button',
        ui: 'management-primary',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
        itemId: 'downloadbtn',
        tooltip: CMDBuildUI.locales.Locales.attachments.download,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.attachments.download'
        }
    }],

    scrollable: true,
    bodyStyle: {
        textAlign: 'center'
    }

});
