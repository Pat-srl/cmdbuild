Ext.define('CMDBuildUI.view.joinviews.configuration.items.AttributesCustomization', {
    extend: 'Ext.form.FieldSet',

    requires: [
        'CMDBuildUI.view.joinviews.configuration.items.AttributesCustomizationController',
        'CMDBuildUI.view.joinviews.configuration.items.AttributesCustomizationModel'
    ],
    alias: 'widget.joinviews-configuration-items-attributescustomization',
    controller: 'joinviews-configuration-items-attributescustomization',
    viewModel: {
        type: 'joinviews-configuration-items-attributescustomization'
    },

    title: CMDBuildUI.locales.Locales.joinviews.attributecustomization,
    localized: {
        title: 'CMDBuildUI.locales.Locales.joinviews.attributecustomization'
    },

    fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
    bind: {
        ui: '{fieldsetUi}'
    },

    scrollable: true,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    items: [
        {
            xtype: 'container',
            itemId: 'warningcheckbox',
            height: '40px',
            margin: '0 5 20 5',
            ui: 'messagewarning',
            hidden: true,
            bind: {
                hidden: '{hideWarningCheckBox}'
            },
            items: [
                {
                    ui: 'custom',
                    xtype: 'container',
                    html: CMDBuildUI.locales.Locales.joinviews.selectdisplayattributegrid,
                    localized: {
                        html: 'CMDBuildUI.locales.Locales.joinviews.selectdisplayattributegrid'
                    }
                }
            ]
        },
        {
            xtype: 'grid',
            forceFit: true,
            ui: 'cmdbuildgrouping',
            itemId: 'attributegridcustom',

            plugins: [
                {
                    pluginId: 'cellediting',
                    ptype: 'cellediting',
                    clicksToEdit: 1,
                    listeners: {
                        beforeedit: function (editor, context) {
                            if (editor.view.lookupViewModel().get('actions.view')) {
                                return false;
                            }
                        },
                        edit: function (editor, context, eOpts) {
                            var mainView = this.view.up('joinviews-configuration-main'),
                                record = context.record;
                            if (context.originalValue !== context.value) {
                                switch (context.field) {
                                    case 'name':
                                        mainView.clearAliasIndex(mainView.aliasType.attribute, context.originalValue);
                                        var lastAliasIndex = mainView.getNewAliasIndex(
                                            mainView.aliasType.attribute,
                                            record.get(context.field)
                                        );
                                        record.set(
                                            context.field,
                                            Ext.String.format(
                                                '{0}{1}',
                                                context.value,
                                                lastAliasIndex ? Ext.String.format('_{0}', lastAliasIndex) : ''
                                            )
                                        );
                                        break;
                                    default:
                                        // do nothing
                                        break;
                                }
                            }
                        }
                    }
                }
            ],

            viewConfig: {
                markDirty: false,
                plugins: [
                    {
                        ptype: 'gridviewdragdrop',
                        dragText: CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop,
                        localized: {
                            dragText: 'CMDBuildUI.locales.Locales.administration.attributes.strings.draganddrop'
                        },
                        containerScroll: true,
                        pluginId: 'gridviewdragdrop'
                    }
                ]
            },

            bind: {
                store: '{attributesSelectedStore}'
            },

            columns: [
                {
                    hideable: false,
                    text: 'deepindex',
                    hidden: true,
                    dataIndex: '_deepIndex'
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.attribute,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.attribute'
                    },
                    dataIndex: '_attributeDescription',
                    renderer: function (value, metadata, record) {
                        return Ext.String.format('{0} - {1}', record.get('targetAlias'), value);
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.alias,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.alias'
                    },
                    hidden: true,
                    dataIndex: 'name',
                    editor: {
                        xtype: 'textfield',
                        vtype: 'nameInputValidation',
                        autoEl: {
                            'data-testid': 'joinviews-configuration-items-attributes-name-input'
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.description,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.description'
                    },
                    dataIndex: 'description',
                    editor: {
                        xtype: 'textfield',
                        triggers: {
                            localized: {
                                bind: {
                                    hidden: '{isAdministrationModule || !record.description.length}'
                                },
                                cls: CMDBuildUI.util.helper.IconHelper.getIconId('flag', 'solid'),
                                handler: function (field, trigger, eOpts) {
                                    field
                                        .up('joinviews-configuration-items-attributescustomization')
                                        .fireEventArgs('attributedescriptionlocalizebtnclick', [field, trigger, eOpts]);
                                },
                                autoEl: {
                                    'data-testid': 'joinviews-configuration-items-attributes-description-localizeBtn'
                                }
                            }
                        },
                        listeners: {
                            beforerender: function () {
                                if (!this.lookupViewModel().get('isAdministrationModule')) {
                                    this.setHideTrigger(true);
                                }
                            }
                        },
                        autoEl: {
                            'data-testid': 'joinviews-configuration-items-attributes-description-input'
                        }
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.group,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.group'
                    },
                    dataIndex: 'group',
                    editor: {
                        xtype: 'combo',
                        allowBlank: false,
                        editable: false,
                        displayField: 'description',
                        valueField: 'name',
                        queryMode: 'local',
                        bind: {
                            store: '{theView.attributeGroups}'
                        },
                        autoEl: {
                            'data-testid': 'joinviews-configuration-items-attributes-groups-input'
                        },
                        triggers: {
                            clear: {
                                cls: Ext.baseCSSPrefix + 'form-clear-trigger',
                                handler: function (field, trigger, eOpts) {
                                    field.setValue(null);
                                },
                                autoEl: {
                                    'data-testid': 'joinviews-configuration-items-attributes-groups-input-trigger'
                                }
                            }
                        }
                    },
                    renderer: function (value) {
                        var vm = this.lookupViewModel(),
                            attributeGroupsStore = vm.get('theView.attributeGroups');

                        if (attributeGroupsStore) {
                            var record = attributeGroupsStore.findRecord('name', value, 0, false, true);
                            if (record) {
                                return record.get('description');
                            }
                        }
                        return value;
                    }
                },
                {
                    text: CMDBuildUI.locales.Locales.joinviews.showingrid,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.showingrid'
                    },
                    xtype: 'checkcolumn',
                    dataIndex: 'showInGrid',
                    listeners: {
                        beforecheckchange: function (column, rowIndex, checked, record, e, eOpts) {
                            if (this.lookupViewModel().get('actions.view')) {
                                return false;
                            }
                            return true;
                        },
                        checkchange: function (column, rowIndex, checked, record, e, eOpts) {
                            column.ownerCt.ownerCt.ownerCt.fireEvent('verifycheck', record.store);
                        }
                    }
                },
                {
                    xtype: 'checkcolumn',
                    text: CMDBuildUI.locales.Locales.joinviews.showshowinreducedgridInGrid,
                    localized: {
                        text: 'CMDBuildUI.locales.Locales.joinviews.showinreducedgrid'
                    },
                    dataIndex: 'showInReducedGrid',
                    listeners: {
                        beforecheckchange: function (check, rowIndex, checked, record, e, eOpts) {
                            if (this.lookupViewModel().get('actions.view')) {
                                return false;
                            }
                            return true;
                        },
                        checkchange: function (column, rowIndex, checked, record, e, eOpts) {
                            column.ownerCt.ownerCt.ownerCt.fireEvent('verifycheck', record.store);
                        }
                    }
                }
            ]
        }
    ],

    goingNextStep: function () {
        var warningCheckBox = this.getViewModel().get('hideWarningCheckBox');

        if (warningCheckBox) {
            return true;
        } else {
            CMDBuildUI.util.Notifier.showWarningMessage(
                Ext.String.format(
                    '<span data-testid="message-window-text">{0}</span>',
                    CMDBuildUI.locales.Locales.joinviews.selectdisplayattributegrid
                )
            );
        }
    }
});
