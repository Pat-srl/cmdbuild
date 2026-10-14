Ext.define('CMDBuildUI.util.administration.helper.SortHelper', {
    singleton: true,
    /**
     * @cgf {String} attribute
     */
    attribute: null,
    /**
     * 
     * @param {Ext.data.Model} modelArray 
     * @param {String} attribute 
     */
    sort: function (modelArray, attribute) {
        if(!attribute){
            Ext.raise('attribute argument is null. Unable to sort!');
        }
        CMDBuildUI.util.administration.helper.SortHelper.attribute = attribute;
        Ext.Array.sort(modelArray, this.sortBy);
        return modelArray;
    },
    /**
     * @private
     */
    privates: {
        sortBy: function (a, b) {
            // TODO: WARNING this work only if attribute type is string!
            if (a.get && b.get) {
                var attribute = CMDBuildUI.util.administration.helper.SortHelper.attribute;
                var nameA = a.get(attribute).toUpperCase(); // ignore upper and lowercase
                var nameB = b.get(attribute).toUpperCase(); // ignore upper and lowercase
                if (nameA < nameB) {
                    return -1;
                }
                if (nameA > nameB) {
                    return 1;
                }
                // if descriptions are same
                return 0;
            }
        }
    }
});