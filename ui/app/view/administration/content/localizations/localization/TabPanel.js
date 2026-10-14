Ext.define('CMDBuildUI.view.administration.content.localizations.localization.TabPanel', {
    extend: 'Ext.tab.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.TabPanelController',
        'CMDBuildUI.view.administration.content.localizations.localization.TabPanelModel'
    ],

    alias: 'widget.administration-content-localizations-localization-tabpanel',
    controller: 'administration-content-localizations-localization-tabpanel',
    viewModel: {
        type: 'administration-content-localizations-localization-tabpanel'
    },

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
    tbar: [{
        xtype: 'textfield',
        name: 'search',
        width: 250,
        enableKeyEvents: true,

        emptyText: CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search,
        localized: {
            emptyText: 'CMDBuildUI.locales.Locales.administration.attributes.emptytexts.search'
        },
        reference: 'localizationsearchtext',
        itemId: 'localizationsearchtext',
        cls: 'administration-input',
        bind: {
            hidden: '{!canFilter}'
        },
        listeners: {
            keyup: 'onKeyUp'
        },
        triggers: {
            clear: {
                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                handler: 'onSearchClear'
            }
        }
    }, {
        flex: 1,
        ui: 'messageinfo',
        xtype: 'container',
        hidden: true,
        bind: {
            html: '{menuInfomessage}',
            hidden: '{canFilter}'
        }
    }, {
        xtype: 'tbfill',
        hidden: true,
        bind: {
            hidden: '{!canFilter}'
        }
    }, {

        xtype: 'tool',
        itemId: 'editBtn',
        cls: 'administration-tool',
        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
        tooltip: CMDBuildUI.locales.Locales.administration.common.actions.edit,
        localized: {
            tooltip: 'CMDBuildUI.locales.Locales.administration.common.actions.edit'
        },
        hidden: true,
        bind: {
            hidden: '{!actions.view}',
            disabled: "{!toolAction._canUpdate}"
        },
        callback: 'onEditBtnClick',
        autoEl: {
            'data-testid': 'administration-content-localizations-localization-tabpanel-tool-editbtn'
        }

    }]
});