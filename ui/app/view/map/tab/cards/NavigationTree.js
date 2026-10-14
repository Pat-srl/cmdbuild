Ext.define('CMDBuildUI.view.map.tab.cards.NavigationTree', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.map.tab.cards.NavigationTreeController',
        'CMDBuildUI.view.map.tab.cards.NavigationTreeModel',

        'CMDBuildUI.store.map.Tree'
    ],

    mixins: [
        'CMDBuildUI.view.map.Mixing'
    ],

    alias: 'widget.map-tab-cards-navigationtree',
    controller: 'map-tab-cards-navigationtree',
    viewModel: {
        type: 'map-tab-cards-navigationtree'
    },

    statics: {
        root_id_composed: 'root_id_composed',
        root_id: 'root_id'
    },

    config: {
        /**
         * Hash map used only for saving tree nodes and them by id quickly. 
         * Is populated in loadChildren function
         */
        nodeHashMap: undefined
    },

    bind: {
        store: '{navigationTreeStore}'
    },

    cls: 'navigationTreeCls',

    selModel: {
        type: 'treemodel',
        mode: 'MULTI'
    },

    layout: 'fit',
    hideHeaders: true,

    columns: [{
        xtype: 'treecolumn',
        dataIndex: 'text',
        flex: 20
    }, {
        xtype: 'actioncolumn',
        width: '100',
        handler: 'onActionColumn',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('arrow-circle-right', 'solid') + ' NavigationTree',
        flex: 1
    }],

    /**
     * 
     */
    initComponent: function () {
        // this.setNodeHashMap(Ext.util.HashMap.create());
        this.setNodeHashMap(new Ext.util.Collection({
            keyFn: function (item) {
                return item.get('_id_composed');
            },
            grouper: {
                groupFn: function (item) {
                    return item.get('_id');
                }
            }
        }));

        this.callParent(arguments);
    }

});
