Ext.define('CMDBuildUI.view.administration.content.users.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-users-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#saveAndAddBtn': {
            click: 'onSaveAndAddBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    // /**
    //  * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.EditController} view
    //  * @param {Object} eOpts
    //  */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        // set vm varibles
        if (!vm.get('theUser') || !vm.get('theUser').phantom) {
            vm.linkTo("theUser", {
                type: 'CMDBuildUI.model.users.User',
                create: true
            });
        }
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__core__DOT__enableMultigroupByDefault').then(function (configValue) {
            vm.set('theUser.multiGroup', configValue === 'true');
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts, callback) {


        var me = this;
        var vm = me.getViewModel();
        var form = me.getView();
        var theUser = vm.getData().theUser;
        function save() {
            CMDBuildUI.util.Utilities.showLoader(true);
            if (form.isValid()) {                               
                theUser.getProxy().setUrl('/users');

                delete theUser.data.inherited;
                delete theUser.data.writable;
                delete theUser.data.hidden;
                Ext.Array.forEach(theUser.get('userGroups'), function (element, index) {
                    if (theUser.data.userGroups[index] && theUser.data.userGroups[index].isModel) {
                        theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
                    }
                    delete theUser.data.userGroups[index].id;
                });
                Ext.Array.forEach(theUser.get('userTenants'), function (element, index) {
                    if (theUser.data.userGroups[index] && theUser.data.userGroups[index].isModel) {
                        theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
                    }
                    delete theUser.data.userTenants[index].id;
                });
                theUser.save({
                    success: function (record, operation) {                                               
                        Ext.GlobalEvents.fireEventArgs('usercreated', [record]);
                        CMDBuildUI.util.Utilities.showLoader(false);
                        CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                        if (callback) {
                            callback();
                        }
                    },
                    failure: function () {
                        CMDBuildUI.util.Utilities.showLoader(false);
                    }
                });
            } else {
                var w = Ext.create('Ext.window.Toast', {
                    ui: 'administration',
                    html: CMDBuildUI.locales.Locales.administration.common.messages.correctformerrors,
                    title: CMDBuildUI.locales.Locales.administration.common.messages.error,
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'solid'),
                    align: 'br'
                });
                w.show();
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        }
        if (!theUser.get('password').length) {
            CMDBuildUI.util.Msg.confirm(
                CMDBuildUI.locales.Locales.administration.common.messages.attention,
                CMDBuildUI.locales.Locales.administration.users.messages.passwordnotset,
                function (btnText) {
                    if (btnText === "yes") {
                        save();
                    }
                }, this);
        } else {
            save();
        }

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveAndAddBtnClick: function (button, e, eOpts) {
        var grid = button.lookupViewModel().get('grid');
        var callback = function () {
            var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

            container.removeAll();
            container.add({
                xtype: 'administration-content-users-card-create',
                viewModel: {
                    data: {
                        grid: grid
                    },
                    links: {
                        theUser: {
                            type: 'CMDBuildUI.model.users.User',
                            create: true
                        }
                    }
                }
            });
        };
        this.onSaveBtnClick(button, e, eOpts, callback);

    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getViewModel().get("theUser").reject();
        this.getView().up().fireEvent("closed");
    },



    onTenantStoreLoad: function (store, items, operation, success) {
        var vm = this.getViewModel();
        var theUser = vm.get('theUser');
        var tenantsData = [];
        if (!theUser.get('userTenants')) {
            theUser.set('userTenants', []);
        }

        items.forEach(function (tenant) {

            var exist = theUser.get('userTenants').find(function (userTenant) {
                return userTenant._id === tenant.get('_id');
            });

            var _tenant = {
                description: tenant.get('description'),
                _id: tenant.get('_id'),
                name: tenant.get('name') || tenant.get('code'),
                active: (exist) ? true : false
            };
            tenantsData.push(_tenant);
        });
        vm.set('tenantsData', tenantsData);
        vm.set('theUsers.userTenants', tenantsData);
    }
});