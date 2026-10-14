Ext.define('CMDBuildUI.view.administration.content.emails.queue.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-queue-grid',

    control: {
        '#': {
            beforerender: 'onBeforeRender',
            removed: 'onDestroy'
        }
    },
    privates: {
        _gridUpdateInterval: 15000,
        _task: null,
        _lastGridUpdate: null
    },
    onDestroy: function () {
        if (this._task) {
            this._task.stopped = true;
        }
    },
    /**
     * 
     * @param {CMDBuildUI.view.administration.content.emails.queue.Grid} view 
     */
    onBeforeRender: function (view) {
        var me = this;
        var vm = this.getViewModel();
        me._lastGridUpdate = new Date().valueOf();
        vm.set('storeConfig.autoLoad', true);
        CMDBuildUI.util.administration.helper.ConfigHelper.getConfig('org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled').then(function (configValue) {
            vm.set('queueEnabled', configValue === 'true');
        });
        this._task = Ext.TaskManager.start({
            run: function () {
                if (!vm.destroyed) {
                    var now = new Date().valueOf();
                    if (now - me._lastGridUpdate >= me._gridUpdateInterval - 500) {
                        var store = view.getStore();
                        if (store) {
                            store.reload();
                            me._lastGridUpdate = now;
                        }
                    }
                }
            },
            interval: me._gridUpdateInterval
        });
    },
    /**
     * Change queue config status
     * 
     * @param {Ext.form.field.Checkbox} checkbox 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     */
    onActiveStop: function (button, newValue, oldValue) {
        button.setDisabled(true);
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('email-queue-stop');
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs({
            'org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled': false
        }, null, null, this).then(function (success) {
            if (!vm.destroyed) {
                if (success && success.status === 200) {
                    vm.set('queueEnabled', false);
                    vm.getStore('gridDataStore').load();
                } else {
                    vm.set('queueEnabled', true);
                }
                button.setDisabled(false);
            }
        });
    },
    /**
     * Change queue config status
     * 
     * @param {Ext.form.field.Checkbox} checkbox 
     * @param {Boolean} newValue 
     * @param {Boolean} oldValue 
     */
    onActiveStart: function (button, newValue, oldValue) {
        button.setDisabled(true);
        var vm = button.lookupViewModel();
        CMDBuildUI.util.Ajax.setActionId('email-queue-start');
        CMDBuildUI.util.administration.helper.ConfigHelper.setConfigs({
            'org__DOT__cmdbuild__DOT__email__DOT__queue__DOT__enabled': true
        }, null, null, this).then(function (success) {
            if (!vm.destroyed) {
                if (success && success.status === 200) {
                    vm.set('queueEnabled', true);
                    vm.getStore('gridDataStore').load();
                } else {
                    vm.set('queueEnabled', false);
                }
                button.setDisabled(false);
            }
        });
    },

    /**
     * send mail
     * 
     * @param {CMDBuildUI.view.administration.content.emails.queue.Grid} grid 
     * @param {Number} rowIndex 
     * @param {Number} colIndex 
     * @param {Ext.panel.Tool} button 
     * @param {Ext.event.Event} event 
     * @param {CMDBuildUI.model.emails.Email} record 
     * @param {*} row 
     */
    onItemSendClick: function (grid, rowIndex, colIndex, button, event, record, row) {
        var me = this;
        record.set('_removing', true);
        CMDBuildUI.util.Ajax.setActionId('email-queue-send');
        Ext.Ajax.request({
            url: Ext.String.format('{0}/email/queue/outgoing/{1}/trigger', CMDBuildUI.util.Config.baseUrl, record.get('_id')),
            method: 'POST',
            success: function () {
                grid.getStore().reload();
                me._lastGridUpdate = new Date().valueOf();
            },
            failure: function () {
                CMDBuildUI.util.Logger.log(Ext.String.format("unable to send email #{0}", record.get('_id')), CMDBuildUI.util.Logger.levels.debug);
            }
        });
    }
});