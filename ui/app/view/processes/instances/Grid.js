Ext.define('CMDBuildUI.view.processes.instances.Grid', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.processes.instances.GridController',
        'CMDBuildUI.view.processes.instances.GridModel',

        // plugins
        'Ext.grid.filters.Filters',
        'CMDBuildUI.components.grid.plugin.FormInRowWidget'
    ],

    mixins: [
        'CMDBuildUI.mixins.grids.AddButtonMixin',
        'CMDBuildUI.mixins.grids.ContextMenuMixin',
        'CMDBuildUI.mixins.grids.Grid'
    ],

    alias: 'widget.processes-instances-grid',
    controller: 'processes-instances-grid',
    viewModel: {
        type: 'processes-instances-grid'
    },

    config: {
        /**
         * @cfg {String} objectTypeName
         *
         * Process name
         */
        objectTypeName: null,

        /**
         * @cfg {Boolean} maingrid
         *
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        /**
         * @cfg {Boolean} allowFilter
         *
         * Set to `false` to hide filters.
         */
        allowFilter: true,

        /**
         * @cfg {Boolean} showAddButton
         *
         * Set to `false` to hide add button.
         */
        showAddButton: true,

        /**
         * @cfg {Numeric|String} [selectedId]
         * Selected card id.
         */
        selectedId: null,

        /**
         * @cfg {String} [selectedActivity]
         * Selected card id.
         */
        selectedActivity: null,

        /**
         * @cfg {Object} filter
         * Advanced filter definition.
         */
        filter: null
    },

    publish: ['selectedId', 'objectTypeName'],

    bind: {
        store: '{instances}',
        selection: '{selected}',
        selectedId: '{selectedId}',
        objectTypeName: '{objectTypeName}'
    },

    title: {
        xtype: 'management-title',
        bind: {
            text: '{title}',
            objectTypeName: '{objectTypeName}',
            menuType: '{menuType}'
        }
    },

    plugins: [
        'gridfilters',
        {
            pluginId: 'forminrowwidget',
            ptype: 'forminrowwidget',
            expandOnDblClick: true,
            removeWidgetOnCollapse: true,
            widget: {
                xtype: 'processes-instances-rowcontainer',
                viewModel: {} // do not remove otherwise the viewmodel will not be initialized
            }
        }
    ],

    features: [
        {
            ftype: 'bufferedsselectall'
        }
    ],

    forceFit: true,
    loadMask: true,
    typeicon: CMDBuildUI.model.menu.MenuItem.icons.process,

    viewConfig: {
        markDirty: false
    },

    selModel: {
        pruneRemoved: false, // See https://docs.sencha.com/extjs/6.2.0/classic/Ext.selection.Model.html#cfg-pruneRemoved
        selType: 'checkboxmodel',
        checkOnly: true,
        mode: 'SINGLE'
    },

    autoEl: {
        'data-testid': 'processes-grid-grid'
    },

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.processes.startworkflow,
            reference: 'addbtn',
            itemId: 'addbtn',
            ui: 'management-primary',
            disabled: true,
            hidden: true,
            bind: {
                text: '{addbtn.text}',
                hidden: '{addbtn.hidden}'
            },
            autoEl: {
                'data-testid': 'processes-instances-grid-addbtn'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.processes.startworkflow'
            }
        },
        {
            xtype: 'textfield',
            name: 'search',
            width: 250,
            emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
            reference: 'searchtext',
            itemId: 'searchtext',
            cls: 'management-input',
            hidden: true,
            bind: {
                value: '{search.value}',
                hidden: '{search.disabled || !allowfilter}'
            },
            listeners: {
                specialkey: 'onSearchSpecialKey'
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
            },
            autoEl: {
                'data-testid': 'processes-instances-grid-searchtext'
            },
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
            }
        },
        {
            xtype: 'combobox',
            name: 'status',
            reference: 'statuscombo',
            itemId: 'statuscombo',
            width: 200,
            cls: 'management-input',
            forceSelection: true,
            displayField: 'text',
            valueField: '_id',
            editable: false,
            hidden: true,
            disabled: true,
            emptyText: CMDBuildUI.locales.Locales.processes.activeprocesses,
            triggers: {
                clear: {
                    cls: 'x-form-clear-trigger',
                    handler: function (combo, trigger, eOpts) {
                        combo.fireEvent('cleartrigger', combo, trigger, eOpts);
                    }
                }
            },
            bind: {
                value: '{statuscombo.value}',
                disabled: '{statuscombo.disabled}',
                hidden: '{statuscombo.hidden}',
                store: '{statuscombostore}'
            },
            autoEl: {
                'data-testid': 'processes-instances-grid-filterbystatus'
            },
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.processes.activeprocesses'
            }
        },
        {
            xtype: 'filters-launcher',
            reference: 'filterslauncher',
            storeName: 'instances',
            bind: {
                selected: '{defaultfilter}'
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
                'data-testid': 'processes-instances-grid-refreshbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
            }
        },
        {
            xtype: 'button',
            itemId: 'contextMenuBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bars', 'solid'),
            ui: 'management-neutral-action',
            tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'processes-instances-grid-contextmenubtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.grid.opencontextualmenu'
            }
        },
        CMDBuildUI.util.helper.GridHelper.getPrintButtonConfig({
            disabled: true,
            bind: {
                disabled: '{btnCanPrintDisabled}'
            }
        }),
        {
            xtype: 'tbfill'
        },
        CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig('instances'),
        CMDBuildUI.util.helper.GridHelper.getSaveGridPreferencesTool()
    ],

    /**
     * Method callend when selected id changes.
     */
    updateSelectedId: function (newvalue, oldvalue) {
        this.fireEvent('selectedidchanged', this, newvalue, oldvalue);
    },

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * @return {CMDBuildUI.model.lookups.Lookup}
     */
    getOpenRunningStatusValue: function () {
        const type = CMDBuildUI.model.lookups.LookupType.getLookupTypeFromName(
            CMDBuildUI.model.processes.Process.flowstatus.lookuptype
        );
        if (type) {
            return type.values().findRecord('code', 'open.running');
        }
        return;
    },

    /**
     * @return {CMDBuildUI.view.processes.instances.Grid}
     */
    getContextMenuGrid: function () {
        return this;
    }
});
