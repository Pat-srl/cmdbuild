Ext.define('CMDBuildUI.view.administration.content.emails.accounts.card.EditController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-accounts-card-edit',

    control: {
        '#': {
            beforeRender: 'onBeforeRender'
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
     * @param {CMDBuildUI.view.administration.content.emails.accounts.card.EditController} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        var title = view.getViewModel().get('theAccount').get('name');
        view.up('administration-detailswindow').getViewModel().set('title', title);
        var vm = this.getViewModel();
        if (!vm.get('theAccount').phantom) {
            vm.linkTo("theAccount", {
                type: 'CMDBuildUI.model.emails.Account',
                id: vm.get('theAccount').get('_id')
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
                    Ext.GlobalEvents.fireEventArgs("accountupdated", [record]);
                    form.up().fireEvent("closed");
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
        var vm = this.getViewModel();
        vm.get("theAccount").reject(); // discard changes
        this.getView().up().fireEvent("closed");
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