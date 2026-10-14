Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.ReportsController', {
    extend: 'Ext.app.ViewController',
    alias: 'controller.administration-content-emails-templates-card-attachments-reports',

    control: {
        '#': {
            beforerender: 'onBeforeRender'
        },
        '#addreport': {
            click: 'onAddReportBtnClick'
        }
    },

    /**
     * 
     * @param {MDBuildUI.view.administration.content.emails.templates.card.attachments.Reports} view 
     */
    onBeforeRender: function (view) {
        var vm = view.lookupViewModel();
        vm.bind({
            bindTo: {
                reportsStore: '{theTemplate.reports}'
            },
            single: true
        }, function (data) {
            if (data && data.reportsStore && data.reportsStore.length) {
                data.reportsStore.forEach(function (report) {
                    var sourceReport = Ext.getStore('reports.Reports').findRecord('code', report.code);
                    if (sourceReport) {
                        var paramsStoreData = [];
                        sourceReport.getAttributes().then(function (attributesStore) {
                            attributesStore.each(function (attribute) {
                                var param = Ext.String.format('params___{0}', attribute.get('_id'));
                                var exisistingParam = report.hasOwnProperty(param);
                                paramsStoreData.push({ key: attribute.get('_id'), value: exisistingParam ? report[param] : '' });
                            });
                            view.add({
                                columnWidth: 1,
                                layout: 'column',
                                xtype: 'administration-content-emails-templates-card-attachments-report',
                                viewModel: {
                                    data: {
                                        report: report,
                                        paramsStoreData: paramsStoreData
                                    },

                                    formulas: {
                                        reportsFormatData: function () {
                                            return CMDBuildUI.util.administration.helper.ModelHelper.getReportFormats();
                                        }
                                    },
                                    stores: {
                                        reportsStore: {
                                            source: 'reports.Reports'
                                        },
                                        reportsFormatStore: {
                                            model: 'CMDBuildUI.model.base.ComboItem',
                                            proxy: 'memory',
                                            data: '{reportsFormatData}'
                                        },
                                        paramsStore: {
                                            fields: ['key', 'value'],
                                            proxy: 'memory',
                                            data: '{paramsStoreData}'
                                        }
                                    }
                                }
                            });
                        });
                    }
                });
            }
        });
    },

    /**
     * 
     */
    onAddReportBtnClick: function () {
        this.getView().add({
            columnWidth: 1,
            layout: 'column',
            xtype: 'administration-content-emails-templates-card-attachments-report',
            viewModel: {
                data: {
                    report: {
                        code: '',
                        format: 'pdf'
                    },
                    paramsStoreData: []
                },

                formulas: {
                    reportsFormatData: function () {
                        return CMDBuildUI.util.administration.helper.ModelHelper.getReportFormats();
                    }
                },

                stores: {
                    reportsStore: {
                        source: 'reports.Reports'
                    },
                    reportsFormatStore: {
                        model: 'CMDBuildUI.model.base.ComboItem',
                        proxy: 'memory',
                        data: '{reportsFormatData}'
                    },
                    paramsStore: {
                        fields: ['key', 'value'],
                        proxy: 'memory',
                        data: '{paramsStoreData}'
                    }
                }
            }
        });
    }

});