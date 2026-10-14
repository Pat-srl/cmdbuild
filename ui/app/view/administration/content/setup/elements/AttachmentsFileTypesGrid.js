
Ext.define('CMDBuildUI.view.administration.content.setup.elements.AttachmentsFileTypesGrid', {
    extend: 'CMDBuildUI.components.grid.reorder.ReorderGrid',
    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.AttachmentsFileTypesGridController',
        'CMDBuildUI.view.administration.content.setup.elements.AttachmentsFileTypesGridModel'
    ],

    alias: 'widget.administration-content-setup-elements-attachmentsfiletypesgrid',
    controller: 'administration-content-setup-elements-attachmentsfiletypesgrid',
    viewModel: {
        type: 'administration-content-setup-elements-attachmentsfiletypesgrid'
    },
    forceFit: true,
    bind: {
        store: '{attachmentsFilesTypesStore}'
    },
    config: {
        formMode: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit
    },
    variableRowHeight: true,
    plugins: [],
    viewConfig: {
        markDirty: false,
        variableRowHeight: true,
        getRowClass: function (record, rowIndex, rowParams, store) {
            return '';
        },
        rowLines: true,
        overItemCls: 'null',
        focusCls: 'null',
        headerBorders: false,
        header: false
        // navigationModel: {}
    },
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: [{
            itemId: 'addrowBtn',
            text: CMDBuildUI.locales.Locales.administration.forms.addrow,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.forms.addrow'
            },
            autoEl: {
                'data-testid': 'administration-fieldsmanagement-addRowBtn'
            },
            ui: 'administration-action-small',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid')
        }]
    }],

    saveDefinitions: function (cb) {
        if (this.getFormMode() === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit) {
            var store = this.getStore();
            store.each(function (item) {
                // trim item.get('mimeTypes') items
                // trim item.get('extensions') items
                item.sanitize();
            });
            // TODO: save the data and call a callback
            if (Ext.isFunction(cb)) {
                cb();
            }
        }
    }
});
