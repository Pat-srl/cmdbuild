(function () {
    const elementId = 'CMDBuildAdministrationDetailsWindow';

    Ext.define('CMDBuildUI.view.administration.DetailsWindow', {
        extend: 'CMDBuildUI.view.management.DetailsWindow',
        statics: {
            elementId: elementId
        },

        requires: [
            'CMDBuildUI.view.management.DetailsWindowController',
            'CMDBuildUI.view.management.DetailsWindowModel'
        ],

        alias: 'widget.administration-detailswindow',
        controller: 'management-detailswindow',
        viewModel: {
            type: 'management-detailswindow'
        },

        preventRefocus: true,
        bind: {
            title: '{title}'
        },

        id: elementId,
        autoEl: {
            'data-testid': 'cards-card-administration-detailsWindow'
        },

        ui: 'administration',

        listeners: {
            // bubbled events are not listened if declared in control property within ViewController
            minimize: function (win, obj) {
                win.minimize();
            }
        }
    });
})();
