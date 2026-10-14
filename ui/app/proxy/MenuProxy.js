(function () {
    Ext.define('CMDBuildUI.proxy.MenuProxy', {
        extend: "CMDBuildUI.proxy.BaseProxy",
        alias: 'proxy.menuproxy',
        requires: [
            'CMDBuildUI.proxy.MenuProxy.MenuReader'
        ],
        timeout: CMDBuildUI.util.Config.ajaxTimeout || 15000,
        reader: {
            type: 'menureader',
            rootProperty: 'children',
            totalProperty: 'meta.total'
        }
    });

    Ext.define('CMDBuildUI.proxy.MenuProxy.MenuReader', {
        extend: 'Ext.data.reader.Json',
        alias: 'reader.menureader',

        config: {
            rootProperty: 'children',
            totalProperty: 'meta.total'
        },

        /**
         * Takes a raw response object (as passed to the {@link #read} method) and returns the useful data
         * segment from it. This must be implemented by each subclass.
         * @param {Object} response The response object
         * @return {Object} The extracted data from the response. For example, a JSON object or an XML document.
         * @override
         */
        getResponseData: function () {
            var jsondata = this.callParent(arguments);
            var data;
            if (jsondata.data) {
                data = jsondata.data;
            } else {
                data = jsondata;
            }
            data.children = checkChildrenTypes(data.children);
            return data;
        }
    });

    function checkChildrenTypes(children) {
        var new_children = [];

        Ext.Array.each(children, function (child, index) {
            var isFolder = child.menuType === 'folder';

            // check children
            if (isFolder) {
                child.leaf = false;
                child.children = checkChildrenTypes(child.children);
                if (child.children.length) {
                    new_children.push(child);
                } else {
                    // log warning
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format("Navigation - Empty folder. Hidding menu item {0} [{1}]", child.objectDescription, child.menuType),
                        CMDBuildUI.util.Logger.levels.warn
                    );
                }
            } else {
                child.leaf = true;
                if (Ext.Object.getValues(CMDBuildUI.model.menu.MenuItem.types).indexOf(child.menuType) !== -1) {
                    new_children.push(child);
                } else {
                    // log warning
                    CMDBuildUI.util.Logger.log(
                        Ext.String.format("Navigation - enu type not implemented. Hidding menu item {0} [{1}]", child.objectDescription, child.menuType),
                        CMDBuildUI.util.Logger.levels.warn
                    );
                }
            }
        });
        return new_children;
    }
})();