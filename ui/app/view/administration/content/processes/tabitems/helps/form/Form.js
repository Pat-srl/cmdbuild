Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.form.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.helps.form.FormController',
        'CMDBuildUI.view.administration.content.processes.tabitems.helps.form.FormModel'
    ],

    alias: 'widget.administration-content-processes-tabitems-helps-form-form',
    controller: 'administration-content-processes-tabitems-helps-form-form',
    viewModel: {
        type: 'administration-content-processes-tabitems-helps-form-form'
    },
    layout: 'fit',
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'fieldset',
        collapsible: true,
        layout: 'card',
        title: CMDBuildUI.locales.Locales.administration.processes.helps.defaultvalue,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.processes.helps.defaultvalue'
        },
        ui: 'administration-formpagination',
        items: {
            xtype: 'fieldcontainer',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.helptext,
            layout: 'card',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.helptext'
            },
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
            labelToolIconClick: 'onTranslateClick',
            items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                    bind: {
                        value: '{help}',
                        hidden: '{actions.view}'
                    }
                }),
                {
                    // view
                    xtype: 'displayfield',
                    name: 'defaultexporttemplate',
                    hidden: true,
                    bind: {
                        value: '{help}',
                        hidden: '{!actions.view}'
                    }
                }
            ]
        }
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }],

    addMask: function () {
        if (!this.getMaskCount()) {
            CMDBuildUI.util.Utilities.showLoader(true, this);
        }
        this.setMaskCount(this.getMaskCount() + 1);
    },

    removeMask: function () {
        this.setMaskCount(this.getMaskCount() - 1);
        if (!this.getMaskCount()) {
            CMDBuildUI.util.Utilities.showLoader(false, this);
        }
    }
});