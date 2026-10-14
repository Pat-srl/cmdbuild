Ext.define('CMDBuildUI.view.administration.content.users.card.ViewController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-users-card-view',

    control: {
        '#': {
            afterrender: 'onAfterRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleBtnClick'
        },
        '#changePasswordBtn': {
            click: 'onChangePasswordClickBtn'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.users.card.Edit} view
     * @param {Object} eOpts
     */
    onAfterRender: function (view) {
        CMDBuildUI.util.Utilities.showLoader(true, view);
        this.getViewModel().bind({
            bindTo: '{theUser}',
            single: true
        }, function () {
            CMDBuildUI.util.Utilities.showLoader(false, view);
        });
    },

    onEditBtnClick: function (button, event) {
        var me = this,
            view = me.getView(),
            vm = view.getViewModel(),
            theUserId = vm.get('theUser._id'),
            grid = vm.get('grid'),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        container.removeAll();
        container.add({
            xtype: 'administration-content-users-card-edit',
            viewModel: {
                links: {
                    theUser: {
                        type: 'CMDBuildUI.model.users.User',
                        id: theUserId
                    }
                },
                data: {
                    grid: grid
                }
            }
        });

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */

    onToggleBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theUser = vm.get('theUser');
        theUser.set('active', !theUser.get('active'));
        // TODO: is best if use proxy writer
        Ext.Array.forEach(theUser.get('userGroups'), function (element, index) {
            if (theUser.data.userGroups[index] && theUser.data.userGroups[index].isModel) {
                theUser.data.userGroups[index] = theUser.data.userGroups[index].getData();
            }
            delete theUser.data.userGroups[index].id;
        });
        Ext.Array.forEach(theUser.get('userTenants'), function (element, index) {
            if (theUser.data.userTenants[index] && theUser.data.userTenants[index].isModel) {
                theUser.data.userTenants[index] = theUser.data.userTenants[index].getData();
            }
            delete theUser.data.userTenants[index].id;
        });
        theUser.save({
            success: function (record, operation) {
                Ext.GlobalEvents.fireEventArgs("userupdated", [view, record]);
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
                    title: CMDBuildUI.locales.Locales.administration.navigation.users,
                    grid: view.config.viewModel.data.grid
                }
            }
        });
    },
    /**
     * unused but necessary function
     * @param {*} store 
     * @param {*} items 
     * @param {*} operation 
     * @param {*} success 
     */
    onTenantStoreLoad: function (store, items, operation, success) {

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