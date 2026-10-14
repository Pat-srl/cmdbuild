Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-importexport-gatetemplates-view',
    data: {
        addBtnText: null,
        searchAllEmptyText: null,
        actions: {
            view: true,
            edit: false,
            add: false,
            empty: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            print: true, // action !== view
            disable: true,
            enable: true
        },
        filterByTypeGate: [],
        toolAction: {
            _canAdd: false
        }
    },
    formulas: {
        gateTypeManager: {
            bind: '{gateType}',
            get: function (gateType) {
                var title, addBtnText, searchAllEmptyText;
                switch (gateType) {
                    case 'ifc':
                        title = CMDBuildUI.locales.Locales.administration.importexport.texts.importexportifcgatetemplates;
                        addBtnText = CMDBuildUI.locales.Locales.administration.gates.addifctemplate;
                        searchAllEmptyText = CMDBuildUI.locales.Locales.administration.gates.searchifcfield;
                        break;
                    case 'cad':
                    case 'gis':
                        title = CMDBuildUI.locales.Locales.administration.importexport.texts.importexportgisgatetemplates;
                        addBtnText = CMDBuildUI.locales.Locales.administration.gates.addgistemplate;
                        searchAllEmptyText = CMDBuildUI.locales.Locales.administration.gates.searchgisfield;
                        break;
                    case 'database':
                        title = CMDBuildUI.locales.Locales.administration.importexport.texts.importexportdatabasegatetemplates;
                        addBtnText = CMDBuildUI.locales.Locales.administration.gates.adddatabasetemplate;
                        searchAllEmptyText = CMDBuildUI.locales.Locales.administration.gates.searchdatabasefield;
                        break;

                    default:
                        new Ext.util.DelayedTask(function () {
                            CMDBuildUI.util.Notifier.showErrorMessage('Gate type not managed!');
                        }).delay(100);
                        addBtnText = '';
                        searchAllEmptyText = '';
                        title = CMDBuildUI.locales.Locales.administration.importexport.texts.importexportfile;
                        break;
                }
                this.getParent().set('title', title);
                this.set('addBtnText', addBtnText);
                this.set('searchAllEmptyText', searchAllEmptyText);

            }
        },
        gateManager: {
            bind: '{theGate}',
            get: function (theGate) {
                if (theGate.phantom) {
                    switch (this.get('gateType')) {
                        case 'gis':
                        case 'cad':
                            theGate.handlers().add(CMDBuildUI.model.importexports.GateGisHandler.create({
                                type: 'cad'
                            }));
                            break;
                        case 'ifc':
                            theGate.handlers().add(CMDBuildUI.model.importexports.GateIfcHandler.create({
                                type: 'ifc'
                            }));
                            theGate.setConfig(CMDBuildUI.model.importexports.GateIfcConfig.create());
                            break;
                        case 'database':
                            theGate.handlers().add(CMDBuildUI.model.importexports.GateDatabaseHandler.create({
                                type: 'database'
                            }));
                            theGate.setConfig(CMDBuildUI.model.importexports.GateDatabaseConfig.create());
                            break;

                        default:
                            break;
                    }
                }
            }
        },
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_etl_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
            }
        },
        action: {
            bind: {
                isView: '{actions.view}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isEmpty: '{actions.empty}'
            },
            get: function (data) {
                this.set('activeTab', this.getView().up('administration-content').getViewModel().get('activeTabs.gatetemplate') || 0);
                if (data.isView) {
                    this.set('formModeCls', 'formmode-view');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                } else if (data.isEdit) {
                    this.set('formModeCls', 'formmode-edit');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    this.set('formModeCls', 'formmode-add');
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                this.set('actions.empty', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.empty);
            }
        },
        getToolbarButtons: {
            bind: '{theGate.active}',
            get: function (get) {
                this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.print', !this.get('actions.view'));
                this.set('toolbarHiddenButtons.disable', true);
                this.set('toolbarHiddenButtons.enable', false);
            }
        },
        updateToolbarButtons: {
            bind: '{theGate.active}',
            get: function (data) {
                if (data) {
                    this.set('toolbarHiddenButtons.disable', false);
                    this.set('toolbarHiddenButtons.enable', true);
                } else {
                    this.set('toolbarHiddenButtons.disable', true);
                    this.set('toolbarHiddenButtons.enable', false);
                }
            }
        },
        templateCodes: {
            bind: '{theGate.handlers}',
            get: function (gateHandler) {
                var me = this;
                if(gateHandler && gateHandler.first()){
                    gateHandler.first().getTemplates().then(function (templatesStore) {
                        me.set('allGateTemplates', templatesStore);
                    });
                }
            }
        }
    }
});