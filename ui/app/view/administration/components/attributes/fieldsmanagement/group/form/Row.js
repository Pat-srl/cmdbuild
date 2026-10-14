Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.Row', {
    extend: 'Ext.panel.Panel',
    requires: [
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.RowController',
        'CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.RowModel'
    ],
    alias: 'widget.administration-components-attributes-fieldsmanagement-group-form-row',
    controller: 'administration-components-attributes-fieldsmanagement-group-form-row',
    viewModel: {
        type: 'administration-components-attributes-fieldsmanagement-group-form-row'
    },
    config: {
        columns: []
    },

    layout: {
        type: 'hbox',
        align: 'stretch'
    },

    userCls: 'cmdbuild-fieldsmanagements-row',
    items: [],

    addColumn: function (group, column, colIndex, rowIndex) {

        return {
            xtype: 'administration-components-attributes-fieldsmanagement-group-form-column',
            flex: column.width || 1,
            colIndex: colIndex,
            rowIndex: rowIndex,
            autoEl: {
                'data-testid': Ext.String.format('administration-fieldsmanagement-row-{0}-column-{1}', rowIndex, colIndex)
            },
            viewConfig: {
                getRowClass: function (record, rowIndex, rowParams, store) {
                    return '';
                },
                rowLines: false,
                overItemCls: 'null',
                headerBorders: false,
                header: false
            },
            store: Ext.create('Ext.data.Store', {
                fields: ['attribute'],
                proxy: {
                    type: 'memory'
                },
                data: column && column.fields ? column.fields : [],
                autoDestroy: true,
                autoLoad: false
            })
        };
    }

});
