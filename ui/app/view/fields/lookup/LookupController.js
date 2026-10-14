Ext.define('CMDBuildUI.view.fields.lookup.LookupController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.fields-lookupfield',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#combo0': {
            change: 'onMainComboChange'
        }
    },

    /**
     * On before render
     * @param {CMDBuildUI.view.fields.lookup.Lookup} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = this.getViewModel();
        var items = [];
        var stores = {};
        var values = {};
        var comboscount = 0;

        var singlelookup;

        var lt = Ext.getStore('lookups.LookupTypes').getById(
            CMDBuildUI.util.Utilities.stringToHex(view.getLookupType())
        );
        while (lt) {
            var hasparent = !Ext.isEmpty(lt.get('parent'));
            var isMainCombo = comboscount === 0;
            var parentlookupcomboid;

            singlelookup = isMainCombo && !hasparent ? true : false;

            // add store
            var storename = 'options' + comboscount;
            var store = {
                model: 'CMDBuildUI.model.lookups.Lookup',
                proxy: {
                    url: CMDBuildUI.util.api.Lookups.getLookupValues(lt.get('name')),
                    type: 'baseproxy'
                },
                pageSize: 0,
                remoteFilter: false,
                remoteSort: true,
                autoLoad: false, // store is loaded on onBindStore function
                autoDestroy: true
            };

            // if (comboscount !== 0) {
            values['value' + comboscount] = null;
            // }

            // define bind
            var bind = isMainCombo && view.initialConfig.bind ? view.initialConfig.bind : {};
            bind = Ext.applyIf(bind, {
                store: Ext.String.format('{{0}}', storename),
                value: Ext.String.format('{value{0}}', comboscount)
            });

            var viewmodel = {
                data: {
                    defaultvalue: null
                },
                formulas: {
                    updateValueFromDefault: {
                        bind: {
                            defaultvalue: isMainCombo ? bind.value : '{defaultvalue}',
                            storelength: '{' + storename + '.data.length}',
                            ready: '{ready}'
                        },
                        get: me.updateDefaultValue
                    }
                }
            };

            if (hasparent) {
                store.storeId = comboscount;
                store.listeners = {
                    filterchange: function (s, f) {
                        var depcombo = view.lookupReference(Ext.String.format('combo{0}', s.storeId));
                        if (depcombo.getStore() && s.find('_id', depcombo.getValue()) == -1) {
                            depcombo.clearValue();
                        }
                    }
                };
                parentlookupcomboid = Ext.String.format('combo{0}', comboscount + 1);

                viewmodel.formulas.updateFilter = {
                    bind: Ext.String.format('{value{0}}', comboscount + 1),
                    get: function (value) {
                        this.getView().getStore().filter({
                            property: 'parent_id',
                            value: value
                        });
                    }
                };
            }

            stores[storename] = store;

            var combo = {
                bind: bind,
                viewModel: viewmodel,
                name: isMainCombo ? view.getName() : undefined,
                emptyText: view.emptyText ? view.emptyText : '',
                reference: Ext.String.format('combo{0}', comboscount),
                itemId: Ext.String.format('combo{0}', comboscount),
                queryMode: 'local',
                padding: hasparent ? '8px 0 0' : null,
                tabIndex: view.tabIndex,
                parentLookupComboId: parentlookupcomboid,
                metadata: isMainCombo ? view.metadata : {},
                ignoreCqlFilter: view.ignoreCqlFilter
            };

            if (view.getLookupIdField()) {
                combo.valueField = view.getLookupIdField();
            }

            // add filter configuration
            if (isMainCombo && view.metadata.ecqlFilter) {
                Ext.apply(combo, {
                    recordLinkName: view.getRecordLinkName(),
                    filter: view.metadata.ecqlFilter
                });
            }

            // update mandatory info
            if (isMainCombo && view.allowBlank == false) {
                combo.allowBlank = false;
            }

            // set custom validation to combo
            if (isMainCombo && Ext.isFunction(view.getValidation)) {
                combo.getValidation = view.getValidation;
                view.getValidation = Ext.emptyFn;
            }

            // set custom style rules if field has isReorderGrid property
            if (view.isReorderGrid) {
                combo.height = 19;
                combo.minHeight = 19;
                combo.maxHeight = 19;
                combo.padding = 0;
                combo.ui = 'reordergrid-editor-combo';
            }
            // define item
            Ext.Array.insert(items, 0, [combo]);

            comboscount++;
            if (hasparent) {
                lt = Ext.getStore('lookups.LookupTypes').getById(
                    CMDBuildUI.util.Utilities.stringToHex(lt.get('parent'))
                );
            } else {
                lt = null;
            }
        }

        vm.setStores(stores);
        vm.set(values);
        if (view) {
            view.add(items);
        }
    },

    /**
     * @param {Ext.form.field.ComboBox} combo
     * @param {Numeric|String} newvalue
     * @param {Numeric|String} oldvalue
     * @param {Object} eOpts
     */
    onMainComboChange: function (combo, newvalue, oldvalue, eOpts) {
        var parent = this.getView(),
            ownerRecord = combo._ownerRecord || combo.ownerCt._ownerRecord;

        function updateObjectMetaFrom(object) {
            if (object && object.isModel) {
                var selected = combo.getSelection();
                object.set(Ext.String.format('_{0}_code', parent.getName()), selected ? selected.get('code') : null);
                object.set(
                    Ext.String.format('_{0}_description', parent.getName()),
                    selected ? selected.get('description') : null
                );
                object.set(
                    Ext.String.format('_{0}_description_translation', parent.getName()),
                    selected ? selected.get('_description_translation') : null
                );
            }
        }

        if (ownerRecord) {
            updateObjectMetaFrom(ownerRecord);
        } else if (parent.column && !parent.column._hasEditEvent) {
            parent.column.getView().ownerGrid.on({
                edit: {
                    fn: function (editor, context, eOpts) {
                        if (parent.getName() === context.field) {
                            context.record.set(parent.getName(), newvalue);
                            // updateObjectMetaFrom(context.record);
                        }
                    },
                    scope: this,
                    single: true
                }
            });
            // parent.column._hasEditEvent = true;
        }
    },

    privates: {
        /**
         * Update default value for lookup
         * @param {Object} data
         * @param {Number} data.defaultvalue
         * @param {Number} data.storelength
         * @param {Boolean} data.ready
         */
        updateDefaultValue: function (data) {
            if (data.ready && data.defaultvalue && data.storelength !== null) {
                // get combo store
                var combo = this.getView(),
                    store = combo.getStore(),
                    record;

                if (store.getCount() > 0) {
                    // set record
                    record = combo.findRecordByValue(data.defaultvalue);
                    combo.setValue(record);
                } else {
                    // clear filter
                    store.clearFilter();
                }

                // set parent combo right value
                if (record && record.get('parent_id')) {
                    // get lookup parent combo
                    var parentcombo = combo.up('fieldcontainer').lookupReference(combo.getParentLookupComboId());
                    // set default value in lookup parent combo view model

                    if (parentcombo) {
                        var pvm = parentcombo.lookupViewModel();
                        pvm.set('defaultvalue', record.get('parent_id'));
                    } else {
                        CMDBuildUI.util.Logger.log(
                            message,
                            CMDBuildUI.util.Logger.levels.warn,
                            'parent combo should be present. Inspect here'
                        );
                    }
                }
            }
        }
    }
});
