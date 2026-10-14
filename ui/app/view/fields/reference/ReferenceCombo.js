Ext.define('CMDBuildUI.view.fields.reference.ReferenceCombo', {
    extend: 'Ext.form.field.ComboBox',

    requires: [
        'CMDBuildUI.view.fields.reference.ReferenceComboController',
        'CMDBuildUI.view.fields.reference.ReferenceComboModel'
    ],

    alias: 'widget.referencecombofield',
    controller: 'fields-referencecombofield',
    viewModel: {
        type: 'fields-referencecombofield'
    },

    valueField: '_id',
    displayField: 'Description',
    autoLoadOnValue: false,
    autoSelect: false,
    autoSelectLast: false,

    // query configuration
    anyMatch: true,
    queryMode: 'local',
    queryDelay: 250,

    forceSelection: true,
    typeAhead: false,

    cls: Ext.baseCSSPrefix + 'referenceloading',
    disabledCls: null,

    config: {
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for
         * value binding.
         */
        recordLinkName: null,
        /**
         * @cfg {String} recordLinkName (required)
         * The name of the full record in ViewModel used for filtering.
         */
        filterRecordLinkName: null,

        /**
         * @cfg {Boolean} ignoreCqlFilter
         * Boolean value to ignore the ecql filter
         */
        ignoreCqlFilter: false,

        /**
         * @cfg {Object} customFormTargetObj
         * theTarget object used for cellediting in customForms
         */
        customFormTargetObj: null
    },

    bind: {
        selection: '{selection}'
    },

    triggers: {
        clear: {
            cls: 'x-form-clear-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent('cleartrigger', combo, trigger, eOpts);
            }
        },
        search: {
            cls: 'x-form-search-trigger',
            handler: function (combo, trigger, eOpts) {
                combo.fireEvent('searchtrigger', combo, trigger, eOpts);
            }
        }
    },

    constructor: function () {
        Ext.applyIf(this, {
            // override template to convert html to text
            tpl:
                '<tpl for=".">' +
                '<li role="option" unselectable="on" class="' +
                Ext.baseCSSPrefix +
                'boundlist-item">' +
                '{[CMDBuildUI.util.helper.FieldsHelper.renderTextField(values["' +
                this.config.displayField +
                '"], {skipnewline: true})]}' +
                '</li></tpl>'
        });
        this.callParent(arguments);
    },

    /**
     * Update value
     * Load record if it is not in store.
     */
    setValue: function (value) {
        const me = this;
        if (value && !value.isModel) {
            const store = me.getStore();
            if (!store.getById(value)) {
                if (store.getProxy().getUrl) {
                    const model = store.getModel();
                    model.getProxy().setUrl(store.getProxy().getUrl());

                    const operations = Ext.Object.getValues(model.getProxy().pendingOperations);
                    const activeOperations = Ext.Array.findBy(operations, function (item, index, allitems) {
                        return item.getId() == value;
                    });

                    if (!activeOperations) {
                        model.load(value, {
                            success: function (record) {
                                store.add(record);
                                me.setSelection(record);
                            }
                        });
                    }
                } else {
                    me.lookupViewModel().set('initialvalue', value);
                }
            }
        }
        this.callParent(arguments);
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
        const me = this;
        // add record if it is not in list
        let count = store.getTotalCount();

        // remove laoding class
        me.removeCls(me.cls);
        const metadata = operation.getProxy().getReader().metaData;

        if (
            me.hasReferenceValue &&
            !me.relsfilter &&
            metadata.positions &&
            !metadata.positions[this.getValue()].found
        ) {
            this.setValue();
        }

        me.hasReferenceValue = false;

        // if exists owner record
        const value = me._ownerRecord && me._ownerRecord.isModel ? me._ownerRecord.get(me.getName()) : this.getValue();
        if (
            store.loadCount === 1 && // is first load
            me._ownerRecord &&
            value && // the value is not empty
            !store.getById(value) // the element is not in the store
        ) {
            const desc = me._ownerRecord.get('_' + this.name + '_description');
            if (desc) {
                store.add([
                    {
                        _id: this.getValue(),
                        Description: desc
                    }
                ]);
                count++;
            }
        }

        if (value !== this.getValue()) {
            this.setValue(value);
        }

        // preselect item if unique
        if ((this.metadata.preselectIfUnique === true || this.metadata.preselectIfUnique === 'true') && count === 1) {
            try {
                this.setValue(store.getAt(0).getId());
            } catch (e) {
                console.error(e);
            }
        }
        // check expander
        if (count > CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.referencecombolimit)) {
            this._allowexpand = false;
        } else {
            this._allowexpand = true;
        }

        // remove positionOf param
        if (store.proxy.extraParams && store.proxy.extraParams.positionOf) {
            delete store.proxy.extraParams.positionOf;
        }

        this.validate();
    },

    /**
     * Get proxy store extra params
     * @return {Object}
     */
    getEcqlFilter: function () {
        let ecql;
        let obj;
        if (this.getCustomFormTargetObj()) {
            obj = this.getCustomFormTargetObj();
        } else {
            if (this.getFilterRecordLinkName() && this.getFilterRecordLinkName() !== this.getRecordLinkName()) {
                obj = this.lookupViewModel().get(this.getFilterRecordLinkName());
            } else {
                if (this._ownerRecord) {
                    obj = this._ownerRecord;
                } else {
                    obj = this.lookupViewModel().get(this.getRecordLinkName());
                }
            }
        }

        if (this.metadata.ecqlFilter) {
            ecql = CMDBuildUI.util.ecql.Resolver.resolve(this.metadata.ecqlFilter, obj);
        }
        return ecql || {};
    },

    /**
     * @override
     * Expands this field's picker dropdown.
     */
    expand: function (searchquery) {
        if (this._allowexpand === true) {
            this.callParent(arguments);
        } else {
            this.fireEvent('searchtrigger', this, null, {
                searchquery: searchquery
            });
        }
    },

    /**
     *
     * @param {Mixed} val
     * @return {Boolean}
     */
    validator: function (val) {
        if (this.getStore() && this.getStore().isLoaded()) {
            return true;
        }
        return false;
    },

    privates: {
        _allowexpand: null
    }
});
