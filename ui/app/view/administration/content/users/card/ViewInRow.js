Ext.define('CMDBuildUI.view.administration.content.users.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',
    requires: [
        'CMDBuildUI.view.administration.content.users.card.ViewInRowController',
        'CMDBuildUI.view.administration.content.users.card.EditModel',
        'Ext.layout.*'
    ],
    autoDestroy: true,
    alias: 'widget.administration-content-users-card-viewinrow',
    controller: 'administration-content-users-card-viewinrow',
    viewModel: {
        type: 'view-administration-content-users-card-edit'
    },

    cls: 'administration',
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,

    config: {
        objectTypeName: null,
        objectId: null,
        shownInPopup: false,
        theUser: null
    },

    items: [
        {
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            xtype: 'fieldset',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'fieldcontainer',
                    items: [
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels
                                            .username,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                                    },
                                    name: 'username',
                                    bind: {
                                        value: '{theUser.username}'
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.common.labels.description'
                                    },
                                    name: 'description',
                                    bind: {
                                        value: '{theUser.description}'
                                    }
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.navigation.email,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.navigation.email'
                                    },
                                    name: 'email',
                                    bind: {
                                        value: '{theUser.email}'
                                    }
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
                                    },
                                    labelAlign: 'top',
                                    name: 'language',
                                    readOnly: true,
                                    bind: {
                                        value: '{theUser._language_description}'
                                    },
                                    renderer: function (value) {
                                        var vm = this.lookupViewModel();
                                        vm.bind(
                                            {
                                                bindTo: {
                                                    theUser: '{theUser}',
                                                    languages: '{languages}'
                                                }
                                            },
                                            function (data) {
                                                var store = data.languages;
                                                if (store) {
                                                    var lang = store.findRecord('code', data.theUser.get('language'));
                                                    if (lang) {
                                                        data.theUser.set(
                                                            '_language_description',
                                                            lang.get('description')
                                                        );
                                                    }
                                                }
                                            }
                                        );
                                        return value;
                                    }
                                },
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
                                    },
                                    name: 'initialPage',
                                    readOnly: true,
                                    bind: {
                                        value: '{theUser._initialPage_description}'
                                    }
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.service,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.users.fieldLabels.service'
                                    },
                                    name: 'service',
                                    readOnly: true,
                                    bind: {
                                        value: '{theUser.service}'
                                    }
                                }
                                // privileged field IS NOT NEEDED ANYMORE removed from 3.2.1
                            ]
                        },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'checkbox',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                                    },
                                    name: 'active',
                                    readOnly: true,
                                    bind: {
                                        value: '{theUser.active}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
            },
            xtype: 'fieldset',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            ui: 'administration-formpagination',
            items: [
                {
                    xtype: 'fieldcontainer',
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'checkbox',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup'
                            },
                            readOnly: true,
                            bind: {
                                value: '{theUser.multiGroup}'
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                            },
                            bind: {
                                value: '{theUser.defaultUserGroup}'
                            },
                            // get the user description from groupStore
                            renderer: function (value) {
                                var group = Ext.getStore('groups.Groups').getById(value);
                                return group && group.isModel ? group.get('description') : value;
                            }
                        }
                    ]
                },
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'displayfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
                            },
                            bind: {
                                value: '{groupsHtml}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            xtype: 'fieldset',
            ui: 'administration-formpagination',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
            title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
            },
            hidden: true,
            itemId: 'tenants',
            items: [
                {
                    xtype: 'fieldcontainer',
                    bind: {
                        hidden: '{!isMultitenantActive}'
                    },
                    items: [
                        /**
                         * currently not supported by server
                         */
                        //     {
                        //     layout: 'column',
                        //     defaults: {
                        //         columnWidth: 0.5
                        //     },
                        //     items: [{
                        //         xtype: 'checkbox',
                        //         labelAlign: 'top',
                        //         flex: '0.5',
                        //         padding: '0 15 0 15',
                        //         layout: 'anchor',
                        //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,
                        //         localized: {
                        //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
                        //         },
                        //         readOnly: true,
                        //         bind: {
                        //             value: '{theUser.multiTenant}'
                        //         }
                        //     }]
                        // }, {
                        //     layout: 'column',
                        //     defaults: {
                        //         columnWidth: 0.5
                        //     },
                        //     items: [{
                        //         xtype: 'displayfield',
                        //         labelAlign: 'top',
                        //         flex: '0.5',
                        //         padding: '0 15 0 15',
                        //         layout: 'anchor',
                        //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant,
                        //         localized: {
                        //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant'
                        //         },
                        //         bind: {
                        //             value: '{theUser.defaultUserTenant}'
                        //         }
                        //     }]
                        // },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    labelAlign: 'top',
                                    flex: '0.5',
                                    padding: '0 15 0 15',
                                    layout: 'anchor',
                                    fieldLabel:
                                        CMDBuildUI.locales.Locales.administration.users.fieldLabels
                                            .multitenantactivationprivileges,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenantactivationprivileges'
                                    },
                                    displayField: 'label',
                                    valueField: 'value',
                                    bind: {
                                        value: '{theUser.multiTenantActivationPrivileges}'
                                    },
                                    renderer: function (value) {
                                        if (value) {
                                            var vm = this.lookupViewModel();
                                            var store = vm.getStore('multiTenantActivationPrivilegesStore');
                                            if (store) {
                                                var record = store.findRecord('value', value);
                                                if (record) {
                                                    return record.get('label');
                                                }
                                            }
                                        }
                                        return value;
                                    }
                                }
                            ]
                        },
                        {
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                                    bind: {
                                        value: '{tenantsHtml}',
                                        fieldLabel: '{tenantLabel}'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        }
    ],
    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools(
        {
            edit: {
                disabled: true
            },
            view: true,
            clone: {
                disabled: true
            },
            delete: false,
            activeToggle: {
                disabled: true
            }
        },

        /* testId */
        'users',

        /* viewModel object needed only for activeTogle */
        'theUser',

        /* add custom tools[] on the left of the bar */
        [],

        /* add custom tools[] before #editBtn*/
        [
            {
                xtype: 'tool',
                itemId: 'changePasswordBtn',
                iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('key', 'solid'),
                tooltip: CMDBuildUI.locales.Locales.main.password.change,
                localized: {
                    tooltip: 'CMDBuildUI.locales.Locales.main.password.change'
                },
                cls: 'administration-tool',
                autoEl: {
                    'data-testid': 'administration-users-changePsswordBtn'
                },
                bind: {
                    hidden: '{!actions.view}',
                    disabled: '{toolAction._canUpdate === false}'
                }
            }
        ],

        /* add custom tools[] after at the end of the bar*/
        []
    ),

    listeners: {
        afterlayout: function (panel) {
            Ext.asap(function () {
                try {
                    panel.unmask();
                } catch (error) {}
            });
        }
    }
});
