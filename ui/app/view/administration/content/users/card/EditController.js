Ext.define('CMDBuildUI.view.administration.content.users.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.view-administration-content-users-card-edit',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            afterrender: 'onAfterRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.users.card.Edit} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        if (!CMDBuildUI.util.Stores.loaded.groups) {
            CMDBuildUI.util.Stores.loadGroupsStore();
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

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        CMDBuildUI.util.Utilities.showLoader(true);
        var me = this;
        var form = me.getView();
        var vm = me.getViewModel();
        var theUser = vm.get('theUser');
        if (theUser) {
            // TODO: is best if use proxy writer
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
        }
        theUser.save({
            success: function (record, operation) {
                Ext.GlobalEvents.fireEventArgs('usercreated', [record]);
                form.up().fireEvent("closed");
            },
            failure: function () {
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        vm.get("theUser").reject(); // discard changes
        this.getView().up().fireEvent("closed");
    },

    /**
     * On translate button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, e, eOpts) {

        var vm = this.getViewModel();
        var theValue = vm.get('theUser');

        var content = {
            xtype: 'administration-localization-localizecontent',
            scrollable: 'y',
            viewModel: {
                data: {
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.edit,
                    translationCode: Ext.String.format('lookup.{0}.description', theValue.get('_type'))
                }
            }
        };
        // custom panel listeners
        var listeners = {
            /**
             * @param {Ext.panel.Panel} panel
             * @param {Object} eOpts
             */
            close: function (panel, eOpts) {
                CMDBuildUI.util.Utilities.closePopup('popup-edit-classuser-localization');
            }
        };
        // create panel
        var popUp = CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-classuser-localization',
            CMDBuildUI.locales.Locales.administration.common.strings.localization,
            content,
            listeners, {
            ui: 'administration-actionpanel',
            width: '450px',
            height: '450px'
        }
        );

        popUp.setPagePosition(e.getX() - 450, e.getY() + 20);
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