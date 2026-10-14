Ext.define('CMDBuildUI.view.fields.lookup.LookupCombo', {
    extend: 'Ext.form.field.ComboBox',

    requires: [
        'CMDBuildUI.view.fields.lookup.LookupComboController'
    ],

    alias: 'widget.lookupcombofield',
    controller: 'fields-lookupcombofield',
    viewModel: {},

    config: {
        /**
         * @cfg {String} recordLinkName
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null,

        /**
         * @cfg {Object} filter
         * Ecql filter definition.
         */
        filter: null,

        /**
         * @cfg {String} parentLookupComboId
         * The reference id of parent lookup combo.
         */
        parentLookupComboId: null,

        /**
         * @cfg {Boolean} ignoreFilter
         * Boolean value to ignore the ecql filter
         */
        ignoreCqlFilter: false
    },

    valueField: '_id',
    displayField: 'text',

    autoLoadOnValue: false,
    autoSelect: false,
    autoSelectLast: false,

    // query configuration
    anyMatch: true,
    queryMode: 'local',
    queryDelay: 250,

    forceSelection: true,
    typeAhead: false,

    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent("cleartrigger", combo, trigger, eOpts);
            }
        }
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
        var me = this;
        var vm = this.getViewModel();
        if (store.getModel().getName()) {
            if (me.getRecordLinkName() && me.getFilter()) {
                // get binds
                var binds = CMDBuildUI.util.ecql.Resolver.getViewModelBindings(
                    me.getFilter(),
                    me.getRecordLinkName()
                );
                if (!Ext.Object.isEmpty(binds) && !me.ignoreCqlFilter) {
                    vm.bind({
                        bindTo: binds
                    }, function (data) {
                        var value = me.getValue();
                        store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
                        store.addListener('load', function (store, records, successful, operation, eOpts) {
                            me.setValue(value);
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
                    if (!me.ignoreCqlFilter) {
                        // add ecql filter
                        store.getAdvancedFilter().addEcqlFilter(me.getEcqlFilter());
                    }
                    store.load();
                }

            } else {
                // load store data
                store.load();
            }

            vm.set("ready", true);
            // add load listener
            store.addListener("load", me.onStoreLoaded, me);
        }
        this.callParent(arguments);
    },

    /**
     * Get proxy store extra params
     * @return {Object}
     */
    getEcqlFilter: function () {
        var ecql;
        var obj = this._ownerRecord || this.ownerCt._ownerRecord;
        if (!obj) {
            obj = this.lookupViewModel().get(this.getRecordLinkName());
        }
        if (this.getFilter() && obj) {
            ecql = CMDBuildUI.util.ecql.Resolver.resolve(this.getFilter(), obj);
        }
        return ecql || {};
    },

    /**
     * Called when store is loaded
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records An array of records
     * @param {Boolean} successful True if the operation was successful.
     * @param {Ext.data.operation.Read} operation The {@link Ext.data.operation.Read Operation} object that was used in the data load call
     * @param {Object} eOpts
     */
    onStoreLoaded: function (store, records, successful, operation, eOpts) {
        // preselect item if unique
        if (
            (this.metadata.preselectIfUnique === true || this.metadata.preselectIfUnique === "true") &&
            store.getTotalCount() === 1
        ) {
            try {
                this.setValue(records[0].getId());
            } catch (e) {
                console.error(e);
            }
        }
    }
});