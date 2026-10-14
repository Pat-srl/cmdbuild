Ext.define('CMDBuildUI.view.events.CalendarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-calendar',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * Before rendere handler
     * @param {CMDBuildUI.view.events.Calendar} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const me = this;
        view.add({
            xtype: 'ux-calendar',
            itemId: 'calendar',
            dataSourceType: CMDBuildUI.util.helper.ModelHelper.objecttypes.calendar,
            dataSourceTypeName: CMDBuildUI.util.helper.ModelHelper.objecttypes.event,
            dataSourceFilter: null,
            targetObject: null,
            eventStartDateAttribute: 'date',
            dataSourceUrl: CMDBuildUI.util.Config.baseUrl + CMDBuildUI.util.api.Calendar.getEventsUrl(),
            dataSourceExtraParams: {
                /* detailed: true */
            },
            eventTitleAttribute: 'description',
            eventTypeAttribute: 'category',
            eventLookupValueField: 'code',
            eventTypeLookup: 'CalendarCategory',
            eventClickHandler: this.eventClickHandler,
            viewSkeletonRender: function () {
                me.listen({
                    store: {
                        '#scheules': {
                            load: 'onEventsStoreLoad'
                        }
                    }
                });

                const container = view.up('events-container'),
                    store = container.getViewModel().get('schedules'),
                    //clones the main filter
                    advancedFilter = store.getAdvancedFilter().config,
                    newAdvancedFilter = new CMDBuildUI.util.AdvancedFilter(advancedFilter),
                    uxCalendar = view.down('#calendar');

                uxCalendar.applyAdvanceFilter(newAdvancedFilter);

                // Accessibility fixes
                const btns = uxCalendar.el.query('.fc-button-group button');
                btns.forEach(function (btn) {
                    const icon = btn.querySelector('span[role="img"]');
                    if (icon) {
                        icon.setAttribute('aria-hidden', 'true');
                    }

                    const title = btn.getAttribute('title');
                    if (title) {
                        btn.setAttribute('aria-label', title);
                    }
                });
            }
        });
    },

    onEventsStoreLoad: function (store, records, success, operation, eOpts) {
        const uxCalendar = this.getView().down('#calendar'),
            //clones the main filter
            advancedFilter = store.getAdvancedFilter().config,
            newAdvancedFilter = new CMDBuildUI.util.AdvancedFilter(advancedFilter);

        //applies the filter
        uxCalendar.applyAdvanceFilter(newAdvancedFilter);
    },

    privates: {
        /**
         *
         * @param {Object} eventClickInfo
         */
        eventClickHandler: function (eventClickInfo) {
            const url = CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                eventClickInfo.event.id,
                CMDBuildUI.mixins.DetailsTabPanel.actions.view
            );

            CMDBuildUI.util.Utilities.redirectTo(url);
        }
    }
});
