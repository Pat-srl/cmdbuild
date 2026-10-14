Ext.define('CMDBuildUI.view.administration.content.customcomponents.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.customcomponents.ViewController',
        'CMDBuildUI.view.administration.content.customcomponents.ViewModel'
    ],

    alias: 'widget.administration-content-customcomponents-view',
    controller: 'administration-content-customcomponents-view',
    viewModel: {
        type: 'administration-content-customcomponents-view'
    },

    layout: 'border',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

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
                    defaults: {
                        columnWidth: 0.5,
                        layout: 'column',
                        xtype: 'container'
                    },
                    items: [
                        {
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextfieldInput(
                                    'componentId',
                                    {
                                        componentId: {
                                            fieldcontainer: {
                                                fieldLabel:
                                                    CMDBuildUI.locales.Locales.administration.custompages.fieldlabels
                                                        .componentid, // Component
                                                localized: {
                                                    fieldLabel:
                                                        'CMDBuildUI.locales.Locales.administration.custompages.fieldlabels.componentid'
                                                },
                                                hidden: true,
                                                bind: {
                                                    hidden: '{actions.add}'
                                                }
                                            },
                                            disabled: true,
                                            bind: {
                                                value: '{theCustomcomponent.componentId}',
                                                disabled: '{actions.edit}'
                                            }
                                        }
                                    }
                                )
                            ]
                        },
                        {
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                                    description: {
                                        name: 'description',
                                        bind: {
                                            value: '{theCustomcomponent.description}'
                                        },
                                        allowBlank: false
                                    }
                                })
                            ]
                        },
                        {
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(
                                    {
                                        active: {
                                            bind: {
                                                value: '{theCustomcomponent.active}'
                                            }
                                        }
                                    },
                                    'active'
                                )
                            ]
                        }
                    ]
                },
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.customcomponents.titles.files, // Files
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.customcomponents.titles.files'
                    },
                    items: [
                        {
                            layout: 'column',
                            columnWidth: 0.5,
                            items: [
                                {
                                    columnWidth: 0.5,
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.customcomponents.texts.desktopversion,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.customcomponents.texts.desktopversion'
                                    },
                                    allowBlank: false,
                                    items: [
                                        {
                                            flex: 1,
                                            xtype: 'filefield',
                                            name: 'fileCustomcomponentDefault',
                                            msgTarget: 'side',
                                            emptyText:
                                                CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile,
                                            localized: {
                                                emptyText:
                                                    'CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile'
                                            },
                                            accept: '.zip',
                                            buttonConfig: {
                                                ui: 'administration-secondary-action-small'
                                            },
                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            listeners: {
                                                change: function (input, newValue, oldValue) {
                                                    var form = this.up('form');
                                                    var vm = form.getViewModel();
                                                    var mobileField = form.down('[name="fileCustomcomponentMobile"]');
                                                    if (vm.get('actions.add')) {
                                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                            mobileField,
                                                            newValue.length > 0,
                                                            form
                                                        );
                                                    }
                                                }
                                            }
                                        },
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(
                                            {
                                                hasDefault: {
                                                    fieldcontainer: {
                                                        fieldLabel: null,
                                                        localized: null
                                                    },
                                                    bind: {
                                                        hidden: '{!actions.view}',
                                                        value: '{hasDefault}'
                                                    }
                                                }
                                            },
                                            'hasDefault'
                                        ),
                                        {
                                            xtype: 'container',
                                            layout: 'hbox',
                                            margin: '10 0 0 0',
                                            hidden: true,
                                            bind: {
                                                hidden: '{actions.view || !hasDefault || defaultRemoved}'
                                            },
                                            items: [
                                                {
                                                    xtype: 'button',
                                                    cls: 'input-action-button',
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'trash-alt',
                                                        'solid'
                                                    ),
                                                    device: CMDBuildUI.model.menu.Menu.device['default'],
                                                    handler: 'removeVersionBtnClick'
                                                },
                                                {
                                                    xtype: 'button',
                                                    device: CMDBuildUI.model.menu.Menu.device['default'],
                                                    handler: 'downloadVersionBtnClick',
                                                    cls: 'input-action-button',
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'download',
                                                        'solid'
                                                    )
                                                }
                                            ]
                                        }
                                    ]
                                },
                                {
                                    columnWidth: 0.5,
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.customcomponents.texts.mobileversion,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.customcomponents.texts.mobileversion'
                                    },
                                    hidden: true,
                                    bind: {
                                        hidden: '{hideMobileFields}'
                                    },
                                    allowBlank: false,
                                    items: [
                                        {
                                            flex: 1,
                                            xtype: 'filefield',
                                            name: 'fileCustomcomponentMobile',
                                            msgTarget: 'side',
                                            emptyText:
                                                CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile,
                                            localized: {
                                                emptyText:
                                                    'CMDBuildUI.locales.Locales.administration.custompages.texts.selectfile'
                                            },
                                            accept: '.zip',
                                            buttonConfig: {
                                                ui: 'administration-secondary-action-small'
                                            },
                                            bind: {
                                                hidden: '{actions.view}'
                                            },
                                            listeners: {
                                                change: function (input, newValue, oldValue) {
                                                    var form = this.up('form');
                                                    var vm = form.getViewModel();
                                                    var defaultField = form.down('[name="fileCustomcomponentDefault"]');
                                                    if (vm.get('actions.add')) {
                                                        CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                            defaultField,
                                                            newValue.length > 0,
                                                            form
                                                        );
                                                    }
                                                }
                                            }
                                        },
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(
                                            {
                                                hasMobile: {
                                                    fieldcontainer: {
                                                        fieldLabel: null,
                                                        localized: null
                                                    },
                                                    bind: {
                                                        hidden: '{!actions.view}',
                                                        value: '{hasMobile}'
                                                    }
                                                }
                                            },
                                            'hasMobile'
                                        ),
                                        {
                                            xtype: 'container',
                                            layout: 'hbox',
                                            margin: '10 0 0 0',
                                            hidden: true,
                                            bind: {
                                                hidden: '{actions.view || !hasMobile || mobileRemoved}'
                                            },
                                            items: [
                                                {
                                                    xtype: 'button',
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'trash-alt',
                                                        'solid'
                                                    ),
                                                    cls: 'input-action-button',
                                                    device: CMDBuildUI.model.menu.Menu.device.mobile,
                                                    handler: 'removeVersionBtnClick'
                                                },
                                                {
                                                    xtype: 'button',
                                                    device: CMDBuildUI.model.menu.Menu.device.mobile,
                                                    handler: 'downloadVersionBtnClick',
                                                    cls: 'input-action-button',
                                                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                        'download',
                                                        'solid'
                                                    )
                                                }
                                            ]
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],

    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            padding: '6 0 6 8',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {},
                'searchfilter',
                'theViewFilter',
                [
                    {
                        xtype: 'button',
                        text: CMDBuildUI.locales.Locales.administration.customcomponents.texts.addcustomcomponent, // Add customcomponent
                        localized: {
                            text: 'CMDBuildUI.locales.Locales.administration.customcomponents.texts.addcustomcomponent'
                        },
                        ui: 'administration-action-small',
                        itemId: 'addBtn',
                        autoEl: {
                            'data-testid': 'administration-class-toolbar-addLookupTypeBtn'
                        },
                        bind: {
                            disabled: '{!toolAction._canAdd}'
                        }
                    },
                    {
                        xtype: 'admin-globalsearchfield',
                        getObjectType: function () {
                            return this.getViewModel().get('componentType');
                        }
                    },
                    {
                        xtype: 'tbfill'
                    }
                ],
                null,
                [
                    {
                        xtype: 'tbtext',
                        hidden: true,
                        bind: {
                            hidden: '{!theCustomcomponent.description}',
                            html: '{componentTypeName}: <b data-testid="administration-customcomponent-description">{theCustomcomponent.description}</b>'
                        }
                    }
                ]
            )
        },
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'top',
            borderBottom: 0,
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true, // #editBtn set true for show the button
                    delete: true, // #deleteBtn set true for show the button
                    activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
                    download: true // #downloadBtn set true for show the buttons
                },

                /* testId */
                'customcomponent',

                /* viewModel object needed only for activeTogle */
                'theCustomcomponent',

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
        },
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
