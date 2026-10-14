Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.templates.card.CardModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.view-administration-content-importexport-gatetemplates-tabitems-templates-card',
    data: {
        actions: {
            view: true,
            edit: false,
            add: false
        },

        toolAction: {
            _canAdd: false,
            _canClone: false,
            _canUpdate: false,
            _canDelete: false,
            _canActiveToggle: false
        },
        classAttributes: null,
        freeAttributesFilters: [],
        mergeModeDisabled: true
    },
    formulas: {
        toolsManager: {
            bind: {
                canModify: '{theSession.rolePrivileges.admin_etl_modify}'
            },
            get: function (data) {
                this.set('toolAction._canAdd', data.canModify === true);
                this.set('toolAction._canClone', data.canModify === true);
                this.set('toolAction._canUpdate', data.canModify === true);
                this.set('toolAction._canDelete', data.canModify === true);
                this.set('toolAction._canActiveToggle', data.canModify === true);
            }
        },

        gateTypes: function () {
            CMDBuildUI.util.administration.helper.ModelHelper.getGateTypes();
        },

        isClone: {
            bind: '{theGateTemplate}',
            get: function (theGateTemplate) {
                return (theGateTemplate && theGateTemplate.phantom) || false;
            }
        },

        panelTitle: {
            bind: '{theGateTemplate.description}',
            get: function (description) {
                var fileFormat = this.get('theGateTemplate.fileFormat');
                var importTypeTitle;
                if (CMDBuildUI.model.importexports.Template.fileTypes.cad === fileFormat) {
                    importTypeTitle = CMDBuildUI.locales.Locales.administration.gates.addgistemplate;
                } else if (CMDBuildUI.model.importexports.Template.fileTypes.database === fileFormat) {
                    importTypeTitle = CMDBuildUI.locales.Locales.administration.gates.adddatabasetemplate;
                }
                var title = Ext.String.format('{0} - {1}', importTypeTitle, description);
                this.getParent().set('title', title);
            }
        },

        allClassesOrDomainsData: {
            bind: '{theGateTemplate.targetType}',
            get: function (targetType) {
                return Ext.getStore('classes.Classes')
                    .getData()
                    .items.filter(function (item) {
                        return !item.get('prototype') && item.get('type') === 'standard';
                    });
            }
        },
        classAttributesManager: {
            bind: '{theGateTemplate.targetName}',
            get: function (className) {
                var me = this;
                var klass = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(className);
                var allAttributes = [];
                me.set('mergeModeDisabled', true);
                if (klass) {
                    me.set('geoAttributesUrl', CMDBuildUI.util.api.Classes.getGeoAttributes(klass.get('name')));
                    klass.getAttributes(true).then(function (attributesStore) {
                        if (!me.destroyed) {
                            me.set('allClassAttributesStore', attributesStore);
                            // GET geoattribute and merge the data
                            me.get('geoAttributesStore').load(function (geoAttributes) {
                                var geoAttributesStore = this;
                                var allowedAttributes = [CMDBuildUI.util.helper.ModelHelper.extraFields.notes];
                                var allowTenantAttribute = CMDBuildUI.util.helper.Configurations.get(
                                    CMDBuildUI.model.Configuration.multitenant.enabled
                                );
                                if (allowTenantAttribute) {
                                    allowedAttributes.push('IdTenant');
                                }
                                attributesStore.each(function (attribute) {
                                    if (attribute.canAdminShow(allowedAttributes)) {
                                        if (
                                            attribute.get('type') ===
                                            CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.reference
                                        ) {
                                            me.set('mergeModeDisabled', false);
                                        }
                                        allAttributes.push({
                                            _type: 'attribute',
                                            _typeLabel: CMDBuildUI.locales.Locales.administration.attributes.attributes,
                                            _subtype: attribute.get('type'),
                                            name: attribute.get('name'),
                                            description: attribute.get('description'),
                                            targetClass: attribute.get('targetClass')
                                        });
                                    }
                                });
                                geoAttributesStore.each(function (geoAttribute) {
                                    if (geoAttribute.get('type') === 'geometry') {
                                        allAttributes.push({
                                            _type: 'geoattribute',
                                            _typeLabel:
                                                CMDBuildUI.locales.Locales.administration.classes.strings
                                                    .geaoattributes,
                                            _subtype: geoAttribute.get('subtype'),
                                            name: geoAttribute.get('name'),
                                            description: geoAttribute.get('description'),
                                            targetClass: geoAttribute.get('targetClass')
                                        });
                                    }
                                });
                                me.set('allAttributes', allAttributes);
                                me.filterFreeAttributes();
                            });
                        } else {
                            me.set('allAttributes', null);
                        }
                    });
                } else {
                    me.set('allAttributes', null);
                }
            }
        },
        mergeModeManager: {
            bind: '{theGateTemplate.mergeMode}',
            get: function (mergeMode) {
                if (mergeMode === CMDBuildUI.model.importexports.Template.missingRecords.modifycard) {
                    this.set('isModifyCard', true);
                } else {
                    this.set('isModifyCard', false);
                }
            }
        },
        gridAttributesData: {
            bind: '{theGateTemplate.columns}',
            get: function (columns) {
                if (columns) {
                    Ext.Array.forEach(columns.getRange(), function (item, index) {
                        item.set('index', index);
                    });
                    return columns.getRange();
                }
                return [];
            }
        },

        mergeModes: {
            get: function () {
                return CMDBuildUI.model.importexports.Template.getMergeModes();
            }
        },
        attributeModes: {
            get: function () {
                return Ext.Array.merge([], CMDBuildUI.model.importexports.Attribute.getAttributeModes(), [
                    {
                        value: CMDBuildUI.model.importexports.GateAttribute.relativelocation,
                        label: CMDBuildUI.locales.Locales.administration.gates.relativelocation
                    }
                ]);
            }
        },
        attributeModesLookup: {
            get: function () {
                return CMDBuildUI.model.importexports.Attribute.getAttributeModes('lookup');
            }
        },

        attributeModesAllData: {
            get: function () {
                return Ext.Array.unique(
                    Ext.Array.merge(
                        [],
                        CMDBuildUI.model.importexports.Attribute.getAttributeModes(),
                        [
                            {
                                value: CMDBuildUI.model.importexports.GateAttribute.relativelocation,
                                label: CMDBuildUI.locales.Locales.administration.gates.relativelocation
                            },
                            {
                                value: 'default',
                                label: CMDBuildUI.locales.Locales.administration.common.labels.default
                            }
                        ],
                        CMDBuildUI.model.importexports.Attribute.getAttributeModes('lookup')
                    )
                );
            }
        },

        attributeModesAllFilter: {
            get: function (store) {
                return [];
            }
        },

        gateManager: {
            bind: {
                gateId: '{theGate._id}',
                theGateTemplate: '{theGateTemplate}'
            },
            get: function (data) {
                if (data.gateId && data.theGateTemplate) {
                    data.theGateTemplate.columns().each(function (col, index) {
                        col.set('index', index);
                    });
                    this.set('theGateTemplate.gateId', data.gateId);
                }
            }
        },
        mergemodeAttribute: {
            bind: {
                attribute: '{theGateTemplate.mergeMode_when_missing_update_attr}',
                allAttributes: '{allClassAttributesStore}'
            },
            get: function (data) {
                var vm = this;
                var view = this.getView();
                var container = view.down('#valueContainer');
                if (data.attribute && data.allAttributes) {
                    container.removeAll();
                    var attribute = CMDBuildUI.util.helper.ModelHelper.getModelFieldFromAttribute(
                        data.allAttributes.findRecord('name', data.attribute)
                    );
                    vm.set('_merge_attribute', attribute);
                    var editor = CMDBuildUI.util.helper.FormHelper.getEditorForField(attribute);
                    if (!editor.listeners) {
                        editor.listeners = {};
                    }
                    editor.listeners.beforedestroy = function (input, newValue, oldValue) {
                        var template = input.lookupViewModel().get('theGateTemplate');
                        if (template) {
                            template.set('mergeMode_when_missing_update_value', '');
                        }
                    };
                    var display = CMDBuildUI.util.helper.FormHelper.getReadOnlyField(attribute, editor.recordLinkName);

                    var field = {
                        itemId: 'mergeMode_when_missing_update_value_display',
                        bind: {
                            value: '{theGateTemplate._mergeMode_when_missing_update_value_description}',
                            hidden: '{!actions.view}'
                        }
                    };

                    field.renderer = function (value) {
                        return value;
                    };

                    container.add([
                        Ext.merge({}, editor, {
                            allowBlank: false,
                            itemId: 'mergeMode_when_missing_update_value_input',
                            bind: {
                                value: '{theGateTemplate.mergeMode_when_missing_update_value}',
                                hidden: '{actions.view}'
                            }
                        }),
                        Ext.merge({}, display, field)
                    ]);
                } else {
                    if (container) {
                        container.removeAll();
                    }
                }
            }
        },
        keyAttributesData: {
            bind: {
                importKeyAttribute: '{theGateTemplate._importKeyAttribute}',
                allAttributes: '{assignedAttributes}'
            },
            get: function (data) {
                var me = this;
                if (data.allAttributes && data.allAttributes.length) {
                    var attributes =
                            typeof data.importKeyAttribute === 'string'
                                ? data.importKeyAttribute.split(',')
                                : data.importKeyAttribute,
                        _attributes = [];

                    if (!Ext.isEmpty(data.importKeyAttribute)) {
                        Ext.Array.forEach(attributes, function (_attribute) {
                            var fullAttribute = Ext.Array.findBy(data.allAttributes, function (attribute) {
                                return attribute.name === _attribute;
                            });
                            if (fullAttribute) {
                                _attributes.push({
                                    name: _attribute,
                                    description: fullAttribute.description
                                });
                            }
                        });
                    }
                    var freeKeyAttributes = [];
                    me.set('freeKeyAttributes', null);
                    Ext.Array.forEach(data.allAttributes, function (_attribute) {
                        if (attributes.indexOf(_attribute.name) < 0) {
                            freeKeyAttributes.push({
                                name: _attribute.name,
                                description: _attribute.description
                            });
                        }
                    });
                    me.set('freeKeyAttributes', freeKeyAttributes);
                    return _attributes;
                }
                return [];
            }
        },
        dateFormatsData: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.dateFormatsData();
            }
        },

        timeFormatsData: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.timeFormatsData();
            }
        },
        dateTimeFormat: {
            bind: {
                dateFormat: '{theGateTemplate.dateFormat}',
                timeFormat: '{theGateTemplate.timeFormat}'
            },
            get: function (data) {
                var timeFormat = Ext.Array.findBy(this.get('timeFormatsData') || [], function (item) {
                    return item.value === data.timeFormat;
                });
                var dateFormat = Ext.Array.findBy(this.get('dateFormatsData') || [], function (item) {
                    return item.value === data.dateFormat;
                });
                if (Ext.isEmpty(dateFormat) && Ext.isEmpty(timeFormat)) {
                    return CMDBuildUI.locales.Locales.main.preferences.defaultvalue;
                } else if (Ext.isEmpty(dateFormat) && !Ext.isEmpty(timeFormat)) {
                    return Ext.String.format(
                        '{0} {1}',
                        CMDBuildUI.locales.Locales.main.preferences.defaultvalue,
                        timeFormat.label
                    );
                } else if (!Ext.isEmpty(dateFormat) && Ext.isEmpty(timeFormat)) {
                    return Ext.String.format(
                        '{0} {1}',
                        dateFormat.label,
                        CMDBuildUI.locales.Locales.main.preferences.defaultvalue
                    );
                } else {
                    return Ext.String.format('{0} {1}', dateFormat.label, timeFormat.label);
                }
            }
        },
        decimalsSeparatorsData: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.decimalsSeparatorsData();
            }
        },

        filterFreeAttributesManager: {
            bind: {
                allAttributes: '{allAttributes}',
                assignedAttributes: '{theGateTemplate.columns}'
            },
            get: function (data) {
                var me = this;
                if (data.allAttributes) {
                    var freeAttributes = [],
                        assignedAttributes = [];
                    Ext.Array.forEach(data.allAttributes, function (attribute) {
                        if (!me.get('theGateTemplate.columns').findRecord('attribute', attribute.name)) {
                            freeAttributes.push(attribute);
                        } else {
                            me.manageDataFormatFieldset(attribute);
                            assignedAttributes.push(attribute);
                        }
                    });
                    me.set('assignedAttributes', assignedAttributes);
                    me.set('freeAttributes', freeAttributes);
                }
            }
        }
    },
    filterFreeAttributes: function () {
        var me = this;
        me.set('freeAttributes', []);
        me.set('dataFormatHidden', true);
        me.set('dataFormatTimeHidden', true);
        me.set('dataFormatDateHidden', true);
        me.set('dataFormatDateTimeHidden', true);
        me.set('dataFormatDecimalHidden', true);

        var allAttributes = me.get('allAttributes');
        var freeAttributes = [],
            assignedAttributes = [];
        Ext.Array.forEach(allAttributes, function (attribute) {
            if (!me.get('theGateTemplate.columns').findRecord('attribute', attribute.name)) {
                freeAttributes.push(attribute);
            } else {
                me.manageDataFormatFieldset(attribute);
                assignedAttributes.push(attribute);
            }
        });
        me.set('assignedAttributes', assignedAttributes);
        me.set('freeAttributes', freeAttributes);
    },
    manageDataFormatFieldset: function (item) {
        var me = this;
        var theTemplate = me.get('theGateTemplate');
        if (theTemplate) {
            switch (item._subtype) {
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.date:
                    me.set('dataFormatHidden', false);
                    me.set('dataFormatDateHidden', false);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.time:
                    me.set('dataFormatHidden', false);
                    me.set('dataFormatTimeHidden', false);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.datetime:
                    me.set('dataFormatHidden', false);
                    me.set('dataFormatTimeHidden', false);
                    me.set('dataFormatDateHidden', false);
                    me.set('dataFormatDateTimeHidden', false);
                    break;
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.double:
                case CMDBuildUI.util.helper.ModelHelper.cmdbuildtypes.decimal:
                    me.set('dataFormatHidden', false);
                    me.set('dataFormatDecimalHidden', false);
                    break;
            }
        }
    },
    stores: {
        attributeModesAllStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attributeModesAllData}',
            filters: '{attributeModesAllFilter}'
        },

        attributeModesReferenceStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{attributeModes}'
        },
        attributeModesReferenceLightStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            filters: [
                function (item) {
                    return item.get('value') !== CMDBuildUI.model.importexports.GateAttribute.relativelocation;
                }
            ],
            data: '{attributeModes}'
        },
        attributeModesLookupStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            filters: [
                function (item) {
                    return item.get('value') !== 'description_translation';
                }
            ],
            data: '{attributeModesLookup}'
        },
        allEmailAccounts: {
            type: 'chained',
            source: 'emails.Accounts',
            filters: [
                function (template) {
                    return template.get('active') === true;
                }
            ],
            autoLoad: true,
            autoDestroy: true
        },

        notificationEmailTemplates: {
            type: 'chained',
            source: 'emails.Templates',
            filters: [
                function (template) {
                    return template.get('provider') === 'email' && template.get('active') === true;
                }
            ],
            autoLoad: true,
            autoDestroy: true
        },
        gateTypesTore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            data: '{gateTypes}',
            autoDestroy: true
        },

        classAtributesStore: {
            fields: ['code', 'description'],
            data: '{classAttributes}',
            autoDestroy: true
        },
        geoAttributesStore: {
            model: 'CMDBuildUI.model.map.GeoAttribute',
            proxy: {
                type: 'baseproxy',
                url: '{geoAttributesUrl}'
            },
            autoDestroy: true,
            autoLoad: false
        },
        allAttributesStore: {
            fields: ['_type', '_typeLabel', '_subType', 'name', 'description', 'targetClass'],
            data: '{allAttributes}',
            proxy: {
                type: 'memory'
            },
            autoDestroy: true
        },
        allClassOrDomainsAtributesFiltered: {
            source: '{allClassOrDomainsAtributes}',
            sorters: 'description',
            filters: '{allClassOrDomainsAttributesFilter}'
        },
        freeAttributesStore: {
            fields: ['_type', '_typeLabel', '_subType', 'name', 'description', 'targetClass'],
            data: '{freeAttributes}',
            proxy: {
                type: 'memory'
            },
            grouper: {
                property: '_typeLabel'
            },
            sorters: 'description',
            autoLoad: true,
            autoDestroy: true
        },
        attributeToEdit: {
            source: '{theGateTemplate.columns}',
            autoDestroy: true
        },
        allSelectedAttributesStore: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.importexports.GateAttribute',
            data: '{gridAttributesData}',
            sorters: ['index'],
            autoDestroy: true
        },

        newSelectedAttributesStore: {
            proxy: {
                type: 'memory'
            },
            model: 'CMDBuildUI.model.importexports.GateAttribute',
            data: [CMDBuildUI.model.importexports.GateAttribute.create()],
            autoDestroy: true
        },
        newKeyAttributesStore: {
            fields: ['attribute'],
            proxy: {
                type: 'memory'
            },
            data: [
                {
                    attribute: ''
                }
            ],
            autoDestroy: true
        },
        freeKeyAttributesStore: {
            proxy: {
                type: 'memory'
            },
            data: '{freeKeyAttributes}',
            autoDestroy: true
        },
        mergeModesStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{mergeModes}',
            autoDestroy: true
        },
        keyAttributesStore: {
            proxy: {
                type: 'memory'
            },
            data: '{keyAttributesData}',
            autoDestroy: true
        },
        dateFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{dateFormatsData}',
            autoDestroy: true
        },
        timeFormats: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{timeFormatsData}',
            autoDestroy: true
        },
        decimalsSeparators: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{decimalsSeparatorsData}',
            autoDestroy: true
        }
    }
});
