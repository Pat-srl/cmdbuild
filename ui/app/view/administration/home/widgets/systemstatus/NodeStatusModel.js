Ext.define('CMDBuildUI.view.administration.home.widgets.systemstatus.NodeStatusModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-systemstatus-nodestatus',

    data: {
        timeChart: 1,
        typeChart: null,
        nodeId: null
    },

    formulas: {
        monitorTitle: function () {
            return CMDBuildUI.locales.Locales.administration.home.monitor;
        },

        proxyUrl: {
            bind: {
                timeChart: '{timeChart}'
            },
            get: function (data) {
                return Ext.String.format(
                    '{0}/system/cluster/nodes/{1}/monitor/{2}',
                    CMDBuildUI.util.Config.baseUrl,
                    this.getView().getItemId(),
                    data.timeChart
                );
            }
        },

        refreshStore: {
            bind: {
                proxyUrl: '{proxyUrl}',
                nodes: '{clusterNodes}',
                systemStatus: '{systemStatus}'
            },
            get: function (data) {
                if (data.proxyUrl && data.nodes && data.systemStatus) {
                    data.systemStatus.load();
                }
            }
        },

        timeValuesData: function () {
            return [
                {
                    label: CMDBuildUI.locales.Locales.administration.home.lasthour,
                    value: 1
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.home.last06hour,
                    value: 6
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.home.last12hour,
                    value: 12
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.home.last18hour,
                    value: 18
                },
                {
                    label: CMDBuildUI.locales.Locales.administration.home.last24hour,
                    value: 24
                }
            ];
        }
    },

    stores: {
        systemStatus: {
            model: 'CMDBuildUI.model.administration.NodeStatus',
            proxy: {
                type: 'baseproxy',
                url: '{proxyUrl}'
            },
            pageSize: 0,
            sorters: [
                {
                    property: 'date',
                    direction: 'ASC'
                }
            ]
        },

        timeValues: {
            proxy: 'memory',
            fields: ['value', 'label'],
            data: '{timeValuesData}'
        }
    }
});
