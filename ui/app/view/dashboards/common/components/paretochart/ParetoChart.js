Ext.define('CMDBuildUI.view.dashboards.common.components.paretochart.ParetoChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    requires: ['CMDBuildUI.view.dashboards.common.components.paretochart.ParetoChartController'],

    controller: 'dashboards-components-paretochart-paretochart',
    alias: 'widget.dashboards-components-paretochart-paretochart',

    getCustomAttributes: function () {
        return [
            {
                active: true,
                description: CMDBuildUI.locales.Locales.dashboards.pareto.cumulative,
                hidden: false,
                index: 0,
                mandatory: false,
                name: 'Cumulative',
                showInGrid: true,
                type: 'double',
                unitOfMeasure: null,
                unitOfMeasureLocation: null,
                writable: true,
                _description_translation: CMDBuildUI.locales.Locales.dashboards.pareto.cumulative,
                _id: 'Cumulative'
            },
            {
                active: true,
                description: CMDBuildUI.locales.Locales.dashboards.pareto.cumulativeperc,
                hidden: false,
                index: 0,
                mandatory: false,
                name: 'CumulativePerc',
                showInGrid: true,
                type: 'integer',
                unitOfMeasure: '%',
                unitOfMeasureLocation: null,
                writable: true,
                _description_translation: CMDBuildUI.locales.Locales.dashboards.pareto.cumulativeperc,
                _id: 'CumulativePerc'
            }
        ];
    },

    initChart: function () {
        const component = this.getItemDefinition();
        const xfield = component.get('categoryAxisField');
        const xlabel = component.get('_categoryAxisLabel_translation') || component.get('categoryAxisLabel');
        const yfields = component.get('valueAxisFields') || [];
        const ylabel = component.get('_valueAxisLabel_translation') || component.get('valueAxisLabel');
        const flipxy = component.get('componentOrientation') === 'horizontal';
        // generate series
        const series = [
            {
                type: 'bar',
                xField: xfield,
                yField: yfields,
                title: this.getFieldsLabels(component, yfields),
                stacked: true,
                style: {
                    minGapWidth: 4,
                    lineWidth: 1
                },
                highlightCfg: {
                    lineWidth: 1,
                    stroke: '#818181'
                },
                renderer: function (sprite, config, rendererData, index) {
                    const itemColor =
                        sprite.attr.fillStyle ||
                        (rendererData.series && rendererData.series.getThemeColor
                            ? rendererData.series.getThemeColor(index)
                            : null) ||
                        '#222';

                    return {
                        color: CMDBuildUI.util.Color.getLighterColor(itemColor, 0.4),
                        stroke: CMDBuildUI.util.Color.getLighterColor(itemColor, 0.1)
                    };
                },
                tooltip: {
                    trackMouse: true,
                    alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1,
                    renderer: function (tooltip, record, item) {
                        tooltip.setHtml(
                            Ext.String.format('<strong>{0}:</strong> {1}', record.get(xfield), record.get(item.field))
                        );
                    }
                }
            },
            {
                type: 'line',
                title: 'Cumulative %',
                xField: xfield,
                yField: 'CumulativePerc',
                style: {
                    lineWidth: 1.5
                },
                marker: {
                    radius: 3
                },
                highlightCfg: {
                    fillStyle: 'black',
                    stroke: 'black'
                },
                listeners: {
                    storechange: function (series, newStore) {
                        const chart = series.getChart();
                        if (chart) {
                            const colors = chart.getTheme().getColors();
                            const themeColor = colors[1] || colors[0] || '#222';

                            series.setStyle({
                                stroke: CMDBuildUI.util.Color.getLighterColor(themeColor, 0.2),
                                color: CMDBuildUI.util.Color.getLighterColor(themeColor, 0.2)
                            });
                        }
                    }
                },

                tooltip: {
                    trackMouse: true,
                    renderer: function (tooltip, record, item) {
                        tooltip.setHtml(
                            '<div style="text-align: center; font-weight: bold">' +
                                record.get('CumulativePerc') +
                                '%</div>'
                        );
                    }
                }
            }
        ];

        const axes = [
            {
                type: 'numeric',
                position: 'left',
                fields: yfields,
                title: ylabel,
                grid: true,
                minimum: 0,
                label: {
                    fontSize: 11,
                    fillStyle: '#222'
                },
                grid: {
                    stroke: '#ececec',
                    lineWidth: 1
                }
            },
            {
                type: 'category',
                position: 'bottom',
                fields: xfield,
                title: xlabel,
                grid: true,
                label: {
                    fontSize: 11,
                    fillStyle: '#222'
                },
                grid: {
                    stroke: '#ececec',
                    lineWidth: 1
                }
            },
            {
                type: 'numeric',
                position: 'right',
                fields: ['CumulativePerc'],
                reconcileRange: true,
                majorTickSteps: 10,
                minimum: 0,
                label: {
                    fontSize: 11,
                    fillStyle: '#222'
                },
                renderer: function (axis, label, layoutContext) {
                    return label + '%';
                }
            }
        ];

        this.addChart({
            xtype: 'cartesian',
            animation: {
                easing: 'easeOut',
                duration: 400
            },
            legend: component.get('legend'),
            flipXY: false,
            theme: cmdbuildConfig.manifest,
            axes: axes,
            series: series,
            bind: {
                store: '{records}'
            }
        });
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.dashboards.Component} component
         * @param {String[]} fields
         */
        getFieldsLabels: function (component, fields) {
            return fields.map(function (field) {
                return component.get(Ext.String.format('_{0}_translation', field)) || field;
            });
        }
    }
});
