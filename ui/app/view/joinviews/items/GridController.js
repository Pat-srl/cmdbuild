Ext.define('CMDBuildUI.view.joinviews.items.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.joinviews-items-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            itemcontextmenu: 'onItemContextMenu'
        },
        '#searchtext': {
            specialkey: 'onSearchSpecialKey'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#manageViewBtn': {
            click: 'onManageViewBtnClick',
            destroy: 'onDestroyTool'
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.joinviews.items.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();

        CMDBuildUI.util.helper.GridHelper.getColumnsForType(vm.get('objectType'), vm.get('objectTypeName'), {
            allowFilter: true
        }).then(function (columns) {
            view.reconfigure(null, columns);
            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
            CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        button.lookupViewModel().get('items').load();
        this.getView().setSelection();
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = field.lookupViewModel();
        // get value
        var searchTerm = field.getValue();
        if (searchTerm) {
            var store = vm.get('items');
            if (store) {
                // add filter
                store.getAdvancedFilter().addQueryFilter(searchTerm);
                store.load();
            }
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var store = vm.get('items');
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() == event.ENTER) {
            this.onSearchSubmit(field);
        }
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onSavePreferencesBtnClick: function (tool, event) {
        var view = this.getView();
        var vm = view.lookupViewModel();
        //var grid = view.lookupReference(view.referenceGridId);
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(
            view,
            tool,
            vm.get('objectType'),
            vm.get('objectTypeName')
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        var view = this.getView();
        view.lookupViewModel().bind(
            {
                isUserView: '{isUserView}'
            },
            function (data) {
                view.initContextMenu(button, false, {
                    addViewItem: false,
                    addMultiselection: !data.isUserView
                });
            }
        );
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        var format = menuitem.printformat;
        var view = this.getView();
        var store = this.getViewModel().get('items');
        var queryparams = {};

        // url and format
        var url = CMDBuildUI.util.api.Views.getPrintItemsUrl(this.getViewModel().get('objectTypeName'), format);
        queryparams.extension = format;

        // visibile columns
        var columns = view.getVisibleColumns();
        var attributes = [];
        columns.forEach(function (c) {
            if (c.attributename) {
                attributes.push(c.attributename);
            }
        });
        queryparams.attributes = Ext.JSON.encode(attributes);

        // apply sorters
        var sorters = store.getSorters().getRange();
        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        // filters
        var filter = store.getAdvancedFilter();
        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + '?' + Ext.Object.toQueryString(queryparams), format);
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onManageViewBtnClick: function (tool, event) {
        var me = this;
        var menu = Ext.create('Ext.menu.Menu', {
            autoShow: true,
            ui: 'default',
            items: [
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    text: CMDBuildUI.locales.Locales.joinviews.editview,
                    handler: function () {
                        me.onEditViewClick();
                    }
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    text: CMDBuildUI.locales.Locales.joinviews.deleteview,
                    handler: function () {
                        me.onDeleteClick();
                    }
                }
            ]
        });
        menu.alignTo(tool.el.id, 't-b?');
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Object} eOpts
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    },

    /**
     * Edit view button click
     */
    onEditViewClick: function () {
        var popup = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.filters.createviewfromclass,
            {
                xtype: 'joinviews-configuration-main',
                viewModel: {
                    data: {
                        uiContext: 'management',
                        actions: {
                            view: false,
                            edit: true,
                            add: false,
                            empty: false
                        }
                    },
                    links: {
                        theView: {
                            type: 'CMDBuildUI.model.views.ConfigurableView',
                            id: this.getViewModel().get('objectTypeName')
                        }
                    }
                },
                listeners: {
                    saved: function (mode, record, operation, eOpts) {
                        CMDBuildUI.util.Msg.confirm(
                            CMDBuildUI.locales.Locales.notifier.attention,
                            CMDBuildUI.locales.Locales.joinviews.refreshafteredit,
                            function (btnText) {
                                if (btnText === 'yes') {
                                    window.location.reload();
                                } else {
                                    popup.close();
                                }
                            }
                        );
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
                viewModel: {}
            }
        );
    },

    /**
     * Delete button click
     */
    onDeleteClick: function () {
        var me = this;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.notifier.attention,
            CMDBuildUI.locales.Locales.joinviews.deleteviewconfirm,
            function (btnText) {
                if (btnText === 'yes') {
                    CMDBuildUI.util.helper.FormHelper.startSavingForm();
                    var viewdata = CMDBuildUI.util.helper.ModelHelper.getViewFromName(
                        me.getViewModel().get('objectTypeName')
                    );
                    viewdata.erase({
                        success: function (record, operation) {
                            var typename = record.get('masterClass'),
                                type = CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(typename);
                            switch (type) {
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                    me.redirectTo('classes/' + typename + '/cards');
                                    break;
                                case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                    me.redirectTo('processes/' + typename + '/instances');
                                    break;
                            }
                            // remove node from navigation
                            var nav = CMDBuildUI.util.Navigation.getManagementNavigation();
                            if (nav) {
                                var node = nav
                                    .getStore()
                                    .findNode(
                                        'findcriteria',
                                        CMDBuildUI.model.menu.MenuItem.types.view + ':' + record.getId()
                                    );
                                if (node) {
                                    node.remove();
                                }
                            }
                        },
                        callback: function (record, operation, success) {
                            CMDBuildUI.util.helper.FormHelper.endSavingForm();
                        }
                    });
                }
            }
        );
    },

    /**
     *
     * @param {Ext.view.View} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     * @returns
     */
    onItemContextMenu: function (grid, record, item, index, e, eOpts) {
        var view = this.getView(),
            vm = grid.lookupViewModel(),
            objectType = vm.get('objectType'),
            objectTypeName = record.get('_type'),
            position = e.getXY();

        var api = Ext.apply({ _grid: grid }, CMDBuildUI.util.api.Client.getApiForContextMenu()),
            contextMenuItems = view.getContextMenuItems(grid, objectType, objectTypeName, record, api);

        if (!Ext.isEmpty(contextMenuItems)) {
            var menu_grid = new Ext.menu.Menu({
                items: contextMenuItems,
                listeners: {
                    hide: function (menu, eOpts) {
                        Ext.asap(function () {
                            menu.destroy();
                        });
                    },
                    show: function (menu, eOpts) {
                        view.onContextMenuShow();
                    }
                }
            });

            menu_grid.showAt(position);
        }

        e.stopEvent();
        return false;
    }
});
