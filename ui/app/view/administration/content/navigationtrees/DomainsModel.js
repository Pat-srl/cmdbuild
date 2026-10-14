Ext.define('CMDBuildUI.view.administration.content.navigationtrees.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-navigationtrees-domains',

    data: {
        treeRoot: null
    },

    formulas: {
        treeRoot: {
            bind: {
                targetClass: '{theNavigationtree.targetClass}',
                theNavigationtree: '{theNavigationtree}'
            },
            get: function (data) {
                const root = data.theNavigationtree.nodes().count()
                    ? data.theNavigationtree.nodes().first().getData()
                    : {};

                if (root && !data.targetClass) {
                    this.set('theNavigationtree.targetClass', root.targetClass);
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
                node.targetIsProcess = false;
                node.direction = '_1';
                node.children = [];
                node.expanded = false;
                node.multilevel = false;
                node.checked = true;
                node.filter = '';
                node.domain = root.domain;
                node.filter = typeof root.filter === 'string' ? root.filter : '';
                node.recursionEnabled = root.recursionEnabled;
                node.showOnlyOne = root.showOnlyOne;

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
