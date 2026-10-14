Ext.define(
    'CMDBuildUI.view.administration.components.attributes.fieldscontainers.typeproperties.commons.MobileEditorModel',
    {
        extend: 'Ext.app.ViewModel',
        alias: 'viewmodel.administration-attribute-commons-mobileedtor',
        formulas: {
            canShowMobileAttributes: {
                bind: '{theAttribute.type}',
                get: function (attributeType) {
                    var mobileEditorTypes = [
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.bigint,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.integer,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.string,
                        CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.text
                    ];
                    return (
                        attributeType &&
                        mobileEditorTypes.indexOf(attributeType) > -1 &&
                        CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.mobile.enabled)
                    );
                }
            },
            mobileEditors: {
                bind: '{theAttribute.type}',
                get: function (attributeType) {
                    return CMDBuildUI.model.Attribute.getMobileEditors(attributeType);
                }
            }
        },

        stores: {
            mobileEditorsStore: {
                model: 'CMDBuildUI.model.base.ComboItem',
                proxy: 'memory',
                data: '{mobileEditors}',
                autoDestroy: true
            }
        }
    }
);
