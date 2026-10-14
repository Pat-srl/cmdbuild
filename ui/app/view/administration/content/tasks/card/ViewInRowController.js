Ext.define('CMDBuildUI.view.administration.content.tasks.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    mixins: ['CMDBuildUI.view.administration.content.tasks.card.CardMixin'],
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-tasks-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.tasks.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        Ext.getStore('importexports.Gates').load();
        var vm = this.getViewModel();
        var me = this;
        var selected = view._rowContext.record;
        vm.set('taskType', selected.get('type'));
        var modelName = CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(selected.get('type'), selected.get('config').tag);
        vm.linkTo('theTask', {
            type: modelName,
            id: selected.get('_id')
        });
        vm.bind({
            bindTo: {
                theTask: '{theTask}'
            }
        }, function (data) {
            if (data.theTask) {
                // workaround after patch "3.3.0-35"              
                if (data.theTask._config.get('tag') === 'database') {
                    var gateStore = Ext.getStore('importexports.Gates');
                    var gate = gateStore.findRecord('code', data.theTask._config.get('gate'));
                    if (gate) {
                        data.theTask._config.set('gateconfig_handlers_0_type', gate.get('_handler_type'));
                        data.theTask._config.set('gateconfig_handlers_0_gate', gate.get('code'));
                        data.theTask._config.set('tag', gate.get('_handler_type'));
                    }
                }
                Ext.asap(function () {
                    try {
                        vm.bind({
                            bindTo: {
                                gate: '{theTask.config.gateconfig_handlers_1_gate}'
                            }
                        }, function (_data) {
                            if (!vm.destroyed) {
                                var container = view.down('#targetCardIdContainer');
                                if (container) {
                                    container.removeAll();
                                    CMDBuildUI.util.Stores.loadETLGatesStore().then(function (gates) {
                                        if (!vm.destroyed) {
                                            var store = Ext.getStore('importexports.Gates');
                                            var record = store.findRecord('code', _data.gate);
                                            var className = record.get('config').bimserver_project_master_card_target_class;
                                            if (className) {
                                                var referencecombo = {
                                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                                                    columnWidth: 0.5,
                                                    xtype: 'referencecombofield',
                                                    displayField: 'Description',
                                                    itemId: 'ownerCard',
                                                    valueField: '_id',
                                                    name: 'gateconfig_handlers_1_config_bimserver_project_master_card_id',
                                                    width: '100%',
                                                    style: 'padding-right: 15px',
                                                    metadata: {
                                                        targetType: 'class',
                                                        targetClass: className
                                                    },
                                                    hidden: true,
                                                    bind: {
                                                        disabled: '{!theTask.config.gateconfig_handlers_1_gate}',
                                                        value: '{theTask.config.gateconfig_handlers_1_config_bimserver_project_master_card_id}',
                                                        hidden: '{actions.view || theTask.config.gateconfig_handlers_1_config_bimserver_project_master_card_mode !== "static"}'
                                                    },
                                                    listeners: {
                                                        change: function (input, newValue, oldValue) {
                                                            var _vm = input.lookupViewModel();
                                                            if (_vm.get('theTask')._config.get('gateconfig_handlers_1_config_bimserver_project_master_card_id') !== newValue) {
                                                                _vm.set('gateconfig_handlers_1_config_bimserver_project_master_card_id', newValue);
                                                                _vm.get('theTask')._config.set('gateconfig_handlers_1_config_bimserver_project_master_card_id', newValue);
                                                            }
                                                        }
                                                    }
                                                };
                                                container.add(referencecombo);
                                                container.add({
                                                    xtype: 'displayfield',
                                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                                                    bind: {
                                                        hidden: '{!actions.view}',
                                                        value: '{theTask.config._gateconfig_handlers_1_config_bimserver_project_master_card_id_description}'
                                                    }
                                                });
                                            }

                                        }
                                    });
                                }
                            }
                        });
                        Ext.asap(function () {
                            me.generateCardFor(selected.get('type'), data, view);
                            view.setHidden(false);
                            view.setActiveTab(0);
                            view.up().unmask();
                        })
                    } catch (error) {

                    }
                }, this);

            }
        });


        if (!CMDBuildUI.util.Stores.loaded.emailaccounts) {
            CMDBuildUI.util.Stores.loadEmailAccountsStore();
        }
        if (!CMDBuildUI.util.Stores.loaded.emailtemplates) {
            CMDBuildUI.util.Stores.loadEmailTemplatesStore();
        }
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();

    },

    onAfterRender: function (view) {
        // var vm = this.getViewModel();
        // var selected = view._rowContext.record;
        // var type = selected.get('type');


    },

    onImportExportTemplateUpdate: function (v, record) {
        new Ext.util.DelayedTask(function () { }).delay(
            150,
            function (v, record) {
                var vm = this.getViewModel();
                var view = this.getView();
                this.linkImportExportTemplate(view, vm);
            },
            this,
            arguments);
    },
    onETLStoreDataChanged: function (data) {
        var vm = this.getViewModel();
        vm.set('allImportExportTemplate.isReady', false);
        vm.set('allImportExportTemplate.isReady', true);
        data.isReady = false;
        data.isReady = true;
    },
    linkImportExportTemplate: function (view, vm) {
        var grid = view.up(),
            record = grid.getSelection()[0];

        vm.set("theTask", record);
    }
});