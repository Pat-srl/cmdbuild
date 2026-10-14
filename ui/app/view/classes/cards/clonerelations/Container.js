Ext.define('CMDBuildUI.view.classes.cards.clonerelations.Container', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.classes.cards.clonerelations.ContainerController',
        'CMDBuildUI.view.classes.cards.clonerelations.ContainerModel'
    ],

    alias: 'widget.classes-cards-clonerelations-container',
    controller: 'classes-cards-clonerelations-container',
    viewModel: {
        type: 'classes-cards-clonerelations-container'
    },

    layout: 'fit',

    items: [
        {
            xtype: 'form',
            scrollable: 'y',
            items: [
                {
                    xtype: 'classes-cards-card-create',
                    itemId: 'classes-cards-card-create',
                    padding: 10,
                    cloneObject: true,
                    tab: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
                    tabAction: CMDBuildUI.mixins.DetailsTabPanel.actions.clonecardandrelations,
                    buttons: null
                },
                {
                    ui: 'formpagination',
                    xtype: 'formpaginationfieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.common.tabs.clonerelationmode,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.common.tabs.clonerelationmode'
                    },
                    items: [
                        {
                            xtype: 'grid',
                            itemId: 'classes-cards-clonerelations-panel',
                            layout: 'fit',
                            forceFit: true,
                            columns: [
                                {
                                    text: CMDBuildUI.locales.Locales.filters.domain,
                                    dataIndex: 'description',
                                    align: 'left',
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.domain'
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.filters.actions,
                                    localized: {
                                        text: 'CMDBuildUI.locales.Locales.filters.actions'
                                    },
                                    columns: [
                                        {
                                            xtype: 'checkcolumn',
                                            text: CMDBuildUI.locales.Locales.filters.ignore,
                                            dataIndex: 'ignore',
                                            headerCheckbox: true,
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.filters.ignore'
                                            },
                                            listeners: {
                                                headercheckchange: 'onHeaderCheckChange',

                                                checkchange: 'onCheckChange'
                                            }
                                        },
                                        {
                                            xtype: 'checkcolumn',
                                            text: CMDBuildUI.locales.Locales.filters.migrate,
                                            dataIndex: 'migrates',
                                            headerCheckbox: true,
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.filters.migrate'
                                            },
                                            listeners: {
                                                headercheckchange: 'onHeaderCheckChange',
                                                checkchange: 'onCheckChange'
                                            }
                                        },
                                        {
                                            xtype: 'checkcolumn',
                                            text: CMDBuildUI.locales.Locales.filters.clone,
                                            itemId: 'cloneCheckColumn',
                                            dataIndex: 'clone',
                                            headerCheckbox: true,
                                            disabled: true,
                                            localized: {
                                                text: 'CMDBuildUI.locales.Locales.filters.clone'
                                            },
                                            bind: {
                                                disabled: '{cloneCheckColumnDisabled}'
                                            },
                                            renderer: function (value, cell, record) {
                                                if (record.get('isDisabled')) {
                                                    cell.tdCls = 'x-item-disabled';
                                                }
                                                if (Ext.isEmpty(value)) {
                                                    return Ext.String.format(
                                                        '<span class="x-grid-checkcolumn"></span>'
                                                    );
                                                }
                                                let klass = '';
                                                if (value) {
                                                    klass = 'x-grid-checkcolumn-checked';
                                                }
                                                return Ext.String.format(
                                                    '<span class="{0} x-grid-checkcolumn "></span>',
                                                    klass
                                                );
                                            },
                                            listeners: {
                                                beforecheckchange: function (column, rowIndex, checked, record) {
                                                    if (record.get('isDisabled')) {
                                                        return false;
                                                    }
                                                },

                                                headercheckchange: 'onHeaderCheckChange',
                                                checkchange: 'onCheckChange'
                                            }
                                        }
                                    ]
                                }
                            ],

                            bind: {
                                store: '{relations}'
                            }
                        }
                    ]
                }
            ],

            fbar: [
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.common.actions.cancel,
                    itemId: 'cancelbtn',
                    ui: 'secondary-action-small',
                    autoEl: {
                        'data-testid': 'card-create-cancel'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.cancel'
                    }
                },
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.common.actions.saveandclose,
                    disabled: true,
                    bind: {
                        disabled: '{saveButtonDisabled}'
                    },
                    ui: 'management-primary-outline-small',
                    itemId: 'saveandclosebtn',
                    autoEl: {
                        'data-testid': 'card-create-saveandclose'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.saveandclose'
                    }
                },
                {
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.common.actions.save,
                    disabled: true,
                    bind: {
                        disabled: '{saveButtonDisabled}'
                    },
                    itemId: 'savebtn',
                    ui: 'management-primary-small',
                    autoEl: {
                        'data-testid': 'card-create-save'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.actions.save'
                    }
                }
            ]
        }
    ]
});
