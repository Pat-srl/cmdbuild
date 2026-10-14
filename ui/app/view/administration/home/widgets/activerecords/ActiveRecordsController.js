Ext.define('CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecordsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-home-widgets-activerecords-activerecords',
    control: {
        '#': {
            afterrender: 'onAfterRender'
        }
    },

    /**
     * 
     * @param {CMDBuildUI.view.administration.home.widgets.activerecords.ActiveRecords} view 
     */   
    onAfterRender: function (view) {
        var vm = this.getViewModel();
        vm.bind({
            bindTo: '{showLoader}'
        }, function (showLoader) {
            CMDBuildUI.util.Utilities.showLoader(showLoader, view);
        });
    },
    
    /**
     * 
     * @param {*} tooltip 
     * @param {*} record 
     * @param {*} item 
     */
    onDataStatsTooltipRender: function (tooltip, record, item) {
        var title = item.series.getTitle();

        var value = CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(record.get(item.series.getYField()), {
            showThousandsSeparator: true
        });

        tooltip.setHtml(Ext.String.format(CMDBuildUI.locales.Locales.administration.home.itemsatdate, value, title, record.get('year')));
    },

    /**
     * 
     * @param {*} axis 
     * @param {*} label 
     * @param {*} layoutContext 
     */
    onDataStatsLabelRender: function (axis, label, layoutContext) {
        if (!isNaN(label) && parseFloat(label) < 1) {
            return CMDBuildUI.util.helper.FieldsHelper.renderDoubleField(label, {
                showThousandsSeparator: true,
                visibleDecimals: 2
            });
        }
        return CMDBuildUI.util.helper.FieldsHelper.renderIntegerField(label, {
            showThousandsSeparator: true
        });
    }

});