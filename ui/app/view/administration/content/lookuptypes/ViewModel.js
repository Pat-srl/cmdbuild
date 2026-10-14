Ext.define('CMDBuildUI.view.administration.content.lookuptypes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-view',

    data: {
        theLookupType: null,
        activeTab: 0,
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            list: false,
            values: false
        },
        parentstoredata: {
            url: null,
            autoLoad: false
        },
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_lookups_modify}',
                theLookupType: '{theLookupType}'
            },
            get: function (data) {
                const isSystem = data.theLookupType ? data.theLookupType.get('_is_system') : false;
                this.set('toolAction._canAdd', !isSystem && data.canModify);
                this.set('toolAction._canUpdate', data.canModify);
                this.set('toolAction._canDelete', !isSystem && data.canModify);
                this.set('toolAction._canActiveToggle', data.canModify);
            }
        },

        tabManager: {
            bind: {
                action: '{action}',
                theLookupType: '{theLookupType}'
            },
            get: function (data) {
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.lookuptypes') || 0);
                this.set('actions.view', data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);

                if (data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.add || data.action === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit) {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', true);
                } else {
                    this.set('disabledTabs.list', false);
                    this.set('disabledTabs.values', false);
                }
            }
        },

        changeProxyUrl: {
            bind: '{theLookupType}',
            get: function (theLookupType) {
                if (theLookupType) {
                    const proxy = CMDBuildUI.model.lookups.Lookup.getProxy();
                    proxy.setUrl(Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(theLookupType.getId())));
                }
            }
        },

        parentLookupValuesProxy: {
            bind: '{theLookupType.parent}',
            get: function (parentTypeName) {
                if (parentTypeName && !this.get('theLookupType').phantom) {
                    this.set('parentstoredata.url', Ext.String.format("/lookup_types/{0}/values", CMDBuildUI.util.Utilities.stringToHex(parentTypeName)));
                    this.set('parentstoredata.autoLoad', true);
                }
            }
        }
    },

    stores: {
        allLookupsStore: {
            source: "lookups.LookupTypes",
            sorters: [
                'name'
            ],
            autoDestroy: true
        },

        parentLookupsStore: {
            model: "CMDBuildUI.model.lookups.Lookup",
            proxy: {
                url: '{parentstoredata.url}',
                type: 'baseproxy'
            },
            extraParams: {
                active: false
            },
            pageSize: 0, // disable pagination
            fields: ['_id', 'description'],
            autoLoad: '{parentstoredata.autoLoad}',
            sorters: ['description']
        }
    }
});