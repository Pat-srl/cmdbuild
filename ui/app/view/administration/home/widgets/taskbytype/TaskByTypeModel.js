Ext.define('CMDBuildUI.view.administration.home.widgets.taskbytype.TaskByTypeModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-home-widgets-taskbytype-taskbytype',
    data: {
        gridData: []
    },
    formulas: {
        initData: function (get) {
            var me = this;
            this.set('showLoader', true);
            var gridData = [];
            // this.set('gridData', [{
            //     type: CMDBuildUI.model.tasks.Task.types.emailService,
            //     description: CMDBuildUI.locales.Locales.administration.tasks.texts.reademails,
            //     active: 0,
            //     nonactive: 0
            // }, {
            //     type: CMDBuildUI.model.tasks.Task.types.sendemail,
            //     description: CMDBuildUI.locales.Locales.administration.tasks.texts.sendemails,
            //     active: 0,
            //     nonactive: 0
            // }, {
            //     type: CMDBuildUI.model.tasks.Task.types.import_export,
            //     description: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile,
            //     active: 0,
            //     nonactive: 0
            // }, {

            //     type: 'database',
            //     description: CMDBuildUI.locales.Locales.administration.importexport.texts.importdatabase,
            //     active: 0,
            //     nonactive: 0
            // }, {
            //     type: 'cad',
            //     description: CMDBuildUI.locales.Locales.administration.navigation.importgis,
            //     active: 0,
            //     nonactive: 0
            // }, {
            //     type: 'ifc',
            //     description: CMDBuildUI.locales.Locales.administration.importexport.texts.importexportifcgatetemplate,
            //     active: 0,
            //     nonactive: 0
            // }, {
            //     type: CMDBuildUI.model.tasks.Task.types.workflow,
            //     description: CMDBuildUI.locales.Locales.administration.classes.texts.startworkflow,
            //     active: 0,
            //     nonactive: 0
            // }]);


            Ext.Array.forEach(CMDBuildUI.model.tasks.Task.getTypes(), function (type) {
                if (type.value !== 'export_file') {
                    gridData.push({
                        type: type.group,
                        description: type.groupLabel,
                        subType: type.subType,
                        active: 0,
                        nonactive: 0
                    });
                }
            });
            this.set('gridData', gridData);
            Ext.Ajax.request({
                url: CMDBuildUI.util.Config.baseUrl + '/jobs?detailed=true',
                method: "GET",
                timeout: 0
            }).then(function (response, opts) {
                if (!me.destroyed) {
                    var responseJson = Ext.JSON.decode(response.responseText, true);
                    var store = me.getStore('tasksStats');
                    store.each(function (item) {
                        item.set('active', 0);
                        item.set('nonactive', 0);
                    });
                    Ext.Array.forEach(responseJson.data, function (item) {
                        if (item.type !== 'script') {
                            switch (item.type) {
                                case CMDBuildUI.model.tasks.Task.types.import_file:
                                case CMDBuildUI.model.tasks.Task.types.export_file:
                                    item.type = CMDBuildUI.model.tasks.Task.types.import_export;
                                    break;
                            }
                            var record = store.findRecord('subType', item.config.tag) || store.findRecord('type', item.type) || store.findRecord('type', item.config.tag);
                            if (!record) {
                                CMDBuildUI.util.Logger.log("Unable to process the following task item:", CMDBuildUI.util.Logger.levels.error);
                                CMDBuildUI.util.Logger.log(Ext.JSON.encode(item), CMDBuildUI.util.Logger.levels.error);
                            } else {
                                if (item.enabled) {
                                    record.set('active', record.get('active') + 1);
                                } else {
                                    record.set('nonactive', record.get('nonactive') + 1);
                                }
                            }
                        }
                    });
                    me.set('showLoader', false);

                }

            },
                function () {
                    if (!me.destroyed) {
                        me.set('showLoader', false);
                    }
                });
        }
    },
    stores: {
        tasksStats: {
            model: new Ext.data.Model({
                fields: [{
                    type: 'string',
                    name: 'type'
                }, {
                    type: 'string',
                    name: 'description'
                }, {
                    type: 'integer',
                    name: 'active'
                }, {
                    type: 'integer',
                    name: 'nonactive'
                }, {
                    type: 'integer',
                    name: 'total',
                    calculate: function (data) {
                        return data.active + data.nonactive;
                    }
                }]
            }),
            data: '{gridData}',
            autoDestroy: true
        }
    }

});