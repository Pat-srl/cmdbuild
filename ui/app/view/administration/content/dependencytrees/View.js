Ext.define('CMDBuildUI.view.administration.content.dependencytrees.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dependencytrees.ViewController',
        'CMDBuildUI.view.administration.content.dependencytrees.ViewModel'
    ],

    alias: 'widget.administration-content-dependencytrees-view',
    controller: 'administration-content-dependencytrees-view',
    viewModel: {
        type: 'administration-content-dependencytrees-view'
    },

    layout: 'border',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    selModel: {
        pruneRemoved: false
    },

    bind: {
        userCls: '{formModeCls}'
    },

    items: [
        {
            xtype: 'panel',
            region: 'center',
            scrollable: 'y',
            hidden: true,
            bind: {
                hidden: '{hideForm}'
            },
            items: [
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                    },
                    items: [
                        {
                            layout: 'column',
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                                    {
                                        name: {
                                            allowBlank: false,
                                            bind: {
                                                value: '{theDependencyTree.name}'
                                            }
                                        }
                                    },
                                    true,
                                    '[name="description"]'
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                                    description: {
                                        allowBlank: false,
                                        bind: {
                                            value: '{theDependencyTree.description}'
                                        },
                                        fieldcontainer: {
                                            userCls: 'with-tool',
                                            labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'flag',
                                                'solid'
                                            ),
                                            labelToolIconQtip:
                                                CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                            labelToolIconClick: 'onTranslateClick'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                {
                                    columnWidth: 0.5,
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                                            {
                                                targetClass: {
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.navigationtrees
                                                                .strings.sourceclass,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.navigationtrees.strings.sourceclass'
                                                        }
                                                    },
                                                    allowBlank: false,
                                                    withStandardClasses: true,
                                                    bind: {
                                                        value: '{theDependencyTree.targetClass}'
                                                    },
                                                    listeners: {
                                                        change: function (store, newValue, oldValue) {
                                                            const vm = this.lookupViewModel();
                                                            const theDependencyTree = vm.get('theDependencyTree');
                                                            theDependencyTree.set('targetClass', newValue);
                                                            if (!Ext.isEmpty(oldValue)) {
                                                                theDependencyTree.nodes().removeAll();
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                            'targetClass'
                                        )
                                    ]
                                },
                                Ext.apply(
                                    CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput(
                                        'theDependencyTree'
                                    ),
                                    { columnWidth: 0.5 }
                                )
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                {
                                    columnWidth: 0.5,
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getInitialExpansionDepthInput(
                                            {
                                                bind: {
                                                    value: '{theDependencyTree.initialExpansionDepth}'
                                                }
                                            }
                                        )
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            items: [
                                {
                                    columnWidth: 0.5,
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                                            active: {
                                                bind: {
                                                    value: '{theDependencyTree.active}'
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
                    collapsible: true,
                    padding: 0,
                    title: CMDBuildUI.locales.Locales.administration.common.labels.tree,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.labels.tree'
                    },
                    items: [
                        {
                            xtype: 'administration-content-dependencytrees-domains'
                        }
                    ],
                    bind: {
                        hidden: '{!theDependencyTree.targetClass}'
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            dock: 'top',
            xtype: 'container',
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                        {
                            edit: true,
                            delete: true,
                            activeToggle: true
                        },
                        'dependencytree',
                        'theDependencyTree'
                    ),
                    bind: {
                        hidden: '{formtoolbarHidden}'
                    }
                }
            ]
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ]
});
