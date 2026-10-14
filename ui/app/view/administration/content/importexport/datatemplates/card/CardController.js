Ext.define('CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-importexport-datatemplates-card',

    mixins: ['CMDBuildUI.view.administration.content.importexport.datatemplates.card.CardMixin'],

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editExportFilterBtn': {
            click: 'onEditExportFilterBtn'
        },
        '#removeExportFilterBtn': {
            click: 'onRemoveExportFilterBtn'
        },
        '#editImportFilterBtn': {
            click: 'onEditImportFilterBtn'
        },
        '#removeImportFilterBtn': {
            click: 'onRemoveImportFilterBtn'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#targetName_input': {
            change: 'onTargetNameInputChange'
        },
        '#autogenarateBtn': {
            click: 'onAutogenerateBtnClick'
        },
        '#importExportAttributeGrid': {
            afterrender: 'onImportExportAttributeGridAfterRender',
            beforeedit: 'onImportExportAttributeGridBeforeEdit',
            edit: 'onImportExportAttributeGridEdit'
        }
    },

    onBeforeRender: function (view, eOpts) {
        this.initStores();
        this.setupViewModel();
        this.addToolbars(view);
    },

    onAfterRender: function (view) {
        this.bindViewModelData();
    },

    onImportExportAttributeGridAfterRender: function (view, eOpts) {
        this.getView()
            .lookupViewModel()
            .bind('{theGateTemplate.fileFormat}', function (fileFormat) {
                if (fileFormat === 'ifc') {
                    view.headerCt
                        .getHeaderAtIndex(1)
                        .setText(CMDBuildUI.locales.Locales.administration.gates.ifcproperty);
                }
            });
    },

    onImportExportAttributeGridBeforeEdit: function (editor, context, eOpts) {
        if (editor.view.lookupViewModel().get('actions.view')) {
            return false;
        }
        var comboStore = editor.view.lookupViewModel().get('attributeModesReferenceStore');
        comboStore.clearFilter();
        switch (context.record.get('attribute')) {
            case 'IdObj1':
            case 'IdObj2':
                comboStore.addFilter([
                    function (item) {
                        return item.get('value') !== 'default';
                    }
                ]);
                editor.editor.items.items[2].setHideTrigger(false);
                editor.editor.items.items[2].setDisabled(false);
                return true;

            default:
                var allAttributesStore = editor.view.lookupViewModel().get('allClassOrDomainsAtributes');
                allAttributesStore.rejectChanges();
                var attribute = allAttributesStore.findRecord(
                    'name',
                    context.record.get('attribute'),
                    false,
                    false,
                    true,
                    true
                );
                if (attribute) {
                    switch (attribute.get('type')) {
                        case 'lookup':
                        case 'lookupArray':
                        case 'reference':
                        case 'foreignKey':
                            editor.editor.items.items[2].setHideTrigger(false);
                            editor.editor.items.items[2].setEmptyText(
                                Ext.String.format(
                                    '{0} *',
                                    CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode
                                )
                            );
                            editor.editor.items.items[2].setDisabled(false);
                            break;
                        default:
                            editor.editor.items.items[2].setHideTrigger(true);
                            editor.editor.items.items[2].setEmptyText('');
                            editor.editor.items.items[2].setDisabled(true);
                            break;
                    }
                    if (attribute.get('_id') === 'IdTenant') {
                        editor.editor.items.items[2].setHideTrigger(false);
                        editor.editor.items.items[2].setEmptyText(
                            Ext.String.format(
                                '{0} *',
                                CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode
                            )
                        );
                        editor.editor.items.items[2].setDisabled(false);
                    }
                }
                return true;
        }
    },

    onImportExportAttributeGridEdit: function (editor, context, eOpts) {
        context.record.set('columnName', editor.editor.items.items[1].getValue());
        context.record.set('mode', editor.editor.items.items[2].getValue());
        if (editor.editor.items.items[3]) {
            context.record.set('default', editor.editor.items.items[3].getValue());
        }
    },

    onMergeSkipChange: function (checkbox, newValue) {
        var theGateTemplate = this.getViewModel().get('theGateTemplate');
        theGateTemplate.set('_importMergeSkip', newValue);
        if (newValue === true) {
            theGateTemplate.set('enableCreate', 'skip');
        } else {
            theGateTemplate.set('enableCreate', 'true');
        }
    },

    addToolbars: function (view) {
        var topbar = this.createTopToolbar();
        var bottombar = this.createBottomToolbar();
        view.addDocked([topbar, bottombar]);
    },

    createTopToolbar: function () {
        return {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            },
            items: this.getTopToolbarItems()
        };
    },

    createBottomToolbar: function () {
        return {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: this.getBottomToolbarItems()
        };
    },

    getTopToolbarItems: function () {
        return CMDBuildUI.util.administration.helper.FormHelper.getTools(
            {
                edit: true,
                view: false,
                clone: true,
                delete: true,
                activeToggle: true
            },
            'importexporttemplates',
            'theGateTemplate',
            [],
            [],
            []
        );
    },

    getBottomToolbarItems: function () {
        var view = this;
        return CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(
            true,
            {},
            {
                listeners: {
                    mouseover: function () {
                        view.checkFormValidity();
                    }
                }
            }
        );
    },

    checkFormValidity: function () {
        var me = this,
            form = me.getView().form;
        form.checkValidity();
        var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(form);
        Ext.Array.forEach(invalidFields, function (field) {
            if (!field.isVisible() || !field.up().isVisible() || !field.up().up().isVisible()) {
                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(field, true, form);
                CMDBuildUI.util.Logger.log(field.itemId + ' is invalid', CMDBuildUI.util.Logger.levels.warn);
                form.isValid();
            }
        });
    },

    bindViewModelData: function () {
        var me = this;
        var vm = this.getViewModel();
        vm.bind(
            {
                bindTo: {
                    type: '{theGateTemplate.type}',
                    fieldsadded: '{fieldsadded}'
                }
            },
            function (data) {
                me.updateFieldsetVisibility(data);
            }
        );
    },

    onSaveBtnClick: function (button) {
        const view = this,
            form = view.getView(),
            vm = this.getViewModel();
        let theGateTemplate = vm.get('theGateTemplate');
        CMDBuildUI.util.Utilities.showLoader(true, button.up('panel'));
        view.setColumnsData();

        // Setting enableCreate value based on _importMergeSkip checkbox value
        // backend handles enableCreate to skip new data creation on importmode merge
        if (theGateTemplate.get('_importMergeSkip') === true) {
            theGateTemplate.set('enableCreate', 'skip');
        } else {
            theGateTemplate.set('enableCreate', 'true');
        }

        if (!theGateTemplate.get('columns').length) {
            CMDBuildUI.util.Notifier.showWarningMessage(
                CMDBuildUI.locales.Locales.administration.importexport.texts.emptyattributegridmessage
            );
            CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
        } else if (theGateTemplate.isValid()) {
            theGateTemplate = view.prepareTemplateForSave(theGateTemplate, form);
            view.saveTemplate(theGateTemplate, button, vm);
        }
    },

    prepareTemplateForSave: function (theGateTemplate, form) {
        if (theGateTemplate.get('type') === CMDBuildUI.model.importexports.Template.types['export']) {
            Ext.Array.forEach(theGateTemplate.get('columns'), function (item) {
                item['default'] = '';
            });
        }

        var viewrgeValueInput = form.down('#viewrgeMode_when_missing_update_value_input');
        if (viewrgeValueInput && viewrgeValueInput.getValueAsString) {
            theGateTemplate.set('viewrgeMode_when_missing_update_value', viewrgeValueInput.getValueAsString());
        }
        return theGateTemplate;
    },

    saveTemplate: function (theGateTemplate, button, vm) {
        const me = this;
        theGateTemplate.save({
            success: function (record, operation) {
                const method = operation.getRequest().getMethod();
                me.saveAllTranslations().then(function () {
                    me.onSaveSuccess(record, method, vm, button);
                });
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false, button.up('panel'));
            }
        });
    },

    onSaveSuccess: function (record, methodRequest, vm, button) {
        if (!vm.get('showInMainPanel')) {
            this.handleSaveInPopup(record, methodRequest, vm);
        } else {
            this.handleSaveInMainPanel(record);
        }
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        if (button.el.dom) {
            button.setDisabled(false);
        }
        Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
    },

    handleSaveInPopup: function (record, methodRequest, vm) {
        var view = this;
        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
            var nextUrl = Ext.util.History.getToken();
            CMDBuildUI.util.administration.MenuStoreBuilder.selectNode('href', nextUrl, view);
            var eventToCall = vm.get('actions.edit') ? 'itemupdated' : 'itemcreated';
            var grid = vm.get('grid');
            grid.getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [grid, record, view]);
            view.getView().fireEventArgs('savesuccess', [record, methodRequest]);
            view.getView().up().fireEvent('closed');
        });
    },

    handleSaveInMainPanel: function (record) {
        var view = this;
        CMDBuildUI.util.administration.MenuStoreBuilder.initialize(function () {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(
                record.get('_id')
            );
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, view);
        });
    },

    onCancelBtnClick: function (button) {
        var view = this;
        var vm = this.getViewModel();
        vm.get('theGateTemplate').reject();
        if (vm.get('showInMainPanel')) {
            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl(
                vm.get('theGateTemplate._id')
            );
            if (vm.get('actions.add')) {
                nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getImportExportDataTemplatesUrl();
            }
            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, view);
        }
        if (this.getView() && !this.getView().destroyed) {
            this.getView().up().fireEvent('closed');
        }
    },

    onAutogenerateBtnClick: function () {
        var view = this.getView();
        var cb = function () {
            Ext.suspendLayouts();
            var vm = view.lookupViewModel();
            var grid = view.down('#importExportAttributeGrid').getView();
            var attributes = vm
                .get('allClassOrDomainsAtributesFiltered')
                .getRange()
                .sort(function (a, b) {
                    if (a.get('index') < b.get('index')) {
                        return -1;
                    }
                    if (a.get('index') > b.get('index')) {
                        return 1;
                    }
                    return 0;
                });
            var gridStore = vm.get('allSelectedAttributesStore');
            var freeAttributesCombo = view.down('#selectAttributeForGrid');
            attributes.forEach(function (attr) {
                var newAttr = {
                    attribute: attr.get('name'),
                    name: attr.get('name'),
                    _attribute_description: attr.get('description'),
                    columnName: attr.get('description'),
                    mode: null,
                    default: null,
                    index: gridStore.getRange().length
                };

                switch (attr.get('type')) {
                    case 'reference':
                    case 'foreignKey':
                        // mode should be 'description'
                        newAttr.mode = 'description';
                        break;
                    case 'lookup':
                    case 'lookupArray':
                        // mode should be 'code'
                        newAttr.mode = 'code';
                        break;
                    default:
                        if (attr.get('name') === 'IdTenant') {
                            // mode should be 'description'
                            newAttr.mode = 'description';
                        }
                        break;
                }
                gridStore.add(newAttr);
            });
            Ext.resumeLayouts();
            grid.refresh();
            vm.filterFreeAttributes();
            freeAttributesCombo.setStoreFilter();
        };

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.importexport.texts.confirmaddallattributes,
            function (btnText) {
                if (btnText === 'yes') {
                    cb();
                }
            },
            this
        );
    },

    onEditAttributeClick: function (grid, rowIndex, colIndex, item, e, record) {
        grid.editingPlugin.startEdit(record, 1);
    },

    onMoveAttributeUp: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        var previousRecord = store.getAt(rowIndex - 1);

        rowIndex--;
        if (!record || rowIndex < 0) {
            return;
        }
        previousRecord.set('index', rowIndex + 1);
        record.set('index', rowIndex);

        try {
            store.sort('index', 'ASC');
            grid.lookupViewModel().getParent().filterFreeAttributes();
            grid.refresh();
            Ext.resumeLayouts();
        } catch (e) {
            console.error('Error moving attribute up:', e);
        }
    },

    isMoveUpDisabled: function (view, rowIndex, colIndex, item, record) {
        if (!record.get('editing')) {
            rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
            return rowIndex == 0;
        } else {
            return true;
        }
    },

    onMoveAttributeDown: function (grid, rowIndex, colIndex) {
        Ext.suspendLayouts();
        var store = grid.getStore();
        var record = store.getAt(rowIndex);
        var nextRecord = store.getAt(rowIndex + 1);
        rowIndex++;
        if (!record || rowIndex >= store.getCount()) {
            return;
        }

        nextRecord.set('index', rowIndex - 1);
        record.set('index', rowIndex);
        try {
            store.sort('index', 'ASC');
            grid.lookupViewModel().getParent().filterFreeAttributes();
            grid.refresh();
            Ext.resumeLayouts();
        } catch (e) {
            console.error('Error moving attribute down:', e);
        }
    },

    isMoveDownDisabled: function (view, rowIndex, colIndex, item, record) {
        if (!record.get('editing')) {
            rowIndex = rowIndex == null ? view.getStore().findExact('id', record.get('id')) : rowIndex;
            return rowIndex >= view.store.getCount() - 1;
        } else {
            return true;
        }
    },

    onRemoveAttribute: function (grid, rowIndex, colIndex) {
        var vm = grid.up('form').getViewModel();
        var store = vm.get('allSelectedAttributesStore');
        var record = store.getAt(rowIndex);
        store.remove(record);
        vm.filterFreeAttributes();
        var importKeyAttributes = vm.get('theGateTemplate._importKeyAttribute');
        if (importKeyAttributes.length) {
            vm.set(
                'theGateTemplate._importKeyAttribute',
                Ext.Array.remove(importKeyAttributes, record.get('attribute'))
            );
        }
        grid.up('fieldset').down('#selectAttributeForGrid').setStoreFilter();
        grid.refresh();
    },

    isRemoveAttributeDisabled: function (view, rowIndex, colIndex, item, record) {
        return ['IdObj1', 'IdObj2'].indexOf(record.get('attribute')) > -1 || record.get('editing');
    },

    getRemoveAttributeClass: function (value, metadata, record, rowIndex, colIndex, store) {
        metadata.value = Ext.String.insert(
            metadata.value,
            Ext.String.format(' data-testid="administration-importexport-attribute-removeBtn-{0}"', rowIndex),
            -7
        );
        return record.get('editing')
            ? CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-h', 'solid')
            : CMDBuildUI.util.helper.IconHelper.getIconId('times', 'solid');
    },

    onSelectAttributeChange: function (combo, newValue, oldValue) {
        var grid = combo.up('grid');
        var vm = combo.lookupViewModel();
        var modeInput = combo.up('grid').down('#newComboMode');
        var columnName = combo.up('grid').down('#newAttributeColumnName');
        var modeStore = vm.get('attributeModesReferenceStore');
        modeStore.clearFilter();

        modeInput.reset();
        var allAttributes = grid.up('form').getViewModel().get('allClassOrDomainsAtributes');
        if (allAttributes && allAttributes.getData().length) {
            var attribute = allAttributes.findRecord('_id', newValue, false, false, true, true);
            if (attribute) {
                columnName.setValue(attribute.get('description'));
                if (
                    ['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 ||
                    attribute.get('_id') === 'IdTenant'
                ) {
                    modeInput.style = 'color:#57585C!important; border: 1px solid #cf4c35';
                    modeInput.allowBlank = false;
                    modeInput.reset();
                    modeInput.markInvalid('Required');
                    modeInput.setHideTrigger(false);
                    modeInput.setEmptyText(
                        Ext.String.format(
                            '{0} *',
                            CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode
                        )
                    );
                    modeInput.enable();
                    modeStore.setData(
                        attribute.get('type').startsWith('lookup')
                            ? vm.get('attributeModesLookup')
                            : vm.get('attributeModes')
                    );
                } else {
                    modeInput.allowBlank = true;
                    modeInput.style = 'border: 0px solid!important';
                    modeInput.setHideTrigger(true);
                    modeInput.setEmptyText('');
                    modeInput.disable();
                }
            }
        } else if (['IdObj1', 'IdObj2'].indexOf(newValue) > -1) {
            modeInput.style = 'color:#57585C!important; border: 1px solid #cf4c35';
            modeInput.allowBlank = false;
            modeInput.markInvalid('Required');
            modeInput.setEmptyText(
                Ext.String.format('{0} *', CMDBuildUI.locales.Locales.administration.importexport.texts.selectmode)
            );
            modeInput.setHideTrigger(false);
            modeInput.enable();
        } else {
            modeInput.allowBlank = true;
            modeInput.setEmptyText('');
            modeInput.style = 'border: 0px solid!important';
            modeInput.setHideTrigger(true);
            modeInput.disable();
        }
    },

    onNewComboModeExpand: function (combo) {
        var grid = combo.up('grid');
        var vm = grid.lookupViewModel();
        var attributeName = grid.down('#selectAttributeForGrid');
        if (Ext.isEmpty(attributeName.getValue())) {
            return;
        }
        var attribute;
        var allAttributes = vm.get('allClassOrDomainsAtributes');
        var store = vm.get('attributeModesReferenceStore');

        if (allAttributes && allAttributes.getData().length) {
            attribute = allAttributes.findRecord('_id', attributeName.getValue(), false, false, true, true);
        }
        if (
            attribute &&
            (['lookup', 'lookupArray', 'reference', 'foreignKey'].indexOf(attribute.get('type')) > -1 ||
                attribute.get('_id') === 'IdTenant')
        ) {
            store.setData(
                attribute.get('type').startsWith('lookup') ? vm.get('attributeModesLookup') : vm.get('attributeModes')
            );
        } else if (['IdObj1', 'IdObj2'].indexOf(attributeName.getValue()) > -1) {
            store.setData(vm.get('attributeModes'));
        }
    },

    onAddNewAttribute: function (button, rowIndex, colIndex) {
        var grid = button.up('grid');
        var attributeName = grid.down('#selectAttributeForGrid');
        var columnName = grid.down('#newAttributeColumnName');
        var attributeMode = grid.down('#newComboMode');
        var defaultValue = grid.down('#newAttributeDefaultValue');
        if (Ext.isEmpty(attributeName.getValue())) {
            attributeName.focus();
            attributeName.expand();
            return false;
        }
        if (!attributeMode.isValid()) {
            attributeMode.focus();
            attributeMode.expand();
            return false;
        }
        Ext.suspendLayouts();
        var mainGrid = button.up('form').down('#importExportAttributeGrid');
        var vm = button.up('form').getViewModel();
        var attributeStore = vm.getStore('allSelectedAttributesStore');

        var newAttribute = CMDBuildUI.model.importexports.Attribute.create({
            attribute: attributeName.getValue(),
            columnName: columnName.getValue(),
            mode: attributeMode.getValue(),
            default: defaultValue.getValue(),
            index: attributeStore.getRange().length
        });

        attributeStore.add(newAttribute);
        attributeName.reset();
        columnName.reset();
        attributeMode.reset();
        attributeMode.allowBlank = true;
        grid.down('#selectAttributeForGrid').setStoreFilter();
        defaultValue.reset();
        Ext.resumeLayouts();
        mainGrid.getView().refresh();
        vm.filterFreeAttributes();
    },

    onSelectKeyAttributeAfterRender: function (combo) {
        var vm = combo.lookupViewModel();
        var form = combo.up('form');
        vm.bind(
            {
                bindTo: {
                    keyAttributes: '{theGateTemplate._importKeyAttribute}',
                    importMode: '{theGateTemplate._importMode}'
                }
            },
            function (data) {
                if (
                    data.importMode === CMDBuildUI.model.importexports.Template.importModes.merge &&
                    (!data.keyAttributes || !data.keyAttributes.length)
                ) {
                    combo.allowBlank = false;
                    combo.markInvalid(CMDBuildUI.locales.Locales.administration.common.messages.thisfieldisrequired);
                } else {
                    combo.allowBlank = true;
                    combo.clearInvalid();
                }
                form.form.checkValidity();
                CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(form.form);
            }
        );
    },

    onSelectKeyAttribute: function (combo, record) {
        var vm = combo.lookupViewModel();
        var _keyAttributes = vm.get('theGateTemplate._importKeyAttribute');
        var keyAttributes = Ext.Array.from(_keyAttributes);
        keyAttributes.push(combo.getValue());
        vm.set('theGateTemplate._importKeyAttribute', keyAttributes.join(','));
        combo.clearValue();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateDescriptionClick: function (button, event, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfTemplateDescription(
                vm.get('theGateTemplate.code')
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm,
            true
        );
    },

    /**
     * exeute all promises for translation save
     */
    saveAllTranslations: function () {
        const deferred = new Ext.Deferred();
        const me = this;
        me.saveDescriptionTranslation().then(function () {
            deferred.resolve();
        });
        return deferred.promise;
    },

    /**
     * exeute promise for description translation
     */
    saveDescriptionTranslation: function () {
        const deferred = new Ext.Deferred();
        const vm = this.getViewModel();
        const key = CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfTemplateDescription(
            vm.get('theGateTemplate.code')
        );
        // save the translation
        if (vm.get('theDescriptionTranslation')) {
            vm.get('theDescriptionTranslation').crudState = 'U';
            vm.get('theDescriptionTranslation').crudStateWas = 'U';
            vm.get('theDescriptionTranslation').phantom = false;
            vm.get('theDescriptionTranslation').set('_id', key);
            vm.get('theDescriptionTranslation').save({
                success: function (translations, operation) {
                    deferred.resolve();
                }
            });
        } else {
            deferred.resolve();
        }
        return deferred.promise;
    }
});
