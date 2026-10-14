Ext.define('CMDBuildUI.view.administration.content.setup.elements.EditLogConfigController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-setup-elements-editlogconfig',

    control: {
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        },
        '#addConfigRow': {
            click: 'onAddRowConfigBtnCLick'
        },
        '#bottomtoolbar': {
            show: 'onBottomToolbarShow'
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, event, eOpts) {
        button.setDisabled(true);
        CMDBuildUI.util.Utilities.showLoader(true);
        var view = this.getView(),
            vm = view.lookupViewModel(),
            store = view.getStore();
        var modifiedRecords = store.getModifiedRecords();
        var requests = 0;
        var onSuccess = function () {
            requests--;
            if (!view.destroyed) {
                if (requests <= 0) {
                    view.lookupViewModel().set(
                        'action',
                        CMDBuildUI.util.administration.helper.FormHelper.formActions.view
                    );
                    CMDBuildUI.util.Utilities.showLoader(false);
                    vm.set('disabledTabs.log', false);
                    vm.set('disabledTabs.retention', false);
                    vm.set('disabledTabs.audit', false);
                    vm.set('disabledTabs.governancedata', false);
                    store.reload();
                }
            }
        };
        var onError = function () {
            if (button && !button.destroyed) {
                button.setDisabled(false);
                CMDBuildUI.util.Utilities.showLoader(false);
            }
        };

        Ext.Array.forEach(modifiedRecords, function (item) {
            var key = item.get('category');
            var data = item.get('level');
            if (key) {
                requests++;
                CMDBuildUI.util.Ajax.setActionId('system.loggers.edit.post');
                Ext.Ajax.request({
                    url: Ext.String.format('{0}/system/loggers/{1}', CMDBuildUI.util.Config.baseUrl, key),
                    method: 'POST',
                    jsonData: data
                }).then(onSuccess, onError);
            }
        });
        if (!requests) {
            onSuccess();
        }
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, event, eOpts) {
        var view = this.getView(),
            store = view.getStore(),
            vm = view.lookupViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.view);
        vm.set('disabledTabs.log', false);
        vm.set('disabledTabs.retention', false);
        vm.set('disabledTabs.audit', false);
        vm.set('disabledTabs.governancedata', false);
        store.reload();
    },

    /**
     *
     * @param {Ext.button.Button} button
     * @param {Event} event
     * @param {Object} eOpts
     */
    onAddRowConfigBtnCLick: function (button, event, eOpts) {
        var view = this.getView(),
            vm = view.lookupViewModel(),
            store = view.getStore();
        store.add({
            category: '',
            content: '',
            level: 'ERROR'
        });

        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        new Ext.util.DelayedTask(function () {
            view.getView().getScrollable().scrollTo(0, Infinity, true);
            view.editingPlugin.startEdit(store.last(), 0);
        }).delay(100);
    },

    /**
     *
     * @param {Ext.grid.plugin.CellEditing} editor
     * @param {Object} context
     * @param {Object} eOpts
     */
    onBeforeCellEdit: function (editor, context, eOpts) {
        var vm = this.getViewModel();
        if (
            !vm.get('actions.view') &&
            ((context.record.phantom === true && context.column.dataIndex !== 'description') ||
                context.column.dataIndex === 'level')
        ) {
            return true;
        }
        return false;
    },

    /**
     *
     * @param {Ext.button.Button} btn
     * @param {Ext.event.Event} e
     */
    onViewLogsBtnClick: function (btn, e) {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.systemconfig.viewlogs,
            {
                xtype: 'administration-content-setup-elements-logviewer'
            },
            {},
            {
                ui: 'administration-actionpanel'
            }
        );
    },

    onDownloadLogsBtnClick: function () {
        CMDBuildUI.util.Utilities.openPopup(
            null,
            CMDBuildUI.locales.Locales.administration.systemconfig.downloadlogs,
            {
                xtype: 'administration-content-setup-elements-downloadlog'
            },
            {},
            {
                ui: 'administration-actionpanel'
            }
        );
    },

    onEditSetupBtnClick: function () {
        var vm = this.getViewModel();
        vm.set('action', CMDBuildUI.util.administration.helper.FormHelper.formActions.edit);
        vm.set('disabledTabs.log', false);
        vm.set('disabledTabs.retention', true);
        vm.set('disabledTabs.audit', true);
        vm.set('disabledTabs.governancedata', true);
    },

    onBottomToolbarShow: function (view) {
        view.down('#saveBtn').enable();
    }
});
