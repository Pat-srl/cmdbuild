Ext.define('CMDBuildUI.view.relations.fieldset.FieldsetController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.relations-fieldset',
    mixins: ['CMDBuildUI.view.relations.Mixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addrelationbtn': {
            click: 'onAddRelationBtnClick'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.relations.fieldset.Fieldset} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();
        const me = this;
        const domain = vm.get('domain');
        const referenceDomains = [];
        const obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            vm.get('objectTypeName'),
            vm.get('objectType')
        );
        const objectHierarchy = obj.getHierarchy();

        obj.getAttributes().then(function (items) {
            items.getRange().forEach(function (item) {
                if (item.get('type') === CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference) {
                    referenceDomains.push(item.get('domain'));
                }
            });
            vm.set('referenceDomains', referenceDomains);
        });

        obj.getDomains().then(function (domains) {
            const menu = [];
            var addbtnDisabled = true;

            const editableDomains = {};

            domains.each(function (domain) {
                if (
                    Ext.Array.contains(objectHierarchy, domain.get('source')) &&
                    !Ext.Array.contains(domain.get('disabledSourceDescendants'), vm.get('objectTypeName')) &&
                    !domain.get('destinationProcess') &&
                    domain.get('sourceEditable')
                ) {
                    editableDomains[domain.get('name')] = true;
                }

                if (
                    Ext.Array.contains(objectHierarchy, domain.get('destination')) &&
                    !Ext.Array.contains(domain.get('disabledDestinationDescendants'), vm.get('objectTypeName')) &&
                    !domain.get('sourceProcess') &&
                    domain.get('targetEditable')
                ) {
                    editableDomains[domain.get('name')] = true;
                }
                // enable add relation button if there is at least one domain that can be added
                if (addbtnDisabled && domain.get(CMDBuildUI.model.base.Base.permissions.add)) {
                    addbtnDisabled = false;
                }
            });

            vm.set('editableDomains', editableDomains);
            vm.set('addbtn.disabled', addbtnDisabled);
        });

        vm.bind('{targetmodel}', function (targetmodel) {
            if (targetmodel) {
                // details xtype
                var detailxtype, readonly;
                if (vm.get('targettype') === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    detailxtype = 'processes-instances-rowcontainer';
                    readonly = true;
                    vm.set(
                        'relstore.proxyurl',
                        CMDBuildUI.util.api.Processes.getProcessInstanceRelations(
                            vm.get('current.objectTypeName'),
                            vm.get('current.objectId')
                        )
                    );
                } else {
                    detailxtype = 'classes-cards-card-view';
                    readonly = view.readOnly;
                    vm.set(
                        'relstore.proxyurl',
                        CMDBuildUI.util.api.Classes.getCardRelations(
                            vm.get('current.objectTypeName'),
                            vm.get('current.objectId')
                        )
                    );
                    if (view.formmode === CMDBuildUI.util.helper.FormHelper.formmodes.read) {
                        readonly = true;
                    } else {
                        if (
                            CMDBuildUI.view.relations.Utils.canAddDirect(
                                domain,
                                objectHierarchy,
                                vm.get('objectTypeName')
                            )
                        ) {
                            readonly = !CMDBuildUI.view.relations.Utils.canAddRelation(
                                domain,
                                'direct',
                                referenceDomains
                            );
                        } else if (
                            CMDBuildUI.view.relations.Utils.canAddInverse(
                                domain,
                                objectHierarchy,
                                vm.get('objectTypeName')
                            )
                        ) {
                            readonly = !CMDBuildUI.view.relations.Utils.canAddRelation(
                                domain,
                                'inverse',
                                referenceDomains
                            );
                        }
                    }
                }

                vm.set('addrelationbtn.hidden', readonly);
                vm.set('readonly', readonly);
                vm.bind('{allRelations}', function (store) {
                    store.load({
                        callback: function (records, operation, success) {
                            vm.getStore('records').load();
                            // get columns
                            CMDBuildUI.util.helper.GridHelper.getColumnsForType(
                                vm.get('targettype'),
                                vm.get('targettypename')
                            ).then(function (cols) {
                                vm.get('domain')
                                    .getAttributes()
                                    .then(function (attrs) {
                                        // get column defs for relation attributes
                                        attrs.getRange().forEach(function (attr) {
                                            const field =
                                                CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(attr);
                                            const col = CMDBuildUI.util.helper.GridHelper.getColumn(field);
                                            if (col) {
                                                const baserenderer = col.renderer;
                                                col.hidden = false;
                                                col.renderer = function (
                                                    value,
                                                    metaData,
                                                    record,
                                                    rowindex,
                                                    colindex,
                                                    store,
                                                    view
                                                ) {
                                                    // find relation item
                                                    const rels = view.lookupViewModel().get('allRelations');
                                                    if (rels) {
                                                        const recordId = record.getId().split('_')[0];
                                                        const relId = record.getId().split('_')[1];
                                                        var relPos = -1;
                                                        if (relId) {
                                                            relPos = rels.find('_id', relId);
                                                        } else {
                                                            relPos = rels.findBy(function (relItem) {
                                                                return (
                                                                    relItem.get('_type') === vm.get('domain').getId() &&
                                                                    relItem.get('_destinationId') == recordId
                                                                );
                                                            });
                                                        }
                                                        if (relPos !== -1) {
                                                            const relItem = rels.getAt(relPos);
                                                            // return base render
                                                            if (baserenderer) {
                                                                return baserenderer(
                                                                    relItem.get(attr.get('name')),
                                                                    metaData,
                                                                    relItem,
                                                                    rowindex,
                                                                    colindex,
                                                                    store,
                                                                    view
                                                                );
                                                            } else {
                                                                return relItem.get(attr.get('name'));
                                                            }
                                                        }
                                                    }
                                                };
                                                cols.push(col);
                                            }
                                        });
                                        if (!view.destroyed) {
                                            // add grid
                                            view.add({
                                                xtype: 'relations-fieldset-grid',
                                                columns: Ext.Array.merge(cols, me.getActionColumns()),
                                                maxHeight:
                                                    (CMDBuildUI.util.helper.Configurations.get(
                                                        CMDBuildUI.model.Configuration.ui.relationlimit
                                                    ) +
                                                        1) *
                                                        31 +
                                                    15,
                                                listeners: {
                                                    rowdblclick: function (grid, record, element, rowIndex, e, eOpts) {
                                                        const id = record.getId().split('_')[0];
                                                        me.redirectToItem(
                                                            vm.get('targettype'),
                                                            record.get('_type'),
                                                            id
                                                        );
                                                    },
                                                    rowclick: function (grid, record, element, rowIndex, e, eOpts) {
                                                        if (
                                                            vm.get('targettype') ===
                                                            CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                                                        ) {
                                                            vm.set(
                                                                'relstore.proxyurl',
                                                                CMDBuildUI.util.api.Processes.getProcessInstanceRelations(
                                                                    vm.get('current.objectTypeName'),
                                                                    vm.get('current.objectId')
                                                                )
                                                            );
                                                        } else {
                                                            vm.set(
                                                                'relstore.proxyurl',
                                                                CMDBuildUI.util.api.Classes.getCardRelations(
                                                                    vm.get('current.objectTypeName'),
                                                                    vm.get('current.objectId')
                                                                )
                                                            );
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    });
                            });
                        }
                    });
                });
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddRelationBtnClick: function (button, eOpts) {
        const vm = this.getViewModel();
        const view = this.getView();
        const domain = vm.get('domain');
        const direction = vm.get('direction') === '_1' ? 'inverse' : 'direct';

        var popup;
        const title = vm.get('basetitle');
        const config = {
            xtype: 'relations-list-add-container',
            originTypeName: vm.get('objectTypeName'),
            originId: vm.get('objectId'),
            multiSelect: true,
            viewModel: {
                data: {
                    theObject: vm.get('theObject'),
                    objectTypeName: vm.get('targettypename'),
                    relationDirection: direction,
                    theDomain: domain
                }
            },
            listeners: {
                popupclose: function () {
                    popup.removeAll(true);
                    popup.close();
                }
            },
            onSaveSuccess: function () {
                vm.get('records').load();
                if (vm.get('allRelations').isLoaded()) {
                    vm.get('allRelations').load({
                        callback: function () {
                            try {
                                view.down('relations-fieldset-grid').reconfigure();
                            } catch (error) {
                                // do nothing
                            }
                        }
                    });
                }
            }
        };

        popup = CMDBuildUI.util.Utilities.openPopup('popup-add-relation', title, config, null);
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Ext.data.Model[]} records
     */
    onStoreLoad: function (store, records) {
        const vm = this.getViewModel();
        const allRelations = vm.get('allRelations');
        const recordsToAdd = [];

        function setAttributesRelation(rec, relation) {
            Ext.Object.each(relation.get('_relationAttributes'), function (key, value, allobjects) {
                rec.set('_relAttr_' + key, relation.get(key));
            });
        }

        Ext.Array.forEach(records, function (record) {
            const relationsSameRecord = allRelations.queryBy(function (item) {
                return item.get('_destinationId') == record.getId() && item.get('_type') == vm.get('domain').getId();
            });

            Ext.Array.forEach(relationsSameRecord.getRange(), function (relation, index) {
                const relationAttributes = relation.get('_relationAttributes');

                if (!Ext.Object.isEmpty(relationAttributes)) {
                    if (!index) {
                        setAttributesRelation(record, relation);
                    } else {
                        const recordClone = record.clone();
                        recordClone.setId(record.getId() + '_' + relation.getId());
                        setAttributesRelation(recordClone, relation);
                        recordsToAdd.push(recordClone);
                    }
                }
            });
        });

        if (recordsToAdd.length) {
            store.add(recordsToAdd);
        }
        vm.set('recordscount', store.getCount());

        if (store.loadCount === 0 || store.loadCount === 1) {
            vm.set('counters.stores', vm.get('counters.stores') + 1);
        }
    },

    privates: {
        /**
         *
         * @param {Ext.form.Panel} view
         */
        onItemViewCardActionClick: function (view) {
            const vm = view.lookupViewModel();
            this.redirectToItem(vm.get('targettype'), vm.get('objectTypeName'), vm.get('objectId'));

            if (vm.get('isInBimPopup')) {
                CMDBuildUI.util.Utilities.closePopup('bimPopup');
            }
        },

        /**
         *
         * @param {Ext.form.Panel} view
         */
        onItemEditCardActionClick: function (view) {
            const vm = view.lookupViewModel();
            this.openEditCardPopup(view.up('grid'), {
                objectTypeName: vm.get('objectTypeName'),
                objectId: vm.get('objectId'),
                title: CMDBuildUI.locales.Locales.relations.editcard,
                masterDetailType: vm.get('domain').getId()
            });
        },

        /**
         *
         * @param {Ext.form.Panel} view
         */
        onItemEditRelationCardClick: function (view) {
            const vm = view.lookupViewModel();
            this.loadRelationStore(vm.get('allRelations')).then(function (store) {
                const relationItem = store.findRecord('_destinationId', vm.get('theObject').getId());
                CMDBuildUI.view.relations.Utils.editRelation(relationItem, {
                    theObject: view.up('grid').lookupViewModel().get('theObject'),
                    proxyurl: vm.get('relstore.proxyurl'),
                    objecttypename: vm.get('current.objectTypeName'),
                    objectid: vm.get('current.objectId')
                }).then(function () {
                    vm.get('records').load();
                    Ext.GlobalEvents.fireEventArgs('updateMasterDetailStore', [relationItem.get('_type')]);
                    Ext.GlobalEvents.fireEvent('updateRelationStore');
                });
            });
        },

        /**
         *
         * @param {Ext.form.Panel} view
         */
        onItemDeleteRelationCardClick: function (view) {
            const vm = view.lookupViewModel();
            this.loadRelationStore(vm.get('allRelations')).then(function (store) {
                const relationItem = store.findRecord('_destinationId', vm.get('theObject').getId());
                CMDBuildUI.view.relations.Utils.deleteRelation(relationItem).then(function () {
                    vm.get('records').load();
                    Ext.GlobalEvents.fireEventArgs('updateMasterDetailStore', [relationItem.get('_type')]);
                    Ext.GlobalEvents.fireEvent('updateRelationStore');
                });
            });
        },

        /**
         *
         * @param {String} targettype
         * @param {String} targettypename
         * @param {Number} objectid
         */
        redirectToItem: function (targettype, targettypename, objectid) {
            var path;
            switch (targettype) {
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                    path = Ext.String.format('classes/{0}/cards/{1}/view', targettypename, objectid);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                    path = Ext.String.format('processes/{0}/instances/{1}', targettypename, objectid);
                    break;
            }
            this.redirectTo(path);
        },

        /**
         *
         * @param {Ext.form.Panel} view
         */
        loadRelationStore: function (relationStore) {
            const deferred = new Ext.Deferred();
            if (!relationStore.isLoaded() || Ext.Object.isEmpty(relationStore.getAdvancedFilter().getAttributes())) {
                relationStore
                    .getAdvancedFilter()
                    .addAttributeFilter('_type', 'equal', this.getViewModel().get('domain.name'));
                relationStore.load(function () {
                    deferred.resolve(relationStore);
                });
            } else {
                deferred.resolve(relationStore);
            }
            return deferred.promise;
        }
    }
});
