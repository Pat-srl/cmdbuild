Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.grid.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-dmscategorytypes-tabitems-values-grid-grid',
    data: {
        selected: null
    },
    formulas: {
        canAdd: {
            bind: '{theDMSCategoryType}',
            get: function(theDMSCategoryType){
                if(theDMSCategoryType){
                    return !theDMSCategoryType.get('_is_system');
                }
            }
        }
    },
    stores: {
        allValues: {
            model: 'CMDBuildUI.model.dms.DMSCategory',
            proxy: '{DMSCategoryValuesProxy}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0,
            sorters: [{
                property: 'index',
                direction: 'ASC'
            }]
        }        
    }

});