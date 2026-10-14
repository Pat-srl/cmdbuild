Ext.define('CMDBuildUI.view.reports.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.reports-container',

    data: {
        url: null,
        objectTypeName: null,
        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.report,
        menuType: CMDBuildUI.model.menu.MenuItem.types.report,
        extension: null,
        defaults: {},
        downloadbtn: {
            href: null
        },
        refreshbtn: {
            disabled: false
        }
    },

    formulas: {
        // get the report from store
        theReport: {
            bind: {
                typename: '{objectTypeName}'
            },
            get: function (data) {
                if (data.typename) {
                    return Ext.getStore('reports.Reports').findRecord('code', data.typename);
                }
            }
        },

        // page title
        title: {
            bind: {
                thereport: '{theReport}'
            },
            get: function (data) {
                if (data.thereport && !this.getView().hideTitle) {
                    return data.thereport.get('_description_translation') || data.thereport.get('description');
                }
            }
        }
    }
});
