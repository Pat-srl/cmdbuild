Ext.define('CMDBuildUI.view.thematisms.thematism.RulesController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.thematisms-thematism-rules',
    listen: {
        component: {
            '#': {
                beforerender: 'onBeforeRender'
            },
            'grid': {
                cellclick: 'onCellClick'
            }
        }
    },

    onBeforeRender: function (view, eOpts) {
        var me = this;
        var vm = me.getViewModel();

        if (view.config.needListener) {
            view.down('#calculaterules').show();

            //add event handler on 'update'
            vm.bind('{legendstore}', function (store) {
                store.addListener('update', me.onUpdate, me);
            })

            //The {legenddata} view model variable is recalculated when the rules() store of thematis fires datachanged event
            vm.bind('{theThematism}', function (thematism) {
                view.mon(thematism.rules(), 'datachanged', view.setrules, me);

                //initialization
                //could have call directly me.setRules() instead of firing the event;
                thematism.rules().fireEvent('datachanged');
            }, this);
        }

    },

    onCellClick: function (grid, td, cellIndex, record, tr, rowIndex, e, eOpts) {
        var view = this.getView();
        if (view.config.needListener) {
            view.expandColorPicker(tr)
            view.setColorPickerRecord(record);
        }
    },

    /**
     * NOTE: This functin is responsable for changing thematism rules color by changing legend color
     * 'update' store event handler
     * @param {*} legendstore 
     * @param {*} record 
     * @param {*} operation 
     * @param {*} modifiedFieldNames 
     * @param {*} details 
     * @param {*} eOpts 
     */
    onUpdate: function (legendstore, record, operation, modifiedFieldNames, details, eOpts) {
        var thematismRule = record.get('referenceRule');
        var newColor = record.get('color');

        if (thematismRule) { //updates the color of an existing rule
            thematismRule.set('style', {
                color: newColor
            })
        } else { //creates a new rule
            var thematism = this.getViewModel().get('theThematism');
            var newRule = thematism.createRule({
                color: newColor,
                value: [record.get('value')]
            });

            record.set('referenceRule', newRule);
            thematism.rules().insert(0, newRule);
        }
    }
});
