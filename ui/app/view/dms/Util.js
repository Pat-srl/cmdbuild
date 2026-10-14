Ext.define('CMDBuildUI.view.dms.Util', {
    singleton: true,

    /**
     * 
     * @returns 
     */
    getTools: function () {
        return [{
            // edit tool
            xtype: 'tool',
            itemId: 'editBtnAttachment',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.editattachment,
            autoEl: {
                'data-testid': 'dms-container-tool-edit'
            },
            handler: 'onEditToolClick',
            bind: {
                hidden: '{readOnly}',
                disabled: '{!basepermissions.edit || !record._can_update}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.editattachment'
            }
        }, {
            // delete tool
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.deleteattachment,
            autoEl: {
                'data-testid': 'dms-container-tool-delete'
            },
            handler: 'onDeleteToolClick',
            bind: {
                hidden: '{readOnly}',
                disabled: '{!record._can_delete}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.deleteattachment'
            }
        }, {
            // download tool
            xtype: 'tool',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('download', 'solid'),
            cls: 'management-tool',
            hidden: true,
            // disabled: true,
            tooltip: CMDBuildUI.locales.Locales.attachments.download,
            autoEl: {
                'data-testid': 'dms-container-tool-delete'
            },
            handler: 'onDownloadToolClick',
            bind: {
                hidden: '{readOnly}',
                disabled: '{isRecordPhantom}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.attachments.download'
            }
        }];
    },

    /**
     * 
     * @returns 
     */
    getHelpTool: function () {
        return {
            // help tool
            xtype: 'tool',
            helpValue: null,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('question-circle', 'solid'),
            itemId: 'helpBtnAttachment',
            cls: 'management-tool no-action',
            tooltip: CMDBuildUI.locales.Locales.common.actions.help,
            autoEl: {
                'data-testid': 'dms-container-tool-help'
            },
            callback: function () {
                CMDBuildUI.util.Utilities.openPopup(
                    null,
                    CMDBuildUI.locales.Locales.common.actions.help,
                    {
                        xtype: 'panel',
                        html: this.helpValue,
                        layout: 'fit',
                        padding: CMDBuildUI.util.helper.FormHelper.properties.padding,
                        cls: 'x-selectable',
                        scrollable: true
                    });
            },
            // hidden: true,
            bind: {
                helpValue: '{DMSClass.help}'
            },
            setHelpValue: function (value) {
                this.helpValue = value;

                this.setVisible(!Ext.isEmpty(value));
                this.setZIndex(1);
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.common.actions.help'
            }
        };
    }
});