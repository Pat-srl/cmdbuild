Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.ReportController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-attachments-report',

    control: {
        '#reportCode_input': {
            change: 'onReportCodeInputChange'
        },
        '#removeNotificationTool': {
            click: 'onRemoveBtnClick'
        }
    },

    /**
     * 
     * @param {Ext.form.field.ComboBox} input 
     * @param {String} newValue 
     * @param {String} oldValue 
     */
    onReportCodeInputChange: function (input, newValue, oldValue) {
        var vm = this.getView().getViewModel();
        if ((!Ext.isEmpty(newValue) && !Ext.isEmpty(oldValue)) || !vm.get("paramsStore").getData().length) {
            this.setParamsForReport(vm, newValue);
        }
    },
    /**
     * 
     */
    onRemoveBtnClick: function () {
        this.getView().destroy();
    },
    /**
     *@private
     */
    privates: {
        /**
         * 
         * @param {Ext.app.ViewModel} viewmodel 
         * @param {String} reportCode 
         */
        setParamsForReport: function (vm, reportCode) {
            var sourceReport = Ext.getStore('reports.Reports').findRecord('code', reportCode);
            sourceReport.getAttributes().then(function (attributesStore) {
                var paramsStoreData = [];
                attributesStore.each(function (attribute) {
                    paramsStoreData.push({ key: attribute.get('_id'), value: '' });
                });
                vm.set('paramsStoreData', paramsStoreData);
            });
        }
    }

});