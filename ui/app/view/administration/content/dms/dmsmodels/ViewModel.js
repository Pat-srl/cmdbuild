Ext.define('CMDBuildUI.view.administration.content.dms.models.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-dms-models-view',

    data: {
        pluginMode: false,
        isClass: true,
        activeTab: 0,
        objectTypeName: null,
        theModel: null,
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false,
            empty: false
        },
        disabledTabs: {
            properties: false,
            attributes: false,
            domains: false,
            layers: false,
            geoattributes: false,
            import_export: true,
            fieldsmanagement: false
        },
        formTriggerCount: 0,
        formWidgetCount: 0,
        contextMenuCount: 0,
        attributeGroupingCount: 0,
        attributeGroups: [],
        toolbarHiddenButtons: {
            edit: true, // action !== view
            delete: true, // action !== view
            enable: true, //action !== view && theModel.active
            disable: true, // action !== view && !theModel.active
            print: true // action !== view
        },
        checkboxAttachmentsInlineClosed: {
            disabled: true
        },
        isMultitenant: false,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false,
            _canClone: false
        }
    },

    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theModel._can_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        theModelManager: {
            bind: '{theModel}',
            get: function (theModel) {
                this.set('activeTab', this.get('activeTabs.dmsmodels'));
                this.set('storesAutoload', theModel ? true : false);
                this.set(
                    'isMultitenant',
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)
                );
                this.set(
                    'formTriggersData',
                    theModel && theModel.getAssociatedData().formTriggers
                        ? theModel.getAssociatedData().formTriggers
                        : []
                );
                this.set(
                    'contextMenuItemData',
                    theModel && theModel.getAssociatedData().contextMenuItems
                        ? theModel.getAssociatedData().contextMenuItems
                        : []
                );
                this.set(
                    'formWidgetData',
                    theModel && theModel.getAssociatedData().widgets ? theModel.getAssociatedData().widgets : []
                );
                this.set(
                    'defaultOrderData',
                    theModel && theModel.getAssociatedData().defaultOrder
                        ? theModel.getAssociatedData().defaultOrder
                        : []
                );
                var dmsModelsData = Ext.getStore('dms.DMSModels').getRange();
                this.set(
                    'allClassesWithClass',
                    Ext.Array.insert(dmsModelsData, 0, [
                        {
                            name: CMDBuildUI.model.dms.DMSModel.masterParentClass,
                            description: CMDBuildUI.locales.Locales.administration.common.labels['default'],
                            prototype: true
                        }
                    ])
                );
            }
        },

        dmsmodelLabel: {
            bind: '{theModel.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.dmsmodels.modelclass;
            }
        },

        localizedComboOptionsManager: {
            get: function () {
                this.set('tenantModes', CMDBuildUI.model.users.Tenant.getTenantModes());
                this.set('contexMenuTypes', CMDBuildUI.model.ContextMenuItem.getTypes());
                this.set('contextMenuApplicabilities', CMDBuildUI.model.ContextMenuItem.getVisibilities());

                this.set('defaultOrders', CMDBuildUI.model.AttributeOrder.getDefaultOrders());
                this.set('formWidgetTypes', CMDBuildUI.model.WidgetDefinition.getTypes());
                this.set(
                    'attriubteGroupingDisplayModes',
                    CMDBuildUI.util.administration.helper.ModelHelper.getAttriubteGroupingDisplayModes()
                );
            }
        },

        updateAttachmnentsInlineClosedCheckboxState: {
            bind: '{theModel.attachmentsInline}',
            get: function (attachmentsInline) {
                if (attachmentsInline) {
                    this.set('checkboxAttachmentsInlineClosed.disabled', false);
                } else {
                    this.set('checkboxAttachmentsInlineClosed.disabled', true);
                    this.set('theModel.attachmentsInlineClosed', false);
                }
            }
        },

        isMultitenatModeHiddenCombo: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isPrototype: '{theModel.prototype}'
            },
            get: function (data) {
                const showMultiTenant = data.isMultitenant && (data.isEdit || data.isAdd) && !data.isPrototype;
                return !showMultiTenant;
            }
        },

        isMultitenatModeHiddenDisplay: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isView: '{actions.view}',
                isPrototype: '{theModel.prototype}'
            },
            get: function (data) {
                return !(data.isMultitenant && data.isView && !data.isPrototype);
            }
        },

        multitenantModeManager: {
            bind: {
                mode: '{theModel.multitenantMode}',
                multitenantModeStore: '{multitenantModeStore}'
            },
            get: function (data) {
                if (data.mode && data.multitenantModeStore) {
                    this.set(
                        'theModel._multitenantMode_description',
                        data.multitenantModeStore.findRecord('value', data.mode).get('label')
                    );
                }
            }
        },

        action: {
            bind: {
                theModel: '{theModel}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}',
                idEmpty: '{actions.empty}'
            },
            get: function (data) {
                const me = this;
                if (data.theModel) {
                    me.configToolbarButtons();
                    me.configDisabledTabs(data.theModel);
                }
                if (data.isEdit) {
                    data.theModel.getAttributes().then(function (attributesStore) {
                        if (!me.destroyed) {
                            me.set('attributesStore', attributesStore);
                        }
                    });
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    data.theModel.getAttributes().then(function (attributesStore) {
                        if (me && !me.destroyed) {
                            me.set('attributesStore', attributesStore);
                        }
                    });
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);
                this.set('actions.empty', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.empty);
                const form = this.getView()
                    .down('administration-content-dms-models-tabitems-properties-fieldsets-generaldatafieldset')
                    .up('form')
                    .getForm();
                const nameField = form.findField('classnamefieldadd');
                nameField.maxLength =
                    value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? Infinity : 20;
            }
        },

        getToolbarButtons: {
            bind: {
                actions: '{actions}',
                active: '{theModel.active}'
            },
            get: function (get) {
                this.configToolbarButtons();
            }
        },

        isSuperClassManager: {
            bind: {
                prototype: '{theModel.prototype}',
                isMultitenant: '{isMultitenant}',
                multitenantMode: '{theModel.multitenantMode}'
            },
            get: function (data) {
                if (data.prototype) {
                    this.set('theModel.multitenantMode', '');
                }
                return data.prototype;
            }
        },

        countersManager: {
            bind: {
                formTriggerCount: '{formTriggerCount}',
                formWidgetCount: '{formWidgetCount}',
                contextMenuCount: '{contextMenuCount}',
                attributeGroupingCount: '{attributeGroupingCount}',
                formTriggers: '{formTriggersStore.data.length}',
                widgets: '{formWidgetsStore.data.length}',
                contextMenuItems: '{contextMenuItemsStore.data.length}',
                attributeGrouping: '{attributeGroupsStore.data.length}'
            },
            get: function (data) {
                this.set('formTriggerCount', this.getStore('formTriggersStore').getCount());
                this.set('formWidgetCount', this.getStore('formWidgetsStore').getCount());
                this.set('contextMenuCount', this.getStore('contextMenuItemsStore').getCount());
                this.set('attributeGroupingCount', this.getStore('attributeGroupsStore').getCount());
                this.set(
                    'groupingFieldsetTitle',
                    Ext.String.format(
                        '{0} ({1})',
                        CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.attributegroups,
                        this.getStore('attributeGroupsStore').getCount()
                    )
                );
            }
        },

        isSimpleClass: {
            bind: {
                type: '{theModel.type}',
                prototype: '{theModel.prototype}'
            },
            get: function (data) {
                if (data.type) {
                    return data.type === 'simple';
                }
            }
        },

        isStandardClassAndIsViewAction: {
            bind: '{theModel.type}',
            get: function (type) {
                if (type) {
                    return type === 'standard' && this.get('actions.view');
                }
            }
        },

        hideParentCombobox: {
            bind: '{theModel.type}',
            get: function (type) {
                if (type) {
                    return type === 'simple' || this.get('actions.view') === true;
                }
            }
        },
        hideParentDisplayfield: {
            bind: {
                type: '{theModel.type}',
                view: '{actions.view}'
            },
            get: function (data) {
                if (data.type) {
                    return data.type === 'simple' || (data.view === false && data.type === 'standard');
                }
            }
        },
        hideParentDisabledfield: {
            bind: {
                type: '{theModel.type}',
                edit: '{actions.edit}'
            },
            get: function (data) {
                if (data.type) {
                    return data.type === 'simple' || (data.edit === false && data.type === 'standard');
                }
            }
        },

        defaultFilterData: {
            bind: '{theModel}',
            get: function (theModel) {
                if (theModel && !theModel.phantom) {
                    this.set('defaultFilterData._id', theModel.get('_id'));
                    this.set('defaultFilterData.name', theModel.get('name'));
                    this.set('defaultFilterProxy', {
                        url: Ext.String.format('/dms/models/{0}/filters', theModel.get('name')),
                        type: 'baseproxy'
                    });
                }
            }
        },

        defaultFilterFilter: {
            bind: '{theModel}',
            get: function (theModel) {
                return [
                    function (item) {
                        return item.get('target') === theModel.get('name');
                    }
                ];
            }
        },

        unorderedAttributes: {
            bind: {
                theModel: '{theModel}',
                attributeStoreLoaded: '{attributesStore.complete}'
            },
            get: function (data) {
                if (data.theModel && data.attributeStoreLoaded) {
                    const defaultOrder = data.theModel.getAssociatedData().defaultOrder;
                    return [
                        function (item) {
                            let found = false;
                            for (const field in defaultOrder) {
                                if (
                                    item.get('_can_modify') !== true ||
                                    defaultOrder[field].attribute === item.get('name')
                                ) {
                                    found = true;
                                }
                            }
                            return !found;
                        }
                    ];
                }
            }
        },

        getParentDescription: {
            bind: {
                theModel: '{theModel}',
                superclassesStore: '{superclassesStore}'
            },
            get: function (data) {
                const me = this;
                const theModel = data.theModel;
                const superclassesStore = data.superclassesStore;
                if (theModel && superclassesStore) {
                    const parent = superclassesStore.findRecord('name', me.get('theModel.parent'));
                    if (parent) {
                        me.set('parentDescription', parent.get('description'));
                        return parent.get('description');
                    }
                }
            }
        },

        formWidgetStoreNewData: {
            bind: {
                theModel: '{theModel}'
            },
            get: function (data) {
                if (data.theModel) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.WidgetDefinition');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
            }
        },

        contextMenuItemsStoreNewData: {
            bind: {
                theModel: '{theModel}'
            },
            get: function (data) {
                if (data.theModel) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.ContextMenuItem');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
            }
        },

        checkCountStoreData: function () {
            return CMDBuildUI.util.administration.helper.ModelHelper.getDMSCountCheckModes('dmsModel');
        },

        checkCountNumberHidden: {
            bind: '{theModel.checkCount}',
            get: function (checkCount) {
                if (Ext.isEmpty(checkCount) || checkCount === CMDBuildUI.model.dms.DMSModel.checkCount.no_check) {
                    return true;
                }
                return false;
            }
        },

        attributeGroupEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.AttributeGrouping');
        },

        defaultOrderEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.AttributeOrder', { direction: 'ascending' });
        },

        formTriggersEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.FormTrigger');
        },

        parentGroupingManager: {
            bind: {
                parent: '{theModel.parent}'
            },
            get: function (data) {
                const me = this;
                const theModel = me.get('theModel');
                if (theModel.phantom) {
                    this.get('attributeGroupsStore').removeAll();
                    theModel.set('attributeGroups', null);
                    if (!Ext.isEmpty(data.parent)) {
                        const parent = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.parent);
                        if (parent) {
                            parent.attributeGroups().each(function (group) {
                                me.get('attributeGroupsStore').add(group.getData());
                            });
                            this.set(
                                'groupingFieldsetTitle',
                                Ext.String.format(
                                    '{0} ({1})',
                                    CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.attributegroups,
                                    this.getStore('attributeGroupsStore').getCount()
                                )
                            );
                        }
                    }
                }
            }
        }
    },

    stores: {
        defaultOrderStore: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order',
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrderData}',
            autoDestroy: true
        },
        defaultOrderStoreNew: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order-new',
            autoLoad: '{storesAutoload}',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrderEmptyRecord}'
        },
        attributeGroupsStoreNew: {
            model: 'CMDBuildUI.model.AttributeGrouping',
            alias: 'store.attribute-groupings-new',
            autoLoad: '{storesAutoload}',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{attributeGroupEmptyRecord}'
        },
        defaultOrderDirectionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrders}'
        },
        superclassesStore: {
            data: '{allClassesWithClass}',
            proxy: {
                type: 'memory'
            },
            filters: [
                function (item) {
                    return item.get('prototype');
                }
            ]
        },

        formTriggersStore: {
            storeId: 'formTriggersStore',
            model: 'CMDBuildUI.model.FormTrigger',
            proxy: {
                type: 'memory'
            },
            autoLoad: '{storesAutoload}',
            autoDestroy: true,
            data: '{formTriggersData}'
        },
        formTriggersStoreNew: {
            model: 'CMDBuildUI.model.FormTrigger',
            proxy: {
                type: 'memory'
            },
            autoLoad: '{storesAutoload}',
            autoDestroy: true,
            data: '{formTriggersEmptyRecord}'
        },
        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus',
            pageSize: 0
        },
        contextMenuItemsStore: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemData}',
            autoDestroy: true
        },
        contextMenuItemTypeStore: {
            autoLoad: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            autoDestroy: true,
            data: '{contexMenuTypes}'
        },

        contextMenuApplicabilityStore: {
            type: 'common-applicability',
            data: '{contextMenuApplicabilities}'
        },
        formWidgetsStore: {
            model: 'CMDBuildUI.model.WidgetDefinition',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{formWidgetData}'
        },
        formWidgetsStoreNew: {
            model: 'CMDBuildUI.model.WidgetDefinition',
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{formWidgetStoreNewData}'
        },
        contextMenuItemsStoreNew: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemsStoreNewData}',
            autoDestroy: true
        },
        defaultFilterStore: {
            source: 'searchfilters.Searchfilters',
            filters: '{defaultFilterFilter}'
        },

        multitenantModeStore: {
            type: 'multitenant-multitenantmode',
            data: '{tenantModes}'
        },

        attributeGroupsStore: {
            source: '{theModel.attributeGroups}'
        },
        widgetTypesStore: {
            type: 'common-widgettypes',
            data: '{formWidgetTypes}'
        },
        attriubteGroupingDisplayModeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attriubteGroupingDisplayModes}',
            autoDestroy: true
        },
        checkCountStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{checkCountStoreData}',
            autoDestroy: true
        }
    },

    configToolbarButtons: function () {
        this.set('disabledTabs.properties', false);
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.delete', !this.get('actions.view'));
        this.set(
            'toolbarHiddenButtons.enable',
            !this.get('actions.view') ||
                (this.get('actions.view') && this.data.theModel.data.active) /*this.get('theModel.active')*/
        );
        this.set(
            'toolbarHiddenButtons.disable',
            !this.get('actions.view') ||
                (this.get('actions.view') && !this.data.theModel.data.active) /*!this.get('theModel.active')*/
        );
        this.set('toolbarHiddenButtons.print', this.get('actions.add'));

        return true;
    },

    configDisabledTabs: function () {
        const me = this;
        const theModel = this.get('theModel') || this.getData().theModel;
        const gisEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_gis_enabled');
        const importExportDisabled =
            me.get('isSimpleClass') || (theModel && theModel.get('prototype')) || !me.get('actions.view');
        me.set('disabledTabs.properties', false);
        me.set('disabledTabs.attributes', !me.get('actions.view'));
        me.set('disabledTabs.fieldsmanagement', importExportDisabled);
        me.set('disabledTabs.domains', !me.get('actions.view'));
        me.set('disabledTabs.layers', !gisEnabled || !me.get('actions.view'));
        me.set('disabledTabs.geoattributes', !gisEnabled || !me.get('actions.view'));
        me.set('disabledTabs.import_export', importExportDisabled);
    },

    toggleEnableTabs: function (currrentTabIndex) {
        const me = this;
        const view = me.getView().down('administration-content-dms-dmsmodel-tabpanel');
        const tabs = view.items.items;

        tabs.forEach(function (tab) {
            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                me.set('disabledTabs.' + tab.reference, true);
            }
        });
    }
});
