Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.importexport.gatetemplates.TopbarController'
    ],

    alias: 'widget.administration-content-importexport-gatetemplates-topbar',
    controller: 'administration-content-importexport-gatetemplates-topbar',
    viewModel: {},

    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'top',
        itemId: 'dockedTop',
        items: [{
            xtype: 'button',
            ui: 'administration-action-small',
            reference: 'addgate',
            itemId: 'addgate',
            autoEl: {
                'data-testid': 'administration-user-toolbar-addGisTemplateBtn'
            },
            bind: {
                text: '{addBtnText}',
                hidden: '{!addBtnText}',
                disabled: '{!toolAction._canAdd}'
            }
        }]
    }]
});