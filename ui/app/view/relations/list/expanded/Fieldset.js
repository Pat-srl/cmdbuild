
Ext.define('CMDBuildUI.view.relations.list.expanded.Fieldset', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.relations.list.expanded.FieldsetController',
        'CMDBuildUI.view.relations.list.expanded.FieldsetModel'
    ],

    alias: 'widget.relations-list-expanded-fieldset',
    controller: 'relations-list-expanded-fieldset',
    viewModel: {
        type: 'relations-list-expanded-fieldset'
    },

    bind: {
        title: '{fieldsetTitle}',
        collapsed: '{fieldsetCollapsed}',
        hidden: '{!recordsCount}'
    },

    collapsible: true,

    ui: 'groupingfieldset',

    hidden: true,

    calculateHeight: function () {
        var container = this.up('relations-list-container');
        var grid = this.down('grid');

        if (grid) {
            var rowHeight = 33;
            var store = this.getViewModel().get('records');
            var height = Math.min((store.getCount() * rowHeight) + 40, container.getHeight() * 0.8);
            grid.setHeight(height);
        }
    }
});
