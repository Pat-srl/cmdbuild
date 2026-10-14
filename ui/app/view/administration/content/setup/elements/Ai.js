Ext.define('CMDBuildUI.view.administration.content.setup.elements.Ai', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-setup-elements-ai',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.AiController',
        'CMDBuildUI.view.administration.content.setup.elements.AiModel',
        'CMDBuildUI.util.administration.helper.RendererHelper'
    ],

    controller: 'administration-content-setup-elements-ai',
    viewModel: {
        type: 'administration-content-setup-elements-ai'
    },

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
            localized: { title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals' },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            // enable ai
                            xtype: 'fieldcontainer',
                            columnWidth: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },

                            items: [
                                {
                                    xtype: 'checkbox',
                                    name: 'enableAi',
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ai__DOT__enabled}',
                                        readOnly: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-systemconfig-generalproperties-enableai_input'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            // Provider
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            style: {
                                paddingRight: '10px'
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.ai.provider,
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.ai.providertooltip,
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid'),
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.ai.provider',
                                labelToolIconQtip: 'CMDBuildUI.locales.Locales.administration.ai.providertooltip'
                            },
                            items: [
                                {
                                    xtype: 'combobox',
                                    itemId: 'provider',
                                    // aiProviders is a chained store, this prevent from combobox to call the store load method, which is not needed since the store is already loaded
                                    queryMode: 'local',
                                    valueField: 'service',
                                    displayField: 'description',
                                    allowBlank: false,
                                    forceSelection: true,
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ai__DOT__service__DOT__type}',
                                        store: '{aiProviders}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-ai_service_type_input'
                                    }
                                },
                                {
                                    xtype: 'displayfieldwithtriggers',
                                    hidden: true,
                                    bind: {
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__ai__DOT__service__DOT__type}',
                                        hidden: '{!actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid':
                                            'administration-systemconfig-generalproperties-ai_service_type_display'
                                    },
                                    renderer: function (value) {
                                        if (value) {
                                            const record = this.lookupViewModel().get('providerRecord');
                                            return record ? record.get('description') : value;
                                        }
                                        return null;
                                    },
                                    triggers: {
                                        open: {
                                            cls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'external-link-alt',
                                                'solid'
                                            ),
                                            handler: function (f, trigger, eOpts) {
                                                const vm = this.lookupViewModel();
                                                const record = vm.get('providerRecord');

                                                if (!record) {
                                                    CMDBuildUI.util.Notifier.showErrorMessage(
                                                        CMDBuildUI.locales.Locales.administration.ai.providernotfound
                                                    );
                                                    return;
                                                }

                                                const url =
                                                    CMDBuildUI.util.administration.helper.ApiHelper.client.getPluginManagerUrl(
                                                        record.get('name')
                                                    );
                                                CMDBuildUI.util.Utilities.closeAllPopups();
                                                CMDBuildUI.util.Utilities.redirectTo(url);
                                            }
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
