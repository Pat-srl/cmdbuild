Ext.define('CMDBuildUI.view.administration.content.dashboards.common.tabs.PropertiesTabBase', {
    extend: 'Ext.panel.Panel',

    controller: 'administration-content-dashboards-common-propertiestab',
    viewModel: {
        type: 'administration-content-dashboards-common-propertiestab'
    },

    config: {
        showCard: true,
        entityXtype: null,
        entityIdVmKey: null,
        listRoute: null
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'card',
    items: []
});
