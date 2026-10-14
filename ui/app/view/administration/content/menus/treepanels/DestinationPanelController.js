Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-menus-treepanels-destinationpanel',

    control: {
        '#': {
            viewready: 'onViewReady'
        }
    },


    onViewReady: function (tree) {
        var view = tree.getView(),
            dd = view.findPlugin('treeviewdragdrop');
        dd.dragZone.onBeforeDrag = function (data, e) {
            var record = view.getRecord(e.getTarget(view.itemSelector)),
                vm = view.lookupViewModel();
            if (vm.get('actions.view')) {
                return false;
            }
            return !record.get('root');
        };
    },

    /**
     * 
     * @param {*} node 
     * @param {*} data 
     * @param {*} overModel 
     * @param {*} dropPosition 
     * @param {*} dropHandlers 
     */
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        dropHandlers.wait = true;
        if (this.getView().up('administration-content-menu-view').getViewModel().get('actions.view')) {
            dropHandlers.cancelDrop();
        } else {
            dropHandlers.processDrop();
        }
    },
    /**
     * 
     * @param {*} store 
     * @param {*} records 
     * @param {*} successful 
     * @param {*} operation 
     * @param {*} model 
     * @param {*} eOpts 
     */
    onGetCurrentMenuStoreLoad: function (store, records, successful, operation, model, eOpts) {

    },
    /**
     * 
     * @param {*} node 
     * @param {*} oldParent 
     * @param {*} newParent 
     * @param {*} index 
     * @param {*} eOpts 
     */
    onGetCurrentMenuStoreNodeMove: function (node, oldParent, newParent, index, eOpts) {
        var children = oldParent.get('children');
        if (children) {
            var oldIndex = children
                .findIndex(function (item) {
                    return item.id === node.getData().id;
                });
            oldParent.get('children').splice(oldIndex, 1);
            if (newParent) {
                var data = node.getData();
                if (data.menutype === 'report') {
                    data.menutype = 'reportpdf';
                    data.objecttype = '_Report';
                } else if (data.menutype === 'dashboard') {
                    data.objecttype = '_Dashboards';
                }
                if (newParent.get('children')) {
                    newParent.get('children')[data.index] = CMDBuildUI.model.menu.MenuItem.create(data);
                }
            }
        }
    },

    /**
     * On translate button click
     * @param {*} view 
     * @param {*} rowIndex 
     * @param {*} colIndex 
     * @param {*} item 
     * @param {*} e 
     */
    onTranslateClick: function (view, rowIndex, colIndex, item, e) {

        var menuId = view.getStore().getAt(rowIndex).getId();
        var vm = view.lookupViewModel();
        var content = {
            xtype: 'administration-localization-localizecontent',
            scrollable: 'y',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    translationCode: CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfMenuItemDescription(vm.get('theMenu.name'), vm.get('device'), menuId)
                }
            }
        };
        // custom panel listeners
        var listeners = {
            setlocalesstore: function (store) {
                if (vm && vm.get) {
                    vm.set('theTranslation', store);
                    CMDBuildUI.util.Utilities.closePopup('popup-edit-menu-item-localization');
                }
            },
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('popup-edit-menu-item-localization');
            }
        };
        // create panel
        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-menu-item-localization',
            CMDBuildUI.locales.Locales.administration.common.strings.localization,
            content,
            listeners, {
                ui: 'administration-actionpanel',
                width: '450px',
                height: '450px'
            }
        );
    }
});