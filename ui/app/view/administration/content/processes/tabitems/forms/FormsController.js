Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.forms.FormsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-processes-tabitems-forms-forms',

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
        // var store = vm.get("activitiesWithForm");
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
                activitiesWithFormReady: '{activitiesWithForm.isReady}',
                activitiesWithoutFormReady: '{activitiesWithoutForm.isReady}'
            }
        }, function (data) {
            if (data.activitiesWithFormReady && data.activitiesWithoutFormReady) {
                // stores are ready and filled
                // now we can do all things
                var addFormBtn = view.down('#addBtn');
                addFormBtn.getMenu().removeAll();
                vm.getStore('activitiesWithoutForm').each(function (item) {
                    addFormBtn.getMenu().add({
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
            xtype: 'administration-attributes-fieldsmanagement-panel',
            viewModel: {
                data: {
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.forms.newform,
                        activity.get('description')
                    ),
                    grid: grid,
                    activity: activity,
                    actions: {
                        add: true,
                        view: false,
                        edit: false
                    },
                    theForm: null
                },
                stores: {
                    attributeGroupsStore: {
                        data: []
                    }
                }
            }
        });
    },

    onEditActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        if (event.type === 'dblclick') {
            record = rowIndex;
        }

        record.getProxy().setUrl(record.store.getProxy().getUrl());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        // open detail window in edit mode
        container.add({
            xtype: 'administration-attributes-fieldsmanagement-panel',
            viewModel: {
                data: {
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.forms.activityform,
                        record.get('description')
                    ),
                    grid: grid.up('grid'),
                    activity: record,
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

    onToggleActiveActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        record.getProxy().setUrl(record.store.getProxy().getUrl());

        var newValue = (record.get('formStructure').active === false) ? true : false;
        var formStructure = record.get('formStructure');
        formStructure.active = newValue;
        record.set('formStructure', formStructure);
        record.save();
    },

    onDeleteActivityItemClick: function (grid, rowIndex, colIndex, item, event, record, row) {
        var view = this.getView();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === 'yes') {
                    record.getProxy().setUrl(record.store.getProxy().getUrl());
                    record.set('formStructure', null);
                    record.save();
                    var addFormBtn = view.down('#addBtn');

                    addFormBtn.getMenu().add({
                        text: record.get('description') || Ext.String.format('<i>{0}</i>', CMDBuildUI.locales.Locales.administration.common.strings.unknowndescription),
                        activityId: record.get('_id'),
                        listeners: {
                            click: 'onAddActivityItemClick'
                        }
                    });
                    addFormBtn.getMenu().items.sort('text');
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
            xtype: 'administration-attributes-fieldsmanagement-panel',
            viewModel: {
                data: {
                    grid: grid.up('grid'),
                    title: Ext.String.format('{0} - {1}',
                        CMDBuildUI.locales.Locales.administration.forms.activityform,
                        record.get('description')
                    ),
                    activity: record,
                    actions: {
                        add: false,
                        view: true,
                        edit: false
                    },
                    toolAction: toolActions
                },
                stores: {
                    attributeGroupsStore: {
                        data: []
                    }
                }
            }
        });
    },

    onActivitiesStoreDataChanged: function (data) {
        var vm = this.getViewModel();
        vm.set('activitiesWithForm.isReady', false);
        vm.set('activitiesWithoutForm.isReady', false);
        vm.set('activitiesWithForm.isReady', true);
        vm.set('activitiesWithoutForm.isReady', true);
        data.isReady = false;
        data.isReady = true;
    }
});