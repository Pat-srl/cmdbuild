Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.Helps', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.helps.HelpsController',
        'CMDBuildUI.view.administration.content.processes.tabitems.helps.HelpsModel'
    ],
    alias: 'widget.administration-content-processes-tabitems-helps-helps',
    controller: 'administration-content-processes-tabitems-helps-helps',
    viewModel: {
        type: 'administration-content-processes-tabitems-helps-helps'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    bind: {
        store: '{activitiesWithHelp}'
    },

    reserveScrollbar: true,

    columns: [
        {
            dataIndex: 'description',
            align: 'left'
        },
        {
            xtype: 'actioncolumn',
            minWidth: 110,
            maxWidth: 110,
            align: 'center',
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' +
                        CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.common.actions.open;
                    },
                    handler: 'onOpenActivityItemClick'
                },
                {
                    isActionDisabled: function () {
                        return !this.lookupViewModel().get('toolAction._canUpdate');
                    },
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.common.actions.edit;
                    },
                    handler: 'onEditActivityItemClick'
                },
                {
                    isActionDisabled: function () {
                        return !this.lookupViewModel().get('toolAction._canUpdate');
                    },
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.administration.emails.remove;
                    },
                    handler: 'onDeleteActivityItemClick'
                }
            ]
        }
    ],

    autoEl: {
        'data-testid': 'administration-content-tasks-grid'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },
    labelWidth: 'auto',

    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            padding: '5 10 5 10',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({}, 'activityhelps', 'theProcess', [
                {
                    xtype: 'button',
                    itemId: 'addBtn',
                    text: CMDBuildUI.locales.Locales.administration.processes.helps.addhelp,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.processes.helps.addhelp'
                    },
                    ui: 'administration-primary-outline-small',
                    autoEl: {
                        'data-testid': 'administration-process-form-toolbar-addHelpBtn'
                    },
                    menu: {
                        items: []
                    },
                    bind: {
                        disabled: '{!toolAction._canUpdate}'
                    }
                },
                {
                    xtype: 'textfield',
                    name: 'search',
                    width: 250,
                    emptyText: CMDBuildUI.locales.Locales.administration.viewfilters.emptytexts.searchingrid, // Search ...
                    localized: {
                        emptyText: 'CMDBuildUI.locales.Locales.administration.viewfilters.emptytexts.searchingrid' // Search ...
                    },
                    cls: 'administration-input',
                    reference: 'searchtext',
                    itemId: 'searchtext',
                    bind: {
                        hidden: '{!canFilter}'
                    },
                    listeners: {
                        change: 'onSearchSpecialKey'
                    },
                    triggers: {
                        search: {
                            cls: Ext.baseCSSPrefix + 'form-search-trigger',
                            handler: 'onSearchSubmit',
                            autoEl: {
                                'data-testid': 'administration-process-form-toolbar-search-trigger'
                            }
                        },
                        clear: {
                            cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                            handler: 'onSearchClear',
                            autoEl: {
                                'data-testid': 'administration-process-form-toolbar-search-clear-trigger'
                            }
                        }
                    },
                    autoEl: {
                        'data-testid': 'administration-process-form-toolbar-search-help'
                    }
                },
                {
                    xtype: 'tbfill'
                }
            ])
        }
    ]
});
