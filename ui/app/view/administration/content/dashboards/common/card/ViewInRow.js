Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.dashboards.common.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.dashboards.common.card.ViewInRowModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-dashboards-card-viewinrow',
    controller: 'administration-content-dashboards-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-dashboards-card'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    userCls: 'formmode-view', // this is used for hide label localzation icon in `view` mode

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theDashboard: null
    },

    items: [],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools(
        {
            edit: true, // #editBtn set true for show the button
            view: true, // #viewBtn set true for show the button
            clone: false, // #cloneBtn set true for show the button
            delete: true, // #deleteBtn set true for show the button
            activeToggle: true, // #enableBtn and #disableBtn set true for show the buttons
            download: false // #downloadBtn set true for show the buttons
        },

        /* testId */
        'dashboards',

        /* viewModel object needed only for activeTogle */
        'theDashboard',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [],

        /* add custom tools[] after at the end of the bar*/
        []
    ),

    listeners: {
        afterlayout: function (panel) {
            try {
                panel.unmask();
            } catch (error) {}
        }
    }
});
