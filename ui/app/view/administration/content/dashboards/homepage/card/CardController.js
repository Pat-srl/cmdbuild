Ext.define('CMDBuildUI.view.administration.content.dashboards.homepage.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-dashboards-homepage-card',

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
        }
    },
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
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

                // construct the tools bar
                const toolbar = view.down('#topFormToolbar');

                if (toolbar) {
                    const toolsConfig = vm.get('toolAction.config');

                    const tools = CMDBuildUI.util.administration.helper.FormHelper.getTools(
                        toolsConfig,
                        'homepages',
                        'theDashboard'
                    );

                    toolbar.add(tools);
                }
                // construct the form
                view.add(
                    CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getGeneralProperties(
                        'display',
                        view,
                        true
                    )
                );
                var layoutGrid = CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper.getLayout(
                    'display',
                    view
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
        var me = this,
            form = me.getView(),
            vm = me.getViewModel(),
            grid = vm.get('grid'),
            theDashboard = vm.get('theDashboard');
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
            rows.push(row.getData());
        });
        theDashboard.set('layout', {
            rows: rows
        });
        theDashboard.set('components', components);
        theDashboard.save({
            success: function (record, operation) {
                me.saveHomepageDescriptionLocales(vm, record);
                me.saveHomepageComponentLocalizedFieldLocales(vm, record);
                if (grid) {
                    grid.fireEventArgs('itemupdated', [record]);
                    form.container.component.fireEvent('closed');
                    CMDBuildUI.util.Utilities.showLoader(false);
                } else if (form.up('administration-maincontainer')) {
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getHomepageUrl(record.getId());
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

                        var displayDescription;
                        if (record.get('name') === '_default') {
                            displayDescription = CMDBuildUI.locales.Locales.administration.common.strings['default'];
                        } else {
                            var group = Ext.getStore('groups.Groups').findRecord(
                                'name',
                                record.get('name'),
                                0,
                                false,
                                false,
                                true
                            );
                            displayDescription = group ? group.get('description') : record.get('description');
                        }

                        CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy(
                            'href',
                            nextUrl,
                            displayDescription,
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
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageDescription(
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
            vm = view.getViewModel();

        CMDBuildUI.util.Utilities.showLoader(true);

        CMDBuildUI.util.Stores.loadGroupsStore()
            .then(function (roles) {
                roles = CMDBuildUI.util.administration.helper.SortHelper.sort(roles, 'description');
                var groups = roles.map(function (role) {
                    return {
                        value: role.get('name'),
                        label: role.get('description')
                    };
                });

                var viewModel = {
                    data: {
                        theDashboard: vm.get('theDashboard'),
                        grid: vm.get('grid'),
                        unusedRoles: groups,
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        actions: {
                            view: false,
                            edit: true,
                            add: false
                        }
                    }
                };

                view.up('administration-content-homepages-tabpanel').getViewModel().toggleEnableTabs(0);
                var container = view.container.component;
                container.removeAll();

                container.add({
                    xtype: 'view-administration-content-dashboards-homepage-card',
                    viewModel: viewModel,
                    data: {}
                });

                CMDBuildUI.util.Utilities.showLoader(false);
            })
            .catch(function () {
                CMDBuildUI.util.Utilities.showLoader(false);
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
                    CMDBuildUI.util.Ajax.setActionId('delete-homepage');
                    theDashboard.erase({
                        success: function (record, operation) {
                            if (grid) {
                                grid.fireEventArgs('reload', [record, 'delete']);
                            }
                            if (view.source) {
                                view.source.container.component.remove(view.source);
                            } else if (view.up('administration-maincontainer')) {
                                var apiHelper = CMDBuildUI.util.administration.helper.ApiHelper.client,
                                    nextUrl = apiHelper.getHomepageUrl(),
                                    recordUrl = apiHelper.getHomepageUrl(record.getId());

                                CMDBuildUI.util.Stores.loadHomepagesStore();
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

    saveHomepageDescriptionLocales: function (vm, record) {
        if (vm.get('actions.add')) {
            var translations = ['theDescriptionTranslation'];
            var keyFunction = ['getLocaleKeyOfHomepageDescription'];
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

    saveHomepageComponentLocalizedFieldLocales: function (vm, record) {
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
                        CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentParameterName(
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
                            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentTileLabel(
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
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentDescription(
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
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentCategoryAxis(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', categoryAxisTitleVmObject), categoryAxisTitleTranslationCode);
                vm.get(categoryAxisTitleVmObject).save();
            }

            const valueAxisLabelVmObject = Ext.String.format('theComponentValueAxisLabel_{0}', idComponentReplaced);
            if (vm.get(valueAxisLabelVmObject)) {
                const valueAxisLabelTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentValueAxis(
                        recordName,
                        idComponent
                    );
                vm.set(Ext.String.format('{0}._id', valueAxisLabelVmObject), valueAxisLabelTranslationCode);
                vm.get(valueAxisLabelVmObject).save();
            }
            const labelFieldVmObject = Ext.String.format('theComponentLabelField_{0}', idComponentReplaced);
            if (vm.get(labelFieldVmObject)) {
                const labelFieldTranslationCode =
                    CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfHomepageComponentLabelField(
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
                        CMDBuildUI.locales.Locales.administration.homepages.unabletoremovenonemptycolumn
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
