Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.CreateController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-accounts-card-create',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#testBtn': {
            click: 'onTestBtnClick'
        }
    },


    /**
     * @param {CMDBuildUI.view.administration.content.emails.templates.card.CreateController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        view.up('administration-detailswindow').getViewModel().set('title', CMDBuildUI.locales.Locales.administration.emails.newaccount);
        var vm = this.getViewModel();
        if (!vm.get('theAccount') || !vm.get('theAccount').phantom) {
            vm.linkTo("theAccount", {
                type: 'CMDBuildUI.model.emails.Account',
                create: true
            });
        }
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var vm = this.getViewModel();
        var form = this.getView();
        if (form.isValid()) {
            var theAccount = vm.get('theAccount');
            theAccount.save({
                success: function (record, operation) {
                    var w = Ext.create('Ext.window.Toast', {
                        ui: 'administration',
                        title: CMDBuildUI.locales.Locales.administration.common.messages.success,
                        html: CMDBuildUI.locales.Locales.administration.emails.accountsavedcorrectly,
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('check-circle', 'solid'),
                        align: 'br'
                    });
                    w.show();

                    Ext.GlobalEvents.fireEventArgs("accountcreated", [record]);
                    var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
                    container.fireEvent('closed');
                },
                callback: function () {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                }
            });
        }
    },
    onGenerateTokenSchemaClick: function (textarea, event, eOpts) {
        var vm = this.getViewModel();
        var schema = '{\n\t"clientId": "",\n\t"tenantId": "",\n\t"clientSecret": ""\n}';
        vm.set('theAccount.password', schema);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var popup = this.getView().up("panel");
        popup.close();
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onTestBtnClick: function (button, e, eOpts) {
        var vm = button.lookupViewModel();
        var theAccount = vm.get('theAccount');
        theAccount.test().then(function (success) {
            if (success) {
                CMDBuildUI.util.Notifier.showSuccessMessage(CMDBuildUI.locales.Locales.administration.emails.configurationsuccesful, null, 'administration');
            }
        });
    }

});