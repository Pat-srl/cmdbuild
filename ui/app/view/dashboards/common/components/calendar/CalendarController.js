Ext.define('CMDBuildUI.view.dashboards.common.components.calendar.CalendarController', {
    extend: 'CMDBuildUI.view.dashboards.common.components.ComponentController',
    alias: 'controller.dashboards-components-calendar-calendar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * @param {CMDBuildUI.view.dashboards.components.calendar.Calendar} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const component = view.getItemDefinition();
        this.initCalendarConfig(view, component);
    },

    initCalendarConfig: function (view, component) {
        view.add({
            xtype: 'ux-calendar',
            itemId: 'calendar',

            shortTitle: true,
            allowViewDay: false,
            allowViewWeek: false,
            allowPrevNextYear: false,
            eventDatesUseTime: true,
            allowViewMonth: true,
            allowViewAgenda: true,

            defaultView: component.get('defaultView'),

            dataSourceType: CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(component.get('dataSourceName')),
            dataSourceTypeName: component.get('dataSourceName'),
            dataSourceFilter: component.get('dataSourceFilter') || null,

            eventColor: component.get('fgcolor') || null,

            eventStartDateAttribute: component.get('eventStartDateAttribute'),
            eventEndDateAttribute: component.get('eventEndDateAttribute') || component.get('eventStartDateAttribute'),
            eventTitleAttribute: component.get('eventTitleAttribute') || 'Description'
        });
    },

    privates: {
        /**
         * Rebuilds the calendar from scratch to fetch new elements
         */
        menuItemReloadHandler: function () {
            const view = this.getView();
            const calendar = view.down('#calendar');
            if (calendar) {
                calendar.destroy();
            }
            this.initCalendarConfig(view, view.getItemDefinition());
        }
    }
});
