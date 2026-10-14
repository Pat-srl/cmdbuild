Ext.define('CMDBuildUI.view.navcontent.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.navcontent-container',

    control: {
        '#': {
            clickIconTitle: 'onClickIconTitle'
        }
    },

    listen: {
        global: {
            menunavtreeitemchanged: 'onMenuNavTreeItemChanged'
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.menu.MenuItem} currentNode
     */
    onMenuNavTreeItemChanged: function (currentNode) {
        const view = this.getView();

        if (currentNode.get('_objectid')) {
            view.setIconCls(CMDBuildUI.util.helper.IconHelper.getIconId('info-circle', 'solid'));
        } else {
            view.setIconCls();
        }

        this.getViewModel().set('currentNode', currentNode);
        this.updateView(currentNode);

        // Need to write the context because would be empty due to previous clearcontext() call
        CMDBuildUI.util.Navigation.updateCurrentManagementContext(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.navtreecontent,
            view.getNavTreeName()
        );
    },

    /**
     *
     */
    onClickIconTitle: function () {
        const currentNode = this.getViewModel().get('currentNode');

        if (currentNode.get('_objectid')) {
            CMDBuildUI.util.Navigation.addIntoManagementDetailsWindow('classes-cards-tabpanel', {
                tabtools: [],
                navTreeNode: currentNode,
                viewModel: {
                    data: {
                        objectType: CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                        objectTypeName: currentNode.get('_objecttype'),
                        objectId: currentNode.get('_objectid'),
                        action: CMDBuildUI.mixins.DetailsTabPanel.actions.view
                    }
                }
            });
        }
    },

    privates: {
        /**
         *
         * @param {*} currentNode
         */
        updateView: function (currentNode) {
            const view = this.getView();
            view.removeAll();

            // set page title
            if (currentNode.get('_objectdescription')) {
                let label = currentNode.get('_label');
                if (!label) {
                    label = CMDBuildUI.util.helper.ModelHelper.getObjectDescription(currentNode.get('_targettypename'));
                }
                view.setTitle(
                    Ext.String.format(
                        CMDBuildUI.locales.Locales.main.treenavcontenttitle,
                        label,
                        currentNode.get('_objectdescription')
                    )
                );
            } else {
                view.setTitle(currentNode.get('text'));
            }

            // add grid
            if (currentNode.get('_targettype') === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass) {
                view.add({
                    xtype: 'classes-cards-grid-container',
                    objectTypeName: currentNode.get('_targettypename'),
                    filter: CMDBuildUI.view.management.navigation.Utils.getFilterForNode(
                        currentNode.get('_navtreedef'),
                        currentNode.get('_objectid'),
                        currentNode.get('_objecttype')
                    ),
                    header: false,
                    maingrid: true
                });
            }
        }
    }
});
