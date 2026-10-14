Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.layers.Layers', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.classes.tabitems.layers.LayersController',
        'CMDBuildUI.view.administration.content.classes.tabitems.layers.LayersModel'
    ],

    alias: 'widget.administration-content-classes-tabitems-layers-layers',
    controller: 'administration-content-classes-tabitems-layers-layers',
    viewModel: {
        type: 'administration-content-classes-tabitems-layers-layers'
    },

    autoEl: {
        'data-testid': 'administration-content-classes-tabitems-layers'
    },
    tbar: [
        {
            xtype: 'button',
            itemId: 'spacer',
            style: {
                visibility: 'hidden'
            }
        },
        {
            // move all buttons on right side
            xtype: 'tbfill'
        },
        {
            xtype: 'tool',
            align: 'right',
            itemId: 'editBtn',
            cls: 'administration-tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditBtnClick',
            hidden: true,
            autoEl: {
                'data-testid': 'administration-class-layers-tool-editbtn'
            },
            bind: {
                hidden: '{actions.edit}',
                disabled: '{!toolAction._canUpdate}'
            }
        }
    ],
    forceFit: true,
    loadMask: true,
    bind: {
        store: '{layersStore}'
    },
    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.referenceclass,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.referenceclass'
            },
            dataIndex: 'owner_type',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.type,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
            },
            dataIndex: 'type',
            align: 'left',
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return CMDBuildUI.util.administration.helper.RendererHelper.getGeoatributeTypeAndSubype(record);
            },
            sorter: '_type_description'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.minzoom'
            },
            dataIndex: 'zoomMin',
            align: 'right'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.maxzoom'
            },
            dataIndex: 'zoomMax',
            align: 'right'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defzoom'
            },
            dataIndex: 'zoomDef',
            align: 'right'
        },
        {
            xtype: 'actioncolumn',
            text: CMDBuildUI.locales.Locales.administration.common.labels.show,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.show'
            },
            align: 'center',
            handler: function (grid, rowIndex, colIndex, item, e, record) {
                var vm = grid.lookupViewModel();
                var visibility = record.get('visibility');
                const objectTypeName = vm.get('objectTypeName');
                if (!vm.get('actions.edit') || record.get('owner_type') === objectTypeName) {
                    return;
                }
                if (!(objectTypeName in visibility)) {
                    visibility[objectTypeName] = true;
                } else {
                    delete visibility[objectTypeName];
                }
                record.set('visibility', visibility, {
                    commit: true
                });
            },
            isActionDisabled: function (grid, rowIndex, colIndex, item, record) {
                var vm = grid.lookupViewModel();
                if (!vm.get('actions.edit') || record.get('owner_type') === vm.get('objectTypeName')) {
                    return true;
                }
                return false;
            },
            getClass: function (value, metadata, record, rowIndex, colIndex, store, grid) {
                const visibility = record.get('visibility');
                const objectTypeName = grid.lookupViewModel().get('objectTypeName');
                return objectTypeName in visibility
                    ? CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular')
                    : CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular');
            }
        },
        {
            xtype: 'actioncolumn',
            text: CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defaultactive,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.geoattributes.fieldLabels.defaultactive'
            },
            align: 'center',
            handler: function (grid, rowIndex, colIndex, item, e, record) {
                var vm = grid.lookupViewModel();
                if (record.get('owner_type') === vm.get('objectTypeName')) {
                    return;
                }
                var visibility = record.get('visibility');
                const objectTypeName = vm.get('objectTypeName');
                if (objectTypeName in visibility) {
                    if (visibility[objectTypeName]) {
                        visibility[objectTypeName] = false;
                    } else {
                        visibility[objectTypeName] = true;
                    }
                }
                record.set('visibility', visibility, {
                    commit: true
                });
            },
            isActionDisabled: function (grid, rowIndex, colIndex, item, record) {
                var vm = grid.lookupViewModel();
                var visibility = record.get('visibility');
                const objectTypeName = vm.get('objectTypeName');
                if (
                    !vm.get('actions.edit') ||
                    record.get('owner_type') === objectTypeName ||
                    !(objectTypeName in visibility)
                ) {
                    return true;
                }
                return false;
            },
            getClass: function (value, metadata, record, rowIndex, colIndex, store, grid) {
                const visibility = record.get('visibility');
                const objectTypeName = grid.lookupViewModel().get('objectTypeName');
                return objectTypeName in visibility && visibility[objectTypeName]
                    ? CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular')
                    : CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular');
            }
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
        }
    ]
});
