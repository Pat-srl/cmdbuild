Ext.define('CMDBuildUI.view.fields.lookuparray.Field', {
    extend: 'Ext.form.field.Tag',

    requires: [
        'CMDBuildUI.view.fields.lookuparray.FieldController',
        'CMDBuildUI.view.fields.lookuparray.FieldModel'
    ],

    alias: 'widget.lookuparrayfield',
    controller: 'fields-lookuparray-field',
    viewModel: {
        type: 'fields-lookuparray-field'
    },

    config: {
        /**
         * @cfg {String} recordLinkName
         * The name of the full record in ViewModel used for value binding.
         */
        recordLinkName: null
    },

    bind: {
        store: '{lookupValues}'
    },

    filterPickList: true,
    valueField: '_id',
    displayField: 'text',
    queryMode: 'local',

    /**
     * Get lookup type.
     * @return {String}
     */
    getLookupType: function () {
        return this.metadata.lookupType;
    },

    /**
     * @override
     * @method
     * Template method, it is called when a new store is bound
     * to the current instance.
     * @protected
     * @param {Ext.data.AbstractStore} store The store being bound
     * @param {Boolean} initial True if this store is being bound as initialization of the instance.
     */
    onBindStore: function (store, initial) {
        var me = this,
            vm = this.getViewModel(),
            filter = this.metadata.ecqlFilter;
        if (store.getModel().getName()) {
            if (me.getRecordLinkName() && filter) {
                // get binds
                var binds = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(
                    filter,
                    me.getRecordLinkName()
                );
                if (!Ext.Object.isEmpty(binds)) {
                    vm.bind({
                        bindTo: binds
                    }, function () {
                        var value = me.getValue();
                        store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
                        store.addListener('load', function (store, records, successful, operation, eOpts) {
                            // remove value item if not present in store records
                            var _value = [];
                            for (var i = 0; i < value.length; i++) {
                                if (store.findExact('_id', value[i].toString()) > -1) {
                                    _value.push(value[i]);
                                }
                            }
                            me.setValue(_value);
                        }, me, {
                            single: true,
                            priority: 1
                        });

                        if (store.isLoading()) {
                            var operations = Ext.Object.getValues(store.getProxy().pendingOperations),
                                operation = operations.length ? operations[0] : null;
                            operation ? operation.abort() : null;
                        }

                        store.load();
                    });
                } else {
                    // add ecql filter
                    store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
                    store.load();
                }
            } else {
                // load store data
                store.load();
            }
        }
        this.callParent(arguments);
    },

    /**
     * Get proxy store extra params
     * @return {Object}
     */
    getEcqlFilter: function () {
        var ecql,
            obj = this._ownerRecord,
            filter = this.metadata.ecqlFilter;
        if (!obj) {
            obj = this.lookupViewModel().get(this.getRecordLinkName());
        }
        if (filter && obj) {
            ecql = CMDBuildUI.util.ecql.Resolver.resolve(filter, obj);
        }
        return ecql || {};
    }
});