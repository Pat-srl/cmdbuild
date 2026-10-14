Ext.define('CMDBuildUI.view.administration.content.views.card.ViewInRowController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-views-card-viewinrow',

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
            url: Ext.String.format('/{0}/{1}/views', vm.get('objectType'), vm.get('theViewFilter.owner_type')),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-content-views-card-form',
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
        CMDBuildUI.util.Logger.log("not implemented", CMDBuildUI.util.Logger.levels.log);
    },

    onOpenBtnClick: function (button, event, eOpts) {
        var view = this.getView();
        var vm = view.getViewModel();
        var container = Ext.getCmp(CMDBuildUI.view.administration.DetailsWindow.elementId) || Ext.create(CMDBuildUI.view.administration.DetailsWindow);
        container.removeAll();
        CMDBuildUI.model.map.GeoAttribute.setProxy({
            url: Ext.String.format('/{0}/{1}/views', vm.get('objectType'), vm.get('theViewFilter.owner_type')),
            type: 'baseproxy'
        });

        container.add({
            xtype: 'administration-content-views-card-form',
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
        CMDBuildUI.util.Logger.log("not implemented", CMDBuildUI.util.Logger.levels.log);
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
                view.up('administration-content-views-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('itemupdated', [null, record, this]);
            },
            failure: function (record, reason) {
                record.reject();
                view.up('administration-content-views-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
                view.up('administration-content-views-grid').getPlugin('administration-forminrowwidget').view.fireEventArgs('togglerow', [null, record, vm.get('recordIndex')]);
            }
        });

    }

});