Ext.define('CMDBuildUI.view.administration.components.splitstring.GridController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-components-splitstring-grid',

    control: {
        'tableview': {
            actiondelete: 'onActionDelete'
        }
    },

    /**
     * @param {CMDBuildUI.view.administration.components.keyvaluegrid.Grid} grid
     * @param {Ext.data.Model} record
     * @param {Number} rowIndex
     * @param {Number} colIndex
     * 
     */
    onActionDelete: function (grid, record, rowIndex, colIndex) {
        var splitStringStore = this.getStore('splitStringStore');
        splitStringStore.remove(record);
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onAddRowBtnClick: function (button, e, eOpts) {

        if (!this.getStore('splitStringStore')) {
            this.getViewModel().setStores({
                splitStringStore: {
                    model: 'CMDBuildUI.model.base.SplitString',
                    proxy: 'memory',
                    autoDestroy: true
                }
            });
        }

        var splitStringStore = this.getStore('splitStringStore');
        splitStringStore.add(CMDBuildUI.model.base.SplitString.create());
    },

    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onOkBtnClick: function (button, e, eOpts) {
        
        var theDivisor = button.lookupViewModel().get('theDivisor'); 
        var stringStore = this.getView().getStore().getRange();
        var stringResult = '';
    
        var myArray = [];
        stringStore.forEach(function (item) {
            if (item.isValid()) {
                myArray.push(item.get('substring'));
            }        
        });
        stringResult = myArray.join(theDivisor);
        this.getView().up('#administration-content-localizations-imports-view').fireEventArgs('returnString', [stringResult]);
    },


    /**
     * @param {Ext.button.Button} button
     * @param {Event} e
     * @param {Object} eOpts
     */
    onCancelBtnClick: function (button, e, eOpts) {
        this.getView().up('#administration-content-localizations-imports-view').fireEventArgs('close', []);
    }

});