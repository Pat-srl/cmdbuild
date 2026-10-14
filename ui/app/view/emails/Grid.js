Ext.define('CMDBuildUI.view.emails.Grid', {
    extend: 'Ext.grid.Panel',

    requires: ['CMDBuildUI.view.emails.GridController', 'CMDBuildUI.view.emails.GridModel'],

    alias: 'widget.emails-grid',
    controller: 'emails-grid',
    viewModel: {
        type: 'emails-grid'
    },

    ui: 'cmdbuildgrouping',

    config: {
        /**
         * @cfg {Boolean} readOnly
         *
         * Set to `true` to show details tabs in read-only mode
         */
        readOnly: false,

        /**
         * @cfg {String} formMode
         *
         * Form mode
         */
        formMode: null
    },

    forceFit: true,

    bind: {
        store: '{chainedemails}'
    },

    viewConfig: {
        getRowClass: function (record) {
            return record &&
                record.get('status') === CMDBuildUI.model.emails.Email.statuses.received &&
                !record.get('isReadByUser')
                ? Ext.baseCSSPrefix + 'new-email'
                : null;
        }
    },

    features: [
        {
            ftype: 'grouping',
            itemId: 'grouper',
            enableGroupingMenu: false,
            groupHeaderTpl: [
                '{name:this.formatName} ({children:this.getTotalRows})',
                {
                    formatName: function (name) {
                        return CMDBuildUI.locales.Locales.emails.statuses[name];
                    },
                    getTotalRows: function (rows) {
                        return rows.length;
                    }
                }
            ],
            depthToIndent: 50
        }
    ],

    columns: [
        {
            minWidth: 40,
            maxWidth: 40,
            align: 'center',
            hidden: true,
            hideable: false,
            draggable: false,
            menuDisabled: true,
            renderer: function (data, cell, record) {
                const status = record.get('status');
                let statusTxt = CMDBuildUI.locales.Locales.emails.statuses[status];
                let icon = CMDBuildUI.model.emails.Email.statusicon[record.get('status')];
                if (!record.get('isReadByUser') && status === CMDBuildUI.model.emails.Email.statuses.received) {
                    icon = CMDBuildUI.model.emails.Email.statusicon['new'];
                    statusTxt = CMDBuildUI.locales.Locales.emails.statuses['new'];
                }
                return Ext.String.format('<i class="{0}" data-qtip="{1}"></i>', icon, statusTxt);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.emails.archivingdate,
            dataIndex: 'date',
            align: 'left',
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.archivingdate'
            },
            renderer: function (value, metaData, record, rowIndex, colIndex, store, view) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.emails.from,
            dataIndex: 'from',
            align: 'left',
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.from'
            },
            renderer: function (data) {
                return CMDBuildUI.util.Utilities.transformMajorMinor(data);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.emails.to,
            dataIndex: 'to',
            align: 'left',
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.to'
            },
            renderer: function (data) {
                return CMDBuildUI.util.Utilities.transformMajorMinor(data);
            }
        },
        {
            text: CMDBuildUI.locales.Locales.emails.subject,
            dataIndex: 'subject',
            align: 'left',
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.subject'
            },
            renderer: function (data) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTextField(data);
            }
        },
        {
            xtype: 'actioncolumn',
            minWidth: 200,
            maxWidth: 200,
            hidden: true,
            hideable: false,
            menuDisabled: true,
            bind: {
                hidden: '{readonly}'
            },
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('envelope', 'regular'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.regenerateemail;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actionregenerate', grid, record, rowIndex, colIndex);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const isAsync = view.lookupViewModel().get('isAsync');
                        const statusDraft =
                            record.get('status') == CMDBuildUI.model.emails.Email.statuses.draft ? true : false;
                        const fromTemplate = record.get('template') !== '';
                        return isAsync || !(view.ownerCt.isFormWritable() && statusDraft && fromTemplate);
                    }
                },
                {
                    iconCls: 'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('reply', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.reply;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actionreply', grid, record, rowIndex, colIndex);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const isAsync = view.lookupViewModel().get('isAsync');
                        const status = record.get('status');
                        const received = status === CMDBuildUI.model.emails.Email.statuses.received;
                        const sent = status === CMDBuildUI.model.emails.Email.statuses.sent;
                        return isAsync || !(view.ownerCt.isFormWritable() && (received || sent));
                    }
                },
                {
                    iconCls:
                        'attachments-grid-action ' +
                        CMDBuildUI.util.helper.IconHelper.getIconId('paper-plane', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.sendemail;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actionsend', grid, record, rowIndex, colIndex);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const isAsync = view.lookupViewModel().get('isAsync');
                        const statusDraft =
                            record.get('status') == CMDBuildUI.model.emails.Email.statuses.draft ? true : false;
                        return isAsync || !(view.ownerCt.isFormWritable() && statusDraft);
                    }
                },
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.edit;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actionedit', grid, record, rowIndex, colIndex);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const statusDraft =
                            record.get('status') == CMDBuildUI.model.emails.Email.statuses.draft ? true : false;
                        return !(view.ownerCt.isFormWritable() && statusDraft);
                    }
                },
                {
                    iconCls:
                        'attachments-grid-action ' +
                        CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.view;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actionview', grid, record, rowIndex, colIndex);
                    }
                },
                {
                    iconCls:
                        'attachments-grid-action ' + CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.remove;
                    },
                    handler: function (grid, rowIndex, colIndex, item, e, record) {
                        grid.fireEvent('actiondelete', grid, record, rowIndex, colIndex);
                    },
                    isActionDisabled: function (view, rowIndex, colIndex, item, record) {
                        const statusDraft =
                            record.get('status') == CMDBuildUI.model.emails.Email.statuses.draft ? true : false;
                        return !(view.ownerCt.isFormWritable() && statusDraft);
                    }
                }
            ]
        },
        {
            xtype: 'actioncolumn',
            minWidth: 30,
            maxWidth: 30,
            hidden: true,
            hideable: false,
            menuDisabled: true,
            bind: {
                hidden: '{!readonly}'
            },
            items: [
                {
                    iconCls:
                        'attachments-grid-action ' +
                        CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
                    getTip: function () {
                        return CMDBuildUI.locales.Locales.emails.view;
                    },
                    handler: function (grid, rowIndex, colIndex) {
                        const record = grid.getStore().getAt(rowIndex);
                        grid.fireEvent('actionview', grid, record, rowIndex, colIndex);
                    }
                }
            ]
        }
    ],

    tbar: [
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.emails.composeemail,
            reference: 'composeemail',
            itemId: 'composeemail',
            ui: 'management-primary-small',
            hidden: true,
            bind: {
                disabled: '{disableButtonOnView}',
                hidden: '{readonly}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.composeemail'
            }
        },
        {
            xtype: 'button',
            text: CMDBuildUI.locales.Locales.emails.regenerateallemails,
            reference: 'regenerateallemails',
            itemId: 'regenerateallemails',
            ui: 'management-neutral-action-small',
            hidden: true,
            bind: {
                disabled: '{disableButtonOnView || isAsync}',
                hidden: '{readonly}'
            },
            localized: {
                text: 'CMDBuildUI.locales.Locales.emails.regenerateallemails'
            }
        },
        {
            xtype: 'button',
            reference: 'gridrefresh',
            itemId: 'gridrefresh',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('sync-alt', 'solid'),
            ui: 'management-neutral-action-small',
            bind: {
                disabled: '{isAsync}'
            }
        },
        '->',
        {
            xtype: 'checkbox',
            boxLabel: CMDBuildUI.locales.Locales.emails.groupbystatus,
            localized: {
                boxLabel: 'CMDBuildUI.locales.Locales.emails.groupbystatus'
            },
            bind: {
                value: '{groupByStatus}'
            }
        }
    ],

    /**
     * @return {Boolean}
     */
    isFormWritable: function () {
        const formMode = this.getFormMode();
        return (
            formMode === CMDBuildUI.util.helper.FormHelper.formmodes.update ||
            formMode === CMDBuildUI.util.helper.FormHelper.formmodes.create
        );
    }
});
