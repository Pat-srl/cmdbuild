Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewEdit', {
    extend: 'Ext.form.Panel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.card.ViewEditController',
        'CMDBuildUI.view.administration.content.bim.projects.card.ViewEditModel'
    ],

    alias: 'widget.administration-content-bim-projects-card-viewedit',
    controller: 'administration-content-bim-projects-card-viewedit',
    viewModel: {
        type: 'administration-content-bim-projects-card-viewedit'
    },

    fieldDefaults: CMDBuildUI.util.helper.FormHelper.fieldDefaults,
    cls: 'administration tab-hidden',
    ui: 'administration-tabandtools',

    items: [
        {
            xtype: 'components-administration-toolbars-formtoolbar',
            region: 'north',
            items: CMDBuildUI.util.administration.helper.FormHelper.getTools(
                {
                    edit: true,
                    download: {
                        arrowVisible: false,
                        menu: {
                            items: [
                                {
                                    text: CMDBuildUI.locales.Locales.administration.bim.ifc,
                                    listeners: {
                                        click: 'onDownloadBtnClick'
                                    },
                                    fileType: 'ifc',
                                    cls: 'menu-item-nospace',
                                    autoEl: {
                                        'data-testid': 'administration-bim-project-download-ifc'
                                    }
                                },
                                {
                                    text: CMDBuildUI.locales.Locales.administration.bim.xkt,
                                    listeners: {
                                        click: 'onDownloadBtnClick'
                                    },
                                    hidden: true,
                                    bind: {
                                        hidden: '{theProject._can_convert}'
                                    },
                                    fileType: 'xkt',
                                    cls: 'menu-item-nospace',
                                    autoEl: {
                                        'data-testid': 'administration-bim-project-download-xkt'
                                    }
                                }
                            ]
                        }
                    },
                    delete: true,
                    clone: true,
                    activeToggle: true
                },
                'bimProjects',
                'theProject',
                // add custom tools[] on the left of the bar
                [],
                // add custom tools[] before #editBtn
                [],
                // add custom tools[] after at the end of the bar
                [
                    {
                        xtype: 'tool',
                        itemId: 'convertBtn',
                        iconCls: CMDBuildUI.util.helper.IconHelper.getIconId('exchange-alt', 'solid'),
                        tooltip: CMDBuildUI.locales.Locales.administration.bim.convertoxkt,
                        localized: {
                            tooltip: 'CMDBuildUI.locales.Locales.administration.bim.convertoxkt'
                        },
                        cls: 'administration-tool',
                        autoEl: {
                            'data-testid': 'administration-bimProjects-convertBtn'
                        },
                        hidden: true,
                        bind: {
                            hidden: '{!actions.view || !toolAction._canConvert}',
                            disabled: '{!toolAction._canUpdate}'
                        }
                    }
                ]
            ),
            hidden: true,
            bind: {
                hidden: '{!actions.view}'
            }
        },
        {
            ui: 'administration-formpagination',
            xtype: 'container',
            fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
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
                            xtype: 'container',
                            layout: 'column',
                            columnWidth: 1,
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getNameInput(
                                    {
                                        name: {
                                            columnWidth: 1,
                                            allowBlank: false,
                                            bind: {
                                                value: '{theProject.name}',
                                                disabled: '{actions.edit}'
                                            }
                                        }
                                    },
                                    true,
                                    '[name="description"]'
                                ),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getDescriptionInput({
                                    description: {
                                        columnWidth: 1,
                                        allowBlank: false,
                                        bind: {
                                            value: '{theProject.description}'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'column',
                            columnWidth: 1,
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getParentProject({
                                    parentId: {
                                        columnWidth: 1,
                                        combofield: {
                                            displayField: 'name',
                                            valueField: '_id',
                                            bind: {
                                                value: '{theProject.parentId}',
                                                disabled: '{!actions.add}',
                                                store: '{projectsWithoutParent}'
                                            },
                                            triggers: {
                                                clear: CMDBuildUI.util.administration.helper.FormHelper.getClearComboTrigger()
                                            },
                                            listeners: {
                                                change: function (combo, newValue, oldValue) {
                                                    if (newValue) {
                                                        const vm = combo.lookupViewModel();
                                                        vm.set('theProject.ownerCard', '');
                                                        vm.set('theProject.ownerClass', '');
                                                    }
                                                }
                                            }
                                        },
                                        displayfield: {
                                            bind: {
                                                value: '{theProject._parentId_description}'
                                            },
                                            renderer: function (value) {
                                                const vm = this.lookupViewModel();
                                                if (Ext.isEmpty(value)) {
                                                    vm.bind(
                                                        {
                                                            bindTo: {
                                                                store: '{projectsWithoutParent}',
                                                                parentId: '{theProject.parentId}'
                                                            },
                                                            single: true
                                                        },
                                                        function (data) {
                                                            if (data.store && data.parentId) {
                                                                const record = data.store.findRecord(
                                                                    '_id',
                                                                    data.parentId
                                                                );
                                                                if (record) {
                                                                    const _value = record.get('description');
                                                                    if (value !== _value) {
                                                                        this.set(
                                                                            'theProject._parentId_description',
                                                                            _value
                                                                        );
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    );
                                                }
                                                return value;
                                            }
                                        }
                                    }
                                }),
                                CMDBuildUI.util.administration.helper.FieldsHelper.getLastCheckin({
                                    lastCheckin: {
                                        columnWidth: 1,
                                        bind: {
                                            value: '{theProject.lastCheckin}',
                                            disabled: '{actions.edit}'
                                        }
                                    }
                                })
                            ]
                        },
                        {
                            xtype: 'container',
                            layout: 'column',
                            columnWidth: 1,
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getActiveInput({
                                    active: {
                                        bind: {
                                            value: '{theProject.active}'
                                        }
                                    }
                                })
                            ]
                        }
                    ]
                },
                {
                    ui: 'administration-formpagination',
                    xtype: 'fieldset',
                    collapsible: true,
                    title: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                    localized: {
                        title: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                    },
                    items: [
                        {
                            xtype: 'container',
                            hidden: true,
                            bind: {
                                hidden: '{theProject.parentId}'
                            },
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                CMDBuildUI.util.administration.helper.FieldsHelper.getAllClassesProcessesViewsInput(
                                    {
                                        associatedClass: {
                                            fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedclass,
                                            localized: {
                                                fieldLabel:
                                                    'CMDBuildUI.locales.Locales.administration.gis.associatedclass'
                                            },
                                            allowBlank: false,
                                            disabled: true,
                                            bind: {
                                                value: '{theProject.ownerClass}',
                                                disabled: '{theProject.parentId}'
                                            },
                                            withClasses: true,
                                            onlyChildClasses: true,
                                            withProcesses: true,
                                            onlyChildProcesses: true,
                                            listeners: {
                                                change: function (field) {
                                                    const ownerCardContainer = field
                                                        .up('fieldset')
                                                        .down('#ownerCardContainer');
                                                    const ownerCard = ownerCardContainer.down('#ownerCard');
                                                    if (ownerCard) {
                                                        ownerCard.setValue(null);
                                                    }
                                                    ownerCardContainer.removeAll();

                                                    const value = field.getValue();
                                                    const referencecombo = {
                                                        xtype: 'referencecombofield',
                                                        displayField: 'Description',
                                                        itemId: 'ownerCard',
                                                        name: 'ownerCard',
                                                        allowBlank: false,
                                                        valueField: '_id',
                                                        width: '100%',
                                                        disabled: !value,
                                                        style: 'padding-right: 15px',
                                                        metadata: {
                                                            targetType: value ? 'class' : undefined,
                                                            targetClass: value ? value : undefined
                                                        },
                                                        bind: {
                                                            disabled: '{theProject.parentId}',
                                                            hidden: '{actions.view}',
                                                            value: '{theProject.ownerCard}'
                                                        },
                                                        /**
                                                         *
                                                         * @param {Mixed} val
                                                         * @return {Boolean}
                                                         */
                                                        validator: function (val) {
                                                            return true;
                                                        }
                                                    };
                                                    ownerCardContainer.add(referencecombo);
                                                }
                                            }
                                        }
                                    },
                                    'associatedClass'
                                ),
                                {
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                                    localized: {
                                        fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                                    },
                                    items: [
                                        {
                                            columnWidth: 1,
                                            hidden: true,
                                            xtype: 'displayfield',
                                            bind: {
                                                hidden: '{!actions.view}',
                                                value: '{theProject._ownerCardDescription}'
                                            }
                                        },
                                        {
                                            columnWidth: 1,
                                            hidden: true,
                                            xtype: 'fieldcontainer',
                                            style: 'padding-right: 15px; min-height: 25px',
                                            itemId: 'ownerCardContainer',
                                            items: [],
                                            bind: {
                                                hidden: '{actions.view}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'container',
                            hidden: true,
                            bind: {
                                hidden: '{!theProject.parentId}'
                            },
                            layout: 'column',
                            defaults: {
                                columnWidth: 0.5
                            },
                            items: [
                                {
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedclass,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedclass'
                                    },
                                    items: [
                                        {
                                            columnWidth: 1,
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theProject._parentClassDescription}'
                                            }
                                        }
                                    ]
                                },
                                {
                                    xtype: 'fieldcontainer',
                                    layout: 'column',
                                    columnWidth: 0.5,
                                    fieldLabel: CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedcard,
                                    localized: {
                                        fieldLabel:
                                            'CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedcard'
                                    },
                                    items: [
                                        {
                                            columnWidth: 1,
                                            xtype: 'displayfield',
                                            bind: {
                                                value: '{theProject._parentCardDescription}'
                                            }
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'filefield',
                            name: 'fileIFC',
                            columnWidth: 0.5,
                            padding: '0 15 0 0',
                            itemId: 'fileIFC',
                            fieldLabel: CMDBuildUI.locales.Locales.administration.bim.ifcfile,
                            localized: {
                                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.ifcfile'
                            },
                            buttonConfig: {
                                ui: 'administration-secondary-action-small'
                            },
                            accept: '.ifc',
                            hidden: true,
                            bind: {
                                hidden: '{actions.view}'
                            }
                        }
                    ]
                }
            ]
        }
    ],
    dockedItems: [
        {
            xtype: 'toolbar',
            dock: 'bottom',
            ui: 'footer',
            hidden: true,
            bind: {
                hidden: '{actions.view}'
            },
            items: CMDBuildUI.util.administration.helper.FormHelper.getSaveCancelButtons()
        }
    ]
});
