Ext.define('CMDBuildUI.view.dms.TabPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-tabpanel',

    data: {
        DMSModelClassName: undefined,
        DMSModelClass: undefined,
        DMSClass: undefined,
        isRecordPhantom: undefined,
        schedulesHidden: true,
        schedulesHeight: undefined,
        eventsStore: undefined
    },

    formulas: {

        DMSModelCalculation: {
            bind: {
                DMSCategoryType: '{DMSCategoryType}',
                DMSCategoryValue: '{record.category}'
            },
            get: function (data) {
                if (data.DMSCategoryType && data.DMSCategoryValue) {
                    const categoryValues = data.DMSCategoryType.values(),
                        r = categoryValues.findRecord('_id', data.DMSCategoryValue);

                    if (r) {
                        const DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                        this.set("DMSModelClassName", DMSModelClassName);

                        const DMSClass = CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName);
                        this.set("DMSClass", DMSClass);

                        CMDBuildUI.util.helper.ModelHelper.getModel(
                            CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                            DMSModelClassName
                        ).then(function (model) {
                            if (this.getView()) {
                                this.set("DMSModelClass", model);
                            }
                        }, Ext.emptyFn, Ext.emptyFn, this);
                    }
                }
            }
        },

        isRecordPhantom: {
            bind: {
                record: '{record}'
            },
            get: function (data) {
                return data.record && data.record.phantom;
            }
        },

        schedulesHidden: {
            bind: '{DMSClass._hasTriggers}',
            get: function (hasTriggers) {
                const ignoreSchedules = this.getView().up('dms-container').getIgnoreSchedules();
                return !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.scheduler.enabled) || !hasTriggers || ignoreSchedules;
            }
        },

        schedulesHeight: {
            bind: {
                hidden: '{schedulesHidden}'
            },
            get: function (data) {
                if (!data.hidden) {
                    return this.getView().up('dms-container').getHeight();
                }
            }
        },

        eventsStore: {
            bind: {
                dmsModelAttachmentId: '{record._card}',
                schedulesHidden: '{schedulesHidden}'
            },
            get: function (data) {
                var store;
                if (data.dmsModelAttachmentId && !data.schedulesHidden) {
                    store = Ext.create('Ext.data.BufferedStore', {
                        model: 'CMDBuildUI.model.calendar.Event',
                        pageSize: 50,
                        leadingBufferZone: 100,
                        sorters: [{
                            property: 'date',
                            direction: "ASC"
                        }],
                        proxy: {
                            type: 'baseproxy',
                            url: '/calendar/events',
                            extraParams: {
                                // detailed: true
                            }
                        }
                    });
                    const filter = store.getAdvancedFilter();
                    filter.addAttributeFilter(
                        'card',
                        CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                        data.dmsModelAttachmentId
                    );
                    store.load();
                }
                return store;
            }
        }

    }
});
