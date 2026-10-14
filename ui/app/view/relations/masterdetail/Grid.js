Ext.define('CMDBuildUI.view.relations.masterdetail.Grid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.relations-masterdetail-grid',

    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],

    layout: 'fit',

    forceFit: true,

    bind: {
        store: '{records}'
    },

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    viewConfig: {
        markDirty: false
    }

});