Ext.define('CMDBuildUI.view.administration.content.views.card.Form', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.views.card.FormController',
        'CMDBuildUI.view.administration.content.views.card.FormModel',
        'CMDBuildUI.view.administration.content.views.card.FieldsHelper'
    ],
    alias: 'widget.administration-content-views-card-form',
    controller: 'administration-content-views-card-form',
    viewModel: {
        type: 'administration-content-views-card-form'
    },

    config: {
        theViewFilter: null
    },
    bind: {
        theViewFilter: '{theViewFilter}',
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },

    layout: {
        type: 'vbox',
        align: 'stretch',
        vertical: true
    },
    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    items: [
        {
            region: 'center',
            xtype: 'form',
            items: [
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    scrollable: 'y',
                    autoScroll: true,
                    padding: '5 0 0 15',
                    title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
                    },
                    items: [
                        {
                            xtype: 'administration-content-views-card-fieldscontainers-generalproperties'
                        }
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            bind: {
                hidden: '{!actions.view || hideForm}'
            },
            listeners: {},
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true,
                    delete: true,
                    activeToggle: true
                },
                'searchfilter',
                'theViewFilter'
            )
        },
        {
            xtype: 'toolbar',
            itemId: 'bottomtoolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view || !theSession.rolePrivileges.admin_views_modify}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(
                true,
                {},
                {
                    listeners: {
                        mouseover: function () {
                            var invalidFields = CMDBuildUI.util.administration.helper.FormHelper.getInvalidFields(
                                this.up('form').form
                            );
                            Ext.Array.forEach(invalidFields, function (field) {
                                CMDBuildUI.util.Logger.log(
                                    Ext.String.format('{0} is invalid', field.itemId),
                                    CMDBuildUI.util.Logger.levels.debug
                                );
                            });
                        }
                    }
                }
            )
        }
    ],

    listeners: {
        afterlayout: function (panel) {
            Ext.GlobalEvents.fireEventArgs('showadministrationcontentmask', [false, true]);
        }
    }
});
