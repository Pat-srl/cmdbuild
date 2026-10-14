Ext.define('CMDBuildUI.view.administration.content.setup.elements.HomepagesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-setup-elements-homepages',

    formulas: {
        isDefaultConfigSelected: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__type}',
            get: function (type) {
                return type === 'default';
            }
        },
        isAiConfigSelected: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__type}',
            get: function (type) {
                return type === 'ai';
            }
        },
        classesTreeManager: {
            get: function () {
                var me = this;
                CMDBuildUI.util.Stores.loadClassesStore().then(function (classes) {
                    if (me.destroyed) {
                        return;
                    }
                    var root = me.buildClassesTreeRoot(classes);
                    me.getStore('classesStore').setRoot(root);
                    me.applySelectedClasses(me.get('theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__searchclasses'));
                });
                return null;
            }
        },
        selectedClassesManager: {
            bind: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__searchclasses}',
            get: function (searchClasses) {
                this.applySelectedClasses(searchClasses);
                return null;
            }
        }
    },

    /**
     * Mark checked=true on tree nodes whose name is in the comma separated
     * searchClasses value, checked=false otherwise. No-op if the tree hasn't
     * been built yet (root still missing).
     *
     * @param {String} searchClasses comma separated class names
     */
    applySelectedClasses: function (searchClasses) {
        var store = this.getStore('classesStore');
        var root = store.getRoot();
        if (!root || !root.childNodes || !root.childNodes.length) {
            return;
        }
        var names = searchClasses ? searchClasses.split(',') : [];
        root.cascade(function (node) {
            node.set('checked', names.indexOf(node.get('name')) > -1);
        });
    },

    /**
     * Build the root node of the classes tree.
     *
     * @param {Ext.data.Model[]} classes records from classes.Classes store
     * @return {Object} root node
     */
    buildClassesTreeRoot: function (classes) {
        var rootName = CMDBuildUI.util.helper.ModelHelper.systemClasses.class;
        var children = this.buildClassesTreeNodes(classes, rootName);
        return {
            expanded: true,
            checked: false,
            text: CMDBuildUI.locales.Locales.administration.navigation.classes,
            name: rootName,
            leaf: !children.length,
            children: children.length ? children : false
        };
    },

    /**
     * Recursively build tree nodes for classes whose parent matches parentName.
     *
     * @param {Ext.data.Model[]} classes all classes records
     * @param {String} parentName the parent class name to filter children by
     * @return {Object[]} tree nodes
     */
    buildClassesTreeNodes: function (classes, parentName) {
        var me = this;
        var children = Ext.Array.filter(classes, function (item) {
            return item.get('parent') === parentName;
        });
        children = CMDBuildUI.util.administration.helper.SortHelper.sort(children, 'description');

        return Ext.Array.map(children, function (item) {
            var subNodes = me.buildClassesTreeNodes(classes, item.get('name'));
            return {
                checked: false,
                text: item.get('description'),
                name: item.get('name'),
                leaf: !subNodes.length,
                children: subNodes.length ? subNodes : false
            };
        });
    },

    stores: {
        configTypesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: [
                {
                    value: 'default',
                    label: CMDBuildUI.locales.Locales.administration.homepages.default
                },
                {
                    value: 'ai',
                    label: CMDBuildUI.locales.Locales.administration.homepages.ai
                }
            ],
            autoDestroy: true
        },
        classesStore: {
            type: 'tree',
            proxy: {
                type: 'memory'
            },
            fields: ['name', 'text', 'checked'],
            root: {
                expanded: true
            },
            autoDestroy: true
        }
    }
});
