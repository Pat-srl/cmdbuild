Ext.define('CMDBuildUI.view.classes.cards.grid.Container', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.classes.cards.grid.ContainerController',
        'CMDBuildUI.view.classes.cards.grid.ContainerModel'
    ],

    mixins: ['CMDBuildUI.mixins.grids.ContextMenuMixin', 'CMDBuildUI.mixins.grids.AddButtonMixin'],

    alias: 'widget.classes-cards-grid-container',
    controller: 'classes-cards-grid-container',
    viewModel: {
        type: 'classes-cards-grid-container'
    },

    config: {
        /**
         * @cfg {Boolean} maingrid
         *
         * Set to true when the grid is added in main content.
         */
        maingrid: false,

        /**
         * @cfg {String} objectTypeName
         * Class name.
         */
        objectTypeName: null,

        /**
         * @cfg {Object} filter
         * Advanced filter definition.
         */
        filter: null
    },

    autoEl: {
        'data-testid': 'cards-card-view-container'
    },

    layout: 'card',
    typeicon: CMDBuildUI.model.menu.MenuItem.icons.klass,

    title: {
        xtype: 'management-title',
        bind: {
            text: '{title}',
            objectTypeName: '{objectTypeName}',
            menuType: '{menuType}'
        }
    },

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.classes.cards.addcard,
            reference: 'addcard',
            itemId: 'addcard',
            ui: 'management-primary',
            autoEl: {
                'data-testid': 'classes-cards-grid-container-addbtn'
            },
            bind: {
                text: '{addbtn.text}',
                hidden: '{btnHide}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.classes.cards.addcard'
            }
        },
        {
            xtype: 'button',
            itemId: 'addcardgis',
            ui: 'management-primary',
            autoEl: {
                'data-testid': 'classes-cards-grid-container-addgisbtn'
            },
            hidden: true,
            disabled: true,
            enableToggle: true,
            allowDepress: false,
            addGeoAttribute: false,
            bind: {
                hidden: '{addgisbtn.hidden}'
            },
            listeners: {
                menuhide: function (button, menu, eOpts) {
                    button.setPressed(false);
                }
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
            autoEl: {
                'data-testid': 'classes-cards-grid-container-searchtext'
            },
            hidden: true,
            bind: {
                hidden: '{search.disabled}',
                value: '{search.value}'
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
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
            }
        },
        {
            xtype: 'filters-launcher',
            storeName: 'cards',
            reference: 'filterslauncher',
            bind: {
                selected: '{defaultfilter}'
            }
        },
        {
            xtype: 'button',
            itemId: 'refreshBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            ui: 'management-neutral-action-small',
            tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
            autoEl: {
                'data-testid': 'classes-cards-grid-container-refreshbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
            }
        },
        {
            xtype: 'thematisms-launcher',
            bind: {
                hidden: '{!btnHide}'
            }
        },
        {
            xtype: 'button',
            itemId: 'contextMenuBtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bars', 'solid'),
            ui: 'management-neutral-action-small',
            tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'classes-cards-grid-container-contextmenubtn'
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
        CMDBuildUI.util.helper.GridHelper.getBufferedGridCounterConfig('cards'),
        CMDBuildUI.util.helper.GridHelper.getSaveGridPreferencesTool(),
        {
            xtype: 'button',
            reference: 'showMapListButton',
            itemId: 'showMapListButton',
            hidden: true,
            ui: 'management-neutral-action-small',
            bind: {
                text: '{btnMapText}',
                hidden: '{btnMapHidden}'
            },
            listeners: {
                click: 'onShowMapListButtonClick'
            },
            autoEl: {
                'data-testid': 'classes-cards-grid-container-togglemapbtn'
            }
        }
    ],

    /**
     * Return true if the grid has been added in main container.
     * @return {Boolean}
     */
    isMainGrid: function () {
        return this.maingrid;
    },

    /**
     * Returns the grid on which apply context menu actions.
     *
     * @override
     * @return {Ext.grid.Panel}
     */
    getContextMenuGrid: function () {
        return this.lookupReference(this.referenceGridId);
    },

    /**
     * Returns the map on which apply context menu actions.
     *
     * @return {Ext.panel.Panel}
     */
    getContextMenuMap: function () {
        return this.lookupReference(this.referenceMapId);
    },

    /**
     *
     * @returns {Ext.grid.Panel}
     */
    getListTab: function () {
        return this.down('map-tab-cards-list');
    },

    /**
     *
     * @returns {Ext.tree.Panel}
     */
    getNavigationTreeTab: function () {
        return this.down('map-tab-cards-navigationtree');
    },

    /**
     *
     * @returns {Ext.panel.Panel}
     */
    getMapContainer: function () {
        return this.down('map-container');
    },

    /**
     *
     * @returns {Ext.panel.Panel}
     */
    getViewMap: function () {
        return this.down('map-map');
    },

    privates: {
        /**
         * @property referenceGridId
         */
        referenceGridId: 'classes-cards-grid-grid-view',

        /**
         * @property referenceMapId
         */
        referenceMapId: 'map-container'
    }
});
