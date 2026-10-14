Ext.define('CMDBuildUI.view.administration.content.dependencytrees.NodeSettings', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dependencytrees.NodeSettingsController',
        'CMDBuildUI.view.administration.content.dependencytrees.NodeSettingsModel',
        'CMDBuildUI.view.dependencytree.Mixin'
    ],

    alias: 'widget.administration-content-dependencytrees-nodesettings',
    controller: 'administration-content-dependencytrees-nodesettings',
    viewModel: {
        type: 'administration-content-dependencytrees-nodesettings'
    },

    scrollable: 'y',

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: false,
            itemId: 'borderfieldset',
            title: CMDBuildUI.locales.Locales.administration.dependencytrees.fieldsetborder,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dependencytrees.fieldsetborder'
            },
            layout: {
                type: 'vbox'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('borderSource', {
                    borderSource: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.source,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.source'
                            },
                            width: '50%',
                            padding: '10 0 10 10'
                        },
                        bind: {
                            value: '{treeNode.borderSource}',
                            store: '{borderSource}'
                        }
                    }
                }),
                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.color,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.color'
                    },
                    hidden: true,
                    itemId: 'bordercolorpicker',
                    width: '50%',
                    padding: 10,
                    bind: {
                        hidden:
                            '{treeNode.borderSource !== "' +
                            CMDBuildUI.view.dependencytree.Mixin.static +
                            '" || actions.view}',
                        value: '{treeNode.borderColor}'
                    },
                    displayColor: {
                        bind: {
                            hidden: '{treeNode.borderSource !== "' + CMDBuildUI.view.dependencytree.Mixin.static + '"}'
                        }
                    }
                })
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            collapsible: false,
            title: CMDBuildUI.locales.Locales.administration.dependencytrees.fieldseticon,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dependencytrees.fieldseticon'
            },
            layout: 'vbox',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('iconSource', {
                    iconSource: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.source,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.source'
                            },
                            width: '50%',
                            padding: '10 0 10 10'
                        },
                        bind: {
                            value: '{treeNode.iconSource}',
                            store: '{iconSource}'
                        }
                    }
                }),
                Ext.apply(CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('treeNode'), {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.icon,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.icon'
                    },
                    width: '50%',
                    padding: '10 0 10 10',
                    hidden: true,
                    bind: {
                        hidden: '{treeNode.iconSource !== "' + CMDBuildUI.view.dependencytree.Mixin.static + '"}'
                    }
                }),
                {
                    xtype: 'container',
                    layout: 'hbox',
                    width: '100%',
                    items: [
                        {
                            xtype: 'container',
                            width: '50%',
                            layout: 'fit',
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                                    'iconColorMode',
                                    {
                                        iconColorMode: {
                                            fieldcontainer: {
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.dependencytrees.colormode,
                                                localized: {
                                                    fieldLabel:
                                                        'CMDBuildUI.locales.Locales.administration.dependencytrees.colormode'
                                                },
                                                hidden: true,
                                                bind: {
                                                    hidden:
                                                        '{treeNode.iconSource === "' +
                                                        CMDBuildUI.view.dependencytree.Mixin.default +
                                                        '"}'
                                                },
                                                padding: '10 0 10 10'
                                            },
                                            bind: {
                                                value: '{treeNode.iconColorMode}',
                                                store: '{iconColorMode}'
                                            }
                                        }
                                    }
                                )
                            ]
                        },
                        {
                            xtype: 'container',
                            width: '50%',
                            layout: 'fit',
                            items: [
                                CMDBuildUI.util.helper.FieldsHelper.getColorpickerField({
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.color,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.color'
                                    },
                                    itemId: 'iconcolorpicker',
                                    padding: '10 0 10 10',
                                    hidden: true,
                                    bind: {
                                        hidden:
                                            '{treeNode.iconColorMode !== "' +
                                            CMDBuildUI.view.dependencytree.Mixin.static +
                                            '" || treeNode.iconSource === "' +
                                            CMDBuildUI.view.dependencytree.Mixin.default +
                                            '" || actions.view}',
                                        value: '{treeNode.iconColor}'
                                    },
                                    displayColor: {
                                        bind: {
                                            hidden:
                                                '{treeNode.iconColorMode !== "' +
                                                CMDBuildUI.view.dependencytree.Mixin.static +
                                                '" || treeNode.iconSource === "' +
                                                CMDBuildUI.view.dependencytree.Mixin.default +
                                                '"}'
                                        }
                                    }
                                })
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: false,
            itemId: 'lookupfieldset',
            title: CMDBuildUI.locales.Locales.administration.dependencytrees.fieldsetlookup,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dependencytrees.fieldsetlookup'
            },
            hidden: true,
            layout: {
                type: 'vbox'
            },
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput('sourceLookup', {
                    sourceLookup: {
                        fieldcontainer: {
                            fieldLabel: CMDBuildUI.locales.Locales.administration.dependencytrees.attribute,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.dependencytrees.attribute'
                            },
                            width: '50%',
                            padding: '10 0 10 10'
                        },
                        emptyText: CMDBuildUI.locales.Locales.administration.dependencytrees.selectlookup,
                        localized: {
                            emptyText: 'CMDBuildUI.locales.Locales.administration.dependencytrees.selectlookup'
                        },
                        bind: {
                            value: '{treeNode.sourceLookup}',
                            store: '{lookupAttributesList}'
                        }
                    }
                })
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
        }
    ]
});
