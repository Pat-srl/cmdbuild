Ext.define('CMDBuildUI.view.administration.content.setup.elements.GovernanceData', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.GovernanceDataController',
        'CMDBuildUI.view.administration.content.setup.elements.GovernanceDataModel'
    ],

    alias: 'widget.administration-content-setup-elements-governancedata',
    controller: 'administration-content-setup-elements-governancedata',
    viewModel: {
        type: 'administration-content-setup-elements-governancedata'
    },

    layout: 'fit',
    scrollable: 'y',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    items: [
        {
            xtype: 'treepanel',
            ui: 'administration-navigation-tree',
            reserveScrollbar: true,
            rootVisible: false,
            selModel: {
                mode: 'MULTI'
            },
            viewConfig: {
                markDirty: false
            },
            bind: {
                store: '{treeStore}'
            },
            columns: [
                {
                    xtype: 'treecolumn',
                    hideable: false,
                    flex: 0.75,
                    dataIndex: 'name',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.entities,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.entities'
                    }
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'list',
                    text: CMDBuildUI.locales.Locales.administration.systemconfig.list,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.systemconfig.list'
                    },
                    headerCheckbox: false,
                    hideable: false,
                    sortable: false,
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',
                        headerCheckbox: '{actions.edit}'
                    },
                    renderer: function (value, cell, record) {
                        if (record.get('isPrincipalFolder')) {
                            return;
                        }

                        return this.defaultRenderer(value, cell);
                    }
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'read',
                    text: CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.texts.read'
                    },
                    headerCheckbox: false,
                    hideable: false,
                    sortable: false,
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',
                        headerCheckbox: '{actions.edit}'
                    },
                    renderer: function (value, cell, record) {
                        if (record.get('isPrincipalFolder') || record.get('prototype')) {
                            return;
                        }

                        return this.defaultRenderer(value, cell);
                    }
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'create',
                    text: CMDBuildUI.locales.Locales.administration.common.actions.create,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.create'
                    },
                    headerCheckbox: false,
                    hideable: false,
                    sortable: false,
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',
                        headerCheckbox: '{actions.edit}'
                    },
                    renderer: function (value, cell, record) {
                        if (record.get('isPrincipalFolder') || record.get('prototype')) {
                            return;
                        }

                        return this.defaultRenderer(value, cell);
                    }
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'update',
                    text: CMDBuildUI.locales.Locales.administration.common.actions.update,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.update'
                    },
                    headerCheckbox: false,
                    hideable: false,
                    sortable: false,
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',
                        headerCheckbox: '{actions.edit}'
                    },
                    renderer: function (value, cell, record) {
                        if (record.get('isPrincipalFolder') || record.get('prototype')) {
                            return;
                        }

                        return this.defaultRenderer(value, cell);
                    }
                },
                {
                    xtype: 'checkcolumn',
                    dataIndex: 'delete',
                    text: CMDBuildUI.locales.Locales.administration.common.actions.delete,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.common.actions.delete'
                    },
                    headerCheckbox: false,
                    hideable: false,
                    sortable: false,
                    disabled: true,
                    bind: {
                        disabled: '{actions.view}',
                        headerCheckbox: '{actions.edit}'
                    },
                    renderer: function (value, cell, record) {
                        if (record.get('isPrincipalFolder') || record.get('prototype')) {
                            return;
                        }

                        return this.defaultRenderer(value, cell);
                    }
                }
            ]
        }
    ],

    dockedItems: [
        {
            dock: 'top',
            xtype: 'components-administration-toolbars-formtoolbar',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: {
                        disabled: true,
                        bind: {
                            disabled: '{!theSession.rolePrivileges.admin_sysconfig_modify}'
                        }
                    }
                },
                'governancedata',
                'theSetup',
                [],
                [
                    {
                        xtype: 'checkbox',
                        itemId: 'showOnlySelected',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.showonlyselected,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.showonlyselected'
                        },
                        labelAlign: 'left',
                        labelStyle: 'width:auto',
                        labelWidth: false
                    }
                ]
            )
        },
        {
            xtype: 'toolbar',
            itemId: 'bottomtoolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(false)
        }
    ]
});
