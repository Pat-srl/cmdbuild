Ext.define('CMDBuildUI.view.administration.content.users.card.Create', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.users.card.CreateController',
        'CMDBuildUI.view.administration.content.users.card.EditModel',

        'CMDBuildUI.util.helper.FormHelper'
    ],

    alias: 'widget.administration-content-users-card-create',
    controller: 'view-administration-content-users-card-create',
    viewModel: {
        type: 'view-administration-content-users-card-edit'
    },

    config: {
        theUser: null
    },
    bind: {
        theUser: '{theUser}'
    },
    modelValidation: true,
    autoScroll: true,
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bubbleEvents: ['itemcreated', 'cancelcreation'],
    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            items: [
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.groupandpermissions.fieldlabels.username'
                            },
                            name: 'username',
                            vtype: 'usernameValidation',
                            itemId: 'username_input',
                            enforceMaxLength: true,
                            allowBlank: false,
                            maxLength: 40,
                            bind: {
                                value: '{theUser.username}'
                            },
                            listeners: {
                                afterrender: function (cmp) {
                                    cmp.inputEl.set({
                                        autocomplete: 'new-password'
                                    });
                                },
                                change: function (input, newVal, oldVal) {
                                    CMDBuildUI.util.administration.helper.FieldsHelper.copyTo(
                                        input,
                                        newVal,
                                        oldVal,
                                        '[name="description"]'
                                    );
                                }
                            }
                        },
                        {
                            xtype: 'textfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
                            },
                            name: 'description',
                            maxLength: 250,
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
                            xtype: 'textfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.navigation.email,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.navigation.email'
                            },
                            name: 'email',
                            enforceMaxLength: true,
                            maxLength: 320,
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
                            xtype: 'combo',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.language,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.language'
                            },
                            labelAlign: 'top',
                            name: 'language',
                            valueField: 'code',
                            displayField: 'description',
                            queryMode: 'local',
                            typeAhead: true,
                            bind: {
                                store: '{languages}',
                                value: '{theUser.language}'
                            }
                        },
                        {
                            xtype: 'allelementscombo',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.initialpage'
                            },
                            name: 'initialPage',
                            bind: {
                                value: '{theUser.initialPage}'
                            },
                            withClasses: true,
                            withProcesses: true,
                            withDashboards: true,
                            withCustompages: true,
                            withViews: true,
                            withMenuNavTrees: true,
                            typePrefix: true
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
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.service'
                            },
                            name: 'service',
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
                            bind: {
                                value: '{theUser.active}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.emails.password,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.emails.password'
            },
            items: [
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },

                    items: [
                        {
                            // TODO: Need validation after ws creation
                            xtype: 'passwordfield',
                            autoEl: {
                                'data-testid': 'administration-user-password'
                            },
                            listeners: {
                                afterrender: function (cmp) {
                                    cmp.inputEl.set({
                                        autocomplete: 'new-password'
                                    });
                                },
                                change: function (cmp) {
                                    var confirm = this.up().down('[name="confirmPassword"]');
                                    confirm.allowBlank = this.getValue() === '';
                                    confirm.validate();
                                },
                                blur: function (input, event, eOpts) {
                                    var value = input.getValue();
                                    var username = input.up('form').down('#username_input');
                                    var errors = CMDBuildUI.util.Utilities.validatePassword(
                                        value,
                                        null,
                                        username.getValue()
                                    );
                                    if (errors && errors !== true) {
                                        CMDBuildUI.util.Notifier.showWarningMessage(errors);
                                    }
                                }
                            },
                            fieldLabel: CMDBuildUI.locales.Locales.administration.emails.password,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.emails.password'
                            },
                            name: 'password',
                            reference: 'password',
                            // vtype: 'passwordValidation',
                            enforceMaxLength: true,
                            // allowBlank: false,
                            maxLength: 40,
                            bind: {
                                value: '{theUser.password}'
                            }
                        },
                        {
                            xtype: 'passwordfield',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.users.fieldLabels.confirmpassword'
                            },
                            name: 'confirmPassword',
                            reference: 'confirmPasswordField',
                            vtype: 'passwordMatch',
                            enforceMaxLength: true,
                            // allowBlank: false,
                            maxLength: 40,
                            autoEl: {
                                'data-testid': 'administration-user-confirmpassword'
                            },
                            bind: {
                                value: '{theUser.confirmPassword}'
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
                            fieldLabel:
                                CMDBuildUI.locales.Locales.administration.users.fieldLabels.changepasswordfirstlogin,
                            localized: {
                                fieldLabel:
                                    'CMDBuildUI.locales.Locales.administration.users.fieldLabels.changepasswordfirstlogin'
                            },
                            name: 'changePasswordRequired',
                            bind: {
                                value: '{theUser.changePasswordRequired}'
                            }
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.groups'
            },
            items: [
                {
                    layout: 'column',
                    defaults: {
                        columnWidth: 0.5
                    },
                    items: [
                        CMDBuildUI.util.administration.helper.FieldsHelper.getCommonComboInput(
                            'defaultUserGroup',
                            {
                                defaultUserGroup: {
                                    fieldcontainer: {
                                        fieldLabel:
                                            CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup,
                                        localized: {
                                            fieldLabel:
                                                'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaultgroup'
                                        },
                                        allowBlank: true
                                    },
                                    itemId: 'defaultUserGroup',
                                    hidden: false,
                                    disableKeyFilter: true,
                                    displayField: 'description',
                                    valueField: '_id',
                                    bind: {
                                        store: '{activeUserRolesStore}',
                                        value: '{theUser.defaultUserGroup}'
                                    },
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    }
                                }
                            },
                            false,
                            true
                        )
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
                            labelAlign: 'top',
                            flex: '0.5',

                            layout: 'anchor',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multigroup'
                            },
                            bind: {
                                value: '{theUser.multiGroup}'
                            },
                            listeners: {
                                change: function (check, newValue, oldValue, eOpts) {
                                    var field = check.up('fieldset').down('[name="defaultUserGroup"]');
                                    var form = check.up('form');
                                    field.allowBlank = !newValue;
                                    field.up('fieldcontainer').allowBlank = !newValue;
                                    if (form && form.form) {
                                        form.form.checkValidity();
                                    }

                                    if (!newValue) {
                                        field.clearInvalid();
                                        field.up('fieldcontainer').labelEl.dom.innerHTML = field
                                            .up('fieldcontainer')
                                            .labelEl.dom.innerHTML.replace(' *', '');
                                    } else {
                                        field.up('fieldcontainer').labelEl.dom.innerHTML = field
                                            .up('fieldcontainer')
                                            .labelEl.dom.innerHTML.replace('</span></span>', '</span> *</span>');
                                    }
                                }
                            }
                        }
                    ]
                },
                {
                    xtype: 'grid',
                    bind: {
                        store: '{rolesStore}'
                    },
                    viewConfig: {
                        markDirty: false
                    },
                    sortable: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    menuDisabled: true,
                    stopSelect: true,
                    columns: [
                        {
                            text: CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.attributes.fieldlabels.group'
                            },
                            dataIndex: 'description',
                            flex: 1,
                            align: 'left'
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            xtype: 'checkcolumn',
                            dataIndex: 'active',
                            align: 'center',
                            listeners: {
                                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                                    var form = check.up('form');
                                    var vm = form.getViewModel();
                                    var currentGroups = vm.get('theUser.userGroups');
                                    switch (checked) {
                                        case true:
                                            currentGroups.push({
                                                id: record.get('_id'),
                                                _id: record.get('_id'),
                                                name: record.get('name'),
                                                description: record.get('description')
                                            });
                                            break;
                                        case false:
                                            var index = currentGroups
                                                .map(function (group) {
                                                    if (group.isModel) {
                                                        return group.get('_id');
                                                    }
                                                    return group._id;
                                                })
                                                .indexOf(record.get('_id'));
                                            currentGroups.splice(index, 1);
                                            if (record.getId() == vm.get('theUser.defaultUserGroup')) {
                                                form.down('#defaultUserGroup').reset();
                                                vm.set('theUser.defaultUserGroup', null);
                                            }
                                            break;
                                    }
                                    form.form.checkValidity();
                                    vm.set('userGroups', currentGroups);
                                }
                            }
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
            bind: {
                hidden: '{!tenantModeIsClass}',
                title: '{tenantLabel}'
            },
            items: [
                /**
                 * currently not supported by server
                 */
                // {
                //     layout: 'column',
                //     defaults: {
                //         columnWidth: 0.5
                //     },
                //     items: [{
                //         xtype: 'combo',
                //         labelAlign: 'top',
                //         flex: '0.5',

                //         layout: 'anchor',

                //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant,
                //         localized: {
                //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.defaulttenant'
                //         },
                //         displayField: 'description',
                //         valueField: '_id',
                //         bind: {
                //             store: '{getSelectedTenants}',
                //             value: '{theUser.defaultTenant}'
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
                            xtype: 'combo',
                            labelAlign: 'top',
                            flex: '0.5',

                            layout: 'anchor',
                            queryMode: 'local',
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
                                store: '{multiTenantActivationPrivilegesStore}',
                                value: '{theUser.multiTenantActivationPrivileges}'
                            }
                        }
                    ]
                },
                /**
                 * currently not supported by server
                 */
                // {
                //     layout: 'column',
                //     defaults: {
                //         columnWidth: 0.5
                //     },
                //     bind: {
                //         hidden: '{!tenantModeIsClass}'
                //     },
                //     items: [{
                //         xtype: 'checkbox',
                //         labelAlign: 'top',
                //         flex: '0.5',

                //         layout: 'anchor',
                //         fieldLabel: CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant,
                //         localized: {
                //             fieldLabel: 'CMDBuildUI.locales.Locales.administration.users.fieldLabels.multitenant'
                //         },
                //         bind: {
                //             value: '{theUser.multitenant}'
                //         }
                //     }]
                // },
                {
                    /**
                     * currently is possible to edit tenants relation only in management
                     */
                    marginTop: 10,
                    xtype: 'grid',
                    bind: {
                        store: '{tenantsStore}',
                        hidden: '{tenantModeIsDbFunction}'
                    },
                    sortable: false,
                    sealedColumns: false,
                    sortableColumns: false,
                    enableColumnHide: false,
                    enableColumnMove: false,
                    enableColumnResize: false,
                    menuDisabled: true,
                    stopSelect: true,
                    columns: [
                        {
                            text: CMDBuildUI.locales.Locales.administration.common.labels.tenant,
                            dataIndex: 'description',
                            flex: 1,
                            align: 'left',
                            bind: {
                                text: '{tenantLabel}'
                            }
                        },
                        {
                            text: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            xtype: 'checkcolumn',
                            dataIndex: 'active',
                            align: 'center',
                            listeners: {
                                checkchange: function (check, rowIndex, checked, record, e, eOpts) {
                                    var vm = check.up('form').getViewModel();
                                    var currentTenants = vm.get('theUser.userTenants');
                                    switch (checked) {
                                        case true:
                                            currentTenants.push({
                                                _id: record.get('_id'),
                                                name: record.get('name'),
                                                description: record.get('description'),
                                                active: true
                                            });
                                            break;
                                        case false:
                                            var index = currentTenants
                                                .map(function (tenant) {
                                                    if (tenant.isModel) {
                                                        return tenant.get('_id');
                                                    }
                                                    return tenant._id;
                                                })
                                                .indexOf(record.get('_id'));
                                            currentTenants.splice(index, 1);
                                            break;
                                    }

                                    vm.set('userTenants', currentTenants);
                                }
                            }
                        }
                    ]
                }
            ]
        }
    ],

    buttons: CMDBuildUI.util.administration.helper.FormHelper.getSaveAndAddCancelButtons(true)
});
