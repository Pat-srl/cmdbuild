Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset',
    viewModel: {},
    ui: 'administration-formpagination',
    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties, // General Properties
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [
                {
                    columnWidth: 0.5,
                    items: [
                        {
                            /********************* Name **********************/
                            // create / edit
                            xtype: 'textfield',
                            vtype: 'nameInputValidation',
                            name: 'processnamefieldadd',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            allowBlank: false,
                            enforceMaxLength: true,
                            maxLength: 20,
                            hidden: true,
                            bind: {
                                value: '{theProcess.name}',
                                hidden: '{!actions.add}'
                            },
                            listeners: {
                                change: function (input, newVal, oldVal) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                        input,
                                        newVal,
                                        oldVal,
                                        '[name="description"]'
                                    );
                                }
                            }
                        },
                        {
                            // edit
                            xtype: 'textfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            name: 'name',
                            hidden: true,
                            disabled: true,
                            bind: {
                                value: '{theProcess.name}',
                                hidden: '{!actions.edit}'
                            },
                            listeners: {
                                change: function (input, newVal, oldVal) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                        input,
                                        newVal,
                                        oldVal,
                                        '[name="description"]'
                                    );
                                }
                            }
                        },
                        {
                            // view
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            name: 'name',
                            hidden: true,
                            bind: {
                                value: '{theProcess.name}',
                                hidden: '{!actions.view}'
                            }
                        },
                        {
                            /********************* description **********************/
                            // create / edit
                            xtype: 'combobox',
                            reference: 'parentField',
                            displayField: 'description',
                            valueField: 'name',
                            name: 'parent',
                            editable: true,
                            typeAhead: true,
                            forceSelection: true,
                            clearFilterOnBlur: true,
                            queryMode: 'local',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData
                                    .inputs.parent.label,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label'
                            },
                            hidden: true,
                            bind: {
                                store: '{superprocessesStore}',
                                value: '{theProcess.parent}',
                                hidden: '{hideParentCombobox}',
                                disabled: '{actions.edit}'
                            },
                            triggers: {
                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger(function (
                                    combo
                                ) {
                                    combo.setValue(CMDBuildUI.model.processes.Process.masterParentClass);
                                },
                                false)
                            }
                        },
                        {
                            // view
                            xtype: 'displayfieldwithtriggers',
                            name: 'parent',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData
                                    .inputs.parent.label,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label'
                            },
                            hidden: true,
                            bind: {
                                value: '{parentDescription}',
                                hidden: '{hideParentDisplayfield}',
                                hideTrigger: Ext.String.format(
                                    '{!theProcess.parent || theProcess.parent == "{0}"}',
                                    CMDBuildUI.util.helper.ModelHelper.systemClasses.process
                                )
                            },
                            triggers: {
                                open: {
                                    cls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                                    handler: function (f, trigger, eOpts) {
                                        const value = f.lookupViewModel().get('theProcess.parent');
                                        const url =
                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(value);
                                        CMDBuildUI.util.Utilities.closeAllPopups();
                                        CMDBuildUI.util.Utilities.redirectTo(url);
                                    }
                                }
                            }
                        },
                        // {
                        //     /********************* Process type **********************/
                        //     // create
                        //     xtype: 'combobox',
                        //     queryMode: 'local',
                        //     displayField: 'label',
                        //     valueField: 'value',
                        //     fieldLabel: CMDBuildUI.locales.Locales.administration.classes.properties.form.fieldsets.generalData.inputs.parent.label,
                        //     name: 'type',
                        //     hidden: true,
                        //     bind: {
                        //         store: '{processParentStore}',
                        //         value: '{theProcess.type}',
                        //         hidden: '{!actions.add}'
                        //     }
                        // }, {
                        //     // view
                        //     xtype: 'displayfield',
                        //     fieldLabel: CMDBuildUI.locales.Locales.administration.processes.fieldlabels.stoppablebyuser,
                        //     name: 'parent',
                        //     hidden: true,
                        //     bind: {
                        //         value: '{theProcess.parent}',
                        //         hidden: '{actions.add}'
                        //     }
                        // },
                        {
                            /********************* stoppableByUser **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.processes.fieldlabels.stoppablebyuser,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.processes.fieldlabels.stoppablebyuser'
                            },
                            name: 'stoppableByUser',
                            hidden: true,
                            bind: {
                                value: '{theProcess.stoppableByUser}',
                                readOnly: '{actions.view}',
                                hidden: '{!theProcess}'
                            }
                        },
                        {
                            /********************* Active **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active, // Active
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            name: 'active',
                            hidden: true,
                            bind: {
                                value: '{theProcess.active}',
                                readOnly: '{actions.view}',
                                hidden: '{!theProcess}'
                            }
                        }
                    ]
                },
                {
                    columnWidth: 0.5,
                    layout: 'column',

                    /********************* description **********************/
                    items: [
                        {
                            xtype: 'fieldcontainer',
                            layout: 'column',
                            columnWidth: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description, // Description
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                            },
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateDescriptionClick',
                            cls: 'ignore-first-type-rule',
                            style: 'margin-left: 15px',
                            items: [
                                {
                                    style: {
                                        paddingRight: 0,
                                        marginLeft: 10
                                    },
                                    // create / edit
                                    xtype: 'textfield',
                                    tabIndex: 1,
                                    allowBlank: false,
                                    name: 'description',
                                    hidden: true,
                                    bind: {
                                        value: '{theProcess.description}',
                                        hidden: '{actions.view}'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfield',
                                    tabIndex: 1,
                                    name: 'description',
                                    hidden: true,
                                    bind: {
                                        value: '{theProcess.description}',
                                        hidden: '{!actions.view}'
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    columnWidth: 0.5,
                    style: {
                        paddingLeft: '15px'
                    },
                    items: [
                        {
                            /********************* Superclass **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass, // Superclass
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.superclass'
                            },
                            name: 'prototype',
                            hidden: true,
                            bind: {
                                value: '{theProcess.prototype}',
                                disabled: '{actions.edit}',
                                readOnly: '{actions.view}',
                                hidden: '{!theProcess}'
                            }
                        },
                        {
                            /********************* enableSaveButton **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enablesavebutton,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.processes.fieldlabels.enablesavebutton'
                            },
                            name: 'hideSaveButton',
                            hidden: true,
                            bind: {
                                hidden: '{!theProcess}',
                                value: '{theProcess.hideSaveButton}',
                                readOnly: '{actions.view}'
                            },
                            listeners: {
                                change: function (checkbox, newValue, oldValue, eOpts) {
                                    this.lookupViewModel().get('theProcess').set('enableSaveButton', !newValue);
                                }
                            }
                        },
                        {
                            /********************* Multitenant mode **********************/
                            xtype: 'fieldcontainer',
                            flex: 1,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode, // Multitenant mode
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.classes.fieldlabels.multitenantmode'
                            },
                            hidden: true,
                            bind: {
                                hidden: '{!isMultitenant || theProcess.prototype}'
                            },
                            items: [
                                {
                                    // edit / add
                                    xtype: 'combobox',
                                    name: 'multitenantMode',
                                    typeAhead: true,
                                    queryMode: 'local',
                                    displayField: 'label',
                                    valueField: 'value',
                                    bind: {
                                        value: '{theProcess.multitenantMode}',
                                        hidden: '{actions.view}',
                                        store: '{multitenantModeStore}'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfield',
                                    name: 'multitenantMode',
                                    hidden: true,
                                    bind: {
                                        value: '{theProcess._multitenantMode_description}',
                                        hidden: '{!actions.view}'
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
