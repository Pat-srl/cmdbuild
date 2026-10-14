(function () {
    var statics = {
        // base
        startingpage: 'cm_ui_startingClass',
        startingpage_actual: '_cm_ui_startingClass_actual',
        language: 'cm_user_language',
        processWidgetAlwaysEnabled: 'cm_ui_processWidgetAlwaysEnabled',
        // numeric fields
        thousandsSeparator: 'cm_ui_thousandsSeparator',
        decimalsSeparator: 'cm_ui_decimalsSeparator',
        // dates
        dateFormat: 'cm_ui_dateFormat',
        timeFormat: 'cm_ui_timeFormat',
        timezone: 'cm_ui_timezone',
        startDay: 'cm_ui_startDay',
        // office suite
        preferredOfficeSuite: 'cm_ui_preferredOfficeSuite',
        // grids
        gridsconfig: 'cm_ui_gridsconfig',
        // charset
        preferredfilecharset: 'cm_ui_preferredFileCharset',
        // csv separator
        preferredCsvSeparator: 'cm_ui_preferredCsvSeparator',
        // notifications
        notifications: {
            soundVolume: 'cm_ui_notifications_sound_volume',
            soundEnabled: 'cm_ui_notifications_sound_enabled',
            groupEmailByStatus: 'cm_ui_email_groupByStatus',
            defaultEmailDelay: 'cm_ui_email_defaultDelay'
        },
        icon: 'icon',
        favouritesmenu: 'cm_ui_preferredMenu',
        // map
        preferredMapLayer: 'cm_ui_preferredMapLayer',
        preferredMapLabelSize: 'cm_ui_preferredMapLabelSize'
    };

    Ext.define('CMDBuildUI.model.users.Preference', {
        extend: 'Ext.data.Model',

        statics: statics,

        fields: [
            { name: statics.startingpage, type: 'string' },
            { name: statics.processWidgetAlwaysEnabled, type: 'string' },
            { name: statics.thousandsSeparator, type: 'string', defaultValue: null },
            { name: statics.decimalsSeparator, type: 'string', defaultValue: null },
            { name: statics.dateFormat, type: 'string', defaultValue: null },
            { name: statics.timeFormat, type: 'string', defaultValue: null },
            { name: statics.startDay, type: 'string', defaultValue: null },
            { name: statics.timezone, type: 'string', defaultValue: null },
            { name: statics.preferredCsvSeparator, type: 'string' },
            { name: statics.preferredfilecharset, type: 'string' },
            { name: statics.preferredOfficeSuite, type: 'string' },
            { name: statics.gridsconfig, defaultValue: {} },
            { name: statics.notifications.soundEnabled, type: 'boolean', allowNull: true },
            { name: statics.icon, type: 'string' },
            { name: statics.notifications.groupEmailByStatus, type: 'boolean', allowNull: true },
            { name: statics.notifications.defaultEmailDelay, type: 'string', defaultValue: null }
        ],

        proxy: { url: '/sessions/current/', type: 'baseproxy' }
    });
})();
