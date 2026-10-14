Ext.define('CMDBuildUI.view.relations.list.edit.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.edit.GridController',
        'CMDBuildUI.view.relations.list.edit.GridModel',
        'Ext.grid.filters.Filters'
    ],

    mixins: ['CMDBuildUI.mixins.grids.AddButtonMixin', 'CMDBuildUI.view.relations.list.AddEditGrid'],

    alias: 'widget.relations-list-edit-grid',
    controller: 'relations-list-edit-grid',
    viewModel: {
        type: 'relations-list-edit-grid'
    },

    statics: {
        disabledcls: Ext.baseCSSPrefix + 'item-disabled'
    },

    bind: {
        store: '{records}',
        selection: '{selection}'
    },

    plugins: ['gridfilters'],

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.classes.cards.addcard,
            reference: 'addcardbtn',
            itemId: 'addcardbtn',
            ui: 'management-primary',
            localized: {
                text: 'CMDBuildUI.locales.Locales.classes.cards.addcard'
            },
            autoEl: {
                'data-testid': 'relations-list-add-grid-addcardbtn'
            },
            bind: {
                text: '{addbtn.text}',
                disabled: '{addbtn.disabled}',
                hidden: '{addbtn.hidden}'
            }
        },
        {
            xtype: 'textfield',
            name: 'search',
            width: 250,
            emptyText: CMDBuildUI.locales.Locales.classes.cards.searchtext,
            reference: 'searchtextinput',
            itemId: 'searchtextinput',
            cls: 'management-input',
            localized: {
                text: 'CMDBuildUI.locales.Locales.classes.cards.searchtext'
            },
            autoEl: {
                'data-testid': 'relations-list-add-grid-searchtextinput'
            },
            bind: {
                value: '{searchvalue}'
            },
            triggers: {
                search: {
                    cls: Ext.baseCSSPrefix + 'form-search-trigger',
                    handler: 'onSearchSubmit'
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: 'onSearchClear'
                }
            }
        },
        {
            xtype: 'button',
            itemId: 'refreshBtn',
            reference: 'refreshBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            ui: 'management-neutral-action',
            tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
            autoEl: {
                'data-testid': 'relations-list-add-grid-refreshbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
            }
        },
        {
            xtype: 'filters-launcher',
            storeName: 'records',
            reference: 'filterslauncher'
        },
        {
            xtype: 'tbfill'
        },
        CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig('records')
    ],

    layout: 'fit',
    forceFit: true,

    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass
});
