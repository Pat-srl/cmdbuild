Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.SimpleGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-simplegrid',
    viewModel: {},

    width: '100%',
    layout: 'fit',

    viewConfig: {
        markDirty: false
    },

    sealedColumns: false,
    sortableColumns: true,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: true,
    menuDisabled: true,
    privates: {
        _headerCheckChange: function (mode) {
            var view = this.getView();
            var store = view.getStore();
            var sorters = store.getSorters();
            sorters.removeAll();
            store.each(function (record) {
                record.changeMode(mode);
            });
            sorters.add('_object_description');
        }
    },
    columns: [
        {
            flex: 9,
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: '_object_description',
            align: 'left',
            renderer: function (value, metaData, record, rowIndex, colIndex, store) {
                if (this.getView().up('grid').gridType === 'object') {
                    var groupsStore = Ext.getStore('groups.Groups');
                    return groupsStore.getById(record.get('role')).get('description');
                } else if (record.get('objectType'))
                    if (['etlgate', 'etltemplate'].indexOf(record.get('objectType')) > -1) {
                        var originalRecord =
                            Ext.getStore('importexports.Templates').findRecord('_id', record.get('objectTypeName')) ||
                            Ext.getStore('importexports.Gates').findRecord('_id', record.get('objectTypeName'));
                        if (originalRecord) {
                            return originalRecord.get('description_composed');
                        }
                    }
                if (!value.length) {
                    return record.get('objectTypeName');
                }
                return value;
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
            },
            dataIndex: 'modeTypeNone',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeNone}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.none);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.none);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.allow'
            },
            dataIndex: 'modeTypeAllow',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeAllow}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.read);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
            },
            dataIndex: 'modeTypeRead',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeRead}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.read);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.basic,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.basic'
            },
            dataIndex: 'modeTypeWFBasic',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeWFBasic}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow.basic);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantTypeWorkflow.basic);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts['default'],
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.default'
            },
            dataIndex: 'modeTypeWFDefault',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeWFDefault}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow['default']);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantTypeWorkflow['default']);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultread,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.defaultread'
            },
            dataIndex: 'modeTypeWFPlus',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeWFPlus}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow.plus);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantTypeWorkflow.plus);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
            },
            dataIndex: 'modeTypeWrite',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            sortable: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeWrite}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.write);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.write);
                }
            }
        },
        {
            xtype: 'actioncolumn',
            minWidth: 80,
            maxWidth: 80,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.filters,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.filters'
            },
            hideable: false,
            disabled: true,
            hidden: true,
            border: 0,
            align: 'center',
            bind: {
                hidden: '{hiddenColumns.actionFilter}'
            },
            listeners: {
                beforerender: function (column) {
                    column.items[0].tooltip =
                        CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters;
                    column.items[1].tooltip =
                        CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removefilters;
                }
            },
            items: [
                {
                    viewModel: {},
                    bind: {
                        disabled: '{!actions.edit}'
                    },
                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                        return CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.filters;
                    },
                    getClass: function (v, meta, row, rowIndex, colIndex, store) {
                        return CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid') + ' margin-right5';
                    },
                    handler: 'onActionFiltersClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-filter'
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (record.get('mode') === 'none') {
                            return true;
                        }
                        if (
                            view.up().getViewModel().get('actions.view') &&
                            !record.get('filter') &&
                            record.get('_attributePrivilegesEmpty')
                        ) {
                            return true;
                        }
                        return false;
                    }
                },
                {
                    viewModel: {},
                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                        return CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.removefilters;
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (record.get('mode') === 'none') {
                            return true;
                        }
                        if (
                            view.up().getViewModel().get('actions.view') ||
                            (!record.get('filter') && record.get('_attributePrivilegesEmpty'))
                        ) {
                            return true;
                        }
                        return false;
                    },
                    getClass: function (v, meta, row, rowIndex, colIndex, store) {
                        return 'cmdbuildicon-filter-remove margin-right5';
                    },
                    handler: 'onRemoveFilterActionClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-filter'
                    }
                }
            ]
        },
        {
            xtype: 'actioncolumn',
            minWidth: 80,
            maxWidth: 80,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.configurations,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.configurations'
            },
            hideable: false,
            disabled: true,
            hidden: true,
            border: 0,
            align: 'center',
            bind: {
                hidden: '{hiddenColumns.actionActionDisabled}'
            },
            listeners: {
                beforerender: function (column) {
                    column.items[0].tooltip =
                        CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.manageconfigurations;
                    column.items[1].tooltip =
                        CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips.clearconfigurations;
                }
            },
            items: [
                {
                    viewModel: {},
                    bind: {
                        disabled: '{!actions.edit}'
                    },
                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                        return CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips
                            .manageconfigurations;
                    },
                    getClass: function (v, meta, row, rowIndex, colIndex, store) {
                        return 'cmdbuildicon-list margin-right5';
                    },
                    handler: 'onManageConfigClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-list'
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                        if (record.get('mode') === 'none') {
                            return true;
                        }
                        return (
                            !view.up().getViewModel().get('actions.edit') &&
                            view.lookupController().getGrantConfigsAreDefault(record)
                        );
                    }
                },
                {
                    viewModel: {},
                    bind: {
                        disabled: '{!actions.edit}'
                    },
                    getTip: function (value, metadata, record, rowIndex, colIndex, store) {
                        return CMDBuildUI.locales.Locales.administration.groupandpermissions.tooltips
                            .clearconfigurations;
                    },
                    getClass: function (v, meta, row, rowIndex, colIndex, store) {
                        return 'cmdbuildicon-list-alt-remove margin-left5';
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                        if (record.get('mode') === 'none') {
                            return true;
                        }
                        if (view.up().getViewModel().get('actions.view')) {
                            return true;
                        }
                        return view.lookupController().getGrantConfigsAreDefault(record);
                    },
                    handler: 'onClearConfigClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-list'
                    }
                }
            ]
        },
        // columns for "other" tab
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.none'
            },
            dataIndex: 'modeTypeNoneOther',
            align: 'center',
            xtype: 'checkcolumn',
            injectCheckbox: false,
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeNoneOther}',
                disabled: '{!actions.edit}',
                headerCheckbox: '{actions.edit}'
            },
            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.none);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.none);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
            },
            dataIndex: 'modeTypeReadOther',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeReadOther}',
                headerCheckbox: '{actions.edit}',
                disabled: '{actions.view}'
            },

            listeners: {
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    view.up()._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.read);
                }
            }
        },
        {
            flex: 1,
            text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.write'
            },
            dataIndex: 'modeTypeWriteOther',
            align: 'center',
            xtype: 'checkcolumn',
            headerCheckbox: false,
            disabled: true,
            hideable: false,
            hidden: true,
            bind: {
                hidden: '{hiddenColumns.modeTypeWriteOther}',
                headerCheckbox: '{actions.edit}'
            },
            renderer: function (value, cell, record) {
                if (this.lookupViewModel().get('actions.view') || !record.get('modeTypeWriteAllow')) {
                    cell.tdCls = 'x-item-disabled';
                }

                if (Ext.isEmpty(value)) {
                    return Ext.String.format('<span class="x-grid-checkcolumn"></span>');
                }
                var klass = '';
                if (value) {
                    klass = 'x-grid-checkcolumn-checked';
                }
                return Ext.String.format('<span class="{0} x-grid-checkcolumn "></span>', klass);
            },
            listeners: {
                click: function (view, node, rowIdex, colIndex, event, record, row) {
                    if (record.get('modeTypeWriteAllow')) {
                        record.changeMode(CMDBuildUI.model.users.Grant.grantType.write);
                    }
                },
                headercheckchange: function (columnHeader, checked, e, eOpts) {
                    var view = this.getView();
                    var store = view.getStore();
                    var sorters = store.getSorters();
                    sorters.removeAll();
                    store.each(function (record) {
                        if (record.get('modeTypeWriteAllow')) {
                            record.changeMode(CMDBuildUI.model.users.Grant.grantType.write);
                        }
                    });
                    sorters.add('_object_description');
                },
                beforecheckchange: function (column, rowIndex, checked, record) {
                    if (!record.get('modeTypeWriteAllow')) {
                        return false;
                    }
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    record.changeMode(CMDBuildUI.model.users.Grant.grantType.write);
                }
            }
        }
    ],

    getActionContent: function (formMode, record, grid, rowIndex, fbar) {
        return {
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            xtype: 'form',
            actions: formMode,
            scrollable: 'y',
            reference: 'customPrivilegesChecks',
            bind: {
                actions: '{actions}',
                record: '{record}'
            },
            config: {
                selection: grid.getSelection(),
                record: grid.getSelection()
            },
            viewModel: {
                data: {
                    index: rowIndex
                }
            },
            items: [
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset',
                    viewModel: {
                        data: {
                            grant: record,
                            actions: formMode
                        }
                    }
                },
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
                    viewModel: {
                        data: {
                            grant: record,
                            actions: formMode
                        }
                    }
                },
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',
                    viewModel: {
                        data: {
                            grant: record,
                            actions: formMode
                        }
                    }
                },
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
                    viewModel: {
                        data: {
                            componentType: 'widget',
                            grant: record,
                            actions: formMode
                        }
                    }
                },
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-customcomponents-customcomponentsfieldset',
                    viewModel: {
                        data: {
                            componentType: 'contextmenu',
                            grant: record,
                            actions: formMode
                        }
                    }
                },
                {
                    xtype: 'administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',
                    viewModel: {
                        data: {
                            grant: record,
                            actions: formMode
                        }
                    }
                }
            ],

            fbar: fbar
        };
    }
});
