Ext.define('CMDBuildUI.mixins.grids.ContextMenuMixin', {
    mixinId: 'grids-context-mixin',

    /**
     * Returns the grid on which apply context menu actions.
     *
     * @return {Ext.gid.Panel}
     */
    getContextMenuGrid: Ext.emptyFn,

    /**
     * Initialize context menu.
     *
     * @param {Ext.button.Button} button
     * @param {Boolean} multiselectionEnabled Set to `true` for set multiselection enabled by default
     * @param {Object} config
     * @param {Boolean} config.addViewItem Set to `false` for disable adding button create view item
     * @param {Boolean} config.addMultiselection Set to `false` for disable adding button multiselection
     */
    initContextMenu: function (button, multiselectionEnabled, config) {
        var me = this,
            vm = this.lookupViewModel(),
            bulkActionsOrViewItem = false;

        config = config = Ext.applyIf(config || {}, {
            addViewItem: this._default_add_view_item,
            addMultiselection: this._default_add_multiselection
        });

        if (multiselectionEnabled) {
            vm.set("contextmenu.multiselection.enabled", true);
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.disablemultiselection);
            vm.set("contextmenu.multiselection.icon", CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular'));
        } else {
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
            vm.set("contextmenu.multiselection.enabled", false);
            vm.set("contextmenu.multiselection.icon", CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'));
        }
        // get model object
        var objectType = vm.get("objectType"),
            objectTypeName;
        if (this.getObjectTypeName && this.getObjectTypeName()) {
            objectTypeName = this.getObjectTypeName();
        } else {
            objectTypeName = vm.get("objectTypeName");
        }
        var modelItem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
            objectTypeName,
            objectType
        );

        // get items
        var menu = [],
            menuitems = modelItem.contextMenuItems().getRange();
        menuitems.forEach(function (item) {
            if (item.get("active")) {
                var bind;
                switch (item.get("visibility")) {
                    case CMDBuildUI.model.ContextMenuItem.visibilities.one:
                        bind = {
                            disabled: '{contextmenu.disabledvone}'
                        };
                        break;
                    case CMDBuildUI.model.ContextMenuItem.visibilities.many:
                        bind = {
                            disabled: '{contextmenu.disabledvmany}'
                        };
                        break;
                }

                var executeContextMenuScript;
                if (item.get("type") === CMDBuildUI.model.ContextMenuItem.types.custom) {
                    /* jshint ignore:start */
                    var jsfn = Ext.String.format(
                        'executeContextMenuScript = function(records, api) {\n{0}\n}',
                        item.get("script")
                    );
                    try {
                        eval(jsfn);
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Error on context menu function.",
                            CMDBuildUI.util.Logger.levels.error,
                            null,
                            e
                        );
                        executeContextMenuScript = Ext.emptyFn;
                    }
                    /* jshint ignore:end */
                }

                function handler() {
                    var grid = me.getContextMenuGrid(),
                        selection = grid.getSelection(),
                        api = Ext.apply({
                            _grid: grid
                        }, CMDBuildUI.util.api.Client.getApiForContextMenu());
                    switch (item.get("type")) {
                        case CMDBuildUI.model.ContextMenuItem.types.custom:
                            try {
                                executeContextMenuScript(selection, api);
                            } catch (e) {
                                CMDBuildUI.util.Logger.log(
                                    "Error on context menu script.",
                                    CMDBuildUI.util.Logger.levels.error,
                                    null,
                                    e
                                );
                            }
                            break;
                        case CMDBuildUI.model.ContextMenuItem.types.component:
                            CMDBuildUI.util.helper.ContextMenuHelper.openCustomComponent(item, selection, grid);
                            break;
                    }

                }
                switch (item.get("type")) {
                    case CMDBuildUI.model.ContextMenuItem.types.separator:
                        menu.push({
                            xtype: 'menuseparator'
                        });
                        break;
                    case CMDBuildUI.model.ContextMenuItem.types.custom:
                    case CMDBuildUI.model.ContextMenuItem.types.component:
                        menu.push({
                            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('angle-double-right', 'solid'),
                            text: item.get("_label_translation") || item.get("label"),
                            handler: handler,
                            bind: bind
                        });
                        break;
                }
            }
        });

        // add separator if menu is not empty
        if (menu.length) {
            menu.push({
                xtype: 'menuseparator'
            });
        }

        if (config.addViewItem) {
            bulkActionsOrViewItem = true;
            // add views submenu
            var viewsStore = Ext.getStore('views.Views'),
                results = viewsStore.query("sourceClassName", objectTypeName, false, true, true).filterBy("type", CMDBuildUI.model.views.View.types.join),
                addviewitem = {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    text: CMDBuildUI.locales.Locales.joinviews.createview,
                    handler: function () {
                        me.onCreateViewClick(objectTypeName);
                    }
                };

            if (results.length) {
                var m = {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                    text: CMDBuildUI.locales.Locales.menu.views,
                    menu: []
                };
                results.getRange().forEach(function (v) {
                    m.menu.push({
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('table', 'solid'),
                        text: v.get("_description_translation"),
                        handler: function () {
                            CMDBuildUI.util.Utilities.redirectTo(Ext.String.format(
                                "views/{0}/items",
                                v.getId()
                            ));
                        }
                    });
                });
                m.menu.push({
                    xtype: 'menuseparator'
                }, addviewitem);
                menu.push(m);
            } else { // TODO: add check on permissions
                menu.push(addviewitem);
            }
        }

        // add bulk update menu item
        if (modelItem.get('_can_bulk_update')) {
            bulkActionsOrViewItem = true;
            menu.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                text: CMDBuildUI.locales.Locales.bulkactions.edit,
                handler: function (menuitem, event) {
                    // open popup
                    var popup = CMDBuildUI.util.Utilities.openPopup(
                        null,
                        CMDBuildUI.locales.Locales.bulkactions.edit,
                        {
                            xtype: 'bulkactions-edit-panel',
                            objectType: objectType,
                            objectTypeName: objectTypeName,
                            ownerGrid: me.getContextMenuGrid(),

                            closePopup: function () {
                                popup.destroy();
                            }
                        }
                    );
                },
                bind: bind = {
                    disabled: '{contextmenu.disabledbulkactions}'
                }
            });
        }
        // add bulk delete menu item
        if (modelItem.get('_can_bulk_delete')) {
            bulkActionsOrViewItem = true;
            menu.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                text: CMDBuildUI.locales.Locales.bulkactions.delete,
                handler: function () {
                    CMDBuildUI.view.bulkactions.Util.delete(me.getContextMenuGrid());
                },
                bind: bind = {
                    disabled: '{contextmenu.disabledbulkactions}'
                }
            });

        }
        // add bulk abort menu item
        if (modelItem.get('_can_bulk_abort')) {
            bulkActionsOrViewItem = true;
            menu.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                text: CMDBuildUI.locales.Locales.bulkactions.abort,
                handler: function () {
                    CMDBuildUI.view.bulkactions.Util.abort(me.getContextMenuGrid());
                },
                bind: bind = {
                    disabled: '{contextmenu.disabledbulkactions}'
                }
            });

        }

        // add separator if menu is not empty
        if (menu.length && bulkActionsOrViewItem) {
            menu.push({
                xtype: 'menuseparator'
            });
        }

        if (config.addMultiselection) {
            // add enable/disable multi-selection
            menu.push({
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'),
                text: CMDBuildUI.locales.Locales.common.grid.enamblemultiselection,
                handler: function (menuitem, eOpts) {
                    me.onMultiselectionChange(menuitem, eOpts);
                },
                bind: {
                    text: '{contextmenu.multiselection.text}',
                    iconCls: '{contextmenu.multiselection.icon}'
                }
            });
        }

        // add import/export actions
        if (modelItem.getImportExportTemplates) {
            modelItem.getAllTemplatesForImportExport().then(function (templates) {
                var btnmenu = button.getMenu();
                // add separator if menu is not empty
                if (btnmenu.items.length && (templates.import.length || templates.export.length)) {
                    btnmenu.add({
                        xtype: 'menuseparator'
                    });
                }
                if (templates.import.length) {
                    btnmenu.add({
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('upload', 'solid'),
                        text: CMDBuildUI.locales.Locales.common.grid.import,
                        handler: function (menuitem, eOpts) {
                            me.openImportPopup(modelItem, templates.import);
                        }
                    });
                }
                if (templates.export.length) {
                    btnmenu.add({
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                        text: CMDBuildUI.locales.Locales.common.grid.export,
                        handler: function (menuitem, eOpts) {
                            me.openExportPopup(modelItem, templates.export);
                        }
                    });
                }
            });
        }

        // create menu
        button.setMenu({
            xtype: 'menu',
            items: menu,
            listeners: {
                show: function () {
                    me.onContextMenuShow();
                }
            }
        });
    },

    onContextMenuShow: function () {
        var grid = this.getContextMenuGrid();

        // break context menu init if grid is empty
        if (!grid) {
            CMDBuildUI.util.Logger.log(
                Ext.String.format("getContextMenuGrid not implemented for {0}.", this.getId()),
                CMDBuildUI.util.Logger.levels.warn
            );
            return;
        }

        var vm = this.getViewModel(),
            selected = grid.getSelection().length;

        if (selected) {
            vm.set("contextmenu.disabledvone", selected > 1);
            vm.set("contextmenu.disabledvmany", false);
            vm.set("contextmenu.disabledbulkactions", selected < 2 && !grid.isSelectAllPressed);
        } else {
            vm.set("contextmenu.disabledvone", true);
            vm.set("contextmenu.disabledvmany", true);
            vm.set("contextmenu.disabledbulkactions", !grid.isSelectAllPressed);
        }
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Object} eOpts
     */
    onMultiselectionChange: function (menuitem, eOpts) {
        var vm = menuitem.lookupViewModel(),
            grid = this.getContextMenuGrid(),
            gridMap = grid.getViewModel().get("objectType") === CMDBuildUI.util.helper.ModelHelper.objecttypes.klass ? this.getContextMenuMap() : null;
        grid.setSelection(null);

        if (grid.isMultiSelectionEnabled()) {
            // set action variables
            vm.set("contextmenu.multiselection.enabled", false);
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.enamblemultiselection);
            vm.set("contextmenu.multiselection.icon", CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'));

            grid.getSelectionModel().setSelectionMode("SINGLE");
            grid.getSelectionModel().excludeToggleOnColumn = null;
            grid.selModel.column.hide();

            if (gridMap) {
                this.changeMultiSelectionMap(gridMap, "SINGLE", false);
            }

        } else {
            // set action variables
            vm.set("contextmenu.multiselection.enabled", true);
            vm.set("contextmenu.multiselection.text", CMDBuildUI.locales.Locales.common.grid.disablemultiselection);
            vm.set("contextmenu.multiselection.icon", CMDBuildUI.util.helper.IconHelper.getIconId('check-square', 'regular'));

            grid.getSelectionModel().setSelectionMode("MULTI");
            grid.getSelectionModel().excludeToggleOnColumn = 1;
            grid.selModel.column.show();

            if (gridMap) {
                this.changeMultiSelectionMap(gridMap, "SIMPLE", true);
            }
        }
    },

    /**
     *
     * @param {CMDBuildUI.model.classes.Class} item
     * @param {CMDBuildUI.model.importexports.Template[]} templates
     */
    openImportPopup: function (item, templates) {
        var me = this,
            grid = this.getContextMenuGrid(),
            popup = CMDBuildUI.util.Utilities.openPopup(
                null,
                CMDBuildUI.locales.Locales.common.grid.import,
                {
                    xtype: "importexport-import",
                    templates: templates,
                    object: item,
                    closePopup: function () {
                        popup.close();
                    },
                    refreshGrid: function (templateCode) {
                        grid.getStore().load();
                        if (templateCode === "ImportDWG") {
                            me.getViewModel().set("importDWG", true);
                        }
                    }
                }
            );
    },

    /**
     *
     * @param {CMDBuildUI.model.classes.Class} item
     * @param {CMDBuildUI.model.importexports.Template[]} templates
     */
    openExportPopup: function (item, templates) {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.common.grid.export,
            {
                xtype: "importexport-export",
                templates: templates,
                object: item,
                filter: this.getContextMenuGrid().getStore().getAdvancedFilter().encode(),
                closePopup: function () {
                    popup.close();
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.grid.Grid | CMDBuildUI.view.processes.instances.Grid | CMDBuildUI.view.joinviews.items.Grid} grid
     * @param {String} objectType 
     * @param {String} objectTypeName 
     * @param {CMDBuildUI.model.classes.Card | CMDBuildUI.model.processes.Instance} record
     * @param {Object} api 
     * @returns 
     */
    getContextMenuItems: function (grid, objectType, objectTypeName, record, api) {
        var modelItem = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType),
            menu = [],
            menuitems = modelItem.contextMenuItems().getRange(),
            supportedTypes = [
                CMDBuildUI.model.ContextMenuItem.types.custom,
                CMDBuildUI.model.ContextMenuItem.types.component
            ],
            visibilityTypes = [
                CMDBuildUI.model.ContextMenuItem.visibilities.one,
                CMDBuildUI.model.ContextMenuItem.visibilities.many
            ];

        menuitems.forEach(function (item) {
            var type = item.get('type');
            if (item.get("active") && supportedTypes.indexOf(type) > -1 && visibilityTypes.indexOf(item.get('visibility')) > -1) {
                var executeContextMenuScript;

                if (type === CMDBuildUI.model.ContextMenuItem.types.custom) {
                    /* jshint ignore:start */
                    var jsfn = Ext.String.format(
                        'executeContextMenuScript = function(records, api) {\n{0}\n}',
                        item.get("script")
                    );
                    try {
                        eval(jsfn);
                    } catch (e) {
                        CMDBuildUI.util.Logger.log(
                            "Error on context menu function.",
                            CMDBuildUI.util.Logger.levels.error,
                            null,
                            e
                        );
                        executeContextMenuScript = Ext.emptyFn;
                    }
                    /* jshint ignore:end */
                }

                var handler = function () {
                    switch (type) {
                        case CMDBuildUI.model.ContextMenuItem.types.custom:
                            try {
                                executeContextMenuScript([record], api);
                            } catch (e) {
                                CMDBuildUI.util.Logger.log(
                                    "Error on context menu script.",
                                    CMDBuildUI.util.Logger.levels.error,
                                    null,
                                    e
                                );
                            }
                            break;
                        case CMDBuildUI.model.ContextMenuItem.types.component:
                            CMDBuildUI.util.helper.ContextMenuHelper.openCustomComponent(item, [record], grid);
                            break;
                    }
                };

                menu.push({
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('angle-double-right', 'solid'),
                    text: item.get("_label_translation") || item.get("label"),
                    handler: handler
                });
            }
        });

        return menu;
    },

    privates: {
        _default_add_view_item: true,
        _default_add_multiselection: true,

        /**
         * Create view click
         *
         * @param {String} objectTypeName
         */
        onCreateViewClick: function (objectTypeName) {
            var theView = Ext.create("CMDBuildUI.model.views.ConfigurableView", {
                masterClass: objectTypeName,
                masterClassAlias: objectTypeName,
                type: CMDBuildUI.model.views.View.types.join,
                shared: false
            });
            var popup = CMDBuildUI.util.Utilities.openPopup(
                null,
                CMDBuildUI.locales.Locales.joinviews.newjoinview,
                {
                    xtype: 'joinviews-configuration-main',
                    viewModel: {
                        data: {
                            uiContext: 'management',
                            theView: theView
                        }
                    },
                    listeners: {
                        saved: function (mode, record, operation, eOpts) {
                            // add item on views store
                            var viewsStore = Ext.getStore('views.Views'),
                                storerecord = viewsStore.add(record.getData());

                            // add item on navigation menu
                            var nav = CMDBuildUI.util.Navigation.getManagementNavigation();
                            if (nav) {
                                var store = nav.getStore();
                                var allitems = store.findNode('allitemsfolder', 'root');
                                if (!allitems) {
                                    allitems = store.getRootNode().appendChild(CMDBuildUI.util.MenuStoreBuilder.getAllItemsNodeDef());
                                }
                                var allviews = allitems.findChild('allitemsfolder', CMDBuildUI.model.menu.MenuItem.types.view);
                                if (!allviews) {
                                    allviews = allitems.appendChild(CMDBuildUI.util.MenuStoreBuilder.getAllViewsNodeDef());
                                }
                                allviews.appendChild(CMDBuildUI.util.MenuStoreBuilder.getRecordsAsList(
                                    storerecord,
                                    CMDBuildUI.model.menu.MenuItem.types.view
                                ));
                            }

                            // redirect to the new view
                            CMDBuildUI.util.Utilities.redirectTo(Ext.String.format(
                                "views/{0}/items",
                                record.getId()
                            ));

                            // close popup
                            popup.close();
                        },
                        cancel: function () {
                            popup.close();
                        }
                    }
                },
                null,
                {
                    controller: {
                        control: {
                            '#': {
                                beforerender: function (view) {
                                    var vm = view.getViewModel();
                                    vm.bind('{panelTitle}', function (title) {
                                        view.setTitle(title);
                                    });
                                }
                            }
                        }
                    },
                    viewModel: {

                    }
                }
            );
        },

        /**
         * Change the modality of selection items in list and navigation tree tabs on map
         * @param {Ext.panel.Panel} grid 
         * @param {Ext.selection.Model} selectionMode 
         * @param {Boolean} enableInfoWindow 
         */
        changeMultiSelectionMap: function (grid, selectionMode, enableInfoWindow) {
            const listTabSelModel = grid.getListTab().getSelectionModel(),
                navigationTreeTabSelModel = grid.getNavigationTreeTab().getSelectionModel(),
                viewMap = grid.getViewMap();

            listTabSelModel.setSelectionMode(selectionMode);
            navigationTreeTabSelModel.setSelectionMode(selectionMode);
            listTabSelModel.deselectAll(true);
            navigationTreeTabSelModel.deselectAll(true);
            viewMap.infoWindow.element.hidden = enableInfoWindow;

            var interaction = viewMap.getOl_interaction_select(viewMap.getOlMap());
            interaction.dispatchEvent(new ol.interaction.Select.SelectEvent('select', [], []));
        }
    }
});