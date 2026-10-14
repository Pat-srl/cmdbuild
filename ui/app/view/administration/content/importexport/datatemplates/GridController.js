Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.GridController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin'],
    alias: 'controller.administration-content-importexport-datatemplates-grid',

    control: {
        '#': {
            sortchange: 'onSortChange',      
            deselect: 'onDeselect',
            select: 'onSelect',
            rowdblclick: 'onRowDblclick'
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
    },

    onSortChange: function () {
        var currentSelected = this.view.getSelection() && this.view.getSelection()[0];
        this.view.getPlugin('administration-forminrowwidget').removeAllExpanded();

        if (currentSelected) {
            var store = this.view.getStore();
            var index = store.findExact("_id", currentSelected.get('_id'));
            var record = store.getAt(index);
            this.view.getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [this.getView(), record, index]);
        }
    }

});