Ext.define('CMDBuildUI.view.relations.list.ContainerModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.relations-list-container',

    data: {
        readonly: false,
        readOnlyAllowCardEdit: false,
        hiddenbtns: {
            relgraph: true,
            addbtn: true
        },
        addbtn: {
            disabled: true
        },
        storedata: {},
        counters: {
            stores: 0
        },
        editableDomains: {},
        referenceDomains: []
    },

    formulas: {
        updateData: {
            get: function () {
                var hidden = {
                    relgraph: !CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.relgraph.enabled
                    ),
                    addbtn: false
                };

                // hide buttons in read-only mode
                var view = this.getView();
                if (view.getReadOnly()) {
                    this.set('readonly', true);
                    hidden.addbtn = true;

                    // hide relation graph button
                    if (!view.getShowRelGraphBtn()) {
                        hidden.relgraph = true;
                    }

                    // set card edit btn permission
                    this.set('readOnlyAllowCardEdit', view.getShowEditCardBtn());
                }
                this.set('hiddenbtns', hidden);
            }
        },

        updateStoreData: {
            bind: {
                objectId: '{objectId}',
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            get: function (data) {
                if (data.objectTypeName && data.objectId) {
                    var url;
                    switch (data.objectType) {
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                            url = CMDBuildUI.util.api.Classes.getCardRelations(data.objectTypeName, data.objectId);
                            break;
                        case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                            url = CMDBuildUI.util.api.Processes.getProcessInstanceRelations(
                                data.objectTypeName,
                                data.objectId
                            );
                            break;
                    }
                    this.set('storedata.proxyurl', url);
                }
            }
        }
    },

    stores: {
        allRelations: {
            model: 'CMDBuildUI.model.domains.Relation',
            proxy: {
                type: 'baseproxy',
                url: '{storedata.proxyurl}',
                extraParams: {
                    detailed: true
                }
            },
            sorters: [
                {
                    property: '_direction',
                    direction: 'ASC'
                },
                {
                    property: '_destinationType',
                    direction: 'ASC'
                }
            ],
            grouper: {
                groupFn: function (item) {
                    return Ext.String.format('{0}_{1}', item.get('_type'), item.get('_direction'));
                }
            },
            remoteSort: false, // autoLoad is ignore if group is set and remoteSort is true so enbla remoteSort after load. See EXTJS-19781.
            autoLoad: false,
            autoDestroy: true,
            pageSize: 0 // disable pagination
        }
    }
});
