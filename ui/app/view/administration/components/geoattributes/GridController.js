Ext.define('CMDBuildUI.view.administration.components.geoattributes.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-grid',

    listen: {
        global: {
            geoattributeupdated: 'onGeoAttributeUpdated'
        }
    },

    control: {
        '#': {
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'
        },
        '#addattribute': {
            click: 'onAddClickBtn'
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.map.GeoAttribute} record
     */
    onGeoAttributeUpdated: function (record) {
        const view = this.getView();
        const plugin = view.getPlugin('administration-forminrowwidget');
        if (plugin) {
            plugin.view.fireEventArgs('itemupdated', [view, record, this]);
        }
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (row, record, index, eOpts) {
        this.view.setSelection(record);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onAddClickBtn: function (button, event, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();
        const objectTypeName = vm.get('objectTypeName');
        const visibility = {};
        visibility[objectTypeName] = true;
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                links: {
                    theGeoAttribute: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        create: {
                            owner_type: objectTypeName,
                            visibility: visibility
                        }
                    }
                },
                data: {
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    tabpanel: view.up('tabpanel')
                }
            }
        });
    },

    /**
     *
     * @param {Ext.view.Table} row
     * @param {Ext.data.Model} record
     * @param {HTMLElement} element
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        const vm = row.grid.getViewModel();
        const formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');

        formInRow.removeAllExpanded(record);
        row.setSelection(record);
        container.removeAll();

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                data: {
                    theGeoAttribute: record.clone(),
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    tabpanel: this.getView().up('tabpanel'),
                    geoattributesStore: vm.get('geoattributesStore')
                }
            }
        });
    }
});
