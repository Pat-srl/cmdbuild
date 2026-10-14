Ext.define('CMDBuildUI.view.administration.content.processes.tabitems.helps.HelpsModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-processes-tabitems-helps-helps',
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
            get: function(data){
                if(data.theProcess){
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
            proxy : {
                type: 'baseproxy',
                url: '{activitiesstore.proxyurl}'
            },
            pageSize: 0,
            remoteSort: false,
            sorters: ['description'],
            autoLoad: '{activitiesstore.autoload}'
        },
        activitiesWithHelp: {
            storeId: 'activitiesWithHelp',
            source: '{activitiesStore}',
            filters: [function(item){                   
                return !Ext.isEmpty(item.get('_instructions_translation')) && !Ext.Object.isEmpty(item.get('_instructions_translation'));
            }],
            listeners: {
                datachanged: 'onActivitiesStoreDataChanged'
            }
        },
        activitiesWithoutHelp: {
            storeId: 'activitiesWithoutForm',
            source: '{activitiesStore}',
            filters: [function(item){          
                return Ext.isEmpty(item.get('instructions')) || Ext.Object.isEmpty(item.get('instructions'));
            }],
            listeners: {
                datachanged: 'onActivitiesStoreDataChanged'
            }
        }
    }

});
