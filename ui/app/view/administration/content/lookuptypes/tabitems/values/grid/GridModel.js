Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-lookuptypes-tabitems-values-grid-grid',

    data: {
        theValue: null,
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
                _is_system: '{theLookupType._is_system}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', !data._is_system && data.canModify);
                this.set('toolAction._canUpdate', data.canModify);
                this.set('toolAction._canDelete', !data._is_system && data.canModify);
                this.set('toolAction._canActiveToggle', !data._is_system && data.canModify);
            }
        },

        lookupValuesProxy: {
            bind: '{theLookupType}',
            get: function (theLookupType) {
                const proxy = CMDBuildUI.model.lookups.Lookup.getProxy();
                proxy.setUrl(
                    Ext.String.format(
                        '/lookup_types/{0}/values',
                        CMDBuildUI.util.Utilities.stringToHex(theLookupType.getId())
                    )
                );

                const objectTypeName = theLookupType.get('name');
                if (objectTypeName && !theLookupType.phantom) {
                    return {
                        url: Ext.String.format(
                            '/lookup_types/{0}/values',
                            CMDBuildUI.util.Utilities.stringToHex(objectTypeName)
                        ),
                        type: 'baseproxy',
                        extraParams: {
                            active: false
                        }
                    };
                }
            }
        }
    },

    stores: {
        allValues: {
            model: 'CMDBuildUI.model.lookups.Lookup',
            proxy: '{lookupValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            listeners: {
                load: 'onLoadLookupValues'
            },
            sorters: [
                {
                    property: 'index',
                    direction: 'ASC'
                }
            ]
        }
    }
});
