Ext.define('CMDBuildUI.view.administration.content.customcomponents.scripts.View', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.customcomponents.scripts.ViewController',
        'CMDBuildUI.view.administration.content.customcomponents.scripts.ViewModel'
    ],
    alias: 'widget.administration-content-customcomponents-scripts-view',
    controller: 'administration-content-customcomponents-scripts-view',
    layout: 'border',
    viewModel: {
        type: 'administration-content-customcomponents-scripts-view'
    },
    ui: 'administration-tabandtools',
    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        userCls: '{formModeCls}' // this is used for hide label localzation icon in `view` mode
    },
    items: [{
        xtype: 'panel',
        region: 'center',
        scrollable: 'y',
        hidden: true,
        bind: {
            hidden: '{hideForm}'
        },
        items: [{
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
            },
            defaults: {
                columnWidth: 0.5,
                layout: 'column',
                xtype: 'container'
            },
            items: [{
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput({
                        name: {
                            vtype: 'nameInputValidation',
                            allowBlank: false,
                            bind: {
                                value: '{theCustomcomponent.name}'
                            }
                        }
                    }, true, '[name="description"]'),
                    CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                        description: {
                            name: 'description',
                            bind: {
                                value: '{theCustomcomponent.description}'
                            },
                            allowBlank: false
                        }
                    })
                ]
            }, {
                items: [
                    CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                        active: {
                            bind: {
                                value: '{theCustomcomponent.active}'
                            }
                        }
                    }, 'active')
                ]
            }]
        }, {
            ui: 'administration-formpagination',
            xtype: "fieldset",
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.customcomponents.strings.script,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.customcomponents.strings.script'
            },
            defaults: {
                columnWidth: 0.5,
                layout: 'column',
                xtype: 'container'
            },
            items: [{
                xtype: 'aceeditortextarea',
                allowBlank: true,
                minHeight: '250px',
                vmObjectName: 'theCustomcomponent',
                inputField: 'data',
                options: {
                    mode: "ace/mode/groovy",
                    readOnly: true,
                    placeholder: CMDBuildUI.locales.Locales.administration.customcomponents.emptytexts.scriptcodeempty,
                    wrap: true
                },
                bind: {
                    value: '{theCustomcomponent.data}',
                    readOnly: '{actions.view}',
                    config: {
                        options: {
                            readOnly: '{actions.view}'
                        }
                    }
                },
                autoEl: {
                    'data-testid': 'administration-data-input'
                },
                fieldLabel: CMDBuildUI.locales.Locales.administration.customcomponents.strings.code,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.customcomponents.strings.code',
                    placeholder: 'CMDBuildUI.locales.Locales.administration.customcomponents.emptytexts.scriptcodeempty'
                },
                listeners: {
                    render: function (element) {
                        var aceEditor = element.getAceEditor();
                        var vm = element.lookupViewModel();
                        vm.bind({
                            bindTo: {
                                validationRule: '{theCustomcomponent.data}'
                            },
                            single: true
                        }, function (data) {
                            if (data.validationRule) {
                                aceEditor.setValue(data.validationRule, -1);
                            }
                        });
                        vm.bind({
                            isView: '{actions.view}'
                        }, function (data) {
                            aceEditor.setReadOnly(data.isView);
                        });
                    }
                },
                name: 'scriptComponent',
                width: '95%'
            }]
        }]
    }],

    dockedItems: [{
            xtype: 'components-administration-toolbars-formtoolbar',
            dock: 'top',
            padding: '6 0 6 8',
            borderBottom: 0,
            itemId: 'toolbarscontainer',
            style: 'border-bottom-width:0!important',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({},
                'searchfilter',
                'theViewFilter',
                [{
                    xtype: 'button',
                    text: CMDBuildUI.locales.Locales.administration.customcomponents.texts.addcustomcomponent, // Add customcomponent
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.administration.customcomponents.texts.addcustomcomponent'
                    },
                    ui: 'administration-action-small',
                    itemId: 'addBtn',
                    iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('plus', 'solid'),
                    autoEl: {
                        'data-testid': 'administration-class-toolbar-addLookupTypeBtn'
                    },
                    bind: {
                        disabled: '{!toolAction._canAdd}'
                    }
                }, {
                    xtype: 'admin-globalsearchfield',
                    getObjectType: function () {
                        return 'scripts';
                    }
                }, {
                    xtype: 'tbfill'
                }],
                null,
                [{
                    xtype: 'tbtext',
                    hidden: true,
                    bind: {
                        hidden: '{!theCustomcomponent.description}',
                        html: '{componentTypeName}: <b data-testid="administration-customcomponent-description">{theCustomcomponent.description}</b>'
                    }
                }])
        }, {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'top',
            borderBottom: 0,
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools({
                    edit: true, // #editBtn set true for show the button
                    'delete': true, // #deleteBtn set true for show the button
                    activeToggle: true // #enableBtn and #disableBtn set true for show the buttons                    
                },

                /* testId */
                'customcomponent',

                /* viewModel object needed only for activeTogle */
                'theCustomcomponent',

                /* add custom tools[] on the left of the bar */
                [],

                /* add custom tools[] before #editBtn*/
                [],

                /* add custom tools[] after at the end of the bar*/
                []
            ),
            bind: {
                hidden: '{formtoolbarHidden}'
            }
        },
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,

            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons(true)
        }
    ]


});