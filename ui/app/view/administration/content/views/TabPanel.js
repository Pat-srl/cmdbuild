Ext.define('CMDBuildUI.view.administration.content.views.TabPanel', {
    extend: 'Ext.tab.Panel',

    alias: 'widget.administration-content-views-tabpanel',
    controller: 'administration-content-views-tabpanel',
    viewModel: {
        type: 'administration-content-views-tabpanel'
    },
    requires: [
        'CMDBuildUI.view.administration.content.views.TabPanelController',
        'CMDBuildUI.view.administration.content.views.TabPanelModel',
        'CMDBuildUI.view.administration.content.views.card.Form',
        'CMDBuildUI.view.administration.content.views.card.Permissions'
    ],
    itemId: 'viewtabpanel',
    tabPosition: 'top',
    tabRotation: 0,
    cls: 'administration-mainview-tabpanel',
    ui: 'administration-tabandtools',
    scrollable: true,
    forceFit: true,
    layout: 'fit',

    bind: {
        activeTab: '{activeTab}'
    },

    dockedItems: [{
        xtype: 'components-administration-toolbars-formtoolbar',
        dock: 'top',
        padding: '5 10 5 10',
        borderBottom: 0,
        itemId: 'toolbarscontainer',
        style: 'border-bottom-width:0!important',
        items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
            'searchfilter',
            'theViewFilter',
            [{
                xtype: 'button',
                text: CMDBuildUI.locales.Locales.joinviews.addview, // Add view
                localized: {
                    text: 'CMDBuildUI.locales.Locales.joinviews.addview' // Add view
                },
                ui: 'administration-action-small',
                itemId: 'addBtn',
                autoEl: {
                    'data-testid': 'administration-class-toolbar-addViewBtn'
                },
                bind: {
                    disabled: '{!toolAction._canAdd}'
                }
            }, {
                xtype: 'admin-globalsearchfield',
                objectType: 'views'
            }, {
                xtype: 'tbfill'
            }],
            null,
            [{
                xtype: 'tbtext',

                bind: {
                    hidden: '{!theViewFilter.name}',
                    html: CMDBuildUI.locales.Locales.administration.localizations.view + ': <b data-testid="administration-views-toolbar-description">{theViewFilter.description}</b>'
                }
            }])
    }],
    listeners: {
        itemupdated: 'onItemUpdated',
        cancelcreation: 'onCancelCreation',
        cancelupdating: 'onCancelUpdating',
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
        }
    }
});
