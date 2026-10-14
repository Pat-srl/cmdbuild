Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.Topbar', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.TopbarController',
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.TopbarModel'
    ],

    alias: 'widget.administration-content-dms-dmscategorytypes-topbar',
    controller: 'administration-content-dms-dmscategorytypes-topbar',
    viewModel: {
        type: 'administration-content-dms-dmscategorytypes-topbar'
    },
    config: {
        objectTypeName: null,
        allowFilter: true,
        showAddButton: true
    },

    forceFit: true,
    loadMask: true,

    tbar: [{
        xtype: 'button',
        text: CMDBuildUI.locales.Locales.administration.dmscategories.adddmscategory,
        localized: {
            text: 'CMDBuildUI.locales.Locales.administration.dmscategories.adddmscategory'
        },
        ui: 'administration-action-small',
        reference: 'addDMSCategory',
        itemId: 'addDMSCategory',
        autoEl: {
            'data-testid': 'administration-class-toolbar-addDMSCtegoryTypeBtn'
        },
        bind: {
            disabled: '{!toolAction._canAdd}'
        }
    }, {
        xtype: 'admin-globalsearchfield',
        objectType: 'dmscategories'
    }, {
        xtype: 'tbfill'
    }, {
        xtype: 'tbtext',
        dock: 'right',
        bind: {
            html: '{DMSCategoryLabel}: <b data-testid="administration-DMSCategories-toolbar-className">{theDMSCategoryType.name}</b>'
        }
    }],

    initComponent: function () {
        var vm = this.lookupViewModel();
        vm.getParent().set('title', CMDBuildUI.locales.Locales.administration.navigation.lookuptypes);
        this.callParent(arguments);
    }
});