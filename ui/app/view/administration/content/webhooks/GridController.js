Ext.define('CMDBuildUI.view.administration.content.webhooks.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-webhooks-grid',

    control: {
        '#': {
            rowdblclick: 'onRowDblclick',
            deselect: 'onDeselect',
            select: 'onSelect'
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
        this.getView().setSelection(record);
        this.getView().lookupViewModel().set('selected', record);
    },

    /**
     * 
     * @param {Ext.view.Table} row 
     * @param {CMDBuildUI.model.webhooks.Webhook} record 
     * @param {HTMLElement} element 
     * @param {Number} rowIndex 
     * @param {Event} e 
     * @param {Object} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = row.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        row.setSelection(record);

        container.removeAll();
        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                links: {
                    theWebhook: {
                        type: 'CMDBuildUI.model.webhooks.Webhook',
                        id: record.get('_id')
                    }
                },
                data: {
                    grid: this.getView().ownerGrid,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
        // this.getView().ownerGrid.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, rowIndex]);
    }
});
