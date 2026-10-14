Ext.define('CMDBuildUI.view.administration.content.users.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-grid',
    listen: {
        global: {
            userupdated: 'onUserUpdated',
            usercreated: 'onUserCreated'
        }
    },
    control: {
        '#': {
            sortchange: 'onSortChange',
            afterrender: 'onAfterRender',
            rowdblclick: 'onRowDblclick'
        },
        '#adduser': {
            click: 'onNewBtnClick'
        }
    },
    stores: ['localizations.Languages'],
    /**
     * @param {CMDBuildUI.view.administration.content.view.users.Grid} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view) {
        var vm = view.up('administration-content-users-view').getViewModel();
        vm.getStore("allGroups").load();
    },

    onOnlyActiveUsersChange: function (field, newValue, oldValue) {
        var vm = this.getViewModel(),
            store = vm.get("allUsers"),
            advancedFilter = store.getAdvancedFilter();

        if (newValue) {
            advancedFilter.addAttributeFilter('active', CMDBuildUI.util.helper.FiltersHelper.operators.equal, newValue);
        } else {
            advancedFilter.removeAttributeFilter('active');
        }
        store.load();
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        create: {
                            changePasswordRequired: CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.passwordrules.defaultchangepasswordfirstlogin)
                        }
                    }
                }
            }
        });
    },

    onUserUpdated: function (record) {
        var view = this.getView();
        view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('itemupdated', [this.getView(), record, this]);
    },

    /**
     * Update grid on card creation.
     * 
     * @param {CMDBuildUI.model.classes.User} record
     */
    onUserCreated: function (record) {
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
                    me.afterLoadWithPosition(store, newid, record);
                },
                scope: this,
                single: true
            }
        });
        // load store
        store.load();
    },

    onSortChange: function () {
        var view = this.getView();
        if (view.getSelection().length) {
            var store = view.getStore();
            var index = store.findExact("_id", view.getSelection()[0].get('_id'));
            var record = store.getAt(index);
            // repeat two time to avoid grid crash 
            view.getPlugin(view.getFormInRowPlugin()).view.fireEventArgs('togglerow', [view, record, index]);
        }
    },

    /**
     * 
     * @param {*} row 
     * @param {*} record 
     * @param {*} element 
     * @param {*} rowIndex 
     * @param {*} e 
     * @param {*} eOpts 
     */
    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView();
        if (record.get('_can_write')) {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            view.setSelection(record);
            container.removeAll();
            container.add({
                xtype: 'administration-content-users-card-edit',
                viewModel: {
                    links: {
                        theUser: {
                            type: 'CMDBuildUI.model.users.User',
                            id: record.get('_id')
                        }
                    },
                    data: {
                        title: Ext.String.format('{0} {1}', CMDBuildUI.locales.Locales.administration.navigation.users, (record.get('username').length) ? Ext.String.format(' - {0}', record.get('name')) : ''),
                        grid: view.ownerGrid,
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        actions: {
                            view: false,
                            edit: true,
                            add: false
                        }
                    }
                }
            });
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        if (event.getKey() === event.ENTER) {
            this.onSearchSubmit(field);
        }
    },
    /**
     * Filter grid items.
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchSubmit: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        var searchValue = vm.getData().search.value;

        var allUserStore = vm.get("allUsers");
        if (searchValue) {
            allUserStore.getAdvancedFilter().addQueryFilter(searchValue);
            allUserStore.load();
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
        // clear store filter
        var allUserStore = vm.get("allUsers");
        allUserStore.getAdvancedFilter().clearQueryFilter();
        allUserStore.load();
        // reset input
        field.reset();
    },

    privates: {
        /**
         * 
         * @param {Ext.data.Store} store 
         * @param {Numeric} newid 
         */
        afterLoadWithPosition: function (store, newid, record) {
            var view = this.getView();
            var vm = view.lookupViewModel();

            // function to expand row
            function expandRow() {
                view.expandRowAfterLoadWithPosition(store, newid);
                Ext.asap(function () {
                    view.updateRowWithExpader(record);
                });
                var extraparams = store.getProxy().getExtraParams();
                delete extraparams.positionOf;
                delete extraparams.positionOf_goToPage;
            }
            // check if item is found with filers
            var metadata = store.getProxy().getReader().metaData;
            if (metadata.positions[newid] && metadata.positions[newid].found) {
                expandRow();
            } else if (
                !CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.common.keepfilteronupdatedcard) &&
                !store.getAdvancedFilter().isEmpty()
            ) {
                var advancedFitler = store.getAdvancedFilter();
                // clear search query
                vm.set("search.value", "");
                advancedFitler.clearQueryFilter();
                // clear attributes and relations filter
                var filterslauncher = view.up().lookupReference("filterslauncher");
                if (filterslauncher) {
                    filterslauncher.clearFilter(true);
                }
                // show message to user
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showInfoMessage(CMDBuildUI.locales.Locales.common.grid.filterremoved);
                });
                // load store
                store.on({
                    load: {
                        fn: function () {
                            var meta = store.getProxy().getReader().metaData;
                            if (meta.positions[newid] && meta.positions[newid].found) {
                                expandRow();
                            } else {
                                // show not found message to user
                                Ext.asap(function () {
                                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                                });
                            }
                        },
                        scope: this,
                        single: true
                    }
                });
                store.load();
            } else {
                // show not found message to user
                Ext.asap(function () {
                    CMDBuildUI.util.Notifier.showWarningMessage(CMDBuildUI.locales.Locales.common.grid.itemnotfound);
                });
            }
        }
    }

});