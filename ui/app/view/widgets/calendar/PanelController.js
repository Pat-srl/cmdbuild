Ext.define('CMDBuildUI.view.widgets.calendar.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.widgets-calendar-panel',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.widgets.calendar.Panel} view 
     * @param {Object} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        var vm = view.lookupViewModel(),
            target = vm.get("theTarget"),
            widget = vm.get("theWidget"),
            className = widget.get("ClassName"),
            eventStartDate = widget.get("EventStartDate"),
            error;

        if (className && eventStartDate) {
            var sourceTypeName = className.replace(/"/g, ''),
                sourceType = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(sourceTypeName);

            if (sourceType && (target.get(eventStartDate) || target.get("_type") !== sourceTypeName)) {

                view.add({
                    xtype: 'ux-calendar',
                    dataSourceType: sourceType,
                    dataSourceTypeName: sourceTypeName,
                    dataSourceFilter: widget.get("Filter") ? widget.get("_Filter_ecql") : null,
                    targetObject: target,
                    eventStartDateAttribute: eventStartDate,
                    eventEndDateAttribute: widget.get("EventEndDate"),
                    eventTitleAttribute: widget.get("EventTitle"),
                    eventTypeAttribute: widget.get("EventType"),
                    eventTypeLookup: widget.get("EventTypeLookup"),
                    openingDate: widget.get("DefaultDate") ? target.get(widget.get("DefaultDate")) : null,
                    height: widget.get('_inline') ? view.up("form").getHeight() * 0.75 : undefined
                });

                if (widget.get("_required")) {
                    widget.getOwner().setValue(true);
                }

            } else {
                error = Ext.String.format(
                    CMDBuildUI.locales.Locales.widgets.calendar.errorwrong,
                    !sourceType ? "ClassName" : "EventStartDate");
            }

        } else {
            error = Ext.String.format(
                CMDBuildUI.locales.Locales.widgets.calendar.error,
                !className ? "ClassName" : "EventStartDate");
        }

        if (error) {
            view.add({
                xtype: 'formvalidatorfield',
                calendarMessage: error
            });
        }
    }

});