Ext.define('CMDBuildUI.view.administration.content.dms.models.tabitems.properties.fieldsets.DMSModelParamentersFieldset', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.administration-content-dms-models-tabitems-properties-fieldsets-dmsmodelparametersfieldset',

    items: [{
        xtype: 'fieldset',
        layout: 'column',
        title: CMDBuildUI.locales.Locales.administration.dmsmodels.modelparameters, // DMS model parameters
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.dmsmodels.modelparameters'
        },
        ui: 'administration-formpagination',
        collapsible: true,
        items: [{
            columnWidth: 0.5,

            items: [{
                /********************* Closed inline notes **********************/
                // create / edit / view
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed, // Closed inline notes
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.noteinlineclosed'
                },
                name: 'noteinlineclosed',

                bind: {
                    value: '{theModel.noteInlineClosed}',
                    readOnly: '{actions.view}',
                    hidden: '{!theModel}'
                }
            }]
        }, {
            columnWidth: 1,
            layout: 'column',
            items: [{
                xtype: 'fieldcontainer',
                layout: 'column',
                style: {
                    paddingRight: 0
                },
                columnWidth: 1,
                fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.helptext,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.helptext'
                },                
                labelToolIconCls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                labelToolIconQtip: CMDBuildUI.locales.Locales.administration.attributes.tooltips.translate,
                labelToolIconClick: 'onTranslateHelpClick',

                items: [CMDBuildUI.util.helper.FieldsHelper.getHTMLEditor({
                        columnWidth: 1,
                        bind: {
                            value: '{theModel.help}',
                            hidden: '{actions.view}'
                        }
                    }),
                    {
                        // view
                        xtype: 'displayfield',
                        name: 'defaultexporttemplate',
                        hidden: true,
                        bind: {
                            value: '{theModel.help}',
                            hidden: '{!actions.view}'
                        }
                    }
                ]
            }]
        }]
    }]
});