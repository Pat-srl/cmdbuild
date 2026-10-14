Ext.define('CMDBuildUI.model.calendar.Trigger', {
    requires: ['CMDBuildUI.util.administration.helper.ApiHelper'],
    extend: 'CMDBuildUI.model.base.Base',

    statics: {
        // ui settings for dynamic generation used in grid and grid helper
        objectTypeName: 'schedule',
        pluralObjectTypeName: 'schedules-ruledefinitions',
        isBuffered: true,
        viewInRowWidgetType: 'forminrowwidget',
        vmObjectName: 'theSchedule',
        vmStore: 'allSchedules',
        getAlias: function (viewType) {
            return Ext.String.format('administration-content-{0}-{1}', CMDBuildUI.model.calendar.Trigger.pluralObjectTypeName, viewType);
        },

        // end ui settings
        calendarClassName: 'event',
        // fixed lookup provided by server
        calendarFrequencies: {
            once: 'once',
            daily: 'daily',
            weekly: 'weekly',
            monthly: 'monthly',
            yearly: 'yearly'
        },
        calendarEndTypes: {
            never: 'never',
            date: 'date',
            number: 'number',
            other: 'other' ////!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        },
        eventtypes: {
            instant: 'instant',
            date: 'date'
        },
        getEventTypes: function () {
            return [{
                'value': CMDBuildUI.model.calendar.Trigger.eventtypes.instant,
                'label': CMDBuildUI.locales.Locales.administration.schedules.instant
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.eventtypes.date,
                'label': CMDBuildUI.locales.Locales.administration.schedules.date
            }];
        },
        delays: {
            Y: 'Y',
            M: 'M',
            D: 'D'
        },
        getDelays: function () {
            return [{
                'value': CMDBuildUI.model.calendar.Trigger.delays.Y,
                'label': CMDBuildUI.locales.Locales.administration.schedules.years
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.delays.M,
                'label': CMDBuildUI.locales.Locales.administration.schedules.months
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.delays.D,
                'label': CMDBuildUI.locales.Locales.administration.schedules.days
            }];
        },
        sequenceParamsEditModes: {
            hidden: 'hidden',
            read: 'read',
            write: 'write'
        },
        getSequenceParamsEditModes: function () {
            return [{
                'value': CMDBuildUI.model.calendar.Trigger.sequenceParamsEditModes.hidden,
                'label': CMDBuildUI.locales.Locales.administration.schedules.hidden
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.sequenceParamsEditModes.read,
                'label': CMDBuildUI.locales.Locales.administration.schedules.read
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.sequenceParamsEditModes.write,
                'label': CMDBuildUI.locales.Locales.administration.schedules.write
            }];
        },
        eventEditModes: {
            hidden: 'hidden',
            read: 'read',
            write: 'write'
        },
        getEventEditModes: function () {
            return CMDBuildUI.model.calendar.Trigger.getSequenceParamsEditModes();
        },
        onCardDeleteActions: {
            clear: 'clear',
            delete: 'delete'
        },
        getOnCardDeleteActions: function () {
            return [{
                'value': CMDBuildUI.model.calendar.Trigger.onCardDeleteActions.delete,
                'label': CMDBuildUI.locales.Locales.administration.schedules.deleteschedules
            }, {
                'value': CMDBuildUI.model.calendar.Trigger.onCardDeleteActions.clear,
                'label': CMDBuildUI.locales.Locales.administration.schedules.keepschedules
            }];
        }
    },

    fields: [{
            name: 'code',
            description: CMDBuildUI.locales.Locales.administration.common.labels.code,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.code'
            },
            type: 'string',
            critical: true,
            showInGrid: true
        }, {
            name: 'description',
            description: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            type: 'string',
            critical: true,
            showInGrid: true
        }, {
            name: 'content',
            type: 'string',
            critical: true
        }, {
            name: 'ownerClass',
            description: CMDBuildUI.locales.Locales.administration.localizations.class,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.localizations.class'
            },
            type: 'string',
            critical: true
        }, {
            name: '_ownerClass_description',
            description: CMDBuildUI.locales.Locales.administration.localizations.class,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.localizations.class'
            },
            type: 'string',
            showInGrid: true
        }, {
            name: 'ownerAttr',
            description: CMDBuildUI.locales.Locales.administration.common.strings.attribute,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.strings.attribute'
            },
            type: 'string',
            critical: true
        }, {
            name: '_ownerAttr_description',
            description: CMDBuildUI.locales.Locales.administration.common.strings.attribute,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.strings.attribute'
            },
            type: 'string',
            showInGrid: true
        }, {
            name: '_comboDescription',
            type: 'string',
            calculate: function (data) {
                return Ext.String.format('{0} [{1}-{2}]', data.description, data.ownerClass, data.ownerAttr);
            }
        }, {
            name: 'active',
            description: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                description: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            align: 'center',
            type: 'boolean',
            critical: true,
            defaultValue: true,
            showInGrid: true
        }, {
            name: 'category',
            type: 'string',
            defaultValue: 'default',
            critical: true
        }, {
            name: 'priority',
            type: 'string',
            defaultValue: 'default',
            critical: true
        }, {
            name: 'conditionScript',
            type: 'string',
            critical: true
        }, {

            /**
             * https://en.wikipedia.org/wiki/ISO_8601
             * 
             * P is the duration designator (for period) placed at the start of the duration representation.
             *  Y is the year designator critical: true,that follows the value for the number of years.
             *  M is the month designator that follows the value for the number of months.
             *  W is the week designator that follows the value for the number of weeks.
             *  D is the day designator that follows the value for the number of days.
             * T is the time designator that precedes the time components of the representation.
             *  H is the hour designator that follows the value for the number of hours.
             *  M is the minute designator that follows the value for the number of minutes.
             *  S is the second designator that follows the value for the number of seconds.
             * 
             * To resolve ambiguity, "P1M" is a one-month duration and "PT1M" is a 
             * one-minute duration (note the time designator, T, that precedes the time value).
             * The smallest value used may also have a decimal fraction,
             * as in "P0.5Y" to indicate half a year. This decimal fraction may be
             * specified with either a comma or a full stop, as in "P0,5Y" or "P0.5Y".
             * The standard does not prohibit date and time values in a duration
             * representation from exceeding their "carry over points" except as noted below. 
             * Thus, "PT36H" could be used as well as "P1DT12H" for representing 
             * the same duration. But keep in mind that "PT36H" is not the same as 
             * "P1DT12H" when switching from or to
             */
            name: 'delay', // iso interval
            type: 'string',
            defaultValue: 'P0D',
            critical: true
        }, {
            name: 'eventCount',
            type: 'number',
            critical: true,
            defaultValue: 1
        }, {
            name: 'eventEditMode', // read|write
            type: 'string',
            defaultValue: 'write',
            critical: true,
            persist: true
        }, {
            name: 'eventTime', // iso time
            type: 'string',
            defaultValue: '00:00:00',
            critical: true
        }, {
            name: 'frequency',
            defaultValue: 'once',
            type: 'string',
            critical: true
        }, {
            name: 'frequencyMultiplier',
            defaultValue: 1,
            type: 'number',
            critical: true
        }, {
            name: 'maxActiveEvents', // max events to be generated at any time
            type: 'number',
            defaultValue: 1,
            critical: true
        }, {
            name: 'notifications___0___delay',
            type: 'number',
            defaultValue: 86400,
            critical: true
        }, {
            name: 'notifications',
            critical: false,
            persist: false,
            type: 'auto',
            convert: function (value, record) {
                if (!value) return [];
                return value;
            },
            serialize: function (value, record) {
                var notifications = [];
                record.notifications().getRange().forEach(function (event) {
                    notifications.push(event.getData());
                });
                return notifications;
            }
        },
        {
            name: 'participants', // array of participants (users or groups, like 'user.MyUser' or 'group.MyGroup')
            type: 'auto',
            defaultValue: [],
            critical: true
        },
        {
            name: 'userId',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'groupId',
            type: 'string',
            defaultValue: null
        },
        {
            name: 'onCardDeleteAction',
            type: 'string',
            defaultValue: 'delete', // one of clear, delete
            critical: true
        },
        {
            name: 'sequenceParamsEditMode',
            type: 'string',
            defaultValue: 'write', // one of hidden, read, write
            critical: true
        },
        {
            name: 'showGeneratedEventsPreview',
            type: 'boolean',
            defaultValue: false,
            critical: true
        },
        {
            name: 'timeZone', // if null, will default to system time zone
            type: 'string',
            defaultValue: null,
            critical: true
        },
        {
            name: 'eventType',
            type: 'string',
            defaultValue: 'instant', // one of instant, date
            critical: true
        },
        {
            name: 'endType',
            type: 'string',
            defaultValue: 'never', //'other', // one of never, date, number, other (other is used for example when frequency = once, or meaning 'auto')
            critical: true
        },
        {
            name: 'lastEvent',
            critical: true,
            type: 'date',
            dateWriteFormat: 'C'
        },
        {
            name: 'notifications___0___template', // this field is populated only if notifications contain 1 record
            critical: true,
            type: 'string',
            serialize: function (value) {
                return Ext.isEmpty(value) ? undefined : value;
            }
        },
        {
            name: 'notifications___0___reports___0___code', // this field is populated only if notifications contain 1 record
            critical: true,
            type: 'string',
            serialize: function (value) {
                return Ext.isEmpty(value) ? undefined : value;
            }
        },
        {
            name: 'notifications___0___reports___0___format',
            critical: true,
            type: 'string',
            serialize: function (value) {
                return Ext.isEmpty(value) ? undefined : value;
            }
        },
        // {
        //     name: '_notification__report_params',
        //     critical: true,
        //     type: 'string',
        //     calculate: function (data) {
        //         var params = {};
        //         Ext.Array.forEach(Ext.Object.getAllKeys(data), function (key) {
        //             if (Ext.String.startsWith(key, 'notifications___0___reports___0___params_')) {
        //                 var param = key.replace('notifications___0___reports___0___params_', '');
        //                 params[param] = data[key];
        //             }
        //         });
        //     }
        // },
        {
            name: 'scope',
            critical: true,
            type: 'string',
            defaultValue: 'interactive_only',
            serialize: function (value, record) {
                if (record.get('createAlsoViaWS')) {
                    return 'always';
                }
                return 'interactive_only';
            }
        },
        {
            name: 'createAlsoViaWS',
            type: 'boolean',
            calculate: function (data) {
                return data.scope === 'always';
            }

        }
    ],
    convertOnSet: false,
    hasMany: [{
        model: 'CMDBuildUI.model.calendar.Notification',
        name: 'notifications'
    }],

    proxy: {
        url: CMDBuildUI.util.administration.helper.ApiHelper.server.getSchedulesTriggerUrl(),
        type: 'baseproxy',
        reader: {
            type: 'json'
        },
        writer: {
            writeAllFields: true
        }
    },

    parseIsoInterval: function (interval) {
        var intervalRegex = /P(?:(-))?(?:([.,\d]+)Y)?(?:([.,\d]+)M)?(?:([.,\d]+)W)?(?:([.,\d]+)D)?(?:T(?:([.,\d]+)H)?(?:([.,\d]+)M)?(?:([.,\d]+)S)?)?/;
        var matches = this.get('delay').match(intervalRegex);
        return {
            // not managed by server
            // only positive value can be used
            sign: matches[1] === undefined ? '' : '-',
            Y: matches[2] === undefined ? 0 : matches[2],
            M: matches[3] === undefined ? 0 : matches[3],
            // not managed by server
            //W: matches[4] === undefined ? 0 : matches[4],
            D: matches[5] === undefined ? 0 : matches[5]
            // not managed by server
            // for times period we use time field
            // hours: matches[6] === undefined ? 0 : matches[6],
            // minutes: matches[7] === undefined ? 0 : matches[7],
            // seconds: matches[8] === undefined ? 0 : matches[8]
        };

    },

    clone: function () {
        var item = this.copy();
        item.set('_id', undefined);
        item.set('description', Ext.String.format('{0}_clone', this.get('description')));
        item.crudState = "C";
        item.phantom = true;
        delete item.crudStateWas;
        delete item.previousValues;
        delete item.modified;
        return item;
    }

});