Ext.define(
    'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.other.OtherModel',
    {
        extend: 'Ext.app.ViewModel',
        alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-tabitems-other-other',
        data: {
            hierachicalViewHidden: true
        },
        formulas: {
            storeData: {
                bind: {
                    theGroup: '{theGroup}'
                },
                get: function (data) {
                    var values = [
                        {
                            _object_description: CMDBuildUI.locales.Locales.administration.navigation.gis,
                            objectType: '_rp_gis_access',
                            modeTypeWriteAllow: false,
                            mode:
                                data.theGroup.get('_rp_gis_access') === true
                                    ? CMDBuildUI.model.users.Grant.grantType.read
                                    : CMDBuildUI.model.users.Grant.grantType.none
                        },
                        {
                            _object_description: CMDBuildUI.locales.Locales.administration.navigation.bim,
                            objectType: '_rp_bim_access',
                            modeTypeWriteAllow: false,
                            mode:
                                data.theGroup.get('_rp_bim_access') === true
                                    ? CMDBuildUI.model.users.Grant.grantType.read
                                    : CMDBuildUI.model.users.Grant.grantType.none
                        },
                        {
                            _object_description: CMDBuildUI.locales.Locales.administration.navigation.schedules,
                            objectType: '_rp_calendar_access',
                            modeTypeWriteAllow: true,
                            mode:
                                data.theGroup.get('_rp_calendar_event_create') === true
                                    ? CMDBuildUI.model.users.Grant.grantType.write
                                    : data.theGroup.get('_rp_calendar_access') === true
                                      ? CMDBuildUI.model.users.Grant.grantType.read
                                      : CMDBuildUI.model.users.Grant.grantType.none
                        }
                    ];

                    if (CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.relgraph.enabled)) {
                        values.push({
                            _object_description: CMDBuildUI.locales.Locales.administration.navigation.relationgraph,
                            objectType: '_rp_relgraph_access',
                            modeTypeWriteAllow: false,
                            mode:
                                data.theGroup.get('_rp_relgraph_access') === true
                                    ? CMDBuildUI.model.users.Grant.grantType.read
                                    : CMDBuildUI.model.users.Grant.grantType.none
                        });
                    }
                    return values;
                }
            }
        },
        stores: {
            otherStore: {
                model: 'CMDBuildUI.model.users.Grant',
                data: '{storeData}',
                autoDestroy: true,
                proxy: {
                    type: 'memory'
                }
            }
        }
    }
);
