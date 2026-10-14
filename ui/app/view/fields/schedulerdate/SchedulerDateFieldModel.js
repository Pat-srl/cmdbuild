Ext.define('CMDBuildUI.view.fields.schedulerdate.SchedulerDateFieldModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.fields-schedulerdate-schedulerdatefield',

    data: {
        initialized: false,
        storesloaded: false,
        objectTypeName: null
    },

    formulas: {
        //This bind handles the visibility of the scheduler trigger
        triggerVisibility: {
            bind: '{triggerStore}', //Contains all the triggers for that class and not for the single attribute
            get: function (triggerStore) {
                if (triggerStore) {
                    const triggersData = triggerStore.getRange(),
                        view = this.getView();

                    for (var i = 0; i < triggersData.length; i++) {
                        const triggerData = triggersData[i];
                        if (triggerData.get('active') && triggerData.get('ownerAttr') == view.getName() && triggerData.get('sequenceParamsEditMode') !== 'hidden') {
                            const trigger = view.getTrigger('scheduler');
                            trigger.setHidden(false);
                            return;
                        }
                    }
                    return true;
                }
            }
        },

        triggerCondition: {
            bind: '{triggerStore}',
            get: function (triggerStore) {
                if (triggerStore) {
                    const triggersData = triggerStore.getRange(),
                        view = this.getView();

                    triggersData.forEach(function (trigger) {
                        var conditionScript = null;
                        if (trigger.get('active') && trigger.get('ownerAttr') == view.getName())
                            conditionScript = trigger.get('conditionScript');
                        if (!Ext.isEmpty(conditionScript)) {
                            view.addConditionalTriggerVisibilityRules(trigger);
                        }
                    }, this);

                    if (view._enumConditions == 0) {
                        //if the component doesn't have conditions on fields is considered initialized otherwise is initialized after the first event fired SchedulerDateField.js#140
                        this.set("initialized", true);
                    }
                }
            }
        },

        manageTheObjectData: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject) {
                    const model = CMDBuildUI.util.helper.ModelHelper.getModelFromName(theObject.$className);
                    this.set('objectTypeName', model.objectTypeName);

                    const sequence = theObject.sequences();
                    sequence.setFilters[{
                        property: 'trigger',
                        operator: '!=',
                        value: 0
                    }];
                }
            }
        },

        triggerStoreData: {
            get: function () {
                return this.getView().metadata.calendarTriggers;
            }
        }

    },

    stores: {
        triggerStore: {
            model: 'CMDBuildUI.model.calendar.Trigger',
            proxy: {
                type: 'memory'
            },
            data: '{triggerStoreData}'
        }
    }

});
