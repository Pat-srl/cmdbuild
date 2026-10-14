Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.values.ValuesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-dmscategorytypes-tabitems-values-values',
    data: {
        theValue: {}
    },
    formulas: {
        getSelectedValue: {
            bind: '{theValue}',
            get: function (theValue) {
                if (theValue) {
                    return theValue;
                }
            }
        },
        allValuesProxy: {
            bind: '{theValue.name}',
            get: function (objectTypeName) {
                if (objectTypeName) {
                    return {
                        url: CMDBuildUI.util.administration.helper.ApiHelper.server.getDMSCategoryValuesUrl(objectTypeName),
                        type: 'baseproxy'
                    };
                }
            }
        }
    }
});