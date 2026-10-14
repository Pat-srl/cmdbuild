Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.DestinationPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gismenus-treepanels-destinationpanel',
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
        if (this.getView().up('administration-content-gismenus-view').getViewModel().get('actions.view')) {
            dropHandlers.cancelDrop();
        } else {
            data.records.forEach(function (record) {
                if(record.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder){
                    record.set('text', Ext.String.format('{0} ({1})', record.get('objectDescription'), CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.get('objectTypeName').split('.')[0])));               
                }
            });
            dropHandlers.processDrop();
        }
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
                if (newParent.get('children')) {
                    newParent.get('children')[data.index] = CMDBuildUI.model.gis.GisMenuItem.create(data);
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
                    translationCode: CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfLayerMenuItemDescription('gismenu', menuId)
                }
            }
        };
        // custom panel listeners
        var listeners = {
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
