Ext.define('CMDBuildUI.view.events.ContainerController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.events-container',
    listen: {
        global: {
            objectidchanged: 'onObjectIdChanged'
        }
    },
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addevent': {
            click: 'onAddEvent',
            beforerender: 'onAddEventBeforeRender'
        },
        '#statuscombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#contextMenuBtn': {
            beforerender: 'onContextMenuBtnBeforeRender'
        },
        '#categorycombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#datecombo': {
            change: 'onStatusCategoryDateChange'
        },
        '#printPdfBtn': {
            click: 'onPrintBtnClick'
        },
        '#printCsvBtn': {
            click: 'onPrintBtnClick'
        },
        '#savePreferencesBtn': {
            click: 'onSavePreferencesBtnClick'
        }
    },

    /**
     * 
     * @param {*} view 
     * @param {*} eOpts 
     */
    onBeforeRender: function (view, eOpts) {
        const vm = this.getViewModel();

        vm.set("title", CMDBuildUI.locales.Locales.calendar.scheduler);

        if (Ext.Object.isEmpty(vm.get("schedules"))) {

            const preferences = CMDBuildUI.util.helper.UserPreferences.getGridPreferences(
                vm.get("objectType"),
                vm.get("objectTypeName")
            );
            // sorters
            const sorters = [];
            if (preferences && !Ext.isEmpty(preferences.defaultOrder)) {
                preferences.defaultOrder.forEach(function (o) {
                    sorters.push({
                        property: o.attribute,
                        direction: o.direction === "descending" ? "DESC" : 'ASC'
                    });
                });
            } else {
                sorters.push({
                    property: "date",
                    direction: "ASC"
                });
            }

            const schedules = Ext.create('Ext.data.BufferedStore', {
                storeId: 'scheules',
                type: 'buffered',
                model: 'CMDBuildUI.model.calendar.Event',
                autoDestroy: true,
                pageSize: 50,
                leadingBufferZone: 100,
                remoteFilter: true,
                remoteSort: true,
                sorters: sorters,
                proxy: {
                    type: 'baseproxy',
                    url: CMDBuildUI.util.api.Calendar.getEventsUrl(),
                    extraParams: {
                        // detailed: true
                    }
                }
            });

            const advancedFilter = schedules.getAdvancedFilter(),
                statusFilter = this.getStatusBaseFilter();

            advancedFilter.addAttributeFilter(
                statusFilter.attribute,
                statusFilter.operator,
                statusFilter.value,
                statusFilter.attributeId);

            schedules.setAutoLoad(true);
            vm.set("schedules", schedules);
        }

        const model = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(vm.get("objectTypeName", vm.get("objectType"))),
            dmsCategoryTypeName = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category),
            dmsCategoryType = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategoryTypeName);

        dmsCategoryType.getCategoryValues().then(function (values) {
            model.set("dmsCategories", values.getRange());
            Ext.Array.forEach(values.getRange(), function (item) {
                item.category = item.get("code");
                item._can_create = true;
            })
        });

        vm.set('datecombostoredata', [{
            value: 0,
            label: CMDBuildUI.locales.Locales.calendar.today,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.today'
            }
        }, {
            value: 7,
            label: CMDBuildUI.locales.Locales.calendar.next7days,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.next7days'
            }
        }, {
            value: 30,
            label: CMDBuildUI.locales.Locales.calendar.next30days,
            localized: {
                label: 'CMDBuildUI.locales.Locales.calendar.next30days'
            }
        }]);

        CMDBuildUI.util.helper.GridHelper.setIconGridPreferences(view);
    },

    /**
     * 
     * @param {*} eventId 
     */
    onObjectIdChanged: function (eventId) {
        this.getViewModel().set('selectedId', eventId);
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onAddEvent: function () {
        this.redirectTo(
            CMDBuildUI.util.Navigation.getScheduleBaseUrl(
                null,
                CMDBuildUI.mixins.DetailsTabPanel.actions.create
            ));
    },

    /**
     * 
     * @param {*} button 
     * @param {*} eopts 
     */
    onAddEventBeforeRender: function (button, eopts) {
        const session = CMDBuildUI.util.helper.SessionHelper.getCurrentSession(),
            disabled = !session.get('rolePrivileges').calendar_event_create;
        button.setDisabled(disabled);
    },

    /**
    * Filter grid items.
    * @param {Ext.form.field.Text} field
    * @param {Ext.form.trigger.Trigger} trigger
    * @param {Object} eOpts
    */
    onSearchSubmit: function (field, trigger, eOpts) {
        // get value
        const searchTerm = field.getValue();
        if (searchTerm) {
            // add filter
            const store = this.getViewModel().get("schedules");
            store.getAdvancedFilter().addQueryFilter(searchTerm);
            store.load();
        } else {
            this.onSearchClear(field);
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Object} eOpts
     */
    onContextMenuBtnBeforeRender: function (button, eOpts) {
        this.getView().initContextMenu(button, null, {
            addViewItem: false
        });
    },

    /**
     * 
     * @returns 
     */
    onStatusCategoryDateChange: function () {
        const store = this.getViewModel().get("schedules");

        if (!store) return;
        const advancedFilter = store.getAdvancedFilter(),
            statusFilter = this.getStatusBaseFilter();
        advancedFilter.removeAttributeFilter('status', 'statusFilter');
        if (statusFilter) {
            advancedFilter.addAttributeFilter(
                statusFilter.attribute,
                statusFilter.operator,
                statusFilter.value,
                statusFilter.attributeId)
        }

        const categoryFilter = this.getCategoryBaseFilter();
        advancedFilter.removeAttributeFilter('category', 'categoryFilter');
        if (categoryFilter) {
            advancedFilter.addAttributeFilter(
                categoryFilter.attribute,
                categoryFilter.operator,
                categoryFilter.value,
                categoryFilter.attributeId)
        }

        const dateFilter = this.getDateBaseFilter();
        advancedFilter.removeAttributeFilter('date', 'dateFilter');
        if (dateFilter) {
            advancedFilter.addAttributeFilter(
                dateFilter.attribute,
                dateFilter.operator,
                dateFilter.value,
                dateFilter.attributeId)
        }
        store.load();
    },

    /**
     * 
     * @returns 
     */
    getStatusBaseFilter: function () {
        const statusValue = this.getView().down('#statuscombo').getValue();

        if (!statusValue) {
            return {
                attributeId: 'statusFilter',
                attribute: 'status',
                operator: 'in',
                value: ['active', 'expired']
            };
        } else {
            return {
                attributeId: 'statusFilter',
                attribute: 'status',
                operator: 'equal',
                value: statusValue
            }
        }
    },

    /**
     * 
     * @returns 
     */
    getCategoryBaseFilter: function () {
        const store = this.getViewModel().get('schedules'),
            categoryValue = this.getView().down('#categorycombo').getValue();

        if (store) {
            if (!categoryValue) {
                return;
            } else {
                return {
                    attributeId: 'categoryFilter',
                    attribute: 'category',
                    operator: 'equal',
                    value: categoryValue
                }
            }
        }
    },

    /**
     * 
     * @returns 
     */
    getDateBaseFilter: function () {
        const store = this.getViewModel().get('schedules'),
            dateValue = this.getView().down('#datecombo').getValue();

        if (store) {
            if (!dateValue) {
                return;
            } else {
                const nowDate = new Date(),
                    futureDate = Ext.Date.add(nowDate, Ext.Date.DAY, dateValue);

                return {
                    attributeId: 'dateFilter',
                    attribute: 'date',//cahnge
                    operator: 'between',
                    value: [
                        Ext.Date.format(nowDate, 'c'),
                        Ext.Date.format(futureDate, 'c')
                    ]
                }
            }
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        // clear store filter
        const store = this.getViewModel().get('schedules');
        store.getAdvancedFilter().clearQueryFilter();
        store.load();
        // reset input
        field.reset();
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
     * @param {Ext.button.Button} button 
     * @param {Event} event 
     * @param {Object} eOpts 
     */
    onRefreshBtnClick: function (button, event, eOpts) {
        this.getViewModel().get("schedules").load();
        this.getView().down('events-grid').setSelection();
    },

    /**
     * 
     * @param {Ext.menu.Item} menuitem 
     * @param {Ext.event.Event} event 
     * @param {Object} eOpts 
     */
    onPrintBtnClick: function (menuitem, event, eOpts) {
        const format = menuitem.printformat,
            view = this.getView(),
            store = this.getViewModel().get('schedules'),
            queryparams = {};

        // url and format
        const url = CMDBuildUI.util.api.Calendar.getPrintCalendarsUrl(format);
        queryparams.extension = format;

        // visibile columns
        const columns = view.getContextMenuGrid().getVisibleColumns();
        var attributes = [];
        columns.forEach(function (c) {
            if (c.initialConfig.text) {
                attributes.push(c.initialConfig.dataIndex);
            }
        });
        attributes = Ext.Array.difference(attributes, ['Type', 'missingDays', 'completion']);
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
        CMDBuildUI.util.Utilities.openPrintPopup(url + "?" + Ext.Object.toQueryString(queryparams), format);
    },

    /**
    * 
    * @param {Ext.panel.Tool} tool 
    * @param {Event} event 
    */
    onSavePreferencesBtnClick: function (tool, event) {
        const view = this.getView(),
            vm = view.lookupViewModel(),
            grid = view.getContextMenuGrid();
        CMDBuildUI.util.helper.GridHelper.saveGridPreferences(grid, tool, vm.get("objectType"), vm.get("objectTypeName"));
    }

});
