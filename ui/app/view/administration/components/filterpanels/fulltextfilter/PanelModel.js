Ext.define('CMDBuildUI.view.administration.components.filterpanels.fulltextfilter.PanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-components-filterpanels-fulltextfilter-panel',
    
    data: {
        _query: null        
    },
    formulas: {
        manager: function(get){
            var filter =this.get('theFilter.configuration');
            if(filter.query && filter.query.length){
                this.set('_query',filter.query );
            }
        }
    }
});
