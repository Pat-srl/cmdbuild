Ext.define('CMDBuildUI.view.relations.fieldset.Grid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.relations-fieldset-grid',
    mixins: ['CMDBuildUI.mixins.grids.Grid'],

    forceFit: true,
    loadMask: true,

    bind: {
        store: '{records}'
    },

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    viewConfig: {
        markDirty: false
    },

    loadMask: true,

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.relations.addrelations,
            itemId: 'addrelationbtn',
            ui: 'management-primary-small',
            hidden: true,
            disabled: true,
            bind: {
                disabled: '{addrelationbtn.disabled}',
                hidden: '{addrelationbtn.hidden}'
            },
            autoEl: {
                'data-testid': 'relations-fieldset-addrelationbtn'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.relations.addrelations'
            }
        }
    ]
});
