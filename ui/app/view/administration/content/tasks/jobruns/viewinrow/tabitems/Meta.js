Ext.define('CMDBuildUI.view.administration.content.tasks.jobruns.viewinrow.tabitems.Meta', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-tasks-jobruns-viewinrow-tabitems-meta',
    ui: 'administration-formpagination',

    bind: {
        store: '{metaStore}'
    },

    headerBorders: false,
    border: false,
    bodyBorder: false,
    rowLines: false,
    sealedColumns: false,
    sortableColumns: false,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    viewConfig: {
        markDirty: false
    },

    columnWidth: 1,
    autoEl: {
        'data-testid': 'administration-content-tasks-descriptors-params-grid'
    },

    forceFit: true,
    loadMask: true,

    labelWidth: "auto",

    columns: [{
        flex: 1,
        text: CMDBuildUI.locales.Locales.administration.bus.key,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.key'
        },
        dataIndex: 'key',
        align: 'left'
    }, {
        text: CMDBuildUI.locales.Locales.administration.bus.value,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.bus.value'
        },
        flex: 1,
        dataIndex: 'value',
        align: 'left'
    }]


});