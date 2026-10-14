Ext.define('CMDBuildUI.view.administration.content.setup.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.ViewController',
        'CMDBuildUI.view.administration.content.setup.ViewModel'
    ],
    alias: 'widget.administration-content-setup-view',
    controller: 'administration-content-setup-view',
    viewModel: {
        type: 'administration-content-setup-view'
    },


    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    layout: 'border',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        region: 'north',
        borderBottom: 1,
        items: [{

            xtype: 'button',
            itemId: 'spacer',
            style: {
                "visibility": "hidden"
            }
        }, {
            xtype: 'tbfill'
        }, {
            xtype: 'tool',
            itemId: 'editAttributeBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
            },
            callback: 'onEditSetupBtnClick',
            cls: 'administration-tool',
            hidden: true,
            bind: {
                hidden: '{isEditBtnHidden}',
                disabled: "{!toolAction._canUpdate}"

            },
            autoEl: {
                'data-testid': 'administration-setup-view-editBtn'
            }
        }]
    }, {
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        items: []
    }],

    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
    }]
});