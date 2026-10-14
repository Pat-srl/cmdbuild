Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.MainPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gismenus-mainpanel',

    control: {

        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddFolderBtnClick: function (button, e, eOpts) {
        var index;
        var vm = this.getViewModel();
        var newFolderName = vm.get('newFolderName');
        if (!newFolderName) {
            CMDBuildUI.util.Notifier.showMessage(
                CMDBuildUI.locales.Locales.administration.gismenus.strings.emptyfoldername, {
                    ui: 'administration',
                    icon: CMDBuildUI.util.Notifier.icons.error
                });
            return false;
        }
        var destination = this.lookupReference('menuTreeViewDestination');
        var destinationStore = destination.getStore();
        var selectedDestinationNode = destination.getSelectionModel().getSelected();
        if (selectedDestinationNode.length > 0) {
            index = selectedDestinationNode.items[0].childNodes.length;
        }
        var uuid = CMDBuildUI.util.Utilities.generateUUID().split('-').join('').substring(0, 24);
        var nodeModel = Ext.create('CMDBuildUI.model.gis.GisMenuItem', {
            _id: uuid,
            id: uuid,
            objectdescription: newFolderName,
            objectDescription: newFolderName,
            menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
            menuType: CMDBuildUI.model.menu.MenuItem.types.folder,
            text: newFolderName,
            expanded: true,
            children: [],
            index: index
        });
        if (selectedDestinationNode.length === 0) {
            destinationStore.getRoot().appendChild(nodeModel);
        } else {
            if (selectedDestinationNode.items[0].get('leaf')) {
                this.getParentNode(selectedDestinationNode.items[0]).insertChild(selectedDestinationNode.items[0].get('index') + 1, nodeModel);
            } else {
                selectedDestinationNode.items[0].appendChild(nodeModel);
            }
        }

        vm.set('newFolderName', '');
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRemoveItemBtnClick: function (button, e, eOpts) {
        var destination = button.up('administration-content-gismenus-mainpanel').down('#treepaneldestination');
        var store = destination.getStore();
        var selectedNode = destination.getSelectionModel().getSelection()[0];
        if (!selectedNode) {
            return;
        }
        if (!selectedNode.get('root')) {
            selectedNode.remove();
            store.sync();
        }
        var origin = button.up('administration-content-gismenus-mainpanel').down('#treepanelorigin');
        var expandedNodes = origin.getStore().queryRecords('expanded', true);
        origin.getController().generateMenu(origin, origin.lookupViewModel().get('geoattributesStore')).then(function () {
            Ext.Array.forEach(expandedNodes, function (node) {
                origin.expandNode(origin.getStore().findNode('text', node.get('text')), true);
            });
            if (selectedNode.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                origin.expandNode(origin.getStore().findNode('menutype', selectedNode.get('menutype')), true);
                var originNode = origin.getStore().findNode('objecttype', selectedNode.get('objecttype'));

                origin.ensureVisible(originNode.getPath());
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        button.setDisabled(true);

        var store = this.getView().down('administration-content-gismenus-treepanels-destinationpanel').getStore();
        var root = store.getRoot();
        var rootData = root.childNodes;
        var vm = me.getView().getViewModel();
        var theMenu = vm.get('theMenu');
        var menu = (!theMenu.isModel) ? Ext.create('CMDBuildUI.model.menu.Menu', theMenu) : theMenu;

        var rootRaw = {
            type: 'gismenu',
            group: '_default',
            children: [],
            menuType: rootData.menutype || 'root',
            objectDescription: rootData.objectdescription || 'ROOT',
            _id: menu.getId(),
            objectTypeName: rootData.objecttype
        };

        root.childNodes.forEach(function (item, index) {
            rootRaw.children.push({
                children: me.getRawTree(item.childNodes),
                menuType: item.get('menutype'),
                objectDescription: item.get('objectdescription'),
                _id: item.get('_id'),
                objectTypeName: item.get('objectTypeName')
            });
        });
        if (theMenu.phantom) {
            CMDBuildUI.util.administration.helper.AjaxHelper.createMenuForGroup(rootRaw).then(
                function (response) {
                    vm.getParent().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGisMenuUrl(response._id);
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        });
                },
                function (e) {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                    button.setDisabled(false);
                    CMDBuildUI.util.Logger.log('Menu create error', 'error');
                });
        } else {
            CMDBuildUI.util.administration.helper.AjaxHelper.updateMenuForGroup(menu.getId(), rootRaw).then(
                function (response) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGisMenuUrl(response._id);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                },
                function (e) {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                    button.setDisabled(false);
                    CMDBuildUI.util.Logger.log('Menu update error', 'error');
                });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        if (!vm.get('theMenu').phantom) {
            this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getGisMenuUrl(vm.get('theMenu._id')), true);
        } else {
            var store = Ext.getStore('administration.MenuAdministration');
            var navTreeVm = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.menu);
            navTreeVm.set('selected', currentNode);
            this.redirectTo(CMDBuildUI.util.administration.helper.ApiHelper.client.getGisMenuUrl(null), true);
        }
    },

    privates: {


        getParentNode: function (node) {
            if (node.get('leaf')) {
                return this.getParentNode(node.parentNode);
            }
            return node;
        },


        getRawTree: function (childNodes) {
            var me = this;
            var output = [];
            childNodes.forEach(function (item, index) {
                var _item = {
                    children: [],
                    menuType: item.get('menutype'),
                    objectDescription: item.get('objectdescription'),
                    _id: item.get('_id'),
                    objectTypeName: item.get('objectTypeName')
                };

                if (item.hasChildNodes()) {
                    _item.children = me.getRawTree(item.childNodes);
                }
                output.push(_item);
            });
            return output;
        }

    }
});