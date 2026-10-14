Ext.define('CMDBuildUI.components.grid.filters.Reference', {
    extend: 'Ext.grid.filters.filter.String',
    alias: 'grid.filter.reference',
 
    type: 'string',
 
    operator: 'description_like'
});