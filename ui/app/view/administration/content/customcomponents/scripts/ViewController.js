Ext.define('CMDBuildUI.view.administration.content.customcomponents.scripts.ViewController', {
    extend: 'Ext.app.ViewController',
    requires: ['CMDBuildUI.util.administration.File'],
    alias: 'controller.administration-content-customcomponents-scripts-view',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#searchtext': {
            beforerender: 'onSearchTextBeforeRender'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#editBtn': {
            click: 'onEditBtnClick'
        },
        '#addBtn': {
            beforerender: 'onAddBtnBeforeRender',
            click: 'onAddBtnClick'
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
     * Before render
     * @param {CMDBuildUI.view.administration.content.customcomponents.scripts.View} view
     */
    onBeforeRender: function (view) {
        var title = Ext.String.format('{0} - {1}',CMDBuildUI.locales.Locales.administration.customcomponents.plural, CMDBuildUI.locales.Locales.administration.customcomponents.strings.script);
        view.up('administration-content').getViewModel().set('title', title);
    },
    /**
     * Before render search textfield
     * @param {Ext.form.field.Text} input
     */
    onSearchTextBeforeRender: function (input) {
        var vm = input.lookupViewModel();
        vm.set('componentTypeName', CMDBuildUI.locales.Locales.administration.customcomponents.strings.script);
        input.setEmptyText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.searchscripts);
    },
    /**
     * Before render add button
     * @param {Ext.button.Button} button
     */
    onAddBtnBeforeRender: function (button) {        
        button.setText(CMDBuildUI.locales.Locales.administration.customcomponents.strings.addscript);
    },
    /**
     * On add customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddBtnClick: function (button, e, eOpts) {        
        this.redirectTo('administration/customcomponents_empty/script/true', true);
    },

    /**
     * On delete customcomponent button click
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
            function (btnText) {
                if (btnText === "yes") {
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);

                    CMDBuildUI.util.Ajax.setActionId('delete-customcomponent');
                    me.getViewModel().get('theCustomcomponent').erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl(vm.get('componentType'));
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        },
                        callback: function (record, reason) {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        }
                    });
                }
            }, this);
    },

    /**
     * On edit customcomponent button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
    },

    /**
     * On cancel button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        var vm = this.getView().getViewModel();
        if (vm.get('actions.add')) {
            var nextUrl = Ext.String.format('administration/customcomponents_empty/{0}/false', vm.get('componentType'));
            this.redirectTo(nextUrl, true);
            var store = Ext.getStore('administration.MenuAdministration');
            var vmNavigation = Ext.getCmp('administrationNavigationTree').getViewModel();
            var currentNode = store.findNode("objecttype", CMDBuildUI.model.administration.MenuItem.types.customcomponent);
            vmNavigation.set('selected', currentNode);
        } else {
            this.redirectTo(Ext.History.getToken(), true);
        }
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theCustomcomponent = vm.get('theCustomcomponent');
        theCustomcomponent.set('active', !theCustomcomponent.get('active'));
        theCustomcomponent.save();
    },

    /**
     * On save button click
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        button.setDisabled(true);
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        var me = this;
        var form = this.getView().getForm();
        var vm = this.getView().getViewModel();

        if (form.isValid()) {
            var afterSave = function (record) {
                var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getCustomComponentUrl(vm.get('componentType'), record.get('_id'));
                if (vm.get('actions.edit')) {
                    var newDescription = record.get('description');
                    CMDBuildUI.util.administration.MenuStoreBuilder.changeRecordBy('href', nextUrl, newDescription, me);
                    CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                } else {
                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                            if (button.el && button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
                        });
                }
            };
            vm.get('theCustomcomponent').save({
                success: function (record, operation) {
                    afterSave(record);
                },
                failure: function (record, reason) {

                    button.setDisabled(false);
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);

                }
            });
        } else {
            button.setDisabled(false);
            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false]);
        }

    }
});