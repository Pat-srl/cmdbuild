Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.users.UsersModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-users-users',
    formulas: {
        storeAutoload: {
            bind: '{theGroup}',
            get: function (theGroup) {
                return theGroup && !theGroup.phantom;
            }
        }
    },
    stores: {
        assignedUser: {
            model: 'CMDBuildUI.model.users.User',
            proxy: {
                url: '/roles/{theGroup._id}/users',
                type: 'baseproxy',
                extraParams: {
                    assigned: true
                }
            },
            autoLoad: '{storeAutoload}',
            autoDestroy: true,
            pageSize: 0,
            sorters: ['username'],
            listeners: {
                load: function (store, records) {
                    store.originalRecords = records;
                }
            }
        },
        notAssignedUser: {
            model: 'CMDBuildUI.model.users.User',
            proxy: {
                url: '/roles/{theGroup._id}/users',
                type: 'baseproxy',
                extraParams: {
                    assigned: false
                }
            },
            sorters: ['username'],
            autoLoad: '{storeAutoload}',
            autoDestroy: true,
            pageSize: 0
        }
    }
});
