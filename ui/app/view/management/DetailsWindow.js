(function () {
    const elementId = 'CMDBuildManagementDetailsWindow';

    Ext.define('CMDBuildUI.view.management.DetailsWindow', {
        extend: 'Ext.window.Window',
        statics: {
            elementId: elementId
        },

        requires: [
            'CMDBuildUI.view.management.DetailsWindowController',
            'CMDBuildUI.view.management.DetailsWindowModel'
        ],
        closeToolText: CMDBuildUI.locales.Locales.common.actions.close,
        localized: {
            closeToolText: 'CMDBuildUI.locales.Locales.common.actions.close'
        },
        controller: 'management-detailswindow',
        viewModel: {
            type: 'management-detailswindow'
        },

        bind: {
            title: '{titledata.operation} {titledata.action} {titledata.type} {titledata.item}'
        },

        id: elementId,
        autoEl: {
            'data-testid': 'management-detailsWindow'
        },
        ui: 'management',
        layout: 'fit',
        autoShow: true,
        resizable: true,
        draggable: true,
        // minimizable: true,
        maximizable: true,
        monitorResize: true,
        defaultAlign: 'br-br',

        showAnimation: {
            type: 'popIn',
            duration: 250,
            easing: 'ease-out'
        },

        hideAnimation: {
            type: 'popOut',
            duration: 250,
            easing: 'ease-out'
        },

        initComponent: function () {
            const me = this;
            const viewSize = Ext.getBody().getViewSize();
            const aiChatContainerWidth = CMDBuildUI.util.Navigation.getMainContainer()
                .down('management-ai-chat')
                .getWidth();
            const percentageAiChatContainerWidth = Math.ceil((aiChatContainerWidth / viewSize.width) * 100);
            const percentageDetailWindowWidth = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.ui.detailwindow.width
            );

            const width =
                percentageDetailWindowWidth + percentageAiChatContainerWidth > 100
                    ? 100 - percentageAiChatContainerWidth + '%'
                    : percentageDetailWindowWidth + '%';
            const height =
                CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.ui.detailwindow.height) + '%';
            Ext.apply(this, {
                width: width,
                height: height,
                tools: []
            });

            // reset width on body resize
            this.mon(Ext.getBody(), 'resize', function () {
                me.setWidth(width);
                me.setHeight(height);
            });

            this.callParent(arguments);
        },

        /**
         * set position within body
         * @param {Numeric} x
         * @param {Numeric} y
         */
        afterSetPosition: function (x, y) {
            var newx, newy;
            const viewsize = Ext.getBody().getViewSize();
            if (x < 0) {
                newx = '0';
            } else if (x > viewsize.width - this.getWidth()) {
                newx = viewsize.width - this.getWidth();
            }
            if (y < 0) {
                newy = '0';
            } else if (y > viewsize.height - this.getHeight()) {
                newy = viewsize.height - this.getHeight();
            }

            if (newx !== undefined || newy !== undefined) {
                newx = newx || x;
                newy = newy || y;
                this.setPosition(newx, newy);
            }
        },

        /**
         *
         * @param {Number} width
         * @param {Number} height
         * @param {Number} oldWidth
         * @param {Number} oldHeight
         */
        onResize: function (width, height, oldWidth, oldHeight) {
            const viewsize = Ext.getBody().getViewSize();
            if (width > viewsize.width) {
                this.setWidth(viewsize.width);
            }
            if (height > viewsize.height) {
                this.setHeight(viewsize.height);
            }
        }
    });
})();
