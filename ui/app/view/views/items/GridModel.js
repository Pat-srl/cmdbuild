Ext.define('CMDBuildUI.view.views.items.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.views-items-grid',

    data: {
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
        objectTypeName: null,
        menuType: CMDBuildUI.model.menu.MenuItem.types.view,
        storeinfo: {
            autoLoad: false
        },
        disabledbuttons: {
            print: true
        },
        search: {
            disabled: false
        }
    },

    formulas: {

        updateData: {
            bind: {
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName) {
                    var theObject = CMDBuildUI.util.helper.ModelHelper.getViewFromName(data.objectTypeName);
                    // set title
                    this.set("title", theObject.get("_description_translation"));
                    this.set("search.disabled", !theObject.get("_can_search"));

                    // model name
                    var modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.view,
                        data.objectTypeName
                    );
                    this.set("storeinfo.modelname", modelName);

                    var model = Ext.ClassManager.get(modelName);
                    this.set("storeinfo.proxytype", model.getProxy().type);
                    this.set("storeinfo.url", model.getProxy().getUrl());

                    var item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objectTypeName, CMDBuildUI.util.helper.ModelHelper.objecttypes.view),
                        sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(item);

                    this.set("storeinfo.sorters", sorters);

                    // auto load
                    this.set("storeinfo.autoload", true);
                }
            }
        }
    },

    stores: {
        items: {
            type: 'buffered',
            model: '{storeinfo.modelname}',
            autoLoad: '{storeinfo.autoload}',
            pageSize: 50,
            leadingBufferZone: 100,
            proxy: {
                type: '{storeinfo.proxytype}',
                url: '{storeinfo.url}'
            },
            sorters: '{storeinfo.sorters}',
            autoDestroy: true,
            listeners: {
                beforeload: 'onStoreBeforeLoad',
                load: 'onStoreLoad'
            }
        }
    }

});