Ext.define('CMDBuildUI.view.administration.content.gis.thematisms.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.thematisms.GridController',
        'CMDBuildUI.view.administration.content.gis.thematisms.GridModel'
    ],

    alias: 'widget.administration-content-gis-thematisms-grid',
    controller: 'administration-content-gis-thematisms-grid',
    viewModel: {
        type: 'administration-content-gis-thematisms-grid'
    },

    forceFit: true,
    loadMask: true,
    bind: {
        store: '{thematismsStore}'
    },

    columns: [
        {
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: 'description',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.gis.owneruser,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.gis.owneruser'
            },
            dataIndex: 'userOwner',
            align: 'left'
        },
        {
            text: CMDBuildUI.locales.Locales.administration.gis.ownerclass,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.gis.ownerclass'
            },
            dataIndex: 'owner',
            align: 'left'
        },
        {
            xtype: 'checkcolumn',
            text: CMDBuildUI.locales.Locales.administration.gis.public,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.gis.public'
            },
            dataIndex: '_public',
            bind: {
                disabled: '{actions.view}'
            },
            align: 'center'
        }
    ],

    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'top',
            items: [
                {
                    xtype: 'textfield',
                    name: 'search',
                    width: 250,
                    emptyText: CMDBuildUI.locales.Locales.administration.gis.searchemptytext,
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.gis.searchemptytext'
                    },
                    cls: 'administration-input',
                    itemId: 'searchtext',
                    bind: {
                        hidden: '{!canFilter}'
                    },
                    listeners: {
                        specialkey: 'onSearchSpecialKey'
                    },
                    triggers: {
                        search: {
                            cls: Ext.baseCSSPrefix + 'form-search-trigger',
                            handler: 'onSearchSubmit',
                            autoEl: {
                                'data-testid': 'administration-gis-thematism-search-trigger'
                            }
                        },
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: 'onSearchClear',
                            autoEl: {
                                'data-testid': 'administration-gis-thematism-search-clear-trigger'
                            }
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-gis-thematism-search-form'
                    }
                }
            ]
        },
        {
            dock: 'top',
            xtype: 'container',
            items: [
                {
                    xtype: 'components-administration-toolbars-formtoolbar',
                    items: [
                        {
                            xtype: 'button',
                            itemId: 'spacer',
                            style: {
                                visibility: 'hidden'
                            }
                        },
                        {
                            xtype: 'tbfill'
                        },
                        {
                            xtype: 'tool',
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                            tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                            localized: {
                                tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
                            },
                            callback: 'onEditBtnClick',
                            cls: 'administration-tool',
                            bind: {
                                disabled: '{!canEdit}'
                            },
                            autoEl: {
                                'data-testid': 'administration-thematisms-editBtn'
                            }
                        }
                    ]
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
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons({ formBind: false })
        }
    ]
});
