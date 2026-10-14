Ext.define('CMDBuildUI.view.joinviews.configuration.items.FieldsetsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.joinviews-configuration-items-fieldsets',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        attributeGroupEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.AttributeGrouping');
        },
        attriubteGroupingDisplayModes: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getAttriubteGroupingDisplayModes();
        }
    },
    stores: {
        attributeGroupsStore: {
            source: '{theView.attributeGroups}'
        },
        attributeGroupsStoreNew: {
            model: 'CMDBuildUI.model.AttributeGrouping',
            alias: 'store.attribute-groupings-new',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{attributeGroupEmptyRecord}'
        },
        attriubteGroupingDisplayModeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attriubteGroupingDisplayModes}',
            autoDestroy: true
        }
    }

});