Ext.define('CMDBuildUI.view.relations.list.expanded.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-list-expanded-fieldset',
    mixins: ['CMDBuildUI.view.relations.Mixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.list.expanded.Fieldset} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        const vm = view.lookupViewModel();
        const direction = vm.get('direction');
        const domain = vm.get('domain');
        var dtype;
        var dtypename;
        var stypename;

        // get destination class/process
        if (direction === '_1') {
            vm.set('basetitle', domain.get('_descriptionInverse_translation'));
            dtype = domain.get('sourceProcess')
                ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            dtypename = domain.get('source');
            stypename = domain.get('destination');
        } else if (direction === '_2') {
            vm.set('basetitle', domain.get('_descriptionDirect_translation'));
            dtype = domain.get('destinationProcess')
                ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
            dtypename = domain.get('destination');
            stypename = domain.get('source');
        }

        const sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(
            CMDBuildUI.util.helper.ModelHelper.getObjectFromName(dtypename, dtype),
            true
        );
        vm.set('storeinfo.sorters', sorters);

        CMDBuildUI.util.helper.ModelHelper.getModel(dtype, dtypename).then(
            function (model) {
                // set model name
                vm.set('storeinfo.model', model.getName());

                // set advanced filter
                vm.set('storeinfo.advancedfilter', {
                    relation: [
                        {
                            domain: domain.getId(),
                            type: 'oneof',
                            destination: dtypename,
                            source: stypename,
                            direction: direction,
                            cards: [
                                {
                                    className: vm.get('objectTypeName'),
                                    id: vm.get('objectId')
                                }
                            ]
                        }
                    ]
                });

                // set auto load to true
                vm.set('storeinfo.autoload', true);

                // get columns
                CMDBuildUI.util.helper.GridHelper.getColumnsForType(dtype, dtypename, {
                    addTypeColumn: true
                }).then(function (cols) {
                    domain.getAttributes().then(function (attrs) {
                        // get column defs for relation attributes
                        attrs.getRange().forEach(function (attr) {
                            const field = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                            if (
                                field &&
                                field.cmdbuildtype !== CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.formula
                            ) {
                                const col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                                if (col) {
                                    col.hidden = false;
                                    col.dataIndex = '_relAttr_' + attr.get('name');
                                    cols.push(col);
                                }
                            }
                        });

                        // add grid
                        view.add({
                            xtype: 'grid',
                            columns: Ext.Array.merge(cols, me.getActionColumns()),
                            forceFit: true,
                            viewConfig: {
                                stripeRows: false,
                                markDirty: false
                            },
                            bind: {
                                store: '{records}',
                                hidden: '{!recordsCount}'
                            },
                            hidden: true,
                            height: 0,
                            listeners: {
                                show: function () {
                                    view.calculateHeight();
                                }
                            }
                        });
                    });
                });
            },
            function () {
                vm.set('counters.stores', vm.get('counters.stores') + 1);
            }
        );
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     */
    onStoreLoad: function (store, records) {
        const vm = this.getViewModel();
        const allRelations = vm.get('allRelations');

        function setAttributesRelation(rec, relation) {
            Ext.Object.each(relation.get('_relationAttributes'), function (key, value, allobjects) {
                rec.set('_relAttr_' + key, relation.get(key));
            });
        }

        Ext.Array.forEach(records, function (record, indexRecord, allrecords) {
            const relationsSameRecord = allRelations.queryBy(function (item, id) {
                return item.get('_destinationId') == record.getId() && item.get('_type') == vm.get('domain').getId();
            });
            Ext.Array.forEach(relationsSameRecord.getRange(), function (relation, index, allitems) {
                if (!Ext.Object.isEmpty(relation.get('_relationAttributes'))) {
                    if (!index) {
                        setAttributesRelation(record, relation);
                    } else {
                        const recordClone = record.clone();
                        recordClone.setId(record.getId() + '_' + relation.getId());
                        setAttributesRelation(recordClone, relation);
                        store.add(recordClone);
                    }
                }
            });
        });

        vm.set('recordsCount', store.getRange().length);
        if (store.loadCount === 0 || store.loadCount === 1) {
            vm.set('counters.stores', vm.get('counters.stores') + 1);
        }

        this.getView().calculateHeight();
    }
});
