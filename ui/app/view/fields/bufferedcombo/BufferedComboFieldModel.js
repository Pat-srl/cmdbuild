Ext.define('CMDBuildUI.view.fields.bufferedcombo.BufferedComboFieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-bufferedcombofield',

    data: {
        selection: null,
        storeinfo: {
            autoload: false, // load store on onBindStore function
            extraparams: {}
        }
    },

    formulas: {
        updateStoreInfo: {
            bind: {
                initialvalue: '{initialvalue}'
            },
            get: function (data) {
                var view = this.getView(),
                    proxy, url;
                // set url                
                try {
                    proxy = eval(view.getModelname()).getProxy();
                    url = proxy.getUrl();
                    this.set("storeinfo.proxyurl", url);
                } catch (error) {
                    CMDBuildUI.util.Logger.log(error, CMDBuildUI.util.Logger.levels.debug);
                }
                this.set('storeinfo.model', view.getModelname());
                // // page size
                this.set("storeinfo.pagesize", CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit));

                var sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(
                    CMDBuildUI.util.helper.ModelHelper.getObjectFromName(this.get("objectTypeName"), this.get("objectType")),
                    true);
                this.set("storeinfo.sorters", sorters);

                if (data.initialvalue) {
                    this.set("storeinfo.extraparams.positionOf", data.initialvalue);
                }
            }
        }
    },

    stores: {
        options: {
            model: '{storeinfo.model}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: '{storeinfo.extraparams}'
            },
            remoteFilter: false,
            remoteSort: true,
            pageSize: '{storeinfo.pagesize}',
            sorters: '{storeinfo.sorters}',
            autoLoad: '{storeinfo.autoload}',
            autoDestroy: true
        }
    }
});
