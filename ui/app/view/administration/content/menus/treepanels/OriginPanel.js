Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.OriginPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.menus.treepanels.OriginPanelController',
        'CMDBuildUI.view.administration.content.menus.treepanels.OriginPanelModel'
    ],

    alias: 'widget.administration-content-menus-treepanels-originpanel',
    controller: 'administration-content-menus-treepanels-originpanel',
    viewModel: {
        type: 'administration-content-menus-treepanels-originpanel'
    },

    itemId: 'treepanelorigin',
    flex: 1,
    cls: 'tree-noborder',
    ui: 'administration-navigation-tree',
    reference: 'menuTreeViewOrigin',
    align: 'start',
    scrollable: true,
    useArrows: true,
    expanded: true,

    bind: {
        store: '{originStore}'
    },

    rootVisible: false,
    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 0.8,
        align: 'start',
        /**
         * @param {String} value
         * @param {Object} metaData
         * @param {Ext.data.Model} record
         * @param {Number} rowIndex
         * @param {Number} colIndex
         * @param {Ext.data.Store} store
         * @param {Ext.view.View} view
         * 
         * @returns {String}
         */
        renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
            metaData.align = 'left';
            if (!record.get('objectDescription')) {
                return record.get('_actualDescription');
            }
            return record.get('objectDescription');
        },
        editor: {
            xtype: 'textfield',
            allowBlank: false
        }
    }],

    viewConfig: {
        plugins: {
            id: 'treeviewdragdroporigin',
            ptype: 'treeviewdragdrop',
            ddGroup: 'TreeDD',
            nodeHighlightOnRepair: false,
            appendOnly: false,
            sortOnDrop: false,
            containerScroll: true,
            allowContainerDrops: true,
            dragZone: {
                animRepair: false
            }
        }
    },
    listeners: {
        beforedrop: 'onBeforeDrop'
    }
});