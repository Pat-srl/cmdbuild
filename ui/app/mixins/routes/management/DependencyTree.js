Ext.define('CMDBuildUI.mixins.routes.management.DependencyTree', {
    mixinId: 'managementroutes-dependencytree-mixin',

    /**
     *
     * @param {String} dependencyTreeName
     */
    showDependencyTree: function (dependencyTreeName) {
        const me = this;

        Ext.Array.each(Ext.getStore('dependencyTrees.DependencyTrees').getRange(), function (item, index, allitems) {
            if (item.getId() == dependencyTreeName) {
                CMDBuildUI.util.DependencyTree.openPopupToSelectRoot(item, function (button) {
                    const selectedCard = button.up('#dependencyTree').down('referencecombofield').getSelection();
                    me.showTree(item, selectedCard.getData());
                });
                return false;
            }
        });
    },

    /**
     *
     * @param {string} dependencyTreeName
     * @param {String} cardId
     */
    showDependencyTreeOnCard: function (dependencyTreeName, cardId) {
        const me = this;

        Ext.Array.each(Ext.getStore('dependencyTrees.DependencyTrees').getRange(), function (item, index, allitems) {
            if (item.getId() == dependencyTreeName) {
                const rootNode = item.nodes().getRange()[0];
                const targetClass = rootNode.targetClass;
                const ecqlFilter = {
                    ecql: rootNode.ecqlFilter
                };

                Ext.Ajax.request({
                    url: Ext.String.format('{0}/classes/{1}/cards', CMDBuildUI.util.Config.baseUrl, targetClass),
                    params: {
                        filter: Ext.JSON.encode(ecqlFilter)
                    },
                    method: 'GET',
                    success: function (response) {
                        const jsonDecode = Ext.JSON.decode(response.responseText);
                        const record = Ext.Array.findBy(jsonDecode.data, function (item, index, allitems) {
                            return item._id == cardId;
                        });

                        if (record) {
                            me.showTree(item, record);
                        } else {
                            CMDBuildUI.util.Notifier.showWarningMessage(
                                CMDBuildUI.locales.Locales.dependencytree.wrongid
                            );
                        }
                    }
                });

                return false;
            }
        });
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.dependencytrees.DependencyTree} domainTree
         * @param {Object} selectedCard
         */
        showTree: function (domainTree, selectedCard) {
            const dependencyTreeName = domainTree.get('name');

            const detailWindow = CMDBuildUI.util.Navigation.getManagementDetailsWindow();
            if (detailWindow) {
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow(true);
            }

            CMDBuildUI.util.Navigation.addIntoManagementContainer('dependencytree-panel', {
                title: Ext.String.format(
                    '{0} {1}',
                    CMDBuildUI.locales.Locales.dependencytree.name,
                    domainTree.get('_description_translation') || domainTree.get('description')
                ),
                rootId: selectedCard._id,
                rootClassName: selectedCard._type,
                rootDescription: selectedCard.Description,
                viewModel: {
                    data: {
                        domainTree: domainTree
                    }
                }
            });

            // update current context
            CMDBuildUI.util.Navigation.updateCurrentManagementContext(
                CMDBuildUI.util.helper.ModelHelper.objecttypes.dependencyTree,
                dependencyTreeName
            );
            CMDBuildUI.util.Utilities.closePopup('dependencyTree');

            // fire global event objecttypechanged
            Ext.GlobalEvents.fireEventArgs('objecttypechanged', [dependencyTreeName]);
        }
    }
});
