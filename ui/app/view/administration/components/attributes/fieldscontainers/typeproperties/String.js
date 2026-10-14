Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.String', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-stringfields',

    config: {
        theAttribute: null,
        actions: {}
    },

    items: [
        {
            // add // edit
            xtype: 'container',
            bind: {
                hidden: '{actions.view}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                            },
                            name: 'password',
                            autoEl: {
                                'data-testid': 'attribute-password_input'
                            },
                            allowBlank: false,
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            disabled: true,
                            bind: {
                                disabled: '{actions.edit}',
                                value: '{theAttribute.password}',
                                store: '{stringEditorTypesStore}'
                            },
                            listeners: {
                                change: function (combo, newValue, oldValue) {
                                    const vm = combo.lookupViewModel();
                                    if (newValue === 'true' && oldValue) {
                                        vm.set(
                                            'theAttribute.textContentSecurity',
                                            CMDBuildUI.model.Attribute.textContentSecurity.plaintext
                                        );
                                        vm.set(
                                            'theAttribute.showPassword',
                                            CMDBuildUI.model.Attribute.showPassword.always
                                        );
                                    }
                                }
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            itemId: 'attribute-showPassword',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword'
                            },
                            valueField: 'value',
                            displayField: 'label',
                            name: 'showPassword',
                            autoEl: {
                                'data-testid': 'attribute-showPassword_input'
                            },
                            hidden: true,
                            queryMode: 'local',
                            forceSelection: true,
                            bind: {
                                hidden: '{!theAttribute.password}',
                                value: '{theAttribute.showPassword}',
                                store: '{showPasswordStore}'
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    hidden: true,
                    bind: {
                        hidden: '{theAttribute.password}'
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'numberfield',
                            step: 1,
                            minValue: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                            },
                            autoEl: {
                                'data-testid': 'attribute-maxLengthString_input'
                            },
                            name: 'maxLength',
                            bind: {
                                value: '{theAttribute.maxLength}',
                                disabled: '{theAttribute.inherited}'
                            },
                            listeners: {
                                afterrender: function (input) {
                                    const vm = input.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: '{theAttribute.password}'
                                        },
                                        function (password) {
                                            if (password) {
                                                input.setMinValue(null);
                                                vm.set('theAttribute.maxLength', null);
                                            } else {
                                                if (!vm.get('theAttribute.maxLength')) {
                                                    const defaultValue = input.bind.value.stub.parent.boundValue
                                                        .getField('maxLength')
                                                        .getDefaultValue();
                                                    input.setMinValue(1);
                                                    vm.set('theAttribute.maxLength', defaultValue);
                                                }
                                            }
                                        }
                                    );
                                }
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity'
                            },
                            valueField: 'value',
                            displayField: 'label',
                            name: 'textContentSecurity',
                            autoEl: {
                                'data-testid': 'attribute-textContentSecurityString_input'
                            },
                            queryMode: 'local',
                            forceSelection: true,
                            bind: {
                                value: '{theAttribute.textContentSecurity}',
                                store: '{textContentSecurityStore}'
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            xtype: 'checkbox',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.anonymizable,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.anonymizable'
                            },
                            autoEl: {
                                'data-testid': 'attribute-anonymizable'
                            },
                            name: 'anonymizable',
                            bind: {
                                value: '{theAttribute.anonymizable}'
                            }
                        },
                        {
                            xtype: 'checkbox',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.trimenabled,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.trimenabled'
                            },
                            autoEl: {
                                'data-testid': 'attribute-trimEnabled'
                            },
                            name: 'trimEnabled',
                            bind: {
                                value: '{theAttribute.trimEnabled}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            // view
            xtype: 'container',
            bind: {
                hidden: '{!actions.view}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            step: 1,
                            minValue: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                            },
                            autoEl: {
                                'data-testid': 'attribute-password_display'
                            },
                            bind: {
                                value: '{theAttribute._password_description}'
                            },
                            listeners: {
                                afterrender: function () {
                                    const vm = this.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: '{theAttribute.password}'
                                        },
                                        function (password) {
                                            const stringEditorTypesStore = vm.get('stringEditorTypesStore');
                                            if (stringEditorTypesStore) {
                                                const record = stringEditorTypesStore.findRecord(
                                                    'value',
                                                    password + ''
                                                );
                                                if (record) {
                                                    vm.set('theAttribute._password_description', record.get('label'));
                                                }
                                            }
                                        }
                                    );
                                }
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword'
                            },
                            autoEl: {
                                'data-testid': 'attribute-showPassword_display'
                            },
                            queryMode: 'local',
                            hidden: true,
                            bind: {
                                hidden: '{!theAttribute.password}',
                                value: '{theAttribute._showPassword_description}'
                            },
                            listeners: {
                                afterrender: function () {
                                    const vm = this.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: '{theAttribute.showPassword}'
                                        },
                                        function (showPassword) {
                                            const showPasswordStore = vm.get('showPasswordStore');
                                            if (showPasswordStore) {
                                                const record = showPasswordStore.findRecord('value', showPassword);
                                                if (record) {
                                                    vm.set(
                                                        'theAttribute._showPassword_description',
                                                        record.get('label')
                                                    );
                                                }
                                            }
                                        }
                                    );
                                }
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    hidden: true,
                    bind: {
                        hidden: '{theAttribute.password}'
                    },
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            step: 1,
                            minValue: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.maxlength'
                            },
                            name: 'maxLength',
                            autoEl: {
                                'data-testid': 'attribute-maxLengthString_display'
                            },
                            bind: {
                                value: '{theAttribute.maxLength}'
                            }
                        },
                        {
                            // textContentSecurity
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity'
                            },
                            autoEl: {
                                'data-testid': 'attribute-textContentSecurityString_display'
                            },
                            bind: {
                                value: '{theAttribute.textContentSecurity}'
                            },
                            renderer: function (value) {
                                const vm = this.lookupViewModel();
                                const store = vm.get('textContentSecurityStore');
                                if (store) {
                                    const record = store.findRecord('value', value);
                                    if (record) {
                                        return record.get('label');
                                    }
                                }
                                return value;
                            }
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showpassword'
                            },
                            autoEl: {
                                'data-testid': 'attribute-showPassword_display'
                            },
                            queryMode: 'local',
                            hidden: true,
                            bind: {
                                hidden: '{!theAttribute.password}',
                                value: '{theAttribute._showPassword_description}'
                            },
                            listeners: {
                                afterrender: function () {
                                    const vm = this.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: '{theAttribute.showPassword}'
                                        },
                                        function (showPassword) {
                                            const showPasswordStore = vm.get('showPasswordStore');
                                            if (showPasswordStore) {
                                                const record = showPasswordStore.findRecord('value', showPassword);
                                                if (record) {
                                                    vm.set(
                                                        'theAttribute._showPassword_description',
                                                        record.get('label')
                                                    );
                                                }
                                            }
                                        }
                                    );
                                }
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            xtype: 'checkbox',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.anonymizable,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.anonymizable'
                            },
                            autoEl: {
                                'data-testid': 'attribute-anonymizable'
                            },
                            name: 'anonymizable',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.anonymizable}'
                            }
                        },
                        {
                            xtype: 'checkbox',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.trimenabled,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.trimenabled'
                            },
                            autoEl: {
                                'data-testid': 'attribute-trimEnabled'
                            },
                            name: 'trimEnabled',
                            readOnly: true,
                            bind: {
                                value: '{theAttribute.trimEnabled}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
