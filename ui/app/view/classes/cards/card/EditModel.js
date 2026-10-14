Ext.define('CMDBuildUI.view.classes.cards.card.EditModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-edit',

    formulas: {
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

        updateDescription: {
            bind: {
                description: '{theObject.Description}'
            },
            get: function (data) {
                this.getParent().set("objectDescription", data.description);
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
        },

        updatePermissionsFromInstance: {
            bind: {
                model: '{theObject._model}'
            },
            get: function (data) {
                if (data.model) {
                    this.getParent().set("permissions", {
                        relgraph: data.model[CMDBuildUI.model.base.Base.permissions.relgraph]
                    });
                }
            }
        }
    }

});
