Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.defaultfilters.DefaultFiltersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-defaultfilters-defaultfilters',

    data: {
        name: 'CMDBuildUI',
        treeStoreData: []
    },

    formulas: {
        filterStoreUrl: {
            bind: {
                theGroup: '{theGroup}'
            },
            get: function (data) {
                if (data.theGroup && data.theGroup.crudState !== 'C') {
                    return CMDBuildUI.util.administration.helper.ApiHelper.server.getPermissionFiltersUrl(
                        data.theGroup.getId()
                    );
                }
            }
        },
        treeStoreDataManager: {
            bind: {
                theGroup: '{theGroup}',
                filterStore: '{filterStore}'
            },
            get: function (data) {
                if (data.theGroup && data.filterStore) {
                    var me = this;
                    var childrens = [];

                    Ext.Promise.all([
                        CMDBuildUI.util.administration.helper.TreeClassesHelper.appendClasses(
                            false,
                            false,
                            true,
                            true
                        ).then(
                            function (classes) {
                                childrens[0] = classes;
                            },
                            function () {
                                CMDBuildUI.util.Msg.alert('Error', 'Classes store NOT LOADED!');
                            }
                        ),
                        CMDBuildUI.util.administration.helper.TreeClassesHelper.appendProcesses(
                            false,
                            false,
                            true,
                            true
                        ).then(
                            function (processes) {
                                childrens[1] = processes;
                            },
                            function () {
                                CMDBuildUI.util.Msg.alert('Error', 'Processes store NOT LOADED!');
                            }
                        )
                    ]).then(function () {
                        if (!me.destroyed) {
                            var defaultFilters = data.filterStore.getRange();
                            var tree = {
                                text: 'Root',
                                expanded: true
                            };
                            childrens = me.setDefaultFilters(childrens, defaultFilters);
                            tree.children = childrens;
                            me.set('treeStoreData', tree);
                        }
                    });
                }
            }
        }
    },

    stores: {
        filterStore: {
            type: 'store',
            model: 'CMDBuildUI.model.users.GroupFilter',
            proxy: {
                type: 'baseproxy',
                url: '{filterStoreUrl}'
            },
            pageSize: 0, // disable pagination
            autoLoad: true
        },
        gridStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            root: '{treeStoreData}',
            listeners: {
                datachanged: 'onTreeStoreDataChanged'
            }
        }
    },

    setDefaultFilters: function (childrens, defaultFilters) {
        var me = this;
        if (childrens && childrens.length) {
            Ext.Array.forEach(childrens, function (children) {
                if (children.children) {
                    children.children = me.setDefaultFilters(children.children, defaultFilters);
                }
                var hasFilter = Ext.Array.findBy(defaultFilters, function (filter) {
                    return children.objecttype === filter.get('_defaultFor');
                });
                if (hasFilter) {
                    children.filter = hasFilter.get('_id');
                }
            });
        }
        return childrens;
    }
});
