Ext.define('CMDBuildUI.view.administration.content.gisnavigationtrees.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gisnavigationtrees-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        'domainsClassTree': {
            checkchange: 'onCheckChange'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#addBtn': {
            click: 'onAddBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        }

    },



    /**
     * Before render
     * @param {CMDBuildUI.view.administration.content.navigationtrees.View} view
     */
    onBeforeRender: function (view) {

        var vm = view.getViewModel();
        view.up('administration-content').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.navigation.gisnavigation);
        var gisNavigation = Ext.getStore('navigationtrees.NavigationTrees').findRecord('_id', 'gisnavigation');
        if (gisNavigation) {
            vm.linkTo('theNavigationtree', {
                type: 'CMDBuildUI.model.administration.AdminNavTree',
                id: 'gisnavigation'
            });
        } else {
            vm.linkTo('theNavigationtree', {
                type: 'CMDBuildUI.model.administration.AdminNavTree',
                create: {
                    _id: 'gisnavigation',
                    name: 'gisnavigation',
                    description: 'Gisnavigation',
                    active: true
                }
            });
            vm.bind('{theNavigationtree}', function (theNavigationtree) {
                vm.set('gisnavigationactive', true);
                theNavigationtree.set('active', true);
            });
            vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
        }
    },
    /**
     * On add navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {
        this.redirectTo('administration/navigationtrees_empty/true');
    },

    /**
     * On delete navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-navigationtree');
                    me.getViewModel().get('theNavigationtree').erase({
                        success: function (record, operation) {
                            Ext.getStore('navigationtrees.NavigationTrees').load({
                                callback: function (records, loadOperation, success) {
                                    // the operation object
                                    CMDBuildUI.util.administration.helper.ConfigHelper.setConfig('org__DOT__cmdbuild__DOT__gis__DOT__navigation__DOT__enabled', false, true, me).then(function (gisnavigationactive) {
                                        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent('administration-content-gisnavigationtrees-view', {
                                            viewModel: {
                                                data: {
                                                    navigationtreesId: 'gisnavigation',
                                                    actions: {
                                                        view: true,
                                                        edit: false,
                                                        add: false
                                                    }
                                                }
                                            }
                                        });
                                    });
                                }
                            });

                        }
                    });
                }
            }, this);
    },


    /**
     * On edit navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        if (vm.get('actions.add')) {
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", 'gis');
            vmNavigation.set('selected', currentNode);
        }
        this.redirectTo('administration/gis/gisnavigation', true);
    },

    /**
     * On disable navigationtree button click {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getView().getViewModel();
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__gis__DOT__navigation__DOT__enabled').then(function (enabled) {
            CMDBuildUI.util.administration.helper.ConfigHelper.setConfig('org__DOT__cmdbuild__DOT__gis__DOT__navigation__DOT__enabled', enabled === 'false' ? true : false, true, me).then(function (gisnavigationactive) {
                vm.set('gisnavigationactive', gisnavigationactive);
                vm.set('theNavigationtree.active', gisnavigationactive);
                me.onSaveBtnClick();
            });
        });
    },


    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();
        var treeView = this.getView().down('#domainstree');

        if (form.isValid()) {
            var data = {};
            var node = treeView.getStore().getRootNode();
            CMDBuildUI.util.administration.helper.ConfigHelper.setConfig('org__DOT__cmdbuild__DOT__gis__DOT__navigation__DOT__enabled', vm.get('gisnavigationactive'), true, this).then(function (gisnavigationactive) {
                if (node.data.checked) {
                    if (node.get('root')) {
                        data.description = vm.get('theNavigationtree.description') || vm.get('theNavigationtree.name');
                        data.name = vm.get('theNavigationtree.name');
                        data.active = vm.get('gisnavigationactive');
                        data._id = vm.get('theNavigationtree._id');
                        data.nodes = [{
                            description: node.get('description'),
                            direction: '_1',
                            domain: node.get('domain'),
                            filter: node.get('filter'),
                            nodes: me.collectAllCheckedNodes(node),
                            recursionEnabled: node.get('recursionEnabled'),
                            showOnlyOne: node.get('showOnlyOne'),
                            targetClass: node.get('targetClass'),
                            subclassFilter: node.get('subclassFilter'),
                            subclassViewShowIntermediateNodes: node.get('subclassViewShowIntermediateNodes'),
                            subclassViewMode: node.get('subclassViewMode')
                        }];
                        Ext.Array.forEach(Ext.Object.getKeys(node.getData()), function (key) {
                            if (Ext.String.startsWith(key, 'subclass_')) {
                                data.nodes[0][key] = node.get(key);
                            }
                        });
                    }
                }
                var method = !vm.get('actions.add') ? 'PUT' : 'POST';

                Ext.Ajax.request({
                    url: Ext.String.format('{0}/domainTrees{1}', CMDBuildUI.util.Config.baseUrl, method === 'PUT' ? '/gisnavigation?treeMode=tree' : ''),
                    method: method,
                    jsonData: data,
                    success: function (transport) {
                        Ext.getStore('navigationtrees.NavigationTrees').load({
                            callback: function (records, operation, success) {
                                // the operation object
                                // contains all of the details of the load operation
                                vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                                me.redirectTo('administration/gis/gisnavigation', true);
                            }
                        });

                    },
                    callback: function (reason) {
                        if (button && button.el.dom) {
                            button.setDisabled(false);
                        }
                    }
                });
            });
        }
    },

    /**
     * 
     * @param {Ext.data.Model} node the node changed
     * @param {Boolean} checked the value of the checked node
     */
    onCheckChange: function (node, checked) {
        this.recursiveParentCheck(node, checked);
    },

    /**
     * privates
     */
    privates: {

        collectAllCheckedNodes: function (node) {
            var me = this;
            var nodes = [];

            Ext.Array.forEach(node.childNodes, function (child) {
                if (child.get('checked')) {
                    var _node = {
                        filter: child.get('filter'),
                        description: child.get('description'),
                        direction: child.get('direction'),
                        domain: child.get('domain'),
                        showOnlyOne: child.get('showOnlyOne'),
                        recursionEnabled: child.get('recursionEnabled'),
                        targetClass: child.get('targetClass'),
                        subclassFilter: child.get('subclassFilter'),
                        subclassViewShowIntermediateNodes: child.get('subclassViewShowIntermediateNodes'),
                        subclassViewMode: child.get('subclassViewMode'),
                        nodes: me.collectAllCheckedNodes(child)
                    };
                    Ext.Array.forEach(Ext.Object.getKeys(child.getData()), function (key) {
                        if (Ext.String.startsWith(key, 'subclass_')) {
                            _node[key] = child.get(key);
                        }
                    });
                    nodes.push(_node);
                }
            });
            return nodes;
        }
    }
});