Ext.define('CMDBuildUI.view.joinviews.configuration.items.GeneralProperties', {
    extend: 'Ext.form.FieldSet',

    requires: ['CMDBuildUI.view.joinviews.configuration.items.GeneralPropertiesController'],
    alias: 'widget.joinviews-configuration-items-generalproperties',

    controller: 'joinviews-configuration-items-generalproperties',

    title: CMDBuildUI.locales.Locales.joinviews.generalproperties,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.generalproperties'
    },
    viewModel: {},
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        ui: '{fieldsetUi}'
    },
    layout: {
        type: 'vbox',
        align: 'stretch',
        vertical: true
    },
    scrollable: 'y',
    // set defaults for each fieldcontainer (inner padding)
    defaults: {
        defaults: {
            padding: CMDBuildUI.util.helper.FormHelper.properties.padding
        }
    },

    items: [{
        layout: 'column',
        xtype: 'fieldcontainer',
        items: [{
            xtype: "fieldcontainer",
            layout: "column",
            columnWidth: 0.5,
            allowBlank: false,
            fieldLabel: CMDBuildUI.locales.Locales.joinviews.name,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.joinviews.name"
            },
            items: [{
                columnWidth: 1,
                xtype: "textfield",
                itemId: "name_input",
                name: "name",
                inputType: "text",
                hidden: true,
                bind: {
                    hidden: "{actions.view}",
                    value: "{theView.name}",
                    disabled: '{actions.edit}'
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        if (input.lookupViewModel().get('actions.add')) {
                            var copyTo = input.up('form').down('[name="description"]');
                            if (copyTo && oldValue === copyTo.getValue()) {
                                copyTo.setValue(newValue);
                            }
                        }
                    }
                },
                vtype: "nameInputValidation",
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-name-input"
                }
            }, {
                columnWidth: 1,
                minHeight: 40,
                xtype: "displayfield",
                bind: {
                    hidden: "{!actions.view}",
                    value: "{theView.name}"
                },
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-name-displayfield"
                }
            }]
        }, {
            xtype: "fieldcontainer",
            layout: "column",
            columnWidth: 0.5,
            allowBlank: false,
            fieldLabel: CMDBuildUI.locales.Locales.joinviews.description,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.joinviews.description"
            },
            userCls: 'with-tool',
            labelToolIconClick: 'onDescriptionTranslationClick',
            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
            listeners: {
                beforerender: function () {
                    this.labelToolIconCls = CMDBuildUI.util.Ajax.getViewContext() === 'admin' ? CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid') : undefined;
                    this.labelToolIconQtip = CMDBuildUI.util.Ajax.getViewContext() === 'admin' ? CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate : undefined;
                }
            },
            items: [{
                columnWidth: 1,
                xtype: "textfield",
                itemId: "description_input",
                name: "description",
                inputType: "text",
                hidden: true,
                bind: {
                    hidden: "{actions.view}",
                    value: "{theView.description}"
                },
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-description-input"
                }
            }, {
                columnWidth: 1,
                minHeight: 40,
                xtype: "displayfield",
                bind: {
                    hidden: "{!actions.view}",
                    value: "{theView.description}"
                },
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-description-displayfield"
                }
            }]
        }]
    }, {
        layout: 'column',
        xtype: 'fieldcontainer',
        items: [{
            xtype: "fieldcontainer",
            layout: "column",
            columnWidth: 0.5,
            items: [{
                columnWidth: 1,
                xtype: "allelementscombo",
                name: "masterClass",
                bind: {
                    hidden: "{actions.view}",
                    value: "{theView.masterClass}",
                    disabled: '{actions.edit}'
                },
                itemId: "masterClass_input",
                hidden: true,
                allowBlank: false,
                withStandardClasses: true,
                withProcesses: true,
                listeners: {
                    change: function (input, newValue, oldValue) {
                        input.up('joinviews-configuration-main').fireEventArgs('classchange', [input, newValue, oldValue]);

                    }
                },
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClass-input"
                }
            }, {
                columnWidth: 1,
                minHeight: 40,
                xtype: "displayfield",
                bind: {
                    hidden: "{!actions.view}",
                    value: "{theView.masterClass}"
                },
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClass-displayfield"
                }
            }],
            allowBlank: false,
            fieldLabel: CMDBuildUI.locales.Locales.joinviews.masterclass,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.joinviews.masterclass"
            }
        }, {
            xtype: "fieldcontainer",
            layout: "column",
            columnWidth: 0.5,
            allowBlank: false,
            fieldLabel: CMDBuildUI.locales.Locales.joinviews.masterclassalias,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.joinviews.masterclassalias"
            },
            items: [{
                columnWidth: 1,
                xtype: "textfield",
                itemId: "masterClassAlias_input",
                name: "masterClassAlias",
                inputType: "text",
                hidden: true,
                bind: {
                    hidden: "{actions.view}",
                    value: "{theView.masterClassAlias}"
                },
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClassAlias-input"
                },
                listeners: {
                    change: function (input, newValue, oldValue) {
                        var mainView = input.up('joinviews-configuration-main');
                        if (oldValue) {
                            mainView.clearAliasIndex(mainView.aliasType.klass, oldValue);
                        }

                        var masterClassAliasIndex = mainView.getNewAliasIndex(mainView.aliasType.klass, newValue);
                        var masterClassAlias = Ext.String.format('{0}{1}', newValue, masterClassAliasIndex ? Ext.String.format('_{0}', masterClassAliasIndex) : '');
                        if (masterClassAlias !== newValue) {
                            input.setValue(masterClassAlias);
                        }
                        input.up('joinviews-configuration-main').fireEventArgs('classaliaschange', [input, newValue, oldValue]);
                    }
                }
            }, {
                columnWidth: 1,
                minHeight: 40,
                xtype: "displayfield",
                bind: {
                    hidden: "{!actions.view}",
                    value: "{theView.masterClassAlias}"
                },
                allowBlank: false,
                autoEl: {
                    "data-testid": "conifgurablesviews-masterClassAlias-displayfield"
                }
            }]
        }]
    }, {
        layout: 'column',        
        padding: '0 30 0 0',
        items: [{
            xtype: 'fieldcontainer',
            layout: "column",
            columnWidth: 1,
            items: [{
                xtype: "fieldcontainer",
                layout: "column",
                columnWidth: 0.5,
                fieldLabel: CMDBuildUI.locales.Locales.administration.views.userpermission,
                localized: {
                    fieldLabel: "CMDBuildUI.locales.Locales.administration.views.userpermission"
                },
                bind: {
                    hidden: "{!theView.shared}"
                },
                items: [{
                    columnWidth: 1,
                    xtype: 'displayfield',
                    hidden: true,
                    bind: {
                        hidden: '{!actions.view}',
                        value: '{theView.privilegeMode}'
                    },
                    renderer: function (value) {
                        var vm = this.lookupViewModel();
                        var store = vm.get('privilegeModesStore');
                        if (store) {
                            var record = store.findRecord('value', value);
                            if (record) {
                                return record.get('label');
                            }
                        }
                        return value;
                    }
                }, {
                    columnWidth: 1,
                    xtype: 'combobox',
                    editable: false,
                    queryMode: 'local',
                    forceSelection: true,
                    allowBlank: false,
                    displayField: 'label',
                    valueField: 'value',
                    hidden: true,
                    bind: {
                        value: '{theView.privilegeMode}',
                        hidden: '{actions.view}',
                        store: '{privilegeModesStore}'
                    }
                }]
            }]

        }]
    }, {
        layout: 'column',
        xtype: 'fieldcontainer',
        items: [{
            xtype: "fieldcontainer",
            layout: "column",
            columnWidth: 0.5,
            fieldLabel: CMDBuildUI.locales.Locales.joinviews.active,
            localized: {
                fieldLabel: "CMDBuildUI.locales.Locales.joinviews.active"
            },
            bind: {
                hidden: "{!theView.shared}"
            },
            items: [{
                xtype: "checkbox",
                name: "active",
                itemId: "active_input",
                bind: {
                    disabled: "{actions.view}",
                    value: "{theView.active}"
                },
                autoEl: {
                    "data-testid": "conifgurablesviews-active-input"
                }
            }]
        }]
    }, {
        xtype: 'joinviews-configuration-items-contextmenusfieldset',
        hidden: true,
        bind: {
            hidden: '{!isAdministrationModule}'
        }
    }],

    initComponent: function (view) {
        if (CMDBuildUI.util.Ajax.getViewContext() === 'admin') {
            this.defaults = {};
        }
        this.callParent(arguments);
    }
});