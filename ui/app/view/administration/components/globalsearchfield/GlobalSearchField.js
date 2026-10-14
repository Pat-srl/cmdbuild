Ext.define('CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchField', {
    extend: 'Ext.form.field.Text',

    requires: [
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldController',
        'CMDBuildUI.view.administration.components.globalsearchfield.GlobalSearchFieldModel'
    ],

    alias: 'widget.admin-globalsearchfield',
    controller: 'administration-components-globalsearchfield-globalsearchfield',
    viewModel: {
        type: 'administration-components-globalsearchfield-globalsearchfield'
    },

    name: 'search',
    width: 350,
    cls: 'administration-input',
    reference: 'globalsearchtext',
    itemId: 'globalsearchtext',
    bind: {
        emptyText: '{emptyText}',
        hidden: '{!canFilter}'
    },

    config: {
        /**
         * @cfg {string} Mandatory es: view/class/process/domain...
         */
        objectType: null,
        subType: null
    },

    autoEl: {
        'data-testid': 'administration-grid-localsearchfield-input'
    }
});