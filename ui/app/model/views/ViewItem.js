Ext.define('CMDBuildUI.model.views.ViewItem', {
    extend: 'CMDBuildUI.model.base.Base',

    /**
     * @return {Numeric|String} Record id. The same value returned by this.getId() function.
     */
    getRecordId: function () {
        return this.getId();
    },
    /**
     * @return {Numeric|String} Record type.
     */
    getRecordType: function () {
        return CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.view;
    }
});
