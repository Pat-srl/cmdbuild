Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.fieldsets.DisabledProcessesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-uiconfig-fieldsets-disabledprocesses',
    formulas: {
        configManager: {
            bind: {
                theGroup: '{theGroup}'
            },
            get: function (data) {
                var gridData = [];
                gridData.push(this.createRowData('_rp_flow_tab_note_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note));
                gridData.push(this.createRowData('_rp_flow_tab_relation_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations));
                gridData.push(this.createRowData('_rp_flow_tab_history_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history));
                gridData.push(this.createRowData('_rp_flow_tab_email_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email));
                gridData.push(this.createRowData('_rp_flow_tab_attachment_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments));
                this.set('gridData', gridData);
            }
        }
    },
    stores: {
        disabledProcessTabsStore: {
            fields: ['name', 'description', 'read', 'write'],
            proxy: {
                type: 'memory'
            },
            data: '{gridData}'
        }
    },
    createRowData: function (name, description) {
        var group = this.get('theGroup');
        return {
            name: name,
            description: description,
            'none': group.get(name + '_read') === false,
            'read': (name === '_rp_flow_tab_attachment_access' &&
                    group.get(name + '_read') === true &&
                    group.get(name + '_write') === false) ||
                (name !== '_rp_flow_tab_attachment_access' &&
                    group.get(name + '_read') === true),
            'write': name === '_rp_flow_tab_attachment_access' && group.get(name + '_write') === true
        };
    }
});