Ext.define('CMDBuildUI.view.administration.content.classes.ViewModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-classes-view',

    data: {
        pluginMode: false,
        isClass: true,
        activeTab: 0,
        objectTypeName: null,
        theObject: null,
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
            layers: false,
            geoattributes: false,
            import_export: true,
            fieldsmanagement: false,
            permissions: false
        },
        formTriggerCount: 0,
        formWidgetCount: 0,
        contextMenuCount: 0,
        attributeGroupingCount: 0,
        attributeGroups: [],
        toolbarHiddenButtons: {
            edit: true, // action !== view
            delete: true, // action !== view
            enable: true, //action !== view && theObject.active
            disable: true, // action !== view && !theObject.active
            print: true // action !== view
        },
        checkboxNoteInlineClosed: {
            disabled: true
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
                canModify: '{theObject._can_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify);
                this.set('toolAction._canUpdate', data.canModify);
                this.set('toolAction._canDelete', data.canModify);
                this.set('toolAction._canClone', data.canModify);
                this.set('toolAction._canActiveToggle', data.canModify);
            }
        },

        theObjectManager: {
            bind: '{theObject}',
            get: function (theObject) {
                // conversion backend -> frontend (f.e. "hippo:solid" -> "x-fas fa-hippo")
                if (theObject) {
                    const rawIconFont = theObject.get('icon_font');
                    if (rawIconFont) {
                        theObject.set('icon_font', CMDBuildUI.util.helper.IconHelper.getIconIdFromDBValue(rawIconFont));

                        theObject.set(
                            'icon_description',
                            CMDBuildUI.util.helper.IconHelper.getIconDescription(rawIconFont.split(':')[0])
                        );
                    }
                }
                this.set('activeTab', this.get('activeTabs.classes'));
                this.set('storesAutoload', theObject ? true : false);
                this.set(
                    'isMultitenant',
                    CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled)
                );
                this.set(
                    'formTriggersData',
                    theObject && theObject.getAssociatedData().formTriggers
                        ? theObject.getAssociatedData().formTriggers
                        : []
                );
                this.set(
                    'contextMenuItemData',
                    theObject && theObject.getAssociatedData().contextMenuItems
                        ? theObject.getAssociatedData().contextMenuItems
                        : []
                );
                this.set(
                    'formWidgetData',
                    theObject && theObject.getAssociatedData().widgets ? theObject.getAssociatedData().widgets : []
                );
                this.set(
                    'defaultOrderData',
                    theObject && theObject.getAssociatedData().defaultOrder
                        ? theObject.getAssociatedData().defaultOrder
                        : []
                );
                const classesData = Ext.getStore('classes.Classes').getRange();
                this.set('allClasses', classesData);
                this.set(
                    'allClassesWithClass',
                    Ext.Array.insert(classesData, 0, [
                        {
                            name: CMDBuildUI.model.classes.Class.masterParentClass,
                            description: CMDBuildUI.locales.Locales.administration.common.labels['default'],
                            prototype: true
                        }
                    ])
                );
            }
        },

        classLabel: {
            bind: '{theObject.description}',
            get: function () {
                return CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel;
            }
        },

        localizedComboOptionsManager: {
            get: function () {
                this.set('tenantModes', CMDBuildUI.model.users.Tenant.getTenantModes());
                this.set('attachmentsDescriptionModes', CMDBuildUI.model.attachments.Attachment.getDescriptionModes());
                this.set('contexMenuTypes', CMDBuildUI.model.ContextMenuItem.getTypes());
                this.set('contextMenuApplicabilities', CMDBuildUI.model.ContextMenuItem.getVisibilities());
                this.set('classTypes', CMDBuildUI.model.classes.Class.getClasstypes());
                this.set('defaultOrders', CMDBuildUI.model.AttributeOrder.getDefaultOrders());
                this.set('formWidgetTypes', CMDBuildUI.model.WidgetDefinition.getTypes());
                this.set(
                    'attriubteGroupingDisplayModes',
                    CMDBuildUI.util.administration.helper.ModelHelper.getAttriubteGroupingDisplayModes()
                );
            }
        },

        updateNoteInlineClosedCheckboxState: {
            bind: '{theObject.noteInline}',
            get: function (noteInline) {
                if (noteInline) {
                    this.set('checkboxNoteInlineClosed.disabled', false);
                } else {
                    this.set('checkboxNoteInlineClosed.disabled', true);
                    this.set('theObject.noteInlineClosed', false);
                }
            }
        },

        updateAttachmnentsInlineClosedCheckboxState: {
            bind: '{theObject.attachmentsInline}',
            get: function (attachmentsInline) {
                if (attachmentsInline) {
                    this.set('checkboxAttachmentsInlineClosed.disabled', false);
                } else {
                    this.set('checkboxAttachmentsInlineClosed.disabled', true);
                    this.set('theObject.attachmentsInlineClosed', false);
                }
            }
        },

        isMultitenatModeHiddenCombo: {
            bind: {
                isMultitenant: '{isMultitenant}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isPrototype: '{theObject.prototype}'
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
                isPrototype: '{theObject.prototype}'
            },
            get: function (data) {
                return !(data.isMultitenant && data.isView && !data.isPrototype);
            }
        },

        multitenantModeManager: {
            bind: {
                mode: '{theObject.multitenantMode}',
                multitenantModeStore: '{multitenantModeStore}'
            },
            get: function (data) {
                if (data.mode && data.multitenantModeStore) {
                    this.set(
                        'theObject._multitenantMode_description',
                        data.multitenantModeStore.findRecord('value', data.mode).get('label')
                    );
                }
            }
        },

        action: {
            bind: {
                theObject: '{theObject}',
                isEdit: '{actions.edit}',
                isAdd: '{actions.add}',
                isView: '{actions.view}'
            },
            get: function (data) {
                const me = this;
                if (data.theObject) {
                    this.configToolbarButtons();
                    this.configDisabledTabs(data.theObject);
                }
                if (data.isEdit) {
                    data.theObject.getAttributes().then(function (attributesStore) {
                        if (!me.destroyed) {
                            me.set('attributesStore', attributesStore);
                        }
                    });
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.edit;
                } else if (data.isAdd) {
                    return CMDBuildUI.util.administration.helper.FormHelper.formActions.add;
                } else {
                    data.theObject.getAttributes().then(function (attributesStore) {
                        if (!me.destroyed) {
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
                const form = this.getView()
                    .down('administration-content-classes-tabitems-properties-fieldsets-generaldatafieldset')
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
                active: '{theObject.active}'
            },
            get: function (get) {
                this.configToolbarButtons();
            }
        },

        isSuperClassManager: {
            bind: {
                prototype: '{theObject.prototype}',
                isMultitenant: '{isMultitenant}',
                multitenantMode: '{theObject.multitenantMode}'
            },
            get: function (data) {
                if (data.prototype) {
                    this.set('theObject.multitenantMode', '');
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
                type: '{theObject.type}',
                prototype: '{theObject.prototype}'
            },
            get: function (data) {
                if (data.type) {
                    const isSimple = data.type === 'simple';
                    return isSimple;
                }
            }
        },

        isStandardClassAndIsViewAction: {
            bind: '{theObject.type}',
            get: function (type) {
                if (type) {
                    return type === 'standard' && this.get('actions.view');
                }
            }
        },

        hideParentCombobox: {
            bind: {
                type: '{theObject.type}',
                view: '{actions.view}'
            },
            get: function (data) {
                return !data.type || data.type === 'simple' || data.view;
            }
        },
        hideParentDisplayfield: {
            bind: {
                type: '{theObject.type}',
                view: '{actions.view}'
            },
            get: function (data) {
                return !data.type || data.type === 'simple' || !data.view;
            }
        },

        attributeProxy: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject.get('name') && !theObject.phantom) {
                    return {
                        url: Ext.String.format('/classes/{0}/attributes', theObject.get('name')),
                        type: 'baseproxy'
                    };
                }
            }
        },

        defaultFilterData: {
            bind: '{theObject}',
            get: function (theObject) {
                if (theObject && !theObject.phantom) {
                    this.set('defaultFilterData._id', theObject.get('_id'));
                    this.set('defaultFilterData.name', theObject.get('name'));
                    this.set('defaultFilterProxy', {
                        url: Ext.String.format('/classes/{0}/filters', theObject.get('name')),
                        type: 'baseproxy'
                    });
                }
            }
        },
        defaultFilterFilter: {
            bind: '{theObject}',
            get: function (theObject) {
                return [
                    function (item) {
                        return item.get('target') === theObject.get('name');
                    }
                ];
            }
        },

        unorderedAttributes: {
            bind: {
                theObject: '{theObject}',
                attributeStoreLoaded: '{attributesStore.complete}'
            },
            get: function (data) {
                if (
                    data.theObject &&
                    data.attributeStoreLoaded &&
                    typeof data.theObject.getAssociatedData !== 'function'
                ) {
                    const defaultOrder = data.theObject.getAssociatedData().defaultOrder;
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
                            return !found && item.canAdminShow();
                        }
                    ];
                }
            }
        },

        getParentDescription: {
            bind: {
                theObject: '{theObject}',
                superclassesStore: '{superclassesStore}'
            },
            get: function (data) {
                const me = this;
                const theObject = data.theObject;
                const superclassesStore = data.superclassesStore; // data.superclassesStore;
                if (theObject && superclassesStore) {
                    const parent = superclassesStore.findRecord('name', me.get('theObject.parent'));
                    if (parent) {
                        me.set('parentDescription', parent.get('description'));
                        return parent.get('description');
                    }
                }
            }
        },

        parentGroupingManager: {
            bind: {
                parent: '{theObject.parent}',
                attributeGroupsStore: '{attributeGroupsStore}'
            },
            get: function (data) {
                const me = this;
                const theObject = me.get('theObject');
                if (theObject.phantom) {
                    this.get('attributeGroupsStore').removeAll();
                    theObject.set('attributeGroups', null);
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
        },

        filterImportExportTemplates: {
            bind: {
                theObjectName: '{theObject.name}'
            },
            get: function (data) {
                var me = this;
                if (!this.get('actions.add')) {
                    const allDomains = Ext.getStore('domains.Domains')
                        .getRange()
                        .filter(function (item) {
                            return (
                                item.get('source') === data.theObjectName ||
                                item.get('destination') === data.theObjectName
                            );
                        });
                    const filterImportTemplates = [
                        function (item) {
                            if (
                                item.get('type') === CMDBuildUI.model.importexports.Template.types['import'] ||
                                item.get('type') === CMDBuildUI.model.importexports.Template.types.importexport
                            ) {
                                let res = false;
                                switch (item.get('targetType')) {
                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                        res = item.get('targetName') === data.theObjectName;
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
                                me._updateDefaultImportData();
                                return res;
                            }
                            return false;
                        }
                    ];

                    const filterExportTemplates = [
                        function (item) {
                            if (
                                CMDBuildUI.model.importexports.Template.types['export'] === item.get('type') ||
                                item.get('type') === CMDBuildUI.model.importexports.Template.types.importexport
                            ) {
                                let res = false;
                                switch (item.get('targetType')) {
                                    case CMDBuildUI.model.administration.MenuItem.types.klass:
                                        res = item.get('targetName') === data.theObjectName;
                                        break;
                                    case CMDBuildUI.model.administration.MenuItem.types.domain:
                                        const domains = Ext.Array.filter(allDomains, function (domain) {
                                            return domain.get('name') === item.get('targetName');
                                        });
                                        res = domains.length;
                                        break;
                                    default:
                                        break;
                                }
                                me._updateDefaultExportData();
                                return res;
                            }
                            return false;
                        }
                    ];
                    this.set('filterImportTemplates', filterImportTemplates);
                    this.set('filterExportTemplates', filterExportTemplates);
                }
            }
        },

        formWidgetStoreNewData: {
            bind: {
                theObject: '{theObject}'
            },
            get: function (data) {
                if (data.theObject) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.WidgetDefinition');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
            }
        },

        contextMenuItemsStoreNewData: {
            bind: {
                theObject: '{theObject}'
            },
            get: function (data) {
                if (data.theObject) {
                    const cleanRecord = Ext.create('CMDBuildUI.model.ContextMenuItem');
                    return [CMDBuildUI.util.administration.helper.ModelHelper.setReadState(cleanRecord)];
                }
                return [];
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
        }
    },

    stores: {
        allClassesStore: {
            data: '{allClasses}',
            proxy: {
                type: 'memory'
            }
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

        unorderedAttributesStore: {
            storeId: 'unorderedAttributesStore',
            source: '{attributesStore}',
            filters: '{unorderedAttributes}',
            sorters: ['description'],
            autoDestroy: true
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

        dmsCategoryTypesStore: {
            source: 'dms.DMSCategoryTypes',
            autoDestroy: true
        },

        defaultFilterStore: {
            source: 'searchfilters.Searchfilters',
            filters: '{defaultFilterFilter}'
        },

        classTypeStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            fields: ['value', 'label'],
            proxy: {
                type: 'memory'
            },
            data: '{classTypes}',
            autoDestroy: true
        },

        importFileTemplateStore: {
            source: 'importexports.Templates',
            filters: '{filterImportTemplates}',
            autoDestroy: true
        },

        defaultImportTemplateStore: {
            fields: ['_type', '_id', '_typeLabel', 'description'],
            data: '{defaultImportStoreData}',
            proxy: {
                type: 'memory'
            },
            grouper: {
                property: '_typeLabel'
            },
            autoDestroy: true
        },

        exportFileTemplateStore: {
            source: 'importexports.Templates',
            filters: '{filterExportTemplates}',
            autoDestroy: true
        },

        defaultExportTemplateStore: {
            // source: 'importexports.Templates',
            // filters: '{filterExportTemplates}'
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

        attributeGroupsStore: {
            source: '{theObject.attributeGroups}'
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
        }
    },

    configToolbarButtons: function () {
        this.set('disabledTabs.properties', false);
        this.set('toolbarHiddenButtons.edit', !this.get('actions.view'));
        this.set('toolbarHiddenButtons.delete', !this.get('actions.view'));
        this.set(
            'toolbarHiddenButtons.enable',
            !this.get('actions.view') ||
                (this.get('actions.view') && this.data.theObject.data.active) /*this.get('theObject.active')*/
        );
        this.set(
            'toolbarHiddenButtons.disable',
            !this.get('actions.view') ||
                (this.get('actions.view') && !this.data.theObject.data.active) /*!this.get('theObject.active')*/
        );
        this.set('toolbarHiddenButtons.print', this.get('actions.add'));

        return true;
    },

    configDisabledTabs: function () {
        const me = this;
        const theObject = this.get('theObject') || this.getData().theObject;
        const gisEnabled = CMDBuildUI.util.helper.Configurations.get('cm_system_gis_enabled');
        const importExportDisabled =
            (theObject && theObject.get('type') === CMDBuildUI.model.classes.Class.classtypes.simple) ||
            (theObject && theObject.get('prototype')) ||
            !me.get('actions.view');
        me.set('disabledTabs.properties', false);
        me.set('disabledTabs.attributes', !me.get('actions.view'));
        me.set('disabledTabs.fieldsmanagement', importExportDisabled);
        me.set(
            'disabledTabs.domains',
            (theObject && theObject.get('type') === CMDBuildUI.model.classes.Class.classtypes.simple) ||
                !me.get('actions.view')
        );
        me.set(
            'disabledTabs.layers',
            !gisEnabled ||
                (theObject && theObject.get('type') === CMDBuildUI.model.classes.Class.classtypes.simple) ||
                !me.get('actions.view')
        );
        me.set('disabledTabs.geoattributes', !gisEnabled || importExportDisabled || !me.get('actions.view'));
        me.set('disabledTabs.import_export', importExportDisabled);
        me.set(
            'disabledTabs.permissions',
            !me.get('theSession.rolePrivileges.admin_roles_view') || !me.get('actions.view')
        );
    },

    /**
     *
     * @param {Number} currrentTabIndex
     */
    toggleEnableTabs: function (currrentTabIndex) {
        const me = this;
        const view = me.getView().down('administration-content-classes-tabpanel');
        const tabs = view.items.items;

        tabs.forEach(function (tab) {
            if (tab.tabConfig.tabIndex !== currrentTabIndex) {
                me.set('disabledTabs.' + tab.reference, true);
            }
        });
    },

    /**
     * @private
     */
    privates: {
        /**
         * @cfg {Ext.util.DelayedTask}
         */
        _notifyExportTemplate: null,

        /**
         * @cfg {Ext.util.DelayedTask}
         */
        _notifyImportTemplate: null,

        /**
         * refresh defaultExportTemplateStoreData
         */
        _updateDefaultExportData: function () {
            const me = this;

            if (me._notifyExportTemplate) {
                me._notifyExportTemplate.cancel();
            }
            me._notifyExportTemplate = new Ext.util.DelayedTask(function () {
                if (me.isDestroyed) {
                    return;
                }
                const _data = [];
                const filesTemplatesStore = me.get('exportFileTemplateStore');
                if (filesTemplatesStore) {
                    filesTemplatesStore.each(function (fileTemplate) {
                        _data.push({
                            _type: 'file',
                            _id: fileTemplate.get('_id'),
                            description: fileTemplate.get('description'),
                            _typeLabel: CMDBuildUI.locales.Locales.administration.navigation.datatemplate
                        });
                    });
                }
                me.set('defaultExportTemplateStoreData', _data);
            });
            me._notifyExportTemplate.delay(500);
        },

        /**
         * refresh defaultImportStoreData
         */
        _updateDefaultImportData: function () {
            const me = this;
            if (me._notifyImportTemplate) {
                me._notifyImportTemplate.cancel();
            }
            me._notifyImportTemplate = new Ext.util.DelayedTask(function () {
                if (me.isDestroyed) {
                    return;
                }
                const _data = [],
                    filesTemplatesStore = me.get('importFileTemplateStore'),
                    theObject = me.get('theObject');

                if (filesTemplatesStore) {
                    filesTemplatesStore.each(function (fileTemplate) {
                        _data.push({
                            _type: 'file',
                            _id: Ext.String.format('template:{0}', fileTemplate.get('_id')),
                            description: fileTemplate.get('description'),
                            _typeLabel: CMDBuildUI.locales.Locales.administration.navigation.datatemplate
                        });
                    });
                }
                if (theObject) {
                    theObject.getImportExportGates(true).then(function (gates) {
                        if (gates) {
                            gates.each(function (gateTemplate) {
                                let label = '';
                                switch (gateTemplate.get('_handler_type')) {
                                    case 'database':
                                        label =
                                            CMDBuildUI.locales.Locales.administration.navigation.databasegatetemplate;
                                        break;
                                    case 'ifc':
                                        label = CMDBuildUI.locales.Locales.administration.navigation.ifcgatetemplate;
                                        break;
                                    case 'cad':
                                        label = CMDBuildUI.locales.Locales.administration.navigation.gisgatetemplate;
                                        break;
                                    default:
                                        break;
                                }
                                _data.push({
                                    _type: gateTemplate.get('_handler_type'),
                                    _id: Ext.String.format('gate:{0}', gateTemplate.get('_id')),
                                    description: gateTemplate.get('description'),
                                    _typeLabel: label
                                });
                            });
                        }
                        me.set('defaultImportStoreData', _data);
                    });
                }
            });
            me._notifyImportTemplate.delay(500);
        }
    }
});
