Ext.define('CMDBuildUI.view.administration.content.setup.elements.SubscriptionController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-subscription',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        }
    },
    onBeforeRender: function (view) {
        view.up('administration-content')
            .getViewModel()
            .set('title', CMDBuildUI.locales.Locales.administration.navigation.subscription);
    },
    onAfterRender: function (view) {
        view.add(
            {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
                },
                defaults: {
                    layout: 'column',
                    columnWidth: 1,
                    defaults: {
                        columnWidth: 0.5
                    }
                },
                items: [
                    {
                        /*********************  org.cmdbuild.subscription.customercode **********************/
                        xtype: 'fieldcontainer',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.customercode,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.customercode'
                        },
                        margin: '0 15 0 0',
                        defaults: {
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__subscription__DOT__customer__DOT__code}'
                            }
                        },
                        userCls: 'with-tool-nomargin',
                        labelToolIconQtip: CMDBuildUI.util.administration.helper.RendererHelper.getCustomeCodeHelp(),
                        labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid'),
                        items: [
                            {
                                vtype: 'TTCustomerCode',
                                xtype: 'textfield',
                                hidden: true,
                                bind: {
                                    hidden: '{actions.view}'
                                }
                            },
                            {
                                xtype: 'displayfield',
                                hidden: true,
                                bind: {
                                    hidden: '{!actions.view}'
                                }
                            }
                        ]
                    },
                    {
                        xtype: 'customer-info'
                    }
                ]
            },
            {
                ui: 'administration-formpagination',
                xtype: 'fieldset',
                collapsible: true,
                title: CMDBuildUI.locales.Locales.administration.systemconfig.mobile,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.systemconfig.mobile'
                },
                defaults: {
                    layout: 'column',
                    columnWidth: 1,
                    defaults: {
                        columnWidth: 0.5
                    }
                },
                items: [
                    {
                        items: [
                            {
                                /*********************  org.cmdbuild.mobile.enabled **********************/
                                xtype: 'checkbox',
                                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.enabled,
                                localized: {
                                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.enabled'
                                },
                                name: 'active',
                                bind: {
                                    value: '{theSetup.org__DOT__cmdbuild__DOT__mobile__DOT__enabled}',
                                    readOnly: '{actions.view}'
                                }
                            }
                        ]
                    },
                    {
                        items: [
                            {
                                /*********************  org.cmdbuild.mobile.devicenameprefix **********************/
                                xtype: 'fieldcontainer',
                                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.devicenameprefix,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.systemconfig.devicenameprefix'
                                },
                                margin: '0 15 0 0',
                                labelToolIconQtip:
                                    CMDBuildUI.util.administration.helper.RendererHelper.getDeviceNamePrefixHelp(),
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                    'question-circle',
                                    'solid'
                                ),
                                defaults: {
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__mobile__DOT__devicename__DOT__prefix}'
                                    }
                                },
                                items: [
                                    {
                                        xtype: 'textfield',
                                        hidden: true,
                                        bind: {
                                            hidden: '{actions.view}'
                                        }
                                    },
                                    {
                                        xtype: 'displayfield',
                                        hidden: true,
                                        bind: {
                                            hidden: '{!actions.view}'
                                        }
                                    }
                                ]
                            }
                        ]
                    },
                    {
                        columnWidth: 1,
                        items: [
                            {
                                /*********************  org.cmdbuild.mobile.notification.authInfo **********************/
                                columnWidth: 1,
                                xtype: 'textarea',
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.systemconfig
                                        .notificationauthenticationinfo,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.systemconfig.notificationauthenticationinfo'
                                },
                                labelToolIconQtip:
                                    CMDBuildUI.util.administration.helper.RendererHelper.getMobileAuthenticationInfo(),
                                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                    'question-circle',
                                    'solid'
                                ),
                                readOnly: true,
                                bind: {
                                    readOnly: '{actions.view}',
                                    value: '{theSetup.org__DOT__cmdbuild__DOT__mobile__DOT__notification__DOT__authInfo}'
                                }
                            }
                        ]
                    }
                ]
            }
        );
    }
});
