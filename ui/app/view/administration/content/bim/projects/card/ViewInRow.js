Ext.define('CMDBuildUI.view.administration.content.bim.projects.card.ViewInRow', {
    extend: 'CMDBuildUI.components.tab.FormPanel',

    requires: [
        'CMDBuildUI.view.administration.content.bim.projects.card.ViewInRowController'
    ],

    alias: 'widget.administration-content-bim-projects-card-viewinrow',
    controller: 'administration-content-bim-projects-card-viewinrow',

    cls: 'administration',
    ui: 'administration-tabandtools',

    items: [{
        title: CMDBuildUI.locales.Locales.administration.common.strings.generalproperties,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.common.strings.generalproperties'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        layout: 'column',
        defaults: {
            columnWidth: 0.5
        },
        items: [{
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.name,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.name'
            },
            name: 'name',
            align: 'left',
            bind: {
                value: '{theProject.name}'
            }
        }, {
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.description,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.description'
            },
            name: 'description',
            align: 'left',
            bind: {
                value: '{theProject.description}'
            }
        }, {
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.bim.parentproject,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.parentproject'
            },
            name: 'ParentProject',
            align: 'left',
            bind: {
                value: '{theProject._parentId_description}'
            },
            renderer: function (value) {
                const vm = this.lookupViewModel();
                if (Ext.isEmpty(value)) {
                    vm.bind({
                        bindTo: {
                            store: '{projectsWithoutParent}',
                            parentId: '{theProject.parentId}'
                        },
                        single: true
                    }, function (data) {
                        if (data.store && data.parentId) {
                            const record = data.store.findRecord('_id', data.parentId);
                            if (record) {
                                const _value = record.get('description');
                                if (value !== _value) {
                                    this.set('theProject._parentId_description', _value);
                                }
                            }
                        }
                    });
                }
                return value;
            }
        }, {
            xtype: 'displayfield',
            fieldLabel: CMDBuildUI.locales.Locales.administration.bim.lastcheckin,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.bim.lastcheckin'
            },
            name: 'lastCheckin',
            align: 'left',
            bind: {
                value: '{theProject.lastCheckin}'
            },
            renderer: function (value) {
                return CMDBuildUI.util.helper.FieldsHelper.renderTimestampField(value);
            }
        }, {
            xtype: 'checkbox',
            disabled: true,
            fieldLabel: CMDBuildUI.locales.Locales.administration.common.labels.active,
            localized: {
                fieldLabel: 'CMDBuildUI.locales.Locales.administration.common.labels.active'
            },
            name: 'active',
            bind: {
                value: '{theProject.active}'
            }
        }]
    }, {
        title: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
        localized: {
            title: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
        },
        xtype: "fieldset",
        ui: 'administration-formpagination',
        fieldDefaults: CMDBuildUI.util.administration.helper.FormHelper.fieldDefaults,
        items: [{
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            xtype: 'container',
            bind: {
                hidden: '{theProject.parentId}'
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedclass,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedclass'
                },
                align: 'left',
                bind: {
                    value: '{theProject.ownerClass}'
                }
            },
            {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.associatedcard,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.associatedcard'
                },
                align: 'left',
                bind: {
                    value: '{theProject._ownerCardDescription}'
                }
            }]
        }, {
            layout: 'column',
            defaults: {
                columnWidth: 0.5
            },
            xtype: 'container',
            bind: {
                hidden: '{!theProject.parentId}'
            },
            items: [{
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedclass,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedclass'
                },
                align: 'left',
                bind: {
                    value: '{theProject._parentClassDescription}'
                }
            }, {
                xtype: 'displayfield',
                fieldLabel: CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedcard,
                localized: {
                    fieldLabel: 'CMDBuildUI.locales.Locales.administration.gis.inheritedassociatedcard'
                },
                align: 'left',
                bind: {
                    value: '{theProject._parentCardDescription}'
                }
            }]
        }]
    }],

    tools: CMDBuildUI.util.administration.helper.FormHelper.getTools({
        edit: true,
        view: true,
        download: {
            menu: {
                arrowVisible: false,
                items: [{
                    text: CMDBuildUI.locales.Locales.administration.bim.ifc,
                    listeners: {
                        click: 'onDownloadBtnClick'
                    },
                    fileType: 'ifc',
                    cls: 'menu-item-nospace',
                    autoEl: {
                        'data-testid': 'administration-bim-project-download-ifc'
                    }
                }, {
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
                }]
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
        [{
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
                hidden: '{!toolAction._canConvert}',
                disabled: '{!toolAction._canUpdate}'
            }
        }]
    )

});