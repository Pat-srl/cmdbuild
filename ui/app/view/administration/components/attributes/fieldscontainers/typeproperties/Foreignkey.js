Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Foreignkey', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-foreignkeyfields',

    items: [
        {
            xtype: 'container',
            items: [
                {
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        {
                            columnWidth: 0.5,
                            itemId: 'targetClass',
                            xtype: 'allelementscombo',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                            name: 'initialPage',
                            withClasses: true,
                            withProcesses: true,
                            allowBlank: false, // Change on CardModel with typeIsForeignKey formulas
                            hidden: true,
                            bind: {
                                value: '{theAttribute.targetClass}',
                                hidden: '{actions.view}',
                                disabled: '{actions.edit}'
                            }
                        },
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeAction', {
                            cascadeAction: {
                                layout: 'column',
                                columnWidth: 1,
                                userCls: 'with-tool-nomargin',
                                fieldcontainer: {
                                    columnWidth: 0.5,
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels
                                            .cascadeactioninverse,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cascadeactioninverse'
                                    },
                                    hidden: true,
                                    allowBlank: false,
                                    bind: {
                                        hidden: '{actions.view}'
                                    }
                                },
                                displayfield: {
                                    hidden: true
                                },
                                combofield: {
                                    hidden: true,
                                    bind: {
                                        store: '{cascadeActionsStore}',
                                        value: '{theAttribute.cascadeAction}',
                                        hidden: '{!types.isForeignkey}'
                                    },
                                    listeners: {
                                        hide: function (input, eOpts) {
                                            const vm = input.lookupViewModel();
                                            vm.set('theAttribute.cascadeAction', null);
                                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                input,
                                                true,
                                                input.up('form')
                                            );
                                        },
                                        show: function (input, eOpts) {
                                            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                input,
                                                false,
                                                input.up('form')
                                            );
                                        }
                                    }
                                },
                                triggers: {
                                    clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                }
                            }
                        })
                    ]
                }
            ]
        },
        {
            xtype: 'container',
            hidden: true,
            bind: {
                hidden: '{actions.view || targetClassIsProcess || !theAttribute.targetClass}'
            },
            items: [
                {
                    layout: 'column',
                    bind: {
                        hidden: Ext.String.format(
                            '{objectType !== "{0}"}',
                            CMDBuildUI.util.helper.ModelHelper.systemClasses.class
                        )
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.ismasterdetail,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.ismasterdetail'
                            },
                            name: 'isMasterDetail',
                            bind: {
                                value: '{theAttribute.isMasterDetail}'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'textfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptionmasterdetail,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptionmasterdetail'
                            },
                            name: 'masterDetailDescription',
                            bind: {
                                value: '{theAttribute.masterDetailDescription}'
                            },
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateClickMasterDetail'
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldcontainer',
            hidden: true,
            bind: {
                hidden: '{!actions.view || targetClassIsProcess || !theAttribute.targetClass}'
            },
            items: [
                {
                    layout: 'column',
                    columnWidth: 1,
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                            name: 'targetClass',
                            withClasses: true,
                            withProcesses: true,
                            onlyDisplay: true,
                            bind: {
                                value: '{theAttribute.targetClassDescription}',
                                disabled: '{actions.edit}'
                            }
                        },
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('cascadeAction', {
                            cascadeAction: {
                                layout: 'column',
                                columnWidth: 1,
                                userCls: 'with-tool-nomargin',
                                fieldcontainer: {
                                    columnWidth: 0.5,
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels
                                            .cascadeactioninverse,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cascadeactioninverse'
                                    }
                                },
                                displayfield: {
                                    bind: {
                                        value: '{theAttribute._cascadeAction_description}'
                                    },
                                    listeners: {
                                        afterrender: function (view) {
                                            const vm = this.lookupViewModel();
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        value: '{theAttribute.cascadeAction}',
                                                        store: '{cascadeActionsStore}'
                                                    },
                                                    single: true
                                                },
                                                function (data) {
                                                    if (data.store && !vm.isDestroyed) {
                                                        const record = data.store.findRecord('value', data.value);
                                                        if (record) {
                                                            vm.set(
                                                                'theAttribute._cascadeAction_description',
                                                                record.get('label')
                                                            );
                                                        }
                                                    }
                                                }
                                            );
                                        }
                                    }
                                },
                                combofield: {
                                    hidden: true
                                }
                            }
                        })
                    ]
                },
                {
                    layout: 'column',
                    hidden: true,
                    bind: {
                        hidden: Ext.String.format(
                            '{objectType !== "{0}"}',
                            CMDBuildUI.util.helper.ModelHelper.systemClasses.class
                        )
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.ismasterdetail,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.ismasterdetail'
                            },
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.isMasterDetail}'
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptionmasterdetail,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptionmasterdetail'
                            },
                            bind: {
                                value: '{theAttribute.masterDetailDescription}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
