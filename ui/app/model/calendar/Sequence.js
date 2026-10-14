Ext.define('CMDBuildUI.model.calendar.Sequence', {
    extend: 'CMDBuildUI.model.base.Base',
    requires: ['CMDBuildUI.locales.Locales'],

    statics: {
        sequenceParamsEditMode: {
            read: 'read',
            write: 'write',
            hidden: 'hidden'
        },
        source: {
            system: 'system',
            user: 'user'
        },

        getFormLayout: function () {
            return {
                _nogroup: {
                    rows: [{//row 1
                        columns: [{
                            fields: [{
                                attribute: 'category'
                            }, {
                                attribute: 'firstEvent'
                            }]
                        }, {
                            fields: [{
                                attribute: 'priority'
                            }]
                        }]
                    }, {//row2
                        columns: [{
                            fields: [{
                                attribute: 'frequency'
                            }]
                        }, {
                            fields: [{
                                attribute: 'frequencyMultiplier'
                            }]
                        }]
                    }, {//row3
                        columns: [{
                            fields: [{
                                attribute: 'endType'
                            }]
                        }, {
                            fields: [{
                                attribute: 'lastEvent'
                            }, {
                                attribute: 'eventCount'
                            }]
                        }]
                    }, {//row3
                        columns: [{
                            fields: [{
                                attribute: 'daysAdvanceNotification'
                            }]
                        }, {
                            fields: [{
                                attribute: 'maxActiveEvents'
                            }]
                        }]
                    }]
                }
            }
        }
    },

    fields: [{
        name: '_id',
        type: 'string',
        persist: false
        // critical: true // This field is allways sent to server even if it has hot changed
    }, {
        name: 'source', // one of system,user; trigger stuff has always system
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'source',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'card',
        critical: true,
        type: 'integer',
        attributeconf: {
            group: ''
        },
        description: 'card',
        hidden: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'owner', // trigger stuff has always null
        critical: true,
        type: 'auto',
        attributeconf: {
            group: ''
        },
        description: 'owner',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'category',
        critical: true,
        type: 'string',
        attributeconf: {
            group: '',
            lookupType: 'CalendarCategory',
            lookupIdField: 'code',
            validationRules: 'api.bind=["category"]; return !Ext.isEmpty(api.getValue("category"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.category,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.category'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'lookup'
    }, {
        name: 'priority',
        critical: true,
        type: 'string',
        attributeconf: {
            group: '',
            lookupType: 'CalendarPriority',
            lookupIdField: 'code',
            validationRules: 'api.bind=["priority"]; return !Ext.isEmpty(api.getValue("priority"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.priority,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.priority'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'lookup'
    }, {
        name: 'firstEvent', // iso date
        critical: true,
        type: 'date',
        dateWriteFormat: 'c',
        attributeconf: {
            group: '',
            calendarTriggers: [],
            validationRules: 'api.bind=["firstEvent"]; return !Ext.isEmpty(api.getValue("firstEvent"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.startdate,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.startdate'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'date'
    }, {
        name: 'frequency',
        critical: true,
        type: 'string',
        attributeconf: {
            group: '',
            lookupType: 'CalendarFrequency',
            lookupIdField: 'code',
            validationRules: 'api.bind=["frequency"]; return !Ext.isEmpty(api.getValue("frequency"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.frequency,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.frequency'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'lookup'
    }, {
        name: 'frequencyMultiplier',
        critical: true,
        type: 'integer',
        attributeconf: {
            group: '',
            showIf: 'return (api.getValue("frequency") && api.getValue("frequency") != CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once)',
            validationRules: 'api.bind=["frequencyMultiplier"]; return !Ext.isEmpty(api.getValue("frequencyMultiplier"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.frequencymultiplier,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.frequencymultiplier'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'endType',
        critical: true,
        type: 'auto',
        attributeconf: {
            group: '',
            lookupType: 'CalendarEndType',
            lookupIdField: 'code',
            showIf: 'return (api.getValue("frequency") && api.getValue("frequency") != CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once )',
            validationRules: 'api.bind=["endType"]; return !Ext.isEmpty(api.getValue("endType"));'
            // autoValue: 'api.bind = ["frequency"] ; debugger; var frequencyValue = api.getValue("frequency"); if (frequencyValue == CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once) {api.record.set("endType",CMDBuildUI.model.calendar.Trigger.calendarEndTypes.other); console.log("EndType setted");}'
        },
        description: CMDBuildUI.locales.Locales.calendar.endtype,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.endtype'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'lookup'
    }, {
        name: 'lastEvent', // iso date
        critical: true,
        type: 'date',
        dateWriteFormat: 'c',
        attributeconf: {
            group: '',
            calendarTriggers: [],
            showIf: 'return (api.getValue("frequency") &&  api.getValue("frequency") != CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once && api.getValue("endType") == CMDBuildUI.model.calendar.Trigger.calendarEndTypes.date)',
            validationRules: 'api.bind=["lastEvent"]; return !Ext.isEmpty(api.getValue("lastEvent"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.enddate,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.enddate'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'date'
    }, {
        name: 'eventCount',
        critical: true,
        type: 'auto',
        attributeconf: {
            group: '',
            showIf: 'return (api.getValue("frequency") && api.getValue("frequency") != CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once && api.getValue("endType") == CMDBuildUI.model.calendar.Trigger.calendarEndTypes.number)',
            validationRules: 'api.bind=["eventCount"]; return !Ext.isEmpty(api.getValue("eventCount"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.occurencies,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.occurencies'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'maxActiveEvents',
        critical: true,
        type: 'auto',
        attributeconf: {
            group: '',
            showIf: 'return (api.getValue("frequency") && api.getValue("frequency") != CMDBuildUI.model.calendar.Trigger.calendarFrequencies.once && !Ext.isEmpty(api.getValue("endType")))',
            validationRules: 'api.bind=["maxActiveEvents"]; return !Ext.isEmpty(api.getValue("maxActiveEvents"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.maxactiveevents,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.maxactiveevents'
        },
        hidden: false,
        writable: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'daysAdvanceNotification',
        type: 'number',
        attributeconf: {
            group: '',
            showThousandsSeparator: false,
            autoValue: 'api.bind = ["daysAdvanceNotification"] ; var value = -api.record.get("daysAdvanceNotification") * 60* 60* 24; api.record.set("notifications___0___delay", value)',
            validationRules: 'api.bind=["daysAdvanceNotification"]; return !Ext.isEmpty(api.getValue("daysAdvanceNotification"));'
        },
        description: CMDBuildUI.locales.Locales.calendar.advancenotification,
        localized: {
            description: 'CMDBuildUI.locales.Locales.calendar.advancenotification'
        },
        hidden: false,
        writable: true,
        convert: function (value, record) {
            return Math.abs(record.get('notifications___0___delay') / (60 * 60 * 24));
        },
        depends: ['notifications___0___delay'],
        cmdbuildtype: 'integer',
        persist: false
    }, {
        name: '__firstconvert',
        persist: false
    }, {
        name: 'notifications___0___delay',
        critical: true,
        type: 'number',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'notifications___0___template',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'notifications___0___content',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'string'
    }, /* {
        name: '_notification__report',
        critical: true,
        type: 'auto',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'string'
    }, */ {
        name: '_participant_user_id',
        critical: true,
        type: 'number',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'integer'
    }, {
        name: '_participant_group_id',
        critical: true,
        type: 'number',
        attributeconf: {
            group: ''
        },
        hidden: true,
        cmdbuildtype: 'integer'
    }, {
        name: 'content',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'content',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'description',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'description',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'eventEditMode',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'eventEditMode',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'job',
        critical: true,
        type: 'auto',
        attributeconf: {
            group: ''
        },
        description: 'job',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'notifications',
        persist: false,
        type: 'auto',
        attributeconf: {
            group: ''
        },
        description: 'notifications',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'partecipants',
        persist: false,
        type: 'auto',
        attributeconf: {
            group: ''
        },
        description: 'partecipants',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'onCardDeleteAction', //clear or delete
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'onCardDeleteAction',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'sequenceParamsEditMode', // one of hidden, read, write
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'sequenceParamsEditMod',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'showGeneratedEventsPreview',
        critical: true,
        type: 'boolean',
        attributeconf: {
            group: ''
        },
        description: 'showGeneratedEventsPreview',
        hidden: true,
        cmdbuildtype: 'boolean'
    }, {
        name: 'timeZone', // if null, will default to system time zone
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'timeZone',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'title',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'title',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'eventType', // one of instant, date 
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'eventType',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'eventTime',
        critical: true,
        type: 'string',
        attributeconf: {
            group: ''
        },
        description: 'eventTime',
        hidden: true,
        cmdbuildtype: 'string'
    }, {
        name: 'events',
        critical: true,
        type: 'auto',
        convert: function (value, record) {
            if (!value) return [];
            return value;
        },
        serialize: function (value, record) {
            var events = [];
            record.events().getRange().forEach(function (event) {
                if (Ext.isEmpty(event.get('notifications___0___template'))) {
                    delete event.data.notifications___0___template;
                    delete event.data.notifications___0___delay;
                    delete event.data.notifications___0___content;
                }

                events.push(event.getData());
            });
            return events;
        },
        cmdbuildtype: 'string',
        attributeconf: {
            group: ''
        },
        description: 'events',
        hidden: true
    }, /* {
            name: 'noEndDate',
            critical: true,
            type: 'boolean',
            attributeconf: {
                group: ''
            },
            description: 'noEndDate',
            hidden: false,
            writable: true,
            cmdbuildtype: 'boolean'
        }, */ {
        name: 'trigger',
        critical: true,
        type: 'number',
        attributeconf: {
            group: ''
        },
        description: 'trigger',
        hidden: true,
        cmdbuildtype: 'integer'
    }],

    hasMany: [{
        model: 'CMDBuildUI.model.calendar.Event',
        name: 'events'
    }],
    convertOnSet: false,
    proxy: {
        type: 'baseproxy',
        url: '/calendar/sequences/',
        extraParams: {
            includeEvents: true
        }
    },

    /**
     * 
     * @param {*} options 
     * @returns 
     */
    save: function () {
        if (Ext.isEmpty(this.get('notifications___0___template'))) {
            delete this.data.notifications___0___template;
            delete this.data.notifications___0___delay;
            delete this.data.notifications___0___content;
        }
        return this.callParent(arguments);
    },

    /**
     * 
     * @returns 
     */
    generateEventsfromSequence: function () {
        const deferred = new Ext.Deferred(),
            data = this.getData();

        delete data.notifications;

        if (Ext.isEmpty(this.get('notifications___0___template'))) {
            delete data.notifications___0___template;
            delete data.notifications___0___delay;
            delete data.notifications___0___content;
        }

        Ext.Ajax.request({
            method: 'POST',
            jsonData: data,
            url: Ext.String.format('{0}/calendar/sequences/_ANY/generate-events', CMDBuildUI.util.Config.baseUrl),
            callback: function (options, success, response) {
                response = JSON.parse(response.responseText);
                if (response.success) {
                    const data = response.data;
                    data.forEach(function (item, index, array) {
                        delete item.notifications;
                    });
                    deferred.resolve(data);
                }
            }
        });

        return deferred.promise;
    }

});