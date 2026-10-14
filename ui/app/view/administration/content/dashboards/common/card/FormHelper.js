Ext.define('CMDBuildUI.view.administration.content.dashboards.common.card.FormHelper', {
    singleton: true,

    /**
     * Get general properties fieldset
     *
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getGeneralProperties: function (mode, view, isHomepage) {
        var me = this;

        const dashboardItems = [
            me.getRow([me.getName(mode), me.getDescription(mode)]),
            me.getRow([
                Ext.apply(CMDBuildUI.util.administration.helper.FieldsHelper.getFaIconInput('theDashboard'), {
                    columnWidth: 0.5
                }),
                me.getActive(mode)
            ])
        ];

        const homepageItems = [me.getRow([me.getGroup(), me.getActive()])];

        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'column',
            collapsible: false,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: isHomepage ? homepageItems : dashboardItems
        };

        return fieldset;
    },

    /**
     * Get parameters fieldset.
     * Returns a fieldset containing the dashboard parameters grid and an action toolbar
     * that allows adding new global parameters when not in view mode.
     *
     * @param {String} mode - The form mode. Accepted values: `display`, `edit`, `both`.
     * @param {Ext.Component} view - The parent view component associated with this fieldset.
     * @return {Object} An ExtJS fieldset configuration object containing the parameters grid and toolbar.
     */
    getParameters: function (mode, view) {
        return {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            title: CMDBuildUI.locales.Locales.administration.dashboards.parameters.parameters,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.parameters.parameters'
            },
            layout: 'fit',
            collapsible: true,
            items: [
                {
                    xtype: 'panel',
                    dockedItems: [
                        {
                            xtype: 'components-administration-toolbars-formtoolbar',
                            items: {
                                xtype: 'button',
                                itemId: 'addParameterBtn',
                                ui: 'administration-primary-outline-small',
                                text: CMDBuildUI.locales.Locales.administration.common.actions.add,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.actions.add'
                                },
                                bind: {
                                    hidden: '{actions.view}'
                                }
                            }
                        }
                    ],
                    items: {
                        xtype: 'grid',
                        itemId: 'globalParametersGrid',
                        userCls: Ext.baseCSSPrefix + 'dashboard-parameters-grid',
                        bind: {
                            store: '{globalParametersStore}'
                        },
                        viewConfig: {
                            markDirty: false
                        },
                        emptyText: CMDBuildUI.locales.Locales.administration.common.strings.nodata,
                        columns: [
                            {
                                text: CMDBuildUI.locales.Locales.administration.dashboards.labeldescription,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.dashboards.labeldescription'
                                },
                                dataIndex: 'description',
                                flex: 1
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.common.labels.type,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.common.labels.type'
                                },
                                dataIndex: 'type',
                                flex: 1,
                                renderer: function (value) {
                                    const store = this.lookupViewModel().getStore('parameterTypesStore');
                                    const record = store.findRecord('value', value);
                                    return record ? record.get('label') : value;
                                }
                            },
                            {
                                text: CMDBuildUI.locales.Locales.administration.dashboards.fieldtype,
                                localized: {
                                    text: 'CMDBuildUI.locales.Locales.administration.dashboards.fieldtype'
                                },
                                dataIndex: 'fieldType',
                                flex: 1,
                                renderer: function (value) {
                                    const store = this.lookupViewModel().getStore('fieldsTypeStore');
                                    const record = store && store.findRecord('value', value);
                                    return record
                                        ? record.get('label')
                                        : value || CMDBuildUI.locales.Locales.administration.common.labels.default;
                                }
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'parameterAction',
                                bind: {
                                    hidden: '{actions.view}'
                                },
                                minWidth: 30,
                                maxWidth: 30,
                                menuDisabled: true,
                                hideable: false,
                                align: 'center',
                                items: [
                                    {
                                        cls: 'x-tool administration-tool',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('ellipsis-v', 'solid'),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.actions;
                                        },
                                        handler: 'onParameterActionClick'
                                    }
                                ]
                            },
                            {
                                xtype: 'actioncolumn',
                                itemId: 'parameterViewAction',
                                bind: {
                                    hidden: '{!actions.view}'
                                },
                                minWidth: 30,
                                maxWidth: 30,
                                menuDisabled: true,
                                hideable: false,
                                align: 'center',
                                items: [
                                    {
                                        cls: 'x-tool administration-tool',
                                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId(
                                            'external-link-alt',
                                            'solid'
                                        ),
                                        getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                                            return CMDBuildUI.locales.Locales.administration.common.actions.open;
                                        },
                                        handler: 'onParameterViewActionClick'
                                    }
                                ]
                            }
                        ]
                    }
                }
            ]
        };
    },

    /**
     * Get general properties fieldset
     *
     * @param {String} mode display|edit|both
     * @return {Ext.form.FieldSet} The url for api resourcess
     */
    getLayout: function (mode, view) {
        if (!mode) {
            mode = 'both';
        }
        var me = this;
        var vm = view.lookupViewModel();
        vm.set('componentsData', vm.getStore('componentsStore').getRange());
        var components = vm.getStore('componentsStore').getRange();

        Ext.Array.forEach(components, function (component) {
            component.set('descriptionWithName', component.get('label'));
        });

        var fieldset = {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            layout: 'fit',
            maxHeight: view.getHeight() - 100,
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.dashboards.layout,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.dashboards.layout'
            },

            items: [
                me.getRow([me.getLayoutGrid(view), me.getOrginComponentContainer(components)], {
                    layout: {
                        type: 'hbox',
                        align: 'top'
                    }
                })
            ]
        };
        var cont = {
            xtype: 'container',
            layout: 'fit',
            forceFit: true,
            items: [fieldset]
        };
        return cont;
    },

    privates: {
        getRow: function (items, config) {
            var fieldcontainer = Ext.merge(
                {},
                {
                    xtype: 'container',
                    layout: 'column',
                    cls: 'row-container',
                    columnWidth: 1
                },
                config || {}
            );
            if (items && items.length) {
                // fieldcontainer.columnWidth = items.length;
                fieldcontainer.items = items;
            }
            return fieldcontainer;
        },

        // General properties fieldset
        getDescription: function (mode) {
            // description
            var propertyName = 'description';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    userCls: 'with-tool',
                    allowBlank: false
                },
                allowBlank: false,
                bind: {
                    value: '{theDashboard.description}'
                }
            };

            config[propertyName].fieldcontainer.labelToolIconCls = CMDBuildUI.util.helper.IconHelper.getIconId(
                'flag',
                'solid'
            );
            config[propertyName].fieldcontainer.labelToolIconQtip =
                CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate;
            config[propertyName].fieldcontainer.labelToolIconClick = 'onTranslateClickDescription';

            return CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput(config);
        },

        getName: function (mode) {
            // content
            var propertyName = 'name';
            var config = {};
            config[propertyName] = {
                fieldcontainer: {
                    userCls: 'with-tool',
                    fieldLabel: CMDBuildUI.locales.Locales.administration.dashboards.extendeddescription
                },
                allowBlank: false,
                bind: {
                    value: '{theDashboard.name}'
                }
            };

            return CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                config,
                true,
                '[name="description"]'
            );
        },

        getActive: function (mode) {
            // active
            var config = {};
            var propertyName = 'active';
            config[propertyName] = {
                fieldcontainer: {
                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                    localized: {
                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                    },
                    bind: {
                        hidden: '{theDashboard.group === "_default"}'
                    }
                },
                bind: {
                    value: '{theDashboard.active}',
                    disabled: '{theDashboard.group === "_default" || actions.view}',
                    hidden: '{theDashboard.group === "_default"}'
                }
            };
            return CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput(config, propertyName, false);
        },

        getGroup: function () {
            return {
                xtype: 'fieldcontainer',
                layout: 'column',
                columnWidth: 0.5,
                allowBlank: false,
                items: [
                    {
                        xtype: 'combobox',
                        reference: 'comboGroup',
                        allowBlank: false,
                        columnWidth: 1,
                        queryMode: 'local',
                        displayField: 'label',
                        valueField: 'value',
                        forceSelection: true,
                        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group'
                        },
                        bind: {
                            store: '{groupsStore}',
                            value: '{theDashboard.group}',
                            hidden: '{!actions.add}',
                            disabled: '{!actions.add}'
                        }
                    },
                    {
                        cls: 'administration-inline-label-textfield',
                        columnWidth: 1,
                        xtype: 'displayfield',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group, // Group
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.group'
                        },
                        name: 'group',
                        bind: {
                            value: '{theDashboard.group}',
                            hidden: '{actions.add}'
                        },
                        renderer: function (value) {
                            if (!value) {
                                return '';
                            }
                            if (value === '_default') {
                                return CMDBuildUI.locales.Locales.administration.common.strings['default']; // '*Default*';
                            }
                            const groupsStore = Ext.getStore('groups.Groups');
                            const group = groupsStore.findRecord('name', value);
                            if (group) {
                                return group.get('description');
                            }
                            return value;
                        }
                    }
                ]
            };
        },

        getLayoutGrid: function (view) {
            return {
                xtype: 'container',
                itemId: 'layoutcontainer',
                flex: 1,
                scrollable: 'y'
            };
        },

        getOrginComponentContainer: function (data) {
            return {
                flex: 0.25,
                xtype: 'administration-content-dashboards-card-builder-column',
                style: {
                    height: 40 * data.length
                },
                bind: {
                    hidden: '{actions.view}'
                },
                isAllFreeItems: true,
                store: Ext.create('Ext.data.Store', {
                    fields: ['label'],
                    proxy: {
                        type: 'memory'
                    },
                    data: data,
                    sorters: ['label'],
                    autoDestroy: true,
                    autoLoad: false
                })
            };
        }
    }
});
