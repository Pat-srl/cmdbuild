Ext.define('CMDBuildUI.view.dashboards.common.components.gaugechart.GaugeChart', {
    extend: 'CMDBuildUI.view.dashboards.common.components.basechart.BaseChart',

    alias: 'widget.dashboards-components-gaugechart-gaugechart',

    initChart: function () {
        const component = this.getItemDefinition();
        let fgcolor = component.get('fgcolor');
        let bgcolor = component.get('bgcolor');
        if (Ext.isEmpty(fgcolor) || Ext.isEmpty(bgcolor)) {
            const theme = Ext.Factory.chartTheme(cmdbuildConfig.manifest);
            const theme_colors = theme.getColors();
            fgcolor = fgcolor || theme_colors[0];
            bgcolor = bgcolor || theme_colors[1];
        }

        const field = component.get('singleSeriesField');

        this.addChart({
            xtype: 'polar',
            theme: cmdbuildConfig.manifest,
            legend: component.get('legend'),
            bind: {
                store: '{records}'
            },
            axes: {
                type: 'numeric',
                position: 'gauge',
                majorTickSteps: component.get('steps'),
                minimum: component.get('minimum'),
                maximum: component.get('maximum'),
                style: {
                    strokeStyle: 'white'
                },
                label: {
                    fontSize: 12,
                    fontFamily: 'inherit',
                    fillStyle: '#595959'
                }
            },
            series: {
                type: 'gauge',
                donut: 50,
                angleField: field,
                totalAngle: Math.PI,
                colors: [fgcolor, bgcolor],
                needleLength: 100
            }
        });
    }
});
