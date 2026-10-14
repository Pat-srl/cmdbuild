Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.type.PropertiesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-lookuptypes-tabitems-type-properties',

    control: {
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#deleteBtn': {
            click: 'onDeleteBtnClick'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        button.setDisabled();
        const me = this;
        const vm = me.getViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (action) {
                if (action === "yes") {
                    const theObject = vm.get('theLookupType');
                    CMDBuildUI.util.Ajax.setActionId('delete-lookuptype');

                    theObject.erase({
                        success: function (record, operation) {
                            const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl();
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
        const me = this;
        const vm = me.getViewModel();
        const theLookupType = vm.get('theLookupType');

        Ext.GlobalEvents.fireEventArgs("showadministrationcontentmask", [true]);
        button.setDisabled(true);

        if (theLookupType.isValid()) {
            theLookupType.save({
                success: function (record, operation) {
                    theLookupType.commit();
                    const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl(record.getId());

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

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        const me = this;
        const nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheLookupTypeUrl();
        button.setDisabled(true);
        CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
    }
});