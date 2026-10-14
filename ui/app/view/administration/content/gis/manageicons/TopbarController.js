Ext.define('CMDBuildUI.view.administration.content.gis.TopbarController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-topbar',

    control: {
        '#addicon': {
            click: 'onNewIconBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.menu.Item} item
     * @param {Ext.event.Event} event
     * @param {Object} eOpts
     */
    onNewIconBtnClick: function (item, event, eOpts) {
        var icon = Ext.create("CMDBuildUI.model.icons.Icon");
        var content = {
            xtype: 'administration-content-gis-icon-create',
            scrollable: 'y',
            viewModel: {
                data: {
                    theIcon: icon
                }
            }
        };
        // custom panel listeners
        var listeners = {};

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.gis.newicon,
            content,
            listeners, {
            ui: 'administration-actionpanel',
            width: '50%',
            height: '50%'
        }
        );
    },

    /**
     * @param {Ext.form.field.Base} field
     * @param {Ext.event.Event} event
     */
    onKeyUp: function (field, event) {
        // get vm value
        var vm = this.getViewModel();
        var searchTerm = vm.getData().search.value;
        var store = vm.get('icons');
        if (searchTerm) {
            CMDBuildUI.util.administration.helper.GridHelper.localSearchFilter(store, searchTerm);
        }
    },

    /**
     * @param {Ext.form.field.Text} field
     * @param {Ext.form.trigger.Trigger} trigger
     * @param {Object} eOpts
     */
    onSearchClear: function (field, trigger, eOpts) {
        var vm = this.getViewModel();
        // clear store filter

        var store = vm.get('icons');
        if (store) {
            CMDBuildUI.util.administration.helper.GridHelper.removeLocalSearchFilter(store);
        }

        // reset input
        field.reset();
    }

});