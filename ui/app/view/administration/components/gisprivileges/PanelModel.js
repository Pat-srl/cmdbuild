Ext.define('CMDBuildUI.view.administration.components.gisprivileges.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-gisprivileges-panel',

    formulas: {
        /**
         * Update geoattributes store data
         */
        updateStoreData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                privileges: '{gisPrivileges}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    var me = this;
                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(data.objecttypename));                    
                    obj.getGeoAttributes().then(function (geoattributes) {
                        if (!me.destroyed) {
                            var rows = [];
                            geoattributes.getRange().forEach(function (a) {

                                rows.push({
                                    name: a.get("name"),
                                    description: a.get("description"),
                                    mode: data.privileges[a.get("name")] || 'default'
                                });

                            });
                            me.set("storedata", rows);
                        }
                    });
                }
            }
        },

        canModifyChecks: {
            bind: {
                actions: '{actions}'
            },
            get: function (data) {
                return !data.actions.view;
            }
        }
    },

    stores: {
        geoAttributes: {
            fields: [{
                type: 'string',
                name: 'name'
            }, {
                type: 'string',
                name: 'description'
            }, {
                type: 'string',
                name: 'mode'
            }, {
                type: 'boolean',
                name: 'default',
                calculate: function (data) {
                    return data.mode === "default";
                }
            }, {
                type: 'boolean',
                name: 'none',
                calculate: function (data) {
                    return data.mode === "none";
                }
            }, {
                type: 'boolean',
                name: 'write',
                calculate: function (data) {
                    return data.mode === "write";
                }
            }, {
                type: 'boolean',
                name: 'read',
                calculate: function (data) {
                    return data.mode === "read";
                }
            }],
            proxy: 'memory',
            autoDestroy: true,
            data: '{storedata}'
        }
    }
});