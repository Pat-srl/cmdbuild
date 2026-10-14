Ext.define('CMDBuildUI.view.joinviews.configuration.items.ContextMenusFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-items-contextmenusfieldset',

    data: {
        contextMenuCount: 0
    },

    formulas: {
        manageContextMenuData: {
            bind: '{theView}',
            get: function (theView) {
                const cleanRecord = Ext.create('CMDBuildUI.model.ContextMenuItem');

                this.set(
                    'contextMenuItemsStoreNewData',
                    theView ? [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)] : []
                );

                this.set(
                    'contextMenuItemData',
                    theView && theView.contextMenuItems() ? theView.contextMenuItems() : []
                );

                this.set('contextMenuCount', theView.contextMenuItems().getCount());
            }
        },

        contexMenuTypes: {
            get: function () {
                return CMDBuildUI.model.ContextMenuItem.getTypes();
            }
        },

        contextMenuApplicabilities: {
            get: function () {
                return CMDBuildUI.model.ContextMenuItem.getVisibilities();
            }
        }
    },

    stores: {
        contextMenuItemsStoreNew: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemsStoreNewData}',
            autoDestroy: true
        },

        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus',
            pageSize: 0
        },

        contextMenuItemsStore: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemData}',
            autoDestroy: true
        },

        contextMenuItemTypeStore: {
            autoLoad: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{contexMenuTypes}'
        },

        contextMenuApplicabilityStore: {
            type: 'common-applicability',
            data: '{contextMenuApplicabilities}'
        }
    }
});
