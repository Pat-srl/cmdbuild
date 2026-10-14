Ext.define('CMDBuildUI.view.administration.content.dashboards.dashboard.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-dashboards-card',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#closeBtn': {
            click: 'onCancelBtnClick'
        },
        '#addrowBtn': {
            click: 'onAddrowBtnClick'
        },
        '#addParameterBtn': {
            click: 'onAddParameterBtnClick'
        }
    },
    onBeforeRender: function (view) {
        const me = this;
        const vm = view.lookupViewModel();
        vm.bind(
            {
                bindTo: {
                    theDashboard: '{theDashboard}'
                }
            },
            function (data) {
                vm.set('componentsData', data.theDashboard.get('components'));
                const layout = data.theDashboard.get('layout');
                vm.set('rowsData', (layout && layout.rows) || []);
                vm.set('globalParametersData', data.theDashboard.get('parameters'));
                // construct here the form
                view.add(
                    CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getGeneralProperties(
                        'display',
                        view
                    )
                );
                const layoutGrid = CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getLayout(
                    'display',
                    view
                );
                view.add(
                    CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getParameters(
                        'display',
                        view
                    )
                );
                view.add(layoutGrid);

                view.down('#layoutcontainer').add([
                    CMDBuildUI.util.administration.helper.GridHelper.getDragAndDropReorderGrid([
                        {
                            xtype: 'widgetcolumn',
                            flex: 1,
                            widget: {
                                xtype: 'administration-content-dashboards-card-builder-row'
                            },
                            plugins: [],
                            variableRowHeight: true
                        }
                    ])
                ]);
            }
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.showLoader(true);
        const me = this;
        const form = me.getView();
        const vm = me.getViewModel();
        const grid = vm.get('grid');
        const theDashboard = vm.get('theDashboard');

        if (theDashboard.get('icon_font')) {
            theDashboard.set(
                'icon_font',
                CMDBuildUI.util.helper.IconHelper.setIconDBValue(theDashboard.get('icon_font'))
            );
        }
        // set components
        var components = [];
        theDashboard.components().each(function (component) {
            var _component = component.getData();
            var dataSourceParameters = [];
            delete component.data.descriptionWithName;
            if (component.dataSourceParameters) {
                Ext.Array.forEach(component.dataSourceParameters().getRange(), function (parameter) {
                    if (Ext.isEmpty(parameter.get('fieldType')) && Ext.isEmpty(parameter.get('type'))) {
                        component.dataSourceParameters().remove(parameter);
                    } else {
                        delete parameter.data._id;
                        delete parameter.data._name_translation;
                        dataSourceParameters.push(parameter.getData());
                    }
                });
            }
            _component.dataSourceParameters = dataSourceParameters;
            components.push(_component);
        });

        var rows = [];
        vm.get('rows').each(function (row) {
            // Clear the id from the row and its data to avoid sending unnecessary information to the server
            delete row.id;
            delete row.data.id;
            rows.push(row.getData());
        });

        const globalParametersData = vm
            .get('globalParametersStore')
            .getRange()
            .map(function (record) {
                return record.getData();
            });

        theDashboard.set('layout', {
            rows: rows
        });
        theDashboard.set('components', components);
        theDashboard.set('parameters', globalParametersData);
        theDashboard.save({
            success: function (record, operation) {
                me.saveDashboardDescriptionLocales(vm, record);
                me.saveDashboardGlobalParameterLocales(vm, record);
                me.saveDashboardComponentLocalizedFieldLocales(vm, record);
                if (grid) {
                    grid.fireEventArgs('itemupdated', [record]);
                    form.container.component.fireEvent('closed');
                    CMDBuildUI.util.Utilities.showLoader(false);
                } else if (form.up('administration-maincontainer')) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDashboardUrl(
                        record.getId()
                    );
                    if (vm.get('action') === CMDBuildUI.util.administration.helper.FormHelper.formActions.add) {
                        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                                'href',
                                nextUrl,
                                me
                            );
                            CMDBuildUI.util.Utilities.showLoader(false);
                        });
                    } else {
                        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                            'href',
                            nextUrl,
                            record.get('description'),
                            me
                        );
                        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy(
                            'href',
                            nextUrl,
                            me
                        );
                        CMDBuildUI.util.Utilities.showLoader(false);
                    }
                }
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = this.getViewModel();
        vm.get('theDashboard').reject(); // discard changes
        view.container.component.fireEvent('closed');
    },

    /**
     * On description translate button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClickDescription: function (button, event, eOpts) {
        const vm = this.getViewModel();
        const theDashboard = vm.get('theDashboard');
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardDescription(
                theDashboard.get('name')
            );

        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm.getParent()
        );
    },

    /**
     * On edit button click
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            viewModel = {
                data: {
                    theDashboard: vm.get('theDashboard'),
                    grid: vm.get('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            };
        view.up('administration-content-dashboards-tabpanel').getViewModel().toggleEnableTabs(0);
        var container = view.container.component;
        container.removeAll();
        container.add({
            xtype: 'view-administration-content-dashboards-card',
            viewModel: viewModel,
            data: {}
        });
    },

    onDeleteBtnClick: function (button, event) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            theDashboard = vm.get('theDashboard');
        Ext.Msg.alwaysOnTop = true;
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === 'yes') {
                    CMDBuildUI.util.Utilities.showLoader(true);
                    CMDBuildUI.util.Ajax.setActionId('delete-dashboard');
                    theDashboard.erase({
                        success: function (record, operation) {
                            if (grid) {
                                grid.fireEventArgs('reload', [record, 'delete']);
                            }
                            if (view.source) {
                                view.source.container.component.remove(view.source);
                            } else if (view.up('administration-maincontainer')) {
                                var apiHelper = CMDBuildUI.util.administration.helper.ApiHelper.client,
                                    nextUrl = apiHelper.getDashboardUrl(),
                                    recordUrl = apiHelper.getDashboardUrl(record.getId());

                                CMDBuildUI.util.Stores.loadDashboardsStore();
                                CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                                    'href',
                                    recordUrl,
                                    nextUrl,
                                    me
                                );
                            }
                            CMDBuildUI.util.Utilities.showLoader(false);
                            view.container.component.fireEvent('closed');
                        },
                        failure: function () {
                            CMDBuildUI.util.Utilities.showLoader(false);
                        }
                    });
                }
            },
            this
        );
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */

    onToggleBtnClick: function (button, e, eOpts) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theDashboard = vm.get('theDashboard');
        theDashboard.set('active', !theDashboard.get('active'));

        me.onSaveBtnClick();
    },

    saveDashboardDescriptionLocales: function (vm, record) {
        if (vm.get('actions.add')) {
            var translations = ['theDescriptionTranslation'];
            var keyFunction = ['getLocaleKeyOfDashboardDescription'];
            Ext.Array.forEach(translations, function (item, index) {
                if (vm.get(item)) {
                    var translationCode = CMDBuildUI.util.administration.helper.LocalizationHelper[keyFunction[index]](
                        record.get('name')
                    );
                    vm.get(item).crudState = 'U';
                    vm.get(item).crudStateWas = 'U';
                    vm.get(item).phantom = false;
                    vm.get(item).set('_id', translationCode);
                    vm.get(item).save({
                        success: function (_translations, operation) {
                            CMDBuildUI.util.Logger.log(
                                item + ' localization was saved',
                                CMDBuildUI.util.Logger.levels.debug
                            );
                        }
                    });
                }
            });
        }
    },

    saveDashboardGlobalParameterLocales: function (vm, record) {
        const recordName = record.get('name');
        vm.get('globalParametersStore').each(function (parameter) {
            const parameterId = parameter.get('_id');
            const parameterIdReplaced = parameterId.replaceAll('-', '_');
            const parameterDescriptionVmObject = Ext.String.format(
                'theGlobalParameterDescription_{0}',
                parameterIdReplaced
            );
            const translation = vm.get(parameterDescriptionVmObject);
            if (translation) {
                const translationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardGlobalParameterDescription(
                        recordName,
                        parameterId
                    );
                translation.crudState = 'U';
                translation.crudStateWas = 'U';
                translation.phantom = false;
                translation.set('_id', translationCode);
                translation.save();
            }

            const parameterEmptyTextVmObject = Ext.String.format(
                'theGlobalParameterEmptyText_{0}',
                parameterIdReplaced
            );
            const emptyTextTranslation = vm.get(parameterEmptyTextVmObject);
            if (emptyTextTranslation) {
                const translationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardGlobalParameterEmptyText(
                        recordName,
                        parameterId
                    );
                emptyTextTranslation.crudState = 'U';
                emptyTextTranslation.crudStateWas = 'U';
                emptyTextTranslation.phantom = false;
                emptyTextTranslation.set('_id', translationCode);
                emptyTextTranslation.save();
            }
        });
    },

    saveDashboardComponentLocalizedFieldLocales: function (vm, record) {
        record.components().each(function (component) {
            const recordName = record.get('name');
            const idComponent = component.get('_id');
            const idComponentReplaced = idComponent.replaceAll('-', '_');
            component.dataSourceParameters().each(function (parameter, index) {
                const parameterDescriptionLocaleVmObject = Ext.String.format(
                    'theComponentParameterDescription_{0}_{1}',
                    idComponentReplaced,
                    index
                );
                if (vm.get(parameterDescriptionLocaleVmObject)) {
                    const parameterDescriptionTranslationCode =
                        CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentParameterName(
                            recordName,
                            idComponent,
                            index
                        );
                    vm.set(
                        Ext.String.format('{0}._id', parameterDescriptionLocaleVmObject),
                        parameterDescriptionTranslationCode
                    );
                    vm.get(parameterDescriptionLocaleVmObject).save();
                }

                const parameterEmptyTextLocaleVmObject = Ext.String.format(
                    'theComponentParameterEmptyText_{0}_{1}',
                    idComponentReplaced,
                    index
                );
                if (vm.get(parameterEmptyTextLocaleVmObject)) {
                    const parameterEmptyTextTranslationCode =
                        CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentParameterEmptyText(
                            recordName,
                            idComponent,
                            index
                        );
                    vm.set(
                        Ext.String.format('{0}._id', parameterEmptyTextLocaleVmObject),
                        parameterEmptyTextTranslationCode
                    );
                    vm.get(parameterEmptyTextLocaleVmObject).save();
                }
            });

            if (component.getTilesStore) {
                component.getTilesStore().each(function (tile) {
                    const tileId = tile.get('_id');
                    const tileLabelVmObject = Ext.String.format(
                        'theComponentTileLabel_{0}',
                        tileId.replaceAll('-', '_')
                    );
                    if (vm.get(tileLabelVmObject)) {
                        const tileLabelTranslationCode =
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentTileLabel(
                                recordName,
                                tileId
                            );
                        vm.set(Ext.String.format('{0}._id', tileLabelVmObject), tileLabelTranslationCode);
                        vm.get(tileLabelVmObject).save();
                    }
                });
            }

            const componentDescritpionVmObject = Ext.String.format('theComponentDescription_{0}', idComponentReplaced);
            if (vm.get(componentDescritpionVmObject)) {
                const componentDescritpionTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentDescription(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', componentDescritpionVmObject), componentDescritpionTranslationCode);
                vm.get(componentDescritpionVmObject).save();
            }

            const categoryAxisTitleVmObject = Ext.String.format(
                'theComponentCategoryAxisTitle_{0}',
                idComponentReplaced
            );
            if (vm.get(categoryAxisTitleVmObject)) {
                const categoryAxisTitleTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentCategoryAxis(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', categoryAxisTitleVmObject), categoryAxisTitleTranslationCode);
                vm.get(categoryAxisTitleVmObject).save();
            }

            const valueAxisLabelVmObject = Ext.String.format('theComponentValueAxisLabel_{0}', idComponentReplaced);
            if (vm.get(valueAxisLabelVmObject)) {
                const valueAxisLabelTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentValueAxis(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', valueAxisLabelVmObject), valueAxisLabelTranslationCode);
                vm.get(valueAxisLabelVmObject).save();
            }
            const labelFieldVmObject = Ext.String.format('theComponentLabelField_{0}', idComponentReplaced);
            if (vm.get(labelFieldVmObject)) {
                const labelFieldTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfDashboardComponentLabelField(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', labelFieldVmObject), labelFieldTranslationCode);
                vm.get(labelFieldVmObject).save();
            }
        });
    },

    onAddrowBtnClick: function (button, event, eOpts) {
        // add row with 2 empty columns
        var view = this.getView().down('#layoutcontainer');
        view.lookupViewModel().get('theDashboard.layout');
        var store = view.down('grid').getStore();
        store.add({
            columns: [
                {
                    width: 0.5,
                    components: []
                },
                {
                    width: 0.5,
                    components: []
                }
            ]
        });
    },

    onRowsStoreDatachanged: function (store) {
        Ext.suspendLayouts();
        var view = this.getView().down('#layoutcontainer');
        view.removeAll();
        view.add([
            CMDBuildUI.util.administration.helper.GridHelper.getDragAndDropReorderGrid([
                {
                    xtype: 'widgetcolumn',
                    flex: 1,
                    widget: {
                        xtype: 'administration-content-dashboards-card-builder-row'
                    },
                    plugins: [],
                    variableRowHeight: true
                }
            ])
        ]);
        Ext.resumeLayouts();
    },

    moveUp: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex - 1, current);
    },

    moveDown: function (view, rowIndex, colIndex, item, e, record, row) {
        var store = record.store;
        var current = store.findRecord('id', record.get('id'));
        var currentIndex = store.findExact('id', record.get('id'), 0);
        store.remove(current, true);
        store.insert(currentIndex + 1, current);
    },

    deleteRow: function (view, rowIndex, colIndex, item, e, record, row) {
        const store = record.store;
        const componentsStore = this.getViewModel().get('theDashboard').components();

        record.get('columns').forEach(function (column) {
            (column.components || []).forEach(function (componentId) {
                const componentRecord = componentsStore.findRecord('_id', componentId);
                if (componentRecord) {
                    componentsStore.remove(componentRecord);
                }
            });
        });

        store.removeAt(rowIndex);
        this.onRowsStoreDatachanged();
    },

    addColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {
            var columns = record.get('columns');
            if (columns.length < 4) {
                columns.push({
                    components: []
                });
                record.set('width', undefined);
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                record.set('columns', columns);
                this.onRowsStoreDatachanged();
            }
        }
    },

    removeColumn: function (view, rowIndex, colIndex, item, e, record, row) {
        if (record.isModel) {
            var columns = record.get('columns');
            if (columns.length > 1) {
                Ext.Array.forEach(columns, function (column) {
                    column.width = undefined;
                });
                var wasRemoved = false;
                for (var i = 0; i < columns.length; i++) {
                    var isEmpty = !columns[i].components.length;
                    if (isEmpty) {
                        Ext.Array.removeAt(columns, i);
                        wasRemoved = true;
                        break;
                    }
                }
                if (!wasRemoved) {
                    CMDBuildUI.util.Notifier.showWarningMessage(
                        CMDBuildUI.locales.Locales.administration.dashboards.unabletoremovenonemptycolumn
                    );
                    return;
                }
                record.set('width', undefined);
                record.set('columns', columns);
                this.onRowsStoreDatachanged();
            }
        }
    },

    onDrop: function (node, data, overModel, dropPosition, eOpts) {
        data.view.refresh();
        this.view.ownerGrid.refreshIndex(data.view.getStore());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddParameterBtnClick: function (button, e, eOpts) {
        const view = this.getView();

        this.openAdministrationDetailsWindow({
            xtype: 'administration-content-dashboards-dashboard-card-globalparameters-form',
            viewModel: {
                data: {
                    owner: view,
                    theParameter: CMDBuildUI.model.dashboards.DataSourceParameter.create(),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    }
                }
            }
        });
    },

    /**
     * Show the contextual menu for a global parameter.
     *
     * @param {Ext.grid.Panel} grid The global parameters grid.
     * @param {Number} rowIndex The row index of the selected parameter.
     * @param {Number} colIndex The column index of the action.
     * @param {Ext.grid.column.Action} item The action column item.
     * @param {Event} e The click event.
     * @param {Ext.data.Model} record The selected parameter record.
     * @param {Object} eOpts The event options.
     */
    onParameterActionClick: function (grid, rowIndex, colIndex, item, e, record, eOpts) {
        const me = this;
        const target = e.getTarget('.x-grid-cell', 10) || e.target;

        const menu = Ext.create('Ext.menu.Menu', {
            items: [
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.open,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    handler: function () {
                        me.onViewParameterActionClick(record);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.edit,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    handler: function () {
                        me.onEditParameterActionClick(grid, rowIndex, colIndex, item, e, record, eOpts);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    handler: function () {
                        me.onDeleteParameterActionClick(grid, rowIndex, colIndex, item, e, record, eOpts);
                    }
                }
            ]
        });

        menu.showBy(target, 'tr-br?');
    },

    /**
     * Open a global parameter from the read-only action column.
     *
     * @param {Ext.grid.Panel} grid The global parameters grid.
     * @param {Number} rowIndex The row index of the selected parameter.
     * @param {Number} colIndex The column index of the action.
     * @param {Ext.grid.column.Action} item The action column item.
     * @param {Event} e The click event.
     * @param {Ext.data.Model} record The selected parameter record.
     * @param {Object} eOpts The event options.
     */
    onParameterViewActionClick: function (grid, rowIndex, colIndex, item, e, record, eOpts) {
        this.onViewParameterActionClick(record);
        return false;
    },

    /**
     * Open a global parameter in read-only mode.
     *
     * @param {Ext.data.Model} record The selected parameter record.
     */
    onViewParameterActionClick: function (record) {
        const view = this.getView();

        this.openAdministrationDetailsWindow({
            xtype: 'administration-content-dashboards-dashboard-card-globalparameters-form',
            viewModel: {
                data: {
                    owner: view,
                    theParameter: CMDBuildUI.model.dashboards.DataSourceParameter.create(record.getData()),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    }
                }
            }
        });
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Ext.grid.column.Action} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     * @param {Object} eOpts
     */
    onEditParameterActionClick: function (grid, rowIndex, colIndex, item, e, record, eOpts) {
        const view = this.getView();

        this.openAdministrationDetailsWindow({
            xtype: 'administration-content-dashboards-dashboard-card-globalparameters-form',
            viewModel: {
                data: {
                    owner: view,
                    theParameter: CMDBuildUI.model.dashboards.DataSourceParameter.create(record.getData()),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    }
                }
            }
        });
    },

    /**
     *
     * @param {Ext.grid.Panel} grid
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * @param {Ext.grid.column.Action} item
     * @param {Event} e
     * @param {Ext.data.Model} record
     * @param {Object} eOpts
     */
    onDeleteParameterActionClick: function (grid, rowIndex, colIndex, item, e, record, eOpts) {
        grid.getStore().remove(record);
    },

    openAdministrationDetailsWindow: function (config) {
        if (!config) {
            return false;
        }

        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        container.removeAll();
        container.addListener('beforeclose', function (panel) {
            const cancelBtn = panel.down('#cancelBtn');
            if (cancelBtn) {
                cancelBtn.fireEventArgs('click', [cancelBtn]);
            }
        });
        container.add(config);
    },

    columnsSizeBtnClick: function (button) {
        var me = button.up().ctrl;
        var values = button.value;
        var record = button.up().record;
        Ext.Array.forEach(record.get('columns'), function (item, index) {
            item.width = values[index];
        });
        me.onRowsStoreDatachanged();
    }
});
