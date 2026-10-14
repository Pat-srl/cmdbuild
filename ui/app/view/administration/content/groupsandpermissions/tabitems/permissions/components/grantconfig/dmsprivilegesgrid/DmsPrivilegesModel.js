Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.dmsprivilegesgrid.DmsPrivilegesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-dmsprivilegesgrid-dmsprivileges',
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
                var me = this;
                var theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.grant.get('objectTypeName'));
                this.set('theObject', theObject);
                var dmsCategory = theObject.get('dmsCategory');
                if (Ext.isEmpty(dmsCategory)) {
                    dmsCategory = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.dms.category);
                }
                var category = CMDBuildUI.model.dms.DMSCategoryType.getCategoryTypeFromName(dmsCategory);
                category.getCategoryValues().then(function (store) {
                    var gridData = [];
                    store.each(function (record) {
                        if (theObject.isClass || theObject.isProcess) {
                            gridData.push(me.createRowData(category.get('name'), record.get('code'), record.get('description')));
                        }
                    });
                    me.set('gridData', gridData);
                });

                if (theObject.get('prototype')) {
                    this.set('isPrototype', true);
                }

            }
        }
    },

    stores: {
        gridDataStore: {
            fields: ['name', 'description', 'default', 'write', 'read', 'none'],
            proxy: {
                type: 'memory'
            },
            data: '{gridData}'
        }
    },

    createRowData: function (category, value, description) {

        var key = Ext.String.format('{0}_{1}', category, value);
        if (Ext.Object.isEmpty(this.get('grant.dmsPrivileges')) || Ext.isEmpty(this.get('grant.dmsPrivileges')[key])) {
            this.get('grant.dmsPrivileges')[key] = 'default';
        }
        return {
            name: key,
            description: description,
            'default': this.get('grant.dmsPrivileges')[key] === 'default',
            'none': this.get('grant.dmsPrivileges')[key] === 'none',
            'read': this.get('grant.dmsPrivileges')[key] === 'read',
            'write': this.get('grant.dmsPrivileges')[key] === 'write'
        };
    }

});