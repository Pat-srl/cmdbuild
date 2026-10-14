Ext.define('CMDBuildUI.view.dashboards.common.components.basechart.BaseChartModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dashboards-components-basechart-basechart',

    data: {
        storedata: {},
        form: {
            hidden: false
        },
        grid: {
            hidden: true
        },
        localParameters: {},
        localParamsNames: {}
    },

    formulas: {
        updateExtraParams: {
            bind: {
                bindTo: '{parameters}',
                deep: true
            },
            get: function () {
                this.setExtraParams();
            }
        },
        updateLocalExtraParams: {
            bind: {
                bindTo: '{localParameters}',
                deep: true
            },
            get: function () {
                this.setExtraParams();
            }
        },
        haveParameters: {
            bind: '{parametersAsText}',
            get: function (text) {
                return !Ext.isEmpty(text);
            }
        }
    },

    privates: {
        setExtraParams: function () {
            const extraparams = {};
            const parameters = this.get('parameters');
            const localParameters = this.get('localParameters');
            const paramsnames = this.get('paramsnames');
            const localParamsNames = this.get('localParamsNames');
            for (const key in paramsnames) {
                const paramname = paramsnames[key];
                const paramValue =
                    parameters && parameters.get ? parameters.get(paramname) : parameters && parameters[key];

                //Add only if the parameter exists in the data source
                try {
                    if (this.getView().getItemDefinition().dataSourceParameters().find('name', key) > -1) {
                        // Prevent null values
                        if (paramValue) {
                            extraparams[key] = paramValue;
                        }
                    }
                } catch (e) {
                    console.error('Error checking data source parameters:', e);
                    if (paramValue) {
                        extraparams[key] = paramValue;
                    }
                }
            }
            for (const key in localParamsNames) {
                const paramname = localParamsNames[key];
                const paramValue =
                    localParameters && localParameters.get
                        ? localParameters.get(key)
                        : localParameters && localParameters[key];
                // Prevent null values
                if (paramValue) {
                    extraparams[paramname] = paramValue;
                }
            }
            this.set('storedata.extraparams', {
                filter: this.get('storedata.filter'),
                parameters: Ext.JSON.encode(extraparams)
            });
        }
    },

    stores: {
        records: {
            type: '{storedata.type}',
            storeId: 'recordsStore',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: '{storedata.extraparams}'
            },
            autoLoad: '{storedata.autoload}',
            autoDestroy: true
        }
    }
});
