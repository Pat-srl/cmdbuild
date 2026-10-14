Ext.define('CMDBuildUI.view.administration.content.dms.dmscategorytypes.tabitems.type.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-dms-dmscategorytypes-tabitems-type-properties',
    require: [
        'CMDBuildUI.util.administration.helper.FormHelper'
    ],

    control: {
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onDeleteBtnClick: function (button) {
        button.setDisabled();
        var me = this;
        var vm = me.getViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    var theObject = vm.get('theDMSCategoryType');
                    CMDBuildUI.util.Ajax.setActionId('delete-DMSCategory');

                    theObject.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', Ext.util.History.getToken(), nextUrl, me);
                        }
                    });
                }
            }
        );
    },



    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var me = this;
        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        button.setDisabled(true);
        var vm = me.getViewModel();
        var theDMSCategoryType = vm.get('theDMSCategoryType');
        if (!theDMSCategoryType.isValid()) {
            var validatorResult = theDMSCategoryType.validate();
            var errors = validatorResult.items;
            for (var i = 0; i < errors.length; i++) {
                try {
                    CMDBuildUI.util.Logger.log(Ext.String.format("{0} field is invalid", errors[i]), CMDBuildUI.util.Logger.levels.debug);
                } catch (error) {

                }
            }
        } else {

            theDMSCategoryType.save({
                success: function (record, operation) {
                    theDMSCategoryType.commit();
                    var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl(record.getId());

                    CMDBuildUI.util.administration.MenuStoreBuilder.initialize(
                        function () {
                            if (button.el.dom) {
                                button.setDisabled(false);
                            }
                            Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        });

                },
                failure: function (record, operation, success) {
                    if (button.el.dom) {
                        button.setDisabled(false);
                    }
                    Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [false, true]);
                }
            });
        }
    },

    onCancelBtnClick: function (button, e, eOpts) {
        var me = this;
        button.setDisabled(true);
        var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getDmsCategoryUrl();
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    }
});