Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-accounts-card-viewinrow',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#setDefaultAccount': {
            click: 'onToggleDefaultBtnClick'
        },
        '#defaultAccount': {
            click: 'onToggleDefaultBtnClick'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.content.classes.tabitems.users.card.ViewInRow} view
     * @param {Object} eOpts
     */

    onBeforeRender: function (view, eOpts) {
        var vm = this.getViewModel();
        var record = view.getInitialConfig()._rowContext.record;
        vm.set("theAccount", record);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-accounts-card-edit',
            viewModel: {
                data: {
                    theAccount: this.getViewModel().get('theAccount')
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
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-emails-accounts-card-view',
            viewModel: {
                data: {
                    theAccount: this.getViewModel().get('theAccount')
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
        var callback = function (btnText) {
            if (btnText === "yes") {
                CMDBuildUI.util.Ajax.setActionId('delete-account');
                me.getViewModel().get('theAccount').erase({
                    success: function (record, operation) {
                        Ext.ComponentQuery.query('administration-content-emails-accounts-grid')[0].fireEventArgs('reload', [record, 'delete']);
                    },
                    failure: function () {
                        try {
                            me.getView()._rowContext.ownerGrid.getStore().reload();
                        } catch (error) {

                        }
                    }
                });
            }
        };

        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);

    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleDefaultBtnClick: function (button, e, eOpts) {
        var me = this;
        var view = this.getView();
        var vm = view.getViewModel();
        var theAccount = vm.get('theAccount');
        var theAccountId = theAccount.get('name');

        var ajaxconf = {
            url: Ext.String.format('{0}/system/config/_MANY', CMDBuildUI.util.Config.baseUrl),
            method: 'PUT',
            jsonData: {
                'org.cmdbuild.email.accountDefault': theAccount.get('default') ? null : theAccountId
            },
            success: function (transport) {
                view.up('grid').getStore().load({
                    callback: function (data, operation) {
                        view.up('administration-content-emails-accounts-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [view.up('grid'), this.findRecord('name', vm.get('theAccount').get('name')), this.findExact('name', vm.get('theAccount').get('name'))]);
                    }
                });
            }
        };

        /**
         * save configuration via custom ajax call
         */
        Ext.Ajax.request(ajaxconf);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var grid = Ext.ComponentQuery.query('administration-content-emails-accounts-grid')[0],
            vm = this.getViewModel(),
            theAccount = vm.get('theAccount') || grid.getInitialConfig()._rowContext.record;
        if (vm.get('toolAction._canUpdate')) {
            theAccount.set('active', !theAccount.get('active'));
            theAccount.save({
                success: function (record, operation) {
                    grid.getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [grid, record, this]);
                }
            });
        }
    }

});