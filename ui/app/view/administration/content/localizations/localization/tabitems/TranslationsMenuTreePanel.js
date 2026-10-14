Ext.define('CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanel', {
    extend: 'Ext.tree.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelController',
        'CMDBuildUI.view.administration.content.localizations.localization.tabitems.TranslationsMenuTreePanelModel'
    ],

    alias: 'widget.administration-content-localizations-localization-tabitems-translationsmenutreepanel',
    controller: 'administration-content-localizations-localization-tabitems-translationsmenutreepanel',
    viewModel: {
        type: 'administration-content-localizations-localization-tabitems-translationsmenutreepanel'
    },
    viewConfig: {
        markDirty: false
    },
    ui: 'administration-navigation-tree',
    config: {
        rootVisible: false,
        section: null
    },
    plugins: {
        pluginId: 'cellediting',
        ptype: 'cellediting',
        clicksToEdit: 1,
        listeners: {
            beforeedit: 'onBeforCellEdit',
            edit: 'editedCell'
        }

    },
    forceFit: true,
    store: Ext.create('Ext.data.TreeStore', {
        model: 'CMDBuildUI.model.menu.MenuItem',
        storeId: 'menuTreeStore',
        reference: 'menuTreeStore',
        root: {
            text: 'Root',
            expanded: true
        },
        proxy: {
            type: 'memory'
        },
        autoLoad: true
    }),

    columns: [],
    dockedItems: [{
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
    }]
});