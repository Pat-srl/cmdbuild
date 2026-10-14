Ext.define('CMDBuildUI.view.administration.content.groupsandpermissions.tabitems.permissions.components.grantconfig.actions.ActionFieldsetModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-groupsandpermissions-tabitems-permissions-components-grantconfig-actions-actionfieldset',
    data: {
        isView: false,
        isClass: false,
        isProcess: false,
        isPrototype: false
    },

    formulas: {
        configManager: {
            bind: {
                grant: '{grant}'
            },
            get: function (data) {
                var theObject;
                if (data.grant.get('objectType') !== 'view') {
                    theObject = CMDBuildUI.util.helper.ModelHelper.getObjectFromName(data.grant.get('objectTypeName'));
                    if (theObject.get('prototype')) {
                        this.set('isPrototype', true);
                    }
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
        }
    }

});