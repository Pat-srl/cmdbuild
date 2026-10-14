Ext.define('CMDBuildUI.util.administration.helper.NavTreeHelper', {
    singleton: true,

    getViewPlugins: function () {
        return {
            pluginId: 'cellediting',
            ptype: 'cellediting',
            clicksToEdit: 1,
            listeners: {
                beforeedit: function (editor, context) {
                    if (
                        editor.view.lookupViewModel().get('actions.view') ||
                        (!context.record.get('checked') && !context.record.get('filter'))
                    ) {
                        return false;
                    }
                }
            }
        };
    },

    recursiveParentCheck: function (node, checked) {
        if (checked) {
            node.set('checked', true);
            if (node.parentNode) {
                // up direction
                CMDBuildUI.util.administration.helper.NavTreeHelper.recursiveParentCheck(node.parentNode, checked);
            }
        } else {
            // down direction
            node.set('checked', false);
            if (node.childNodes && node.childNodes.length) {
                Ext.Array.forEach(node.childNodes, function (item) {
                    CMDBuildUI.util.administration.helper.NavTreeHelper.recursiveParentCheck(item, checked);
                });
            }
        }
    },

    findCheckedNodes: function (node, nodes) {
        var foundNode;
        Ext.Array.forEach(nodes, function (item) {
            if (!foundNode) {
                if (item.parent === node.get('_id')) {
                    foundNode = item;
                } else {
                    foundNode = CMDBuildUI.util.administration.helper.NavTreeHelper.findCheckedNodes(node, item.nodes);
                }
            }
        });
        return foundNode;
    }
});
