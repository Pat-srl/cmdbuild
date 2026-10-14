Ext.define('CMDBuildUI.view.management.DetailsWindowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.management-detailswindow',

    control: {
        '#': {
            show: 'onShow',
            closed: 'onClosed'
        }
    },

    /**
     *
     * @param {Ext.window.Window} window
     * @param {Object} eOpts
     */
    onShow: function (window, eOpts) {
        const size = Ext.getBody().getViewSize();
        const aiChatContainerWidth = CMDBuildUI.util.Navigation.getMainContainer()
            .down('management-ai-chat')
            .getWidth();

        window.setPosition(
            size.width - window.getWidth() - aiChatContainerWidth - 10,
            size.height - window.getHeight()
        );
    },

    /**
     *
     */
    onClosed: function () {
        this.destroyDetailsWindow();
        this.fireEvent('close');
    },

    privates: {
        /**
         * @private
         */
        destroyDetailsWindow: function () {
            const view = this.getView();
            if (view) {
                view.close();
            }
        }
    }
});
