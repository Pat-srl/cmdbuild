Ext.define('CMDBuildUI.view.filters.relations.PanelController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.filters-relations-panel',

    control: {
        '#domainsgrid': {
            rowclick: 'onDomainsGridRowClick'
        }
    },

    /**
     *
     * @param {Ext.view.Table} grid
     * @param {Ext.data.Model} record
     * @param {HTMLElement} element
     * @param {Number} rowIndex
     * @param {Ext.event.Event} e
     * @param {Object} eOpts
     */
    onDomainsGridRowClick: function (grid, record, element, rowIndex, e, eOpts) {
        const container = this.getView().down('#relselectioncontainer');
        switch (record.get('mode')) {
            case CMDBuildUI.util.helper.FiltersHelper.relationstypes.oneof:
                this.showCardsList(record);
                break;
            case CMDBuildUI.util.helper.FiltersHelper.relationstypes.fromfilter:
                this.showFilterPanel(record);
                break;
            default:
                container.hide();
        }
    },

    /**
     *
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        this.handleQueryFilter(field);
    },

    /**
     *
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        field.reset();
        this.handleQueryFilter(field);
    },

    /**
     *
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event, dom, eOpts) {
        if (event.getKey() == event.ENTER) {
            this.handleQueryFilter(field);
        }
    },

    /**
     *
     * @param {Ext.form.field.Text} field
     */
    handleQueryFilter: function (field) {
        const searchTerm = field.getValue();
        const grid = field.getGrid();
        const store = grid.getStore();

        if (searchTerm) {
            // add filter
            store.getAdvancedFilter().addQueryFilter(searchTerm);
        } else {
            //remove filter
            store.getAdvancedFilter().clearQueryFilter();
        }
        store.load();
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.domains.Filter} record
         */
        getGridId: function (record) {
            return Ext.String.format('grid{0}{1}', record.get('domain'), record.get('direction'));
        },

        /**
         *
         * @param {CMDBuildUI.model.domains.Filter} record
         */
        getFilterPanelId: function (record) {
            return Ext.String.format('filter{0}{1}', record.get('domain'), record.get('direction'));
        },

        /**
         * Show the grid for the 'one of' selection
         * @param {CMDBuildUI.model.domains.Filter} record
         */
        showCardsList: function (record) {
            const view = this.getView();
            const gridid = this.getGridId(record);
            const container = view.down('#relselectioncontainer');
            const activeitem = view.down('#' + gridid);
            const domain = Ext.getStore('domains.Domains').findRecord('_id', record.get('domain'));
            const cards = record.get('cards') || [];

            if (!activeitem) {
                const objectType = record.get('destinationIsProcess')
                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                    : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                const objectTypeName = record.get('destination');
                const isAdministration = this.getViewModel().get('isAdministrationModule');

                CMDBuildUI.util.helper.GridHelper.getColumnsForType(objectType, objectTypeName, {
                    allowFilter: true
                }).then(function (columns) {
                    const modelName = CMDBuildUI.util.helper.ModelHelper.getModelName(objectType, objectTypeName);
                    const advancedfilter = new CMDBuildUI.util.AdvancedFilter();
                    advancedfilter.applyAdvancedFilter({
                        attribute: {
                            simple: {
                                attribute: '_type',
                                operator: 'IN',
                                value:
                                    record.get('direction') === '_1'
                                        ? domain.get('destinations')
                                        : domain.get('sources')
                            }
                        }
                    });
                    const grid = container.add({
                        xtype: 'grid',
                        // Top bar
                        tbar: [
                            {
                                xtype: 'textfield',
                                name: 'search',
                                width: 250,
                                emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
                                cls: isAdministration ? 'administration-input' : 'management-input',
                                autoEl: {
                                    'data-testid': 'filters-relations-panel-searchtext'
                                },
                                listeners: {
                                    specialkey: 'onSearchSpecialKey'
                                },
                                triggers: {
                                    search: {
                                        cls: Ext.baseCSSPrefix + 'form-search-trigger',
                                        handler: 'onSearchSubmit'
                                    },
                                    clear: {
                                        cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                        handler: 'onSearchClear'
                                    }
                                },
                                localized: {
                                    emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
                                },
                                getGrid: function () {
                                    return this.up('#' + gridid);
                                }
                            }
                        ],
                        columns: columns,
                        forceFit: true,
                        loadMask: true,
                        plugins: ['gridfilters'],
                        itemId: gridid,
                        selModel: {
                            selType: 'checkboxmodel',
                            mode: 'SIMPLE'
                        },
                        store: {
                            type: record.get('destinationIsProcess') ? 'processes-instances' : 'classes-cards',
                            model: modelName,
                            autoLoad: true,
                            autoDestroy: true,
                            listeners: {
                                prefetch: function (store, records) {
                                    Ext.asap(function () {
                                        const grid = view.down('#' + gridid);
                                        const selected = grid.getSelection() || [];
                                        cards.forEach(function (s) {
                                            const rec = store.findRecord('_id', s.id);
                                            if (rec && !Ext.Array.contains(selected, rec)) {
                                                selected.push(rec);
                                            }
                                        });
                                        grid.suspendEvent('select');
                                        grid.setSelection(selected);
                                        grid.resumeEvent('select');

                                        // if current view context is administration and view mode is `view`
                                        // we need to disable grid selection
                                        if (
                                            CMDBuildUI.util.helper.SessionHelper.getViewportVM().get(
                                                'isAdministrationModule'
                                            ) &&
                                            container.lookupViewModel().get('actions.view')
                                        ) {
                                            grid.getSelectionModel().setLocked(true);
                                        }
                                    });
                                }
                            },
                            advancedFilter: advancedfilter
                        },
                        listeners: {
                            /**
                             *
                             * @param {Ext.selection.RowModel} selMod
                             * @param {Ext.data.Model} record
                             * @param {Number} index
                             * @param {Object} eOpts
                             */
                            select: function (selMod, _record, index, eOpts) {
                                const sel = record.get('cards');
                                const obj = {
                                    className: _record.get('_type'),
                                    id: _record.getId()
                                };

                                const isItemPresent = Ext.Array.some(sel, function (item, index) {
                                    return item.id == obj.id && item.className == obj.className;
                                });

                                if (!isItemPresent) {
                                    sel.push(obj);
                                }

                                record.set('cards', sel);
                            },

                            /**
                             *
                             * @param {Ext.selection.RowModel} selMod
                             * @param {Ext.data.Model} record
                             * @param {Number} index
                             * @param {Object} eOpts
                             */
                            deselect: function (selMod, _record, index, eOpts) {
                                const filteredArray = Ext.Array.filter(
                                    record.get('cards'),
                                    function (item, index, allitems) {
                                        return item.id != _record.getId() || item.className != _record.get('_type');
                                    }
                                );
                                record.set('cards', filteredArray);
                            }
                        }
                    });
                    grid.getStore().getAdvancedFilter(); // this row fix #3531
                    // set the item as active item in container
                    container.setActiveItem(gridid);
                });
            } else {
                // set the item as active item in container
                container.setActiveItem(gridid);
                container.getLayout().getActiveItem().filters.clearFilters();
            }
            container.show();
        },

        /**
         * Show the filter for the 'from selection' option
         * @param {CMDBuildUI.model.domains.Filter} record
         */
        showFilterPanel: function (record) {
            const view = this.getView();
            const panelid = this.getFilterPanelId(record);
            const activeitem = view.down('#' + panelid);
            const container = view.down('#relselectioncontainer');

            if (!activeitem) {
                const objectType = record.get('destinationIsProcess')
                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.process
                    : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass;
                const objectTypeName = record.get('destination');

                const filterPanel = {
                    xtype: 'filters-attributes-panel',
                    allowInputParameter: false,
                    itemId: panelid,
                    header: false,
                    viewModel: {
                        data: {
                            objectType: objectType,
                            objectTypeName: objectTypeName
                        },
                        links: {
                            theFilter: {
                                type: 'CMDBuildUI.model.base.Filter',
                                create: {
                                    ownerType:
                                        objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                                            ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                                            : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
                                    configuration: {
                                        attribute: record.get('filter')
                                    }
                                }
                            }
                        }
                    }
                };
                if (CMDBuildUI.util.helper.SessionHelper.getViewportVM().get('isAdministrationModule')) {
                    filterPanel.allowCurrentUser = true;
                    filterPanel.allowCurrentGroup = true;
                }
                container.add(filterPanel);
            }
            container.setActiveItem(panelid);
            container.show();
        }
    }
});
