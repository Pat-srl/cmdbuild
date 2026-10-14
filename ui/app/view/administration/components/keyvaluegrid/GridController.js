Ext.define('CMDBuildUI.view.administration.components.keyvaluegrid.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-keyvaluegrid-grid',

    control: {
        'tableview': {
            actiondelete: 'onActionDelete'
        },
        '#saveBtn': {
            click: 'onSaveBtnClick'
        },
        '#cancelBtn': {
            click: 'onCancelBtnClick'
        }
    },

    onBeforeEditCell: function(editor, context, eOpts){
        return !this.getViewModel().get('actions.view');
    },
    /**
     * @param {CMDBuildUI.view.administration.components.keyvaluegrid.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDelete: function (grid, record, rowIndex, colIndex) {
        var keyvaluedataStore = this.getStore('keyvaluedataStore');
        keyvaluedataStore.remove(record);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddRowBtnClick: function (button, e, eOpts) {

        if (!this.getStore('keyvaluedataStore')) {
            this.getViewModel().setStores({
                keyvaluedataStore: {
                    model: 'CMDBuildUI.model.base.KeyValue',
                    proxy: 'memory',
                    autoDestroy: true
                }
            });
        }

        var keyvaluedataStore = this.getStore('keyvaluedataStore');
        keyvaluedataStore.add(CMDBuildUI.model.base.KeyValue.create());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onSaveBtnClick: function (button, e, eOpts) {
        var vm = this.getViewModel();
        var emptykey = false;
        var w = Ext.create('Ext.window.Toast', {
            title: CMDBuildUI.locales.Locales.administration.common.messages.error,
            html: CMDBuildUI.locales.Locales.administration.emails.notnullkey,
            iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('times-circle', 'solid'),
            align: 'br'
        });

        var storeditems = this.getView().getStore().getRange();
        var theOwnerObject = vm.get('theOwnerObject');
        var newKeyvaluedata = {};
        storeditems.forEach(function (item) {
            newKeyvaluedata[item.data.key] = item.data.value;
            if (!item.data.key.length) {
                emptykey = true;
                w.show();
            }
        });
        if (!emptykey) {
            theOwnerObject.set(vm.get('theOwnerObjectKey'), newKeyvaluedata);
            this.getView().up('panel').close();
        }
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up().close();
    }
});