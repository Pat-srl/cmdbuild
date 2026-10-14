Ext.define('CMDBuildUI.view.administration.components.geoattributes.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-geoattributes-card-viewinrow',

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
     *
     * @param {CMDBuildUI.components.tab.FormPanel} view
     * @param {Object} eOpts
     */
    onBeforeRender: function (view, eOpts) {
        const vm = view.getViewModel();

        if (view && view._rowContext) {
            const record = view._rowContext.record;
            vm.set('theGeoAttribute', record);

            if (record.get('type') === CMDBuildUI.model.map.GeoAttribute.type.geometry) {
                view.child('#typepropertiestab').tab.show();
                view.child('#infowindowtab').tab.show();
            }
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onEditBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                data: {
                    theGeoAttribute: vm.get('theGeoAttribute').clone(),
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    tabpanel: view.up('tabpanel'),
                    geoattributesStore: vm.get('geoattributesStore')
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCloneBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                data: {
                    theGeoAttribute: vm.get('theGeoAttribute').clone(true),
                    actions: {
                        view: false,
                        edit: false,
                        add: true
                    },
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName'),
                    tabpanel: view.up('tabpanel')
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onOpenBtnClick: function (button, event, eOpts) {
        const view = this.getView();
        const vm = view.getViewModel();
        const container =
            Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) ||
            Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();

        container.add({
            xtype: 'administration-components-geoattributes-card-form',
            viewModel: {
                data: {
                    theGeoAttribute: vm.get('theGeoAttribute'),
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    objectType: vm.get('objectType'),
                    objectTypeName: vm.get('objectTypeName')
                }
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onDeleteBtnClick: function (button, event, eOpts) {
        const me = this;
        const view = me.getView();
        const vm = me.getViewModel();

        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText.toLowerCase() === 'yes') {
                    CMDBuildUI.util.Utilities.showLoader(true, view);
                    CMDBuildUI.util.Ajax.setActionId('delete-geoattribute');
                    const store = view._rowContext.ownerGrid.getStore();

                    vm.get('theGeoAttribute').erase({
                        success: function (record, operation) {
                            CMDBuildUI.util.Utilities.showLoader(false, view);
                            CMDBuildUI.util.Navigation.removeAdministrationDetailsWindow();
                            store.load();
                        },
                        failure: function () {
                            CMDBuildUI.util.Utilities.showLoader(false, view);
                            store.load();
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
    onToggleActiveBtnClick: function (button, e, eOpts) {
        const view = this.getView();
        const grid = view.up('grid');
        const vm = view.getViewModel();
        const theGeoAttribute = vm.get('theGeoAttribute').clone();

        theGeoAttribute.set('active', !theGeoAttribute.get('active'));
        theGeoAttribute.set('style', theGeoAttribute.getAssociatedData().style);
        delete theGeoAttribute.data.style.id;

        theGeoAttribute.save({
            success: function (record, operation) {
                view.up('administration-components-geoattributes-grid')
                    .getPlugin('administration-forminrowwidget')
                    .view.fireEventArgs('itemupdated', [grid, record, this]);
            },
            failure: function (record, reason) {
                theGeoAttribute.reject();
                view.up('administration-components-geoattributes-grid')
                    .getPlugin('administration-forminrowwidget')
                    .view.fireEventArgs('togglerow', [grid, record, vm.get('recordIndex')]);
                view.up('administration-components-geoattributes-grid')
                    .getPlugin('administration-forminrowwidget')
                    .view.fireEventArgs('togglerow', [grid, record, vm.get('recordIndex')]);
            }
        });
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onTranslateClick: function (button, event, eOpts) {
        const me = this;
        const vm = me.getViewModel();
        const translationCode =
            CMDBuildUI.util.administration.helper.LocalizationHelper.getLocaleKeyOfGisAttributeClass(
                vm.get('theObject.name'),
                vm.get('theGeoAttribute.name')
            );
        CMDBuildUI.util.administration.helper.FormHelper.openLocalizationPopup(
            translationCode,
            vm.get('action'),
            'theDescriptionTranslation',
            vm
        );
    }
});
