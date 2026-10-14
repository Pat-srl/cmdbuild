Ext.define('CMDBuildUI.view.administration.content.gis.gismenus.treepanels.DestinationPanelModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-gismenus-treepanels-destinationpanel',
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
                    var root = Ext.create('CMDBuildUI.model.gis.GisMenuItem', {
                        _id: record._id,
                        id: record._id,
                        expanded: true,
                        menutype: record.menuType,
                        index: record.index || 0,
                        objecttype: record.objectTypeName,
                        text: '', // record.description,
                        leaf: false,
                        root: true,
                        children: [] // record.children && record.children.length > 0 ? me.getRecordsAsSubmenu(record.children, record.menuType, record) : []
                    });

                    if (record.children && record.children.length > 0) {
                        root.appendChild(me.getRecordsAsSubmenu(record.children, record.menuType, record));
                    }
                    var currents = me.get('currents');
                    var ostore = me.getView()
                        .up('administration-content-gismenus-view')
                        .down('administration-content-gismenus-treepanels-originpanel')
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

            record.menutype = record.menuType;
            if (record.objectTypeName && record.menutype !== 'folder') {
                var currents = this.get('currents.' + record.menutype) || [];
                currents.push(record.objectTypeName);
                this.set('currents.' + record.menutype, currents);
            }

            var text = record.menuType !== 'folder' ? Ext.String.format('{0} ({1})', record.objectDescription, CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.objectTypeName.split('.')[0])) : record.objectDescription;

            var menuitem = {
                _id: record._id,
                id: record._id,
                menutype: record.menuType,
                index: i || record.index || 0,
                objecttype: record.objectTypeName,
                objectTypeName: record.objectTypeName,
                _actualDescription: record._actualDescription,
                _targetDescription: record._targetDescription,
                text: text,
                objectdescription: record.objectDescription, //
                objectDescription: record.objectDescription, //Ext.String.format('{0} ({1})',record.objectDescription, CMDBuildUI.util.helper.ModelHelper.getObjectDescription(record.objectTypeName.split('.')[0])),
                leaf: record.menutype !== 'folder' ? true : false,
                _forAdmin: true,
                expanded: true,
                parentId: parent._id,
                children: record.children && record.children.length > 0 ? me.getRecordsAsSubmenu(record.children, menutype, record) : [],
                iconCls: record.menutype !== 'folder' ? CMDBuildUI.util.helper.IconHelper.getIconId('map-marker-alt', 'solid') : undefined
            };

            var model = Ext.create('CMDBuildUI.model.gis.GisMenuItem', menuitem);

            output.push(model);
        }
        return output;
    }
});