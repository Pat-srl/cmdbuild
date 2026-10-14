Ext.define('CMDBuildUI.view.administration.content.setup.elements.Homepages', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.setup.elements.HomepagesController',
        'CMDBuildUI.view.administration.content.setup.elements.HomepagesModel'
    ],
    alias: 'widget.administration-content-setup-elements-homepages',
    controller: 'administration-content-setup-elements-homepages',
    viewModel: {
        type: 'administration-content-setup-elements-homepages'
    },

    items: [
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.systemconfig.generals,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.systemconfig.generals'
            },
            items: [
                {
                    xtype: 'container',
                    layout: 'column',
                    items: [
                        {
                            xtype: 'checkbox',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
                            },
                            name: 'isEnabled',
                            bind: {
                                value: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__enabled}',
                                readOnly: '{actions.view}'
                            }
                        },
                        {
                            xtype: 'fieldcontainer',
                            columnWidth: 0.5,
                            fieldLabel: CMDBuildUI.locales.Locales.administration.homepages.configtype,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.homepages.configtype'
                            },
                            items: [
                                {
                                    xtype: 'displayfield',
                                    bind: {
                                        value: '{_configType_description}',
                                        hidden: '{!actions.view}'
                                    },
                                    renderer: function (value) {
                                        var me = this;
                                        var vm = me.lookupViewModel();
                                        if (!value) {
                                            vm.bind(
                                                {
                                                    bindTo: {
                                                        store: '{configTypesStore}',
                                                        value: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__type}'
                                                    },
                                                    single: true
                                                },
                                                function (data) {
                                                    var record = data.store
                                                        ? data.store.findRecord('value', data.value)
                                                        : null;
                                                    if (record) {
                                                        vm.set('_configType_description', record.get('label'));
                                                    }
                                                }
                                            );
                                        }
                                        return value;
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-homepages-generalproperties-configType_display'
                                    }
                                },
                                {
                                    xtype: 'combobox',
                                    displayField: 'label',
                                    valueField: 'value',
                                    forceSelection: true,
                                    editable: false,
                                    autoSelect: false,
                                    triggers: {
                                        clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                    },
                                    bind: {
                                        store: '{configTypesStore}',
                                        value: '{theSetup.org__DOT__cmdbuild__DOT__homepages__DOT__type}',
                                        hidden: '{actions.view}'
                                    },
                                    autoEl: {
                                        'data-testid': 'administration-homepages-generalproperties-configType_input'
                                    }
                                }
                            ]
                        }
                    ]
                }
            ]
        },
        {
            ui: 'administration-formpagination',
            xtype: 'fieldset',
            collapsible: true,
            title: CMDBuildUI.locales.Locales.administration.homepages.searchonclass,
            hidden: true,
            localized: {
                title: 'CMDBuildUI.locales.Locales.administration.homepages.searchonclass'
            },
            bind: {
                hidden: '{!isDefaultConfigSelected}'
            },
            items: [
                {
                    xtype: 'treepanel',
                    itemId: 'classesTree',
                    viewConfig: {
                        markDirty: false
                    },
                    enableColumnHide: false,
                    ui: 'administration-navigation-tree',
                    bind: {
                        store: '{classesStore}'
                    },
                    columns: [
                        {
                            xtype: 'treecolumn',
                            text: CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel,
                            localized: {
                                text: 'CMDBuildUI.locales.Locales.administration.classes.toolbar.classLabel'
                            },
                            dataIndex: 'text',
                            align: 'left',
                            flex: 1
                        }
                    ]
                }
            ]
        }
    ]
});
