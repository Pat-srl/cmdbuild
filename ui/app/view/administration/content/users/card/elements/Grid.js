Ext.define('CMDBuildUI.view.administration.content.users.elements.Grid', {
    extend: 'Ext.grid.Panel',

    alias: 'widget.administration-content-users-elements-grid',
    viewModel: {},
    
    bind: {
        store: Ext.create('Ext.data.Store',{
            data:'{theUser.userGroups}'
        })
    },

    columns: [{
        text: CMDBuildUI.locales.Locales.administration.common.labels.description,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
        },
        dataIndex: 'description',
        align: 'left'
    }],

    autoEl: {
        'data-testid': 'administration-content-user-group-grid'
    },

    forceFit: true,
    loadMask: true
});

