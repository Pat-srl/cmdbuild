Ext.define('CMDBuildUI.view.dms.attachment.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.dms-attachment-view',

    data: {
        DMSModelClassName: undefined,
        DMSModelClass: undefined,
        DMSClass: undefined
    },

    formulas: {
        DMSModelClassNameCalculation: {
            bind: {
                DMSCategoryTypeName: '{DMSCategoryTypeName}',
                DMSCategoryValue: '{record.category}'
            },
            get: function (data) {
                if (data.DMSCategoryTypeName && data.DMSCategoryValue) {
                    const DMSCatygoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(data.DMSCategoryTypeName);

                    DMSCatygoryType.getCategoryValues().then(function (categoryValues) {
                        const r = categoryValues.findRecord('_id', data.DMSCategoryValue);

                        if (r) {
                            const DMSModelClassName = r.get('modelClass') || 'BaseDocument';
                            this.set("DMSModelClassName", DMSModelClassName);
                        }
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        DMSDataCalculations: {
            bind: '{DMSModelClassName}',
            get: function (DMSModelClassName) {
                if (DMSModelClassName) {
                    this.set("DMSClass", CMDBuildUI.util.helper.ModelHelper.getDMSModelFromName(DMSModelClassName));

                    CMDBuildUI.util.helper.ModelHelper.getModel(
                        CMDBuildUI.util.helper.ModelHelper.objecttypes.dmsmodel,
                        DMSModelClassName
                    ).then(function (model) {
                        this.set("DMSModelClass", model);
                    }, Ext.emptyFn, Ext.emptyFn, this);
                }
            }
        },

        DMSWidgets: {
            bind: '{record.widgets}',
            get: function (widgets) {
                return widgets;
            }
        }
    }
});
