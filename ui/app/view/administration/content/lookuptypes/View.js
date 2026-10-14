Ext.define('CMDBuildUI.view.administration.content.lookuptypes.View', {
    extend: 'Ext.container.Container',

    requires: [
        'CMDBuildUI.view.administration.content.lookuptypes.ViewController',
        'CMDBuildUI.view.administration.content.lookuptypes.ViewModel'
    ],

    alias: 'widget.administration-content-lookuptypes-view',
    controller: 'administration-content-lookuptypes-view',
    viewModel: {
        type: 'administration-content-lookuptypes-view'
    },

    title: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookuptype,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookuptype'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    style: 'background-color:#fff',

    items: [
        {
            xtype: 'administration-content-lookuptypes-topbar',
            region: 'north'
        },
        {
            xtype: 'administration-content-lookuptypes-tabpanel',
            region: 'center',
            hidden: true,
            bind: {
                hidden: '{!theLookupType}'
            }
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
    }
});
