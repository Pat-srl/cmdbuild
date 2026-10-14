const gotoIconCls = CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid');

Ext.define('CMDBuildUI.view.dashboards.common.components.tile.TileTemplateHelper', {
    singleton: true,

    layouts: [
        {
            value: 'layout #1',
            labelSuffix: '1',
            cls: 'dashboard-tile-card--layout-1',
            tpl: new Ext.XTemplate(
                '<div class="row">',
                '   <div class="icon"><span class="{icon}"></span></div>',
                '   <div class="col">',
                '      <div class="label">{label}</div>',
                '      <tpl if="hasValue">',
                '         <div class="row-baseline-aligned">',
                '            <div class="value">{value}</div>',
                '            <div class="unit">{unit}</div>',
                '         </div>',
                '      </tpl>',
                '   </div>',
                '</div>'
            )
        },
        {
            value: 'layout #2',
            labelSuffix: '2',
            cls: 'dashboard-tile-card--layout-2',
            tpl: new Ext.XTemplate(
                '<div class="col">',
                '   <div class="row-space-between">',
                '      <div class="label">{label}</div>',
                '      <div class="icon"><span class="{icon}"></span></div>',
                '   </div>',
                '   <tpl if="hasValue">',
                '      <div class="row-baseline-aligned">',
                '         <span class="value">{value}</span>',
                '         <div class="unit">{unit}</div>',
                '      </div>',
                '   </tpl>',
                '</div>'
            )
        },
        {
            value: 'layout #3',
            labelSuffix: '3',
            cls: 'dashboard-tile-card--layout-3',
            tpl: new Ext.XTemplate(
                '<div class="icon-panel"><span class="{icon}"></span></div>',
                '<div class="content">',
                '   <div class="label">{label}</div>',
                '   <tpl if="hasValue">',
                '      <div class="row-baseline-aligned">',
                '         <div class="value">{value}</div>',
                '         <tpl if="unit"><div class="unit">{unit}</div></tpl>',
                '      </div>',
                '   </tpl>',
                '</div>'
            )
        },
        {
            value: 'layout #4',
            labelSuffix: '4',
            cls: 'dashboard-tile-card--layout-4',
            tpl: new Ext.XTemplate(
                '<div class="col">',
                '   <div class="label-uppercase">{label}</div>',
                '   <tpl if="hasValue">',
                '      <div class="row-end">',
                '         <div class="value-bigger">{value}</div>',
                '      </div>',
                '   </tpl>',
                '</div>'
            )
        },
        {
            value: 'layout #5',
            labelSuffix: '5',
            cls: 'dashboard-tile-card--layout-5',
            tpl: new Ext.XTemplate(
                '<div class="col-centered">',
                '   <div class="label-uppercase">{label}</div>',
                '   <tpl if="hasValue">',
                '      <div class="value-bigger">{value}</div>',
                '   </tpl>',
                '</div>'
            )
        }
    ],

    getLayoutCls: function (layout) {
        const def = this.layouts.find(function (l) {
            return l.value === layout;
        });
        return (def && def.cls) || '';
    },

    getLayoutTpl: function (layout) {
        const def = this.layouts.find(function (l) {
            return l.value === layout;
        });
        return (def && def.tpl) || '';
    },

    getLayoutStore: function () {
        return Ext.create('Ext.data.Store', {
            fields: ['value', 'label'],
            data: this.layouts.map(function (l) {
                return [
                    l.value,
                    CMDBuildUI.locales.Locales.administration.dashboards.tiles.layout + ' ' + l.labelSuffix
                ];
            })
        });
    },

    /**
     * Checks if a single tile is clickable.
     *
     * @param {Object} tileConfig one entry of component.get('tiles')
     * @returns {Boolean}
     */
    isClickable: function (tileConfig) {
        return !!(tileConfig.goto && (tileConfig.scope === 'all' || tileConfig.cardorfilter));
    },

    getLayoutComponent: function (tileConfig, layout) {
        const layoutCls = this.getLayoutCls(layout);
        const layoutTpl = this.getLayoutTpl(layout);
        const clickable = this.isClickable(tileConfig);

        let value = null;
        if (tileConfig.value != null) {
            value = tileConfig.value;
        } else if (this.hasDataSource(tileConfig)) {
            value = '...';
        }

        return {
            xtype: 'component',
            flex: 1,
            cls:
                'dashboard-tile-item ' +
                (layoutCls || '') +
                (clickable ? ' dashboard-tile-item-link' : '') +
                (Ext.isEmpty(value) ? ' dashboard-tile-item--no-value' : ''),
            style: tileConfig.colortype === 'static' ? this.getColorStyle(tileConfig.fgcolor) : undefined,
            data: this.getTileData(tileConfig, value),
            tpl: layoutTpl
        };
    },

    /**
     * Resolves the tile card style for colortype 'dynamic', reading the
     * accent color from the already-fetched output record.
     *
     * @param {Object} tileConfig one entry of component.get('tiles')
     * @param {Ext.data.Model} record the record loaded via loadTileValue's store
     * @returns {Object|undefined}
     */
    resolveDynamicColors: function (tileConfig, record) {
        if (!record || !tileConfig.fgcolorField) {
            return undefined;
        }

        return this.getColorStyle(record.get(tileConfig.fgcolorField));
    },

    /**
     * Builds the tile card style object from a single accent color (fgcolor).
     *
     * @param {String} accentColor hex color
     * @returns {Object|undefined}
     */
    getColorStyle: function (accentColor) {
        if (!accentColor) return undefined;

        return {
            '--tile-accent-color': accentColor
        };
    },

    /**
     * A tile has a value only if it has a data source and a value field.
     */
    hasDataSource: function (tileConfig) {
        return !!(tileConfig.dataSourceName && tileConfig.valueField);
    },

    /**
     * Builds the template data. hasValue is false when the value is empty
     * (0 is a valid value).
     */
    getTileData: function (tileConfig, value) {
        return {
            icon: tileConfig.icon_font,
            label: tileConfig.label,
            unit: tileConfig.unit,
            value: value,
            hasValue: !Ext.isEmpty(value),
            clickable: this.isClickable(tileConfig)
        };
    }
});
