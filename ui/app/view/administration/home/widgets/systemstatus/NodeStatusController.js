Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatusController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-systemstatus-nodestatus',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#memoryLoadButton': {
            click: 'onClickMemoryLoadButton'
        },
        '#systemLoadButton': {
            click: 'onClickSystemLoadButton'
        },
        '#activeSessionsButton': {
            click: 'onClickActiveSessionsButton'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatus} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        vm.set('nodeId', view.getItemId());
        this.onClickMemoryLoadButton();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickMemoryLoadButton: function (button, event, eOpts) {
        const me = this;
        const fields = ['system_memory_total', 'system_memory_used', 'java_memory_total'];
        const series = [
            {
                type: 'area',
                title: CMDBuildUI.locales.Locales.administration.home.system_total,
                xField: 'date',
                yField: 'system_memory_total',
                showInLegend: this._advancedmode,
                style: {
                    opacity: 0.4
                },
                colors: ['#A1AEB5']
            },
            {
                type: 'area',
                title: CMDBuildUI.locales.Locales.administration.home.system_used,
                xField: 'date',
                yField: 'system_memory_used',
                colors: ['#005CA9'],
                style: {
                    opacity: 0.8
                },
                tooltip: {
                    trackMouse: true,
                    renderer: 'onDataTooltipRender'
                }
            },
            {
                type: 'area',
                title: CMDBuildUI.locales.Locales.administration.home.java_total,
                xField: 'date',
                yField: 'java_memory_total',
                colors: ['#4B8DC5'],
                style: {
                    opacity: 0.8
                },
                tooltip: {
                    trackMouse: true,
                    renderer: 'onDataTooltipRender'
                }
            }
        ];

        if (this._advancedmode) {
            fields.push('java_memory_used');
            series.push({
                type: 'area',
                title: CMDBuildUI.locales.Locales.administration.home.java_used,
                xField: 'date',
                yField: 'java_memory_used',
                colors: ['#3A8BCF'],
                style: {
                    opacity: 0.8
                },
                tooltip: {
                    trackMouse: true,
                    renderer: 'onDataTooltipRender'
                }
            });
        }

        const config = {
            legend: {
                type: 'dom',
                padding: 5,
                docked: 'top'
            },
            fields: fields,
            renderer: 'onMemoryLabelRender',
            otherSeries: series,
            listeners: {
                click: function (e) {
                    const now = new Date();
                    if (!me._clickstart || now - me._clickstart > 10000) {
                        me._clickcount = 0;
                        me._clickstart = now;
                    }
                    me._clickcount += 1;
                    if (me._clickcount === 10) {
                        me._advancedmode = !me._advancedmode;
                        me._clickcount = 0;
                        me.onClickMemoryLoadButton();
                    }
                },
                element: 'el'
            }
        };

        this.getViewModel().set('typeChart', 'memoryLoad');
        this.createChart(config);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickActiveSessionsButton: function (button, event, eOpts) {
        const config = {
            yField: 'active_session',
            chartType: 'line'
        };

        this.getViewModel().set('typeChart', 'activeSessions');
        this.createChart(config);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onClickSystemLoadButton: function (button, event, eOpts) {
        const config = {
            yField: 'system_load',
            chartType: 'line'
        };

        this.getViewModel().set('typeChart', 'systemLoad');
        this.createChart(config);
    },

    privates: {
        /**
         * Flag for advanced mode
         *
         * @property {Boolean} _advancedmode
         */
        _advancedmode: false,

        /**
         * @property {Number} _clickcount
         */
        _clickcount: 0,

        /**
         * @property {Date} _clickstart
         */
        _clickstart: null,

        /**
         * Create chart
         * @param {Object} config
         */
        createChart: function (config) {
            const view = this.getView();
            const series = config.yField
                ? [
                      {
                          type: config.chartType,
                          title: config.title,
                          xField: 'date',
                          yField: config.yField,
                          colors: ['#4B8DC5'],
                          tooltip: {
                              trackMouse: true,
                              renderer: 'onDataTooltipRender'
                          }
                      }
                  ]
                : [];
            const chart = {
                xtype: 'cartesian',
                width: '100%',
                height: 300,
                legend: config.legend,
                bind: {
                    store: '{systemStatus}'
                },
                insetPadding: '10 20',
                axes: [
                    {
                        type: 'category',
                        fields: 'date',
                        position: 'bottom',
                        grid: true,
                        renderer: function (axis, label, layoutContext, lastLabel) {
                            const d = new Date(label);
                            return Ext.Date.format(d, 'G:i');
                        }
                    },
                    {
                        type: 'numeric',
                        fields: config.fields,
                        position: 'left',
                        grid: true,
                        minimum: 0,
                        renderer: config.renderer || 'onNumericAxisRender'
                    }
                ],
                series: series,
                listeners: config.listeners
            };

            if (config.otherSeries) {
                chart.series = Ext.Array.union(config.otherSeries, series);
            }

            view.down('#chart').removeAll();
            view.down('#chart').add(chart);
        },

        /**
         * Render the label for memory axis
         * @param {Ext.chart.axis.Axis} axis
         * @param {Number} label
         * @param {Object} layoutContext
         * @returns {String}
         */
        onMemoryLabelRender: function (axis, label, layoutContext) {
            return Number.isInteger(label) ? Ext.String.format('{0} GB', label / 1000) : '';
        },

        /**
         * Render the label for numeric axis
         * @param {Ext.chart.axis.Axis} axis
         * @param {Number} label
         * @param {Object} layoutContext
         * @returns {String}
         */
        onNumericAxisRender: function (axis, label, layoutContext) {
            return Number.isInteger(label) ? label : '';
        },

        /**
         * Render data on tooltip
         * @param {Ext.tip.ToolTip} tooltip
         * @param {Ext.data.Model} record
         * @param {Object} item
         */
        onDataTooltipRender: function (tooltip, record, item) {
            let value = record.get(item.field);
            if (['system_memory_total', 'system_memory_used', 'java_memory_total'].indexOf(item.field) > -1) {
                value = Ext.util.Format.fileSize(value * 1024 * 1024);
            }
            tooltip.setHtml(
                Ext.String.format(
                    '{0}: <b>{1}<b>',
                    CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(record.get('timestamp')),
                    value
                )
            );
        }
    }
});
