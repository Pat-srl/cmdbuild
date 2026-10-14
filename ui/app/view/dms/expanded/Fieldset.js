
Ext.define('CMDBuildUI.view.dms.expanded.Fieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.dms.expanded.FieldsetController',
        'CMDBuildUI.view.dms.expanded.FieldsetModel'
    ],

    alias: 'widget.dms-expanded-fieldset',
    controller: 'dms-expanded-fieldset',
    viewModel: {
        type: 'dms-expanded-fieldset'
    },

    bind: {
        title: '{fieldsetTitle}',
        hidden: '{!recordsCount}'
    },

    ui: 'groupingfieldset',

    hidden: true
});