Ext.define('CMDBuildUI.view.administration.content.menus.treepanels.DestinationPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-menus-treepanels-destinationpanel',
    data: {
        currents: {

        }
    },
    formulas: {

        getCurrentMenuData: {
            bind: {
                theMenu: '{theMenu}',
                childrens: '{theMenu.children}'
            },
            get: function (data) {
                if (data.theMenu) {
                    var me = this;

                    if ((!data.theMenu.isModel)) {
                        data.theMenu.id = undefined;
                        data.theMenu._id = undefined;
                        data.theMenu = Ext.create('CMDBuildUI.model.menu.Menu', data.theMenu);
                    }
                    var record = data.theMenu.getData();
                    var root = Ext.create('CMDBuildUI.model.menu.MenuItem', {
                        _id: record._id,
                        id: record._id,
                        expanded: true,
                        menutype: record.menuType,
                        index: record.index || 0,
                        objecttype: record.objectTypeName,
                        text: record.description,
                        leaf: false,
                        root: true,
                        originId: record.originId,
                        children: []
                    });

                    if (record.children && record.children.length > 0) {
                        root.appendChild(me.getRecordsAsSubmenu(record.children, record.menuType, record));
                    }
                    var currents = me.get('currents');
                    var ostore = me.getView()
                        .up('administration-content-menu-view')
                        .down('administration-content-menus-treepanels-originpanel')
                        .getStore();

                    ostore.clearFilter();
                    ostore.addFilter(function (item) {
                        var menutype = item.get('menutype');
                        var objecttype = item.get('objecttype');
                        var objecttypeIndex = Ext.Array.indexOf(currents[menutype], objecttype);
                        return objecttypeIndex === -1;

                    });
                    this.getView().getStore().setRoot(root);
                }
            }
        }
    },

    getRecordsAsSubmenu: function (records, menutype, parent) {
        var output = [];
        var me = this;

        for (var i = 0; i < records.length; i++) {
            var record = records[i];

            if (record.menuType === '_Report') {
                record.menuType = 'reportpdf';
            }

            record.menutype = record.menuType;
            if (record.objectTypeName && record.menutype !== 'folder') {
                var currents = this.get('currents.' + record.menutype) || [];
                currents.push(record.objectTypeName);
                this.set('currents.' + record.menutype, currents);
            }

            var menuitem = {
                _id: record._id,
                id: record._id,
                menutype: record.menuType,
                index: i || record.index || 0,
                objecttype: record.objectTypeName,
                text: record._actualDescription,
                _actualDescription: record._actualDescription,
                _targetDescription: record._targetDescription,
                objectdescription: record.objectDescription,
                objectDescription: record.objectDescription,
                leaf: record.menutype !== 'folder' ? true : false,
                _forAdmin: true,
                expanded: true,
                parentId: parent._id,
                originId: record.originId,
                children: record.children && record.children.length > 0 ? me.getRecordsAsSubmenu(record.children, menutype, record) : []
            };

            var model = Ext.create('CMDBuildUI.model.menu.MenuItem', menuitem);
            CMDBuildUI.util.Logger.log("destination menu objecttype: " + record.menuType, CMDBuildUI.util.Logger.levels.log);
            switch (record.menuType) {

                case CMDBuildUI.model.menu.MenuItem.types.klass:
                    var klass = Ext.getStore('classes.Classes').getById(record.objectTypeName);
                    if (klass && klass.get('prototype')) {
                        model.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.klassparent;
                    }
                    break;
                case CMDBuildUI.model.menu.MenuItem.types.process:
                    CMDBuildUI.util.Logger.log("destination type process", CMDBuildUI.util.Logger.levels.debug);
                    var process = Ext.getStore('processes.Processes').getById(record.objectTypeName);
                    if (process && process.get('prototype')) {
                        model.data.iconCls = CMDBuildUI.model.menu.MenuItem.icons.processparent;
                    }
                    break;
                default:
                    break;
            }

            output.push(model);
        }
        return output;
    }
});