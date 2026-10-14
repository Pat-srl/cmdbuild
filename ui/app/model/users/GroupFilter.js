Ext.define('CMDBuildUI.model.users.GroupFilter', {
    extend: 'CMDBuildUI.model.base.Base',

    requires: [
        'Ext.data.validator.Presence'
    ],
    
    fields: [{
        name: 'description',
        type: 'string',
        calculate: function (data) {
            var searchFiltersStore = Ext.getStore('searchfilters.Searchfilters');
            var serachFilter = searchFiltersStore.getById(data._id);
            if(serachFilter){
                return serachFilter.get('description');
            }
            return data._id;
        }
    }, {
        name: '_defaultFor',
        type: 'string'
    }]
});