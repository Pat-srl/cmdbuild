Ext.define('CMDBuildUI.view.classes.cards.card.CreateModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.classes-cards-card-create',

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

        title: function () {
            return this.getView().getObjectTypeName();
        },

        updateDescription: {
            bind: {
                description: '{theObject.Description}'
            },
            get: function (data) {
                var tabpanel = this.getView().up("classes-cards-tabpanel");
                if (tabpanel) {
                    tabpanel.getViewModel().set("objectDescription", data.description);
                }
            }
        },

        detailWindowTitleManager: {
            bind: '{detailsWindowTitle}',
            get: function (detailWindowTitle) {
                if (detailWindowTitle) {
                    var detailWindow = this.getView().up();
                    if (detailWindow) {
                        this.set("titledata.type", detailWindowTitle);
                    }
                }
            }
        },

        /**
         * Return card widgets
         */
        widgets: {
            bind: '{classObject.widgets}',
            get: function (widgets) {
                return widgets;
            }
        }
    }

});