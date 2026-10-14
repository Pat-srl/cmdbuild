Ext.define('CMDBuildUI.view.dashboards.common.components.piechart.PieChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    alias: 'widget.dashboards-components-piechart-piechart',

    initChart: function () {
        const component = this.getItemDefinition();
        const labelfield = component.get('labelField');
        const valuefield = component.get('singleSeriesField');
        const useAccessibleStyle = component.get('useAccessibleStyle');

        this.addChart({
            xtype: 'polar',
            theme: cmdbuildConfig.manifest,
            bind: {
                store: '{records}'
            },
            legend: {
                docked: 'bottom',
                label: {
                    fontSize: 12
                }
            },
            interactions: ['rotate'],
            insetPadding: 5,
            innerPadding: 5,
            sprites: [],
            series: [
                {
                    type: 'pie',
                    angleField: valuefield,
                    donut: 50,
                    rotation: -90,
                    label: {
                        field: labelfield,
                        display: 'none'
                    },
                    highlight: {
                        margin: 4
                    },
                    renderer: function (sprite, config, rendererData, index) {
                        const itemColor =
                            sprite.attr.fillStyle ||
                            (rendererData.series && rendererData.series.getThemeColor
                                ? rendererData.series.getThemeColor(index)
                                : null) ||
                            '#222';

                        return {
                            color: CMDBuildUI.util.Color.getLighterColor(itemColor, 0.1),
                            stroke: useAccessibleStyle ? '#fff' : 'none',
                            lineWidth: useAccessibleStyle ? 2 : 0
                        };
                    },
                    tooltip: {
                        trackMouse: true,
                        alwaysOnTop: CMDBuildUI.util.Utilities._popupAlwaysOnTop + 1,
                        renderer: function (tooltip, record, item) {
                            if (Ext.isEmpty(record.get('percentage'))) {
                                let sum = 0;
                                this.getStore()
                                    .getRange()
                                    .forEach(function (i) {
                                        sum += i.get(valuefield);
                                    });
                                record.set('percentage', Ext.Number.toFixed((100 / sum) * record.get(valuefield), 2));
                            }
                            tooltip.setHtml(
                                Ext.String.format(
                                    '<strong>{0}:</strong> {1}',
                                    record.get(labelfield),
                                    Ext.String.format('{0} - {1}%', record.get(valuefield), record.get('percentage'))
                                )
                            );
                        }
                    }
                }
            ]
        });
    }
});
