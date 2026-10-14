Ext.define('CMDBuildUI.view.relations.fieldset.FieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-fieldset',

    data: {
        readonly: true,
        basetitle: null,
        isprocess: false,
        domain: null,
        direction: null,
        current: {
            objectType: null,
            objectTypeName: null,
            objectId: null
        },
        targetmodel: null,
        targetitem: null,
        storeinfo: {
            autoLoad: false,
            proxyurl: null
        },
        recordscount: 0,
        objectModel: null,
        addrelationbtn: {
            disabled: true,
            hidden: true
        },
        referenceDomains: [],
        editableDomains: {}
    },

    formulas: {
        updatedata: {
            bind: { domain: '{domain}', direction: '{direction}', current: '{current}' },
            get: function (data) {
                if (data.domain) {
                    var destinationtype, destinationtypename, sourcetypename;
                    if (data.direction === '_1') {
                        this.set('basetitle', data.domain.get('_descriptionInverse_translation'));
                        destinationtype = data.domain.get('sourceProcess')
                            ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                            : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                        destinationtypename = data.domain.get('source');
                        sourcetypename = data.domain.get('destination');
                    } else if (data.direction === '_2') {
                        this.set('basetitle', data.domain.get('_descriptionDirect_translation'));
                        destinationtype = data.domain.get('destinationProcess')
                            ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                            : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                        destinationtypename = data.domain.get('destination');
                        sourcetypename = data.domain.get('source');
                    }

                    // load model
                    CMDBuildUI.util.helper.ModelHelper.getModel(destinationtype, destinationtypename).then(
                        function (model) {
                            this.set('targettype', destinationtype);
                            this.set('targettypename', destinationtypename);
                            this.set('targetmodel', model);
                            // set model name
                            this.set('storeinfo.model', model.getName());

                            //set store proxy name
                            this.set('storeinfo.proxyurl', model.getProxy().getUrl());

                            // set store type and proxy
                            var storetype = 'classes-cards';
                            if (destinationtype === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                                storetype = 'processes-instances';
                                this.set('isprocess', true);
                            }
                            this.set('storeinfo.type', storetype);

                            // set advanced filter
                            this.set('storeinfo.advancedfilter', {
                                relation: [
                                    {
                                        domain: data.domain.getId(),
                                        type: 'oneof',
                                        destination: destinationtypename,
                                        source: sourcetypename,
                                        direction: data.direction,
                                        cards: [{ className: data.current.objectTypeName, id: data.current.objectId }]
                                    }
                                ],
                                ecql: data.domain.get('ecqlFilterMasterDetail')
                            });

                            const item = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                    destinationtypename,
                                    destinationtype
                                ),
                                sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(item, true);

                            this.set('targetitem', item);
                            this.set('storeinfo.sorters', sorters);

                            // add button
                            this.set('addrelationbtn.disabled', false);
                        },
                        Ext.emptyFn,
                        Ext.emptyFn,
                        this
                    );
                }
            }
        },
        title: {
            bind: {
                basetitle: '{basetitle}',
                count: '{recordscount}'
            },
            get: function (data) {
                var title = data.basetitle;
                if (data.count !== undefined) {
                    title += ' (' + data.count + ')';
                }
                return title;
            }
        },

        // Check if source or target is editable
        sourceOrTargetEditable: {
            bind: {
                domain: '{domain}',
                type: '{theObject._type}'
            },
            get: function (data) {
                const domain = data.domain;
                return (
                    (domain.get('sources').includes(data.type) && domain.get('sourceEditable')) ||
                    (domain.get('destinations').includes(data.type) && domain.get('targetEditable'))
                );
            }
        }
    },

    stores: {
        records: {
            model: '{storeinfo.model}',
            proxy: {
                type: 'baseproxy',
                url: '{storeinfo.proxyurl}',
                extraParams: {
                    include_tasklist: true
                }
            },
            listeners: {
                load: 'onStoreLoad'
            },
            pageSize: 0,
            autoLoad: false,
            autoDestroy: true,
            advancedFilter: '{storeinfo.advancedfilter}',
            sorters: '{storeinfo.sorters}'
        },
        allRelations: {
            model: 'CMDBuildUI.model.domains.Relation',
            proxy: {
                type: 'baseproxy',
                url: '{relstore.proxyurl}',
                extraParams: {
                    detailed: true
                }
            },
            remoteSort: false,
            pageSize: 0 // disable pagination
        }
    }
});
