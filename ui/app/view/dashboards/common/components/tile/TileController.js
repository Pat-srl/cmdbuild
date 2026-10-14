Ext.define('CMDBuildUI.view.dashboards.common.components.tile.TileController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.ComponentController',
    alias: 'controller.dashboards-components-tile-tile',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * On component before render
     * @param {CMDBuildUI.view.dashboards.components.tile.Tile} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const component = view.getItemDefinition();
        this.initTileConfig(view, component);
    },

    /**
     * Builds the tiles container and one card per tile config, then
     * kicks off the async load of each tile's value.
     *
     * @param {CMDBuildUI.view.dashboards.components.tile.Tile} view
     * @param {CMDBuild.model.dashboards.Component} component
     */
    initTileConfig: function (view, component) {
        const me = this;
        const tiles = component.get('tiles') || [];
        const templateHelper = CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper;
        const layout = component.get('layout');
        const tilesPerRow = component.get('tilesPerRow') || tiles.length || 1;

        const tilesContainer = view.add({
            xtype: 'container',
            cls: 'dashboard-tile-view',
            style: {
                '--tiles-per-row': tilesPerRow
            }
        });

        tiles.forEach(function (tileConfig) {
            const card = tilesContainer.add(
                Ext.merge(templateHelper.getLayoutComponent(tileConfig, layout), {
                    listeners: {
                        afterrender: function (cmp) {
                            if (templateHelper.isClickable(tileConfig)) {
                                cmp.getEl().on('click', me.onTileClick.bind(me, tileConfig));
                            }
                        }
                    }
                })
            );

            if (templateHelper.hasDataSource(tileConfig)) {
                me.loadTileValue(tileConfig, card);
            }
        });
    },

    privates: {
        /**
         * Loads the single output value for one tile by calling the
         * function's outputs endpoint, then updates the tile's card.
         *
         * @param {Object} tileConfig one entry of component.get('tiles')
         * @param {Ext.Component} card the tile card to update once loaded
         */
        loadTileValue: function (tileConfig, card) {
            const proxyurl = CMDBuildUI.util.api.Functions.getOutputsUrlByFunctionName(tileConfig.dataSourceName);
            const templateHelper = CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper;

            const store = Ext.create('Ext.data.Store', {
                proxy: {
                    type: 'baseproxy',
                    url: proxyurl,
                    extraParams: {
                        filter: '',
                        parameters: Ext.JSON.encode({})
                    }
                },
                autoLoad: false
            });

            store.load(function (records, operation, success) {
                const record = success && records && records.length ? records[0] : null;
                const value = record ? record.get(tileConfig.valueField) : null;

                if (!card.destroyed) {
                    card.update(templateHelper.getTileData(tileConfig, value));
                    card.toggleCls('dashboard-tile-item--no-value', Ext.isEmpty(value));

                    if (tileConfig.colortype === 'dynamic') {
                        const style = templateHelper.resolveDynamicColors(tileConfig, record);
                        if (style) {
                            const accentColor = style['--tile-accent-color'];
                            delete style['--tile-accent-color'];

                            card.getEl().setStyle(style);

                            if (accentColor) {
                                card.getEl().dom.style.setProperty('--tile-accent-color', accentColor);
                            }
                        }
                    }
                }

                store.destroy();
            });
        },

        /**
         * Handles the click event for a tile.
         *
         * @param {Object} tileConfig
         */
        onTileClick: function (tileConfig) {
            const name = tileConfig.goto;

            switch (tileConfig.scope) {
                case 'all':
                    if (CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(name) === 'process') {
                        this.redirectTo(Ext.String.format('#processes/{0}/instances', name));
                    } else {
                        this.redirectTo(Ext.String.format('#classes/{0}/cards/', name));
                    }
                    break;

                case 'filtered':
                    this.applySearchFilter(tileConfig).then(basePath => {
                        if (basePath) {
                            this.redirectTo(basePath);
                        }
                    });
                    break;

                case 'selected':
                    if (CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(name) === 'process') {
                        this.redirectTo(
                            Ext.String.format('#processes/{0}/instances/{1}/view', name, tileConfig.cardorfilter)
                        );
                    } else {
                        this.redirectTo(
                            Ext.String.format('#classes/{0}/cards/{1}/view', name, tileConfig.cardorfilter)
                        );
                    }
                    break;
            }
        },

        /**
         * Resolves the base path for a tile's 'goto' target (class or process) and,
         * for scope 'filtered', sets that item's current filter to the saved filter
         * named in tileConfig.cardorfilter (or clears it if not found) before
         * returning the base path to redirect to.
         *
         * @param {Object} tileConfig one entry of component.get('tiles')
         * @returns {Ext.promise.Promise<String|null>}
         */
        applySearchFilter: function (tileConfig) {
            const goto = tileConfig.goto;
            const searchFilterName = tileConfig.cardorfilter;

            const objectType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(goto);
            const basePath = this.getTileSourceBasePath(goto, objectType);
            const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(goto, objectType);

            if (!item || !item.setCurrentFilter) {
                return Promise.resolve(basePath);
            }

            if (!searchFilterName) {
                item.setCurrentFilter(null);
                return Promise.resolve(basePath);
            }

            return item.getFilters().then(function (filters) {
                item.setCurrentFilter(filters.findRecord('name', searchFilterName) || null);
                return basePath;
            });
        },

        /**
         * @param {String} name class or process name (tileConfig.goto)
         * @param {String} objectType
         * @returns {String|null}
         */
        getTileSourceBasePath: function (name, objectType) {
            let path;

            switch (objectType) {
                case CMDBuildUI.model.dashboards.Component.dataSourceTypes.klass:
                    path = 'classes/{0}/cards';
                    break;
                case CMDBuildUI.model.dashboards.Component.dataSourceTypes.process:
                    path = 'processes/{0}/instances';
                    break;
            }

            return path ? Ext.String.format(path, name) : null;
        }
    }
});
