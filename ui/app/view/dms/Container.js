Ext.define('CMDBuildUI.view.dms.Container', {
    extend: 'Ext.panel.Panel',

    requires: ['CMDBuildUI.view.dms.ContainerController', 'CMDBuildUI.view.dms.ContainerModel'],

    alias: 'widget.dms-container',
    controller: 'dms-container',
    viewModel: {
        type: 'dms-container'
    },

    config: {
        /**
         * @cfg {Boolean} readOnly
         * Set to `true` to show attachment tab in read-only mode
         */
        readOnly: false,

        /**
         * @cfg {Boolean} ignoreSchedules
         * Ignore schedules generation for date field with 'schedules rule definition' associated.
         */
        ignoreSchedules: false,

        /**
         * @cfg {Boolean} isAsyncSave
         * Set to `true` if saving is managed by other components
         */
        isAsyncSave: false
    },

    layout: {
        type: 'vbox',
        align: 'stretch' //stretch vertically to parent
    },

    scrollable: true,

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.attachments.add,
            localized: {
                text: 'CMDBuildUI.locales.Locales.attachments.add'
            },
            ui: 'management-primary-small',
            itemId: 'attachmentsButton',
            hidden: true,
            disabled: true,
            bind: {
                hidden: '{readOnly}'
            },
            handler: 'onAttachmentsButtonClick',
            autoEl: {
                'data-testid': 'dms-container-addbtn'
            }
        },
        {
            xtype: 'textfield',
            name: 'search',
            width: 250,
            emptyText: CMDBuildUI.locales.Locales.common.actions.searchtext,
            itemId: 'dmssearchtext',
            cls: 'management-input',
            disabled: true,
            autoEl: {
                'data-testid': 'dms-container-searchtext'
            },
            bind: {
                disabled: '{disableRemoteActions}'
            },
            triggers: {
                search: {
                    cls: Ext.baseCSSPrefix + 'form-search-trigger',
                    handler: function (field, trigger, eOpts) {
                        field.fireEvent('searchsubmit', field, field.getValue(), eOpts);
                    }
                },
                clear: {
                    cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                    handler: function (field, trigger, eOpts) {
                        field.fireEvent('clearsearch', field, eOpts);
                    }
                }
            },
            localized: {
                emptyText: 'CMDBuildUI.locales.Locales.common.actions.searchtext'
            }
        },
        {
            xtype: 'filters-launcher',
            storeName: 'attachments',
            reference: 'filterslauncher',
            disabled: true,
            showAttributesPanel: false,
            showRelationsPanel: false,
            isDms: true,
            bind: {
                disabled: '{disableRemoteActions}'
            }
        },
        {
            xtype: 'button',
            itemId: 'dmsrefreshbtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            ui: 'management-neutral-action',
            tooltip: CMDBuildUI.locales.Locales.common.actions.refresh,
            disabled: true,
            autoEl: {
                'data-testid': 'dms-container-refreshbtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.refresh'
            },
            bind: {
                disabled: '{disableRemoteActions}'
            }
        },
        {
            xtype: 'button',
            itemId: 'dmscontextmenubtn',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('bars', 'solid'),
            ui: 'management-neutral-action',
            tooltip: CMDBuildUI.locales.Locales.common.grid.opencontextualmenu,
            arrowVisible: false,
            autoEl: {
                'data-testid': 'dms-container-contextmenubtn'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.grid.opencontextualmenu'
            },
            menu: [
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'),
                    itemId: 'dmscontextmenumultiselection',
                    text: CMDBuildUI.locales.Locales.common.grid.enamblemultiselection,
                    bind: {
                        disabled: '{enableExtendedGrid}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.common.grid.enamblemultiselection'
                    }
                },
                {
                    xtype: 'menuseparator'
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    itemId: 'dmscontextmenudelete',
                    text: CMDBuildUI.locales.Locales.bulkactions.delete,
                    hidden: true,
                    bind: {
                        hidden: '{readOnly}',
                        disabled: '{disabledbulkactions || enableExtendedGrid}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.bulkactions.delete'
                    }
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    itemId: 'dmscontextmenudownload',
                    text: CMDBuildUI.locales.Locales.bulkactions.download,
                    bind: {
                        disabled: '{disabledbulkactions || enableExtendedGrid}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.bulkactions.download'
                    }
                },
                {
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
                    itemId: 'dmscontextmenudownloadall',
                    text: CMDBuildUI.locales.Locales.bulkactions.downloadall,
                    bind: {
                        disabled: '{enableExtendedGrid}'
                    },
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.bulkactions.downloadall'
                    }
                }
            ]
        },
        {
            xtype: 'button',
            itemId: 'dmsselectallbtn',
            text: CMDBuildUI.locales.Locales.bulkactions.selectall,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('square', 'regular'),
            ui: 'secondary-action',
            enableToggle: true,
            hidden: true,
            bind: {
                hidden: '{!multiSelectionActive}'
            }
        },
        {
            xtype: 'tbfill'
        },
        {
            xtype: 'checkboxfield',
            fieldLabel: CMDBuildUI.locales.Locales.relations.extendeddata,
            itemId: 'showextendedfield',
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.relations.extendeddata'
            }
        }
    ]
});
