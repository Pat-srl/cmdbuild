Ext.define('CMDBuildUI.view.administration.components.viewfilters.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-viewfilters-card-viewinrow',

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

    onBeforeRender: function (view) {
        var vm = view.getViewModel();
        if (view && view._rowContext) {
            vm.set('theViewFilter', view._rowContext.record);
        }
    },

    onEditBtnClick: function (button, event, eOpts) {

        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.searchfilters.Searchfilter.setProxy({
            url: Ext.String.format('/{0}/{1}/viewfilters', vm.get('objectType'), vm.get('theViewFilter.owner_type')),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-viewfilters-card-form',
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theViewFilter.name')
                    }
                },

                data: {
                    actions: {
                        view: false,
                        edit: true,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },


    onCloneBtnClick: function (button, event, eOpts) {
        // unused
    },

    onOpenBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: Ext.String.format('/{0}/{1}/viewfilters', vm.get('objectType'), vm.get('theViewFilter.owner_type')),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-components-viewfilters-card-form',
            viewModel: {
                links: {
                    theViewFilter: {
                        type: 'CMDBuildUI.model.map.GeoAttribute',
                        id: vm.get('theViewFilter.name')
                    }
                },

                data: {
                    actions: {
                        view: true,
                        edit: false,
                        add: false
                    },
                    grid: this.getView().up()
                }
            }
        });
    },

    onDeleteBtnClick: function (button, event, eOpts) {
        var me = this;
        var vm = button.lookupViewModel();
        var theViewFilter = vm.get('theViewFilter');
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.common.messages.attention,
            CMDBuildUI.locales.Locales.administration.common.messages.areyousuredeleteitem,
            function (btnText) {
                if (btnText === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('delete-serachfilter');
                    theViewFilter.erase({
                        success: function (record, operation) {
                            var nextUrl = CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl();
                            CMDBuildUI.util.administration.MenuStoreBuilder.removeRecordBy('href', CMDBuildUI.util.administration.helper.ApiHelper.client.getTheViewFilterUrl(record.get('name')));
                            CMDBuildUI.util.administration.MenuStoreBuilder.selectAndRedirectToRecordBy('href', nextUrl, me);
                        }
                    });
                }
            }, me);
    },
    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onToggleActiveBtnClick: function (button, e, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var theViewFilter = vm.get('theViewFilter');
        theViewFilter.set('active', !theViewFilter.get('active'));

        theViewFilter.save({
            success: function (record, operation) {
                view.up('administration-components-viewfilters-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [null, record, this]);
            },
            failure: function (record, reason) {
                record.reject();
                view.up('administration-components-viewfilters-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
                view.up('administration-components-viewfilters-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
            }
        });

    }

});