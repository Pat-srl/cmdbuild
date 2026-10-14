Ext.define('CMDBuildUI.view.administration.content.dashboards.homepage.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-dashboards-homepage-card',
    requires: ['CMDBuildUI.model.dashboards.Component'],
    data: {
        hideForm: false,
        rowsData: [],
        componentsData: [],
        unusedRoles: []
    },

    formulas: {
        rowsDataManager: {
            get: function () {
                return [];
            }
        },
        unusedRolesManager: {
            bind: {
                currentGroup: '{theDashboard.group}'
            },
            get: function (data) {
                const me = this;
                CMDBuildUI.util.Stores.loadGroupsStore().then(function (roles) {
                    roles = CMDBuildUI.util.administration.helper.SortHelper.sort(roles, 'description');

                    CMDBuildUI.util.Stores.loadHomepagesStore().then(function (homepages) {
                        if (me.destroyed) {
                            return;
                        }

                        let unusedRoles = [];

                        Ext.Array.forEach(roles, function (role) {
                            const homepageForRole = Ext.Array.findBy(homepages, function (hp) {
                                return hp.get('group') === role.get('name');
                            });
                            if (!homepageForRole || role.get('name') === data.currentGroup) {
                                unusedRoles.push({
                                    value: role.get('name'),
                                    label: role.get('description')
                                });
                            }
                        });

                        me.set('unusedRoles', unusedRoles);
                    });
                });
                return null;
            }
        },
        toolAction: {
            bind: {
                group: '{theDashboard.group}'
            },
            get: function (data) {
                var isDefaultGroup = data.group === '_default';
                return {
                    _canDelete: !isDefaultGroup,
                    _canActiveToggle: !isDefaultGroup,

                    config: {
                        edit: true,
                        activeToggle: !isDefaultGroup,
                        delete: !isDefaultGroup
                    }
                };
            }
        }
    },

    stores: {
        componentsStore: {
            proxy: { type: 'memory' },
            data: CMDBuildUI.model.dashboards.Component.getComponentTypes()
        },
        rows: {
            proxy: {
                type: 'memory'
            },
            data: '{rowsData}',
            listeners: {
                datachanged: 'onRowsStoreDatachanged'
            }
        },
        components: {
            proxy: { type: '{memory}' },
            data: '{componentsData}',
            listeners: { datachanged: 'onRowsStoreDatachanged' }
        },
        groupsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{unusedRoles}',
            proxy: { type: 'memory' }
        }
    }
});
