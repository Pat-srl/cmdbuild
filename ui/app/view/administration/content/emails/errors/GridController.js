Ext.define('CMDBuildUI.view.administration.content.emails.errors.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-errors-grid',

    control: {
        '#refreshBtn': {
            click: 'onRefreshBtnClick'

        }
    },

    /**
     * 
     * @param {Ext.button.Button} button 
     */
    onRefreshBtnClick: function (button) {
        var view = this.getView();
        var store = view.getStore();
        if (store) {
            store.reload();
        }
    },
    /**
     * send mail
     * 
     * @param {CMDBuildUI.view.administration.content.emails.errors.Grid} grid 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Ext.panel.Tool} button 
     * @param {Ext.event.Event} event 
     * @param {CMDBuildUI.model.emails.Email} record 
     * @param {*} row 
     */
    onItemEditClick: function (grid, rowIndex, colIndex, button, event, record, row) {
        var me = this,
            vm = this.getViewModel(),
            objectdata = {};

        record.getProxy().setUrl(vm.get("gridDataStore").getProxy().getUrl());

        CMDBuildUI.util.Utilities.openPopup(
            'popup-edit-email',
            CMDBuildUI.locales.Locales.emails.edit,
            {
                xtype: 'administration-content-emails-errors-edit',
                viewModel: {
                    links: {
                        theEmail: {
                            type: 'CMDBuildUI.model.emails.Email',
                            id: record.getId()
                        }
                    },
                    data: {
                        objectdata: objectdata
                    }
                },
                listeners: {
                    saveandsend: function () {
                        var vm = this.lookupViewModel();
                        me.onItemSendClick(grid, rowIndex, colIndex, button, event, vm.get('theEmail'), row);
                    }
                }
            });
    },

    /**
     * send mail
     * 
     * @param {CMDBuildUI.view.administration.content.emails.errors.Grid} grid 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Ext.panel.Tool} button 
     * @param {Ext.event.Event} event 
     * @param {CMDBuildUI.model.emails.Email} record 
     * @param {*} row 
     */
    onItemSendClick: function (grid, rowIndex, colIndex, button, event, record, row) {
        var me = this;
        CMDBuildUI.util.Ajax.setActionId('email-errors-send');
        record.getProxy().setUrl(grid.getStore().getProxy().getUrl());
        record.save({
            success: function () {
                grid.getStore().reload();
                me._lastGridUpdate = new Date().valueOf();
            },
            failure: function () {
                CMDBuildUI.util.Logger.log(Ext.String.format("unable to send email #{0}", record.get('_id')), CMDBuildUI.util.Logger.levels.debug);
            }
        });
    },

    /**
     * send mail
     * 
     * @param {CMDBuildUI.view.administration.content.emails.errors.Grid} grid 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Ext.panel.Tool} button 
     * @param {Ext.event.Event} event 
     * @param {CMDBuildUI.model.emails.Email} record 
     * @param {*} row 
     */
    onItemDeleteClick: function (grid, rowIndex, colIndex, button, event, record, row) {
        var callback = function (btnText) {
            if (btnText === "yes") {
                var me = this;
                record.set('_removing', true);
                CMDBuildUI.util.Ajax.setActionId('email-errors-delete');
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/email/error/{1}', CMDBuildUI.util.Config.baseUrl, record.get('_id')),
                    method: 'DELETE',
                    success: function () {
                        grid.getStore().reload();
                        me._lastGridUpdate = new Date().valueOf();
                    },
                    failure: function () {
                        CMDBuildUI.util.Logger.log(Ext.String.format("unable to delete email #{0}", record.get('_id')), CMDBuildUI.util.Logger.levels.debug);
                    }
                });
            }
        };
        CMDBuildUI.util.administration.helper.ConfirmMessageHelper.showDeleteItemMessage(null, null, callback, this);
    }
});