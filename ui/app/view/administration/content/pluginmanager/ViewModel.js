Ext.define('CMDBuildUI.view.administration.content.pluginmanager.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-pluginmanager-view',

    data: {
        hideButtons: false,
        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        overview: {
            isFilteredByWarnings: false,
            search: '',
            tagscombo: {
                value: null
            }
        }
    },

    formulas: {
        setActions: {
            bind: '{action}',
            get: function (action) {
                this.set('actions.view', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        isPatchesAvailable: {
            bind: '{plugins}',
            get: function (store) {
                return store.getData().items.some(function (record) {
                    return record.get('_hasPatches') === true;
                });
            }
        }
    },

    stores: {
        plugins: {
            id: 'plugins',
            source: 'pluginmanager.Plugins',
            autoDestroy: true,
            autoLoad: true
        },
        tags: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: {
                type: 'memory'
            },
            autoLoad: true,
            remoteFilter: false,
            autoDestroy: true,
            data: [
                {
                    _id: '__GENERIC__',
                    _type: '_FAKELOOKUP_',
                    code: 'generic',
                    description: CMDBuildUI.locales.Locales.administration.plugin.tags.generic
                },
                {
                    _id: '__CONNECTORS__',
                    _type: '_FAKELOOKUP_',
                    code: 'connector',
                    description: CMDBuildUI.locales.Locales.administration.plugin.tags.connector
                },
                {
                    _id: '__DMS__',
                    _type: '_FAKELOOKUP_',
                    code: 'dms',
                    description: CMDBuildUI.locales.Locales.administration.plugin.tags.dms
                }
            ]
        }
    }
});
