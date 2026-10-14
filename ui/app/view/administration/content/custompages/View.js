Ext.define('CMDBuildUI.view.administration.content.custompages.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.custompages.ViewController',
        'CMDBuildUI.view.administration.content.custompages.ViewModel'
    ],
    alias: 'widget.administration-content-custompages-view',
    controller: 'administration-content-custompages-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-custompages-view'
    },
    ui: 'administration-tabandtools',

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            xtype: 'container',
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
                                                value: '{theCustompage.componentId}',
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
                                            value: '{theCustompage.description}'
                                        },
                                        allowBlank: false,
                                        fieldcontainer: {
                                            // userCls: 'with-tool',
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
                            items: [
                                Ext.apply(
                                    CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('theCustompage'),
                                    { columnWidth: 0.5 }
                                )
                            ]
                        },
                        {
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(
                                    {
                                        active: {
                                            bind: {
                                                value: '{theCustompage.active}'
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
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true,
                    download: true,
                    delete: true,
                    activeToggle: true
                },
                'custompage',
                'theCustompage'
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
                hidden: '{actions.view || hideForm}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ]
});
