Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.tabsconfiggrid.TabsConfigModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-tabsconfiggrid-tabsconfig',
    data: {
        componentType: null,
        gridData: []
    },

    formulas: {
        configManager: {
            bind: {
                grant: '{grant}'
            },
            get: function (data) {
                var theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.grant.get('objectTypeName'));
                this.set('theObject', theObject);
                var gridData = [];
                if (theObject.isClass) {
                    gridData.push(this.createRowData('_detail_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.detail));
                }
                gridData.push(this.createRowData('_note_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.note));
                gridData.push(this.createRowData('_relation_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.relations));
                gridData.push(this.createRowData('_history_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.history));
                gridData.push(this.createRowData('_email_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.email));
                gridData.push(this.createRowData('_attachment_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.attachments));
                if (theObject.isClass) {
                    gridData.push(this.createRowData('_schedule_access', CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.schedules));
                }
                if (theObject.get('prototype')) {
                    this.set('isPrototype', true);
                }
                this.set('gridData', gridData);
            }
        }
    },

    stores: {
        gridDataStore: {
            fields: ['name', 'description', 'default', 'read', 'write'],
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
            default: !this.get('grant').get(name + '_read') && this.get('grant').get(name + '_read') !== 'false',
            none: (this.get('grant').get(name + '_read') === 'false'),
            read: (this.get('grant').get(name + '_read') === 'true' && this.get('grant').get(name + '_write') === 'false'),
            write: (this.get('grant').get(name + '_write') === 'true')
        };
    }

});