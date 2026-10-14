Ext.define('CMDBuildUI.view.administration.content.users.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.helper.ConfigHelper'],
    alias: 'controller.administration-content-users-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onViewBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onActiveToggleBtnClick'
        },
        '#changePasswordBtn': {
            click: 'onChangePasswordClickBtn'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        this.linkUser(view, vm);
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     *
     */
    onAfterRender: function (view) {
        var vm = this.getViewModel(),
            isMultitenantActive = CMDBuildUI.util.helper.Configurations.get(CMDBuildUI.model.Configuration.multitenant.enabled);
        if (!isMultitenantActive) {
            view.child('#tenants').tab.hide();
        } else {
            view.child('#tenants').tab.show();
        }
        CMDBuildUI.util.Utilities.showLoader(true, view);
        vm.bind({
            bindTo: '{theUser}',
            single: true
        }, function () {
            CMDBuildUI.util.Utilities.showLoader(false, view);
        });
    },

    onEditBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        var currentUser = view.getViewModel().get('selected') || view.getViewModel().get('theUser');

        container.add({
            xtype: 'administration-content-users-card-edit',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        id: currentUser.get('_id')
                    }
                },
                data: {
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    actions: {
                        edit: true,
                        view: false,
                        add: false
                    }
                }
            }
        });
    },

    onViewBtnClick: function () {
        var view = this.getView();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        var currentUser = view.getViewModel().get('selected') || view.getViewModel().get('theUser');
        container.add({
            xtype: 'administration-content-users-card-view',

            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        id: currentUser.get('_id')
                    }
                },
                data: {
                    title: CMDBuildUI.locales.Locales.administration.navigation.users + ' - ' + view.getViewModel().get('theUser').get('username'),
                    grid: this.getView().up('grid')
                }
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theUser = vm.get('theUser');
        theUser.set('active', !theUser.get('active'));
        // TODO: is best if use proxy writer
        Ext.Array.forEach(theUser.get('userGroups'), function (element, index) {
            if (theUser.data.userGroups[index].isModel) {
                theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
            }
            delete theUser.data.userGroups[index].id;
        });
        Ext.Array.forEach(theUser.get('userTenants'), function (element, index) {
            if (theUser.data.userTenants[index].isModel) {
                theUser.data.userTenants[index] = theUser.data.userTenants[index].getData();
            }
            delete theUser.data.userTenants[index].id;
        });
        theUser.save({
            success: function (record, operation) {
                view.up('grid').updateRowWithExpader(record);

            }
        });

    },

    onCloneBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        var newUser = vm.get('theUser').clone();

        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-create',

            viewModel: {
                data: {
                    theUser: newUser,
                    title: CMDBuildUI.locales.Locales.administration.navigation.users + ' - ' + vm.get('theUser').get('username'),
                    grid: this.getView().up('grid')
                }
            }
        });
    },

    linkUser: function (view, vm) {
        if (view) {
            var selected = view._rowContext.record;
            vm.linkTo('theUser', {
                type: 'CMDBuildUI.model.users.User',
                id: selected.get('_id')
            });
        }
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
    },


    /**
     * 
     * @param {*} btn 
     */
    onChangePasswordClickBtn: function (btn) {
        var vm = this.getViewModel();
        var theUser = vm.get('theUser');
        var title = Ext.String.format('{0} - {1}', CMDBuildUI.locales.Locales.main.password.change, theUser.get('username'));
        var config = {
            xtype: 'administration-users-changepassword-form'
        };
        CMDBuildUI.util.Utilities.openPopup('popup-change-password', title, config, null, {
            width: '450px',
            height: '350px',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        id: theUser.get('_id')
                    }
                }
            }
        });
    }

});