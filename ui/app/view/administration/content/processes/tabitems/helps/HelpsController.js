Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.HelpsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-helps-helps',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        tableview: {
            rowdblclick: 'onEditActivityItemClick'
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
        // get value
        var searchTerm = field.getValue();
        var store = this.getView().getStore();
        this.onSearchClear();
        if (searchTerm) {
            var regex = RegExp(searchTerm, 'i');
            store.filter(function (record) {
                var data = record.getData();
                var result = false;
                Ext.Object.each(data, function (property, value) {
                    result = result || regex.test(String(value));
                });

                return result;
            });
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
        // clear store filter    
        var store = this.getView().getStore();
        if (store.getFilters().length > 1) {
            store.removeFilter(store.getFilters().items[store.getFilters().length - 1]);
        }
        // reset input
        if (field) {
            field.reset();
        }
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onSearchSpecialKey: function (field, event) {
        // if (event.getKey() === event.ENTER) {
        this.onSearchSubmit(field);
        // }
    },
    onAfterRender: function (view) {
        var vm = view.getViewModel();

        vm.bind({
            bindTo: {
                activitiesWithHelpReady: '{activitiesWithHelp.isReady}',
                activitiesWithoutHelpReady: '{activitiesWithoutHelp.isReady}'
            }
        }, function (data) {
            if (data.activitiesWithHelpReady && data.activitiesWithoutHelpReady) {
                // stores are ready and filled
                // now we can do all things
                var addHelpBtn = view.down('#addBtn');
                addHelpBtn.getMenu().removeAll();
                vm.getStore('activitiesWithoutHelp').each(function (item) {
                    addHelpBtn.getMenu().add({
                        text: item.get('description') || Ext.String.format('<i>{0}</i>', CMDBuildUI.locales.Locales.administration.common.strings.unknowndescription),
                        activityId: item.get('_id'),
                        listeners: {
                            click: 'onAddActivityItemClick'
                        }
                    });
                });
            }


        });
    },

    onAddActivityItemClick: function (menuitem) {

        var grid = this.getView();
        var vm = menuitem.lookupViewModel();
        var activity = vm.getStore('activitiesStore').findRecord('_id', menuitem.activityId);
        activity.getProxy().setUrl(activity.store.getProxy().getUrl());

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        // open detail window in add mode        
        container.add({
            xtype: 'administration-content-processes-tabitems-helps-form-form',
            viewModel: {
                data: {
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.processes.helps.newhelp,
                        activity.get('description')
                    ),
                    objectTypeName: vm.get('objectTypeName'),
                    activityName: activity.getId(),
                    grid: grid,
                    theActivity: activity,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        add: true,
                        edit: false,
                        view: false
                    }
                }
            }
        });
    },

    onEditActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        if (event.type === 'dblclick') {
            record = rowIndex;
        }
        var vm = grid.lookupViewModel();
        record.getProxy().setUrl(record.store.getProxy().getUrl());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        // open detail window in edit mode
        container.add({
            xtype: 'administration-content-processes-tabitems-helps-form-form',
            viewModel: {
                data: {
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.processes.helps.help,
                        record.get('description')
                    ),
                    objectTypeName: vm.get('objectTypeName'),
                    activityName: record.getId(),
                    grid: grid.up('grid'),
                    theActivity: record,
                    actions: {
                        add: false,
                        view: true,
                        edit: false
                    }
                },
                stores: {
                    attributeGroupsStore: {
                        data: []
                    }
                }
            },
            listeners: {
                afterrender: function (view) {
                    var vm = this.getViewModel();
                    vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                    Ext.asap(function () {
                        view.items.each(function (_view) {
                            if (_view.xtype === 'administration-components-attributes-fieldsmanagement-fieldset') {
                                _view.down('administration-components-attributes-fieldsmanagement-group-group').updateGroupAndRefresh(true);
                            }
                        });
                    });
                }
            }
        });



    },


    onDeleteActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        var view = this.getView(),
            vm = view.lookupViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    var addHelpBtn = view.down('#addBtn');
                    Ext.Ajax.request({
                        url: Ext.String.format("{0}/translations/{1}", CMDBuildUI.util.Config.baseUrl, Ext.String.format('activity.{0}.{1}.instructions', vm.get('objectTypeName'), record.getId())),
                        method: 'DELETE',
                        success: function (_response) {
                            if (Ext.isEmpty(record.get('instructions'))) {
                                addHelpBtn.getMenu().items.sort('text');
                                vm.get('activitiesStore').load();
                            } else {
                                CMDBuildUI.util.Notifier.showMessage(
                                    CMDBuildUI.locales.Locales.administration.processes.texts.helpcantberemoved, {
                                    ui: 'administration',
                                    icon: CMDBuildUI.util.Notifier.icons.success
                                }
                                );
                            }
                        },
                        error: function (response) {
                            CMDBuildUI.util.Logger.log("unable to remove the default translations of the instruction", CMDBuildUI.util.Logger.levels.error);
                        }
                    });
                }
            }
        );
    },

    onOpenActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        record.getProxy().setUrl(record.store.getProxy().getUrl());

        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        // open detail window in view mode
        var vm = this.getViewModel();
        var toolActions = vm.get('toolAction');
        container.add({
            xtype: 'administration-content-processes-tabitems-helps-form-form',
            viewModel: {
                data: {
                    grid: grid.up('grid'),
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.processes.helps.help,
                        record.get('description')
                    ),
                    objectTypeName: vm.get('objectTypeName'),
                    activityName: record.getId(),
                    theActivity: record,
                    actions: {
                        add: false,
                        view: true,
                        edit: false
                    },
                    toolAction: toolActions
                }
            }
        });
    },

    onActivitiesStoreDataChanged: function (data) {
        var vm = this.getViewModel();
        vm.set('activitiesWithHelp.isReady', false);
        vm.set('activitiesWithoutHelp.isReady', false);
        vm.set('activitiesWithHelp.isReady', true);
        vm.set('activitiesWithoutHelp.isReady', true);
        data.isReady = false;
        data.isReady = true;
    }
});