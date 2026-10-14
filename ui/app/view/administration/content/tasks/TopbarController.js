Ext.define('CMDBuildUI.view.administration.content.tasks.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-tasks-topbar',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#addtask': {
            click: 'onNewTaskBtnClick'
        },
        '#navigateToServices': {
            click: 'onNavigateToServicesClick'
        }
    },
    onBeforeRender: function (view) {

        var vm = view.lookupViewModel();
        if (vm.get('type')) {
            view.down('#addtask').setArrowVisible(false);
        }
    },
    onAfterRender: function (view) {

        var vm = view.lookupViewModel();
        const config_ui = view.getViewModel().get('isStandalone') ?
        'administration-action-small'
        : 'administration-primary-outline-small';
        if (vm.get('type')) {
            view.down('#addtask').setUi(config_ui);
        }
    },

    /**
     *
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewTaskBtnClick: function (item, event, eOpts) {
        var type = this.getViewModel().get('type');
        if (!type) {
            return this.showMenuButton(item, event, eOpts);
        }
        var subType = this.getViewModel().get('subType');
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        switch (type) {
            case CMDBuildUI.model.tasks.Task.types.import_export:
                type = CMDBuildUI.model.tasks.Task.types.import_file;
                break;

            default:
                break;
        }
        var cardVm = {
            links: {
                theTask: {
                    type: item.lookupViewModel().get('taskModelName'),
                    create: {
                        type: type,
                        config: {
                            tag: subType
                        }
                    }
                }
            },
            data: {
                workflowClassName: item.lookupViewModel().get('objectTypeName'),
                taskType: type,
                subType: subType,
                grid: item.up('administration-content-tasks-view').down('administration-content-tasks-grid'),
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    view: false,
                    edit: false,
                    add: true
                }
            }
        };
        if (item.lookupViewModel().get('objectTypeName')) {
            cardVm.links.theTask.create.config.classname = item.lookupViewModel().get('objectTypeName');
            cardVm.data.comeFromClass = item.lookupViewModel().get('objectTypeName');
        }
        container.add({
            xtype: 'administration-content-tasks-card',
            viewModel: cardVm
        });
    },

    onNavigateToServicesClick: function () {
        this.redirectTo('administration/setup/system', true);
    },

    privates: {
        showMenuButton: function (button) {
            var me = this;
            var type = this.getViewModel().get('type');
            if (Ext.isEmpty(type)) {
                var types = CMDBuildUI.model.tasks.Task.getTypes();
                if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.processes.enabled)) {
                    types = Ext.Array.filter(types, function (item) {
                        return item.value !== CMDBuildUI.model.tasks.Task.types.workflow;
                    });
                }
                if (!CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.gis.enabled)) {
                    types = Ext.Array.filter(types, function (item) {
                        return !item.subType || item.subType !== 'cad';
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
                            handler: me.onAddTaskMenuItemBtnClick
                        });
                    }
                });
                var menu = Ext.create('Ext.menu.Menu', {
                    autoShow: true,
                    items: menus
                });

                menu.alignTo(button.el.id, 'bl');
                button.setMenu(menu);
            }
        },
        onAddTaskMenuItemBtnClick: function (menuItem) {

            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
            container.removeAll();
            var type = menuItem.type;

            if (type === CMDBuildUI.model.tasks.Task.types.import_export) {
                type = CMDBuildUI.model.tasks.Task.types.import_file;
            }
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
                    grid: menuItem.up('administration-content-tasks-view').down('administration-content-tasks-grid'),
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
            container.add({
                xtype: 'administration-content-tasks-card',
                viewModel: cardVm
            });
        }
    }
});