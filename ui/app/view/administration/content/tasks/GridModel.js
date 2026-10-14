Ext.define('CMDBuildUI.view.administration.content.tasks.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-grid',
    data: {
        theTask: null,
        search: {
            value: null
        },
        selected: null,
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },
    formulas: {
        targetTypesData: function (get) {
            return [{
                label: CMDBuildUI.locales.Locales.administration.localizations.class,
                value: CMDBuildUI.model.administration.MenuItem.types.klass
            }, {
                label: CMDBuildUI.locales.Locales.administration.localizations.domain,
                value: CMDBuildUI.model.administration.MenuItem.types.domain
            }];

        }
    },

    stores: {
        targetTypes: {
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            data: '{targetTypesData}'
        }

    }


});
