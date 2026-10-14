Ext.define('CMDBuildUI.view.administration.content.dependencytrees.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dependencytrees-domains',

    data: {
        treeRoot: null
    },

    formulas: {
        treeRoot: {
            bind: {
                targetClass: '{theDependencyTree.targetClass}',
                theDependencyTree: '{theDependencyTree}'
            },
            get: function (data) {
                const dependencyTree = data.theDependencyTree;
                const root = dependencyTree.nodes().count() ? dependencyTree.nodes().first().getData() : {};

                if (root && !data.targetClass) {
                    dependencyTree.set('targetClass', root.targetClass);
                }

                const target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                    data.targetClass || root.targetClass
                );

                const targetDescription =
                    target && target.get('description') ? target.get('description') : data.targetClass;

                const node = {};
                node._id = root._id;
                node.text = targetDescription;
                node.targetClass = data.targetClass;
                node.direction = '_1';
                node.children = [];
                node.expanded = false;
                node.checked = true;
                node.domain = root.domain;
                node.filter = typeof root.filter === 'string' ? root.filter : '';
                node.isRoot = true;

                return node;
            }
        }
    },

    stores: {
        treeStore: {
            type: 'tree',
            folderSort: true,
            proxy: {
                type: 'memory'
            },
            sorters: ['text'],
            root: '{treeRoot}',
            listeners: {
                rootchange: 'onTreeStoreRootChange'
            }
        }
    }
});
