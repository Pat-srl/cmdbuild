Ext.define('CMDBuildUI.view.administration.content.navigationtrees.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.navigationtrees.ViewController',
        'CMDBuildUI.view.administration.content.navigationtrees.ViewModel'
    ],

    alias: 'widget.administration-content-navigationtrees-view',
    controller: 'administration-content-navigationtrees-view',
    viewModel: {
        type: 'administration-content-navigationtrees-view'
    },

    layout: 'border',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    bind: {
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
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
                                                value: '{theNavigationtree.name}'
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
                                            value: '{theNavigationtree.description}'
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
                                                        value: '{theNavigationtree.targetClass}'
                                                    },
                                                    listeners: {
                                                        change: function (store, newValue, oldValue) {
                                                            const vm = this.lookupViewModel();
                                                            const theNavigationtree = vm.get('theNavigationtree');
                                                            theNavigationtree.set('targetClass', newValue);
                                                            if (!Ext.isEmpty(oldValue)) {
                                                                theNavigationtree.nodes().removeAll();
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
                                        'theNavigationtree'
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
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                                            active: {
                                                bind: {
                                                    value: '{theNavigationtree.active}'
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
                            xtype: 'administration-content-navigationtrees-domains'
                        }
                    ],
                    bind: {
                        hidden: '{!theNavigationtree.targetClass}'
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            padding: '5 10 5 10',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {},
                'navigationtree',
                'theNavigationtree',
                [
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.administration.navigationtrees.texts.addnavigationtree,
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.navigationtrees.texts.addnavigationtree'
                        },
                        ui: 'administration-action-small',
                        itemId: 'addBtn',
                        autoEl: {
                            'data-testid': 'administration-class-toolbar-addNavigationtreeBtn'
                        },
                        bind: {
                            disabled: '{!toolAction._canAdd}'
                        }
                    }
                ],
                null,
                [
                    {
                        xtype: 'tbtext',
                        bind: {
                            hidden: '{!theNavigationtree.description}',
                            html:
                                CMDBuildUI.locales.Locales.administration.navigationtrees.singular +
                                ': <b data-testid="administration-navigationtree-description">{theNavigationtree.description}</b>'
                        }
                    }
                ]
            )
        },
        {
            dock: 'top',
            xtype: 'container',
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                        {
                            edit: true, // #editBtn set true for show the button
                            view: false, // #viewBtn set true for show the button
                            open: false, // #openBtn set true for show the button
                            clone: false, // #cloneBtn set true for show the button
                            delete: true, // #deleteBtn set true for show the button
                            activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
                            download: false // #downloadBtn set true for show the buttons
                        },

                        /* testId */
                        'navigationtree',

                        /* viewModel object needed only for activeTogle */
                        'theNavigationtree',

                        /* add custom tools[] on the left of the bar */
                        [],

                        /* add custom tools[] before #editBtn*/
                        [],

                        /* add custom tools[] after at the end of the bar*/
                        []
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
