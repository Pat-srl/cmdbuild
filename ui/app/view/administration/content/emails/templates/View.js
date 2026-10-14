Ext.define('CMDBuildUI.view.administration.content.emails.templates.View', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.ViewController',
        'CMDBuildUI.view.administration.content.emails.templates.ViewModel'
    ],

    alias: 'widget.administration-content-emails-templates-view',
    controller: 'administration-content-emails-templates-view',
    viewModel: {
        type: 'administration-content-emails-templates-view'
    },

    loadMask: true,
    defaults: {
        textAlign: 'left',
        scrollable: true
    },
    layout: 'border',
    items: [{
            xtype: 'administration-content-emails-templates-topbar',
            region: 'north'
        },
        {
            xtype: 'administration-content-emails-templates-grid',
            region: 'center',
            bind: {
                hidden: '{isGridHidden}'
            }
        }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }
    }
});