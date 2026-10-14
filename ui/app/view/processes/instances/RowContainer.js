Ext.define('CMDBuildUI.view.processes.instances.RowContainer', {
    extend: 'Ext.container.Container',
    requires: [
        'CMDBuildUI.view.processes.instances.RowContainerController',
        'CMDBuildUI.view.processes.instances.RowContainerModel'
    ],

    alias: 'widget.processes-instances-rowcontainer',
    controller: 'processes-instances-rowcontainer',
    viewModel: {
        type: 'processes-instances-rowcontainer'
    },
    layout: 'fit' 
});