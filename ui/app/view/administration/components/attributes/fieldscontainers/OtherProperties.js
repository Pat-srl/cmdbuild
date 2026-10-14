Ext.define('CMDBuildUI.view.administration.components.attributes.fieldscontainers.OtherProperties', {
    extend: 'Ext.form.Panel',
    alias: 'widget.administration-components-attributes-fieldscontainers-otherproperties',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [
        {
            hidden: true,
            bind: {
                hidden: '{isOtherPropertiesHidden}'
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'help',
                            autoEl: {
                                'data-testid': 'attribute-help_input'
                            },
                            options: {
                                mode: 'ace/mode/markdown',
                                readOnly: true
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.help'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorHelpExpand',
                            listeners: {
                                render: function (element) {
                                    const aceEditor = element.getAceEditor();
                                    const vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                theAttribute: '{theAttribute}'
                                            },
                                            single: true
                                        },
                                        function (data) {
                                            if (data.theAttribute) {
                                                aceEditor.setValue(data.theAttribute.get('help'), -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                    aceEditor.getSession().on('change', function (event, _editor) {
                                        vm.set('theAttribute.help', _editor.getValue());
                                    });
                                }
                            },
                            name: 'help',
                            width: '95%'
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'showIf',
                            options: {
                                mode: 'ace/mode/javascript',
                                readOnly: true
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.showif'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorShowIfExpand',
                            listeners: {
                                render: function (element) {
                                    const aceEditor = element.getAceEditor();
                                    const vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                theAttribute: '{theAttribute}'
                                            },
                                            single: true
                                        },
                                        function (data) {
                                            if (data.theAttribute) {
                                                aceEditor.setValue(data.theAttribute.get('showIf'), -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                }
                            },
                            name: 'showIf',
                            width: '95%'
                        }
                    ]
                },
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'checkbox',
                            boxLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.helpalwaysvisible,
                            localized: {
                                boxLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.helpalwaysvisible'
                            },
                            name: 'helpAlwaysVisible',
                            autoEl: {
                                'data-testid': 'attribute-helpAlwaysVisible_input'
                            },
                            bind: {
                                value: '{theAttribute.helpAlwaysVisible}',
                                hidden: '{!theAttribute.help}'
                            },
                            readOnly: true,
                            hidden: true
                        }
                    ]
                }
            ]
        },
        {
            hidden: true,
            bind: {
                hidden: Ext.String.format(
                    '{!theAttribute || theAttribute.type == "{0}" || theAttribute.type == "{1}"}',
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.file,
                    CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                )
            },
            items: [
                {
                    layout: 'column',
                    items: [
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'validationRules',
                            autoEl: {
                                'data-testid': 'attribute-validationRules_input'
                            },
                            options: {
                                mode: 'ace/mode/javascript',
                                readOnly: true
                            },
                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.validationrules'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorValidationRulesExpand',
                            listeners: {
                                render: function (element) {
                                    const aceEditor = element.getAceEditor();
                                    const vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                theAttribute: '{theAttribute}'
                                            },
                                            single: true
                                        },
                                        function (data) {
                                            if (data.theAttribute) {
                                                aceEditor.setValue(data.theAttribute.get('validationRules'), -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                    aceEditor.getSession().on('change', function (event, _editor) {
                                        vm.set('theAttribute.validationRules', _editor.getValue());
                                    });
                                }
                            },
                            name: 'validationRules',
                            width: '95%'
                        },
                        {
                            columnWidth: 0.5,
                            xtype: 'aceeditortextarea',
                            allowBlank: true,
                            vmObjectName: 'theAttribute',
                            inputField: 'autoValue',
                            autoEl: {
                                'data-testid': 'attribute-autoValue_input'
                            },
                            options: {
                                mode: 'ace/mode/javascript',
                                readOnly: true
                            },

                            bind: {
                                readOnly: '{actions.view}',
                                config: {
                                    options: {
                                        readOnly: '{actions.view}'
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.autovalue'
                            },
                            minHeight: '85px',
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('expand', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.expand,
                            labelToolIconClick: 'onAceEditorAutoValueExpand',
                            listeners: {
                                render: function (element) {
                                    const aceEditor = element.getAceEditor();
                                    const vm = element.lookupViewModel();
                                    vm.bind(
                                        {
                                            bindTo: {
                                                theAttribute: '{theAttribute}'
                                            },
                                            single: true
                                        },
                                        function (data) {
                                            if (data.theAttribute) {
                                                aceEditor.setValue(data.theAttribute.get('autoValue'), -1);
                                            }
                                        }
                                    );
                                    vm.bind(
                                        {
                                            isView: '{actions.view}'
                                        },
                                        function (data) {
                                            aceEditor.setReadOnly(data.isView);
                                        }
                                    );
                                    aceEditor.getSession().on('change', function (event, _editor) {
                                        vm.set('theAttribute.autoValue', _editor.getValue());
                                    });
                                }
                            },
                            name: 'autoValue',
                            width: '95%'
                        }
                    ]
                }
            ]
        }
    ]
});
