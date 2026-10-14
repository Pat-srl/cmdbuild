Ext.define('CMDBuildUI.view.map.tab.cards.LegendModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.map-tab-cards-legend',

    formulas: {

        updateRulesThematism: {
            bind: '{theThematism}',
            get: function (theThematism) {
                if (theThematism) {
                    this.getView().down('thematisms-thematism-rules').setrules();
                }
            }
        }

    }
});