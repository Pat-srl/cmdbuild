Ext.define('CMDBuildUI.view.administration.content.classes.tabitems.properties.fieldsets.MobileFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-tabitems-properties-fieldsets-mobilefieldset',

    formulas: {
        canShowMobileAttributes: {
            get: function () {
                return CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled);
            }
        }
    },
    stores: {
        mobileSearchAttributesStore: {
            source: '{attributesStore}',
            filters: [
                function (item) {
                    var mobileEditorTypes = [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ];
                    return item.get('type') && mobileEditorTypes.indexOf(item.get('type')) > -1;
                }
            ]
        }
    }
});
