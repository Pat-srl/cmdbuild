Ext.define('CMDBuildUI.view.administration.content.lookuptypes.tabitems.values.card.ToolsMixin', {
    mixinId: 'administration-lookupvalue-tools-mixin',

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} e
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        const theValue = vm.get('theValue');
        const theLookupType = vm.get('theLookupType');
        const toolAction = vm.get('toolAction');
        const parentLookupsStore = vm.get('parentLookupsStore');
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card',
            viewModel: {
                data: {
                    theValue: theValue,
                    actions: {
                        edit: true,
                        add: false,
                        view: false
                    },
                    theLookupType: theLookupType,
                    toolAction: toolAction,
                    parentLookupsStore: parentLookupsStore
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} e
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        const detailsWindow = this.getView().up('#CMDBuildAdministrationDetailsWindow');

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-lookupvalue-card');
                    const theValue = vm.get('theValue');
                    const icon_font = theValue.get('icon_font');

                    theValue.erase({
                        failure: function () {
                            theValue.reject();
                            theValue.set('icon_font', icon_font);
                        },
                        success: function (record, operation) {
                            if (detailsWindow) {
                                detailsWindow.fireEvent("closed");
                            }
                        }
                    });
                }
            });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} e
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, e, eOpts) {
        const vm = this.getViewModel();
        const theValue = vm.get('theValue');
        const theLookupType = vm.get('theLookupType');
        const toolAction = vm.get('toolAction');
        const parentLookupsStore = vm.get('parentLookupsStore');
        const container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        container.add({
            xtype: 'administration-content-lookuptypes-tabitems-values-card',
            viewModel: {
                data: {
                    theValue: theValue,
                    actions: {
                        edit: false,
                        add: false,
                        view: true
                    },
                    theLookupType: theLookupType,
                    toolAction: toolAction,
                    parentLookupsStore: parentLookupsStore
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Object} e
     * @param {Object} eOpts
     */
    onActiveToggleBtnClick: function (button, e, eOpts) {
        const theValue = this.getView().getViewModel().get('theValue');

        CMDBuildUI.util.Ajax.setActionId('toggleactive-lookupvalue');
        theValue.set('active', !theValue.get('active'));
        theValue.save();
    }
});