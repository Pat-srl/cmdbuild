
Ext.define('CMDBuildUI.view.bim.xeokit.tab.Layers', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.bim.xeokit.tab.LayersController'
    ],

    mixins: [
        'CMDBuildUI.view.bim.xeokit.Mixin'
    ],

    alias: 'widget.bim-xeokit-tab-layers',
    controller: 'bim-xeokit-tab-layers',

    bind: {
        store: '{layersStore}'
    },

    dockedItems: {
        xtype: 'toolbar',
        dock: 'top',
        items: [{
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            cls: 'management-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('eye', 'regular'),
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.showAllLayers,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.showAllLayers'
            },
            callback: function (owner, tool, event) {
                var scene = owner.up().getContainer().getViewer().scene;
                scene.setObjectsVisible(scene.objectIds, true);
                Ext.Array.forEach(owner.up().getStore().getRange(), function (item, index, allitems) {
                    item.set("visible", true);
                });
            }
        }, {
            xtype: 'tool',
            cls: 'management-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('eye-slash', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.bim.layers.menu.hideAllLayers,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.bim.layers.menu.hideAllLayers'
            },
            callback: function (owner, tool, event) {
                var scene = owner.up().getContainer().getViewer().scene;
                scene.setObjectsVisible(scene.visibleObjectIds, false);
                Ext.Array.forEach(owner.up().getStore().getRange(), function (item, index, allitems) {
                    item.set("visible", false);
                });
            }
        }, {
            xtype: 'tbspacer',
            width: 10
        }]
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.bim.layers.name,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.name'
        },
        hideable: false,
        dataIndex: 'text',
        flex: 0.55
    }, {
        text: CMDBuildUI.locales.Locales.bim.layers.quantity,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.quantity'
        },
        hideable: false,
        dataIndex: 'length',
        align: 'center',
        flex: 0.25
    }, {
        xtype: 'actioncolumn',
        text: CMDBuildUI.locales.Locales.bim.layers.visibility,
        localized: {
            text: 'CMDBuildUI.locales.Locales.bim.layers.visibility'
        },
        menuDisabled: false,
        hideable: false,
        align: 'center',
        flex: 0.2,
        getClass: function (value, metadata, record, rowIndex, colIndex, store) {
            return record.get("visible") ? CMDBuildUI.util.helper.IconHelper.getIconId('eye', 'regular') : CMDBuildUI.util.helper.IconHelper.getIconId('eye-slash', 'solid');
        },
        handler: function (view, rowIndex, colIndex, item, e, record) {
            var scene = view.up().getContainer().getViewer().scene,
                entities = Ext.Array.pluck(record.data.children, "entityId"),
                visible = record.get("visible");
            scene.setObjectsVisible(entities, !visible);
            record.set("visible", !visible);
        },
        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
            var entityType = this.getView().ownerCt.cleanEntityType(record.get("entityType")),
                text = record.get("visible") ? CMDBuildUI.locales.Locales.bim.layers.menu.hideAllXeokit : CMDBuildUI.locales.Locales.bim.layers.menu.showAllXeokit;
            return Ext.String.format(text, entityType);
        }
    }]
});
