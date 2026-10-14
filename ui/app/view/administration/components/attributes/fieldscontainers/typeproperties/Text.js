Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.Text', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-attribute-textfields',

    items: [
        {
            // add / edit
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
                            name: 'editorType',
                            clearFilterOnBlur: false,
                            anyMatch: true,
                            autoSelect: true,
                            forceSelection: true,
                            typeAhead: true,
                            queryMode: 'local',
                            displayField: 'label',
                            valueField: 'value',
                            autoEl: {
                                'data-testid': 'attribute-editorTypeText_input'
                            },
                            bind: {
                                value: '{theAttribute.editorType}',
                                store: '{editorTypeStore}'
                            },
                            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getEditorType
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'combo',
                            itemId: 'textContentSecurity_input',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.textcontentsecurity'
                            },
                            valueField: 'value',
                            displayField: 'label',
                            name: 'textContentSecurity',
                            forceSelection: true,
                            hidden: true,
                            queryMode: 'local',
                            autoEl: {
                                'data-testid': 'attribute-textContentSecurityText_input'
                            },
                            bind: {
                                value: '{theAttribute.textContentSecurity}',
                                store: '{textContentSecurityStore}',
                                hidden: '{theAttribute.editorType === "PLAIN"}'
                            },
                            listeners: {
                                show: function (combo) {
                                    if (
                                        !combo.getValue() ||
                                        combo.getValue() === CMDBuildUI.model.Attribute.textContentSecurity.plaintext
                                    ) {
                                        combo.setValue(CMDBuildUI.model.Attribute.textContentSecurity.html_safe);
                                    }
                                    combo.forceSelection = true;
                                },
                                hide: function (combo) {
                                    const vm = combo.lookupViewModel();
                                    combo.forceSelection = false;
                                    if (vm.get('theAttribute.editorType') === 'PLAIN') {
                                        combo.setValue(CMDBuildUI.model.Attribute.textContentSecurity.plaintext);
                                        vm.set(
                                            'theAttribute.textContentSecurity',
                                            CMDBuildUI.model.Attribute.textContentSecurity.plaintext
                                        );
                                    } else {
                                        combo.setValue(CMDBuildUI.model.Attribute.textContentSecurity.html_safe);
                                        vm.set(
                                            'theAttribute.textContentSecurity',
                                            CMDBuildUI.model.Attribute.textContentSecurity.html_safe
                                        );
                                    }
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
                            bind: {
                                value: '{theAttribute.anonymizable}'
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
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.editortype'
                            },
                            autoEl: {
                                'data-testid': 'attribute-editorTypeText_display'
                            },
                            bind: {
                                value: '{theAttribute.editorType}'
                            },
                            renderer: CMDBuildUI.util.administration.helper.RendererHelper.getEditorType
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
                            hidden: true,
                            autoEl: {
                                'data-testid': 'attribute-textContentSecurityText_display'
                            },
                            bind: {
                                hidden: '{theAttribute.editorType === "PLAIN"}',
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
                        }
                    ]
                }
            ]
        }
    ]
});
