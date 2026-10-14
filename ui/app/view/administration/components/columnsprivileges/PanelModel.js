Ext.define('CMDBuildUI.view.administration.components.columnsprivileges.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-columnsprivileges-panel',

    formulas: {
        /**
         * Update attributes store data
         */
        updateStoreData: {
            bind: {
                objecttype: '{objectType}',
                objecttypename: '{objectTypeName}',
                privileges: '{attributesPrivileges}'
            },
            get: function (data) {
                if (data.objecttype && data.objecttypename) {
                    var me = this;
                    var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.objecttypename, CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(data.objecttypename));
                    obj.getAttributes().then(function (attributes) {
                        if (!me.destroyed) {
                            var rows = [];
                            attributes.getRange().forEach(function (a) {
                                if (a.canAdminShow()) {
                                    rows.push({
                                        name: a.get("name"),
                                        description: a.get("description"),
                                        mode: data.privileges[a.get("name")] || 'write'
                                    });
                                }
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
        attributes: {
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
