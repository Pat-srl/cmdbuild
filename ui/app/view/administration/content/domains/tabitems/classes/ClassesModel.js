Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.classes.ClassesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-domains-tabitems-domains-classes',

    formulas: {
        destinationTreeDisabled: {
            bind: {
                destinationStore: '{destinationStore}',
                view: '{actions.view}'
            },
            get: function (data) {
                const store = data.destinationStore;
                if (!store.getRoot().get('children') || data.view) {
                    return true;
                } else {
                    return false;
                }
            }
        },

        originTreeDisabled: {
            bind: {
                originStore: '{originStore}',
                view: '{actions.view}'
            },
            get: function (data) {
                const store = data.originStore;
                if (!store.getRoot().get('children') || data.view) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    },

    stores: {
        originStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description', 'enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        },

        destinationStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['description', 'enabled'],
            root: {
                expanded: true
            },
            autoDestroy: true
        }
    }
});
