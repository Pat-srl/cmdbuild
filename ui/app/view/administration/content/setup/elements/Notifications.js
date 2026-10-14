Ext.define('CMDBuildUI.view.administration.content.setup.elements.Notifications', {
    extend: 'Ext.panel.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.NotificationsController',
        'CMDBuildUI.view.administration.content.setup.elements.NotificationsModel'
    ],
    alias: 'widget.administration-content-setup-elements-notifications',
    controller: 'administration-content-setup-elements-notifications',
    viewModel: {
        type: 'administration-content-setup-elements-notifications'
    },
    items: [{
        ui: 'administration-formpagination',
        xtype: "fieldset",
        collapsible: true,
        title: CMDBuildUI.locales.Locales.administration.emails.email,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.emails.email'
        },
        defaults: {
            layout: 'column',
            columnWidth: 1,
            defaults: {
                columnWidth: 0.5
            }
        },
        items: [{
            items: [{
                /*********************  config org.cmdbuild.ui.email.groupByStatus **********************/
                xtype: 'checkbox',
                fieldLabel: CMDBuildUI.locales.Locales.main.preferences.groupemailsbystatus,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.main.preferences.groupemailsbystatus'
                },
                name: 'groupByStatus',
                bind: {
                    value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__email__DOT__groupByStatus}',
                    readOnly: '{actions.view}'
                }
            }, {
                /*********************  org.cmdbuild.email.maxAttachmentSizeForEmail **********************/
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.maxattachmentsizeforemail'
                },
                items: [{
                    xtype: 'numberfield',
                    name: 'maxAttachmentSizeForEmail',
                    hidden: true,
                    minValue: 0,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__email__DOT__maxAttachmentSizeForEmail}',
                        hidden: '{actions.view}'
                    }
                }, {
                    xtype: 'displayfield',
                    hidden: true,
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__email__DOT__maxAttachmentSizeForEmail}',
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        return value ? value : CMDBuildUI.locales.Locales.administration.systemconfig.nolimit;
                    }
                }]
            }]
        }, {
            items: [{
                /*********************  org.cmdbuild.ui.email.defaultDelay **********************/
                xtype: 'fieldcontainer',
                fieldLabel: CMDBuildUI.locales.Locales.administration.systemconfig.emailsendecancellationperiod,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.systemconfig.emailsendecancellationperiod'
                },
                margin: '0 10 0 0',
                defaults: {
                    bind: {
                        value: '{theSetup.org__DOT__cmdbuild__DOT__ui__DOT__email__DOT__defaultDelay}'
                    }
                },
                items: [{
                    xtype: 'combobox',
                    displayField: 'label',
                    valueField: 'value',
                    forceSelection: true,
                    queryMode: 'local',
                    hidden: true,
                    bind: {
                        hidden: '{actions.view}',
                        store: '{defaultEmailDelay}'
                    }
                }, {
                    xtype: 'displayfield',
                    hidden: true,
                    bind: {
                        hidden: '{!actions.view}'
                    },
                    renderer: function (value) {
                        return value;
                    }
                }]
            }]
        }]
    }]
});