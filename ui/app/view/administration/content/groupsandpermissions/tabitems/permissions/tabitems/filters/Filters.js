Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.Filters', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.FiltersController',
        'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.filters.FiltersModel'
    ],
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters',
    controller: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters',
    viewModel: {
        type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-filters-filters'
    },
    reference: 'filters-tab',
    layout: 'fit',
    dockedItems: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
    }],
    items: [{
        xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
        viewModel: {},
        bind: {
            store: '{objectTypeGrantsStore}'
        }
    }]
});