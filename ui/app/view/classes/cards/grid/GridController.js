Ext.define('CMDBuildUI.view.classes.cards.grid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.classes-cards-grid-grid',

    listen: {
        global: {
            cardcreated: 'onCardCreated',
            carddeleted: 'onCardDeleted',
            cardupdated: 'onCardUpdated',
            objectidchanged: 'onObjectIdChanged'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            selectionchange: 'onSelectionChange',
            select: 'onSelect',
            deselect: 'onDeselect',
            reload: 'onReload',
            rowdblclick: 'onRowDblClick',
            show: 'onShow',
            itemcontextmenu: 'onItemContextMenu'
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selectionModel
     * @param {Ext.data.Model} selected
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (selectionModel, selected, index, eOpts) {
        if (selectionModel.selectionMode === 'MULTI') {
            this.modifySelection(selected, false);
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selectionModel
     * @param {Ext.data.Model} deselected
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (selectionModel, deselected, index, eOpts) {
        if (selectionModel.selectionMode === 'MULTI') {
            this.modifySelection(deselected, true);
        }
    },

    /**
     * Update grid on card update.
     *
     * @param {CMDBuildUI.model.classes.Card} record
     */
    onCardUpdated: function (record) {
        this.getView().updateRowWithExpader(record);
    },

    /**
     * Update grid on card creation.
     *
     * @param {CMDBuildUI.model.classes.Card} record
     */
    onCardCreated: function (record) {
        var me = this;
        var view = this.getView();
        var store = view.getStore();
        var newid = record.getId();
        // update extra params to get new card position
        var extraparams = store.getProxy().getExtraParams();
        extraparams.positionOf = newid;
        extraparams.positionOf_goToPage = false;
        // add event listener. Use event listener instaed of callback
        // otherwise the load listener used within afterLoadWithPosition
        // is called at first load.
        store.on({
            load: {
                fn: function () {
                    me.afterLoadWithPosition(store, newid);
                },
                scope: this,
                single: true
            }
        });
        // load store
        store.load();
    },

    /**
     * Update grid of card deletion.
     */
    onCardDeleted: function (record) {
        var grid = this.getView();
        if (grid.getPlugin('forminrowwidget') && grid.getViewModel().get('activeView') !== 'map') {
            grid.getPlugin('forminrowwidget').view.fireEventArgs('itemremoved', [grid, record, this]);
        } else {
            var store = grid.getStore();
            store.load();
        }
    },

    onReload: function (record, action, eOpts) {
        var currentPage;
        var view = this.getView();
        var store = view.getStore();
        var selection = view.getSelection();
        var proxy = store.getProxy();

        if (action === 'edit' || action === 'delete') {
            currentPage = Math.ceil(view.getSelectionModel().getSelection()[0].internalId / store.getConfig().pageSize);
            var selection = view.getSelectionModel().getSelection()[0];
            var index = view.store.indexOf(selection);
            view.getView().deselect(selection);

            view.suspendLayouts();
            store.load({
                params: {
                    limit: store.getConfig().pageSize,
                    page: 1,
                    start: 0
                },
                callback: function (records, operation, success) {
                    view.getView().refresh();
                    view.resumeLayouts();
                },
                scope: this
            });
        } else if (action === 'add') {
            view.suspendLayouts();
            view.getStore().loadPage(1, {
                callback: function (r, o) {
                    view.getView().refresh();

                    view.resumeLayouts();
                }
            });
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        var vm = this.getViewModel();
        var objectTypeName = view.getObjectTypeName();
        if (!objectTypeName) {
            objectTypeName = vm.get('objectTypeName');
        }

        // get grid columns
        CMDBuildUI.util.helper.GridHelper.getColumnsForType(
            CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            objectTypeName,
            {
                allowFilter: view.getAllowFilter()
            }
        ).then(function (columns) {
            view.reconfigure(null, columns);
            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }
        });

        // sets the selectedId for the first time
        const selectedId = CMDBuildUI.util.Navigation.getCurrentContext().objectId;
        vm.set('selectedId', selectedId);

        vm.bind(
            {
                cards: '{cards}',
                newId: '{selectedId}'
            },
            function (data) {
                if (data.newId) {
                    this.loadWithPosition(data.cards, data.newId);
                }
            },
            this, //bind scope
            {
                //bind options
                single: true
            }
        );
    },

    /**
     * fired by it's view Model
     * @param {Object} selected
     * {
     *  type: { String }
     *  id: { String }
     *  conf: {
     *      center: true || false,
     *      zoom: true || false
     *
     *      }
     *  }
     * @param {Ext.data.Model} records the records rapresenting the geovalues of the selected card
     */
    onSelectedChange: function (selected) {
        var view = this.getView();
        var store = view.getStore();

        if (view.isVisible()) {
            if (selected.conf.toogleRow != false) {
                this.getView().expandRowAfterLoadWithPosition(store, selected.id, {
                    suppressSelectEvent: true
                });
            }
        } else {
            var selectedId = selected.id;
            // var record = store.findRecord(selected.id);
            this._ensureNodeVisible = selectedId;
        }
        this.getViewModel().set('selectedId', selected.id);
    },

    /**
     *
     * @param {*} grid
     * @param {*} eOpts
     */
    onShow: function (grid, eOpts) {
        if (this._ensureNodeVisible) {
            /**
             * This is an auxiliar function
             */
            function selectRecord() {
                var recordIndex = store.find('id', this._ensureNodeVisible);
                if (recordIndex !== -1) {
                    this.getView().expandSelection(recordIndex, store, this._ensureNodeVisible);
                    delete this._ensureNodeVisible;
                    return true;
                }
                return false;
            }

            //defines the store used in this function
            var store = grid.getStore();
            var me = this;
            //if the record is not loaded
            if (!selectRecord.call(this)) {
                Ext.asap(function () {
                    // loads the record from the server
                    var extraparams = store.getProxy().getExtraParams();
                    extraparams.positionOf = me._ensureNodeVisible;
                    extraparams.positionOf_goToPage = false;
                    extraparams.limit = store.getConfig().pageSize;
                    store.load({
                        callback: function () {
                            //expands the loaded record from the store
                            grid.expandRowAfterLoadWithPosition(grid.getStore(), me._ensureNodeVisible);
                            delete me._ensureNodeVisible;
                        },
                        scope: this
                    });
                });
            }
        }
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        var url = CMDBuildUI.util.Navigation.getClassBaseUrl(record.getRecordType(), record.getRecordId(), 'edit');
        this.redirectTo(url, true);
        return false;
    },

    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        var view = this.getView();
        if (view.isMainGrid() && !view.isMultiSelectionEnabled()) {
            var selid;
            if (selected.length) {
                selid = selected[0].getId();
            }

            //TODO: make more test due this modification
            // var path = this.getRouteUrl(this.getViewModel().get("objectTypeName"), selid);
            var path = CMDBuildUI.util.Navigation.getClassBaseUrl(this.getViewModel().get('objectTypeName'), selid);

            //changes the path only if managementDetailWindow is closed
            if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {
                Ext.util.History.add(path);
                this.getViewModel().getParent().set('selectedId', selid);
            }
        }
    },

    /**
     * @param {Ext.data.Store} store
     * @param {Int} newid
     */
    loadWithPosition: function (store, newid) {
        const me = this;
        /**
         *
         * @param {CMDBuildUI.store.classes.Cards} store
         * @param {Ext.data.operation.Read} operation
         * @param {Object} eOpts
         */
        function onFirstBeforeLoad(store, operation, eOpts) {
            // Get position of card without store
            const params = me.generateParamsForPosition(store, newid);
            Ext.Ajax.request({
                url: store.getProxy().url,
                method: 'GET',
                params: params,
                success: function (response, opts) {
                    const responseData = Ext.decode(response.responseText);
                    if (responseData.meta.positions[newid].found) {
                        // After position has been found we can load the page
                        store.load();
                        store.on({
                            load: {
                                fn: function () {
                                    me.afterLoadWithPosition(store, newid, responseData.meta);
                                },
                                scope: this,
                                single: true
                            }
                        });
                    } else {
                        if (
                            !CMDBuildUI.util.helper.Configurations.get(
                                CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard
                            ) &&
                            !store.getAdvancedFilter().isEmpty()
                        ) {
                            me.loadPositionWithoutFilter(store, newid);
                        } else {
                            Ext.asap(function () {
                                CMDBuildUI.util.Notifier.showWarningMessage(
                                    CMDBuildUI.locales.Locales.common.grid.itemnotfound
                                );
                                store.load();
                            });
                        }
                    }
                }
            });
            return false;
        }

        store.on({
            beforeload: {
                fn: onFirstBeforeLoad,
                scope: this,
                single: true
            }
        });

        if (store.isLoaded()) {
            store.load();
        }
    },

    /**
     *
     * @param {} newId
     */
    onObjectIdChanged: function (newId) {
        var view = this.getView();
        var vm = this.getViewModel();

        var cards = vm.get('cards');
        var plugin = view.getPlugin('forminrowwidget');
        var gridVisible = view.isVisible();

        if (gridVisible && cards) {
            //execute immediatly the function
            this.showHandler(newId, cards, plugin, view);
        } else {
            if (!gridVisible) {
                //executes when the component is displayed due to grid expansion bug
                view.removeListener('show', this.showHandler, this);

                view.addListener('show', this.showHandler, this, {
                    single: true,
                    args: [newId, cards, plugin]
                });
            } else if (!cards) {
                vm.bind(
                    'cards',
                    function (cards) {
                        this.onObjectIdChanged(newId);
                    },
                    this,
                    {
                        single: true
                    }
                );
            }
        }
    },

    /**
     * @param {Number} newId
     * @param {Est.data.Store} cards  the cards store
     * @param {*} index the index of the record to show
     * @param {*} plugin the forminrowwidget
     * @param {*} view
     * @param {*} eOpts
     */
    showHandler: function (newId, cards, plugin, view, eOpts) {
        if (!cards) {
            cards = this.getViewModel().get('cards');
        }

        var expandedRecords = plugin.recordsExpanded;
        var index = newId ? cards.find('_id', newId) : null;
        var record = index != null ? cards.getAt(index) : null;

        switch (true) {
            //deselects all and collapse all the expanded rows
            case index == null:
                view.getSelectionModel().deselectAll();

                for (var internalId in expandedRecords) {
                    var record = expandedRecords[internalId];
                    var index = cards.indexOf(record);

                    plugin.toggleRow(index, record);
                }
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(cards, newId);
                break;

            //the record is found but not expanded
            case index >= 0 && !expandedRecords[record.internalId]:
                view.getView().refresh(); //https://stackoverflow.com/questions/43898410/rendered-block-refreshed-at-16-rows-while-bufferedrenderer-view-size-is-46/#50376960
                view.ensureVisible(record);
                plugin.toggleRow(index, record);
                break;
        }
    },

    /**
     *
     * @param {Ext.view.View} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} item
     * @param {Number} index
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onItemContextMenu: function (grid, record, item, index, e, eOpts) {
        var me = this,
            vm = grid.lookupViewModel(),
            objectType = vm.get('objectType'),
            objectTypeName = record.get('_type'),
            gridContainer = me.getView().getGridContainer(),
            theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType),
            position = e.getXY(),
            items = [
                {
                    text: CMDBuildUI.locales.Locales.classes.cards.opencard,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    disabled: !theObject.get('_can_read'),
                    bind: {
                        hidden: '{hiddentools.open}'
                    },
                    handler: function () {
                        CMDBuildUI.view.classes.cards.Util.doOpenCard(
                            objectTypeName,
                            record.getId(),
                            CMDBuildUI.mixins.DetailsTabPanel.actions.view
                        );
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.classes.cards.modifycard,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    disabled: !theObject.get('_can_update'),
                    bind: {
                        hidden: '{hiddentools.edit}'
                    },
                    handler: function () {
                        CMDBuildUI.view.classes.cards.Util.doEditCard(
                            objectTypeName,
                            record.getId(),
                            CMDBuildUI.mixins.DetailsTabPanel.actions.edit
                        );
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.classes.cards.deletecard,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    disabled: !theObject.get('_can_delete'),
                    bind: {
                        hidden: '{hiddentools.delete}'
                    },
                    handler: function () {
                        CMDBuildUI.view.classes.cards.Util.doDeleteCard(objectType, objectTypeName, record);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.classes.cards.clone,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clone', 'regular'),
                    disabled: !theObject.get('_can_clone'),
                    bind: {
                        hidden: '{hiddentools.clone}'
                    },
                    handler: function () {
                        CMDBuildUI.view.classes.cards.Util.doCloneCard(objectTypeName, record.getId());
                    }
                }
            ];

        var api = Ext.apply({ _grid: grid }, CMDBuildUI.util.api.Client.getApiForContextMenu()),
            contextMenuItems = gridContainer.getContextMenuItems(grid, objectType, objectTypeName, record, api);

        if (contextMenuItems && contextMenuItems.length) {
            items.push({
                xtype: 'menuseparator'
            });
            items = Ext.Array.merge([], items, contextMenuItems);
        }

        var menu_grid = new Ext.menu.Menu({
            items: items,
            listeners: {
                hide: function (menu, eOpts) {
                    Ext.asap(function () {
                        menu.destroy();
                    });
                },
                show: function () {
                    gridContainer.onContextMenuShow();
                }
            }
        });

        e.stopEvent();
        menu_grid.showAt(position);
        return false;
    },

    privates: {
        /**
         * @param {String} className
         * @param {Numeric} cardId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (className, cardId, action) {
            var path = 'classes/' + className + '/cards';
            if (cardId) {
                path += '/' + cardId;
            }
            if (action) {
                path += '/' + action;
            }
            return path;
        },

        generateParamsForPosition: function (store, newid) {
            const objectTypeName = this.getViewModel().get('objectTypeName');
            const klass = CMDBuildUI.util.helper.ModelHelper.getClassFromName(objectTypeName);
            const sorters = CMDBuildUI.util.helper.GridHelper.getStoreSorters(klass);
            const attrs = Ext.Array.map(sorters, function (sorter) {
                return sorter.property;
            }).join(',');

            const params = {
                positionOf: newid,
                positionOf_goToPage: true,
                attrs: attrs
            };
            params[store.getProxy().getSortParam()] = Ext.JSON.encode(sorters);
            params[store.getProxy().getLimitParam()] = store.getPageSize();
            params[store.getProxy().getFilterParam()] = store.getAdvancedFilter().encode();
            return params;
        },

        /**
         *
         * @param {Ext.data.Store} store
         * @param {Numeric} newid
         * @param {Object} metaData
         */
        afterLoadWithPosition: function (store, newid, metaData) {
            var view = this.getView();
            var vm = view.lookupViewModel();

            // Get metaData from proxy if it'isn't loaded with Ajax
            if (!metaData) {
                metaData = store.getProxy().getReader().metaData;
            }

            if (!view.isVisible()) {
                this._ensureNodeVisible = newid;
            } else {
                // function to expand row
                function expandRow() {
                    view.expandRowAfterLoadWithPosition(store, newid, null, metaData);
                    var extraparams = store.getProxy().getExtraParams();
                    delete extraparams.positionOf;
                    delete extraparams.positionOf_goToPage;
                }

                // check if item is found with filers
                if (metaData.positions[newid] && metaData.positions[newid].found) {
                    expandRow();
                } else if (
                    !CMDBuildUI.util.helper.Configurations.get(
                        CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard
                    ) &&
                    !store.getAdvancedFilter().isEmpty()
                ) {
                    this.loadPositionWithoutFilter(store, newid);
                } else {
                    // show not found message to user
                    var extraparams = store.getProxy().getExtraParams();
                    delete extraparams.positionOf;
                    delete extraparams.positionOf_goToPage;
                    Ext.asap(function () {
                        CMDBuildUI.util.Notifier.showWarningMessage(
                            CMDBuildUI.locales.Locales.common.grid.itemnotfound
                        );
                    });
                }
            }
        },

        /**
         * Reloads the grid position for a card after clearing active filters.
         *
         * @param {Ext.data.Store} store The grid store instance.
         * @param {number|string} newid The card id to locate.
         * @returns {void}
         */
        loadPositionWithoutFilter: function (store, newid) {
            const me = this;
            const view = me.getView();
            const vm = view.lookupViewModel();
            const advancedFitler = store.getAdvancedFilter();
            // clear search query
            vm.set('search.value', '');
            advancedFitler.clearQueryFilter();
            // clear attributes and relations filter
            const filterslauncher = view.up().lookupReference('filterslauncher');
            if (filterslauncher) {
                filterslauncher.clearFilter(true);
            }
            // clear filter columns
            Ext.Array.forEach(view.getVisibleColumns(), function (item, index, allitems) {
                const filter = item.filter;
                if (filter && filter.active) {
                    filter.setActive(false);
                }
            });
            // show message to user
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
            });
            const params = me.generateParamsForPosition(store, newid);
            Ext.Ajax.request({
                url: store.getProxy().url,
                method: 'GET',
                params: params,
                success: function (response, opts) {
                    const responseData = Ext.decode(response.responseText);
                    if (responseData.meta.positions[newid].found) {
                        // After position has been found we can load the page
                        store.load();
                        store.on({
                            load: {
                                fn: function () {
                                    me.afterLoadWithPosition(store, newid, responseData.meta);
                                },
                                scope: me,
                                single: true
                            }
                        });
                    } else {
                        Ext.asap(function () {
                            CMDBuildUI.util.Notifier.showWarningMessage(
                                CMDBuildUI.locales.Locales.common.grid.itemnotfound
                            );
                            store.load();
                        });
                    }
                }
            });
        },

        /**
         * Select or deselect the record in list and navigation tree tab based on the value of deselected
         * @param {Ext.data.Model} record
         * @param {Boolean} deselected
         */
        modifySelection: function (record, deselected) {
            var mapContainer = this.getView().getGridContainer().getMapContainer(),
                id = Ext.num(record.getId());

            if (mapContainer) {
                var listTab = mapContainer.getListTab(),
                    listStore = listTab.getStore(),
                    selectionModelList = listTab.getSelectionModel(),
                    navTreeTab = mapContainer.getNavigationTreeTab(),
                    navTreeStore = navTreeTab.getStore(),
                    selectionModelNavTree = navTreeTab.getSelectionModel(),
                    recordList = listStore.findRecord('_id', id),
                    recordNavTree = navTreeStore.findRecord('_id', id);

                if (deselected) {
                    selectionModelList.deselect(recordList);
                    selectionModelNavTree.deselect(recordNavTree, true);
                } else {
                    selectionModelList.select(recordList, true);
                    selectionModelNavTree.select(recordNavTree, true, true);
                }
            }
        }
    }
});
