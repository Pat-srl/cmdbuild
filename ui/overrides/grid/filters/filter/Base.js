/**
 * Abstract base class for filter implementations.
 */
Ext.define('Overrides.grid.filters.filter.Base', {
    override: 'Ext.grid.filters.filter.Base',

    /**
     * @override
     *
     * @param {*} filter
     */
    addStoreFilter: function (filter) {
        var store = this.getGridStore();
        this.addAdvancedFilter(store, filter);
        store.load();
    },

    /**
     * @override
     *
     * @param {*} filter
     */
    removeStoreFilter: function (filter) {
        var store = this.getGridStore();
        this.removeAdvancedFilter(store, filter);
        store.load();
    },

    /**
     * @override
     *
     */
    updateStoreFilter: function () {
        var store = this.getGridStore();
        this.removeAdvancedFilter(store, this.filter);
        this.addAdvancedFilter(store, this.filter);
        store.load();
    },

    privates: {
        /**
         *
         * @param {Ext.data.ProxyStore} store
         * @param {CMDBuildUI.util.AdvancedFilter} filter
         */
        addAdvancedFilter: function (store, filter) {
            var advancedfilter = store.getAdvancedFilter(),
                attribute = filter.getProperty(),
                value = filter.getValue(),
                operator;
            switch (filter.getOperator()) {
                case 'in':
                    operator = 'in';
                    break;
                case 'lt':
                    operator = 'less';
                    if (
                        !advancedfilter.isAttributesFilterEmpty() &&
                        advancedfilter.getAttributes()[attribute] &&
                        !Ext.isDate(value)
                    ) {
                        var attributeFilter = advancedfilter.getAttributes()[attribute][0];
                        if (attributeFilter.attributeId !== filter.getId()) {
                            advancedfilter.removeAttributeFilter(attribute, attributeFilter.attributeId);
                            operator = 'between';
                            value = [attributeFilter.value[0], value];
                        }
                    }
                    break;
                case 'gt':
                    operator = 'greater';
                    if (
                        !advancedfilter.isAttributesFilterEmpty() &&
                        advancedfilter.getAttributes()[attribute] &&
                        !Ext.isDate(value)
                    ) {
                        var attributeFilter = advancedfilter.getAttributes()[attribute][0];
                        if (attributeFilter.attributeId !== filter.getId()) {
                            advancedfilter.removeAttributeFilter(attribute, attributeFilter.attributeId);
                            operator = 'between';
                            value = [value, attributeFilter.value[0]];
                        }
                    }
                    break;
                case 'eq':
                case '==':
                    operator = 'equal';
                    break;
                case 'like':
                    operator = 'contain';
                    break;
                case 'description_like':
                    operator = 'description_contains';
                    break;
                case 'overlap':
                    operator = 'overlap';
                    break;
            }

            // for dates use between operator
            if (Ext.isDate(value)) {
                if (this.isDateField) {
                    value = Ext.Date.localToUtc(value);
                }
                switch (operator) {
                    case 'equal':
                        operator = 'between';
                        value = [value, Ext.Date.subtract(value, Ext.Date.DAY, -0.99999)];
                        break;
                    case 'greater':
                        var filterAttribute = advancedfilter.getAttributes()[attribute];
                        operator = 'between';
                        // if there is already a filter check if the second value is
                        // greater than current value and set range
                        if (
                            filterAttribute &&
                            filterAttribute.length &&
                            (filterAttribute[0].value.length !== 2 || filterAttribute[0].value[1] !== 'infinity')
                        ) {
                            if (filterAttribute[0].value[1] >= value) {
                                value = [value, filterAttribute[0].value[1]];
                                advancedfilter.removeAttributeFilter(attribute, filterAttribute[0].attributeId);
                            }
                        } else {
                            value = [value, 'infinity'];
                        }
                        break;
                    case 'less':
                        var filterAttribute = advancedfilter.getAttributes()[attribute];
                        operator = 'between';
                        // if there is already a filter check if the first value is
                        // minor than current value and set range
                        if (
                            filterAttribute &&
                            filterAttribute.length &&
                            (filterAttribute[0].value.length !== 2 || filterAttribute[0].value[0] !== '-infinity')
                        ) {
                            if (filterAttribute[0].value[0] <= value) {
                                value = [filterAttribute[0].value[0], value];
                                advancedfilter.removeAttributeFilter(attribute, filterAttribute[0].attributeId);
                            }
                        } else {
                            value = ['-infinity', value];
                        }
                        break;
                }
            }

            advancedfilter.removeAttributeFilter(attribute, filter.getId());
            advancedfilter.addAttributeFilter(attribute, operator, value, filter.getId());
        },

        /**
         *
         * @param {Ext.data.ProxyStore} store
         * @param {CMDBuildUI.util.AdvancedFilter} filter
         */
        removeAdvancedFilter: function (store, filter) {
            var attribute = filter.getProperty(),
                filterid = filter.getId(),
                operator = filter.getOperator(),
                advancedfilter = store.getAdvancedFilter(),
                filterAttribute = advancedfilter.getAttributes()[attribute],
                isFilterAttribute = filterAttribute && filterAttribute.length,
                storeFilter = isFilterAttribute ? filterAttribute[0] : null,
                firstValueFilter = storeFilter.value[0],
                secondValueFilter = storeFilter.value[1],
                isBetween = isFilterAttribute && firstValueFilter != '-infinity' && secondValueFilter != 'infinity',
                isDate = isFilterAttribute && (Ext.isDate(firstValueFilter) || Ext.isDate(secondValueFilter));

            if (isBetween && operator == 'gt' && Ext.isDate(secondValueFilter)) {
                // if the attribute has a between date range and operator is greater then set less then filter
                storeFilter.value = ['-infinity', secondValueFilter];
            } else if (isBetween && operator == 'lt' && Ext.isDate(firstValueFilter)) {
                // if the attribute has a between date range and operator is less then set greater then filter
                storeFilter.value = [firstValueFilter, 'infinity'];
            } else if (isDate) {
                // if is date filter remove the filter by attribute
                advancedfilter.removeAttributeFilter(attribute);
            } else {
                // if the attribute is numeric and remove one value on extreme when operator is between
                if (isFilterAttribute && storeFilter.operator == 'between' && (operator == 'lt' || operator == 'gt')) {
                    var less = operator == 'lt',
                        idCommon = storeFilter.attributeId.slice(0, -2);
                    advancedfilter.clearAttributesFilter();
                    advancedfilter.addAttributeFilter(
                        attribute,
                        less ? 'greater' : 'less',
                        less ? firstValueFilter : secondValueFilter,
                        less ? idCommon + 'gt' : idCommon + 'lt'
                    );
                } else {
                    // otherwise remove the filter by attribute and filterid
                    advancedfilter.removeAttributeFilter(attribute, filterid);
                }
            }
        }
    }
});
