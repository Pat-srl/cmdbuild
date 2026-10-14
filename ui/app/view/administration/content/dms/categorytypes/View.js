(function () {
    var elementId = 'CMDBuildAdministrationContentDMSCategoryTypesView';
    Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.View', {
        extend: 'Ext.container.Container',

        requires: [
            'CMDBuildUI.view.administration.content.dms.dmscategorytypes.ViewController',
            'CMDBuildUI.view.administration.content.dms.dmscategorytypes.ViewModel'
        ],

        alias: 'widget.administration-content-dms-dmscategorytypes-view',
        controller: 'administration-content-dms-dmscategorytypes-view',
        viewModel: {
            type: 'administration-content-dms-dmscategorytypes-view'
        },

        title: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookuptype,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.lookuptype'
        },

        itemId: elementId,
        reference: elementId,
        id: elementId,

        config: {
            objectTypeName: null,
            allowFilter: true,
            showAddButton: true,
            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
            title: null
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
                xtype: 'administration-content-dms-dmscategorytypes-topbar',
                region: 'north'
            },
            {
                xtype: 'administration-content-dms-dmscategorytypes-tabpanel',
                region: 'center',
                hidden: true,
                bind: {
                    hidden: '{!theDMSCategoryType}'
                }
            }
        ],

        listeners: {
            afterlayout: function (panel) {
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            }
        },

        initComponent: function () {
            this.callParent(arguments);
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        }
    });
})();
