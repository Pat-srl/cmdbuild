
Ext.define('CMDBuildUI.view.mobile.config.Panel', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.mobile.config.PanelController',
        'CMDBuildUI.view.mobile.config.PanelModel'
    ],

    alias: 'widget.mobile-config-panel',
    controller: 'mobile-config-panel',
    viewModel: {
        type: 'mobile-config-panel'
    },

    bodyPadding: CMDBuildUI.util.helper.FormHelper.properties.padding,
    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    scrollable: true,

    items: [{
        xtype: 'fieldcontainer',
        layout: 'anchor',
        items: [{
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.mobile.config.serverurl,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.mobile.config.serverurl'
            },
            bind: {
                value: '{values.serverurl}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        layout: 'anchor',
        items: [{
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.mobile.config.customercode,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.mobile.config.customercode'
            },
            bind: {
                value: '{values.customercode}'
            }
        }]
    }, {
        xtype: 'fieldcontainer',
        layout: 'anchor',
        items: [{
            xtype: 'textfield',
            fieldLabel: CMDBuildUI.locales.Locales.mobile.config.devicename,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.mobile.config.devicename'
            },
            itemId: 'devicename',
            autoEl: {
                'data-testid': 'mobile-config-panel-devicename'
            },
            bind: {
                value: '{values.devicename}'
            }
        }]
    }, {
        xtype: 'container',
        layout: 'hbox',
        padding: '10 0 0 0',
        items: [{
            xtype: 'component',
            flex: 1
        }, {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.mobile.config.regenerate,
            itemId: 'regeneratebtn',
            ui: 'management-primary',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            disabled: true,
            localized: {
                text: 'CMDBuildUI.locales.Locales.mobile.config.regenerate'
            },
            autoEl: {
                'data-testid': 'mobile-config-panel-regeneratebtn'
            },
            bind: {
                disabled: '{regeneratebtn.disabled}'
            }
        }]
    }, {
        xtype: 'container',
        layout: 'hbox',
        padding: '10 0 0 0',
        items: [{
            xtype: 'component',
            itemId: 'qrcode',
            with: 200,
            height: 200,
            autoEl: {
                tag: 'canvas',
                'data-testid': 'mobile-config-panel-canvas'
            }
        }]
    }],

    fbar: [{
        xtype: 'button',
        ui: 'secondary-action',
        itemId: 'closebtn',
        text: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            text: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        autoEl: {
            'data-testid': 'mobile-config-panel-close'
        }
    }]
});
