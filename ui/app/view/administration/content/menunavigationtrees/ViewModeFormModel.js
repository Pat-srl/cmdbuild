Ext.define('CMDBuildUI.view.administration.content.menunavigationtrees.ViewModeFormModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-menunavigationtrees-viewmodeform',
    data: {
        name: 'CMDBuildUI'
    },
    formulas: {
        treeFiledsetTitle: {
            bind: {
                subclassViewMode: '{record.subclassViewMode}'
            },
            get: function (data) {
                if (data.subclassViewMode === 'cards') {                    
                    return CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.filtersubclasses;
                } else if (data.subclassViewMode === 'subclasses') {
                    return CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.visiblesubclasses;
                }

            }
        },
        showIntemediatesHidden: {
            bind: {
                subclassViewMode: '{record.subclassViewMode}'
            },
            get: function (data) {
                if (data.subclassViewMode) {
                    return data.subclassViewMode !== CMDBuildUI.model.navigationTrees.TreeNode.subclassViewMode.subclasses;
                }
            }
        },
        subclassesTreeManager: {
            bind: {
                record: '{record}'
            },
            get: function (data) {
                var source = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.record.get('targetClass'));
                var sourceTree = source.getChildrenAsTree(true, function (item) {
                    return item;
                }, true, function (node) {
                    var subclassFilter = [];
                    if (!Ext.isEmpty(data.record.get('subclassFilter'))) {
                        subclassFilter = data.record.get('subclassFilter').split(',');
                    }
                    node.checked = subclassFilter.indexOf(node.name) > -1;
                    var subclassDescription = data.record.get(Ext.String.format('subclass_{0}_description', node.name));
                    node.description = subclassDescription || '';
                    node.enabled = true;
                    return node;
                });

                // generate the source tree
                var originRoot = {
                    expanded: true,
                    description: source.get('description'),
                    text: source.get('description'),
                    name: source.get('name'),
                    leaf: sourceTree.length > 1 ? false : true,
                    children: sourceTree.length > 1 ? sourceTree : false,
                    enabled: sourceTree.length == 1 ? (sourceTree[0].enabled) ? true : false : undefined
                };
                this.get('subclassesStore').setRoot(originRoot);
            }
        },

        subclassViewModeData: {
            get: function () {
                return [{
                    value: CMDBuildUI.model.navigationTrees.TreeNode.subclassViewMode.cards,
                    label: CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.allsubclassess
                }, {
                    value: CMDBuildUI.model.navigationTrees.TreeNode.subclassViewMode.subclasses,
                    label: CMDBuildUI.locales.Locales.administration.navigationtrees.fieldlabels.onlysomesubclassesinsuperclassnode

                }];
            }
        }
    },

    stores: {
        subclassViewModeStore: {
            proxy: {
                type: 'memory'
            },
            data: '{subclassViewModeData}'
        },
        subclassesStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['active', 'description', 'label'],
            root: {
                expanded: true
            },
            autoDestroy: true

        }
    }

});