/**
 * Abstract base class for filter implementations.
 */
Ext.define('Overrides.grid.filters.filter.TriFilter', {
    override: 'Ext.grid.filters.filter.TriFilter',

    /**
     * @override
     * Used to return the number of active filters and 
     * launch method removeStoreFilter on deactivate private function ExtJS   
     * 
     * @returns 
     */
    countActiveFilters: function () {
        var store = this.getGridStore(),
            advancedFilter = store.getAdvancedFilter();

        return Ext.Object.getSize(advancedFilter.getAttributes());
    }
});