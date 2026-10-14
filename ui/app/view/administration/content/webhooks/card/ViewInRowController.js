Ext.define('CMDBuildUI.view.administration.content.webhooks.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-webhooks-viewinrow',
    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#openBtn': {
            click: 'onOpenBtnClick'
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
    * @param {CMDBuildUI.view.administration.content.webhooks.card.ViewInRow} view    
    */
    onBeforeRender: function (view) {
        var me = this;
        var vm = me.getViewModel();
        var selected = view._rowContext.record;

        vm.linkTo('theWebhook', {
            type: 'CMDBuildUI.model.webhooks.Webhook',
            id: selected.get('_id')
        });

        me.getViewModel().set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
    },

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onEditBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);

        container.removeAll();

        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                links: {
                    theWebhook: {
                        type: 'CMDBuildUI.model.webhooks.Webhook',
                        id: vm.get('theWebhook._id')
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

    /**
    * @param {Ext.button.Button} button
    * @param {Event} e
    * @param {Object} eOpts
    */
    onOpenBtnClick: function (button, e, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                links: {
                    theWebhook: {
                        type: 'CMDBuildUI.model.webhooks.Webhook',
                        id: vm.get('theWebhook._id')
                    }
                },
                data: {
                    grid: view.up('grid'),
                    action: CMDBuildUI.util.administration.helper.FormHelper.formActions.view,
                    actions: {
                        edit: false,
                        view: true,
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
                view.up('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [view.up('grid'), record, this]);

            }
        });
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var cloned = Ext.copy(vm.get('theWebhook').clone());
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-webhooks-card',
            viewModel: {
                data: {
                    theWebhook: cloned,
                    grid: view.up('grid'),
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
                            button.up('grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemremoved', [button.up('grid'), record, this]);
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
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
