Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin', {
    mixinId: 'administration-importexportmixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        if (vm.get('showInMainPanel')) {
            this.handleEditInMainPanel(vm, view);
        } else {
            this.handleEditInPopup(view, vm);
        }
    },

    handleEditInMainPanel: function (vm, view) {
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        view.up('#tabpanel').getViewModel().toggleEnableTabs(0);
        CMDBuildUI.util.Navigation.addIntoMainAdministrationContent(
            'administration-content-importexport-datatemplates-view',
            {
                viewModel: {
                    data: {
                        showInMainPanel: true,
                        templateId: vm.get('templateId'),
                        action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                        actions: {
                            view: false,
                            edit: true,
                            add: false
                        }
                    }
                }
            }
        );
    },

    handleEditInPopup: function (view, vm) {
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var viewModel = {
            data: {
                showInMainPanel: false,
                theGateTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theGateTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                actions: {
                    edit: true,
                    view: false,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: viewModel
        });
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === 'yes') {
                    me.performDelete(button, vm);
                }
            },
            this
        );
    },

    performDelete: function (button, vm) {
        if (button.el.dom) {
            button.setDisabled(true);
        }
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [true]);
        var theGateTemplate = this.getViewModel().get('theGateTemplate');
        CMDBuildUI.util.Ajax.setActionId('delete-importexporttemplate');
        var grid =
            CMDBuildUI.util.Navigation.getMainAdministrationContainer().down(
                'administration-content-importexport-datatemplates-grid'
            ) ||
            this.getView().up('grid') ||
            this.getView().lookupViewModel().get('grid');
        var recordHref = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(
            theGateTemplate.get('_id')
        );

        if (!vm.get('showInMainPanel') || grid) {
            this.handleDeleteInGrid(grid, theGateTemplate, recordHref);
        } else {
            this.handleDeleteInMainPanel(theGateTemplate, button, recordHref);
        }
    },

    handleDeleteInGrid: function (grid, theGateTemplate, recordHref) {
        if (grid.getStore().source) {
            grid.getStore().remove(theGateTemplate);
            grid.getStore().source.sync();
        } else {
            grid.fireEventArgs('removetemplate', [theGateTemplate, grid]);
        }
        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
        CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', recordHref);
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
    },

    handleDeleteInMainPanel: function (theGateTemplate, button, recordHref) {
        var me = this;
        theGateTemplate.erase({
            failure: function (error) {
                theGateTemplate.reject();
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(
                    theGateTemplate.get('_id')
                );
                me.redirectTo(nextUrl, true);
            },
            success: function (record, operation) {
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl();
                CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function () {
                    CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy(
                        'href',
                        Ext.util.History.getToken(),
                        nextUrl,
                        me
                    );
                });
            },
            callback: function (record, reason) {
                if (button.el.dom) {
                    button.setDisabled(false);
                }
                Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false]);
            }
        });
    },

    onViewBtnClick: function () {
        var view = this.getView();
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var vm = view.getViewModel();
        var viewModel = {
            data: {
                showInMainPanel: false,
                theGateTemplate: view.getViewModel().get('selected') || view.getViewModel().get('theGateTemplate'),
                grid: vm.get('grid') || this.getView().up().grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                actions: {
                    edit: false,
                    view: true,
                    add: false
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: viewModel
        });
    },

    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        theGateTemplate.set('active', !theGateTemplate.get('active'));

        me.setColumnsData();
        if (!theGateTemplate.get('columns').length) {
            CMDBuildUI.util.Notifier.showWarningMessage(
                CMDBuildUI.locales.Locales.administration.importexport.texts.emptyattributegridmessage
            );
            CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
        } else if (theGateTemplate.isValid()) {
            this.prepareSaveTemplate(theGateTemplate);
            this.saveTemplate(theGateTemplate, button, vm);
        }
    },

    prepareSaveTemplate: function (theGateTemplate) {
        if (theGateTemplate.get('type') === CMDBuildUI.model.importexports.Template.types['export']) {
            Ext.Array.forEach(theGateTemplate.get('columns'), function (item) {
                item['default'] = '';
            });
        }
        this.setColumnsData();

        var mergeMode = theGateTemplate.get('mergeMode_when_missing_update_value');
        if (Ext.isArray(mergeMode)) {
            var lookupArraySeparator = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.lookuparray.separator
            );
            theGateTemplate.set('mergeMode_when_missing_update_value', mergeMode.join(lookupArraySeparator));
        }
    },

    saveTemplate: function (theGateTemplate, button, vm) {
        var me = this;
        theGateTemplate.save({
            success: function (record, operation) {
                me.onSaveSuccess(record, vm, button);
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
            }
        });
    },

    onSaveSuccess: function (record, vm, button) {
        var me = this;
        var grid = vm.get('grid')
            ? vm.get('grid')
            : this.getView().up('administration-content-importexport-datatemplates-grid') || this.getView().up('grid');
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore().then(function () {
            if (!vm.get('showInMainPanel') || grid) {
                me.updateGridRecord(record, grid);
            }
        });
    },

    updateGridRecord: function (record, grid) {
        var lookupArraySeparator = CMDBuildUI.util.helper.Configurations.get(
            CMDBuildUI.model.Configuration.lookuparray.separator
        );
        var regex = new RegExp(Ext.String.format('.*{0}.*', lookupArraySeparator));
        try {
            if (record && regex.test(record.get('mergeMode_when_missing_update_value'))) {
                record.set(
                    'mergeMode_when_missing_update_value',
                    record.get('mergeMode_when_missing_update_value').split(lookupArraySeparator)
                );
            }
        } catch (e) {
            console.error('Error updating grid record:', e);
        }
        grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, this]);
    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        if (!vm.get('showInMainPanel')) {
            this.handleCloneInPopup(vm);
        } else {
            this.handleCloneInMainPanel(vm);
        }
    },

    handleCloneInPopup: function (vm) {
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newImportExportTemplate = vm.get('theGateTemplate').copyForClone();
        var grid = vm.get('grid') || this.getView().up().grid;
        var viewModel = {
            data: {
                theGateTemplate: newImportExportTemplate,
                showInMainPanel: false,
                grid: grid,
                action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                actions: {
                    edit: false,
                    view: false,
                    add: false,
                    clone: true
                }
            }
        };

        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: viewModel,
            listeners: {
                savesuccess: this.onCloneSaveSuccess.bind(this)
            }
        });
    },

    handleCloneInMainPanel: function (vm) {
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCloneImportExportDataTemplatesUrl(
            vm.get('theGateTemplate._id')
        );
        this.redirectTo(nextUrl);
    },

    onCloneSaveSuccess: function (record, requestMethod, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.lookupViewModel();
        var grid = vm.get('grid') || view.up().grid;
        var theGate = vm.get('theGate');
        if (theGate) {
            var handler = theGate.handlers().first();
            handler.addTemplate(record.get('code'));
            theGate.save({
                success: function (gate, gateOperation) {
                    var eventToCall = requestMethod === 'PUT' ? 'itemupdated' : 'itemcreated';
                    handler.getTemplates().then(function (templatesStore) {
                        vm.get('allGateTemplates').setData(templatesStore.getRange());
                        grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [
                            grid,
                            record,
                            me
                        ]);
                    });
                }
            });
        }
    },

    onRowDblclick: function (row, record, element, rowIndex, e, eOpts) {
        var view = this.getView();
        var container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        var formInRow = view.ownerGrid.getPlugin('administration-forminrowwidget');
        formInRow.removeAllExpanded(record);
        view.setSelection(record);
        container.removeAll();
        container.add({
            xtype: 'administration-content-importexport-datatemplates-card',
            viewModel: {
                data: {
                    showInMainPanel: false,
                    theGateTemplate: record,
                    grid: view.ownerGrid,
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

    csvCharsetStoreLoad: function () {
        var vm = this.getViewModel();
        vm.set('csvCharsetStoreLoaded', true);
    },

    onAllSelectedAttributesStoreDatachanged: function (store) {
        var vm = this.getViewModel();
        vm.set('dataFormatHidden', true);
        vm.set('dataFormatTiviewHidden', true);
        vm.set('dataFormatDateHidden', true);
        vm.set('dataFormatDateTiviewHidden', true);
        vm.set('dataFormatDecimalHidden', true);
        vm.bind(
            {
                bindTo: {
                    allAttrStore: '{theGateTemplate.columns}'
                },
                single: true
            },
            function (data) {
                store.each(function (item) {
                    var templateAttribute = data.allAttrStore.findRecord(
                        'attribute',
                        item.get('name'),
                        false,
                        false,
                        true,
                        true
                    );
                    if (templateAttribute) {
                        templateAttribute.set('_attribute_description', item.get('description'));
                    }
                    vm.manageDataFormatFieldset(item);
                });
            }
        );
    },

    setColumnsData: function () {
        var vm = this.getViewModel();
        var theGateTemplate = vm.get('theGateTemplate');
        var columns = [];
        var filter = JSON.parse(theGateTemplate.get('importFilter'));
        var extraHiddenColumns = [];
        Ext.Array.forEach(vm.get('allSelectedAttributesStore').getRange(), function (item) {
            columns.push(item.getData());
        });
        if (filter) {
            extraHiddenColumns = this.makeColumnsFromFilter(filter);
            if (extraHiddenColumns.length) {
                extraHiddenColumns.forEach(function (eC) {
                    if (
                        !Ext.Array.findBy(columns, function (c) {
                            return c.attribute === eC.attribute;
                        })
                    ) {
                        columns.push(eC);
                    }
                });
            }
        }
        theGateTemplate.set('columns', columns);
    },

    makeColumnsFromFilter: function (filter) {
        var newColumns = [];
        if (filter.attribute) {
            if (filter.attribute.and) {
                filter.attribute.and.forEach(function (attr) {
                    newColumns.push({
                        attribute: attr.simple.attribute,
                        columnName: attr.simple.attribute,
                        default: '',
                        index: 0,
                        mode: 'ignore'
                    });
                });
            } else if (filter.attribute.or) {
                filter.attribute.or.forEach(function (attr) {
                    newColumns.push({
                        attribute: attr.simple.attribute,
                        columnName: attr.simple.attribute,
                        default: '',
                        index: 0,
                        mode: 'ignore'
                    });
                });
            } else if (filter.attribute.simple) {
                newColumns.push({
                    attribute: filter.attribute.simple.attribute,
                    columnName: filter.attribute.simple.attribute,
                    default: '',
                    index: 0,
                    mode: 'ignore'
                });
            }
        }

        return newColumns;
    },

    onEditImportFilterBtn: function (button, e, eOpts) {
        var view = this;
        var vm = view.getViewModel();
        var record = vm.get('theGateTemplate');
        var actions = vm.get('actions');
        var recordFilter = record.get('importFilter').length ? JSON.parse(record.get('importFilter')) : {};

        var popupTitle = CMDBuildUI.locales.Locales.administration.importexport.texts.filterfortemplate;
        var type =
            CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get('targetName')) ||
            vm.get('theGateTemplate.targetType');

        popupTitle = Ext.String.format(popupTitle, record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType:
                record.get('targetType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    : record.get('targetType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
                      ? CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
                      : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get('targetName'),
            configuration: recordFilter
        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get('targetName'),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };

        this.showImportFilterPopup(viewmodel, record, popupTitle);
    },

    showImportFilterPopup: function (viewmodel, record, popupTitle) {
        var view = this;
        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        attrbutePanel.onlyOneLevel = true;
        attrbutePanel.allowArbitraryAttributeName = true;
        attrbutePanel.useTextFieldForValue = true;
        var listeners = {
            applyfilter: function (panel, _filter, _eOpts) {
                view.onApplyFilter(_filter);
                view.popup.close();
            },
            saveandapplyfilter: function (panel, _filter, _eOpts) {
                view.onSaveAndApplyFilter(_filter);
                view.popup.close();
            },
            popupclose: function (_eOpts) {
                view.popup.close();
            }
        };

        var dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: view.getViewModel().get('actions.view'),
                items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons(
                    {
                        handler: function (_button) {
                            CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(
                                view.popup,
                                record,
                                'importFilter'
                            );
                        }
                    },
                    {
                        handler: function () {
                            view.popup.close();
                        }
                    }
                )
            }
        ];

        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel],
            dockedItems: dockedItems,
            listeners: listeners
        };

        if (
            view.getView().lookupViewModel().get('theGateTemplate.targetType') ===
            CMDBuildUI.model.administration.MenuItem.types.view
        ) {
            Ext.Array.remove(content.items, relationsPanel);
        }

        view.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popupTitle,
            content,
            {},
            {
                ui: 'administration-actionpanel',
                viewModel: {
                    data: {
                        index: '0',
                        grid: {},
                        record: record,
                        displayOnly: viewmodel.data.actions.view
                    }
                }
            }
        );
    },

    onHiddenImportKeyAttributeAfterRender: function (input) {
        var form = input.up('form');
        var vm = input.lookupViewModel();
        if (!vm.get('actions.view')) {
            vm.bind(
                {
                    bindTo: {
                        targetType: '{theGateTemplate.targetType}',
                        type: '{theGateTemplate.type}',
                        importMode: '{theGateTemplate._importMode}'
                    }
                },
                function (data) {
                    if (
                        data.importMode === CMDBuildUI.model.importexports.Template.importModes.add ||
                        data.type === 'export' ||
                        data.targetType === 'domain'
                    ) {
                        input.allowBlank = true;
                    } else {
                        input.allowBlank = false;
                    }

                    if (form && form.form) {
                        form.form.checkValidity();
                    }
                }
            );
        }
    },

    onMergeModeChange: function (combo, newValue, oldValue) {
        var form = combo.up('form');
        if (form) {
            var importKeyAttribute = form.down('#importKeyAttribute_input');
            if (importKeyAttribute) {
                var allowBlank =
                    form.lookupViewModel().get('isDomain') ||
                    newValue === CMDBuildUI.model.importexports.Template.missingRecords.nomerge;
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(importKeyAttribute, allowBlank, form);
            }
        }
    },

    onTypeChange: function (combo, newValue, oldValue) {
        if (!Ext.isEmpty(oldValue)) {
            combo.up('form').down('[name="targetName"]').setValue('');
        }
    },

    onTargetTypeChange: function (input, newVal, oldVal) {
        var form = input.up('form');
        if (form) {
            var vm = input.lookupViewModel();
            var typeInput = form.down('#type_input');
            if (typeInput) {
                var isViewObject = newVal === CMDBuildUI.model.administration.MenuItem.types.view;
                var isProcessObject = newVal === CMDBuildUI.util.helper.ModelHelper.objecttypes.process;
                typeInput.setDisabled(isViewObject || isProcessObject || vm.get('actions.edit'));
                if (isViewObject || isProcessObject) {
                    typeInput.setValue(CMDBuildUI.model.importexports.Template.types['export']);
                }
            }
            var targetNameField = input.up('fieldset').down('[name="targetName"]');
            var targetNameFieldContainer = targetNameField.up('fieldcontainer');
            if (oldVal) {
                vm.resetAllAttributesStores();
            }
            if (newVal && (input.getXType() === 'combobox' || input.getXType() === 'combo')) {
                if (targetNameField.getValue() !== '' && oldVal) {
                    targetNameField.setValue('');
                }
                if (input.getSelectedRecord()) {
                    targetNameFieldContainer.setFieldLabel(input.getSelectedRecord().get('label'));
                }
            }
        }
    },

    initStores: function () {
        CMDBuildUI.util.Stores.loadImportExportTemplatesStore();
        CMDBuildUI.util.Stores.loadEmailAccountsStore();
        CMDBuildUI.util.Stores.loadEmailTemplatesStore();
    },

    setupViewModel: function () {
        var view = this;
        var vm = this.getViewModel();

        vm.bind(
            {
                bindTo: '{allSelectedAttributesStore}'
            },
            function (store) {
                view.onAllSelectedAttributesStoreDatachanged(store);
            }
        );

        if (!vm.get('showInMainPanel')) {
            this.setupGateTemplate(vm);
        }
    },

    setupGateTemplate: function (vm) {
        if (!vm.get('theGateTemplate')) {
            if (!vm.get('grid')) {
                vm.set('grid', vm.getParent().getView());
            }
            vm.set('theGateTemplate', vm.get('grid').getSelection());
        }

        if (vm.get('theGateTemplate').phantom) {
            Ext.Array.forEach(vm.get('theGateTemplate').get('columns'), function (item) {
                vm.get('theGateTemplate').columns().add(item);
            });
        } else {
            vm.linkTo('theGateTemplate', {
                type: 'CMDBuildUI.model.importexports.Template',
                id: vm.get('grid').getSelection()[0].get('_id')
            });
        }
    },

    onTargetNameChange: function (input, newValue, oldValue) {
        var vm = this.getView().lookupViewModel();
        var obj = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(newValue, vm.get('theGateTemplate.targetType'));
        if (obj) {
            if (oldValue && oldValue !== obj.get('name')) {
                vm.resetAllAttributesStores();
            }
            var allowedAttributes = [CMDBuildUI.util.helper.ModelHelper.extraFields.notes];
            var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(
                CMDBuildUI.model.Configuration.multitenant.enabled
            );
            if (allowTenantAttribute) {
                allowedAttributes.push('IdTenant');
            }
            obj.getAttributes(true).then(function (attributeStore) {
                var attributes = Ext.Array.filter(attributeStore.getRange(), function (item) {
                    return item.get('active') && item.canAdminShow(allowedAttributes);
                });
                if (!vm.destroyed) {
                    vm.set('rawAttributeStore', attributeStore);
                    vm.set('allClassOrDomainAttributes', attributes);
                    vm.addDefaultDomainAttributes(obj);
                }
            });
        }
    },

    onFileFormatChange: function (combo, newValue, oldValue) {
        var panel = combo.up('panel');
        var attributeCombo = panel.down('#selectAttributeForGrid');
        if (attributeCombo) {
            attributeCombo.setStoreFilter();
        }
    },

    onOpenTargetTriggerClick: function (f, trigger, eOpts) {
        var url,
            targetType = this.getView().lookupViewModel().get('theGateTemplate.targetType'),
            target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(f.getValue());
        switch (targetType) {
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(target.get('name'));
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(target.get('name'));
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.view:
                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getJoinViewUrl(f.getValue());
                break;
            case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                url = CMDBuildUI.util.administration.helper.ApiHelper.client.getDomainUrl(target.get('name'));
                break;
            default:
                return;
        }
        CMDBuildUI.util.Utilities.closeAllPopups();
        CMDBuildUI.util.Utilities.redirectTo(url);
    },

    updateFieldsetVisibility: function (data) {
        if (data.fieldsadded && data.type && this.getView().down('#importfieldset')) {
            var me = this;
            var view = this.getView();
            var vm = this.getViewModel();
            var importFieldset = view.down('#importfieldset');
            var exportFilterFieldset = view.down('#exportfilterfieldset');

            switch (data.type) {
                case CMDBuildUI.model.importexports.Template.types['import']:
                    importFieldset.setHidden(false);
                    exportFilterFieldset.setHidden(true);
                    break;
                case CMDBuildUI.model.importexports.Template.types['export']:
                    importFieldset.setHidden(true);
                    exportFilterFieldset.setHidden(vm.get('isDomain'));
                    break;
                case CMDBuildUI.model.importexports.Template.types.importexport:
                    importFieldset.setHidden(false);
                    exportFilterFieldset.setHidden(vm.get('isDomain'));
                    break;
                default:
                    importFieldset.setHidden(true);
                    exportFilterFieldset.setHidden(true);
                    break;
            }
            Ext.asap(function () {
                me.checkFormValidity();
            });
        }
    },
    onCsvSeparatorContainerHide: function (component, eOpts) {
        var input = component.down('#csv_separator_input');
        if (input) {
            input.setValue('');
            this.updateFieldAllowBlank(input, true);
        }
    },

    onCsvSeparatorContainerShow: function (component, eOpts) {
        var input = component.down('#csv_separator_input');
        if (input) {
            this.updateFieldAllowBlank(input, false);
        }
    },

    onFileFormatContainerHide: function (component, eOpts) {
        var input = component.down('#fileFormat_input');
        if (input) {
            input.setValue('');
            this.updateFieldAllowBlank(input, true);
        }
    },

    onFileFormatContainerShow: function (component, eOpts) {
        var input = component.down('#fileFormat_input');
        if (input) {
            this.updateFieldAllowBlank(input, false);
        }
    },

    onCharsetContainerHide: function (component, eOpts) {
        var input = component.down('#charset_input');
        if (input) {
            input.setValue(null);
        }
    },

    onSourceContainerHide: function (component, eOpts) {
        var input = component.down('#source_input');
        if (input) {
            input.setValue('');
            this.updateFieldAllowBlank(input, true);
        }
    },

    onSourceContainerShow: function (component, eOpts) {
        var input = component.down('#source_input');
        if (input) {
            this.updateFieldAllowBlank(input, false);
        }
    },

    onMergeModeWhenMissingUpdateAttrHide: function (component, eOpts) {
        var input = component.up().down('#combo_mergeMode_when_missing_update_attr');
        if (input) {
            this.updateFieldAllowBlank(input, true);
        }
    },

    onMergeModeWhenMissingUpdateAttrShow: function (component, eOpts) {
        var input = component.up().down('#combo_mergeMode_when_missing_update_attr');
        if (input) {
            this.updateFieldAllowBlank(input, false);
        }
    },

    onMergeModeWhenMissingUpdateValueHide: function (component, eOpts) {
        this.updateFieldAllowBlank(component, true);
    },

    onMergeModeWhenMissingUpdateValueShow: function (component, eOpts) {
        this.updateFieldAllowBlank(component, false);
    },

    updateFieldAllowBlank: function (field, value) {
        if (field) {
            CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, value, field.up('form'));
        }
    },

    onSelectAttributeAfterRender: function (field) {
        field.lookupViewModel().bind(
            {
                bindTo: {
                    store: '{allClassOrDomainsAtributesFiltered}'
                },
                single: true
            },
            function () {
                field.setStoreFilter();
            }
        );
    },

    renderKeyAttributeDescription: function (value, cell, record) {
        var vm = this.getView().lookupViewModel();

        vm.bind(
            {
                bindTo: {
                    allAttributesStore: '{allAttributesStore}'
                },
                single: true
            },
            function (data) {
                if (!value) {
                    var attribute = data.allAttributesStore.findRecord('name', record.get('attribute'));
                    if (attribute) {
                        record.set('_attribute_description', attribute.get('description'));
                    } else {
                        record.set('_attribute_description', record.get('attribute'));
                    }
                }
            }
        );

        return value;
    },

    onMergeModeBeforeShow: function () {
        var input = this.getView().down('#mergeMode_input');
        if (input) {
            input.forceSelection = true;
        }
    },

    onMergeModeBeforeHide: function () {
        var input = this.getView().down('#mergeMode_input');
        if (input) {
            input.forceSelection = false;
        }
        if (input.getValue() === null) {
            input.setValue(CMDBuildUI.model.importexports.Template.missingRecords.nomerge);
        }
    },

    onHandleMissingRecordsOnErrorHide: function (fieldcontainer) {
        fieldcontainer.down('checkbox').setValue(false);
    },

    onMergeModeWhenMissingUpdateAttrChange: function (combo, newValue, oldValue) {
        var store = combo.getStore(),
            vm = combo.lookupViewModel(),
            record;
        if (store) {
            record = store.findRecord('name', newValue);
        } else {
            record = vm.get('allAttributes').find(function (attribute) {
                return attribute.get('name') === newValue;
            });
        }
        if (record) {
            vm.set('theGateTemplate._mergeMode_when_missing_update_attr_description', record.get('description'));
        }
    },

    onMergeModeWhenMissingUpdateAttrBeforeSelect: function (combo, attributename, oldValue) {
        if (oldValue) {
            var valueField = combo
                .up('form')
                .getForm()
                .getFields()
                .findBy(function (item) {
                    return item.itemId === 'mergeMode_when_missing_update_value_input';
                });
            if (valueField) {
                valueField.up('#valueContainer').removeAll();
            }
        }
    },

    renderAttributeDescription: function (value, cell, record) {
        var vm = this.getView().lookupViewModel();

        vm.bind(
            {
                bindTo: {
                    rawAttributeStore: '{rawAttributeStore}'
                },
                single: true
            },
            function (data) {
                if (!value && data.rawAttributeStore) {
                    var attribute = data.rawAttributeStore.findRecord(
                        'name',
                        record.get('attribute'),
                        false,
                        false,
                        true,
                        true
                    );
                    if (attribute) {
                        switch (attribute.get('type')) {
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date:
                                vm.set('dataFormatHidden', false);
                                vm.set('dataFormatDateHidden', false);
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time:
                                vm.set('dataFormatHidden', false);
                                vm.set('dataFormatTimeHidden', false);
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime:
                                vm.set('dataFormatHidden', false);
                                vm.set('dataFormatTimeHidden', false);
                                vm.set('dataFormatDateHidden', false);
                                vm.set('dataFormatDateTimeHidden', false);
                                break;
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double:
                            case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal:
                                vm.set('dataFormatHidden', false);
                                vm.set('dataFormatDecimalHidden', false);
                                break;
                        }
                        record.set('_attribute_description', attribute.get('description'));
                    } else {
                        record.set('_attribute_description', record.get('attribute'));
                    }
                }
            }
        );

        return value;
    },

    onModeComboBeforeRender: function (combo) {
        var grid = combo.up('grid');
        var vm = grid.lookupViewModel();
        var record = grid.editingPlugin.context.record;
        var attribute;
        var allAttributes = vm.get('allClassOrDomainsAtributes');
        if (allAttributes && allAttributes.getData().length) {
            attribute = allAttributes.findRecord('name', record.get('attribute'), false, false, true, true);
        }
        if (
            attribute &&
            (['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 ||
                attribute.get('_id') === 'IdTenant')
        ) {
            combo.setDisabled(false);
            combo.setHideTrigger(false);
        } else if (['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
            combo.setDisabled(false);
            combo.setStore(combo.lookupViewModel().get('attributeModesReferenceStore'));
            combo.getStore().setData(vm.get('attributeModes'));
        } else {
            combo.setDisabled(true);
        }
    },

    onModeComboExpand: function (combo) {
        var grid = combo.up('grid');
        var vm = grid.lookupViewModel();
        var record = grid.editingPlugin.context.record;
        var attribute;
        var allAttributes = vm.get('allClassOrDomainsAtributes');
        if (allAttributes && allAttributes.getData().length) {
            attribute = allAttributes.findRecord('name', record.get('attribute'), false, false, true, true);
        }
        if (
            attribute &&
            (['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 ||
                attribute.get('_id') === 'IdTenant')
        ) {
            vm.get('attributeModesReferenceStore').setData(
                attribute.get('type').startsWith('lookup') ? vm.get('attributeModesLookup') : vm.get('attributeModes')
            );
        } else if (['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
            combo.getStore().setData(vm.get('attributeModes'));
        }
    },

    renderAttributeMode: function (value, cell, record, rowIndex, colIndex, store, grid) {
        if (!value || value === 'default') {
            return CMDBuildUI.locales.Locales.administration.common.labels.default;
        }
        var vm = this.getView().lookupViewModel();
        var attribute;
        var allAttributes = vm.get('allClassOrDomainsAtributes');
        if (allAttributes && allAttributes.getData().length) {
            attribute = allAttributes.findRecord('_id', record.get('attribute'), false, false, true, true);
        }
        if (
            !value &&
            attribute &&
            ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1
        ) {
            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
        } else if (!value && ['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1) {
            return CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode;
        }
        var attributeModesStore = vm.get('attributeModesReferenceStore');
        if (attributeModesStore) {
            if (attribute) {
                attributeModesStore.setData(
                    attribute.get('type').startsWith('lookup')
                        ? vm.get('attributeModesLookup')
                        : vm.get('attributeModes')
                );
            }
            var mode = attributeModesStore.findRecord('value', value, false, false, true, true);
            return mode && mode.get('label');
        }
        return value;
    },

    onTargetNameInputChange: function (combo, newValue, oldValue) {
        var form = combo.up('form');
        var vm = combo.lookupViewModel();
        if (oldValue) {
            vm.set('theGateTemplate.viewrgeMode_when_missing_update_attr', null);
        }
        form.form.checkValidity();
    },

    onEditExportFilterBtn: function (button, e, eOpts) {
        var view = this;
        var vm = view.getViewModel();
        var record = vm.get('theGateTemplate');
        var actions = vm.get('actions');
        var recordFilter = record.get('exportFilter').length ? JSON.parse(record.get('exportFilter')) : {};

        var popupTitle = CMDBuildUI.locales.Locales.administration.importexport.texts.filterfortemplate;
        var type =
            CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(record.get('targetName')) ||
            vm.get('theGateTemplate.targetType');

        popupTitle = Ext.String.format(popupTitle, record.get('description'));

        var filter = Ext.create('CMDBuildUI.model.base.Filter', {
            name: CMDBuildUI.locales.Locales.filters.newfilter,
            ownerType:
                record.get('targetType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    ? CMDBuildUI.util.helper.ModelHelper.objecttypes.view
                    : record.get('targetType') === CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
                      ? CMDBuildUI.util.helper.ModelHelper.objecttypes.domain
                      : CMDBuildUI.util.helper.ModelHelper.objecttypes.klass,
            description: CMDBuildUI.locales.Locales.filters.newfilter,
            target: record.get('targetName'),
            configuration: recordFilter
        });

        var viewmodel = {
            data: {
                objectType: type,
                objectTypeName: record.get('targetName'),
                theFilter: filter,
                actions: Ext.copy(actions)
            }
        };

        this.showExportFilterPopup(viewmodel, record, popupTitle);
    },
    onRemoveExportFilterBtn: function (button, e, eOpts) {
        var vm = this.getViewModel();

        // Mostra un messaggio di conferma prima di rimuovere il filtro
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.actions.remove,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeletefilter,
            function (btn) {
                if (btn === 'yes') {
                    // Rimuovi il filtro
                    vm.set('theGateTemplate.exportFilter', '{}');

                    // Notifica l'utente
                    CMDBuildUI.util.Notifier.showSuccessMessage(
                        CMDBuildUI.locales.Locales.administration.common.messages.success,
                        CMDBuildUI.locales.Locales.administration.common.messages.filterremoved
                    );
                }
            }
        );
    },

    showExportFilterPopup: function (viewmodel, record, popupTitle) {
        var view = this;
        var attrbutePanel = this.getAttributesFilterTab(viewmodel, record);
        var relationsPanel = this.getRelationsFilterTab(viewmodel, record);
        var functionPanel = this.getFunctionFilterTab(viewmodel, record);
        var fulltextPanel = this.getFulltextFilterTab(viewmodel, record);

        var listeners = {
            applyfilter: function (panel, _filter, _eOpts) {
                view.onApplyFilter(_filter);
                view.popup.close();
            },
            saveandapplyfilter: function (panel, _filter, _eOpts) {
                view.onSaveAndApplyFilter(_filter);
                view.popup.close();
            },
            popupclose: function (_eOpts) {
                view.popup.close();
            }
        };

        var dockedItems = [
            {
                xtype: 'toolbar',
                dock: 'bottom',
                ui: 'footer',
                hidden: view.getViewModel().get('actions.view'),
                items: CMDBuildUI.util.administration.helper.FormHelper.getOkCloseButtons(
                    {
                        handler: function (_button) {
                            CMDBuildUI.util.administration.helper.FilterHelper.setRecordFilterFromPanel(
                                view.popup,
                                record,
                                'exportFilter'
                            );
                        }
                    },
                    {
                        handler: function () {
                            view.popup.close();
                        }
                    }
                )
            }
        ];

        var content = {
            xtype: 'tabpanel',
            cls: 'administration',
            ui: 'administration-tabandtools',
            items: [attrbutePanel, relationsPanel, functionPanel, fulltextPanel],
            dockedItems: dockedItems,
            listeners: listeners
        };

        if (
            view.getView().lookupViewModel().get('theGateTemplate.targetType') ===
            CMDBuildUI.model.administration.MenuItem.types.view
        ) {
            Ext.Array.remove(content.items, relationsPanel);
        }

        view.popup = CMDBuildUI.util.Utilities.openPopup(
            'filterpopup',
            popupTitle,
            content,
            {},
            {
                ui: 'administration-actionpanel',
                viewModel: {
                    data: {
                        index: '0',
                        grid: {},
                        record: record,
                        canedit: true
                    }
                }
            }
        );
    },

    onRemoveImportFilterBtn: function (button, e, eOpts) {
        var vm = this.getViewModel();

        // Mostra un messaggio di conferma prima di rimuovere il filtro
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.actions.remove,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeletefilter,
            function (btn) {
                if (btn === 'yes') {
                    // Rimuovi il filtro
                    vm.set('theGateTemplate.importFilter', '{}');

                    // Notifica l'utente
                    CMDBuildUI.util.Notifier.showSuccessMessage(
                        CMDBuildUI.locales.Locales.administration.common.messages.success,
                        CMDBuildUI.locales.Locales.administration.common.messages.filterremoved
                    );
                }
            }
        );
    },

    privates: {
        /**
         *
         * @param {CMDBuildUI.model.base.Filter} filter The filter to edit.
         */
        getAttributesFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-attributes-panel',
                title: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.attributes.attributes'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getRelationsFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-relationfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations,
                localized: {
                    title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFunctionFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-functionfilters-panel',
                title: CMDBuildUI.locales.Locales.administration.common.labels.funktion,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.common.labels.funktion'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        },

        getFulltextFilterTab: function (viewmodel, record) {
            var filterPanel = {
                xtype: 'administration-components-filterpanels-fulltextfilter-panel',
                title: CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltext,
                localized: {
                    title: 'CMDBuildUI.locales.Locales.administration.searchfilters.texts.fulltext'
                },
                viewModel: viewmodel
            };
            return filterPanel;
        }
    }
});
