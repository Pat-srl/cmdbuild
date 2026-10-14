Ext.define('CMDBuildUI.view.administration.content.schedules.ruledefinitions.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-schedules-ruledefinitions-view',

    control: {
        '#addschedule': {
            click: 'onNewBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewBtnClick: function (item, event, eOpts) {
        var view = this.getView();
        // view.getViewModel().set('isGridHidden', false);
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-schedules-ruledefinitions-card',
            viewModel: {
                data: {
                    grid: item.up('administration-maincontainer').down('administration-content-schedules-ruledefinitions-grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                },
                links: {
                    theSchedule: {
                        type: 'CMDBuildUI.model.calendar.Trigger',
                        create: true
                    }
                }
            }
        });
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
        var searchValue = field.getValue();

        if (searchValue) {
            var store = this.getViewModel().get("allSchedules");
            store.getAdvancedFilter().addQueryFilter(searchValue);
            store.load();
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
        var vm = this.getViewModel(),
            store = vm.get("allSchedules");
        if (store) {
            // clear store filter
            store.getAdvancedFilter().clearQueryFilter();
            store.load();
            // reset input
            field.reset();
        }
    },
    /**
     * 
     * @param {Ext.data.Store} store 
     * @param {Ext.data.Model[]} records 
     */
    onAllSchedulesStoreDatachanged: function (store, records) {
        var counter = this.getView().down('#scheduleGridCounter');
        counter.setHtml(Ext.String.format(CMDBuildUI.locales.Locales.administration.groupandpermissions.strings.displaytotalrecords, null, null, store.totalCount));
    }
});
