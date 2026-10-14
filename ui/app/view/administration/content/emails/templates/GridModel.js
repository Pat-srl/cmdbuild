Ext.define('CMDBuildUI.view.administration.content.emails.templates.GridModel', {
    extend: 'Ext.app.ViewModel',
    alias: 'viewmodel.administration-content-emails-templates-grid',
    data: {
        actions: {
            add: false,
            edit: false,
            view: true
        }
    },
    formulas: {
        advancedfilter: {
            bind: '{templateType}',
            get: function (templateType) {
                if (templateType != 'all') {
                    return {
                        baseFilter: {
                            attributes: {
                                provider: [{
                                    operator: CMDBuildUI.util.helper.FiltersHelper.operators.equal,
                                    value: [CMDBuildUI.model.emails.Template.providers[templateType]]
                                }]
                            }
                        }
                    };
                }
                return null;
            }
        }
    },

    stores: {
        templates: {
            model: 'CMDBuildUI.model.emails.Template',
            autoLoad: true,
            autoDestroy: true,
            proxy: {
                url: CMDBuildUI.util.api.Emails.getTemplatesUrl(),
                type: 'baseproxy',
                extraParams: {
                    detailed: true
                }
            },
            advancedFilter: '{advancedfilter}',
            pageSize: 0
        }
    }
});