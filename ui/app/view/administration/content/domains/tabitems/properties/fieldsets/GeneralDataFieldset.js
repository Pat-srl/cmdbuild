Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetController',
        'CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldsetModel'
    ],

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',
    controller: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset',
    viewModel: {
        type: 'administration-content-domains-tabitems-properties-fieldsets-generaldatafieldset'
    },

    ui: 'administration-formpagination',
    statics: {
        onHideInlineOptions: function (view) {
            const vm = view.lookupViewModel();
            vm.set('theDomain.isMasterDetail', false);
            vm.set('theDomain.descriptionMasterDetail', '');
            vm.set('theDomain.sourceInline', false);
            vm.set('theDomain.sourceDefaultClosed', false);
            vm.set('theDomain.destinationInline', false);
            vm.set('theDomain.destinationDefaultClosed', false);
        }
    },

    items: [
        {
            xtype: 'fieldset',
            title: CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.titles.generalattributes'
            },
            layout: 'anchor',
            itemId: 'domain-generaldatafieldset',
            ui: 'administration-formpagination',
            items: [
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            /********************* Name **********************/
                            items: [
                                {
                                    // create / edit
                                    xtype: 'textfield',
                                    vtype: 'alphanum',
                                    itemId: 'domainname',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                    },
                                    name: 'name',
                                    enforceMaxLength: true,
                                    hidden: true,
                                    allowBlank: false,
                                    bind: {
                                        value: '{theDomain.name}',
                                        hidden: '{!actions.add}'
                                    },
                                    listeners: {
                                        change: function (input, newVal, oldVal) {
                                            CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                                input,
                                                newVal,
                                                oldVal,
                                                '[name="description"]'
                                            );
                                        }
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                    },
                                    name: 'name',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.name}',
                                        hidden: '{!actions.view}'
                                    }
                                },
                                {
                                    // edit
                                    xtype: 'textfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
                                    },
                                    name: 'name',
                                    hidden: true,
                                    disabled: true,
                                    allowBlank: false,
                                    bind: {
                                        value: '{theDomain.name}',
                                        hidden: '{!actions.edit}'
                                    }
                                }
                            ]
                        },
                        {
                            // empty space
                            flex: 0.3
                        },
                        {
                            flex: 1,
                            /********************* description **********************/
                            items: [
                                {
                                    flex: 1,
                                    padding: 0,
                                    xtype: 'fieldcontainer',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.common.labels.description',
                                        labelToolIconQtip:
                                            'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                                    },
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateClickDescription',
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            name: 'description',
                                            bind: {
                                                value: '{theDomain.description}',
                                                hidden: '{actions.view}'
                                            },
                                            allowBlank: false
                                        },
                                        {
                                            xtype: 'displayfield',
                                            hidden: true,
                                            bind: {
                                                value: '{theDomain.description}',
                                                hidden: '{!actions.view}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            /********************* Source **********************/
                            items: [
                                {
                                    // create
                                    xtype: 'combobox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source'
                                    },
                                    valueField: '_id',
                                    displayField: 'label',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    typeAhead: true,
                                    allowBlank: false,
                                    name: 'source',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.source}',
                                        hidden: '{actions.view}',
                                        disabled: '{actions.edit}',
                                        store: '{sourceClassStore}'
                                    },
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    },
                                    tpl: new Ext.XTemplate(
                                        '<tpl for=".">',
                                        '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                                        '<div class="x-boundlist-item">{label}</div>',
                                        '</tpl>',
                                        {
                                            shouldShowHeader: function (group) {
                                                return this.currentGroup !== group;
                                            },
                                            showHeader: function (group) {
                                                this.currentGroup = group;
                                                return group;
                                            }
                                        }
                                    ),
                                    listeners: {
                                        change: 'onSourceChange'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfieldwithtriggers',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.source'
                                    },
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.source}',
                                        hidden: '{!actions.view}',
                                        hideTrigger: Ext.String.format(
                                            '{!theDomain.source || theDomain.source == "{0}" || theDomain.source == "{1}"}',
                                            CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                                            CMDBuildUI.util.helper.ModelHelper.systemClasses.process
                                        )
                                    },
                                    triggers: {
                                        open: {
                                            cls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'external-link-alt',
                                                'solid'
                                            ),
                                            handler: function (f, trigger, eOpts) {
                                                let url;
                                                const targetType =
                                                    CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
                                                        f.getValue()
                                                    );
                                                const target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                    f.getValue()
                                                );
                                                switch (targetType) {
                                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                                        url =
                                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(
                                                                target.get('name')
                                                            );
                                                        break;
                                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                                        url =
                                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(
                                                                target.get('name')
                                                            );
                                                        break;
                                                    default:
                                                        return;
                                                }
                                                CMDBuildUI.util.Utilities.closeAllPopups();
                                                CMDBuildUI.util.Utilities.redirectTo(url);
                                            }
                                        }
                                    },
                                    renderer: function (value, input) {
                                        if (value) {
                                            const vm = input.lookupViewModel();
                                            const storeId = vm.get('theDomain.sourceProcess')
                                                ? 'processes.Processes'
                                                : 'classes.Classes';
                                            const record = Ext.getStore(storeId).getById(vm.get('theDomain.source'));
                                            return record && record.get('description');
                                        }
                                    }
                                }
                            ]
                        },
                        {
                            flex: 0.3,
                            padding: '0 10',
                            /********************* cardinality **********************/
                            items: [
                                {
                                    // create
                                    xtype: 'combobox',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    displayField: 'label',
                                    valueField: 'value',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                                    },
                                    name: 'cardinality',
                                    allowBlank: false,
                                    hidden: true,
                                    bind: {
                                        store: '{cardinalityStore}',
                                        value: '{theDomain.cardinality}',
                                        hidden: '{actions.view}',
                                        disabled: '{actions.edit}'
                                    },
                                    listeners: {
                                        change: 'resetSummaryGrid'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfield',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cardinality'
                                    },
                                    name: 'cardinality',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.cardinality}',
                                        hidden: '{!actions.view}'
                                    }
                                }
                            ]
                        },
                        {
                            flex: 1,
                            /********************* destination **********************/
                            items: [
                                {
                                    xtype: 'combobox',
                                    valueField: '_id',
                                    displayField: 'label',
                                    queryMode: 'local',
                                    forceSelection: true,
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                                    },
                                    typeAhead: true,
                                    allowBlank: false,
                                    name: 'destination',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.destination}',
                                        hidden: '{actions.view}',
                                        disabled: '{actions.edit}',
                                        store: '{destinationClassStore}'
                                    },
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    },
                                    tpl: new Ext.XTemplate(
                                        '<tpl for=".">',
                                        '<tpl for="group" if="this.shouldShowHeader(group)"><div class="group-header">{[this.showHeader(values.group)]}</div></tpl>',
                                        '<div class="x-boundlist-item">{label}</div>',
                                        '</tpl>',
                                        {
                                            shouldShowHeader: function (group) {
                                                return this.currentGroup !== group;
                                            },
                                            showHeader: function (group) {
                                                this.currentGroup = group;
                                                return group;
                                            }
                                        }
                                    ),
                                    listeners: {
                                        change: 'onDestinationChange'
                                    }
                                },
                                {
                                    // view
                                    xtype: 'displayfieldwithtriggers',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destination'
                                    },
                                    name: 'destination',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.destination}',
                                        hidden: '{!actions.view}',
                                        hideTrigger: Ext.String.format(
                                            '{!theDomain.destination || theDomain.destination == "{0}" || theDomain.destination == "{1}"}',
                                            CMDBuildUI.util.helper.ModelHelper.systemClasses.class,
                                            CMDBuildUI.util.helper.ModelHelper.systemClasses.process
                                        )
                                    },
                                    triggers: {
                                        open: {
                                            cls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                                'external-link-alt',
                                                'solid'
                                            ),
                                            handler: function (f, trigger, eOpts) {
                                                let url;
                                                const targetType =
                                                    CMDBuildUI.util.helper.ModelHelper.getObjectTypeByName(
                                                        f.getValue()
                                                    );
                                                const target = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(
                                                    f.getValue()
                                                );
                                                switch (targetType) {
                                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.klass:
                                                        url =
                                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getClassUrl(
                                                                target.get('name')
                                                            );
                                                        break;
                                                    case CMDBuildUI.util.helper.ModelHelper.objecttypes.process:
                                                        url =
                                                            CMDBuildUI.util.administration.helper.ApiHelper.client.getProcessUrl(
                                                                target.get('name')
                                                            );
                                                        break;
                                                    default:
                                                        return;
                                                }
                                                CMDBuildUI.util.Utilities.closeAllPopups();
                                                CMDBuildUI.util.Utilities.redirectTo(url);
                                            }
                                        }
                                    },
                                    renderer: function (value, input) {
                                        if (value) {
                                            const vm = input.lookupViewModel();
                                            const storeId = vm.get('theDomain.destinationProcess')
                                                ? 'processes.Processes'
                                                : 'classes.Classes';
                                            const record = Ext.getStore(storeId).getById(
                                                vm.get('theDomain.destination')
                                            );
                                            return record && record.get('description');
                                        }
                                    }
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            /********************* direct description **********************/
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    padding: 0,
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptiondirect,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptiondirect',
                                        labelToolIconQtip:
                                            'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                                    },
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateClickDirect',
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            name: 'descriptionDirect',
                                            allowBlank: false,
                                            hidden: true,
                                            bind: {
                                                value: '{theDomain.descriptionDirect}',
                                                hidden: '{actions.view}'
                                            }
                                        },
                                        {
                                            xtype: 'displayfield',
                                            hidden: true,
                                            bind: {
                                                value: '{theDomain.descriptionDirect}',
                                                hidden: '{!actions.view}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            // empty space
                            flex: 0.3,
                            padding: '0 10'
                        },
                        {
                            flex: 1,
                            /********************* inverse description **********************/
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    padding: 0,
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.domains.fieldlabels
                                            .descriptioninverse,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.descriptioninverse',
                                        labelToolIconQtip:
                                            'CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate'
                                    },
                                    labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                    labelToolIconQtip:
                                        CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                                    labelToolIconClick: 'onTranslateClickInverse',
                                    items: [
                                        {
                                            xtype: 'textfield',
                                            name: 'descriptionInverse',
                                            allowBlank: false,
                                            hidden: true,
                                            bind: {
                                                value: '{theDomain.descriptionInverse}',
                                                hidden: '{actions.view}'
                                            }
                                        },
                                        {
                                            xtype: 'displayfield',
                                            hidden: true,
                                            bind: {
                                                value: '{theDomain.descriptionInverse}',
                                                hidden: '{!actions.view}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    flex: 1,
                    items: [
                        {
                            layout: 'hbox',
                            flex: 1,
                            /********************* origin card on delete ***************************/
                            items: [
                                {
                                    // placeholder element
                                    xtype: 'fieldcontainer',
                                    hidden: true,
                                    bind: {
                                        hidden: '{!theDomain.sourceProcess}'
                                    },
                                    items: [
                                        {
                                            xtype: 'displayfield'
                                        }
                                    ]
                                },
                                {
                                    hidden: true,
                                    bind: {
                                        hidden: '{theDomain.sourceProcess}'
                                    },
                                    flex: 0.65,
                                    layout: 'hbox',
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                                            'cascadeActionDirect',
                                            {
                                                cascadeActionDirect: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.cascadeactiondirect,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cascadeactiondirect'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        bind: {
                                                            disabled: '{!theDomain.source || theDomain.sourceProcess}'
                                                        }
                                                    },
                                                    displayfield: {
                                                        bind: {
                                                            value: '{theDomain._cascadeActionDirect_description}'
                                                        },
                                                        listeners: {
                                                            afterrender: function () {
                                                                const vm = this.lookupViewModel();

                                                                vm.bind(
                                                                    {
                                                                        bindTo: {
                                                                            value: '{theDomain.cascadeActionDirect}',
                                                                            store: '{cascadeActionsDirectStore}'
                                                                        },
                                                                        single: true
                                                                    },
                                                                    function (data) {
                                                                        if (data.store && !vm.isDestroyed) {
                                                                            const record = data.store.findRecord(
                                                                                'value',
                                                                                data.value
                                                                            );
                                                                            if (record) {
                                                                                vm.set(
                                                                                    'theDomain._cascadeActionDirect_description',
                                                                                    record.get('label')
                                                                                );
                                                                            }
                                                                        }
                                                                    }
                                                                );
                                                            }
                                                        }
                                                    },
                                                    combofield: {
                                                        disabled: true,
                                                        bind: {
                                                            store: '{cascadeActionsDirectStore}',
                                                            value: '{theDomain.cascadeActionDirect}',
                                                            disabled: '{!theDomain.source || theDomain.sourceProcess}'
                                                        },
                                                        listeners: {
                                                            disable: function (input, eOpts) {
                                                                const vm = input.lookupViewModel();
                                                                vm.set('theDomain.cascadeActionDirect', null);
                                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                                    input,
                                                                    true,
                                                                    input.up('form')
                                                                );
                                                            },
                                                            enable: function (input, eOpts) {
                                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                                    input,
                                                                    false,
                                                                    input.up('form')
                                                                );
                                                            },
                                                            change: function (combo, newValue, oldValue) {
                                                                const vm = combo.lookupViewModel();
                                                                if (
                                                                    newValue ===
                                                                    CMDBuildUI.model.domains.Domain.cascadeAction
                                                                        .restrict
                                                                ) {
                                                                    vm.set(
                                                                        'theDomain.cascadeActionDirect_askConfirm',
                                                                        false
                                                                    );
                                                                }
                                                            }
                                                        }
                                                    },
                                                    triggers: {
                                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                },
                                {
                                    hidden: true,
                                    bind: {
                                        hidden: '{theDomain.sourceProcess}'
                                    },
                                    flex: 0.4,
                                    layout: 'hbox',
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'cascadeActionDirect_askConfirm',
                                            {
                                                cascadeActionDirect_askConfirm: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains.texts
                                                                .askconfirm,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        bind: { disabled: '{cascadeActionDirect_askConfirm_disabled}' }
                                                    },
                                                    disabledCls: 'x-item-disabled-forced',
                                                    disabled: true,
                                                    bind: {
                                                        value: '{theDomain.cascadeActionDirect_askConfirm}',
                                                        disabled:
                                                            '{!theDomain.source || theDomain.sourceProcess || actions.view  ||  cascadeActionDirect_askConfirm_disabled}'
                                                    },
                                                    listeners: {
                                                        disable: function (input, eOpts) {
                                                            input.setValue(false);
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'hbox',
                            flex: 1,
                            /********************* destination card on delete **********************/
                            items: [
                                {
                                    hidden: true,
                                    bind: {
                                        hidden: '{theDomain.destinationProcess}'
                                    },
                                    flex: 0.65,
                                    layout: 'hbox',
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                                            'cascadeActionInverse',
                                            {
                                                cascadeActionInverse: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.cascadeactioninverse,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.cascadeactioninverse'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        bind: {
                                                            disabled:
                                                                '{!theDomain.destination || theDomain.destinationProcess}'
                                                        }
                                                    },
                                                    displayfield: {
                                                        bind: {
                                                            value: '{theDomain._cascadeActionInverse_description}'
                                                        },
                                                        listeners: {
                                                            afterrender: function (view) {
                                                                const me = this;
                                                                const vm = me.lookupViewModel();

                                                                vm.bind(
                                                                    {
                                                                        bindTo: {
                                                                            value: '{theDomain.cascadeActionInverse}',
                                                                            store: '{cascadeActionsInverseStore}'
                                                                        },
                                                                        single: true
                                                                    },
                                                                    function (data) {
                                                                        if (data.store && !vm.isDestroyed) {
                                                                            const record = data.store.findRecord(
                                                                                'value',
                                                                                data.value
                                                                            );
                                                                            if (record) {
                                                                                vm.set(
                                                                                    'theDomain._cascadeActionInverse_description',
                                                                                    record.get('label')
                                                                                );
                                                                            }
                                                                        }
                                                                    }
                                                                );
                                                            }
                                                        }
                                                    },
                                                    combofield: {
                                                        disabled: true,
                                                        bind: {
                                                            store: '{cascadeActionsInverseStore}',
                                                            value: '{theDomain.cascadeActionInverse}',
                                                            disabled:
                                                                '{!theDomain.destination || theDomain.destinationProcess}'
                                                        },
                                                        listeners: {
                                                            disable: function (input, eOpts) {
                                                                const vm = input.lookupViewModel();
                                                                vm.set('theDomain.cascadeActionInverse', null);
                                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                                    input,
                                                                    true,
                                                                    input.up('form')
                                                                );
                                                            },
                                                            enable: function (input, eOpts) {
                                                                CMDBuildUI.util.administration.helper.FieldsHelper.setAllowBlank(
                                                                    input,
                                                                    false,
                                                                    input.up('form')
                                                                );
                                                            },
                                                            change: function (combo, newValue, oldValue) {
                                                                const vm = combo.lookupViewModel();
                                                                if (
                                                                    newValue ===
                                                                    CMDBuildUI.model.domains.Domain.cascadeAction
                                                                        .restrict
                                                                ) {
                                                                    vm.set(
                                                                        'theDomain.cascadeActionInverse_askConfirm',
                                                                        false
                                                                    );
                                                                }
                                                            }
                                                        }
                                                    },
                                                    triggers: {
                                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                },
                                {
                                    hidden: true,
                                    bind: { hidden: '{theDomain.destinationProcess}' },
                                    flex: 0.4,
                                    layout: 'hbox',
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'cascadeActionInverse_askConfirm',
                                            {
                                                cascadeActionInverse_askConfirm: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains.texts
                                                                .askconfirm,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.texts.askconfirm'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        bind: { disabled: '{cascadeActionInverse_askConfirm_disabled}' }
                                                    },
                                                    disabledCls: 'x-item-disabled-forced',
                                                    disabled: true,
                                                    bind: {
                                                        disabled:
                                                            '{!theDomain.destination || theDomain.destinationProcess || actions.view  || cascadeActionInverse_askConfirm_disabled}',
                                                        value: '{theDomain.cascadeActionInverse_askConfirm}'
                                                    },
                                                    listeners: {
                                                        disable: function (input, eOpts) {
                                                            input.setValue(false);
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            /********************* Source editable **********************/
                            items: {
                                // create / edit / view
                                xtype: 'checkbox',
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourceeditable,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourceeditable'
                                },
                                name: 'sourceEditable',
                                hidden: true,
                                bind: {
                                    value: '{theDomain.sourceEditable}',
                                    readOnly: '{actions.view}',
                                    hidden: '{!theDomain}'
                                }
                            }
                        },
                        {
                            flex: 1,
                            /********************* Target editable **********************/
                            items: {
                                // create / edit / view
                                xtype: 'checkbox',
                                fieldLabel:
                                    CMDBuildUI.locales.Locales.administration.domains.fieldlabels.targeteditable,
                                localized: {
                                    fieldLabel:
                                        'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.targeteditable'
                                },
                                name: 'targetEditable',
                                hidden: true,
                                bind: {
                                    value: '{theDomain.targetEditable}',
                                    readOnly: '{actions.view}',
                                    hidden: '{!theDomain}'
                                }
                            }
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    flex: 1,
                    hidden: true,
                    bind: {
                        hidden: '{ theDomain.cardinality === "1:1"}'
                    },
                    listeners: {
                        hide: function (view) {
                            CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.GeneralDataFieldset.onHideInlineOptions(
                                view
                            );
                        }
                    },
                    items: [
                        {
                            layout: 'hbox',
                            flex: 1,
                            minHeight: '1',
                            items: [
                                {
                                    flex: 1,
                                    /********************* Inline origin **********************/
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'sourceInline',
                                            {
                                                sourceInline: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.sourceinline,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourceinline'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        disabled: true,
                                                        bind: {
                                                            disabled:
                                                                '{!theDomain.source || theDomain.sourceProcess|| !theDomain.cardinality || theDomain.cardinality === "N:1"}'
                                                        },
                                                        listeners: {
                                                            disable: function (fieldcontainer, eOpts) {
                                                                fieldcontainer
                                                                    .lookupViewModel()
                                                                    .set('theDomain.sourceInline', false);
                                                            }
                                                        }
                                                    },
                                                    bind: {
                                                        value: '{theDomain.sourceInline}',
                                                        readOnly: '{actions.view}',
                                                        disabled:
                                                            '{!theDomain.source || theDomain.sourceProcess|| !theDomain.cardinality || theDomain.cardinality === "N:1"}'
                                                    },
                                                    listeners: {
                                                        change: function (checkbox, newValue, oldValue) {
                                                            if (!newValue) {
                                                                checkbox
                                                                    .lookupViewModel()
                                                                    .set('theDomain.sourceDefaultClosed', false);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                },
                                {
                                    flex: 1,
                                    /********************* Default inline closed **********************/
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'sourceDefaultClosed',
                                            {
                                                sourceDefaultClosed: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.sourcedefaultclosed,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourcedefaultclosed'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        disabled: true,
                                                        bind: {
                                                            disabled:
                                                                '{!theDomain.source || theDomain.sourceProcess || !theDomain.cardinality || theDomain.cardinality === "N:1" || !theDomain.sourceInline}'
                                                        },
                                                        listeners: {
                                                            disable: function (fieldcontainer, eOpts) {
                                                                fieldcontainer
                                                                    .lookupViewModel()
                                                                    .set('theDomain.sourceDefaultClosed', false);
                                                            }
                                                        }
                                                    },
                                                    bind: {
                                                        value: '{theDomain.sourceDefaultClosed}',
                                                        readOnly: '{actions.view}',
                                                        disabled:
                                                            '{!theDomain.source || theDomain.sourceProcess || !theDomain.cardinality || theDomain.cardinality === "N:1" || !theDomain.sourceInline}'
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                }
                            ]
                        },
                        {
                            layout: 'hbox',
                            flex: 1,
                            items: [
                                {
                                    layout: 'hbox',
                                    flex: 0.5,
                                    /********************* Inline destination **********************/
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'destinationInline',
                                            {
                                                destinationInline: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.destinationinline,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationinline'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        disabled: true,
                                                        bind: {
                                                            disabled:
                                                                '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N"}'
                                                        },
                                                        listeners: {
                                                            disable: function (fieldcontainer, eOpts) {
                                                                fieldcontainer
                                                                    .lookupViewModel()
                                                                    .set('theDomain.destinationInline', false);
                                                            }
                                                        }
                                                    },
                                                    bind: {
                                                        value: '{theDomain.destinationInline}',
                                                        readOnly: '{actions.view}',
                                                        disabled:
                                                            '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N"}'
                                                    },
                                                    listeners: {
                                                        change: function (checkbox, newValue, oldValue) {
                                                            if (!newValue) {
                                                                checkbox
                                                                    .lookupViewModel()
                                                                    .set('theDomain.destinationDefaultClosed', false);
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                },
                                {
                                    flex: 0.5,
                                    /********************* Default inline closed **********************/
                                    items: [
                                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonCheckboxInput(
                                            'destinationDefaultClosed',
                                            {
                                                destinationDefaultClosed: {
                                                    flex: 1,
                                                    fieldcontainer: {
                                                        fieldLabel:
                                                            CMDBuildUI.locales.Locales.administration.domains
                                                                .fieldlabels.destinationdefaultclosed,
                                                        localized: {
                                                            fieldLabel:
                                                                'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.destinationdefaultclosed'
                                                        },
                                                        disabledCls: 'x-item-disabled-forced',
                                                        disabled: true,
                                                        bind: {
                                                            disabled:
                                                                '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N" || !theDomain.destinationInline}'
                                                        },
                                                        listeners: {
                                                            disable: function (fieldcontainer, eOpts) {
                                                                fieldcontainer
                                                                    .lookupViewModel()
                                                                    .set('theDomain.destinationDefaultClosed', false);
                                                            }
                                                        }
                                                    },
                                                    bind: {
                                                        value: '{theDomain.destinationDefaultClosed}',
                                                        readOnly: '{actions.view}',
                                                        disabled:
                                                            '{!theDomain.destination || theDomain.destinationProcess || !theDomain.cardinality || theDomain.cardinality === "1:N" || !theDomain.destinationInline}'
                                                    }
                                                }
                                            }
                                        )
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    layout: 'hbox',
                    items: [
                        {
                            flex: 1,
                            /********************* Active **********************/
                            items: [
                                {
                                    // create / edit / view
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'active',
                                    hidden: true,
                                    bind: {
                                        value: '{theDomain.active}',
                                        readOnly: '{actions.view}',
                                        hidden: '{!theDomain}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});
