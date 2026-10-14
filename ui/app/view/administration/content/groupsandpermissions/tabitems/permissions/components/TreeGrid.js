Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.TreeGrid', {
    extend: 'Ext.tree.Panel',
    alias: 'widget.administration-content-groupsandpermissions-tabitems-permissions-components-treegrid',
    viewModel: {},

    width: '100%',
    layout: 'fit',

    viewConfig: {
        markDirty: false
    },
    ui: 'administration-navigation-tree',
    sealedColumns: false,
    sortableColumns: true,
    enableColumnHide: false,
    enableColumnMove: false,
    enableColumnResize: true,
    menuDisabled: true,
    rootVisible: false,

    changeMode: function (record, mode) {
        var grantStore = this.lookupViewModel().get('grantsChainedStore'),
            grant = grantStore.findRecord('objectTypeName', record.get('objectTypeName'));
        if (grant) {
            record.changeMode(mode);
            grant.changeMode(mode);
        }
    },
    _headerCheckChange: function (mode) {
        var me = this;
        var view = me.getView();
        var store = view.getStore();
        var sorters = store.getSorters();
        sorters.removeAll();
        store.suspendEvent('change');
        store.each(function (record) {
            me.changeMode(record, mode);
        });
        sorters.add('_object_description');
        store.resumeEvent('change');
    },
    columns: [
        {
            xtype: 'treecolumn',
            flex: 9,
            text: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                text: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            dataIndex: '_object_description',
            align: 'left'
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
                    this.getView().up('treepanel')._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.none);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView().up('treepanel').changeMode(record, CMDBuildUI.model.users.Grant.grantType.none);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView().up('treepanel')._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView().up('treepanel').changeMode(record, CMDBuildUI.model.users.Grant.grantType.read);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView().up('treepanel')._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.read);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView().up('treepanel').changeMode(record, CMDBuildUI.model.users.Grant.grantType.read);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView()
                        .up('treepanel')
                        ._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow.basic);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView()
                        .up('treepanel')
                        .changeMode(record, CMDBuildUI.model.users.Grant.grantTypeWorkflow.basic);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView()
                        .up('treepanel')
                        ._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow['default']);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView()
                        .up('treepanel')
                        .changeMode(record, CMDBuildUI.model.users.Grant.grantTypeWorkflow['default']);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView()
                        .up('treepanel')
                        ._headerCheckChange(CMDBuildUI.model.users.Grant.grantTypeWorkflow.plus);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView()
                        .up('treepanel')
                        .changeMode(record, CMDBuildUI.model.users.Grant.grantTypeWorkflow.plus);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                    this.getView().up('treepanel')._headerCheckChange(CMDBuildUI.model.users.Grant.grantType.write);
                },
                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                    this.getView().up('treepanel').changeMode(record, CMDBuildUI.model.users.Grant.grantType.write);
                }
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                if (record.get('nodetype') === 'folder') {
                    return '';
                }
                return this.defaultRenderer(value, metaData);
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
                        if (meta.record.get('nodetype') === 'folder') {
                            return '';
                        }
                        return CMDBuildUI.util.helper.IconHelper.getIconId('filter', 'solid') + ' margin-right5';
                    },
                    handler: 'onActionFiltersClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-filter'
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        if (record.get('mode') === 'none' || record.get('nodetype') === 'folder') {
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
                        if (record.get('mode') === 'none' || record.get('nodetype') === 'folder') {
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
                        if (meta.record.get('nodetype') === 'folder') {
                            return '';
                        }
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
                        if (meta.record.get('nodetype') === 'folder') {
                            return '';
                        }
                        return 'cmdbuildicon-list margin-right5';
                    },
                    handler: 'onManageConfigClick',
                    autoEl: {
                        'data-testid': 'administration-permissions-grid-row-list'
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                        if (record.get('mode') === 'none' || record.get('nodetype') === 'folder') {
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
                        if (meta.record.get('nodetype') === 'folder') {
                            return '';
                        }
                        return 'cmdbuildicon-list-alt-remove margin-left5';
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, button, record) {
                        if (record.get('mode') === 'none' || record.get('nodetype') === 'folder') {
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
