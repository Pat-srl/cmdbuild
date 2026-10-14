Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.gis.gismenus.ViewController',
        'CMDBuildUI.view.administration.content.gis.gismenus.ViewModel'
    ],

    controller: 'administration-content-gismenus-view',
    viewModel: {
        type: 'administration-content-gismenus-view'
    },
    alias: 'widget.administration-content-gismenus-view',
    defaults: {
        backgrounColor: '#ffffff',
        textAlign: 'left'
    },
    layout: 'fit',
    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        cls: 'administration-mainview-tabpanel',
        region: 'top',
        borderBottom: 0,
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                edit: true, // #editBtn set true for show the button
                view: false, // #viewBtn set true for show the button
                clone: false, // #cloneBtn set true for show the button
                'delete': false, // #deleteBtn set true for show the button
                activeToggle: false, // #enableBtn and #disableBtn set true for show the buttons
                download: false // #downloadBtn set true for show the buttons
            },

            /* testId */
            'menu',

            /*viewModel object*/
            'theMenu',

            /*add custom tools[] on the left of the bar*/
            null,

            /* add custom tools[] before #editBtn*/
            null           
        )

    }],
    items: [{
        xtype: 'administration-content-gismenus-mainpanel',       
        hidden: true,
        bind: {
            hidden: '{!theMenu}'
        },
        viewModel: {}
    }],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    },
    initComponent: function () {
        this.callParent(arguments);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
    }
});