Ext.define('CMDBuildUI.view.views.items.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.views.items.GridController',
        'CMDBuildUI.view.views.items.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget',
        'CMDBuildUI.view.views.items.View',
        'CMDBuildUI.util.helper.SessionHelper'
    ],

    alias: 'widget.views-items-grid',
    controller: 'views-items-grid',
    viewModel: {
        type: 'views-items-grid'
    },

    title: {
        xtype: "management-title",
        bind: {
            text: '{title}',
            objectTypeName: '{objectTypeName}',
            menuType: '{menuType}'
        }
    },

    config: {
        /**
         * @cfg {String} [objectTypeName]
         */
        objectTypeName: null,

        allowFilter: true,

        /**
         * @cfg {Numeric} [selectedId]
         * Selected card id.
         */
        selectedId: null
    },

    publish: [
        'objectTypeName',
        'selectedId'
    ],

    bind: {
        objectTypeName: '{objectTypeName}',
        store: '{items}',
        selectedId: '{selectedId}',
        selection: '{selection}'
    },

    forceFit: true,
    loadMask: true,

    selModel: {
        pruneRemoved: false // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
    },

    tbar: [{
        xtype: 'textfield',
        name: 'search',
        width: 250,
        emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
        reference: 'searchtext',
        itemId: 'searchtext',
        cls: 'management-input',
        hidden: true,
        autoEl: {
            'data-testid': 'classes-views-grid-searchtext'
        },
        bind: {
            hidden: '{search.disabled}'
        },
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
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
            'data-testid': 'classes-views-grid-refreshbtn'
        },
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
        }
    },
    CMDBuildUI.util.helper.GridHelper.getPrintButtonConfig({
        disabled: true,
        bind: {
            disabled: '{disabledbuttons.print}'
        }
    }), {
        xtype: 'tbfill'
    },
    CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig("items"),
    CMDBuildUI.util.helper.GridHelper.getSaveGridPreferencesTool()
    ],

    plugins: [
        'gridfilters', {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            id: 'forminrowwidget',
            removeWidgetOnCollapse: true,
            widget: {
                xtype: 'views-items-view',
                bind: {}, // do not remove otherwise the view will break
                viewModel: {} // do not remove otherwise the viewmodel will not be initialized
            }
        }
    ],
    autoEl: {
        'data-testid': 'views-items-grid'
    }
});