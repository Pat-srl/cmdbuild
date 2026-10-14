Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.OriginPanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gismenus-treepanels-originpanel',


    /**
     * 
     * @param {*} node 
     * @param {*} data 
     * @param {*} overModel 
     * @param {*} dropPosition 
     * @param {*} dropHandlers 
     */
    onBeforeDrop: function (node, data, overModel, dropPosition, dropHandlers) {
        var view = this.getView();
        var geoattributesStore = this.getViewModel().getStore('geoattributesStore');
        dropHandlers.wait = true;
        dropHandlers.processDrop();

        data.records.forEach(function (record) {
            var destination = view.up('administration-content-gismenus-mainpanel').down('#treepaneldestination');
            var store = destination.getStore();

            if (!record.get('root')) {
                record.remove();
                store.sync();
            }
            view.getController().generateMenu(view, geoattributesStore).then(function () {               
                if (record.get('menutype') !== CMDBuildUI.model.menu.MenuItem.types.folder) {
                    view.expandNode(view.getStore().findNode('menutype', record.get('menutype')), true);
                    var originNode = view.getStore().findNode('objecttype', record.get('objecttype'));
                    view.ensureVisible(originNode.getPath());
                }
            });
        });
    },

    onGeoAttributesStoreloaded: function (store) {
        var me = this,
            view = me.getView();
        Ext.asap(function () {
            me.generateMenu(view, store);
        });
    },

    /** 
     * @param {*} view 
     */
    generateMenu: function (view, store) {
        var deferred = new Ext.Deferred();
        var me = this;
        var treeStore = view.getStore();
        if (!treeStore) {
            return;
        }
        treeStore.clearFilter();
        var root = treeStore.getRootNode();
        root.removeAll();
        var i = 0;
        store.getGroups().each(function (group) {
            
            var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(group._groupKey);
            root.appendChild({
                menutype: CMDBuildUI.model.menu.MenuItem.types.folder,
                // menuType: CMDBuildUI.model.menu.MenuItem.types.folder,
                index: i++,
                objecttype: CMDBuildUI.model.menu.MenuItem.types.klass,
                objectdescription: klass.get('description'), // Classes
                objectDescription: klass.get('description'), // Classes            
                _targetDescription: klass.get('description'),
                leaf: false,
                allowDrag: false,
                expanded: false,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('folder', 'solid'),
                children: me.generateChildren(group.items)
            });
        });


        deferred.resolve(true);

        return deferred.promise;
    },
    privates: {

        /**
         * 
         * @private
         */
        generateChildren: function (items) {
            var _items = [];
            var destination = this.getView().up('administration-content-gismenus-mainpanel').down('#treepaneldestination');
            var elementIsPresent = function (element) {
                return destination.getStore().getData().findBy(function (item) {
                    return item.get('objecttype') === Ext.String.format('{0}.{1}', element.get('owner_type'), element.get('name'));
                });
            };
            items.forEach(function (element, index) {
                if (!elementIsPresent(element)) {
                    var objectTypeName = Ext.String.format('{0}.{1}', element.get('owner_type'), element.get('name'));
                    var uuid = CMDBuildUI.util.Utilities.generateUUID();
                    var leaf = {
                        _id: uuid,
                        id: uuid,
                        menutype: 'geoattribute',
                        menuType: 'geoattribute',
                        index: index,
                        objecttype: objectTypeName,
                        objectType: objectTypeName,
                        text: element.get('description'),
                        objecttypename: objectTypeName,
                        objectTypeName: objectTypeName,
                        objectdescription: element.get('description'),
                        objectDescription: element.get('description'),
                        leaf: true,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid')
                    };
                    _items.push(Ext.create('CMDBuildUI.model.gis.GisMenuItem', leaf));
                }
            });
            return _items;
        }
    }
});