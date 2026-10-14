
Ext.define('CMDBuildUI.view.classes.cards.grid.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.grid.GridController',
        'CMDBuildUI.view.classes.cards.grid.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget',
        'CMDBuildUI.view.classes.cards.card.View',
        'CMDBuildUI.util.helper.SessionHelper'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],

    alias: 'widget.classes-cards-grid-grid',
    controller: 'classes-cards-grid-grid',
    viewModel: {
        type: 'classes-cards-grid-grid'
    },
    rowViewModel: {
        type: 'classes-cards-tabpanel'
    },

    config: {
        /**
         * @cfg {Boolean} maingrid
         * 
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    bind: {
        store: '{cards}',
        selection: '{selection}'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    viewConfig: {
        markDirty: false
    },

    plugins: [
        'gridfilters', {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            id: 'forminrowwidget',
            expandOnDblClick: false,
            removeWidgetOnCollapse: true,
            widget: CMDBuildUI.util.helper.GridHelper.getFormInRowWidget(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.klass
            )
        }
    ],

    features: [{
        ftype: 'bufferedsselectall'
    }],

    autoEl: {
        'data-testid': 'cards-grid-grid'
    },

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * 
     * @returns {Ext.panel.Panel}
     */
    getGridContainer: function () {
        return this.up("classes-cards-grid-container");
    }
});
