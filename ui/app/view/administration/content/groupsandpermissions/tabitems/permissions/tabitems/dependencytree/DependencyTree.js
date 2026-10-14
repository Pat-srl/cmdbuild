Ext.define(
    'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dependencytree.DependencyTree',
    {
        extend: 'Ext.panel.Panel',

        requires: [
            'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dependencytree.DependencyTreeController',
            'CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.tabitems.dependencytree.DependencyTreeModel'
        ],
        alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-tabitems-dependencytree-dependencytree',
        controller:
            'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dependencytree-dependencytree',
        viewModel: {
            type: 'administration-content-groupsandpermissions-tabitems-permissions-tabitems-dependencytree-dependencytree'
        },

        reference: 'dependencytree-tab',

        layout: 'fit',
        dockedItems: [
            {
                xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-topbar'
            }
        ],
        items: [
            {
                xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
                viewModel: {}
            }
        ]
    }
);
