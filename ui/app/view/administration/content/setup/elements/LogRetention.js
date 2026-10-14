Ext.define('CMDBuildUI.view.administration.content.setup.elements.LogRetention', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.LogRetentionController',
        'CMDBuildUI.view.administration.content.setup.elements.LogRetentionModel'
    ],

    alias: 'widget.administration-content-setup-elements-logretention',
    controller: 'administration-content-setup-elements-logretention',
    viewModel: {
        type: 'administration-content-setup-elements-logretention'
    },

    layout: 'column',
    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    statics: {
        modes: {
            custom: 'systemstatuslog,request,eventlog,jobrun,etlmessage'
        }
    },
    items: [{
        xtype: 'container',
        itemId: 'fieldsetscontainer',
        hidden: true,
        bind: {
            hidden: '{isDefault}'
        },
        layout: 'column',
        columnWidth: 1,
        items: []
    }],
    dockedItems: [{
        dock: 'top',
        xtype: 'components-administration-toolbars-formtoolbar',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
            edit: {
                disabled: true,
                bind: {
                    disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
                }
            }
        }, 'logretention', 'theSetup',
            [],
            [{
                xtype: 'tool',
                itemId: 'resetBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
                hidden: true,
                bind: {
                    hidden: '{actions.view}'
                },
                tooltip: CMDBuildUI.locales.Locales.administration.common.tooltips.reset,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.administration.common.tooltips.reset'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': 'administration-logretention-resetBtn'
                }
            }])
    }, {
        xtype: 'toolbar',
        itemId: 'bottomtoolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true, {
            testid: 'logretention'
        }, {
            testid: 'logretention'
        })
    }]
});