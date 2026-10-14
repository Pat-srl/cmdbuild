
Ext.define('CMDBuildUI.view.administration.content.setup.elements.StatusGrid',{
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.StatusGridController',
        'CMDBuildUI.view.administration.content.setup.elements.StatusGridModel'
    ],
    alias: 'widget.administration-content-setup-elements-statusgrid',
    controller: 'administration-content-setup-elements-statusgrid',
    viewModel: {
        type: 'administration-content-setup-elements-statusgrid'
    },

    statics: {
        statusicons: {
            error: CMDBuildUI.util.helper.IconHelper.getIconId('times-circle', 'solid'),
            ready: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'solid'),
            disabled: CMDBuildUI.util.helper.IconHelper.getIconId('minus-circle', 'solid'),
            notrunning: CMDBuildUI.util.helper.IconHelper.getIconId('stop-circle', 'solid')
        },
        statuscolors: {
            error: '#FF0000',
            ready: '#33cc00'
        }
    },

    forceFit: true,
    scrollable: true,
    reserveScrollbar: true,    
    sortable: false,    
    columns: []    
});
