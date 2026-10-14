Ext.define('CMDBuildUI.view.administration.content.setup.elements.GovernanceDataModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-governancedata',

    stores: {
        treeStore: {
            type: 'tree',
            model: 'CMDBuildUI.model.setup.GovernanceData',
            root: {
                expanded: false
            },
            proxy: {
                type: 'memory'
            },
            sorters: ['description_translation']
        }
    }
});
