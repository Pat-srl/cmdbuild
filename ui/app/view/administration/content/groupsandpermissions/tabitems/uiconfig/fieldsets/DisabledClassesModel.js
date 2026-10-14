Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledClassesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledclasses',
    formulas: {
        configManager: {
            bind: {
                theGroup: '{theGroup}'
            },
            get: function (data) {
                var gridData = [];
                gridData.push(this.createRowData('_rp_card_tab_detail_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.detail));
                gridData.push(this.createRowData('_rp_card_tab_note_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note));
                gridData.push(this.createRowData('_rp_card_tab_relation_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations));
                gridData.push(this.createRowData('_rp_card_tab_history_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history));
                gridData.push(this.createRowData('_rp_card_tab_email_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email));
                gridData.push(this.createRowData('_rp_card_tab_attachment_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments));
                gridData.push(this.createRowData('_rp_card_tab_schedule_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.schedules));
                this.set('gridData', gridData);
            }
        }
    },
    stores: {
        disabledClassTabsStore: {
            fields: ['name', 'description', 'read', 'write'],
            proxy: {
                type: 'memory'
            },
            data: '{gridData}'
        }
    },
    createRowData: function (name, description) {
        return {
            name: name,
            description: description,
            'none': (this.get('theGroup').get(name + '_read') === false),
            'read': (this.get('theGroup').get(name + '_read') === true && this.get('theGroup').get(name + '_write') === false),
            'write': (this.get('theGroup').get(name + '_write') === true)
        };
    }
});