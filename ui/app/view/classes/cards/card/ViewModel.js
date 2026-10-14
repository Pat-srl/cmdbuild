Ext.define('CMDBuildUI.view.classes.cards.card.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-view',

    data: {
        bim: {
            projectid: null,
            selectedid: null
        }
    },

    formulas: {
        title: function (get) {
            return null; // return null to hide header
        },
        /**
         * Update description in parent
         */
        updateDescription: {
            bind: {
                description: '{theObject.Description}'
            },
            get: function (data) {
                if (this.getView().getShownInPopup()) {
                    this.getParent().set("objectDescription", data.description);
                }
            }
        },

        /**
         * class object by type name
         */
        classObject: {
            bind: {
                typename: '{objectTypeName}'
            },
            get: function (data) {
                return CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.typename);
            }
        },

        updatePermissions: {
            bind: {
                typename: '{objectTypeName}',
                objectid: '{objectId}'
            },
            get: function (data) {
                var parent = this.getParent();

                if (data.typename && parent) {
                    var configs = CMDBuildUI.util.helper.Configurations;
                    var item = CMDBuildUI.util.helper.ModelHelper.getClassFromName(data.typename);
                    var isSimpleClass = item.isSimpleClass();
                    // hidden buttons
                    parent.set("configenabled.relgraph", (!isSimpleClass && configs.get(CMDBuildUI.model.Configuration.relgraph.enabled)));

                    // bim options
                    if (!isSimpleClass && data.objectid && configs.get(CMDBuildUI.model.Configuration.bim.enabled) && !this.get('diffMode')) {
                        Ext.Ajax.request({
                            url: CMDBuildUI.util.api.Classes.getCardBimUrl(data.typename, data.objectid),
                            success: function (response) {
                                var jsonResponse = JSON.parse(response.responseText);
                                if (jsonResponse.data.exists) {
                                    parent.set('configenabled.bim', true);
                                    parent.set('bim.projectid', jsonResponse.data.projectId);
                                    parent.set('bim.selectedid', jsonResponse.data.globalId);
                                } else {
                                    parent.set('configenabled.bim', false);
                                    parent.set('bim.projectid', null);
                                }
                            }
                        });
                    }
                }
            }
        },

        updatePermissionsFromInstance: {
            bind: {
                model: '{theObject._model}'
            },
            get: function (data) {
                if (data.model) {
                    this.getParent().set("permissions", {
                        clone: data.model[CMDBuildUI.model.base.Base.permissions.clone],
                        delete: data.model[CMDBuildUI.model.base.Base.permissions.delete],
                        edit: data.model[CMDBuildUI.model.base.Base.permissions.edit],
                        relgraph: data.model[CMDBuildUI.model.base.Base.permissions.relgraph],
                        print: data.model[CMDBuildUI.model.base.Base.permissions.print]
                    });
                }
            }
        },

        /**
         * Return card widgets
         */
        widgets: {
            bind: '{theObject.widgets}',
            get: function (widgets) {
                return widgets;
            }
        }
    }
});
