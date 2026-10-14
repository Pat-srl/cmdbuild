Ext.define('CMDBuildUI.view.administration.content.menunavigationtrees.DomainsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-menunavigationtrees-domains',
    data: {
        treeRoot: null
    },
    formulas: {
        sourceClassManager: {
            bind: '{theView.masterClass}',
            get: function (masterClassName) {
                var masterClass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(masterClassName);
                if (masterClass) {
                    this.set('fieldsetTitle', Ext.String.format(CMDBuildUI.locales.Locales.joinviews.domainsof, masterClass.getTranslatedDescription()));
                    this.set('treepanelHidden', false);
                } else {
                    this.set('fieldsetTitle', CMDBuildUI.locales.Locales.joinviews.pleaseseleceavalidmasterclass);
                    this.set('treepanelHidden', true);
                }
            }
        },
        treeRoot: {
            bind: {
                targetClass: '{theNavigationtree.targetClass}',
                theNavigationtree: '{theNavigationtree}'
            },
            get: function (data) {
                var treeStore = this.getStore('treeStore');

                var root = data.theNavigationtree.nodes().count() ? data.theNavigationtree.nodes().first().getData() : null;
                if (root && !data.targetClass) {
                    this.set('theNavigationtree.targetClass', root.targetClass);
                }

                if(Ext.isEmpty(root)) {
                    root = {};
                }
                var target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.targetClass || root.targetClass);
                var targetDescription = target && target.get('description') ? target.get('description') : data.targetClass;                
                var node = {};
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
                node.filter = (typeof root.filter === 'string') ? root.filter : '';
                node.recursionEnabled = root.recursionEnabled;
                node.showOnlyOne = root.showOnlyOne;

                return node;

            }
        },
        joinTypes: {
            get: function (get) {
                return [{
                    value: CMDBuildUI.model.views.JoinViewJoin.jointypes.inner_join,
                    label: CMDBuildUI.locales.Locales.joinviews.innerjoin
                }, {
                    value: CMDBuildUI.model.views.JoinViewJoin.jointypes.outer_join,
                    label: CMDBuildUI.locales.Locales.joinviews.outerjoin
                }];
            }
        }
    },

    stores: {

        childrensClassesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            sorter: ['label'],
            autoDestroy: true
        },

        joinTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{joinTypes}',
            proxy: 'memory',
            sorters: ['label'],
            autoDestroy: true
        },

        treeStore: {
            type: 'tree',
            folderSort: true,
            proxy: {
                type: 'memory'
            },
            storeId: 'joinTreeStore',
            sorters: ['text'],
            root: '{treeRoot}',
            listeners: {                
                rootchange: 'onTreeStoreRootChange'
            }
        }
    }

});