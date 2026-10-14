Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.uiconfig.UiConfigModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-uiconfig-uiconfig',
    formulas: {
        bulkComboData: function () {
            return CMDBuildUI.util.helper.ModelHelper.bulkComboPermissionsData();
        },

        bulkUpdate_value: {
            bind: '{theGroup.bulkUpdate}',
            get: function (value) {
                return value === null ? 'null' : value;
            },
            set: function (value) {
                this.get('theGroup').set('bulkUpdate', Ext.JSON.decode(value)); //needed to handle the null value in combobox 
            }
        },
        bulkDelete_value: {
            bind: '{theGroup.bulkDelete}',
            get: function (value) {
                return value === null ? 'null' : value;
            },
            set: function (value) {
                this.get('theGroup').set('bulkDelete', Ext.JSON.decode(value));
            }
        },
        bulkAbort_value: {
            bind: '{theGroup.bulkAbort}',
            get: function (value) {
                return value === null ? 'null' : value;
            },
            set: function (value) {
                this.get('theGroup').set('bulkAbort', Ext.JSON.decode(value));
            }
        },
        fulltext_value: {
            bind: '{theGroup.fullTextSearch}',
            get: function (value) {
                return value === null ? 'null' : value;
            },
            set: function (value) {
                this.get('theGroup').set('fullTextSearch', Ext.JSON.decode(value));
            }
        },
        fulltextStoreData: {
            get: function () {
                return CMDBuildUI.util.administration.helper.ModelHelper.getSearchfieldInGridsOptions(true);
            }
        }
    },
    stores: {

        bulkActionsStore: {
            model: 'CMDBuildUI.model.base.ComboItem',
            proxy: 'memory',
            data: '{bulkComboData}',
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