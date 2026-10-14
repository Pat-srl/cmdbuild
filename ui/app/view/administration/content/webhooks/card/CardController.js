Ext.define('CMDBuildUI.view.administration.content.webhooks.card.CardController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-webhooks-card',


    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#saveAndAddBtn': {
            click: 'onSaveBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#cloneBtn': {
            click: 'onCloneBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#enableBtn': {
            click: 'onToggleActiveBtnClick'
        },
        '#disableBtn': {
            click: 'onToggleActiveBtnClick'
        }
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);
        var vm = button.lookupViewModel();
        var headers = {};
        var isPhantom = vm.get('theWebhook').phantom;
        vm.get('headersStore').each(function (e) {
            headers[e.get('key')] = e.get('value');
        });
        vm.set('theWebhook.headers', headers);
        if ([CMDBuildUI.model.webhooks.Webhook.methods.get, CMDBuildUI.model.webhooks.Webhook.methods.delete].indexOf(vm.get('theWebhook.method')) > -1) {
            vm.set('theWebhook.body', {});
        }
        vm.get('theWebhook').save({
            success: function (record, operation) {
                CMDBuildUI.util.Utilities.showLoader(false);
                var eventToCall = 'itemcreated';
                if (!isPhantom) {
                    eventToCall = 'itemupdated';
                }
                vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs(eventToCall, [vm.get('grid'), record, this]);

                var detailsWindow = Ext.getCmp('CMDBuildAdministrationDetailsWindow');
                if (button.getItemId() !== 'saveAndAddBtn' && !detailsWindow.destroyed) {
                    detailsWindow.fireEvent('closed');
                } else {
                    button.setDisabled(false);
                }
            },
            failure: function () {
                button.setDisabled(false);
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
        this.getView().up('panel').close();
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onEditBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            _id = vm.get('theWebhook._id'),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        container.removeAll();
        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                links: {
                    theWebhook: {
                        type: 'CMDBuildUI.model.webhooks.Webhook',
                        id: _id
                    }
                },
                data: {
                    grid: grid,
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

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = me.getView();
        var vm = view.getViewModel();
        var theWebhook = vm.get('theWebhook');
        theWebhook.set('active', !theWebhook.get('active'));
        theWebhook.save({
            success: function (record, operation) {
                vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [vm.get('grid'), record, this]);

            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.getViewModel(),
            grid = vm.get('grid'),
            cloned = Ext.copy(vm.get('theWebhook').clone()),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                data: {
                    theWebhook: cloned,
                    grid: grid,
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.add,
                    actions: {
                        edit: false,
                        view: false,
                        add: true
                    }
                }
            }
        });
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        var me = this;
        var vm = me.getViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    button.setDisabled(true);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
                    var theWebhook = vm.get('theWebhook');
                    CMDBuildUI.util.Ajax.setActionId('delete-webhook');
                    theWebhook.erase({
                        failure: function (error) {
                            theWebhook.reject();
                        },
                        success: function (record, operation) {
                            vm.get('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemremoved', [vm.get('grid'), record, this]);
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                            var detailsWindow = Ext.getCmp('CMDBuildAdministrationDetailsWindow');
                            detailsWindow.fireEvent('closed');
                        }
                    });
                } else {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                }
            }, this
        );
    }
});
