Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.forms.Forms', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.processes.tabitems.forms.FormsController',
        'CMDBuildUI.view.administration.content.processes.tabitems.forms.FormsModel'
    ],
    alias: 'widget.administration-content-processes-tabitems-forms-forms',
    controller: 'administration-content-processes-tabitems-forms-forms',
    viewModel: {
        type: 'administration-content-processes-tabitems-forms-forms'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true,
        selected: null
    },

    bind: {
        store: '{activitiesWithForm}'
    },

    reserveScrollbar: true,

    columns: [
        {
            // text: 'Activity', // Type
            dataIndex: 'description',
            align: 'left',
            flex: 1
        },
        {
            minWidth: 110,
            maxWidth: 110,
            xtype: 'actioncolumn',
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
                },
                {
                    isActionDisabled: function () {
                        return !this.lookupViewModel().get('toolAction._canUpdate');
                    },
                    getClass: function (value, metadata, record, row, col, store) {
                        if (record.get('formStructure').active === false) {
                            return (
                                'attachments-grid-action ' +
                                CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'regular')
                            );
                        } else {
                            return (
                                'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('ban', 'solid')
                            );
                        }
                    },
                    getTip: function (value, metadata, record, row, col, store) {
                        if (record.get('formStructure').active === false) {
                            return CMDBuildUI.locales.Locales.administration.common.actions.enable;
                        } else {
                            return CMDBuildUI.locales.Locales.administration.common.actions.disable;
                        }
                    },
                    handler: 'onToggleActiveActivityItemClick'
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
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({}, 'activityforms', 'theProcess', [
                {
                    xtype: 'button',
                    itemId: 'addBtn',
                    text: CMDBuildUI.locales.Locales.administration.forms.addform,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.forms.addform'
                    },
                    ui: 'administration-primary-outline-small',
                    autoEl: {
                        'data-testid': 'administration-process-form-toolbar-addFormBtn'
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
                        'data-testid': 'administration-process-form-toolbar-search-form'
                    }
                },
                {
                    xtype: 'tbfill'
                }
            ])
        }
    ]
});
