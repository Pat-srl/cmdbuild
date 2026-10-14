Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.forms.FormsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-forms-forms',
    data: {
        name: 'CMDBuildUI',
        activitiesstore: {
            proxyurl: null,
            autoload: null
        }
    },
    formulas: {
        theProcessManager: {
            bind: {
                theProcess: '{theProcess}'
            },
            get: function (data) {
                if (data.theProcess && !data.theProcess.phantom) {
                    var url = CMDBuildUI.util.administration.helper.ApiHelper.server.getProcessActivity(data.theProcess.get('name'));
                    this.set('activitiesstore.proxyurl', url);
                    this.set('activitiesstore.autoload', true);
                }
            }
        }
    },

    stores: {
        activitiesStore: {
            model: 'CMDBuildUI.model.processes.Activity',
            proxy: {
                type: 'baseproxy',
                url: '{activitiesstore.proxyurl}'
            },
            pageSize: 0,
            remoteSort: false,
            sorters: ['addFormBtnSorter'],
            autoLoad: '{activitiesstore.autoload}'
        },
        activitiesWithForm: {
            storeId: 'activitiesWithForm',
            source: '{activitiesStore}',
            filters: [function (item) {
                return !Ext.isEmpty(item.get('formStructure')) && !Ext.Object.isEmpty(item.get('formStructure'));
            }],
            listeners: {
                datachanged: 'onActivitiesStoreDataChanged'
            }
        },
        activitiesWithoutForm: {
            storeId: 'activitiesWithoutForm',
            source: '{activitiesStore}',
            filters: [function (item) {
                return Ext.isEmpty(item.get('formStructure')) || Ext.Object.isEmpty(item.get('formStructure'));
            }],
            listeners: {
                datachanged: 'onActivitiesStoreDataChanged'
            }
        }
    }

});