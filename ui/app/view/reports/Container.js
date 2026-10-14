Ext.define('CMDBuildUI.view.reports.Container', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.reports.ContainerController', 'CMDBuildUI.view.reports.ContainerModel'],

    alias: 'widget.reports-container',
    controller: 'reports-container',
    viewModel: {
        type: 'reports-container'
    },

    cls: 'reports-container',

    config: {
        /**
         * @cfg {String} objectTypeName
         * The report name.
         */
        objectTypeName: null,

        /**
         * @cfg {String} extension
         */
        extension: null
    },

    /**
     * @cf {Boolean} hideTitle
     * `true` to hide title bar.
     */
    hideTitle: false,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    publish: ['objectTypeName', 'extension'],

    bind: {
        objectTypeName: '{objectTypeName}',
        extension: '{extension}'
    },

    tbar: [
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            itemId: 'refreshbtn',
            tooltip: CMDBuildUI.locales.Locales.reports.reload,
            disabled: false,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.reports.reload'
            },
            bind: {
                disabled: '{refreshbtn.disabled}'
            }
        },
        {
            xtype: 'button',
            ui: 'management-neutral-action',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
            itemId: 'downloadbtn',
            tooltip: CMDBuildUI.locales.Locales.reports.download,
            disabled: true,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.reports.download'
            },
            bind: {
                disabled: '{!downloadbtn.href}'
            }
        }
    ],

    items: [
        {
            xtype: 'container',
            itemId: 'messagesbox'
        },
        {
            xtype: 'uxiframe',
            width: '100%',
            height: '100%',
            flex: 1,
            reference: 'reportiframe',
            ariaAttributes: {
                role: 'document'
            }
        }
    ]
});
