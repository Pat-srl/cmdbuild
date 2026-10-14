Ext.define('CMDBuildUI.view.administration.content.tasks.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-tasks-view',
    data: {
        type: '',
        subType: '',
        actions: {
            view: true,
            edit: false,
            add: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        servicesStatusLabel: '',
        isStandalone: true
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_jobs_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },
        typeManager: {
            bind: {
                type: '{type}'
            },
            get: function (data) {
                var me = this;
                var modelName, gridFilters;
                if (data.type) {
                    switch (data.type) {
                        case CMDBuildUI.model.tasks.Task.types.import_export:
                            gridFilters = [
                                function (item) {
                                    return (
                                        item.get('type') === CMDBuildUI.model.tasks.Task.types.import_file ||
                                        item.get('type') === CMDBuildUI.model.tasks.Task.types.export_file
                                    );
                                }
                            ];
                            break;
                        case CMDBuildUI.model.tasks.Task.types.workflow:
                            gridFilters = [
                                function (item) {
                                    if (!me.getView().getWorkflowClassName()) {
                                        return data.type === item.get('type');
                                    } else {
                                        return (
                                            data.type === item.get('type') &&
                                            me.getView().getWorkflowClassName() === item.get('config').classname
                                        );
                                    }
                                }
                            ];
                            break;
                        case CMDBuildUI.model.tasks.Task.types.importgis: // etl
                            gridFilters = [
                                function (item) {
                                    if (!item.get('config').tag && item.get('config').gate) {
                                        var gateStore = Ext.getStore('importexports.Gates');
                                        var gate = gateStore.findRecord('code', item.get('config').gate);
                                        if (gate) {
                                            item.get('config').gateconfig_handlers_0_type = gate.get('_handler_type');
                                            item.get('config').gateconfig_handlers_0_gate = gate.get('code');
                                            item.get('config').tag = gate.get('_handler_type');
                                        }
                                    }

                                    if (me.get('subType') === 'database') {
                                        return (
                                            CMDBuildUI.model.tasks.Task.types.importgis === item.get('type') &&
                                            item.get('config').tag === 'database'
                                        );
                                    } else if (me.get('subType') === 'cad') {
                                        return (
                                            CMDBuildUI.model.tasks.Task.types.importgis === item.get('type') &&
                                            item.get('config').tag === 'cad'
                                        );
                                    } else if (me.get('subType') === 'ifc') {
                                        return (
                                            CMDBuildUI.model.tasks.Task.types.importgis === item.get('type') &&
                                            item.get('config').tag === 'ifc'
                                        );
                                    }
                                }
                            ];
                            break;
                        case CMDBuildUI.model.tasks.Task.types.emailService:
                        case CMDBuildUI.model.tasks.Task.types.sendemail:
                        case CMDBuildUI.model.tasks.Task.types.waterway:
                        default:
                            gridFilters = [
                                function (item) {
                                    return data.type === item.get('type');
                                }
                            ];
                            break;
                    }
                    modelName = CMDBuildUI.util.administration.helper.ModelHelper.getTaskModelNameByType(
                        data.type,
                        me.get('subType')
                    );
                } else {
                    modelName = 'CMDBuildUI.model.tasks.Task';
                    gridFilters = [
                        function (item) {
                            if (
                                item.get('type') === 'etl' &&
                                (!item.get('config') || !item.get('config').tag || item.get('config').tag === 'script')
                            ) {
                                return false;
                            }

                            return (
                                [
                                    CMDBuildUI.model.tasks.Task.types.export_file,
                                    CMDBuildUI.model.tasks.Task.types.import_file,
                                    CMDBuildUI.model.tasks.Task.types.emailService,
                                    CMDBuildUI.model.tasks.Task.types.workflow,
                                    CMDBuildUI.model.tasks.Task.types.import_database,
                                    CMDBuildUI.model.tasks.Task.types.importgis,
                                    CMDBuildUI.model.tasks.Task.types.waterway
                                ].indexOf(item.get('type')) > -1
                            );
                        }
                    ];
                }
                this.set('taskModelName', modelName);
                this.set('gridFilters', gridFilters);
                return data.type;
            }
        },
        actionManager: {
            bind: '{action}',
            get: function (action) {
                if (this.get('actions.edit')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (this.get('actions.add')) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
            }
        },

        addTaskButtonText: {
            bind: {
                type: '{taskType}'
            },
            get: function (data) {
                var me = this;
                var type = Ext.Array.findBy(CMDBuildUI.model.tasks.Task.getTypes(), function (item) {
                    if (!me.get('subType')) {
                        return item.value === data.type || item.group === data.type;
                    }
                    return item.value === data.type && item.subType === me.get('subType');
                });
                if (type) {
                    return Ext.String.format(
                        '{0} {1}',
                        CMDBuildUI.locales.Locales.administration.tasks.texts.addtask,
                        type.groupLabel
                    );
                }
            }
        },

        serviceStatusManager: {
            get: function () {
                var me = this;
                CMDBuildUI.util.Ajax.setActionId('system.schedulerjobs.status');
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/system_services/schedulerjobs', CMDBuildUI.util.Config.baseUrl),
                    method: 'GET'
                }).then(function (response) {
                    var res = JSON.parse(response.responseText);
                    if (!me.destroyed) {
                        var value = res.data.status;
                        var output = '<span';
                        if (CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statuscolors[value]) {
                            output +=
                                ' style="color:' +
                                CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statuscolors[value] +
                                ';"';
                        }
                        output +=
                            '>' +
                            // icon
                            '<span class="' +
                            CMDBuildUI.view.administration.content.setup.elements.StatusGrid.statusicons[value] +
                            '"></span> ' +
                            // label
                            CMDBuildUI.locales.Locales.administration.systemconfig['status' + value] +
                            '</span>';

                        me.set(
                            'servicesStatusLabel',
                            Ext.String.format(
                                '{0}: {1}',
                                CMDBuildUI.locales.Locales.administration.tasks.texts.servicestatus,
                                output
                            )
                        );
                    }
                });
            }
        }
    },

    stores: {
        gridDataStore: {
            type: 'tasks',
            proxy: {
                type: 'baseproxy',
                url: '/jobs',
                extraParams: {
                    detailed: true,
                    type: '{type}'
                }
            },
            filters: '{gridFilters}',
            autoLoad: true,
            autoDestroy: true
        },
        allImportExportTemplates: {
            source: 'importexports.Templates',
            autoload: true,
            autoDestroy: true
        },

        allEmailAccountTemplates: {
            type: 'importexports-templates',
            autoload: true,
            autoDestroy: true
        }
    }
});
