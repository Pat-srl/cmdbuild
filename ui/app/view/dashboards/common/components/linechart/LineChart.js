Ext.define('CMDBuildUI.view.dashboards.common.components.linechart.LineChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    alias: 'widget.dashboards-components-linechart-linechart',

    initChart: function () {
        const component = this.getItemDefinition();
        const series = [];
        const xfield = component.get('categoryAxisField');
        const xlabel = component.get('_categoryAxisLabel_translation') || component.get('categoryAxisLabel');
        const yfields = component.get('valueAxisFields') || [];
        const ylabel = component.get('_valueAxisLabel_translation') || component.get('valueAxisLabel');

        // generate series
        yfields.forEach(function (yfield) {
            series.push({
                type: 'line',
                xField: xfield,
                yField: yfield,
                title: component.get(Ext.String.format('_{0}_translation', yfield)) || yfield,
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
                    alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1,
                    renderer: function (tooltip, record, item) {
                        tooltip.setHtml(
                            Ext.String.format('<strong>{0}:</strong> {1}', record.get(xfield), record.get(item.field))
                        );
                    }
                }
            });
        });

        this.addChart({
            xtype: 'cartesian',
            reference: 'chart',
            theme: cmdbuildConfig.manifest,
            legend: component.get('legend'),
            animation: {
                duration: 200
            },
            bind: {
                store: '{records}'
            },
            series: series,
            insetPadding: {
                right: 25
            },
            innerPadding: 10,
            axes: [
                {
                    type: 'numeric',
                    position: 'left',
                    grid: true,
                    title: ylabel,
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
                    grid: true,
                    title: xlabel,
                    label: {
                        fontSize: 11,
                        fillStyle: '#222'
                    },
                    grid: {
                        stroke: '#ececec',
                        lineWidth: 1
                    }
                }
            ]
        });
    }
});
