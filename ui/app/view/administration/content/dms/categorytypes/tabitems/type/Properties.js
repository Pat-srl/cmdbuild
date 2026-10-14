Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.type.Properties', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.type.PropertiesController'
    ],

    alias: 'widget.administration-content-dms-dmscategorytypes-tabitems-type-properties',
    controller: 'administration-content-dms-dmscategorytypes-tabitems-type-properties',
    autoScroll: false,
    modelValidation: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    layout: 'fit',
    items: [{
        xtype: 'administration-content-dms-dmscategorytypes-tabitems-type-fieldsets-generaldatafieldset',
        scrollable: 'y'
    }],

    dockedItems: [{
        dock: 'top',
        xtype: 'container',
        items: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                delete: true
            }, 'DMSCategory', 'theDMSCategoryType'),
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        }]
    }, {
        xtype: 'toolbar',
        dock: 'bottom',
        ui: 'footer',
        hidden: true,
        bind: {
            hidden: '{actions.view}'
        },
        items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
    }]
});