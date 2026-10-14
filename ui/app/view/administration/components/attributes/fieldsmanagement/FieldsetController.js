Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-fieldsmanagement-fieldset',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },

    onBeforeRender: function (view) {
        // get the data
        var group = view.getGroup();

        // set the title of fieldset
        view.setTitle(group.get('description'));

        // assigned attributes column
        view.add({
            xtype: 'administration-components-attributes-fieldsmanagement-group-group',
            itemId: 'group',
            group: group,
            region: 'center',
            flex: 0.75,
            border: 1

        });

        // free attributes column
        view.add({
            xtype: 'container',
            flex: 0.25,
            border: 1,
            region: 'east',
            layout: {
                type: 'fit'
            },
            style: 'border-left: 1px solid gray;height: 100%',
            items: [{
                xtype: 'administration-components-attributes-fieldsmanagement-group-attributes',
                isAllAttributes: true,
                itemId: 'freeattributes',
                group: group,
                style: {
                    height: '100%'
                }
            }]
        });
    },

    onAfterRender: function (view) {
        view.el.dom.dataset.testid = Ext.String.format('administration-fieldsmanagement-fieldset-{0}', view.getGroup().get('_id'));
    }
});
