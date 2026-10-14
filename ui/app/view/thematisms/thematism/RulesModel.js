Ext.define('CMDBuildUI.view.thematisms.thematism.RulesModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.thematisms-thematism-rules',

    formulas: {
        optionsDisabled: {
            bind: '{legenddata}',
            get: function (data) {
                if (data.length > 0) {
                    if (this.get("theThematism.analysistype") == CMDBuildUI.model.thematisms.Thematism.analysistypes.intervals) {
                        this.set('options.hidden', false);
                        this.set('options.disabled', data.length > 2 ? false : true);
                    } else {
                        this.set('options.hidden', true);
                    }
                } else {
                    this.set('options.hidden', true);
                }
            }
        }
    },

    stores: {
        legendstore: {
            model: 'CMDBuildUI.model.thematisms.LegendModel',
            proxy: {
                type: 'memory'
            },
            data: '{legenddata}',
            sorters: [{
                sorterFn: function (a, b) {
                    if (Ext.isArray(a.get("value")) && Ext.isArray(b.get("value"))) {
                        var array1 = a.get("value").map(Number),
                            array2 = b.get("value").map(Number);
                        return array1[0] < array2[0] ? -1 : array1[0] > array2[0] ? 1 : array1[1] <= array2[1] ? -1 : 1;
                    } else {
                        return a.get("viewValue") < b.get("viewValue") ? -1 : 1;
                    }
                }
            }]
            //update listener defined in viewController
        }
    }
});
