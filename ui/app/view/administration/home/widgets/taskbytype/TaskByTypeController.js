Ext.define('CMDBuildUI.view.administration.home.widgets.taskbytype.TaskByTypeController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-taskbytype-taskbytype',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#addTaskTool': {
            click: 'onAddTaskToolClick',
            destroy: 'onDestroyTool'
        }
    },

    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    },

    onAddTaskToolClick: function (tool) {
        if (tool.menu) {
            tool.menu.show();
        } else {
            var me = this;

            var types = CMDBuildUI.model.tasks.Task.getTypes();
            if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled)) {
                types = Ext.Array.filter(types, function (item) {
                    return item.value !== CMDBuildUI.model.tasks.Task.types.workflow;
                });
            }
            if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled)) {
                types = Ext.Array.filter(types, function (item) {
                    return item.value !== CMDBuildUI.model.tasks.Task.types.importgis;
                });
            }
            var menus = [];
            Ext.Array.forEach(types, function (_type) {
                if (_type.value !== CMDBuildUI.model.tasks.Task.types.export_file) {
                    if (_type.value === CMDBuildUI.model.tasks.Task.types.import_file) {
                        _type.label = CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile;
                    }
                    menus.push({
                        text: _type.label,
                        type: _type.value,
                        subType: _type.subType,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('clock', 'regular'),
                        listeners: {
                            click: function (menuItem, _eOpts) {
                                me.redirectTo('administration/tasks');
                                Ext.asap(function () {
                                    var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                                    container.removeAll();
                                    var type = menuItem.type;

                                    if (type === CMDBuildUI.model.tasks.Task.types.import_export) {
                                        type = CMDBuildUI.model.tasks.Task.types.import_file;
                                    }
                                    var grid = Ext.ComponentQuery.query('administration-content-tasks-grid')[0];
                                    var cardVm = {
                                        links: {
                                            theTask: {
                                                type: CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(type, menuItem.subType),
                                                create: {
                                                    type: type,
                                                    config: {
                                                        tag: menuItem.subType
                                                    }
                                                }
                                            }
                                        },
                                        data: {
                                            workflowClassName: menuItem.lookupViewModel().get('objectTypeName'),
                                            taskType: menuItem.type,
                                            subType: menuItem.subType,
                                            grid: grid,
                                            action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                                            actions: {
                                                view: false,
                                                edit: false,
                                                add: true
                                            }
                                        }
                                    };
                                    if (menuItem.lookupViewModel().get('objectTypeName')) {
                                        cardVm.links.theTask.create.config.classname = menuItem.lookupViewModel().get('objectTypeName');
                                        cardVm.data.comeFromClass = menuItem.lookupViewModel().get('objectTypeName');
                                    }
                                    grid.getViewModel().bind({ bindTo: '{gridDataStore}', single: true }, function () {
                                        container.add({
                                            xtype: 'administration-content-tasks-card',
                                            viewModel: cardVm
                                        });
                                    });
                                });
                            }
                        }
                    });
                }
            });

            tool.menu = Ext.create('Ext.menu.Menu', {
                autoShow: true,
                items: menus
            });

            tool.menu.alignTo(tool.el.id, 'tr-br?');
        }
    },

    /**
     * 
     * @param {Ext.panel.Tool} tool 
     * @param {Object} eOpts 
     */
    onDestroyTool: function (tool, eOpts) {
        if (tool.menu) {
            tool.menu.destroy();
        }
    }

});