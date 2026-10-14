
Ext.define('CMDBuildUI.view.relations.list.add.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.relations.list.add.GridController',
        'CMDBuildUI.view.relations.list.add.GridModel',
        'Ext.grid.filters.Filters'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.AddButtonMixin',
        'CMDBuildUI.view.relations.list.AddEditGrid'
    ],

    alias: 'widget.relations-list-add-grid',
    controller: 'relations-list-add-grid',
    viewModel: {
        type: 'relations-list-add-grid'
    },

    statics: {
        disabledcls: Ext.baseCSSPrefix + "item-disabled",
        errorcls: Ext.baseCSSPrefix + "cell-error"
    },

    config: {
        /**
         * @cfg {Object[]} relationAttributes
         */
        relationAttributes: []
    },

    bind: {
        store: '{records}',
        selection: '{selection}'
    },

    plugins: [
        'gridfilters',
        {
            ptype: 'cellediting',
            clicksToEdit: 1
        }],

    tbar: [{
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
    }, {
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
    }, {
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
    }, {
        xtype: 'button',
        ui: 'management-neutral-action',
        itemId: 'checkedonly',
        text: CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly,
        localized: {
            text: 'CMDBuildUI.locales.Locales.widgets.linkcards.checkedonly'
        },
        enableToggle: true,
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('list-ul', 'solid'),
        hidden: true,
        bind: {
            hidden: '{singleSelection}'
        }
    }, {
        xtype: 'filters-launcher',
        storeName: 'records',
        reference: 'filterslauncher'
    }, {
        xtype: 'tbfill'
    }, CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("records")],

    layout: 'fit',
    forceFit: true,

    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass
});
