Ext.define('CMDBuildUI.view.map.tab.cards.ListController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.map-tab-cards-list',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender',
            selectionchange: 'onSelectionChange',
            beforedeselect: 'onBeforeDeselect',
            deselect: 'onDeselect',
            select: 'onSelect'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.lookupViewModel();

        //configure columns
        vm.bind(
            {
                objectType: '{objectType}',
                objectTypeName: '{objectTypeName}'
            },
            function (data) {
                if (data.objectType && data.objectTypeName) {
                    CMDBuildUI.util.helper.ModelHelper.getModel(data.objectType, data.objectTypeName).then(
                        function (model) {
                            if (!view.destroyed) {
                                view.reconfigure(
                                    null,
                                    CMDBuildUI.util.helper.GridHelper.getColumns(model.getFields(), {
                                        objectType: data.objectType,
                                        objectTypeName: data.objectTypeName,
                                        allowFilter: view.getAllowFilter(),
                                        reducedGrid: true
                                    })
                                );
                            }
                        }
                    );
                }
            }
        );
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view, eOpts) {
        const me = this;
        const vm = view.lookupViewModel();
        const mapViewModel = this.getView().getMapContainerView().down('map-map').getViewModel();

        //selects element
        Ext.asap(function () {
            mapViewModel.bind(
                // create the bind only when the map is ready
                '{mapCreated}',
                function (mapCreated) {
                    if (mapCreated) {
                        vm.bind(
                            {
                                store: '{cards}',
                                objectId: '{objectId}',
                                storeload: '{storeinfo.loaded}'
                            },
                            function (data) {
                                const activeView = vm.get('activeView'); // Load side grid ONLY when is on map view
                                if (data.store && data.storeload && activeView === 'map') {
                                    me.showHandler(data.store, data.objectId);
                                }
                            }
                        );
                    }
                }
            );
        });
    },

    /**
     *
     * @param {Ext.selection.RowModel} selectionModel
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onSelect: function (selectionModel, record, index, eOpts) {
        if ((selectionModel.selectionMode === 'SIMPLE' || selectionModel.selectionMode === 'SINGLE') && record) {
            this.getView().onSelectItem(record);
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selectionModel
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     */
    onDeselect: function (selectionModel, record, index, eOpts) {
        if ((selectionModel.selectionMode === 'SIMPLE' || selectionModel.selectionMode === 'SINGLE') && record) {
            this.getView().modifySelection(record, false);
        }
    },

    /**
     *
     * @param {Ext.selection.RowModel} selectionmodel
     * @param {Ext.data.Model} record
     * @param {Number} index
     * @param {Object} eOpts
     * @returns false will not deselect the record and not firing 'onDeselect' event
     * returns true will deselect the record and firing 'onDeselect' event
     */
    onBeforeDeselect: function (selectionmodel, record, index, eOpts) {
        return this.getView().lookupViewModel().get('settingsMap.selectAll') ? false : true;
    },

    /**
     *
     * @param {Ext.selection.Model} selectionModel
     * @param {Ext.data.Model[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selectionModel, selected, eOpts) {
        const vm = this.getView().getMapContainerView().getViewModel();
        if (selectionModel.selectionMode !== 'SIMPLE') {
            vm.set('objectId', selected && selected[0] ? Ext.num(selected[0].getId()) : null);
        }
    },

    /**
     *
     * @param {Ext.data.Store} cards
     * @param {Number} newId
     */
    showHandler: function (cards, newId) {
        const view = this.getView();
        const index = newId ? cards.find('_id', newId) : null;
        const record = index != null ? cards.getAt(index) : null;
        const selectionModel = view.getSelectionModel();
        // if the grid is not loaded yet the checkSelectionNode will be [null]
        const checkSelectionNode = view.down('tableview').getSelectedNodes()[0];

        switch (true) {
            //deselects all
            case index == null:
                selectionModel.deselectAll();
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(cards, newId);
                break;

            //the record is found, make it visible
            case index >= 0:
                if (selectionModel.selectionMode !== 'SIMPLE') {
                    selectionModel.select(record);
                    const options = {
                        // highlight: true, // selection glow on select
                        select: true,
                        animate: true
                    };
                    if (checkSelectionNode) {
                        // do only if the node is avaible
                        view.ensureVisible(record, options);
                    } else {
                        this.loadWithPosition(cards, newId);
                    }
                }
                break;
        }
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Number} objectId
     */
    loadWithPosition: function (store, objectId) {
        const me = this;
        const extraparams = store.getProxy().getExtraParams();
        extraparams.positionOf = objectId;
        extraparams.positionOf_goToPage = false;
        store.load({
            callback: function () {
                me.afterLoadWithPosition(store, objectId);
            }
        });
    },

    /**
     *
     * @param {Ext.data.Store} store
     * @param {Number} objectId
     */
    afterLoadWithPosition: function (store, objectId) {
        // check if item is found with filters
        const view = this.getView();
        const advancedFilter = store.getAdvancedFilter();
        const metadata = store.getProxy().getReader().metaData;

        const selectItem = function (positionInfo) {
            if (!positionInfo.pageOffset) {
                view.getSelectionModel().select(positionInfo.positionInPage, false, true); //HACK: avoid propagation
            } else {
                view.ensureVisible(positionInfo.positionInTable, {
                    select: true
                });
            }
        };

        if (metadata.positions[objectId] && metadata.positions[objectId].found) {
            const posinfo = (metadata && metadata.positions && metadata.positions[objectId]) || { positionInPage: 0 };
            selectItem(posinfo);
        } else if (
            !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard) &&
            !advancedFilter.isEmpty()
        ) {
            // clear search query
            view.lookupViewModel().set('search.value', '');
            advancedFilter.clearQueryFilter();
            const filterslauncher = view.getCardView().down('filters-launcher');
            if (filterslauncher) {
                // clear attributes and relations filter
                filterslauncher.clearFilter(true);
            }
            // clear filter columns
            Ext.Array.forEach(view.getCardGridView().getVisibleColumns(), function (item, index, allitems) {
                const filter = item.filter;
                if (filter && filter.active) {
                    filter.setActive(false);
                }
            });

            // show message to user
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
            });

            // load store
            store.load({
                callback: function () {
                    const metadata = store.getProxy().getReader().metaData;
                    if (metadata.positions[objectId] && metadata.positions[objectId].found) {
                        const posinfo = (metadata && metadata.positions && metadata.positions[objectId]) || {
                            positionInPage: 0
                        };
                        selectItem(posinfo);
                    } else {
                        // show not found message to user
                        Ext.asap(function () {
                            CMDBuildUI.util.Notifier.showWarningMessage(
                                CMDBuildUI.locales.Locales.common.grid.itemnotfound
                            );
                        });
                    }
                }
            });
        } else {
            // show not found message to user
            const extraparams = store.getProxy().getExtraParams();
            delete extraparams.positionOf;
            delete extraparams.positionOf_goToPage;
            Ext.asap(function () {
                CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
            });
        }
    }
});
