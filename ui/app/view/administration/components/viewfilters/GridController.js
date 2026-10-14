Ext.define('CMDBuildUI.view.administration.components.viewfilters.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-viewfilters-grid',
    control: {
        '#': {
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'
        },
        '#addBtn': {
            click: 'onAddClickBtn'
        }
    },

    /**
     * @param {Ext.selection.RowModel} row
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (row, record, index, eOpts) {

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

    onAddClickBtn: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: vm.get('viewfiltersStoreProxy'),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-viewfilters-card-form',
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        create: true
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: true
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = row.grid.getViewModel();
        
        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);
        
        container.removeAll();


        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: vm.get('viewfiltersStoreProxy'),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-viewfilters-card-form',
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: record.get('name')
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: row.grid
                }
            }
        });
    }
});