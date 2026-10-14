Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.group.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-group-fieldsets-generaldatafieldset',
    viewModel: {},

    ui: 'administration-formpagination',
    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
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
                            tabIndex: 1,
                            vtype: 'alphanum',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                            },
                            name: 'name',
                            enforceMaxLength: true,
                            maxLength: 100,
                            hidden: true,
                            bind: {
                                value: '{theGroup.name}',
                                hidden: '{actions.view}',
                                disabled: '{actions.edit}'
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
                                value: '{theGroup.name}',
                                hidden: '{!actions.view}'
                            }
                        },
                        {
                            /********************* Type **********************/
                            // create / edit
                            xtype: 'combobox',
                            tabIndex: 3,
                            editable: false,
                            reference: 'type',
                            displayField: 'label',
                            valueField: 'value',
                            name: 'type',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                            },
                            hidden: true,
                            queryMode: 'local',
                            anchor: '100%',
                            forceSelection: true,
                            bind: {
                                value: '{theGroup.type}',
                                hidden: '{actions.view}',
                                store: '{typesStore}'
                            }
                        },
                        {
                            // view
                            xtype: 'displayfield',
                            name: 'type',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.type,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                            },
                            hidden: true,
                            bind: {
                                value: '{typeDescription}',
                                hidden: '{!actions.view}'
                            }
                        },
                        {
                            xtype: 'displayfield',
                            tabIndex: 5,
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage'
                            },
                            bind: {
                                value: '{theGroup._startingClass_description}',
                                hidden: '{!actions.view}'
                            }
                        },
                        {
                            xtype: 'allelementscombo',
                            tabIndex: 7,
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.defaultpage'
                            },
                            hidden: true,
                            name: 'initialPage',
                            withClasses: true,
                            withProcesses: true,
                            withDashboards: true,
                            withCustompages: true,
                            withViews: true,
                            withMenuNavTrees: true,
                            typePrefix: true,
                            bind: {
                                value: '{theGroup.startingClass}',
                                hidden: '{actions.view}'
                            }
                        },
                        {
                            /********************* Active **********************/
                            // create / edit / view
                            xtype: 'checkbox',
                            tabIndex: 9,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            name: 'active',
                            hidden: true,
                            bind: {
                                value: '{theGroup.active}',
                                readOnly: '{actions.view}',
                                hidden: '{!theGroup}'
                            }
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
                            /********************* Description **********************/
                            xtype: 'fieldcontainer',
                            tabIndex: 2,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                            },
                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                            labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                            labelToolIconClick: 'onTranslateClickDescription',
                            items: [
                                {
                                    // create / edit
                                    xtype: 'textfield',
                                    name: 'description',
                                    hidden: true,
                                    bind: {
                                        value: '{theGroup.description}',
                                        hidden: '{actions.view}'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{theGroup.description}',
                                        hidden: '{!actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            /********************* Type **********************/
                            // create / edit
                            xtype: 'textfield',
                            tabIndex: 4,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                            },
                            name: 'email',

                            hidden: true,
                            bind: {
                                value: '{theGroup.email}',
                                hidden: '{actions.view}'
                            }
                        },
                        {
                            // view
                            xtype: 'displayfield',
                            name: 'email',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email'
                            },
                            hidden: true,
                            bind: {
                                value: '{theGroup.email}',
                                hidden: '{!actions.view}'
                            }
                        }
                    ]
                }
            ]
        }
    ]
});
