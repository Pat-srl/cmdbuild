Ext.define("CMDBuildUI.components.grid.reorder.ReorderGrid", {
    extend: 'Ext.grid.Panel',
    alias: 'widget.components-grid-reorder-grid',
    scrollable: false,
    //layout: 'fit',
    sortable: true,


    headerBorders: false,
    border: false,
    bodyBorder: false,
    rowLines: false,
    sealedColumns: false,
    sortableColumns: false,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: false,

    cls: 'administration-reorder-grid',

    menuDisabled: true,
    stopSelect: true,

    showActionColumn: true,
    useDefaultAction: false,

    plugins: [{
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 0,
        listeners: {
            beforeedit: function (editor, context) {                            
                if (editor.view.lookupViewModel().get('actions.view')) {
                    return false;
                }
            }
        }
    }]

});