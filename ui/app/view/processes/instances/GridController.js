Ext.define('CMDBuildUI.view.processes.instances.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.processes-instances-grid',

    listen: {
        global: {
            processinstanceaborted: 'onProcessInstanceAborted',
            processinstancecreated: 'onProcessInstanceCreated',
            processinstanceupdated: 'onProcessInstanceUpdated',
            processinstanceresume: 'onRefreshBtnClick',
            objectidchanged: 'onObjectIdChanged'
        }
    },

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            rowdblclick: 'onRowDblClick',
            selectionchange: 'onSelectionChange',
            selectedidchanged: 'onSelectedIdChanged',
            itemcontextmenu: 'onItemContextMenu'
        },
        '#addbtn': {
            beforerender: 'onAddBtnBeforeRender'
        },
        '#statuscombo': {
            beforerender: 'onStatusComboBeforeRender',
            cleartrigger: 'onStatusComboClear'
        },
        '#refreshBtn': {
            click: 'onRefreshBtnClick'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view) {
        const vm = this.getViewModel();
        const objectTypeName = vm.get('objectTypeName');

        CMDBuildUI.util.helper.GridHelper.getColumnsForType(vm.get('objectType'), objectTypeName, {
            allowFilter: view.getAllowFilter()
        }).then(function (columns) {
            vm.set('isModelLoaded', true);
            columns.push(CMDBuildUI.util.helper.GridHelper.getProcessFlowStatusColumn());

            // hide selection column
            if (!view.isMultiSelectionEnabled() && view.selModel.column) {
                view.selModel.column.hide();
            }

            // reconfigure columns
            view.reconfigure(null, columns);

            CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
        });
    },

    /**
     * @param {Ext.selection.RowModel} element
     * @param {CMDBuildUI.model.classes.Card[]} record
     * @param {HTMLElement} rowIndex
     * @param {Event} e
     * @param {Object} eOpts
     */
    onRowDblClick: function (element, record, rowIndex, e, eOpts) {
        if (record.get('_tasklist') && record.get('_tasklist').length === 1) {
            const url = CMDBuildUI.util.Navigation.getProcessBaseUrl(
                record.getRecordType(),
                record.getRecordId(),
                record.get('_tasklist')[0]._id,
                'edit'
            );

            this.redirectTo(url, true);
        }
        return false;
    },

    /**
     * @param {Ext.selection.RowModel} selection
     * @param {CMDBuildUI.model.classes.Card[]} selected
     * @param {Object} eOpts
     */
    onSelectionChange: function (selection, selected, eOpts) {
        const vm = this.getViewModel();

        if (this.getView().isMainGrid()) {
            const selId = selected.length ? selected[0].getId() : null;

            //generates the right path even if ther's no selection
            // var path = this.getRouteUrl(vm.get("objectTypeName"), selId);

            const path = CMDBuildUI.util.Navigation.getProcessBaseUrl(this.getViewModel().get('objectTypeName'), selId);

            //changes the path only if managementDetailWindow is closed
            if (!CMDBuildUI.util.Navigation.getManagementDetailsWindow(false)) {
                Ext.util.History.add(path);
            }
        }
    },

    /**
     * @param {CMDBuildUI.view.classes.cards.Grid} view
     * @param {Numeric|String} newid
     * @param {Numeric|String} oldid
     */
    onSelectedIdChanged: function (view, newid, oldid) {
        const vm = this.getViewModel();

        // bind cards store to open selected card
        vm.bind(
            {
                bindTo: '{instances}'
            },
            function (store) {
                if (store && !store.isLoaded()) {
                    this.loadWithPosition(store, newid);
                }
            },
            this
        );
    },

    /**
     * @param {CMDBuildUI.store.processes.Instances} store
     * @param {number} newid
     */
    loadWithPosition: function (store, newid) {
        const me = this;
        const vm = me.getViewModel();
        const view = me.getView();

        /**
         *
         * @param {CMDBuildUI.store.processes.Instances} store
         * @param {Ext.data.operation.Read} operation
         * @param {Object} eOpts
         */
        function onFirstBeforeLoad(store, operation, eOpts) {
            const objectTypeName = this.getViewModel().get('objectTypeName');
            const klass = CMDBuildUI.util.helper.ModelHelper.getProcessFromName(objectTypeName);
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

            /**
             * @param {Object} responseData - response
             * @param {Object} responseData.meta - metadata of response
             */
            const positionFound = function (responseData) {
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
            };

            // Get position of instance without store
            Ext.Ajax.request({
                url: store.getProxy().getUrl(),
                method: 'GET',
                params: params,
                success: function (response, opts) {
                    const responseData = Ext.decode(response.responseText);
                    if (responseData.meta.positions[newid].found) {
                        positionFound(responseData);
                    } else if (
                        !CMDBuildUI.util.helper.Configurations.get(
                            CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard
                        ) &&
                        !store.getAdvancedFilter().isEmpty()
                    ) {
                        const advancedFitler = store.getAdvancedFilter();
                        // clear search query
                        vm.set('search.value', '');
                        advancedFitler.clearQueryFilter();
                        // clear attributes and relations filter
                        view.lookupReference('filterslauncher').clearFilter(true);
                        // remove status filter
                        vm.set('statuscombo.value', '__ALL__');
                        // clear filter columns
                        Ext.Array.forEach(view.getVisibleColumns(), function (item, index, allitems) {
                            const filter = item.filter;
                            if (filter && filter.active) {
                                filter.setActive(false);
                            }
                        });
                        // Delete filter
                        delete params.filter;
                        // Second Ajax without the filter
                        Ext.Ajax.request({
                            url: store.getProxy().getUrl(),
                            method: 'GET',
                            params: params,
                            success: function (response, opts) {
                                const responseData = Ext.decode(response.responseText);
                                if (responseData.meta.positions[newid].found) {
                                    positionFound(responseData);
                                } else {
                                    // Item not found
                                    Ext.asap(function () {
                                        CMDBuildUI.util.Notifier.showWarningMessage(
                                            CMDBuildUI.locales.Locales.common.grid.itemnotfound
                                        );
                                        me.redirectTo(
                                            CMDBuildUI.util.Navigation.getProcessBaseUrl(
                                                vm.get('objectTypeName'),
                                                false
                                            )
                                        );
                                        CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                                        store.load();
                                    });
                                }
                            }
                        });

                        // Remove the filter message
                        Ext.asap(function () {
                            CMDBuildUI.util.Notifier.showInfoMessage(
                                CMDBuildUI.locales.Locales.common.grid.filterremoved
                            );
                        });
                    } else {
                        // Item not found
                        Ext.asap(function () {
                            CMDBuildUI.util.Notifier.showWarningMessage(
                                CMDBuildUI.locales.Locales.common.grid.itemnotfound
                            );
                            me.redirectTo(
                                CMDBuildUI.util.Navigation.getProcessBaseUrl(vm.get('objectTypeName'), false)
                            );
                            CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
                            store.load();
                        });
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

        //only if the store has made the first load can continue
        if (store.isLoaded()) {
            store.load();
        }
    },

    /**
     *
     * @param {Number} instanceId  the id of the instance
     */
    onObjectIdChanged: function (instanceId) {
        const view = this.getView();
        const vm = this.getViewModel();

        //get instances store
        const instances = vm.get('instances');

        //get plugin
        const plugin = view.getPlugin('forminrowwidget');

        if (instanceId) {
            this.showHandler.call(this, instanceId, instances, plugin, view);
        }
    },

    /**
     * @param {Number} newId
     * @param {Est.data.Store} instances  the instances store
     * @param {*} plugin the forminrowwidget
     * @param {*} view
     */
    showHandler: function (newId, instances, plugin, view, eOpts) {
        const expandedRecords = plugin.recordsExpanded;

        //get the index in the store of the passed id
        const index = newId ? instances.find('_id', newId) : null;

        //get the record from the index
        const record = index != null ? instances.getAt(index) : null;

        switch (true) {
            //deselects all and collapse all the expanded rows
            case index == null:
                view.getSelectionModel().deselectAll();

                for (let internalId in expandedRecords) {
                    const record = expandedRecords[internalId];
                    const index = instances.indexOf(record);

                    plugin.toggleRow(index, record);
                }
                break;

            //The record is not found, have to load it first
            case index == -1:
                this.loadWithPosition(instances, newId);
                break;

            //the record is found but not expanded
            case index >= 0 && !expandedRecords[record.internalId]:
                view.ensureVisible(record);
                plugin.toggleRow(index, record);
                break;
        } //if the record is found and it is expanded, no operation is made
    },

    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        const vm = this.getViewModel();
        // get value
        const searchTerm = vm.get('search').value;
        if (searchTerm) {
            const store = vm.get('instances');
            if (store) {
                CMDBuildUI.util.Ajax.setActionId('proc.inst.search');
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
        const vm = this.getViewModel();
        const store = vm.get('instances');
        if (store) {
            CMDBuildUI.util.Ajax.setActionId('proc.inst.clearsearch');
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
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnBeforeRender: function (button, eOpts) {
        const me = this;
        const vm = button.lookupViewModel();
        this.getView().updateAddButton(
            button,
            function (item, event, eOpts) {
                me.onAddBtnClick(item, event, eOpts);
            },
            vm.get('objectTypeName'),
            vm.get('objectType')
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, eOpts) {
        CMDBuildUI.util.Ajax.setActionId('proc.inst.add');
        // var url = 'processes/' + button.objectTypeName + '/instances/new';
        const url = CMDBuildUI.util.Navigation.getProcessBaseUrl(button.objectTypeName, null, null, 'new');

        this.redirectTo(url, true);
    },

    /**
     * Update grid on instance creation.
     *
     * @param {CMDBuildUI.model.processes.Instance} record
     */
    onProcessInstanceCreated: function (record, forcereload) {
        const me = this;
        const view = this.getView();
        const store = view.getStore();

        if (!record) {
            if (forcereload) {
                store.load();
            }
            return;
        }

        const newid = record.getId();
        view.lookupViewModel().set('selectedId', record.getId());
        // update extra params to get new card position
        const extraparams = store.getProxy().getExtraParams();
        extraparams.positionOf = newid;
        extraparams.positionOf_goToPage = false;
        // add event listener. Use event listener instaed of callback
        // otherwise the load listener used within afterLoadWithPosition
        // is called at first load.
        store.on({
            load: {
                fn: function () {
                    if (CMDBuildUI.util.Ajax.getActionId() !== CMDBuildUI.util.Ajax.processStatAbort) {
                        me.afterLoadWithPosition(store, newid);
                    }
                },
                scope: this,
                single: true
            }
        });
        // load store
        store.load();
    },

    /**
     * @param {CMDBuildUI.model.processes.Instance} instance
     */
    onProcessInstanceUpdated: function (instance) {
        if (instance) {
            this.getViewModel().set('selectedId', instance.getId());
            this.getView().updateRowWithExpader(instance);
        } else {
            this.onProcessInstanceAborted();
        }
    },

    /**
     * On process instance aborted
     */
    onProcessInstanceAborted: function (record) {
        const grid = this.getView();
        if (grid.getPlugin('forminrowwidget') && record) {
            grid.getPlugin('forminrowwidget').view.fireEventArgs('itemremoved', [grid, record, this]);
        } else {
            const store = grid.getStore();
            store.load();
        }
    },

    /**
     *
     * @param {CMDBuildUI.view.processes.instances.Grid} combo
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onStatusComboBeforeRender: function (combo, event, eOpts) {
        const me = this;
        const vm = combo.lookupViewModel();
        vm.bind(
            {
                bindTo: {
                    store: '{instances}',
                    value: '{statuscombo.value}',
                    field: '{statuscombo.field}'
                }
            },
            function (data) {
                if (data.store) {
                    const view = me.getView();
                    let attr, value, baseFilter;
                    // set status filter
                    if (data.value && data.value !== '__ALL__') {
                        attr = data.field;
                        value = data.value;
                    } else if (data.value !== '__ALL__') {
                        const record = view.getOpenRunningStatusValue();
                        if (record) {
                            attr = CMDBuildUI.model.processes.Process.flowstatus.field;
                            value = record.getId();
                        }
                    }

                    // calculate filter
                    if (view.getFilter() && attr && value) {
                        // merge base filter (used for views) with status filter
                        const advancedFilter = new CMDBuildUI.util.AdvancedFilter();
                        advancedFilter.applyAdvancedFilter(view.getFilter());
                        advancedFilter.addAttributeFilter(attr, 'equal', value);
                        baseFilter = advancedFilter.encode();
                    } else if (view.getFilter()) {
                        // use base filter
                        baseFilter = view.getFilter();
                    } else if (attr && value) {
                        // use status filter
                        baseFilter = {
                            attribute: {
                                simple: {
                                    attribute: attr,
                                    operator: 'equal',
                                    value: [value]
                                }
                            }
                        };
                    }
                    if (baseFilter) {
                        data.store.getAdvancedFilter().addBaseFilter(baseFilter);
                    } else {
                        data.store.getAdvancedFilter().clearBaseFilter();
                    }
                    if (!vm.get('defaultfilter') || data.store.isLoaded()) {
                        data.store.load();
                    }
                }
            }
        );
    },

    /**
     * On status combo clear trigger
     * @param {Ext.form.field.ComboBox} combo
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onStatusComboClear: function (combo, trigger, eOpts) {
        combo.setValue(null);
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        view.lookupViewModel().get('instances').load();
        view.setSelection();
    },

    /**
     *
     * @param {Ext.menu.Item} menuitem
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        const format = menuitem.printformat;
        const store = this.getViewModel().get('instances');
        const queryparams = {};

        // url and format
        const url = CMDBuildUI.util.api.Classes.getPrintCardsUrl(this.getViewModel().get('objectTypeName'), format);
        queryparams.extension = format;

        // visibile columns
        const columns = this.getView().getVisibleColumns();
        const attributes = [];
        columns.forEach(function (c) {
            if (c.dataIndex) {
                attributes.push(c.dataIndex);
            }
        });
        queryparams.attributes = Ext.JSON.encode(attributes);

        // apply sorters
        const sorters = store.getSorters().getRange();
        if (sorters.length) {
            queryparams.sort = store.getProxy().encodeSorters(sorters);
        }

        // filters
        const filter = store.getAdvancedFilter();
        if (!(filter.isEmpty() && filter.isBaseFilterEmpty())) {
            queryparams.filter = filter.encode();
        }

        // open file in popup
        CMDBuildUI.util.Utilities.openPrintPopup(url + '?' + Ext.Object.toQueryString(queryparams), format);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button);
    },

    /**
     *
     * @param {Ext.panel.Tool} tool
     * @param {Event} event
     */
    onSavePreferencesBtnClick: function (tool, event) {
        const view = this.getView();
        const vm = view.lookupViewModel();
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(
            view,
            tool,
            vm.get('objectType'),
            vm.get('objectTypeName')
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
        const view = this.getView();
        const vm = grid.lookupViewModel();
        const objectTypeName = record.get('_type');
        const objectType = vm.get('objectType');
        const process = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(objectTypeName, objectType);
        const activity = record.get('_tasklist')[0];
        const position = e.getXY();
        let items = [
            {
                text: CMDBuildUI.locales.Locales.processes.openactivity,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                bind: {
                    hidden: '{hiddentools.open}'
                },
                handler: function () {
                    CMDBuildUI.view.processes.instances.Util.doOpenInstance(
                        objectTypeName,
                        record.getId(),
                        activity._id,
                        CMDBuildUI.mixins.DetailsTabPanel.actions.view
                    );
                }
            },
            {
                text: CMDBuildUI.locales.Locales.processes.editactivity,
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                disabled: !activity.writable,
                bind: {
                    hidden: '{hiddentools.edit}'
                },
                handler: function () {
                    CMDBuildUI.view.processes.instances.Util.doEditInstance(
                        objectTypeName,
                        record.getId(),
                        activity._id,
                        CMDBuildUI.mixins.DetailsTabPanel.actions.edit
                    );
                }
            }
        ];

        if (record.get('_tasklist').length > 1) {
            items = [];
        }
        items.push({
            text: CMDBuildUI.locales.Locales.processes.abortprocess,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            disabled: !(
                CMDBuildUI.util.helper.SessionHelper.getCurrentSession().get('rolePrivileges').admin_access ||
                process.get('stoppableByUser')
            ),
            bind: {
                hidden: '{hiddentools.delete}'
            },
            handler: function () {
                CMDBuildUI.view.processes.instances.Util.doAbortInstance(objectType, objectTypeName, record);
            }
        });

        const api = Ext.apply(
                {
                    _grid: grid,
                    abortProcess: function () {
                        this._abortProcess(record);
                    },
                    resumeProcess: function () {
                        this._resumeProcess(record);
                    }
                },
                CMDBuildUI.util.api.Client.getApiForContextMenu()
            ),
            contextMenuItems = view.getContextMenuItems(grid, objectType, objectTypeName, record, api);

        if (contextMenuItems && contextMenuItems.length) {
            items.push({
                xtype: 'menuseparator'
            });
            items = Ext.Array.merge([], items, contextMenuItems);
        }
        const menu_grid = new Ext.menu.Menu({
            items: items,
            listeners: {
                hide: function (menu, eOpts) {
                    Ext.asap(function () {
                        menu.destroy();
                    });
                },
                show: function () {
                    view.onContextMenuShow();
                }
            }
        });

        e.stopEvent();
        menu_grid.showAt(position);
        return false;
    },

    privates: {
        /**
         * @param {String} processName
         * @param {Numeric|String} instanceId
         * @param {String} activityId
         * @param {String} action
         * @return {String}
         */
        getRouteUrl: function (processName, instanceId, action) {
            let path = 'processes/' + processName + '/instances';
            if (instanceId) {
                path += '/' + instanceId;
            }
            if (action) {
                path += '/' + action;
            }
            return path;
        },

        /**
         *
         * @param {Ext.data.Store} store
         * @param {Numeric} newid
         * @param {Object} metaData
         */
        afterLoadWithPosition: function (store, newid, metaData) {
            const view = this.getView();
            const vm = view.lookupViewModel();
            // Get metaData from proxy if it'isn't loaded with Ajax
            if (!metaData) {
                metaData = store.getProxy().getReader().metaData;
            }

            // function to expand row
            function expandRow() {
                view.expandRowAfterLoadWithPosition(store, newid, null, metaData);
                const extraparams = store.getProxy().getExtraParams();
                delete extraparams.positionOf;
                delete extraparams.positionOf_goToPage;
            }

            if (metaData && metaData.positions[newid] && metaData.positions[newid].found) {
                expandRow();
            } else if (
                !CMDBuildUI.util.helper.Configurations.get(
                    CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard
                )
            ) {
                const advancedFitler = store.getAdvancedFilter();
                // clear search query
                vm.set('search.value', '');
                advancedFitler.clearQueryFilter();
                // clear attributes and relations filter
                view.lookupReference('filterslauncher').clearFilter(true);
                // remove status filter
                vm.set('statuscombo.value', '__ALL__');
                // clear filter columns
                Ext.Array.forEach(view.getVisibleColumns(), function (item, index, allitems) {
                    const filter = item.filter;
                    if (filter && filter.active) {
                        filter.setActive(false);
                    }
                });

                // add load event listener
                store.on({
                    load: {
                        fn: function () {
                            const meta = store.getProxy().getReader().metaData;
                            if (meta && meta.positions[newid] && meta.positions[newid].found) {
                                expandRow();
                            } else {
                                // show not found message to user
                                Ext.asap(function () {
                                    CMDBuildUI.util.Notifier.showWarningMessage(
                                        CMDBuildUI.locales.Locales.common.grid.itemnotfound
                                    );
                                });
                            }
                        },
                        scope: this,
                        single: true
                    }
                });

                Ext.asap(function () {
                    // show message to user
                    CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                    // load store
                    store.load();
                });
            } else {
                this.redirectTo(
                    CMDBuildUI.util.Navigation.getProcessBaseUrl(this.getViewModel().get('objectTypeName'), false)
                );
                CMDBuildUI.util.Navigation.removeManagementDetailsWindow();
            }
        }
    }
});
