Ext.define('CMDBuildUI.view.bim.xeokit.tab.ObjectsTree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeController',
        'CMDBuildUI.view.bim.xeokit.tab.ObjectsTreeModel'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-tab-objectstree',
    controller: 'bim-xeokit-tab-objectstree',
    viewModel: {
        type: 'bim-xeokit-tab-objectstree'
    },

    bind: {
        store: '{objectsTreeStore}'
    },

    rootVisible: false,
    singleExpand: true,
    hideHeaders: true,

    checkPropagation: 'down',

    layout: 'fit',

    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1
    }, {
        xtype: 'actioncolumn',
        width: 30,
        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
            return CMDBuildUI.util.helper.IconHelper.getIconId('arrow-right', 'solid');
        },
        handler: function (view, rowIndex, colIndex, item, e, record) {
            view.up().fireEvent("selectelementtree", view, record);
        },
        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
            if (record.get("leaf")) {
                return CMDBuildUI.locales.Locales.bim.tree.arrowTooltip;
            } else {
                return CMDBuildUI.locales.Locales.bim.tree.multipleElements;
            }
        }
    }]

});