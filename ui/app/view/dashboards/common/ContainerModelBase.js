Ext.define('CMDBuildUI.view.dashboards.common.ContainerModelBase', {
    extend: 'Ext.app.ViewModel',
    stores: {
        classes: {
            type: 'chained',
            source: 'classes.Classes',
            autoDestroy: true
        }
    }
});
