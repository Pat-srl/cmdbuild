Ext.define('CMDBuildUI.view.administration.content.importexport.gatetemplates.card.tabitems.prperties.ViewMixin', {

    mixinId: 'administration-importexportgatemixin',

    onEditBtnClick: function () {
        var view = this.getView();
        var vm = view.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.getParent().set('enabledTab', 'properties');
    },

    onDeleteBtnClick: function (button) {
        var me = this;
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-importexportgate');
                    var theGate = vm.get('theGate');
                    var tag = theGate.getConfig().get('tag');
                    theGate.erase({
                        failure: function (error) {
                            theGate.reject();
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(tag === 'cad' ? 'gis' : tag, theGate.get('_id'));
                            me.redirectTo(nextUrl, true);
                        },
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getGateTemplateUrl(tag === 'cad' ? 'gis' : tag);
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
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theGate = vm.get('theGate');
        theGate.set('enabled', !theGate.get('enabled'));
        theGate.save();
        Ext.getStore('importexports.Gates').load();

    }
});