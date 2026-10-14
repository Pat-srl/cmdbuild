Ext.define('CMDBuildUI.view.boot.patches.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.boot-patches-panel',

    stores: {
        patches: {
            model: 'CMDBuildUI.model.base.Patch',
            autoLoad: true,
            autoDestroy: true,
            sorters: ['name'],
            pageSize: 0
        }
    }

});
