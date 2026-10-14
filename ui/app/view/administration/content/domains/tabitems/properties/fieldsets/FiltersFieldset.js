Ext.define('CMDBuildUI.view.administration.content.domains.tabitems.properties.fieldsets.FiltersFieldset', {
    extend: 'Ext.panel.Panel',

    alias: 'widget.administration-content-domains-tabitems-properties-fieldsets-filtersfieldset',

    ui: 'administration-formpagination',
    items: [
        {
            xtype: 'fieldset',
            layout: 'column',
            itemId: 'domain-filterfieldset',
            title: CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.filters,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.filters'
            },
            ui: 'administration-formpagination',
            items: [
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('sourceFilter', {
                    sourceFilter: {
                        xtype: 'textarea',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourcefilter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.sourcefilter'
                        },
                        resizable: {
                            handles: 's'
                        },
                        name: 'sourceFilter',
                        bind: {
                            readOnly: '{actions.view}',
                            value: '{theDomain.sourceFilter}'
                        }
                    }
                }),
                CMDBuildUI.util.administration.helper.FieldsHelper.getCommonTextareaInput('targetFilter', {
                    targetFilter: {
                        xtype: 'textarea',
                        fieldLabel: CMDBuildUI.locales.Locales.administration.domains.fieldlabels.targetfilter,
                        localized: {
                            fieldLabel: 'CMDBuildUI.locales.Locales.administration.domains.fieldlabels.targetfilter'
                        },
                        resizable: {
                            handles: 's'
                        },
                        name: 'targetFilter',
                        bind: {
                            readOnly: '{actions.view}',
                            value: '{theDomain.targetFilter}'
                        }
                    }
                })
            ]
        }
    ]
});
