Ext.define('CMDBuildUI.view.administration.content.gis.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-gis-grid',
    control: {
        'tableview': {
            actionedit: 'onActionEdit',
            actiondelete: 'onActionDelete'
        }
    },


    onActionEdit: function (grid, record, rowIndex, colIndex) {
        var vm = this.getViewModel();
        var content = {
            xtype: 'administration-content-gis-icon-edit',
            viewModel: {            
                links: {
                    theIcon: {
                        reference: 'CMDBuildUI.model.icons.Icon',
                        id: record.getId()
                    }
                }
            }
        };

        var popUp = CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.gis.editicon,
            content, {}, {
                ui: 'administration-actionpanel',
                width: '50%',
                height: '50%'
            }
        );

    },

    onActionDelete: function (grid, record, rowIndex, colIndex) {
        CMDBuildUI.util.Msg.confirm(
            CMDBuildUI.locales.Locales.administration.gis.deleteicon,
            CMDBuildUI.locales.Locales.administration.gis.deleteicon_confirmation,
            function (action) {
                if (action === "yes") {
                    CMDBuildUI.util.Ajax.setActionId('icons.delete');
                    record.erase();
                }
            }
        );
    }

});