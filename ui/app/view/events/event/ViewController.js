Ext.define('CMDBuildUI.view.events.event.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-event-view',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();
        vm.bind('{theEvent}', function (theEvent) {
            if (theEvent) {
                view.generateForm();
            }
        }, view);
    },

    privates: {
        /**
         * Get resource base path for routing.
         * @return {String}
         */
        getBasePath: function () {
            const theEvent = this.getViewModel().get("theEvent");
            return 'events/' + theEvent.getId();
        }
    }
});
