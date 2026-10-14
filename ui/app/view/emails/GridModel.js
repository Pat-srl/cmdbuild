Ext.define('CMDBuildUI.view.emails.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.emails-grid',

    data: { readonly: true },

    formulas: {
        updateCounter: {
            bind: '{emails}',
            get: function (emails) {
                if (emails) {
                    const me = this;
                    const view = me.getView();

                    view.mon(emails, 'datachanged', function (store, eOpts) {
                        me.set('tabcounters.emails', store.getCount());
                        if (view.loadMaskTableView) {
                            CMDBuildUI.util.Utilities.removeLoadMask(view.loadMaskTableView);
                            view.loadMaskTableView = null;
                        }
                    });

                    if (this.get('theTarget').phantom || (emails.isLoaded() && view.loadMaskTableView)) {
                        CMDBuildUI.util.Utilities.removeLoadMask(view.loadMaskTableView);
                        view.loadMaskTableView = null;
                    }
                }
            }
        },

        disableButtonOnView: {
            get: function () {
                const view = this.getView();
                this.set('readonly', view.getReadOnly());
                return view.isFormWritable() ? false : true;
            }
        },

        isAsync: {
            bind: { theTarget: '{theTarget}' },
            get: function (data) {
                return data.theTarget.phantom;
            }
        },

        grouperStore: {
            get: function () {
                return CMDBuildUI.util.Navigation.getGroupEmailByStatus() ? { property: 'status' } : null;
            }
        }
    },

    stores: {
        chainedemails: {
            type: 'chained',
            source: '{emails}',
            grouper: '{grouperStore}',
            sorters: [{ property: 'date', direction: 'DESC' }]
        }
    }
});
