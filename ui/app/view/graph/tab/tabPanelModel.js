Ext.define('CMDBuildUI.view.graph.tab.tabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.graph-tab-tabpanel',

    data: {
        relationTabTitle: ""
    },

    formulas: {
        setInitialTitle: function () {
            this.set("relationTabTitle", Ext.String.format("{0} (0)", CMDBuildUI.locales.Locales.relationGraph.cardRelations));
        }
    }
});