Ext.define('CMDBuildUI.view.administration.content.emails.templates.card.attachments.Attachments', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.administration.content.emails.templates.card.attachments.AttachmentsModel'
    ],

    alias: 'widget.administration-content-emails-templates-card-attachments',
    viewModel: {
        type: 'administration-content-emails-templates-card-attachments'
    },

    title: CMDBuildUI.locales.Locales.administration.busmessages.attachments,
    localized: {
        title: 'CMDBuildUI.locales.Locales.administration.busmessages.attachments'
    },
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    ui: 'administration-formpagination',

    collapsible: true,
    config: {
        reports: null,
        attachments: null
    },
    layout: 'column',
    hidden: true,
    bind: {
        hidden: '{!isDMSEnabled}'
    },
    items: [{
        columnWidth: 1,
        xtype: 'administration-content-emails-templates-card-attachments-reports'
    }, {
        columnWidth: 1,
        xtype: 'administration-content-emails-templates-card-attachments-fromcard'
    }],

    getData: function () {
        var data = {
            reports: [],
            fromCard: {}
        };
        this.down('administration-content-emails-templates-card-attachments-reports').items.each(function (e) {
            if (e.xtype === 'administration-content-emails-templates-card-attachments-report') {
                var vm = e.getViewModel();
                var params = {};
                vm.get('paramsStore').each(function (param) {
                    params[param.get('key')] = param.get('value');
                });
                data.reports.push({
                    code: vm.get('report.code'),
                    format: vm.get('report.format'),
                    params: params
                });
            }
        });
        return data;

    }
});