Ext.define('CMDBuildUI.view.administration.content.setup.elements.DownloadLog', {
    extend: 'Ext.grid.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.DownloadLogController',
        'CMDBuildUI.view.administration.content.setup.elements.DownloadLogModel'
    ],

    alias: 'widget.administration-content-setup-elements-downloadlog',
    controller: 'administration-content-setup-elements-downloadlog',
    viewModel: {
        type: 'administration-content-setup-elements-downloadlog'
    },
    forceFit: true,
    scrollable: true,
    reserveScrollbar: true,    
    sortable: false,

    columns: []    
});