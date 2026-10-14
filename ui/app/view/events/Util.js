Ext.define('CMDBuildUI.view.events.Util', {
    singleton: true,

    getTools: function () {
        return [{
            // open tool
            xtype: 'tool',
            itemId: 'openBtnEvent',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('external-link-alt', 'solid'),
            cls: 'management-tool',
            action: 'view',
            hidden: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.opencard,
            autoEl: {
                'data-testid': 'events-event-tool-open'
            },
            bind: {
                hidden: '{hiddentools.open}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.opencard'
            }
        }, {
            // edit tool
            xtype: 'tool',
            itemId: 'editBtnEvent',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('pencil-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            // disabled: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.modifycard,
            autoEl: {
                'data-testid': 'events-event-tool-edit'
            },
            bind: {
                hidden: '{hiddentools.edit}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.modifycard'
            }
        }, {
            // delete tool
            xtype: 'tool',
            itemId: 'deleteBtnEvent',
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('trash-alt', 'solid'),
            cls: 'management-tool',
            hidden: true,
            disabled: true,
            tooltip: CMDBuildUI.locales.Locales.classes.cards.deletecard,
            autoEl: {
                'data-testid': 'events-event-tool-delete'
            },
            bind: {
                hidden: '{hiddentools.delete}',
                disabled: '{!permissions.delete}'
            },
            localized: {
                tooltip: 'CMDBuildUI.locales.Locales.classes.cards.deletecard'
            }
        }];
    }
});