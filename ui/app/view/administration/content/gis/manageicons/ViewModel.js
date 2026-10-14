Ext.define('CMDBuildUI.view.administration.content.gis.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gis-view',    
    
    stores: {
        icons: {
            model: 'CMDBuildUI.model.icons.Icon',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url:  Ext.String.format(
                    '{0}/uploads/?path=images/gis',
                    CMDBuildUI.util.Config.baseUrl
                ),
                type: 'baseproxy'
            },
            pagination: 0
        }
    }

});