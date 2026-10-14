Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.SettingsFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-settingsfieldset',
    data: {
        isView: false,
        isClass: false,
        isProcess: false
    },
    formulas: {
        settingsOrActionsTitle: {
            bind: {
                objectType: '{grant.objectType}'
            },
            get: function (data) {
                if (data.objectType === CMDBuildUI.util.helper.ModelHelper.objecttypes.process) {
                    return CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.actions;
                }
                return CMDBuildUI.locales.Locales.administration.systemconfig.settings;
            }
        },
        configManager: {
            bind: {
                grant: '{grant}'
            },
            get: function (data) {
                var theObject;
                if (data.grant.get('objectType') !== CMDBuildUI.util.helper.ModelHelper.objecttypes.view) {
                    theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.grant.get('objectTypeName'));
                } else {
                    //TODO: open server issue coz view grant doesn't contain objectTypeName but id.
                    // theObject = Ext.getStore('views.Views').getById(data.grant.get('objectTypeName'));

                    theObject = {
                        isView: true
                    };
                }
                this.set('isView', !theObject.isView ? false : true);
                this.set('isClass', !theObject.isClass ? false : true);
                this.set('isProcess', !theObject.isProcess ? false : true);

            }
        },
        _can_bulk_update_value: {
            bind: '{grant._can_bulk_update}',
            get: function (value) {
                if (value === null) { //needed to handle the null value in combobox;
                    return 'null';
                } else {
                    return value;
                }
            },
            set: function (value) {
                this.get('grant').set('_can_bulk_update', Ext.JSON.decode(value)); //needed to handle the null value in combobox 
            }
        },
        _can_bulk_delete_value: {
            bind: '{grant._can_bulk_delete}',
            get: function (value) {
                if (value === null) { //needed to handle the null value in combobox;
                    return 'null';
                } else {
                    return value;
                }
            },
            set: function (value) {
                this.get('grant').set('_can_bulk_delete', Ext.JSON.decode(value));
            }
        },
        _can_fc_attachment_value: {
            bind: '{grant._can_fc_attachment}',
            get: function (value) {
                if (value === null) { //needed to handle the null value in combobox;
                    return 'null';
                } else {
                    return value;
                }
            },
            set: function (value) {
                this.get('grant').set('_can_fc_attachment', Ext.JSON.decode(value)); //needed to handle the null value in combobox 
            }
        },
        _can_bulk_abort_value: {
            bind: '{grant._can_bulk_abort}',
            get: function (value) {
                if (value === null) { //needed to handle the null value in combobox;
                    return 'null';
                } else {
                    return value;
                }
            },
            set: function (value) {
                this.get('grant').set('_can_bulk_abort', Ext.JSON.decode(value));
            }
        },
        _can_search_value: {
            bind: '{grant._can_search}',
            get: function (value) {
                if (value === null) { //needed to handle the null value in combobox;
                    return 'null';
                }
                return value;
            },
            set: function (value) {
                this.get('grant').set('_can_search', Ext.JSON.decode(value));
            }
        },
        fulltextStoreData: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getSearchfieldInGridsOptions(true);
            }
        },
        bulkStoreData: {
            get: function () {
                return CMDBuildUI.util.helper.ModelHelper.bulkComboPermissionsData();
            }
        }
    },
    stores: {
        canBulkUpdateStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{bulkStoreData}',
            autoDestroy: true
        },
        canBulkDeleteStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{bulkStoreData}',
            autoDestroy: true
        },

        canAttachmentStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{bulkStoreData}',
            autoDestroy: true
        },
        canBulkAbortStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{bulkStoreData}',
            autoDestroy: true
        },
        fulltextStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: {
                type: 'memory'
            },
            data: '{fulltextStoreData}',
            autoDestroy: true
        }
    }
});