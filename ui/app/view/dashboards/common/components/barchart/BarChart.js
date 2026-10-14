Ext.define('CMDBuildUI.view.dashboards.common.components.barchart.BarChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    alias: 'widget.dashboards-components-barchart-barchart',

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
                        let html;
                        if (yfields.length > 1) {
                            html = Ext.String.format(
                                '<strong>{0} - {1}:</strong> {2}',
                                record.get(xfield),
                                component.get(Ext.String.format('_{0}_translation', item.field)),
                                record.get(item.field)
                            );
                        } else {
                            html = Ext.String.format(
                                '<strong>{0}:</strong> {1}',
                                record.get(xfield),
                                record.get(item.field)
                            );
                        }
                        tooltip.setHtml(html);
                    }
                }
            }
        ];

        const axes = [
            {
                type: 'numeric',
                position: flipxy ? 'bottom' : 'left',
                fields: yfields,
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
                position: flipxy ? 'left' : 'bottom',
                fields: xfield,
                label: {
                    fontSize: 11,
                    fillStyle: '#222'
                },
                grid: {
                    stroke: '#ececec',
                    lineWidth: 1
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
            flipXY: flipxy,
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
