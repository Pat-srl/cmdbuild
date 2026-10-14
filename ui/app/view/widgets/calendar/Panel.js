/**
 * @file CMDBuildUI.view.widgets.calendar
 * @module CMDBuildUI.view.widgets.calendar
 * @author PAT srl
 * @access public
 */
Ext.define('CMDBuildUI.view.widgets.calendar.Panel', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.widgets.calendar.PanelController'],

    mixins: ['CMDBuildUI.view.widgets.Mixin'],

    alias: 'widget.widgets-calendar-panel',
    controller: 'widgets-calendar-panel',

    layout: 'fit',

    /**
     * @constant {String} ClassName
     * Class name or process name to get data.
     */
    ClassName: null,

    /**
     * @constant {String} Filter
     * The filter to apply to the list request.
     */
    Filter: null,

    /**
     * @constant {Object} _Filter_ecql
     * eCQL filter definition.
     */
    _Filter_ecql: {},

    /**
     * @constant {String} EventStartDate
     * The field to use as event start date.
     */
    EventStartDate: null,

    /**
     * @constant {String} EventEndDate
     * The field to use as event end date.
     */
    EventEndDate: null,

    /**
     * @constant {String} EventTitle
     * The field to use as event title.
     */
    EventTitle: null,

    /**
     * @constant {String} EventType
     * The field to use as event type.
     */
    EventType: null,

    /**
     * @constant {String} EventTypeLookup
     * The LookUp type to use as event type info.
     */
    EventTypeLookup: null,

    /**
     * @constant {String} DefaultDate
     * The field in current class/process to use as opening date in calendar.
     */
    DefaultDate: null,

    /**
     * @constant {Boolean} Inline
     * If True show the widget inline.
     */
    Inline: false,

    /**
     * @constant {Boolean} Required
     * If True this widget is mandatory.
     */
    Required: false
});
