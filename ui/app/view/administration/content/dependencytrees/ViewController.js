Ext.define('CMDBuildUI.view.administration.content.dependencytrees.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dependencytrees-view',

    control: {
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        }
    },

    /**
     * On translate button click (button, e, eOpts) {
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, e, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDependencyTreeDescription(
                vm.get('theDependencyTree').get('name') || '.'
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theTranslation',
            vm,
            true
        );
    },

    /**
     * On delete navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        const me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === 'yes') {
                    CMDBuildUI.util.Ajax.setActionId('delete-dependencytree');
                    me.getViewModel()
                        .get('theDependencyTree')
                        .erase({
                            success: function (record, operation) {
                                const nextUrl = 'administration/dependencytrees_empty/false';
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                    'href',
                                    Ext.util.History.getToken(),
                                    nextUrl,
                                    me
                                );
                            }
                        });
                }
            },
            this
        );
    },

    /**
     * On edit navigationtree button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const vm = this.getView().getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        if (vm.get('actions.edit')) {
            this.redirectTo(
                Ext.String.format('administration/dependencytrees/{0}', vm.get('theDependencyTree.name')),
                true
            );
        } else if (vm.get('actions.add')) {
            const store = Ext.getStore('administration.MenuAdministration');
            const currentNode = store.findNode(
                'objecttype',
                CMDBuildUI.model.administration.MenuItem.types.dependencytree
            );
            Ext.getCmp('administrationNavigationTree').getViewModel().set('selected', currentNode);
            this.redirectTo('administration/dependencytrees_empty/false', true);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        const vm = this.getView().getViewModel();
        const data = this.calculateTreeData(true);

        Ext.Ajax.request({
            url: Ext.String.format(
                '{0}/domainTrees{1}',
                CMDBuildUI.util.Config.baseUrl,
                '/' + vm.get('theDependencyTree.name') + '?treeMode=tree'
            ),
            method: 'PUT',
            jsonData: data,
            success: function (response, options) {
                const record = Ext.JSON.decode(response.responseText).data;
                vm.get('theDependencyTree').set(record);
            },
            callback: function (reason) {
                if (button && button.el.dom) {
                    button.setDisabled(false);
                }
            }
        });
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        const me = this;
        const form = this.getView().getForm();
        const vm = this.getView().getViewModel();

        if (form.isValid()) {
            const theDependencyTree = vm.get('theDependencyTree');
            if (theDependencyTree.get('icon_font')) {
                theDependencyTree.set(
                    'icon_font',
                    CMDBuildUI.util.helper.IconHelper.setIconDBValue(theDependencyTree.get('icon_font'))
                );
            }

            const data = this.calculateTreeData();
            const method = theDependencyTree.crudState === 'C' ? 'POST' : 'PUT';
            Ext.Ajax.request({
                url: Ext.String.format(
                    '{0}/domainTrees{1}',
                    CMDBuildUI.util.Config.baseUrl,
                    method === 'PUT' ? '/' + theDependencyTree.get('name') + '?treeMode=tree' : ''
                ),
                method: method,
                jsonData: data,
                success: function (transport) {
                    const nextUrl = Ext.String.format(
                        'administration/dependencytrees/{0}',
                        theDependencyTree.get('name')
                    );

                    const theTranslation = vm.get('theTranslation');
                    if (theTranslation) {
                        const translationCode =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDependencyTreeDescription(
                                theDependencyTree.get('name')
                            );
                        theTranslation.set('_id', translationCode);
                        theTranslation.crudState = 'U';
                        theTranslation.crudStateWas = 'U';
                        theTranslation.phantom = false;
                        theTranslation.save();
                    }

                    const treeComponent = Ext.getCmp('administrationNavigationTree');
                    const treeComponentStore = treeComponent.getStore();
                    treeComponent.setSelection(null);

                    if (vm.get('actions.edit')) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                            'href',
                            nextUrl,
                            theDependencyTree.get('description'),
                            me
                        );
                        const selected = treeComponentStore.findNode('href', nextUrl);
                        treeComponent.setSelection(selected);
                    } else {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                            const selected = treeComponentStore.findNode('href', nextUrl);
                            treeComponent.setSelection(selected);
                        });
                        me.redirectTo(nextUrl, true);
                    }

                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                },
                callback: function (reason) {
                    theDependencyTree.set(
                        'icon_font',
                        CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(theDependencyTree.get('icon_font'))
                    );

                    if (button && button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            });
        }
    },

    privates: {
        /**
         *
         * @returns {Object}
         */
        calculateTreeData: function (changeActiveState) {
            const data = {};
            const dependencyTree = this.getViewModel().get('theDependencyTree');
            const node = this.getView().down('#domainstree').getStore().getRootNode();

            if (node.data.checked) {
                if (node.get('root')) {
                    data.description = dependencyTree.get('description') || dependencyTree.get('name');
                    data.icon_description = dependencyTree.get('icon_description');
                    data.icon_font = dependencyTree.get('icon_font');
                    data.active = changeActiveState ? !dependencyTree.get('active') : dependencyTree.get('active');
                    data.name = dependencyTree.get('name');
                    data.type = dependencyTree.get('type');
                    data._id = dependencyTree.get('_id');
                    data.initialExpansionDepth = dependencyTree.get('initialExpansionDepth');
                    data.nodes = [
                        {
                            direction: '_1',
                            domain: node.get('domain'),
                            filter: node.get('filter'),
                            nodes: this.collectAllCheckedNodes(node),
                            targetClass: node.get('targetClass'),
                            borderSource: node.get('borderSource'),
                            borderColor: node.get('borderColor'),
                            iconSource: node.get('iconSource'),
                            iconFont: node.get('iconFont'),
                            iconColorMode: node.get('iconColorMode'),
                            iconColor: node.get('iconColor'),
                            sourceLookup: node.get('sourceLookup'),
                            sourceLookupAttribute: node.get('sourceLookupAttribute')
                        }
                    ];
                }
            }

            return data;
        },

        /**
         *
         * @param {Ext.data.TreeModel} node
         * @returns
         */
        collectAllCheckedNodes: function (node) {
            const me = this;
            const nodes = [];

            Ext.Array.forEach(node.childNodes, function (child) {
                if (child.get('checked')) {
                    const _node = {
                        filter: child.get('filter'),
                        direction: child.get('direction'),
                        domain: child.get('domain'),
                        targetClass: child.get('targetClass'),
                        borderSource: child.get('borderSource'),
                        borderColor: child.get('borderColor'),
                        iconSource: child.get('iconSource'),
                        iconFont: child.get('iconFont'),
                        iconColorMode: child.get('iconColorMode'),
                        iconColor: child.get('iconColor'),
                        sourceLookup: child.get('sourceLookup'),
                        sourceLookupAttribute: child.get('sourceLookupAttribute'),
                        nodes: me.collectAllCheckedNodes(child)
                    };
                    nodes.push(_node);
                }
            });
            return nodes;
        }
    }
});
