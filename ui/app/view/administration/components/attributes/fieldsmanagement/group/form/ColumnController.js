Ext.define('CMDBuildUI.view.administration.components.attributes.fieldsmanagement.group.form.ColumnController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-attributes-fieldsmanagement-group-form-column',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            drop: 'onDrop'
        }
    },
    onDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        var record = data.records[0];        
        record.set('attribute', record.get('descriptionName'));
    },
    onBeforeRender: function () {

    }
});
