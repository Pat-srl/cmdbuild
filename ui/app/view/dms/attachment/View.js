
Ext.define('CMDBuildUI.view.dms.attachment.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.dms.Util',
        'CMDBuildUI.view.dms.attachment.ViewController',
        'CMDBuildUI.view.dms.attachment.ViewModel'
    ],
    mixins: [
        'CMDBuildUI.view.dms.attachment.Mixin'
    ],

    alias: 'widget.dms-attachment-view',
    controller: 'dms-attachment-view',
    viewModel: {
        type: 'dms-attachment-view'
    },

    items: [{
        xtype: 'form',
        itemId: 'formpanel'
    }],

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.read,
    scrollable: 'y'

});
