
Ext.define('CMDBuildUI.view.dms.attachment.Edit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.dms.Util',
        'CMDBuildUI.view.dms.attachment.EditController',
        'CMDBuildUI.view.dms.attachment.EditModel'
    ],
    mixins: [
        'CMDBuildUI.view.dms.attachment.Mixin'
    ],

    alias: 'widget.dms-attachment-edit',
    controller: 'dms-attachment-edit',
    viewModel: {
        type: 'dms-attachment-edit'
    },

    formmode: CMDBuildUI.util.helper.FormHelper.formmodes.update,
    modelValidation: true,

    items: [{
        xtype: 'toolbar',
        cls: 'fieldset-toolbar',
        items: [{
            xtype: 'tbfill'
        }, CMDBuildUI.view.dms.Util.getHelpTool()]
    }, {
        xtype: 'form',
        itemId: 'formpanel'
    }],

    buttons: [{
        text: CMDBuildUI.locales.Locales.common.actions.cancel,
        ui: 'secondary-action-small',
        autoEl: {
            'data-testid': 'dms-edit-cancel'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
        },
        handler: 'onCancelButton'
    }, {
        text: CMDBuildUI.locales.Locales.common.actions.save,
        formBind: true, //only enabled once the form is valid
        disabled: true,
        ui: 'management-primary-small',
        autoEl: {
            'data-testid': 'dms-edit-save'
        },
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.save'
        },
        handler: 'onSaveButton'
    }],

    scrollable: true,

    closePanel: function () {
        var panel = this.up("panel");

        panel.fireEvent('popupcancel');
        panel.close();
    }

});
