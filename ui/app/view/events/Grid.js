
Ext.define('CMDBuildUI.view.events.Grid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.events-grid',

    requires: [
        'CMDBuildUI.view.events.GridController',
        'CMDBuildUI.view.events.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.Grid'
    ],

    controller: 'events-grid',
    viewModel: {
        type: 'events-grid'
    },

    rowViewModel: {
        type: 'events-tabpanel'
    },

    plugins: [
        'gridfilters', {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            expandOnDblClick: true,
            removeWidgetOnCollapse: true,
            widget:
                CMDBuildUI.util.helper.GridHelper.getFormInRowWidget(
                    CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar, {
                    showInPopup: false,
                    formmode: 'read',
                    readOnly: true,
                    padding: '0 10 8 0'
                })
        }
    ],

    selModel: {
        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
        selType: 'checkboxmodel',
        checkOnly: true,
        mode: 'SINGLE'
    },

    // ----- //
    /**
     * this tree configurations are needed to publish on the view model the variable events-grid.eventsdata
     */
    config: {
        /**
         * @cfg {Boolean} maingrid
         * 
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        /**
         * @cfg {Boolean}
         * Configuration used for the plugin
         */
        hideTools: false
    },

    viewConfig: {
        markDirty: false
    },

    forceFit: true,

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * 
     */
    initComponent: function () {
        const p = this.findPlugin('forminrowwidget');
        p.widget.hideTools = this.config.hideTools;
        this.callParent(arguments);
    }
});
