Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.DestinationPanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.gismenus.treepanels.DestinationPanelController',
        'CMDBuildUI.view.administration.content.gis.gismenus.treepanels.DestinationPanelModel'
    ],

    alias: 'widget.administration-content-gismenus-treepanels-destinationpanel',
    controller: 'administration-content-gismenus-treepanels-destinationpanel',
    viewModel: {
        type: 'administration-content-gismenus-treepanels-destinationpanel'
    },

    itemId: 'treepaneldestination',
    margin: '0 15 0 0',
    cls: 'tree-noborder',
    ui: 'administration-navigation-tree',
    reference: 'menuTreeViewDestination',
    align: 'start',
    useArrows: true,
    scrollable: 'y',
    store: Ext.create('Ext.data.TreeStore', {
        model: 'CMDBuildUI.model.gis.GisMenuItem',
        storeId: 'menuDestinationTreeStore',
        reference: 'menuDestinationTreeStore',
        root: {
            text: 'Root',
            expanded: true
        },
        rootVisible: false,
        proxy: {
            type: 'memory'
        },
        sorters: [{
            property: 'index',
            direction: 'ASC'
        }],
        autoLoad: true
    }),


    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 1,
        align: 'start'
    }, {
        xtype: 'actioncolumn',
        width: '60',
        flex: 1,
        hidden: true,
        items: [{
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
            handler: 'onTranslateClick',
            getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                return CMDBuildUI.locales.Locales.administration.common.tooltips.localize;
            },
            getClass: function (v, meta, rec) { // Or return a class from a function
                if (rec.get('root')) {
                    return '';
                }
                return CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid');
            },
            isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                return record.get('root') || 'geoattribute' === record.get('menutype');
            }
        }],
        bind: {
            hidden: '{!actions.edit}'
        }
    }],

    viewConfig: {
        plugins: {
            id: 'treeviewdragdropdestination',
            ptype: 'treeviewdragdrop',
            ddGroup: 'TreeDD',
            nodeHighlightOnRepair: false,
            appendOnly: false,
            sortOnDrop: true,
            containerScroll: true,
            allowContainerDrops: true
        }
    },
    listeners: {
        beforedrop: 'onBeforeDrop'
    },
    viewready: function (tree) {
        var view = tree.getView(),
            dd = view.findPlugin('treeviewdragdrop');
        dd.dragZone.onBeforeDrag = function (data, e) {
            var record = view.getRecord(e.getTarget(view.itemSelector));
            return record.isLeaf();
        };
    }
});