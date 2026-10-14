Ext.define('CMDBuildUI.view.fields.bufferedcombo.BufferedComboField', {
    extend: 'Ext.form.field.ComboBox',

    requires: [
        'CMDBuildUI.view.fields.bufferedcombo.BufferedComboFieldController',
        'CMDBuildUI.view.fields.bufferedcombo.BufferedComboFieldModel'
    ],

    alias: 'widget.bufferedcombofield',
    controller: 'fields-bufferedcombofield',
    viewModel: {
        type: 'fields-bufferedcombofield'
    },


    autoLoadOnValue: true,
    autoSelect: true,
    autoSelectLast: true,

    // query configuration
    anyMatch: true,
    queryMode: 'local',
    queryDelay: 250,

    forceSelection: true,
    typeAhead: false,

    config: {
        /**
         * @cfg {String} modelname (required)
         * The name of the model used.
         * 
         */
        modelname: null,
        /**
         * @cfg {String} storealias (required)
         * The alias aof the model.
         */
        storealias: null,
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for 
         * value binding.
         */
        recordLinkName: null,
        /**
         * @cfg {String} valueField (required)
         * The name of primary key of model
         */
        valueField: '_id',
        /**
         * @cfg {String} displayField (required)
         * The name of display field of model
         */
        displayField: 'description'
    },

    bind: {
        store: '{options}',
        selection: '{selection}'
    },

    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent("cleartrigger", combo, trigger, eOpts);
            }
        },
        search: {
            cls: 'x-form-search-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent("searchtrigger", combo, trigger, eOpts);
            }
        }
    },
    initComponent: function () {
        var me = this;
        if (!me.autoEl) {
            me.autoEl = {};
        }

        me.autoEl['data-testid'] = Ext.String.format('bufferedcombo-{0}-input', me.getName() || 'noname');

        me.callParent(arguments);
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
        var vm = me.getViewModel();
        if (store.getModel().getName()) {
            store.addListener("load", me.onStoreLoaded, this);
            // load store
            store.load();
        }
        if (me.getInitialConfig().bind && !Ext.Object.isEmpty(me.getInitialConfig().bind)) {
            vm.bind({
                bindTo: me.getInitialConfig().bind.value
            }, function (value) {
                vm.set("initialvalue", value);
            });
        } else {
            vm.set("initialvalue", null);
        }
        me.callParent(arguments);
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
        // add record if it is not in list        
        var count = store.getTotalCount();
        if (!store.getById(this.lookupViewModel().get("initialvalue")) && this._ownerRecord) {
            var _id = this._ownerRecord.get(this.name);
            var desc = this._ownerRecord.get("_" + this.name + "_description");
            if (_id && desc) {
                store.add([{
                    _id: _id,
                    Description: desc
                }]);
                this.setValue(this._ownerRecord.get(this.name));
                count++;
            }
        }
        // preselect item if unique
        if (
            (this.preselectIfUnique === true || this.preselectIfUnique === "true") &&
            count === 1
        ) {
            this.setValue(store.getAt(0).getId());
        }
        // check expander
        if (count > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit)) {
            this._allowexpand = false;
        } else {
            this._allowexpand = true;
        }
    },


    /**
     * @override
     * Expands this field's picker dropdown.
     */
    expand: function (searchquery) {
        if (this._allowexpand === true) {
            this.callParent(arguments);
        } else {
            this.fireEvent("searchtrigger", this, null, {
                searchquery: searchquery
            });
        }
    },

    privates: {
        _allowexpand: null
    }
});