Ext.define('CMDBuildUI.view.administration.content.processes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    requires: ['CMDBuildUI.model.processes.Process'],
    alias: 'viewmodel.administration-content-processes-view',

    data: {
        pluginMode: false,
        isSimpleClass: false,
        activeTab: 0,
        objectTypeName: null,
        theProcess: null,
        attributeGroups: [],
        action: null,
        actions: {
            view: true,
            edit: false,
            add: false
        },
        disabledTabs: {
            properties: false,
            attributes: false,
            domains: false,
            tasks: false,
            helps: false
        },
        toolbarHiddenButtons: {
            edit: true, // action !== view
            delete: true, // action !== view
            enable: true, //action !== view && theProcess.active
            disable: true, // action !== view && !theProcess.active
            version: true // action !== view
        },
        checkboxNoteInlineClosed: {
            disabled: true
        },
        formTriggerCount: 0,
        contextMenuCount: 0,
        attributeGroupingCount: 0,
        formWidgetCount: 0,
        isMultitenant: false,
        toolAction: {
            _canAdd: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        }
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theProcess._can_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        theProcessManager: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    const rawIconFont = theProcess.get('icon_font');
                    if (rawIconFont) {
                        theProcess.set(
                            'icon_font',
                            CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont)
                        );

                        theProcess.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }
                }
                this.set(
                    'activeTab',
                    this.getView().up('administration-content').getViewModel().get('activeTabs.processes') || 0
                );
                this.set('storesAutoload', theProcess ? true : false);
                this.set(
                    'isMultitenant',
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled) &&
                        !theProcess.get('prototype')
                );
                this.set(
                    'formTriggersData',
                    theProcess && theProcess.getAssociatedData().formTriggers
                        ? theProcess.getAssociatedData().formTriggers
                        : []
                );
                this.set(
                    'contextMenuItemData',
                    theProcess && theProcess.getAssociatedData().contextMenuItems
                        ? theProcess.getAssociatedData().contextMenuItems
                        : []
                );
                this.set(
                    'formWidgetData',
                    theProcess && theProcess.getAssociatedData().widgets ? theProcess.getAssociatedData().widgets : []
                );
                this.set(
                    'defaultOrderData',
                    theProcess && theProcess.getAssociatedData().defaultOrder
                        ? theProcess.getAssociatedData().defaultOrder
                        : []
                );
                const processesData = Ext.getStore('processes.Processes').getRange();
                this.set(
                    'allPrototypeProcesses',
                    Ext.Array.insert(processesData, 0, [
                        {
                            name: CMDBuildUI.model.processes.Process.masterParentClass,
                            description: CMDBuildUI.locales.Locales.administration.common.labels['default'],
                            prototype: true
                        }
                    ])
                );
            }
        },

        precessLabel: {
            bind: '{theProcess.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.processes.toolbar.processLabel;
            }
        },

        localizedComboOptionsManager: {
            get: function () {
                this.set('attachmentsDescriptionModes', CMDBuildUI.model.attachments.Attachment.getDescriptionModes());
                this.set('contexMenuTypes', CMDBuildUI.model.ContextMenuItem.getTypes());
                this.set('contextMenuApplicabilities', CMDBuildUI.model.ContextMenuItem.getVisibilities());
                this.set('formWidgetTypes', CMDBuildUI.model.WidgetDefinition.getTypes());
                this.set('tenantModes', CMDBuildUI.model.users.Tenant.getTenantModes());
                this.set('defaultOrders', CMDBuildUI.model.AttributeOrder.getDefaultOrders());
                this.set(
                    'attriubteGroupingDisplayModes',
                    CMDBuildUI.util.administration.helper.ModelHelper.getAttriubteGroupingDisplayModes()
                );
            }
        },

        updateNoteInlineClosedCheckboxState: {
            bind: '{theProcess.noteInline}',
            get: function (data) {
                if (data) {
                    this.set('checkboxNoteInlineClosed.disabled', false);
                } else {
                    this.set('checkboxNoteInlineClosed.disabled', true);
                    this.set('theProcess.noteInlineClosed', false);
                }
            }
        },

        multitenantModeManager: {
            bind: {
                mode: '{theProcess.multitenantMode}',
                multitenantModeStore: '{multitenantModeStore}'
            },
            get: function (data) {
                if (data.mode && data.multitenantModeStore) {
                    this.set(
                        'theProcess._multitenantMode_description',
                        data.multitenantModeStore.findRecord('value', data.mode).get('label')
                    );
                }
            }
        },

        action: {
            bind: {
                theProcess: '{theProcess}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },

            get: function (data) {
                this.configToolbarButtons();
                this.configDisabledTabs();
                const me = this;

                if (data.isEdit) {
                    data.theProcess.getAttributes().then(function (attributesStore) {
                        if (!me.destroyed) {
                            me.set('attributesStore', attributesStore);
                            me.set('unorderedAttributesData', attributesStore.getRange());
                        }
                    });
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    data.theProcess.getAttributes().then(function (attributesStore) {
                        if (!me.destroyed) {
                            me.set('attributesStore', attributesStore);
                            me.set('unorderedAttributesData', attributesStore.getRange());
                        }
                    });
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.view;
                }
            },
            set: function (value) {
                this.set('actions.view', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
                this.set('actions.edit', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
                this.set('actions.add', value === CMDBuildUI.util.administration.helper.FormHelper.formActions.add);

                const form = this.getView()
                    .down('administration-content-processes-tabitems-properties-fieldsets-generaldatafieldset')
                    .up('form')
                    .getForm();
                const nameField = form.findField('processnamefieldadd');
                nameField.maxLength =
                    value === CMDBuildUI.util.administration.helper.FormHelper.formActions.edit ? Infinity : 20;
            }
        },

        isSuperProcess: {
            bind: {
                prototype: '{theProcess.prototype}'
            },
            get: function (data) {
                const isMultitenant =
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled) &&
                    !data.prototype;
                if (data.prototype) {
                    this.set('theProcess.multitenantMode', '');
                } else {
                    this.set(
                        'theProcess.multitenantMode',
                        isMultitenant ? this.get('theProcess.multitenantMode') || 'never' : ''
                    );
                }
                return data.prototype;
            }
        },

        flowStatusManager: {
            bind: {
                storeComplete: '{attributesStore.complete}',
                flowStatusAttr: '{theProcess.flowStatusAttr}'
            },
            get: function (data) {
                if (data.storeComplete && data.flowStatusAttr) {
                    let description = data.flowStatusAttr;
                    const store = this.get('attributesStore');
                    const record = store.findRecord('name', data.flowStatusAttr);
                    if (record) {
                        description = record.get('description');
                    }
                    this.set('theProcess._flowStatusAttr_description', description);
                }
            }
        },

        messageAttrMenager: {
            bind: {
                storeComplete: '{attributesStore.complete}',
                messageAttr: '{theProcess.messageAttr}'
            },
            get: function (data) {
                if (data.storeComplete && data.messageAttr) {
                    let description = data.messageAttr;
                    const store = this.get('attributesStore');
                    const record = store.findRecord('name', data.messageAttr);
                    if (record) {
                        description = record.get('description');
                    }
                    this.set('theProcess._messageAttr_description', description);
                }
            }
        },

        hideParentCombobox: {
            bind: {
                type: '{theProcess}',
                view: '{actions.view}'
            },
            get: function (data) {
                if (data.type) {
                    return data.view;
                }
            }
        },

        hideParentDisplayfield: {
            bind: {
                type: '{theProcess.type}',
                view: '{actions.view}'
            },
            get: function (data) {
                if (data.type) {
                    return data.type === 'simple' || (data.view === false && data.type === 'standard') ? true : false;
                }
            }
        },

        getParentDescription: {
            bind: {
                theProcess: '{theProcess}',
                superprocessesStore: '{superprocessesStore}'
            },
            get: function (data) {
                const me = this;
                const theProcess = data.theProcess;
                const superprocessesStore = data.superprocessesStore; // data.superclassesStore;
                if (theProcess && superprocessesStore) {
                    const parent = superprocessesStore.findRecord('name', me.get('theProcess.parent'));
                    if (parent) {
                        me.set('parentDescription', parent.get('description'));
                        return parent.get('description');
                    }
                }
            }
        },

        attributeProxy: {
            bind: '{theProcess.name}',
            get: function (objectTypeName) {
                if (objectTypeName && !this.get('theProcess').phantom) {
                    return {
                        url: Ext.String.format('/processes/{0}/attributes', objectTypeName),
                        type: 'baseproxy'
                    };
                }
            }
        },

        unorderedAttributes: {
            bind: '{theProcess}',
            get: function (theProcess) {
                if (theProcess) {
                    return [
                        function (item) {
                            if (item.get('_can_modify') !== true) {
                                return false;
                            } else {
                                const defaultOrder = theProcess.getAssociatedData().defaultOrder;
                                for (const field in defaultOrder) {
                                    if (defaultOrder[field].attribute === item.get('name')) {
                                        return false;
                                    }
                                }
                                return true && item.canAdminShow();
                            }
                        }
                    ];
                }
            }
        },

        countersManager: {
            bind: {
                formTriggerCount: '{formTriggerCount}',
                formWidgetCount: '{formWidgetCount}',
                contextMenuCount: '{contextMenuCount}',
                attributeGroupingCount: '{attributeGroupingCount}',
                formTriggers: '{formTriggersStore.data.length}',
                contextMenuItems: '{contextMenuItemsStore.data.length}',
                widgets: '{formWidgetsStore.data.length}',
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

        isMultitenatModeHiddenCombo: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isPrototype: '{theProcess.prototype}'
            },
            get: function (data) {
                return !(data.isMultitenant && (data.isEdit || data.isAdd) && !data.isPrototype);
            }
        },

        isMultitenatModeHiddenDisplay: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isView: '{actions.view}',
                isPrototype: '{theProcess.prototype}'
            },
            get: function (data) {
                return !(data.isMultitenant && data.isView && !data.isPrototype);
            }
        },

        contextMenuItemsStoreNewData: {
            bind: {
                theProcess: '{theProcess}'
            },
            get: function (data) {
                if (data.theProcess) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.ContextMenuItem');
                    const ret = [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                    return ret;
                }
                return [];
            }
        },

        formWidgetStoreNewData: {
            bind: {
                theProcess: '{theProcess}'
            },
            get: function (data) {
                if (data.theProcess) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.WidgetDefinition');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
            }
        },

        defaultFilterData: {
            bind: '{theProcess}',
            get: function (theObject) {
                if (theObject && !theObject.phantom) {
                    this.set('defaultFilterData._id', theObject.get('_id'));
                    this.set('defaultFilterData.name', theObject.get('name'));
                    this.set('defaultFilterProxy', {
                        url: Ext.String.format('/processes/{0}/filters', theObject.get('name')),
                        type: 'baseproxy'
                    });
                }
            }
        },

        defaultImportExportStoreDataManager: {
            bind: {
                filesTemplatesStore: '{exportFileTemplateStore}',
                theObject: '{theProcess}'
            },
            get: function (data) {
                const me = this;
                const _data = [];
                if (data.filesTemplatesStore) {
                    data.filesTemplatesStore.each(function (fileTemplate) {
                        _data.push({
                            _type: 'file',
                            _id: Ext.String.format('template:{0}', fileTemplate.get('_id')),
                            description: fileTemplate.get('description'),
                            _typeLabel: CMDBuildUI.locales.Locales.administration.navigation.datatemplate
                        });
                    });
                }
                me.set('defaultExportTemplateStoreData', _data);
            }
        },

        filterImportExportTemplates: {
            bind: {
                theProcessName: '{theProcess.name}'
            },
            get: function (data) {
                if (!this.get('actions.add')) {
                    const allDomains = Ext.getStore('domains.Domains')
                        .getRange()
                        .filter(function (item) {
                            return (
                                item.get('source') === data.theProcessName ||
                                item.get('destination') === data.theProcessName
                            );
                        });

                    const filterExportTemplates = [
                        function (item) {
                            if (CMDBuildUI.model.importexports.Template.types['export'] === item.get('type')) {
                                let res = false;
                                switch (item.get('targetType')) {
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                        res = item.get('targetName') === data.theProcessName;
                                        break;
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.domain:
                                        const domains = Ext.Array.filter(allDomains, function (domain) {
                                            return domain.get('name') === item.get('targetName');
                                        });
                                        res = domains.length;
                                        break;
                                    default:
                                        break;
                                }
                                return res;
                            }
                            return false;
                        }
                    ];
                    this.set('filterExportTemplates', filterExportTemplates);
                }
            }
        },

        attributeGroupEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.AttributeGrouping');
        },

        defaultOrderEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.AttributeOrder', {
                direction: 'ascending'
            });
        },

        formTriggersEmptyRecord: function () {
            return Ext.create('CMDBuildUI.model.FormTrigger');
        },

        parentGroupingManager: {
            bind: {
                parent: '{theProcess.parent}',
                attributeGroupsStore: '{attributeGroupsStore}'
            },
            get: function (data) {
                const me = this;
                const theProcess = me.get('theProcess');
                if (theProcess.phantom) {
                    this.get('attributeGroupsStore').removeAll();
                    theProcess.set('attributeGroups', null);
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
        processVersionStore: {
            alias: 'store.processVersion',
            proxy: {
                url: '/processes/{theProcess.name}/versions',
                type: 'baseproxy'
            },
            autoDestroy: true,
            pageSize: 0
        },

        defaultOrderStoreNew: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrderEmptyRecord}'
        },

        defaultOrderStore: {
            model: 'CMDBuildUI.model.AttributeOrder',
            alias: 'store.attribute-default-order',
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrderData}',
            autoDestroy: true
        },
        defaultOrderDirectionsStore: {
            autoLoad: true,
            autoDestroy: true,
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{defaultOrders}'
        },
        // this break the ui
        unorderedAttributesStore: {
            model: 'CMDBuildUI.model.Attribute',
            proxy: 'memory',
            data: '{unorderedAttributesData}',
            storeId: 'unorderedAttributesStore',
            filters: '{unorderedAttributes}',
            sorters: ['description'],
            autoDestroy: true
        },
        superprocessesStore: {
            data: '{allPrototypeProcesses}',
            sorters: ['description'],
            proxy: {
                type: 'memory'
            },
            filters: [
                function (item) {
                    return item.get('prototype');
                }
            ]
        },
        dmsCategoryTypesStore: {
            source: 'dms.DMSCategoryTypes',
            autoDestroy: true,
            storeId: 'dmsCategoryTypesStore'
        },
        messageAttributesStore: {
            source: '{attributesStore}',
            type: 'chained',
            sorters: [
                {
                    property: 'description',
                    direction: 'ASC'
                }
            ],
            filters: [
                function (item) {
                    return item.canAdminShow();
                }
            ],
            autoDestroy: true
        },
        allLookupAttributesStore: {
            source: '{attributesStore}',
            storeId: 'allLookupAttributesStore',
            type: 'chained',
            filters: [
                function (item) {
                    return item.get('type') === 'lookup' && !item.get('hidden');
                }
            ],
            sorters: [
                {
                    property: 'description',
                    direction: 'ASC'
                }
            ],
            autoDestroy: true
        },

        exportFileTemplateStore: {
            source: 'importexports.Templates',
            filters: '{filterExportTemplates}'
        },
        defaultExportTemplateStore: {
            fields: ['_type', '_id', '_typeLabel', 'description'],
            data: '{defaultExportTemplateStoreData}',
            proxy: {
                type: 'memory'
            },
            grouper: {
                property: '_typeLabel'
            },
            autoDestroy: true
        },

        multitenantModeStore: {
            type: 'multitenant-multitenantmode',
            data: '{tenantModes}'
        },
        attributeGroupsStoreNew: {
            model: 'CMDBuildUI.model.AttributeGrouping',
            alias: 'store.attribute-groupings-new',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                type: 'memory'
            },
            data: '{attributeGroupEmptyRecord}'
        },
        attributeGroupsStore: {
            source: '{theProcess.attributeGroups}',
            autoLoad: true
        },
        contextMenuComponentStore: {
            model: 'CMDBuildUI.model.base.Base',
            source: 'customcomponents.ContextMenus'
        },
        contextMenuItemsStoreNew: {
            model: 'CMDBuildUI.model.ContextMenuItem',
            proxy: {
                type: 'memory'
            },
            data: '{contextMenuItemsStoreNewData}',
            autoDestroy: true
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
            // autoDestroy: true,
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
        widgetTypesStore: {
            type: 'common-widgettypes',
            data: '{formWidgetTypes}'
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

        defaultFilterStore: {
            proxy: '{defaultFilterProxy}',
            data: '{defaultFilterData}',
            autoLoad: true,
            autoDestroy: true,
            pageSize: 0
        },
        attriubteGroupingDisplayModeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attriubteGroupingDisplayModes}',
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
                (this.get('actions.view') && this.data.theProcess.data.active) /*this.get('theProcess.active')*/
        );
        this.set(
            'toolbarHiddenButtons.disable',
            !this.get('actions.view') ||
                (this.get('actions.view') && !this.data.theProcess.data.active) /*!this.get('theProcess.active')*/
        );
        this.set('toolbarHiddenButtons.version', this.get('actions.add') || this.get('theProcess.prototype'));
        this.set('toolbarHiddenButtons.print', this.get('actions.add'));
        return true;
    },

    configDisabledTabs: function () {
        this.set('disabledTabs.properties', false);
        this.set('disabledTabs.attributes', !this.get('actions.view'));
        this.set('disabledTabs.domains', !this.get('actions.view'));
        this.set('disabledTabs.tasks', !this.get('actions.view') || this.get('theProcess.prototype'));
        this.set('disabledTabs.forms', !this.get('actions.view') || this.get('theProcess.prototype'));
        this.set('disabledTabs.helps', !this.get('actions.view') || this.get('theProcess.prototype'));
        this.set('disabledTabs.export', !this.get('actions.view') || this.get('theProcess.prototype'));
        this.set('disabledTabs.permissions', !this.get('actions.view') || this.get('theProcess.prototype'));
    },

    /**
     *
     * @param {Number} currrentTabIndex
     */
    toggleEnableTabs: function (currrentTabIndex) {
        const me = this;
        const view = me.getView().down('administration-content-processes-tabpanel');
        const tabs = view.items.items;

        tabs.forEach(function (tab) {
            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                me.set('disabledTabs.' + tab.reference, true);
            }
        });
    }
});
